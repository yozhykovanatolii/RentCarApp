package com.example.myapplication.rentcarapp.view.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
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
import com.example.myapplication.rentcarapp.adapter.ReviewAdapter;
import com.example.myapplication.rentcarapp.model.firestore.models.Car;
import com.example.myapplication.rentcarapp.model.firestore.models.Client;
import com.example.myapplication.rentcarapp.model.firestore.models.Review;
import com.example.myapplication.rentcarapp.receiver.InternetReceiver;
import com.example.myapplication.rentcarapp.viewmodel.CarViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class DetailActivity extends AppCompatActivity {
    TextView carModel, transmissionText, fuelText, engineVolumeText, fuelConsumptionText, childrenChairText;
    TextInputEditText reviewText;
    RatingBar ratingBar;
    AlertDialog alertDialog;
    MaterialButton materialButton, sendReviewButton;
    FloatingActionButton favoriteButton;
    ImageSlider imageSlider;
    RecyclerView reviewsList;
    ReviewAdapter reviewAdapter;
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
        getAllIDUsersReviewByCar();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initBroadcastReceiver();
    }

    private void initComponents(){
        imageSlider = findViewById(R.id.imageSlider);
        reviewsList = findViewById(R.id.reviewsList);
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

    public void leaveReview(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View layout_dialog = LayoutInflater.from(this).inflate(R.layout.leave_review, null);
        builder.setView(layout_dialog);
        sendReviewButton = layout_dialog.findViewById(R.id.sendReviewButton);
        reviewText = layout_dialog.findViewById(R.id.reviewText);
        ratingBar = layout_dialog.findViewById(R.id.ratingBar);
        alertDialog = builder.create();
        alertDialog.show();
        editReviewText();
        sendReviewButton.setOnClickListener(this::sendReview);
    }

    private void editReviewText(){
        reviewText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkReviewTextAndRatingBar();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void checkReviewTextAndRatingBar(){
        carViewModel.checkReviewTextAndRating(Objects.requireNonNull(reviewText.getText()).length(), ratingBar.getRating()).observe(this, aBoolean -> {
            if(aBoolean){
                sendReviewButton.setClickable(true);
                sendReviewButton.setTextColor(getResources().getColor(R.color.white));
            }else{
                sendReviewButton.setClickable(false);
                sendReviewButton.setTextColor(getResources().getColor(R.color.gray));
            }
        });
    }

    private void getAllIDUsersReviewByCar(){
        carViewModel.getAllUsersReviewByCar(car.getID()).observe(this, strings -> {
            if(strings != null){
                getAllUsersReviewByTheirID(strings);
            }
        });
    }

    private void getAllUsersReviewByTheirID(List<String> usersID){
        carViewModel.getAllUsersByTheirID(usersID).observe(this, strings -> {
            if(strings != null){
                getAllReviewsByCar(strings);
            }
        });
    }

    private void getAllReviewsByCar(List<String> users){
        carViewModel.getAllReviewsByCar(car.getID()).observe(this, reviews -> {
            if(reviews != null){
                initRecyclerView(users, reviews);
            }
        });
    }

    private void initRecyclerView(List<String> users, List<Review> reviews){
        reviewAdapter = new ReviewAdapter(users, reviews);
        reviewsList.setAdapter(reviewAdapter);
        reviewsList.setLayoutManager(new LinearLayoutManager(this));
    }

    private void sendReview(View view){
        getClient();
    }

    private void getClient(){
        carViewModel.getClient().observe(this, client -> {
            if(client != null){
                leaveFeadback(client.getToken());
            }
        });
    }

    private void leaveFeadback(String idClient){
        Random random = new Random();
        String id = String.valueOf(random.nextInt(10000));
        float rating = ratingBar.getRating();
        String text = Objects.requireNonNull(reviewText.getText()).toString();
        String currentDate = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(new Date());
        Review review = new Review(id, idClient, car.getID(), rating, text, currentDate);
        carViewModel.createReview(review);
        getAllIDUsersReviewByCar();
        alertDialog.dismiss();
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