package com.connect.meetupsfellow.mvp.model.activity

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.connect.meetupsfellow.application.ArchitectureApp
import com.connect.meetupsfellow.constants.Constants
import com.connect.meetupsfellow.developement.interfaces.FirebaseUtil
import com.connect.meetupsfellow.developement.interfaces.SharedPreferencesUtil
import com.connect.meetupsfellow.global.firebase.FirebaseListener
import com.connect.meetupsfellow.log.LogManager
import com.connect.meetupsfellow.mvp.connector.activity.UserProfileConnector
import javax.inject.Inject

class UserProfileModel(internal var mPresenter: UserProfileConnector.RequiredPresenterOps) :
    UserProfileConnector.ModelOps {

    @Inject
    lateinit var sharedPreferencesUtil: SharedPreferencesUtil

    @Inject
    lateinit var firebaseUtil: FirebaseUtil

    init {
        ArchitectureApp.component!!.inject(this@UserProfileModel)
    }

    override fun logoutUser() {
        sharedPreferencesUtil.clearAll()
    }

    override fun saveReason(reason: String) {
        sharedPreferencesUtil.saveReason(reason)
    }

    override fun blockUser(userId: String) {
        firebaseUtil.hasConversation(
            "${sharedPreferencesUtil.userId()}/${Constants.ConvoId.id(
                sharedPreferencesUtil.userId().toInt(),
                userId.toInt()
            )}", object :
                FirebaseListener {
                override fun onCancelled(error: DatabaseError) {
                    LogManager.logger.i(ArchitectureApp().tag, "DataSnapshot is Error : $error")
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    if (null == snapshot) {
                        return
                    }
                    when (snapshot.exists()) {
                        true -> {
                            firebaseUtil.updateUserBlocked(
                                sharedPreferencesUtil.userId(),
                                userId,
                                sharedPreferencesUtil.userId()
                            )
                        }

                        false -> {}
                    }
                }
            })
    }
}