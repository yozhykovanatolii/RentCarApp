package com.example.myapplication.rentcarapp.service;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class RentCarFirebaseMessagingService extends FirebaseMessagingService {

    //Todo: Show notification about rent
    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);
    }

    @Override
    public void onNewToken(@NonNull String token) {
        sendTokenToServer(token);
    }

    //Todo: Add token to Firestore database
    private void sendTokenToServer(String token){
        Log.i("Token", token);
    }

    //Todo: Create notfication channel and notification
    private void sendNotification(){

    }

}
