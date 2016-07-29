package com.muatik.flj.flj.UI.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by alpay on 11.10.2015.
 */
public class Alarm extends JobFilter {
    private int id;

    public Alarm(String keyword, String city, String country) {
        super(keyword, country, city);
    }

    public static Alarm build(JobFilter jobFilter) {
        return new Alarm(jobFilter.keyword, jobFilter.city, jobFilter.country);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
