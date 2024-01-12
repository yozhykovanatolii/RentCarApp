package com.example.myapplication.rentcarapp.model.network;

import com.example.myapplication.rentcarapp.model.firestore.models.Rent;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface ApiPoint {
    @Headers({
                "Authorization: key=AAAAeAaK2VU:APA91bEHBoEZ05tX2stHcraLwJ2AN784mSdMKTyeq8W0FPUJVgkXMu2l5J_s2Pl7fB43TL6FXhqnPMMxR-gNa08nQyGPLcrfPH12BY8dbbXdo8dMnpWP9EHiL9i0Jr8dql6b8bSwdxkt",
                "Content-Type: application/json"})
    @POST("/fcm/send")
    Call<String> sendNotification(@Body String message);
}
