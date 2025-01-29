package com.connect.meetupsfellow.mvp.connector.reusable

import com.connect.meetupsfellow.constants.ConstantsApi

interface ReusableRetrofitConnector {
    fun callRetrofit(type: ConstantsApi)
}