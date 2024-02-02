package com.example.myapplication.rentcarapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.rentcarapp.R;
import com.example.myapplication.rentcarapp.model.firestore.models.Review;
import com.example.myapplication.rentcarapp.util.TextConverterUtil;

import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.Hold> {
    private List<String> users;
    private List<Review> reviews;
    private TextConverterUtil converter;

    public ReviewAdapter(List<String> users, List<Review> reviews){
        this.users = users;
        this.reviews = reviews;
    }

    @NonNull
    @Override
    public Hold onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.review_list, parent, false);
        return new Hold(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Hold holder, int position) {
        Review review = reviews.get(position);
        String user = users.get(position);
        converter = new TextConverterUtil();
        holder.username.setText(user);
        holder.rating.setText(Float.toString(review.getRating()));
        holder.text.setText(converter.getText(review.getText()));
        holder.date.setText(review.getDate());
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    public static class Hold extends RecyclerView.ViewHolder{
        TextView username, rating, text, date;

        public Hold(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.usernameClient);
            rating = itemView.findViewById(R.id.rating);
            text = itemView.findViewById(R.id.textReview);
            date = itemView.findViewById(R.id.date);
        }
    }
}
