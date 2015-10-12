package com.tigerapp.sparepart.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by suparera on 15/06/2015.
 */
public class MaterialList {

    @SerializedName("materials")
    private List<Material> materials;

    public List<Material> getMaterials() {
        return materials;
    }

    public void setMaterials(List<Material> materials) {
        this.materials = materials;
    }
}
