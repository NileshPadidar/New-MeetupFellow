package com.connect.meetupsfellow.mvp.model.activity

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.gson.Gson
import com.connect.meetupsfellow.application.ArchitectureApp
import com.connect.meetupsfellow.developement.interfaces.FirebaseUtil
import com.connect.meetupsfellow.developement.interfaces.SharedPreferencesUtil
import com.connect.meetupsfellow.global.firebase.FirebaseListener
import com.connect.meetupsfellow.log.LogManager
import com.connect.meetupsfellow.mvp.connector.activity.HomeConnector
import com.connect.meetupsfellow.mvp.view.model.ConversationModel
import javax.inject.Inject

class HomeModel(internal var mPresenter: HomeConnector.RequiredPresenterOps) :
    HomeConnector.ModelOps {

    @Inject
    lateinit var sharedPreferencesUtil: SharedPreferencesUtil

    @Inject
    lateinit var firebaseUtil: FirebaseUtil

    init {
        ArchitectureApp.component!!.inject(this@HomeModel)
    }

    override fun logoutUser() {
        firebaseUtil.userLogout()
        sharedPreferencesUtil.clearAll()
    }

    override fun saveReason(reason: String) {
        sharedPreferencesUtil.saveReason(reason)
    }

    override fun fetchConversation(userId: String) {
        fetchConversations(userId)
        fetchUserProfiles()
    }

    override fun saveAdminChatCount(count: String) {
        sharedPreferencesUtil.saveAdminChatCount(count)
    }

    private fun fetchConversations(userId: String) {
        firebaseUtil.fetchFullConversation(userId, object : FirebaseListener {
            override fun onCancelled(error: DatabaseError) {
                LogManager.logger.i(ArchitectureApp().tag, "DataSnapshot is Error : $error")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                when (snapshot.exists()) {
                    true -> {
                        sharedPreferencesUtil.saveConversation("")
                        val conversation = snapshot.value as Map<*, *>
                        val data = arrayListOf<ConversationModel>()
                        for ((key, value) in conversation) {
                            val item =
                                Gson().fromJson<ConversationModel>(
                                    Gson().toJson(value),
                                    ConversationModel::class.java
                                )
                            when (item.meDeletedConvo) {
                                false -> {
                                    when (item.blockedBY.isEmpty() || item.blockedBY != sharedPreferencesUtil.userId()) {
                                        true -> data.add(item)
                                        false -> TODO()
                                    }
                                }

                                true -> TODO()
                            }
                        }
                        LogManager.logger.i(
                            ArchitectureApp().tag,
                            "DataSnapshot is Error : ${Gson().toJson(data)}"
                        )
                        sharedPreferencesUtil.saveConversation(
                            if (data.isNotEmpty()) Gson().toJson(
                                data
                            ) else ""
                        )
                    }

                    false -> {
                        sharedPreferencesUtil.saveConversation("")
                    }
                }
            }

        })
    }

    override fun saveNotification(count: String) {
        sharedPreferencesUtil.saveNotificationCount(count)
    }

    private fun fetchUserProfiles() {
//        firebaseUtil.fetchUserProfile()
    }
}