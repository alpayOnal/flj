package com.muatik.flj.flj.UI.adapters;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.muatik.flj.flj.R;
import com.muatik.flj.flj.UI.entities.Job;
import java.util.List;


public class JobsRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    List<Job> data;

    public JobsRecyclerViewAdapter(Context context, List<Job> data) {
        this.data = data;
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
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.job_item, parent, false);
            vh = new JobViewHolder(v);
        } else {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.job_progressbar_item, parent, false);
            vh = new JobProgressViewHolder(v);
        }
        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof JobViewHolder) {
            JobViewHolder vh = (JobViewHolder) holder;
            Job job = data.get(position);
            vh.setJob(job);
        } else {
            ((JobProgressViewHolder) holder).progressBar.setIndeterminate(true);
        }

    }

    @Override
    public int getItemCount() {
        return this.data.size();
    }
}
