package com.muatik.flj.flj.UI.activities;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.muatik.flj.flj.R;
import com.muatik.flj.flj.UI.RESTful.API;
import com.muatik.flj.flj.UI.adapters.EndlessRecyclerOnScrollListener;
import com.muatik.flj.flj.UI.adapters.JobsRecyclerViewAdapter;
import com.muatik.flj.flj.UI.entities.Job;
import com.muatik.flj.flj.UI.fragments.AlarmSuggestion;
import com.muatik.flj.flj.UI.entities.JobFilter;
import com.muatik.flj.flj.UI.styles.DividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by muatik on 22.07.2016.
 */

public class JobList extends ActivityWithDrawer {

//    /** EVENTS */
//    public static String STATE_onViewCreated = "onViewCreated";
    private AlarmSuggestion alarmSuggestion;
//
//    public class EventFragmentState {
//        public String state;
//        public EventFragmentState(String state) {
//            this.state = state;
//        }
//    }


    /**
     * specify how many milliseconds should be passed before
     * making a api request.
     */
    private static final int REQUEST_DELAY = 1000;
    int pageLength = 7;

    private static final String JOB_LIST_STATE_NAME = "jobList";

    private JobFilter jobFilter;

    EndlessRecyclerOnScrollListener infiniteScroller;
    JobsRecyclerViewAdapter adapter;
    ArrayList<Job> jobs;
    RecyclerView listView;
    Handler delayedRequest ;
    SwipeRefreshLayout swipeRefreshLayout;
    boolean isSugesstionGone = false;
    /**
     * specify next job list page's maximum job id.
     * all jobs to be listed in the next page will be older
     * than this maxId.
     */
    private String maxId;

    /**
     * specify next job list page's maximum job id.
     * all jobs to be listed in the next page will be older
     * than this maxId.
     */
    private String sinceId;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.jobs_list_activity);

        jobFilter = (JobFilter) getIntent().getSerializableExtra("jobFilter");
        if (jobFilter == null)
            throw new RuntimeException("joblist fragment needs job filter as an argument");


        init();
        prepareToolbar();

        if (!isSugesstionGone)
            showAlarmSuggestion();

        jobs = new ArrayList<Job>();
        adapter = new JobsRecyclerViewAdapter(this, jobs);
        listView = (RecyclerView) findViewById(R.id.posts_list);
        listView.addItemDecoration(new DividerItemDecoration(this));
        listView.setAdapter(adapter);
        listView.setLayoutManager(new LinearLayoutManager(this));

        // PREPARING SWIPE REFRESHER
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                resetMaxSinceId();
                refreshJobList();
            }
        });

        // @TODO: if no any job posts found, show a message saying this.
        if(savedInstanceState != null && savedInstanceState.getSerializable(JOB_LIST_STATE_NAME) != null) {
            bindInfiniteScroller();
            populateJobList((ArrayList<Job>) savedInstanceState.getSerializable(JOB_LIST_STATE_NAME));
        } else {
            swipeRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    swipeRefreshLayout.setRefreshing(true);
                }
            });
            refreshJobList();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // removing fragment because fragment cannot bind its listener successfully.
        // so make it recreated
        getSupportFragmentManager().beginTransaction().remove(alarmSuggestion).commit();

        // prevent re-fetching job posts from the server.
        outState.putSerializable(JOB_LIST_STATE_NAME, jobs);

        super.onSaveInstanceState(outState);
    }

    void showAlarmSuggestion() {
        alarmSuggestion = new AlarmSuggestion();
        alarmSuggestion.setListener(new AlarmSuggestion.Listener() {
            @Override
            public void onClose() {
                getSupportFragmentManager().beginTransaction()
                        .remove(alarmSuggestion).commit();
                isSugesstionGone = true;
            }

            @Override
            public JobFilter getJobFilter() {
                return jobFilter;
            }
        });

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.alarm_suggestion_fragment, alarmSuggestion)
                .commit();
    }

    void prepareToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("hlelo");
        if (jobFilter.keyword == null) {

        } else {
            toolbar.setTitle(jobFilter.keyword);
        }
        toolbar.setSubtitle("Jobs in " + jobFilter.city);
    }

    private Job getOldestJob() {
        // already sorted descending by created_at
        if (jobs.size() > 0)
            return jobs.get(jobs.size() -1);
        return null;
    }

    private void resetMaxSinceId() {
        maxId = null;
        sinceId = null;
    }

    private void updateMaxId() {
        Job job = getOldestJob();
        if (job !=null) {
            maxId = job.getId();
        } else {
            maxId = null;
        }
    }

    private void addProgressItem() {
        // adding a null item into the end of the list for progress item
        jobs.add(null);
        adapter.notifyItemInserted(jobs.size() - 1);
    }

    private void removeProgressItem() {
        if (jobs.size() > 1) {
            jobs.remove(jobs.size() - 1);
            adapter.notifyItemRemoved(jobs.size());
        }
    }

    private void bindInfiniteScroller() {
        infiniteScroller = new EndlessRecyclerOnScrollListener() {
            @Override
            public void onLoadMore() {

                if (swipeRefreshLayout.isRefreshing())
                    return;
                Log.d("FLJ", "loading more");
                updateMaxId();
                addProgressItem();
                fetchDelayedData();
            }
        };
        listView.addOnScrollListener(infiniteScroller);
    }

    private void refreshJobList() {
        bindInfiniteScroller();
        jobs.clear();
        adapter.notifyDataSetChanged();
        infiniteScroller.setDontListen(false);
        updateMaxId();
        fetchDelayedData();
    }

    private void fetchDelayedData() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {fetchData();}
        };

        if (delayedRequest != null)
            delayedRequest.removeCallbacksAndMessages(null);

        delayedRequest = new Handler();
        delayedRequest.postDelayed(runnable, REQUEST_DELAY);
    }

    private void fetchData() {
        Call<List<Job>> call;
        call = API.anonymous.getJobs(jobFilter.keyword, maxId, sinceId);
        call.enqueue(new Callback<List<Job>>() {
            @Override
            public void onResponse(Call<List<Job>> call, Response<List<Job>> response) {
                if (response.code() == 200) {
                    populateJobList(response.body());
                    onFetchDataDone();
                }
            }
            @Override
            public void onFailure(Call<List<Job>> call, Throwable t) {
                // @TODO: show an error message to user
                onFetchDataDone();
            }
        });
    }

    private void onFetchDataDone() {
        Log.d("FLJ", "onFetchDataDone");
        swipeRefreshLayout.setRefreshing(false);
    }

    private void populateJobList(List<Job> new_jobs) {
        removeProgressItem();
        for (Job j:new_jobs) {
            jobs.add(j);
            adapter.notifyItemInserted(jobs.size());
        }
        infiniteScroller.setLoading(false);

        if (new_jobs.size() < pageLength)
            infiniteScroller.setDontListen(true);
    }


//    @Subscribe
//    public void onJobClicked(JobViewHolder.EventOnJobClicked event) {
//
//        Bundle bundle = new Bundle();
//        bundle.putSerializable("job", event.job);
//
//        startActivity(new Intent(this, JobDetail.class), bundle);
//    }

}
