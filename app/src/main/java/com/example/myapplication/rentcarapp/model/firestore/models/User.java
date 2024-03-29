package com.example.myapplication.rentcarapp.model.firestore.models;

import java.io.Serializable;

public class User implements Serializable {
    private String token;
    private String fcmToken;
    private String username;
    private String password;

    public User(){}

    public User(String username, String password) {
        this.username = username;
        this.password = password;
        fcmToken = "";
    }

    public String getToken(){return token;}

    public void setToken(String token){this.token = token;}

    public String getFcmToken() {
        return fcmToken;
    }

    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
