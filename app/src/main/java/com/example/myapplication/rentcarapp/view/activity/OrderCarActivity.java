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
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.myapplication.rentcarapp.R;
import com.example.myapplication.rentcarapp.model.firestore.models.Rent;
import com.example.myapplication.rentcarapp.receiver.InternetReceiver;
import com.example.myapplication.rentcarapp.viewmodel.CarViewModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class OrderCarActivity extends AppCompatActivity {
    HashMap<String, Integer> dataAboutCar;
    Spinner stationIssuingSpinner, stationReturnSpinner;
    TextView dateIssuing, dateReturn, totalAmountText, sum;
    TextView errorDateIssuing, errorDateReturn;
    CarViewModel carViewModel;
    BroadcastReceiver broadcastReceiver;
    Date current;
    String token = "";
    String tokenClient = "";
    int price = 0;
    int totalSum = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_car);
        carViewModel = new ViewModelProvider(this).get(CarViewModel.class);
        dataAboutCar = (HashMap<String, Integer>) getIntent().getSerializableExtra("DataAboutCar");
        current = new Date();
        initComponents();
        initData();
        giveDataFromHashMap();
        changeTextView();
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

    private void initData(){
        carViewModel.getStations().observe(this, strings -> {
            String[] stations = strings.toArray(new String[0]);
            initSpinner(stations);
        });
        carViewModel.getClient().observe(this, client -> tokenClient = client.getToken());
    }

    private void initSpinner(String[] stations){
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, com.google.android.material.R.layout.support_simple_spinner_dropdown_item, stations);
        stationIssuingSpinner.setAdapter(adapter);
        stationReturnSpinner.setAdapter(adapter);
    }

    public void rentCar(View view){
        checkDateIssuingAndReturn();
    }

    private void checkDateIssuingAndReturn(){
        String startDate = dateIssuing.getText().toString();
        String endDate = dateReturn.getText().toString();
        if(startDate.equals("Choose date") && endDate.equals("Choose date")){
            showErrorMessage();
        }else{
            hideErrorMessage();
            rent(startDate, endDate);
        }
    }

    private void rent(String startDate, String endDate){
        Random random = new Random();
        String stationIssuing = stationIssuingSpinner.getSelectedItem().toString();
        String stationReturn = stationReturnSpinner.getSelectedItem().toString();
        String id = String.valueOf(random.nextInt(10000));
        Rent rent = new Rent(id, tokenClient, token, stationIssuing, stationReturn, startDate, startDate, endDate, totalSum, totalSum);
        rent.setFines(0);
        rent.setStatus("No return");
        carViewModel.createRent(rent);
        goToPaymentMethodActivity();
    }

    private void goToPaymentMethodActivity(){
        Intent intent = new Intent(this, PaymentMethodActivity.class);
        intent.putExtra("Amount", totalSum);
        startActivity(intent);
    }

    private void initComponents(){
        initSpinner();
        initDateTextView();
        initPriceTextView();
    }

    private void initSpinner(){
        stationIssuingSpinner = findViewById(R.id.stationIssuingSpinner);
        stationReturnSpinner = findViewById(R.id.stationReturnSpinner);
    }

    private void initDateTextView(){
        dateIssuing = findViewById(R.id.dateAndTimeIssuing);
        dateReturn = findViewById(R.id.dateAndTimeReturn);
        errorDateIssuing = findViewById(R.id.errorDateIssuing);
        errorDateReturn = findViewById(R.id.errorDateReturn);
    }

    private void hideErrorMessage(){
        errorDateIssuing.setVisibility(View.GONE);
        errorDateReturn.setVisibility(View.GONE);
    }

    private void showErrorMessage(){
        errorDateIssuing.setVisibility(View.VISIBLE);
        errorDateReturn.setVisibility(View.VISIBLE);
    }

    private void initPriceTextView(){
        totalAmountText = findViewById(R.id.totalAmount);
        sum = findViewById(R.id.sum);
    }

    public void pickDateIssuingRent(View view){
        pickDate(dateIssuing);
    }

    public void pickDateReturnRent(View view){pickDate(dateReturn);}

    private void pickDate(TextView dateText){
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            LocalDate date = LocalDate.now();
            DatePickerDialog datePickerDialog = new DatePickerDialog(this, (datePicker, year, month, day) -> dateText.setText(day + "." + (month + 1) + "." + year), date.getYear(), date.getMonth().getValue() - 1, date.getDayOfMonth());
            datePickerDialog.getDatePicker().setMinDate(current.getTime());
            datePickerDialog.show();
        }
    }

    private void calculate(String startDate, String endDate){
        carViewModel.calculateDifferenceBetweenTwoDates(startDate, endDate, price).observe(this, integer -> {
            totalSum = integer;
            sum.setText(integer + " ₴");
            totalAmountText.setText(integer + " ₴");
        });
    }

    private void changeTextView(){
        dateReturn.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                String startDate = dateIssuing.getText().toString();
                String endDate = dateReturn.getText().toString();
                if (startDate.length() != 0 && endDate.length() != 0) {
                    calculate(startDate, endDate);
                }
            }
        });
    }

    private void giveDataFromHashMap(){
        for(Map.Entry<String, Integer> entry: dataAboutCar.entrySet()){
            token = entry.getKey();
            price = entry.getValue();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
    }
}