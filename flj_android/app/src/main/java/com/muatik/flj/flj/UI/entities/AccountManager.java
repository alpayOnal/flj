package com.muatik.flj.flj.UI.entities;

import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.muatik.flj.flj.UI.RESTful.API;
import com.muatik.flj.flj.UI.utilities.BusManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by muatik on 7/28/16.
 */
public class AccountManager {

    public static class onSignInCompleted {}
    public static class EventSuccessfulSignIn {
        public Account account;
        public EventSuccessfulSignIn(Account authenticated) {

            this.account = authenticated;
        }
    }
    private static class EventSignInFailure {}
    public static class EventSignout {}

    private static Account authenticated;
    private static String authenticator_type;
    private static API.AuthHeaderGenerator authenticator;
    private static SharedPreferences prefs;

    public static void init(SharedPreferences prefs) {
        // not to lose static values, init once.
        if (AccountManager.prefs == null) {
            AccountManager.prefs = prefs;
            loadState();
        }
    }

    public static void signinBasicAuth(final String username, final String password) {
        authenticator = new API.BasicAuth(username, password);
        API.setAuthHeaderInterceptor(authenticator);
        Call<Account> call = API.authorized.getAuthenticatedUser();
        call.enqueue(new Callback<Account>() {
            @Override
            public void onResponse(Call<Account> call, Response<Account> response) {
                if (response.isSuccessful()) {
                    authenticator_type = "BasicAuth";
                    authenticated = response.body();
                    BusManager.get().post(new EventSuccessfulSignIn(authenticated));
                    BusManager.get().post(new onSignInCompleted());
                }
            }

            @Override
            public void onFailure(Call<Account> call, Throwable t) {
                Log.d("FLJ", "alarm post failure: " + t.getMessage());
                BusManager.get().post(new EventSignInFailure());
                BusManager.get().post(new onSignInCompleted());
            }
        });
    }

    public static void signinViaGoogle(String token) {
        API.anonymous.verifyGoogleSignin(token).enqueue(new API.BriefCallback<Account>() {
            @Override
            public void onSuccess(Call<Account> call, Response<Account> response) {
                authenticated = response.body();
                String email = authenticated.getEmail();
                String credential = authenticated.userprofile.getCredential();
                authenticator_type = "GoogleSignin";
                authenticator = new API.GoogleSignin(email, credential);
                API.setAuthHeaderInterceptor(authenticator);
                BusManager.get().post(new EventSuccessfulSignIn(authenticated));
                BusManager.get().post(new onSignInCompleted());
            }
            @Override
            public void onFailure(Call<Account> call, Throwable t) {
                BusManager.get().post(new EventSignInFailure());
                BusManager.get().post(new onSignInCompleted());
            }
        });
    }

    public static void signinViaFacebook(String profileId, String token) {
        API.anonymous.verifyFacebookSignin(profileId, token).enqueue(new API.BriefCallback<Account>() {
            @Override
            public void onSuccess(Call<Account> call, Response<Account> response) {
                authenticated = response.body();
                authenticator_type = "FacebookSignin";
                authenticator = new API.FacebookSignin(
                        authenticated.getEmail(),
                        authenticated.userprofile.getCredential());
                API.setAuthHeaderInterceptor(authenticator);
                BusManager.get().post(new EventSuccessfulSignIn(authenticated));
                BusManager.get().post(new onSignInCompleted());
            }
            @Override
            public void onFailure(Call<Account> call, Throwable t) {
                BusManager.get().post(new EventSignInFailure());
                BusManager.get().post(new onSignInCompleted());
            }
        });
    }


    public static void signout() {
        authenticated = null;
        authenticator = null;
        authenticator_type = null;
        BusManager.get().post(new EventSignout());
    }


    public static boolean isAuthenticated() {
        return authenticated != null;
    }

    public static void saveState() {
        Gson gson = new Gson();
        prefs.edit()
                .putString("_accountManager_account", gson.toJson(authenticated))
                .putString("_accountManager_authenticator_type", authenticator_type)
                .putString("_accountManager_authenticator", gson.toJson(authenticator))
                .commit();
    }

    public static void loadState() {
        Gson gson = new Gson();
        authenticated = gson.fromJson(
                prefs.getString("_accountManager_account", null),
                Account.class);
        String a = prefs.getString("_accountManager_account", "");
        authenticated = gson.fromJson(a, Account.class);
        authenticator_type = prefs.getString("_accountManager_authenticator_type", "");
        String authenticator_json = prefs.getString("_accountManager_authenticator", null);

        switch (authenticator_type) {
            case "BasicAuth":
                authenticator = gson.fromJson(authenticator_json, API.BasicAuth.class);
            case "GoogleSignin":
                authenticator = gson.fromJson(authenticator_json, API.GoogleSignin.class);
            case "FacebookSignin":
                authenticator = gson.fromJson(authenticator_json, API.FacebookSignin.class);
        }
        API.setAuthHeaderInterceptor(authenticator);
    }

    public static void signupBasicAuth(Account account) {

        Call<Account> call;
        call = API.anonymous.basicAuthSignUp(account);
        call.enqueue(new Callback<Account>() {
            @Override
            public void onResponse(Call<Account> call, Response<Account> response) {
                if (response.isSuccessful()) {
                    Log.d("basic-auth","signup-success" + response.body());
                    authenticator_type = "BasicAuth";
                    authenticated = response.body();
                    BusManager.get().post(new EventSuccessfulSignIn(authenticated));
                    BusManager.get().post(new onSignInCompleted());
                }else{
                    Log.d("basic-auth","signup-error" + response.body());
                }
            }

            @Override
            public void onFailure(Call<Account> call, Throwable t) {
                Log.d("basic-auth","signup-failure" + t.getMessage());
                BusManager.get().post(new EventSignInFailure());
                BusManager.get().post(new onSignInCompleted());
            }
        });
    }
}