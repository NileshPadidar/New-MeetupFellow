package com.connect.meetupsfellow.retrofit.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class CurrentPlans : Serializable {
    @SerializedName("plan_id")
    @Expose
    var planId: Int? = null

    @SerializedName("status")
    @Expose
    var status: String? = null

    @SerializedName("valid_upto")
    @Expose
    var validUpto: String? = null

    @SerializedName("plan_title")
    @Expose
    var planTitle: String? = null

    @SerializedName("plan_price")
    @Expose
    var planPrice: String? = null

    @SerializedName("validity")
    @Expose
    var validity: String? = null

     @SerializedName("transaction_id")
    @Expose
    var transaction_id: String? = null

    @SerializedName("next_renewal_date")
    @Expose
    var next_renewal_date: String? = null

    @SerializedName("plan_description")
    @Expose
    var planDescription: String? = null

    @SerializedName("plan_purchase_date")
    @Expose
    var planPurchaseDate: String? = null

    @SerializedName("plan_status")
    @Expose
    var planStatus: String? = null

    @SerializedName("plan_validity")
    @Expose
    var planValidity: String? = null
}