package com.connect.meetupsfellow.mvp.view.activities


import VideoPlayerExoPlayer2181Impl
import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityManager
import android.app.Dialog
import android.content.*
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.pdf.PdfRenderer
import android.media.ThumbnailUtils
import android.net.Uri
import android.os.*
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.text.InputFilter
import android.util.Log
import android.util.Size
import android.view.*
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.widget.NestedScrollView
import androidx.exifinterface.media.ExifInterface
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.connect.meetupsfellow.R
import com.connect.meetupsfellow.application.ArchitectureApp
import com.connect.meetupsfellow.components.activitytouch.CustomAppActivityCompatViewImpltouch
import com.connect.meetupsfellow.constants.*
import com.connect.meetupsfellow.databinding.ActivityChatBinding
import com.connect.meetupsfellow.developement.interfaces.ApiConnect
import com.connect.meetupsfellow.developement.interfaces.FirebaseUtil
import com.connect.meetupsfellow.developement.interfaces.SharedPreferencesUtil
import com.connect.meetupsfellow.global.firebase.FirebaseChatListener
import com.connect.meetupsfellow.global.interfaces.UploadMediaInterface
import com.connect.meetupsfellow.global.utils.FileUtils
import com.connect.meetupsfellow.global.utils.Path
import com.connect.meetupsfellow.global.utils.copyFileToExternalDir
import com.connect.meetupsfellow.global.utils.getFileDetails
import com.connect.meetupsfellow.global.utils.getFileExtensionFromUri
import com.connect.meetupsfellow.global.view.DownloadsFiles
import com.connect.meetupsfellow.global.view.DownloadsImage
import com.connect.meetupsfellow.global.view.UploadChatMediaOnFireabase
import com.connect.meetupsfellow.global.view.easyphotopicker.ConstantsImage
import com.connect.meetupsfellow.global.view.easyphotopicker.DefaultCallback
import com.connect.meetupsfellow.global.view.easyphotopicker.EasyImage
import com.connect.meetupsfellow.localDatabase.database.MyDbHandeler
import com.connect.meetupsfellow.localDatabase.models.PersonalChatModel
import com.connect.meetupsfellow.log.LogManager
import com.connect.meetupsfellow.mvp.connector.activity.ChatConnector
import com.connect.meetupsfellow.mvp.connector.activity.ConnectionRequestConnector
import com.connect.meetupsfellow.mvp.connector.activity.UserProfileConnector
import com.connect.meetupsfellow.mvp.presenter.activity.ChatPresenter
import com.connect.meetupsfellow.mvp.presenter.activity.ConnectionRequestPresenter
import com.connect.meetupsfellow.mvp.presenter.activity.UserProfilePresenter
import com.connect.meetupsfellow.mvp.view.adapter.ChatAdapter
import com.connect.meetupsfellow.mvp.view.adapter.RecycleViewPersonalChatAdapter
import com.connect.meetupsfellow.mvp.view.dialog.AlertBuyPremium
import com.connect.meetupsfellow.mvp.view.dialog.UploadDialogCustom
import com.connect.meetupsfellow.mvp.view.model.ChatModel
import com.connect.meetupsfellow.mvp.view.model.ConversationModel
import com.connect.meetupsfellow.mvp.view.model.RecycleModel
import com.connect.meetupsfellow.mvp.view.model.UploadedGalleryImage
import com.connect.meetupsfellow.retrofit.request.*
import com.connect.meetupsfellow.retrofit.response.CommonResponse
import com.connect.meetupsfellow.retrofit.response.ResponseUserData
import com.connect.meetupsfellow.service.VideoCache
import com.giphy.sdk.core.GPHCore
import com.giphy.sdk.core.models.Media
import com.giphy.sdk.tracking.isVideo
import com.giphy.sdk.ui.GPHContentType
import com.giphy.sdk.ui.GPHSettings
import com.giphy.sdk.ui.Giphy
import com.giphy.sdk.ui.themes.GPHTheme
import com.giphy.sdk.ui.themes.GridType
import com.giphy.sdk.ui.utils.videoUrl
import com.giphy.sdk.ui.views.GiphyDialogFragment
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.firebase.database.*
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import pub.devrel.easypermissions.AfterPermissionGranted
import timber.log.Timber
import java.io.*
import java.math.BigInteger
import java.security.SecureRandom
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@Suppress("SameParameterValue")
class ChatActivity : CustomAppActivityCompatViewImpltouch(), UploadMediaInterface {

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                // Permission granted, proceed with your operations
                Toast.makeText(this@ChatActivity, "Try again!", Toast.LENGTH_SHORT).show()
            } else {
                // Permission denied, show a message or handle the case accordingly
                Toast.makeText(this@ChatActivity, "Permission denied", Toast.LENGTH_SHORT).show()
            }
        }


    private val presenterProfile = object : UserProfileConnector.RequiredViewOps {
        override fun showToast(error: String, logout: Boolean?) {
            when (logout) {
                true -> {
                    logout()
                }

                false -> {}
                null -> {}
            }
            universalToast(error)
            hideProgressView(progressBar)
            binding!!.progressbarCircal.visibility = View.GONE
        }

        override fun showResponse(response: CommonResponse, type: ConstantsApi) {
            Log.e("Api_Response", "Msg: ${response.message} ")
            when (type) {
                ConstantsApi.FETCH_PROFILE -> {
                    when (response.message.isNotEmpty()) {
                        true -> {
                            universalToast(response.message)
                            //  finish()
                            binding!!.giphyBtn.visibility = View.GONE
                            binding!!.ivAttachment.visibility = View.GONE
                            binding!!.cardSendOneSms.visibility = View.VISIBLE
                            binding!!.tvOnesms.text = response.message
                            this@ChatActivity.binding!!.rlFooter.visibility = View.GONE
                            Log.e("otherUserData21", "User_delete_account")
                        }

                        false -> {
                            when (response.userInfo.message.isEmpty()) {
                                true -> {
                                    otherUserProfile = response.userInfo
                                    Log.e("otherUserData", "id ${otherUserProfile.userId}")
                                    Log.e("otherUserData", "name ${otherUserProfile.name}")
                                    Log.e(
                                        "otherUserData",
                                        "canectionSts ${otherUserProfile.connection_status}"
                                    )

                                    if (otherUserProfile.connection_status.equals("request_sent") || otherUserProfile.connection_status.equals(
                                            "direct_message_sent"
                                        )
                                    ) {
                                        //   giphyBtn.visibility = View.GONE
                                        binding!!.ivAttachment.visibility = View.GONE
                                        binding!!.cardSendOneSms.visibility = View.VISIBLE
                                        this@ChatActivity.binding!!.rlFooter.visibility = View.GONE
                                        binding!!.etChat.isEnabled = false
                                        binding!!.ivSend.isClickable = false
                                        binding!!.llButtonChat.visibility = View.GONE
                                        if (otherUserProfile.connection_status.equals("direct_message_sent")) {
                                            //tv_onesms.text = getString(R.string.you_sent_direct_connection_request)
                                            binding!!.tvOnesms.text =
                                                "You have sent a direct connect message. Once ${otherUserProfile.name} responds, you both will be connected and can continue further communication."
                                        } else binding!!.tvOnesms.text =
                                            getString(R.string.you_sent_connection_request)
                                        Log.e("otherUserData", "wait_for_connection**GETFirst")
                                    } else if (otherUserProfile.connection_status.equals("request_received") || otherUserProfile.connection_status.equals(
                                            "direct_message_received"
                                        )
                                    ) {
                                        binding!!.llButtonChat.visibility = View.VISIBLE
                                        binding!!.cardSendOneSms.visibility = View.VISIBLE
                                        this@ChatActivity.binding!!.rlFooter.visibility = View.GONE
                                        if (otherUserProfile.connection_status.equals("direct_message_received")) {
                                            //tv_onesms.text = getString(R.string.receiver_direct_connection_request)
                                            binding!!.tvOnesms.text =
                                                "You have received a direct connect message from ${otherUserProfile.name}. Please review it and take the necessary action. Once you click 'Connect now,' the connection will be established between both of you."
                                        } else binding!!.tvOnesms.text =
                                            getString(R.string.you_receiver_connection_request)
                                    } else if (otherUserProfile.connection_status.equals("not_connected")) {
                                        //  giphyBtn.visibility = View.GONE
                                        binding!!.ivAttachment.visibility = View.GONE
                                        binding!!.cardSendOneSms.visibility = View.VISIBLE
                                        binding!!.tvOnesms.text =
                                            getString(R.string.you_not_connect)
                                        this@ChatActivity.binding!!.rlFooter.visibility = View.GONE
                                        Log.e("otherUserData", "wait_for_connection*")

                                    } else if (otherUserProfile.connection_status.equals("connected")) {
                                        //  giphyBtn.visibility = View.VISIBLE
                                        binding!!.ivAttachment.visibility = View.VISIBLE
                                        binding!!.cardSendOneSms.visibility = View.GONE
                                        binding!!.llButtonChat.visibility = View.GONE
                                        this@ChatActivity.binding!!.rlFooter.visibility =
                                            View.VISIBLE
                                        binding!!.etChat.isEnabled = true
                                        binding!!.ivSend.isClickable = true
                                        Log.e("otherUserData", "Already_connected*")
                                    } else {
                                        //  giphyBtn.visibility = View.GONE
                                        binding!!.ivAttachment.visibility = View.GONE
                                        binding!!.cardSendOneSms.visibility = View.VISIBLE
                                        binding!!.tvOnesms.text =
                                            getString(R.string.you_not_connect)
                                        //  tv_onesms.text = "Please connect each other, then continue your conversation."
                                        this@ChatActivity.binding!!.rlFooter.visibility = View.GONE
                                        Log.e("otherUserData", "Elss_notConnected")
                                    }
                                    ///    menuClear.isVisible = otherUserProfile.connection_status.equals("connected")

                                }

                                false -> {
                                    universalToast(response.userInfo.message)
                                    finish()
                                }
                            }
                        }
                    }
                }

                else -> {
                    Log.e("Api_Response", "Elss: ${response.message} ")
                }
            }
            hideProgressView(progressBar)
            binding!!.progressbarCircal.visibility = View.GONE
        }

    }

    private val getConectionpresenter = object : ConnectionRequestConnector.RequiredViewOps {
        override fun showToast(error: String, logout: Boolean?) {
            when (logout) {
                true -> {
                    logout()
                }

                false -> {}
                null -> {}
            }
            universalToast(error)
            hideProgressView(progressBar)
        }

        override fun showResponse(response: CommonResponse, type: ConstantsApi) {

            when (type) {
                ConstantsApi.ACCEPT_CONNECTION_REQUEST -> {
                    if (response.sendConnectionReqResponse.connection_status.equals("connected")) {
                        Log.e(
                            "ConnectRequest",
                            "Status ${response.sendConnectionReqResponse.connection_status}"
                        )

                        if (sharedPreferencesUtil.fetchUserProfile().currentPlanInfo!!.chatMessageTypes == 0) {
                            binding!!.giphyBtn.visibility = View.GONE
                        } else {
                            //giphyBtn.visibility = View.VISIBLE
                            binding!!.giphyBtn.visibility = View.GONE
                        }
                        binding!!.ivAttachment.visibility = View.VISIBLE
                        binding!!.cardSendOneSms.visibility = View.GONE
                        binding!!.llButtonChat.visibility = View.GONE
                        this@ChatActivity.binding!!.rlFooter.visibility = View.VISIBLE
                        binding!!.etChat.isEnabled = true
                        binding!!.ivSend.isClickable = true
                        binding!!.etChat.filters += InputFilter.LengthFilter(1000)
                        /// menuClear.isVisible = true
                        val chatList = chatListDb.child(userId).child(chatRoomId)
                        val otherChatList = chatListDb.child(otherUserId).child(chatRoomId)
                        UpdateChatListConnected(chatList)
                        UpdateChatListConnected(otherChatList)
                    }
                    Toast.makeText(this@ChatActivity, response.message, Toast.LENGTH_SHORT).show()

                    Log.e(
                        "ConnectRequest",
                        "Status ${response.message} and ${response.connection_status}"
                    )
                }

                ConstantsApi.DECLINE_CONNECTION_REQUEST -> {
                    Log.e(
                        "ConnectRequest",
                        "Status ${response.message} and^* ${response.connection_status}"
                    )
                    Toast.makeText(this@ChatActivity, response.message, Toast.LENGTH_SHORT).show()
                    finish()
                }

                else -> {
                    Log.e("ConnectRequest", "Api_Else")
                }
            }
            hideProgressView(progressBar)
        }
    }

    private val presenter = object : ChatConnector.RequiredViewOps {
        override fun showToast(error: String, logout: Boolean?) {
            when (logout) {
                true -> {
                    logout()
                }

                false -> {}
                null -> {}
            }

            universalToast(error)
            hideProgressView(progressBar)
        }

        override fun showResponse(response: CommonResponse, type: ConstantsApi) {
            when (type) {
                ConstantsApi.BLOCK -> {
                    finish()
                }

                ConstantsApi.SEND_CONNECTION_REQUEST -> {
                    Log.e(
                        "SEND_CONNECTION_REQUEST",
                        "Api_REsponse_Rec ${response.sendConnectionReqResponse.receiver_id}"
                    )
                    Log.e(
                        "SEND_CONNECTION_REQUEST",
                        "Status ${response.sendConnectionReqResponse.connection_status}"
                    )
                }

                ConstantsApi.FETCH_CHAT -> {
                    LogManager.logger.i(
                        ArchitectureApp.instance!!.tag, "Keys is : ${Gson().toJson(response.chat)}"
                    )
                    chatAdapter!!.addAll(response.chat)
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

                    updateChatUi()
                }

                ConstantsApi.SHARE_PRIVATE -> {
                    if (response.isShareStatus > 0) {
                        lockMenuItem.icon = ContextCompat.getDrawable(
                            this@ChatActivity, R.drawable.ic_unlocked
                        )
                        universalToast(response.message)
                    } else {
                        lockMenuItem.icon = ContextCompat.getDrawable(
                            this@ChatActivity, R.drawable.ic_locked_gray
                        )
                    }
                }

                else -> {
                }
            }
            hideProgressView(progressBar)
        }
    }
    override val lifecycleScope: Any
        get() = this.lifecycleScope

    override fun onThumbImageUploaded(url: String) {

    }

    override fun onOriginalImageUploaded(
        thumbUrl: String,
        originalUrl: String,
        fileLength: Long,
        thumbBitmap: Bitmap,
        originalBitmap: Bitmap,
        currentMiliSeconds: Long
    ) {
        uploadDialog!!.setProgress(100)
        if (current == -1) {
            try {
                sendImage(thumbUrl, originalUrl, fileLength)
            } catch (e: IOException) {
                e.printStackTrace()
            }
            Handler().post { closeUploadDialog() }
        } else {
            uploaded.add(UploadedGalleryImage(originalUrl, thumbUrl, fileLength))
            uploadGalleryImage(images, current + 1)
        }

    }

    override fun onImageUploadFailure(error: String) {
        if (current != -1) {
            uploadGalleryImage(images, current + 1)
        }
        closeUploadDialog()
        universalToast("Image uploading failed.")
    }

    override fun onThumbVideoUploaded(url: String) {
    }

    override fun onVideoUploaded(
        thumbUrl: String,
        videoUrl: String,
        videoSize: Long,
        currentMiliSeconds: Long,
        bitmapVideoThumb: Bitmap,
        uploadedFile: File
    ) {

        try {
            closeUploadDialog()
            sendVideo(thumbUrl, videoUrl, videoSize)

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun onVideoUploadFailure(error: String) {
        closeUploadDialog()
        universalToast("Video Upload Failed")
    }

    override fun PdfiumCore(chatActivity: ChatActivity): Any {
        return ""
    }


    private val onTypingTimeout = Runnable {

        if (!mTyping) return@Runnable
        LogManager.logger.i(ArchitectureApp.instance!!.tag, "Token is Typing TimeOut :")
        mTyping = false
        isTyping(mTyping)
    }

    private var photoFile: File? = null
    private var photoFileUri: Uri? = null
    private var videoFileUri: Uri? = null
    private var audioFileUri: Uri? = null
    private var fileUri: Uri? = null
    private var imgAbsolutePath = ""
    private var videoAbsolutePath = ""
    private var fileAbsolutePath = ""
    private var mediaSize: Long = 0
    private var mediaSizeStr = ""
    private var mediaName = ""
    private var audioAbsolutePath = ""

    @Inject
    lateinit var firebaseUtil: FirebaseUtil

    @Inject
    lateinit var apiConnect: ApiConnect

    @Inject
    lateinit var sharedPreferencesUtil: SharedPreferencesUtil

    private var userImage = ""
    private var status = 1
    private var conversation: ConversationModel? = null
    private var otherUserConversation: ConversationModel? = null

    private var activeConvoId = ""

    private var message = ""
    private var current = -1
    private var images = arrayListOf<File>()
    private val uploaded = arrayListOf<UploadedGalleryImage>()

    @Suppress("PrivatePropertyName")
    private val ONE_MB: Long = 8000000

    private var chatAdapter: ChatAdapter? = null
    private var uploadDialog: UploadDialogCustom? = null
    private var uploadMediaObj: UploadChatMediaOnFireabase? = null

    private var linearLayoutManager: LinearLayoutManager? = null

    private val typingTimer = 600L
    private val mTypingHandler = Handler()
    private var mTyping = false
    private var isOtherUserPushEnabled = true
    private var isReplying = false
    private lateinit var chatDeleteIc: MenuItem
    private lateinit var menuClear: MenuItem

    private var unreadCount = 0

    private var mPresenter: ChatConnector.PresenterOps? = null
    private var mPresenterProfile: UserProfileConnector.PresenterOps? = null
    private var mProfilePresenter: UserProfileConnector.PresenterOps? = null

    lateinit var myDbHandeler: MyDbHandeler

    var permissions = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE
    )
    var requestCode = 1

    private var hasSharedPrivateAlbumWithMe = true
    private var isMyPrivateAlbumSharedWithUser = true
    private val PREVIEW_REQUEST_CODE = 999

    var userId = ""
    var otherUserId = ""
    var chatRoomId = ""
    var otherUserName = ""
    var otherUserImg = ""
    var IsUserConnected = ""
    var onCurrentActivity = true
    lateinit var profile: ResponseUserData
    lateinit var otherUserProfile: ResponseUserData
    var recycleViewPersonalChatAdapter: RecycleViewPersonalChatAdapter? = null
    lateinit var recycleModel: ArrayList<RecycleModel>
    lateinit var personalChatModel: PersonalChatModel
    lateinit var chatDb: DatabaseReference
    lateinit var chatListDb: DatabaseReference
    var replyTo = ""
    var replyMsg = ""
    var replyMsgTime = ""
    var chatId = ""
    var msgToCopy = ""
    var chatImg = ""
    var rvChatScrollPos = 0
    var loadMoreChat = 50
    var isSending = false
    var isSendingImg = false
    var isSendingVideo = false
    var isSendingFile = false
    lateinit var popupWindow: PopupWindow
    var deleteChatsArr = ArrayList<String>()
    var msgIdArr = ArrayList<String>()
    var tempChatList = ArrayList<RecycleModel>()
    lateinit var recyclerViewState: Parcelable
    var lastChatSize = 0
    var lastReadIndex = 0
    var loadMore = true
    var firstTime = false
    var unSeenCount = 0
    var connection_status = ""
    var deleteFor = ""
    var newMsgIndex = ArrayList<Int>()
    var tempRV: ArrayList<RecycleModel>? = null
    var chatsArr = ArrayList<PersonalChatModel>()
    var bitmapVideoThumb: Bitmap? = null
    val TAG = "GIPHY TAG"
    var newMsgId = ""
    var fileExt = ""
    var settings =
        GPHSettings(gridType = GridType.waterfall, theme = GPHTheme.Light, stickerColumnCount = 3)
    var contentType = GPHContentType.gif
    private var connectionPresenter: ConnectionRequestConnector.PresenterOps? = null

    lateinit var lockMenuItem: MenuItem
    lateinit var copyMenuItem: MenuItem

    private var loadMoreGloble: Boolean = false
    private var loadMoreDataSize: Int = 0
    private var firstVisiblePosition: Int = 0
    private var newDataHeight: Int = 0
    private var topOffset: Int = 0
    private var firstVisibleView: View? = null
    private var lastMsgKey: String? = null // To track the last message key for pagination
    private var isLoadingMore = false // To prevent multiple simultaneous loads
    private val messagesPerPage = 20 // Number of messages to load per page

    private var binding: ActivityChatBinding? = null
    private lateinit var progressBar: LinearLayout


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        component.inject(this@ChatActivity)
        setContentView(binding!!.root)
        initBinding(this) // Pass the valid Context here
        progressBar = binding!!.includedLoading.rlProgress
        // setContentView(R.layout.activity_chat)
        getIntentData()
        setupActionBarSearch(false)
        startLoadingAnim()

        Giphy.configure(this, Constants.GIPHY_API_KEY)
        VideoCache.initialize(this, 100 * 1024 * 1024)

        binding!!.header.addGroupMembers.visibility = View.GONE
        binding!!.header.ivClearChat.visibility = View.VISIBLE
        binding!!.header.chatDetails.visibility = View.GONE

        profile = sharedPreferencesUtil.fetchUserProfile()

        userId = profile.userId
        Constants.currentUser = profile.name
        //otherUserId = conversation!!.otherUserId

        fetchOtherUserProfile()

        getSharedPreferences("UserId", MODE_PRIVATE).edit().putString("UserId", userId).apply()
        chatDb =
            FirebaseDatabase.getInstance(Constants.Firebase.databaseUrl).getReference(Constants.Firebase.ChatDB)
                .child("ChatMessages")
        Log.e("ChatIds", userId + "  " + otherUserId)
        ///   chatRoomId = userId + "-" + otherUserId

        chatListDb =
            FirebaseDatabase.getInstance(Constants.Firebase.databaseUrl).getReference(Constants.Firebase.ChatDB)
                .child("ChatList")

        linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        linearLayoutManager!!.stackFromEnd = true
        binding!!.rvChat.layoutManager = linearLayoutManager
        binding!!.rvChat.setHasFixedSize(true)

        /*if (null == conversation) {
            finish()
            return
        }*/

        myDbHandeler = MyDbHandeler(this@ChatActivity)

        if (chatRoomId.isEmpty() || chatRoomId == "null") {

            showProgressView(progressBar)
            getChatRoomId()
        }

        checkChatExistsOrNot()

        setUpOtherUserDetails()

        /* io.fabric.sdk.android.services.concurrency.AsyncTask.execute(object : Runnable {
             override fun run() {
                 if (profile.isProMembership) {
                     Log.e("chatActivity*&", "getChatLOadMore_Call")
                     getAllChatMsgs(isLoadMore = false)
                 } else {
                     Log.e("chatActivity*&", "getAllChatMsgForFree_Call ")
                     getAllChatMsgForFree()
                 }
             }
         })*/


        CoroutineScope(Dispatchers.IO).launch {
            if (profile.isProMembership) {
                Log.e("chatActivity*&", "getChatLoadMore_Call")
                getAllChatMsgs(isLoadMore = false)
            } else {
                Log.e("chatActivity*&", "getAllChatMsgForFree_Call")
                getAllChatMsgForFree()
            }
        }

        //downloadAndSaveImage("https://firebasestorage.googleapis.com/v0/b/meetupsfellow-eacdf.appspot.com/o/chatImgs%2F1672737263937.jpg?alt=media&token=fd1b50a8-c663-42ee-b521-8f643cc8db43")

        Log.d("ChatActivity&* ", "set_oneText")

        binding!!.ivBtnCancel.setOnClickListener {
            Log.e("chatActivity*& ", "Btn_Cancel")
            declineConnectionRequest(otherUserProfile.userId.toInt())
        }
        binding!!.header.ivClearChat.setOnClickListener {
            Log.e("chatActivity*& ", "ivClearChat_All")
            if (recycleModel.size > 0) {
                showAlertChatDialog("Clear Chat", getString(R.string.clear_all_chat), "clear")
            }
        }
        binding!!.header.deleteChat.setOnClickListener {
            Log.e("chatActivity*& ", "deleteChat_select")
            showDeleteMasgsDialog()
        }
        binding!!.header.copyChat.setOnClickListener {
            Log.e("chatActivity*& ", "copyChat_select")
            val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("label", msgToCopy)
            clipboard.setPrimaryClip(clip)
            Log.e("CopyMsg@# ", msgToCopy)
            Toast.makeText(this@ChatActivity, "Message copied to clipboard", Toast.LENGTH_SHORT)
                .show()
        }

        binding!!.ivBtnconnectNow.setOnClickListener {
            Log.e("chatActivity*& ", "Btn_connectNow")
            acceptConnectionRequest(otherUserProfile.userId.toInt())
        }

        binding!!.header.ivUserImage.setOnClickListener { profile() }

        binding!!.header.ivBack.setOnClickListener {
            val mngr = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager

            val taskList = mngr.getRunningTasks(10)

            if (taskList[0].numActivities == 1 && taskList[0].topActivity!!.className == this::class.java.name) {
                Log.e(TAG, "This is the last activity in the stack")
                val intent = Intent(
                    this, HomeActivity::class.java
                )
                startActivity(intent)
                finish()
            } else {
                Log.e(TAG, "activity allready exist in the stack")
                finish()
            }
        }

        binding!!.ivSend.setOnClickListener {

            isSending = true

            if (isSendingImg) {

                binding!!.etChat.isEnabled = false

                sendImgMsg(photoFileUri)

            } else if (isSendingVideo) {

                sendVideoMsg(videoFileUri)

            } else if (isSendingFile) {

                sendFile(fileUri)
            } else {

                binding!!.etChat.isEnabled = true

                if (binding!!.etChat.text.isNotEmpty() && binding!!.etChat.text.isNotBlank()) {

                    sendMsg(binding!!.etChat.text.toString())
                }
            }
        }

        if (sharedPreferencesUtil.fetchUserProfile().currentPlanInfo!!.chatMessageTypes == 0) {
            binding!!.giphyBtn.visibility = View.GONE
        } else {
            // giphyBtn.visibility = View.VISIBLE
            binding!!.giphyBtn.visibility = View.GONE
        }

        binding!!.ivAttachment.setOnClickListener {

            selectImagePopup()
            Log.e("Call_Api", "sendConnection")
            //ActionSheetDialogChat(this@ChatActivity, actionSheet).show()
        }

        binding!!.cancelReplyChat.setOnClickListener {

            binding!!.replyChatLay.visibility = View.GONE
            binding!!.replyLay.visibility = View.GONE
            binding!!.sendingImgLay.visibility = View.GONE

            isReplying = false
            isSendingImg = false
            isSendingVideo = false
            isSendingFile = false

            videoFileUri = null
            photoFileUri = null
            fileUri = null
            binding!!.etChat.isEnabled = true

            replyTo = ""
            replyMsg = ""
            replyMsgTime = ""
            chatId = ""
            chatImg = ""
        }

        binding!!.etChat.setOnClickListener {
            Log.d("Scroll_Nested ", "Click_EditText")
            binding!!.etChat.postDelayed({
                scrollToBottomNested()
            }, 100)
        }

        /* var maxScroll: Int = 0
         nestedScrollView.setOnScrollChangeListener { v: NestedScrollView?, scrollX: Int, scrollY: Int, oldScrollX: Int, oldScrollY: Int ->
             // Calculate scroll Distance from bottom
             val dy = scrollY - oldScrollY
             val viewHeight = v?.measuredHeight ?: 0
             val contentHeight = v?.getChildAt(0)?.measuredHeight ?: 0
             maxScroll = contentHeight - viewHeight
             val distanceFromBottom = maxScroll - scrollY
             newDataHeight = maxScroll

             // Calculate scroll position
             rvChatScrollPos += dy

             Log.d("Scroll_Nested ", "scrollY: $distanceFromBottom")
             // Check if you need to show additional content based on scroll position
             if (recycleModel.lastIndex > 10) {
                 if (nestedScrollView.canScrollVertically(1) && distanceFromBottom >= 170) {
                     Log.d("Scroll_Nested ", recycleModel[0].lastMsg.toString())

                     // Show additional content or perform other actions
                     scrollToStartLay.visibility = View.VISIBLE

                     if (unSeenCount != 0) {
                         newMsgCountLay.visibility = View.VISIBLE
                     } else {
                         newMsgCountLay.visibility = View.GONE
                     }
                 } else {
                     unSeenCount = 0
                     scrollToStartLay.visibility = View.GONE
                 }
             }
         }*/

        var maxScroll: Int = 0
        binding!!.nestedScrollView.setOnScrollChangeListener { v: NestedScrollView?, scrollX: Int, scrollY: Int, oldScrollX: Int, oldScrollY: Int ->
            // Calculate scroll Distance from bottom
            val dy = scrollY - oldScrollY
            val viewHeight = v?.measuredHeight ?: 0
            val contentHeight = v?.getChildAt(0)?.measuredHeight ?: 0
            maxScroll = contentHeight - viewHeight
            val distanceFromBottom = maxScroll - scrollY
            newDataHeight = maxScroll

            // Calculate scroll position
            rvChatScrollPos += dy

            Log.d("Scroll_Nested ", "scrollY: $scrollY")
            Log.d("Scroll_Nested ", "distanceFromBottom: $distanceFromBottom")
            // Check if you need to show additional content based on scroll position
            if (recycleModel.lastIndex > 10) {
                if (binding!!.nestedScrollView.canScrollVertically(1) && distanceFromBottom >= 170) {
                    Log.e("Scroll_Nested_", "IFF " + recycleModel[0].lastMsg.toString())

                    // Show additional content or perform other actions
                    binding!!.scrollToStartLay.visibility = View.VISIBLE
                    if (unSeenCount != 0) {
                        binding!!.newMsgCountLay.visibility = View.VISIBLE
                    } else {
                        binding!!.newMsgCountLay.visibility = View.GONE
                    }
                    if (scrollY == 0 && profile.currentPlanInfo!!.onetwooneChatHistoryLimit == 0 && profile.isProMembership) {
                        loadMoreChats()
                    }
                } else {
                    unSeenCount = 0
                    binding!!.scrollToStartLay.visibility = View.GONE
                    Log.e("Scroll_Nested_", "ELSS")
                }
            }
        }

        binding!!.rvChat.viewTreeObserver.addOnGlobalLayoutListener {

            if (binding!!.rvChat.adapter != null && binding!!.rvChat.adapter!!.itemCount > 0 && loadMoreGloble && loadMoreDataSize != 0) {
                Log.e("viewTreeObserver", "rvChat_viewTreeObserver: $loadMoreDataSize")
                // Calculate the total height of newly added items
                var newDataHeight = 0
                for (i in 0 until loadMoreDataSize) {
                    val viewHolder = binding!!.rvChat.findViewHolderForAdapterPosition(i)
                    viewHolder?.itemView?.let {
                        newDataHeight += it.height  // Add the height of each new item
                    }
                }
                val targetPosition = newDataHeight

                // Restore the previous scrollY position of the NestedScrollView
                Log.e("Scroll", "Dynamic new data height: $newDataHeight")
                Log.e("viewTreeObserver", "Target_position: $targetPosition")

                // Scroll the NestedScrollView to maintain the position
                binding!!.nestedScrollView.scrollTo(0, targetPosition - 50)
                loadMoreGloble = false
            }
        }


        binding!!.scrollToStartLay.setOnClickListener {
            scrollToBottomNested()
            binding!!.scrollToStartLay.visibility = View.GONE
        }

        binding!!.cvLimitChatHistory.setOnClickListener {
            AlertBuyPremium.Builder(this@ChatActivity, Constants.DialogTheme.NoTitleBar).build()
                .show()
        }

        binding!!.giphyBtn.setOnClickListener {

            val dialog =
                GiphyDialogFragment.newInstance(settings.copy(selectedContentType = contentType),
                    videoPlayer = { playerView, repeatable, showCaptions ->
                        VideoPlayerExoPlayer2181Impl(playerView, repeatable, showCaptions)
                    })
            dialog.gifSelectionListener = getGifSelectionListener()
            dialog.show(supportFragmentManager, "gifs_dialog")
        }

    }

    private fun scrollToBottomNested() {
        // Scroll to the bottom initially
        binding!!.rvChat.postDelayed({
            binding!!.nestedScrollView.post {
                /// nestedScrollView.smoothScrollTo(0, rvChat.bottom)
                Log.e("Send_Msg%", "Scroll_below")
                if (binding!!.rvChat.adapter != null && binding!!.rvChat.adapter!!.itemCount > 0) {
                    // Scroll to the last item in RecyclerView
                    binding!!.rvChat.scrollToPosition(binding!!.rvChat.adapter!!.itemCount - 1)
                    // Scroll NestedScrollView to the bottom
                    binding!!.nestedScrollView.scrollTo(
                        0, binding!!.nestedScrollView.getChildAt(0).bottom
                    )
                }
            }
        }, 100)
    }

    /* fun generateImageFromPdf(pdfUri: Uri?): Bitmap? {
         val pageNumber = 0
         val pdfiumCore = PdfiumCore(this@ChatActivity)
         var bmp: Bitmap? = null
         try {
             //http://www.programcreek.com/java-api-examples/index.php?api=android.os.ParcelFileDescriptor
             val fd = this@ChatActivity.contentResolver.openFileDescriptor(pdfUri!!, "r")
             val pdfDocument: com.shockwave.pdfium.PdfDocument = pdfiumCore.newDocument(fd)
             pdfiumCore.openPage(pdfDocument, pageNumber)
             val width: Int = pdfiumCore.getPageWidthPoint(pdfDocument, pageNumber)
             val height: Int = pdfiumCore.getPageHeightPoint(pdfDocument, pageNumber)
             Log.e("Width_height", "$width $height")
             bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
             pdfiumCore.renderPageBitmap(pdfDocument, bmp, pageNumber, 0, 0, width, height)
             pdfiumCore.closeDocument(pdfDocument)
         } catch (e: java.lang.Exception) {
             //todo with exception
         }
         return bmp
     }*/

    fun generateImageFromPdf(context: Context, pdfUri: Uri, pageNumber: Int = 0): Bitmap? {
        try {
            // Open the file descriptor from the URI
            val fileDescriptor = context.contentResolver.openFileDescriptor(pdfUri, "r") ?: return null

            // Create a PdfRenderer from the file descriptor
            val pdfRenderer = PdfRenderer(fileDescriptor)

            // Ensure the page number is within bounds
            if (pageNumber < 0 || pageNumber >= pdfRenderer.pageCount) {
                Log.e("PdfRenderer", "Invalid page number: $pageNumber")
                pdfRenderer.close()
                return null
            }

            // Open the requested page
            val page = pdfRenderer.openPage(pageNumber)

            // Create a bitmap with the dimensions of the page
            val bitmap = Bitmap.createBitmap(page.width, page.height, Bitmap.Config.ARGB_8888)

            // Render the page onto the bitmap
            page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)

            // Close the page and PdfRenderer
            page.close()
            pdfRenderer.close()

            // Return the generated bitmap
            return bitmap
        } catch (e: Exception) {
            Log.e("generateImageFromPdf", "Error rendering PDF: ${e.message}", e)
            return null
        }
    }


  /*  fun generateImageFromPdf(context: Context, pdfUri: Uri, pageNumber: Int = 0): Bitmap? {
        val fileDescriptor = context.contentResolver.openFileDescriptor(pdfUri, "r") ?: return null
        val pdfRenderer = PdfRenderer(fileDescriptor)
        val page = pdfRenderer.openPage(pageNumber)
        val bitmap = Bitmap.createBitmap(page.width, page.height, Bitmap.Config.ARGB_8888)
        page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
        page.close()
        pdfRenderer.close()
        return bitmap
    }*/


    private fun getGifSelectionListener() = object : GiphyDialogFragment.GifSelectionListener {
        override fun onGifSelected(
            media: Media, searchTerm: String?, selectedContentType: GPHContentType
        ) {
            Timber.tag(TAG).d("onGifSelected")
            Log.d(TAG, "onGifSelected")
            if (selectedContentType == GPHContentType.clips && media.isVideo) {/*messageItems.forEach {
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

                GPHCore.gifById(media.id) { result, e ->
                    //gifView.setMedia(result?.data, RenditionType.original)
                    e?.let {
                        //your code here
                    }
                }

                sendGifMsg(media)
                //showSelectedMedia(null)
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

    /*@SuppressLint("SuspiciousIndentation")
    private fun getAllChatMsgs() {

        if (firstTime) {
            Log.e("firstTime", "Visible_Update")
            progressbar_circal.visibility = View.VISIBLE
        }
        recycleModel = ArrayList()
        recycleModel.clear()
        val profile = sharedPreferencesUtil.fetchUserProfile()
        //scrollToMsg(lastMsgId)

        Log.e("ChatRoomId&*", chatRoomId)

        var allChats: Query = chatDb
            .child(chatRoomId)
            .child("chats")
        //.orderByChild("timestamp")
        //  .orderByChild("messageTime")

        /// for free user see only limited chat History
        if (profile.currentPlanInfo!!.onetwooneChatHistoryLimit != 0) {
            Log.e("Chat_Msg@#", "NOT_Pro")
            //allChats = allChats.limitToLast(20)
            val fortyEightHoursAgo = System.currentTimeMillis() - 3 * 24 * 60 * 60 * 1000
            allChats = allChats.orderByChild("messageTime")
                .startAt(fortyEightHoursAgo.toDouble())
        }

        allChats.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snap: DataSnapshot) {

                if (snap.exists()) {
                    Log.e("chatActivity*& ", "getChat_Exist*")
                    recycleModel.clear()
                    for (chats in snap.children) {

                        *//* if (userNonPro){
                             if (recycleModel.size > 20){
                                 break
                             }
                         }*//*
                        val senderId = chats.child("senderId").value.toString()
                        val senderName = chats.child("senderName").value.toString()
                        val msg = chats.child("messageText").value.toString()
                        val msgTime = chats.child("msgTime").value.toString()
                        val isRead = chats.child("deliveryStatus").value.toString()
                        ///  val isRead = chats.child("messageStatus").value.toString()
                        val mediaUrl = chats.child("mediaUrl").value.toString()
                        val mediaSize = chats.child("mediaSize").value.toString()
                        //val mediaSize = chats.child("mediaLength").value.toString()
                        val isClearedSelf =
                            chats.child("messagesDeletedBY").child(userId).value.toString()
                                .toBoolean()
                        val isClearedOther =
                            chats.child("messagesDeletedBY").child(otherUserId).value.toString()
                                .toBoolean()
                        val replyTo = chats.child("replyTo").value.toString()
                        val replyMsg = chats.child("repliedOnText").value.toString()
                        val chatId = chats.key.toString()
                        val replyId = chats.child("repliedOnMessageId").value.toString()
                        val replyImg = chats.child("repliedOnMediaUrl").value.toString()
                        //val receiverId = chats.child("receiverId").value.toString()
                        // val receiverName = chats.child("receiverName").value.toString()
                        val imgPathSelf =
                            chats.child("mediaDeviceUrlOf").child(userId).value.toString()
                        val imgPathOther =
                            chats.child("mediaDeviceUrlOf").child(otherUserId).value.toString()
                        //val videoUrl = chats.child("mediaUrl").value.toString()
                        val thumbImg = chats.child("thumbnailUrl").value.toString()
                        val mediaType = chats.child("mediaType").value.toString()
                        val gifId = chats.child("GifId").value.toString()

                        var imageUrl = ""
                        var videoUrl = ""
                        var fileUrl = ""

                        when (mediaType) {

                            "1" -> {}

                            "2" -> {
                                imageUrl = mediaUrl
                                videoUrl = ""
                                fileUrl = ""
                            }

                            "3" -> {}

                            "4" -> {
                                videoUrl = mediaUrl
                                imageUrl = ""
                                fileUrl = ""
                            }

                            "5" -> {

                                fileUrl = mediaUrl
                                imageUrl = ""
                                videoUrl = ""
                            }
                        }

                        var isReadMsg = false

                        isReadMsg = isRead == "3"


                        if (!isClearedSelf) {

                            if (senderName != "null" && msgTime != "null" && senderId != "null") {

                                recycleModel.add(
                                    RecycleModel(
                                        senderName,
                                        msg,
                                        msgTime,
                                        senderId,
                                        isRead,
                                        "",
                                        imageUrl,
                                        replyTo,
                                        replyMsg,
                                        chatId,
                                        replyId,
                                        replyImg,
                                        imgPathSelf,
                                        thumbImg,
                                        videoUrl,
                                        gifId,
                                        fileUrl,
                                        mediaSize
                                    )
                                )

                            }
                        }

                    }
                    *//*   val last20Messages = recycleModel.asReversed().take(20)

                       // Clear recycleModel and add last 20 messages
                       recycleModel.clear()
                       recycleModel.addAll(last20Messages)*//*

                    Log.e("ChatActivity", "recycleModel_Size: ${recycleModel.size}")
                    //rvChat.setItemViewCacheSize(recycleModel.size)
                    if (lastChatSize == 0) {

                        lastChatSize = recycleModel.size
                    }

                    if (lastReadIndex == 0) {

                        lastReadIndex = recycleModel.lastIndex
                    }

                    if (recycleViewPersonalChatAdapter == null && recycleModel.size > 0) {

                        recycleViewPersonalChatAdapter =
                            RecycleViewPersonalChatAdapter(this@ChatActivity)

                        recycleViewPersonalChatAdapter!!.addChats(recycleModel)

                        rvChat.adapter = recycleViewPersonalChatAdapter

                        recycleViewPersonalChatAdapter!!.notifyDataSetChanged()

                        lastChatSize = recycleModel.size
                        Log.e("Send_Msg%", "getChat11")

                    }
                    Log.d("ChatSize", lastChatSize.toString() + "   " + recycleModel.size)
                    Log.d("ChatSending", isSending.toString())

                    if (onCurrentActivity && !isSending) {

                        if (lastChatSize < recycleModel.size) {

                            Log.d("ChatSe", recycleModel.lastIndex.toString())

                            recycleViewPersonalChatAdapter!!.addChats(recycleModel)

                            recycleViewPersonalChatAdapter!!.notifyItemInserted(recycleModel.lastIndex)
                            Log.e("Send_Msg%", "getChat22")
                            //recycleViewPersonalChatAdapter!!.notifyDataSetChanged()
                            unSeenCount += 1
                            newMsgCountLay.visibility = View.VISIBLE
                            newMsgCount.text = unSeenCount.toString()

                            if (linearLayoutManager!!.findLastVisibleItemPosition() == recycleModel.lastIndex - 1) {

                                //rvChat.smoothScrollToPosition(recycleModel.lastIndex)
                                rvChat.adapter = recycleViewPersonalChatAdapter
                                recycleViewPersonalChatAdapter!!.notifyDataSetChanged()
                                Log.e("Send_Msg%", "getChat33")
                            }

                            lastChatSize = recycleModel.size

                        } else if (lastChatSize > recycleModel.size) {

                            recycleViewPersonalChatAdapter!!.addChats(recycleModel)

                            //recycleViewPersonalChatAdapter!!.notifyItemInserted(recycleModel.lastIndex)
                            recycleViewPersonalChatAdapter!!.notifyDataSetChanged()
                            Log.e("Send_Msg%", "getChat44")

                            lastChatSize = recycleModel.size
                        } else {

                            //recycleViewPersonalChatAdapter!!.addChats(recycleModel)

                            Log.d("ChatStatus", recycleModel.lastIndex.toString())

                            //val newData = recycleModel.lastIndex - lastReadIndex

                            if (lastReadIndex != 0) {

                                for (i in lastReadIndex + 1..recycleModel.lastIndex) {

                                    Log.d("ChatIndexs", i.toString())

                                    recycleViewPersonalChatAdapter!!.updateChats(recycleModel[i])
                                    recycleViewPersonalChatAdapter!!.notifyItemChanged(
                                        i,
                                        recycleModel[i]
                                    )
                                    Log.e("Send_Msg%", "getChat55")
                                }
                            }

                            if (recycleModel.lastIndex != -1) {

                                recycleViewPersonalChatAdapter!!.updateChats(recycleModel[recycleModel.lastIndex])
                                recycleViewPersonalChatAdapter!!.notifyItemChanged(
                                    recycleModel.lastIndex,
                                    recycleModel[recycleModel.lastIndex]
                                )
                                Log.e("Send_Msg%", "getChat66")
                            }


                            lastChatSize = recycleModel.size
                            lastReadIndex = recycleModel.lastIndex
                        }
                    } else {

                        *//*recycleViewPersonalChatAdapter!!.addChats(recycleModel)

                        recycleViewPersonalChatAdapter!!.notifyDataSetChanged()

                        lastChatSize = recycleModel.size*//*
                    }

                    /// new comment
                    if (isSending) {
                        *//* recycleViewPersonalChatAdapter =
                             RecycleViewPersonalChatAdapter(this@ChatActivity)

                         recycleViewPersonalChatAdapter!!.addChats(recycleModel)

                         rvChat.adapter = recycleViewPersonalChatAdapter

                          recycleViewPersonalChatAdapter!!.notifyDataSetChanged()*//*
                        //recycleViewPersonalChatAdapter!!.notifyItemInserted(recycleModel.lastIndex)
                        Log.e("Send_Msg&*", "getChat77")
                        Handler().postDelayed(object : Runnable {

                            override fun run() {
                                isSending = false
                            }
                        }, 300)

                        newMsgIndex.add(recycleModel.lastIndex)
                        lastChatSize = recycleModel.size
                        isSendingImg = false
                    }


                    deleteChatsArr.clear()
                    msgIdArr.clear()

                    Handler().postDelayed(object : Runnable {
                        override fun run() {
                            chatDeleteIc.isVisible = false
                            copyMenuItem.isVisible = false
                            if (scrollToStartLay.visibility == View.GONE) {
                                Log.e("Recycler$", "Is_Bottom")
                                scrollToBottomNested()
                            }
                            if (firstTime) {
                                Log.e("firstTime", " GOne^*")
                                firstTime = false
                                progressbar_circal.visibility = View.GONE
                                ///  no_chatImg.visibility = View.GONE
                                ///  tvNoMessage.visibility = View.GONE
                            }
                        }
                    }, 1000)

                    if (onCurrentActivity) {

                        readMsgs()
                    }

                    updateChatUi()
                } else {
                    Log.e("firstTime%#", " No_Chat_availeble")
                    firstTime = false
                    progressbar_circal.visibility = View.GONE
                    no_chatImg.visibility = View.VISIBLE
                    tvNoMessage.visibility = View.VISIBLE

                }

            }

            override fun onCancelled(p0: DatabaseError) {
                firstTime = false
                progressbar_circal.visibility = View.GONE
                no_chatImg.visibility = View.VISIBLE
                tvNoMessage.visibility = View.VISIBLE

            }


        })
    }*/

    private fun getAllChatMsgForFree() {
        recycleModel = ArrayList()
        if (firstTime) {
            Log.e("firstTime", "Visible_Update")
            binding!!.progressbarCircal.visibility = View.VISIBLE
        }
        Log.e("getAllChatMsgForFree", "ChatRoomId&* " + chatRoomId)

        var allChats: Query = chatDb.child(chatRoomId).child("chats")

        // For non-Pro users, limit the chat history
        Log.e("Chat_Msg@#", "NOT_Pro_getAllChatMsgForFree")
        val fortyEightHoursAgo = System.currentTimeMillis() - 3 * 24 * 60 * 60 * 1000
        allChats = allChats.orderByChild("messageTime").startAt(fortyEightHoursAgo.toDouble())
            .limitToLast(50)

        allChats.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snap: DataSnapshot) {
                if (snap.exists()) {
                    Log.e("chatActivity*& ", "getChat_Exist*")

                    recycleModel.clear()
                    val chatList = mutableListOf<RecycleModel>()

                    for (chats in snap.children) {
                        val senderId = chats.child("senderId").value.toString()
                        val senderName = chats.child("senderName").value.toString()
                        val msg = chats.child("messageText").value.toString()
                        val msgTime = chats.child("msgTime").value.toString()
                        val isRead = chats.child("deliveryStatus").value.toString()
                        val mediaUrl = chats.child("mediaUrl").value.toString()
                        val mediaSize = chats.child("mediaSize").value.toString()
                        val isClearedSelf =
                            chats.child("messagesDeletedBY").child(userId).value.toString()
                                .toBoolean()
                        val replyTo = chats.child("replyTo").value.toString()
                        val replyMsg = chats.child("repliedOnText").value.toString()
                        val chatId = chats.key.toString()
                        val replyId = chats.child("repliedOnMessageId").value.toString()
                        val replyImg = chats.child("repliedOnMediaUrl").value.toString()
                        val imgPathSelf =
                            chats.child("mediaDeviceUrlOf").child(userId).value.toString()
                        val thumbImg = chats.child("thumbnailUrl").value.toString()
                        val mediaType = chats.child("mediaType").value.toString()
                        val gifId = chats.child("GifId").value.toString()

                        var imageUrl = ""
                        var videoUrl = ""
                        var fileUrl = ""

                        when (mediaType) {
                            "2" -> imageUrl = mediaUrl
                            "4" -> videoUrl = mediaUrl
                            "5" -> fileUrl = mediaUrl
                        }

                        if (!isClearedSelf) {
                            if (senderName != "null" && msgTime != "null" && senderId != "null") {
                                chatList.add(
                                    RecycleModel(
                                        senderName,
                                        msg,
                                        msgTime,
                                        senderId,
                                        isRead,
                                        "",
                                        imageUrl,
                                        replyTo,
                                        replyMsg,
                                        chatId,
                                        replyId,
                                        replyImg,
                                        imgPathSelf,
                                        thumbImg,
                                        videoUrl,
                                        gifId,
                                        fileUrl,
                                        mediaSize
                                    )
                                )
                            }
                        }
                    }

                    if (chatList.isNotEmpty()) {
                        recycleModel.addAll(chatList)
                        Log.e("getAllChatMsgForFree*&", "recycleModel_Size: ${recycleModel.size}")
                        if (recycleViewPersonalChatAdapter == null && recycleModel.size > 0) {
                            Log.e("getAllChatMsgForFree&*", "handleRecycleViewUpdate_IFF")
                            recycleViewPersonalChatAdapter =
                                RecycleViewPersonalChatAdapter(this@ChatActivity)
                            recycleViewPersonalChatAdapter!!.addChats(recycleModel)
                            binding!!.rvChat.adapter = recycleViewPersonalChatAdapter
                            scrollToBottomNested()
                        } else {
                            Log.e("getAllChatMsgForFree&*", "handleRecycleViewUpdate_ELS")
                            recycleViewPersonalChatAdapter!!.addChats(recycleModel)
                            recycleViewPersonalChatAdapter!!.notifyDataSetChanged()
                            //  recycleViewPersonalChatAdapter!!.notifyItemRangeChanged(0, recycleModel.size)
                            if (binding!!.scrollToStartLay.visibility == View.GONE) {
                                Log.e("Recycler$", "Is_Bottom")
                                scrollToBottomNested()
                            }
                        }
                        binding!!.noChatImg.visibility = View.GONE
                        binding!!.tvNoMessage.visibility = View.GONE
                        binding!!.cvLimitChatHistory.visibility = View.VISIBLE
                    } else {
                        binding!!.noChatImg.visibility = View.VISIBLE
                        binding!!.tvNoMessage.visibility = View.VISIBLE
                        binding!!.tvLoadChat.visibility = View.GONE
                        binding!!.cvLimitChatHistory.visibility = View.GONE
                    }
                    firstTime = false
                    binding!!.progressbarCircal.visibility = View.GONE
                } else {
                    Log.e("getAllChatMsgForFree%#", "No_Chat_availeble")
                    firstTime = false
                    binding!!.progressbarCircal.visibility = View.GONE
                    binding!!.noChatImg.visibility = View.VISIBLE
                    binding!!.tvNoMessage.visibility = View.VISIBLE
                    binding!!.tvLoadChat.visibility = View.GONE
                    binding!!.cvLimitChatHistory.visibility = View.GONE
                }
            }

            override fun onCancelled(error: DatabaseError) {
                firstTime = false
                binding!!.progressbarCircal.visibility = View.GONE
                binding!!.noChatImg.visibility = View.VISIBLE
                binding!!.tvNoMessage.visibility = View.VISIBLE
                binding!!.tvLoadChat.visibility = View.GONE
                isLoadingMore = false
            }
        })
    }

    private fun getAllChatMsgs(isLoadMore: Boolean = false) {
        recycleModel = ArrayList()
        if (firstTime && !isLoadMore) {
            Log.e("firstTime", "Visible_Update")
            binding!!.progressbarCircal.visibility = View.VISIBLE
        }
        val profile = sharedPreferencesUtil.fetchUserProfile()
        Log.e("ChatRoomId&*", chatRoomId)

        var allChats: Query = chatDb.child(chatRoomId).child("chats").orderByKey()
            ///  .orderByChild("messagesDeletedBY").equalTo(false)
            .limitToLast(messagesPerPage)

        Log.e("chatActivity*&", "lastMsgKey- " + lastMsgKey)
        // Check if we are loading more messages (pagination)
        if (isLoadMore && lastMsgKey != null) {
            allChats = allChats.endAt(lastMsgKey)
            isLoadingMore = true
            binding!!.tvLoadChat.visibility = View.VISIBLE
        }

        // For non-Pro users, limit the chat history
        /* if (profile.currentPlanInfo!!.onetwooneChatHistoryLimit != 0) {
             Log.e("Chat_Msg@#", "NOT_Pro")
             val fortyEightHoursAgo = System.currentTimeMillis() - 3 * 24 * 60 * 60 * 1000
             allChats = allChats.startAt(fortyEightHoursAgo.toDouble())
         }*/

        allChats.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snap: DataSnapshot) {

                if (snap.exists()) {
                    Log.e("chatActivity*& ", "getChat_Exist*")

                    // If not loading more, clear the recycleModel
                    /*  if (!isLoadMore) {
                          recycleModel.clear()
                      }*/
                    loadMore = isLoadMore
                    recycleModel.clear()
                    val chatList = mutableListOf<RecycleModel>()

                    for (chats in snap.children) {
                        val senderId = chats.child("senderId").value.toString()
                        val senderName = chats.child("senderName").value.toString()
                        val msg = chats.child("messageText").value.toString()
                        val msgTime = chats.child("msgTime").value.toString()
                        val isRead = chats.child("deliveryStatus").value.toString()
                        val mediaUrl = chats.child("mediaUrl").value.toString()
                        val mediaSize = chats.child("mediaSize").value.toString()
                        val isClearedSelf =
                            chats.child("messagesDeletedBY").child(userId).value.toString()
                                .toBoolean()
                        val replyTo = chats.child("replyTo").value.toString()
                        val replyMsg = chats.child("repliedOnText").value.toString()
                        val chatId = chats.key.toString()
                        val replyId = chats.child("repliedOnMessageId").value.toString()
                        val replyImg = chats.child("repliedOnMediaUrl").value.toString()
                        val imgPathSelf =
                            chats.child("mediaDeviceUrlOf").child(userId).value.toString()
                        val thumbImg = chats.child("thumbnailUrl").value.toString()
                        val mediaType = chats.child("mediaType").value.toString()
                        val gifId = chats.child("GifId").value.toString()

                        var imageUrl = ""
                        var videoUrl = ""
                        var fileUrl = ""

                        when (mediaType) {
                            "2" -> imageUrl = mediaUrl
                            "4" -> videoUrl = mediaUrl
                            "5" -> fileUrl = mediaUrl
                        }

                        if (!isClearedSelf) {
                            if (senderName != "null" && msgTime != "null" && senderId != "null") {
                                chatList.add(
                                    RecycleModel(
                                        senderName,
                                        msg,
                                        msgTime,
                                        senderId,
                                        isRead,
                                        "",
                                        imageUrl,
                                        replyTo,
                                        replyMsg,
                                        chatId,
                                        replyId,
                                        replyImg,
                                        imgPathSelf,
                                        thumbImg,
                                        videoUrl,
                                        gifId,
                                        fileUrl,
                                        mediaSize
                                    )
                                )
                            }
                        }
                        // Set the lastMsgKey to the first (oldest) chat in the snapshot for pagination
                        /*  if (lastMsgKey == null) {
                              lastMsgKey = chatId
                          }*/
                        // lastMsgKey = chatId
                    }

                    if (chatList.isNotEmpty()) {
                        lastMsgKey = chatList.first().chatId
                        //recycleModel.addAll(0, chatList) // Add new chats to the top of the list
                        recycleModel.addAll(chatList)
                        Log.e("chatActivity*&", "recycleModel_Size: ${recycleModel.size}")
                        handleRecycleViewUpdate()
                        binding!!.noChatImg.visibility = View.GONE
                        binding!!.tvNoMessage.visibility = View.GONE
                    } else {
                        binding!!.noChatImg.visibility = View.VISIBLE
                        binding!!.tvNoMessage.visibility = View.VISIBLE
                        binding!!.tvLoadChat.visibility = View.GONE
                    }

                    firstTime = false
                    binding!!.progressbarCircal.visibility = View.GONE
                } else {
                    Log.e("firstTime%#", "No_Chat_availeble")
                    firstTime = false
                    binding!!.progressbarCircal.visibility = View.GONE
                    binding!!.noChatImg.visibility = View.VISIBLE
                    binding!!.tvNoMessage.visibility = View.VISIBLE
                    binding!!.tvLoadChat.visibility = View.GONE
                }
                isLoadingMore = false
            }

            override fun onCancelled(error: DatabaseError) {
                firstTime = false
                binding!!.progressbarCircal.visibility = View.GONE
                binding!!.noChatImg.visibility = View.VISIBLE
                binding!!.tvNoMessage.visibility = View.VISIBLE
                binding!!.tvLoadChat.visibility = View.GONE
                isLoadingMore = false
            }
        })
    }

    private fun handleRecycleViewUpdate() {
        if (recycleViewPersonalChatAdapter == null && recycleModel.size > 0) {
            Log.e("Data_SetRecycle&*", "handleRecycleViewUpdate_IFF")
            recycleViewPersonalChatAdapter = RecycleViewPersonalChatAdapter(this@ChatActivity)
            recycleViewPersonalChatAdapter!!.addChats(recycleModel)
            binding!!.rvChat.adapter = recycleViewPersonalChatAdapter
            scrollToBottomNested()
        } else if (recycleModel.isNotEmpty() && loadMore) {
            firstVisiblePosition = linearLayoutManager!!.findFirstVisibleItemPosition()
            firstVisibleView = linearLayoutManager!!.findViewByPosition(firstVisiblePosition)
            topOffset = firstVisibleView?.top ?: 0
            Log.e("Data_SetRecycle&*", "handleRecycleViewUpdate_ELS_IFF")
            recycleModel.removeLast()
            recycleViewPersonalChatAdapter!!.appendNewData(recycleModel)
            loadMore = false
            Handler().postDelayed(object : Runnable {
                override fun run() {
                    binding!!.tvLoadChat.visibility = View.GONE
                }
            }, 1000)

        } else {
            Log.e("Data_SetRecycle&*", "handleRecycleViewUpdate_ELS")
            recycleViewPersonalChatAdapter!!.addChats(recycleModel)
            recycleViewPersonalChatAdapter!!.notifyDataSetChanged()
            //  recycleViewPersonalChatAdapter!!.notifyItemRangeChanged(0, recycleModel.size)
            if (binding!!.scrollToStartLay.visibility == View.GONE) {
                Log.e("Recycler$", "Is_Bottom")
                scrollToBottomNested()
            }
        }
    }

    fun loadMoreChats() {
        if (!isLoadingMore) {
            Log.e("chatActivity*&", "LOAD_More_chat")
            getAllChatMsgs(isLoadMore = true)
        }
    }

    fun mantanScrollPostion(
        newDataSize: Int, chats: ArrayList<RecycleModel>
    ) {
         recycleModel.clear()
         recycleModel.addAll(chats)
        // Screen width and height in pixels
        //    val screenHeight = resources.displayMetrics.heightPixels
        loadMoreDataSize = newDataSize
        binding!!.rvChat.postDelayed({
            binding!!.nestedScrollView.post {
                if (binding!!.rvChat.adapter != null && binding!!.rvChat.adapter!!.itemCount > 0) {
                    // Scroll RecyclerView to the same position with the top offset
                    linearLayoutManager!!.scrollToPositionWithOffset(
                        firstVisiblePosition + newDataSize, topOffset
                    )
                    // Restore the previous scrollY position of the NestedScrollView
                   ///  binding!!.nestedScrollView.scrollTo(0, firstVisiblePosition + newDataSize)
                }
                loadMoreGloble = true
            }
        }, 500)
    }

    private fun showPopupWindow(anchor: View) {

        Log.e("NotificationPermission", "ChatActivity_showPopupWindow")

        PopupWindow(anchor.context).apply {
            isOutsideTouchable = true
            val inflater = LayoutInflater.from(anchor.context)
            contentView = inflater.inflate(R.layout.custom_popup_window, null)
            this.height = WindowManager.LayoutParams.WRAP_CONTENT
            this.width = WindowManager.LayoutParams.WRAP_CONTENT
            this.setBackgroundDrawable(BitmapDrawable())

        }.also { popupWindow ->
            // Absolute location of the anchor view
            val location = IntArray(2).apply {
                anchor.getLocationOnScreen(this)
            }
            val size = Size(
                popupWindow.contentView.measuredWidth, popupWindow.contentView.measuredHeight
            )

            binding!!.rvChat.post {

                popupWindow.showAtLocation(
                    anchor, Gravity.TOP or Gravity.CENTER, 0, 200
                )
            }

            //popupWindow.showAsDropDown(anchor, 0, 0)
            this.popupWindow = popupWindow

            Handler().postDelayed(object : Runnable {
                override fun run() {

                    popupWindow.dismiss()
                }
            }, 200)

        }

    }

    fun replyToChat(
        from: String, msg: String, msgTime: String, id: String, img: String, imgPath: String
    ) {

        isReplying = true
        val imgMsg = img
        binding!!.replyChatLay.visibility = View.VISIBLE
        binding!!.replyLay.visibility = View.VISIBLE
        binding!!.sendingImgLay.visibility = View.GONE

        Log.e("swipeChat", "replayToChat:: " + imgMsg)
        Log.e("swipeChat", "replayToChatId:: " + id)
        binding!!.userReplyName.text = from
        binding!!.replyChatMessage.text = msg
        binding!!.replyChatTime.text = msgTime

        if (msg == "image" || msg == "GIF") {

            binding!!.replyToImg.visibility = View.VISIBLE
            binding!!.videoIc.visibility = View.GONE

            Glide.with(this@ChatActivity).load(img).thumbnail(0.01f).override(30, 30)
                .placeholder(R.drawable.meetupsfellow_transpatent).into(binding!!.replyToImg)

        } else if (msg == "video") {

            binding!!.replyToImg.visibility = View.VISIBLE
            binding!!.videoIc.visibility = View.VISIBLE

            Glide.with(this@ChatActivity).load(img).thumbnail(0.01f).override(30, 30)
                .placeholder(R.drawable.meetupsfellow_transpatent).into(binding!!.replyToImg)

        } else {

            binding!!.replyToImg.visibility = View.GONE
            binding!!.videoIc.visibility = View.GONE
        }

        replyTo = from
        replyMsg = msg
        replyMsgTime = msgTime
        chatId = id
        chatImg = imgMsg
    }

    private fun checkChatExistsOrNot() {
        chatListDb.child(userId).child(chatRoomId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snap: DataSnapshot) {

                    if (snap.exists()) {

                        if (snap.child("blockedBy").value.toString()
                                .isNotEmpty() && snap.child("blockedBy").value.toString() != "null"
                        ) {

                            if (snap.child("blockedBy").value.toString() == otherUserId) {

                                binding!!.rlBlocked.visibility = View.VISIBLE
                                binding!!.tvBlock.text = String.format(
                                    getString(R.string.text_blocked_you), otherUserName
                                )

                            } else {

                                binding!!.rlBlocked.visibility = View.VISIBLE
                                binding!!.tvBlock.text = String.format(
                                    getString(R.string.text_you_block), otherUserName
                                )
                            }

                        } else {

                            binding!!.rlBlocked.visibility = View.GONE

                            if (onCurrentActivity) {

                                readMsgs()
                            }
                        }
                    }

                }

                override fun onCancelled(p0: DatabaseError) {


                }
            })

    }

    private fun readMsgs() {
        chatDb.child(chatRoomId).child("chats")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snap: DataSnapshot) {

                    if (snap.exists()) {

                        for (chats in snap.children) {

                            chatListDb.child(userId).child(chatRoomId).child("unReadCount")
                                .setValue("0")

                            if (chats.child("senderId").value.toString() != profile.userId && chats.child(
                                    "deliveryStatus"
                                ).value.toString() != "3"
                            ) {
                                val data = 3
                                chatDb.child(chatRoomId).child("chats").child(chats.key.toString())
                                    .child("deliveryStatus").setValue("3")

                                chatDb.child(chatRoomId).child("chats").child(chats.key.toString())
                                    .child("messageStatus").setValue(data)

                                chatListDb.child(userId).child(chatRoomId).child("messageStatus")
                                    .setValue(data)
                                chatListDb.child(otherUserId).child(chatRoomId)
                                    .child("messageStatus").setValue(data)

                                chatListDb.child(userId).child(chatRoomId).child("deliveryStatus")
                                    .setValue("3")
                                chatListDb.child(otherUserId).child(chatRoomId)
                                    .child("deliveryStatus").setValue("3")

                            }
                        }
                    }

                }

                override fun onCancelled(p0: DatabaseError) {

                }


            })

    }

    fun downloadAndSaveImage(urlImg: String, id: String, type: Int) {

        // Check if the permission is already granted
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
            if (ContextCompat.checkSelfPermission(
                    this, Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // Permission is not granted, request it
                requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                Log.e("TestImgChat", "Request_Permission")
                return
            } else {
                // Permission already granted, proceed with your operations
                Log.e("TestImgChat", "Write_Parmission_Already_given")
            }
        }

        //if (!isImgExists) {

        Log.e("TestImgChat_Personal", "file $urlImg ,Id: $id")
        val imgPathDB = chatDb.child(chatRoomId).child("chats").child(id)

        if (urlImg.contains(".png") || urlImg.contains(".jpg") || urlImg.contains(".jpeg") || urlImg.contains(
                ".mp4"
            ) || urlImg.contains(".mkv") || type == 2 || type == 4
        ) {
            Log.e("TestImgChat", "in_Iff_IMG_Vid")/* val downloadsFiles = DownloadsFiles(context!!, chatDb, userId)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                downloadsFiles.downloadFile(urlImg)
            }else{
                universalToast("You cna't download this file")
            }*/

            DownloadsImage(this, imgPathDB, userId, type).execute(urlImg)

        } /*else if (urlImg.contains(".pdf") || urlImg.contains(".xls") || urlImg.contains(".xlsx")
            || urlImg.contains(".doc") || urlImg.contains(".docx") || urlImg.contains(".txt") || urlImg.contains(".ppt") || type == 3
        ) {

            DownloadsFiles(this, imgPathDB, userId).execute(urlImg)

        }*/ else {
            Log.e("TestImgChat", "in_Elss_File")/* val downloadsFiles = DownloadsFiles(context!!, chatDb, userId)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                downloadsFiles.downloadFile(urlImg)
            }else{
                universalToast("You cna't download this file")
            }*/
            DownloadsFiles(this, imgPathDB, userId).execute(urlImg)
        }
        //  testImgChat.setImageURI(imgUri)
        // }
    }

    fun chatsToDelete(chatId: String) {
        /// Add_below user connect or not
        if (otherUserProfile.connection_status.equals("connected")) {
            Log.e("chatActivity*& ", "chatsToDelete#$")
            if (deleteChatsArr.contains(chatId)) {

                deleteChatsArr.remove(chatId)

                Log.d("deletedChatR", chatId)

            } else {
                deleteChatsArr.add(chatId)
                Log.d("deletedChatA", chatId)
            }
        } else {
            //Toast.makeText(this,"Sorry you can't delete this massage",Toast.LENGTH_SHORT).show()
        }

        if (deleteChatsArr.isNotEmpty()) {
            binding!!.header.deleteChat.visibility = View.VISIBLE
            binding!!.header.copyChat.visibility = View.VISIBLE
        } else {
            binding!!.header.deleteChat.visibility = View.GONE
            binding!!.header.copyChat.visibility = View.GONE
        }
        /// chatDeleteIc.isVisible = !deleteChatsArr.isEmpty()
        //  binding!!.header.deleteChat.isVisible = deleteChatsArr.isNotEmpty()
    }

    fun showCopyOption(msg: String, senderName: String, msgTime: String, msgId: String) {
        ///  val tempMsg = "[$msgTime] $senderName : $msg\n"
        val tempMsg = msg
        Log.d("CopyMsg@#11", tempMsg)
        if (msgToCopy.contains(tempMsg)) {
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
            Log.d("CopyMsg@#22", tempMsg)
            Log.d("CopyMsgET", tempMsg)

            msgToCopy += tempMsg
            msgIdArr.add(msgId)
        }

        Log.e("CopyMsgA%", msgToCopy)
        if (msgToCopy.isNotBlank()) {
            binding!!.header.copyChat.visibility = View.VISIBLE
            binding!!.header.deleteChat.visibility = View.VISIBLE
        } else {
            binding!!.header.copyChat.visibility = View.GONE
            binding!!.header.deleteChat.visibility = View.GONE
        }
        //   copyMenuItem.isVisible = msgToCopy.isNotBlank()
        ///  binding!!.header.copyChat.isVisible = msgToCopy.isNotBlank()
    }

    private fun deleteChat() {

        /*for (id in recycleViewPersonalChatAdapter!!.selectedPos) {

            recycleViewPersonalChatAdapter!!.removeChats(id.toInt())
            //recycleViewPersonalChatAdapter!!.notifyItemRemoved(id.toInt())
        }*/
        Log.e("chatActivity*& ", "DeleteChat_By_Room")
        ///  chatDeleteIc.isVisible = false
        binding!!.header.deleteChat.visibility = View.GONE
        //deleteChatsArr.clear()
        //  copyMenuItem.isVisible = false
        binding!!.header.copyChat.visibility = View.GONE
        recycleViewPersonalChatAdapter!!.selectedPos.clear()
        recycleViewPersonalChatAdapter!!.notifyDataSetChanged()

        var lastMsg = ""
        var msg = ""


        chatDb.child(chatRoomId).child("chats").orderByKey().limitToLast(1)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snap: DataSnapshot) {

                    if (snap.exists()) {
                        for (chats in snap.children) {
                            lastMsg = chats.child("messageText").value.toString()
                            Log.e("chatActivity*& ", "Last_Msg: $lastMsg")
                        }
                    }
                }

                override fun onCancelled(p0: DatabaseError) {}

            })


        chatDb.child(chatRoomId).child("chats")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snap: DataSnapshot) {

                    if (snap.exists()) {

                        for (ids in snap.children) {

                            if (deleteChatsArr.contains(ids.key.toString())) {

                                if (deleteFor == "everyone") {

                                    /*  chatDb.child(chatRoomId).child("chats")
                                          .child(ids.key.toString()).removeValue()*/

                                    msg = ids.child("messageText").value.toString()
                                    Log.e("chatActivity*& ", "DeleteD_Msg: $msg")

                                    chatDb.child(chatRoomId).child("chats")
                                        .child(ids.key.toString()).child("messagesDeletedBY")
                                        .child(userId).setValue(true)

                                    chatDb.child(chatRoomId).child("chats")
                                        .child(ids.key.toString()).child("messagesDeletedBY")
                                        .child(otherUserId).setValue(true)

                                    if (msg == lastMsg) {

                                        chatListDb.child(userId).child(chatRoomId)
                                            .addListenerForSingleValueEvent(object :
                                                ValueEventListener {
                                                override fun onDataChange(snap: DataSnapshot) {

                                                    if (snap.exists()) {
                                                        chatListDb.child(userId).child(chatRoomId)
                                                            .child("messageText")
                                                            .setValue("Massage Deleted")
                                                    }
                                                }

                                                override fun onCancelled(p0: DatabaseError) {}

                                            })

                                        chatListDb.child(otherUserId).child(chatRoomId)
                                            .addListenerForSingleValueEvent(object :
                                                ValueEventListener {
                                                override fun onDataChange(snap: DataSnapshot) {

                                                    if (snap.exists()) {
                                                        if (snap.exists()) {
                                                            chatListDb.child(otherUserId)
                                                                .child(chatRoomId)
                                                                .child("messageText")
                                                                .setValue("Massage Deleted")
                                                        }
                                                    }
                                                }

                                                override fun onCancelled(p0: DatabaseError) {}

                                            })
                                    }

                                } else {
                                    msg = ids.child("messageText").value.toString()
                                    Log.e("chatActivity*& ", "DeleteD_Msg2: $msg")

                                    chatDb.child(chatRoomId).child("chats")
                                        .child(ids.key.toString()).child("messagesDeletedBY")
                                        .child(userId).setValue(true)

                                    if (msg == lastMsg) {
                                        chatListDb.child(userId).child(chatRoomId)
                                            .addListenerForSingleValueEvent(object :
                                                ValueEventListener {
                                                override fun onDataChange(snap: DataSnapshot) {

                                                    if (snap.exists()) {
                                                        chatListDb.child(userId).child(chatRoomId)
                                                            .child("messageText")
                                                            .setValue("Massage Deleted")
                                                    }
                                                }

                                                override fun onCancelled(p0: DatabaseError) {}

                                            })
                                    }

                                }
                            }
                        }
                        msgToCopy = ""
                        /// chatDeleteIc.isVisible = false
                        binding!!.header.deleteChat.visibility = View.GONE
                        binding!!.header.copyChat.visibility = View.GONE
                        deleteChatsArr.clear()
                    }
                }

                override fun onCancelled(p0: DatabaseError) {}

            })

    }

    fun scrollToMsg(replyMsg: String) {

        Log.d("replyMsg", replyMsg)

        for (i in recycleModel.indices) {

            Log.d("replyMsgF", recycleModel.get(i).replyMsg.toString())

            if (recycleModel.get(i).chatId.toString() == replyMsg) {

                Log.d("replyMsgIF", recycleModel.get(i).lastMsg.toString())
                Log.d("replyMsgP", i.toString())

                binding!!.rvChat.smoothScrollToPosition(i)
                scrollToPosition(i)

                //binding!!.rvChat.getChildAt(i).setBackgroundColor(Color.GRAY)
                //linearLayoutManager!!.findViewByPosition(i)!!.setBackgroundColor(Color.GRAY)

                Log.d("replyMsgAP", binding!!.rvChat.findViewHolderForAdapterPosition(i).toString())


            }
        }

    }

    // Define a method to scroll RecyclerView to a specific position
    private fun scrollToPosition(position: Int) {
        binding!!.nestedScrollView.post {
            val viewAtPosition = linearLayoutManager!!.findViewByPosition(position)
            if (viewAtPosition != null) {
                val y = viewAtPosition.y.toInt()
                binding!!.nestedScrollView.smoothScrollTo(0, y)
            } else {
                linearLayoutManager!!.scrollToPositionWithOffset(position, 0)
                binding!!.nestedScrollView.post {
                    val viewAtPosition = linearLayoutManager!!.findViewByPosition(position)
                    if (viewAtPosition != null) {
                        val y = viewAtPosition.y.toInt()
                        binding!!.nestedScrollView.smoothScrollTo(0, y)
                    }
                }
            }
        }
    }

    private fun getChatRoomId() {

        chatDb.child("$userId-$otherUserId")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snap: DataSnapshot) {

                    if (snap.exists()) {

                        chatRoomId = "$userId-$otherUserId"

                        Log.d("ChatRoomId", "$userId-$otherUserId")

                        fetchChatMsgs()
                        hideProgressView(progressBar)

                    } else {

                        chatDb.child("$otherUserId-$userId")
                            .addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(snap: DataSnapshot) {

                                    if (snap.exists()) {

                                        chatRoomId = "$otherUserId-$userId"

                                        Log.d("ChatRoomId", "$otherUserId-$userId")

                                        fetchChatMsgs()

                                        hideProgressView(progressBar)

                                    } else {

                                        chatRoomId = "$userId-$otherUserId"

                                        Log.d("ChatRoomId", "$userId-$otherUserId")

                                        hideProgressView(progressBar)
                                    }
                                }

                                override fun onCancelled(p0: DatabaseError) {

                                }

                            })
                    }
                }

                override fun onCancelled(p0: DatabaseError) {

                }


            })

    }

    private fun fetchChatMsgs() {

        recycleModel = ArrayList()
        recycleModel.clear()

        Log.d("ChatRoom", chatRoomId)

        val myTopChatsQuery: Query = chatDb.child(chatRoomId).child("chats")
            //.orderByChild("chats")
            .limitToLast(20)

        //chatDb.child(chatRoomId).child("chats")
        myTopChatsQuery.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snap: DataSnapshot) {

                if (snap.exists()) {

                    recycleModel.clear()

                    for (chats in snap.children) {
                        val senderId = chats.child("senderId").value.toString()
                        val receiverId = chats.child("receiverId").value.toString()
                        val senderName = chats.child("senderName").value.toString()
                        val msg = chats.child("messageText").value.toString()
                        val msgTime = chats.child("msgTime").value.toString()
                        val isRead = chats.child("deliveryStatus").value.toString()
                        /// val isRead = chats.child("messageStatus").value.toString()
                        val mediaUrl = chats.child("mediaUrl").value.toString()
                        val mediaSize = chats.child("mediaSize").value.toString()
                        // val mediaSize = chats.child("mediaLength").value.toString()

                        val isClearedSelf =
                            chats.child("messagesDeletedBY").child(userId).value.toString()
                                .toBoolean()
                        val isClearedOther =
                            chats.child("messagesDeletedBY").child(otherUserId).value.toString()
                                .toBoolean()
                        val replyTo = chats.child("replyTo").value.toString()
                        val replyMsg = chats.child("repliedOnText").value.toString()
                        val chatId = chats.key.toString()
                        val replyId = chats.child("repliedOnMessageId").value.toString()
                        val replyImg = chats.child("repliedOnMediaUrl").value.toString()
                        //val receiverId = chats.child("receiverId").value.toString()
                        // val receiverName = chats.child("receiverName").value.toString()
                        val imgPathSelf =
                            chats.child("mediaDeviceUrlOf").child(userId).value.toString()
                        val imgPathOther =
                            chats.child("mediaDeviceUrlOf").child(otherUserId).value.toString()
                        //val videoUrl = chats.child("mediaUrl").value.toString()
                        val thumbImg = chats.child("thumbnailUrl").value.toString()
                        val mediaType = chats.child("mediaType").value.toString()
                        //val gifId = chats.child("GifId").value.toString()


                        var imageUrl = ""
                        var videoUrl = ""
                        var fileUrl = ""

                        when (mediaType) {

                            "1" -> {}

                            "2" -> {
                                imageUrl = mediaUrl
                                videoUrl = ""
                                fileUrl = ""
                            }

                            "3" -> {}

                            "4" -> {
                                videoUrl = mediaUrl
                                imageUrl = ""
                                fileUrl = ""
                            }

                            "5" -> {

                                fileUrl = mediaUrl
                                imageUrl = ""
                                videoUrl = ""
                            }
                        }

                        var isReadMsg = false

                        isReadMsg = isRead == "3"

                        if (true) {

                            if (senderName != "null" && msg != "null" && msgTime != "null" && senderId != "null") {

                                recycleModel.add(
                                    RecycleModel(
                                        senderName,
                                        msg,
                                        msgTime,
                                        senderId,
                                        isRead,
                                        "",
                                        imageUrl,
                                        replyTo,
                                        replyMsg,
                                        chatId,
                                        replyId,
                                        replyImg,
                                        imgPathSelf,
                                        thumbImg,
                                        videoUrl,
                                        "",
                                        fileUrl,
                                        mediaSize
                                    )
                                )

                                /*io.fabric.sdk.android.services.concurrency.AsyncTask.execute(object : Runnable {
                                    override fun run() {

                                        val isChatExists =
                                            myDbHandeler.isChatExists(chats.key.toString().toLong())

                                        //Log.d("ChatEXISTSC", isChatExists.toString())

                                        personalChatModel = PersonalChatModel(
                                            chats.key.toString().toLong(),
                                            img,
                                            msg,
                                            msgTime,
                                            receiverId,
                                            receiverName,
                                            replyId,
                                            replyImg,
                                            replyMsg,
                                            "",
                                            replyTo,
                                            senderId,
                                            senderName,
                                            isRead,
                                            imgPathSelf,
                                            imgPathOther,
                                            isClearedSelf,
                                            isClearedOther
                                        )

                                        if (!isChatExists) {

                                            myDbHandeler.addChat(personalChatModel)

                                            //getChats()

                                        } else {

                                            myDbHandeler.updateChat(personalChatModel)
                                            //getChats()
                                        }
                                    }
                                })*/

                            }
                        }
                    }

                    //rvChat.setItemViewCacheSize(recycleModel.size)
                    if (lastChatSize == 0) {

                        lastChatSize = recycleModel.size
                    }

                    if (recycleViewPersonalChatAdapter == null) {

                        recycleViewPersonalChatAdapter =
                            RecycleViewPersonalChatAdapter(this@ChatActivity)

                        recycleViewPersonalChatAdapter!!.addChats(recycleModel)

                        binding!!.rvChat.adapter = recycleViewPersonalChatAdapter

                        recycleViewPersonalChatAdapter!!.notifyDataSetChanged()

                        lastChatSize = recycleModel.size

                    } /*else {

                            recycleViewPersonalChatAdapter!!.addChats(recycleModel)

                            recycleViewPersonalChatAdapter!!.notifyDataSetChanged()
                        }*/
                    Log.d("ChatSize", lastChatSize.toString() + "   " + recycleModel.size)
                    Log.d("ChatSending", isSending.toString())

                    if (onCurrentActivity && !isSending) {

                        if (lastChatSize < recycleModel.size) {

                            Log.d("ChatSe", recycleModel.lastIndex.toString())

                            recycleViewPersonalChatAdapter!!.addChats(recycleModel)

                            recycleViewPersonalChatAdapter!!.notifyItemInserted(recycleModel.lastIndex)

                            //recycleViewPersonalChatAdapter!!.notifyDataSetChanged()

                            lastChatSize = recycleModel.size

                        } else if (lastChatSize > recycleModel.size) {

                            recycleViewPersonalChatAdapter!!.addChats(recycleModel)

                            recycleViewPersonalChatAdapter!!.notifyItemInserted(recycleModel.lastIndex)
                            //recycleViewPersonalChatAdapter!!.notifyDataSetChanged()

                            lastChatSize = recycleModel.size
                        } else {

                            //recycleViewPersonalChatAdapter!!.addChats(recycleModel)

                            Log.d("ChatStatus", recycleModel.lastIndex.toString())

                            //val newData = recycleModel.lastIndex - lastReadIndex

                            if (lastReadIndex != 0) {

                                for (i in lastReadIndex + 1..recycleModel.lastIndex) {

                                    Log.d("ChatIndexs", i.toString())

                                    recycleViewPersonalChatAdapter!!.updateChats(recycleModel[i])
                                    recycleViewPersonalChatAdapter!!.notifyItemChanged(
                                        i, recycleModel[i]
                                    )
                                }
                            }

                            lastChatSize = recycleModel.size
                            //lastReadIndex = recycleModel.lastIndex
                        }
                    } else {

                        /*recycleViewPersonalChatAdapter!!.addChats(recycleModel)

                        recycleViewPersonalChatAdapter!!.notifyDataSetChanged()

                        lastChatSize = recycleModel.size*/
                    }

                    if (isSending) {

                        recycleViewPersonalChatAdapter =
                            RecycleViewPersonalChatAdapter(this@ChatActivity)

                        recycleViewPersonalChatAdapter!!.addChats(recycleModel)

                        binding!!.rvChat.adapter = recycleViewPersonalChatAdapter

                        recycleViewPersonalChatAdapter!!.notifyDataSetChanged()

                        Handler().postDelayed(object : Runnable {
                            override fun run() {

                                isSending = false
                            }
                        }, 300)

                        newMsgIndex.add(recycleModel.lastIndex)
                        lastChatSize = recycleModel.size
                        isSendingImg = false
                    }

                    //val recyclerViewState = rvChat.layoutManager?.onSaveInstanceState()

                    //rvChat.layoutManager?.onRestoreInstanceState(recyclerViewState)
                    //val scrollTo = rvChatScrollPos
                    //rvChat.scrollBy(0, scrollTo)
                    //recycleViewPersonalChatAdapter.notifyDataSetChanged()

                    deleteChatsArr.clear()

                    /*Handler().postDelayed(object : Runnable {
                        override fun run() {

                            chatDeleteIc.setVisible(false)
                        }
                    }, 1000)*/

                    if (onCurrentActivity) {

                        readMsgs()
                    }

                    updateChatUi()
                }
            }

            override fun onCancelled(p0: DatabaseError) {


            }


        })


        /*Handler().postDelayed(object : Runnable {

            override fun run() {

                try {
                    val personalChatArr = myDbHandeler.allChats

                    for (chat in personalChatArr) {

                        Log.d(
                            "PersonalChatARR",
                            chat.chat_id.toString() + "  " + chat.msg + "  " + chat.msgTime + "  " + chat.replyMsgTime + "  " + chat.replyId + "  " +
                                    chat.imgUrl + "  " + chat.receiverId + "  " + chat.replyImg + "  " + chat.replyTo + "  " + chat.senderId + "  " + chat.receiverName + "  " + chat.senderName
                                    + "  " + chat.replyMsg
                        )
                    }
                } catch (e : Exception) {


                }
            }
        }, 300)*/
    }

    private fun sendVideoMsg(videoFileUri: Uri?) {
        showProgressView(progressBar)
        message = "video"
        binding!!.sendingImgLay.visibility = View.GONE
        binding!!.replyChatLay.visibility = View.GONE

        isSending = true

        val chatIds = System.currentTimeMillis().toString()

        newMsgId = chatIds

        Log.d("UploadImgPath", videoFileUri!!.path.toString())

        val cDb =
            FirebaseDatabase.getInstance(Constants.Firebase.databaseUrl).getReference(Constants.Firebase.ChatDB)
                .child("UserChat").child(chatRoomId).child("chats")

        val storageRef =
            FirebaseStorage.getInstance().getReference("chats").child(chatRoomId).child("Video")
                .child("Original").child(System.currentTimeMillis().toString() + ".mp4")

        val storageRefThumb =
            FirebaseStorage.getInstance().getReference("chats").child(chatRoomId).child("Video")
                .child("ThumbnailUrl").child(System.currentTimeMillis().toString() + ".jpg")

        val uploadTask: UploadTask = storageRef.putFile(videoFileUri)

        //val msgDb = cDb.child(chatId)

        val msgDb = chatDb.child(chatRoomId).child("chats").push()

        val userChatDb = chatDb.child(chatRoomId)

        var thumbImgUrl = ""

        val f = File(this.cacheDir, "thumb")
        f.createNewFile()

        Log.e("chatVideo", "bitmapVideoThumb- " + bitmapVideoThumb)
//Convert bitmap to byte array
        val bitmap = bitmapVideoThumb
        val bos = ByteArrayOutputStream()
        bitmap!!.compress(Bitmap.CompressFormat.JPEG, 2 /*ignored for PNG*/, bos)
        val bitmapdata = bos.toByteArray()

//write the bytes in file
        val fos = FileOutputStream(f)
        fos.write(bitmapdata)
        fos.flush()
        fos.close()

        Log.d("ThumbNail", f.absolutePath)

        val uploadTaskThumb: UploadTask = storageRefThumb.putFile(Uri.fromFile(f))

        uploadTaskThumb.continueWithTask(object : Continuation<UploadTask.TaskSnapshot, Task<Uri>> {
            override fun then(task: Task<UploadTask.TaskSnapshot>): Task<Uri> {

                if (!task.isSuccessful) {

                    throw task.exception!!
                }
                return storageRefThumb.downloadUrl
            }


        }).addOnCompleteListener(object : OnCompleteListener<Uri> {
            override fun onComplete(task: Task<Uri>) {

                if (task.isSuccessful) {

                    thumbImgUrl = task.result.toString()
                    Log.e("VideoThumb", "Url: " + thumbImgUrl)

                    /* recycleModel.add(
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
                             chatIds,
                             "",
                             "",
                             videoAbsolutePath,
                             task.result.toString(),
                             "",
                             "",
                             "",
                             ""
                         )
                     )

                     if (recycleViewPersonalChatAdapter == null) {

                         recycleViewPersonalChatAdapter =
                             RecycleViewPersonalChatAdapter(this@ChatActivity)

                         rvChat.adapter = recycleViewPersonalChatAdapter

                         recycleViewPersonalChatAdapter!!.addChats(recycleModel)

                         recycleViewPersonalChatAdapter!!.notifyItemInserted(recycleModel.lastIndex)

                         val chatList = chatListDb.child(userId).child(chatRoomId)
                         val otherChatList = chatListDb.child(otherUserId).child(chatRoomId)

                         createUpdateChatList(
                             chatList,
                             "Video",
                             otherUserImg,
                             "",
                             "",
                             "",
                             "",
                             0,
                             otherUserName
                         )

                         if (profile.images.isNotEmpty()) {
                             createUpdateChatList(
                                 otherChatList,
                                 "Video",
                                 profile.images[0].imagePath,
                                 "",
                                 "",
                                 "",
                                 "",
                                 0,
                                 profile.name
                             )
                         } else {

                             createUpdateChatList(
                                 otherChatList,
                                 "Video",
                                 profile.userPic,
                                 "",
                                 "",
                                 "",
                                 "",
                                 0,
                                 profile.name
                             )
                         }

                     } else {

                         recycleViewPersonalChatAdapter!!.addChats(recycleModel)

                         recycleViewPersonalChatAdapter!!.notifyItemInserted(recycleModel.lastIndex)
                     }*/

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
                                Log.e("ViseoSend%", "isReplying: " + isReplying)
                                if (isReplying) {
                                    recycleModel.add(
                                        RecycleModel(
                                            profile.name,
                                            "",
                                            System.currentTimeMillis().toString(),
                                            userId,
                                            "1",
                                            "",
                                            "",
                                            replyTo,
                                            replyMsg,
                                            chatIds,
                                            chatId,
                                            chatImg,
                                            videoAbsolutePath,
                                            thumbImgUrl,
                                            task.result.toString(),
                                            "",
                                            "",
                                            ""
                                        )
                                    )
                                } else {
                                    recycleModel.add(
                                        RecycleModel(
                                            profile.name,
                                            "",
                                            System.currentTimeMillis().toString(),
                                            userId,
                                            "1",
                                            "",
                                            "",
                                            "",
                                            "",
                                            chatIds,
                                            "",
                                            "",
                                            videoAbsolutePath,
                                            thumbImgUrl,
                                            task.result.toString(),
                                            "",
                                            "",
                                            ""
                                        )
                                    )
                                }

                                if (recycleViewPersonalChatAdapter == null) {

                                    recycleViewPersonalChatAdapter =
                                        RecycleViewPersonalChatAdapter(this@ChatActivity)

                                    binding!!.rvChat.adapter = recycleViewPersonalChatAdapter

                                    recycleViewPersonalChatAdapter!!.addChats(recycleModel)

                                    recycleViewPersonalChatAdapter!!.notifyItemInserted(recycleModel.lastIndex)

                                } else {
                                    recycleViewPersonalChatAdapter!!.updateChats(recycleModel[recycleModel.lastIndex])

                                    recycleViewPersonalChatAdapter!!.notifyItemChanged(
                                        recycleModel.lastIndex, recycleModel[recycleModel.lastIndex]
                                    )
                                }

                                val chatList = chatListDb.child(userId).child(chatRoomId)
                                val otherChatList = chatListDb.child(otherUserId).child(chatRoomId)

                                chatListDb.child(otherUserId)
                                    .addListenerForSingleValueEvent(object : ValueEventListener {
                                        override fun onDataChange(snap: DataSnapshot) {

                                            if (snap.exists()) {
                                                try {
                                                    unreadCount = snap.child(chatRoomId)
                                                        .child("unReadCount").value.toString()
                                                        .toInt()
                                                } catch (e: java.lang.Exception) {
                                                    Log.e("VideoSend", "Exception_Catch: " + e)
                                                }

                                                createUpdateChatList(

                                                    chatList,
                                                    "Video",
                                                    otherUserImg,
                                                    "",
                                                    "",
                                                    "",
                                                    "",
                                                    0,
                                                    otherUserName
                                                )

                                                if (profile.images.isNotEmpty()) {
                                                    createUpdateChatList(
                                                        otherChatList,
                                                        "Video",
                                                        profile.images[0].imagePath,
                                                        "",
                                                        "",
                                                        "",
                                                        "",
                                                        unreadCount + 1,
                                                        profile.name
                                                    )
                                                } else {

                                                    createUpdateChatList(
                                                        otherChatList,
                                                        "Video",
                                                        profile.userPic,
                                                        "",
                                                        "",
                                                        "",
                                                        "",
                                                        unreadCount + 1,
                                                        profile.name
                                                    )
                                                }
                                            }
                                        }

                                        override fun onCancelled(p0: DatabaseError) {

                                        }

                                    })

                                val childRefKey: String = msgDb.key.toString()
                                if (isReplying) {
                                    val dataMap = hashMapOf(
                                        "messageText" to "Video",
                                        "deliveredTime" to chatIds,
                                        "deliveryStatus" to "1",
                                        "mediaDeviceUrlOf" to hashMapOf<String, String>(
                                            otherUserId to "", userId to videoAbsolutePath
                                        ),
                                        "messagesDeletedBY" to hashMapOf<String, Boolean>(
                                            userId to false, otherUserId to false
                                        ),
                                        "messagesDeletedBYSender" to false,
                                        "msgTime" to System.currentTimeMillis(),
                                        "messageTime" to System.currentTimeMillis(),
                                        "mediaLength" to "0",
                                        "mediaType" to 4,
                                        "mediaUrl" to task.result.toString(),
                                        "messageId" to childRefKey,
                                        "messageStatus" to 1,
                                        "messageType" to 4,
                                        "readTime" to "",
                                        "readStatus" to "",
                                        "mediaSize" to "",
                                        "replyTo" to replyTo,
                                        "repliedOnMediaName" to "Video",
                                        "repliedOnMediaType" to "4",
                                        "repliedOnMediaUrl" to chatImg,
                                        "repliedOnMessageId" to chatId,
                                        "repliedOnText" to replyMsg,
                                        "isFromReply" to isReplying,
                                        "senderId" to userId,
                                        "senderName" to profile.name,
                                        "thumbnailUrl" to thumbImgUrl
                                    )

                                    msgDb.setValue(dataMap)

                                    binding!!.replyChatLay.visibility = View.GONE
                                    binding!!.replyLay.visibility = View.GONE
                                    binding!!.sendingImgLay.visibility = View.GONE
                                    isReplying = false
                                    replyTo = ""
                                    replyMsg = ""
                                    replyMsgTime = ""
                                    chatImg = ""
                                    scrollToBottomNested()
                                } else {
                                    val dataMap = hashMapOf<String, Any>(
                                        "messageText" to "Video",
                                        "deliveredTime" to chatIds,
                                        "deliveryStatus" to "1",
                                        "mediaDeviceUrlOf" to hashMapOf<String, String>(
                                            otherUserId to "", userId to videoAbsolutePath
                                        ),
                                        "messagesDeletedBY" to hashMapOf<String, Boolean>(
                                            userId to false, otherUserId to false
                                        ),
                                        "messagesDeletedBYSender" to false,
                                        "msgTime" to System.currentTimeMillis(),
                                        "messageTime" to System.currentTimeMillis(),
                                        "mediaLength" to "0",
                                        "mediaType" to 4,
                                        "mediaUrl" to task.result.toString(),
                                        "messageId" to childRefKey,
                                        "messageStatus" to 1,
                                        "messageType" to 4,
                                        "readTime" to "",
                                        "readStatus" to "",
                                        "mediaSize" to "",
                                        "replyTo" to "",
                                        "repliedOnMediaName" to "",
                                        "repliedOnMediaType" to "",
                                        "repliedOnMediaUrl" to "",
                                        "repliedOnMessageId" to "",
                                        "repliedOnText" to "",
                                        "isFromReply" to "",
                                        "senderId" to userId,
                                        "senderName" to profile.name,
                                        "thumbnailUrl" to thumbImgUrl
                                    )
                                    msgDb.setValue(dataMap)

                                    binding!!.replyChatLay.visibility = View.GONE
                                    binding!!.replyLay.visibility = View.GONE
                                    binding!!.sendingImgLay.visibility = View.GONE
                                    isReplying = false
                                    replyTo = ""
                                    replyMsg = ""
                                    replyMsgTime = ""
                                    chatImg = ""
                                    scrollToBottomNested()
                                }

                                sendMsgNotification("video")
                                isSendingVideo = false

                                binding!!.etChat.isEnabled = true

                                this@ChatActivity.videoFileUri = null
                                hideProgressView(progressBar)
                            } else hideProgressView(progressBar)
                        }
                    })

                    /// sendMsgNotification("video")

                }
            }
        })

    }

    private fun sendImgMsg(photoFileUri: Uri?) {
        showProgressView(progressBar)
        message = "image"
        binding!!.sendingImgLay.visibility = View.GONE
        binding!!.replyChatLay.visibility = View.GONE
        isSending = true

        val chatIds = System.currentTimeMillis().toString()

        newMsgId = chatIds

        Log.d("UploadImgPath", photoFileUri!!.path.toString())

        val cDb =
            FirebaseDatabase.getInstance(Constants.Firebase.databaseUrl).getReference(Constants.Firebase.ChatDB)
                .child("UserChat").child(chatRoomId).child("chats")

        val storageRef =
            FirebaseStorage.getInstance().getReference("chats").child(chatRoomId).child("Image")
                .child("Original").child(System.currentTimeMillis().toString() + ".jpg")

        val storageRefThumb =
            FirebaseStorage.getInstance().getReference("chats").child(chatRoomId).child("Image")
                .child("ThumbnailUrl").child(System.currentTimeMillis().toString() + ".jpg")

        val uploadTask: UploadTask = storageRef.putFile(photoFileUri)

        //val msgDb = cDb.child(chatId)

        val msgDb = chatDb.child(chatRoomId).child("chats").push()

        val userChatDb = chatDb.child(chatRoomId)

        val imageFile = photoFile

        var thumbImgUrl = ""

        var thumbBitmap: Bitmap? = null

        /*  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                 thumbBitmap = ThumbnailUtils.createImageThumbnail(imageFile!!, Size(100,100), null)
          }*/
        thumbBitmap = ThumbnailUtils.extractThumbnail(
            BitmapFactory.decodeFile(imageFile.toString()), 100, 100
        )
        Log.e("ThumbNail#@$", "Image- " + thumbBitmap)


        //create a file to write bitmap data
        val f = File(this.cacheDir, "thumb")
        f.createNewFile()

//Convert bitmap to byte array
        val bitmap = thumbBitmap
        val bos = ByteArrayOutputStream()
        Log.e("ThumbNail#@$", "bitmap- " + bitmap)
        bitmap!!.compress(Bitmap.CompressFormat.JPEG, 2 /*ignored for PNG*/, bos)
        val bitmapdata = bos.toByteArray()

//write the bytes in file
        val fos = FileOutputStream(f)
        fos.write(bitmapdata)
        fos.flush()
        fos.close()

        Log.d("ThumbNail", f.absolutePath)

        val uploadTaskThumb: UploadTask = storageRefThumb.putFile(Uri.fromFile(f))

        uploadTaskThumb.continueWithTask(object : Continuation<UploadTask.TaskSnapshot, Task<Uri>> {
            override fun then(task: Task<UploadTask.TaskSnapshot>): Task<Uri> {

                if (!task.isSuccessful) {

                    throw task.exception!!
                }
                return storageRefThumb.downloadUrl
            }


        }).addOnCompleteListener(object : OnCompleteListener<Uri> {
            override fun onComplete(task: Task<Uri>) {

                if (task.isSuccessful) {

                    //msgDb.child("thumbnailUrl").setValue(task.result.toString())

                    thumbImgUrl = task.result.toString()
                    Log.e("ImgThumb", "Url: " + thumbImgUrl)/* recycleModel.add(
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
                             "",
                             "",
                             ""
                         )
                     )

                     if (recycleViewPersonalChatAdapter == null) {

                         recycleViewPersonalChatAdapter =
                             RecycleViewPersonalChatAdapter(this@ChatActivity)

                         rvChat.adapter = recycleViewPersonalChatAdapter

                         recycleViewPersonalChatAdapter!!.addChats(recycleModel)

                         recycleViewPersonalChatAdapter!!.notifyItemInserted(recycleModel.lastIndex)

                         val chatList = chatListDb.child(userId).child(chatRoomId)
                         val otherChatList = chatListDb.child(otherUserId).child(chatRoomId)

                         createUpdateChatList(
                             chatList,
                             "Video",
                             otherUserImg,
                             "",
                             "",
                             "",
                             "",
                             0,
                             otherUserName
                         )

                         if (profile.images.isNotEmpty()) {
                             createUpdateChatList(
                                 otherChatList,
                                 "Video",
                                 profile.images[0].imagePath,
                                 "",
                                 "",
                                 "",
                                 "",
                                 0,
                                 profile.name
                             )
                         } else {

                             createUpdateChatList(
                                 otherChatList,
                                 "Video",
                                 profile.userPic,
                                 "",
                                 "",
                                 "",
                                 "",
                                 0,
                                 profile.name
                             )
                         }

                     } else {

                         recycleViewPersonalChatAdapter!!.addChats(recycleModel)

                         recycleViewPersonalChatAdapter!!.notifyItemInserted(recycleModel.lastIndex)

                         //recycleViewPersonalChatAdapter!!.notifyDataSetChanged()
                     }*/


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
                                Log.e("ImgThumb", "Url@#: " + thumbImgUrl)
                                Log.e("ImgSend%", "isReplying: " + isReplying)
                                if (isReplying) {
                                    recycleModel.add(
                                        RecycleModel(
                                            profile.name,
                                            "",
                                            System.currentTimeMillis().toString(),
                                            userId,
                                            "1",
                                            "",
                                            task.result.toString(),
                                            replyTo,
                                            replyMsg,
                                            chatIds,
                                            chatId,
                                            chatImg,
                                            imgAbsolutePath,
                                            thumbImgUrl,
                                            "",
                                            "",
                                            "",
                                            ""
                                        )
                                    )
                                } else {
                                    recycleModel.add(
                                        RecycleModel(
                                            profile.name,
                                            "",
                                            System.currentTimeMillis().toString(),
                                            userId,
                                            "1",
                                            "",
                                            task.result.toString(),
                                            "",
                                            "",
                                            chatIds,
                                            "",
                                            "",
                                            imgAbsolutePath,
                                            thumbImgUrl,
                                            "",
                                            "",
                                            "",
                                            ""
                                        )
                                    )
                                }

                                if (recycleViewPersonalChatAdapter == null) {

                                    recycleViewPersonalChatAdapter =
                                        RecycleViewPersonalChatAdapter(this@ChatActivity)

                                    binding!!.rvChat.adapter = recycleViewPersonalChatAdapter

                                    recycleViewPersonalChatAdapter!!.addChats(recycleModel)

                                    recycleViewPersonalChatAdapter!!.notifyItemInserted(recycleModel.lastIndex)

                                } else {
                                    recycleViewPersonalChatAdapter!!.updateChats(recycleModel[recycleModel.lastIndex])

                                    recycleViewPersonalChatAdapter!!.notifyItemChanged(
                                        recycleModel.lastIndex, recycleModel[recycleModel.lastIndex]
                                    )
                                }/* recycleViewPersonalChatAdapter!!.updateChats(recycleModel[recycleModel.lastIndex])

                                 recycleViewPersonalChatAdapter!!.notifyItemChanged(
                                     recycleModel.lastIndex,
                                     recycleModel[recycleModel.lastIndex]
                                 )*/

                                val chatList = chatListDb.child(userId).child(chatRoomId)
                                val otherChatList = chatListDb.child(otherUserId).child(chatRoomId)

                                chatListDb.child(otherUserId)
                                    .addListenerForSingleValueEvent(object : ValueEventListener {
                                        override fun onDataChange(snap: DataSnapshot) {

                                            if (snap.exists()) {
                                                try {
                                                    unreadCount = snap.child(chatRoomId)
                                                        .child("unReadCount").value.toString()
                                                        .toInt()
                                                } catch (e: java.lang.Exception) {
                                                    Log.e("ImgSend", "Exception_Catch: " + e)
                                                }

                                                createUpdateChatList(
                                                    chatList,
                                                    "Image",
                                                    otherUserImg,
                                                    "",
                                                    "",
                                                    "",
                                                    "",
                                                    0,
                                                    otherUserName
                                                )

                                                if (profile.images.isNotEmpty()) {
                                                    createUpdateChatList(
                                                        otherChatList,
                                                        "Image",
                                                        profile.images[0].imagePath,
                                                        "",
                                                        "",
                                                        "",
                                                        "",
                                                        unreadCount + 1,
                                                        profile.name
                                                    )
                                                } else {

                                                    createUpdateChatList(
                                                        otherChatList,
                                                        "Image",
                                                        profile.userPic,
                                                        "",
                                                        "",
                                                        "",
                                                        "",
                                                        unreadCount + 1,
                                                        profile.name
                                                    )
                                                }

                                            } else {
                                                createUpdateChatList(
                                                    chatList,
                                                    "Image",
                                                    otherUserImg,
                                                    "",
                                                    "",
                                                    "",
                                                    "",
                                                    0,
                                                    otherUserName
                                                )

                                                if (profile.images.isNotEmpty()) {
                                                    createUpdateChatList(
                                                        otherChatList,
                                                        "Image",
                                                        profile.images[0].imagePath,
                                                        "",
                                                        "",
                                                        "",
                                                        "",
                                                        1,
                                                        profile.name
                                                    )
                                                } else {

                                                    createUpdateChatList(
                                                        otherChatList,
                                                        "Image",
                                                        profile.userPic,
                                                        "",
                                                        "",
                                                        "",
                                                        "",
                                                        1,
                                                        profile.name
                                                    )
                                                }
                                            }
                                        }

                                        override fun onCancelled(p0: DatabaseError) {

                                        }

                                    })
                                val childRefKey: String = msgDb.key.toString()

                                /*  msgDb.child("messageText").setValue("Image")
                                  msgDb.child("deliveredTime").setValue(chatId)
                                  msgDb.child("deliveryStatus").setValue("1")
                                  msgDb.child("mediaDeviceUrlOf").setValue("")
                                  msgDb.child("mediaDeviceUrlOf").child(otherUserId).setValue("")
                                  msgDb.child("mediaDeviceUrlOf").child(userId).setValue(imgAbsolutePath)
                                  msgDb.child("messagesDeletedBY").child(userId).setValue(false)
                                  msgDb.child("messagesDeletedBY").child(otherUserId).setValue(false)
                                  msgDb.child("messagesDeletedBYSender").setValue(false)
                                  msgDb.child("msgTime").setValue(System.currentTimeMillis())
                                  msgDb.child("messageTime").setValue(System.currentTimeMillis())
                                  msgDb.child("mediaLength").setValue("0")
                                  msgDb.child("mediaType").setValue(2)
                                  msgDb.child("mediaUrl").setValue(task.result.toString())
                                  msgDb.child("messageId").setValue(childRefKey)
                                  msgDb.child("messageStatus").setValue(1)
                                  msgDb.child("messageType").setValue(2)
                                  msgDb.child("readTime").setValue("")
                                  msgDb.child("readStatus").setValue("")
                                  msgDb.child("mediaSize").setValue("")
              //                    msgDb.child("otherUserId").setValue(otherUserId)
              //                    msgDb.child("otherUserName").setValue(otherUserName)
                                  //msgDb.child("replyTo").setValue(replyTo)
                                  msgDb.child("repliedOnMediaName").setValue("")
                                  msgDb.child("repliedOnMediaType").setValue("")
                                  msgDb.child("repliedOnMediaUrl").setValue("")
                                  msgDb.child("repliedOnMessageId").setValue("")
                                  msgDb.child("repliedOnText").setValue("")
                                  msgDb.child("senderId").setValue(userId)
                                  msgDb.child("senderName").setValue(profile.name)
                                  msgDb.child("thumbnailUrl").setValue(thumbImgUrl)*/
                                if (isReplying) {
                                    val dataMap = hashMapOf(
                                        "messageText" to "Image",
                                        "deliveredTime" to chatIds,
                                        "deliveryStatus" to "1",
                                        "mediaDeviceUrlOf" to hashMapOf(
                                            otherUserId to "", userId to imgAbsolutePath
                                        ),
                                        "messagesDeletedBY" to hashMapOf(
                                            userId to false, otherUserId to false
                                        ),
                                        "messagesDeletedBYSender" to false,
                                        "msgTime" to System.currentTimeMillis(),
                                        "messageTime" to System.currentTimeMillis(),
                                        "mediaLength" to "0",
                                        "mediaType" to 2,
                                        "mediaUrl" to task.result.toString(),
                                        "messageId" to childRefKey,
                                        "messageStatus" to 1,
                                        "messageType" to 2,
                                        "readTime" to "",
                                        "readStatus" to "",
                                        "mediaSize" to "",
                                        "isFromReply" to isReplying,
                                        "replyTo" to replyTo,
                                        "repliedOnMediaName" to "Image",
                                        "repliedOnMediaType" to "2",
                                        "repliedOnMediaUrl" to chatImg,
                                        "repliedOnMessageId" to chatId,
                                        "repliedOnText" to replyMsg,
                                        "senderId" to userId,
                                        "senderName" to profile.name,
                                        "thumbnailUrl" to thumbImgUrl
                                    )

                                    msgDb.setValue(dataMap)
                                    binding!!.replyChatLay.visibility = View.GONE
                                    binding!!.replyLay.visibility = View.GONE
                                    binding!!.sendingImgLay.visibility = View.GONE
                                    isReplying = false
                                    replyTo = ""
                                    replyMsg = ""
                                    replyMsgTime = ""
                                    chatImg = ""
                                    scrollToBottomNested()
                                } else {
                                    Log.e("ImgSend%", "Elsss@!")
                                    val dataMap = hashMapOf(
                                        "messageText" to "Image",
                                        "deliveredTime" to chatIds,
                                        "deliveryStatus" to "1",
                                        "mediaDeviceUrlOf" to hashMapOf(
                                            otherUserId to "", userId to imgAbsolutePath
                                        ),
                                        "messagesDeletedBY" to hashMapOf(
                                            userId to false, otherUserId to false
                                        ),
                                        "messagesDeletedBYSender" to false,
                                        "msgTime" to System.currentTimeMillis(),
                                        "messageTime" to System.currentTimeMillis(),
                                        "mediaLength" to "0",
                                        "mediaType" to 2,
                                        "mediaUrl" to task.result.toString(),
                                        "messageId" to childRefKey,
                                        "messageStatus" to 1,
                                        "messageType" to 2,
                                        "readTime" to "",
                                        "readStatus" to "",
                                        "mediaSize" to "",

                                        "replyTo" to "",
                                        "repliedOnMediaName" to "",
                                        "repliedOnMediaType" to "",
                                        "repliedOnMediaUrl" to "",
                                        "repliedOnMessageId" to "",
                                        "repliedOnText" to "",
                                        "isFromReply" to "",
                                        "senderId" to userId,
                                        "senderName" to profile.name,
                                        "thumbnailUrl" to thumbImgUrl
                                    )

                                    msgDb.setValue(dataMap)

                                    binding!!.replyChatLay.visibility = View.GONE
                                    binding!!.replyLay.visibility = View.GONE
                                    binding!!.sendingImgLay.visibility = View.GONE
                                    isReplying = false
                                    replyTo = ""
                                    replyMsg = ""
                                    replyMsgTime = ""
                                    chatImg = ""
                                    scrollToBottomNested()
                                }


                                sendMsgNotification("img")
                                isSendingImg = false
                                binding!!.etChat.isEnabled = true
                                this@ChatActivity.photoFileUri = null
                                hideProgressView(progressBar)
                            } else hideProgressView(progressBar)
                        }
                    })
                    ///sendMsgNotification("img")
                } else hideProgressView(progressBar)
            }
        })
    }

    private fun fetchOtherUserProfile() {
        if (null == mPresenterProfile) mPresenterProfile = UserProfilePresenter(presenterProfile)

        run {
            binding!!.progressbarCircal.visibility = View.VISIBLE
            mPresenterProfile!!.addUserProfileObject("$otherUserId")
            mPresenterProfile!!.callRetrofit(ConstantsApi.FETCH_PROFILE)
        }
    }

    fun acceptConnectionRequest(userId: Int?) {
        if (null == connectionPresenter) connectionPresenter =
            ConnectionRequestPresenter(getConectionpresenter)

        run {
            showProgressView(progressBar)
            connectionPresenter!!.addConnectionRequestObject(RequestConnectionRequest().apply {
                sender_id = userId!!
            })
            connectionPresenter!!.callRetrofit(ConstantsApi.ACCEPT_CONNECTION_REQUEST)
        }
    }

    fun declineConnectionRequest(userId: Int) {
        if (null == connectionPresenter) connectionPresenter =
            ConnectionRequestPresenter(getConectionpresenter)

        run {
            showProgressView(progressBar)
            connectionPresenter!!.addConnectionRequestObject(RequestConnectionRequest().apply {
                sender_id = userId
            })
            connectionPresenter!!.callRetrofit(ConstantsApi.DECLINE_CONNECTION_REQUEST)
        }
    }

    private fun sendMsg(msg: String) {
        message = binding!!.etChat.text.toString()
        binding!!.etChat.text.clear()
        isSending = true

        val msgTime = System.currentTimeMillis().toString()
        newMsgId = msgTime

        Log.e("SendMsgTime", newMsgId)
        if (!isReplying) {

            chatDb.child(chatRoomId).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snap: DataSnapshot) {

                    if (snap.exists()) {

                        val chatList = chatListDb.child(userId).child(chatRoomId)
                        val otherChatList = chatListDb.child(otherUserId).child(chatRoomId)

                        chatListDb.child(otherUserId)
                            .addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(snap: DataSnapshot) {

                                    if (snap.exists()) {

                                        unreadCount = snap.child(chatRoomId)
                                            .child("unReadCount").value.toString().toInt()
                                        Log.e("Send_Sms*&", "createUpdateChatList_Call_exist11")

                                        createUpdateChatList(
                                            chatList,
                                            msg,
                                            otherUserImg,
                                            "",
                                            "",
                                            "",
                                            "",
                                            0,
                                            otherUserName
                                        )

                                        if (profile.images.isNotEmpty()) {
                                            Log.e("Send_Sms*&", "createUpdateChatList_call_exist22")
                                            createUpdateChatList(
                                                otherChatList,
                                                msg,
                                                profile.images[0].imagePath,
                                                "",
                                                "",
                                                "",
                                                "",
                                                unreadCount + 1,
                                                profile.name
                                            )
                                        } else {
                                            Log.e("Send_Sms*&", "createUpdateChatList_call_exist33")
                                            createUpdateChatList(
                                                otherChatList,
                                                msg,
                                                profile.userPic,
                                                "",
                                                "",
                                                "",
                                                "",
                                                unreadCount + 1,
                                                profile.name
                                            )

                                        }

                                    }
                                }

                                override fun onCancelled(p0: DatabaseError) {

                                }

                            })


                        val chatId = System.currentTimeMillis().toString()

                        val msgDb = chatDb.child(chatRoomId).child("chats").push()
                        val childRefKey: String = msgDb.key.toString()
                        val dataMap = HashMap<String, Any>()
                        dataMap["messageText"] = msg
                        dataMap["deliveredTime"] = chatId
                        dataMap["deliveryStatus"] = "1"
                        val innerHashMap = HashMap<String, String>()
                        innerHashMap[otherUserId] = ""
                        innerHashMap[userId] = ""
                        dataMap["mediaDeviceUrlOf"] = innerHashMap
                        val innerHashMapDelet = HashMap<String, Boolean>()
                        innerHashMapDelet[otherUserId] = false
                        innerHashMapDelet[userId] = false
                        dataMap["messagesDeletedBY"] = innerHashMapDelet
                        dataMap["messagesDeletedBYSender"] = false
                        dataMap["msgTime"] = System.currentTimeMillis()
                        dataMap["messageTime"] = System.currentTimeMillis()
                        dataMap["mediaLength"] = "0"
                        dataMap["mediaType"] = 1
                        dataMap["mediaUrl"] = ""
                        dataMap["messageId"] = childRefKey
                        dataMap["messageStatus"] = 1
                        dataMap["messageType"] = 1
                        dataMap["readTime"] = ""
                        dataMap["readStatus"] = ""
                        dataMap["mediaSize"] = ""
                        dataMap["receiverId"] = otherUserId
                        dataMap["repliedOnMediaName"] = ""
                        dataMap["repliedOnMediaType"] = ""
                        dataMap["repliedOnMediaUrl"] = ""
                        dataMap["repliedOnMessageId"] = ""
                        dataMap["repliedOnText"] = ""
                        dataMap["senderId"] = userId
                        dataMap["senderName"] = profile.name
                        dataMap["thumbnailUrl"] = ""

                        msgDb.setValue(dataMap)
                        Log.e("Send_Msg%", "Item_insert22- " + recycleModel.lastIndex)
                        Log.e("Sent_msg", "object: $dataMap")
                        Log.e("Sent_msg", "object: $msgDb")
                        recycleModel.add(
                            RecycleModel(
                                profile.name,
                                msg,
                                System.currentTimeMillis().toString(),
                                userId,
                                "1",
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
                                "",
                                "",
                                ""
                            )
                        )

                        if (recycleViewPersonalChatAdapter == null) {

                            recycleViewPersonalChatAdapter =
                                RecycleViewPersonalChatAdapter(this@ChatActivity)

                            binding!!.rvChat.adapter = recycleViewPersonalChatAdapter

                            recycleViewPersonalChatAdapter!!.addChats(recycleModel)

                            recycleViewPersonalChatAdapter!!.notifyItemInserted(recycleModel.lastIndex)
                            Log.e("Send_Msg%", "Item_insert1")
                        } else {
                            recycleViewPersonalChatAdapter!!.addChats(recycleModel) // CRASH nullpointer
                            /// recycleViewPersonalChatAdapter!!.notifyItemInserted(recycleModel.lastIndex)
                            Log.e("Send_Msg%", "Item_insert22- " + recycleModel.lastIndex)
                        }

                    } else {
                        Log.e("NotExists", "Create One")
                        val chatList = chatListDb.child(userId).child(chatRoomId)
                        val otherChatList = chatListDb.child(otherUserId).child(chatRoomId)
                        Log.e("Send_Sms*&", "createUpdateChatList_Call1_self")
                        createUpdateChatList(
                            chatList, msg, otherUserImg, "", "", "", "", 0, otherUserName
                        )

                        if (profile.images.isNotEmpty()) {
                            Log.e("Send_Sms*&", "createUpdateChatList_Call2_Other")
                            createUpdateChatList(
                                otherChatList,
                                msg,
                                profile.images[0].imagePath,
                                "",
                                "",
                                "",
                                "",
                                1,
                                profile.name
                            )
                        } else {
                            Log.e("Send_Sms*&", "createUpdateChatList_Call33_Other")
                            createUpdateChatList(
                                otherChatList, msg, profile.userPic, "", "", "", "", 1, profile.name
                            )
                        }


                        val msgId = System.currentTimeMillis().toString()

                        val msgDb = chatDb.child(chatRoomId).child("chats").push()

                        val dataMap = HashMap<String, Any>()
                        dataMap["messageText"] = msg
                        dataMap["deliveredTime"] = msgId
                        dataMap["deliveryStatus"] = "1"
                        val childRefKey: String = msgDb.key.toString()
                        val innerHashMap = HashMap<String, String>()
                        innerHashMap[otherUserId] = ""
                        innerHashMap[userId] = ""
                        dataMap["mediaDeviceUrlOf"] = innerHashMap
                        val innerHashMapDelet = HashMap<String, Boolean>()
                        innerHashMapDelet[otherUserId] = false
                        innerHashMapDelet[userId] = false
                        dataMap["messagesDeletedBY"] = innerHashMapDelet
                        dataMap["messagesDeletedBYSender"] = false
                        dataMap["msgTime"] = System.currentTimeMillis()
                        dataMap["messageTime"] = System.currentTimeMillis()
                        dataMap["mediaLength"] = "0"
                        dataMap["mediaType"] = 1
                        dataMap["mediaUrl"] = ""
                        dataMap["messageId"] = childRefKey
                        dataMap["messageStatus"] = 1
                        dataMap["messageType"] = 1
                        dataMap["readTime"] = ""
                        dataMap["readStatus"] = ""
                        dataMap["mediaSize"] = ""
                        dataMap["receiverId"] = otherUserId
                        dataMap["repliedOnMediaName"] = ""
                        dataMap["repliedOnMediaType"] = ""
                        dataMap["repliedOnMediaUrl"] = ""
                        dataMap["repliedOnMessageId"] = ""
                        dataMap["repliedOnText"] = ""
                        dataMap["senderId"] = userId
                        dataMap["senderName"] = profile.name
                        dataMap["thumbnailUrl"] = ""
                        msgDb.setValue(dataMap)

                        Log.e("Sent_msg", "object2: $dataMap")
                        Log.e("Sent_msg", "object2: $msgDb")

                        recycleModel.add(
                            RecycleModel(
                                profile.name,
                                msg,
                                System.currentTimeMillis().toString(),
                                userId,
                                "1",
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
                                "",
                                "",
                                ""
                            )
                        )

                        recycleViewPersonalChatAdapter =
                            RecycleViewPersonalChatAdapter(this@ChatActivity)

                        binding!!.rvChat.adapter = recycleViewPersonalChatAdapter

                        recycleViewPersonalChatAdapter!!.addChats(recycleModel)

                        recycleViewPersonalChatAdapter!!.notifyItemInserted(recycleModel.lastIndex)
                        Log.e("Send_Msg%", "Item_add33")

                        Log.e("chatActivity*& ", "No_snapshort")
                    }
                    scrollToBottomNested()
                }

                override fun onCancelled(p0: DatabaseError) {

                }
            })

        } else {

            chatDb.child(chatRoomId).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snap: DataSnapshot) {

                    if (snap.exists()) {
                        val chatList = chatListDb.child(userId).child(chatRoomId)
                        val otherChatList = chatListDb.child(otherUserId).child(chatRoomId)

                        chatListDb.child(otherUserId)
                            .addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(snap: DataSnapshot) {
                                    if (snap.exists()) {

                                        unreadCount = snap.child(chatRoomId)
                                            .child("unReadCount").value.toString().toInt()

                                        createUpdateChatList(
                                            chatList,
                                            msg,
                                            otherUserImg,
                                            replyTo,
                                            replyMsg,
                                            chatId,
                                            chatImg,
                                            0,
                                            otherUserName
                                        )

                                        if (profile.images.isNotEmpty()) {
                                            createUpdateChatList(
                                                otherChatList,
                                                msg,
                                                profile.images[0].imagePath,
                                                replyTo,
                                                replyMsg,
                                                chatId,
                                                chatImg,
                                                unreadCount + 1,
                                                profile.name
                                            )
                                        } else {

                                            createUpdateChatList(
                                                otherChatList,
                                                msg,
                                                profile.userPic,
                                                replyTo,
                                                replyMsg,
                                                chatId,
                                                chatImg,
                                                unreadCount + 1,
                                                profile.name
                                            )
                                        }
                                    }
                                }

                                override fun onCancelled(p0: DatabaseError) {

                                }

                            })

                        val msgId = System.currentTimeMillis().toString()

                        val msgDb = chatDb.child(chatRoomId).child("chats").push()

                        val childRefKey: String = msgDb.key.toString()
                        val dataMap = HashMap<String, Any>()
                        dataMap["messageText"] = msg
                        dataMap["deliveredTime"] = msgId
                        dataMap["deliveryStatus"] = "1"
                        val innerHashMap = HashMap<String, String>()
                        innerHashMap[otherUserId] = ""
                        innerHashMap[userId] = ""
                        dataMap["mediaDeviceUrlOf"] = innerHashMap
                        val innerHashMapDelet = HashMap<String, Boolean>()
                        innerHashMapDelet[otherUserId] = false
                        innerHashMapDelet[userId] = false
                        dataMap["messagesDeletedBY"] = innerHashMapDelet
                        dataMap["messagesDeletedBYSender"] = false
                        dataMap["msgTime"] = System.currentTimeMillis()
                        dataMap["messageTime"] = System.currentTimeMillis()
                        dataMap["mediaLength"] = "0"
                        dataMap["mediaType"] = 1
                        dataMap["mediaUrl"] = ""
                        dataMap["messageId"] = childRefKey
                        dataMap["messageType"] = 1
                        dataMap["messageStatus"] = 1
                        dataMap["readTime"] = ""
                        dataMap["readStatus"] = ""
                        dataMap["receiverId"] = otherUserId
                        dataMap["mediaSize"] = ""
                        dataMap["replyTo"] = replyTo
                        dataMap["repliedOnMediaName"] = "text"
                        dataMap["repliedOnMediaType"] = "1"
                        dataMap["repliedOnMediaUrl"] = chatImg
                        dataMap["repliedOnMessageId"] = chatId
                        dataMap["repliedOnText"] = replyMsg
                        dataMap["isFromReply"] = isReplying
                        dataMap["senderId"] = userId
                        dataMap["senderName"] = profile.name
                        dataMap["thumbnailUrl"] = ""
                        msgDb.setValue(dataMap)

                        recycleModel.add(
                            RecycleModel(
                                profile.name,
                                msg,
                                System.currentTimeMillis().toString(),
                                userId,
                                "1",
                                "",
                                "",
                                replyTo,
                                replyMsg,
                                chatId,
                                chatId,
                                chatImg,
                                "",
                                "",
                                "",
                                "",
                                "",
                                ""
                            )
                        )

                        recycleViewPersonalChatAdapter!!.addChats(recycleModel)
                        ///  recycleViewPersonalChatAdapter!!.notifyItemInserted(recycleModel.lastIndex)

                        Log.e("Send_Msg%", "Item_insert333")
                        binding!!.replyChatLay.visibility = View.GONE
                        binding!!.replyLay.visibility = View.GONE
                        binding!!.sendingImgLay.visibility = View.GONE
                        isReplying = false
                        replyTo = ""
                        replyMsg = ""
                        replyMsgTime = ""
                        chatId = ""
                        chatImg = ""
                        scrollToBottomNested()

                    }
                }

                override fun onCancelled(p0: DatabaseError) {

                }
            })
        }

        sendMsgNotification("text")
    }

    private fun sendFile(fileUri: Uri?) {
        showProgressView(progressBar)
        message = "File"
        binding!!.sendingImgLay.visibility = View.GONE
        binding!!.replyChatLay.visibility = View.GONE

        isSending = true

        val chatIdTm = System.currentTimeMillis().toString()

        newMsgId = chatIdTm

        Log.d("UploadImgPath", fileAbsolutePath)


        /* val cDb = FirebaseDatabase.getInstance(Constants.Firebase.databaseUrl).getReference(Constants.Firebase.ChatDB)
            .child("UserChat").child(chatRoomId).child("chats")*/
        var fileExt = ""
        var storageRef: StorageReference? = null


        if (mediaName.contains(".pdf")) {

            storageRef =
                FirebaseStorage.getInstance().getReference("chats").child(chatRoomId).child("Files")
                    .child("Original").child(mediaName)
            ///.child(mediaName + ".pdf")

            fileExt = "PDF"
        } else if (mediaName.contains(".docx")) {

            storageRef =
                FirebaseStorage.getInstance().getReference("chats").child(chatRoomId).child("Files")
                    .child("Original").child(mediaName)
            /// .child(mediaName + ".docx")

            fileExt = "DOCX"
        } else if (mediaName.contains(".doc")) {

            storageRef =
                FirebaseStorage.getInstance().getReference("chats").child(chatRoomId).child("Files")
                    .child("Original").child(mediaName)
            /// .child(mediaName + ".docx")

            fileExt = "DOC"
        } else if (mediaName.contains(".xlsx")) {

            storageRef =
                FirebaseStorage.getInstance().getReference("chats").child(chatRoomId).child("Files")
                    .child("Original").child(mediaName)

            fileExt = "XLSX"

        } else if (mediaName.contains(".xls")) {

            storageRef =
                FirebaseStorage.getInstance().getReference("chats").child(chatRoomId).child("Files")
                    .child("Original").child(mediaName)

            fileExt = "XLS"

        } else if (mediaName.contains(".txt")) {

            storageRef =
                FirebaseStorage.getInstance().getReference("chats").child(chatRoomId).child("Files")
                    .child("Original").child(mediaName)

            fileExt = "TXT"
        } else if (mediaName.contains(".ppt")) {

            storageRef =
                FirebaseStorage.getInstance().getReference("chats").child(chatRoomId).child("Files")
                    .child("Original").child(mediaName)

            fileExt = "PPT"
        } else {
            storageRef =
                FirebaseStorage.getInstance().getReference("chats").child(chatRoomId).child("Files")
                    .child("Original").child(mediaName)

        }

        fileExt = "File"
        val uploadTask: UploadTask = storageRef.putFile(fileUri!!)


        val storageRefThumb =
            FirebaseStorage.getInstance().getReference("chats").child(chatRoomId).child("Files")
                .child("ThumbnailUrl").child(mediaName + ".jpg")

        //val msgDb = cDb.child(chatId)

        val msgDb = chatDb.child(chatRoomId).child("chats").push()

        //val userChatDb = chatDb.child(chatRoomId)

        var thumbImgUrl = ""


        if (mediaName.contains(".pdf")) {

            val f = File(this.cacheDir, "thumb")
            f.createNewFile()
            /// val fileBitmap = generateImageFromPdf(fileUri)
            val fileBitmap = generateImageFromPdf(this@ChatActivity, fileUri, 0)
//Convert bitmap to byte array
            val bitmap = fileBitmap
            val bos = ByteArrayOutputStream()
            bitmap!!.compress(Bitmap.CompressFormat.JPEG, 2, bos)
            val bitmapdata = bos.toByteArray()

//write the bytes in file
            val fos = FileOutputStream(f)
            fos.write(bitmapdata)
            fos.flush()
            fos.close()

            Log.d("ThumbNail", f.absolutePath)

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

                    if (task.isSuccessful) {

                        thumbImgUrl = task.result.toString()

                        Log.d("FileThumb", thumbImgUrl.toString())

                        /*   recycleModel.add(
                               RecycleModel(
                                   profile.name,
                                   mediaName,
                                   System.currentTimeMillis().toString(),
                                   userId,
                                   false,
                                   "",
                                   "meetUpsFellow",
                                   "",
                                   "",
                                   chatIdTm,
                                   "",
                                   "",
                                   imgAbsolutePath,
                                   task.result.toString(),
                                   "",
                                   "",
                                   "",
                                   ""
                               )
                           )

                           if (recycleViewPersonalChatAdapter == null) {

                               recycleViewPersonalChatAdapter =
                                   RecycleViewPersonalChatAdapter(this@ChatActivity)

                               rvChat.adapter = recycleViewPersonalChatAdapter

                               recycleViewPersonalChatAdapter!!.addChats(recycleModel)

                               recycleViewPersonalChatAdapter!!.notifyItemInserted(recycleModel.lastIndex)

                               val chatList = chatListDb.child(userId).child(chatRoomId)
                               val otherChatList = chatListDb.child(otherUserId).child(chatRoomId)

                               createUpdateChatList(
                                   chatList,
                                   fileExt,
                                   otherUserImg,
                                   "",
                                   "",
                                   "",
                                   "",
                                   0,
                                   otherUserName
                               )

                               if (profile.images.isNotEmpty()) {
                                   createUpdateChatList(
                                       otherChatList,
                                       fileExt,
                                       profile.images[0].imagePath,
                                       "",
                                       "",
                                       "",
                                       "",
                                       0,
                                       profile.name
                                   )
                               } else {

                                   createUpdateChatList(
                                       otherChatList,
                                       fileExt,
                                       profile.userPic,
                                       "",
                                       "",
                                       "",
                                       "",
                                       0,
                                       profile.name
                                   )
                               }

                           } else {

                               recycleViewPersonalChatAdapter!!.addChats(recycleModel)

                               recycleViewPersonalChatAdapter!!.notifyItemInserted(recycleModel.lastIndex)
                           }*/
                    }

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
                                Log.e("SendFile%$", "sending&^% " + task.result)
                                //closeUploadDialog()
                                isSending = true


                                val chatList = chatListDb.child(userId).child(chatRoomId)
                                val otherChatList = chatListDb.child(otherUserId).child(chatRoomId)

                                chatListDb.child(otherUserId)
                                    .addListenerForSingleValueEvent(object : ValueEventListener {
                                        override fun onDataChange(snap: DataSnapshot) {

                                            if (snap.exists()) {

                                                unreadCount = snap.child(chatRoomId)
                                                    .child("unReadCount").value.toString().toInt()
                                                createUpdateChatList(
                                                    chatList,
                                                    fileExt,
                                                    otherUserImg,
                                                    "",
                                                    "",
                                                    "",
                                                    "",
                                                    0,
                                                    otherUserName
                                                )

                                                if (profile.images.isNotEmpty()) {
                                                    createUpdateChatList(
                                                        otherChatList,
                                                        fileExt,
                                                        profile.images[0].imagePath,
                                                        "",
                                                        "",
                                                        "",
                                                        "",
                                                        unreadCount + 1,
                                                        profile.name
                                                    )
                                                } else {

                                                    createUpdateChatList(
                                                        otherChatList,
                                                        fileExt,
                                                        profile.userPic,
                                                        "",
                                                        "",
                                                        "",
                                                        "",
                                                        unreadCount + 1,
                                                        profile.name
                                                    )
                                                }
                                            }
                                        }

                                        override fun onCancelled(p0: DatabaseError) {
                                        }
                                    })
                                val childRefKey: String = msgDb.key.toString()

                                if (isReplying) {
                                    recycleModel.add(
                                        RecycleModel(
                                            profile.name,
                                            mediaName,
                                            System.currentTimeMillis().toString(),
                                            userId,
                                            "1",
                                            "",
                                            "",
                                            replyTo,
                                            replyMsg,
                                            chatIdTm,
                                            chatId,
                                            chatImg,
                                            imgAbsolutePath,
                                            thumbImgUrl,
                                            "",
                                            "",
                                            task.result.toString(),
                                            mediaSizeStr
                                        )
                                    )
                                    val dataMap = HashMap<String, Any>()
                                    dataMap["messageText"] = mediaName
                                    dataMap["deliveredTime"] = chatIdTm
                                    dataMap["deliveryStatus"] = "1"
                                    val mediaDeviceUrlOfMap = HashMap<String, Any>()
                                    mediaDeviceUrlOfMap[otherUserId] = ""
                                    mediaDeviceUrlOfMap[userId] = fileAbsolutePath
                                    dataMap["mediaDeviceUrlOf"] = mediaDeviceUrlOfMap
                                    val messagesDeletedByMap = HashMap<String, Any>()
                                    messagesDeletedByMap[userId] = false
                                    messagesDeletedByMap[otherUserId] = false
                                    dataMap["messagesDeletedBY"] = messagesDeletedByMap
                                    dataMap["messagesDeletedBYSender"] = false
                                    dataMap["msgTime"] = System.currentTimeMillis()
                                    dataMap["messageTime"] = System.currentTimeMillis()
                                    dataMap["mediaLength"] = mediaSizeStr
                                    dataMap["mediaType"] = 5
                                    dataMap["mediaUrl"] = task.result.toString()
                                    dataMap["messageId"] = childRefKey
                                    dataMap["messageStatus"] = 1
                                    dataMap["messageType"] = 5
                                    dataMap["readTime"] = ""
                                    dataMap["readStatus"] = ""
                                    dataMap["mediaSize"] = mediaSizeStr
                                    dataMap["receiverId"] = otherUserId
                                    dataMap["isFromReply"] = isReplying
                                    dataMap["replyTo"] = replyTo
                                    dataMap["repliedOnMediaName"] = "File"
                                    dataMap["repliedOnMediaType"] = "5"
                                    dataMap["repliedOnMediaUrl"] = chatImg
                                    dataMap["repliedOnMessageId"] = chatId
                                    dataMap["repliedOnText"] = replyMsg
                                    dataMap["senderId"] = userId
                                    dataMap["senderName"] = profile.name
                                    dataMap["thumbnailUrl"] = thumbImgUrl
                                    msgDb.setValue(dataMap)

                                    Log.e("SendFile%$", " PDF_Rply")
                                } else {

                                    recycleModel.add(
                                        RecycleModel(
                                            profile.name,
                                            mediaName,
                                            System.currentTimeMillis().toString(),
                                            userId,
                                            "1",
                                            "",
                                            "",
                                            "",
                                            "",
                                            chatIdTm,
                                            "",
                                            "",
                                            imgAbsolutePath,
                                            thumbImgUrl,
                                            "",
                                            "",
                                            task.result.toString(),
                                            mediaSizeStr
                                        )
                                    )
                                    val dataMap = HashMap<String, Any>()
                                    dataMap["messageText"] = mediaName
                                    dataMap["deliveredTime"] = chatIdTm
                                    dataMap["deliveryStatus"] = "1"
                                    val mediaDeviceUrlOfMap = HashMap<String, Any>()
                                    mediaDeviceUrlOfMap[otherUserId] = ""
                                    mediaDeviceUrlOfMap[userId] = fileAbsolutePath
                                    dataMap["mediaDeviceUrlOf"] = mediaDeviceUrlOfMap
                                    val messagesDeletedByMap = HashMap<String, Any>()
                                    messagesDeletedByMap[userId] = false
                                    messagesDeletedByMap[otherUserId] = false
                                    dataMap["messagesDeletedBY"] = messagesDeletedByMap
                                    dataMap["messagesDeletedBYSender"] = false
                                    dataMap["msgTime"] = System.currentTimeMillis()
                                    dataMap["messageTime"] = System.currentTimeMillis()
                                    dataMap["mediaLength"] = mediaSizeStr
                                    dataMap["mediaType"] = 5
                                    dataMap["mediaUrl"] = task.result.toString()
                                    dataMap["messageId"] = childRefKey
                                    dataMap["messageStatus"] = 1
                                    dataMap["messageType"] = 5
                                    dataMap["readTime"] = ""
                                    dataMap["readStatus"] = ""
                                    dataMap["mediaSize"] = mediaSizeStr
                                    dataMap["receiverId"] = otherUserId
                                    dataMap["isFromReply"] = ""
                                    dataMap["replyTo"] = ""
                                    dataMap["repliedOnMediaName"] = ""
                                    dataMap["repliedOnMediaType"] = ""
                                    dataMap["repliedOnMediaUrl"] = ""
                                    dataMap["repliedOnMessageId"] = ""
                                    dataMap["repliedOnText"] = ""
                                    dataMap["senderId"] = userId
                                    dataMap["senderName"] = profile.name
                                    dataMap["thumbnailUrl"] = thumbImgUrl

                                    msgDb.setValue(dataMap)
                                    Log.e("SendFile%$", " PDF_DirectSend")
                                }

                                /*  msgDb.child("messageText").setValue(mediaName)
                                  msgDb.child("deliveredTime").setValue(chatId)
                                  msgDb.child("deliveryStatus").setValue("1")
                                  msgDb.child("mediaDeviceUrlOf").setValue("")
                                  msgDb.child("mediaDeviceUrlOf").child(otherUserId).setValue("")
                                  msgDb.child("mediaDeviceUrlOf").child(userId)
                                      .setValue(fileAbsolutePath)
                                  msgDb.child("messagesDeletedBY").child(userId).setValue(false)
                                  msgDb.child("messagesDeletedBY").child(otherUserId).setValue(false)
                                  msgDb.child("messagesDeletedBYSender").setValue(false)
                                  msgDb.child("msgTime").setValue(System.currentTimeMillis())
                                  msgDb.child("mediaLength").setValue(mediaSizeStr)
                                  msgDb.child("mediaType").setValue(5)
                                  msgDb.child("mediaUrl").setValue(task.result.toString())
                                  msgDb.child("messageId").setValue(childRefKey)
                                  msgDb.child("messageStatus").setValue(1)
                                  msgDb.child("messageType").setValue(5)
                                  msgDb.child("readTime").setValue("")
                                  msgDb.child("readStatus").setValue("")
                                  msgDb.child("mediaSize").setValue(mediaSizeStr)
  //                            msgDb.child("otherUserId").setValue(otherUserId)
  //                            msgDb.child("otherUserName").setValue(otherUserName)
                                  //msgDb.child("replyTo").setValue(replyTo)
                                  msgDb.child("repliedOnMediaName").setValue("")
                                  msgDb.child("repliedOnMediaType").setValue("")
                                  msgDb.child("repliedOnMediaUrl").setValue("")
                                  msgDb.child("repliedOnMessageId").setValue("")
                                  msgDb.child("repliedOnText").setValue("")
                                  msgDb.child("senderId").setValue(userId)
                                  msgDb.child("senderName").setValue(profile.name)
                                  msgDb.child("thumbnailUrl").setValue(thumbImgUrl)
                                  //val userChatDb = chatDb.child(chatRoomId)*/

                                if (recycleViewPersonalChatAdapter == null) {

                                    recycleViewPersonalChatAdapter =
                                        RecycleViewPersonalChatAdapter(this@ChatActivity)

                                    binding!!.rvChat.adapter = recycleViewPersonalChatAdapter

                                    recycleViewPersonalChatAdapter!!.addChats(recycleModel)

                                    recycleViewPersonalChatAdapter!!.notifyItemInserted(recycleModel.lastIndex)

                                    val chatList = chatListDb.child(userId).child(chatRoomId)
                                    val otherChatList =
                                        chatListDb.child(otherUserId).child(chatRoomId)

                                    createUpdateChatList(
                                        chatList,
                                        fileExt,
                                        otherUserImg,
                                        "",
                                        "",
                                        "",
                                        "",
                                        0,
                                        otherUserName
                                    )

                                    if (profile.images.isNotEmpty()) {
                                        createUpdateChatList(
                                            otherChatList,
                                            fileExt,
                                            profile.images[0].imagePath,
                                            "",
                                            "",
                                            "",
                                            "",
                                            0,
                                            profile.name
                                        )
                                    } else {

                                        createUpdateChatList(
                                            otherChatList,
                                            fileExt,
                                            profile.userPic,
                                            "",
                                            "",
                                            "",
                                            "",
                                            0,
                                            profile.name
                                        )
                                    }
                                    scrollToBottomNested()

                                    Log.e("SendFile%$", " Refresh_PDF_Adapter")
                                } else {

                                    recycleViewPersonalChatAdapter!!.addChats(recycleModel)
                                    ///   recycleViewPersonalChatAdapter!!.notifyItemInserted(recycleModel.lastIndex)
                                    Log.e("SendFile%$", " Add_PDF_Adapter")
                                    scrollToBottomNested()
                                }
                                sendMsgNotification("file")
                                isSendingFile = false

                                binding!!.etChat.isEnabled = true

                                this@ChatActivity.fileUri = null
                                hideProgressView(progressBar)
                            } else hideProgressView(progressBar)
                        }
                    })
                }
            })

        } else {

            uploadTask.continueWithTask(object : Continuation<UploadTask.TaskSnapshot, Task<Uri>> {
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

                        val chatList = chatListDb.child(userId).child(chatRoomId)
                        val otherChatList = chatListDb.child(otherUserId).child(chatRoomId)

                        chatListDb.child(otherUserId)
                            .addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(snap: DataSnapshot) {

                                    if (snap.exists()) {

                                        unreadCount = snap.child(chatRoomId)
                                            .child("unReadCount").value.toString().toInt()
                                        createUpdateChatList(
                                            chatList,
                                            fileExt,
                                            otherUserImg,
                                            "",
                                            "",
                                            "",
                                            "",
                                            0,
                                            otherUserName
                                        )

                                        if (profile.images.isNotEmpty()) {
                                            createUpdateChatList(
                                                otherChatList,
                                                fileExt,
                                                profile.images[0].imagePath,
                                                "",
                                                "",
                                                "",
                                                "",
                                                unreadCount + 1,
                                                profile.name
                                            )
                                        } else {

                                            createUpdateChatList(
                                                otherChatList,
                                                fileExt,
                                                profile.userPic,
                                                "",
                                                "",
                                                "",
                                                "",
                                                unreadCount + 1,
                                                profile.name
                                            )
                                        }
                                    }
                                }

                                override fun onCancelled(p0: DatabaseError) {

                                }

                            })
                        val childRefKey: String = msgDb.key.toString()

                        if (isReplying) {
                            recycleModel.add(
                                RecycleModel(
                                    profile.name,
                                    mediaName,
                                    System.currentTimeMillis().toString(),
                                    userId,
                                    "1",
                                    "",
                                    "",
                                    replyTo,
                                    replyMsg,
                                    chatIdTm,
                                    chatId,
                                    chatImg,
                                    imgAbsolutePath,
                                    thumbImgUrl,
                                    "",
                                    "",
                                    task.result.toString(),
                                    mediaSizeStr
                                )
                            )
                            val dataMap = HashMap<String, Any>()
                            dataMap["messageText"] = mediaName
                            dataMap["deliveredTime"] = chatIdTm
                            dataMap["deliveryStatus"] = "1"
                            val mediaDeviceUrlOfMap = HashMap<String, Any>()
                            mediaDeviceUrlOfMap[otherUserId] = ""
                            mediaDeviceUrlOfMap[userId] = fileAbsolutePath
                            dataMap["mediaDeviceUrlOf"] = mediaDeviceUrlOfMap
                            val messagesDeletedByMap = HashMap<String, Any>()
                            messagesDeletedByMap[userId] = false
                            messagesDeletedByMap[otherUserId] = false
                            dataMap["messagesDeletedBY"] = messagesDeletedByMap
                            dataMap["messagesDeletedBYSender"] = false
                            dataMap["msgTime"] = System.currentTimeMillis()
                            dataMap["messageTime"] = System.currentTimeMillis()
                            dataMap["mediaLength"] = mediaSizeStr
                            dataMap["mediaType"] = 5
                            dataMap["mediaUrl"] = task.result.toString()
                            dataMap["messageId"] = childRefKey
                            dataMap["messageStatus"] = 1
                            dataMap["messageType"] = 5
                            dataMap["readTime"] = ""
                            dataMap["readStatus"] = ""
                            dataMap["mediaSize"] = mediaSizeStr
                            dataMap["receiverId"] = otherUserId
                            dataMap["isFromReply"] = isReplying
                            dataMap["replyTo"] = replyTo
                            dataMap["repliedOnMediaName"] = "File"
                            dataMap["repliedOnMediaType"] = "5"
                            dataMap["repliedOnMediaUrl"] = chatImg
                            dataMap["repliedOnMessageId"] = chatId
                            dataMap["repliedOnText"] = replyMsg
                            dataMap["senderId"] = userId
                            dataMap["senderName"] = profile.name
                            dataMap["thumbnailUrl"] = thumbImgUrl
                            msgDb.setValue(dataMap)

                            isSendingFile = false

                            binding!!.etChat.isEnabled = true

                            this@ChatActivity.fileUri = null

                            binding!!.replyChatLay.visibility = View.GONE
                            binding!!.replyLay.visibility = View.GONE
                            binding!!.sendingImgLay.visibility = View.GONE
                            //  sendingGifLay.visibility = View.GONE
                            isReplying = false
                            replyTo = ""
                            replyMsg = ""
                            replyMsgTime = ""
                            chatImg = ""
                        } else {
                            recycleModel.add(
                                RecycleModel(
                                    profile.name,
                                    mediaName,
                                    System.currentTimeMillis().toString(),
                                    userId,
                                    "1",
                                    "",
                                    "",
                                    "",
                                    "",
                                    chatIdTm,
                                    "",
                                    "",
                                    imgAbsolutePath,
                                    thumbImgUrl,
                                    "",
                                    "",
                                    task.result.toString(),
                                    mediaSizeStr
                                )
                            )
                            val dataMap = HashMap<String, Any>()
                            dataMap["messageText"] = mediaName
                            dataMap["deliveredTime"] = chatIdTm
                            dataMap["deliveryStatus"] = "1"

                            val mediaDeviceUrlOfMap = HashMap<String, Any>()
                            mediaDeviceUrlOfMap[otherUserId] = ""
                            mediaDeviceUrlOfMap[userId] = fileAbsolutePath
                            dataMap["mediaDeviceUrlOf"] = mediaDeviceUrlOfMap

                            val messagesDeletedByMap = HashMap<String, Any>()
                            messagesDeletedByMap[userId] = false
                            messagesDeletedByMap[otherUserId] = false
                            dataMap["messagesDeletedBY"] = messagesDeletedByMap

                            dataMap["messagesDeletedBYSender"] = false
                            dataMap["msgTime"] = System.currentTimeMillis()
                            dataMap["messageTime"] = System.currentTimeMillis()
                            dataMap["mediaLength"] = mediaSizeStr
                            dataMap["mediaType"] = 5
                            dataMap["mediaUrl"] = task.result.toString()
                            dataMap["messageId"] = childRefKey
                            dataMap["messageStatus"] = 1
                            dataMap["messageType"] = 5
                            dataMap["readTime"] = ""
                            dataMap["readStatus"] = ""
                            dataMap["mediaSize"] = mediaSizeStr
                            dataMap["isFromReply"] = ""
                            dataMap["replyTo"] = ""
                            dataMap["repliedOnMediaName"] = ""
                            dataMap["repliedOnMediaType"] = ""
                            dataMap["repliedOnMediaUrl"] = ""
                            dataMap["repliedOnMessageId"] = ""
                            dataMap["repliedOnText"] = ""
                            dataMap["senderId"] = userId
                            dataMap["senderName"] = profile.name
                            dataMap["thumbnailUrl"] = thumbImgUrl

                            msgDb.setValue(dataMap)

                            binding!!.replyChatLay.visibility = View.GONE
                            binding!!.replyLay.visibility = View.GONE
                            binding!!.sendingImgLay.visibility = View.GONE
                            //  sendingGifLay.visibility = View.GONE
                            isReplying = false
                            replyTo = ""
                            replyMsg = ""
                            replyMsgTime = ""
                            chatImg = ""
                        }

                        if (recycleViewPersonalChatAdapter == null) {

                            recycleViewPersonalChatAdapter =
                                RecycleViewPersonalChatAdapter(this@ChatActivity)

                            binding!!.rvChat.adapter = recycleViewPersonalChatAdapter

                            recycleViewPersonalChatAdapter!!.addChats(recycleModel)

                            recycleViewPersonalChatAdapter!!.notifyItemInserted(recycleModel.lastIndex)

                            val chatList = chatListDb.child(userId).child(chatRoomId)
                            val otherChatList = chatListDb.child(otherUserId).child(chatRoomId)

                            createUpdateChatList(
                                chatList, fileExt, otherUserImg, "", "", "", "", 0, otherUserName
                            )

                            if (profile.images.isNotEmpty()) {
                                createUpdateChatList(
                                    otherChatList,
                                    fileExt,
                                    profile.images[0].imagePath,
                                    "",
                                    "",
                                    "",
                                    "",
                                    0,
                                    profile.name
                                )
                            } else {

                                createUpdateChatList(
                                    otherChatList,
                                    fileExt,
                                    profile.userPic,
                                    "",
                                    "",
                                    "",
                                    "",
                                    0,
                                    profile.name
                                )
                            }
                            scrollToBottomNested()
                        } else {

                            recycleViewPersonalChatAdapter!!.addChats(recycleModel)
                            /// recycleViewPersonalChatAdapter!!.notifyItemInserted(recycleModel.lastIndex)
                            scrollToBottomNested()
                        }


                        /*  msgDb.child("messageText").setValue(mediaName)
                          msgDb.child("deliveredTime").setValue(chatId)
                          msgDb.child("deliveryStatus").setValue("1")
                          msgDb.child("mediaDeviceUrlOf").setValue("")
                          msgDb.child("mediaDeviceUrlOf").child(otherUserId).setValue("")
                          msgDb.child("mediaDeviceUrlOf").child(userId)
                              .setValue(fileAbsolutePath)
                          msgDb.child("messagesDeletedBY").child(userId).setValue(false)
                          msgDb.child("messagesDeletedBY").child(otherUserId).setValue(false)
                          msgDb.child("messagesDeletedBYSender").setValue(false)
                          msgDb.child("msgTime").setValue(System.currentTimeMillis())
                          msgDb.child("mediaLength").setValue(mediaSizeStr)
                          msgDb.child("mediaType").setValue(5)
                          msgDb.child("mediaUrl").setValue(task.result.toString())
                          msgDb.child("messageId").setValue(childRefKey)
                          msgDb.child("messageStatus").setValue(1)
                          msgDb.child("messageType").setValue(5)
                          msgDb.child("readTime").setValue("")
                          msgDb.child("readStatus").setValue("")
                          msgDb.child("mediaSize").setValue(mediaSizeStr)
  //                            msgDb.child("otherUserId").setValue(otherUserId)
  //                            msgDb.child("otherUserName").setValue(otherUserName)
                          //msgDb.child("replyTo").setValue(replyTo)
                          msgDb.child("repliedOnMediaName").setValue("")
                          msgDb.child("repliedOnMediaType").setValue("")
                          msgDb.child("repliedOnMediaUrl").setValue("")
                          msgDb.child("repliedOnMessageId").setValue("")
                          msgDb.child("repliedOnText").setValue("")
                          msgDb.child("senderId").setValue(userId)
                          msgDb.child("senderName").setValue(profile.name)
                          msgDb.child("thumbnailUrl").setValue(thumbImgUrl)*/
                        //val userChatDb = chatDb.child(chatRoomId)
                        sendMsgNotification("file")
                        isSendingFile = false

                        binding!!.etChat.isEnabled = true

                        this@ChatActivity.fileUri = null
                        scrollToBottomNested()
                        hideProgressView(progressBar)
                    } else hideProgressView(progressBar)
                }
            })
        }
        /// sendMsgNotification("file")
    }

    private fun sendGifMsg(media: Media) {
        isSending = true
        binding!!.etChat.isEnabled = true
        message = "GIF"
        val chatIdTm = System.currentTimeMillis().toString()
        newMsgId = chatIdTm
        Log.e("Tag", "Id: " + newMsgId)
        Log.e("Tag", "sendGifMsg: " + media.id)

        chatDb.child(chatRoomId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snap: DataSnapshot) {

                if (snap.exists()) {

                    /* userChatDb.child("LastMsg").setValue("GIF")
                    userChatDb.child("SenderId").setValue(userId)
                    userChatDb.child("SenderName").setValue(profile.name)
                    userChatDb.child("LastMsgTime").setValue(System.currentTimeMillis().toString())*/

                    val userChatDb = chatDb.child(chatRoomId)

                    val msgDb = userChatDb.child("chats").push()

                    /*   val readDb = msgDb.child("readBy").child(userId)
                       readDb.child("read").setValue("yes")
                       msgDb.child("msg").setValue("")
                       msgDb.child("senderId").setValue(userId)
                       msgDb.child("senderName").setValue(profile.name)
                       msgDb.child("msgTime").setValue(System.currentTimeMillis().toString())
                       msgDb.child("messageId").setValue(childRefKey)
                       msgDb.child("videoUrl").setValue("")
                       msgDb.child("GifId").setValue(media.id)
                       msgDb.child("msgClearedBy").child(userId).setValue("no")
                       msgDb.child("msgClearedBy").child(otherUserId).setValue("no")*/


                    /*  val data = HashMap<String, Any>()
                      val readByhMap = HashMap<String, Any>()
                      val readhMap = HashMap<String, String>()
                      readhMap["read"] = "yes"
                      readByhMap[userId] = readhMap
                      data["readBy"] = readByhMap
                      data["messageText"] = "Gif"
                      data["msg"] = ""
                      data["senderId"] = userId
                      data["senderName"] = profile.name
                      data["msgTime"] = System.currentTimeMillis().toString()
                      data["messageId"] = childRefKey
                      data["videoUrl"] = ""
                      data["GifId"] = media.id
                      val innerHashMap = HashMap<String, String>()
                      innerHashMap[otherUserId] = "no"
                      innerHashMap[userId] = "no"
                      data["msgClearedBy"] = innerHashMap
                      msgDb.setValue(data)*/

                    if (isReplying) {

                        val childRefKey: String = msgDb.key.toString()
                        val dataMap = HashMap<String, Any>()
                        dataMap["messageText"] = "GIF"
                        dataMap["deliveredTime"] = chatIdTm
                        dataMap["deliveryStatus"] = "1"
                        val innerHashMap = HashMap<String, String>()
                        innerHashMap[otherUserId] = ""
                        innerHashMap[userId] = ""
                        dataMap["mediaDeviceUrlOf"] = innerHashMap
                        dataMap["GifId"] = media.id
                        val innerHashMapDelet = HashMap<String, Boolean>()
                        innerHashMapDelet[otherUserId] = false
                        innerHashMapDelet[userId] = false
                        dataMap["messagesDeletedBY"] = innerHashMapDelet
                        dataMap["messagesDeletedBYSender"] = false
                        dataMap["msgTime"] = System.currentTimeMillis()
                        dataMap["messageTime"] = System.currentTimeMillis()
                        dataMap["mediaLength"] = "0"
                        dataMap["mediaType"] = 6
                        dataMap["mediaUrl"] = ""
                        dataMap["messageId"] = childRefKey
                        dataMap["messageStatus"] = 1
                        dataMap["messageType"] = 6
                        dataMap["readTime"] = ""
                        dataMap["readStatus"] = ""
                        dataMap["mediaSize"] = ""
                        dataMap["receiverId"] = otherUserId
                        dataMap["isFromReply"] = isReplying
                        dataMap["replyTo"] = replyTo
                        dataMap["repliedOnMediaName"] = "GIF"
                        dataMap["repliedOnMediaType"] = "6"
                        dataMap["repliedOnMediaUrl"] = chatImg
                        dataMap["repliedOnMessageId"] = chatId
                        dataMap["repliedOnText"] = replyMsg
                        dataMap["senderId"] = userId
                        dataMap["senderName"] = profile.name
                        dataMap["thumbnailUrl"] = ""
                        msgDb.setValue(dataMap)

                        Log.e("Tag", "msgDb: " + msgDb)

                        recycleModel.add(
                            RecycleModel(
                                profile.name,
                                "",
                                System.currentTimeMillis().toString(),
                                userId,
                                "1",
                                "",
                                "",
                                replyTo,
                                replyMsg,
                                chatIdTm,
                                chatId,
                                chatImg,
                                "",
                                "",
                                "",
                                media.id,
                                "",
                                ""
                            )
                        )

                        binding!!.replyChatLay.visibility = View.GONE
                        binding!!.replyLay.visibility = View.GONE
                        binding!!.sendingImgLay.visibility = View.GONE
                        //  sendingGifLay.visibility = View.GONE
                        isReplying = false
                        replyTo = ""
                        replyMsg = ""
                        replyMsgTime = ""
                        chatImg = ""
                    } else {

                        chatImg = ""
                        val childRefKey: String = msgDb.key.toString()
                        val dataMap = HashMap<String, Any>()
                        dataMap["messageText"] = "GIF"
                        dataMap["deliveredTime"] = chatIdTm
                        dataMap["deliveryStatus"] = "1"
                        val innerHashMap = HashMap<String, String>()
                        innerHashMap[otherUserId] = ""
                        innerHashMap[userId] = ""
                        dataMap["mediaDeviceUrlOf"] = innerHashMap
                        dataMap["GifId"] = media.id
                        val innerHashMapDelet = HashMap<String, Boolean>()
                        innerHashMapDelet[otherUserId] = false
                        innerHashMapDelet[userId] = false
                        dataMap["messagesDeletedBY"] = innerHashMapDelet
                        dataMap["messagesDeletedBYSender"] = false
                        dataMap["msgTime"] = System.currentTimeMillis()
                        dataMap["messageTime"] = System.currentTimeMillis()
                        dataMap["mediaLength"] = "0"
                        dataMap["mediaType"] = 6
                        dataMap["mediaUrl"] = ""
                        dataMap["messageId"] = childRefKey
                        dataMap["messageStatus"] = 1
                        dataMap["messageType"] = 6
                        dataMap["readTime"] = ""
                        dataMap["readStatus"] = ""
                        dataMap["mediaSize"] = ""
                        dataMap["receiverId"] = otherUserId
                        dataMap["isFromReply"] = ""
                        dataMap["replyTo"] = ""
                        dataMap["repliedOnMediaName"] = ""
                        dataMap["repliedOnMediaType"] = ""
                        dataMap["repliedOnMediaUrl"] = ""
                        dataMap["repliedOnMessageId"] = ""
                        dataMap["repliedOnText"] = ""
                        dataMap["senderId"] = userId
                        dataMap["senderName"] = profile.name
                        dataMap["thumbnailUrl"] = ""
                        msgDb.setValue(dataMap)

                        Log.e("Tag", "msgDb: " + msgDb)

                        recycleModel.add(
                            RecycleModel(
                                profile.name,
                                "",
                                System.currentTimeMillis().toString(),
                                userId,
                                "1",
                                "",
                                "",
                                replyTo,
                                replyMsg,
                                chatIdTm,
                                "",
                                "",
                                "",
                                "",
                                "",
                                media.id,
                                "",
                                ""
                            )
                        )
                        binding!!.replyChatLay.visibility = View.GONE
                        binding!!.replyLay.visibility = View.GONE
                        binding!!.sendingImgLay.visibility = View.GONE
                        // sendingGifLay.visibility = View.GONE
                        isReplying = false
                        replyTo = ""
                        replyMsg = ""
                        replyMsgTime = ""
                    }

                    val chatList = chatListDb.child(userId).child(chatRoomId)
                    val otherChatList = chatListDb.child(otherUserId).child(chatRoomId)

                    ///  createUpdateChatList(chatList, "GIF", otherUserImg, "", "", "", "", 0, otherUserName)

                    chatListDb.child(otherUserId)
                        .addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snap: DataSnapshot) {

                                if (snap.exists()) {

                                    unreadCount =
                                        snap.child(chatRoomId).child("unReadCount").value.toString()
                                            .toInt()
                                    createUpdateChatList(
                                        chatList,
                                        "GIF",
                                        otherUserImg,
                                        "",
                                        "",
                                        "",
                                        "",
                                        0,
                                        otherUserName
                                    )

                                    if (profile.images.isNotEmpty()) {
                                        createUpdateChatList(
                                            otherChatList,
                                            "GIF",
                                            profile.images[0].imagePath,
                                            "",
                                            "",
                                            "",
                                            "",
                                            unreadCount + 1,
                                            profile.name
                                        )
                                    } else {

                                        createUpdateChatList(
                                            otherChatList,
                                            "GIF",
                                            profile.userPic,
                                            "",
                                            "",
                                            "",
                                            "",
                                            unreadCount + 1,
                                            profile.name
                                        )
                                    }

                                }
                            }

                            override fun onCancelled(p0: DatabaseError) {
                                Log.e("Tag", "onCancelled_Gif_*&")
                            }


                        })

                    if (recycleViewPersonalChatAdapter == null) {

                        recycleViewPersonalChatAdapter =
                            RecycleViewPersonalChatAdapter(this@ChatActivity)

                        binding!!.rvChat.adapter = recycleViewPersonalChatAdapter

                        recycleViewPersonalChatAdapter!!.addChats(recycleModel)

                        recycleViewPersonalChatAdapter!!.notifyItemInserted(recycleModel.lastIndex)

                        if (profile.images.isNotEmpty()) {
                            createUpdateChatList(
                                otherChatList,
                                "GIF",
                                profile.images[0].imagePath,
                                "",
                                "",
                                "",
                                "",
                                0,
                                profile.name
                            )
                        } else {

                            createUpdateChatList(
                                otherChatList,
                                "GIF",
                                profile.userPic,
                                "",
                                "",
                                "",
                                "",
                                0,
                                profile.name
                            )
                        }
                        scrollToBottomNested()
                    } else {

                        recycleViewPersonalChatAdapter!!.addChats(recycleModel)

                        ///  recycleViewPersonalChatAdapter!!.notifyItemInserted(recycleModel.lastIndex)
                        Log.e(TAG, "GifItem_sendGifMsg-Group: " + recycleModel.size)
                        scrollToBottomNested()
                        //recycleViewPersonalChatAdapter!!.notifyDataSetChanged()
                    }
                    sendMsgNotification("gif")

                } else {
                    val chatList = chatListDb.child(userId).child(chatRoomId)
                    val otherChatList = chatListDb.child(otherUserId).child(chatRoomId)

                    ///  createUpdateChatList(chatList, "GIF", otherUserImg, "", "", "", "", 0, otherUserName)

                    chatListDb.child(otherUserId)
                        .addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snap: DataSnapshot) {

                                val msgId = System.currentTimeMillis().toString()

                                val msgDb = chatDb.child(chatRoomId).child("chats").push()
                                val childRefKey: String = msgDb.key.toString()
                                val dataMap = HashMap<String, Any>()
                                dataMap["messageText"] = "GIF"
                                dataMap["deliveredTime"] = chatIdTm
                                dataMap["deliveryStatus"] = "1"
                                val innerHashMap = HashMap<String, String>()
                                innerHashMap[otherUserId] = ""
                                innerHashMap[userId] = ""
                                dataMap["mediaDeviceUrlOf"] = innerHashMap
                                dataMap["GifId"] = media.id
                                val innerHashMapDelet = HashMap<String, Boolean>()
                                innerHashMapDelet[otherUserId] = false
                                innerHashMapDelet[userId] = false
                                dataMap["messagesDeletedBY"] = innerHashMapDelet
                                dataMap["messagesDeletedBYSender"] = false
                                dataMap["msgTime"] = System.currentTimeMillis()
                                dataMap["messageTime"] = System.currentTimeMillis()
                                dataMap["mediaLength"] = "0"
                                dataMap["mediaType"] = 6
                                dataMap["mediaUrl"] = ""
                                dataMap["messageId"] = childRefKey
                                dataMap["messageStatus"] = 1
                                dataMap["messageType"] = 6
                                dataMap["readTime"] = ""
                                dataMap["readStatus"] = ""
                                dataMap["mediaSize"] = ""
                                dataMap["receiverId"] = otherUserId
                                dataMap["isFromReply"] = ""
                                dataMap["replyTo"] = ""
                                dataMap["repliedOnMediaName"] = ""
                                dataMap["repliedOnMediaType"] = ""
                                dataMap["repliedOnMediaUrl"] = ""
                                dataMap["repliedOnMessageId"] = ""
                                dataMap["repliedOnText"] = ""
                                dataMap["senderId"] = userId
                                dataMap["senderName"] = profile.name
                                dataMap["thumbnailUrl"] = ""
                                msgDb.setValue(dataMap)

                                Log.e("Tag", "msgDb: " + msgDb)

                                recycleModel.add(
                                    RecycleModel(
                                        profile.name,
                                        "",
                                        System.currentTimeMillis().toString(),
                                        userId,
                                        "1",
                                        "",
                                        "",
                                        replyTo,
                                        replyMsg,
                                        chatIdTm,
                                        "",
                                        "",
                                        "",
                                        "",
                                        "",
                                        media.id,
                                        "",
                                        ""
                                    )
                                )
                                binding!!.replyChatLay.visibility = View.GONE
                                binding!!.replyLay.visibility = View.GONE
                                binding!!.sendingImgLay.visibility = View.GONE
                                // sendingGifLay.visibility = View.GONE
                                isReplying = false
                                replyTo = ""
                                replyMsg = ""
                                replyMsgTime = ""


                                unreadCount =
                                    snap.child(chatRoomId).child("unReadCount").value.toString()
                                        .toInt()
                                createUpdateChatList(
                                    chatList, "GIF", otherUserImg, "", "", "", "", 0, otherUserName
                                )

                                if (profile.images.isNotEmpty()) {
                                    createUpdateChatList(
                                        otherChatList,
                                        "GIF",
                                        profile.images[0].imagePath,
                                        "",
                                        "",
                                        "",
                                        "",
                                        unreadCount + 1,
                                        profile.name
                                    )
                                } else {

                                    createUpdateChatList(
                                        otherChatList,
                                        "GIF",
                                        profile.userPic,
                                        "",
                                        "",
                                        "",
                                        "",
                                        unreadCount + 1,
                                        profile.name
                                    )
                                }


                            }

                            override fun onCancelled(p0: DatabaseError) {
                                Log.e("Tag", "onCancelled_Gif_*&")
                            }


                        })

                    if (recycleViewPersonalChatAdapter == null) {

                        recycleViewPersonalChatAdapter =
                            RecycleViewPersonalChatAdapter(this@ChatActivity)

                        binding!!.rvChat.adapter = recycleViewPersonalChatAdapter

                        recycleViewPersonalChatAdapter!!.addChats(recycleModel)

                        recycleViewPersonalChatAdapter!!.notifyItemInserted(recycleModel.lastIndex)

                        if (profile.images.isNotEmpty()) {
                            createUpdateChatList(
                                otherChatList,
                                "GIF",
                                profile.images[0].imagePath,
                                "",
                                "",
                                "",
                                "",
                                0,
                                profile.name
                            )
                        } else {

                            createUpdateChatList(
                                otherChatList,
                                "GIF",
                                profile.userPic,
                                "",
                                "",
                                "",
                                "",
                                0,
                                profile.name
                            )
                        }
                        scrollToBottomNested()
                    } else {
                        recycleViewPersonalChatAdapter!!.addChats(recycleModel)
                        ///       recycleViewPersonalChatAdapter!!.notifyItemInserted(recycleModel.lastIndex)
                        Log.e(TAG, "GifItem_sendGifMsg-Group: " + recycleModel.size)
                        scrollToBottomNested()
                        //recycleViewPersonalChatAdapter!!.notifyDataSetChanged()
                    }
                    sendMsgNotification("gif")
                }
            }

            override fun onCancelled(p0: DatabaseError) {
                Log.e("Tag", "onCancelled_Gif")
            }
        })
    }

    private fun UpdateChatListConnected(chatList: DatabaseReference) {
        Log.e("UpdateChatListConnected", ": Yes")
        chatList.child("isUserConnected").setValue("yes")
    }

    private fun createUpdateChatList(
        chatList: DatabaseReference,
        msg: String,
        otherImg: String,
        replyTo: String,
        replyMsg: String,
        replyId: String,
        replyMedia: String,
        unreadCount: Int,
        userName: String,
    ) {

        var otherId = ""

        if (userName == profile.name) {

            otherId = profile.userId

        } else {

            otherId = otherUserId
        }
        val msgTime = System.currentTimeMillis().toString()
        Log.e("Send_Sms*&", "createUpdateChatList")
        chatList.child("chatOpen").setValue(false)
        chatList.child("convoId").setValue(chatRoomId)
        chatList.child("deliveredTime").setValue(System.currentTimeMillis())
        chatList.child("deliveryStatus").setValue("1")
        chatList.child("messageText").setValue(msg)
        chatList.child("messageTime").setValue(System.currentTimeMillis())
        chatList.child("otherUserOnline").setValue(true)
        chatList.child("readStatus").setValue("")
        chatList.child("readTime").setValue("")
        chatList.child("messagesDeletedBY").setValue("")
        chatList.child("messagesDeletedBYSender").setValue(false)
        chatList.child("receiverId").setValue(otherUserId)
        //chatList.child("receiverName").setValue(otherUserName)
        chatList.child("blockedBY").setValue("")

        /*chatList.child("isUserConnected").setValue(isUserConnected)
        chatList.child("isFirstMassage").setValue(isFirstMassage)*/

        chatList.child("otherUserId").setValue(otherId)
        chatList.child("repliedOnMediaName").setValue("")
        chatList.child("repliedOnMediaType").setValue("")
        chatList.child("repliedOnMediaUrl").setValue(replyMedia)
        chatList.child("repliedOnMessageId").setValue(replyId)
        chatList.child("repliedOnText").setValue(replyMsg)
        chatList.child("senderId").setValue(userId)
        chatList.child("senderName").setValue(profile.name)
        chatList.child("unReadCount").setValue(unreadCount.toString())
        chatList.child("OtherUserImage").setValue(otherImg)
        chatList.child("otherUserName").setValue(userName)

    }

    private fun sendMsgNotification(msgType: String) {

        val time = newMsgId.toLong()

        Log.d("SendMsgTimeN", time.toString())
        val adminTime = -System.currentTimeMillis()
        val id = userId
        val other = otherUserId/*  if(other == "3"){
              time = - time
          }*/

        val name = sharedPreferencesUtil.fetchUserProfile().name
        val conversationConstantId = chatId(10)
        val conversationModel = ConversationModel(
            message,
            id,
            conversationConstantId,
            chatRoomId,
            chatOpen = false,
            meDeletedConvo = false,
            otherUserOnline = if (null != conversation) conversation!!.otherUserOnline else false,
            userTyping = false,
            blockedBY = "",
            lastMessagetime = time,
            otherUserId = other,
            otherUserName = otherUserName,
            unReadCount = "0"
        )

        val otherConversationModel = ConversationModel(
            message,
            otherUserId,
            conversationConstantId,
            chatRoomId,
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
            chatId(8),
            message,
            id,
            name,
            Constants.MessageStatus.Sent,
            time,
            "0",
            Constants.MediaType.Text,
            "",
            ""
        )

        val dataModel = Data().apply {
            senderImageUrl = sharedPreferencesUtil.fetchUserProfileImage()
            receiverId = otherUserId
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
            } else if (msgType == "file") {

                String.format(getString(R.string.text_shared_file), name)
            } else {

                String.format(getString(R.string.text_shared_video), name)
            }
            title = ArchitectureApp.instance!!.getString(R.string.app_name)
        }

        val notificationMod = NotificationModel().apply {
            data = dataModel
            notification = notificationMo
            to = sharedPreferencesUtil.fetchOtherUserProfile(otherUserId).deviceToken
        }

        val requestSendNotification = RequestSendNotification().apply {
            notificationModel = notificationMod
            senderImageUrl = dataModel.senderImageUrl
            receiverId = this@ChatActivity.otherUserId
            senderId = conversationModel.chatSenderId
            this.otherUserId = this@ChatActivity.otherUserId
            otherUserName = this@ChatActivity.otherUserName
        }

        Log.e("Request$@", "Notification::" + requestSendNotification.toString())

        if (null == mPresenter) mPresenter = ChatPresenter(presenter)

        run {
            mPresenter!!.addSendNotificationObject(requestSendNotification)
            mPresenter!!.callRetrofit(ConstantsApi.SEND_NOTIFICATION)
        }
    }

    private fun setUpOtherUserDetails() {


        binding!!.header.tvUsername.text = otherUserName
        binding!!.header.tvUserStatus.visibility = View.VISIBLE

        var requestOptions = RequestOptions()
        requestOptions = requestOptions.transforms(CenterCrop(), RoundedCorners(16))

        Glide.with(this@ChatActivity).load(otherUserImg)
            .placeholder(R.drawable.meetupsfellow_transpatent).apply(requestOptions)
            .into(binding!!.header.ivUserImage)

    }

    private fun startLoadingAnim() {

        val animation = android.view.animation.AnimationUtils.loadAnimation(
            applicationContext, R.anim.blink_anim
        )

        binding!!.includedLoading.animLogo.startAnimation(animation)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val mngr = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager

        val taskList = mngr.getRunningTasks(10)
        if (taskList[0].numActivities == 1 && taskList[0].topActivity!!.className == this::class.java.name) {
            Log.e(TAG, "This is the last activity in the stack")
            val intent = Intent(
                this, HomeActivity::class.java
            )
            startActivity(intent)
            finish()
        } else {
            Log.e(TAG, "activity allready exist in the stack")
            finish()
        }
    }

    override fun onResume() {
        super.onResume()

        onCurrentActivity = true

        // Close old notification banner in open chat below
        showPopupWindow(binding!!.rvChat)

        //myDbHandeler = MyDbHandeler(this@ChatActivity)

        deleteChatsArr.clear()/*var lasttrackTime = sharedPreferencesUtil.featchTimmerTime()
        var currentbackTime: Long = System.currentTimeMillis()
        Log.e("lasttrackTime", lasttrackTime)
        LogManager.logger.i(ArchitectureApp.instance!!.tag, "onResume ::: " + lasttrackTime)

        if (lasttrackTime != "") {
            val currentSeconds = ((currentbackTime - lasttrackTime.toLong()) / 1000).toInt()
            if (currentSeconds >= Constants.TimerRefresh.START_TIME_IN_SEC) {
                startActivity(Intent(this, SplashActivity::class.java))
                finish()
            }
        }
*/

        /*if (conversation!!.convoId != Constants.Admin.Convo) {
            updateProfile(conversation!!.convoId)
            running = true
            clearBadge()
            RecyclerViewClick.enableClick(object : ItemClick {
                override fun onItemClick(position: Int, status: ItemClickStatus) {
                    showAlertForPremium("Your account has been upgraded to PRO.")
                }
            })
        }*/
    }

    override fun onPause() {
        super.onPause()

        sharedPreferencesUtil.saveTimmerTime(System.currentTimeMillis().toString())
        LogManager.logger.i(
            ArchitectureApp.instance!!.tag, "onPause ::: " + System.currentTimeMillis().toString()
        )

        onCurrentActivity = false/*if (conversation!!.convoId != Constants.Admin.Convo) {
            updateProfile("")
            saveChat()
            if (null != HomeActivity.typing)
                HomeActivity.typing = null
            RecyclerViewClick.disableClick()
            running = false
        }*/
    }

    override fun onDestroy() {
        super.onDestroy()

        onCurrentActivity = false
    }

    private fun showAlertForPremiumImage(message: String, s: String) {
        val alertDialog = AlertDialog.Builder(this@ChatActivity, R.style.MyDialogTheme2)
        // Setting DialogAction Message
        alertDialog.setMessage(message)
        // On pressing SettingsActivity button
        alertDialog.setPositiveButton(getString(R.string.text_upgrade)) { dialog, _ ->
            dialog.dismiss()
            sharedPreferencesUtil.premiunNearby("true")
            AlertBuyPremium.Builder(this@ChatActivity, Constants.DialogTheme.NoTitleBar).build()
                .show()

        }
        alertDialog.setNegativeButton(getString(R.string.text_cancel)) { dialog, _ ->
            dialog.dismiss()
        }
        alertDialog.setCancelable(false)
        // Showing Alert Message
        alertDialog.show()
    }

    /*override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        conversation?.apply {

            val profile = sharedPreferencesUtil.fetchUserProfile()

            if (convoId == Constants.Admin.Convo || otherUserId == Constants.Admin.ConvoAdminOneToOne) {
                if (otherUserId == Constants.Admin.ConvoAdminOneToOne) {
                    menuInflater.inflate(R.menu.menu_user, menu)
                    lockMenuItem = menu!!.findItem(R.id.menu_lock)
                    lockMenuItem.isVisible = false
                }
            } else {
                menuInflater.inflate(R.menu.menu_user, menu)
                lockMenuItem = menu!!.findItem(R.id.menu_lock)
                // lockMenuItem.setVisible(true)
                if (isMyPrivateAlbumSharedWithUser) {
                    lockMenuItem.icon =
                        ContextCompat.getDrawable(this@ChatActivity, R.drawable.ic_unlocked)
                } else {
                    lockMenuItem.icon = ContextCompat.getDrawable(
                        this@ChatActivity, R.drawable.ic_locked_gray
                    )
                }
            }

            //menuInflater.inflate(R.menu.menu_user, menu)
            val menu_callItem = menu!!.findItem(R.id.menu_call)
            menuClear = menu.findItem(R.id.menu_clear)
            chatDeleteIc = menu.findItem(R.id.menu_delete_chat)
            copyMenuItem = menu.findItem(R.id.menu_copy_chat)

            Log.e("chatActivity*& ", "menu_clear_chat_Gone")
            /// Add_below user connect or not
            *//*   if (otherUserProfile.connection_status.equals("connected")) {
                   menuClear.isVisible = true
                   menu_callItem.isVisible = false
               } else {
                   menuClear.isVisible = false
               }*//*
            menu_callItem.isVisible = false
          ///  chatDeleteIc.isVisible = false
            binding!!.header.deleteChat.visibility = View.GONE
           /// binding!!.header.deleteChat.isVisible = false
            *//*val menu_blockItem = menu!!.findItem(R.id.menu_block)

            if (conversation!!.blockedBY.trim().isNotEmpty()) {

                menu_blockItem.setVisible(false)
            } else {

                menu_blockItem.setVisible(true)
            }*//*
        }

        if (conversation == null) {

            menuInflater.inflate(R.menu.menu_user, menu)
            val menu_callItem = menu!!.findItem(R.id.menu_call)
            menuClear = menu.findItem(R.id.menu_clear)
            chatDeleteIc = menu.findItem(R.id.menu_delete_chat)
            copyMenuItem = menu.findItem(R.id.menu_copy_chat)
            /// Add_below user connect or not
            Log.e("chatActivity*& ", "menu_clear_chat_Gone22")*//* if (otherUserProfile.connection_status.equals("connected")) {
                 menuClear.isVisible = true
             } else {
                 menuClear.isVisible = false
             }*//*
            menu_callItem.isVisible = false

        }

        *//*if ( otherUserId!= Constants.Admin.ConvoAdminOneToOne  ) {
            menuInflater.inflate(R.menu.menu_user, menu)
            lockMenuItem = menu!!.findItem(R.id.menu_lock)
            if (isMyPrivateAlbumSharedWithUser) {
                lockMenuItem?.setIcon(
                    ContextCompat.getDrawable(this@ChatActivity, R.drawable.ic_unlocked)
                )
            } else {
                lockMenuItem?.setIcon(
                    ContextCompat.getDrawable(this@ChatActivity,R.drawable.ic_locked_gray
                    )
                )
            }
        }*//*
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_more -> {
                optionMenu()
                true
            }

            R.id.menu_lock -> {
                run {
                    mPresenter!!.addUserProfileObject(conversation!!.otherUserId)
                    mPresenter!!.callRetrofit(ConstantsApi.SHARE_PRIVATE)
                }
                true
            }

            R.id.menu_clear -> {

                //showAlertClearChat("Are you sure, you want to clear Chat?", "Chat")
                showAlertChatDialog("Clear Chat", "Are you sure, you want to clear chat?", "clear")

                true
            }

            R.id.menu_block -> {

                *//*showAlertClearChat(
                    "Are you sure, you want to block ${conversation!!.otherUserName}?",
                    "block"
                )*//**//*showAlertChatDialog(
                    "Block User",
                    "Are you sure, you want to block this user?",
                    "block"
                )*//*

                true
            }

            R.id.menu_delete_chat -> {

                showDeleteMasgsDialog()
                //deleteChat()
                true
            }

            R.id.menu_copy_chat -> {

                val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("label", msgToCopy)
                clipboard.setPrimaryClip(clip)
                Log.e("CopyMsg@# ", msgToCopy)
                Toast.makeText(this@ChatActivity, "Message copied to clipboard", Toast.LENGTH_SHORT)
                    .show()

                true
            }

            R.id.menu_call -> {

                if (checkPermissionGranted()) {

                    val bundle = Bundle()
                    bundle.putString("otherUserId", otherUserId)
                    bundle.putString("otherUserName", binding!!.header.tvUsername.text.toString())
                    bundle.putString("otherUserImg", otherUserImg)
                    bundle.putString("type", "create")
                    bundle.putString("roomId", "")

                    switchActivity(Constants.Intent.PersonalCall, false, bundle)

                } else {

                    askPermissions()
                }
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }*/

    private fun showDeleteMasgsDialog() {
        Log.e("chatActivity*&", "openChatDelet_Dialog")
        val dialog = Dialog(this)
        dialog.setCancelable(true)

        val view = layoutInflater.inflate(R.layout.custom_delete_chat_dialog, null)

        dialog.setContentView(view)

        dialog.window!!.attributes.windowAnimations = R.style.DialogAnimation

        if (dialog.window != null) {
            dialog.window!!.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
            )
        }
        dialog.window!!.setGravity(Gravity.CENTER)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val dialogHead = view.findViewById<TextView>(R.id.dialog_title)
        val dialogContent = view.findViewById<TextView>(R.id.dialog_content)
        val cancelBtn = view.findViewById<Button>(R.id.noExitBtn)
        val confirm = view.findViewById<Button>(R.id.yesExitBtn)
        val forEveryExitBtn = view.findViewById<Button>(R.id.forEveryExitBtn)
        Log.e("TimeDiff_OK", "Size: " + recycleViewPersonalChatAdapter!!.selectedPos.size)
        if (recycleViewPersonalChatAdapter!!.selectedPos.size == 1) {
            Log.e("TimeDiff_OK2", "Size: " + recycleModel.size)

            if (recycleModel[recycleViewPersonalChatAdapter!!.selectedPos.get(0)
                    .toInt()].userId == profile.userId
            ) {
                val msgTime = recycleModel[recycleViewPersonalChatAdapter!!.selectedPos.get(0)
                    .toInt()].lastMsgTime!!.toLong()
                val currentTime = System.currentTimeMillis()

                Log.d("TimeDiff_OK", (currentTime - msgTime).toString())

                if (currentTime - msgTime <= 300000) {

                    //confirm.text = "For Everyone"
                    forEveryExitBtn.visibility = View.VISIBLE

                } else {

                    //confirm.text = "For Me"
                    forEveryExitBtn.visibility = View.GONE

                }

            } else {

                //confirm.text = "For Me"
                forEveryExitBtn.visibility = View.GONE
            }

        } else {

            //confirm.text = "For Me"
            forEveryExitBtn.visibility = View.GONE
        }

        cancelBtn.setOnClickListener {

            dialog.dismiss()

        }

        confirm.setOnClickListener {
            deleteFor = "me"
            deleteChat()
            dialog.dismiss()
        }

        forEveryExitBtn.setOnClickListener {

            deleteFor = "everyone"
            deleteChat()
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun askPermissions() {

        ActivityCompat.requestPermissions(this, permissions, requestCode)
    }

    private fun checkPermissionGranted(): Boolean {

        for (permission in permissions) {

            if (ActivityCompat.checkSelfPermission(
                    this, permission
                ) != PackageManager.PERMISSION_GRANTED
            ) {

                return false
            }
        }

        return true
    }


    private fun showJoinDialog(callFrom: String, roomId: String) {

        val dialog = Dialog(this)

        dialog.setCancelable(true)

        val view = layoutInflater.inflate(R.layout.custom_exit_dialog, null)

        dialog.setContentView(view)

        dialog.window!!.attributes.windowAnimations = R.style.DialogAnimation

        if (dialog.window != null) {
            dialog.window!!.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
            )
        }
        dialog.window!!.setGravity(Gravity.CENTER)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val dialogHead = view.findViewById<TextView>(R.id.dialog_title)
        val dialogContent = view.findViewById<TextView>(R.id.dialog_content)
        val cancelBtn = view.findViewById<Button>(R.id.noExitBtn)
        val confirm = view.findViewById<Button>(R.id.yesExitBtn)

        dialogHead.text = "Call from $callFrom"
        dialogContent.text = "Accept or Reject the call"
        cancelBtn.text = "Reject"
        confirm.text = "Accept"

        cancelBtn.setOnClickListener {
            dialog.dismiss()
            clearRoom(roomId)
        }

        confirm.setOnClickListener {

            if (checkPermissionGranted()) {

                val bundle = Bundle()
                bundle.putString("otherUserId", conversation!!.otherUserId)
                bundle.putString("otherUserName", binding!!.header.tvUsername.text.toString())
                bundle.putString("otherUserImg", conversation!!.otherUserImageUrl)
                bundle.putString("type", "join")
                bundle.putString("roomId", roomId)

                switchActivity(Constants.Intent.PersonalCall, false, bundle)

                dialog.dismiss()
            } else {

                askPermissions()
            }
        }
        dialog.show()

    }

    private fun clearRoom(id: String) {

        val videoCallRoomDb =
            FirebaseDatabase.getInstance(Constants.Firebase.databaseUrl).getReference(Constants.Firebase.ChatDB)
                .child("VideoChatRoom")

        videoCallRoomDb.child(id).removeValue()
    }

    private fun showAlertChatDialog(head: String, content: String, type: String) {

        val dialog = Dialog(this)

        dialog.setCancelable(true)

        val view = layoutInflater.inflate(R.layout.custom_exit_dialog, null)

        dialog.setContentView(view)

        dialog.window!!.attributes.windowAnimations = R.style.DialogAnimation

        if (dialog.window != null) {
            dialog.window!!.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
            )
        }
        dialog.window!!.setGravity(Gravity.CENTER)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val dialogHead = view.findViewById<TextView>(R.id.dialog_title)
        val dialogContent = view.findViewById<TextView>(R.id.dialog_content)
        val cancelBtn = view.findViewById<Button>(R.id.noExitBtn)
        val confirm = view.findViewById<Button>(R.id.yesExitBtn)
        Log.e("Chat_Clear", "all_chat_clear_dialog")
        dialogHead.text = head
        dialogContent.text = content

        cancelBtn.setOnClickListener {

            dialog.cancel()
        }

        confirm.setOnClickListener {

            if (type == "clear") {

                Log.e("Chate_Clear", "iff_Confirm")
                dialog.dismiss()/*sharedPreferencesUtil.remove(conversation!!.convoId)
                sharedPreferencesUtil.remove("${conversation!!.convoId}_keys")
                chatAdapter!!.removeAll()
                clearChat()*/
                // clearSupportChat()
                clearChatMsgs()
                updateChatUi()
                binding!!.header.deleteChat.visibility = View.GONE
                binding!!.header.copyChat.visibility = View.GONE
            } /*else {

                dialog.dismiss()
                showProgressView(progressBar)
                firebaseUtil.updateUserBlocked(
                    sharedPreferencesUtil.userId(),
                    conversation!!.otherUserId,
                    sharedPreferencesUtil.userId()
                )
                Handler().postDelayed({
                    blockUser()
                }, 500)
            }*/
        }

        dialog.show()

    }

    private fun clearChatMsgs() {
        Log.e("Chate_Clear", "ClearChat")
        chatDb.child(chatRoomId).child("chats")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snap: DataSnapshot) {

                    if (snap.exists()) {

                        for (chats in snap.children) {
                            Log.e("Chate_Clear", "Snap:")/* chatDb.child(chatRoomId).child("chats").child(chats.key.toString())
                                .child("msgClearedBy").child(userId).setValue("yes")*/
                            chatDb.child(chatRoomId).child("chats").child(chats.key.toString())
                                .child("messagesDeletedBY").child(userId).setValue("true")
                        }
                    }
                }

                override fun onCancelled(p0: DatabaseError) {

                }
            })

    }

    private fun getIntentData() {
        when (intent.hasExtra(Constants.IntentDataKeys.Conversation)) {
            true -> {
                conversation = Gson().fromJson(
                    intent.getStringExtra(
                        Constants.IntentDataKeys.Conversation
                    ), ConversationModel::class.java
                )
                LogManager.logger.i(
                    ArchitectureApp.instance!!.tag, "Notification : Conversation 2 : $conversation"
                )
                userImage = intent.getStringExtra(Constants.IntentDataKeys.UserImage)!!
                connection_status =
                    intent.getStringExtra(Constants.IntentDataKeys.connection_status)
                        ?: "Not_connected"
                //  userImage = "https://s3.us-east-1.amazonaws.com/reegurapp/UserProfileImg/vIdlB8fJ72IHCO2toMgt3j8MitskOBqpRKUyKGJl.jpeg"
                status = intent.getIntExtra(Constants.IntentDataKeys.Status, 1)
                hasSharedPrivateAlbumWithMe = intent.getBooleanExtra(
                    Constants.IntentDataKeys.HasSharedPrivateAlbumWithMe, false
                )
                isMyPrivateAlbumSharedWithUser = intent.getBooleanExtra(
                    Constants.IntentDataKeys.IsMyPrivateAlbumSharedWithUser, false
                )
                Log.e(
                    "Value",
                    "***" + isMyPrivateAlbumSharedWithUser + "" + isMyPrivateAlbumSharedWithUser
                )
                Log.e("IntentData*", "connection_status: " + connection_status)

                Log.d("OtherUserImg", userImage)

                otherUserId = conversation!!.otherUserId
                otherUserImg = userImage
                otherUserName = conversation!!.otherUserName
                chatRoomId = conversation!!.convoId
                Log.e("ChatRoomIdIntentIn:  ", chatRoomId)
            }

            false -> {}
        }

        Log.e("ChatRoomIdIntent", intent.getStringExtra("ChatRoomId").toString())

        if (intent.getStringExtra("otherUserId").toString() != "null") {

            chatRoomId = intent.getStringExtra("ChatRoomId").toString()
            otherUserId = intent.getStringExtra("otherUserId").toString()
            otherUserName = intent.getStringExtra("otherUserName").toString()
            otherUserImg = intent.getStringExtra("otherUserImg").toString()
            IsUserConnected = intent.getStringExtra("IsUserConnected").toString()
            firstTime = intent.getBooleanExtra("firstTime", false)
        }
        Log.e("ChatRoomIdIntent", "IsUserConnected: $IsUserConnected")
        if (IsUserConnected != null && IsUserConnected.equals("no")) {
            this@ChatActivity.binding!!.rlFooter.visibility = View.GONE
        }

    }

    private fun fetchFirebaseChat() {
        firebaseUtil.fetchUserChat(conversation!!.convoId, object : FirebaseChatListener {
            override fun onChatEventListener(snapshot: DataSnapshot, chat: FirebaseChat) {
                when (snapshot.exists()) {
                    true -> {
                        try {
                            val message = snapshot.getValue(ChatModel::class.java)!!

                            when (chat) {
                                FirebaseChat.Added -> {
                                    when (message.chatStatus) {
                                        Constants.MessageStatus.Sent -> {
                                            LogManager.logger.i(
                                                ArchitectureApp.instance!!.tag,
                                                "Token is Message Add : $snapshot"
                                            )
                                            chatAdapter!!.add(message)
                                        }
                                    }
                                }

                                FirebaseChat.Removed -> {
//                                chatAdapter!!.remove("${snapshot.key}", message)
                                }

                                FirebaseChat.Changed -> {
                                    LogManager.logger.i(
                                        ArchitectureApp.instance!!.tag,
                                        "Token is Message update : $snapshot"
                                    )
                                    chatAdapter!!.updateMessage(message)
                                }
                            }

                            when (message.chatSenderId) {
                                sharedPreferencesUtil.userId() -> {
                                    when (message.chatStatus) {
                                        Constants.MessageStatus.NotSent -> {
                                            updateChatStatus(
                                                message.chatStatus, "${snapshot.key}", false
                                            )
                                        }

                                        /* Constants.MessageStatus.AdminSupportRead -> {
                                            otherUserConversation?.apply {
                                                unReadCount = "0"
                                            }
                                            removeMessage("${snapshot.key}")
                                        }
*/



                                        Constants.MessageStatus.Read -> {
                                            otherUserConversation?.apply {
                                                unReadCount = "0"
                                            }

                                            if (conversation!!.otherUserId != Constants.Admin.ConvoAdminOneToOne) {
                                                removeMessage("${snapshot.key}")

                                            }


                                        }
                                    }
//                                showImage(itemView.ivChatImage, message.mediaUrlThumb, R.drawable.mask_image)
                                }

                                else -> {
//                                showImage(itemView.ivChatImage, chatModel.mediaUrlThumb, R.drawable.mask_image_oth)
                                    updateChatStatus(message.chatStatus, "${snapshot.key}", true)
                                }
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }

                    false -> {}
                }
                updateChatUi()
            }
        })
    }

    @Suppress("UNUSED_PARAMETER")
    private fun updateChatStatus(status: Int, key: String, other: Boolean) {
        when (status) {
            Constants.MessageStatus.NotSent -> chatStatus(Constants.MessageStatus.Sent, key)
            Constants.MessageStatus.Sent -> chatStatus(Constants.MessageStatus.Delivered, key)
            Constants.MessageStatus.Delivered ->

                if (running) chatStatus(
                    Constants.MessageStatus.Read, key
                )

        }
    }

    private fun removeMessage(key: String) {
        firebaseUtil.deleteMessage(
            "${Constants.Firebase.Chat}/${conversation!!.convoId}/Chat/$key", key
        )
    }

    private fun chatStatus(status: Int, key: String) {
        LogManager.logger.i(ArchitectureApp.instance!!.tag, "Status is : $status")
        LogManager.logger.i(ArchitectureApp.instance!!.tag, "Status is : ${conversation!!.convoId}")
        LogManager.logger.i(ArchitectureApp.instance!!.tag, "Status is : $key")
        firebaseUtil.updateChatStatus("${Constants.Firebase.Chat}/${conversation!!.convoId}/Chat/$key/chatStatus",
            ChatModel().apply {
                chatStatus = status
            })
    }

    private fun updateChatUi() {
///        Log.e("CHate_Act", "recycle_Data: " + recycleViewPersonalChatAdapter!!.itemCount)
        ///  when (recycleViewPersonalChatAdapter?.itemCount ?: recycleViewPersonalChatAdapter!!.itemCount > 0) {
        when (recycleModel.size > 0) {
            true -> {
                binding!!.rvChat.visibility = View.VISIBLE
                binding!!.tvNoMessage.visibility = View.GONE
                binding!!.noChatImg.visibility = View.GONE
                binding!!.cvLimitChatHistory.visibility = View.GONE
                if (sharedPreferencesUtil.fetchUserProfile().currentPlanInfo!!.onetwooneChatHistoryLimit != 0) {
                    binding!!.cvLimitChatHistory.visibility = View.VISIBLE
                }
                //scrollToBottom()
            }

            false -> {
                binding!!.cvLimitChatHistory.visibility = View.GONE
                binding!!.rvChat.visibility = View.GONE
                binding!!.tvNoMessage.visibility = View.VISIBLE
                binding!!.noChatImg.visibility = View.VISIBLE
            }
        }
    }


    private fun profile() {

        switchActivity(Constants.Intent.ProfileUser, false, Bundle().apply {
            putInt(
                Constants.IntentDataKeys.UserId, otherUserId.toInt()
            )
        })

    }


    private fun selectImagePopup() {

        val dialog = Dialog(this@ChatActivity)

        dialog.setCancelable(false)

        //val activity = context as AppCompatActivity

        val view = layoutInflater.inflate(R.layout.custom_media_chooser, null)

        dialog.setContentView(view)

        dialog.window!!.attributes.windowAnimations = R.style.DialogAnimation

        if (dialog.window != null) {
            dialog.window!!.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
        dialog.window!!.setGravity(Gravity.CENTER)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val takePhoto = view.findViewById<LinearLayout>(R.id.takePhoto)
        val chooseGallery = view.findViewById<LinearLayout>(R.id.chooseGallery)
        val cancelBtn = view.findViewById<Button>(R.id.cancel_btn)
        val uploadVideos = view.findViewById<CardView>(R.id.uploadVideos)
        val uploadDoc = view.findViewById<CardView>(R.id.uploadDoc)
        uploadVideos.visibility = View.VISIBLE
        uploadDoc.visibility = View.VISIBLE

        if (sharedPreferencesUtil.fetchUserProfile().currentPlanInfo!!.chatMessageTypes == 0) {
            uploadVideos.visibility = View.GONE
            uploadDoc.visibility = View.GONE
        } else if (sharedPreferencesUtil.fetchUserProfile().currentPlanInfo!!.chatMessageTypes == 1) {
            uploadVideos.visibility = View.GONE
            uploadDoc.visibility = View.VISIBLE
        } else {
            uploadVideos.visibility = View.VISIBLE
            uploadDoc.visibility = View.VISIBLE
        }

        takePhoto.setOnClickListener {

            //this@ChatActivity.onCameraTapped()

            //EasyImage.openCameraForImage(this@ChatActivity, Constants.ImagePicker.CAMERA)

            ///  capturePhoto()
            camerarequestRuntimePermission()
            dialog.cancel()
        }

        chooseGallery.setOnClickListener {

            EasyImage.openGallery(
                this@ChatActivity,
                //ConstantsImage.RequestCodes.PICK_PICTURE_FROM_GALLERY
                Constants.ImagePicker.Image
            )

            //this@ChatActivity.onGalleryTapped()
            dialog.cancel()
        }

        uploadVideos.setOnClickListener {

            //this@ChatActivity.onVideoTapped()
            //showFileChooser()

            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.type = "video/*"
            startActivityForResult(intent, Constants.ImagePicker.Video)

            dialog.cancel()
        }

        uploadDoc.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "application/*" // All file types
                putExtra(Intent.EXTRA_MIME_TYPES, arrayOf(
                    "application/pdf",
                    "application/msword",
                    "application/vnd.ms-excel",
                    "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                    "text/plain"
                ))
            }
            startActivityForResult(intent, 300)
            dialog.cancel()
        }


        cancelBtn.setOnClickListener {

            dialog.cancel()
        }

        dialog.show()

        /*val options = arrayOf<CharSequence>("Recent", "Private", "Camera", "Gallery", "Video", "Cancel")
        val builder: android.app.AlertDialog.Builder = android.app.AlertDialog.Builder(this)
        builder.setTitle("Send Attachment!")
        builder.setItems(options, DialogInterface.OnClickListener { dialog, item ->
            if (options[item] == "Camera") {

                this@ChatActivity.onCameraTapped()

            } else if (options[item] == "Gallery") {

                this@ChatActivity.onGalleryTapped()

            } else if (options[item] == "Cancel") {

                dialog.dismiss()
            }
            else if (options[item] == "Recent"){

                when (sharedPreferencesUtil.fetchUserProfile().isProMembership) {
                    true -> {
                        startActivityForResult(
                            Intent(Constants.Intent.Recent), Constants.ImagePicker.Private
                        )
                    }

                    false -> showAlertForPremiumImage(
                        getString(R.string.alert_pro_user),
                        "recent"
                    )
                }
            }
            else if (options[item] == "Private"){

                when (sharedPreferencesUtil.fetchUserProfile().isProMembership) {
                    true -> {
                        startActivityForResult(
                            Intent(Constants.Intent.PrivateAlbumImages).putExtras(Bundle().apply {
                                putString(
                                    Constants.IntentDataKeys.Type,
                                    "${Constants.ImagePicker.Private}"
                                )
                            }),
                            Constants.ImagePicker.Private
                        )
                    }

                    false -> showAlertForPremiumImage(
                        getString(R.string.alert_pro_user),
                        "private"
                    )
                }
            }
        })
        builder.show()*/
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

    private fun optionMenu() {
        val dialog = Dialog(this@ChatActivity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_chat)
        dialog.setCanceledOnTouchOutside(true)
        val rect = locateView(binding!!.header.toolbar)
        val wmlp = dialog.window!!.attributes
        wmlp.gravity = Gravity.TOP
        wmlp.width = (resources.displayMetrics.widthPixels / 2.2).toInt()
        assert(rect != null)
        wmlp.x = rect!!.right
        wmlp.y = rect.top
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val block = dialog.findViewById<TextView>(R.id.tvBlock)
        val view_divider = dialog.findViewById<View>(R.id.view_divider)

        if (conversation!!.otherUserId == Constants.Admin.ConvoAdminOneToOne) {

            block.visibility = View.GONE
            view_divider.visibility = View.GONE

        } else {

            block.visibility = View.VISIBLE
            view_divider.visibility = View.VISIBLE

            block.setOnClickListener {
                dialog.cancel()
                showAlertClearChat(
                    "Are you sure, you want to block ${conversation!!.otherUserName}?", "block"
                )
            }

        }



        dialog.findViewById<TextView>(R.id.tvClearChat).setOnClickListener {
            dialog.cancel()
            showAlertClearChat("Are you sure, you want to clear Chat?", "Chat")
        }

        when (conversation!!.blockedBY.trim().isNotEmpty()) {
            true -> {
                block.text = getString(R.string.text_blocked)
                block.isClickable = false
                block.isEnabled = false
                block.setTextColor(Color.RED)
            }

            false -> {

                when (status) {
                    Constants.Profile.Deactivate, Constants.Profile.Delete -> {
                        block.isClickable = false
                        block.isEnabled = false
                    }

                    else -> {
                        block.text = getString(R.string.text_block)
                        block.isClickable = true
                        block.isEnabled = true
                        block.setTextColor(Color.WHITE)
                    }
                }
            }
        }
        dialog.show()
    }

    @SuppressLint("SuspiciousIndentation")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (resultCode) {
            Activity.RESULT_OK -> {
                when (requestCode) {
                    Constants.ImagePicker.CAMERA -> {

                        //showUploadDialog("Uploading Image")
                        ///uploadDialog?.setCount("1/1")
                        var exif: ExifInterface? = null
                        try {
                            exif = ExifInterface(photoFile!!.path)
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }


                        val orientation = exif!!.getAttributeInt(
                            ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED
                        )
                        val bmOptions = BitmapFactory.Options()
                        val imageBitmap = BitmapFactory.decodeFile(photoFile!!.path, bmOptions)
                        val rotatedBitmap = rotateBitmap(imageBitmap, orientation)!!
                        //uploadFileBitmap(rotatedBitmap)
                        val uri = photoFile!!.path

                        Log.d("CameraImg", uri.toString())

                        photoFileUri = Uri.fromFile(photoFile)
                        imgAbsolutePath = photoFile!!.absolutePath

                        showSelectedMedia(photoFileUri)
                        //sendImgMsg(photoFileUri)

                    }

                    ConstantsImage.RequestCodes.PICK_PICTURE_FROM_GALLERY -> {
                        imageFromGallery(requestCode, resultCode, data)
                    }

                    Constants.ImagePicker.GALLERY -> {
                        when (data!!.hasExtra("images")) {
                            true -> {
                                images = Gson().fromJson<ArrayList<File>>(
                                    data.getStringExtra("images"),
                                    object : TypeToken<ArrayList<File>>() {}.type
                                )
                                if (images.isNotEmpty()) {
                                    showUploadDialog("Uploading Images")
                                    current = 0
                                    uploadGalleryImage(images, current)
                                } else {
                                    universalToast("No image found")
                                }
                            }

                            false -> {}
                        }
                    }

                    Constants.ImagePicker.Video -> {
                        //showUploadDialog("Uploading Video")
                        //uploadDialog?.setCount("1/1")
                        //uploadVideoFile(data!!.data!!)
                        videoFileUri = data!!.data!!

                        showSelectedMedia(videoFileUri)

                        val projection = arrayOf(MediaStore.Images.Media.DATA)
                        val selectedImageUri: Uri? = data.data

                        try {
                            val cursor: Cursor? = contentResolver.query(
                                selectedImageUri!!, projection, null, null, null
                            )
                            cursor!!.moveToFirst()
                            val columnIndex: Int = cursor.getColumnIndex(projection.get(0))
                            val videoPath: String = cursor.getString(columnIndex)
                            cursor.close()
                            Log.d("Video Path", videoPath)
                            //imgAbsolutePath = videoPath
                        } catch (e: java.lang.Exception) {
                            Log.e("Video Path Error", e.toString())
                        }
                    }

                   /* 300 -> {

                        val uri: Uri = data!!.data!!
                        Log.e("DocumentFile", "URi: $uri")
                        mediaSize = (getFileSize(uri) / 1024)

                        Log.e("DocumentFile", "mediaSize: $mediaSize")
                        Log.e("DocumentFile", "formatSize: ${formatFileSize(getFileSize(uri))}")*//* if (Build.VERSION.SDK_INT == 33) {
                             val path = copyFile(uri, "MeetUpsFellow Files")

                             Log.d("DocumentFilePath$%", path.toString())
                         }*//*

                        val fileSize = mediaSize / 1024

                        Log.d("DocumentFileSiz", fileSize.toString())

                        if (fileSize <= 10) {
                            val selectedImageUri: Uri? = data.data!!

                            mediaSizeStr = formatFileSize(getFileSize(uri))

                            try {
                                Log.e("DocumentFile", "selectedImageUri: $selectedImageUri")
                                *//* val filePath1 =
                                    Commons.getPath(selectedImageUri, this@ChatActivity)!!*//*
                                val filePath1 = selectedImageUri?.let {
                                    readContentFromUri(
                                        this@ChatActivity, it
                                    )
                                }!!

                                Log.d("DocumentFilePath$%", "$filePath1")
                                val ext = filePath1.substring(filePath1.length - 5)
                                val dotIndex: Int = ext.indexOf('.')
                                if (dotIndex != -1) {
                                    fileExt = ext.substring(dotIndex)
                                    // modifiedString now contains the substring after the '.' character
                                }
                                Log.e("DocumentFile", "File Ext: $ext")
                                Log.e("DocumentFile", "File_Ext: $fileExt")
                                Log.e("DocumentFile1", filePath1)

                                *//* if (ext.contains("pdf") || ext.contains("docx") || ext.contains("xlsx") || ext.contains("doc") || ext.contains("xls") || ext.contains("txt")|| ext.contains("ppt")) {

                                     fileUri = uri
                                     fileAbsolutePath = filePath1
                                     photoFileUri = null
                                     videoFileUri = null
                                     audioFileUri = null
                                     showSelectedMedia(fileUri)

                                 } else*//* if (ext.contains("png") || ext.contains("jpg") || ext.contains(
                                        "jpeg"
                                    )
                                ) {

                                    photoFileUri = uri
                                    imgAbsolutePath = filePath1
                                    fileUri = null
                                    videoFileUri = null
                                    audioFileUri = null
                                    showSelectedMedia(photoFileUri)

                                } else if (ext.contains("mp4") || ext.contains("mkv")) {

                                    videoFileUri = uri
                                    imgAbsolutePath = filePath1
                                    fileUri = null
                                    photoFileUri = null
                                    audioFileUri = null
                                    showSelectedMedia(videoFileUri)

                                } else if (ext.contains("mp3") || ext.contains("m4a") || ext.contains(
                                        "3ga"
                                    )
                                ) {

                                    audioFileUri = uri
                                    audioAbsolutePath = filePath1
                                    fileUri = null
                                    photoFileUri = null
                                    videoFileUri = null
                                    showSelectedMedia(audioFileUri)

                                } else {
                                    if (fileExt != null) {
                                        fileUri = uri
                                        fileAbsolutePath = filePath1
                                        photoFileUri = null
                                        videoFileUri = null
                                        audioFileUri = null
                                        showSelectedMedia(fileUri)
                                    } else {
                                        Toast.makeText(
                                            this@ChatActivity,
                                            "File type not supported",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }


                            } catch (e: Exception) {
                                Toast.makeText(
                                    this@ChatActivity,
                                    "Please select file in local device folder",
                                    Toast.LENGTH_SHORT
                                ).show()

                                Log.e("DocumentFile1_Err", e.toString())
                            }

                            val projection1 = arrayOf(DocumentsContract.Document.COLUMN_SUMMARY)

                            try {
                                val cursor: Cursor? = contentResolver.query(
                                    selectedImageUri!!, projection1, null, null, null
                                )
                                cursor!!.moveToFirst()
                                val columnIndex: Int = cursor.getColumnIndex(projection1.get(0))
                                val picturePath: String = cursor.getString(columnIndex)
                                cursor.close()
                                Log.d("DocumentFile2", picturePath)
                                imgAbsolutePath = picturePath
                            } catch (e: java.lang.Exception) {
                                Log.e("DocumentFile2", e.toString())
                            }

                        } else {

                            Toast.makeText(
                                this@ChatActivity,
                                "File Size should not exceed 10 MB",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }*/

                    300 -> {
                        data?.data?.let { uri ->
                            try {
                                // File Details
                                val fileDetails = getFileDetails(this, uri)
                               /// val fileSizeMB = fileDetails.size / (1024 * 1024) // Convert size to MB
                                Log.d("SelectedFile", "Name: ${fileDetails.name}, Size: ${fileDetails.size}, URI: $uri")

                                mediaSize = (getFileSize(uri) / 1024)
                                Log.e("DocumentFile", "mediaSize: $mediaSize")
                                val fileSize = mediaSize / 1024
                                Log.d("DocumentFileSiz", fileSize.toString())
                                if (fileSize <= 10) {
                                    mediaSizeStr = formatFileSize(getFileSize(uri))
                                    val fileExtension = getFileExtensionFromUri(this, uri) ?: "unknown"
                                    Log.d("FileExtension", "File extension: $fileExtension")

                                    val copiedFilePath = copyFileToExternalDir(this, uri, fileDetails.name ?: "unknown_file")
                                    copiedFilePath?.let { path ->
                                        Log.d("CopiedFile", "File copied to: ${path.absolutePath}")
                                       /// openFile(this, path.absolutePath)
                                        fileExt = fileExtension
                                        mediaName = fileDetails.name
                                        fileAbsolutePath = path.absolutePath
                                        fileUri = uri
                                        showSelectedMedia(uri)
                                    } ?: run {
                                        Toast.makeText(this, "Failed to copy file", Toast.LENGTH_SHORT).show()
                                    }
                                } else {
                                    Toast.makeText(this, "File size should not exceed 10 MB", Toast.LENGTH_SHORT).show()
                                }
                            } catch (e: Exception) {
                                Log.e("FileProcessingError", "Error: ${e.message}", e)
                                Toast.makeText(this, "Error processing file", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }

                    Constants.ImagePicker.Image -> {
                        //showUploadDialog("Uploading Image")
                        //uploadDialog?.setCount("1/1")
                        //uploadImage(data!!.data!!)
                        photoFileUri = data!!.data


                        val projection = arrayOf(MediaStore.Images.Media.DATA)
                        val selectedImageUri: Uri? = data.data

                        try {
                            val cursor: Cursor? = contentResolver.query(
                                selectedImageUri!!, projection, null, null, null
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

                        showSelectedMedia(photoFileUri)
                    }


                    Constants.ImagePicker.Private -> {
                        when (data!!.hasExtra("images")) {
                            true -> {
                                val images = Gson().fromJson<ArrayList<String>>(
                                    data.getStringExtra("images"),
                                    object : TypeToken<ArrayList<String>>() {}.type
                                )
                                if (images.isNotEmpty()) {
                                    images.asSequence().forEach {
                                        sendImage(it, it, 0)
                                    }
                                }
                            }

                            false -> {}
                        }
                    }

                    PREVIEW_REQUEST_CODE -> {
                        Log.e("ActivityImgVideo", "FromChat: sendPreview")
                        binding!!.ivSend.callOnClick()
                    }
                }
            }

            Activity.RESULT_CANCELED -> {
                Log.e("ActivityImgVideo", "FromChat: Result_Cancle")
                binding!!.cancelReplyChat.callOnClick()
            }

            AutocompleteActivity.RESULT_ERROR -> {
                val status = Autocomplete.getStatusFromIntent(data!!)
                LogManager.logger.i(ArchitectureApp.instance!!.tag, "${status.statusMessage}")
                universalToast("${status.statusMessage}")
            }

        }
    }

    fun readContentFromUri(context: Context, uri: Uri): String? {
        return try {
            context.contentResolver.openInputStream(uri)?.bufferedReader()?.use { it.readText() }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
   public fun openFile(context: Context, filePath: String) {
        val file = File(filePath)
        val fileUri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
       Log.e("openFile", "openFile_Call")
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(fileUri, context.contentResolver.getType(fileUri))
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        try {
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(context, "No application found to open this file", Toast.LENGTH_SHORT).show()
        }
    }



    private fun formatFileSize(sizeInBytes: Long): String {
        if (sizeInBytes <= 0) return "0 B"

        val units = arrayOf("B", "KB", "MB", "GB", "TB")
        val digitGroups = (Math.log10(sizeInBytes.toDouble()) / Math.log10(1024.0)).toInt()

        return String.format(
            "%.1f %s", sizeInBytes / Math.pow(1024.0, digitGroups.toDouble()), units[digitGroups]
        )
    }

    fun getFileSize(uri: Uri): Long {
        if ("content" == uri.scheme) {
            // If the URI is a content URI
            val cursor = contentResolver.query(uri, null, null, null, null)
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
                    if (sizeIndex != -1) {
                        return cursor.getLong(sizeIndex)
                    }
                }
            } finally {
                cursor?.close()
            }
        } else if ("file" == uri.scheme) {
            // If the URI is a file URI
            val file = File(uri.path)
            if (file.exists()) {
                return file.length()
            }
        }
        // Return 0 if file size cannot be determined
        return 0
    }


    private fun uploadGalleryImage(images: ArrayList<File>, current: Int) {
        if (uploaded.size == images.size) {
            try {
                this.current = -1
                if (uploaded.isNotEmpty()) {
                    uploaded.asSequence().forEach {
                        sendImage(it.thumb, it.original, it.length)
                    }

                    uploaded.clear()
                    this.images.clear()
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
            Handler().post { closeUploadDialog() }
        } else {
            uploadDialog?.setCount("${(current + 1)}/${images.size}")
            var exif: ExifInterface? = null
            try {
                exif = ExifInterface(images[current].path)
            } catch (e: IOException) {
                e.printStackTrace()
            }

            val orientation = exif!!.getAttributeInt(
                ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED
            )

            val bmOptions = BitmapFactory.Options()
            val imageBitmap = BitmapFactory.decodeFile(images[current].path, bmOptions)
            val rotatedBitmap = rotateBitmap(imageBitmap, orientation)!!
            uploadFileBitmap(rotatedBitmap)
        }
    }

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

    private fun imageFromGallery(requestCode: Int, resultCode: Int, data: Intent?) {
        EasyImage.handleActivityResult(requestCode,
            resultCode,
            data,
            this,
            object : DefaultCallback() {
                override fun onImagesPicked(
                    imageFiles: MutableList<File>, source: EasyImage.ImageSource?, type: Int
                ) {

                    for (i in imageFiles.indices) {

                        Log.e("ImagesSelected", imageFiles.get(i).toString())
                    }

                    //showUploadDialog("Uploading Image")
                    var exif: ExifInterface? = null
                    try {
                        exif = ExifInterface(imageFiles[0].path)
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }

                    val orientation = exif!!.getAttributeInt(
                        ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED
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

                    showSelectedMedia(photoFileUri)

                    //uploadFileBitmap(rotatedBitmap)
                }

                override fun onCanceled(source: EasyImage.ImageSource, type: Int) {
                    if (source == EasyImage.ImageSource.CAMERA_IMAGE) {
                        EasyImage.lastlyTakenButCanceledPhoto(this@ChatActivity)?.delete()
                    }
                }

                override fun onImagePickerError(
                    e: Exception, source: EasyImage.ImageSource, type: Int
                ) {
                    e.printStackTrace()
                }
            })
    }

    @SuppressLint("SuspiciousIndentation")
    private fun showSelectedMedia(photoFileUri: Uri?) {
        //  sendingGifLay.visibility = View.GONE
        ///  replyChatLay.visibility = View.VISIBLE
        /// sendingImgLay.visibility = View.VISIBLE
        binding!!.replyChatLay.visibility = View.GONE
        binding!!.sendingImgLay.visibility = View.GONE
        binding!!.replyLay.visibility = View.GONE
        binding!!.etChat.isEnabled = false

        if (isReplying) {
            /// replyLay.visibility = View.VISIBLE
            binding!!.replyLay.visibility = View.GONE
        }

        if (videoFileUri != null) {

            binding!!.videoPlayImg.visibility = View.VISIBLE
            binding!!.audioImg.visibility = View.GONE
            binding!!.fileImg.visibility = View.GONE
            binding!!.fileNameTxt.visibility = View.GONE

            isSendingVideo = true

            /// val path = Path.getPath(this@ChatActivity, videoFileUri!!)
            val path = Path.copyUriToFile(this@ChatActivity, videoFileUri!!)
            Log.i("sss", "From Gallery_videoFileUri -- $videoFileUri")

            videoAbsolutePath = path.toString()
            Log.i("sss", "videoAbsolutePath-- $videoAbsolutePath")

            bitmapVideoThumb = ThumbnailUtils.createVideoThumbnail(
                path.toString(), MediaStore.Video.Thumbnails.MINI_KIND
            )

            binding!!.sendingImg.setImageBitmap(bitmapVideoThumb)

            Log.e("ActivityImgVideo", "FromChat_Video: " + photoFileUri)
            val intent = Intent(this@ChatActivity, ImageVideoPreviewActivity::class.java)
            intent.putExtra(Constants.IntentDataKeys.VideoUrl, photoFileUri.toString())
            startActivityForResult(intent, PREVIEW_REQUEST_CODE)

        } else if (fileUri != null) {

            binding!!.videoPlayImg.visibility = View.GONE
            binding!!.audioImg.visibility = View.GONE
            binding!!.fileNameTxt.visibility = View.VISIBLE
            binding!!.fileImg.visibility = View.VISIBLE

            /*val fileName = fileAbsolutePath.split("/")
            binding!!.fileNameTxt.text = fileName[fileName.lastIndex]
            mediaName = fileName[fileName.lastIndex]*/

            // below code for set file name in chat activity preview old working
          /*  val fileDetails = getFileDetails(this, fileUri!!)
            binding!!.fileNameTxt.text = fileDetails.name
            mediaName = fileDetails.name*/

            // val fileBitmap = generateImageFromPdf(fileUri)
            val fileBitmap = generateImageFromPdf(this@ChatActivity, fileUri!!, 0)

            Log.d("FileBitmap", fileBitmap.toString())

            binding!!.fileImg.setImageBitmap(fileBitmap)

            isSendingFile = true

            Log.e("ActivityImgVideo", "FromChat_File_uri: $fileUri")
            Log.e("ActivityImgVideo", "FromChat_File_path: $fileAbsolutePath")
            Log.e("ActivityImgVideo", "FromChat_File_EXT: $fileExt")
            val intent = Intent(this@ChatActivity, ImageVideoPreviewActivity::class.java)
            intent.putExtra(Constants.IntentDataKeys.FileUri, fileUri.toString())
            intent.putExtra(Constants.IntentDataKeys.FilePath, fileAbsolutePath)
            intent.putExtra(Constants.IntentDataKeys.FileExt, fileExt)
            startActivityForResult(intent, PREVIEW_REQUEST_CODE)

        } else if (audioFileUri != null) {

            binding!!.fileImg.visibility = View.GONE
            binding!!.videoPlayImg.visibility = View.GONE
            binding!!.audioImg.visibility = View.VISIBLE
            binding!!.fileNameTxt.visibility = View.VISIBLE

            val fileName = audioAbsolutePath.split("/")

            binding!!.fileNameTxt.text = fileName[fileName.lastIndex]

        } else {
            binding!!.audioImg.visibility = View.GONE
            binding!!.fileImg.visibility = View.GONE
            binding!!.videoPlayImg.visibility = View.GONE
            binding!!.fileNameTxt.visibility = View.GONE
            isSendingImg = true
            binding!!.sendingImg.setImageURI(photoFileUri)

            Log.e("ActivityImgVideo", "FromChat: " + photoFileUri)
            val intent = Intent(this@ChatActivity, ImageVideoPreviewActivity::class.java)
            intent.putExtra(Constants.IntentDataKeys.ImgPath, photoFileUri.toString())
            // startActivity(intent)
            startActivityForResult(intent, PREVIEW_REQUEST_CODE)
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
                        //startActivityForResult(intent, Constants.ImagePicker.Image)
                        startActivityForResult(
                            intent, ConstantsImage.RequestCodes.PICK_PICTURE_FROM_GALLERY
                        )

                    }

                    false -> {

                        val intent = Intent(Intent.ACTION_GET_CONTENT)
                        intent.addCategory(Intent.CATEGORY_OPENABLE)

                        val filePath = Uri.parse(Environment.DIRECTORY_PICTURES)
                        intent.setDataAndType(filePath, "image/*")
                        //  intent.type =
                        //startActivityForResult(intent, Constants.ImagePicker.Image)
                        startActivityForResult(
                            intent, ConstantsImage.RequestCodes.PICK_PICTURE_FROM_GALLERY
                        )
                    }
                }
            }

            false -> {

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

    private fun capturePhoto() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            var photoURI: Uri? = null
            try {
                photoFile = createImageFileWith()
                photoURI = FileProvider.getUriForFile(
                    this@ChatActivity,
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
            startActivityForResult(takePictureIntent, Constants.ImagePicker.CAMERA)
        }else {
            Toast.makeText(this, "No camera app available", Toast.LENGTH_SHORT).show()
        }
    }

    @Suppress("DEPRECATION")
    @Throws(IOException::class)
    private fun createImageFileWith(): File {
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())


        val imageFileName = "JPEG_$timestamp"
        val storageDir = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "pics"
        )
        storageDir.mkdirs()
        return File.createTempFile(imageFileName, ".jpg", storageDir)
    }

    private fun chatId(size: Int): String {
        val token = ByteArray(size)
        SecureRandom().nextBytes(token)
        return BigInteger(1, token).toString(size)
    }

    @Suppress("NAME_SHADOWING")
    private fun uploadFileBitmap(imageBitmap: Bitmap) {
        var imageBitmap = imageBitmap

        val filePathOriginal = "Chat/${conversation!!.convoId}/Image/original/"
        val filePathThumb = "Chat/${conversation!!.convoId}/Image/thumb/"



        try {

            if (null == uploadMediaObj) uploadMediaObj =
                UploadChatMediaOnFireabase(this@ChatActivity)

            val currentMiliseconds = System.currentTimeMillis()
            val thumbBitmap: Bitmap?

            val width = resources.getDimensionPixelOffset(android.R.dimen.thumbnail_width)
            val height = resources.getDimensionPixelOffset(android.R.dimen.thumbnail_height)

            if (imageBitmap.width > width && imageBitmap.height > height) {

                thumbBitmap = ThumbnailUtils.extractThumbnail(imageBitmap, width, height)

                imageBitmap = compressImage(imageBitmap)

                uploadMediaObj!!.uploadThumbImage(
                    filePathThumb + currentMiliseconds, thumbBitmap, imageBitmap, currentMiliseconds
                )
                uploadMediaObj!!.uploadOriginalImage(
                    filePathOriginal + currentMiliseconds,
                    thumbBitmap,
                    imageBitmap,
                    uploadDialog!!,
                    currentMiliseconds
                )

            } else {
                uploadMediaObj!!.uploadThumbImage(
                    filePathThumb + currentMiliseconds, imageBitmap, imageBitmap, currentMiliseconds
                )
                uploadMediaObj!!.uploadOriginalImage(
                    filePathOriginal + currentMiliseconds,
                    imageBitmap,
                    imageBitmap,
                    uploadDialog!!,
                    currentMiliseconds
                )
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }


    @Suppress("NAME_SHADOWING")
    private fun compressImage(imageBitmap: Bitmap): Bitmap {
        var imageBitmap = imageBitmap
        Log.e("sss", "compressImage ENTERED 1 ")
        val bos = ByteArrayOutputStream()
        if (imageBitmap.byteCount > ONE_MB * 2) {
            Log.e("sss", "compressImage ENTERED 2 ")
            imageBitmap =
                if (imageBitmap.width > 1700 && imageBitmap.height > 1700) Bitmap.createScaledBitmap(
                    imageBitmap, imageBitmap.width / 3, imageBitmap.height / 3, true
                )
                else Bitmap.createScaledBitmap(
                    imageBitmap, imageBitmap.width / 2, imageBitmap.height / 2, true
                )
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 75, bos)
        }

        if (imageBitmap.byteCount > ONE_MB && imageBitmap.byteCount < ONE_MB * 1.5) {
            Log.e("sss", "compressImage ENTERED 3 ")
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 75, bos)
        }

        Log.e("sss", "compressImage ENTERED 4 ")
        return imageBitmap
    }

    @Throws(IOException::class)
    private fun sendImage(thumbUrl: String, originalUrl: String, fileLength: Long) {
        val time = System.currentTimeMillis()
        val id = sharedPreferencesUtil.userId()
        val name = sharedPreferencesUtil.fetchUserProfile().name
        val conversationConstantId = chatId(10)
        val conversationModel = ConversationModel(
            "Image",
            id,
            conversationConstantId,
            conversation!!.convoId,
            chatOpen = false,
            meDeletedConvo = false,
            otherUserOnline = false,
            userTyping = false,
            blockedBY = "",
            lastMessagetime = time,
            otherUserId = conversation!!.otherUserId,
            otherUserName = conversation!!.otherUserName
        )

        val otherConversationModel = ConversationModel(
            "Image",
            id,
            conversationConstantId,
            conversation!!.convoId,
            false,
            meDeletedConvo = false,
            otherUserOnline = false,
            userTyping = false,
            blockedBY = "",
            lastMessagetime = time,
            otherUserId = id,
            otherUserName = name,
            unReadCount = messageUnreadCount()
        )

        val chatModel = ChatModel(
            chatId(8),
            "Image",
            id,
            name,
            Constants.MessageStatus.Sent,
            time,
            "$fileLength",
            Constants.MediaType.Image,
            originalUrl,
            thumbUrl
        )

        val send = send(conversationModel, otherConversationModel, chatModel)
        if (!send) {
            return
        }

        when (activeConvoId != conversation!!.convoId) {
            true -> {
                if (isOtherUserPushEnabled) {
                    val dataModel = Data().apply {
                        senderImageUrl = sharedPreferencesUtil.fetchUserProfileImage()
                        receiverId = conversation!!.otherUserId
                        type = Constants.Notification.Chat
                        userSide = conversationModel
                        otherSide = otherConversationModel
                        chatInfo = chatModel
                    }

                    val notificationMo = Notification().apply {
                        body = String.format(getString(R.string.text_shared_photo), name)
                        title = ArchitectureApp.instance!!.getString(R.string.app_name)
                    }

                    val notificationMod = NotificationModel().apply {
                        data = dataModel
                        notification = notificationMo
                        to =
                            sharedPreferencesUtil.fetchOtherUserProfile(conversation!!.otherUserId).deviceToken
                    }

                    val requestSendNotification = RequestSendNotification().apply {
                        notificationModel = notificationMod
                        senderImageUrl = dataModel.senderImageUrl
                        receiverId = dataModel.receiverId
                        senderId = conversationModel.chatSenderId
                        otherUserId = conversationModel.otherUserId
                        otherUserName = conversationModel.otherUserName
                    }

                    if (null == mPresenter) mPresenter = ChatPresenter(presenter)

                    run {
                        mPresenter!!.addSendNotificationObject(requestSendNotification)
                        mPresenter!!.callRetrofit(ConstantsApi.SEND_NOTIFICATION)
                    }
                }
            }

            false -> {}
        }

        Handler().postDelayed({
            binding!!.etChat.text.clear()
            binding!!.etChat.isEnabled = true
        }, 100)

    }

    private fun messageUnreadCount(): String {
        if (otherUserConversation == null) return "1"
        LogManager.logger.i(
            ArchitectureApp.instance!!.tag,
            "Message count is -Before- : ${otherUserConversation!!.unReadCount}"
        )
        return when (otherUserConversation!!.unReadCount.trim().isEmpty()) {
            true -> {
                otherUserConversation!!.unReadCount = "1"
                otherUserConversation!!.unReadCount
            }

            false -> when (otherUserConversation!!.unReadCount.toInt() > 0) {
                true -> {
                    otherUserConversation!!.unReadCount =
                        "${otherUserConversation!!.unReadCount.toInt() + 1}"
                    otherUserConversation!!.unReadCount
                }

                false -> {
                    otherUserConversation!!.unReadCount = "1"
                    otherUserConversation!!.unReadCount
                }
            }
        }
    }

    @Throws(IOException::class)
    private fun sendVideo(thumbUrl: String, originalUrl: String, fileLength: Long) {

        val time = System.currentTimeMillis()
        val id = sharedPreferencesUtil.userId()
        val name = sharedPreferencesUtil.fetchUserProfile().name
        val conversationConstantId = chatId(10)
        val conversationModel = ConversationModel(
            "Video",
            id,
            conversationConstantId,
            conversation!!.convoId,
            false,
            meDeletedConvo = false,
            otherUserOnline = false,
            userTyping = false,
            blockedBY = "",
            lastMessagetime = time,
            otherUserId = conversation!!.otherUserId,
            otherUserName = conversation!!.otherUserName,
            unReadCount = "0"
        )

        val otherConversationModel = ConversationModel(
            "Video",
            id,
            conversationConstantId,
            conversation!!.convoId,
            false,
            meDeletedConvo = false,
            otherUserOnline = false,
            userTyping = false,
            blockedBY = "",
            lastMessagetime = time,
            otherUserId = id,
            otherUserName = name,
            unReadCount = messageUnreadCount()
        )

        val chatModel = ChatModel(
            chatId(8),
            "Video",
            id,
            name,
            Constants.MessageStatus.Sent,
            time,
            "$fileLength",
            Constants.MediaType.Video,
            originalUrl,
            thumbUrl
        )

        val send = send(conversationModel, otherConversationModel, chatModel)
        if (!send) {
            return
        }

        when (activeConvoId != conversation!!.convoId) {
            true -> {

                if (isOtherUserPushEnabled) {
                    val dataModel = Data().apply {
                        senderImageUrl = sharedPreferencesUtil.fetchUserProfileImage()
                        receiverId = conversation!!.otherUserId
                        type = Constants.Notification.Chat
                        userSide = conversationModel
                        otherSide = otherConversationModel
                        chatInfo = chatModel
                    }

                    val notificationMo = Notification().apply {
                        body = String.format(getString(R.string.text_shared_video), name)
                        title = ArchitectureApp.instance!!.getString(R.string.app_name)
                    }

                    val notificationMod = NotificationModel().apply {
                        data = dataModel
                        notification = notificationMo
                        to =
                            sharedPreferencesUtil.fetchOtherUserProfile(conversation!!.otherUserId).deviceToken
                    }

                    val requestSendNotification = RequestSendNotification().apply {
                        notificationModel = notificationMod
                        senderImageUrl = dataModel.senderImageUrl
                        receiverId = dataModel.receiverId
                        senderId = conversationModel.chatSenderId
                        otherUserId = conversationModel.otherUserId
                        otherUserName = conversationModel.otherUserName
                    }

                    if (null == mPresenter) mPresenter = ChatPresenter(presenter)

                    run {
                        mPresenter!!.addSendNotificationObject(requestSendNotification)
                        mPresenter!!.callRetrofit(ConstantsApi.SEND_NOTIFICATION)
                    }
                }

            }

            false -> {}
        }

        Handler().postDelayed({
            binding!!.etChat.text.clear()
            binding!!.etChat.isEnabled = true
        }, 100)

    }

    private fun send(
        conversationModel: ConversationModel,
        otherConversationModel: ConversationModel,
        chatModel: ChatModel
    ): Boolean {

        val convo = sharedPreferencesUtil.fetchConversation()
        val index =
            convo.firstOrNull { conversation!!.convoId == it.convoId }?.let { convo.indexOf(it) }
                ?: -1

        if (index > -1) {
            conversation = convo[index]
        }

        return when (conversation!!.blockedBY.trim().isNotEmpty()) {
            true -> {
                binding!!.rlBlocked.visibility = View.VISIBLE
                binding!!.rlFooter.visibility = View.GONE
                when (conversation!!.blockedBY.trim()) {
                    sharedPreferencesUtil.userId() -> {
                        binding!!.tvBlock.text = String.format(
                            getString(R.string.text_you_block), conversation!!.otherUserName
                        )
                        false
                    }

                    else -> {
                        binding!!.tvBlock.text = String.format(
                            getString(R.string.text_blocked_you), conversation!!.otherUserName
                        )
                        false
                    }
                }
            }

            false -> {
                when (status) {
                    Constants.Profile.Deactivate -> {
                        binding!!.rlBlocked.visibility = View.VISIBLE
                        binding!!.rlFooter.visibility = View.GONE
                        binding!!.tvBlock.text = String.format(
                            getString(R.string.text_deactivated), conversation!!.otherUserName
                        )
                        false
                    }

                    Constants.Profile.Delete -> {
                        binding!!.rlBlocked.visibility = View.VISIBLE
                        binding!!.rlFooter.visibility = View.GONE
                        binding!!.tvBlock.text = String.format(
                            getString(R.string.text_deleted), conversation!!.otherUserName
                        )
                        false
                    }

                    else -> when {
                        conversationModel.convoId.isNotEmpty() && conversationModel.otherUserId.isNotEmpty() && otherConversationModel.convoId.isNotEmpty() && otherConversationModel.otherUserId.isNotEmpty() -> {
                            firebaseUtil.createConversationAndChat(
                                conversationModel, otherConversationModel, chatModel
                            )
                            true
                        }

                        else -> {
                            universalToast("Unable to send message")
                            false
                        }
                    }
                }
            }
        }
    }

    private fun isTyping(typing: Boolean) {
        when (conversation!!.otherUserId.isNotEmpty() && conversation!!.convoId.isNotEmpty()) {
            true -> {
                firebaseUtil.isTyping("${Constants.Firebase.Conversation}/${conversation!!.otherUserId}/${conversation!!.convoId}/userTyping",
                    conversation!!.apply { userTyping = typing })
            }

            false -> {}
        }
    }

    private fun userBlocked() {
        when (conversation!!.blockedBY.trim().isNotEmpty()) {
            true -> {
                binding!!.rlBlocked.visibility = View.VISIBLE
                binding!!.rlFooter.visibility = View.GONE
                when (conversation!!.blockedBY.trim()) {
                    sharedPreferencesUtil.userId() -> {
                        binding!!.tvBlock.text = String.format(
                            getString(R.string.text_you_block), conversation!!.otherUserName
                        )
                    }

                    else -> {
                        binding!!.tvBlock.text = String.format(
                            getString(R.string.text_blocked_you), conversation!!.otherUserName
                        )
                    }
                }
            }

            false -> {

                when (status) {
                    Constants.Profile.Deactivate -> {
                        binding!!.rlBlocked.visibility = View.VISIBLE
                        binding!!.rlFooter.visibility = View.GONE
                        binding!!.tvBlock.text = String.format(
                            getString(R.string.text_deactivated), conversation!!.otherUserName
                        )
                    }

                    Constants.Profile.Delete -> {
                        binding!!.rlBlocked.visibility = View.VISIBLE
                        binding!!.rlFooter.visibility = View.GONE
                        binding!!.tvBlock.text = String.format(
                            getString(R.string.text_deleted), conversation!!.otherUserName
                        )
                    }

                    else -> {
                        binding!!.rlBlocked.visibility = View.GONE
                        binding!!.rlFooter.visibility = View.VISIBLE
                        binding!!.header.tvUserStatus.text = when {
                            conversation!!.userTyping -> "typing..."
                            sharedPreferencesUtil.fetchOtherUserProfile(
                                conversation!!.otherUserId
                            ).onlineStatus -> ""

                            else -> ""
                        }
                    }
                }
            }
        }
    }

    private fun showUploadDialog(message: String) {
        closeUploadDialog()
        if (null == uploadDialog) {
            uploadDialog = UploadDialogCustom(this@ChatActivity, message)
            uploadDialog!!.show()
        }
    }

    interface Typing {
        fun isTyping(conversationModel: ConversationModel)
    }

    private fun closeUploadDialog() {
        if (uploadDialog != null) {
            if (uploadDialog!!.isShowing) {
                uploadDialog!!.dismiss()
            }
            uploadDialog = null
        }
    }

    private fun blockUser() {
        if (null == mPresenter) mPresenter = ChatPresenter(presenter)

        run {
            mPresenter!!.addBlockUserObject(RequestUserBlock().apply {
                blockUserid = conversation!!.otherUserId
                status = "1"
            })
            mPresenter!!.callRetrofit(ConstantsApi.BLOCK)
        }
    }

    private fun sendConnectionReq() {
        if (null == mPresenter) mPresenter = ChatPresenter(presenter)

        run {
            mPresenter!!.addSendConnectionObject(SendConnectionRequest().apply {
                receiver_id = 19103
                is_direct_message = 1
                direct_message = "i sent direct message request"
            })
            Log.e("Call_Api", "sendConnection_Data")
            mPresenter!!.callRetrofit(ConstantsApi.SEND_CONNECTION_REQUEST)
        }
    }

    private fun showAlertClearChat(message: String, type: String) {
        val alertDialog = AlertDialog.Builder(this@ChatActivity, R.style.MyDialogTheme2)
        alertDialog.setMessage(message)
        alertDialog.setPositiveButton(
            if (type == "Chat") getString(R.string.text_clear) else getString(
                R.string.text_block
            )
        ) { dialog, _ ->
            dialog.dismiss()
            when (type) {
                "Chat" -> {
                    sharedPreferencesUtil.remove(conversation!!.convoId)
                    sharedPreferencesUtil.remove("${conversation!!.convoId}_keys")
                    chatAdapter!!.removeAll()
                    clearChat()

                    updateChatUi()
                }

                else -> {
                    showProgressView(progressBar)
                    firebaseUtil.updateUserBlocked(
                        sharedPreferencesUtil.userId(),
                        conversation!!.otherUserId,
                        sharedPreferencesUtil.userId()
                    )
                    Handler().postDelayed({
                        blockUser()
                    }, 500)
                }
            }
        }
        alertDialog.setNegativeButton(getString(R.string.label_cancel_text)) { dialog, _ ->
            dialog.cancel()
        }
        alertDialog.setCancelable(false)
        // Showing Alert Message
        alertDialog.show()
    }

    private fun clearChat() {
        if (null == mPresenter) mPresenter = ChatPresenter(presenter)

        run {
            mPresenter!!.addClearChatObject(RequestClearChat().apply {
                conversationId = conversation!!.convoId
                clearChatTime = "${System.currentTimeMillis()}"
            })
            mPresenter!!.callRetrofit(ConstantsApi.CLEAR_CHAT)
        }
    }


    private fun scrollToBottom() {
        binding!!.rvChat.scrollToPosition(chatAdapter!!.itemCount - 1)
    }


    companion object {
        internal var chatExist = false
        internal var running = false
    }

}


