package com.connect.meetupsfellow.mvp.model.model

import com.connect.meetupsfellow.application.ArchitectureApp
import com.connect.meetupsfellow.developement.interfaces.SharedPreferencesUtil
import com.connect.meetupsfellow.mvp.connector.model.UserPaymentConnector
import javax.inject.Inject

class UpdateUserPaymentModel(internal var mPresenter: UserPaymentConnector.RequiredPresenterOps) :
    UserPaymentConnector.ModelOps {

    @Inject
    lateinit var sharedPreferencesUtil: SharedPreferencesUtil

    init {
        ArchitectureApp.component!!.inject(this)
    }
}