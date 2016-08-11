package com.muatik.flj.flj.UI.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.muatik.flj.flj.R;
import com.muatik.flj.flj.UI.RESTful.API;
import com.muatik.flj.flj.UI.entities.AccountManager;
import com.muatik.flj.flj.UI.entities.Job;
import com.muatik.flj.flj.UI.entities.StarredJob;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Response;

import static com.muatik.flj.flj.UI.views.JobViewHolder.capitalize;

/**
 * Created by muatik on 26.07.2016.
 */
public class JobDetail extends MyFragment {

    private Job job;
    private Unbinder unbinder;

    @BindView(R.id.job_title) TextView view_job_title;
    @BindView(R.id.job_description) TextView view_job_description;
    @BindView(R.id.job_created_at) TextView view_job_created_at;
    @BindView(R.id.job_location) TextView view_job_location;
    @BindView(R.id.job_employer) TextView view_job_employer;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.job_detail, container, false);
        Bundle bundle = getArguments();
        job = (Job) bundle.getSerializable("job");
        Toast.makeText(getContext(),job.getTitle(), Toast.LENGTH_LONG).show();
        unbinder = ButterKnife.bind(this, view);

        view_job_title.setText(job.getTitle());
        view_job_description.setText(job.getDescription());
        view_job_location.setText(String.format("%s, %s", new String[]{
                capitalize(job.getCity()), capitalize(job.getCountry())}));
        view_job_employer.setText(job.getEmployer());

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        try {
            Date d = format.parse(job.getCreated_at());
            view_job_created_at.setText(d.toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

//    @OnClick(R.id.toggle_star)
//    void toggleStar(ToggleButton button) {
//
//        if (!AccountManager.isAuthenticated()) {
//            Toast.makeText(getContext(), "git login ol", Toast.LENGTH_LONG).show();
//            return;
//        }
//
//        boolean starred = button.isChecked();
//        Toast.makeText(getContext(), "ischecked " + starred, Toast.LENGTH_LONG).show();
////        StarredJobs.star(job);
////        StarredJobs.unstar(job);
////        StarredJob.isStarred(job);
////        StarredJob.getAll();
////        StarredJob starredJob = new StarredJob(job);
////        API.authorized.starJob(starredJob).enqueue(new API.BriefCallback<StarredJob>() {
////            @Override
////            public void onSuccess(Call<StarredJob> call, Response<StarredJob> response) {
////                Toast.makeText(getContext(), "success" , Toast.LENGTH_LONG).show();
////            }
////        });
//    }

}
