package com.example.myapplication.rentcarapp.view.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.myapplication.rentcarapp.R;
import com.example.myapplication.rentcarapp.adapter.CarAdapter;
import com.example.myapplication.rentcarapp.adapter.RecyclerViewInterface;
import com.example.myapplication.rentcarapp.model.firestore.models.Car;
import com.example.myapplication.rentcarapp.receiver.InternetReceiver;
import com.example.myapplication.rentcarapp.viewmodel.CarViewModel;

import java.util.ArrayList;
import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class FavoriteCarsActivity extends AppCompatActivity implements RecyclerViewInterface {
    RecyclerView favoriteCars;
    CarViewModel carViewModel;
    GridLayoutManager gridLayoutManager;
    CarAdapter carAdapter;
    List<Car> carList;
    BroadcastReceiver broadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite_cars);
        favoriteCars = findViewById(R.id.favoriteCars);
        carViewModel = new ViewModelProvider(this).get(CarViewModel.class);
        //initBroadcastReceiver();
        initData();
    }

    private void initBroadcastReceiver(){
        broadcastReceiver = new InternetReceiver();
        registerReceiver(broadcastReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    private void initData(){
        carViewModel.getFavoriteClientsCars().observe(this, strings -> {
            if(!strings.isEmpty()){
                getCars(strings);
            }
        });
    }

    private void getCars(List<String> strings){
        carViewModel.getCarsById(strings).observe(this, this::initRecyclerView);
    }

    private void initRecyclerView(List<Car> cars){
        carList = cars;
        gridLayoutManager = new GridLayoutManager(this, 2);
        carAdapter = new CarAdapter(cars, this);
        favoriteCars.setAdapter(carAdapter);
        favoriteCars.setLayoutManager(gridLayoutManager);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //unregisterReceiver(broadcastReceiver);
    }

    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra("Car", carList.get(position));
        startActivity(intent);
    }
}