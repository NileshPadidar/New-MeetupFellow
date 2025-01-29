package com.connect.meetupsfellow.mvp.presenter.model

import com.connect.meetupsfellow.application.ArchitectureApp
import com.connect.meetupsfellow.constants.ConstantsApi
import com.connect.meetupsfellow.developement.interfaces.ApiConnect
import com.connect.meetupsfellow.developement.interfaces.SharedPreferencesUtil
import com.connect.meetupsfellow.global.utils.Connected
import com.connect.meetupsfellow.mvp.connector.model.LocationConnector
import com.connect.meetupsfellow.retrofit.request.RequestUpdateLocation
import com.connect.meetupsfellow.retrofit.response.CommonResponse
import retrofit2.Response
import rx.Observer
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject

class LocationPresenter(internal var mView: LocationConnector.RequiredViewOps) :
    LocationConnector.PresenterOps, LocationConnector.RequiredPresenterOps {

    private lateinit var requestUpdateLocation: RequestUpdateLocation

    override fun addCurrentLocationObject(requestUpdateLocation: RequestUpdateLocation) {
        this.requestUpdateLocation = requestUpdateLocation
    }

    @Inject
    lateinit var apiConnect: ApiConnect

    @Inject
    lateinit var sharedPreferencesUtil: SharedPreferencesUtil

    init {
        ArchitectureApp.component!!.inject(this@LocationPresenter)
    }

    override fun callRetrofit(type: ConstantsApi) {
        when (Connected.isConnected()) {
            false -> {
                return
            }

            true -> {}
        }

        when (type) {
            ConstantsApi.UPDATE_LOCATION -> {
                callForCoordinatesUpdate(type)
            }

            else -> {

            }
        }
    }

    private fun callForCoordinatesUpdate(type: ConstantsApi) {
        try {
            val login = apiConnect.api.updateLocation(
                sharedPreferencesUtil.loginToken(),
                requestUpdateLocation
            )

            login.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<Response<CommonResponse>> {
                    override fun onError(e: Throwable?) {
                        e!!.printStackTrace()
                    }

                    override fun onNext(response: Response<CommonResponse>?) {

                    }

                    override fun onCompleted() {
                    }

                })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}