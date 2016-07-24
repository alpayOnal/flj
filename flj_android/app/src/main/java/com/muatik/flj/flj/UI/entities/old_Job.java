package com.muatik.flj.flj.UI.entities;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by alpay on 10/6/15.
 */
public class old_Job implements Serializable {

    private String _id;
    private String date;
    private String title;
    private String description;
    private String company;
    private String salary;
    private String jobType;
    private HashMap<String, String> location;
    private HashMap<String, Object> stats;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String descrption) {
        this.description = descrption;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getSalary() {
        return salary;
    }

    public void setSalary(String salary) {
        this.salary = salary;
    }

    public String getJobType() {
        return jobType;
    }

    public void setJobType(String jobType) {
        this.jobType = jobType;
    }

    public HashMap<String, String> getLocation() {
        return location;
    }

    public void setLocation(HashMap<String, String> location) {
        this.location = location;
    }

    public HashMap<String, Object> getStats() {
        return stats;
    }

    public void setStats(HashMap<String, Object> stats) {
        this.stats = stats;
    }

    public String getId() {
        return _id;
    }

    public void setId(String id) {
        this._id = _id;
    }



    public ArrayList<String> getLocationList(){
        ArrayList<String> location = new ArrayList<String>();

        if (getLocation().containsKey("country")){
            if (!getLocation().get("country").isEmpty() && getLocation().get("city") !=null)
                location.add(getLocation().get("country"));
        }

        if (getLocation().containsKey("city")){
            if (!getLocation().get("city").isEmpty() && getLocation().get("city") !=null)
                location.add(getLocation().get("city"));
        }

        if (getLocation().containsKey("state")){
            if (!getLocation().get("state").isEmpty() && getLocation().get("state") !=null)
                location.add(getLocation().get("state"));
        }

        if (getLocation().containsKey("region")){
            if (!getLocation().get("region").isEmpty() && getLocation().get("region") !=null)
                location.add(getLocation().get("region"));
        }

        return location;
    }
}