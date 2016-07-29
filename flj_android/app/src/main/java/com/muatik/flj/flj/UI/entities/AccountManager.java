package com.muatik.flj.flj.UI.entities;

import android.util.Log;

import com.muatik.flj.flj.UI.RESTful.API;
import com.muatik.flj.flj.UI.utilities.BusManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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

    private static Account authenticated;

    public static void signin(String username, String password) {
        API.setBasicAuth(username, password);
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
                BusManager.get().post(new onSignInCompleted());
            }
        });
    }


    public static boolean isAuthenticated() {
        return authenticated != null;
    }
}