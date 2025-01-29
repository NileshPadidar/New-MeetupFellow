package com.connect.meetupsfellow.global.utils

interface UtilExceptionalHandler {

    fun builder(): IException

    interface IException {
        fun exception(e: Exception): IBuild
    }

    interface IBuild {
        fun build(): IPrint
    }


    interface IPrint {
        fun printStackTrace()
        fun printMessage(message: String)
    }


}
