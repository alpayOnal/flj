package com.muatik.flj.flj.UI.entities;

/**
 * Created by muatik on 29.07.2016.
 */
public class StarredJob {
    private String job;
    public StarredJob(Job o) {
        job = o.getJob_id();
    }
}
