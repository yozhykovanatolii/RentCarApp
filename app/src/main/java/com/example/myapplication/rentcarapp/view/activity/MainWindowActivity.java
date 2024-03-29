package com.example.myapplication.rentcarapp.view.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.myapplication.rentcarapp.R;
import com.example.myapplication.rentcarapp.model.firestore.models.Car;
import com.example.myapplication.rentcarapp.receiver.InternetReceiver;
import com.example.myapplication.rentcarapp.view.fragment.FavoriteCarsFragment;
import com.example.myapplication.rentcarapp.view.fragment.HomeFragment;
import com.example.myapplication.rentcarapp.view.fragment.RentFragment;
import com.example.myapplication.rentcarapp.viewmodel.CarViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MainWindowActivity extends AppCompatActivity {
    BottomNavigationView navigationBottom;
    List<Car> cars;

    CarViewModel carViewModel;
    BroadcastReceiver broadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_window);
        navigationBottom = findViewById(R.id.navigationBottom);
        showFragment(savedInstanceState);
        navigationBottom.setOnItemSelectedListener(this::onItemClicked);
        cars = (List<Car>) getIntent().getSerializableExtra("FilterCar");
        carViewModel = new ViewModelProvider(this).get(CarViewModel.class);
        getRegistrationToken();
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

    private void showFragment(Bundle savedInstanceState){
        if(savedInstanceState == null){
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainer, HomeFragment.class, null)
                    .commit();
        }
    }

    private void getRegistrationToken(){
        carViewModel.getRegistrationToken().observe(this, this::updateFcmTokenInUsers);
    }

    private void updateFcmTokenInUsers(String fcmToken){
        System.out.println(fcmToken);
        carViewModel.updateFcmToken(fcmToken);
    }

    public List<Car> getCars() {
        return cars;
    }


    private boolean onItemClicked(MenuItem item){
        if(item.getItemId() == R.id.home){
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainer, HomeFragment.class, null)
                    .setReorderingAllowed(true)
                    .commit();
            return true;
        }if(item.getItemId() == R.id.rent){
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainer, RentFragment.class, null)
                    .setReorderingAllowed(true)
                    .commit();
            return true;
        }if(item.getItemId() == R.id.favorite){
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainer, FavoriteCarsFragment.class, null)
                    .setReorderingAllowed(true)
                    .commit();
            return true;
        }
        return false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
    }
}