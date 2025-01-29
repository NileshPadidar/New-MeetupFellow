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
import com.connect.meetupsfellow.global.interfaces.RecyclerViewClickListener
import com.connect.meetupsfellow.retrofit.response.ResponseFavoriteUser
import javax.inject.Inject

class FavoritePeopleViewHolder(
    private val binding: ItemInterestedPeopleBinding,
    private val clickListener: RecyclerViewClickListener
) : androidx.recyclerview.widget.RecyclerView.ViewHolder(binding.root) {

    @Inject
    lateinit var sharedPreferencesUtil: SharedPreferencesUtil

    init {
        ArchitectureApp.component!!.inject(this@FavoritePeopleViewHolder)
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
        binding.ivFavorite.visibility = View.VISIBLE
        binding.ivFavorite.setOnClickListener {
            clickListener.onClick(itemView, adapterPosition)
        }
    }

    private var id = -1

    internal fun bind(responseInterestedPeople: ResponseFavoriteUser) {
        this.id = responseInterestedPeople.userId!!
        binding.ivFavorite.isSelected = true
        binding.tvInterestedUserName.text = responseInterestedPeople.name
        binding.tvInterestedUserDistance.visibility = View.GONE
        binding.tvInterestedUserLocation.text = responseInterestedPeople.aboutMe

//        Log.e("n++", responseInterestedPeople.km +"    "+ responseInterestedPeople.mile)

        /* if(sharedPreferencesUtil.fetchSettings().unit != 0) {
             binding.tvInterestedUserDistance.text = itemView.context.getString(R.string.text_distance, responseInterestedPeople.mile)
         }else {
             binding.tvInterestedUserDistance.text = itemView.context.getString(R.string.text_distance_km, responseInterestedPeople.km)
         }*/

        showCircleImage(
            binding.ivInterestedUserImage, responseInterestedPeople.imagePath.toString()
        )
    }

    private fun showCircleImage(imageView: ImageView, imageUrl: String) {

        var requestOptions = RequestOptions()
        requestOptions = requestOptions.transforms(CenterCrop(), RoundedCorners(16))

        Glide.with(itemView.context).load(imageUrl)
            .placeholder(R.drawable.meetupsfellow_transpatent).apply(requestOptions).into(imageView)


        /*DisplayImage.with(itemView.context)
            .load(imageUrl)
            .placeholder(R.drawable.meetupsfellow_transpatent)
            .shape(DisplayImage.Shape.CropSquare)
            .into(imageView)
            .build()*/
    }

    private fun universalToast(message: String) {
        Toast.makeText(itemView.context, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        internal val DEVICE_WIDTH =
            (ArchitectureApp.instance!!.resources.displayMetrics.widthPixels)
    }
}