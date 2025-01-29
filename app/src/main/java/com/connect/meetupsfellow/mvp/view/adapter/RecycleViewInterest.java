package com.connect.meetupsfellow.mvp.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.connect.meetupsfellow.R;
import com.connect.meetupsfellow.mvp.view.model.RecycleModel;

import java.util.ArrayList;
import java.util.List;

public class RecycleViewInterest extends RecyclerView.Adapter<RecycleViewInterest.MyViewHolder> {

    Context context;
    ArrayList<RecycleModel> userInterest;
    List<RecycleModel> userInterestAll;

    public RecycleViewInterest(Context context, ArrayList<RecycleModel> userInterest) {
        this.context = context;
        this.userInterest = userInterest;
        this.userInterest = new ArrayList<>(userInterest);
    }


    @NonNull
    @Override
    public RecycleViewInterest.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //Inflate the layout (Giving look)
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.user_interests_rv, parent, false);
        return new RecycleViewInterest.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecycleViewInterest.MyViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return userInterest.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        //grabbing the views from rv_column.xml

        ImageView pic;
        TextView name;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            pic = itemView.findViewById(R.id.interestImg);

        }
    }
}
