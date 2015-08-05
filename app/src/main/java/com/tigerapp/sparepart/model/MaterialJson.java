package com.tigerapp.sparepart.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Little on 11/7/2015.
 */
public class MaterialJson {

    @SerializedName("material")
    private Material material;

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }
}
