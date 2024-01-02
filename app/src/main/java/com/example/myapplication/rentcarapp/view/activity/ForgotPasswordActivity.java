package com.example.myapplication.rentcarapp.view.activity;

import androidx.appcompat.app.AppCompatActivity;
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

public class ForgotPasswordActivity extends AppCompatActivity {
    TextInputEditText emailText;
    TextView incorrect_email;
    AuthViewModel authViewModel;
    BroadcastReceiver broadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        emailText = findViewById(R.id.emailText);
        incorrect_email = findViewById(R.id.errorEmail);
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);
        initBroadcastReceiver();
        editEmail();
    }

    private void initBroadcastReceiver(){
        broadcastReceiver = new InternetReceiver();
        registerReceiver(broadcastReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    private void editEmail(){
        emailText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkEmail();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void checkEmail(){
        String email = Objects.requireNonNull(emailText.getText()).toString();
        authViewModel.getClientTokenByEmail(email).observe(this, token -> {
            if(token != null){
                incorrect_email.setVisibility(View.GONE);
            }else{
                incorrect_email.setVisibility(View.VISIBLE);
            }
        });
    }

    public void resetPassword(View view){
        String email = Objects.requireNonNull(emailText.getText()).toString();
        authViewModel.getClientTokenByEmail(email).observe(this, token -> {
            if(token != null){
                incorrect_email.setVisibility(View.GONE);
                getUser(token);
            }else{
                incorrect_email.setVisibility(View.VISIBLE);
            }
        });
    }

    private void getUser(String username){
        authViewModel.getUserByToken(username).observe(this, user -> {
            if(user != null){
                goToResetPasswordActivity(user);
            }else{
                Toast.makeText(getApplicationContext(), "Something happened wrong", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void goToResetPasswordActivity(User user){
        String email = Objects.requireNonNull(emailText.getText()).toString();
        Intent intent = new Intent(this, ResetPasswordActivity.class);
        intent.putExtra("User", user);
        intent.putExtra("Email", email);
        startActivity(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
    }
}