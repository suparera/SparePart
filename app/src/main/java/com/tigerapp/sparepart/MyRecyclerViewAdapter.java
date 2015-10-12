package com.tigerapp.sparepart;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by suparera on 04/06/2015.
 */
public class MyRecyclerViewAdapter extends RecyclerView.Adapter {
    private List<FeedItem> feedItemList;
    private Context mContext;

    public MyRecyclerViewAdapter(Context context, List<FeedItem> feedItemList){
        this.feedItemList = feedItemList;
        this.mContext = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_row, null);
        FeedListRowHolder mh = new FeedListRowHolder(v);
        return mh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder feedListRowHolder, int i) {
        FeedItem feedItem = feedItemList.get(i);
        Picasso.with(mContext).load(feedItem.getThumbnail())
                .error(R.drawable.placeholder)
                .placeholder(R.drawable.placeholder)
                .into(((FeedListRowHolder)feedListRowHolder).thumbnail);
        ((FeedListRowHolder)feedListRowHolder).title.setText(Html.fromHtml(feedItem.getTitle()));
    }

    @Override
    public int getItemCount() {
        return (null != feedItemList ? feedItemList.size() : 0);
    }
}
