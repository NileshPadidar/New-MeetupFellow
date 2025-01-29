package com.connect.meetupsfellow.retrofit.request

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class SaveContactDetailsReq  : Serializable {

    @SerializedName("contact_details")
    internal var contact_details = arrayListOf<Contact>()


}