package com.connect.meetupsfellow.mvp.view.viewholder

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import com.connect.meetupsfellow.R
import com.connect.meetupsfellow.application.ArchitectureApp
import com.connect.meetupsfellow.constants.Constants
import com.connect.meetupsfellow.databinding.ItemSearchBinding
import com.connect.meetupsfellow.developement.interfaces.SharedPreferencesUtil
import com.connect.meetupsfellow.global.utils.DisplayImage
import com.connect.meetupsfellow.retrofit.response.ResponseSearch
import com.connect.meetupsfellow.retrofit.response.ResponseUserData
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

class SearchItemViewHolder(private val binding: ItemSearchBinding) :
    androidx.recyclerview.widget.RecyclerView.ViewHolder(binding.root) {

    @Inject
    lateinit var sharedPreferencesUtil: SharedPreferencesUtil

    init {
        ArchitectureApp.component!!.inject(this@SearchItemViewHolder)

        if (itemView.layoutParams != null) {
            itemView.layoutParams.width = (DEVICE_WIDTH / 4)
            itemView.layoutParams.height = (itemView.layoutParams.width * 1).toInt()
        }


        /* itemView.setOnClickListener {
             when (responseSearch.type) {

                 Constants.View.User -> {
                     when ("${responseSearch.search_id}") {
                         sharedPreferencesUtil.userId() -> {
                             itemView.context.startActivity(Intent(Constants.Intent.Profile))
                         }

                         else -> {
                             itemView.context.startActivity(
                                 Intent(Constants.Intent.ProfileUser).putExtras(
                                     Bundle().apply {
                                         putInt(
                                             Constants.IntentDataKeys.UserId,
                                             responseSearch.search_id
                                         )
                                     })
                             )
                         }
                     }
                 }

                 Constants.View.Event -> {
                     itemView.context.startActivity(
                         Intent(Constants.Intent.EventDetails).putExtras(
                             Bundle().apply {
                                 putInt(Constants.IntentDataKeys.EventId, responseSearch.search_id)
                             })
                     )
                 }

                 Constants.View.Conversation -> {
                     itemView.context.startActivity(Intent(Constants.Intent.Chat).putExtras(Bundle().apply {
                         putString(Constants.IntentDataKeys.Conversation, Gson().toJson(responseSearch.conversation))
                         putString(Constants.IntentDataKeys.UserImage, responseSearch.userImage)
                         putInt(Constants.IntentDataKeys.Status, responseSearch.userId.toInt())
                     }))
                 }
             }
         }*/

        itemView.setOnClickListener {
            when ("${responseSearchUser.userId}") {
                sharedPreferencesUtil.userId() -> {
                    itemView.context.startActivity(Intent(Constants.Intent.Profile))
                }

                else -> {
                    itemView.context.startActivity(
                        Intent(Constants.Intent.ProfileUser).putExtras(Bundle().apply {
                            putInt(
                                Constants.IntentDataKeys.UserId, responseSearchUser.id.toInt()
                            )
                        })
                    )
                }
            }
        }
    }

    private lateinit var responseSearch: ResponseSearch

    internal fun bind(responseSearch: ResponseSearch) {

        this.responseSearch = responseSearch

        binding.tvSearchItemDate.visibility = View.GONE
//        binding.ivUserStatus.visibility = View.GONE

        Log.d("status", responseSearch.status)

        when (responseSearch.type) {
            Constants.View.User -> showUser()
            Constants.View.Event -> showEvent()
            Constants.View.Conversation -> showConversation()
        }
    }

    private lateinit var responseSearchUser: ResponseUserData
    internal fun bindUserSearch(responseSearch: ResponseUserData) {

        this.responseSearchUser = responseSearch

        binding.tvSearchItemDate.visibility = View.GONE
//        binding.ivUserStatus.visibility = View.GONE

        Log.d("status", responseSearchUser.status.toString())

        showUser()

    }

    private fun showConversation() {
        binding.tvSearchItemLocation.visibility = View.GONE
        binding.tvSearchItemDate.visibility = View.VISIBLE
//        binding.ivUserStatus.visibility = View.VISIBLE
//        binding.ivUserStatus.isSelected = responseSearch.profileStatus.toBoolean()
        binding.tvSearchItemName.text = responseSearch.name

        Log.e("aa+1", responseSearch.toString())
//        Log.e("aa+2", responseSearch.km)

        if (sharedPreferencesUtil.fetchSettings().unit != 0) {
            if (responseSearch.mile.isNotEmpty()) binding.tvSearchItemDistance.text =
                setFormattedDate(responseSearch.mile.toLong())
        } else {
            if (responseSearch.km.isNotEmpty()) binding.tvSearchItemDistance.text =
                setFormattedDate(responseSearch.km.toLong())
        }

        binding.tvSearchItemDate.text = responseSearch.startDate
        showCircleImage(binding.ivSearchItemImage, responseSearch.userImage)
    }

    private fun showUser() {
        binding.tvSearchItemName.text = responseSearchUser.name

        if (sharedPreferencesUtil.fetchSettings().unit != 0) {
            binding.tvSearchItemDistance.text = String.format(
                itemView.context.getString(R.string.text_distance),
                responseSearchUser.mile
            )
        } else {
            binding.tvSearchItemDistance.text = String.format(
                itemView.context.getString(R.string.text_distance_km),
                responseSearchUser.km
            )
        }

        binding.tvSearchItemLocation.text = responseSearchUser.homeLong

        showCircleImage(binding.ivSearchItemImage, responseSearchUser.userPic)
    }

    private fun showEvent() {
        binding.tvSearchItemDate.visibility = View.VISIBLE

        binding.tvSearchItemName.text = responseSearch.title

        if (sharedPreferencesUtil.fetchSettings().unit != 0) {
            binding.tvSearchItemDistance.text = String.format(
                itemView.context.getString(R.string.text_distance),
                responseSearch.mile
            )
        } else {
            binding.tvSearchItemDistance.text = String.format(
                itemView.context.getString(R.string.text_distance_km),
                responseSearch.km
            )
        }

        binding.tvSearchItemLocation.text = responseSearch.eventLocation

        binding.tvSearchItemDate.text = String.format(
            itemView.context.getString(
                R.string.text_event_dates
            ),
            formatDateFavorite(responseSearch.startDate),
            formatDateFavorite(responseSearch.endDate)
        )

        showCircleImage(binding.ivSearchItemImage, responseSearch.eventImage)
    }

    private fun showCircleImage(imageView: ImageView, imageUrl: String) {

        DisplayImage.with(itemView.context).load(imageUrl)
            .placeholder(R.drawable.meetupsfellow_transpatent).shape(DisplayImage.Shape.CropSquare)
            .into(imageView).build()
    }

    private fun universalToast(message: String) {
        Toast.makeText(itemView.context, message, Toast.LENGTH_SHORT).show()
    }

    private fun formatDate(date: String): String {
        return DateFormat.getDateInstance(DateFormat.DEFAULT, Locale("en", "US")).format(
                SimpleDateFormat("yyyy-MM-dd", Locale("en", "US")).parse(date)
            )
    }

    private fun formatDateFavorite(date: String): String {
        return DateFormat.getDateInstance(DateFormat.DEFAULT, Locale("en", "US")).format(
                SimpleDateFormat("MM-dd-yyyy", Locale("en", "US")).parse(date)
            )
    }

    private fun setFormattedDate(smsTimeInMilis: Long): String {

        val smsTime = Calendar.getInstance()
        smsTime.timeInMillis = smsTimeInMilis

        val now = Calendar.getInstance()

        val timeFormatString = "h:mm aa"
        //            final String dateTimeFormatString = "EEEE, MMMM d, h:mm aa";
        //            final long HOURS = 60 * 60 * 60;

        return when {
            now.get(Calendar.DATE) == smsTime.get(Calendar.DATE) -> "Today " + android.text.format.DateFormat.format(
                timeFormatString, smsTime
            )

            now.get(Calendar.DATE) - smsTime.get(Calendar.DATE) == 1 -> "Yesterday " + android.text.format.DateFormat.format(
                timeFormatString, smsTime
            )

            now.get(Calendar.YEAR) == smsTime.get(Calendar.YEAR) -> /*return DateFormat.format(dateTimeFormatString, smsTime).toString();*/
                android.text.format.DateFormat.format("dd MMMM yyyy", smsTime).toString()

            else -> /*return DateFormat.format("MMMM dd yyyy, h:mm aa", smsTime).toString();*/
                android.text.format.DateFormat.format("dd MMMM yyyy", smsTime).toString()
        }
    }

    companion object {
        internal val DEVICE_WIDTH =
            (ArchitectureApp.instance!!.resources.displayMetrics.widthPixels)
    }
}