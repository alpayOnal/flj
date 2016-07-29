package com.muatik.flj.flj.UI.entities;

import java.io.Serializable;

/**
 * Created by muatik on 26.07.2016.
 */
public class JobFilter implements Serializable {
    public static class EventOnChanged {
        public JobFilter filter;
        public EventOnChanged(JobFilter filter) {
            this.filter = filter;
        }
    }

    public String keyword, country, city;
    public JobFilter(String keyword, String country, String city) {
        this.keyword = keyword;
        this.city = city;
        this.country = country;
    }
}
