package com.muatik.flj.flj.UI.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.muatik.flj.flj.R;
import com.muatik.flj.flj.UI.entities.Job;
import com.muatik.flj.flj.UI.entities.StarredJob;
import com.muatik.flj.flj.UI.entities.StarredJobs;
import com.squareup.otto.Subscribe;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.Unbinder;

import static com.muatik.flj.flj.UI.views.JobViewHolder.capitalize;

/**
 * Created by muatik on 26.07.2016.
 */
public class JobDetail extends DetailActivity {

    private Job job;
    private Unbinder unbinder;

    @BindView(R.id.job_title) TextView view_job_title;
    @BindView(R.id.job_description) TextView view_job_description;
    @BindView(R.id.job_created_at) TextView view_job_created_at;
    @BindView(R.id.job_location) TextView view_job_location;
    @BindView(R.id.job_employer) TextView view_job_employer;
    @BindView(R.id.job_view_counter) TextView view_job_view_counter;
    @BindView(R.id.job_star_toggle) ToggleButton view_job_star_toggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        job = (Job) getIntent().getSerializableExtra("job");
        setContentView(R.layout.job_detail_activity);
        init();
        bindJob(job);
        job.countView();
    }

    protected void bindJob(Job job) {
        view_job_title.setText(job.getTitle());
        view_job_description.setText(job.getDescription());
        view_job_location.setText(String.format("%s, %s", new String[]{
                capitalize(job.getCity()), capitalize(job.getCountry())}));
        view_job_employer.setText(job.getEmployer());

        String view_counter = getResources().getString(R.string.view_counter);
        view_job_view_counter.setText(
                String.format(view_counter.toString(),job.getView_counter())
        );

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        try {
            Date d = format.parse(job.getCreated_at());
            view_job_created_at.setText(d.toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        getSupportActionBar().setTitle("");
        toolbar.setSubtitle(job.getTitle());
        setJobStarState(new StarredJobs.EventDataChanged());
    }

    @OnClick(R.id.job_star_toggle)
    protected void toggleJobStar(Button button) {
        StarredJob starredJob = new StarredJob(job);
        if (StarredJobs.exists(starredJob)) {
            StarredJobs.delete(starredJob);
        } else {
            try {
                StarredJobs.insert(starredJob);
            } catch (StarredJobs.ReachedMaximumStarredJobs reachedMaximumStarredJobs) {
                reachedMaximumStarredJobs.printStackTrace();
            }
        }
    }

    @Subscribe
    public void setJobStarState(StarredJobs.EventDataChanged event) {
        boolean starred = StarredJobs.exists(new StarredJob(job));
        view_job_star_toggle.setChecked(starred);
    }

    @Subscribe
    public void jobUnstartFailed(StarredJobs.EventOnDelete event) {
        setJobStarState(new StarredJobs.EventDataChanged());
    }

    @Subscribe
    public void jobStartFailed(StarredJobs.EventOnInsertFailure event) {
        setJobStarState(new StarredJobs.EventDataChanged());
    }

    @OnClick(R.id.job_apply)
    public void onApplyClicked() {
        if (job.isApplicable()) {
            Toast.makeText(this, "dunno what to do yet", Toast.LENGTH_LONG).show();
        } else {
            applyExternally();
        }
    }

    protected void applyExternally() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // @TODO: string.xml
        builder
                .setMessage("This job is cannot be applied in this app. Want to go to external site to apply?")
                .setPositiveButton("Go and apply", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent browserIntent = new Intent(
                                Intent.ACTION_VIEW, Uri.parse(job.getSource_url()));
                        startActivity(browserIntent);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
        builder.show();
    }
}
