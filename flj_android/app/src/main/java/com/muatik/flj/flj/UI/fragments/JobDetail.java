package com.muatik.flj.flj.UI.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.muatik.flj.flj.R;
import com.muatik.flj.flj.UI.RESTful.API;
import com.muatik.flj.flj.UI.entities.AccountManager;
import com.muatik.flj.flj.UI.entities.Job;
import com.muatik.flj.flj.UI.entities.StarredJob;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by muatik on 26.07.2016.
 */
public class JobDetail extends MyFragment {

    private Job job;
    private Unbinder unbinder;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.job_detail, container, false);
        Bundle bundle = getArguments();
        job = (Job) bundle.getSerializable("job");
        Toast.makeText(getContext(),job.getTitle(), Toast.LENGTH_LONG).show();
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @OnClick(R.id.toggle_star)
    void toggleStar(ToggleButton button) {

        if (!AccountManager.isAuthenticated()) {
            Toast.makeText(getContext(), "git login ol", Toast.LENGTH_LONG).show();
            return;
        }

        boolean starred = button.isChecked();
        Toast.makeText(getContext(), "ischecked " + starred, Toast.LENGTH_LONG).show();
//        StarredJobs.star(job);
//        StarredJobs.unstar(job);
//        StarredJob.isStarred(job);
//        StarredJob.getAll();
//        StarredJob starredJob = new StarredJob(job);
//        API.authorized.starJob(starredJob).enqueue(new API.BriefCallback<StarredJob>() {
//            @Override
//            public void onSuccess(Call<StarredJob> call, Response<StarredJob> response) {
//                Toast.makeText(getContext(), "success" , Toast.LENGTH_LONG).show();
//            }
//        });
    }

}
