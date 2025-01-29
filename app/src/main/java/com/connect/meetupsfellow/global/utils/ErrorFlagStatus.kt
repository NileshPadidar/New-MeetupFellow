package com.connect.meetupsfellow.global.utils

import com.connect.meetupsfellow.constants.Constants
import com.connect.meetupsfellow.global.interfaces.ErrorFlagStatusMessage
import com.connect.meetupsfellow.retrofit.response.ErrorResponse

object ErrorFlagStatus {

    internal fun getErrorMessage(errorResponse: ErrorResponse, message: ErrorFlagStatusMessage) {

        when (errorResponse.error) {
            Constants.ServerError.InvalidToken, Constants.ServerError.SessionExpired,
            Constants.ServerError.Blocked -> {
                message.status(errorResponse.error_description, true)
            }
        }
        when (errorResponse.errors.name.isNotEmpty()) {
            true -> {
                message.status(errorResponse.errors.name, false)
            }

            false -> {
                message.status(errorResponse.error_description, false)
            }
        }
    }
}