package com.example.myapplication.rentcarapp.model.firestore.models;

public class Review {
    private String id;
    private String client;
    private String car;
    private float rating;
    private String text;
    private String date;

    public Review(){}

    public Review(String id, String client, String car, float rating, String text, String date) {
        this.id = id;
        this.client = client;
        this.car = car;
        this.rating = rating;
        this.text = text;
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }

    public String getCar() {
        return car;
    }

    public void setCar(String car) {
        this.car = car;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
