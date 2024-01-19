package com.example.myapplication.rentcarapp.model.network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NetworkConnect {
    private static final String URL = "https://fcm.googleapis.com/";
    private Retrofit retrofit;
    private static NetworkConnect instance;

    private NetworkConnect(){
        retrofit = new Retrofit.Builder()
                .baseUrl(URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public static NetworkConnect getInstance(){
        if(instance == null){
            instance = new NetworkConnect();
        }
        return instance;
    }

    public ApiPoint getApi(){
        return retrofit.create(ApiPoint.class);
    }
}
