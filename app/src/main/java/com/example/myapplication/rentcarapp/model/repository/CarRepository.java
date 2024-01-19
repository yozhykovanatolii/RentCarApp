package com.example.myapplication.rentcarapp.model.repository;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import com.example.myapplication.rentcarapp.model.firestore.models.Car;
import com.example.myapplication.rentcarapp.model.firestore.models.Client;
import com.example.myapplication.rentcarapp.model.firestore.models.DriverLicence;
import com.example.myapplication.rentcarapp.model.firestore.models.Rent;
import com.example.myapplication.rentcarapp.model.firestore.models.Station;
import com.example.myapplication.rentcarapp.model.firestore.models.User;
import com.example.myapplication.rentcarapp.service.CheckRentWorker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

public class CarRepository {
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;
    private Context context;

    @Inject
    public CarRepository(FirebaseAuth firebaseAuth, FirebaseFirestore firestore, Context context) {
        this.firebaseAuth = firebaseAuth;
        this.firestore = firestore;
        this.context = context;
    }

    public LiveData<Client> getClient(){
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        MutableLiveData<Client> clientMutableLiveData = new MutableLiveData<>();
        if(firebaseUser != null){
            String token = firebaseUser.getUid();
            firestore.collection("clients").document(token).get().addOnCompleteListener(task -> {
                if(task.isSuccessful() && task.getResult().exists()){
                    Log.i("client", "Successful");
                    clientMutableLiveData.setValue(task.getResult().toObject(Client.class));
                }else{
                    Log.i("client", "Unsuccessful");
                    clientMutableLiveData.setValue(null);
                    Log.i("Errors", "Exception:", task.getException());
                }
            });
        }else{
            clientMutableLiveData.setValue(null);
        }
        return clientMutableLiveData;
    }

    public LiveData<List<Car>> getAllCars(){
        MutableLiveData<List<Car>> cars = new MutableLiveData<>();
        firestore.collection("cars").get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                cars.setValue(task.getResult().toObjects(Car.class));
            }else{
                cars.setValue(null);
                Log.i("Errors", "Exception:", task.getException());
            }
        });
        return cars;
    }

    public LiveData<List<Car>> getCarByModel(String model){
        MutableLiveData<List<Car>> cars = new MutableLiveData<>();
        firestore.collection("cars").whereEqualTo("model", model).get().addOnCompleteListener(task -> {
            if(task.isSuccessful() && !task.getResult().isEmpty()){
                cars.setValue(task.getResult().toObjects(Car.class));
            }else{
                cars.setValue(null);
                Log.i("Errors", "Exception:", task.getException());
            }
        });
        return cars;
    }

    public LiveData<List<Car>> getCarsById(List<String> ids){
        MutableLiveData<List<Car>> cars = new MutableLiveData<>();
        firestore.collection("cars").whereIn("ID", ids).get().addOnCompleteListener(task -> {
            if(task.isSuccessful() && !task.getResult().isEmpty()){
                cars.setValue(task.getResult().toObjects(Car.class));
            }else{
                cars.setValue(null);
                Log.i("Errors", "Exception:", task.getException());
            }
        });
        return cars;
    }

    public LiveData<List<String>> getFavoriteClientsCars(){
        MutableLiveData<List<String>> favoritesCars = new MutableLiveData<>();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if(user != null){
            String token = user.getUid();
            firestore.collection("clients").document(token).get().addOnCompleteListener(task -> {
                if(task.isSuccessful()){
                    favoritesCars.setValue(Objects.requireNonNull(task.getResult().toObject(Client.class)).getCars());
                }else{
                    favoritesCars.setValue(null);
                    Log.i("Errors", "Exception:", task.getException());
                }
            });
        }
        return favoritesCars;
    }

    public void updateFavoriteClientsCars(List<String> cars){
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if(user != null){
            String token = user.getUid();
            firestore.collection("clients").document(token).update("cars", cars).addOnCompleteListener(task -> {
                if(!task.isSuccessful()){
                    Log.i("Errors", "Exception:", task.getException());
                }
            });
        }
    }

    public LiveData<List<Car>> getCarsByPriceAndChildrenChair(int minPrice, int maxPrice, boolean childrenChair){
        MutableLiveData<List<Car>> cars = new MutableLiveData<>();
        firestore.collection("cars").whereGreaterThanOrEqualTo("price", minPrice).whereLessThanOrEqualTo("price", maxPrice).whereEqualTo("childrenChair", childrenChair).get().addOnCompleteListener(task -> {
            if(task.isSuccessful() && !task.getResult().isEmpty()){
                cars.setValue(task.getResult().toObjects(Car.class));
            }else{
                cars.setValue(null);
                Log.i("Errors", "Exception:", task.getException());
            }
        });
        return cars;
    }

    public LiveData<List<Car>> getCarsByAllChoices(int minPrice, int maxPrice, boolean childrenChair, String transmission, String typeOfFuel){
        MutableLiveData<List<Car>> cars = new MutableLiveData<>();
        firestore.collection("cars").whereEqualTo("childrenChair", childrenChair).whereGreaterThanOrEqualTo("price", minPrice).whereLessThanOrEqualTo("price", maxPrice).whereEqualTo("transmission", transmission).whereEqualTo("typeOfFuel", typeOfFuel).get().addOnCompleteListener(task -> {
            if(task.isSuccessful() && !task.getResult().isEmpty()){
                cars.setValue(task.getResult().toObjects(Car.class));
            }else{
                cars.setValue(null);
                Log.i("Errors", "Exception:", task.getException());
            }
        });
        return cars;
    }

    public LiveData<List<Car>> getCarsWithoutTransmission(int minPrice, int maxPrice, boolean childrenChair, String typeOfFuel){
        MutableLiveData<List<Car>> cars = new MutableLiveData<>();
        firestore.collection("cars").whereEqualTo("childrenChair", childrenChair).whereGreaterThanOrEqualTo("price", minPrice).whereLessThanOrEqualTo("price", maxPrice).whereEqualTo("typeOfFuel", typeOfFuel).get().addOnCompleteListener(task -> {
            if(task.isSuccessful() && !task.getResult().isEmpty()){
                cars.setValue(task.getResult().toObjects(Car.class));
            }else{
                cars.setValue(null);
                Log.i("Errors", "Exception:", task.getException());
            }
        });
        return cars;
    }

    public LiveData<List<Car>> getCarsWithoutTypeOfFuel(int minPrice, int maxPrice, boolean childrenChair, String transmission){
        MutableLiveData<List<Car>> cars = new MutableLiveData<>();
        firestore.collection("cars").whereEqualTo("childrenChair", childrenChair).whereGreaterThanOrEqualTo("price", minPrice).whereLessThanOrEqualTo("price", maxPrice).whereEqualTo("transmission", transmission).get().addOnCompleteListener(task -> {
            if(task.isSuccessful() && !task.getResult().isEmpty()){
                cars.setValue(task.getResult().toObjects(Car.class));
            }else{
                cars.setValue(null);
                Log.i("Errors", "Exception:", task.getException());
            }
        });
        return cars;
    }

    public LiveData<String> getClientDriverLicence(){
        MutableLiveData<String> driverLicence = new MutableLiveData<>();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if(firebaseUser != null){
            String token = firebaseUser.getUid();
            firestore.collection("clients").document(token).get().addOnCompleteListener(task -> {
                if(task.isSuccessful() && task.getResult().exists()){
                    driverLicence.setValue(Objects.requireNonNull(task.getResult().toObject(Client.class)).getDriverLicence());
                }else{
                    driverLicence.setValue(null);
                    Log.i("Errors", "Exception:", task.getException());
                }
            });
        }
        return driverLicence;
    }

    public LiveData<String> getProducerByModel(String model){
        MutableLiveData<String> producer = new MutableLiveData<>();
        firestore.collection("models").document(model).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    producer.setValue(Objects.requireNonNull(task.getResult().get("producer")).toString());
                }else{
                    producer.setValue(null);
                    Log.i("Errors", "Exception:", task.getException());
                }
            }
        });
        return producer;
    }

    public LiveData<List<String>> getStations(){
        MutableLiveData<List<String>> stations = new MutableLiveData<>();
        firestore.collection("stations").get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                List<String> strings = new ArrayList<>();
                for (QueryDocumentSnapshot queryDocumentSnapshot: task.getResult()){
                    strings.add(queryDocumentSnapshot.toObject(Station.class).getName());
                }
                stations.setValue(strings);
            }else{
                stations.setValue(null);
                Log.i("Errors", "Exception:", task.getException());
            }
        });
        return stations;
    }

    public LiveData<Boolean> checkCarIfHeWasBook(String idCar){
        MutableLiveData<Boolean> isCarWasAddedInRent = new MutableLiveData<>();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if(firebaseUser != null){
            String token = firebaseAuth.getUid();
            firestore.collection("rents").whereEqualTo("car", idCar).whereEqualTo("client", token).get().addOnCompleteListener(task -> {
                if(task.isSuccessful()){
                    if(!task.getResult().isEmpty()){
                        isCarWasAddedInRent.setValue(true);
                    }else{
                        isCarWasAddedInRent.setValue(false);
                    }
                }else{
                    isCarWasAddedInRent.setValue(null);
                    Log.i("Errors", "Exception:", task.getException());
                }
            });
        }
        return isCarWasAddedInRent;
    }

    public LiveData<String> getClientsUsername(){
        MutableLiveData<String> userName = new MutableLiveData<>();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if(firebaseUser != null){
            firestore.collection("users").document(firebaseUser.getUid()).get().addOnCompleteListener(task -> {
                if(task.isSuccessful() && task.getResult().exists()){
                    userName.setValue(Objects.requireNonNull(task.getResult().toObject(User.class)).getUsername());
                }else{
                    userName.setValue(null);
                    Log.i("Errors", "Exception", task.getException());
                }
            });
        }else{
            Log.i("Error", "You are not authorization");
        }
        return userName;
    }

    public LiveData<List<Rent>> getClientRents(){
        MutableLiveData<List<Rent>> rents = new MutableLiveData<>();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if(firebaseUser != null){
            String token = firebaseAuth.getUid();
            firestore.collection("rents").whereEqualTo("client", token).get().addOnCompleteListener(task -> {
                if(task.isSuccessful() && !task.getResult().isEmpty()){
                    rents.setValue(task.getResult().toObjects(Rent.class));
                }else{
                    rents.setValue(null);
                    Log.i("Errors", "Exception:", task.getException());
                }
            });
        }
        return rents;
    }

    public LiveData<String> getRegistrationToken(){
        MutableLiveData<String> registrationToken = new MutableLiveData<>();
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                registrationToken.setValue(task.getResult());
            }else{
                Log.i("TokenError", "Fetching FCM registration token failed", task.getException());
            }
        });
        return registrationToken;
    }

    public void updateFcmToken(String fcmToken){
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if(firebaseUser != null){
            String userID = firebaseUser.getUid();
            firestore.collection("users").document(userID).update("fcmToken", fcmToken).addOnSuccessListener(unused -> Log.i("Success", "Field fcmToken was updated")).addOnFailureListener(e -> Log.i("UpdateTokenError", "Update FCM registration token failed", e));
        }
    }

    public void createWorkRequest(List<Rent> rents, String token){
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();
        Gson gson = new Gson();
        String rentsJson = gson.toJson(rents);
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("Data", rentsJson);
        dataMap.put("Token", token);
        WorkRequest workRequest = new PeriodicWorkRequest.Builder(CheckRentWorker.class, 15, TimeUnit.MINUTES)
                .setInputData(new Data.Builder()
                        .putAll(dataMap)
                        .build())
                .setConstraints(constraints)
                .build();
        sendRequest(workRequest);
    }

    private void sendRequest(WorkRequest workRequest){
        WorkManager.getInstance(context).enqueue(workRequest);
    }

    public void updateFineRent(String idRent, int fine){
        firestore.collection("rents").document(idRent).update("fines", fine).addOnSuccessListener(unused -> Log.i("Success", "Fines updated")).addOnFailureListener(e -> Log.i("Error", "Update fines exception: ", e));
    }

    public void updateRentStatus(String idRent, String status){
        firestore.collection("rents").document(idRent).update("status", status).addOnSuccessListener(unused -> Log.i("Success", "Status updated")).addOnFailureListener(e -> Log.i("Error", "Update status exception: ", e));
    }

    public void createDriverLicence(DriverLicence driverLicence){
        firestore.collection("driverslicence").document(driverLicence.getId()).set(driverLicence).addOnCompleteListener(task -> {
            if(!task.isSuccessful()){
                Log.i("Errors", "Exception:", task.getException());
            }
        });
    }

    public void updateCreditCardClient(List<String> cards, String token){
        firestore.collection("clients").document(token).update("cards", cards).addOnCompleteListener(task -> {
            if(!task.isSuccessful()){
                Log.i("Errors", "Exception:", task.getException());
            }
        });
    }

    public void updateDriverLicenceClient(String driverLicence){
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if(user != null){
            String token = user.getUid();
            firestore.collection("clients").document(token).update("driverLicence", driverLicence).addOnCompleteListener(task -> {
                if(!task.isSuccessful()){
                    Log.i("Errors", "Exception:", task.getException());
                }
            });
        }
    }

    public void createRent(Rent rent){
        firestore.collection("rents").document(rent.getId()).set(rent).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(!task.isSuccessful()){
                    Log.i("Errors", "Exception:", task.getException());
                }
            }
        });
    }

    public void deleteRent(String idRent){
        firestore.collection("rents").document(idRent).delete().addOnCompleteListener(task -> {
            if(!task.isSuccessful()){
                Log.i("Errors", "Exception:", task.getException());
            }
        });
    }
}
