package com.muatik.flj.flj.UI.entities;

import java.io.Serializable;

/**
 * Created by muatik on 7/28/16.
 */
public class Account implements Serializable {

    private int id;
    private String first_name;
    private String last_name;
    private String username;
    private String email;
    private String password;

    public UserProfile userprofile;
    private int firstName;

    public Account(int id, String username, String email, String password) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public Account(String first_name, String last_name, String email, String username, String password) {
        this.first_name = first_name;
        this.last_name = last_name;
        this.email = email;
        this.username = username;
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

    public int getId() {
        return id;
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
