package com.example.myapplication.rentcarapp.model.network;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
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

    private HttpLoggingInterceptor getLoggingInterceptor(){
        return new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY);
    }

    private OkHttpClient getClient(){
        return new OkHttpClient.Builder()
                .addInterceptor(getLoggingInterceptor())
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
