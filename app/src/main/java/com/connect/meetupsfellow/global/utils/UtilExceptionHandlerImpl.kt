package com.connect.meetupsfellow.global.utils

import android.app.Application
import android.util.Log

import com.connect.meetupsfellow.application.ArchitectureApp

class UtilExceptionHandlerImpl(private val application: Application) : UtilExceptionalHandler {

    private val isShowError = true

    internal lateinit var exception: Exception

    override fun builder(): UtilExceptionalHandler.IException = Builder(this)


    private class Builder(private val uniqInstance: UtilExceptionHandlerImpl) :
            UtilExceptionalHandler.IException, UtilExceptionalHandler.IPrint,
            UtilExceptionalHandler.IBuild {

        override fun exception(e: Exception): UtilExceptionalHandler.IBuild {
            this.uniqInstance.exception = e
            return this
        }

        override fun build(): UtilExceptionalHandler.IPrint = this

        override fun printStackTrace() {
            this.uniqInstance.exception.printStackTrace()
        }

        override fun printMessage(message: String) {
            Log.d(ArchitectureApp().tag, message)
        }
    }
}
