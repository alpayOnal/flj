package com.muatik.flj.flj.UI.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.muatik.flj.flj.R;

/**
 * Created by muatik on 27.07.2016.
 */
public class AlarmSuggestion extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.alarm_suggestion_content, container, false);
    }
}
