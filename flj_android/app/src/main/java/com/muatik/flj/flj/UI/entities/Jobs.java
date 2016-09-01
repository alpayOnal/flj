package com.muatik.flj.flj.UI.entities;

import com.muatik.flj.flj.UI.RESTful.API;
import com.muatik.flj.flj.UI.utilities.BusManager;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by alpay on 9/1/16.
 */
public class Jobs {


    public static class EventOnInsert {
        public Job job;
        public EventOnInsert(Job job) {
            this.job = job;
        }
    }

    public static class EventOnInsertFailure {
        public Job job;
        public Throwable error;
        public String message;

        public EventOnInsertFailure(Job job, Throwable t, String message) {
            this.job = job;
            this.error = t;
            this.message = message;
        }
    }

    public void insertJob(final Job newJob) {
        Call<Job> call;
        call = API.authorized.saveJob(newJob);
        call.enqueue(new API.BriefCallback<Job>() {
            @Override
            public void onFailure(Call<Job> call, Throwable t, Response<Job> response) {
                if (response != null){
                    ResponseBody body = response.errorBody();
                    try {
                        BusManager.get().post(new EventOnInsertFailure(newJob, t, body.string()));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }else {
                    BusManager.get().post(new EventOnInsertFailure(newJob, t, t.getMessage()));
                }
            }


            @Override
            public void onSuccess(Call<Job> call, Response<Job> response) {
                BusManager.get().post(new EventOnInsert(response.body()));
            }
        });
    }
}
