package com.muatik.flj.flj.UI.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.muatik.flj.flj.R;
import com.muatik.flj.flj.UI.adapters.EndlessRecyclerOnScrollListener;
import com.muatik.flj.flj.UI.adapters.EntityHolder;
import com.muatik.flj.flj.UI.adapters.JobViewHolder;
import com.muatik.flj.flj.UI.adapters.JobsRecyclerViewAdapter;
import com.muatik.flj.flj.UI.adapters.ViewHandler;
import com.muatik.flj.flj.UI.entities.Job;
import com.muatik.flj.flj.UI.styles.DividerItemDecoration;
import com.muatik.flj.flj.UI.styles.VerticalSpaceItemDecoration;

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
    JobsRecyclerViewAdapter adapter;
    List<Job> jobs;
    RecyclerView listView;
    String keyword;

    public interface FLJ_ENDPOIN {
        @GET("posts")
        Call<List<Job>> getJobs(@Query("keyword") String keyword);
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

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.2.25:8000/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();


        jobs = new ArrayList<Job>();
        adapter = new JobsRecyclerViewAdapter(getContext(), jobs);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        listView = (RecyclerView) getView().findViewById(R.id.posts_list);

        listView.addItemDecoration(new DividerItemDecoration(getActivity()));

        listView.setAdapter(adapter);
        listView.setLayoutManager(layoutManager);
        listView.addOnScrollListener(new EndlessRecyclerOnScrollListener() {
            @Override
            public void onLoadMore() {
               loadData();


            }
        });
        loadData();


        EditText keywordBox = (EditText) getView().findViewById(R.id.asd);
        keywordBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                keyword = String.valueOf(editable);
                resetAdapter();
            }
        });
    }

    void resetAdapter() {
        jobs.clear();
        adapter.notifyDataSetChanged();
        loadData();
    }

    void loadData() {
        Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("http://192.168.2.25:8000/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();

        FLJ_ENDPOIN apiService = retrofit.create(FLJ_ENDPOIN.class);
        Call<List<Job>> call = apiService.getJobs(keyword);
        call.enqueue(new Callback<List<Job>>() {
            @Override
            public void onResponse(Call<List<Job>> call, Response<List<Job>> response) {
                Log.d("", "jj");
                List<Job> new_jobs = response.body();
                for (Job j:new_jobs) {
                    jobs.add(j);
                    adapter.notifyItemInserted(jobs.size());
                }

            }

            @Override
            public void onFailure(Call<List<Job>> call, Throwable t) {
                Log.d("", "jj");
            }
        });

//        Job j = new Job();
//        j.title = "elma";
//        j.country= "istanbul";
//        jobs.add(j);
//        adapter.notifyDataSetChanged();
//        adapter.notifyItemInserted(jobs.size());
    }

}
