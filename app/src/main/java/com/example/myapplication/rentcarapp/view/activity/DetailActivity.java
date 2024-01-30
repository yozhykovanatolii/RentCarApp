package com.example.myapplication.rentcarapp.view.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ActionTypes;
import com.denzcoskun.imageslider.constants.AnimationTypes;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.interfaces.ItemClickListener;
import com.denzcoskun.imageslider.interfaces.TouchListener;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.myapplication.rentcarapp.R;
import com.example.myapplication.rentcarapp.model.firestore.models.Car;
import com.example.myapplication.rentcarapp.receiver.InternetReceiver;
import com.example.myapplication.rentcarapp.viewmodel.CarViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class DetailActivity extends AppCompatActivity {
    TextView carModel, transmissionText, fuelText, engineVolumeText, fuelConsumptionText, childrenChairText;
    MaterialButton materialButton;
    FloatingActionButton favoriteButton;
    ImageSlider imageSlider;
    Car car;
    ArrayList<SlideModel> imageList;
    CarViewModel carViewModel;
    BroadcastReceiver broadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        car = (Car) getIntent().getSerializableExtra("Car");
        carViewModel = new ViewModelProvider(this).get(CarViewModel.class);
        imageList = new ArrayList<>();
        initComponents();
        initData();
        checkCarByRent();
        checkIsFavorite();
        clickOnImage();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initBroadcastReceiver();
    }

    private void initComponents(){
        imageSlider = findViewById(R.id.imageSlider);
        carModel = findViewById(R.id.carModel);
        transmissionText = findViewById(R.id.textView4);
        fuelText = findViewById(R.id.fuel);
        engineVolumeText = findViewById(R.id.engineVolume);
        fuelConsumptionText = findViewById(R.id.fuelConsumption);
        childrenChairText = findViewById(R.id.childrenChair);
        initButtons();
    }

    private void initButtons(){
        favoriteButton = findViewById(R.id.favorite);
        materialButton = findViewById(R.id.rentCar);
    }

    private void initBroadcastReceiver(){
        broadcastReceiver = new InternetReceiver();
        registerReceiver(broadcastReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    public void back(View view){
        Intent intent = new Intent(this, MainWindowActivity.class);
        startActivity(intent);
    }

    public void getFavoriteClientsCars(View view){
        carViewModel.getFavoriteClientsCars().observe(this, this::updateFavoriteClientsCars);
    }

    private void updateFavoriteClientsCars(List<String> cars){
        String idCar = car.getID();
        if(!cars.contains(idCar)){
            cars.add(idCar);
            favoriteButton.setImageResource(R.drawable.baseline_favorite_24);
        }else{
            cars.remove(idCar);
            favoriteButton.setImageResource(R.drawable.heart);
        }
        carViewModel.updateFavoriteClientsCars(cars);
    }

    private void initData(){
        carModel.setText(car.getModel());
        transmissionText.setText(car.getTransmission());
        fuelText.setText(car.getTypeOfFuel());
        engineVolumeText.setText(car.getEngineVolume());
        fuelConsumptionText.setText(car.getFuel() + "/100km");
        materialButton.setText("Rent: " + car.getPrice() + " ₴/Day");
        childrenChairText.setText(car.getChildrenChair());
        initImages();
    }

    private void initImages(){
        for(String image: car.getPhoto()){
            imageList.add(new SlideModel(image, ScaleTypes.CENTER_CROP));
        }
        imageSlider.setImageList(imageList);
        imageSlider.setSlideAnimation(AnimationTypes.ZOOM_OUT);
    }

    public void rentCar(View view){
        HashMap<String, Integer> dataAboutCar = new HashMap<>();
        dataAboutCar.put(car.getID(), car.getPrice());
        carViewModel.getClientDriverLicence().observe(this, s -> {
            if(s != null){
                goToOrderCarActivity(dataAboutCar);
            }else{
                goToAddDriverLicence(dataAboutCar);
            }
        });
    }

    private void clickOnImage(){
        imageSlider.setItemClickListener(new ItemClickListener() {
            @Override
            public void onItemSelected(int i) {
                Intent intent = new Intent(DetailActivity.this, ZoomImageActivity.class);
                intent.putExtra("Image", imageList.get(i).getImageUrl());
                startActivity(intent);
            }

            @Override
            public void doubleClick(int i) {}
        });
    }

    private void goToOrderCarActivity(HashMap<String, Integer> dataAboutCar){
        Intent intent = new Intent(DetailActivity.this, OrderCarActivity.class);
        intent.putExtra("DataAboutCar", dataAboutCar);
        startActivity(intent);
    }

    private void goToAddDriverLicence(HashMap<String, Integer> dataAboutCar){
        Intent addDriverLicence = new Intent(DetailActivity.this, AddDriverLicenceActivity.class);
        addDriverLicence.putExtra("AddDriverLicence", dataAboutCar);
        startActivity(addDriverLicence);
    }

    private void checkCarByRent(){
        carViewModel.checkCarIfHeWasBook(car.getID()).observe(this, aBoolean -> {
            if(aBoolean != null){
                checkRent(aBoolean);
            }
        });
    }

    private void checkRent(Boolean aBoolean){
        if(aBoolean){
            materialButton.setEnabled(false);
            materialButton.setBackgroundColor(Color.parseColor("#FF0000"));
            materialButton.setText("Booked");
        }
    }

    private void checkIsFavorite(){
        carViewModel.getFavoriteClientsCars().observe(this, strings -> {
            String idCar = car.getID();
            if(strings.contains(idCar)){
                favoriteButton.setImageResource(R.drawable.baseline_favorite_24);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
    }
}