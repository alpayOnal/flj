package com.muatik.flj.flj.UI.fragments;

import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.muatik.flj.flj.R;
import com.muatik.flj.flj.UI.adapters.JobsRecyclerViewAdapter;
import com.muatik.flj.flj.UI.adapters.OnLoadListener;
import com.muatik.flj.flj.UI.adapters.ViewHandler;
import com.muatik.flj.flj.UI.entities.Job;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by muatik on 24.07.2016.
 */
public class InfiniteListView<Entity, ViewHolder> extends Fragment implements OnLoadListener {

    public int listView_resource;
    public int entity_view_resource;
    public int loading_view_resource;
    public ViewHandler viewHandler;
    public Class holder;
    public Runnable apiService;

//    public void initJobListView() {
//        RecyclerView listView = (RecyclerView) getView().findViewById(listView_resource);
//        JobsRecyclerViewAdapter adapter = new JobsRecyclerViewAdapter<Entity, ViewHolder>(
//                listView,
//                new ArrayList<Entity>(),
//                this.entity_view_resource,
//                this.loading_view_resource,
//                this.viewHandler
//        );
//
//        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
//        adapter.setOnLoadListener(this);
//        listView.setAdapter(adapter);
//        listView.setLayoutManager(layoutManager);
//    }

    @Override
    public void onLoadMore() {
//        Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl("http://192.168.2.25:8000/")
//                .addConverterFactory(GsonConverterFactory.create())
//                .build();
//
//        FLJ_ENDPOIN apiService = retrofit.create(FLJ_ENDPOIN.class);
//        Call<List<Job>> call = apiService.getJobs();
//        call.enqueue(new Callback<List<Job>>() {
//            @Override
//            public void onResponse(Call<List<Job>> call, Response<List<Job>> response) {
//                Log.d("", "jj");
//                List<Job> jobs = response.body();
//                JobsRecyclerViewAdapter a = new JobsRecyclerViewAdapter(getContext(), jobs);
//                jobListView.setAdapter(a);
//            }
//
//            @Override
//            public void onFailure(Call<List<Job>> call, Throwable t) {
//                Log.d("", "jj");
//            }
//        });

    }
}
