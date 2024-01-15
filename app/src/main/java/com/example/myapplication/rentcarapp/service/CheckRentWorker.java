package com.example.myapplication.rentcarapp.service;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import com.example.myapplication.rentcarapp.model.firestore.models.Rent;
import com.example.myapplication.rentcarapp.model.network.NetworkConnect;
import com.example.myapplication.rentcarapp.model.network.NotificationData;
import com.example.myapplication.rentcarapp.model.network.NotificationSender;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

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

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
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
        Gson gson = new Gson();
        String rentList = (String) getInputData().getKeyValueMap().get("Data");
        String token = (String) getInputData().getKeyValueMap().get("Token");
        List<Rent> rents = gson.fromJson(rentList, new TypeToken<List<Rent>>() {}.getType());
        for (Rent rent: rents){
            if(checkDifferenceBetweenTwoDates(rent.getReturnDate())){
                sendMessage(rent, token);
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

    private void sendMessage(Rent rent, String token){
        NotificationSender notification = new NotificationSender(token, new NotificationData(rent));
        NetworkConnect.getInstance().getApi().sendNotification(notification).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if(response.isSuccessful()){
                    Log.i("Success", response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Log.i("Failed", "Error");
            }
        });
    }

}
