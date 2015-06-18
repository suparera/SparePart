package com.tigerapp.sparepart.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by suparera on 15/06/2015.
 */
public class Material {
    @SerializedName("id")
    private String materialNo;

    @SerializedName("descr")
    private String descr;

    @SerializedName("category")
    private String category;
    private String location;

    @SerializedName("min")
    private int min;

    @SerializedName("max")
    private int max;

    @SerializedName("employee")
    private String employee;

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescr() {
        return descr;
    }

    public void setDescr(String descr) {
        this.descr = descr;
    }

    public String getEmployee() {
        return employee;
    }

    public void setEmployee(String employee) {
        this.employee = employee;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getMaterialNo() {
        return materialNo;
    }

    public void setMaterialNo(String materialNo) {
        this.materialNo = materialNo;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }
}
