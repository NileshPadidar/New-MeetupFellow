package com.connect.meetupsfellow.global.utils

import android.content.Context
import com.connect.meetupsfellow.application.ArchitectureApp
import com.connect.meetupsfellow.constants.Constants
import com.connect.meetupsfellow.developement.interfaces.SharedPreferencesUtil
import com.connect.meetupsfellow.mvp.view.model.ChatModel
import com.connect.meetupsfellow.retrofit.response.ResponsePrivatePics
import javax.inject.Inject
import kotlin.collections.ArrayList

/**
 * Created by Maheshwar on 07-09-2017.
 */
class MediaChatData {

    @Inject
    lateinit var sharedPreferencesUtil: SharedPreferencesUtil

    private val chat = arrayListOf<ChatModel>()
    private var userId = ""

    init {
        ArchitectureApp.component!!.inject(this@MediaChatData)
        userId = sharedPreferencesUtil.userId()
        val convo = sharedPreferencesUtil.fetchConversation()
        convo.asSequence().forEach {
            chat.addAll(sharedPreferencesUtil.fetchUserChat(it.convoId))
        }
    }

    fun loadPhotoUris(context: Context): ArrayList<ResponsePrivatePics> {
        val uris = arrayListOf<ResponsePrivatePics>()
        try {
            chat.asSequence().forEach {
                when (it.mediaType) {
                    Constants.MediaType.Image -> {
                        if (userId == it.chatSenderId) {
                            uris.add(ResponsePrivatePics().apply {
                                path = it.mediaUrlOriginal
                            })
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        uris.reverse()
        return uris
    }

}