package com.example.myapplication.rentcarapp.view.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.rentcarapp.R;
import com.example.myapplication.rentcarapp.adapter.CarAdapter;
import com.example.myapplication.rentcarapp.adapter.RecyclerViewInterface;
import com.example.myapplication.rentcarapp.model.firestore.models.Car;
import com.example.myapplication.rentcarapp.view.activity.DetailActivity;
import com.example.myapplication.rentcarapp.view.activity.FilterActivity;
import com.example.myapplication.rentcarapp.view.activity.MainWindowActivity;
import com.example.myapplication.rentcarapp.view.activity.MyProfileActivity;
import com.example.myapplication.rentcarapp.viewmodel.CarViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.squareup.picasso.Picasso;

import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class HomeFragment extends Fragment implements RecyclerViewInterface {
    RecyclerView carList;
    SearchView searchView;
    TextView homeUsername;
    FloatingActionButton filter;
    CarViewModel carViewModel;
    CarAdapter carAdapter;
    List<Car> listCar;
    ShapeableImageView homeAvatar;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        carViewModel = new ViewModelProvider(this).get(CarViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        homeUsername = view.findViewById(R.id.homeUsername);
        homeAvatar = view.findViewById(R.id.homeAvatar);
        homeAvatar.setOnClickListener(this::myProfile);
        carList = view.findViewById(R.id.carList);
        searchView = view.findViewById(R.id.searchView);
        filter = view.findViewById(R.id.floatingActionButton);
        filter.setOnClickListener(this::filtersCars);
        isExistFilterData();
        searchCar();
    }

    @Override
    public void onStart() {
        super.onStart();
        initUser();
    }

    private void initData(){
        carViewModel.getAllCars().observe(requireActivity(), cars -> {
            if(cars != null){
                initRecyclerView(cars);
            }else{
                Toast.makeText(getContext(), "Something was happened. Error", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void searchCar(){
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {return false;}

            @Override
            public boolean onQueryTextChange(String newText) {
                getCarByModel(newText);
                return true;
            }
        });
    }

    private void getCarByModel(String model){
        carViewModel.getCarByModel(model).observe(requireActivity(), cars -> {
            if(cars != null){
                initRecyclerView(cars);
            }else{
                Toast.makeText(getContext(), "Sorry, but there is no such car.", Toast.LENGTH_LONG).show();
                initData();
            }
        });
    }

    private void isExistFilterData(){
        MainWindowActivity mainWindowActivity = (MainWindowActivity) getActivity();
        List<Car> filterCars = mainWindowActivity.getCars();
        if(filterCars != null){
            initRecyclerView(filterCars);
        }else{
            initData();
        }
    }

    private void initRecyclerView(List<Car> cars){
        listCar = cars;
        carAdapter = new CarAdapter(cars, this);
        carList.setAdapter(carAdapter);
        carList.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void filtersCars(View view){
        Intent intent = new Intent(getActivity(), FilterActivity.class);
        startActivity(intent);
    }

    private void initUser(){
        carViewModel.getClient().observe(requireActivity(), client -> {
            if(client != null){
                Picasso.get().load(client.getPhoto()).into(homeAvatar);
                showClientUserName();
            }
        });
    }

    private void showClientUserName(){
        carViewModel.getClientsUserName().observe(requireActivity(), userName -> {
            if(userName != null){
                homeUsername.setText(userName);
            }
        });
    }

    private void myProfile(View view){
        Intent intent = new Intent(requireActivity(), MyProfileActivity.class);
        startActivity(intent);
    }

    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(getActivity(), DetailActivity.class);
        intent.putExtra("Car", listCar.get(position));
        startActivity(intent);
    }
}