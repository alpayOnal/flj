package com.muatik.flj.flj.UI.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.muatik.flj.flj.R;
import com.muatik.flj.flj.UI.entities.SearchHistory;
import com.muatik.flj.flj.UI.utilities.BusManager;
import com.muatik.flj.flj.UI.adapters.RecyclerViewAdapter;
import com.muatik.flj.flj.UI.entities.Alarm;
import com.muatik.flj.flj.UI.entities.JobFilter;
import com.muatik.flj.flj.UI.views.BaseViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by muatik on 7/26/16.
 */
public class AlarmList extends Fragment {
    List<Alarm> data;
    private RecyclerView listView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.alarm_list_content, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        data = new ArrayList<Alarm>();
        data.add(new Alarm("Python", "Istanbul, Turkey"));
        data.add(new Alarm("Java", "Berlin, Germany"));
        data.add(new Alarm("Software developer", "London, UK"));
        data.add(new Alarm("Oracle developer", "Istanbul, Turkey"));
        data.add(new Alarm("Python", "Berlin, Germany"));
        data.add(new Alarm("Oracle developer", "Istanbul, Turkey"));
        data.add(new Alarm("Database manager", "Berlin, Germany"));

        class ItemHolder extends BaseViewHolder {
            TextView keyword;
            TextView location;
            JobFilter jobFilter;

            public ItemHolder(View itemView) {
                super(itemView);
                keyword = (TextView) itemView.findViewById(R.id.history_keyword);
                location = (TextView) itemView.findViewById(R.id.history_location);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        BusManager.get().post(new SearchForm.EventOnSubmit(jobFilter));
                    }
                });
            }

            @Override
            public void handle(Object o) {
                jobFilter = (JobFilter) o;
                keyword.setText(jobFilter.keyword);
                location.setText(jobFilter.location);
            }
        }

        RecyclerViewAdapter<Alarm> adapter = new RecyclerViewAdapter<Alarm>(
                getContext(),
                data,
                R.layout.alarm_list_item,
                R.layout.job_progressbar_item,
                new RecyclerViewAdapter.Listener() {
                    @Override
                    public BaseViewHolder getViewHolder(View view, int viewType) {
                        return new ItemHolder(view);
                    }
                });

        listView = (RecyclerView) view.findViewById(R.id.alarm_list_view);
        listView .setLayoutManager(new LinearLayoutManager(getContext()));
        adapter.bindListView(listView);
    }
}
