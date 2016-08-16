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
import com.muatik.flj.flj.UI.activities.Main;
import com.muatik.flj.flj.UI.entities.SearchHistory;
import com.muatik.flj.flj.UI.utilities.BusManager;
import com.muatik.flj.flj.UI.adapters.RecyclerViewAdapter;
import com.muatik.flj.flj.UI.entities.JobFilter;
import com.muatik.flj.flj.UI.views.BaseViewHolder;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

public class SearchHistoryList extends MyFragment {

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
                    BusManager.get().post(new Main.EventOnSubmit(jobFilter));
                }
            });
        }

        @Override
        public void handle(Object o) {
            jobFilter = (JobFilter) o;
            keyword.setText(jobFilter.keyword);
            location.setText(jobFilter.city);
        }
    }

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
        data = SearchHistory.getAll();
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
        data = SearchHistory.getAll();
        adapter.notifyDataSetChanged();
    }
}
