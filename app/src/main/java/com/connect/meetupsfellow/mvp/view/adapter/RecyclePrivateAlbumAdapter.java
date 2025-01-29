package com.connect.meetupsfellow.mvp.view.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
//import com.fivehundredpx.greedolayout.GreedoLayoutSizeCalculator;
import com.connect.meetupsfellow.R;
import com.connect.meetupsfellow.constants.Constants;
import com.connect.meetupsfellow.mvp.view.activities.ImagePagerActivity;
import com.connect.meetupsfellow.mvp.view.activities.PrivateAlbumActivity;
import com.connect.meetupsfellow.mvp.view.activities.VideoPlayerActivity;
import com.connect.meetupsfellow.mvp.view.model.RecycleModel;

import java.util.ArrayList;
import java.util.List;

public class RecyclePrivateAlbumAdapter extends RecyclerView.Adapter<RecyclePrivateAlbumAdapter.MyViewHolder>{

    public ArrayList<Boolean> clickedItems = new ArrayList<>();
    Context context;
    ArrayList<RecycleModel> userPics;
    public ArrayList<String> deletePosition;
    List<RecycleModel> userPicsAll;
    String userId;
    //private ArrayList<ResponsePrivatePics> images = new ArrayList<>();
    //double[] mImageAspectRatios;
    /*private int height[] = {200,400,300,
            200,-2,-2,-2};
    private int width[] = {800,200,300,
            300,400,-1,-1};*/
    //private ArrayList<ResponseUserProfileImages> images = null;

    public RecyclePrivateAlbumAdapter(Context context, ArrayList<RecycleModel> userPics) {
        this.context = context;
        this.userPics = userPics;
        this.userPicsAll = new ArrayList<>(userPics);
        deletePosition = new ArrayList<>();
        //mImageAspectRatios = new double[userPics.size()];
        //calculateImageAspectRatios();
    }

    @NonNull
    @Override
    public RecyclePrivateAlbumAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //Inflate the layout (Giving look)
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_private_album, parent, false);
        // set the view's size, margins, paddings and layout parameters
        //return new MyViewHolder(view);
        return new RecyclePrivateAlbumAdapter.MyViewHolder(view);
        /*ImageView imageView = new ImageView(context);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

        imageView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));*/
        //return new MyViewHolder(imageView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclePrivateAlbumAdapter.MyViewHolder holder, int position) {

        int pos = position;

        Log.d("imgPath", userPics.get(pos).getImages());
          PrivateAlbumActivity activity = (PrivateAlbumActivity) context;

        holder.ivSelected.setVisibility(View.GONE);
        holder.ivSelect.setVisibility(View.GONE);

        Glide.with(context)
                .load(userPics.get(pos).getImages())
                .placeholder(R.drawable.meetupsfellow_transpatent)
                .dontAnimate()
                .into(holder.ivPrivateImage);

        if (userPics.get(pos).getMediaType().equalsIgnoreCase("video")){
            holder.video.setVisibility(View.VISIBLE);
        }else {
            holder.video.setVisibility(View.GONE);
        }
/*
        int screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
        // Calculate item dimensions
        int itemWidth = screenWidth / 2; // Adjust based on number of columns
        int itemHeight = (int) (itemWidth * 1.2); // Example: height 1.5x width
        // Set dynamic width and height
        ViewGroup.LayoutParams layoutParams = holder.itemView.getLayoutParams();
        layoutParams.width = itemWidth;
        layoutParams.height = itemHeight;
        holder.itemView.setLayoutParams(layoutParams);

        GradientDrawable background = new GradientDrawable();
        background.setShape(GradientDrawable.RECTANGLE);
      //  background.setColor(Color.WHITE); // Set the background color
        background.setCornerRadius(20f); // Set the corner radius
        background.setStroke(1, Color.BLACK); // Optional: Add a border with color and width

        holder.itemView.setBackground(background);*/


        holder.ivPrivateImage.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                //holder.ivSelect.setVisibility(View.VISIBLE);
                //holder.ivPrivateImage.setVisibility(View.GONE);
                holder.ivSelected.setVisibility(View.VISIBLE);
                holder.ivSelect.setVisibility(View.VISIBLE);

                if (!deletePosition.contains(String.valueOf(pos))) {

                    deletePosition.add(String.valueOf(pos));
                    Log.d("postion", String.valueOf(pos));
                }

                activity.getDeletePvtImage().setVisibility(View.VISIBLE);

                //Toast.makeText(context, "pressed"+pos, Toast.LENGTH_LONG).show();

                //notifyDataSetChanged();

                return false;
            }
        });



        holder.ivPrivateImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("Private_Album", "RecyclePrivateAlbumAdapter: CLICK");
                if (holder.ivSelected.getVisibility() == View.GONE) {
                    Log.e("Private_Album", "RecyclePrivateAlbumAdapter: Switch_Activity");
                    Intent intent = new Intent(context, ImagePagerActivity.class);
                    intent.putExtra(Constants.IntentDataKeys.ProfileImages, userPics.get(pos).getAllImg());
                    intent.putExtra(Constants.IntentDataKeys.UserId, userPics.get(pos).getUserId());
                    intent.putExtra("Class", "Private");
                    intent.putExtra("position", pos);
                    context.startActivity(intent);
                }
            }
        });

        holder.ivSelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //holder.ivPrivateImage.setVisibility(View.VISIBLE);
                holder.ivSelected.setVisibility(View.GONE);
                holder.ivSelect.setVisibility(View.GONE);

                deletePosition.remove(String.valueOf(pos));
                if (deletePosition.isEmpty()){

                    activity.getDeletePvtImage().setVisibility(View.GONE);
                }
            }
        });

       // holder.ivPrivateImage.requestLayout();


    }

    @Override
    public int getItemCount() {
        //Number of Items you want to display
        return userPics.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        //grabbing the views from rv_column.xml

        ImageView ivPrivateImage, ivSelected;
        RelativeLayout pvtImgLay;
        ImageView ivSelect;
        ImageView video;
        //private ImageView mImageView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
           // mImageView = imageView;
            ivPrivateImage = itemView.findViewById(R.id.ivPrivateImage);
            pvtImgLay = itemView.findViewById(R.id.pvtImgLay);
            ivSelect = itemView.findViewById(R.id.ivSelect);
            ivSelected = itemView.findViewById(R.id.ivSelected);
            video = itemView.findViewById(R.id.video_thumbnail);
        }
    }

    /*private void calculateImageAspectRatios() {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        for (int i = 0; i < userPics.size(); i++) {
            //BitmapFactory.decodeResource(context.getResources(), userPics.get(i).getImages(), options);



        }

        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        double ratio = ((double)metrics.heightPixels / (double)metrics.widthPixels);

        Log.d("ratio", String.valueOf(ratio/3));

        mImageAspectRatios = new double[]{ratio/3, ratio/3, ratio/3, 1.1, 1.1, 1.1, 1.1};



    }


    private int getLoopedIndex(int index) {
        return index % userPics.size(); // wrap around
    }*/
}
