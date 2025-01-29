package com.connect.meetupsfellow.retrofit.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class PlanList  : Serializable {

    @SerializedName("id")
    @Expose
    var id: Int? = null

    @SerializedName("title")
    @Expose
    var title: String? = null

    @SerializedName("store_id")
    @Expose
    var store_id: String? = null

    @SerializedName("offer_price")
    @Expose
    var offerPrice: String? = null

    @SerializedName("price")
    @Expose
    var price: String? = null

    @SerializedName("validity")
    @Expose
    var validity: String? = null
}