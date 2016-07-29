package com.muatik.flj.flj.UI.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.muatik.flj.flj.R;
import com.muatik.flj.flj.UI.entities.Alarm;
import com.muatik.flj.flj.UI.entities.Alarms;
import com.muatik.flj.flj.UI.entities.JobFilter;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by muatik on 27.07.2016.
 */
public class AlarmSuggestion extends Fragment {


    public interface Listener {
        void onClose();
        JobFilter getJobFilter();
    }

    private JobFilter jobFilter;
    private Listener listener;

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.alarm_suggestion_content, container, false);
        jobFilter = listener.getJobFilter();
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @OnClick(R.id.alarm_suggestion_yes)
    public void onAlarmAccept() {
        Alarm alarm = Alarm.build(jobFilter);
        try {
            Alarms.insert(alarm);
            Toast.makeText(getActivity(), "You will get notified about new '"
                    + jobFilter.keyword
                    + "' jobs in "
                    + jobFilter.city, Toast.LENGTH_LONG).show();
            close();
        } catch (Exception e) {
            Toast.makeText(getActivity(),
                    "Alarm cannot be set. " + e.getMessage(), Toast.LENGTH_LONG).show();
        } catch (Alarms.ReachedMaximumAlarm reachedMaximumAlarm) {
            reachedMaximumAlarm.printStackTrace();
        }
    }


    @OnClick(R.id.alarm_suggestion_not_now)
    public void onAlarmReceted() {
        close();
    }


    private void close() {
        getView().setVisibility(View.GONE);
        listener.onClose();
    }

}
