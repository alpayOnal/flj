package com.muatik.flj.flj.UI.entities;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.muatik.flj.flj.UI.RESTful.API;
import com.muatik.flj.flj.UI.utilities.BusManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by muatik on 27.07.2016.
 */
public class Alarms {

    /** EVENTS **/
    public static class EventDataChanged {}
    public static class EventOnInsert {
        public Alarm alarm;
        public EventOnInsert(Alarm alarm) {
            this.alarm = alarm;
        }
    }
    public static class EventOnBulkInsert {}
    public static class EventOnFetchFailure {}
    public static class EventOnDelete {
        public Alarm alarm;
        public int position;
        public EventOnDelete(Alarm alarm, int position) {
            this.alarm = alarm;
            this.position = position;
        }
    }
    public static class EventOnInsertFailure {
        public Alarm alarm;
        public Throwable error;

        public EventOnInsertFailure(Alarm alarm, Throwable t) {
            this.alarm = alarm;
            this.error = t;
        }
    }

    /** THROWABLE **/
    public static class ReachedMaximumAlarm extends Throwable {}

    private static Context context;
    public static final int MAX_LENGTH = 4;
    private static final String PREFERENCE_NAME = "alarms";
    private static List<Alarm> data = new ArrayList<Alarm>();
    private static SharedPreferences prefs;
    private static boolean inited = false;
    public static void init(Context context) {
        Alarms.context = context;
        if (inited == false)
            refresh();
        inited = true;
    }

    public static void refresh() {
        API.authorized.getAlarms().enqueue(new Callback<List<Alarm>>() {
            @Override
            public void onResponse(Call<List<Alarm>> call, Response<List<Alarm>> response) {
                if (response.isSuccessful()) {
                    // data variable should keep the same instance,
                    // so data = response.body is not allowed. otherwise adapters may fail.
                    data.clear();
                    data.addAll(response.body());
                    BusManager.get().post(new EventDataChanged());
                    BusManager.get().post(new EventOnBulkInsert());
                } else {
                    this.onFailure(call, new Exception(
                            String.format("Alarms cannot be fetched. %s", response.message())));
                }
            }
            @Override
            public void onFailure(Call<List<Alarm>> call, Throwable t) {
                Toast.makeText(context, t.getMessage(), Toast.LENGTH_LONG ).show();
                BusManager.get().post(new EventOnFetchFailure());
            }
        });
    }

    public static void insert(Alarm alarm) throws ReachedMaximumAlarm {
        insert(alarm, true);
    }

    public static void insert(final Alarm alarm, final Boolean atBegining) throws ReachedMaximumAlarm {
        if (data.size() > MAX_LENGTH)
            throw new ReachedMaximumAlarm();

        Call<Alarm> call = API.authorized.createAlarm(alarm);
        call.enqueue(new API.BriefCallback<Alarm>() {
            @Override
            public void onSuccess(Call<Alarm> call, Response<Alarm> response) {
                alarm.setId(response.body().getId());
                if (atBegining)
                    data.add(0, alarm);
                else
                    data.add(alarm);

                BusManager.get().post(new EventDataChanged());
                BusManager.get().post(new EventOnInsert(alarm));
            }

            @Override
            public void onFailure(Call<Alarm> call, Throwable t, Response<Alarm> response) {
                Log.d("FLJ", "alarm post failure: " + t.getMessage());
                if (response != null) {
                    ResponseBody body = response.errorBody();
                    Log.d("FLJ", "alarm post failure: " + response.errorBody());
                }
                BusManager.get().post(new EventOnInsertFailure(alarm, t));
            }
         });


    }

    public static void delete(final Alarm alarm) {
        API.authorized.deleteAlarm(alarm.getId()).enqueue(new API.BriefCallback<Void>() {
            @Override
            public void onFailure(Call<Void> call, Throwable t, Response<Void> response) {
                Log.d("FLJ", "alarm delete failure: " + t.getMessage());
                if (response != null) {
                    ResponseBody body = response.errorBody();
                    Log.d("FLJ", "alarm delete failure: " + response.errorBody());
                }
            }

            @Override
            public void onSuccess(Call<Void> call, Response<Void> response) {
                int position = removeFromList(alarm);
                BusManager.get().post(new EventOnDelete(alarm, position));
                refresh();
            }
        });
    }

    private static int removeFromList(Alarm alarm) {
        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).getId() == alarm.getId()) {
                data.remove(i);
                return i;
            }
        }
        return -1;
    }

    public static List<Alarm> getAll() {
        return data;
    }

    public static boolean exists(Alarm alarm) {
        for (Alarm i : data) {
            if (i.isSame(alarm)) {
                return true;
            }
        }
        return false;
    }

}
