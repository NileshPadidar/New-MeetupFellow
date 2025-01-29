package com.connect.meetupsfellow.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import com.connect.meetupsfellow.constants.Constants


class SmsReceiver : BroadcastReceiver() {

    override fun onReceive(p0: Context?, intent: Intent?) {

        Log.d("SmsReceived", intent.toString())

        val remoteMsg = RemoteMessage(intent!!.extras)

        val convoObj = Gson().toJson(remoteMsg.data["otherSide"])

        if (remoteMsg.data["type"] == "chat") {

            val convoId = convoObj.split(",")[5].split(":")[1].replace("\\\"", "")
            val userId = remoteMsg.data["receiverId"].toString()
            val otherId = remoteMsg.data["senderId"].toString()
            val msgId = convoObj.split(",")[6].split(":")[1].toString()

            Log.d("SmsReceivedC", msgId)

            FirebaseDatabase.getInstance(Constants.Firebase.databaseUrl).getReference(Constants.Firebase.ChatDB)
                .child("ChatList").child(userId).child(convoId)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snap: DataSnapshot) {

                        if (snap.exists()) {

                            if (snap.child("deliveryStatus").value != "3") {

                                FirebaseDatabase.getInstance(Constants.Firebase.databaseUrl)
                                    .getReference(Constants.Firebase.ChatDB).child("ChatList").child(userId)
                                    .child(convoId).child("deliveryStatus")
                                    .setValue("2")
                            }
                        }

                    }

                    override fun onCancelled(p0: DatabaseError) {

                    }


                })

            FirebaseDatabase.getInstance(Constants.Firebase.databaseUrl).getReference(Constants.Firebase.ChatDB)
                .child("ChatList").child(otherId).child(convoId)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snap: DataSnapshot) {

                        if (snap.exists()) {

                            if (snap.child("deliveryStatus").value != "3") {

                                FirebaseDatabase.getInstance(Constants.Firebase.databaseUrl)
                                    .getReference(Constants.Firebase.ChatDB).child("ChatList").child(otherId)
                                    .child(convoId).child("deliveryStatus")
                                    .setValue("2")
                            }
                        }
                    }

                    override fun onCancelled(p0: DatabaseError) {

                    }


                })
        }
    }
}