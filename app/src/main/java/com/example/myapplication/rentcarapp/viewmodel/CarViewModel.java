package com.example.myapplication.rentcarapp.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.myapplication.rentcarapp.model.firestore.models.Car;
import com.example.myapplication.rentcarapp.model.firestore.models.Client;
import com.example.myapplication.rentcarapp.model.firestore.models.CreditCard;
import com.example.myapplication.rentcarapp.model.firestore.models.DriverLicence;
import com.example.myapplication.rentcarapp.model.firestore.models.Rent;
import com.example.myapplication.rentcarapp.model.firestore.models.Station;
import com.example.myapplication.rentcarapp.model.repository.CarRepository;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CarViewModel extends ViewModel {
    private CarRepository carRepository;

    public CarViewModel(){
        carRepository = new CarRepository();
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

    public LiveData<List<String>> getClientCreditCards(){
        return carRepository.getClientCreditCards();
    }

    public LiveData<List<CreditCard>> getCreditCards(List<String> clientCards){
        return carRepository.getCreditCards(clientCards);
    }

    public LiveData<List<Rent>> getClientRents(){
        return carRepository.getClientRents();
    }

    public LiveData<List<String>> getBanks(){
        return carRepository.getBanks();
    }

    public LiveData<String> getClientDriverLicence(){
        return carRepository.getClientDriverLicence();
    }

    public LiveData<String> getProducerByModel(String model){
        return carRepository.getProducerByModel(model);
    }

    public LiveData<Boolean> checkCarIfHeWasBook(String idCar){
        return carRepository.checkCarIfHeWasBook(idCar);
    }

    public LiveData<List<String>> getStations(){
        return carRepository.getStations();
    }

    public void updateCreditCardClient(List<String> cards, String token){
        carRepository.updateCreditCardClient(cards, token);
    }

    public void updateDriverLicenceClient(String driverLicence){
        carRepository.updateDriverLicenceClient(driverLicence);
    }
    public void updateFavoriteClientsCars(List<String> cars){
        carRepository.updateFavoriteClientsCars(cars);
    }

    public void createCreditCard(CreditCard creditCard){
        carRepository.createCreditCard(creditCard);
    }

    public void createDriverLicence(DriverLicence driverLicence){
        carRepository.createDriverLicence(driverLicence);
    }

    public void getRegistrationToken(){
        carRepository.getRegistrationToken();
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

    public LiveData<Boolean> isCreditCardNumberWriteCorrect(String creditCard){
        if(isCreditCardNumberLengthEqualNineteen(creditCard)){
            return new MutableLiveData<>(true);
        }else{
            return new MutableLiveData<>(false);
        }
    }

    public LiveData<Boolean> isSpecialCodeWriteCorrect(String specialCode){
        if(isSpecialCodeLengthEqualThree(specialCode)){
            return new MutableLiveData<>(true);
        }else{
            return new MutableLiveData<>(false);
        }
    }

    public LiveData<Boolean> isDateOfCardWriteCorrect(String cardDate){
        if(isDateOfCardLengthEqualFive(cardDate)){
            return new MutableLiveData<>(true);
        }else{
            return new MutableLiveData<>(false);
        }
    }

    public LiveData<String> choiceTransmission(boolean automaton, boolean mechanic){
        if(automaton){
            return new MutableLiveData<>("Automaton");
        }if(mechanic){
            return new MutableLiveData<>("Mechanic");
        }
        return new MutableLiveData<>(null);
    }

    public LiveData<String> choiceTypeOfFuel(boolean gasoline, boolean diesel){
        if(gasoline){
            return new MutableLiveData<>("Gasoline");
        }if(diesel){
            return new MutableLiveData<>("Diesel");
        }
        return new MutableLiveData<>(null);
    }

    public LiveData<List<Car>> confirmChoice(boolean childrenChair, String transmission, String typeOfFuel, int minPrice, int maxPrice){
        if(isTransmissionEqualNull(transmission, typeOfFuel)){
            return carRepository.getCarsWithoutTransmission(minPrice, maxPrice, childrenChair, typeOfFuel);
        }if(isTypeOfFuelEqualNull(transmission, typeOfFuel)){
            return carRepository.getCarsWithoutTypeOfFuel(minPrice, maxPrice, childrenChair, transmission);
        }if(isTransmissionAndTypeOfFuelEqualNull(transmission, typeOfFuel)){
            return carRepository.getCarsByPriceAndChildrenChair(minPrice, maxPrice, childrenChair);
        }
        return carRepository.getCarsByAllChoices(minPrice, maxPrice, childrenChair, transmission, typeOfFuel);
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

    private boolean isTransmissionEqualNull(String transmission, String typeOfFuel){
        return transmission == null && typeOfFuel != null;
    }

    private boolean isTypeOfFuelEqualNull(String transmission, String typeOfFuel){
        return transmission != null && typeOfFuel == null;
    }

    private boolean isTransmissionAndTypeOfFuelEqualNull(String transmission, String typeOfFuel){
        return transmission == null && typeOfFuel == null;
    }

    private boolean isDriverLicenceNumberCorrect(String driverLicenceNumber){
        return driverLicenceNumber.length() == 9;
    }

    private boolean isCreditCardNumberLengthEqualNineteen(String creditCard){
        return creditCard.length() == 19 && creditCard.contains(" ");
    }

    private boolean isSpecialCodeLengthEqualThree(String specialCode){
        return specialCode.length() == 3;
    }

    private boolean isDateOfCardLengthEqualFive(String cardDate){
        return cardDate.length() == 5 && cardDate.contains("/") && !cardDate.startsWith("/") && !cardDate.endsWith("/");
    }

}
