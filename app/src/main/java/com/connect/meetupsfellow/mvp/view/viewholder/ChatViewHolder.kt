package com.connect.meetupsfellow.mvp.view.viewholder

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.text.format.DateFormat
import android.text.util.Linkify
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.connect.meetupsfellow.R
import com.connect.meetupsfellow.application.ArchitectureApp
import com.connect.meetupsfellow.constants.Constants
import com.connect.meetupsfellow.databinding.ItemChatOtherBinding
import com.connect.meetupsfellow.developement.interfaces.FirebaseUtil
import com.connect.meetupsfellow.developement.interfaces.SharedPreferencesUtil
import com.connect.meetupsfellow.global.utils.DisplayImage
import com.connect.meetupsfellow.log.LogManager
import com.connect.meetupsfellow.mvp.view.model.ChatModel
//import tcking.github.com.giraffeplayer2.GiraffePlayer
//import tcking.github.com.giraffeplayer2.VideoInfo
import java.util.*
import javax.inject.Inject

class ChatViewHolder(private val binding: ItemChatOtherBinding, val convoId: String) :
    RecyclerView.ViewHolder(binding.root) {

    @Inject
    lateinit var firebaseUtil: FirebaseUtil

    @Inject
    lateinit var sharedPreferencesUtil: SharedPreferencesUtil

    init {
        ArchitectureApp.component!!.inject(this@ChatViewHolder)

        if (binding.flImage != null) if (null != binding.flImage.layoutParams) {
            binding.flImage.layoutParams.width = (DEVICE_WIDTH / 1.8).toInt()
            binding.flImage.layoutParams.height = (DEVICE_WIDTH / 2.8).toInt()
        }

        if (binding.ivChatImage != null) binding.ivChatImage.setOnClickListener {
            when (chatModel.mediaType) {
                Constants.MediaType.Image -> {
                    itemView.context.startActivity(
                        Intent(Constants.Intent.Chat_Video_Player).putExtras(Bundle().apply {
                            putString(
                                Constants.IntentDataKeys.VideoUrl, chatModel.mediaUrlOriginal
                            )
                        })
                    )
                }

                Constants.MediaType.Video -> {
                    playVideo()
                }
            }
        }

        if (binding.flPlay != null) binding.flPlay.setOnClickListener { playVideo() }

        /* if (itemView.rlPro != null)
             itemView.rlPro.setOnClickListener {
                 if (RecyclerViewClick.onClickListener == null)
                     return@setOnClickListener

                 RecyclerViewClick.onClickListener!!.onItemClick(
                     adapterPosition,
                     ItemClickStatus.Upgrade
                 )
             }*/
    }

    private lateinit var chatModel: ChatModel
//    private lateinit var key: String

    internal fun bind(chatModel: ChatModel) {
//        this.key = key
        this.chatModel = chatModel

//        if (chatModel.chatSenderId.isEmpty()) {
//            itemView.pro_text.text = itemView.context.getString(R.string.upgrade_to_pro_chat)
//            return
//        }


        Log.d("sender", chatModel.chatSenderName)
        Log.d("userName", sharedPreferencesUtil.fetchUserProfile().name)

        /*if (itemView.userNameChat != null) {

            if (chatModel.chatSenderName == sharedPreferencesUtil.fetchUserProfile().name){

                itemView.userNameChat.text = "You"
            }

        } else {

            if (itemView.userNameChat1 != null) {

                itemView.userNameChatImg1.text = chatModel.chatSenderName
                itemView.userNameChat1.text = chatModel.chatSenderName
            }
        }*/

        binding.tvChatMessage.visibility = View.GONE
        binding.flImage.visibility = View.GONE
        binding.flPlay.visibility = View.GONE

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            binding.tvChatMessage.text = Html.fromHtml(chatModel.chatMessage, 0)
        } else {
            binding.tvChatMessage.text = Html.fromHtml(chatModel.chatMessage)
        }
        Linkify.addLinks(binding.tvChatMessage, Linkify.ALL)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            binding.tvChatMessage.setLinkTextColor(
                itemView.context.resources.getColor(
                    R.color.hyper_link,
                    itemView.context.theme
                )
            )
        } else {
            binding.tvChatMessage.setLinkTextColor(itemView.context.resources.getColor(R.color.hyper_link))
        }
        binding.tvChatTime.text = setFormattedDate(chatModel.chatTimeStamp)
        binding.ivChatStatus.setImageResource(chatStatus(chatModel.chatStatus))

        //binding.tvChatMessage.visibility = View.VISIBLE
        when (chatModel.mediaType) {
            Constants.MediaType.Text -> {
                binding.tvChatMessage.visibility = View.VISIBLE
            }

            Constants.MediaType.Image -> {
                binding.flImage.visibility = View.VISIBLE
            }

            Constants.MediaType.Video -> {
                binding.flImage.visibility = View.VISIBLE
                binding.flPlay.visibility = View.VISIBLE
            }
        }

        when (chatModel.chatSenderId) {
            sharedPreferencesUtil.userId() -> {
                when (chatModel.chatStatus) {
                    Constants.MessageStatus.NotSent -> {
//                        updateChatStatus(chatModel.chatStatus, chatModel.chatSenderId)
                    }
//                    Constants.MessageStatus.Read -> removeMessage(chatModel.chatSenderId)
                }
                showImage(binding.ivChatImage, chatModel.mediaUrlThumb, R.drawable.mask_image)
            }

            else -> {
                showImage(binding.ivChatImage, chatModel.mediaUrlThumb, R.drawable.mask_image_oth)
//                updateChatStatus(chatModel.chatStatus, chatModel.chatSenderId)
            }
        }
    }

    private fun showImage(imageView: ImageView, imageUrl: String, mask: Int) {

        DisplayImage.with(itemView.context).load(imageUrl)
            .placeholder(R.drawable.meetupsfellow_transpatent).mask(mask)
            .shape(DisplayImage.Shape.NinePatchMask).into(imageView).build()
    }

    private fun chatStatus(status: Int): Int {
        LogManager.logger.i(
            ArchitectureApp.instance!!.tag + 2222,
            "Status is : $status Msg : ${chatModel.chatMessage}"
        )
        return when (status) {
            Constants.MessageStatus.Sent -> R.drawable.double_tick
            Constants.MessageStatus.Delivered -> R.drawable.double_tick
            Constants.MessageStatus.Read -> R.drawable.double_tick_seen
            // Constants.MessageStatus.AdminSupportRead -> R.drawable.double_tick_seen
            Constants.MessageStatus.Deleted -> 1
            else -> 0
        }
    }

//    private fun updateChatStatus(status: Int, userId: String) {
//        when (status) {
//            Constants.MessageStatus.NotSent -> chatStatus(Constants.MessageStatus.Sent, userId)
//            Constants.MessageStatus.Sent -> chatStatus(Constants.MessageStatus.Delivered, userId)
//            Constants.MessageStatus.Delivered -> chatStatus(Constants.MessageStatus.Read, userId)
////            Constants.MessageStatus.Read -> removeMessage(userId)
//        }
//    }
//
//    private fun removeMessage(userId: String) {
//        firebaseUtil.deleteMessage("${Constants.Firebase.Chat}/$convoId/Chat/$key", key)
//    }
//
//    private fun chatStatus(status: Int, userId: String) {
//        LogManager.logger.i(ArchitectureApp.instance!!.tag, "Status is : $status")
//        LogManager.logger.i(ArchitectureApp.instance!!.tag, "Status is : $convoId")
//        LogManager.logger.i(ArchitectureApp.instance!!.tag, "Status is : $key")
//        firebaseUtil.updateChatStatus(
//            "${Constants.Firebase.Chat}/$convoId/Chat/$key/chatStatus",
//            chatModel.apply {
//                chatStatus = status
//            })
//    }

    private fun setFormattedDate(smsTimeInMilis: Long): String {

        val smsTime = Calendar.getInstance()
        smsTime.timeInMillis = smsTimeInMilis

        val now = Calendar.getInstance()

        val timeFormatString = "h:mm aa"

        return when {
            now.get(Calendar.DATE) == smsTime.get(Calendar.DATE) -> "Today " + DateFormat.format(
                timeFormatString, smsTime
            )

            now.get(Calendar.DATE) - smsTime.get(Calendar.DATE) == 1 -> "Yesterday " + DateFormat.format(
                timeFormatString, smsTime
            )

            now.get(Calendar.YEAR) == smsTime.get(Calendar.YEAR) -> /*return DateFormat.format(dateTimeFormatString, smsTime).toString();*/
                DateFormat.format("dd MMM yyyy h:mm aa", smsTime).toString()

            else -> /*return DateFormat.format("MMMM dd yyyy, h:mm aa", smsTime).toString();*/
                DateFormat.format("dd MMM yyyy h:mm aa", smsTime).toString()
        }
    }

//    private fun convoId(userId: String): String {
//        return when (userId < sharedPreferencesUtil.userId()) {
//            true -> "${userId}_${sharedPreferencesUtil.userId()}"
//            false -> "${sharedPreferencesUtil.userId()}_$userId"
//        }
//    }

    private fun playVideo() {
        Log.e("GiraffePlayer", "GiraffePlayer_is Not initialize correctly")
      ///  GiraffePlayer.play(itemView.context, VideoInfo(chatModel.mediaUrlOriginal))

    }

    companion object {
        internal val DEVICE_WIDTH =
            (ArchitectureApp.instance!!.resources.displayMetrics.widthPixels)
    }
}