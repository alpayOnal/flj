package com.muatik.flj.flj.UI.activities;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import com.muatik.flj.flj.R;
import com.muatik.flj.flj.UI.entities.JobFilter;
import com.muatik.flj.flj.UI.entities.SearchHistory;
import com.muatik.flj.flj.UI.fragments.*;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by muatik on 25.07.2016.
 */
public class Main extends ActivityWithDrawer {

    static public class EventOnSubmit {
        public JobFilter filter;
        public EventOnSubmit(JobFilter filter) {
            this.filter= filter;
        }
    }

    @BindView(R.id.search_keyword) EditText keywordBox;
    @BindView(R.id.search_location) TextView locationBox;

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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        init();
    }


    @Override
    protected void init() {
        super.init();
//        findViewById(R.id.search_submit).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                onSubmit();
//            }
//        });
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab());
        tabLayout.addTab(tabLayout.newTab());
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        final PagerAdapter adapter = new PagerAdapter
                (getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

    }

    @OnClick(R.id.search_submit)
    public void onSubmit() {
        Log.d("flj", "summ");
        JobFilter filter = new JobFilter(
                keywordBox.getText().toString(),
                "Germany", // country
                locationBox.getText().toString());
        SearchHistory.insert(filter);
        bus.post(new EventOnSubmit(filter));
    }
}