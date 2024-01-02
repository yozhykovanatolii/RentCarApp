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
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.rentcarapp.R;
import com.example.myapplication.rentcarapp.model.firestore.models.CreditCard;
import com.example.myapplication.rentcarapp.receiver.InternetReceiver;
import com.example.myapplication.rentcarapp.viewmodel.CarViewModel;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class AddCreditCardActivity extends AppCompatActivity {
    HashMap<String, Integer> dataAboutCar;
    EditText creditCardNumber, creditCardDate, creditSpecialCode;
    TextView errorCreditCardNumber, errorDate, errorSpecialCode;
    Spinner bankSpinner;
    CarViewModel carViewModel;
    BroadcastReceiver broadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_credit_card);
        initComponents();
        carViewModel = new ViewModelProvider(this).get(CarViewModel.class);
        dataAboutCar = (HashMap<String, Integer>) getIntent().getSerializableExtra("AddCreditCard");
        initBroadcastReceiver();
        initData();
        editCreditCardNumber();
        editDateOfCard();
        editSpecialCode();
    }

    private void initComponents(){
        creditCardNumber = findViewById(R.id.creditCardNumber);
        creditCardDate = findViewById(R.id.creditCardDate);
        creditSpecialCode = findViewById(R.id.creditSpecialCode);
        errorCreditCardNumber = findViewById(R.id.errorCreditCardNumber);
        errorDate = findViewById(R.id.errorDate);
        errorSpecialCode = findViewById(R.id.errorSpecialCode);
        bankSpinner = findViewById(R.id.bankSpinner);
    }

    private void initBroadcastReceiver(){
        broadcastReceiver = new InternetReceiver();
        registerReceiver(broadcastReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    private void editCreditCardNumber(){
        creditCardNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String creditCard = creditCardNumber.getText().toString();
                if(!checkCreditCardNumber(creditCard)){
                    errorCreditCardNumber.setVisibility(View.VISIBLE);
                }else{
                    errorCreditCardNumber.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void editDateOfCard(){
        creditCardDate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String cardDate = creditCardDate.getText().toString();
                if(!checkDateOfCard(cardDate)){
                    errorDate.setVisibility(View.VISIBLE);
                }else{
                    errorDate.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void editSpecialCode(){
        creditSpecialCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String cardSpecialCode = creditSpecialCode.getText().toString();
                if(!checkSpecialCode(cardSpecialCode)){
                    errorSpecialCode.setVisibility(View.VISIBLE);
                }else{
                    errorSpecialCode.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private boolean checkCreditCardNumber(String creditCard){
        AtomicBoolean isCreditCardNumberCorrect = new AtomicBoolean(false);
        carViewModel.isCreditCardNumberWriteCorrect(creditCard).observe(this, aBoolean -> {
            if(aBoolean){
                isCreditCardNumberCorrect.set(true);
            }
        });
        return isCreditCardNumberCorrect.get();
    }

    private boolean checkDateOfCard(String cardDate){
        AtomicBoolean isCardDateCorrect = new AtomicBoolean(false);
        carViewModel.isDateOfCardWriteCorrect(cardDate).observe(this, aBoolean -> {
            if(aBoolean){
                isCardDateCorrect.set(true);
            }
        });
        return isCardDateCorrect.get();
    }

    private boolean checkSpecialCode(String cardSpecialCode){
        AtomicBoolean isSpecialCodeCorrect = new AtomicBoolean(false);
        carViewModel.isSpecialCodeWriteCorrect(cardSpecialCode).observe(this, aBoolean -> {
            if(aBoolean){
                isSpecialCodeCorrect.set(true);
            }
        });
        return isSpecialCodeCorrect.get();
    }

    private void showErrorMessage(){
        errorCreditCardNumber.setVisibility(View.VISIBLE);
        errorDate.setVisibility(View.VISIBLE);
        errorSpecialCode.setVisibility(View.VISIBLE);
    }

    private void hideErrorMessage(){
        errorCreditCardNumber.setVisibility(View.GONE);
        errorDate.setVisibility(View.GONE);
        errorSpecialCode.setVisibility(View.GONE);
    }

    public void addCreditCard(View view){
        String creditCard = creditCardNumber.getText().toString();
        String cardDate = creditCardDate.getText().toString();
        String cardSpecialCode = creditSpecialCode.getText().toString();
        if(checkCreditCardNumber(creditCard) && checkDateOfCard(cardDate) && checkSpecialCode(cardSpecialCode)){
            hideErrorMessage();
            addCard(creditCard, cardDate, cardSpecialCode);
        }else{
            showErrorMessage();
        }
    }

    private void addCard(String creditCardNum, String cardDate, String cardSpecialCode){
        String bank = bankSpinner.getSelectedItem().toString();
        CreditCard creditCard = new CreditCard(creditCardNum, bank, cardDate, cardSpecialCode);
        if(dataAboutCar != null){
            updateClientCard(creditCard);
            goToOrderCarActivity();
        }else{
            updateClientCard(creditCard);
            goToMyCards();
        }
    }

    private void initData(){
        carViewModel.getBanks().observe(this, strings -> {
            String[] banks = strings.toArray(new String[0]);
            initSpinner(banks);
        });
    }

    private void initSpinner(String[] banks){
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, com.google.android.material.R.layout.support_simple_spinner_dropdown_item, banks);
        bankSpinner.setAdapter(adapter);
    }

    private void updateClientCard(CreditCard creditCard){
        carViewModel.createCreditCard(creditCard);
        carViewModel.getClient().observe(this, client -> update(client.getCards(), client.getToken(), creditCard.getNumberCard()));
    }

    private void goToOrderCarActivity(){
        Intent intent = new Intent(this, OrderCarActivity.class);
        intent.putExtra("DataAboutCar", dataAboutCar);
        startActivity(intent);
    }

    private void goToMyCards(){
        Intent intent = new Intent(this, MyCardsActivity.class);
        startActivity(intent);
    }

    private void update(List<String> cards, String token, String creditCardNumber){
        cards.add(creditCardNumber);
        carViewModel.updateCreditCardClient(cards, token);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
    }
}