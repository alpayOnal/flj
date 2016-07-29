package com.muatik.flj.flj.UI.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.muatik.flj.flj.R;

/**
 * Created by muatik on 7/28/16.
 */
public class AlarmManager extends MyFragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.alarm_manager_content, container, false);
        getChildFragmentManager().beginTransaction()
                .add(R.id.alarmlist, new AlarmList())
                .commit();
        return view;
    }


}
