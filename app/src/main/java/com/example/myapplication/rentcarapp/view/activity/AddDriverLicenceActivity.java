package com.example.myapplication.rentcarapp.view.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.app.DatePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.myapplication.rentcarapp.R;
import com.example.myapplication.rentcarapp.model.firestore.models.DriverLicence;
import com.example.myapplication.rentcarapp.receiver.InternetReceiver;
import com.example.myapplication.rentcarapp.viewmodel.CarViewModel;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class AddDriverLicenceActivity extends AppCompatActivity {
    HashMap<String, Integer> dataAboutCar;
    EditText driverLicenceNumber;
    TextView dateIssuingDriverLicence, dateValidUntilDriverLicence;
    TextView errorDriverLicenceID, errorIssuing, errorUntil;
    CarViewModel carViewModel;
    BroadcastReceiver broadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_driver_licence);
        initComponents();
        dataAboutCar = (HashMap<String, Integer>) getIntent().getSerializableExtra("AddDriverLicence");
        carViewModel = new ViewModelProvider(this).get(CarViewModel.class);
        initBroadcastReceiver();
        editDriverLicenceID();
    }

    private void initComponents(){
        driverLicenceNumber = findViewById(R.id.driverLicenceNumber);
        dateIssuingDriverLicence = findViewById(R.id.dateIssuingDriverLicence);
        dateValidUntilDriverLicence = findViewById(R.id.dateValidUntilDriverLicence);
        errorDriverLicenceID = findViewById(R.id.errorDriverLicenceID);
        errorIssuing = findViewById(R.id.errorIssuing);
        errorUntil = findViewById(R.id.errorUntil);
    }

    private void initBroadcastReceiver(){
        broadcastReceiver = new InternetReceiver();
        registerReceiver(broadcastReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    private void editDriverLicenceID(){
        driverLicenceNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String driverLicence = driverLicenceNumber.getText().toString();
                if(!checkDriverLicenceID(driverLicence).get()){
                    errorDriverLicenceID.setVisibility(View.VISIBLE);
                }else{
                    errorDriverLicenceID.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void showErrorMessage(){
        errorDriverLicenceID.setVisibility(View.VISIBLE);
        errorIssuing.setVisibility(View.VISIBLE);
        errorUntil.setVisibility(View.VISIBLE);
    }

    private void hideErrorMessage(){
        errorDriverLicenceID.setVisibility(View.GONE);
        errorIssuing.setVisibility(View.GONE);
        errorUntil.setVisibility(View.GONE);
    }

    private AtomicBoolean checkDriverLicenceID(String driverLicence){
        AtomicBoolean isDriverLicenceCorrect = new AtomicBoolean(false);
        carViewModel.isDriverLicenceNumberWriteCorrect(driverLicence).observe(this, aBoolean -> {
            if(aBoolean){
                isDriverLicenceCorrect.set(true);
            }
        });
        return isDriverLicenceCorrect;
    }

    private boolean checkDate(String dateIssuing, String dateValid){
        return dateIssuing.equals("Choose date") && dateValid.equals("Choose date");
    }

    public void addDriverLicence(View view){
        String driverLicence = driverLicenceNumber.getText().toString();
        String dateIssuing = dateIssuingDriverLicence.getText().toString();
        String dateValid = dateValidUntilDriverLicence.getText().toString();
        if(checkDriverLicenceID(driverLicence).get() && !checkDate(dateIssuing, dateValid)){
            hideErrorMessage();
            createDriverLicenceAndUpdateClientDriverLicence(driverLicence, dateIssuing, dateValid);
        }else{
            showErrorMessage();
        }
    }

    private void createDriverLicenceAndUpdateClientDriverLicence(String driverLicenceNum, String dateIssuing, String dateValid){
        DriverLicence driverLicence = new DriverLicence(driverLicenceNum, dateIssuing, dateValid);
        carViewModel.createDriverLicence(driverLicence);
        carViewModel.updateDriverLicenceClient(driverLicenceNum);
        goToOrderActivity();
    }

    private void goToOrderActivity(){
        Intent intent = new Intent(AddDriverLicenceActivity.this, OrderCarActivity.class);
        intent.putExtra("DataAboutCar", dataAboutCar);
        startActivity(intent);
    }

    public void pickDateIssuingDriverLicence(View view){
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (datePicker, year, month, day) -> dateIssuingDriverLicence.setText(day + "." + (month + 1) + "." + year), 2023, 7, 24);
        datePickerDialog.show();
    }

    public void pickDateValidUntilDriverLicence(View view){
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (datePicker, year, month, day) -> dateValidUntilDriverLicence.setText(day + "." + (month + 1) + "." + year), 2023, 7, 24);
        datePickerDialog.show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
    }
}