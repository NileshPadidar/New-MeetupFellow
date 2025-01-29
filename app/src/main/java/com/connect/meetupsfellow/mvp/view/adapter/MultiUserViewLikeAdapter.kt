package com.connect.meetupsfellow.mvp.view.adapter

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.connect.meetupsfellow.R
import com.connect.meetupsfellow.constants.Constants
import com.connect.meetupsfellow.retrofit.response.VisitorsList

class MultiUserViewLikeAdapter : RecyclerView.Adapter<MultiUserViewLikeAdapter.UserViewHolder>() {

    private val connectArray = ArrayList<VisitorsList>()
    private lateinit var context: Context

    internal fun add(
        context: Context, users: ArrayList<VisitorsList>
    ) {
        this.connectArray.clear()
        this.connectArray.addAll(users)
        this.context = context
        Log.e("MyConnections^*", "AdapterSize- ${connectArray.size}")
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): MultiUserViewLikeAdapter.UserViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_notifications, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val connectItem = connectArray[position]
        holder.bind(connectItem, position)
    }

    override fun getItemCount(): Int {
        return connectArray.size
    }

    inner class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvPlanName: TextView = itemView.findViewById(R.id.tvNotificationMessage)
        private val userProfile: ImageView = itemView.findViewById(R.id.ivNotificationImage)
        private val LlMain: LinearLayout = itemView.findViewById(R.id.llNotification)

        fun bind(connectItem: VisitorsList, position: Int) {
            Log.e("adapterHistory", "name: ${connectItem.visitorName}")
            tvPlanName.text = connectItem.visitorName
            showCircleImage(userProfile, connectItem.imagePath.toString())


             LlMain.setOnClickListener {
                 itemView.context.startActivity(
                 Intent(Constants.Intent.ProfileUser).putExtras(
                     Bundle().apply {
                         putInt(
                             Constants.IntentDataKeys.UserId,
                             connectItem.visitorId!!.toInt()
                         )
                     })
                 )
                /* val intent = Intent(
                     context,
                     UserProfileActivity::class.java
                 )
                 intent.putExtra(Constants.IntentDataKeys.UserId, connectItem.userId)
                 context.startActivity(intent)*/
             }

        }

    }

    private fun showCircleImage(imageView: ImageView, imageUrl: String) {

        var requestOptions = RequestOptions()
        requestOptions = requestOptions.transforms(CenterCrop(), RoundedCorners(12))

        Glide.with(context).load(imageUrl).placeholder(R.drawable.meetupsfellow_transpatent)
            .apply(requestOptions).into(imageView)

    }

}