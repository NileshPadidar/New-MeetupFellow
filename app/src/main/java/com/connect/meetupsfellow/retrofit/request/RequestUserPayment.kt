package com.connect.meetupsfellow.retrofit.request

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class RequestUserPayment : Serializable{

    @SerializedName("plan_id")
    @Expose
    internal var planId = 0

    @SerializedName("transaction_id")
    @Expose
    internal var transaction_id = ""

    @SerializedName("pay_method")
    @Expose
    internal var pay_method = ""

    @SerializedName("transaction_receipt")
    @Expose
    internal var transaction_receipt = ""


}