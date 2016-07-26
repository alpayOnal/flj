package com.muatik.flj.flj.UI.entities;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

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
//
//    @Override
//    public int describeContents() {
//        return 0;
//    }
//
//    @Override
//    public void writeToParcel(Parcel parcel, int i) {
//        parcel.writeString(title);
//        parcel.writeString(description);
//        parcel.writeString(country);
//        parcel.writeString(city);
//        parcel.writeString(latitude);
//        parcel.writeString(longitude);
//        parcel.writeInt(user);
//        parcel.writeString(job_id);
//        parcel.writeString(employer);
//    }
}
