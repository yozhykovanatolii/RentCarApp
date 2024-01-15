package com.example.myapplication.rentcarapp.model.network;

public class NotificationSender {
    private String to;
    private NotificationData data;

    public NotificationSender(String to, NotificationData data) {
        this.to = to;
        this.data = data;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public NotificationData getData() {
        return data;
    }

    public void setData(NotificationData data) {
        this.data = data;
    }
}
