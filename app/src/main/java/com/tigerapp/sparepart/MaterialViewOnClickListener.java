package com.tigerapp.sparepart;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

class MaterialViewItemClickListener implements RecyclerView.OnItemTouchListener{

    public interface OnItemClickListener {
        public void onItemClick(View view, int position);
    }
    private OnItemClickListener mListener;
    GestureDetector gestureDetector;

    public MaterialViewItemClickListener(Context context, OnItemClickListener listener) {
        mListener = listener;
        gestureDetector = new GestureDetector(
                context, new GestureDetector.SimpleOnGestureListener(){
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }
        });
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        View childView = rv.findChildViewUnder(e.getX(), e.getY());
        if(childView!=null && mListener!=null && gestureDetector.onTouchEvent(e)){
            mListener.onItemClick(childView, rv.getChildAdapterPosition(childView));
            return true;
        }
        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {

    }
}