package com.muatik.flj.flj.UI.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.muatik.flj.flj.R;
import com.muatik.flj.flj.UI.utilities.BusManager;
import com.muatik.flj.flj.UI.adapters.EndlessRecyclerOnScrollListener;
import com.muatik.flj.flj.UI.adapters.JobsRecyclerViewAdapter;
import com.muatik.flj.flj.UI.entities.Job;
import com.muatik.flj.flj.UI.entities.JobFilter;
import com.muatik.flj.flj.UI.styles.DividerItemDecoration;
import com.squareup.otto.Bus;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by muatik on 22.07.2016.
 */
public class JobList extends Fragment {

    /** EVENTS */
    public static String STATE_onViewCreated = "onViewCreated";

    public class EventFragmentState {
        public String state;
        public EventFragmentState(String state) {
            this.state = state;
        }
    }


    /**
     * specify how many milliseconds should be passed before
     * making a api request.
     */
    private static final int REQUEST_DELAY = 1000;
    int pageLength = 7;

    private JobFilter jobFilter;

    private Bus bus = BusManager.get();

    /**
     * holds jobs list as a static variable in order to keep the list as same as it is when
     * user comes back to this fragment.
     */
    public static Bundle savedState;

    EndlessRecyclerOnScrollListener infiniteScroller;
    JobsRecyclerViewAdapter adapter;
    ArrayList<Job> jobs;
    RecyclerView listView;
    Handler delayedRequest ;

    SwipeRefreshLayout swipeRefreshLayout;

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


    public interface FLJ_ENDPOINT {
        @GET("posts/")
        Call<List<Job>> getJobs(
                @Query("keyword") String keyword,
                @Query("maxId") String maxId,
                @Query("sinceId") String sinceId
        );
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        // if jobFilter is null, then the view is being created and this means
        // there is not list history to be kept, savedState must be empty.
        if (jobFilter == null)
            savedState = new Bundle();

        jobFilter = (JobFilter) getArguments().getSerializable("jobFilter");
        if (jobFilter == null)
            throw new RuntimeException("joblist fragment needs job filter as an argument");


        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.jobs_list_content, container, false);
    }

    void prepareToolbar() {
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle("hlelo");
        if (jobFilter.keyword == null) {

        } else {
            toolbar.setTitle(jobFilter.keyword);
        }
        toolbar.setSubtitle("Jobs in " + jobFilter.location);
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.job_list_action_bar, menu);
        prepareToolbar();
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // PREPARING LIST VIEW
        jobs = new ArrayList<Job>();
        adapter = new JobsRecyclerViewAdapter(getContext(), jobs);
        listView = (RecyclerView) getView().findViewById(R.id.posts_list);
        listView.addItemDecoration(new DividerItemDecoration(getActivity()));
        listView.setAdapter(adapter);
        listView.setLayoutManager(new LinearLayoutManager(getContext()));

        // PREPARING SWIPE REFRESHER
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swiperefresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                resetMaxSinceId();
                refreshJobList();
            }
        });


        if(savedState != null && savedState.getSerializable("jobs") != null) {
            bindInfiniteScroller();
            populateJobList((ArrayList<Job>) savedState.getSerializable("jobs"));
        } else {
            swipeRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    swipeRefreshLayout.setRefreshing(true);
                }
            });
            refreshJobList();
        }

        bus.register(this);
        bus.post(new EventFragmentState("onViewCreated"));

//        ((Button) view.findViewById(R.id.alarm_suggestion_not_now)).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//              view.findViewById(R.id.alarm_suggestion).setVisibility(View.GONE);
//            }
//        });
//
//        ((Button) view.findViewById(R.id.alarm_suggestion_yes)).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(getContext(), "You will get notified about new '"
//                        + jobFilter.keyword
//                        + "' jobs in "
//                        + jobFilter.location, Toast.LENGTH_LONG).show();
//                view.findViewById(R.id.alarm_suggestion).setVisibility(View.GONE);
//            }
//        });
    }

    @Override
    public void onDestroyView() {
        savedState.putSerializable("jobs", jobs);
        bus.unregister(this);
        super.onDestroyView();
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
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.2.25:8000/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        FLJ_ENDPOINT apiService = retrofit.create(FLJ_ENDPOINT.class);

        Call<List<Job>> call;
        call = apiService.getJobs(jobFilter.keyword, maxId, sinceId);

        call.enqueue(new Callback<List<Job>>() {
            @Override
            public void onResponse(Call<List<Job>> call, Response<List<Job>> response) {
                populateJobList(response.body());
                onFetchDataDone();
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

}
