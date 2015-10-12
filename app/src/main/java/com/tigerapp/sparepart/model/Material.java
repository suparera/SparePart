package com.tigerapp.sparepart.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by suparera on 15/06/2015.
 */
public class Material {

    @SerializedName("_id")
    private long _id;

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

    @SerializedName("haveImage1")
    private boolean haveImage1;

    @SerializedName("haveImage2")
    private boolean haveImage2;

    @SerializedName("haveImage3")
    private boolean haveImage3;

    @SerializedName("haveImage4")
    private boolean haveImage4;

    public long get_id() {
        return _id;
    }

    public void set_id(long _id) {
        this._id = _id;
    }

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

    public boolean isHaveImage1() {
        return haveImage1;
    }

    public void setHaveImage1(boolean haveImage1) {
        this.haveImage1 = haveImage1;
    }

    public boolean isHaveImage2() {
        return haveImage2;
    }

    public void setHaveImage2(boolean haveImage2) {
        this.haveImage2 = haveImage2;
    }

    public boolean isHaveImage3() {
        return haveImage3;
    }

    public void setHaveImage3(boolean haveImage3) {
        this.haveImage3 = haveImage3;
    }

    public boolean isHaveImage4() {
        return haveImage4;
    }

    public void setHaveImage4(boolean haveImage4) {
        this.haveImage4 = haveImage4;
    }
}
