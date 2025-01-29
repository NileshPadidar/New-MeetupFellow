package com.connect.meetupsfellow.developement.implementation

import com.connect.meetupsfellow.developement.interfaces.ApiConnect
import com.connect.meetupsfellow.retrofit.api.Api

import retrofit2.Retrofit

class ApiConnectImpl(val retrofit: Retrofit) : ApiConnect {
    override val api: Api
        get() = retrofit.create(Api::class.java)
}
