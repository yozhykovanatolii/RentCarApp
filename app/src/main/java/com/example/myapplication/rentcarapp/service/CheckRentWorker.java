package com.example.myapplication.rentcarapp.service;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import com.example.myapplication.rentcarapp.model.firestore.models.Rent;
import com.example.myapplication.rentcarapp.model.network.ApiPoint;
import com.example.myapplication.rentcarapp.model.network.NetworkConnect;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class CheckRentWorker extends Worker {

    public CheckRentWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        checkRentsTerm();
        return Result.success();
    }

    private void checkRentsTerm(){
        List<Rent> rents = (List<Rent>) getInputData().getKeyValueMap().get("Data");
        for (Rent rent: rents){
            if(checkDifferenceBetweenTwoDates(rent.getReturnDate())){
                sendMessage(rent);
            }
        }
    }

    private boolean checkDifferenceBetweenTwoDates(String date){
        long difference_In_Days = checkDate(date);
        return difference_In_Days <= 0;
    }

    private long checkDate(String date){
        long difference_In_Days = 0;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        try {
            Date currentDate = new Date();
            Date returnDate = simpleDateFormat.parse(date);
            long difference_In_Time = returnDate.getTime() - currentDate.getTime();
            difference_In_Days = (difference_In_Time / (1000 * 60 * 60 * 24)) % 365;
        }catch (ParseException e){
            e.printStackTrace();
        }
        return difference_In_Days;
    }

    private void sendMessage(Rent rent){
        Gson gson = new Gson();
        String rentJsonString = gson.toJson(rent);
        String payload = convertJsonToString(rentJsonString);
        NetworkConnect.getInstance().getApi().sendNotification(payload).enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if(response.isSuccessful()){
                    System.out.println(response.body());
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                Log.i("ErrorPost", Objects.requireNonNull(t.getMessage()));
            }
        });
    }

    private String convertJsonToString(String rentJsonString){
        JSONObject payload = new JSONObject();
        try{
            JSONObject dataObject = new JSONObject();
            dataObject.put("rent", rentJsonString);
            payload.put("data", dataObject);
        }catch (JSONException exception){
            exception.printStackTrace();
        }
        return payload.toString();
    }
}
