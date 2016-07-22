package com.muatik.flj.flj.UI.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by alpay on 11.10.2015.
 */
public class Alarm implements Serializable {

    private String id;
    private String createdAt;
    private ArrayList<String> keywords;
    private HashMap<String, String> location;

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public ArrayList<String> getKeywords() {
        return keywords;
    }

    public void setKeywords(ArrayList<String> keywords) {
        this.keywords = keywords;
    }

    public HashMap<String, String> getLocation() {
        return location;
    }

    public void setLocation(HashMap<String, String> location) {
        this.location = location;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
