package com.muatik.flj.flj.UI.RESTful;

import android.util.Base64;
import android.util.Log;

import com.muatik.flj.flj.UI.entities.Account;
import com.muatik.flj.flj.UI.entities.Alarm;
import com.muatik.flj.flj.UI.entities.Job;

import java.io.IOException;
import java.util.List;

import okhttp3.Authenticator;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;
import okio.Buffer;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by muatik on 7/28/16.
 */
public class API {

    public static final int HTTP_POST_SUCCESS = 201;

    static String username = "aaa";
    static String password = "123456";

    static public void setBasicAuth(String username, String password) {
        API.username = username;
        API.password = password;
    }

    private static String bodyToString(final Request request){

        try {
            final Request copy = request.newBuilder().build();
            final Buffer buffer = new Buffer();
            copy.body().writeTo(buffer);
            return buffer.readUtf8();
        } catch (Exception e) {
            return "did not work";
        }
    }




    abstract public static class BriefCallback<T> implements Callback<T> {
        @Override
        public void onResponse(Call<T> call, retrofit2.Response<T> response) {
            if (response.isSuccessful()) {
                onSuccess(call, response);
            } else {
                this.onFailure(call, new Exception(response.code() + " - " + response.message()));
            }
        }

        @Override
        public void onFailure(Call<T> call, Throwable t) {
            Log.d("FLJ", "Failed: " + t.getMessage());
        }

        public abstract void onSuccess(Call<T> call, retrofit2.Response<T> response);
    }

    static class LoggingInterceptor implements Interceptor {
        @Override public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();

            long t1 = System.nanoTime();
            Log.d("OkHttp", String.format("Sending request %s on %s%n%s",
                    request.url(), chain.connection(), request.headers()));
            Log.d("OkHttp", String.format("Sending request %s on %s%n%s",
                    request.url(), chain.connection(), bodyToString(request).toString() ));


            Response response = chain.proceed(request);

            long t2 = System.nanoTime();
            Log.d("OkHttp", String.format("Received response for %s in %.1fms%n%s",
                    response.request().url(), (t2 - t1) / 1e6d, response.headers()));
            Log.d("OkHttp", String.format("Received response for %s in %.1fms%n%s",
                    response.request().url(), (t2 - t1) / 1e6d, response.message()));

            return response;
        }
    }

    static class AuthInterceptor implements Interceptor {

        @Override public Response intercept(Interceptor.Chain chain) throws IOException {

            // concatenate username and password with colon for authentication
            String credentials = username + ":" + password;

            // create Base64 encodet string
            final String basic =
                    "Basic " + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);

            Request request = chain.request().newBuilder()
                    .addHeader("Authorization", basic).build();
            return chain.proceed(request);
        }
    }


    static OkHttpClient client = new OkHttpClient().newBuilder()
            .addInterceptor(new LoggingInterceptor())
            .addInterceptor(new AuthInterceptor())
            .build();

    /**
     * This endpoints exposes APIS that do not require authentication.
     */
    public interface AnonymousENDPOIT {
        @GET("users/1/")
        Call<Account> getAuthenticatedUser();

        @GET("posts/")
        Call<List<Job>> getJobs(
                @Query("keyword") String keyword,
                @Query("maxId") String maxId,
                @Query("sinceId") String sinceId
        );
    }

    /**
     * This endpoint requires basic authentication.
     */
    public interface AuthorizedENDPOIT {
        @GET("users/1/")
        Call<Account> getAuthenticatedUser();

        @POST("alarms/")
        Call<Alarm> createAlarm(@Body Alarm alarm);

        @GET("alarms/")
        Call<List<Alarm>> getAlarms();

        @DELETE("alarms/{alarmId}/")
        Call<Void> deleteAlarm(@Path("alarmId") int alarmId);
    }



    public static Retrofit anonymousRetrofit = new Retrofit.Builder()
            .baseUrl("http://192.168.2.90:8000/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    public static Retrofit authorizedRetrofit = new Retrofit.Builder()
            .baseUrl("http://192.168.2.90:8000/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build();


    public static AnonymousENDPOIT anonymous = anonymousRetrofit.create(AnonymousENDPOIT.class);
    public static AuthorizedENDPOIT authorized = authorizedRetrofit.create(AuthorizedENDPOIT.class);

}
