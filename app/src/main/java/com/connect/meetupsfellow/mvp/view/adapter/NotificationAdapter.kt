package com.connect.meetupsfellow.mvp.view.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import com.daimajia.swipe.SwipeLayout
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter
import com.connect.meetupsfellow.R
import com.connect.meetupsfellow.databinding.ItemNotificationBinding
import com.connect.meetupsfellow.mvp.view.viewholder.NotificationsViewHolder
import com.connect.meetupsfellow.retrofit.response.ResponseNotifications

class NotificationAdapter :
    RecyclerSwipeAdapter<NotificationsViewHolder>() {

    private val swipeListener = object : SwipeLayout.SwipeListener {
        override fun onOpen(layout: SwipeLayout?) {
        }

        override fun onUpdate(layout: SwipeLayout?, leftOffset: Int, topOffset: Int) {
        }

        override fun onStartOpen(layout: SwipeLayout?) {
            mItemManger.closeAllExcept(layout)
        }

        override fun onStartClose(layout: SwipeLayout?) {

        }

        override fun onHandRelease(layout: SwipeLayout?, xvel: Float, yvel: Float) {

        }

        override fun onClose(layout: SwipeLayout?) {
        }

    }

    private val notification = arrayListOf<ResponseNotifications>()
    private var proMembership : Boolean = false
    private var plan_id : Int = 2
    private var binding : ItemNotificationBinding? = null

    internal fun add(
        notification: ArrayList<ResponseNotifications>,
        proMembership: Boolean,
        planId: Int
    ) {
        this.notification.addAll(notification)
        this.proMembership = proMembership
        this.plan_id = planId
        update()
    }

    private fun update() {
        val dummy = linkedSetOf<ResponseNotifications>()
        dummy.addAll(notification)
        notification.clear()
        notification.addAll(dummy)
        dummy.clear()
        notifyDataSetChanged()
        Log.e("Notification*&","Noti_Adapter%: ${notification.size}")
    }

    internal fun id(position: Int): Int {
        return when (indexExists(notification, position)) {
            true -> notification[position].notificationId
            false -> -1
        }
    }

    internal fun removeAll() {
        if (notification.isNotEmpty())
            notification.clear()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationsViewHolder {
        /*return NotificationsViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_notification, parent, false)
        )*/
         binding = ItemNotificationBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return NotificationsViewHolder(binding!!)
    }

    override fun getSwipeLayoutResourceId(position: Int): Int {
        return R.id.swipe
    }

    override fun getItemCount(): Int {
        return notification.size
    }

    override fun onBindViewHolder(viewHolder: NotificationsViewHolder, position: Int) {
        viewHolder.bind(notification[viewHolder.adapterPosition],proMembership,plan_id)
       /// viewHolder.itemView.swipe.addSwipeListener(swipeListener)
        binding!!.swipe.addSwipeListener(swipeListener)
        mItemManger.bindView(viewHolder.itemView, viewHolder.adapterPosition)
    }

    private fun indexExists(list: java.util.ArrayList<*>, index: Int): Boolean =
        index >= 0 && index < list.size
}