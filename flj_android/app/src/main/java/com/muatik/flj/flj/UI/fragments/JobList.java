package com.muatik.flj.flj.UI.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.muatik.flj.flj.R;
import com.muatik.flj.flj.UI.BusManager;
import com.muatik.flj.flj.UI.adapters.EndlessRecyclerOnScrollListener;
import com.muatik.flj.flj.UI.adapters.JobsRecyclerViewAdapter;
import com.muatik.flj.flj.UI.entities.Job;
import com.muatik.flj.flj.UI.styles.DividerItemDecoration;
import com.muatik.flj.flj.UI.views.SearchForm;
import com.squareup.otto.Subscribe;

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

    /**
     * specify how many milliseconds should be passed before
     * making a api request.
     */
    private static final int REQUEST_DELAY = 1400;
    public static final String ARG_KEYWORD = "keyword";
    public static final String ARG_LOCATION = "location";

    JobsRecyclerViewAdapter adapter;
    List<Job> jobs;
    RecyclerView listView;
    String keyword;
    Handler delayedRequest ;

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


    String keywrod;
    String location;

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
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.jobs_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle bundle = getArguments();
        keywrod = bundle.getString(ARG_KEYWORD);
        location = bundle.getString(ARG_LOCATION);

        jobs = new ArrayList<Job>();
        adapter = new JobsRecyclerViewAdapter(getContext(), jobs);
        listView = (RecyclerView) getView().findViewById(R.id.posts_list);
        listView.addItemDecoration(new DividerItemDecoration(getActivity()));
        listView.setAdapter(adapter);
        listView.setLayoutManager(new LinearLayoutManager(getContext()));
        BusManager.get().register(this);
        search();
    }



    @Subscribe
    public void onSearchFormUpdated(SearchForm.SearchFormChanged event) {
        resetJobList(event.form);
    }

    private Job getOldestJob() {
        // already sorted descending by created_at
        if (jobs.size() > 0)
            return jobs.get(jobs.size() -1);
        return null;
    }

    private void updateMaxId() {
        Job job = getOldestJob();
        if (job !=null) {
            maxId = job.getId();
        } else {
            maxId = null;
        }
    }

    private void resetJobList(SearchForm searchForm) {
        jobs.clear();
        adapter.notifyDataSetChanged();
        // binding again
        listView.addOnScrollListener(new EndlessRecyclerOnScrollListener() {
            @Override
            public void onLoadMore() {
                Log.d("flj", "onloadmore invoked");
                updateMaxId();
                fetchDelayedData(SearchForm.get());
            }
        });

        updateMaxId();
        fetchDelayedData(searchForm);
    }

    private void fetchDelayedData(final SearchForm searchForm) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Log.d("flj", "runnable.run");
                fetchData(searchForm);
            }
        };

        if (delayedRequest != null)
            delayedRequest.removeCallbacksAndMessages(null);

        delayedRequest = new Handler();
        delayedRequest.postDelayed(runnable, REQUEST_DELAY);
    }

    private void fetchData(SearchForm searchForm) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.2.25:8000/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        FLJ_ENDPOINT apiService = retrofit.create(FLJ_ENDPOINT.class);

        Call<List<Job>> call;
        call = apiService.getJobs(searchForm.getKeyword(), maxId, sinceId);

        //Log.d("flj", call.request().toString());
        call.enqueue(new Callback<List<Job>>() {
            @Override
            public void onResponse(Call<List<Job>> call, Response<List<Job>> response) {
                populateJobList(response.body());
            }

            @Override
            public void onFailure(Call<List<Job>> call, Throwable t) {
                // @TODO: show an error message to user
            }
        });
    }


    private void search() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.2.25:8000/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        FLJ_ENDPOINT apiService = retrofit.create(FLJ_ENDPOINT.class);

        Call<List<Job>> call;
        call = apiService.getJobs(keyword, maxId, sinceId);

        //Log.d("flj", call.request().toString());
        call.enqueue(new Callback<List<Job>>() {
            @Override
            public void onResponse(Call<List<Job>> call, Response<List<Job>> response) {
                populateJobList(response.body());
            }

            @Override
            public void onFailure(Call<List<Job>> call, Throwable t) {
                // @TODO: show an error message to user
            }
        });
    }

    private void populateJobList(List<Job> new_jobs) {
        for (Job j:new_jobs) {
            jobs.add(j);
            adapter.notifyItemInserted(jobs.size());
        }
    }

}
