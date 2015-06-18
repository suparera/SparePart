package com.tigerapp.sparepart;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

/**
 * Created by suparera on 16/06/2015.
 */
public class MaterialListRowHolder extends RecyclerView.ViewHolder {

    protected TextView materialNo;
    protected  TextView descr;

    public MaterialListRowHolder(View view) {
        super(view);
        this.materialNo = (TextView)view.findViewById(R.id.materialNo);
        this.descr = (TextView)view.findViewById(R.id.descr);
    }
}
