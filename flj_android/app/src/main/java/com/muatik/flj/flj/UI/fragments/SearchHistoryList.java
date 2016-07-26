package com.muatik.flj.flj.UI.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.muatik.flj.flj.R;
import com.muatik.flj.flj.UI.BusManager;
import com.muatik.flj.flj.UI.entities.JobFilter;

import java.util.ArrayList;
import java.util.List;

public class SearchHistoryList extends Fragment {

    public class HistoryAdapter extends ArrayAdapter<JobFilter> {
        public HistoryAdapter(Context context, List<JobFilter> data) {
            super(context, 0, data);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            final JobFilter jobFilter = getItem(position);

            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(
                        R.layout.search_history_list_item, parent, false);
            }


            TextView keyword = (TextView) convertView.findViewById(R.id.history_keyword);
            TextView location = (TextView) convertView.findViewById(R.id.history_location);

            keyword.setText(jobFilter.keyword);
            location.setText(jobFilter.location);
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    BusManager.get().post(new Search.EventOnSubmit(jobFilter));
                }
            });
            return convertView;
        }
    }

    private ListView historyList;
    private List<JobFilter> data;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.search_history_list_content, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        data = new ArrayList<JobFilter>();
        data.add(new JobFilter("Python", "Istanbul, Turkey"));
        data.add(new JobFilter("Java", "Berlin, Germany"));
        data.add(new JobFilter("Software developer", "London, UK"));
        data.add(new JobFilter("Oracle developer", "Istanbul, Turkey"));
        data.add(new JobFilter("Python", "Berlin, Germany"));
        data.add(new JobFilter("Oracle developer", "Istanbul, Turkey"));
        data.add(new JobFilter("Database manager", "Berlin, Germany"));
        ArrayAdapter<JobFilter> adapter = new HistoryAdapter(getContext(), data);
        historyList = (ListView) view.findViewById(R.id.search_history_list_view);
        historyList.setAdapter(adapter);

    }
}
