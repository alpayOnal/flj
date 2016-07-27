package com.muatik.flj.flj.UI.entities;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.muatik.flj.flj.UI.utilities.BusManager;

import java.util.ArrayList;
import java.util.List;
/**
 * Created by muatik on 27.07.2016.
 */
public class SearchHistory {

    final public static class EventOnInsert {
        public JobFilter filter;
        public EventOnInsert(JobFilter filter) {
            this.filter = filter;
        }
    }

    public static final int MAX_LENGTH = 50;
    private static final String PREFERENCE_NAME = "search_history";

    private static List<JobFilter> data = new ArrayList<JobFilter>();
    private static SharedPreferences prefs;

    public static void init(Context context) {
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        Gson gson = new Gson();
        String json = prefs.getString(PREFERENCE_NAME, "");
        JobFilter[] filters = gson.fromJson(json, JobFilter[].class);
        if (filters != null) {
            for (JobFilter i : filters)
                insert(i, false);
            trim();
        }
    }
    public static void insert(JobFilter filter) {
        insert(filter, true);
    }

    public static void insert(JobFilter filter, Boolean atBegining) {
        if (atBegining) {
            data.add(0, filter);
            if (data.size() > MAX_LENGTH)
                data.remove(MAX_LENGTH);
        } else {
            data.add(filter);
        }
        BusManager.get().post(new EventOnInsert(filter));
    }

    public static void trim() {
        data = data.subList(0, MAX_LENGTH > data.size() ? data.size() : MAX_LENGTH);
    }

    public static List<JobFilter> getAll() {
        return data;
    }

    public static void save() {
        Gson gson = new Gson();
        String json = gson.toJson(data);
        prefs.edit().putString("search_history", json).commit();
    }

    public static void clear() {
        data.clear();
        save();
    }
}
