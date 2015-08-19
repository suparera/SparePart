package com.tigerapp.sparepart;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tigerapp.sparepart.model.Material;

import java.util.List;

/**
 * Created by Little on 6/8/2015.
 */
public class MaterialSearchListViewAdapter extends RecyclerView.Adapter {
    private List<Material> materials;
    private Context context;

    public MaterialSearchListViewAdapter(Context context, List<Material> materials) {
        this.context = context;
        this.materials = materials;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.material_search_row, null);
        MaterialSearchListRowHolder rowHolder = new MaterialSearchListRowHolder(v);
        return rowHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Material material = materials.get(position);
        ((MaterialSearchListRowHolder)holder).location.setText(material.getLocation());
        ((MaterialSearchListRowHolder)holder).materialNo.setText(material.getMaterialNo());
        ((MaterialSearchListRowHolder)holder).descr.setText(material.getDescr());
    }

    @Override
    public int getItemCount() {
        return (materials!=null ? materials.size() : 0);
    }
}
