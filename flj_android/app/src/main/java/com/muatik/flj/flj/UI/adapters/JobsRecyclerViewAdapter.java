package com.muatik.flj.flj.UI.adapters;
import android.support.v4.view.LayoutInflaterCompat;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.textservice.TextInfo;
import android.widget.TextView;

import com.muatik.flj.flj.R;
import com.muatik.flj.flj.UI.views.JobRowHolder;

import org.w3c.dom.Text;

import java.util.zip.Inflater;


public class JobsRecyclerViewAdapter extends RecyclerView.Adapter {

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public TextView created_at;
        public TextView location;
        public TextView owner;

        public ViewHolder(TextView title, TextView created_at, TextView location, TextView owner) {
            super(title);
            this.title = title;
            this.created_at = created_at;
            this.location = location;
            this.owner = owner;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.job_item, parent, false);
        ViewHolder(v)
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
