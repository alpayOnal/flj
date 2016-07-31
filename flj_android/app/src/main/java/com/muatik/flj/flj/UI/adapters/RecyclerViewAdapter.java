package com.muatik.flj.flj.UI.adapters;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.muatik.flj.flj.R;
import com.muatik.flj.flj.UI.views.BaseViewHolder;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.TimerTask;

/**
 * Created by muatik on 27.07.2016.
 */

public class RecyclerViewAdapter<Entity> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    List<Entity> data;
    int layout_item;
    int layout_progress_item;
    RecyclerView listView;

    public interface Listener {
        BaseViewHolder getViewHolder(View view, int viewType);
    }

    private Listener listener;

    public RecyclerViewAdapter(
            Context context,
            List<Entity> data,
            int layout_item,
            int layout_progress_item,
            Listener listener) {

        this.listener = listener;
        this.layout_item = layout_item;
        this.layout_progress_item = layout_progress_item;
        this.data = data;
    }

    public void bindListView(RecyclerView listView) {
        this.listView = listView;
        listView.setAdapter(this);
    }

    // A check for the pre-definied value that will indicate footer
    private final int VIEW_ITEM = 1;
    private final int VIEW_PROG = 0;

    @Override
    public int getItemViewType(int position) {
        return data.get(position) != null ? VIEW_ITEM : VIEW_PROG;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        if (viewType == VIEW_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(
                    layout_item, parent, false);
            vh = listener.getViewHolder(v, viewType);
        } else {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(layout_progress_item, parent, false);
            vh = listener.getViewHolder(v, viewType);
        }
        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == VIEW_ITEM) {
            BaseViewHolder vh = (BaseViewHolder) holder;
            Entity item = data.get(position);
            vh.handle(item);
        } else {
            BaseViewHolder vh = (BaseViewHolder) holder;
            vh.handle(null);
        }
    }

    @Override
    public int getItemCount() {
        return this.data.size();
    }
}