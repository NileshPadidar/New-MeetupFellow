package com.connect.meetupsfellow.mvp.view.activities

import VideoPlayerExoPlayer2181Impl
import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.drawable.ColorDrawable
import android.media.MediaScannerConnection
import android.media.ThumbnailUtils
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.provider.MediaStore
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.exifinterface.media.ExifInterface
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.giphy.sdk.core.models.Media
import com.giphy.sdk.tracking.isVideo
import com.giphy.sdk.ui.GPHContentType
import com.giphy.sdk.ui.GPHSettings
import com.giphy.sdk.ui.Giphy
import com.giphy.sdk.ui.themes.GPHTheme
import com.giphy.sdk.ui.themes.GridType
import com.giphy.sdk.ui.utils.videoUrl
import com.giphy.sdk.ui.views.GiphyDialogFragment
import com.google.android.gms.tasks.*
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.google.gson.Gson
import com.connect.meetupsfellow.R
import com.connect.meetupsfellow.application.ArchitectureApp
import com.connect.meetupsfellow.components.activitytouch.CustomAppActivityCompatViewImpltouch
import com.connect.meetupsfellow.constants.Constants
import com.connect.meetupsfellow.constants.ConstantsApi
import com.connect.meetupsfellow.databinding.ActivityForgotPasswordBinding
import com.connect.meetupsfellow.databinding.ActivityGroupChatBinding
import com.connect.meetupsfellow.developement.interfaces.SharedPreferencesUtil
//import com.connect.meetupsfellow.global.utils.DownloadsFiles
import com.connect.meetupsfellow.global.utils.Path
import com.connect.meetupsfellow.global.view.DownloadsFiles
import com.connect.meetupsfellow.global.view.DownloadsImage
import com.connect.meetupsfellow.global.view.easyphotopicker.ConstantsImage
import com.connect.meetupsfellow.global.view.easyphotopicker.DefaultCallback
import com.connect.meetupsfellow.global.view.easyphotopicker.EasyImage
import com.connect.meetupsfellow.log.LogManager
import com.connect.meetupsfellow.mvp.connector.activity.ChatConnector
import com.connect.meetupsfellow.mvp.presenter.activity.ChatPresenter
import com.connect.meetupsfellow.mvp.view.adapter.RecycleViewGroupChatAdpter
import com.connect.meetupsfellow.mvp.view.dialog.UploadDialogCustom
import com.connect.meetupsfellow.mvp.view.model.ChatModel
import com.connect.meetupsfellow.mvp.view.model.ConversationModel
import com.connect.meetupsfellow.mvp.view.model.RecycleModel
import com.connect.meetupsfellow.retrofit.request.Data
import com.connect.meetupsfellow.retrofit.request.Notification
import com.connect.meetupsfellow.retrofit.request.NotificationModel
import com.connect.meetupsfellow.retrofit.request.RequestSendNotification
import com.connect.meetupsfellow.retrofit.response.CommonResponse
import com.connect.meetupsfellow.retrofit.response.ResponseUserData
import com.connect.meetupsfellow.service.VideoCache
/*import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.activity_group_chat.*
import kotlinx.android.synthetic.main.activity_group_chat.etChat
import kotlinx.android.synthetic.main.activity_group_chat.ivAttachment
import kotlinx.android.synthetic.main.activity_group_chat.ivSend
import kotlinx.android.synthetic.main.item_block.*
import kotlinx.android.synthetic.main.layout_image_user_new.*
import kotlinx.android.synthetic.main.layout_loading.*
import kotlinx.android.synthetic.main.layout_toolbar_chat.*
import kotlinx.android.synthetic.main.layout_toolbar_chat.ivUserImage
import kotlinx.android.synthetic.main.layout_toolbar_with_image.* */
import pub.devrel.easypermissions.AfterPermissionGranted
import timber.log.Timber
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.lang.Exception
import java.math.BigInteger
import java.net.URL
import java.security.SecureRandom
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

class GroupChatActivity : CustomAppActivityCompatViewImpltouch() {

   /* private val presenter = object : ChatConnector.RequiredViewOps {
        override fun showToast(error: String, logout: Boolean?) {
            when (logout) {
                true -> {
                    logout()
                }
            }

            universalToast(error)
            hideProgressView(rl_progress)
        }

        override fun showResponse(response: CommonResponse, type: ConstantsApi) {
            when (type) {
                ConstantsApi.BLOCK -> {
                    finish()
                }

                ConstantsApi.FETCH_CHAT -> {
                    LogManager.logger.i(
                        ArchitectureApp.instance!!.tag,
                        "Keys is : ${Gson().toJson(response.chat)}"
                    )
                    //chatAdapter!!.addAll(response.chat)
//                    when (conversation!!.convoId) {
//                        Constants.Admin.Convo -> when (response.chat.isNotEmpty()) {
//                            true -> {
//                                chatAdapter!!.addAll(response.admin)
//                            }
//                        }
//                        else -> when (response.chat.isNotEmpty()) {
//                            true -> {
//                                chatAdapter!!.addAll(response.chat)
//                            }
//                        }
//                    }

                    //updateChatUi()
                }
                ConstantsApi.SHARE_PRIVATE -> {
                    *//*if (response.isShareStatus > 0) {
                        lockMenuItem?.setIcon(
                            ContextCompat.getDrawable(
                                this@ChatActivity,
                                R.drawable.ic_unlocked
                            )
                        )
                        universalToast(response.message)
                    } else {
                        lockMenuItem?.setIcon(
                            ContextCompat.getDrawable(
                                this@ChatActivity,
                                R.drawable.ic_locked_gray
                            )
                        )
                    }*//*
                }
                else -> {
                }
            }
            hideProgressView(rl_progress)
        }
    }*/

    var groupId = ""
    var userId = ""
    var isReplying = false
    var replyTo = ""
    var replyMsg = ""
    var replyMsgTime = ""
    var chatId = ""
    var chatImg = ""
    var isSending = false
    private var linearLayoutManager: LinearLayoutManager? = null
    var recycleViewGroupChatAdpter: RecycleViewGroupChatAdpter? = null
    lateinit var recycleModel: ArrayList<RecycleModel>
    var groupMembers = ArrayList<String>()
    private var uploadDialog: UploadDialogCustom? = null
    private var photoFile: File? = null
    private var photoFileUri: Uri? = null
    private var videoFileUri: Uri? = null
    var onCurrentActivity = true
    var message = ""
    private val mTypingHandler = Handler()
    private var mTyping = false
    private val typingTimer = 600L
    var msgToCopy = ""
    var lastChatSize = 0
    var lastReadIndex = 0
    var unSeenCount = 0
    var isSendingImg = false
    var isSendingVideo = false
    var imgAbsolutePath = ""
    private var videoAbsolutePath = ""
    private lateinit var chatDeleteIc: MenuItem
    lateinit var copyMenuItem: MenuItem
    var deleteChatsArr = ArrayList<String>()
    var msgIdArr = ArrayList<String>()
    lateinit var profile: ResponseUserData
    var bitmapVideoThumb : Bitmap? = null
    var settings = GPHSettings(gridType = GridType.waterfall, theme = GPHTheme.Light, stickerColumnCount = 3)
    var contentType = GPHContentType.gif
    val TAG = "Giphy tag"
    private var conversation: ConversationModel? = null
    private var otherUserConversation: ConversationModel? = null
    private var mPresenter: ChatConnector.PresenterOps? = null
     var binding: ActivityGroupChatBinding? = null
    private lateinit var progressBar: LinearLayout

    @Inject
    lateinit var sharedPreferencesUtil: SharedPreferencesUtil

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        component.inject(this@GroupChatActivity)
        binding = ActivityGroupChatBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
       // setContentView(R.layout.activity_group_chat)
        profile = sharedPreferencesUtil.fetchUserProfile()

        Giphy.configure(this, Constants.GIPHY_API_KEY)
        VideoCache.initialize(this, 100 * 1024 * 1024)

        groupId = intent.getStringExtra("groupId").toString()
        userId = profile.userId
        getSharedPreferences("UserId", MODE_PRIVATE).edit().putString("UserId", userId).apply()

    /*    linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        linearLayoutManager!!.stackFromEnd = true
        rvGroupChat.layoutManager = linearLayoutManager

        ivBack.setOnClickListener { onBackPressed() }

        chatDetails.visibility = View.GONE
        addGroupMembers.visibility = View.VISIBLE

        chatDetails.setOnClickListener {

            val bundle = Bundle()
            bundle.putString("groupId", groupId)

            switchActivity(Constants.Intent.GroupChatDetails, false, bundle)
        }

        tvUserStatus.visibility = View.GONE

        Log.d("groupID", groupId.toString())

        //checkInGroup()

        //setGroupNameAndImg()
        getGroupMembersList()
        fetchMsgs()
        readMsgs()

        tvUsername.setOnClickListener {

            val bundle = Bundle()
            bundle.putString("groupId", groupId)

            switchActivity(Constants.Intent.GroupChatDetails, false, bundle)
        }

        ivUserImage.setOnClickListener {

            val bundle = Bundle()
            bundle.putString("groupId", groupId)

            switchActivity(Constants.Intent.GroupChatDetails, false, bundle)
        }

        ivSend.setOnClickListener {

            isSending = true
            Log.e("Send#$","Clickkkk")
            if (isSendingImg) {
                Log.e("Send#$","Send_Img")
                etChat.isEnabled = false

                sendImg(photoFileUri)

            } else if (isSendingVideo) {
                Log.e("Send#$","Send_Video")
                sendVideoMsg(videoFileUri)

            } else {

                etChat.isEnabled = true

                if (etChat.text.isNotEmpty() && etChat.text.isNotBlank()) {
                    Log.e("Send#$","Send_MSG")
                    sendMsg(etChat.text.toString())
                }else{
                    Log.e("Send#$","Send_Nothing")
                }
            }

        }

        etChat.afterTextChanged { chat ->
            message = chat
            ivSend.isClickable = message.isNotEmpty()
            ivSend.isEnabled = message.isNotEmpty()

            if (etChat.text.isEmpty())
                return@afterTextChanged

            when (ChatActivity.chatExist) {
                true -> {
                    if (!mTyping) {
                        mTyping = true
                        //isTyping(mTyping)
                    }

                    mTypingHandler.removeCallbacks(onTypingTimeout)
                    mTypingHandler.postDelayed(onTypingTimeout, typingTimer)
                }
            }
        }

        ivAttachment.setOnClickListener {

            selectImagePopup()
        }

        addGroupMembers.setOnClickListener {

            val bundle = Bundle()

            bundle.putStringArrayList("members", groupMembers)
            bundle.putString("groupId", groupId)

            switchActivity(Constants.Intent.AddGroupMembers, false, bundle)
        }

        cancelReplyChatG.setOnClickListener {

            replyChatLayG.visibility = View.GONE
            replyLayG.visibility = View.GONE
            sendingImgLayG.visibility = View.GONE

            isReplying = false
            isSendingImg = false
            isSendingVideo = false

            videoFileUri = null
            photoFileUri = null

            replyTo = ""
            replyMsg = ""
            replyMsgTime = ""
            chatId = ""
            chatImg = ""
        }

        deleteChat.setOnClickListener {

            showDeleteMasgsDialog()
        }

        copyChat.setOnClickListener {

            val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("label", msgToCopy)
            clipboard.setPrimaryClip(clip)

            Toast.makeText(this@GroupChatActivity, "Message copied to clipboard", Toast.LENGTH_SHORT).show()

        }

        rvGroupChat.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                //recyclerViewState = rvChat.layoutManager?.onSaveInstanceState()!!

                if (newState == RecyclerView.SCROLL_STATE_IDLE) {

                    Log.d("Scroll IndexV", "LastIndex")
                }

            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                //rvChatScrollPos += dy
                Log.d("Scroll Index", dy.toString() + "   " + dx.toString())

                if (recycleModel.lastIndex > 10){

                    if (linearLayoutManager!!.findLastVisibleItemPosition() != recycleModel.lastIndex) {

                        Log.d("Scroll IndexV", recycleModel[0].lastMsg.toString())

                        //showPopupWindow(rvChat)

                        //moreChat()
                        //fetchChatMsgs()

                        scrollToStartLayG.visibility = View.VISIBLE

                        if (unSeenCount != 0) {

                            newMsgCountLayG.visibility = View.VISIBLE
                            //newMsgCount.text = unSeenCount.toString()

                        } else {

                            newMsgCountLayG.visibility = View.GONE
                        }

                    } else {

                        unSeenCount = 0
                        scrollToStartLayG.visibility = View.GONE
                    }
                }
            }
        })

        scrollToStartLayG.setOnClickListener {

            rvGroupChat.smoothScrollToPosition(recycleViewGroupChatAdpter!!.itemCount - 1)
        }

        giphyBtnG.setOnClickListener {

            val dialog = GiphyDialogFragment.newInstance(
                settings.copy(selectedContentType = contentType),
                videoPlayer = { playerView, repeatable, showCaptions ->
                    VideoPlayerExoPlayer2181Impl(playerView, repeatable, showCaptions)
                }
            )
            dialog.gifSelectionListener = getGifSelectionListener()
            dialog.show(supportFragmentManager, "gifs_dialog")
        }*/

    }

    private fun getGifSelectionListener() = object : GiphyDialogFragment.GifSelectionListener {
        override fun onGifSelected(media: Media, searchTerm: String?, selectedContentType: GPHContentType) {
            Timber.tag(TAG).d("onGifSelected")
            Log.d(TAG, "onGifSelected")
            if (selectedContentType == GPHContentType.clips && media.isVideo) {
                /*messageItems.forEach {
                    (it as? ClipItem)?.autoPlay = false
                }*/
                // messageItems.add(ClipItem(media, Author.Me, autoPlay = true))
                Log.d(TAG, media.id)
                Log.d(TAG, "Clip Item")

            } else {
                Log.d(TAG, media.videoUrl.toString())
                Log.d(TAG, media.bitlyGifUrl.toString())
                Log.d(TAG, media.contentUrl.toString())
                Log.d(TAG, media.sourcePostUrl.toString())
                Log.d(TAG, media.embedUrl.toString())
                Log.d(TAG, "Gif Item")

               /// sendGifMsg(media)

                // messageItems.add(GifItem(media, Author.Me))
            }
            //feedAdapter?.notifyItemInserted(messageItems.size - 1)
            contentType = selectedContentType
        }

        override fun onDismissed(selectedContentType: GPHContentType) {
            //Timber.tag(TAG).d("onDismissed")
            Log.d(TAG, "onDismissed")
            contentType = selectedContentType
        }

        override fun didSearchTerm(term: String) {
            //Timber.tag(TAG).d("didSearchTerm $term")
            Log.d(TAG, "didSearchTerm $term")
        }
    }

    /*private fun sendGifMsg(media: Media) {
        Log.e("Tag", "sendGifMsg: "+media.id);
        isSending = true

        val cDb = FirebaseDatabase.getInstance(Constants.Firebase.databaseUrl).getReference("MeetUpsLive")
            .child("ChatGroup")

        cDb.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snap: DataSnapshot) {

                if (snap.exists()) {

                    val chatId = System.currentTimeMillis().toString()

                    val userChatDb = cDb.child(groupId)
                    val chatsDb = userChatDb.child("Chats")
                    val msgDb = chatsDb.child(chatId)
                    *//*userChatDb.child("lastMsg").setValue("GIF")
                    userChatDb.child("senderId").setValue(userId)
                    userChatDb.child("senderName").setValue(profile.name)
                    userChatDb.child("msgTime").setValue(System.currentTimeMillis().toString())

                    msgDb.child("msg").setValue("")
                    msgDb.child("senderId").setValue(userId)
                    msgDb.child("senderName").setValue(profile.name)
                    msgDb.child("receiverId").setValue(otherUserId)
                    msgDb.child("receiverName").setValue(otherUserName)
                    msgDb.child("msgTime").setValue(chatId)
                    msgDb.child("isRead").setValue(false)
                    msgDb.child("imgUrl").setValue("")
                    msgDb.child("msgClearedBy").child(userId).setValue("no")
                    msgDb.child("msgClearedBy").child(otherUserId).setValue("no")
                    msgDb.child("GifId").setValue(media.id)*//*
                    //fetchChatMsgs()

                    userChatDb.child("LastMsg").setValue("GIF")
                    userChatDb.child("SenderId").setValue(userId)
                    userChatDb.child("SenderName").setValue(profile.name)
                    userChatDb.child("LastMsgTime").setValue(System.currentTimeMillis().toString())
                    val readDb = msgDb.child("readBy").child(userId)
                    readDb.child("read").setValue("yes")
                    msgDb.child("msg").setValue("")
                    msgDb.child("senderId").setValue(userId)
                    msgDb.child("senderName").setValue(profile.name)
                    msgDb.child("msgTime").setValue(System.currentTimeMillis().toString())
                    msgDb.child("videoUrl").setValue("")
                    msgDb.child("GifId").setValue(media.id)
                    for (i in groupMembers) {

                        msgDb.child("msgClearedBy").child(i).setValue("no")
                    }

                    recycleModel.add(
                        RecycleModel(
                            profile.name,
                            "",
                            System.currentTimeMillis().toString(),
                            userId,
                            false,
                            "",
                            "",
                            replyTo,
                            replyMsg,
                            chatId,
                            "",
                            "",
                            "",
                            "",
                            "",
                            media.id
                        )
                    )

                    Log.e(TAG, "GifItem_sendGifMsg-Group: "+recycleModel.size)


                    if (recycleViewGroupChatAdpter == null || isSending) {

                        recycleViewGroupChatAdpter =
                            RecycleViewGroupChatAdpter(this@GroupChatActivity)

                        recycleViewGroupChatAdpter!!.addChats(recycleModel)

                        rvGroupChat.adapter = recycleViewGroupChatAdpter

                        Handler().postDelayed(object : Runnable {

                            override fun run() {
                                isSending = false
                            }
                        }, 300)

                        lastChatSize = recycleModel.size
                        isSendingImg = false

                    } else {

                        recycleViewGroupChatAdpter!!.addChats(recycleModel)

                        recycleViewGroupChatAdpter!!.notifyItemInserted(recycleModel.lastIndex)
                    }
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })

    }

    private fun sendVideoMsg(videoFileUri: Uri?) {

        message = "video"
        sendingImgLayG.visibility = View.GONE
        replyChatLayG.visibility = View.GONE

        isSending = true

        val chatId = System.currentTimeMillis().toString()

        Log.e("UploadImgPath", videoFileUri!!.path.toString())
        val profile = sharedPreferencesUtil.fetchUserProfile()

        val cDb = FirebaseDatabase.getInstance(Constants.Firebase.databaseUrl).getReference("MeetUpsLive")
            .child("ChatGroup").child(groupId).child("Chats")

        val storageRefThumb = FirebaseStorage.getInstance().getReference("chats").child("chatVideos").child("ThumbnailUrl")
            .child(System.currentTimeMillis().toString() + ".jpg")

        val storageRef = FirebaseStorage.getInstance().getReference("chats").child("chatVideos").child("Original")
            .child(System.currentTimeMillis().toString() + ".mp4")

        val uploadTask: UploadTask = storageRef.putFile(videoFileUri)

        val msgDb = cDb.child(chatId)

        val chatDb = FirebaseDatabase.getInstance(Constants.Firebase.databaseUrl).getReference("MeetUpsLive")
            .child("ChatGroup")
        val userChatDb = chatDb.child(groupId)

        var thumbImgUrl = ""

        val f = File(this.cacheDir, "thumb");
        f.createNewFile();

        Log.e("chatGroupVideo", "bitmapVideoThumb- "+bitmapVideoThumb)
//Convert bitmap to byte array
        val bitmap = bitmapVideoThumb
        val bos = ByteArrayOutputStream()
        bitmap!!.compress(Bitmap.CompressFormat.JPEG, 2 *//*ignored for PNG*//*, bos)
        val bitmapdata = bos.toByteArray()

//write the bytes in file
        val fos = FileOutputStream(f)
        fos.write(bitmapdata)
        fos.flush()
        fos.close()

        Log.e("ThumbNail", f.absolutePath)

        val uploadTaskThumb: UploadTask = storageRefThumb.putFile(Uri.fromFile(f))

        uploadTaskThumb.continueWithTask(object :
            Continuation<UploadTask.TaskSnapshot, Task<Uri>> {
            override fun then(task: Task<UploadTask.TaskSnapshot>): Task<Uri> {

                if (!task.isSuccessful) {

                    throw task.exception!!
                }
                return storageRefThumb.downloadUrl
            }


        }).addOnCompleteListener(object : OnCompleteListener<Uri> {
            override fun onComplete(task: Task<Uri>) {

                if (task.isSuccessful)  {

                    msgDb.child("thumbImg").setValue(task.result.toString())

                    thumbImgUrl = task.result.toString()
                    Log.e("ThumbNailURL#: ", thumbImgUrl)
                    Log.e("videoAbsolutePath$$: ", videoAbsolutePath)

                    recycleModel.add(
                        RecycleModel(
                            profile.name,
                            "",
                            System.currentTimeMillis().toString(),
                            userId,
                            false,
                            "",
                            "meetUpsFellow",
                            "",
                            "",
                            chatId,
                            "",
                            "",
                            videoAbsolutePath,
                            task.result.toString(),
                            "",
                            ""
                        )
                    )

                    if (recycleViewGroupChatAdpter == null) {

                        recycleViewGroupChatAdpter =
                            RecycleViewGroupChatAdpter(this@GroupChatActivity)

                        recycleViewGroupChatAdpter!!.addChats(recycleModel)

                        rvGroupChat.adapter = recycleViewGroupChatAdpter

                        Handler().postDelayed(object : Runnable {

                            override fun run() {
                                isSending = false
                            }
                        }, 300)

                        lastChatSize = recycleModel.size
                        isSendingImg = false

                    } else {

                        recycleViewGroupChatAdpter!!.addChats(recycleModel)

                        recycleViewGroupChatAdpter!!.notifyItemInserted(recycleModel.lastIndex)
                    }
                  *//*  recycleViewGroupChatAdpter!!.addChats(recycleModel)

                    recycleViewGroupChatAdpter!!.notifyItemInserted(recycleModel.lastIndex)*//*

                    userChatDb.child("LastMsg").setValue("video")
                    userChatDb.child("SenderId").setValue(userId)
                    userChatDb.child("SenderName").setValue(profile.name)
                    userChatDb.child("LastMsgTime").setValue(System.currentTimeMillis().toString())
                    val readDb = msgDb.child("readBy").child(userId)
                    readDb.child("read").setValue("yes")
                    msgDb.child("msg").setValue("")
                    msgDb.child("senderId").setValue(userId)
                    msgDb.child("senderName").setValue(profile.name)
                    msgDb.child("msgTime").setValue(System.currentTimeMillis().toString())
                    msgDb.child("videoUrl").setValue(task.result.toString())

                    for (i in groupMembers) {

                        if (i == userId){

                            msgDb.child("mediaDeviceUrlOf").child(userId).setValue(videoAbsolutePath)
                        }else {

                            msgDb.child("mediaDeviceUrlOf").child(i).setValue("")
                        }

                        msgDb.child("msgClearedBy").child(i).setValue("no")
                    }

                }
            }
        })


        //isSendingImg = false

        uploadTask.continueWithTask(object :
            Continuation<UploadTask.TaskSnapshot, Task<Uri>> {
            override fun then(task: Task<UploadTask.TaskSnapshot>): Task<Uri> {

                if (!task.isSuccessful) {

                    throw task.exception!!
                }
                return storageRef.downloadUrl
            }


        }).addOnCompleteListener(object : OnCompleteListener<Uri> {
            override fun onComplete(task: Task<Uri>) {

                if (task.isSuccessful) {

                    //closeUploadDialog()
                    isSending = true
                    Log.e("VideoUrl: ", task.result.toString())
                    Log.e("videoAbsolutePath$$2: ", videoAbsolutePath)
                    recycleModel.add(
                        RecycleModel(
                            profile.name,
                            "",
                            System.currentTimeMillis().toString(),
                            userId,
                            false,
                            "",
                            "",
                            "",
                            "",
                            chatId,
                            "",
                            "",
                            videoAbsolutePath,
                            thumbImgUrl,
                            task.result.toString(),
                            ""
                        )
                    )


                    if (recycleViewGroupChatAdpter == null) {

                        recycleViewGroupChatAdpter =
                            RecycleViewGroupChatAdpter(this@GroupChatActivity)

                        recycleViewGroupChatAdpter!!.addChats(recycleModel)

                        rvGroupChat.adapter = recycleViewGroupChatAdpter

                        Handler().postDelayed(object : Runnable {

                            override fun run() {
                                isSending = false
                            }
                        }, 300)

                        lastChatSize = recycleModel.size
                        isSendingImg = false

                    } else {

                        recycleViewGroupChatAdpter!!.updateChats(recycleModel[recycleModel.lastIndex])

                        recycleViewGroupChatAdpter!!.notifyItemChanged(
                            recycleModel.lastIndex,
                            recycleModel[recycleModel.lastIndex]
                        )
                    }

                    //val userChatDb = chatDb.child(chatRoomId)
                    userChatDb.child("LastMsg").setValue("video")
                    userChatDb.child("SenderId").setValue(userId)
                    userChatDb.child("SenderName").setValue(profile.name)
                    userChatDb.child("LastMsgTime").setValue(System.currentTimeMillis().toString())
                    //val msgDb = cDb.child(System.currentTimeMillis().toString())
                    val readDb = msgDb.child("readBy").child(userId)
                    readDb.child("read").setValue("yes")
                    msgDb.child("msg").setValue("")
                    msgDb.child("senderId").setValue(userId)
                    msgDb.child("senderName").setValue(profile.name)
                    msgDb.child("msgTime").setValue(System.currentTimeMillis().toString())
                    msgDb.child("videoUrl").setValue(task.result.toString())

                    for (i in groupMembers) {

                        if (i == userId){

                            msgDb.child("mediaDeviceUrlOf").child(userId).setValue(videoAbsolutePath)
                        }else {

                            msgDb.child("mediaDeviceUrlOf").child(i).setValue("")
                        }

                        msgDb.child("msgClearedBy").child(i).setValue("no")
                    }

                    isSendingImg = false

                    etChat.isEnabled = true
                    this@GroupChatActivity.videoFileUri = null
                }
            }
        })

        //sendMsgNotification("video")
    }*/


    private val onTypingTimeout = Runnable {

        if (!mTyping) return@Runnable
        LogManager.logger.i(ArchitectureApp.instance!!.tag, "Token is Typing TimeOut :")
        mTyping = false
        //isTyping(mTyping)
    }

    private fun showDeleteMasgsDialog() {

        val dialog = Dialog(this)

        dialog.setCancelable(true)

        val view = layoutInflater.inflate(R.layout.custom_exit_dialog, null)

        dialog.setContentView(view)

        dialog.window!!.attributes.windowAnimations = R.style.DialogAnimation

        if (dialog.window != null) {
            dialog.window!!.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }
        dialog.window!!.setGravity(Gravity.CENTER)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val dialogHead = view.findViewById<TextView>(R.id.dialog_title)
        val dialogContent = view.findViewById<TextView>(R.id.dialog_content)
        val cancelBtn = view.findViewById<Button>(R.id.noExitBtn)
        val confirm = view.findViewById<Button>(R.id.yesExitBtn)

        dialogHead.text = "Delete Messages"
        dialogContent.text = "Only your selected messages will be deleted for everyone."

        cancelBtn.setOnClickListener {

            dialog.dismiss()

        }

        confirm.setOnClickListener {

          ///  deleteChat()
            dialog.dismiss()
        }

        dialog.show()
    }

   /* fun downloadAndSaveImage(urlImg: String, id: String) {

        var url: URL? = null

        //Create Path to save Image
        val path =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES + "/MeetUpsFellow Chat Images")
        var isImgExists = false

        MediaScannerConnection.scanFile(
            applicationContext, arrayOf(Constants.ImgPath),
            null
        ) { path, uri ->
            Log.d("TestImgChat-Group", "file $path Image exists $uri")

            if (uri != null) {

                isImgExists = true

                //testImgChat.setImageURI(uri)
            }
        }

        //if (!isImgExists) {
        Log.e("TestImgChat_Group", "file $urlImg ,Image Id: $id")

        val imgPathDB = FirebaseDatabase.getInstance(Constants.Firebase.databaseUrl).getReference("MeetUpsLive")
            .child("ChatGroup").child(groupId).child("Chats").child(id)

        DownloadsImage(this, imgPathDB, userId).execute(urlImg)

        //testImgChat.setImageURI(imgUri)
        // }
    }*/

     fun downloadAndSaveImage(urlImg: String, id: String, type: Int) {

        var url: URL? = null

        //Create Path to save Image
        val path =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES + "/MeetUpsFellow Chat Images")
        var isImgExists = false

        MediaScannerConnection.scanFile(
            applicationContext, arrayOf(Constants.ImgPath),
            null
        ) { path, uri ->

            Log.e("TestImgChat", "file $path Image exists $uri")

            if (uri != null) {

                isImgExists = true

                //testImgChat.setImageURI(uri)
            }
        }

        //if (!isImgExists) {

        Log.e("TestImgChat_Personal", "file $urlImg ,Id: $id")
        val imgPathDB = FirebaseDatabase.getInstance(Constants.Firebase.databaseUrl).getReference(Constants.Firebase.ChatDB)
            .child("ChatGroup").child(groupId).child("Chats").child(id)

        if (urlImg.contains(".png") || urlImg.contains(".jpg") || urlImg.contains(".jpeg")
            || urlImg.contains(".mp4") || urlImg.contains(".mkv") || type == 2 || type == 4
        ) {
           /* val downloadsFiles = DownloadsFiles(context!!, imgPathDB, userId)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                downloadsFiles.downloadFile(urlImg)
            }else{
                universalToast("You cna't download this file")
            }*/
            DownloadsImage(this, imgPathDB, userId,type).execute(urlImg)

        } else if (urlImg.contains(".pdf") || urlImg.contains(".xls") || urlImg.contains(".xlsx")
            || urlImg.contains(".doc") || urlImg.contains(".docx") || urlImg.contains(".txt") || type == 3
        ) {
           /* val downloadsFiles = DownloadsFiles(context!!, imgPathDB, userId)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                downloadsFiles.downloadFile(urlImg)
            }else{
                universalToast("You cna't download this file")
            }*/
                 DownloadsFiles(this, imgPathDB, userId).execute(urlImg)


        } else {
            Log.e("TestImgChat_Personal", "in Elss_Not_Contain")
        }
        //  testImgChat.setImageURI(imgUri)
        // }
    }

    private fun checkInGroup() {

        val db = FirebaseDatabase.getInstance(Constants.Firebase.databaseUrl).getReference(Constants.Firebase.ChatDB)
            .child("ChatGroup").child(groupId).child("GroupMembers")

        db.child(userId).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snap: DataSnapshot) {

                if (snap.exists()) {

                    Log.d("UserExists", "Yes")
                    getGroupMembersList()

                } else {

                    Log.d("UserExists", "NO")
                    finish()
                }
            }

            override fun onCancelled(p0: DatabaseError) {


            }
        })

    }

    private fun getGroupMembersList() {

        val db = FirebaseDatabase.getInstance(Constants.Firebase.databaseUrl).getReference(Constants.Firebase.ChatDB)
            .child("ChatGroup").child(groupId).child("GroupMembers")

        db.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snap: DataSnapshot) {

                if (snap.exists()) {

                    groupMembers.clear()

                    for (id in snap.children) {

                        groupMembers.add(id.key.toString())
                    }
                }
            }

            override fun onCancelled(p0: DatabaseError) {


            }
        })

    }

    override fun onResume() {
        super.onResume()

       /// setGroupNameAndImg()
        checkInGroup()
        onCurrentActivity = true
        /*var lasttrackTime = sharedPreferencesUtil.featchTimmerTime()
        var currentbackTime: Long = System.currentTimeMillis()
        Log.e("lasttrackTime", lasttrackTime)
        LogManager.logger.i(ArchitectureApp.instance!!.tag, "onResume ::: " + lasttrackTime)

        if (lasttrackTime != "") {
            val currentSeconds = ((currentbackTime - lasttrackTime.toLong()) / 1000).toInt()
            LogManager.logger.i(
                ArchitectureApp.instance!!.tag,
                "onResume ::: " + currentSeconds
            )
            if (currentSeconds > Constants.TimerRefresh.START_TIME_IN_SEC) {
                startActivity(Intent(this, SplashActivity::class.java))
                finish()
            }
        }*/
    }

    override fun onPause() {
        super.onPause()

        onCurrentActivity = false
    }

    override fun onDestroy() {
        super.onDestroy()

        onCurrentActivity = false
    }

    private fun closeUploadDialog() {
        if (uploadDialog != null) {
            if (uploadDialog!!.isShowing) {
                uploadDialog!!.dismiss()
            }
            uploadDialog = null
        }
    }

    private fun showUploadDialog(message: String) {
        closeUploadDialog()
        if (null == uploadDialog) {
            uploadDialog = UploadDialogCustom(this@GroupChatActivity, message)
            uploadDialog!!.show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (resultCode) {
            Activity.RESULT_OK -> {
                when (requestCode) {
                    100 -> {
                        //showUploadDialog("Uploading Image")
                        //uploadDialog?.setCount("1/1")
                        var exif: ExifInterface? = null
                        try {
                            exif = ExifInterface(photoFile!!.path)
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }

                        val orientation = exif!!.getAttributeInt(
                            ExifInterface.TAG_ORIENTATION,
                            ExifInterface.ORIENTATION_UNDEFINED
                        )
                        val bmOptions = BitmapFactory.Options()
                        val imageBitmap = BitmapFactory.decodeFile(photoFile!!.path, bmOptions)
                        val rotatedBitmap = rotateBitmap(imageBitmap, orientation)!!

                        photoFileUri = Uri.fromFile(photoFile)

                        imgAbsolutePath = photoFile!!.absolutePath

                     ///   showSelectedImg(photoFileUri)

                        //uploadFileBitmap(rotatedBitmap)
                    }

                    Constants.ImagePicker.Image -> {
                        //showUploadDialog("Uploading Image")
                        //uploadDialog?.setCount("1/1")
                        //uploadImage(data!!.data!!)
                        photoFileUri = data!!.data


                        val projection = arrayOf(MediaStore.Images.Media.DATA)
                        val selectedImageUri: Uri? = data.data

                        try {
                            val cursor: Cursor? =
                                contentResolver.query(
                                    selectedImageUri!!,
                                    projection,
                                    null,
                                    null,
                                    null
                                )
                            cursor!!.moveToFirst()
                            val columnIndex: Int = cursor.getColumnIndex(projection.get(0))
                            val picturePath: String = cursor.getString(columnIndex)
                            cursor.close()
                            Log.d("Picture Path", picturePath)
                            imgAbsolutePath = picturePath
                        } catch (e: java.lang.Exception) {
                            Log.e("Picture Path Error", e.toString())
                        }

                     ///   showSelectedImg(photoFileUri)
                    }

                    Constants.ImagePicker.Video -> {

                        videoFileUri = data!!.data!!

                     ///   showSelectedImg(videoFileUri)


                    }

                    ConstantsImage.RequestCodes.PICK_PICTURE_FROM_GALLERY -> {
                        imageFromGallery(requestCode, resultCode, data)

                        /*if (data != null && data.data != null) {

                            showUploadDialog("Uploading Image")
                            uploadDialog?.setCount("1/1")

                            photoFileUri = data.data



                           // sendImg(photoFileUri)
                        }*/
                    }
                }
            }

            AutocompleteActivity.RESULT_ERROR -> {
                val status = Autocomplete.getStatusFromIntent(data!!)
                LogManager.logger.i(ArchitectureApp.instance!!.tag, "${status.statusMessage}")
                universalToast("${status.statusMessage}")
            }

        }
    }

    private fun imageFromGallery(requestCode: Int, resultCode: Int, data: Intent?) {
        EasyImage.handleActivityResult(
            requestCode,
            resultCode,
            data,
            this,
            object : DefaultCallback() {
                override fun onImagesPicked(
                    imageFiles: MutableList<File>,
                    source: EasyImage.ImageSource?,
                    type: Int
                ) {

                    for (i in imageFiles.indices) {

                        Log.d("ImagesSelected", imageFiles.get(i).toString())
                    }

                    //showUploadDialog("Uploading Image")
                    var exif: ExifInterface? = null
                    try {
                        exif = ExifInterface(imageFiles[0].path)
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }

                    val orientation = exif!!.getAttributeInt(
                        ExifInterface.TAG_ORIENTATION,
                        ExifInterface.ORIENTATION_UNDEFINED
                    )

                    val bmOptions = BitmapFactory.Options()
                    val imageBitmap = BitmapFactory.decodeFile(imageFiles[0].path, bmOptions)
                    val rotatedBitmap = rotateBitmap(imageBitmap, orientation)!!

                    photoFileUri = Uri.fromFile(imageFiles[0])
                    photoFile = imageFiles[0]

                    val projection = arrayOf(MediaStore.Images.Media.DATA)
                    val selectedImageUri: Uri? = data!!.data

                    try {
                        val cursor: Cursor? =
                            contentResolver.query(selectedImageUri!!, projection, null, null, null)
                        cursor!!.moveToFirst()
                        val columnIndex: Int = cursor.getColumnIndex(projection.get(0))
                        val picturePath: String = cursor.getString(columnIndex)
                        cursor.close()
                        Log.d("Picture Path", picturePath)
                        imgAbsolutePath = picturePath
                    } catch (e: java.lang.Exception) {
                        Log.e("Picture Path Error", e.toString())
                    }


                    //sendImgMsg(photoFileUri)

                  ///  showSelectedImg(photoFileUri)

                    //uploadFileBitmap(rotatedBitmap)
                }

                override fun onCanceled(source: EasyImage.ImageSource, type: Int) {
                    if (source == EasyImage.ImageSource.CAMERA_IMAGE) {
                        EasyImage.lastlyTakenButCanceledPhoto(this@GroupChatActivity)?.delete()
                    }
                }

                override fun onImagePickerError(
                    e: Exception,
                    source: EasyImage.ImageSource,
                    type: Int
                ) {
                    e.printStackTrace()
                }
            })
    }

    public fun replyToChat(from: String, msg: String, msgTime: String, id: String, img : String, imgPath : String) {

        isReplying = true
        val imgMsg = img
       /* replyChatLayG.visibility = View.VISIBLE
        replyLayG.visibility = View.VISIBLE
        sendingImgLayG.visibility = View.GONE

        userReplyNameG.text = from
        replyChatMessageG.text = msg
        replyChatTimeG.text = msgTime*/

       /* if (msg == "image") {

            replyToImgG.visibility = View.VISIBLE
            videoIcG.visibility = View.GONE

            Glide.with(this@GroupChatActivity)
                .load(img)
                .thumbnail(0.01f)
                .override(30, 30)
                .placeholder(R.drawable.meetupsfellow_transpatent)
                .into(replyToImgG)

        } else if (msg == "video"){

            replyToImgG.visibility = View.VISIBLE
            videoIcG.visibility = View.VISIBLE

            Glide.with(this@GroupChatActivity)
                .load(img)
                .thumbnail(0.01f)
                .override(30, 30)
                .placeholder(R.drawable.meetupsfellow_transpatent)
                .into(replyToImgG)

        } else {

            replyToImgG.visibility = View.GONE
            videoIcG.visibility = View.GONE
        }*/

        replyTo = from
        replyMsg = msg
        replyMsgTime = msgTime
        chatId = id
        chatImg = imgMsg
    }

    /*private fun showSelectedImg(photoFileUri: Uri?) {

        //isSendingImg = true

        replyChatLayG.visibility = View.VISIBLE
        sendingImgLayG.visibility = View.VISIBLE
        replyLayG.visibility = View.GONE

        if (videoFileUri != null) {

            videoPlayImgG.visibility = View.VISIBLE

            isSendingVideo = true

            val path = Path.getPath(this@GroupChatActivity, videoFileUri!!)
            Log.i("sss", "From Gallery 1 URI -- $path")
            videoAbsolutePath = path.toString()
            Log.e("sss", "videoPath-- $videoAbsolutePath")
            bitmapVideoThumb =
                ThumbnailUtils.createVideoThumbnail(
                    path.toString(),
                    MediaStore.Video.Thumbnails.MINI_KIND
                )

            sendingImgG.setImageBitmap(bitmapVideoThumb)

        } else {

            isSendingImg = true

            videoPlayImgG.visibility = View.GONE

            sendingImgG.setImageURI(photoFileUri)
        }

    }

    private fun sendImg(photoFileUri: Uri?) {
        message = "image"
        sendingImgLayG.visibility = View.GONE
        replyChatLayG.visibility = View.GONE
        isSending = true

        val currentDateTime = Date().time

        val profile = sharedPreferencesUtil.fetchUserProfile()
        val cDb = FirebaseDatabase.getInstance(Constants.Firebase.databaseUrl).getReference(Constants.Firebase.ChatDB)
            .child("ChatGroup").child(groupId).child("Chats")

        val groupDb = FirebaseDatabase.getInstance(Constants.Firebase.databaseUrl).getReference("MeetUpsLive")
            .child("ChatGroup").child(groupId)

        val storageRef = FirebaseStorage.getInstance().getReference("chatImgs")
            .child(System.currentTimeMillis().toString() + ".jpg")

        val uploadTask: UploadTask = storageRef.putFile(photoFileUri!!)

        val chatDb = cDb.child(currentDateTime.toString())

        val imageFile = photoFile

        var thumbImgUrl = ""

        var thumbBitmap : Bitmap? = null

        *//*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            thumbBitmap = ThumbnailUtils.createImageThumbnail(imageFile!!, Size(100,100), null)
        }*//*
        thumbBitmap = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(imageFile.toString()),100,100)
        Log.e("ThumbNail#@$", "GroupImage- "+thumbBitmap)

        //create a file to write bitmap data
        val f = File(this.cacheDir, "thumb");
        f.createNewFile();

//Convert bitmap to byte array
        val bitmap = thumbBitmap
        val bos = ByteArrayOutputStream()
        bitmap!!.compress(Bitmap.CompressFormat.JPEG, 2 *//*ignored for PNG*//*, bos)
        val bitmapdata = bos.toByteArray()

//write the bytes in file
        val fos = FileOutputStream(f)
        fos.write(bitmapdata)
        fos.flush()
        fos.close()

        Log.d("ThumbNail", f.absolutePath)

        val uploadTaskThumb: UploadTask = storageRef.putFile(Uri.fromFile(f))

        uploadTaskThumb.continueWithTask(object :
            Continuation<UploadTask.TaskSnapshot, Task<Uri>> {
            override fun then(task: Task<UploadTask.TaskSnapshot>): Task<Uri> {

                if (!task.isSuccessful) {

                    throw task.exception!!
                }
                return storageRef.downloadUrl
            }


        }).addOnCompleteListener(object : OnCompleteListener<Uri> {
            override fun onComplete(task: Task<Uri>) {

                if (task.isSuccessful) {

                    chatDb.child("thumbImg").setValue(task.result.toString())

                    thumbImgUrl = task.result.toString()

                    recycleModel.add(
                        RecycleModel(
                            profile.name,
                            "",
                            System.currentTimeMillis().toString(),
                            userId,
                            false,
                            "",
                            "meetUpsFellow",
                            "",
                            "",
                            chatId,
                            "",
                            "",
                            imgAbsolutePath,
                            task.result.toString(),
                            "",
                            ""
                        )
                    )


                    *//*recycleViewGroupChatAdpter!!.addChats(recycleModel)
                    recycleViewGroupChatAdpter!!.notifyItemInserted(recycleModel.lastIndex)*//*
                }
            }
        })

        groupDb.child("LastMsg").setValue(message)
        groupDb.child("LastMsgTime").setValue(currentDateTime.toString())
        groupDb.child("SenderName").setValue(profile.name)
        groupDb.child("SenderId").setValue(userId)

        chatDb.child("msg").setValue(message)
        chatDb.child("img").setValue("image")
        chatDb.child("notice").setValue("")
        chatDb.child("msgTime").setValue(currentDateTime.toString())
        chatDb.child("senderId").setValue(userId)
        chatDb.child("senderName").setValue(profile.name)
        val readDb = chatDb.child("readBy").child(userId)
        readDb.child("read").setValue("yes")

        for (i in groupMembers) {

            if (i == userId){

                chatDb.child("mediaDeviceUrlOf").child(userId).setValue(imgAbsolutePath)
            }else {

                chatDb.child("mediaDeviceUrlOf").child(i).setValue("")
            }

            chatDb.child("msgClearedBy").child(i).setValue("no")
        }

        uploadTask.continueWithTask(object :
            Continuation<UploadTask.TaskSnapshot, Task<Uri>> {
            override fun then(task: Task<UploadTask.TaskSnapshot>): Task<Uri> {

                if (!task.isSuccessful) {

                    throw task.exception!!
                }
                return storageRef.downloadUrl
            }


        }).addOnCompleteListener(object : OnCompleteListener<Uri> {
            override fun onComplete(task: Task<Uri>) {

                if (task.isSuccessful) {

                    isSending = true
                    closeUploadDialog()

                    recycleModel.add(
                        RecycleModel(
                            profile.name,
                            message,
                            currentDateTime.toString(),
                            userId,
                            false,
                            "",
                            task.result.toString(),
                            "",
                            "",
                            currentDateTime.toString(),
                            "",
                            "",
                            imgAbsolutePath,
                            thumbImgUrl,
                            "",
                            ""
                        )
                    )

                    if (recycleViewGroupChatAdpter == null) {

                        recycleViewGroupChatAdpter =
                            RecycleViewGroupChatAdpter(this@GroupChatActivity)

                        recycleViewGroupChatAdpter!!.addChats(recycleModel)

                        rvGroupChat.adapter = recycleViewGroupChatAdpter

                        Handler().postDelayed(object : Runnable {

                            override fun run() {
                                isSending = false
                            }
                        }, 300)

                        lastChatSize = recycleModel.size
                        isSendingImg = false

                    } else {

                        recycleViewGroupChatAdpter!!.updateChats(recycleModel[recycleModel.lastIndex])

                        recycleViewGroupChatAdpter!!.notifyItemChanged(recycleModel.lastIndex, recycleModel[recycleModel.lastIndex])
                    }



                    chatDb.child("msg").setValue("image")
                    chatDb.child("img").setValue(task.result.toString())
                    chatDb.child("notice").setValue("")
                    chatDb.child("msgTime").setValue(currentDateTime.toString())
                    chatDb.child("senderId").setValue(userId)
                    chatDb.child("senderName").setValue(profile.name)
                    val readDb = chatDb.child("readBy").child(userId)
                    readDb.child("read").setValue("yes")

                    for (i in groupMembers) {

                        if (i == userId){

                            chatDb.child("mediaDeviceUrlOf").child(userId).setValue(imgAbsolutePath)
                        }else {

                            chatDb.child("mediaDeviceUrlOf").child(i).setValue("")
                        }

                        chatDb.child("msgClearedBy").child(i).setValue("no")
                    }
                }
            }
        })
    }*/

    private fun rotateBitmap(bitmap: Bitmap, orientation: Int): Bitmap? {
        val matrix = Matrix()
        when (orientation) {
            ExifInterface.ORIENTATION_NORMAL -> {
                return bitmap
            }

            ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> {
                matrix.setScale((-1).toFloat(), 1F)
            }

            ExifInterface.ORIENTATION_ROTATE_180 -> {
                matrix.setRotate(180F)
            }

            ExifInterface.ORIENTATION_FLIP_VERTICAL -> {
                matrix.setRotate(180F)
                matrix.postScale((-1).toFloat(), 1F)
            }

            ExifInterface.ORIENTATION_TRANSPOSE -> {
                matrix.setRotate(90F)
                matrix.postScale((-1).toFloat(), 1F)
            }

            ExifInterface.ORIENTATION_ROTATE_90 -> {
                matrix.setRotate(90F)
            }

            ExifInterface.ORIENTATION_TRANSVERSE -> {
                matrix.setRotate((-90).toFloat())
                matrix.postScale((-1).toFloat(), 1F)
            }

            ExifInterface.ORIENTATION_ROTATE_270 -> {
                matrix.setRotate((-90).toFloat())
            }

            else -> {
                return bitmap
            }
        }

        return try {
            val bmRotated =
                Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
            bitmap.recycle()
            bmRotated
        } catch (e: OutOfMemoryError) {
            e.printStackTrace()
            null
        }
    }

    private fun selectImagePopup() {

        val dialog = Dialog(this@GroupChatActivity)

        dialog.setCancelable(false)

        //val activity = context as AppCompatActivity

        val view = layoutInflater.inflate(R.layout.custom_media_chooser, null)

        dialog.setContentView(view)

        dialog.window!!.attributes.windowAnimations = R.style.DialogAnimation

        if (dialog.window != null) {
            dialog.window!!.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
        dialog.window!!.setGravity(Gravity.CENTER)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val takePhoto = view.findViewById<LinearLayout>(R.id.takePhoto)
        val chooseGallery = view.findViewById<LinearLayout>(R.id.chooseGallery)
        val cancelBtn = view.findViewById<Button>(R.id.cancel_btn)
        val uploadVideos = view.findViewById<CardView>(R.id.uploadVideos)

        uploadVideos.visibility = View.VISIBLE

        takePhoto.setOnClickListener {

            //this@GroupChatActivity.onCameraTapped()
           // capturePhoto()
            camerarequestRuntimePermission();
            dialog.cancel()
        }

        chooseGallery.setOnClickListener {

            //this@GroupChatActivity.onGalleryTapped()
            EasyImage.openGallery(
                this@GroupChatActivity,
                //ConstantsImage.RequestCodes.PICK_PICTURE_FROM_GALLERY
                Constants.ImagePicker.Image
            )
            dialog.cancel()
        }

        uploadVideos.setOnClickListener {

           // this@GroupChatActivity.onVideoTapped()
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.type = "video/*"
            startActivityForResult(intent, Constants.ImagePicker.Video)
            dialog.cancel()
        }

        cancelBtn.setOnClickListener {

            dialog.cancel()
        }

        dialog.show()

    }
    private val CAMERA_PERMISSION = Manifest.permission.CAMERA
    private val READ_STORAGE_PERMISSION = Manifest.permission.READ_EXTERNAL_STORAGE
    private fun camerarequestRuntimePermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                CAMERA_PERMISSION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            ///  Toast.makeText(this, "Permission accepted", Toast.LENGTH_SHORT).show()
            capturePhoto()
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(CAMERA_PERMISSION, READ_STORAGE_PERMISSION),
                100
            )
        }
    }


    @AfterPermissionGranted(Constants.PermissionsCode.REQUEST_PERMISSION_VIDEO)
    fun onVideoTapped() =
        when (cameraPermission(Constants.PermissionsCode.REQUEST_PERMISSION_VIDEO)) {
            true -> {
                when (checkGalleryAppAvailability()) {
                    true -> {
                        val intent = Intent(Intent.ACTION_GET_CONTENT)
                        intent.addCategory(Intent.CATEGORY_OPENABLE)
                        intent.type = "video/*"
                        startActivityForResult(intent, Constants.ImagePicker.Video)
                    }
                    false -> {
                        universalToast(getString(R.string.text_no_gallery))
                    }
                }
            }

            false -> {

            }
        }

    @AfterPermissionGranted(Constants.PermissionsCode.REQUEST_PERMISSION_CAMERA)
    fun onCameraTapped() =
        when (cameraPermission(Constants.PermissionsCode.REQUEST_PERMISSION_CAMERA)) {
            true -> {
                when (isDeviceSupportCamera()) {
                    true -> {
                        capturePhoto()
                    }
                    false -> {
                        universalToast(getString(R.string.text_no_camera))
                    }
                }
            }

            false -> {

            }
        }

    @AfterPermissionGranted(Constants.PermissionsCode.REQUEST_PERMISSION_GALLERY)
    fun onGalleryTapped() =
        when (cameraPermission(Constants.PermissionsCode.REQUEST_PERMISSION_GALLERY)) {
            true -> {
                when (checkGalleryAppAvailability()) {
                    true -> {
                        val intent = Intent(Intent.ACTION_GET_CONTENT)
                        intent.addCategory(Intent.CATEGORY_OPENABLE)


                        val filePath = Uri.parse(Environment.DIRECTORY_PICTURES)
                        intent.setDataAndType(filePath, "image/*")
                        //  intent.type =
                        //startActivityForResult(intent, 200)
                        startActivityForResult(
                            intent,
                            ConstantsImage.RequestCodes.PICK_PICTURE_FROM_GALLERY
                        )
                    }
                    false -> {

                        val intent = Intent(Intent.ACTION_GET_CONTENT)
                        intent.addCategory(Intent.CATEGORY_OPENABLE)


                        val filePath = Uri.parse(Environment.DIRECTORY_PICTURES)
                        intent.setDataAndType(filePath, "image/*")
                        //  intent.type =
                        //startActivityForResult(intent, 200)
                        startActivityForResult(
                            intent,
                            ConstantsImage.RequestCodes.PICK_PICTURE_FROM_GALLERY
                        )
                    }
                }
            }

            false -> {

            }
        }

    private fun capturePhoto() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            var photoURI: Uri? = null
            try {
                photoFile = createImageFileWith()
                photoURI = FileProvider.getUriForFile(
                    this@GroupChatActivity,
                    getString(R.string.file_provider_authority), photoFile!!
                )

            } catch (ex: IOException) {
                LogManager.logger.e("TakePicture", ex.message!!)
            }

            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
                takePictureIntent.clipData = ClipData.newRawUri("", photoURI)
                takePictureIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            }
            startActivityForResult(takePictureIntent, 100)
        }
    }

    @Suppress("DEPRECATION")
    @Throws(IOException::class)
    private fun createImageFileWith(): File {
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())


        val imageFileName = "JPEG_$timestamp"
        val storageDir = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
            "pics"
        )
        storageDir.mkdirs()
        return File.createTempFile(imageFileName, ".jpg", storageDir)
    }


   /* private fun setGroupNameAndImg() {

        val db = FirebaseDatabase.getInstance(Constants.Firebase.databaseUrl).getReference("MeetUpsLive")
            .child("ChatGroup").child(groupId)

        db.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snap: DataSnapshot) {

                if (snap.exists()) {

                    tvUsername.text = snap.child("GroupName").value.toString()

                    var requestOptions = RequestOptions()
                    requestOptions = requestOptions.transforms(CenterCrop(), RoundedCorners(16))

                    Glide.with(this@GroupChatActivity)
                        .load(snap.child("GroupImg").value.toString())
                        .placeholder(R.drawable.meetupsfellow_transpatent)
                        .apply(requestOptions)
                        .into(ivUserImage)

                    if (snap.child("GroupAdmin").value.toString() == userId) {

                        addGroupMembers.visibility = View.VISIBLE
                    } else {

                        addGroupMembers.visibility = View.GONE
                    }
                }

            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }

    private fun sendMsg(msg: String) {

        etChat.text.clear()
        isSending = true

        val profile = sharedPreferencesUtil.fetchUserProfile()
        val groupDb =
            FirebaseDatabase.getInstance(Constants.Firebase.databaseUrl).getReference(Constants.Firebase.ChatDB)
                .child("ChatGroup").child(groupId)
        val db = FirebaseDatabase.getInstance(Constants.Firebase.databaseUrl).getReference(Constants.Firebase.ChatDB)
            .child("ChatGroup").child(groupId).child("Chats")
        val msgTime = System.currentTimeMillis().toString()

        if (!isReplying) {

            groupDb.child("LastMsg").setValue(msg)
            groupDb.child("LastMsgTime").setValue(msgTime)
            groupDb.child("SenderName").setValue(profile.name)
            groupDb.child("SenderId").setValue(userId)

            val newMsgDb = db.child(msgTime)
            val readDb = newMsgDb.child("readBy").child(userId)
            readDb.child("read").setValue("yes")

            newMsgDb.child("msg").setValue(msg)
            newMsgDb.child("msgTime").setValue(msgTime)
            newMsgDb.child("senderId").setValue(userId)
            newMsgDb.child("notice").setValue("")

            for (i in groupMembers) {

                newMsgDb.child("msgClearedBy").child(i).setValue("no")
            }

            newMsgDb.child("senderName").setValue(profile.name)
                .addOnSuccessListener(object : OnSuccessListener<Void> {
                    override fun onSuccess(p0: Void?) {

                        //fetchMsgs()

                    }
                }).addOnFailureListener(object : OnFailureListener {
                    override fun onFailure(p0: Exception) {

                        Toast.makeText(this@GroupChatActivity, p0.toString(), Toast.LENGTH_SHORT)
                            .show()
                    }
                })

            recycleModel.add(
                RecycleModel(
                    profile.name,
                    msg,
                    msgTime,
                    userId,
                    false,
                    "",
                    "",
                    "",
                    "",
                    msgTime,
                    "",
                    "",
                    "",
                    "",
                    "",
                    ""
                )
            )


            if (recycleViewGroupChatAdpter == null || isSending) {

                recycleViewGroupChatAdpter =
                    RecycleViewGroupChatAdpter(this@GroupChatActivity)

                recycleViewGroupChatAdpter!!.addChats(recycleModel)

                rvGroupChat.adapter = recycleViewGroupChatAdpter

                Handler().postDelayed(object : Runnable {

                    override fun run() {
                        isSending = false
                    }
                }, 300)

                lastChatSize = recycleModel.size
                isSendingImg = false

            } else {

                recycleViewGroupChatAdpter!!.addChats(recycleModel)

                recycleViewGroupChatAdpter!!.notifyItemInserted(recycleModel.lastIndex)
            }


        } else {

            groupDb.child("LastMsg").setValue(msg)
            groupDb.child("LastMsgTime").setValue(msgTime)
            groupDb.child("SenderName").setValue(profile.name)
            groupDb.child("SenderId").setValue(userId)

            val newMsgDb = db.child(msgTime)
            val readDb = newMsgDb.child("readBy").child(userId)
            readDb.child("read").setValue("yes")

            newMsgDb.child("replyTo").setValue(replyTo)
            newMsgDb.child("replyMsg").setValue(replyMsg)
            newMsgDb.child("replyMsgTime").setValue(replyMsgTime)
            newMsgDb.child("replyId").setValue(chatId)
            newMsgDb.child("replyImg").setValue(chatImg)

            newMsgDb.child("msg").setValue(msg)
            newMsgDb.child("msgTime").setValue(msgTime)
            newMsgDb.child("senderId").setValue(userId)
            newMsgDb.child("notice").setValue("")

            for (i in groupMembers) {

                newMsgDb.child("msgClearedBy").child(i).setValue("no")
            }


            newMsgDb.child("senderName").setValue(profile.name)
                .addOnSuccessListener(object : OnSuccessListener<Void> {
                    override fun onSuccess(p0: Void?) {

                        fetchMsgs()

                    }
                }).addOnFailureListener(object : OnFailureListener {
                    override fun onFailure(p0: Exception) {

                        Toast.makeText(this@GroupChatActivity, p0.toString(), Toast.LENGTH_SHORT)
                            .show()
                    }
                })

            recycleModel.add(
                RecycleModel(
                    profile.name,
                    msg,
                    msgTime,
                    userId,
                    false,
                    "",
                    "",
                    replyTo,
                    replyMsg,
                    msgTime,
                    chatId,
                    chatImg,
                    "",
                    "",
                    "",
                    ""
                )
            )


            if (recycleViewGroupChatAdpter == null) {

                recycleViewGroupChatAdpter =
                    RecycleViewGroupChatAdpter(this@GroupChatActivity)

                recycleViewGroupChatAdpter!!.addChats(recycleModel)

                rvGroupChat.adapter = recycleViewGroupChatAdpter

                Handler().postDelayed(object : Runnable {

                    override fun run() {
                        isSending = false
                    }
                }, 300)

                lastChatSize = recycleModel.size
                isSendingImg = false

            } else {

                recycleViewGroupChatAdpter!!.addChats(recycleModel)
                recycleViewGroupChatAdpter!!.notifyItemInserted(recycleModel.lastIndex)
            }

            replyChatLayG.visibility = View.GONE
            replyLayG.visibility = View.GONE
            sendingImgLayG.visibility = View.GONE
            isReplying = false
            replyTo = ""
            replyMsg = ""
            replyMsgTime = ""
            chatId = ""
            chatImg = ""
        }

        *//*db.child(msgTime).setValue(hashMap).addOnSuccessListener( object  : OnSuccessListener<Void> {
            override fun onSuccess(p0: Void?) {

                etChat.text.clear()
                fetchMsgs()
            }

        }).addOnFailureListener(object : OnFailureListener {
            override fun onFailure(p0: Exception) {

                Toast.makeText(this@GroupChatActivity, p0.toString(), Toast.LENGTH_SHORT).show()
            }


        })*//*

        sendMsgNotification("text")
    }*/

    private fun chatId(size: Int): String {
        val token = ByteArray(size)
        SecureRandom().nextBytes(token)
        return BigInteger(1, token).toString(size)
    }

  /*  private fun sendMsgNotification(msgType: String) {

        val time = System.currentTimeMillis()
       // val adminTime = -System.currentTimeMillis()
        val id = userId

        for (otherId in groupMembers){

            if (otherId != userId) {

                val other = otherId

                val name = sharedPreferencesUtil.fetchUserProfile().name
                val conversationConstantId = chatId(10)
                val conversationModel = ConversationModel(
                    message,
                    id,
                    conversationConstantId,
                    groupId,
                    chatOpen = false,
                    meDeletedConvo = false,
                    otherUserOnline = if (null != conversation) conversation!!.otherUserOnline else false,
                    userTyping = false,
                    blockedBY = "",
                    lastMessagetime = time,
                    otherUserId = other,
                    otherUserName = sharedPreferencesUtil.fetchOtherUserProfile(other).userName,
                    unReadCount = "0"
                )

                val otherConversationModel = ConversationModel(
                    message,
                    other,
                    conversationConstantId,
                    groupId,
                    false,
                    meDeletedConvo = false,
                    otherUserOnline = true,
                    userTyping = false,
                    blockedBY = "",
                    lastMessagetime = time,
                    otherUserId = id,
                    otherUserName = name,
                    unReadCount = "0"
                )

                val chatModel = ChatModel(
                    chatId(8), message, id, name, Constants.MessageStatus.Sent, time, "0",
                    Constants.MediaType.Text, "", ""
                )

                val dataModel = Data().apply {
                    senderImageUrl = sharedPreferencesUtil.fetchUserProfileImage()
                    receiverId = other
                    type = Constants.Notification.Chat
                    userSide = conversationModel
                    otherSide = otherConversationModel
                    chatInfo = chatModel
                }

                val notificationMo = Notification().apply {

                    body = if (msgType == "text") {

                        String.format(getString(R.string.text_sent_message), name)
                    } else if (msgType == "img") {

                        String.format(getString(R.string.text_shared_photo), name)
                    } else if (msgType == "gif") {

                        String.format(getString(R.string.text_shared_gif), name)
                    } else {

                        String.format(getString(R.string.text_shared_video), name)
                    }
                    title = ArchitectureApp.instance!!.getString(R.string.app_name)
                }

                val notificationMod = NotificationModel().apply {
                    data = dataModel
                    notification = notificationMo
                    to =
                        sharedPreferencesUtil.fetchOtherUserProfile(other)
                            .deviceToken
                }

                val requestSendNotification = RequestSendNotification().apply {
                    notificationModel = notificationMod
                    senderImageUrl = dataModel.senderImageUrl
                    receiverId = other
                    senderId = conversationModel.chatSenderId
                    this.otherUserId = other
                    otherUserName = sharedPreferencesUtil.fetchOtherUserProfile(other).userName
                }

                if (null == mPresenter)
                    mPresenter = ChatPresenter(presenter)

                run {
                    mPresenter!!.addSendNotificationObject(requestSendNotification)
                    mPresenter!!.callRetrofit(ConstantsApi.SEND_NOTIFICATION)
                }
            }

        }


        *//*  if(other == "3"){
              time = - time
          }*//*


    }

    private fun fetchMsgs() {

        recycleModel = ArrayList()
        recycleModel.clear()

        val db = FirebaseDatabase.getInstance(Constants.Firebase.databaseUrl).getReference("MeetUpsLive")
            .child("ChatGroup").child(groupId).child("Chats")

       *//* val query: Query = db
            .orderByChild("msgTime")
            .ref*//*

        db.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snap: DataSnapshot) {

                if (snap.exists()) {

                    recycleModel.clear()
                    var readCount = 0

                    for (i in snap.children) {
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        
                        for (j in i.child("readBy").children) {

                            if (groupMembers.contains(j.key.toString())) {

                                readCount++
                            }
                        }

                        var isReadByAll = false
                        Log.d("ReceivedMsg", i.child("msg").value.toString())
                        val senderName = i.child("senderName").value.toString()
                        val msg = i.child("msg").value.toString()
                        val msgTime = i.child("msgTime").value.toString()
                        val senderId = i.child("senderId").value.toString()
                        val notice = i.child("notice").value.toString()
                        val img = i.child("img").value.toString()
                        val replyTo = i.child("replyTo").value.toString()
                        val replyMsg = i.child("replyMsg").value.toString()
                        val chatId = i.key.toString()
                        val replyId = i.child("replyId").value.toString()
                        val isMsgCleared = i.child("msgClearedBy").child(userId).value.toString()
                        val replyImg = i.child("replyImg").value.toString()
                        val imgPathSelf = i.child("mediaDeviceUrlOf").child(userId).value.toString()
                        val videoUrl = i.child("videoUrl").value.toString()
                        val thumbImg = i.child("thumbImg").value.toString()
                        val gifId = i.child("GifId").value.toString()

                        if (readCount == groupMembers.size) {

                            Log.d("readStatus", "allRead")

                            isReadByAll = true

                        } else {

                            Log.d(
                                "readStatus",
                                readCount.toString() + "   " + groupMembers.size.toString()
                            )
                        }

                        if (isMsgCleared != "yes") {

                            if (senderName != "null" && msg != "null" && msgTime != "null" && senderId != "null") {

                                recycleModel.add(
                                    RecycleModel(
                                        senderName,
                                        msg,
                                        msgTime,
                                        senderId,
                                        isReadByAll,
                                        notice,
                                        img,
                                        replyTo,
                                        replyMsg,
                                        chatId,
                                        replyId,
                                        replyImg,
                                        imgPathSelf,
                                        thumbImg,
                                        videoUrl,
                                        gifId
                                    )
                                )
                            }

                            readCount = 0
                        }
                    }

                    if (lastChatSize == 0) {

                        lastChatSize = recycleModel.size
                    }

                    if (lastReadIndex == 0) {

                        lastReadIndex = recycleModel.lastIndex
                    }

                    if (recycleViewGroupChatAdpter == null || isSending) {

                        recycleViewGroupChatAdpter =
                            RecycleViewGroupChatAdpter(this@GroupChatActivity)

                        recycleViewGroupChatAdpter!!.addChats(recycleModel)

                        rvGroupChat.adapter = recycleViewGroupChatAdpter

                        Handler().postDelayed(object : Runnable {

                            override fun run() {
                                isSending = false
                            }
                        }, 300)

                        lastChatSize = recycleModel.size
                        isSendingImg = false

                    } else {

                        recycleViewGroupChatAdpter!!.addChats(recycleModel)

                        recycleViewGroupChatAdpter!!.notifyDataSetChanged()
                    }

                    if (onCurrentActivity && !isSending) {

                        if (lastChatSize < recycleModel.size) {

                            Log.d("ChatSe", recycleModel.lastIndex.toString())

                            recycleViewGroupChatAdpter!!.addChats(recycleModel)

                            recycleViewGroupChatAdpter!!.notifyItemInserted(recycleModel.lastIndex)

                            //recycleViewPersonalChatAdapter!!.notifyDataSetChanged()

                            if (recycleModel.lastIndex > 10) {

                                unSeenCount += 1
                                newMsgCountLayG.visibility = View.VISIBLE
                                newMsgCountG.text = unSeenCount.toString()

                                if (linearLayoutManager!!.findLastVisibleItemPosition() != recycleModel.lastIndex) {

                                    scrollToStartLayG.visibility = View.VISIBLE
                                }
                            }

                            lastChatSize = recycleModel.size

                        } else if (lastChatSize > recycleModel.size) {

                            recycleViewGroupChatAdpter!!. addChats(recycleModel)

                            //recycleViewPersonalChatAdapter!!.notifyItemInserted(recycleModel.lastIndex)
                            recycleViewGroupChatAdpter!!.notifyDataSetChanged()

                            lastChatSize = recycleModel.size
                        } else {

                            //recycleViewPersonalChatAdapter!!.addChats(recycleModel)

                            Log.e("ChatStatus", recycleModel.lastIndex.toString())

                            //val newData = recycleModel.lastIndex - lastReadIndex

                            if (lastReadIndex != 0) {

                                for (i in lastReadIndex + 1..recycleModel.lastIndex) {

                                    Log.d("ChatIndexs", i.toString())

                                    recycleViewGroupChatAdpter!!.updateChats(recycleModel[i])
                                    recycleViewGroupChatAdpter!!.notifyItemChanged(
                                        i,
                                        recycleModel[i]
                                    )
                                }
                            }

                            recycleViewGroupChatAdpter!!.updateChats(recycleModel[recycleModel.lastIndex])
                            recycleViewGroupChatAdpter!!.notifyItemChanged(
                                recycleModel.lastIndex,
                                recycleModel[recycleModel.lastIndex]
                            )


                            lastChatSize = recycleModel.size
                            lastReadIndex = recycleModel.lastIndex
                        }
                    } else {
                        *//*recycleViewPersonalChatAdapter!!.addChats(recycleModel)

                        recycleViewPersonalChatAdapter!!.notifyDataSetChanged()

                        lastChatSize = recycleModel.size*//*
                    }


                    //recycleModel.clear()

                    if (onCurrentActivity) {

                        readMsgs()
                    }

                }
            }

            override fun onCancelled(p0: DatabaseError) {}

        })

        Handler().postDelayed({
            etChat.text.clear()
            etChat.isEnabled = true
        }, 100)
    }

    fun scrollToMsg(replyMsg: String) {

        Log.d("replyMsg", replyMsg)

        for (i in recycleModel.indices) {

            Log.d("replyMsgF", recycleModel.get(i).replyMsg.toString())

            if (recycleModel.get(i).chatId.toString() == replyMsg) {

                Log.d("replyMsgIF", recycleModel.get(i).lastMsg.toString())
                Log.d("replyMsgP", i.toString())

                rvGroupChat.smoothScrollToPosition(i)
            }
        }

    }

    fun chatsToDelete(chatId: String) {

        if (deleteChatsArr.contains(chatId)) {

            deleteChatsArr.remove(chatId)

            Log.d("deletedChatR", chatId)

        } else {

            deleteChatsArr.add(chatId)
            Log.d("deletedChatA", chatId)
        }

        if (deleteChatsArr.isEmpty()) {

            //chatDeleteIc.setVisible(false)
            deleteChat.visibility = View.GONE

        } else {

            //chatDeleteIc.setVisible(true)
            deleteChat.visibility = View.VISIBLE
        }

    }

    fun showCopyOption(msg: String, senderName : String, msgTime : String, msgId : String) {

        val tempMsg = "[$msgTime] $senderName : $msg\n"

        if (msgToCopy.contains(tempMsg)){

            if (msgIdArr.contains(msgId)) {

                Log.d("CopyMsgT", tempMsg)

                msgToCopy = msgToCopy.replace(tempMsg, "")
                msgIdArr.remove(msgId)
            } else {

                Log.d("CopyMsgET", tempMsg)

                msgToCopy += tempMsg
                msgIdArr.add(msgId)
            }

        } else {

            Log.d("CopyMsgET", tempMsg)

            msgToCopy += tempMsg
            msgIdArr.add(msgId)
        }

        Log.d("CopyMsgA", msgToCopy)

        if (msgToCopy.isBlank()) {

            //copyMenuItem.setVisible(false)
            copyChat.visibility = View.GONE

        } else {

            copyChat.visibility = View.VISIBLE
            //copyMenuItem.setVisible(true)
        }
    }

    @SuppressLint("SuspiciousIndentation")
    private fun deleteChat() {

        for (id in recycleViewGroupChatAdpter!!.selectedPos) {

            recycleViewGroupChatAdpter!!.removeChats(id.toInt())
            //recycleViewPersonalChatAdapter!!.notifyItemRemoved(id.toInt())
        }

        deleteChat.visibility = View.GONE
        //deleteChatsArr.clear()
        copyChat.visibility = View.GONE
        recycleViewGroupChatAdpter!!.selectedPos.clear()
        recycleViewGroupChatAdpter!!.notifyDataSetChanged()


        val db = FirebaseDatabase.getInstance(Constants.Firebase.databaseUrl).getReference("MeetUpsLive")
            .child("ChatGroup").child(groupId).child("Chats")

            db.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snap: DataSnapshot) {

                    if (snap.exists()) {

                        for (ids in snap.children) {

                            if (deleteChatsArr.contains(ids.key.toString())) {

                                for (i in groupMembers){

                                    db.child(ids.key.toString())
                                        .child("msgClearedBy").child(i).setValue("yes")
                                }
                            }
                        }

                        //chatDeleteIc.setVisible(false)
                        deleteChat.visibility = View.GONE
                        deleteChatsArr.clear()
                    }
                }

                override fun onCancelled(p0: DatabaseError) {


                }


            })
    }*/

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        chatDeleteIc = menu!!.findItem(R.id.menu_delete_chat)
        copyMenuItem = menu!!.findItem(R.id.menu_copy_chat)

        return super.onCreateOptionsMenu(menu)
    }

    private fun readMsgs() {

        val db = FirebaseDatabase.getInstance(Constants.Firebase.databaseUrl).getReference(Constants.Firebase.ChatDB)
            .child("ChatGroup").child(groupId).child("Chats")

        db.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snap: DataSnapshot) {

                if (snap.exists()) {

                    for (i in snap.children) {

                        if (i.child("readBy").child(userId).exists()) {
                            Log.d("exists", "exists")

                        } else {
                            Log.d("exists", "Notexists")
                            val readDb = db.child(i.key.toString()).child("readBy")
                            readDb.child(userId).child("read").setValue("yes")
                        }
                    }
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })


    }
}