package com.muatik.flj.flj.UI.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.muatik.flj.flj.R;
import com.muatik.flj.flj.UI.BusManager;
import com.muatik.flj.flj.UI.entities.Job;

/**
 * Created by muatik on 26.07.2016.
 */
public class JobDetail extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.job_detail, container, false);
        Bundle bundle = getArguments();
        Job job = (Job) bundle.getSerializable("job");
        Toast.makeText(getContext(),job.getTitle(), Toast.LENGTH_LONG).show();
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

}
