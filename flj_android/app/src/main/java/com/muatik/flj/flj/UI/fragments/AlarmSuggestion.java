package com.muatik.flj.flj.UI.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.muatik.flj.flj.R;
import com.muatik.flj.flj.UI.entities.AccountManager;
import com.muatik.flj.flj.UI.entities.Alarm;
import com.muatik.flj.flj.UI.entities.Alarms;
import com.muatik.flj.flj.UI.entities.JobFilter;
import com.muatik.flj.flj.UI.utilities.BusManager;
import com.squareup.otto.Subscribe;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by muatik on 27.07.2016.
 */
public class AlarmSuggestion extends MyFragment {

    public interface Listener {
        void onClose();
        JobFilter getJobFilter();
    }

    private Alarm alarm;
    private Listener listener;
    private Unbinder unbinder;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view;
        alarm = Alarm.build(listener.getJobFilter());
        Log.d("flj", "alarm suggestion alarm: " + alarm.toString());
        if (Alarms.exists(alarm)) {
            view = new View(getActivity());
            close();
        } else {
            view = inflater.inflate(R.layout.alarm_suggestion_content, container, false);
            unbinder = ButterKnife.bind(this, view);
        }

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    @OnClick(R.id.alarm_suggestion_yes)
    public void onAlarmAccept() {
        Alarm alarm = Alarm.build(this.alarm);
        try {
            Alarms.insert(alarm);
            hide();
        } catch (Alarms.ReachedMaximumAlarm reachedMaximumAlarm) {
            // @TODO: string.xml
            Toast.makeText(getActivity(),
                    "Error: you have reached the maximum number of alarms. Edit your alarms.",
                    Toast.LENGTH_LONG).show();
        }
    }

    @OnClick(R.id.alarm_suggestion_not_now)
    public void onAlarmRejected() {
        close();
    }

    @Subscribe
    public void onAlarmInserted(Alarms.EventOnInsert event) {
        Toast.makeText(getActivity(), "You will get notified about new '"
                + alarm.keyword
                + "' jobs in "
                + alarm.city, Toast.LENGTH_LONG).show();
        close();
    }

    @Subscribe
    public void OnAlarmInsertFailure(Alarms.EventOnInsertFailure event) {
        // @TODO: string.xml
        Toast.makeText(
                getActivity(),
                "Alarm error: " + event.error.getMessage(), Toast.LENGTH_LONG).show();
        show();
    }

    private void hide() {
        if (getView() != null)
            getView().setVisibility(View.GONE);
    }

    private void show() {
        getView().setVisibility(View.VISIBLE);
    }

    private void close() {
        hide();
        listener.onClose();
    }

    @Override
    public void onResume() {

        BusManager.get().register(this);
        super.onResume();
    }

    @Override
    public void onPause() {
        BusManager.get().unregister(this);
        if (unbinder != null)
            unbinder.unbind();
        super.onPause();
    }


}
