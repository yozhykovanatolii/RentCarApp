package com.example.myapplication.rentcarapp.model.network;

import com.example.myapplication.rentcarapp.model.firestore.models.Rent;

import java.util.List;

public class NotificationData {
    private Rent rent;

    public NotificationData(Rent rent) {
        this.rent = rent;
    }

    public Rent getRent() {
        return rent;
    }

    public void setRent(Rent rent) {
        this.rent = rent;
    }
}
