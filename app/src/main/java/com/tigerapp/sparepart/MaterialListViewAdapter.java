package com.tigerapp.sparepart;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tigerapp.sparepart.model.Material;

import java.util.List;

/**
 * Created by suparera on 16/06/2015.
 */
public class MaterialListViewAdapter extends RecyclerView.Adapter {

    private List<Material> materials;
    private Context context;

    public MaterialListViewAdapter(Context context, List<Material> materials) {
        this.context = context;
        this.materials = materials;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.material_row, null);
        MaterialListRowHolder rowHolder = new MaterialListRowHolder(v);
        return rowHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int i) {
        Material material = materials.get(i);
        ((MaterialListRowHolder) holder).materialNo.setText(material.getMaterialNo());
        ((MaterialListRowHolder) holder).descr.setText(material.getDescr());
    }

    @Override
    public int getItemCount() {
        return (materials != null ? materials.size() : 0);
    }
}
