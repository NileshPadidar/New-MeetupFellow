package com.connect.meetupsfellow.mvp.view.adapter


import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.connect.meetupsfellow.R
import com.connect.meetupsfellow.constants.Constants
import com.connect.meetupsfellow.mvp.view.activities.ConnectRequestActivity
import com.connect.meetupsfellow.mvp.view.activities.UserProfileActivity
import com.connect.meetupsfellow.mvp.view.fragment.GetConnectionRequestFragment
import com.connect.meetupsfellow.mvp.view.fragment.SendConnectionRequestFragment
import com.connect.meetupsfellow.retrofit.response.ResponseConnectionRequest
import kotlin.coroutines.coroutineContext


class SendConnectionAdapter() :
    RecyclerView.Adapter<SendConnectionAdapter.ConnectRequestViewHolder>(){

    private  val connectArray = ArrayList<ResponseConnectionRequest>()
    private lateinit var sendConnectionRequestFragment: SendConnectionRequestFragment
    private lateinit var context: Context

    internal fun add(context: Context, fragment: SendConnectionRequestFragment, users: ArrayList<ResponseConnectionRequest>) {
        this.connectArray.clear()
        this.connectArray.addAll(users)
        this.sendConnectionRequestFragment = fragment
        this.context = context

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConnectRequestViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_connect_request, parent, false)
        return ConnectRequestViewHolder(view)
    }

    @SuppressLint("SuspiciousIndentation")
    override fun onBindViewHolder(holder: ConnectRequestViewHolder, position: Int) {
        val connectItem = connectArray[position]
            holder.bind_send(connectItem,position)
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

        fun bind_send(connectItem: ResponseConnectionRequest, position: Int) {
            connectTextView.text = connectItem.name
            tvUserBio.text = connectItem.aboutMe
            connectItem.imageThumb?.let { showCircleImage(ivUserImage, it) }

            if (connectItem.connectionStatus.equals("request_sent")||connectItem.connectionStatus.equals("direct_message_sent")){
                ivAccept.visibility = View.GONE
                ivReject.visibility = View.GONE
                btn_cancel_request.visibility = View.VISIBLE
            }else if (connectItem.connectionStatus.equals("request_received") || connectItem.connectionStatus.equals("direct_message_received")){
                ivAccept.visibility = View.VISIBLE
                ivReject.visibility = View.VISIBLE
                btn_cancel_request.visibility = View.GONE
            }else{
                ivAccept.visibility = View.GONE
                ivReject.visibility = View.GONE
                btn_cancel_request.visibility = View.GONE
            }
            btn_cancel_request.setOnClickListener{
                sendConnectionRequestFragment.cancelConnectionRequest(connectItem.receiverId!!)
                /*connectArray.removeAt(position)
                notifyDataSetChanged()
                Toast.makeText(context, "Cancel ${connectItem.connectionId}", Toast.LENGTH_SHORT).show()*/
               /* Handler().postDelayed(object : Runnable {

                    override fun run() {
                        connectArray.removeAt(position)
                        notifyDataSetChanged()
                    }
                }, 2000)*/
            }

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
        requestOptions = requestOptions.transforms(CenterCrop(), RoundedCorners(12))

        Glide.with(context)
            .load(imageUrl)
            .placeholder(R.drawable.meetupsfellow_transpatent)
            .apply(requestOptions)
            .into(imageView)

    }
}