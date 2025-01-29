package com.connect.meetupsfellow.mvp.model.activity

import com.connect.meetupsfellow.application.ArchitectureApp
import com.connect.meetupsfellow.developement.interfaces.FirebaseUtil
import com.connect.meetupsfellow.developement.interfaces.SharedPreferencesUtil
import com.connect.meetupsfellow.mvp.connector.activity.PrivateAlbumImagesConnector
import javax.inject.Inject

class PrivateAlbumImagesModel(internal var mPresenter: PrivateAlbumImagesConnector.RequiredPresenterOps) :
    PrivateAlbumImagesConnector.ModelOps {

    @Inject
    lateinit var sharedPreferencesUtil: SharedPreferencesUtil

    @Inject
    lateinit var firebaseUtil: FirebaseUtil

    init {
        ArchitectureApp.component!!.inject(this@PrivateAlbumImagesModel)
    }

    override fun logoutUser() {
//        sharedPreferencesUtil.clearAll()
    }

    override fun unBlockUser(userId: String) {
//        firebaseUtil.hasConversation(
//            "${sharedPreferencesUtil.userId()}/${Constants.ConvoId.id(
//                sharedPreferencesUtil.userId().toInt(),
//                userId.toInt()
//            )}", object :
//                FirebaseListener {
//                override fun onCancelled(error: DatabaseError) {
//                    LogManager.logger.i(ArchitectureApp().tag, "DataSnapshot is Error : $error")
//                }
//
//                override fun onDataChange(snapshot: DataSnapshot) {
//                    if (null == snapshot) {
//                        return
//                    }
//                    when (snapshot.exists()) {
//                        true -> {
//                            firebaseUtil.updateUserBlocked("", userId, sharedPreferencesUtil.userId())
//                        }
//                    }
//                }
//            })

    }
}