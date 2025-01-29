package com.connect.meetupsfellow.mvp.view.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.connect.meetupsfellow.R;
import com.connect.meetupsfellow.constants.Constants;
import com.connect.meetupsfellow.mvp.view.activities.ImagePagerActivity;
import com.connect.meetupsfellow.mvp.view.model.SliderData;
//import com.smarteist.autoimageslider.SliderViewAdapter;

import java.util.ArrayList;

/*public class SliderAdapter extends SliderViewAdapter<SliderAdapter.SliderAdapterViewHolder> {


    // list for storing urls of images.
    private final ArrayList<SliderData> mSliderItems = new ArrayList<>();
    private final Context context;
    private String allImgs, userId;

    // Constructor
    public SliderAdapter(Context context, String allImgs, String userId) {
        //this.mSliderItems = sliderDataArrayList;
        this.context = context;
        this.allImgs = allImgs;
        this.userId = userId;
    }

    // We are inflating the slider_layout
    // inside on Create View Holder method.
    @Override
    public SliderAdapterViewHolder onCreateViewHolder(ViewGroup parent) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.slider_item, null);
        return new SliderAdapterViewHolder(inflate);
    }

    public void addItem(SliderData sliderItem) {
        this.mSliderItems.add(sliderItem);
        notifyDataSetChanged();
    }

    // Inside on bind view holder we will
    // set data to item of Slider View.
    @Override
    public void onBindViewHolder(SliderAdapterViewHolder viewHolder, final int position) {

        final SliderData sliderItem = mSliderItems.get(position);
        int pos = position;

        // Glide is use to load image
        // from url in your imageview.

        Log.d("userImg", String.valueOf(sliderItem.getImgUrl()));
        Log.d("userImg", String.valueOf(mSliderItems.size()));

        if (sliderItem.getImgUrl().equals("https://s3.ap-south-1.amazonaws.com/reegurapps/UserProfileImg/favicon.png") || sliderItem.getImgUrl().equals("https://s3.ap-south-1.amazonaws.com/reegurapps/UserProfileImg/1644789090.png")
        || sliderItem.getImgUrl().isEmpty() || sliderItem.getImgUrl().equals("https://s3.ap-south-1.amazonaws.com/reegurapps/UserProfileImg/1666491165.png")){

            viewHolder.imageViewBackground.setImageResource(R.drawable.meetupsfellow_transpatent);

        }
        else {

            Glide.with(context)
                    .load(sliderItem.getImgUrl())
                    .centerCrop()
                    .into(viewHolder.imageViewBackground);
        }

        viewHolder.imageViewBackground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(context, ImagePagerActivity.class);
                intent.putExtra(Constants.IntentDataKeys.ProfileImages, allImgs);
                intent.putExtra(Constants.IntentDataKeys.UserId, userId);
                intent.putExtra("Class", "Profile");
                intent.putExtra("position", pos);
                context.startActivity(intent);

            }
        });

    }

    // this method will return
    // the count of our list.
    @Override
    public int getCount() {
        return mSliderItems.size();
    }

    static class SliderAdapterViewHolder extends ViewHolder {
        // Adapter class for initializing
        // the views of our slider view.
        View itemView;
        ImageView imageViewBackground;

        public SliderAdapterViewHolder(View itemView) {
            super(itemView);
            imageViewBackground = itemView.findViewById(R.id.myimage);
            this.itemView = itemView;
        }
    }
}*/
