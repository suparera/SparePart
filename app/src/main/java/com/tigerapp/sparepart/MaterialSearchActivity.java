package com.tigerapp.sparepart;

import android.app.ListActivity;
import android.app.SearchManager;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;
import com.tigerapp.sparepart.com.tigerapp.sparepart.okhttp.MaterialManager;
import com.tigerapp.sparepart.model.Material;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 2015-08-06 : when click from quick suggestion, and back must search for same result.
 * ทาง Store ต้องการให้ back แล้ว ได้ list เดิมที่หาไว้
 */
public class MaterialSearchActivity extends ActionBarActivity{
    public static final String TAG = "MaterialSearchActivity";
    private RecyclerView materialListView;
    private RecyclerView.Adapter materialSearchListViewAdapter;
    private List<Material> materials = new ArrayList<>();
    private RecyclerView.LayoutManager layoutManager;
    private Button searchButton;
    private TextView searchText;
    private TextView searchResult;
    private String searchTextString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate() called.");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_material_search);

        // UI mapping with layout
        materialListView = (RecyclerView) findViewById(R.id.materialListView);
        searchText = (TextView) findViewById(R.id.searchText);
        searchButton = (Button) findViewById(R.id.searchButton);
        searchResult = (TextView) findViewById(R.id.searchResult);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchTextString = searchText.getText().toString().trim();
                searchMaterials(searchTextString);
            }
        });

        // event when select item from list, will go to MaterialActivity to show detail
        materialListView.addOnItemTouchListener(new MaterialViewItemClickListener(
                this
                , new MaterialViewItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        final Material material = materials.get(position);
                        Intent materialIntent= new Intent(MaterialSearchActivity.this, MaterialActivity.class);
                        materialIntent.putExtra("materialJson", new Gson().toJson(material));
                        startActivity(materialIntent);
                    }
        }));

        // RecyclerView set hasFixSize(true) for performance.
        materialListView.setHasFixedSize(true);

        //Layout Manager
        layoutManager = new LinearLayoutManager(this);
        materialListView.setLayoutManager(layoutManager);

        //handleIntent(getIntent());
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume() called.");
    }

    private void searchMaterials(String queryText){
        new MaterialLoadingAsyncTask().execute(queryText);
    }

    private void doMySearch(String query) {
        Log.e(TAG, "doMySearch() called. query="+query);
        setContentView(R.layout.activity_material_search);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_VIEW)) {
            Intent materialIntent = new Intent(this, MaterialActivity.class);
            materialIntent.setData(intent.getData());
            startActivity(materialIntent);
            finish();
        }
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query =
                    intent.getStringExtra(SearchManager.QUERY);
            doMySearch(query);
        }
    }

    /**
     * onPreExecute() setProgressBarIndeerminateVisibility
     */
    class MaterialLoadingAsyncTask extends AsyncTask<String , Void, Integer> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            setProgressBarIndeterminateVisibility(true);
        }

        @Override
        protected Integer doInBackground(String... params) {
            try {
                materials = MaterialManager.findAllByIdLikeOrDescrLike(params[0]);
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
                materialSearchListViewAdapter = new MaterialSearchListViewAdapter(MaterialSearchActivity.this, materials);
                materialListView.setAdapter(materialSearchListViewAdapter);
                searchResult.setText(searchTextString+": พบ "+materials.size()+" รายการ");
                Log.d(TAG, "setAdapter() complete.");
            } else {
                Log.e(TAG, "Failed to fetch data.");
            }
        }
    }


}
