package com.muatik.flj.flj.UI.entities;

import com.muatik.flj.flj.UI.RESTful.API;

import java.io.Serializable;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by muatik on 24.07.2016.
 */
public class Job implements Serializable {
    private String job_id;
    private String title;
    private String employer;
    private String description;
    private String created_at;
    private String country;
    private String city;
    private String latitude;
    private String longitude;
    private Integer user;
    private Integer view_counter;
    private boolean applicable;
    private String source_url;

    public Job() {

    }

    public Job(String title, String created_at, String city, String country) {
        this.title = title;
        this.city = city;
        this.country = country;
        this.created_at= created_at;
    }

    public void setJob_id(String job_id) {
        this.job_id = job_id;
    }

    public String getId() {
        return job_id;
    }

    public String getTitle() {
        return title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public String getCountry() {
        return country;
    }

    public String getCity() {
        return city;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setUser(Integer user) {
        this.user = user;
    }

    public Integer getUser() {
        return user;
    }

    public String getCreated_at() {
        return created_at;
    }

    public String getJob_id() {
        return job_id;
    }


    public String getEmployer() {
        return employer;
    }

    public void setEmployer(String employer) {
        this.employer = employer;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Integer getView_counter() {
        return view_counter;
    }

    public void setView_counter(Integer view_counter) {
        this.view_counter = view_counter;
    }

    public void countView() {
        API.authorized.countView(getId()).enqueue(new API.BriefCallback<Job>() {
            @Override
            public void onFailure(Call<Job> call, Throwable t, Response<Job> response) {

            }

            @Override
            public void onSuccess(Call<Job> call, Response<Job> response) {
                Job job = response.body();
                Job.this.setView_counter(job.getView_counter());
            }
        });
    }


    public boolean isApplicable() {
        return applicable;
    }

    public void setApplicable(boolean applicable) {
        this.applicable = applicable;
    }

    public String getSource_url() {
        return source_url;
    }

    public void setSource_url(String source_url) {
        this.source_url = source_url;
    }

    public void setLatLong(double latitude, double longitude) {
        this.latitude = String.valueOf(latitude);
        this.longitude = String.valueOf(longitude);
    }
}
