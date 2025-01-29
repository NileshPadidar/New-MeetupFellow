package com.connect.meetupsfellow.mvp.view.viewholder

import android.util.Log
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.connect.meetupsfellow.R
import com.connect.meetupsfellow.application.ArchitectureApp
import com.connect.meetupsfellow.constants.Constants
import com.connect.meetupsfellow.constants.ItemClickStatus
import com.connect.meetupsfellow.databinding.ItemPrivateAlbumBinding
import com.connect.meetupsfellow.developement.interfaces.SharedPreferencesUtil
import com.connect.meetupsfellow.global.utils.DisplayImage
import com.connect.meetupsfellow.global.utils.RecyclerViewClick
import com.connect.meetupsfellow.mvp.view.activities.PrivateAlbumActivity
import com.connect.meetupsfellow.retrofit.response.ResponsePrivatePics
import javax.inject.Inject

class PrivateAlbumViewHolder(
    private val binding: ItemPrivateAlbumBinding,
    private val userId: String,
    private val type: String
) : RecyclerView.ViewHolder(binding.root) {

    @Inject
    lateinit var sharedPreferencesUtil: SharedPreferencesUtil

    init {
        ArchitectureApp.component!!.inject(this@PrivateAlbumViewHolder)
    }

    init {
        if (itemView.layoutParams != null) {
            itemView.layoutParams.width = (DEVICE_WIDTH / 4.2).toInt()
            itemView.layoutParams.height = (DEVICE_WIDTH / 4.2).toInt()
        }

        binding.ivPrivateImage.setOnClickListener {
            if (userId.isEmpty() && type.isEmpty()) {
                if (RecyclerViewClick.onClickListener == null) return@setOnClickListener
                RecyclerViewClick.onClickListener!!.onItemClick(
                    adapterPosition, ItemClickStatus.Add
                )
            } else {
                if (type == "${Constants.ImagePicker.View}") {
                    if (RecyclerViewClick.onClickListener == null) return@setOnClickListener
                    if (adapterPosition > 1 && !sharedPreferencesUtil.fetchUserProfile().isProMembership) {
                        RecyclerViewClick.onClickListener!!.onItemClick(
                            adapterPosition, ItemClickStatus.Purchase
                        )
                    } else {
                        RecyclerViewClick.onClickListener!!.onItemClick(
                            adapterPosition, ItemClickStatus.View
                        )
                    }
                } else {
                    album.selected = !album.selected
                    if (RecyclerViewClick.onClickListener == null) return@setOnClickListener
                    RecyclerViewClick.onClickListener!!.onItemClick(
                        adapterPosition, ItemClickStatus.Select
                    )
                }

            }
        }

        if (userId.isEmpty() && type.isEmpty()) {
            binding.ivCross.setOnClickListener {
                if (RecyclerViewClick.onClickListener == null) return@setOnClickListener
                RecyclerViewClick.onClickListener!!.onItemClick(
                    adapterPosition, ItemClickStatus.Delete
                )
            }
        }
    }

    private lateinit var album: ResponsePrivatePics

    internal fun bind(album: ResponsePrivatePics) {
        this.album = album
        when (album.type) {
            "add" -> {
                binding.ivCross.visibility = View.GONE
                binding.videoThumbnail.visibility = View.GONE
                showImage("")
            }

            else -> {
                binding.ivCross.visibility = View.VISIBLE
                //binding.ivCross.visibility = View.GONE
                binding.videoThumbnail.visibility = View.GONE
                if (userId.isEmpty() && type.isEmpty()) {
                    binding.ivCross.setImageResource(R.drawable.ic_cross)
                } else {
                    binding.ivCross.setImageResource(R.drawable.selector_image)
                    binding.ivCross.isSelected = album.selected
                    if (itemView.context is PrivateAlbumActivity && !sharedPreferencesUtil.fetchUserProfile().isProMembership && adapterPosition > 1) {
                        binding.ivPrivateImage.setBackgroundColor(
                            itemView.context.resources.getColor(R.color.white)
                        )
                        binding.ivPrivateImage.setImageResource(R.drawable.lock_image_white)
                        return
                    }
                }
                if (album.path.endsWith(".jpg") || album.path.endsWith(".jpeg") || album.path.endsWith(
                        ".png"
                    )
                ) {
                    binding.videoThumbnail.visibility = View.GONE
                    showImage(album.path)
                } else {
                    //showImage(album.video_thumbnail)
                    binding.videoThumbnail.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun showImage(imageUrl: String) {

        if (imageUrl.isEmpty() || imageUrl == " ") {

            Log.d("eptyImg", imageUrl)

            //itemView.pvtImgLay.visibility = View.GONE
        }

        DisplayImage.with(itemView.context).load(imageUrl)
            .placeholder(if (imageUrl.isEmpty()) R.drawable.ic_image_upload else R.drawable.meetupsfellow_transpatent)
            .placeholder(R.drawable.meetupsfellow_transpatent)
            .shape(DisplayImage.Shape.RoundedCorners).into(binding.ivPrivateImage).build()
    }

    companion object {
        internal val DEVICE_WIDTH =
            (ArchitectureApp.instance!!.resources.displayMetrics.widthPixels)
    }
}
