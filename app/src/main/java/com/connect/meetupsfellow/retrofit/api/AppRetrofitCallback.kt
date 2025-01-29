package com.connect.meetupsfellow.retrofit.api

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Created by Maheshwar on 07/03/16.
 */
abstract class AppRetrofitCallback<S> : Callback<S> {

    override fun onResponse(call: Call<S>, response: Response<S>) {
        common()
        onResponseVoidzResponse(call, response)

        val obj = response.body()
        if (obj != null) {
            val objectResponse = obj as S?
            onResponseVoidzObject(call, objectResponse!!)
        }
    }

    override fun onFailure(call: Call<S>, t: Throwable) {
        common()
        //onFailureVoidz(call, t);
    }

    /**
     * Invoked for a received HTTP response.
     *
     *
     * Note: An HTTP response may still indicate an application-level failure such as a 404 or 500.
     * Call [Response.isSuccess] to determine if the response indicates success.
     */
    protected abstract fun onResponseVoidzResponse(call: Call<*>, response: Response<*>)

    /**
     * Invoked for a received HTTP response.
     *
     *
     * Note: An HTTP response may still indicate an application-level failure such as a 404 or 500.
     * Call [Response.isSuccess] to determine if the response indicates success.
     */
    protected abstract fun onResponseVoidzObject(call: Call<*>, response: S)

    /**
     * Invoked when a network exception occurred talking to the server or when an unexpected
     * exception occurred creating the request or processing the response.
     */
    //protected customAbstract void onFailureVoidz(Call call, Throwable t);

    /**
     * Invoked everyTime
     */
    protected abstract fun common()
}
