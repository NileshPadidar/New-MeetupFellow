//package com.oit.reegur.developement.implementation
//
//import android.content.Context
//import android.content.Intent
//import android.util.Log
//import android.widget.Toast
//import com.oit.datingondl.R
//import com.oit.datingondl.application.ArchitectureApp
//import com.oit.reegur.constants.ConstantsImage
//import com.oit.reegur.developement.interfaces.ChatUtil
//import com.oit.reegur.global.interfaces.StartFailedListener
//import com.oit.reegur.log.LogManager
//import com.oit.datingondl.mvp.view.activities.CallActivity
//import com.oit.datingondl.mvp.view.adapters.ChatAdapter
//import com.oit.datingondl.mvp.view.models.SaveChatMessages
//import com.sinch.android.rtc.*
//import com.sinch.android.rtc.calling.Call
//import com.sinch.android.rtc.calling.CallClient
//import com.sinch.android.rtc.calling.CallClientListener
//import com.sinch.android.rtc.messaging.*
//import pub.devrel.easypermissions.EasyPermissions
//
///**
// * Created by windows on 01-11-2017.
// */
//class ChatUtilImpl(private val context: Context) : ChatUtil {
//
//    private val TAG = ChatUtilImpl::class.java.simpleName
//
//    private var mSinchClient: SinchClient? = null
//
//    private var mListener: StartFailedListener? = null
//
//    private var sinchServiceInterface: SinchServiceInterface? = null
//    private var saveChatMessages = SaveChatMessages()
//
//    override val mSocket: SinchClient?
//        get() = mSinchClient
//
//    override fun initiateSocket(userId: String, userName: String, mListener: StartFailedListener) {
//        if (userId.isNotEmpty()) {
//            if (mSinchClient == null) {
//                if (EasyPermissions.hasPermissions(context, *ConstantsImage.Permissions.CALLING)) {
//                    this.mListener = mListener
//                    sinchServiceInterface = SinchServiceInterface()
//                    mSinchClient = Sinch.getSinchClientBuilder().context(context)
//                            .userId(userId)
//                            .applicationKey(ArchitectureApp.instance!!.getString(R.string.sinch_key))
//                            .applicationSecret(ArchitectureApp.instance!!.getString(R.string.sinch_secret))
//                            .environmentHost(ArchitectureApp.instance!!.getString(R.string.sinch_environment)).build()
//
//                    mSinchClient!!.setSupportMessaging(true)
//                    mSinchClient!!.setSupportCalling(true)
//                    mSinchClient!!.setSupportManagedPush(true)
//                    mSinchClient!!.setSupportPushNotifications(true)
//                    mSinchClient!!.setPushNotificationDisplayName(userName)
//                    mSinchClient!!.startListeningOnActiveConnection()
//                    mSinchClient!!.setSupportActiveConnectionInBackground(true)
//                    mSinchClient!!.checkManifest()
//
//                    mSinchClient!!.addSinchClientListener(MySinchClientListener())
//                    mSinchClient!!.callClient.setRespectNativeCalls(false)
//                    mSinchClient!!.callClient.addCallClientListener(SinchCallClientListener())
//                    mSinchClient!!.messageClient.addMessageClientListener(messageListener)
//                    mSinchClient!!.start()
//
//                }
//
//            } else {
//                mListener.onStarted(sinchServiceInterface!!)
//            }
//        }
//    }
//
//    @Suppress("OverridingDeprecatedMember")
//    private inner class MySinchClientListener : SinchClientListener {
//
//        override fun onClientFailed(client: SinchClient, error: SinchError) {
//            universalToast("SinchClient failed : $error")
//            mSinchClient!!.messageClient.removeMessageClientListener(messageListener)
//            mListener!!.onStartFailed(error)
//            mSinchClient!!.terminate()
//            mSinchClient = null
//        }
//
//        override fun onClientStarted(client: SinchClient) {
//            Log.d(TAG, "SinchClient started")
//            mSinchClient!!.messageClient.addMessageClientListener(messageListener)
//            mListener!!.onStarted(sinchServiceInterface!!)
//        }
//
//        override fun onClientStopped(client: SinchClient) {
//            Log.d(TAG, "SinchClient stopped")
//        }
//
//        override fun onLogMessage(level: Int, area: String, message: String) {
//            when (level) {
//                Log.DEBUG -> LogManager.logger.d(area, message)
//                Log.ERROR -> LogManager.logger.e(area, message)
//                Log.INFO -> LogManager.logger.i(area, message)
//                Log.VERBOSE -> LogManager.logger.v(area, message)
//                Log.WARN -> LogManager.logger.w(area, message)
//            }
//        }
//
//        override fun onRegistrationCredentialsRequired(client: SinchClient,
//                                                       clientRegistration: ClientRegistration) {
//        }
//    }
//
//    private inner class SinchCallClientListener : CallClientListener {
//
//        override fun onIncomingCall(callClient: CallClient, call: Call) {
//            val intent = Intent(context, CallActivity::class.java)
//            intent.putExtra(ConstantsImage.IntentData.CALL_ID, call.callId)
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//            context.startActivity(intent)
//        }
//    }
//
//
//    private val messageListener: MessageClientListener = object : MessageClientListener {
//
//        override fun onIncomingMessage(messageClient: MessageClient?, message: Message?) {
//            saveChatMessages.saveMessagesToLocal(message, ChatAdapter.MESSAGE_INCOMING)
//        }
//
//        override fun onMessageFailed(messageClient: MessageClient?, message: Message?, failureInfo: MessageFailureInfo?) {
//            val sb = StringBuilder()
//            sb.append("Sending failed: ")
//                    .append(failureInfo!!.sinchError.message)
//            Toast.makeText(ArchitectureApp.instance, "$sb", Toast.LENGTH_SHORT).show()
//        }
//
//        override fun onShouldSendPushData(messageClient: MessageClient?, message: Message?, list: MutableList<PushPair>?) {
//        }
//
//        override fun onMessageSent(messageClient: MessageClient?, message: Message?, recipientId: String?) {
//            saveChatMessages.saveMessagesToLocal(message, ChatAdapter.MESSAGE_OUTGOING)
//        }
//
//        override fun onMessageDelivered(messageClient: MessageClient?, messageDeliveryInfo: MessageDeliveryInfo?) {
//        }
//    }
//
//    inner class SinchServiceInterface {
//
//        fun stopClient() {
//            stop()
//        }
//
//        fun callUser(userId: String): Call? {
//            return if (mSinchClient == null) {
//                null
//            } else mSinchClient!!.callClient.callUser(userId)
//        }
//
//        fun getCall(callId: String): Call {
//            return mSinchClient!!.callClient.getCall(callId)
//        }
//
//        fun enableSpeaker() {
//            if (mSinchClient != null) {
//                mSinchClient!!.audioController.enableSpeaker()
//            }
//        }
//
//        fun disableSpeaker() {
//            if (mSinchClient != null) {
//                mSinchClient!!.audioController.disableSpeaker()
//            }
//        }
//
//        fun sendMessage(recipientUserId: String, userName: String, textBody: String) {
//            this@ChatUtilImpl.sendMessage(recipientUserId, userName, textBody)
//        }
//    }
//
//    private fun stop() {
//        if (mSinchClient != null) {
//            sinchServiceInterface = null
//            mSinchClient!!.terminate()
//            mSinchClient = null
//        }
//    }
//
//    private fun isStarted(): Boolean {
//        return mSinchClient != null && mSinchClient!!.isStarted
//    }
//
//    private fun sendMessage(recipientUserId: String, userName: String, textBody: String) {
//        if (isStarted()) {
//            val message = WritableMessage(recipientUserId, textBody)
//            mSinchClient!!.setPushNotificationDisplayName(userName)
//            mSinchClient!!.messageClient.send(message)
//        } else {
//            universalToast("Client not running")
//        }
//    }
//
//    private fun universalToast(message: String) {
//        Toast.makeText(ArchitectureApp.instance, message, Toast.LENGTH_SHORT).show()
//    }
//}