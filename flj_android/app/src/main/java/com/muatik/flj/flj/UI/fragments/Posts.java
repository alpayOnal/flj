package com.muatik.flj.flj.UI.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.muatik.flj.flj.R;

import java.util.List;

/**
 * Created by muatik on 22.07.2016.
 */
public class Posts extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        jobsRecyclerViewAdapter = new JobsRecyclerViewAdapter(getContext(), (List<Job>) savedJobList);
        recyclerView.setAdapter(jobsRecyclerViewAdapter);
        return inflater.inflate(R.layout.jobs_fragment, container, false);
    }
}
