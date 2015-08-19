package com.tigerapp.sparepart;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

/**
 * Created by Little on 6/8/2015.
 */
public class MaterialSearchListRowHolder extends RecyclerView.ViewHolder {
    protected TextView location;
    protected TextView materialNo;
    protected TextView descr;

    public MaterialSearchListRowHolder(View view) {
        super(view);
        this.location = (TextView) view.findViewById(R.id.location);
        this.materialNo = (TextView) view.findViewById(R.id.materialNo);
        this.descr = (TextView) view.findViewById(R.id.descr);
    }
}
