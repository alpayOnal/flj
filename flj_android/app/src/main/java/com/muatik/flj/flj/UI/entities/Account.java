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
    private String username;
    private String email;
    private String password;

    public UserProfile userprofile;

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

    public class UserProfile {

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
