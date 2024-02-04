package com.example.myapplication.rentcarapp.view.activity;


import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.Barrier;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.ViewModelProvider;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.rentcarapp.R;
import com.example.myapplication.rentcarapp.model.firestore.models.Car;
import com.example.myapplication.rentcarapp.receiver.InternetReceiver;
import com.example.myapplication.rentcarapp.viewmodel.CarViewModel;
import com.google.android.material.slider.RangeSlider;
import com.google.firebase.firestore.Query;

import java.io.Serializable;
import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class FilterActivity extends AppCompatActivity {
    RangeSlider priceSlider;
    CardView checkAuto, checkMechanic, checkGasoline, checkElectric, existChildrenChair;
    TextView notChosen, exist, auto, mechanic, electric, gasoline;
    ImageView imageView3, autoImage, mechanicImage, electricImage, gasolineImage;
    RadioButton buttonPriceHighToLow, buttonPriceLowToHigh, buttonRatingHighToLow, buttonRatingLowToHigh;
    AlertDialog alertDialog;
    CarViewModel carViewModel;
    BroadcastReceiver broadcastReceiver;
    String chooseSortByCar = null;
    boolean isCardAutoClicked = false;
    boolean isCardMechanicClicked = false;
    boolean isCardElectricClicked = false;
    boolean isCardGasolineClicked = false;
    boolean isCardExistChildrenChairClicked = false;
    String transmission, typeOfFuel;
    String isChairExist = "Not Exist";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);
        priceSlider = findViewById(R.id.priceSlider);
        carViewModel = new ViewModelProvider(this).get(CarViewModel.class);
        initComponents();
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

    private void initComponents(){
        existChildrenChair = findViewById(R.id.existChildrenChair);
        existChildrenChair.setOnClickListener(this::clickExistChildrenChair);
        checkAuto = findViewById(R.id.checkAuto);
        checkAuto.setOnClickListener(this::clickTransmissionCards);
        checkMechanic = findViewById(R.id.checkMechanic);
        checkMechanic.setOnClickListener(this::clickTransmissionCards);
        checkGasoline = findViewById(R.id.checkGasoline);
        checkGasoline.setOnClickListener(this::clickTypeOfFuel);
        checkElectric = findViewById(R.id.checkElectric);
        checkElectric.setOnClickListener(this::clickTypeOfFuel);
        initTextsView();
        initImagesView();
    }

    private void initTextsView(){
        notChosen = findViewById(R.id.notChosen);
        notChosen.setOnClickListener(this::sortCarBy);
        exist = findViewById(R.id.exist);
        auto = findViewById(R.id.auto);
        mechanic = findViewById(R.id.mechanic);
        electric = findViewById(R.id.electric);
        gasoline = findViewById(R.id.gasoline);
    }

    private void initImagesView(){
        imageView3 = findViewById(R.id.imageView3);
        autoImage = findViewById(R.id.autoImage);
        mechanicImage = findViewById(R.id.mechanicImage);
        electricImage = findViewById(R.id.electricImage);
        gasolineImage = findViewById(R.id.gasolineImage);
    }

    public void apply(View view){
        List<Float> values = priceSlider.getValues();
        float minPrice = values.get(0);
        float maxPrice = values.get(1);
        getCarsByFilter(isChairExist, transmission, typeOfFuel, chooseSortByCar, (int) minPrice, (int) maxPrice);
    }

    private void getCarsByFilter(String childrenChair, String transmission, String typeOfFuel, String chooseSortByCar, int minPrice, int maxPrice){
        carViewModel.confirmChoice(childrenChair, transmission, typeOfFuel, chooseSortByCar, minPrice, maxPrice).observe(this, this::sendChoicesClient);
    }

    private void sendChoicesClient(List<Car> cars){
        Intent intent = new Intent(this, MainWindowActivity.class);
        intent.putExtra("FilterCar", (Serializable) cars);
        startActivity(intent);
    }

    public void clickTransmissionCards(View view){
        if(view.getId() == R.id.checkAuto){
            checkCardAuto();
        }if(view.getId() == R.id.checkMechanic){
            checkCardMechanic();
        }
    }

    private void checkCardAuto(){
        if(isCardAutoClicked){
            transmission = "";
            isCardAutoClicked = false;
            checkMechanic.setClickable(true);
            checkAuto.setCardBackgroundColor(getResources().getColor(R.color.white));
            autoImage.setImageResource(R.drawable.baseline_smart_toy_24);
            auto.setTextColor(getResources().getColor(R.color.blue));
        }else{
            transmission = "Automaton";
            isCardAutoClicked = true;
            checkMechanic.setClickable(false);
            checkAuto.setCardBackgroundColor(getResources().getColor(R.color.blue));
            autoImage.setImageResource(R.drawable.baseline_smart_toy_24_white);
            auto.setTextColor(getResources().getColor(R.color.white));
        }
    }

    private void checkCardMechanic(){
        if(isCardMechanicClicked){
            transmission = "";
            isCardMechanicClicked = false;
            changePositionCardGasoline();
            checkAuto.setClickable(true);
            checkElectric.setVisibility(View.VISIBLE);
            checkMechanic.setCardBackgroundColor(getResources().getColor(R.color.white));
            mechanicImage.setImageResource(R.drawable.baseline_settings_24);
            mechanic.setTextColor(getResources().getColor(R.color.blue));
        }else{
            transmission = "Mechanic";
            isCardMechanicClicked = true;
            changePositionCardGasoline();
            checkAuto.setClickable(false);
            checkElectric.setVisibility(View.GONE);
            checkMechanic.setCardBackgroundColor(getResources().getColor(R.color.blue));
            mechanicImage.setImageResource(R.drawable.baseline_settings_24_white);
            mechanic.setTextColor(getResources().getColor(R.color.white));
        }
    }

    private void changePositionCardGasoline(){
        ViewGroup.MarginLayoutParams cardGasoline = (ViewGroup.MarginLayoutParams) checkGasoline.getLayoutParams();
        if(isCardMechanicClicked){
            cardGasoline.setMarginStart(0);
        }else{
            cardGasoline.setMarginStart(30);
        }
        checkGasoline.setLayoutParams(cardGasoline);
    }

    public void clickTypeOfFuel(View view){
        if(view.getId() == R.id.checkElectric){
            checkCardElectric();
        }if(view.getId() == R.id.checkGasoline){
            checkCardGasoline();
        }
    }

    private void checkCardElectric(){
        if(isCardElectricClicked){
            typeOfFuel = "";
            isCardElectricClicked = false;
            checkGasoline.setClickable(true);
            checkMechanic.setVisibility(View.VISIBLE);
            checkElectric.setCardBackgroundColor(getResources().getColor(R.color.white));
            electricImage.setImageResource(R.drawable.baseline_battery_charging_full_24);
            electric.setTextColor(getResources().getColor(R.color.blue));
        }else{
            typeOfFuel = "Electric";
            isCardElectricClicked = true;
            checkGasoline.setClickable(false);
            checkMechanic.setVisibility(View.GONE);
            checkElectric.setCardBackgroundColor(getResources().getColor(R.color.blue));
            electricImage.setImageResource(R.drawable.baseline_battery_charging_full_24_white);
            electric.setTextColor(getResources().getColor(R.color.white));
        }
    }

    private void checkCardGasoline(){
        if(isCardGasolineClicked){
            typeOfFuel = "";
            isCardGasolineClicked = false;
            checkElectric.setClickable(true);
            checkGasoline.setCardBackgroundColor(getResources().getColor(R.color.white));
            gasolineImage.setImageResource(R.drawable.baseline_local_gas_station_24);
            gasoline.setTextColor(getResources().getColor(R.color.blue));
        }else{
            typeOfFuel = "Gasoline";
            isCardGasolineClicked = true;
            checkElectric.setClickable(false);
            checkGasoline.setCardBackgroundColor(getResources().getColor(R.color.blue));
            gasolineImage.setImageResource(R.drawable.baseline_local_gas_station_24_white);
            gasoline.setTextColor(getResources().getColor(R.color.white));
        }
    }

    public void clickExistChildrenChair(View view){
        if(view.getId() == R.id.existChildrenChair){
            checkCardExistChildrenChair();
        }
    }

    private void checkCardExistChildrenChair(){
        if(isCardExistChildrenChairClicked){
            isChairExist = "Not Exist";
            isCardExistChildrenChairClicked = false;
            existChildrenChair.setCardBackgroundColor(getResources().getColor(R.color.white));
            imageView3.setImageResource(R.drawable.baseline_child_care_24);
            exist.setTextColor(getResources().getColor(R.color.blue));
        }else{
            isChairExist = "Exist";
            isCardExistChildrenChairClicked = true;
            existChildrenChair.setCardBackgroundColor(getResources().getColor(R.color.blue));
            imageView3.setImageResource(R.drawable.baseline_child_care_24_white);
            exist.setTextColor(getResources().getColor(R.color.white));
        }
    }

    public void sortCarBy(View view){
        initDialogBox();
    }

    private void initDialogBox(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View layout_dialog = LayoutInflater.from(this).inflate(R.layout.sort_by_dialog, null);
        builder.setView(layout_dialog);
        initDialogComponents(layout_dialog);
        alertDialog = builder.create();
        alertDialog.show();
    }

    private void initDialogComponents(View layout_dialog){
        buttonPriceHighToLow = layout_dialog.findViewById(R.id.buttonPriceHighToLow);
        buttonPriceHighToLow.setOnClickListener(this::checkButtonsPrice);
        buttonPriceLowToHigh = layout_dialog.findViewById(R.id.buttonPriceLowToHigh);
        buttonPriceLowToHigh.setOnClickListener(this::checkButtonsPrice);
    }

    private void checkButtonsPrice(View view){
        if(buttonPriceHighToLow.isChecked()){
            notChosen.setText(R.string.price_high_to_low);
            chooseSortByCar = "price " + Query.Direction.DESCENDING;
        }if(buttonPriceLowToHigh.isChecked()){
            notChosen.setText(R.string.price_low_to_high);
            chooseSortByCar = "price " + Query.Direction.ASCENDING;
        }
        alertDialog.dismiss();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
    }
}