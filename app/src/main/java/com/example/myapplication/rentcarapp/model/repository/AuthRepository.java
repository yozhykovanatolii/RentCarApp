package com.example.myapplication.rentcarapp.model.repository;

import android.net.Uri;
import android.util.Log;


import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.myapplication.rentcarapp.model.firestore.models.Client;
import com.example.myapplication.rentcarapp.model.firestore.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

public class AuthRepository {
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;
    private FirebaseStorage firebaseStorage;

    @Inject
    public AuthRepository(FirebaseAuth firebaseAuth, FirebaseFirestore firestore, FirebaseStorage firebaseStorage) {
        this.firebaseAuth = firebaseAuth;
        this.firestore = firestore;
        this.firebaseStorage = firebaseStorage;
    }

    public LiveData<User> getUserByUsername(String username){
        MutableLiveData<User> userMutableLiveData = new MutableLiveData<>();
        firestore.collection("users").whereEqualTo("username", username).get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                if(!task.getResult().isEmpty()){
                    for(QueryDocumentSnapshot queryDocumentSnapshot: task.getResult()){
                        userMutableLiveData.setValue(queryDocumentSnapshot.toObject(User.class));
                    }
                }else{
                    userMutableLiveData.setValue(null);
                }
            }else{
                userMutableLiveData.setValue(null);
                Log.i("Errors", "Exception", task.getException());
            }
        });
        return userMutableLiveData;
    }

    public LiveData<User> getUserByToken(String token){
        MutableLiveData<User> userMutableLiveData = new MutableLiveData<>();
        firestore.collection("users").document(token).get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                if(task.getResult().exists()){
                    userMutableLiveData.setValue(task.getResult().toObject(User.class));
                }else{
                    userMutableLiveData.setValue(null);
                }
            }else{
                userMutableLiveData.setValue(null);
                Log.i("Errors", "Exception", task.getException());
            }
        });
        return userMutableLiveData;
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
        }
        return userName;
    }

    public LiveData<User> getUserByUsernameAndPassword(String username, String password){
        MutableLiveData<User> userMutableLiveData = new MutableLiveData<>();
        firestore.collection("users").whereEqualTo("username", username).whereEqualTo("password", password).get().addOnCompleteListener(task -> {
            if(task.isSuccessful() && !task.getResult().isEmpty()){
                for(QueryDocumentSnapshot queryDocumentSnapshot: task.getResult()){
                    userMutableLiveData.setValue(queryDocumentSnapshot.toObject(User.class));
                }
            }else{
                userMutableLiveData.setValue(null);
                Log.i("Exceptions", "Error", task.getException());
            }
        });
        return userMutableLiveData;
    }

    public LiveData<String> getClientEmailByToken(String token){
        MutableLiveData<String> clientEmail = new MutableLiveData<>();
        firestore.collection("clients").whereEqualTo("token", token).get().addOnCompleteListener(task -> {
            if(task.isSuccessful() && !task.getResult().isEmpty()){
                for(QueryDocumentSnapshot queryDocumentSnapshot: task.getResult()){
                    clientEmail.setValue(queryDocumentSnapshot.toObject(Client.class).getEmail());
                }
            }else{
                clientEmail.setValue(null);
                Log.i("Exceptions", "Error", task.getException());
            }
        });
        return clientEmail;
    }

    public LiveData<String> getClientTokenByEmail(String email){
        MutableLiveData<String> userMutableLiveData = new MutableLiveData<>();
        firestore.collection("clients").whereEqualTo("email", email).get().addOnCompleteListener(task -> {
            if(task.isSuccessful() && !task.getResult().isEmpty()){
                for(QueryDocumentSnapshot queryDocumentSnapshot: task.getResult()){
                    String token = queryDocumentSnapshot.toObject(Client.class).getToken();
                    userMutableLiveData.setValue(token);
                }
            }else{
                userMutableLiveData.setValue(null);
                Log.i("Errors", "Exception:", task.getException());
            }
        });
        return userMutableLiveData;
    }


    public LiveData<Client> getClient(){
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        MutableLiveData<Client> clientMutableLiveData = new MutableLiveData<>();
        if(firebaseUser != null){
            String token = firebaseUser.getUid();
            firestore.collection("clients").document(token).get().addOnCompleteListener(task -> {
                if(task.isSuccessful()){
                    clientMutableLiveData.setValue(task.getResult().toObject(Client.class));
                }else{
                    clientMutableLiveData.setValue(null);
                    Log.i("Errors", "Exception:", task.getException());
                }
            });
        }else{
            clientMutableLiveData.setValue(null);
        }
        return clientMutableLiveData;
    }

    public void logOut(){
        firebaseAuth.signOut();
    }

    public void updateUsersPassword(User user, String email, String newPassword){
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        AuthCredential credential = EmailAuthProvider.getCredential(email, user.getPassword());
        if(firebaseUser != null){
            firebaseUser.reauthenticate(credential).addOnCompleteListener(task -> {
                if(task.isSuccessful()){
                    firebaseUser.updatePassword(newPassword);
                    updatePasswordInFirestore(user.getToken(), newPassword);
                }else{
                    Log.i("Errors", "Exception:", task.getException());
                }
            });
        }
    }

    public void updatePasswordInFirestore(String token, String newPassword){
        firestore.collection("users").document(token).update("password", newPassword).addOnCompleteListener(task -> {
            if(!task.isSuccessful()){
                Log.i("Errors", "Exception:", task.getException());
            }
        });
    }

    public void updateEmailAndPasswordInFirebase(String newEmail, String newPassword, String email, String password){
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        AuthCredential credential = EmailAuthProvider.getCredential(email, password);
        if(firebaseUser != null){
            firebaseUser.reauthenticate(credential).addOnCompleteListener(task -> {
                Log.i("Status", "Perfect");
                if (task.isSuccessful()) {
                    firebaseUser.updateEmail(newEmail);
                    firebaseUser.updatePassword(newPassword);
                }else{
                    Log.i("Errors", "Exception:", task.getException());
                }
            });
        }
    }


    public void updateUser(String oldUsername, String newUsername){
        firestore.collection("users").document(oldUsername).update("username", newUsername).addOnCompleteListener(task -> {
            if(!task.isSuccessful()){
                Log.i("Errors", "Exception:", task.getException());
            }
        });
    }

    public void authenticationByGoogle(AuthCredential credential){
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user != null){
                    User newUser = new User(user.getDisplayName(), user.getProviderId());
                    newUser.setToken(user.getUid());
                    createUser(newUser);
                    Client newClient = new Client(user.getDisplayName(), user.getPhoneNumber(), user.getEmail(), Objects.requireNonNull(user.getPhotoUrl()).toString());
                    newClient.setToken(user.getUid());
                    createClient(newClient);
                }
            }else{
                Log.i("Errors", "Exception:", task.getException());
            }
        });
    }

    public void signUpWithEmailAndPassword(Client client, User newUser, String password){
        firebaseAuth.createUserWithEmailAndPassword(client.getEmail(), password).addOnCompleteListener(task -> {
            if(!task.isSuccessful()){
                Log.i("Exceptions", "Error", task.getException());
            }else{
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user != null){
                    client.setToken(user.getUid());
                    newUser.setToken(user.getUid());
                    saveImageInCloudStorage(client);
                    createUser(newUser);
                }
            }
        });
    }

    public LiveData<String> signInByEmailAndPassword(String email, String password){
        MutableLiveData<String> userToken = new MutableLiveData<>();
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if(!task.isSuccessful()){
                userToken.setValue(null);
                Log.i("Errors", "Exception:", task.getException());
            }else{
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                userToken.setValue(firebaseUser.getUid());
            }
        });
        return userToken;
    }

    public void createUser(User user){
        firestore.collection("users").document(user.getToken()).set(user).addOnCompleteListener(task -> {
            if(!task.isSuccessful()){
                Log.i("Errors", "Exception", task.getException());
            }
        });
    }

    public void createClient(Client client){
        firestore.collection("clients").document(client.getToken()).set(client).addOnCompleteListener(task -> {
            if(!task.isSuccessful()){
                Log.i("Errors", "Exception", task.getException());
            }
        });
    }

    public void saveImageInCloudStorage(Client client){
        Uri image = Uri.parse(client.getPhoto());
        StorageReference storageReference = firebaseStorage.getReference().child("image/" + image.getLastPathSegment());
        storageReference.putFile(image).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                storageReference.getDownloadUrl().addOnCompleteListener(task1 -> {
                    client.setPhoto(task1.getResult().toString());
                    createClient(client);
                });
            }else{
                Log.i("Errors", "Exception", task.getException());
            }
        });
    }

}
