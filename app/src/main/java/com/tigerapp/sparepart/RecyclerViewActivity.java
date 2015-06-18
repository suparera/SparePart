package com.tigerapp.sparepart;

import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.Buffer;
import java.util.ArrayList;
import java.util.List;


public class RecyclerViewActivity extends ActionBarActivity {
    private static final String TAG = "RecyclerViewExample";
    private List<FeedItem> feedItemList = new ArrayList<FeedItem>();
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_recycler_view);

        // allow actvity to show indeternimate progress-bar



        mRecyclerView = (RecyclerView)findViewById(R.id.myRecyclerView);

        // change in content not change size of view
        mRecyclerView.setHasFixedSize(true);

        // use linear layoutManger
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        /* download data from below url*/
        final String url = "http://javatechig.com/api/get_category_posts/?dev=1&slug=android";
        new AsyncHttpTask().execute(url);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_recycler_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public class AsyncHttpTask extends AsyncTask<String, Void, Integer> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            setProgressBarIndeterminateVisibility(true);
        }

        @Override
        protected Integer doInBackground(String... params) {
            InputStream inputStream = null;
            Integer result = 0;
            HttpURLConnection urlConnection = null;

            try {
                /*forming java.net.URL object */
                URL url = new URL(params[0]);
                urlConnection = (HttpURLConnection)url.openConnection();

                /* for GET request */
                urlConnection.setRequestMethod("GET");
                int statusCode = urlConnection.getResponseCode();

                /* 200 represent HTTP OK */
                if(statusCode == 200 ){
                    BufferedReader r = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while( (line=r.readLine())!=null){
                        response.append(line);
                    }
                    parseResult(response.toString());
                    result = 1; // success

                } else {
                    result = 0; //failed to fetch data
                }

            } catch (Exception e) {
                Log.d(TAG, e.getLocalizedMessage());
            }
            return result;
        }

        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
            setProgressBarIndeterminateVisibility(false);
            /* Download Complete  Let update UI */
            if(result == 1){
                mAdapter = new MyRecyclerViewAdapter(RecyclerViewActivity.this, feedItemList);
                mRecyclerView.setAdapter(mAdapter);

            }else {
                Log.e(TAG, "Failed to fetch data!.");
            }
        }

        private void parseResult(String result) {
            try {
                JSONObject response = new JSONObject(result);
                JSONArray posts = response.optJSONArray("posts");

            /*Initialize array if null*/
                if (null == feedItemList) {
                    feedItemList = new ArrayList<FeedItem>();
                }

                for (int i = 0; i < posts.length(); i++) {
                    JSONObject post = posts.optJSONObject(i);

                    FeedItem item = new FeedItem();
                    item.setTitle(post.optString("title"));
                    item.setThumbnail(post.optString("thumbnail"));
                    feedItemList.add(item);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


}
