package com.example.myapplication.rentcarapp.view.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.example.myapplication.rentcarapp.R;
import com.example.myapplication.rentcarapp.viewmodel.AuthViewModel;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class SplashScreenActivity extends AppCompatActivity {
    AuthViewModel authViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

    }

    @Override
    protected void onStart() {
        super.onStart();
        authViewModel.getClient().observe(this, client -> {
            if (client == null) {
                SplashScreenActivity.this.usingIntent(MainActivity.class);
            } else {
                SplashScreenActivity.this.usingIntent(MainWindowActivity.class);
            }
        });
    }



    private void usingIntent(Class<?> currentClass){
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(SplashScreenActivity.this, currentClass);
            startActivity(intent);
        }, 3000);
    }
}