package com.muatik.flj.flj.UI.adapters;

import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.TextView;

import com.muatik.flj.flj.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by muatik on 24.07.2016.
 */
public class JobViewHolder extends RecyclerView.ViewHolder{

    public TextView title;
    public TextView employer;
    public TextView country;
    public TextView city;
    public TextView created_at;

    public JobViewHolder(View view) {
        super(view);
        this.title = (TextView) view.findViewById(R.id.job_title);
        this.employer = (TextView) view.findViewById(R.id.job_employer);
        this.country= (TextView) view.findViewById(R.id.job_country);
        this.city= (TextView) view.findViewById(R.id.job_city);
        this.created_at= (TextView) view.findViewById(R.id.job_created_at);
    }

    public static String capitalize(String t) {
        if (t != null)
            return t.substring(0,1).toUpperCase() + t.substring(1).toLowerCase();
        return t;
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
