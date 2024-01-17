package com.example.myapplication.rentcarapp.view.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.myapplication.rentcarapp.R;
import com.example.myapplication.rentcarapp.adapter.CardAdapter;
import com.example.myapplication.rentcarapp.adapter.PaymentMethodAdapter;
import com.example.myapplication.rentcarapp.adapter.PaymentMethodInterface;
import com.example.myapplication.rentcarapp.model.firestore.models.CreditCard;
import com.example.myapplication.rentcarapp.receiver.InternetReceiver;
import com.example.myapplication.rentcarapp.viewmodel.CarViewModel;

import org.checkerframework.checker.units.qual.C;

import java.util.List;

import co.paystack.android.Paystack;
import co.paystack.android.PaystackSdk;
import co.paystack.android.Transaction;
import co.paystack.android.model.Card;
import co.paystack.android.model.Charge;

public class PaymentMethodActivity extends AppCompatActivity implements PaymentMethodInterface {
    RecyclerView recyclerView;
    PaymentMethodAdapter paymentMethodAdapter;
    CarViewModel carViewModel;
    List<CreditCard> creditCardsClient;
    CreditCard creditCard;
    BroadcastReceiver broadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_method);
        recyclerView = findViewById(R.id.cards);
        carViewModel = new ViewModelProvider(this).get(CarViewModel.class);
        initBroadcastReceiver();
        initData();
    }

    private void initBroadcastReceiver(){
        broadcastReceiver = new InternetReceiver();
        registerReceiver(broadcastReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    private void initData(){
        carViewModel.getClientCreditCards().observe(this, strings -> {
            if(strings != null){
                getCreditCards(strings);
            }
        });
    }

    private void getCreditCards(List<String> strings){
        carViewModel.getCreditCards(strings).observe(this, this::initRecyclerView);
    }

    private void initRecyclerView(List<CreditCard> creditCards){
        creditCardsClient = creditCards;
        paymentMethodAdapter = new PaymentMethodAdapter(creditCards, this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(paymentMethodAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    public void paidCard(View view){
        if(creditCard == null){
            Toast.makeText(this, "Choose payment method", Toast.LENGTH_LONG).show();
        }else{
            performCard();
        }
    }

    private void performCard(){
        String[] cardExpiryArray = creditCard.getValidDate().split("/");
        int expiryMonth = Integer.parseInt(cardExpiryArray[0]);
        int expiryYear = Integer.parseInt(cardExpiryArray[1]);
        Card card = new Card(creditCard.getNumberCard(), expiryMonth, expiryYear, creditCard.getSpecialCode());
        Charge charge = new Charge();
        charge.setCard(card);
        chargeCard(charge);
    }

    private void chargeCard(Charge charge){
        PaystackSdk.chargeCard(this, charge, new Paystack.TransactionCallback() {
            @Override
            public void onSuccess(Transaction transaction) {
                Intent intent = new Intent(PaymentMethodActivity.this, SuccessfulActivity.class);
                startActivity(intent);
            }

            @Override
            public void beforeValidate(Transaction transaction) {
                Log.i("PaymentMethodActivity", "Before validate: " + transaction.getReference());
            }

            @Override
            public void onError(Throwable error, Transaction transaction) {
                Toast.makeText(getApplicationContext(), "Something was happened. Error", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
    }

    @Override
    public void onClickRadioButton(int position) {
        creditCard = creditCardsClient.get(position);
    }
}