package com.muatik.flj.flj.UI.adapters;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.muatik.flj.flj.R;
import com.muatik.flj.flj.UI.entities.Job;
import java.util.List;


public class JobsRecyclerViewAdapter extends RecyclerView.Adapter<JobViewHolder> {
    List<Job> data;

    public JobsRecyclerViewAdapter(Context context, List<Job> data) {
        this.data = data;
    }

    @Override
    public JobViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.job_item, parent, false);
        return new JobViewHolder(view);
    }

    @Override
    public void onBindViewHolder(JobViewHolder holder, int position) {
        Job job = data.get(position);
        holder.title.setText(job.getTitle());
        holder.setCity(job.getCity());
        holder.setCountry(job.getCountry());
        holder.setCreatedAt(job.getCreated_at());
        holder.setEmployer(job.getEmployer());
    }

    @Override
    public int getItemCount() {
        return this.data.size();
    }
}
