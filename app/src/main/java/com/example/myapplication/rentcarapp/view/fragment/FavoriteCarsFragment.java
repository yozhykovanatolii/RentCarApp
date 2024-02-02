package com.example.myapplication.rentcarapp.view.fragment;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.myapplication.rentcarapp.R;
import com.example.myapplication.rentcarapp.adapter.CarAdapter;
import com.example.myapplication.rentcarapp.adapter.RecyclerViewInterface;
import com.example.myapplication.rentcarapp.model.firestore.models.Car;
import com.example.myapplication.rentcarapp.view.activity.DetailActivity;
import com.example.myapplication.rentcarapp.viewmodel.CarViewModel;

import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;


@AndroidEntryPoint
public class FavoriteCarsFragment extends Fragment implements RecyclerViewInterface {
    RecyclerView favoriteCars;
    CarViewModel carViewModel;
    CarAdapter carAdapter;
    List<Car> carList;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        carViewModel = new ViewModelProvider(this).get(CarViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_favorite_cars, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        favoriteCars = view.findViewById(R.id.favoriteCars);
        initData();
    }

    private void initData(){
        carViewModel.getFavoriteClientsCars().observe(requireActivity(), strings -> {
            if(!strings.isEmpty()){
                getCars(strings);
            }
        });
    }

    private void getCars(List<String> strings){
        carViewModel.getCarsById(strings).observe(requireActivity(), this::initRecyclerView);
    }

    private void initRecyclerView(List<Car> cars){
        carList = cars;
        carAdapter = new CarAdapter(cars, this);
        favoriteCars.setAdapter(carAdapter);
        favoriteCars.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(getActivity(), DetailActivity.class);
        intent.putExtra("Car", carList.get(position));
        startActivity(intent);
    }
}