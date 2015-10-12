package com.tigerapp.sparepart;

import android.app.ListActivity;
import android.app.SearchManager;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;


public class MaterialSearchActivity extends ListActivity{
    public static final String TAG = "MaterialSearchActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_material_search);
        handleIntent( getIntent() );

        //Getting the intent that invoked this activity
        Intent intent = getIntent();
        Log.d(TAG, "onCreate(), intent.getData()=" + intent.getData());
        String query = intent.getStringExtra("query");

        // Intent.ACTION_VIEW : If this activity is invoked by selection an item from Suggestion of SearchDialog
//        if(getIntent().getAction().equals(Intent.ACTION_VIEW)) {
//            Intent materialIntent = new Intent(this, MaterialActivity.class);
//            materialIntent.setData(intent.getData());
//            startActivity(materialIntent);
//            finish();
//        }


    }

    private void doMySearch(String query) {
        Log.e(TAG, "doMySearch() called.");
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
}
