package com.connect.meetupsfellow.mvp.model.activity

import com.connect.meetupsfellow.application.ArchitectureApp
import com.connect.meetupsfellow.developement.interfaces.FirebaseUtil
import com.connect.meetupsfellow.developement.interfaces.SharedPreferencesUtil
import com.connect.meetupsfellow.mvp.connector.activity.ChatConnector
import com.connect.meetupsfellow.mvp.view.activities.ChatActivity
import javax.inject.Inject

class ChatModel(internal var mPresenter: ChatConnector.RequiredPresenterOps) :
    ChatConnector.ModelOps {

    @Inject
    lateinit var sharedPreferencesUtil: SharedPreferencesUtil

    @Inject
    lateinit var firebaseUtil: FirebaseUtil

    init {
        ArchitectureApp.component!!.inject(this@ChatModel)
    }

    override fun logoutUser() {
        sharedPreferencesUtil.clearAll()
    }

    override fun blockUser(userId: String) {
        when (ChatActivity.chatExist) {
            true -> {
                firebaseUtil.updateUserBlocked(
                    sharedPreferencesUtil.userId(),
                    userId,
                    sharedPreferencesUtil.userId()
                )
            }

            false -> TODO()
        }
    }
}