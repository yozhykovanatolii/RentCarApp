package com.example.myapplication.rentcarapp.service;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.myapplication.rentcarapp.R;
import com.example.myapplication.rentcarapp.model.firestore.models.Rent;
import com.example.myapplication.rentcarapp.view.activity.RentDetailActivity;
import com.example.myapplication.rentcarapp.view.activity.SplashScreenActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;

import java.util.Objects;
import java.util.Random;

public class RentCarFirebaseMessagingService extends FirebaseMessagingService {
    private FirebaseFirestore firestore;
    private FirebaseUser firebaseUser;

    public RentCarFirebaseMessagingService() {
        firestore = FirebaseFirestore.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);
        Gson gson = new Gson();
        String data = message.getData().get("rent");
        Rent rent = gson.fromJson(data, Rent.class);
        int notificationID = new Random().nextInt();
        Intent intent = getIntent(rent);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);
        sendNotification(pendingIntent, rent, notificationID);
    }

    @Override
    public void onNewToken(@NonNull String token) {
        sendRegistrationTokenToServer(token);
    }

    private void sendRegistrationTokenToServer(String token) {
        Log.i("Token", token);
        String idClient = Objects.requireNonNull(firebaseUser).getUid();
        firestore.collection("users").document(idClient).update("fcmToken", token).addOnSuccessListener(unused -> Log.i("Success", "Registration token updated")).addOnFailureListener(e -> Log.i("Error", "Update registration token exception: ", e));
    }

    private Intent getIntent(Rent rent){
        Intent intent;
        if(firebaseUser != null){
            intent = new Intent(this, RentDetailActivity.class);
            intent.putExtra("Rent", rent);
        }else{
            intent = new Intent(this, SplashScreenActivity.class);
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        return intent;
    }

    private void sendNotification(PendingIntent pendingIntent, Rent rent, int notificationID) {
        createNotificationManager();
        NotificationCompat.Builder notification = createNotification(pendingIntent, rent);
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        notificationManagerCompat.notify(notificationID, notification.build());
    }

    private NotificationCompat.Builder createNotification(PendingIntent pendingIntent, Rent rent){
        return new NotificationCompat.Builder(this, "CHANNEL_ID")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Rental period expires")
                .setContentText("Your " + rent.getCar() + " rental car is expiring. The car must be returned")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);
    }

    private void createNotificationManager(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            CharSequence name = "Rent Car";
            String description = "This notification channel is used for rents notifications";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            createNotificationChannel(name, importance, description);
        }
    }

    private void createNotificationChannel(CharSequence name, int importance, String description){
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("CHANNEL_ID", name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
