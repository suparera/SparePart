package com.tigerapp.sparepart.com.tigerapp.sparepart.okhttp;

import android.util.Log;

import com.google.gson.Gson;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.tigerapp.sparepart.Config;
import com.tigerapp.sparepart.model.Material;
import com.tigerapp.sparepart.model.MaterialJson;
import com.tigerapp.sparepart.model.MaterialList;

import java.io.IOException;
import java.util.List;

/**
 * Created by suparera on 15/06/2015.
 */
public class MaterialManager {
    public static final String tag = "MaterialManager";

    public static List<Material> getMaterialList() throws IOException {

        OkHttpClient okHttpClient = new OkHttpClient();
        Request.Builder builder = new Request.Builder();

        Request request = builder.url(Config.APP_SERVER_URL + "material/listJson").build();
        Response response = okHttpClient.newCall(request).execute();
        if(response.isSuccessful()){
             final String materialsJson = response.body().string();
            MaterialList materialList = new Gson().fromJson(materialsJson, MaterialList.class);
            return materialList.getMaterials();
        } else {
            return null;
        }
    }

    /**
     * BUGFIX: NetworkOnMainThreadException :
     *      10/07/2015 reimplement okhttp for Asynchronized way (enqueue)
     *
     * @param matNo
     * @return
     * @throws IOException
     */
    public static Material getMaterial(String matNo)  {
        Log.d(tag, "getMaterial() called. matNo=" + matNo);
        OkHttpClient okHttpClient = new OkHttpClient();
        Request.Builder builder = new Request.Builder();

        Request request = builder.url(Config.APP_SERVER_URL + "material/json/" + matNo).build();
        try {
            Response response = okHttpClient.newCall(request).execute();
            if (response.isSuccessful()) {
                Material m = new Material();
                m.setMaterialNo("440012347");
                m.setDescr("Testing");
                m.setLocation("A1");
                String mJon = new Gson().toJson(m);

                final String materialsJson = response.body().string();
                MaterialJson materialJson = new Gson().fromJson(materialsJson, MaterialJson.class);
                return materialJson.getMaterial();
            }
        } catch (IOException ioe) {
            Log.e(tag, "IOException occur" + ioe.getMessage());
        }
        return null;

    }

    public static List<Material> findAllByIdLikeOrDescrLike(String searchText) throws IOException {
        OkHttpClient okHttpClient = new OkHttpClient();
        Request.Builder builder = new Request.Builder();

        Request request = builder.url(Config.APP_SERVER_URL + "material/findAllByIdLikeOrDescrLike?searchText="+searchText).build();
        Response response = okHttpClient.newCall(request).execute();
        if(response.isSuccessful()){
            final String materialsJson = response.body().string();
            MaterialList materialList = new Gson().fromJson(materialsJson, MaterialList.class);
            return materialList.getMaterials();
        } else {
            return null;
        }
    }
}


