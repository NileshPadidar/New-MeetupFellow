package com.connect.meetupsfellow.mvp.view.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.connect.meetupsfellow.R
import com.connect.meetupsfellow.mvp.view.activities.NotificationSettingActivity
import com.connect.meetupsfellow.retrofit.response.AllTypeNotificationResponse


class AllTypeNotificationsAdapter :
    RecyclerView.Adapter<AllTypeNotificationsAdapter.NotificationsViewHolder>() {

    private val connectArray = ArrayList<AllTypeNotificationResponse>()
    private lateinit var context: NotificationSettingActivity


    internal fun add(context: Context, users: ArrayList<AllTypeNotificationResponse>) {
        this.connectArray.clear()
        this.connectArray.addAll(users)
        this.context = context as NotificationSettingActivity
        Log.e("ALL_NOtification^*", "AdapterSize- ${connectArray.size}")
    }


    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): AllTypeNotificationsAdapter.NotificationsViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_all_type_notifications, parent, false)
        return NotificationsViewHolder(view)
    }

    override fun onBindViewHolder(holder: NotificationsViewHolder, position: Int) {
        val connectItem = connectArray[position]
        holder.bind(connectItem, position)
    }

    override fun getItemCount(): Int {
        return connectArray.size
    }

    inner class NotificationsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val connectTextView: TextView = itemView.findViewById(R.id.tvNotificationName)
        private val notificationSwitch: Switch = itemView.findViewById(R.id.notificationSwitch)
        private val ivNotificationImage: ImageView = itemView.findViewById(R.id.ivNotificationImage)

        fun bind(connectItem: AllTypeNotificationResponse, position: Int) {
            connectTextView.text = connectItem.name
            if (connectItem.isCheck == 1) {
                //notificationSwitch.checked = IconSwitch.Checked.RIGHT
                notificationSwitch.isChecked = true
            } else {

                //  notificationSwitch.checked = IconSwitch.Checked.LEFT
                notificationSwitch.isChecked = false
            }


            notificationSwitch.setOnCheckedChangeListener(null)
            notificationSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
                when (isChecked) {
                    false -> {
                        connectItem.id?.let { context.enableDisableNotificationUpdate(it) }
                    }
                    true -> {
                        connectItem.id?.let { context.enableDisableNotificationUpdate(it) }
                    }

                    else -> {}
                }
            }/*
            notificationSwitch.setCheckedChangeListener(null)

           notificationSwitch.setCheckedChangeListener(object : IconSwitch.CheckedChangeListener {
                override fun onCheckChanged(current: IconSwitch.Checked?) {

                    when (current) {
                        IconSwitch.Checked.LEFT -> {
                            connectItem.id?.let { context.enableDisableNotificationUpdate(it) }
                        }
                        IconSwitch.Checked.RIGHT -> {
                            connectItem.id?.let { context.enableDisableNotificationUpdate(it) }
                        }
                    }
                }
            })*/


            //  tvUserBio.text = connectItem.id.toString()
            // connectItem.imageThumb?.let { showCircleImage(ivUserImage, it) }

        }

    }

    private fun showCircleImage(imageView: ImageView, imageUrl: String) {

        var requestOptions = RequestOptions()
        requestOptions = requestOptions.transforms(CenterCrop(), RoundedCorners(16))

        Glide.with(context).load(imageUrl).placeholder(R.drawable.meetupsfellow_transpatent)
            .apply(requestOptions).into(imageView)

    }
}