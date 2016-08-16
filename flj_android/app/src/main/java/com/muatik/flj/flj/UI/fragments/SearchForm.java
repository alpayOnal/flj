package com.muatik.flj.flj.UI.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.muatik.flj.flj.R;
import com.muatik.flj.flj.UI.entities.SearchHistory;
import com.muatik.flj.flj.UI.utilities.BusManager;
import com.muatik.flj.flj.UI.entities.JobFilter;
import com.squareup.otto.Bus;

/**
 * Created by muatik on 25.07.2016.
 */
public class SearchForm extends MyFragment {

    static public class _EventOnSubmit {
        public JobFilter filter;
        public _EventOnSubmit(JobFilter filter) {
            this.filter= filter;
        }
    }

    Bus bus = BusManager.get();

    EditText keywordBox;
    TextView locationBox;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle("SearchForm jobs");
        toolbar.setSubtitle(null);
        return inflater.inflate(R.layout.search_form_activity_content, container, false);
    }

    public class PagerAdapter extends FragmentStatePagerAdapter {
        int mNumOfTabs;

        public PagerAdapter(FragmentManager fm, int NumOfTabs) {
            super(fm);
            this.mNumOfTabs = NumOfTabs;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new SearchHistoryList();
                case 1:
                    return new AlarmList();
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return mNumOfTabs;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "History";
                case 1:
                    return "Alarms";
                default:
                    return null;
            }
        }
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

        TabLayout tabLayout = (TabLayout) view.findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab());
        tabLayout.addTab(tabLayout.newTab());
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final ViewPager viewPager = (ViewPager) view.findViewById(R.id.pager);
        final PagerAdapter adapter = new PagerAdapter
                (getChildFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    public void onSubmit() {
        JobFilter filter = new JobFilter(
                keywordBox.getText().toString(),
                "Germany", // country
                locationBox.getText().toString());
        bus.post(new _EventOnSubmit(filter));
    }


}