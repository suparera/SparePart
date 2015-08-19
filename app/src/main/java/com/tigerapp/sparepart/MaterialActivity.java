package com.tigerapp.sparepart;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.tigerapp.sparepart.com.tigerapp.sparepart.okhttp.MaterialManager;
import com.tigerapp.sparepart.model.Material;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


/**
 * this Activity called from 2 Activity
 * 1. MaterialListActivity
 * 2. Quick Search Suggestion (Check by intent.getDataString() start With content://
 *      so, at onCreate, Material Activity must check intent's data that match to CallingActivity
 */
public class MaterialActivity extends ActionBarActivity {
    public static final String TAG = "MaterialActivity";

    //directory to store image after capture
    private static final String IMAGE_DIRECTORY_NAME = "Hello Camera";
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    private static final int CAMERA_CAPTURE_IMAGE2_REQUEST_CODE = 102;
    public static final int MEDIA_TYPE_IMAGE=1;
    public static final int MEDIA_TYPE_VIDEO=2;

    // file uri to store image /video
    private Uri file1Uri;
    private Uri file2Uri;
    long totalSize = 0;

    /*
    isImage1Change : used for tell Android , Server about image1
    Modifier:   1. updateUI(); set to false;
                2. onActivityResult() when back from Camera Activity, if have image = true

`   Checker:    1. uploadJsonAndImage() check it need to upload image1 with MultiPartEntity
                 2.upload this parameter for server use it to check to save image1
     */
    private boolean isImage1Change = false;
    private boolean isImage2Change = false;
    private boolean isImage3Change = false;
    private boolean isImage4Change = false;

    // UI Components
    private EditText materialNo;
    private EditText descr;
    private EditText location;
    private Button btnCaptureImage1;
    private ImageView imageView1;
    private Material material;
    private ImageView imageView2;
    private Button btnCaptureImage2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_material);

        // Mounting UI component from layout to EditText Property
        materialNo  = (EditText) findViewById(R.id.materialNo);
        descr       = (EditText) findViewById(R.id.descr);
        location = (EditText) findViewById(R.id.location);
        btnCaptureImage1 = (Button) findViewById(R.id.btnCaptureImage1);
        imageView1 = (ImageView) findViewById(R.id.imageView1);
        imageView2 = (ImageView) findViewById(R.id.imageView2);
        btnCaptureImage2 = (Button)findViewById(R.id.btnCaptureImage2);

        /* get Material back from Extra */
        Intent intent = getIntent();

        // check caller Activity,
        if(intent.getDataString()!=null && intent.getDataString().startsWith("content://")){
            String suggestionMaterialNo = intent.getData().getLastPathSegment();
            LoadMaterialAsyncTask loadMaterialAsynTask = new LoadMaterialAsyncTask(this);
            loadMaterialAsynTask.execute(suggestionMaterialNo);

        } else {
            //come from select item in MaterialSearchActivity
            final Material aMat = new Gson().fromJson(intent.getStringExtra("materialJson"), Material.class);
            LoadMaterialAsyncTask loadMaterialAsynTask = new LoadMaterialAsyncTask(this);
            loadMaterialAsynTask.execute(aMat.getMaterialNo());
        }




        /* set material props to EditText */
        if (material != null) {
            updateUI();
        }



        //// set bontton listener event
        btnCaptureImage1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                captureImage();
            }
        });

        btnCaptureImage2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                captureImage2();
            }
        });


    }

    public void updateUI() {
        materialNo.setText(material.getMaterialNo());
        descr.setText(material.getDescr());
        location.setText(material.getLocation());

        Log.d(TAG, "updateUI(): haveImage1="+material.isHaveImage1());
        // I'm blind programmer, don't known it exist or not, download it.
        if(material.isHaveImage1()){
            Picasso.with(this).load(Config.APP_SERVER_URL+"images/"+ material.getMaterialNo()+".jpg?time="+new Date().getTime()).into(imageView1);
        }
        if(material.isHaveImage2()){
            Picasso.with(this).load(Config.APP_SERVER_URL+"images/"+ material.getMaterialNo()+"-image2.jpg?time="+new Date().getTime()).into(imageView2);
        }

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        //setContentView(R.layout.activity_material);
    }

    /**
    Capturing Camera Image will launch camera app requst image capture
     */
    private void captureImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        file1Uri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, file1Uri);
        startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
    }

    private void captureImage2() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        file2Uri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, file2Uri);
        startActivityForResult(intent, CAMERA_CAPTURE_IMAGE2_REQUEST_CODE);
    }

    public Uri getOutputMediaFileUri(int type){
        return Uri.fromFile(getOutputMediaFile(type));
    }

    private static File getOutputMediaFile(int type){
        // External SDCARD location
        File mediaStroageDir = new File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                , IMAGE_DIRECTORY_NAME);

        // Create storage directory if not exist
        if(!mediaStroageDir.exists()){
            if(!mediaStroageDir.mkdirs()){
                Log.d(IMAGE_DIRECTORY_NAME, "Oops! Failed create "+IMAGE_DIRECTORY_NAME+" directory.");
                return null;
            }
        }

        // Create a media filename
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        File mediaFile;
        if(type==MEDIA_TYPE_IMAGE){
            String imgFilePath = mediaStroageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg";
            Log.d(TAG, "imgFilePath=" + imgFilePath);
            mediaFile = new File(imgFilePath);
        } else if(type==MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStroageDir.getPath()+File.separator+"VID_"+timeStamp+".mp4");
        } else {
            return null;
        }
        return mediaFile;
    }

    /**
     * Receive activity result method will call after closing Intent camera
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        // if the result is capturing image
        if(requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE){
            if(resultCode == RESULT_OK){
                // success captured the image display it with image view
                previewCaptureImage();
                isImage1Change = true;
            } else if(resultCode == RESULT_CANCELED){
                // user cancel image capture
                Toast.makeText(getApplicationContext(), "User cancelled image capture", Toast.LENGTH_SHORT).show();
            } else {
                // failed to capture image from others reason.
                Toast.makeText(getApplicationContext(), "Failed to capture image Sorry.", Toast.LENGTH_SHORT).show();
            }
        } else if(requestCode==CAMERA_CAPTURE_IMAGE2_REQUEST_CODE){
            if(resultCode == RESULT_OK){
                previewCaptureImage2();
                isImage2Change = true;
            } else if(resultCode == RESULT_CANCELED){
                // user cancel image capture
                Toast.makeText(getApplicationContext(), "User cancelled image capture", Toast.LENGTH_SHORT).show();
            } else {
                // failed to capture image from others reason.
                Toast.makeText(getApplicationContext(), "Failed to capture image Sorry.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Display image from a path to ImageView
     */
    private void previewCaptureImage(){

        try{
            ExifInterface exif = new ExifInterface(file1Uri.getPath());
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
            Log.d(TAG,"IMAGE ORIENTATION = "+orientation);
            BitmapFactory.Options options = new BitmapFactory.Options();
            // downsize image
            options.inSampleSize = 8;
            final Bitmap bitmap = BitmapFactory.decodeFile(file1Uri.getPath(), options);
            Bitmap correctRotateBitmap = null;
            if(orientation == 6 || orientation == 8 ){
                Matrix matrix = new Matrix();
                matrix.postRotate(90);
                correctRotateBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            } else {
                correctRotateBitmap = bitmap;
            }
            imageView1.setImageBitmap(correctRotateBitmap);
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    private void previewCaptureImage2(){

        try{
            ExifInterface exif = new ExifInterface(file2Uri.getPath());
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
            Log.d(TAG,"IMAGE2 ORIENTATION = "+orientation);
            BitmapFactory.Options options = new BitmapFactory.Options();
            // downsize image
            options.inSampleSize = 8;
            final Bitmap bitmap = BitmapFactory.decodeFile(file2Uri.getPath(), options);
            Bitmap correctRotateBitmap = null;
            if(orientation == 6 || orientation == 8 ){
                Matrix matrix = new Matrix();
                matrix.postRotate(90);
                correctRotateBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            } else {
                correctRotateBitmap = bitmap;
            }
            imageView2.setImageBitmap(correctRotateBitmap);
        } catch(Exception e){
            e.printStackTrace();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_material, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        Log.d(TAG, "R.id.saveButton = "+R.id.saveButton+", id item.getItemId()="+id);

        switch(id){
            case R.id.action_settings:
                return true;
            case R.id.saveButton:
                saveMaterial();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * save (Upload image, material info back to server
     */
    private void saveMaterial(){
        // upload local image to Server image, with id tell is main image or 2nd,3rd image
        // upload also include json that parse from Material object, for save back to server
        // remove localImage
        new UploadFileToServerAsyncTask().execute();
    }

    private class UploadFileToServerAsyncTask extends AsyncTask<Void, Integer, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            // Making progressBar visible
            // update progressBar value;
            // update percentage value
        }

        @Override
        protected String doInBackground(Void... params) {
            return uploadFile();
        }

        @SuppressWarnings("deprecation")
        private String uploadFile(){
            String responseString = null;

            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(Config.APP_SERVER_URL+"material/uploadJsonAndImage");
            try {
                AndroidMultipartEntity entity = new AndroidMultipartEntity(
                        new AndroidMultipartEntity.ProgressListener(){
                            @Override
                            public void transfered(long num) {
                                publishProgress((int) ((num/(float)totalSize)));
                            }
                        }
                );
                // Adding file data to http body
                // Image1
                entity.addPart("isImage1Change", new StringBody(""+isImage1Change));
                if(isImage1Change){
                    File sourceFile = new File(file1Uri.getPath());
                    entity.addPart("image1", new FileBody(sourceFile));
                }

                //Image2
                entity.addPart("isImage2Change", new StringBody(""+isImage2Change));
                if(isImage2Change){
                    File sourceFile2 = new File(file2Uri.getPath());
                    entity.addPart("image2", new FileBody(sourceFile2));
                }

                // Extra parameters if your want to pass to server
                //entity.addPart("website", new StringBody("www.androidhive.info"));
                String materialJson = new Gson().toJson(material);
                Log.d(TAG, "ตรวจสอบภาษาไทย materialJson = " + materialJson);
                entity.addPart("materialJson", new StringBody(materialJson, Charset.forName("UTF-8")));

                httpPost.setEntity(entity);

                //Making server call
                HttpResponse response = httpClient.execute(httpPost);
                HttpEntity r_entity = response.getEntity();
                int statusCode = response.getStatusLine().getStatusCode();
                if(statusCode == 200){
                    // reset image change,
                    isImage1Change = false;
                    isImage2Change = false;
                    isImage3Change = false;

                    responseString = EntityUtils.toString(r_entity);
                } else {
                    responseString = "Error occured ! Http StatusCode = "+statusCode;
                }
            } catch (Exception e) {
                responseString = e.toString();
            }
            return responseString;
        }

        @Override
        protected void onPostExecute(String s) {
            Log.e(TAG, "Response from server: " + s);
            Toast.makeText(MaterialActivity.this, "Save OK.", Toast.LENGTH_SHORT).show();
            super.onPostExecute(s);
        }
    }

    /**
     * LoadMaterialAsyncTask make network connection to load Material and show to MaterialActivity's View
     *
     * @return
     */
    private class LoadMaterialAsyncTask extends AsyncTask<String, Integer, Boolean> {
        private MaterialActivity callerActivity;

        public LoadMaterialAsyncTask(MaterialActivity callerActivity) {
            this.callerActivity = callerActivity;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected Boolean doInBackground(String... params) {
            Log.d(MaterialActivity.TAG, "LoadMaterialAsyncTask.doInBackground() for materialNo=" + params[0]);
            return loadMaterial(params[0]);
        }

        private Boolean loadMaterial(String materialNo) {
            material = MaterialManager.getMaterial(materialNo);
            return true;
        }

        @Override
        protected void onPostExecute(Boolean s) {
            super.onPostExecute(s);
            callerActivity.updateUI();
    }
    }






    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
        updateUI();
    }
}
