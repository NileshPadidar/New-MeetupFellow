package com.connect.meetupsfellow.mvp.model.fragment

import com.connect.meetupsfellow.application.ArchitectureApp
import com.connect.meetupsfellow.developement.interfaces.SharedPreferencesUtil
import com.connect.meetupsfellow.mvp.connector.fragment.FavoriteUserConnector
import javax.inject.Inject

class FavoriteUserModel(internal var mPresenter: FavoriteUserConnector.RequiredPresenterOps) :
    FavoriteUserConnector.ModelOps {

    @Inject
    lateinit var sharedPreferencesUtil: SharedPreferencesUtil

    init {
        ArchitectureApp.component!!.inject(this@FavoriteUserModel)
    }
//
//    override fun saveUserProfile(userProfileResponse: UserProfileResponse) {
//        sharedPreferencesUtil.saveUserProfile(Gson().toJson(userProfileResponse))
//    }

}