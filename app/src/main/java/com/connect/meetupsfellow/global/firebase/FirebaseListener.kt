package com.connect.meetupsfellow.global.firebase

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError

interface FirebaseListener {
    fun onCancelled(error: DatabaseError)
    fun onDataChange(snapshot: DataSnapshot)
}