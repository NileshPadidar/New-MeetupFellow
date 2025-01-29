package com.connect.meetupsfellow.developement.implementation


import com.connect.meetupsfellow.developement.interfaces.EndPoint

class ConnectApiEndPointImpl : EndPoint {

    private var url = ""

    fun setEndPoint(url: String): ConnectApiEndPointImpl {
        this.url = url
        return this
    }

    override fun getUrl(): String {
        if (url.trim { it <= ' ' }.isEmpty())
            throw IllegalStateException("URL is not set")
        return url
    }

    override fun getName(): String = "Connect API endpoint"

}
