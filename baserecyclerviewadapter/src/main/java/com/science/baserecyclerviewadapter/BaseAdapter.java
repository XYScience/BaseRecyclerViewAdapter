package com.science.baserecyclerviewadapter;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

/**
 * @author SScience
 * @description
 * @email chentushen.science@gmail.com
 * @data 2016/9/28
 */

public class BaseAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }
}
