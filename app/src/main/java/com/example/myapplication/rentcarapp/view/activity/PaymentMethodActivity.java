package com.example.myapplication.rentcarapp.view.activity;

import androidx.appcompat.app.AppCompatActivity;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.myapplication.rentcarapp.R;
import com.example.myapplication.rentcarapp.receiver.InternetReceiver;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONObject;


public class PaymentMethodActivity extends AppCompatActivity implements PaymentResultListener {
    Checkout checkout;
    BroadcastReceiver broadcastReceiver;
    int amount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_method);
        checkout = new Checkout();
        Checkout.preload(getApplicationContext());
        checkout.setKeyID("rzp_test_QTBE3oLl2AJlMl");
        amount = getIntent().getIntExtra("Amount", 0);
        initBroadcastReceiver();
        startPayment();
    }

    private void initBroadcastReceiver(){
        broadcastReceiver = new InternetReceiver();
        registerReceiver(broadcastReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    private void startPayment(){
        amount *= 0.026;
        try {
            JSONObject options = new JSONObject();
            options.put("name", "Rent Car");
            options.put("image", "https://st2.depositphotos.com/7752738/11627/v/950/depositphotos_116276226-stock-illustration-rent-a-car-agency-vector.jpg");
            options.put("theme.color", "#3399cc");
            options.put("currency", "USD");
            options.put("amount", amount * 100);
            checkout.open(this, options);
        } catch(Exception e) {
            Log.e("Error", "Error in starting Razorpay Checkout", e);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
    }

    @Override
    public void onPaymentSuccess(String s) {
        Intent intent = new Intent(PaymentMethodActivity.this, SuccessfulActivity.class);
        startActivity(intent);
    }

    @Override
    public void onPaymentError(int i, String s) {
        Toast.makeText(getApplicationContext(), "Something worked incorrect. Error", Toast.LENGTH_LONG).show();
    }
}