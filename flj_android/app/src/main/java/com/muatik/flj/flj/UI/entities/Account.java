package com.muatik.flj.flj.UI.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by alpay on 10/9/15.
 */
public class Account implements Serializable {
    private String createdAt;
    private String email;
    private String fname;
    private String gcmId;
    private String id;
    private String lname;
    private ArrayList<Alarm> alarms = new ArrayList<Alarm>();
    private HashMap<String, Object> jobs = new HashMap<String, Object>();

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getGcmId() {
        return gcmId;
    }

    public void setGcmId(String gcmId) {
        this.gcmId = gcmId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLname() {
        return lname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    public ArrayList<Alarm> getAlarms() {
        return alarms;
    }

    public void setAlarms(ArrayList<Alarm> alarms) {
        this.alarms = alarms;
    }

    public HashMap<String, Object> getJobs() {
        return jobs;
    }

    public void setJobs(HashMap<String, Object> jobs) {
        this.jobs = jobs;
    }
}
