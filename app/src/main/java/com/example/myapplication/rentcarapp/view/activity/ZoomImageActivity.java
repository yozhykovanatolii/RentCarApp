package com.example.myapplication.rentcarapp.view.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import com.example.myapplication.rentcarapp.R;
import com.github.chrisbanes.photoview.PhotoView;
import com.squareup.picasso.Picasso;


public class ZoomImageActivity extends AppCompatActivity {
    PhotoView photoView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zoom_image);
        initComponent();
    }

    private void initComponent(){
        photoView = findViewById(R.id.photoView);
        Picasso.get().load(getIntent().getStringExtra("Image")).into(photoView);
    }
}