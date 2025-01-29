package com.connect.meetupsfellow.mvp.view.viewholder

import android.widget.ImageView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.connect.meetupsfellow.R
import com.connect.meetupsfellow.constants.ItemClickStatus
import com.connect.meetupsfellow.databinding.ItemBlockBinding
import com.connect.meetupsfellow.global.utils.RecyclerViewClick
import com.connect.meetupsfellow.retrofit.response.ResponseBlockedUsers

class BlockedUsersViewHolder(private val binding: ItemBlockBinding) :
    androidx.recyclerview.widget.RecyclerView.ViewHolder(binding.root) {

    init {
        binding.btnUnblock.setOnClickListener {
            if (RecyclerViewClick.onClickListener != null) {
                RecyclerViewClick.onClickListener!!.onItemClick(
                    adapterPosition,
                    ItemClickStatus.UnBlock
                )
            }
        }
    }

    internal fun bind(responseBlockedUsers: ResponseBlockedUsers) {
        binding.tvUserName.text = responseBlockedUsers.name
        showCircleImage(binding.ivUserImage, responseBlockedUsers.imageThumb)
    }

    private fun universalToast(message: String) {
        Toast.makeText(itemView.context, message, Toast.LENGTH_SHORT).show()
    }

    private fun showCircleImage(imageView: ImageView, imageUrl: String) {


        var requestOptions = RequestOptions()
        requestOptions = requestOptions.transforms(CenterCrop(), RoundedCorners(16))

        Glide.with(itemView.context)
            .load(imageUrl)
            .placeholder(R.drawable.meetupsfellow_transpatent)
            .apply(requestOptions)
            .into(imageView)


        /*DisplayImage.with(itemView.context)
            .load(imageUrl)
            .placeholder(R.drawable.meetupsfellow_transpatent)
            .shape(DisplayImage.Shape.CropSquare)
            .into(imageView)
            .build()*/
    }
}