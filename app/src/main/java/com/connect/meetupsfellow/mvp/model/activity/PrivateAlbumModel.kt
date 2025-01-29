package com.connect.meetupsfellow.mvp.model.activity

import com.connect.meetupsfellow.application.ArchitectureApp
import com.connect.meetupsfellow.developement.interfaces.FirebaseUtil
import com.connect.meetupsfellow.developement.interfaces.SharedPreferencesUtil
import com.connect.meetupsfellow.mvp.connector.activity.PrivateAlbumConnector
import javax.inject.Inject

class PrivateAlbumModel(internal var mPresenter: PrivateAlbumConnector.RequiredPresenterOps) :
    PrivateAlbumConnector.ModelOps {

    @Inject
    lateinit var sharedPreferencesUtil: SharedPreferencesUtil

    @Inject
    lateinit var firebaseUtil: FirebaseUtil

    init {
        ArchitectureApp.component!!.inject(this@PrivateAlbumModel)
    }

    override fun logoutUser() {

    }

    override fun unBlockUser(userId: String) {

    }
}