package com.example.myapplication.rentcarapp.view.activity;

import androidx.annotation.Nullable;
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
import com.example.myapplication.rentcarapp.model.firestore.models.Client;
import com.example.myapplication.rentcarapp.model.firestore.models.User;
import com.example.myapplication.rentcarapp.receiver.InternetReceiver;
import com.example.myapplication.rentcarapp.viewmodel.AuthViewModel;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.TextInputEditText;
import com.squareup.picasso.Picasso;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MyProfileActivity extends AppCompatActivity {
    AuthViewModel authViewModel;
    TextInputEditText accountFullName, accountPhone, accountUsername, accountEmail, accountUserPassword;
    TextView errorUsername, errorFullName, errorPhone, errorEmail, errorPassword;
    ShapeableImageView updateAvatar;
    Client clientCurrent;
    User userCurrent;
    String urlPhoto = "";
    BroadcastReceiver broadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);
        initComponent();
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);
        initClientsData();
        editUsername();
        editFullName();
        editPhone();
        editEmail();
        editPassword();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initBroadcastReceiver();
    }

    private void initComponent(){
        accountFullName = findViewById(R.id.accountFullName);
        accountPhone = findViewById(R.id.accountPhone);
        accountUsername = findViewById(R.id.accountUsername);
        accountEmail = findViewById(R.id.accountEmail);
        accountUserPassword = findViewById(R.id.accountUserPassword);
        updateAvatar = findViewById(R.id.updateAvatar);
        initErrorMessages();
    }

    private void initErrorMessages(){
        errorUsername = findViewById(R.id.errorUsername);
        errorFullName = findViewById(R.id.errorFullName);
        errorPhone = findViewById(R.id.errorPhone);
        errorEmail = findViewById(R.id.mistakeEmail);
        errorPassword = findViewById(R.id.mistakePassword);
    }

    private void initBroadcastReceiver(){
        broadcastReceiver = new InternetReceiver();
        registerReceiver(broadcastReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    private void showErrorMessage(){
        errorFullName.setVisibility(View.VISIBLE);
        errorPhone.setVisibility(View.VISIBLE);
        errorEmail.setVisibility(View.VISIBLE);
        errorPassword.setVisibility(View.VISIBLE);
    }

    private void hideErrorMessage(){
        errorFullName.setVisibility(View.GONE);
        errorPhone.setVisibility(View.GONE);
        errorEmail.setVisibility(View.GONE);
        errorPassword.setVisibility(View.GONE);
    }

    private void editUsername(){
        accountUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String username = Objects.requireNonNull(accountUsername.getText()).toString();
                if(!username.equals(userCurrent.getUsername())){
                    checkUsername(username);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void editFullName(){
        accountFullName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String fullName = Objects.requireNonNull(accountFullName.getText()).toString();
                if(!checkFullName(fullName)){
                    errorFullName.setVisibility(View.VISIBLE);
                }else{
                    errorFullName.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void editPhone(){
        accountPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String phone = Objects.requireNonNull(accountPhone.getText()).toString();
                if(!checkPhoneNumber(phone)){
                    errorPhone.setVisibility(View.VISIBLE);
                }else{
                    errorPhone.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void editEmail(){
        accountEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String email = Objects.requireNonNull(accountEmail.getText()).toString();
                if(!checkEmail(email)){
                    errorEmail.setVisibility(View.VISIBLE);
                }else{
                    errorEmail.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void editPassword(){
        accountUserPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String password = Objects.requireNonNull(accountUserPassword.getText()).toString();
                if(!checkPassword(password)){
                    errorPassword.setVisibility(View.VISIBLE);
                }else{
                    errorPassword.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void checkUsername(String username){
        authViewModel.getUserByUsername(username).observe(this, user -> {
            if(user != null){
                errorUsername.setVisibility(View.VISIBLE);
            }else{
                errorUsername.setVisibility(View.GONE);
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

    public void logOut(View view){
        authViewModel.logOut();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void back(View view){
        Intent intent = new Intent(this, MainWindowActivity.class);
        startActivity(intent);
    }

    public void updateProfile(View view){
        String username = Objects.requireNonNull(accountUsername.getText()).toString();
        if(username.equals(userCurrent.getUsername())){
            updateClient(username);
        }else{
            isUsernameExist(username);
        }
    }

    public void changePhoto(View view){
        Intent intent = new Intent(Intent.ACTION_PICK);
        startActivityForResult(intent, 1000);
    }

    private void isUsernameExist(String username){
        authViewModel.getUserByUsername(username).observe(this, user -> {
            if(user != null){
                errorUsername.setVisibility(View.VISIBLE);
            }else{
                errorUsername.setVisibility(View.GONE);
                updateClient(username);
            }
        });
    }

    private void updateClient(String username){
        String newEmail = Objects.requireNonNull(accountEmail.getText()).toString();
        String fullName = Objects.requireNonNull(accountFullName.getText()).toString();
        String phone = Objects.requireNonNull(accountPhone.getText()).toString();
        String newPassword = Objects.requireNonNull(accountUserPassword.getText()).toString();
        String email = clientCurrent.getEmail();
        String password = userCurrent.getPassword();
        if(checkFullName(fullName) && checkPhoneNumber(phone) && checkEmail(newEmail) && checkPassword(newPassword)){
            authViewModel.updateEmailAndPasswordInFirebase(newEmail, newPassword, email, password);
            clientCurrent.setEmail(newEmail);
            clientCurrent.setFullName(fullName);
            clientCurrent.setPhoneNumber(phone);
            hideErrorMessage();
            updatePhoto(newPassword, username);
        }else{
            showErrorMessage();
        }
    }

    private void updatePhoto(String newPassword, String username){
        if(!urlPhoto.isEmpty()){
            clientCurrent.setPhoto(urlPhoto);
            authViewModel.saveImageInCloudStorage(clientCurrent);
        }else{
            authViewModel.updateClient(clientCurrent);
        }
        updateUser(newPassword, username);
    }

    private void updateUser(String newPassword, String username){
        userCurrent.setUsername(username);
        userCurrent.setPassword(newPassword);
        authViewModel.updateUser(userCurrent);
        Toast.makeText(getApplicationContext(), "Profile was updated", Toast.LENGTH_LONG).show();
    }

    private void initClientsData(){
        authViewModel.getClient().observe(this, client -> {
            if(client != null){
                initUsersData(client);
            }
        });
    }

    private void initUsersData(Client client){
        authViewModel.getUserByToken(client.getToken()).observe(this, user -> initComponents(client, user));
    }

    private void initComponents(Client client, User user){
        clientCurrent = client;
        userCurrent = user;
        accountUsername.setText(user.getUsername());
        accountEmail.setText(client.getEmail());
        accountUserPassword.setText(user.getPassword());
        accountFullName.setText(client.getFullName());
        accountPhone.setText(client.getPhoneNumber());
        Picasso.get().load(client.getPhoto()).into(updateAvatar);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            if(requestCode == 1000){
                if(data != null){
                    urlPhoto = Objects.requireNonNull(data.getData()).toString();
                    updateAvatar.setImageURI(data.getData());
                    updateAvatar.setRotation(0);
                }
            }
        }
    }
}