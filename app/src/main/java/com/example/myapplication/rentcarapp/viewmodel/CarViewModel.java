package com.example.myapplication.rentcarapp.viewmodel;


import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.myapplication.rentcarapp.model.firestore.models.Car;
import com.example.myapplication.rentcarapp.model.firestore.models.Client;
import com.example.myapplication.rentcarapp.model.firestore.models.DriverLicence;
import com.example.myapplication.rentcarapp.model.firestore.models.Rent;
import com.example.myapplication.rentcarapp.model.firestore.models.Review;
import com.example.myapplication.rentcarapp.model.repository.CarRepository;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class CarViewModel extends ViewModel {
    private CarRepository carRepository;

    @Inject
    public CarViewModel(CarRepository carRepository){
        this.carRepository = carRepository;
    }

    public LiveData<Client> getClient(){
        return carRepository.getClient();
    }

    public LiveData<List<Car>> getAllCars(){
        return carRepository.getAllCars();
    }

    public LiveData<List<Car>> getCarByModel(String model){
        return carRepository.getCarByModel(model);
    }

    public LiveData<List<Car>> getCarsById(List<String> ids){
        return carRepository.getCarsById(ids);
    }

    public LiveData<List<String>> getFavoriteClientsCars(){
        return carRepository.getFavoriteClientsCars();
    }

    public LiveData<List<Rent>> getClientRents(){
        return carRepository.getClientRents();
    }

    public LiveData<String> getClientDriverLicence(){
        return carRepository.getClientDriverLicence();
    }

    public LiveData<Boolean> checkCarIfHeWasBook(String idCar){
        return carRepository.checkCarIfHeWasBook(idCar);
    }

    public LiveData<List<String>> getStations(){
        return carRepository.getStations();
    }

    public void updateDriverLicenceClient(String driverLicence){
        carRepository.updateDriverLicenceClient(driverLicence);
    }
    public void updateFavoriteClientsCars(List<String> cars){
        carRepository.updateFavoriteClientsCars(cars);
    }

    public void createDriverLicence(DriverLicence driverLicence){
        carRepository.createDriverLicence(driverLicence);
    }

    public void createReview(Review review){
        carRepository.createReview(review);
    }

    public void updateAvgRating(float newAvgRating, String idCar){
        carRepository.updateAvgRating(newAvgRating, idCar);
    }

    public LiveData<List<Review>> getAllReviewsByCar(String idCar){
        return carRepository.getAllReviewsByCar(idCar);
    }

    public LiveData<List<String>> getAllUsersReviewByCar(String idCar){
        return carRepository.getAllUsersReviewByCar(idCar);
    }

    public LiveData<List<String>> getAllUsersByTheirID(List<String> usersID){
        return carRepository.getAllUsersByTheirID(usersID);
    }

    public LiveData<String> getClientsUserName(){
        return carRepository.getClientsUsername();
    }

    public LiveData<String> getRegistrationToken(){
         return carRepository.getRegistrationToken();
    }

    public void updateFcmToken(String fcmToken){
        carRepository.updateFcmToken(fcmToken);
    }

    public void createWorkRequest(List<Rent> rents, String token){
        carRepository.createWorkRequest(rents, token);
    }

    public void createRent(Rent rent){
        carRepository.createRent(rent);
    }

    public void deleteRent(String idRent){
        carRepository.deleteRent(idRent);
    }

    public void updateFineRent(String idRent, int fine){
        carRepository.updateFineRent(idRent, fine);
    }

    public void updateRentStatus(String idRent, String status){
        carRepository.updateRentStatus(idRent, status);
    }

    public LiveData<Boolean> isDriverLicenceNumberWriteCorrect(String driverLicenceNumber){
        if(isDriverLicenceNumberCorrect(driverLicenceNumber)){
            return new MutableLiveData<>(true);
        }else{
            return new MutableLiveData<>(false);
        }
    }

    public LiveData<Boolean> checkReviewTextAndRating(int lengthReviewText, float rating){
        if(lengthReviewText > 0 && rating > 0){
            return new MutableLiveData<>(true);
        }else{
            return new MutableLiveData<>(false);
        }
    }

    public LiveData<List<Car>> confirmChoice(String childrenChair, String transmission, String typeOfFuel, String sortBy, int minPrice, int maxPrice){
        System.out.println(sortBy + "\n" + transmission + "\n" + typeOfFuel);
        if(isSortByEqualNull(sortBy, transmission, typeOfFuel)){
            return carRepository.getCarsWithoutSortBy(minPrice, maxPrice, childrenChair, typeOfFuel, transmission);
        }if(isTransmissionAndSortByEqualNull(transmission, typeOfFuel, sortBy)){
            return carRepository.getCarsWithoutTransmissionAndSortBy(minPrice, maxPrice, childrenChair, typeOfFuel);
        }if(isTypeOfFuelAndSortByEqualNull(transmission, typeOfFuel, sortBy)){
            return carRepository.getCarsWithoutTypeOfFuelAndSortBy(minPrice, maxPrice, childrenChair, transmission);
        }if(isTransmissionAndTypeOfFuelEqualNull(transmission, typeOfFuel, sortBy)){
            return carRepository.getCarsWithoutTransmissionAndTypeOfFuel(minPrice, maxPrice, childrenChair, sortBy);
        }if(isTransmissionAndTypeOfFuelAndSortByEqualNull(transmission, typeOfFuel, sortBy)){
            return carRepository.getCarsByPriceAndChildrenChair(minPrice, maxPrice, childrenChair);
        }if(isTransmissionEqualNull(transmission, typeOfFuel, sortBy)){
            return carRepository.getCarsWithoutTransmission(minPrice, maxPrice, childrenChair, typeOfFuel, sortBy);
        }if(isTypeOfFuelEqualNull(transmission, typeOfFuel, sortBy)){
            return carRepository.getCarsWithoutTypeOfFuel(minPrice, maxPrice, childrenChair, transmission, sortBy);
        }
        return carRepository.getCarsByAllChoices(minPrice, maxPrice, childrenChair, transmission, typeOfFuel, sortBy);
    }

    public LiveData<Long> checkDate(String date){
        MutableLiveData<Long> differenceBetweenDates = new MutableLiveData<>();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        try {
            Date currentDate = new Date();
            Date returnDate = simpleDateFormat.parse(date);
            long difference_In_Time = returnDate.getTime() - currentDate.getTime();
            differenceBetweenDates.setValue((difference_In_Time / (1000 * 60 * 60 * 24)) % 365);
        }catch (ParseException e){
            e.printStackTrace();
        }
        return differenceBetweenDates;
    }

    public LiveData<Integer> calculateDifferenceBetweenTwoDates(String startDate, String endDate, int price){
        MutableLiveData<Integer> priceRent = new MutableLiveData<>();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        try {
            Date beginDate = simpleDateFormat.parse(startDate);
            Date finalDate = simpleDateFormat.parse(endDate);
            long difference_In_Time = finalDate.getTime() - beginDate.getTime();
            long difference_In_Days = (difference_In_Time / (1000 * 60 * 60 * 24)) % 365;
            priceRent.setValue(calculatePriceWithDifferenceInDays(difference_In_Days, price));
        }catch (ParseException e){
            e.printStackTrace();
        }
        return priceRent;
    }

    private Integer calculatePriceWithDifferenceInDays(long difference_In_Days, int price){
        if(difference_In_Days >= 4 && difference_In_Days <= 9){
            price -= 200;
        }if (difference_In_Days >= 10 && difference_In_Days <= 25) {
            price -= 300;
        }if (difference_In_Days >= 26){
            price -= 400;
        }
        return price;
    }

    private boolean isSortByEqualNull(String sortBy, String transmission, String typeOfFuel){
        return sortBy == null && (transmission != null && typeOfFuel != null);
    }

    private boolean isTransmissionEqualNull(String transmission, String typeOfFuel, String sortBy){
        return transmission == null && (sortBy != null && typeOfFuel != null);
    }

    private boolean isTypeOfFuelEqualNull(String transmission, String typeOfFuel, String sortBy){
        return  typeOfFuel == null && (transmission != null && sortBy != null);
    }

    private boolean isTransmissionAndSortByEqualNull(String transmission, String typeOfFuel, String sortBy){
        return (transmission == null && sortBy == null) && typeOfFuel != null;
    }

    private boolean isTypeOfFuelAndSortByEqualNull(String transmission, String typeOfFuel, String sortBy){
        return (typeOfFuel == null && sortBy == null) && transmission != null;
    }

    private boolean isTransmissionAndTypeOfFuelEqualNull(String transmission, String typeOfFuel, String sortBy){
        return (typeOfFuel == null && transmission == null) && sortBy != null;
    }

    private boolean isTransmissionAndTypeOfFuelAndSortByEqualNull(String transmission, String typeOfFuel, String sortBy){
        return transmission == null && typeOfFuel == null && sortBy == null;
    }

    private boolean isDriverLicenceNumberCorrect(String driverLicenceNumber){
        return driverLicenceNumber.length() == 9;
    }
}
