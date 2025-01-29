package com.connect.meetupsfellow.mvp.view.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.connect.meetupsfellow.R;
import com.connect.meetupsfellow.constants.Constants;
import com.connect.meetupsfellow.mvp.view.activities.ImagePagerActivity;
import com.connect.meetupsfellow.mvp.view.activities.PrivateAlbumActivity;
import com.connect.meetupsfellow.mvp.view.model.RecycleModel;
import com.connect.meetupsfellow.retrofit.response.ResponseUserProfileImages;

import java.util.ArrayList;
import java.util.List;

public class RecycleViewUserPicAdapter extends RecyclerView.Adapter<RecycleViewUserPicAdapter.MyViewHolder> {

    Context context;
    ArrayList<RecycleModel> userPics;
    List<RecycleModel> userPicsAll;
    String userId;
    private ArrayList<ResponseUserProfileImages> images = null;

    public RecycleViewUserPicAdapter(Context context, ArrayList<RecycleModel> userPics) {
        this.context = context;
        this.userPics = userPics;
        this.userPicsAll = new ArrayList<>(userPics);
    }

    @NonNull
    @Override
    public RecycleViewUserPicAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //Inflate the layout (Giving look)
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.user_prics_rv, parent, false);
        return new RecycleViewUserPicAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecycleViewUserPicAdapter.MyViewHolder holder, int position) {

        int pos = position;

        if (userPics.get(position).getUserPics() != null) {

           // holder.userPic.setBackground(context.getResources().getDrawable(userPics.get(position).getUserPics()));
        }

        /*if (userPics.get(position).getImages() != null) {

            for (int i = 0; i < userPics.get(position).getImages().size(); i++) {


            }
        }*/

            if (pos == 10) {

                holder.userImgBlur.setVisibility(View.VISIBLE);
                holder.moreImgTxt.setVisibility(View.VISIBLE);
                holder.moreImgTxt.setText("+" + (userPics.get(pos).getTotalImg()-10));

                holder.moreImgTxt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent intent = new Intent(context, PrivateAlbumActivity.class);
                        context.startActivity(intent);

                    }
                });

                holder.userImgBlur.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent intent = new Intent(context, PrivateAlbumActivity.class);
                        context.startActivity(intent);
                    }
                });

            }
            else {

                holder.userImgBlur.setVisibility(View.GONE);
                holder.moreImgTxt.setVisibility(View.GONE);
            }

            Glide.with(context)
                    .load(userPics.get(position).getImages())
                    .placeholder(R.drawable.meetupsfellow_transpatent)
                    .dontAnimate()
                    .into(holder.userPic);

        if (userPics.get(pos).getMediaType().equalsIgnoreCase("video")){
            holder.iv_video.setVisibility(View.VISIBLE);
        }else {
            holder.iv_video.setVisibility(View.GONE);
        }

            holder.userPic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Log.d("userPvtPics", userPics.get(pos).getAllImg());

                    Intent intent = new Intent(context, ImagePagerActivity.class);
                    intent.putExtra(Constants.IntentDataKeys.ProfileImages, userPics.get(pos).getAllImg());
                    intent.putExtra(Constants.IntentDataKeys.UserId, userPics.get(pos).getUserId());
                    intent.putExtra("Class", "Private");
                    intent.putExtra("position", pos);
                    context.startActivity(intent);
                }
            });
        }

    @Override
    public int getItemCount() {
        //Number of Items you want to display
        return userPics.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        //grabbing the views from rv_column.xml

        ImageView userPic, userImgBlur, iv_video;
        TextView moreImgTxt;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            userPic = itemView.findViewById(R.id.userImages);
            userImgBlur = itemView.findViewById(R.id.userImgBlur);
            moreImgTxt = itemView.findViewById(R.id.moreImgTxt);
            iv_video = itemView.findViewById(R.id.iv_video);
        }
    }
}
