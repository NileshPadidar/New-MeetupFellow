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
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.connect.meetupsfellow.R
import com.connect.meetupsfellow.constants.Constants
import com.connect.meetupsfellow.mvp.view.activities.UserProfileActivity
import com.connect.meetupsfellow.mvp.view.fragment.MyConnectionsFragment
import com.connect.meetupsfellow.retrofit.response.ResponseConnectionRequest
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


class MyConnectionsAdapter() :
    RecyclerView.Adapter<MyConnectionsAdapter.ConnectRequestViewHolder>(){

    private  val connectArray = ArrayList<ResponseConnectionRequest>()
    private lateinit var context :Context
    private lateinit var myConnectionsFragment: MyConnectionsFragment


    internal fun add(context: Context,fragment: MyConnectionsFragment, users: ArrayList<ResponseConnectionRequest>) {
        this.connectArray.clear()
        this.connectArray.addAll(users)
        this.context = context
        this.myConnectionsFragment = fragment
        Log.e("MyConnections^*","AdapterSize- ${connectArray.size}")
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyConnectionsAdapter.ConnectRequestViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_connect_request, parent, false)
        return ConnectRequestViewHolder(view)
    }

    override fun onBindViewHolder(holder: ConnectRequestViewHolder, position: Int) {
        val connectItem = connectArray[position]
        holder.bind(connectItem,position)
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

        fun bind(connectItem: ResponseConnectionRequest, position: Int) {
            connectTextView.text = connectItem.name
            tvUserBio.text = connectItem.aboutMe
            connectItem.imageThumb?.let { showCircleImage(ivUserImage, it) }

            ivAccept.visibility = View.GONE
            ivReject.visibility = View.GONE
            btn_cancel_request.text = "Unfriend"
            btn_cancel_request.visibility = View.VISIBLE
            LlMain.setOnClickListener {
              //  Toast.makeText(context, "Intent ${connectItem.userId}", Toast.LENGTH_SHORT).show()
                val intent = Intent(
                    context,
                    UserProfileActivity::class.java
                )
                intent.putExtra(Constants.IntentDataKeys.UserId, connectItem.userId)
                context.startActivity(intent)
            }

            btn_cancel_request.setOnClickListener(){
                if (connectItem.directMessage != null){
                  ///  val chatRoomId = connectItem.senderId.toString() + "-" + connectItem.receiverId.toString()
                    val  chatRoomId  = Constants.ConvoId.id(
                        connectItem.senderId!!,
                        connectItem.receiverId!!
                    )
                    Log.e("chatRoomId*&","chatRoomId:- $chatRoomId")
                    myConnectionsFragment.showUnfriendDialog(connectItem.userId!!,"directMessage",chatRoomId,
                        connectItem.name.toString()
                    )
                }else{
                    myConnectionsFragment.showUnfriendDialog(connectItem.userId!!,"request","",connectItem.name.toString())
                }
               /* Handler().postDelayed(object : Runnable {
                    override fun run() {
                        connectArray.removeAt(position)
                        notifyDataSetChanged()
                    }
                }, 2000)*/
            }
        }

    }

    private fun showCircleImage(imageView: ImageView, imageUrl: String) {

        var requestOptions = RequestOptions()
        requestOptions = requestOptions.transforms(CenterCrop(), RoundedCorners(12))

        Glide.with(context)
            .load(imageUrl)
            .placeholder(R.drawable.meetupsfellow_transpatent)
            .apply(requestOptions)
            .into(imageView)

    }

}