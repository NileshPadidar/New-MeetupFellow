package com.connect.meetupsfellow.mvp.view.viewholder

import android.view.View
import com.connect.meetupsfellow.application.ArchitectureApp
import com.connect.meetupsfellow.developement.interfaces.SharedPreferencesUtil
import javax.inject.Inject

class AdminAdsViewHolder (itemView: View) :
androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {

    @Inject
    lateinit var sharedPreferencesUtil: SharedPreferencesUtil

    init {

        ArchitectureApp.component!!.inject(this@AdminAdsViewHolder)

        if (itemView.layoutParams != null) {
            itemView.layoutParams.width = (DEVICE_WIDTH / 4)
            itemView.layoutParams.height = (itemView.layoutParams.width * 1.5).toInt()
        }

        /*itemView.setOnClickListener {
            when ("$id") {
                sharedPreferencesUtil.userId() -> {
                    itemView.context.startActivity(Intent(Constants.Intent.Profile))
                }

                else -> {
                    itemView.context.startActivity(
                        Intent(Constants.Intent.ProfileUser).putExtras(
                            Bundle().apply {
                                putInt(Constants.IntentDataKeys.UserId, id)
                            })
                    )
                }
            }
        }*/
    }

    /* private var id = -1

    internal fun bind(responseUserData: ResponseUserData) {

        if (responseUserData.item == 2) {
            return
        }

        this.id = responseUserData.id.toInt()

        itemView.tvUserName.text = responseUserData.name

        LogManager.logger.i(
            ArchitectureApp.instance!!.tag,
            "NearBy Users : ${responseUserData.name}, Online Status : ${responseUserData.onlineStatus}"
        )



        if(responseUserData.onlineStatus == 0){
            itemView.ivUserStatus.isSelected = false
            //Gray Dot
        }else{
            if(responseUserData.lastLoginTimeStamp != ""){
                try{

                    val format = SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
                    var lastDate = format.parse(responseUserData.lastLoginTimeStamp)
                    var currentUTCTime = format.parse(getUTCTime())
                    val diff: Long = currentUTCTime.getTime() - lastDate.getTime()
                    val seconds = diff / 1000
                    val minutes = seconds / 60
                    val hours = minutes / 60

                    if(hours<=4){
                        itemView.ivUserStatus.isSelected = true
                        itemView.ivUserStatus.setImageResource(R.drawable.drawable_user_online)
                        //Green Dot
                    }else if(hours>4 && hours<=48){
                        itemView.ivUserStatus.isSelected = false
                        itemView.ivUserStatus.setImageResource(R.drawable.drawable_user_recent_online)
                        // Orange DOt
                    }else{
                        itemView.ivUserStatus.isSelected = false
                        itemView.ivUserStatus.setImageResource(R.drawable.drawable_user_offline)
                        //Gray Dot
                    }
                }catch (e: Exception){

                }
            }else {
                if(responseUserData.onlineStatus == 1){
                    itemView.ivUserStatus.isSelected = true
                    itemView.ivUserStatus.setImageResource(R.drawable.drawable_user_online)
                    //Green Dot
                }else{
                    itemView.ivUserStatus.isSelected = false
                    itemView.ivUserStatus.setImageResource(R.drawable.drawable_user_offline)
                    //Gray Dot
                }
            }
        }



//        itemView.ivUserStatus.isSelected = responseUserData.onlineStatus != 0

        when (responseUserData.id) {
            sharedPreferencesUtil.userId() -> {
                when (responseUserData.userPic.isEmpty()) {
                    true -> showImage(sharedPreferencesUtil.fetchUserProfileImage())
                    false -> showImage(responseUserData.userThumb)
                }
                itemView.ivUserStatus.isSelected = true
            }
            else -> {
                showImage(responseUserData.userThumb)
//                when (val status = sharedPreferencesUtil.fetchOtherUserProfile(responseUserData.id).onlineStatus) {
//                    true -> itemView.ivUserStatus.isSelected = status
//                    else -> itemView.ivUserStatus.isSelected = responseUserData.onlineStatus != 0
//                }
            }
        }

        itemView.ivUserStatus.visibility = View.VISIBLE

    }

    fun getUTCTime(): String? {
        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSS")
        formatter.timeZone = TimeZone.getTimeZone("UTC")
        return formatter.format(Date())
    }

    private fun showImage(imageUrl: String) {
        DisplayImage.with(itemView.context)
            .load(imageUrl)
            .placeholder(R.drawable.placeholder_icon)
            .shape(DisplayImage.Shape.SHAPE_NEARBY)
            .into(itemView.ivUserImage)
            .build()
    }

    private fun indexExists(list: ArrayList<*>, index: Int): Boolean =
        index >= 0 && index < list.size

    private fun universalToast(message: String) {
        Toast.makeText(itemView.context, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        internal val DEVICE_WIDTH = (ArchitectureApp.instance!!.resources.displayMetrics
            .widthPixels)
    }*/

    companion object {
        internal val DEVICE_WIDTH = (ArchitectureApp.instance!!.resources.displayMetrics
            .widthPixels)
    }
}