package com.muatik.flj.flj.UI.entities;

/**
 * Created by muatik on 24.07.2016.
 */
public class Job {
    public String job_id;
    public String title;



    public String employer;
    public String description;
    public String created_at;
    public String country;
    public String city;
    public String latitude;
    public String longitude;
    public Integer user;

    public Job() {

    }

    public Job(String title, String created_at, String city, String country) {
        this.title = title;
        this.city = city;
        this.country = country;
        this.created_at= created_at;
    }

    public String getId() {
        return job_id;
    }

    public String getTitle() {
        return title;
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

}
