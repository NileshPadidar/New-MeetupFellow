package com.connect.meetupsfellow.retrofit.response

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class Error : Serializable {

    @SerializedName("name", alternate = ["email", "title", "age", "image_1_file"])
    internal var name = ""

//    @SerializedName("email")
//    internal var email = ""
//
//    @SerializedName("title")
//    internal var title = ""


//    {"error_description":"Params are missing","error":"missing_params","errors":{"name":"The username field is required","email":"The email field is required."}}
}