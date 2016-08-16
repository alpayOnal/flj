package com.muatik.flj.flj.UI.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.muatik.flj.flj.R;
import com.muatik.flj.flj.UI.RESTful.API;
import com.muatik.flj.flj.UI.activities.Main;
import com.muatik.flj.flj.UI.entities.AccountManager;
import com.muatik.flj.flj.UI.entities.Alarms;
import com.muatik.flj.flj.UI.utilities.BusManager;
import com.muatik.flj.flj.UI.adapters.RecyclerViewAdapter;
import com.muatik.flj.flj.UI.entities.Alarm;
import com.muatik.flj.flj.UI.entities.JobFilter;
import com.muatik.flj.flj.UI.views.BaseViewHolder;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Created by muatik on 7/26/16.
 */
public class AlarmList extends MyFragment {


    // @TODO: Alarm ItemHolder ve SearchHistory ItemHolder tek bir yerde toplanabilir.
    class ItemHolder extends BaseViewHolder {

        TextView keyword;
        TextView location;
        Alarm alarm;

        View.OnClickListener itemMenuClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popup = new PopupMenu(getActivity(), view);
                MenuInflater inflater = popup.getMenuInflater();
                inflater.inflate(R.menu.alarm_list_menu, popup.getMenu());
                popup.show();
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.alarm_item_menu_search:
                                BusManager.get().post(new Main.EventOnSubmit(alarm));
                                return true;
                            case R.id.alarm_item_menu_delete:
                                delete(alarm);
                                return true;
                            default:
                                return false;
                        }
                    } // end of OnMenuItemClck
                }); // setOnMenuItemClickListener
            }// end of onClick
        };


        public ItemHolder(View itemView) {
            super(itemView);
            keyword = (TextView) itemView.findViewById(R.id.history_keyword);
            location = (TextView) itemView.findViewById(R.id.history_location);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    BusManager.get().post(new Main.EventOnSubmit(alarm));
                }
            });

            itemView.findViewById(R.id.alarm_item_menu).setOnClickListener(itemMenuClickListener);
        }

        @Override
        public void handle(Object o) {
            alarm = (Alarm) o;
            keyword.setText(alarm.keyword + " " + alarm.getId());
            location.setText(alarm.city);
        }

    }


    List<Alarm> data;
    RecyclerView listView;
    RecyclerViewAdapter<Alarm> adapter;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.alarm_list_content, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        data = Alarms.getAll();
        adapter = new RecyclerViewAdapter<Alarm>(
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
        BusManager.get().register(this);
    }

    @Override
    public void onDestroyView() {
        BusManager.get().unregister(this);
        super.onDestroyView();
    }

    private void delete(Alarm alarm) {
        Alarms.delete(alarm);
    }

    @Subscribe
    public void onAlarmDeleted(Alarms.EventOnDelete event) {
        adapter.notifyItemRemoved(event.position);
    }

    @Subscribe
    public void onDataChanged(Alarms.EventDataChanged event) {
        adapter.notifyDataSetChanged();
    }


}
