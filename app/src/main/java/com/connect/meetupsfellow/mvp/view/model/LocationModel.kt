package com.connect.meetupsfellow.mvp.view.model

import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.connect.meetupsfellow.application.ArchitectureApp
import com.connect.meetupsfellow.constants.ConstantsApi
import com.connect.meetupsfellow.developement.interfaces.SharedPreferencesUtil
import com.connect.meetupsfellow.mvp.connector.model.LocationConnector
import com.connect.meetupsfellow.mvp.presenter.model.LocationPresenter
import com.connect.meetupsfellow.retrofit.request.RequestUpdateLocation
import com.connect.meetupsfellow.retrofit.response.CommonResponse
import java.io.Serializable
import javax.inject.Inject

class LocationModel : Serializable {

    private val presenter = object : LocationConnector.RequiredViewOps {
        override fun showToast(message: String, logout: Boolean?) {
        }

        override fun showResponse(response: CommonResponse, type: ConstantsApi) {
        }
    }

    @SerializedName("currentLat")
    private var latitude = 0.0

    @SerializedName("currentLong")
    private var longitude = 0.0

    @SerializedName("currentLocation")
    private var locationName = ""

    internal fun setLatitude(latitude: Double) {
        this.latitude = latitude
    }

    internal fun setLongitude(longitude: Double) {
        this.longitude = longitude
    }

    internal fun setLocationName(locationName: String) {
        this.locationName = locationName
    }

    @Inject
    lateinit var sharedPreferencesUtil: SharedPreferencesUtil

    private var mPresenter: LocationConnector.PresenterOps? = null

    init {
        ArchitectureApp.component!!.inject(this@LocationModel)
    }

    internal fun saveLocation() {
        sharedPreferencesUtil.saveLocation(
            Gson().toJson(LatLng(latitude, longitude))
        )
        sharedPreferencesUtil.saveLocationName(locationName)
        when (sharedPreferencesUtil.loginToken().isNotEmpty()) {
            true -> {
                updateLocation()
            }

            false -> {}
        }
    }

    private fun updateLocation() {
        if (null == mPresenter)
            mPresenter = LocationPresenter(presenter)

        run {
            mPresenter!!.addCurrentLocationObject(RequestUpdateLocation().apply {
                locationName = this@LocationModel.locationName
                latitude = this@LocationModel.latitude
                longitude = this@LocationModel.longitude
            })
            mPresenter!!.callRetrofit(ConstantsApi.UPDATE_LOCATION)
        }
    }

}