package com.connect.meetupsfellow.global.interfaces

interface NewMessageListener {
    fun onNewMessage(userId: String, direction: Int)
}