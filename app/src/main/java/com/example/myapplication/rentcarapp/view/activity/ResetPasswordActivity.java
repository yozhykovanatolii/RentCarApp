package com.example.myapplication.rentcarapp.view.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.rentcarapp.R;
import com.example.myapplication.rentcarapp.model.firestore.models.User;
import com.example.myapplication.rentcarapp.receiver.InternetReceiver;
import com.example.myapplication.rentcarapp.viewmodel.AuthViewModel;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Objects;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ResetPasswordActivity extends AppCompatActivity {
    TextInputEditText resetNewPassword, resetConfirmPassword;
    TextView incorrect_newPassword, incorrect_confirmedPassword;
    User user;
    String email;
    AuthViewModel authViewModel;
    BroadcastReceiver broadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        initComponents();
        user = (User) getIntent().getSerializableExtra("User");
        email = getIntent().getStringExtra("Email");
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);
        initBroadcastReceiver();
        editNewPassword();
        editConfirmedPassword();
    }

    private void initBroadcastReceiver(){
        broadcastReceiver = new InternetReceiver();
        registerReceiver(broadcastReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    private void initComponents(){
        resetNewPassword = findViewById(R.id.resetNewPassword);
        resetConfirmPassword = findViewById(R.id.resetConfirmPassword);
        incorrect_newPassword = findViewById(R.id.errorNewPassword);
        incorrect_confirmedPassword = findViewById(R.id.errorConfirmedPassword);
    }

    private void editNewPassword(){
        resetNewPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkNewPassword();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void editConfirmedPassword(){
        resetConfirmPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkConfirmedPassword();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void checkNewPassword(){
        String newPassword = Objects.requireNonNull(resetNewPassword.getText()).toString();
        authViewModel.isPasswordWriteCorrect(newPassword).observe(this, aBoolean -> {
            if(aBoolean){
                incorrect_newPassword.setVisibility(View.GONE);
            }else{
                incorrect_newPassword.setVisibility(View.VISIBLE);
            }
        });
    }

    private void checkConfirmedPassword(){
        String newPassword = Objects.requireNonNull(resetNewPassword.getText()).toString();
        String confirmNewPassword = Objects.requireNonNull(resetConfirmPassword.getText()).toString();
        authViewModel.isNewPasswordEqualConfirmedPassword(newPassword, confirmNewPassword).observe(this, aBoolean -> {
            if(aBoolean){
                incorrect_confirmedPassword.setVisibility(View.GONE);
            }else{
                incorrect_confirmedPassword.setVisibility(View.VISIBLE);
            }
        });
    }

    public void confirmPassword(View view){
        String newPassword = Objects.requireNonNull(resetNewPassword.getText()).toString();
        String confirmNewPassword = Objects.requireNonNull(resetConfirmPassword.getText()).toString();
        authViewModel.isNewPasswordEqualConfirmedPassword(newPassword, confirmNewPassword).observe(this, aBoolean -> {
            if(aBoolean){
                updatePassword();
                incorrect_newPassword.setVisibility(View.GONE);
                incorrect_confirmedPassword.setVisibility(View.GONE);
            }else{
                incorrect_newPassword.setVisibility(View.VISIBLE);
                incorrect_confirmedPassword.setVisibility(View.VISIBLE);
            }
        });
    }

    private void updatePassword(){
        String newPassword = Objects.requireNonNull(resetNewPassword.getText()).toString();
        authViewModel.updateUsersPassword(user, email, newPassword);
        Toast.makeText(getApplicationContext(), "Password update", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
    }
}