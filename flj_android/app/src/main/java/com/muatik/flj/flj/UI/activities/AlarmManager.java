package com.muatik.flj.flj.UI.activities;

import android.os.Bundle;

import com.muatik.flj.flj.R;
import com.muatik.flj.flj.UI.fragments.AlarmList;

/**
 * Created by muatik on 7/28/16.
 */
public class AlarmManager extends ActivityWithDrawer {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alarms_activity);
        init();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.alarmlist, new AlarmList())
                .commit();
    }
}
