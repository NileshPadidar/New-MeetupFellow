package com.connect.meetupsfellow.retrofit.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class TransactionHistory : Serializable {

    @SerializedName("transaction_id")
    @Expose
    var transactionId: String? = null

    @SerializedName("transaction_receipt")
    @Expose
    var transactionReceipt: String? = null

    @SerializedName("pay_method")
    @Expose
    var payMethod: String? = null

    @SerializedName("plan_price")
    @Expose
    var planPrice: String? = null

    @SerializedName("plan_title")
    @Expose
    var planTitle: String? = null

    @SerializedName("plan_status")
    @Expose
    var planStatus: String? = null

    @SerializedName("plan_id")
    @Expose
    var planId: Int? = null

    @SerializedName("plan_description")
    @Expose
    var planDescription: String? = null

    @SerializedName("plan_purchase_date")
    @Expose
    var planPurchaseDate: String? = null

    @SerializedName("plan_validity")
    @Expose
    var planValidity: String? = null
}