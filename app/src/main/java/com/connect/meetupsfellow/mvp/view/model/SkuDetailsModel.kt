package com.connect.meetupsfellow.mvp.view.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class SkuDetailsModel : Serializable {

    @SerializedName("skuDetailsToken")
    @Expose
    internal var skuDetailsToken = ""

    @SerializedName("productId")
    @Expose
    internal var productId = ""

    @SerializedName("type")
    @Expose
    internal var type = ""

    @SerializedName("price")
    @Expose
    internal var price = ""

    @SerializedName("price_amount_micros")
    @Expose
    internal var price_amount_micros = ""

    @SerializedName("price_currency_code")
    @Expose
    internal var price_currency_code = ""

    @SerializedName("subscriptionPeriod")
    @Expose
    internal var subscriptionPeriod = ""

    @SerializedName("title")
    @Expose
    internal var title = ""

    @SerializedName("description")
    @Expose
    internal var description = ""
}