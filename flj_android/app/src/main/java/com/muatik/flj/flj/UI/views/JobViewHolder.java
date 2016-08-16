package com.muatik.flj.flj.UI.views;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.TextView;

import com.muatik.flj.flj.R;
import com.muatik.flj.flj.UI.activities.JobDetail;
import com.muatik.flj.flj.UI.utilities.BusManager;
import com.muatik.flj.flj.UI.entities.Job;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by muatik on 24.07.2016.
 */
public class JobViewHolder extends RecyclerView.ViewHolder{


    private Job job;

    public class EventOnJobClicked {
        public Job job;
        public EventOnJobClicked(Job job) {
            this.job = job;
        }
    }

    private TextView title;
    private TextView employer;
    private TextView country;
    private TextView city;
    private TextView created_at;

    public JobViewHolder(View view) {
        super(view);
        this.title = (TextView) view.findViewById(R.id.job_title);
        this.employer = (TextView) view.findViewById(R.id.job_employer);
        this.country= (TextView) view.findViewById(R.id.job_country);
        this.city= (TextView) view.findViewById(R.id.job_city);
        this.created_at= (TextView) view.findViewById(R.id.job_created_at);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BusManager.get().post(new EventOnJobClicked(job));
            }
        });
    }

    public static String capitalize(String t) {
        if (t != null)
            return t.substring(0,1).toUpperCase() + t.substring(1).toLowerCase();
        return t;
    }

    public void setJob(Job job) {
        this.job = job;
        title.setText(job.getTitle()  + " " + job.getId());
        setCity(job.getCity());
        setCountry(job.getCountry());
        setCreatedAt(job.getCreated_at());
        setEmployer(job.getEmployer());
    }

    public void setCreatedAt(String created_at) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        try {
            Date d = format.parse(created_at);
            this.created_at.setText(
                    DateUtils.getRelativeTimeSpanString(
                    d.getTime(),
                    new Date().getTime(),
                    DateUtils.MINUTE_IN_MILLIS));


        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void setCity(String city) {
        this.city.setText(capitalize(city) + ", ");
    }

    public void setCountry(String country) {
        this.country.setText(capitalize(country));
    }

    public void setEmployer(String employer) {
        this.employer.setText("Google Microsoft LTD. STI");
    }

}
