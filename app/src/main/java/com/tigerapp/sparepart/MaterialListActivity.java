package com.tigerapp.sparepart;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.tigerapp.sparepart.com.tigerapp.sparepart.okhttp.MaterialManager;
import com.tigerapp.sparepart.model.Material;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class MaterialListActivity extends ActionBarActivity {
    public static final String TAG = "MaterialListActivity";
    private RecyclerView materialListView;
    private RecyclerView.Adapter materialListViewAdapter;
    private List<Material> materials = new ArrayList<>();
    private RecyclerView.LayoutManager layoutManager;

    /**
     * 1. map view Instance to view.id
     * 2. setHasFixedSize for performance
     * 3. set LayoutManager
     * 4. Load Material by AsyncTask

     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // enable progress circle to rotate
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_material_list);

        materialListView = (RecyclerView) findViewById(R.id.materialListView);
        /*2. setHasFixedSize */
        materialListView.setHasFixedSize(true);

        // 3. layoutManager
        layoutManager = new LinearLayoutManager(this);
        materialListView.setLayoutManager(layoutManager);

        // 4. load Material by AsyncTask
        new MaterialLoadingAsyncTask().execute("");

        //** Add Listener when selection on Item
        materialListView.addOnItemTouchListener(
                new MaterialViewItemClickListener(this, new MaterialViewItemClickListener.OnItemClickListener(){

                    @Override
                    public void onItemClick(View view, int position) {
                        //Toast.makeText(MaterialListActivity.this, "HI position="+((TextView)view.findViewById(R.id.materialNo)).getText(), Toast.LENGTH_SHORT).show();
                        final Material material = materials.get(position);
                        Intent materialIntent= new Intent(MaterialListActivity.this, MaterialActivity.class);
                        materialIntent.putExtra("materialJson", new Gson().toJson(material));
                        startActivity(materialIntent);

                    }
                }));

    }


    /**
     * onPreExecute() setProgressBarIndeerminateVisibility
     */
    class MaterialLoadingAsyncTask extends AsyncTask<String , Void, Integer>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
                setProgressBarIndeterminateVisibility(true);
        }

        @Override
        protected Integer doInBackground(String... params) {
            try {
                materials = MaterialManager.getMaterialList();
                return 1;
            } catch (IOException e) {
                Log.e(TAG, "Error at MaterialAsyncTask.", e);
                return 0;
            }
        }

        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
            setProgressBarIndeterminateVisibility(false);

            Log.d(TAG, "onPostExecute() result = "+result);
            //download material complete let update UI
            if(result == 1){
                materialListViewAdapter = new MaterialListViewAdapter(MaterialListActivity.this, materials);
                materialListView.setAdapter(materialListViewAdapter);
                Log.d(TAG, "setAdapter() comploete.");
            } else {
                Log.e(TAG, "Failed to fetch data.");
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_material_list, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        //SearchView searchView = (SearchView) menu.findItem(R.id.searchView);
        //SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.searchView));
        SearchView searchView = (SearchView)menu.findItem(R.id.searchView).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id){
            case R.id.action_settings:
                return true;

            case R.id.searchView:
                openSearchDialog();
                return true;

            case R.id.materialSearchButton:
                Intent intent = new Intent(this, MaterialSearchActivity.class);
                startActivity(intent);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void openSearchDialog() {
        Log.d(TAG, "openSearchDialog() called.");
        onSearchRequested();
    }


}
