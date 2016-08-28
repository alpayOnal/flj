package com.muatik.flj.flj.UI.RESTful;

import android.util.Base64;
import android.util.Log;

import com.muatik.flj.flj.UI.entities.Account;
import com.muatik.flj.flj.UI.entities.Alarm;
import com.muatik.flj.flj.UI.entities.Job;
import com.muatik.flj.flj.UI.entities.StarredJob;

import java.io.IOException;
import java.util.List;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
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
                Exception e;
                // @TODO: string.xml
                if (response.code() == 403)
                    e = new Exception(response.code() + " - Please login. ");
                else
                    e = new Exception(response.code() + " - " + response.message());

                this.onFailure(call, e, response);
            }
        }


        abstract public void onFailure(Call<T> call, Throwable t, retrofit2.Response<T> response);

        @Override
        public void onFailure(Call<T> call, Throwable t){
            onFailure(call, t, null);
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

    /** AUTHENTICATION INTERCEPTORS AND HELPERS */
    public static abstract  class AuthHeaderGenerator {
        public static String name = "AuthHeaderGenerator";

        public String getName() {
            return name;
        }

        abstract public Request injectHeaders(Request request);
    }

    public static class BasicAuth extends AuthHeaderGenerator {
        public final String name = "BasicAuth";
        private String username;
        private String password;

        public String getName() {
            return name;
        }

        public BasicAuth(String username, String password) {
            this.username = username;
            this.password = password;
        }

        @Override
        public Request injectHeaders(Request request) {
            // concatenate username and password with colon for authentication
            String credentials = username + ":" + password;

            // create Base64 encodet string
            final String basic =
                    "Basic " + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);

            return request.newBuilder()
                    .addHeader("Authorization", basic).build();
        }
    }

    public static class GoogleSignin extends AuthHeaderGenerator {
        public final String name = "GoogleSignin";
        private String username;
        private String credential;

        public GoogleSignin(String username, String credential) {
            this.username = username;
            this.credential = credential;
        }

        @Override
        public Request injectHeaders(Request request) {
            final String x_credential = String.format("%s:%s", new String[]{username, credential});
            return request.newBuilder()
                    .addHeader("X-USERNAME", username)
                    .addHeader("X-CREDENTIAL", credential).build();
        }
    }

    public static class FacebookSignin extends AuthHeaderGenerator {
        public final String name = "FacebookSignin";
        private String username;
        private String credential;

        public FacebookSignin(String username, String credential) {
            this.username = username;
            this.credential = credential;
        }

        @Override
        public Request injectHeaders(Request request) {
            return request.newBuilder()
                    .addHeader("X-USERNAME", username)
                    .addHeader("X-CREDENTIAL", credential)
                    .build();
        }
    }

    static AuthHeaderGenerator authHeaderGenerator;

    public static void setAuthHeaderInterceptor(AuthHeaderGenerator generator) {
        authHeaderGenerator= generator;
    }

    static class AuthInterceptor implements Interceptor {
        @Override public Response intercept(Interceptor.Chain chain) throws IOException {
            Request request = chain.request();
            if (authHeaderGenerator !=null)
                request = authHeaderGenerator.injectHeaders(request);
            return chain.proceed(request);
        }
    }
    /** ENF OF AUTHENTICATION INTERCEPTORS AND HELPERS */



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

        @FormUrlEncoded
        @POST("users/verifyGoogleSignin/")
        Call<Account> verifyGoogleSignin(@Field("token") String token);

        @FormUrlEncoded
        @POST("users/verifyFacebookSignin/")
        Call<Account> verifyFacebookSignin(
                @Field("profileId") String profileId, @Field("token") String token);

        @POST("users/")
        Call<Account> basicAuthSignUp(@Body Account account);

    }

    /**
     * This endpoint requires basic authentication.
     */
    public interface AuthorizedENDPOIT {
        @GET("users/me/")
        Call<Account> getAuthenticatedUser();

        @POST("alarms/")
        Call<Alarm> createAlarm(@Body Alarm alarm);

        @GET("alarms/")
        Call<List<Alarm>> getAlarms();

        @DELETE("alarms/{alarmId}/")
        Call<Void> deleteAlarm(@Path("alarmId") int alarmId);

        @GET("starredjobs/")
        Call<List<StarredJob>> getStarredJobs();

        @POST("starredjobs/")
        Call<StarredJob> starJob(@Body StarredJob starredJob);

        @DELETE("starredjobs/{starredjobId}/")
        Call<Void> unstarJob(@Path("starredjobId") String id);

        @PUT("posts/{jobId}/countView/")
        Call<Job> countView(@Path("jobId") String jobId);

        @PUT("users/{userId}/")
        Call<Account> updateAccout(@Path("userId") int userId, @Body Account account);
    }

    private static final String HOST = "http://192.168.1.105:8000/";

    static OkHttpClient anonymousClient = new OkHttpClient().newBuilder()
            .addInterceptor(new LoggingInterceptor())
            .build();

    static OkHttpClient authorizedClient = new OkHttpClient().newBuilder()
            .addInterceptor(new LoggingInterceptor())
            .addInterceptor(new AuthInterceptor())
            .build();

    public static Retrofit anonymousRetrofit = new Retrofit.Builder()
            .baseUrl(HOST)
            .client(anonymousClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    public static Retrofit authorizedRetrofit = new Retrofit.Builder()
            .baseUrl(HOST)
            .addConverterFactory(GsonConverterFactory.create())
            .client(authorizedClient)
            .build();


    public static AnonymousENDPOIT anonymous = anonymousRetrofit.create(AnonymousENDPOIT.class);
    public static AuthorizedENDPOIT authorized = authorizedRetrofit.create(AuthorizedENDPOIT.class);
}