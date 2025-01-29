package com.connect.meetupsfellow.mvp.view.viewholder

import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.connect.meetupsfellow.R
import com.connect.meetupsfellow.constants.ItemClickStatus
import com.connect.meetupsfellow.databinding.ItemPrivatePictureAccessBinding
import com.connect.meetupsfellow.global.utils.DisplayImage
import com.connect.meetupsfellow.global.utils.RecyclerViewClick
import com.connect.meetupsfellow.mvp.view.adapter.PrivateAlbumAdapter
import com.connect.meetupsfellow.retrofit.response.ResponsePrivateAccess

class PrivateAccessViewHolder(
    private val binding: ItemPrivatePictureAccessBinding,
    private val viewType: Int
) : RecyclerView.ViewHolder(binding.root) {

    init {

        binding.llLayout.setOnClickListener {
            if (RecyclerViewClick.onClickListener == null) return@setOnClickListener
            RecyclerViewClick.onClickListener!!.onItemClick(
                responsePrivateAccess.userId, ItemClickStatus.Album
            )
        }
        binding.ivUserImage.setOnClickListener {
            if (RecyclerViewClick.onClickListener == null) return@setOnClickListener
            RecyclerViewClick.onClickListener!!.onItemClick(
                responsePrivateAccess.userId, ItemClickStatus.Profile
            )
        }
        binding.ivUserAccess.setOnClickListener {
            if (RecyclerViewClick.onClickListener == null) return@setOnClickListener
            RecyclerViewClick.onClickListener!!.onItemClick(
                responsePrivateAccess.userId, ItemClickStatus.Private
            )
        }
    }

    private lateinit var responsePrivateAccess: ResponsePrivateAccess

    internal fun bind(responsePrivateAccess: ResponsePrivateAccess) {
        this.responsePrivateAccess = responsePrivateAccess
        binding.tvUserName.text = responsePrivateAccess.userName
        binding.tvUserLocation.text = responsePrivateAccess.location
        showCircleImage(binding.ivUserImage, responsePrivateAccess.imagePath)
        when (viewType) {
            PrivateAlbumAdapter.Other -> binding.ivUserAccess.visibility = View.GONE
            else -> binding.ivUserAccess.visibility = View.VISIBLE
        }
        binding.ivUserAccess.isSelected = true
    }

    private fun showCircleImage(imageView: ImageView, imageUrl: String) {
        DisplayImage.with(itemView.context).load(imageUrl)
            .placeholder(R.drawable.meetupsfellow_transpatent).shape(DisplayImage.Shape.CropCircle)
            .into(imageView).build()
    }
}