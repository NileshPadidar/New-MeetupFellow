package com.connect.meetupsfellow.mvp.view.viewholder

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.Toast
import com.connect.meetupsfellow.R
import com.connect.meetupsfellow.application.ArchitectureApp
import com.connect.meetupsfellow.constants.Constants
import com.connect.meetupsfellow.constants.ItemClickStatus
import com.connect.meetupsfellow.databinding.ItemFeedBinding
import com.connect.meetupsfellow.developement.interfaces.SharedPreferencesUtil
import com.connect.meetupsfellow.global.utils.DisplayImage
import com.connect.meetupsfellow.global.utils.RecyclerViewClick
import com.connect.meetupsfellow.log.LogManager
import com.connect.meetupsfellow.retrofit.response.ResponseEventFeeds
import com.google.gson.Gson
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone
import javax.inject.Inject

class EventFeedsViewHolder(private val binding: ItemFeedBinding) :
    androidx.recyclerview.widget.RecyclerView.ViewHolder(binding.root) {

    private val interestedPeople = arrayListOf<RelativeLayout>()
    private val interestedPeopleImage = arrayListOf<ImageView>()

    @Inject
    lateinit var sharedPreferencesUtil: SharedPreferencesUtil

    init {

        ArchitectureApp.component!!.inject(this@EventFeedsViewHolder)

        /* if (null != itemView.ivEventImage.layoutParams) {
             itemView.ivEventImage.layoutParams.height = (DEVICE_HEIGHT / 4)
         }*/

        itemView.setOnClickListener {
            LogManager.logger.i(
                ArchitectureApp.instance!!.tag, "Event is : ${Gson().toJson(responseEventFeeds)}"
            )
            itemView.context.startActivity(Intent(Constants.Intent.EventDetails).putExtras(Bundle().apply {
                putString(Constants.IntentDataKeys.EventDetails, Gson().toJson(responseEventFeeds))
            }))
        }

        binding.ivFavorite.setOnClickListener {
            if (RecyclerViewClick.onClickListener == null) return@setOnClickListener

            responseEventFeeds.meliked = !responseEventFeeds.meliked
            binding.ivFavorite.isSelected = responseEventFeeds.meliked
            RecyclerViewClick.onClickListener!!.onItemClick(
                adapterPosition, ItemClickStatus.FeedEventLike
            )
        }

        binding.llInterestedPeople.setOnClickListener {
            itemView.context.startActivity(Intent(Constants.Intent.Interested).putExtras(Bundle().apply {
                putInt(Constants.IntentDataKeys.EventId, responseEventFeeds.id)
            }))
        }

        interestedPeople.add(binding.rlImage1)
        interestedPeople.add(binding.rlImage2)
        interestedPeople.add(binding.rlImage3)

        interestedPeopleImage.add(binding.ivInterestedPeople1)
        interestedPeopleImage.add(binding.ivInterestedPeople2)
        interestedPeopleImage.add(binding.ivInterestedPeople3)

    }


    private lateinit var responseEventFeeds: ResponseEventFeeds

    internal fun bind(responseEventFeeds: ResponseEventFeeds) {
        this.responseEventFeeds = responseEventFeeds
        removeNotAvailableUser()
        setYourProfileImage()

        binding.rlImageText.visibility = View.GONE
        binding.tvMorePeople.visibility = View.GONE
        binding.tvEventName.text = responseEventFeeds.title
        binding.tvWeekDate.text = responseEventFeeds.week_time + ","

        /* val paint = binding.tvEventName.paint
         val width = paint.measureText(binding.tvEventName.text.toString())
         val textShader: Shader = LinearGradient(
             0f, 0f, width, binding.tvEventName.textSize, intArrayOf(
                 Color.parseColor("#F4447E"),
                 Color.parseColor("#8448F4")
                 *//*Color.parseColor("#64B678"),
                Color.parseColor("#478AEA"),*//*
                //Color.parseColor("#8446CC")
            ), null, Shader.TileMode.REPEAT
        )

        binding.tvEventName.paint.shader = textShader*/

        val inputFormat = SimpleDateFormat("MM-dd-yyyy", Locale.ENGLISH)
        val outputFormat = SimpleDateFormat("dd-MMM-yy", Locale.ENGLISH)
        // Parse the input date string into a Date object
        val date = inputFormat.parse(responseEventFeeds.startDate)
        // Convert the Date object to the desired format
        val formattedDate = outputFormat.format(date)
        // Split the formatted date into components
        val parts = formattedDate.split("-")
        binding.tvEDate.text = parts[0]
        binding.tvEventMonth.text = parts[1]

        binding.tvEventLocation.text = responseEventFeeds.location
        binding.tvEventDate.text = itemView.context.getString(
            R.string.text_event_dates,
            formatDate(responseEventFeeds.startDate),
            formatDate(responseEventFeeds.endDate)
        )
        if (sharedPreferencesUtil.fetchSettings().unit != 0) {
            binding.tvEventDistance.text = String.format(
                itemView.context.getString(R.string.text_distance),
                responseEventFeeds.mile
            )
        } else {
            binding.tvEventDistance.text = String.format(
                itemView.context.getString(R.string.text_distance_km),
                responseEventFeeds.km
            )
        }

        binding.tvEventAgo.text = responseEventFeeds.created_at
        binding.ivFavorite.isSelected = responseEventFeeds.meliked

        DisplayImage.with(itemView.context).load(responseEventFeeds.image)
            .placeholder(R.drawable.image).shape(DisplayImage.Shape.DEFAULT)
            .into(binding.ivEventImage).build()


        interestedPeople.asSequence().forEach {
            it.visibility = View.GONE
        }

        responseEventFeeds.interestedUsers.asSequence().forEach {
            val index = responseEventFeeds.interestedUsers.indexOf(it)
            when (indexExists(interestedPeople, index)) {
                true -> {
                    interestedPeople[index].visibility = View.VISIBLE
                    showCircleImage(interestedPeopleImage[index], it.imagePath)
                }

                false -> {}
            }
        }

        when {
            responseEventFeeds.interestedUsers.isEmpty() -> binding.llInterestedPeople.visibility =
                View.GONE

            responseEventFeeds.interestedUsers.size == 3 -> binding.rlImageText.visibility =
                View.VISIBLE

            responseEventFeeds.interestedUsers.size > 3 -> {
                binding.rlImageText.visibility = View.VISIBLE
                binding.tvMorePeople.visibility = View.VISIBLE
                binding.tvMorePeople.text = itemView.context.getString(
                    R.string.text_more_people,
                    (responseEventFeeds.interestedUsers.size - 2).toString()
                ).replace(" ", "")
            }
        }
    }


    private fun setYourProfileImage() {
        val index = responseEventFeeds.interestedUsers.firstOrNull {
            "${it.uid}" == sharedPreferencesUtil.userId()
        }?.let { responseEventFeeds.interestedUsers.indexOf(it) } ?: -1
        if (index > -1) {
            when (indexExists(responseEventFeeds.interestedUsers, index)) {
                true -> {
                    if (responseEventFeeds.interestedUsers[index].imagePath.isEmpty()) {
                        responseEventFeeds.interestedUsers[index].imagePath =
                            sharedPreferencesUtil.fetchUserProfileImage()
                    }
                }

                false -> {}
            }
        }
    }

    private fun removeNotAvailableUser() {
        val index = responseEventFeeds.interestedUsers.firstOrNull {
            it.name == null || it.name.isEmpty()
        }?.let { responseEventFeeds.interestedUsers.indexOf(it) } ?: -1

        if (index > -1) {
            responseEventFeeds.interestedUsers.removeAt(index)
        }
    }

    private fun formatDate(date: String): String {
        LogManager.logger.i(ArchitectureApp.instance!!.tag, "Date is : $date")
        val sdf = SimpleDateFormat("MM-dd-yyyy", Locale.getDefault())
        sdf.timeZone = TimeZone.getDefault()
        return DateFormat.getDateInstance(DateFormat.DEFAULT, Locale.getDefault())
            .format(sdf.parse(date))
    }

    private fun showCircleImage(imageView: ImageView, imageUrl: String) {
        DisplayImage.with(itemView.context).load(imageUrl)
            .placeholder(R.drawable.meetupsfellow_transpatent).shape(DisplayImage.Shape.CropCircle)
            .into(imageView).build()
    }

    private fun indexExists(list: ArrayList<*>, index: Int): Boolean =
        index >= 0 && index < list.size

    private fun universalToast(message: String) {
        Toast.makeText(itemView.context, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        internal val DEVICE_HEIGHT =
            (ArchitectureApp.instance!!.resources.displayMetrics.heightPixels)
    }
}