package com.muatik.flj.flj.UI.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.muatik.flj.flj.R;
import com.muatik.flj.flj.UI.BusManager;
import com.muatik.flj.flj.UI.entities.JobFilter;
import com.squareup.otto.Bus;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by muatik on 25.07.2016.
 */
public class Search extends Fragment {

    static public class EventOnSubmit {
        public JobFilter filter;
        public EventOnSubmit(JobFilter filter) {
            this.filter= filter;
        }
    }

    Bus bus = BusManager.get();

    EditText keywordBox;
    TextView locationBox;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle("Search jobs");
        toolbar.setSubtitle(null);
        return inflater.inflate(R.layout.search_activity_content, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        keywordBox = ((EditText) getView().findViewById(R.id.search_keyword));
        locationBox = ((TextView) getView().findViewById(R.id.search_location));
        view.findViewById(R.id.search_submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View button) {onSubmit();}
        });

    }

    public void onSubmit() {
        bus.post(new EventOnSubmit(new JobFilter(
                keywordBox.getText().toString(),
                locationBox.getText().toString())));
    }


}