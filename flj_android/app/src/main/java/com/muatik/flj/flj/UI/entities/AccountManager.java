package com.muatik.flj.flj.UI.entities;

import android.util.Base64;
import android.util.Log;

import com.google.gson.Gson;
import com.muatik.flj.flj.UI.RESTful.API;
import com.muatik.flj.flj.UI.utilities.BusManager;
import com.muatik.flj.flj.UI.views.BaseViewHolder;

import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.HEAD;
import retrofit2.http.Header;

/**
 * Created by muatik on 7/28/16.
 */
public class AccountManager {

    public static class onSignInCompleted {}
    public static class onSuccessfulSignIn {
        public Account account;
        public onSuccessfulSignIn(Account authenticated) {

            this.account = authenticated;
        }
    }
    private static class onSignInFailure {}

    private static Account authenticated;

    public static void signin(final String username, final String password) {
        API.setAuthHeaderInterceptor(new API.BasicAuth(username, password));
        Call<Account> call = API.authorized.getAuthenticatedUser();
        call.enqueue(new Callback<Account>() {
            @Override
            public void onResponse(Call<Account> call, Response<Account> response) {
                if (response.isSuccessful()) {
                    authenticated = response.body();
                    BusManager.get().post(new onSuccessfulSignIn(authenticated));
                    BusManager.get().post(new onSignInCompleted());
                }
            }

            @Override
            public void onFailure(Call<Account> call, Throwable t) {
                Log.d("FLJ", "alarm post failure: " + t.getMessage());
                BusManager.get().post(new onSignInFailure());
                BusManager.get().post(new onSignInCompleted());
            }
        });
    }

    public static void signinViaGoogle(String token, String email, String displayname) {
//        BusManager.get().post(new onSuccessfulSignIn(authenticated));
//        BusManager.get().post(new onSignInCompleted());
//        BusManager.get().post(new onSignInFailure());
        API.anonymous.getCredential(token).enqueue(new API.BriefCallback<Gson>() {
            @Override
            public void onSuccess(Call<Gson> call, Response<Gson> response) {
                String credential = "";
                String email = "";
                API.setAuthHeaderInterceptor(new API.GoogleSignin(email, credential));
//                authenticated = response.body();
//                API.setBasicAuth(username, password);
//                BusManager.get().post(new onSuccessfulSignIn(authenticated));
//                BusManager.get().post(new onSignInCompleted());
            }
        });
    }

    public static boolean isAuthenticated() {
        return authenticated != null;
    }


}