package com.tigerapp.sparepart;

/**
 * Created by suparera on 20/05/2015.
 */
public class Config {
    //// wifi nicfight
    //public final static String APP_SERVER_URL = "http://10.62.2.111:8080/sparepart/";
    //// internal Virtualbox (when use with GenyMotion
    //public final static String APP_SERVER_URL = "http://192.168.56.1:8080/sparepart/";
    //// wifi tot
    public final static String APP_SERVER_URL = "http://10.0.173.20:8080/sparepart/";

    //public static final String FILE_UPLOAD_URL= "http://192.168.56.1:8080/sparepart/avatar/upload_avatar";
    public static final String FILE_UPLOAD_URL= APP_SERVER_URL+"avatar/upload_avatar";
    public static final String IMAGE_DIRECTORY_NAME="Android File Upload";
}
