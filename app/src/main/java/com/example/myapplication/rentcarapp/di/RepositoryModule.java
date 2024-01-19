package com.example.myapplication.rentcarapp.di;

import android.content.Context;

import com.example.myapplication.rentcarapp.model.repository.AuthRepository;
import com.example.myapplication.rentcarapp.model.repository.CarRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ViewModelComponent;

@Module
@InstallIn(ViewModelComponent.class)
public class RepositoryModule {
    @Provides
    public AuthRepository provideAuthRepository(FirebaseAuth firebaseAuth, FirebaseFirestore firestore, FirebaseStorage firebaseStorage){
        return new AuthRepository(firebaseAuth, firestore, firebaseStorage);
    }

    @Provides
    public CarRepository provideCarRepository(FirebaseAuth firebaseAuth, FirebaseFirestore firestore, Context context){
        return new CarRepository(firebaseAuth, firestore, context);
    }
}
