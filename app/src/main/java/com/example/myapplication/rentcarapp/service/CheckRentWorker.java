package com.example.myapplication.rentcarapp.service;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import com.example.myapplication.rentcarapp.model.firestore.models.Rent;
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
        return difference_In_Days >= 0;
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
        Map<String, String> dataMap = new HashMap<>();
        dataMap.put("rent", rentJsonString);

        /*
        RemoteMessage remoteMessage = new RemoteMessage.Builder("515505838421@gcm.googleapis.com")
                .setData(dataMap)
                .build();
       */
    }

    private JSONObject convertToJson(String rentJsonString){
        JSONObject payload = new JSONObject();
        try{
            JSONObject dataObject = new JSONObject();
            dataObject.put("rent", rentJsonString);
            payload.put("data", dataObject);
        }catch (JSONException exception){
            exception.printStackTrace();
        }
        return payload;
    }
}
