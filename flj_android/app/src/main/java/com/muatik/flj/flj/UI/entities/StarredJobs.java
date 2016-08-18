package com.muatik.flj.flj.UI.entities;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.muatik.flj.flj.UI.RESTful.API;
import com.muatik.flj.flj.UI.utilities.BusManager;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by muatik on 8/18/16.
 */
public class StarredJobs {

    /** EVENTS **/
    public static class EventDataChanged {}
    public static class EventOnInsert {
        public StarredJob starredJob;
        public EventOnInsert(StarredJob starredJob) {
            this.starredJob = starredJob;
        }
    }
    public static class EventOnBulkInsert {}
    public static class EventOnFetchFailure {}
    public static class EventOnDelete {
        public StarredJob starredJob;
        public int position;
        public EventOnDelete(StarredJob starredJob, int position) {
            this.starredJob = starredJob;
            this.position = position;
        }
    }
    public static class EventOnInsertFailure {
        public StarredJob starredJob;
        public Throwable error;

        public EventOnInsertFailure(StarredJob starredJob, Throwable t) {
            this.starredJob = starredJob;
            this.error = t;
        }
    }

    /** THROWABLE **/
    public static class ReachedMaximumStarredJobs extends Throwable {}

    private static Context context;
    public static final int MAX_LENGTH = 4;
    private static final String PREFERENCE_NAME = "starredjobs";
    private static List<StarredJob> data = new ArrayList<StarredJob>();
    private static SharedPreferences prefs;
    private static boolean inited = false;

    public static void init(Context context) {
        StarredJobs.context = context;
        if (inited == false)
            refresh();
        inited = true;
    }

    public static void refresh() {
        API.authorized.getStarredJobs().enqueue(new API.BriefCallback<List<StarredJob>>() {
            @Override
            public void onSuccess(Call<List<StarredJob>> call, Response<List<StarredJob>> response) {
                data.clear();
                data.addAll(response.body());
                BusManager.get().post(new EventDataChanged());
                BusManager.get().post(new EventOnBulkInsert());
            }

            @Override
            public void onFailure(Call<List<StarredJob>> call, Throwable t, Response<List<StarredJob>> response) {
                Toast.makeText(context, t.getMessage(), Toast.LENGTH_LONG ).show();
                BusManager.get().post(new EventOnFetchFailure());
            }
        });
    }

    public static void insert(StarredJob starredJob) throws ReachedMaximumStarredJobs {
        insert(starredJob, true);
    }

    public static void insert(final StarredJob starredJob, final Boolean atBegining) throws ReachedMaximumStarredJobs {
        if (data.size() > MAX_LENGTH)
            throw new ReachedMaximumStarredJobs();

        API.authorized.starJob(starredJob).enqueue(new API.BriefCallback<StarredJob>() {
            @Override
            public void onSuccess(Call<StarredJob> call, Response<StarredJob> response) {

                if (atBegining)
                    data.add(0, starredJob);
                else
                    data.add(starredJob);

                BusManager.get().post(new EventDataChanged());
                BusManager.get().post(new EventOnInsert(starredJob));
            }

            @Override
            public void onFailure(Call<StarredJob> call, Throwable t, Response<StarredJob> response) {
                Log.d("FLJ", "starredJob cannot be starred failure: " + t.getMessage());
                if (response != null) {
                    ResponseBody body = response.errorBody();
                    Log.d("FLJ", "starredJob post failure: " + response.errorBody());
                }
                BusManager.get().post(new EventOnInsertFailure(starredJob, t));
            }
        });
    }

    public static void delete(final StarredJob starredJob) {
        API.authorized.unstarJob(starredJob.getId()).enqueue(new API.BriefCallback<Void>() {
            @Override
            public void onFailure(Call<Void> call, Throwable t, Response<Void> response) {
                Log.d("FLJ", "starredJob delete failure: " + t.getMessage());
                if (response != null) {
                    ResponseBody body = response.errorBody();
                    Log.d("FLJ", "starredJob delete failure: " + response.errorBody());
                }
            }

            @Override
            public void onSuccess(Call<Void> call, Response<Void> response) {
                int position = removeFromList(starredJob);
                BusManager.get().post(new EventOnDelete(starredJob, position));
                BusManager.get().post(new EventDataChanged());
            }
        });
    }

    private static int removeFromList(StarredJob starredJob) {
        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).isSame(starredJob)) {
                data.remove(i);
                return i;
            }
        }
        return -1;
    }

    public static List<StarredJob> getAll() {
        return data;
    }

    public static boolean exists(StarredJob starredJob) {
        for (StarredJob i : data) {
            if (i.isSame(starredJob)) {
                return true;
            }
        }
        return false;
    }

}
