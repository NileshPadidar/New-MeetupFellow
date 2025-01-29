package com.connect.meetupsfellow.mvp.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.connect.meetupsfellow.R
import com.connect.meetupsfellow.databinding.ItemNotificationBinding
import com.connect.meetupsfellow.databinding.ItemPrivatePictureAccessBinding
import com.connect.meetupsfellow.mvp.view.viewholder.NotificationsViewHolder
import com.connect.meetupsfellow.mvp.view.viewholder.PrivateAccessViewHolder
import com.connect.meetupsfellow.retrofit.response.ResponsePrivateAccess

class PrivateAccessAdapter : RecyclerView.Adapter<PrivateAccessViewHolder>() {

    private var privateAccess = arrayListOf<ResponsePrivateAccess>()

    internal fun update(privateAccess: ArrayList<ResponsePrivateAccess>) {
        this.privateAccess = privateAccess
        val dummy = linkedSetOf<ResponsePrivateAccess>()
        dummy.addAll(this.privateAccess)
        this.privateAccess.clear()
        this.privateAccess.addAll(dummy)
        dummy.clear()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PrivateAccessViewHolder {
        val binding = ItemPrivatePictureAccessBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return PrivateAccessViewHolder(binding,viewType)
       /* return PrivateAccessViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_private_picture_access,
                parent,
                false
            ),
            viewType
        )*/
    }

    override fun getItemCount(): Int {
        return privateAccess.size
    }

    override fun onBindViewHolder(holder: PrivateAccessViewHolder, position: Int) {
        holder.bind(privateAccess[holder.adapterPosition])
    }
}