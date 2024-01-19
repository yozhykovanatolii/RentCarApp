package com.example.myapplication.rentcarapp.viewmodel;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import java.util.regex.*;

import com.example.myapplication.rentcarapp.model.firestore.models.Client;
import com.example.myapplication.rentcarapp.model.firestore.models.User;
import com.example.myapplication.rentcarapp.model.repository.AuthRepository;
import com.google.firebase.auth.AuthCredential;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class AuthViewModel extends ViewModel {
    private AuthRepository authRepository;

    @Inject
    public AuthViewModel(AuthRepository authRepository){
        this.authRepository = authRepository;
    }

    public LiveData<Boolean> isEmailAndPasswordWriteCorrect(String email, String newPassword){
        if(isEmailWriteCorrect(email) && isPasswordLargeThanOrEqualEight(newPassword)){
            return new MutableLiveData<>(true);
        }else{
            return new MutableLiveData<>(false);
        }
    }

    public LiveData<Boolean> isFullNameWriteCorrect(String fullName){
        if(preventFullName(fullName)){
            return new MutableLiveData<>(true);
        }else{
            return new MutableLiveData<>(false);
        }
    }

    public LiveData<Boolean> isEmailCorrect(String email){
        if(isEmailWriteCorrect(email)){
            return new MutableLiveData<>(true);
        }else{
            return new MutableLiveData<>(false);
        }
    }

    public LiveData<Boolean> isPasswordWriteCorrect(String password){
        if(isPasswordLargeThanOrEqualEight(password)){
            return new MutableLiveData<>(true);
        }else{
            return new MutableLiveData<>(false);
        }
    }

    public LiveData<Boolean> isUserPhoneCorrect(String phone){
        if(isPhoneWriteCorrect(phone)){
            return new MutableLiveData<>(true);
        }else{
            return new MutableLiveData<>(false);
        }
    }
    public LiveData<Boolean> isNewPasswordEqualConfirmedPassword(String newPassword, String confirmNewPassword){
        if(isNewPasswordEqualConfirmNewPassword(newPassword, confirmNewPassword)){
            return new MutableLiveData<>(true);
        }else{
            return new MutableLiveData<>(false);
        }
    }

    public LiveData<String> getClientsUserName(){
        return authRepository.getClientsUsername();
    }

    public LiveData<User> getUserByUsername(String username){
        return authRepository.getUserByUsername(username);
    }

    public LiveData<User> getUserByToken(String username){
        return authRepository.getUserByToken(username);
    }

    public LiveData<User> getUserByUsernameAndPassword(String username, String password){
        return authRepository.getUserByUsernameAndPassword(username, password);
    }

    public LiveData<Client> getClient(){
        return authRepository.getClient();
    }


    public LiveData<String> getClientTokenByEmail(String email){
        return authRepository.getClientTokenByEmail(email);
    }


    public LiveData<String> getClientEmailByToken(String token){
        return authRepository.getClientEmailByToken(token);
    }

    public void saveImageInCloudStorage(Client client){
        authRepository.saveImageInCloudStorage(client);
    }

    public void updateUsersPassword(User user, String email, String newPassword){
        authRepository.updateUsersPassword(user, email, newPassword);
    }

    public void updateEmailAndPasswordInFirebase(String newEmail, String newPassword, String email, String password){
        authRepository.updateEmailAndPasswordInFirebase(newEmail, newPassword, email, password);
    }

    public void updateUser(User user){
        authRepository.createUser(user);
    }

    public void signUpWithEmailAndPassword(Client client, User newUser, String password){
        authRepository.signUpWithEmailAndPassword(client,newUser, password);
    }

    public void signInByEmailAndPassword(String email, String password){
        authRepository.signInByEmailAndPassword(email, password);
    }

    public void authenticationByGoogle(AuthCredential credential){
        authRepository.authenticationByGoogle(credential);
    }

    public void updateClient(Client client){
        authRepository.createClient(client);
    }

    public void updateUser(String oldUsername, String newUsername){
        authRepository.updateUser(oldUsername, newUsername);
    }

    public void logOut(){
        authRepository.logOut();
    }

    private boolean isNewPasswordEqualConfirmNewPassword(String newPassword, String confirmNewPassword){
        return newPassword.equals(confirmNewPassword) && newPassword.length() != 0 && confirmNewPassword.length() != 0;
    }

    private boolean isPasswordLargeThanOrEqualEight(String newPassword){
        return newPassword.length() >= 8;
    }

    private boolean isEmailWriteCorrect(String email){
        return email.endsWith("@gmail.com");
    }

    private boolean isPhoneWriteCorrect(String phone){
        String pattern = "^\\+380\\d{9}$";
        return Pattern.matches(pattern, phone);
    }

    private boolean preventFullName(String fullName){
        return !fullName.matches(".*\\d.*") && !fullName.matches("\".*[!@#$%^&*()].*\"");
    }
}
