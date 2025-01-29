package com.connect.meetupsfellow.firebase

import AppStatusTracker
import android.annotation.SuppressLint
import android.app.ActivityManager
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.bumptech.glide.Glide
import com.connect.meetupsfellow.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import com.connect.meetupsfellow.application.ArchitectureApp
import com.connect.meetupsfellow.constants.Constants
import com.connect.meetupsfellow.developement.interfaces.SharedPreferencesUtil
import com.connect.meetupsfellow.global.interfaces.OnNotificationReceived
import com.connect.meetupsfellow.global.utils.Notifications
import com.connect.meetupsfellow.log.LogManager
import com.connect.meetupsfellow.mvp.view.activities.HomeActivity
import com.connect.meetupsfellow.mvp.view.activities.SplashActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.leolin.shortcutbadger.ShortcutBadger
import java.util.*
import javax.inject.Inject

@Suppress("DEPRECATION")
class NotificationService : FirebaseMessagingService() {

    init {
        ArchitectureApp.component!!.inject(this@NotificationService)
    }

    @Inject
    lateinit var sharedPreferencesUtil: SharedPreferencesUtil

    private var mNotificationReceived : OnNotificationReceived? = null

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.v("token",token +" TTTTT")
        LogManager.logger.i(ArchitectureApp.instance!!.tag, "Device Token is ::: $token")
        sharedPreferencesUtil.saveDeviceToken(token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        Log.e("Pushmessage%", "message= $remoteMessage")
        Log.e("Pushmessage%", "Type= ${remoteMessage.data["type"]}")
        if(HomeActivity.mContext != null)

            mNotificationReceived = HomeActivity.mContext!!
        Log.d("Push", "onMessageReceived() called with: remoteMessage = $remoteMessage")

        LogManager.logger.i(ArchitectureApp.instance!!.tag, "Device Token is ::: $remoteMessage")
        when (sharedPreferencesUtil.loginToken().isNotEmpty()) {
            true -> {
                LogManager.logger.i(
                    ArchitectureApp.instance!!.tag, "Push_Notification is1 ::: ${remoteMessage?.data}"
                )
                LogManager.logger.e(
                    ArchitectureApp.instance!!.tag, "Push_Notification_Body ::: ${remoteMessage.data["title"]}"
                )

                if (remoteMessage.data["type"] == "chat") {
                    val convoObj = Gson().toJson(remoteMessage.data["otherSide"])
                    val userId = remoteMessage.data["receiverId"].toString()
                    val otherId = remoteMessage.data["senderId"].toString()
                    val senderName = remoteMessage.data["senderName"].toString()

                    val convoId = convoObj.split(",")[5].split(":")[1].replace("\\\"", "")

                     if (AppStatusTracker.isAppInBackground()) {
                        /// below Show outside notification
                        CoroutineScope(Dispatchers.Main).launch {
                            showNotification(remoteMessage.data["title"].toString(), remoteMessage.data["alert"].toString(), remoteMessage.data["senderImageUrl"].toString(),convoId,remoteMessage.data["senderId"].toString(),senderName,"userChat")
                        }
                    } else {
                        LogManager.logger.e(
                            ArchitectureApp.instance!!.tag, "Push_Notification_Local_Chat::: ${remoteMessage?.data}"
                        )
                    }
                    Log.d("NotificationConvoIDN", convoId + "  " + remoteMessage.data["receiverId"].toString() + " " +remoteMessage.data["senderId"].toString())

                    FirebaseDatabase.getInstance(Constants.Firebase.databaseUrl).getReference(Constants.Firebase.ChatDB).child("ChatList").child(userId).child(convoId).addListenerForSingleValueEvent(object :
                        ValueEventListener {
                        override fun onDataChange(snap: DataSnapshot) {

                            if (snap.exists()) {
                                    val data = 2
                                if (snap.child("deliveryStatus").value != "3") {

                                    FirebaseDatabase.getInstance(Constants.Firebase.databaseUrl).getReference(Constants.Firebase.ChatDB).child("ChatList").child(userId).child(convoId).child("deliveryStatus")
                                        .setValue("2")
                                    FirebaseDatabase.getInstance(Constants.Firebase.databaseUrl).getReference(Constants.Firebase.ChatDB).child("ChatList").child(otherId).child(convoId).child("messageStatus")
                                        .setValue(data)
                                }
                            }
                        }

                        override fun onCancelled(p0: DatabaseError) {}

                    })

                    FirebaseDatabase.getInstance(Constants.Firebase.databaseUrl).getReference(Constants.Firebase.ChatDB).child("ChatList").child(otherId).child(convoId).addListenerForSingleValueEvent(object :
                        ValueEventListener {
                        override fun onDataChange(snap: DataSnapshot) {

                            if (snap.exists()) {
                                    val data = 2
                                if (snap.child("deliveryStatus").value != "3") {

                                    FirebaseDatabase.getInstance(Constants.Firebase.databaseUrl).getReference(Constants.Firebase.ChatDB).child("ChatList").child(otherId).child(convoId).child("deliveryStatus")
                                        .setValue("2")
                                    FirebaseDatabase.getInstance(Constants.Firebase.databaseUrl).getReference(Constants.Firebase.ChatDB).child("ChatList").child(otherId).child(convoId).child("messageStatus")
                                        .setValue(data)
                                }
                            }
                        }

                        override fun onCancelled(p0: DatabaseError) {

                        }

                    })

                    // Update status in chatMessage Room new add by nilu
                    FirebaseDatabase.getInstance(Constants.Firebase.databaseUrl).getReference(Constants.Firebase.ChatDB).child("ChatMessages").child(convoId)
                        .child("chats").addListenerForSingleValueEvent(object :
                        ValueEventListener {
                        override fun onDataChange(snap: DataSnapshot) {
                            if (snap.exists()) {
                                for (chats in snap.children) {

                                    if (chats.child("deliveryStatus").value != "3") {
                                        val data = 2
                                        FirebaseDatabase.getInstance(Constants.Firebase.databaseUrl).getReference(Constants.Firebase.ChatDB).child("ChatMessages").child(convoId)
                                            .child("chats").child(chats.key.toString()).child("deliveryStatus")
                                            .setValue("2")
                                        FirebaseDatabase.getInstance(Constants.Firebase.databaseUrl).getReference(Constants.Firebase.ChatDB).child("ChatMessages").child(convoId)
                                            .child("chats").child(chats.key.toString()).child("messageStatus")
                                            .setValue(data)
                                    }
                                }
                            }

                        }
                        override fun onCancelled(p0: DatabaseError) {

                        }
                    })

                }else{
                    LogManager.logger.i(
                        ArchitectureApp.instance!!.tag, "Push_Notification_Elss ::: ${remoteMessage?.data}"
                    )
                    if (AppStatusTracker.isAppInBackground()) {
                        CoroutineScope(Dispatchers.Main).launch {
                            showNotification(remoteMessage.data["title"].toString(), remoteMessage.data["alert"].toString(),"","",remoteMessage.data["senderId"].toString(),"",remoteMessage.data["type"].toString())
                        }
                    } else {
                        LogManager.logger.e(
                            ArchitectureApp.instance!!.tag, "Push_Notification_Local_Profile::: ${remoteMessage?.data}"
                        )
                    }

                   /* CoroutineScope(Dispatchers.Main).launch {
                        showNotification(remoteMessage.data["title"].toString(), remoteMessage.data["alert"].toString(),"","",remoteMessage.data["senderId"].toString(),"",remoteMessage.data["type"].toString())
                    }*/
                }

                LogManager.logger.i(
                    ArchitectureApp.instance!!.tag,
                    "Push_Notification is3 ::: ${remoteMessage!!.data["senderName"]}"
                )
                remoteMessage.data?.isNotEmpty().let {
                    Notifications
                        .getNotification()?.let {
                            Handler(Looper.getMainLooper()).post {
                                Notifications
                                    .getNotification()!!.onNotification(remoteMessage)

                                if(  remoteMessage!!.data["type"] != "pushNotification") {
                                    if(remoteMessage!!.data["type"] != "open-profile" ){
                                        updateNotificationBadge("${remoteMessage.data["badge"]}")
                                    }
                                }

                                if(mNotificationReceived != null)
                                    mNotificationReceived?.onNotification(remoteMessage)
                            }
                        }
                }


            }

            false -> {}
        }
    }
    private fun updateNotificationBadge(count: String) {
        sharedPreferencesUtil.saveNotificationCount(count)
    }


    // Method to display the notifications add by nilu 31/july/24
    @SuppressLint("NotificationPermission")
    suspend fun showNotification(
        title: String,
        message: String,
        img: String?,
        convoID: String,
        senderId: String,
        senderName: String,
        type: String
    ) {

        // Pass the intent to switch to the MainActivity
        val intent = Intent(this, SplashActivity::class.java)
        // Assign channel ID
       // val channel_id = "notification_channel"
        val channel_id = when (type) {
            "userChat" -> getString(R.string.chat_notification_channel_id)
            "profile-like" -> getString(R.string.profile_notification_channel_id)
            "event" -> getString(R.string.event_notification_channel_id)
            else -> getString(R.string.default_notification_channel_id)
        }
        // Here FLAG_ACTIVITY_CLEAR_TOP flag is set to clear
        // the activities present in the activity stack,
        // on the top of the Activity that is to be launched
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.putExtra(Constants.Notification.SENDER_ID, senderId)
        intent.putExtra(Constants.Notification.ROOM_ID, convoID)
        intent.putExtra(Constants.Notification.IMAGE, img)
        intent.putExtra(Constants.Notification.USER_NAME, senderName)
        intent.putExtra(Constants.Notification.TYPE, type)
        // Pass the intent to PendingIntent to start the
        // next Activity
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Create a Builder object using NotificationCompat
        // class. This will allow control over all the flags
       /* var builder: NotificationCompat.Builder = NotificationCompat.Builder(
            ApplicationProvider.getApplicationContext<Context>(),
            channel_id
        )*/
        val context = applicationContext
        var builder: NotificationCompat.Builder = NotificationCompat.Builder(
            context, // use `this` if within a Service
            channel_id
        )
            .setSmallIcon(R.drawable.meetupsfellow_transpatent)
            .setAutoCancel(true)
            .setVibrate(
                longArrayOf(
                    1000, 1000, 1000,
                    1000, 1000
                )
            )
            .setOnlyAlertOnce(true)
            .setContentIntent(pendingIntent)

        // A customized design for the notification can be
        // set only for Android versions 4.1 and above. Thus
        // condition for the same is checked here.
        builder = builder.setContent(
            getCustomDesign(title, message, img,context)
        )
        // Create an object of NotificationManager class to
        // notify the
        // user of events that happen in the background.
        val notificationManager = getSystemService(
            Context.NOTIFICATION_SERVICE
        ) as NotificationManager?
        // Check if the Android Version is greater than Oreo
        if (Build.VERSION.SDK_INT
            >= Build.VERSION_CODES.O
        ) {
            val notificationChannel = NotificationChannel(
                channel_id,  getString(R.string.default_notification_channel_name),
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager!!.createNotificationChannel(
                notificationChannel
            )
        }
        // Generate a unique notification ID
        val notificationId = generateUniqueNotificationId(type, convoID, senderId)
        notificationManager!!.notify(notificationId, builder.build())

       // updateBadgeCount(context, 10)
       // ShortcutBadger.applyCount(context, 21)
    }

    // Function to generate a unique notification ID
    fun generateUniqueNotificationId(type: String, convoID: String, senderId: String): Int {
        return when (type) {
            "userChat" -> convoID.hashCode()
            "profile-like" -> senderId.hashCode()
            else -> (type + convoID + senderId).hashCode()
        }
    }

    // Method to get the custom Design for the display of
    // notification.
    private suspend fun getCustomDesign(
        title: String,
        message: String,
        img: String?,
        context: Context
    ): RemoteViews {
        val remoteViews = RemoteViews(
           applicationContext.packageName ,
            R.layout.new_notification
        )
        remoteViews.setTextViewText(R.id.title, title)
        remoteViews.setTextViewText(R.id.message, message)
        remoteViews.setImageViewResource(
            R.id.icon,
            R.drawable.meetupsfellow_transpatent
        )

        if (img != null && img.isNotEmpty()){
            img.let {
                val bitmap = loadBitmapFromUrl(context, it)
                bitmap?.let {
                    remoteViews.setImageViewBitmap(R.id.icon, it)
                }
            }
        }


        return remoteViews
    }

    private suspend fun loadBitmapFromUrl(context: Context, url: String): Bitmap? {
        return withContext(Dispatchers.IO) {
            try {
                Glide.with(context)
                    .asBitmap()
                    .load(url)
                    .submit()
                    .get()
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }


    fun updateBadgeCount(context: Context, count: Int) {
        if (ShortcutBadger.isBadgeCounterSupported(context)) {
            Log.d("Badge#", "Badge counter is supported")
        } else {
            Log.d("Badge#", "Badge counter is not supported")
        }

        val success = ShortcutBadger.applyCount(context, count)
        if (!success) {
            Log.e("Badge#", "Failed to set badge count")
        }
    }
}
