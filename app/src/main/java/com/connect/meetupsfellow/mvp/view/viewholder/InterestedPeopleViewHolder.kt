package com.connect.meetupsfellow.mvp.view.viewholder

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.connect.meetupsfellow.R
import com.connect.meetupsfellow.application.ArchitectureApp
import com.connect.meetupsfellow.constants.Constants
import com.connect.meetupsfellow.databinding.ItemInterestedPeopleBinding
import com.connect.meetupsfellow.developement.interfaces.SharedPreferencesUtil
import com.connect.meetupsfellow.retrofit.response.ResponseInterestedPeople
import javax.inject.Inject

class InterestedPeopleViewHolder(private val binding: ItemInterestedPeopleBinding) :
    androidx.recyclerview.widget.RecyclerView.ViewHolder(binding.root) {

    @Inject
    lateinit var sharedPreferencesUtil: SharedPreferencesUtil

    init {

        ArchitectureApp.component!!.inject(this@InterestedPeopleViewHolder)

        itemView.setOnClickListener {
            when ("$id") {
                sharedPreferencesUtil.userId() -> {
                    itemView.context.startActivity(Intent(Constants.Intent.Profile))
                }

                else -> {
                    itemView.context.startActivity(
                        Intent(Constants.Intent.ProfileUser).putExtras(Bundle().apply {
                            putInt(Constants.IntentDataKeys.UserId, id)
                        })
                    )
                }
            }
        }
    }

    private var id = -1
    private var userPic = ""

    internal fun bind(responseInterestedPeople: ResponseInterestedPeople) {
        this.id = responseInterestedPeople.id
        this.userPic = responseInterestedPeople.userPic
        setYourProfileImage()
        binding.tvInterestedUserName.text = responseInterestedPeople.name
        binding.tvInterestedUserLocation.text = responseInterestedPeople.homeLocation

        if (sharedPreferencesUtil.fetchSettings().unit != 0) {
            binding.tvInterestedUserDistance.text = String.format(
                itemView.context.getString(R.string.text_distance),
                responseInterestedPeople.mile
            )
        } else {
            binding.tvInterestedUserDistance.text = String.format(
                itemView.context.getString(R.string.text_distance_km),
                responseInterestedPeople.km
            )
        }

        when ("${responseInterestedPeople.id}") {
            sharedPreferencesUtil.userId() -> {
                binding.tvInterestedUserDistance.visibility = View.GONE
            }

            else -> {
                binding.tvInterestedUserDistance.visibility = View.VISIBLE
            }
        }

        showCircleImage(binding.ivInterestedUserImage, userPic)
    }

    /* private fun showCircleImage(imageView: ImageView, imageUrl: String) {

         DisplayImage.with(binding.context)
             .load(imageUrl)
             .placeholder(R.drawable.meetupsfellow_transpatent)
             .shape(DisplayImage.Shape.CropCircle)
             .into(imageView)
             .build()
     }*/

    private fun showCircleImage(imageView: ImageView, imageUrl: String) {

        var requestOptions = RequestOptions()
        requestOptions = requestOptions.transforms(CenterCrop(), RoundedCorners(12))

        Glide.with(itemView.context).load(imageUrl)
            .placeholder(R.drawable.meetupsfellow_transpatent).apply(requestOptions).into(imageView)
    }

    private fun setYourProfileImage() {
        when ("$id") {
            sharedPreferencesUtil.userId() -> {
                when (userPic.isEmpty()) {
                    true -> {
                        userPic = sharedPreferencesUtil.fetchUserProfileImage()
                    }

                    false -> {}
                }
            }
        }
    }

    private fun universalToast(message: String) {
        Toast.makeText(itemView.context, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        internal val DEVICE_WIDTH =
            (ArchitectureApp.instance!!.resources.displayMetrics.widthPixels)
    }
}