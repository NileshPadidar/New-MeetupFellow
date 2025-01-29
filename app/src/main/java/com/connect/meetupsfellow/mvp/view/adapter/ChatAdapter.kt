package com.connect.meetupsfellow.mvp.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.connect.meetupsfellow.R
import com.connect.meetupsfellow.application.ArchitectureApp
import com.connect.meetupsfellow.constants.Constants
import com.connect.meetupsfellow.databinding.ItemBlockBinding
import com.connect.meetupsfellow.databinding.ItemChatOtherBinding
import com.connect.meetupsfellow.developement.interfaces.SharedPreferencesUtil
import com.connect.meetupsfellow.log.LogManager
import com.connect.meetupsfellow.mvp.view.model.ChatModel
import com.connect.meetupsfellow.mvp.view.viewholder.BlockedUsersViewHolder
import com.connect.meetupsfellow.mvp.view.viewholder.ChatViewHolder
import javax.inject.Inject
import kotlin.math.max

class ChatAdapter(private val convoId: String) : RecyclerView.Adapter<ChatViewHolder>() {

    private val messages = arrayListOf<ChatModel>()
    private val dummy = arrayListOf<ChatModel>()

    @Inject
    lateinit var sharedPreferencesUtil: SharedPreferencesUtil

    private var premium = false

    init {
        ArchitectureApp.component!!.inject(this@ChatAdapter)
//        if (convoId != Constants.Admin.Convo) {
        updatePremium()
        addAll(sharedPreferencesUtil.fetchUserChat(convoId))
//        }
    }

    internal fun updatePremium() {
        val user = sharedPreferencesUtil.fetchUserProfile()
//        premium = sharedPreferencesUtil.fetchPremiunNearby()
        premium = user.isProMembership
        notifyDataSetChanged()
    }

    internal fun addAll(message: ArrayList<ChatModel>) {
        if (convoId == Constants.Admin.Convo) {
            messages.clear()
        }
        messages.addAll(message)
        dummy.clear()
        updateChatMessages()
    }

    internal fun addAllAdminSuppory(message: ArrayList<ChatModel>) {
        messages.clear()
        messages.addAll(message)
        dummy.clear()
        updateChatMessages()
    }




    fun getChat(): ArrayList<ChatModel> {
        return messages
    }

    internal fun add(message: ChatModel) {
        messages.add(message)
        dummy.clear()
        updateChatMessages()
    }

    internal fun updateMessage(message: ChatModel) {

        val index =
            messages.firstOrNull { it.chatId == message.chatId }?.let { messages.indexOf(it) } ?: -1
        if (index > -1) {
            when (indexExists(messages, index)) {
                true -> {
                    messages[index] = message
                    saveChat()
                }

                false -> {}
            }
        } else {
            messages.add(message)
            dummy.clear()
            updateChatMessages()
        }

        val index1 =
            dummy.firstOrNull { it.chatId == message.chatId }?.let { dummy.indexOf(it) } ?: -1
        if (index1 > -1) {
            when (indexExists(dummy, index1)) {
                true -> {
                    dummy[index1] = message
                    notifyItemChanged(index1)
                    saveChat()
                }
                false -> {}
            }
        } else {
            messages.add(message)
            dummy.clear()
            updateChatMessages()
        }
    }

    internal fun remove(message: ChatModel) {
        val index =
            messages.firstOrNull { it.chatId == message.chatId }?.let { messages.indexOf(it) } ?: -1
        if (index > -1) {
            when (indexExists(messages, index)) {
                true -> {
                    messages.removeAt(index)
                    saveChat()
                }
                false -> {}
            }
        }

        val index1 =
            dummy.firstOrNull { it.chatId == message.chatId }?.let { dummy.indexOf(it) } ?: -1
        if (index1 > -1) {
            when (indexExists(dummy, index1)) {
                true -> {
                    dummy.removeAt(index1)
                    notifyItemRemoved(index1)
                    saveChat()
                }
                false -> {}
            }
        }
    }

    internal fun removeAll() {
        when (messages.isNotEmpty()) {
            true -> {
                messages.clear()
            }
            false -> {}
        }

        when (dummy.isNotEmpty()) {
            true -> {
                dummy.clear()
                notifyDataSetChanged()
            }
            false -> {}
        }
    }

    private fun saveChat() {
        sharedPreferencesUtil.saveUserChat(convoId, Gson().toJson(messages))
    }

    private fun updateChatMessages() {
        val dummy1 = linkedSetOf<ChatModel>()
        if (convoId != Constants.Admin.Convo) {
            dummy1.addAll(messages)
            messages.clear()
            messages.addAll(dummy1)
        }
        dummy1.clear()
        val items = messages.subList(max(messages.size - 20, 0), messages.size)
        if (items.size == 20) {
            dummy.add(ChatModel())
        }
        dummy.addAll(items)
        notifyDataSetChanged()
        saveChat()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        /*return ChatViewHolder(
            LayoutInflater.from(parent.context).inflate(getLayout(viewType), parent, false), convoId
        )*/
        val binding = ItemChatOtherBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ChatViewHolder(binding,convoId)
    }

    override fun getItemCount(): Int {
        return when (premium) {
            true -> messages.size
            false -> dummy.size
        }
    }

    override fun getItemViewType(position: Int): Int {

        val id = when (premium) {
            true -> messages[position].chatSenderId
            false -> dummy[position].chatSenderId
        }

        return when (id) {
            sharedPreferencesUtil.userId() -> Constants.Chat.Me
            else -> Constants.Chat.Other
        }

//        return when (premium) {
//            true -> when (messages[position].chatSenderId) {
//                sharedPreferencesUtil.userId() -> Constants.Chat.Me
//                "" -> Constants.Chat.Add
//                else -> Constants.Chat.Other
//            }
//            false -> when (dummy[position].chatSenderId) {
//                sharedPreferencesUtil.userId() -> Constants.Chat.Me
//                "" -> Constants.Chat.Add
//                else -> Constants.Chat.Other
//            }
//        }
    }

    override fun onBindViewHolder(viewHolder: ChatViewHolder, position: Int) {
        when (premium) {
            true -> when (messages.isNotEmpty()) {
                true -> {
                    viewHolder.bind(messages[viewHolder.adapterPosition])
                }
                false -> {}
            }
            false -> when (dummy.isNotEmpty()) {
                true -> {
                    viewHolder.bind(dummy[viewHolder.adapterPosition])
                }
                false -> {}
            }
        }
    }

    private fun getLayout(viewType: Int): Int {
        LogManager.logger.i(ArchitectureApp.instance!!.tag, "Device type is : $viewType")
        return when (viewType) {
            Constants.Chat.Other -> R.layout.item_chat_other
//            Constants.Chat.Add -> R.layout.become_pro
            else -> R.layout.item_chat_me
        }
    }

    private fun indexExists(list: java.util.ArrayList<*>, index: Int): Boolean =
        index >= 0 && index < list.size

}
