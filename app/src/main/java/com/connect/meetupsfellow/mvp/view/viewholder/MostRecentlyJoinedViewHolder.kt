package com.connect.meetupsfellow.mvp.view.viewholder

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.connect.meetupsfellow.R
import com.connect.meetupsfellow.application.ArchitectureApp
import com.connect.meetupsfellow.constants.Constants
import com.connect.meetupsfellow.databinding.ItemNearbyBinding
import com.connect.meetupsfellow.developement.interfaces.SharedPreferencesUtil
import com.connect.meetupsfellow.global.utils.DisplayImage
import com.connect.meetupsfellow.log.LogManager
import com.connect.meetupsfellow.retrofit.response.ResponseUserData
import java.text.SimpleDateFormat
import java.util.Date
import java.util.TimeZone
import javax.inject.Inject


class MostRecentlyJoinedViewHolder(private val binding: ItemNearbyBinding) :
    androidx.recyclerview.widget.RecyclerView.ViewHolder(binding.root) {

    @Inject
    lateinit var sharedPreferencesUtil: SharedPreferencesUtil

    init {

        ArchitectureApp.component!!.inject(this@MostRecentlyJoinedViewHolder)

        if (itemView.layoutParams != null) {
            itemView.layoutParams.width = (DEVICE_WIDTH / 4)
            itemView.layoutParams.height = (itemView.layoutParams.width * 1).toInt()
        }

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

    internal fun bind(responseUserData: ResponseUserData) {

        if (responseUserData.item == 2) {
            return
        }

        this.id = responseUserData.id.toInt()

        binding.tvUserName.text = responseUserData.name

        LogManager.logger.i(
            ArchitectureApp.instance!!.tag,
            "NearBy Users : ${responseUserData.name}, Online Status : ${responseUserData.onlineStatus}"
        )

        /*   if(responseUserData.onlineStatus == 1){
               itemView.ivUserStatus.isSelected = true
           }else{
               if(responseUserData.lastLoginTimeStamp != ""){
                   val format = SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
                   var lastDate = format.parse(responseUserData.lastLoginTimeStamp)
                   var currentUTCTime = format.parse(getUTCTime())
                   val diff: Long = currentUTCTime.getTime() - lastDate.getTime()
                   val seconds = diff / 1000
                   val minutes = seconds / 60
                   val hours = minutes / 60
                   if (hours <= 48){
                       itemView.ivUserStatus.isSelected = false
                       itemView.ivUserStatus.setImageResource(R.drawable.drawable_user_recent_online)
                   }else{
                       itemView.ivUserStatus.isSelected = false
                   }
               }else{
                   binding.ivUserStatus.isSelected = false
               }
           }*/



        if (responseUserData.onlineStatus == 0) {
            binding.ivUserStatus.isSelected = false
            //Gray Dot
        } else {
            if (responseUserData.lastLoginTimeStamp != "") {
                try {

                    val format = SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
                    var lastDate = format.parse(responseUserData.lastLoginTimeStamp)
                    var currentUTCTime = format.parse(getUTCTime())
                    val diff: Long = currentUTCTime.time - lastDate.time
                    val seconds = diff / 1000
                    val minutes = seconds / 60
                    val hours = minutes / 60

                    if (hours <= 2) {
                        binding.ivUserStatus.isSelected = true
                        binding.ivUserStatus.setImageResource(R.drawable.drawable_user_online)
                        //Green Dot
                    } else if (hours > 2 && hours < 48) {
                        binding.ivUserStatus.isSelected = false
                        binding.ivUserStatus.setImageResource(R.drawable.drawable_user_recent_online)
                        // Orange DOt
                    } else {
                        binding.ivUserStatus.isSelected = false
                        binding.ivUserStatus.setImageResource(R.drawable.drawable_user_offline)
                        //Gray Dot
                    }
                } catch (e: Exception) {

                }
            } else {
                if (responseUserData.onlineStatus == 1) {
                    binding.ivUserStatus.isSelected = true
                    binding.ivUserStatus.setImageResource(R.drawable.drawable_user_online)
                    //Green Dot
                } else {
                    binding.ivUserStatus.isSelected = false
                    binding.ivUserStatus.setImageResource(R.drawable.drawable_user_offline)
                    //Gray Dot
                }
            }
        }


//        binding.ivUserStatus.isSelected = responseUserData.onlineStatus != 0

        when (responseUserData.id) {
            sharedPreferencesUtil.userId() -> {
                when (responseUserData.userPic.isEmpty()) {
                    true -> showImage(sharedPreferencesUtil.fetchUserProfileImage())
                    false -> showImage(responseUserData.userThumb)
                }
                binding.ivUserStatus.isSelected = true
            }

            else -> {
                showImage(responseUserData.userThumb)
//                when (val status = sharedPreferencesUtil.fetchOtherUserProfile(responseUserData.id).onlineStatus) {
//                    true -> itemView.ivUserStatus.isSelected = status
//                    else -> itemView.ivUserStatus.isSelected = responseUserData.onlineStatus != 0
//                }
            }
        }

        binding.ivUserStatus.visibility = View.VISIBLE

    }

    fun getUTCTime(): String? {
        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSS")
        formatter.timeZone = TimeZone.getTimeZone("UTC")
        return formatter.format(Date())
    }


    private fun showImage(imageUrl: String) {

        if (imageUrl == "https://s3.ap-south-1.amazonaws.com/reegurapps/UserProfileImg/thumb/favicon.png" || imageUrl == "https://s3.ap-south-1.amazonaws.com/reegurapps/UserProfileImg/thumb/1644789090.png" || imageUrl == "https://s3.ap-south-1.amazonaws.com/reegurapps/UserProfileImg/thumb/1666491165.png") {

            binding.ivUserImage.setImageResource(R.drawable.meetupsfellow_transpatent)

        } else {

            DisplayImage.with(itemView.context).load(imageUrl)
                .placeholder(R.drawable.meetupsfellow_transpatent)
                .shape(DisplayImage.Shape.SHAPE_NEARBY).into(binding.ivUserImage).build()
        }
    }

    private fun indexExists(list: ArrayList<*>, index: Int): Boolean =
        index >= 0 && index < list.size

    private fun universalToast(message: String) {
        Toast.makeText(itemView.context, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        internal val DEVICE_WIDTH =
            (ArchitectureApp.instance!!.resources.displayMetrics.widthPixels)
    }
}