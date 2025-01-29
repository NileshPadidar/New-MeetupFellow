package com.connect.meetupsfellow.retrofit.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable


class ResponseAds : Serializable {

    @SerializedName("id")
    @Expose
    internal var id = 0

    @SerializedName("userId")
    @Expose
    internal var userId = 0

    @SerializedName("image")
    @Expose
    internal var image = ""

    @SerializedName("price")
    @Expose
    internal var price = 0

    @SerializedName("schedule")
    @Expose
    internal var schedule = ""

    @SerializedName("type")
    @Expose
    internal var type = ""

    @SerializedName("status")
    @Expose
    internal var status = ""

    @SerializedName("position")
    @Expose
    internal var position = ""

    @SerializedName("address")
    @Expose
    internal var address = ""

    @SerializedName("userlat")
    @Expose
    internal var userlat = ""

    @SerializedName("userlong")
    @Expose
    internal var userlong = ""

    @SerializedName("created_at")
    @Expose
    internal var createdAt = ""

    @SerializedName("updated_at")
    @Expose
    internal var updatedAt = ""

   /* fun getId(): Int? {
        return id
    }

    fun setId(id: Int?) {
        this.id = id
    }

    fun getUserId(): Int? {
        return userId
    }

    fun setUserId(userId: Int?) {
        this.userId = userId
    }

    fun getImage(): String? {
        return image
    }

    fun setImage(image: String?) {
        this.image = image
    }

    fun getPrice(): Int? {
        return price
    }

    fun setPrice(price: Int?) {
        this.price = price
    }

    fun getSchedule(): String? {
        return schedule
    }

    fun setSchedule(schedule: String?) {
        this.schedule = schedule
    }

    fun getType(): String? {
        return type
    }

    fun setType(type: String?) {
        this.type = type
    }

    fun getStatus(): String? {
        return status
    }

    fun setStatus(status: String?) {
        this.status = status
    }

    fun getPosition(): Int? {
        return position
    }

    fun setPosition(position: Int?) {
        this.position = position
    }

    fun getAddress(): String? {
        return address
    }

    fun setAddress(address: String?) {
        this.address = address
    }

    fun getUserlat(): String? {
        return userlat
    }

    fun setUserlat(userlat: String?) {
        this.userlat = userlat
    }

    fun getUserlong(): String? {
        return userlong
    }

    fun setUserlong(userlong: String?) {
        this.userlong = userlong
    }

    fun getCreatedAt(): String? {
        return createdAt
    }

    fun setCreatedAt(createdAt: String?) {
        this.createdAt = createdAt
    }

    fun getUpdatedAt(): String? {
        return updatedAt
    }

    fun setUpdatedAt(updatedAt: String?) {
        this.updatedAt = updatedAt
    }
*/
}