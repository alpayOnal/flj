package com.muatik.flj.flj.UI.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.muatik.flj.flj.R;
import com.muatik.flj.flj.UI.entities.SearchHistory;
import com.muatik.flj.flj.UI.utilities.BusManager;
import com.muatik.flj.flj.UI.adapters.RecyclerViewAdapter;
import com.muatik.flj.flj.UI.entities.JobFilter;
import com.muatik.flj.flj.UI.views.BaseViewHolder;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

public class SearchHistoryList extends Fragment {
//
//    public class HistoryAdapter extends ArrayAdapter<JobFilter> {
//        public HistoryAdapter(Context context, List<JobFilter> data) {
//            super(context, 0, data);
//        }
//
//        @Override
//        public View getView(int position, View convertView, ViewGroup parent) {
//
//            final JobFilter jobFilter = getItem(position);
//
//            if (convertView == null) {
//                convertView = LayoutInflater.from(getContext()).inflate(
//                        R.layout.search_history_list_item, parent, false);
//            }
//
//
//            TextView keyword = (TextView) convertView.findViewById(R.id.history_keyword);
//            TextView location = (TextView) convertView.findViewById(R.id.history_location);
//
//            keyword.setText(jobFilter.keyword);
//            location.setText(jobFilter.location);
//            convertView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    BusManager.get().post(new SearchForm.EventOnSubmit(jobFilter));
//                }
//            });
//            return convertView;
//        }
//    }

    private RecyclerView historyList;
    private List<JobFilter> data;
    private RecyclerViewAdapter<JobFilter> adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.search_history_list_content, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        data = new ArrayList<JobFilter>();
//        data.add(new JobFilter("Python", "Istanbul, Turkey"));
//        data.add(new JobFilter("Java", "Berlin, Germany"));
//        data.add(new JobFilter("Software developer", "London, UK"));
//        data.add(new JobFilter("Oracle developer", "Istanbul, Turkey"));
//        data.add(new JobFilter("Python", "Berlin, Germany"));
//        data.add(new JobFilter("Oracle developer", "Istanbul, Turkey"));
//        data.add(new JobFilter("Database manager", "Berlin, Germany"));
        data = SearchHistory.getAll();

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

        adapter = new RecyclerViewAdapter<JobFilter>(
                getContext(),
                data,
                R.layout.search_history_list_item,
                R.layout.job_progressbar_item,
                new RecyclerViewAdapter.Listener() {
                    @Override
                    public BaseViewHolder getViewHolder(View view, int viewType) {
                        return new ItemHolder(view);
                    }
                });

        historyList = (RecyclerView) view.findViewById(R.id.search_history_list_view);
        historyList .setLayoutManager(new LinearLayoutManager(getContext()));
        adapter.bindListView(historyList);
    }

    @Subscribe
    public void onHistoryEventOnInsert(SearchHistory.EventOnInsert event) {
        Log.d("a", "onHistoryEventOnInsert");
        data = SearchHistory.getAll();
        adapter.notifyDataSetChanged();
    }
}
