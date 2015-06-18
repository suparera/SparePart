package com.tigerapp.sparepart.com.tigerapp.sparepart.okhttp;

import com.google.gson.Gson;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.tigerapp.sparepart.model.Material;
import com.tigerapp.sparepart.model.MaterialList;

import java.io.IOException;
import java.util.List;

/**
 * Created by suparera on 15/06/2015.
 */
public class MaterialManager {

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
}


