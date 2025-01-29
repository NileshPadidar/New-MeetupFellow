package com.connect.meetupsfellow.mvp.model.model

import com.connect.meetupsfellow.application.ArchitectureApp
import com.connect.meetupsfellow.developement.interfaces.SharedPreferencesUtil
import com.connect.meetupsfellow.mvp.connector.model.LocationConnector
import javax.inject.Inject

class UpdateLocationModel(internal var mPresenter: LocationConnector.RequiredPresenterOps) :
    LocationConnector.ModelOps {

    @Inject
    lateinit var sharedPreferencesUtil: SharedPreferencesUtil

    init {
        ArchitectureApp.component!!.inject(this)
    }
}