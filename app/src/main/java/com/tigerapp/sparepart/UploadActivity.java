package com.tigerapp.sparepart;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


/**
 * Created by suparera on 20/05/2015.
 */
public class UploadActivity extends Activity {

    //logCat tag
    private static final String TAG = MainActivity.class.getSimpleName();
    private ProgressBar progressBar;
    private String filePath = "/sdcard/Download/kidAndRabbit.jpg";
    private TextView txtPercentage;
    private ImageView imgPreview;
    private Button btnUpload;
    private Button btnPreview;
    private Button btnRecycler;
    private Button btnMaterialList;
    long totalSize = 0;
    private Button btnCaptureImage;

    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    private static final int CAMERA_CAPTURE_VIDEO_REQUEST_CODE = 200;
    public static final int MEDIA_TYPE_IMAGE=1;
    public static final int MEDIA_TYPE_VIDEO=2;

    //directory to store image after capture
    private static final String IMAGE_DIRECTORY_NAME = "Hello Camera";

    // file uri to store image /video
    private Uri fileUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        // reference XML UI to object
        txtPercentage = (TextView) findViewById(R.id.txtPercentage);
        btnUpload = (Button)findViewById(R.id.btnUpload);
        progressBar = (ProgressBar)findViewById(R.id.progressBar);
        imgPreview = (ImageView)findViewById(R.id.imgPreview);
        btnPreview = (Button)findViewById(R.id.btnPreview);
        btnCaptureImage = (Button)findViewById(R.id.btnCapturePicture);
        btnRecycler = (Button)findViewById(R.id.btnRecycler);
        btnMaterialList = (Button) findViewById(R.id.btnMaterialList);



        // receive data from previous activity
        Intent i = getIntent();

        //image or vid
        btnPreview.setOnClickListener(new View.OnClickListener() {
                                          @Override
                                          public void onClick(View v) {
                                              previewMedia();
                                          }
                                      }
        );

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new UploadFileToServer().execute();
            }
        });

        // captureImage Listener
        btnCaptureImage.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                captureImage();
            }
        });

        btnRecycler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent recyclerIntent = new Intent(UploadActivity.this, RecyclerViewActivity.class);
                startActivity(recyclerIntent);
            }
        });

        btnMaterialList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UploadActivity.this, MaterialListActivity.class);
                startActivity(intent);
            }
        });

    }

    /**
     * Checking camera availability
     * @return
     */
    private boolean isDeviceSupportCamera(){
        if(getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
            return true;
        } else {
            return false;
        }
    }

    /*
    Capturing Camera Image will launch camera app requst image capture
     */
    private void captureImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
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
            imgPreview.setVisibility(View.VISIBLE);
            BitmapFactory.Options options = new BitmapFactory.Options();
            // downsize image
            options.inSampleSize = 8;
            final Bitmap bitmap = BitmapFactory.decodeFile(fileUri.getPath(), options);
            imgPreview.setImageBitmap(bitmap);
        } catch(NullPointerException nullE){
            nullE.printStackTrace();
        }
    }

    /**
     * Display capture image
     */
    private void previewMedia(){
        imgPreview.setVisibility(View.VISIBLE);
        BitmapFactory.Options options = new BitmapFactory.Options();
        //downsize image , as it throws OutOfMemero Exception for large Image
        options.inSampleSize=8;
        final Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);
        imgPreview.setImageBitmap(bitmap);
    }

    private class UploadFileToServer extends AsyncTask<Void, Integer, String> {

        @Override
        protected void onPreExecute() {
            progressBar.setProgress(0);
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            // Making progressBar visible
            progressBar.setVisibility(View.VISIBLE);
            // update progressBar value;
            progressBar.setProgress(values[0]);

            // update percentage value
            txtPercentage.setText(String.valueOf(values[0] + "%"));
        }

        @Override
        protected String doInBackground(Void... params) {
            return uploadFile();
        }

        @SuppressWarnings("deprecation")
        private String uploadFile(){
            String responseString = null;

            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(Config.FILE_UPLOAD_URL);
            try {
                AndroidMultipartEntity entity = new AndroidMultipartEntity(
                        new AndroidMultipartEntity.ProgressListener(){


                            @Override
                            public void transfered(long num) {
                                publishProgress((int) ((num/(float)totalSize)));
                            }
                        }
                );
                File sourceFile = new File(fileUri.getPath());

                // Adding file data to http body
                entity.addPart("avatar", new FileBody(sourceFile));

                // Extra parameters if your want to pass to server
                entity.addPart("website", new StringBody("www.androidhive.info"));

                totalSize = entity.getContentLength();
                httpPost.setEntity(entity);

                //Making server call
                HttpResponse response = httpClient.execute(httpPost);
                HttpEntity r_entity = response.getEntity();
                int statusCode = response.getStatusLine().getStatusCode();
                if(statusCode == 200){
                    // Server response
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
            showAlert(s);
            super.onPostExecute(s);
        }


    }

    private void showAlert(String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message).setTitle("Response from server")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    /*
    HelperMethod
     */

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
            mediaFile = new File(mediaStroageDir.getPath()+File.separator+"IMG_"+timeStamp+".jpg");
        } else if(type==MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStroageDir.getPath()+File.separator+"VID_"+timeStamp+".mp4");
        } else {
            return null;
        }
        return mediaFile;
    }
}
