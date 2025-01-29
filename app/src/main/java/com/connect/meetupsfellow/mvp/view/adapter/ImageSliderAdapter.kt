package com.connect.meetupsfellow.mvp.view.adapter

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.connect.meetupsfellow.constants.Constants
import com.connect.meetupsfellow.databinding.ItemUserProfileBinding
import com.connect.meetupsfellow.mvp.view.activities.ImagePagerActivity
import com.connect.meetupsfellow.retrofit.response.ResponseUserProfileImages
import com.google.gson.Gson


class ImageSliderAdapter(private val context: Context, private val imageList: List<String>,
                         val userId: String, val images: ArrayList<ResponseUserProfileImages>
) :
    RecyclerView.Adapter<ImageSliderAdapter.ImageViewHolder>() {

    inner class ImageViewHolder(private val binding: ItemUserProfileBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(imageUrl: String) {
            Glide.with(binding.ivUserPic.context)
                .load(imageUrl)
                .into(binding.ivUserPic)

            binding!!.ivUserPic.setOnClickListener {
                val intent = Intent(context, ImagePagerActivity::class.java)
                intent.putExtras(Bundle().apply {
                    putString(Constants.IntentDataKeys.ProfileImages, Gson().toJson(images))
                    putString(Constants.IntentDataKeys.UserId, userId)
                    putString("Class", "Profile")
                })
                context.startActivity(intent)
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val binding = ItemUserProfileBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ImageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        holder.bind(imageList[position])
    }

    override fun getItemCount(): Int = imageList.size
}
