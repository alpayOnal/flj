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
public class Alarms {

    final public static class EventOnInsert {
        public Alarm alarm;
        public EventOnInsert(Alarm alarm) {
            this.alarm = alarm;
        }
    }

    public static final int MAX_LENGTH = 20;
    private static final String PREFERENCE_NAME = "alarms";

    private static List<Alarm> data = new ArrayList<Alarm>();
    private static SharedPreferences prefs;

    public static void init(Context context) {
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        Gson gson = new Gson();
        String json = prefs.getString(PREFERENCE_NAME, "");
        Alarm[] alarms = gson.fromJson(json, Alarm[].class);
        if (alarms != null) {
            for (Alarm i : alarms)
                insert(i, false);
            trim();
        }
    }

    public static void insert(Alarm alarm) {
        insert(alarm, true);
    }

    public static void insert(Alarm alarm, Boolean atBegining) {
        if (atBegining) {
            data.add(0, alarm);
            if (data.size() > MAX_LENGTH)
                data.remove(MAX_LENGTH);
        } else {
            data.add(alarm);
        }
        BusManager.get().post(new EventOnInsert(alarm));
    }

    public static void trim() {
        data = data.subList(0, MAX_LENGTH > data.size() ? data.size() : MAX_LENGTH);
    }

    public static List<Alarm> getAll() {
        return data;
    }

    public static void save() {
        Gson gson = new Gson();
        String json = gson.toJson(data);
        prefs.edit().putString(PREFERENCE_NAME, json).commit();
    }

    public static void clear() {
        data.clear();
        save();
    }
}
