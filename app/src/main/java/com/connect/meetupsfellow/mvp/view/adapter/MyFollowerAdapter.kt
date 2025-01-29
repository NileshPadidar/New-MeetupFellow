package com.connect.meetupsfellow.mvp.view.adapter

import android.content.Context
import android.content.Intent
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
import com.connect.meetupsfellow.mvp.view.activities.UserProfileActivity
import com.connect.meetupsfellow.retrofit.response.MyFollowFollowingListRes

class MyFollowerAdapter() :
    RecyclerView.Adapter<MyFollowerAdapter.ConnectRequestViewHolder>() {

    private val connectArray = ArrayList<MyFollowFollowingListRes>()
    private lateinit var context: Context


    internal fun add(context: Context, users: ArrayList<MyFollowFollowingListRes>) {
        this.connectArray.clear()
        this.connectArray.addAll(users)
        this.context = context
        Log.e("MyConnections^*", "AdapterSize- ${connectArray.size}")
    }


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyFollowerAdapter.ConnectRequestViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_connect_request, parent, false)
        return ConnectRequestViewHolder(view)
    }

    override fun onBindViewHolder(holder: ConnectRequestViewHolder, position: Int) {
        val connectItem = connectArray[position]
        holder.bind(connectItem, position)
    }

    override fun getItemCount(): Int {
        return connectArray.size
    }

    inner class ConnectRequestViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val connectTextView: TextView = itemView.findViewById(R.id.tvUserName)
        private val tvUserBio: TextView = itemView.findViewById(R.id.tvUserBio)
        private val btn_cancel_request: TextView = itemView.findViewById(R.id.btn_cancel_request)
        private val ivAccept: ImageView = itemView.findViewById(R.id.ivAccept)
        private val ivReject: ImageView = itemView.findViewById(R.id.ivReject)
        private val ivUserImage: ImageView = itemView.findViewById(R.id.ivUserImage)
        private val LlMain: LinearLayout = itemView.findViewById(R.id.llMain)

        fun bind(connectItem: MyFollowFollowingListRes, position: Int) {
            connectTextView.text = connectItem.name
            tvUserBio.text = connectItem.aboutMe
            connectItem.imageThumb?.let { showCircleImage(ivUserImage, it) }

            ivAccept.visibility = View.GONE
            ivReject.visibility = View.GONE
            btn_cancel_request.visibility = View.GONE
            LlMain.setOnClickListener {
                //  Toast.makeText(context, "Intent ${connectItem.userId}", Toast.LENGTH_SHORT).show()
                val intent = Intent(
                    context,
                    UserProfileActivity::class.java
                )
                intent.putExtra(Constants.IntentDataKeys.UserId, connectItem.userId)
                context.startActivity(intent)
            }


        }

    }

    private fun showCircleImage(imageView: ImageView, imageUrl: String) {

        var requestOptions = RequestOptions()
        requestOptions = requestOptions.transforms(CenterCrop(), RoundedCorners(16))

        Glide.with(context)
            .load(imageUrl)
            .placeholder(R.drawable.meetupsfellow_transpatent)
            .apply(requestOptions)
            .into(imageView)

    }
}