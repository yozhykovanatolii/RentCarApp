package com.example.myapplication.rentcarapp.view.activity;


import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.ViewModelProvider;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;

import com.example.myapplication.rentcarapp.R;
import com.example.myapplication.rentcarapp.model.firestore.models.Car;
import com.example.myapplication.rentcarapp.receiver.InternetReceiver;
import com.example.myapplication.rentcarapp.viewmodel.CarViewModel;
import com.google.android.material.slider.RangeSlider;

import java.io.Serializable;
import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class FilterActivity extends AppCompatActivity {
    RangeSlider priceSlider;
    CardView checkAuto, checkMechanic, checkGasoline, checkElectric, existChildrenChair;
    CarViewModel carViewModel;
    BroadcastReceiver broadcastReceiver;
    String transmission, typeOfFuel;
    String isChairExist = "Not Exist";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);
        priceSlider = findViewById(R.id.priceSlider);
        carViewModel = new ViewModelProvider(this).get(CarViewModel.class);
        initComponents();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initBroadcastReceiver();
    }

    private void initBroadcastReceiver(){
        broadcastReceiver = new InternetReceiver();
        registerReceiver(broadcastReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    private void initComponents(){
        existChildrenChair = findViewById(R.id.existChildrenChair);
        existChildrenChair.setOnClickListener(this::clickExistChildrenChair);
        checkAuto = findViewById(R.id.checkAuto);
        checkAuto.setOnClickListener(this::clickTransmissionCards);
        checkMechanic = findViewById(R.id.checkMechanic);
        checkMechanic.setOnClickListener(this::clickTransmissionCards);
        checkGasoline = findViewById(R.id.checkGasoline);
        checkGasoline.setOnClickListener(this::clickTypeOfFuel);
        checkElectric = findViewById(R.id.checkElectric);
        checkElectric.setOnClickListener(this::clickTypeOfFuel);
    }

    public void apply(View view){
        List<Float> values = priceSlider.getValues();
        float minPrice = values.get(0);
        float maxPrice = values.get(1);
        getCarsByFilter(isChairExist, transmission, typeOfFuel, (int) minPrice, (int) maxPrice);
    }

    private void getCarsByFilter(String childrenChair, String transmission, String typeOfFuel, int minPrice, int maxPrice){
        carViewModel.confirmChoice(childrenChair, transmission, typeOfFuel, minPrice, maxPrice).observe(this, this::sendChoicesClient);
    }

    private void sendChoicesClient(List<Car> cars){
        Intent intent = new Intent(this, MainWindowActivity.class);
        intent.putExtra("FilterCar", (Serializable) cars);
        startActivity(intent);
    }

    public void clickTransmissionCards(View view){
        if(view.getId() == R.id.checkAuto){
            transmission = "Automaton";
            checkAuto.setCardBackgroundColor(getResources().getColor(R.color.blue));
        }if(view.getId() == R.id.checkMechanic){
            transmission = "Mechanic";
            checkMechanic.setCardBackgroundColor(getResources().getColor(R.color.blue));
        }
    }

    public void clickTypeOfFuel(View view){
        if(view.getId() == R.id.checkElectric){
            typeOfFuel = "Electric";
        }if(view.getId() == R.id.checkGasoline){
            typeOfFuel = "Gasoline";
        }
    }

    public void clickExistChildrenChair(View view){
        if(view.getId() == R.id.existChildrenChair){
            isChairExist = "Exist";
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
    }
}