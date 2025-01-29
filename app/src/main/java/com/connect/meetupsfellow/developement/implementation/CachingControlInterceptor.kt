package com.connect.meetupsfellow.developement.implementation

import com.connect.meetupsfellow.global.utils.Connected
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

class CachingControlInterceptor : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()

        // Add Cache Control only for GET methods
        if (request.method == "GET") {
            request = if (Connected.isConnected()) {
                // 1 day
                request.newBuilder()
                        .header("Cache-Control", "no-cache")
                        .build()
            } else {
                // 4 weeks stale
                request.newBuilder()
                        .header("Cache-Control", "no-cache")
                        .build()
            }
        }

        val originalResponse = chain.proceed(request)
        return originalResponse.newBuilder()
                .header("Cache-Control", "max-age=0")
                .build()
    }
}