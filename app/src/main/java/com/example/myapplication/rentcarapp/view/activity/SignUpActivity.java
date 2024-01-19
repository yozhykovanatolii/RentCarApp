package com.example.myapplication.rentcarapp.view.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.rentcarapp.R;
import com.example.myapplication.rentcarapp.model.firestore.models.Client;
import com.example.myapplication.rentcarapp.model.firestore.models.User;
import com.example.myapplication.rentcarapp.receiver.InternetReceiver;
import com.example.myapplication.rentcarapp.viewmodel.AuthViewModel;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.TextInputEditText;

import org.checkerframework.checker.units.qual.A;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class SignUpActivity extends AppCompatActivity {
    ShapeableImageView icon;
    Uri uri;
    AuthViewModel authViewModel;
    TextInputEditText newUsername, newFullName, newEmail, userPassword, newPhone;
    TextView incorrectFullName, incorrectEmail, incorrectPassword, incorrectPhone, existUsername, choosePhoto;
    //BroadcastReceiver broadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        initComponents();
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);
        initBroadcastReceiver();
        editUsername();
        editFullName();
        editEmail();
        editPassword();
        editPhoneNumber();
    }

    private void initComponents(){
        icon = findViewById(R.id.avatar);
        newUsername = findViewById(R.id.newUsername);
        newFullName = findViewById(R.id.newFullName);
        newEmail = findViewById(R.id.newEmail);
        userPassword = findViewById(R.id.userPassword);
        newPhone = findViewById(R.id.newPhone);
        initErrorMessages();
    }

    private void initErrorMessages(){
        existUsername = findViewById(R.id.existUsername);
        incorrectFullName = findViewById(R.id.incorrectFullName);
        incorrectEmail = findViewById(R.id.incorrectEmail);
        incorrectPassword = findViewById(R.id.incorrectPassword);
        incorrectPhone = findViewById(R.id.incorrectPhone);
        choosePhoto = findViewById(R.id.choosePhoto);
    }

    private void initBroadcastReceiver(){
        //broadcastReceiver = new InternetReceiver();
        //registerReceiver(broadcastReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    public void pickUpImage(View view){
        Intent intent = new Intent(Intent.ACTION_PICK);
        startActivityForResult(intent, 1000);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            if(requestCode == 1000){
                if(data != null){
                    uri = data.getData();
                    icon.setImageURI(data.getData());
                }
            }
        }
    }

    private void editUsername(){
        newUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String username = Objects.requireNonNull(newUsername.getText()).toString();
                checkUsername(username);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void editFullName(){
        newFullName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String fullName = Objects.requireNonNull(newFullName.getText()).toString();
                if(!checkFullName(fullName)){
                    incorrectFullName.setVisibility(View.VISIBLE);
                }else{
                    incorrectFullName.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void editEmail(){
        newEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String email = Objects.requireNonNull(newEmail.getText()).toString();
                if(!checkEmail(email)){
                    incorrectEmail.setVisibility(View.VISIBLE);
                }else{
                    incorrectEmail.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void editPassword(){
        userPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String password = Objects.requireNonNull(userPassword.getText()).toString();
                if(!checkPassword(password)){
                    incorrectPassword.setVisibility(View.VISIBLE);
                }else{
                    incorrectPassword.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void editPhoneNumber(){
        newPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String phone = Objects.requireNonNull(newPhone.getText()).toString();
                if(!checkPhoneNumber(phone)){
                    incorrectPhone.setVisibility(View.VISIBLE);
                }else{
                    incorrectPhone.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void checkUsername(String username){
        authViewModel.getUserByUsername(username).observe(this, user -> {
            if(user != null){
                existUsername.setVisibility(View.VISIBLE);
            }else{
                existUsername.setVisibility(View.GONE);
            }
        });
    }

    private boolean checkFullName(String fullName){
        AtomicBoolean isFullNameCorrect = new AtomicBoolean(false);
        authViewModel.isFullNameWriteCorrect(fullName).observe(this, aBoolean -> {
            if(aBoolean){
                isFullNameCorrect.set(true);
            }
        });
        return isFullNameCorrect.get();
    }

    private boolean checkEmail(String email){
        AtomicBoolean isEmailWriteCorrect = new AtomicBoolean(false);
        authViewModel.isEmailCorrect(email).observe(this, aBoolean -> {
            if(aBoolean){
                isEmailWriteCorrect.set(true);
            }
        });
        return isEmailWriteCorrect.get();
    }

    private boolean checkPassword(String password){
        AtomicBoolean isPasswordCorrect = new AtomicBoolean(false);
        authViewModel.isPasswordWriteCorrect(password).observe(this, aBoolean -> {
            if(aBoolean){
                isPasswordCorrect.set(true);
            }
        });
        return isPasswordCorrect.get();
    }

    private boolean checkPhoneNumber(String phone){
        AtomicBoolean isPhoneNumberWriteCorrect = new AtomicBoolean(false);
        authViewModel.isUserPhoneCorrect(phone).observe(this, aBoolean -> {
            if(aBoolean){
                isPhoneNumberWriteCorrect.set(true);
            }
        });
        return isPhoneNumberWriteCorrect.get();
    }

    private void showErrorMessages(){
        choosePhoto.setVisibility(View.VISIBLE);
        incorrectFullName.setVisibility(View.VISIBLE);
        incorrectEmail.setVisibility(View.VISIBLE);
        incorrectPassword.setVisibility(View.VISIBLE);
        incorrectPhone.setVisibility(View.VISIBLE);
    }

    private void hideErrorMessages(){
        choosePhoto.setVisibility(View.GONE);
        incorrectFullName.setVisibility(View.GONE);
        incorrectEmail.setVisibility(View.GONE);
        incorrectPassword.setVisibility(View.GONE);
        incorrectPhone.setVisibility(View.GONE);
    }

    public void createAccount(View view){
        String username = Objects.requireNonNull(newUsername.getText()).toString();
        authViewModel.getUserByUsername(username).observe(this, user -> {
            if(user == null){
                createUser(username);
                existUsername.setVisibility(View.GONE);
            }else{
                existUsername.setVisibility(View.VISIBLE);
            }
        });
    }

    private void createUser(String username){
        String email = Objects.requireNonNull(newEmail.getText()).toString();
        String password = Objects.requireNonNull(userPassword.getText()).toString();
        String phone = Objects.requireNonNull(newPhone.getText()).toString();
        String fullName = Objects.requireNonNull(newFullName.getText()).toString();
        if(checkFullName(fullName) && checkPhoneNumber(phone) && checkEmail(email) && checkPassword(password) && uri != null){
            hideErrorMessages();
            User newUser = new User(username, password);
            createClient(newUser, fullName, email, password, phone);
        }else{
            showErrorMessages();
        }
    }

    private void createClient(User newUser, String fullName, String email, String password, String phone){
        Client newClient = new Client(fullName, phone, email, uri.toString());
        authViewModel.signUpWithEmailAndPassword(newClient, newUser, password);
        goToMainActivity();
    }

    private void goToMainActivity(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //unregisterReceiver(broadcastReceiver);
    }
}