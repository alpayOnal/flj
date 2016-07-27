package com.muatik.flj.flj.UI.views;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;

import com.muatik.flj.flj.R;

/**
 * Created by muatik on 26.07.2016.
 */
public class JobProgressViewHolder extends RecyclerView.ViewHolder {
    public ProgressBar progressBar;

    public JobProgressViewHolder(View v) {
        super(v);
        progressBar = (ProgressBar) v.findViewById(R.id.job_item_progressBar);
    }
}