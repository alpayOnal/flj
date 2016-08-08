package com.muatik.flj.flj.UI.entities;

import android.util.Log;

import com.muatik.flj.flj.UI.RESTful.API;
import com.muatik.flj.flj.UI.utilities.BusManager;

import java.io.Serializable;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by muatik on 7/28/16.
 */
public class Account implements Serializable {

    private int userId;
    private String first_name;
    private String last_name;
    private String username;
    private String email;
    private String password;

    public UserProfile userprofile;
    private int firstName;

    public Account(int userId, String username, String email, String password) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getDisplayName() {
        if (first_name != null && first_name.length() > 0) {
            return first_name + " " + last_name;
        }
        return "";
    }

    public String getFirstName() {
        return first_name;
    }

    public String getLast_name() {
        return first_name;
    }

    public static class UserProfile {

        private String created_at;
        private String gcm_id;
        private String picture;
        private String credential;

        public String getCreated_at() {
            return created_at;
        }

        public String getGcm_id() {
            return gcm_id;
        }

        public String getPicture() {
            return picture;
        }

        public String getCredential() {
            return credential;
        }
    }
}
