package com.connect.meetupsfellow.mvp.view.fragment

import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.util.Size
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.RelativeLayout
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import com.connect.meetupsfellow.R
import com.connect.meetupsfellow.application.ArchitectureApp
import com.connect.meetupsfellow.components.fragment.CustomFragment
import com.connect.meetupsfellow.constants.Constants
import com.connect.meetupsfellow.constants.ConstantsApi
import com.connect.meetupsfellow.constants.FirebaseChat
import com.connect.meetupsfellow.databinding.FragmentPersonalChatBinding
import com.connect.meetupsfellow.databinding.LayoutChatBinding
import com.connect.meetupsfellow.developement.interfaces.FirebaseUtil
import com.connect.meetupsfellow.developement.interfaces.SharedPreferencesUtil
import com.connect.meetupsfellow.global.firebase.FirebaseChatListener
import com.connect.meetupsfellow.global.firebase.FirebaseListener
import com.connect.meetupsfellow.log.LogManager
import com.connect.meetupsfellow.mvp.connector.activity.HomeConnector
import com.connect.meetupsfellow.mvp.presenter.activity.HomePresenter
import com.connect.meetupsfellow.mvp.view.activities.HomeActivity
import com.connect.meetupsfellow.mvp.view.adapter.ConversationAdapter
import com.connect.meetupsfellow.mvp.view.adapter.RecyclePersonalConvoAdapter
import com.connect.meetupsfellow.mvp.view.model.ChatModel
import com.connect.meetupsfellow.mvp.view.model.ConversationModel
import com.connect.meetupsfellow.mvp.view.model.RecycleModel
import com.connect.meetupsfellow.retrofit.response.CommonResponse
import com.connect.meetupsfellow.retrofit.response.ResponseUserData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson
import org.json.JSONObject
import javax.inject.Inject

class PersonalChatFragment : CustomFragment() {

    private val presenter = object : HomeConnector.RequiredViewOps {
        override fun showToast(error: String, logout: Boolean?) {
            when (logout) {
                true -> {
                    // logout()
                }

                false -> {}
                null -> {}
            }

            if (error.equals("Trying to get property 'conversationId' of non-object")) {
                //universalToast("")
            } else {
                universalToast(error)
            }

            //hideProgressView(rl_progress)
            //swipeRefresh.isRefreshing = false
        }

        override fun showResponse(response: CommonResponse, type: ConstantsApi) {
            when (type) {

                ConstantsApi.ADMIN_CONVERSATION -> {
                    Log.e("ChatFragment", "List_from_API: ${response.adminChat}")
                    Log.e("ChatFragment", "List_from_API: ${response.adminChat.convoId}")
                    conversationAdapter.addAdminConversation(response.adminChat)
                }

                else -> {}

            }
        }
    }

    private val conversationAdapter = ConversationAdapter()
    lateinit var personalChatRv: RecyclerView
    lateinit var noChatLay: RelativeLayout
    lateinit var swipePersonalChat: SwipeRefreshLayout
    private var messageUnreadCount = arrayListOf<String>()
    private var conversation: ConversationModel? = null
    private var otherUserConversation: ConversationModel? = null
    private var mPresenter: HomeConnector.PresenterOps? = null
    lateinit var recyclerModal: ArrayList<RecycleModel>
    lateinit var recycleChatConvoAdapter: RecyclePersonalConvoAdapter
    var unreadCount = 0
    var unreadCountT = 0
    lateinit var profile: ResponseUserData
    lateinit var chatDb: DatabaseReference
    lateinit var rl_progress: LinearLayout
    lateinit var animLogo: ImageView
    lateinit var popupWindow: PopupWindow
    lateinit var homeActivity: HomeActivity
    private var _binding: FragmentPersonalChatBinding? = null
    private val binding get() = _binding!!
    private lateinit var layoutChatBinding: LayoutChatBinding

    @Inject
    lateinit var sharedPreferencesUtil: SharedPreferencesUtil

    @Inject
    lateinit var firebaseUtil: FirebaseUtil

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        ArchitectureApp.component!!.inject(this@PersonalChatFragment)
        // val view = inflater.inflate(R.layout.fragment_personal_chat, container, false)
        _binding = FragmentPersonalChatBinding.inflate(inflater, container, false)
        return binding.root
        // return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        homeActivity = activity as HomeActivity
        layoutChatBinding = homeActivity.binding!!.includedLayHome.includedLChat
        personalChatRv = view.findViewById(R.id.personalChatRv)
        noChatLay = view.findViewById(R.id.noChatLay)
        swipePersonalChat = view.findViewById(R.id.swipePersonalChat)
        rl_progress = view.findViewById(R.id.rl_progress)
        animLogo = view.findViewById(R.id.animLogo)

        startLoadingAnim()
        //fetchConversations()
        //showProgressView(rl_progress)

        //showLocalConversation()
        //adminConversation()
        //initiateConversationUI()
        //initiateConversationUI()
        profile = sharedPreferencesUtil.fetchUserProfile()
        chatDb =
            FirebaseDatabase.getInstance(Constants.Firebase.databaseUrl).getReference(Constants.Firebase.ChatDB)
                .child("ChatList")


        swipePersonalChat.setOnRefreshListener(object : OnRefreshListener {
            override fun onRefresh() {
                setUpChatConvo()
                //initiateConversationUI()
                swipePersonalChat.isRefreshing = false
            }
        })

        with(personalChatRv) {
            layoutManager = LinearLayoutManager(
                personalChatRv.context, RecyclerView.VERTICAL, false
            )
            addItemDecoration(
                DividerItemDecoration(
                    personalChatRv.context, DividerItemDecoration.VERTICAL
                )
            )
        }

        //getUnreadCount()

        setUpChatConvo()


    }

    private fun startLoadingAnim() {

        val animation = android.view.animation.AnimationUtils.loadAnimation(
            requireContext().applicationContext, R.anim.blink_anim
        )

        animLogo.startAnimation(animation)
    }

    private fun getUnreadCount() {

        chatDb.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snap: DataSnapshot) {

                if (snap.exists()) {

                    recyclerModal.clear()

                    for (chats in snap.children) {

                        if (chats.key.toString().contains(profile.userId)) {
                            unreadCount = 0
                            for (i in chats.child("chats").children) {

                                if (i.child("receiverId").value.toString() == profile.userId && i.child(
                                        "isRead"
                                    ).value.toString() == "false"
                                ) {

                                    unreadCount++
                                }
                            }
                        }
                    }

                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }


        })


    }

    private fun setUpChatConvo() {
        swipePersonalChat.isRefreshing = true
        recyclerModal = ArrayList()
        recyclerModal.clear()

        chatDb.child(profile.userId).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snap: DataSnapshot) {

                if (snap.exists()) {
                    //showPopupWindow(personalChatRv)
                    //showPopupWindow(personalChatRv)
                    recyclerModal.clear()

                    for (list in snap.children) {

                        val chatRoomId = list.child("convoId").value.toString()
                        val lastMsg = list.child("messageText").value.toString()
                        val lastMsgTime = list.child("messageTime").value.toString()
                        val otherUserImg = list.child("OtherUserImage").value.toString()
                        val senderId = list.child("senderId").value.toString()
                        val receiverId = list.child("receiverId").value.toString()
                        /// val deliveryStatus = list.child("messageStatus").value.toString()
                        val deliveryStatus = list.child("deliveryStatus").value.toString()
                        val IsUserConnected = list.child("isUserConnected").value.toString()
                        //val receiverName = list.child("receiverName").value.toString()
                        val otherUserName = list.child("otherUserName").value.toString()

                        var lastMsgSender = ""
                        var otherUserId = ""

                        if (senderId == profile.userId) {

                            otherUserId = receiverId
                            lastMsgSender = profile.name

                        } else {

                            //otherUserName = senderName
                            otherUserId = senderId
                            lastMsgSender = otherUserName

                        }

                        if (chatRoomId != "null" && lastMsg != "null" && lastMsgTime != "null" && otherUserName != "null") {

                            unreadCount = list.child("unReadCount").value.toString().toInt()
                            Log.d("recycleSize", recyclerModal.size.toString())
                            Log.e("ChatUnreadCount", unreadCount.toString())
                            Log.e("PermissionStore: ", "Fragment: " + IsUserConnected)

                            recyclerModal.add(
                                RecycleModel(
                                    otherUserImg,
                                    otherUserName,
                                    lastMsg,
                                    unreadCount.toString(),
                                    lastMsgTime,
                                    chatRoomId,
                                    lastMsgSender,
                                    otherUserId,
                                    deliveryStatus,
                                    IsUserConnected
                                )
                            )
                        }

                        unreadCountT += unreadCount
                        unreadCount = 0

                    }


                    /*for (chats in snap.children) {

                        if (chats.key.toString().contains(profile.userId)) {

                            unreadCount = 0
                            for (i in chats.child("chats").children) {

                                if (i.child("receiverId").value.toString() == profile.userId && i.child(
                                        "isRead"
                                    ).value.toString() == "false"
                                ) {

                                    unreadCount++
                                }
                            }

                            val chatRoomId = chats.key.toString()
                            var lastMsg = chats.child("lastMsg").value.toString()

                            val myTopChatsQuery: Query = chatDb
                                .child(chatRoomId)
                                .child("chats")
                                //.orderByChild("chats")
                                .limitToLast(1)
                            myTopChatsQuery.addListenerForSingleValueEvent(object :
                                ValueEventListener {
                                override fun onDataChange(snap1: DataSnapshot) {

                                    if (snap1.exists()) {

                                        for (id in snap1.children) {

                                            Log.d("MsgDeletedU", profile.userId)

                                            Log.d(
                                                "MsgDeleted",
                                                id.child("msgClearedBy")
                                                    .child(profile.userId).value.toString()
                                            )

                                            if (id.child("msgClearedBy")
                                                    .child(profile.userId).value.toString() == "yes"
                                            ) {

                                                Log.d(
                                                    "MsgDeleted1",
                                                    id.child("msgClearedBy")
                                                        .child(profile.userId).value.toString()
                                                )

                                                lastMsg = "message deleted"
                                            }
                                        }
                                    }
                                }

                                override fun onCancelled(p0: DatabaseError) {

                                }


                            })

                            val lastMsgTime = chats.child("msgTime").value.toString()
                            var otherUserName = ""
                            var otherUserImg = ""
                            var otherUserId = ""

                            for (users in chats.child("users").children) {

                                if (users.key.toString() != profile.userId) {

                                    otherUserName = users.child("userName").value.toString()
                                    otherUserImg = users.child("userImg").value.toString()
                                    otherUserId = users.key.toString()

                                    if (otherUserImg.isEmpty() || otherUserImg.equals("null")) {

                                        Log.d("OtherID", otherUserId)

                                        FirebaseDatabase.getInstance(Constants.Firebase.databaseUrl)
                                            .getReference("Live")
                                            .child("UserProfile").child(otherUserId)
                                            .addListenerForSingleValueEvent(object :
                                                ValueEventListener {
                                                override fun onDataChange(snap: DataSnapshot) {
                                                    if (snap.exists()) {

                                                        otherUserImg =
                                                            snap.child("imgUrl").value.toString()

                                                        Log.d("OtherUserImgs", otherUserImg)
                                                    }
                                                }

                                                override fun onCancelled(p0: DatabaseError) {

                                                    Log.d("OtherError", p0.toString())
                                                }


                                            })


                                    }

                                }
                            }

                            var lastMsgSender = chats.child("senderName").value.toString()

                            lastMsgSender = if (lastMsgSender == profile.name) {

                                "You : "
                            } else {

                                lastMsgSender + " : "
                            }

                            if (chatRoomId != "null" && lastMsg != "null" && lastMsgTime != "null" && otherUserName != "null" && otherUserImg != "null") {

                                Log.d("recycleSize", recyclerModal.size.toString())
                                Log.d("ChatUnreadCount", unreadCount.toString())

                                recyclerModal.add(
                                    RecycleModel(
                                        otherUserImg,
                                        otherUserName,
                                        lastMsg,
                                        unreadCount.toString(),
                                        lastMsgTime,
                                        chatRoomId,
                                        lastMsgSender,
                                        otherUserId
                                    )
                                )
                            }

                            unreadCountT += unreadCount
                            unreadCount = 0

                        } else {


                        }
                    }*/

                    ///  Collections.sort(recyclerModal)

                    // Sort the recyclerModal by lastMsgTime (ascending or descending as needed)
                    recyclerModal.sortByDescending { it.lastMsgTime } // This will sort the list by time in descending order


                    //recyclerModal.sortedWith(Comparator.comparing(RecycleModel::getUnread))

                    recycleChatConvoAdapter = RecyclePersonalConvoAdapter(
                        activity, recyclerModal
                    )

                    personalChatRv.adapter = recycleChatConvoAdapter

                    recycleChatConvoAdapter.notifyDataSetChanged()
                    updateConversation()

                    hideProgressView(rl_progress)

                    if (unreadCountT == 0) {

                        layoutChatBinding.tvUnReadCountP.visibility = View.GONE

                    } else {

                        layoutChatBinding.tvUnReadCountP.visibility = View.GONE
                        ///  layoutChatBinding.tvUnReadCountP.visibility = View.VISIBLE
                        layoutChatBinding.tvUnReadCountP.text = unreadCountT.toString()
                    }

                    Constants.unreadCountP += unreadCountT

                    if (Constants.unreadCountP == 0 && Constants.unreadCountG == 0) {

                        homeActivity.binding!!.includedLayHome.tvUnReadCountH.visibility = View.GONE

                    } else {

                        homeActivity.binding!!.includedLayHome.tvUnReadCountH.visibility =
                            View.VISIBLE
                        homeActivity.binding!!.includedLayHome.tvUnReadCountH.text =
                            (Constants.unreadCountP + Constants.unreadCountG).toString()
                    }

                    Constants.unreadCountP = 0
                    unreadCountT = 0
                    swipePersonalChat.isRefreshing = false/*Handler().postDelayed(object : Runnable {

                    override fun run() {
                        popupWindow.dismiss()
                    }
                },500)*/

                } else {
                    Log.e("PersonalFragment", "NoChat_VISIBle")
                    personalChatRv.visibility = View.GONE
                    noChatLay.visibility = View.VISIBLE
                    hideProgressView(rl_progress)
                    //popupWindow.dismiss()
                    swipePersonalChat.isRefreshing = false
                }

            }

            override fun onCancelled(p0: DatabaseError) {
                swipePersonalChat.isRefreshing = false
            }


        })
    }

    private fun showPopupWindow(anchor: View) {
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
            popupWindow.showAtLocation(
                anchor, Gravity.TOP or Gravity.CENTER, 0, location[1] - size.height
            )

            //popupWindow.showAsDropDown(anchor, 0, 0)
            this.popupWindow = popupWindow
            //setUpChatConvo()
            //popupWindow.showAtLocation(anchor, Gravity.BOTTOM, 0, anchor.getHeight())
        }


    }

    private fun initiateConversationUI() {

        showLocalConversation()
        fetchConversations()
        updateConversation()

        with(personalChatRv) {
            layoutManager = LinearLayoutManager(
                personalChatRv.context, RecyclerView.VERTICAL, false
            )
            adapter = conversationAdapter
            addItemDecoration(
                DividerItemDecoration(
                    personalChatRv.context, DividerItemDecoration.VERTICAL
                )
            )
        }

        Log.d("RvChatCount", personalChatRv.adapter!!.itemCount.toString())

    }

    private fun adminConversation() {
        if (null == mPresenter) mPresenter = HomePresenter(presenter)

        run {
            mPresenter!!.callRetrofit(ConstantsApi.ADMIN_CONVERSATION)
        }
    }

    private fun fetchConversations() {
        val userId = sharedPreferencesUtil.userId()
        LogManager.logger.i(ArchitectureApp.instance!!.tag, "User id : $userId")
        when (userId.isEmpty()) {
            true -> {
                //showAlertLogout("Your session has been expired. Please login again.")
                return
            }

            false -> {}
        }
        firebaseUtil.fetchConversation(userId, object : FirebaseChatListener {
            override fun onChatEventListener(snapshot: DataSnapshot, chat: FirebaseChat) {
                LogManager.logger.i(
                    ArchitectureApp.instance!!.tag,
                    "Type is : $chat ${snapshot.value is JSONObject}, conversationModel is ${snapshot.value}"
                )

                when (chat) {
                    FirebaseChat.Changed -> {
                        try {
                            val conversationModel =
                                snapshot.getValue(ConversationModel::class.java)!!
                            LogManager.logger.i(
                                ArchitectureApp.instance!!.tag,
                                "Profile is Changed by other, ${conversationModel.blockedBY}"
                            )
                            when (conversationModel.meDeletedConvo) {
                                false -> {
                                    updateConvo(conversationModel)
                                    addRemoveUnreadCount(conversationModel)
                                }

                                true -> {}
                            }
                        } catch (e: TypeCastException) {
                            e.printStackTrace()
                        }
                    }

                    else -> {

                    }
                }
            }
        })
    }

    private fun addRemoveUnreadCount(conversationModel: ConversationModel) {
        when (conversationModel.unReadCount.isNotEmpty()) {
            true -> {
                when (conversationModel.unReadCount.toInt() > 0) {
                    true -> {
                        when (messageUnreadCount.contains(conversationModel.convoId)) {
                            false -> {
                                messageUnreadCount.add(conversationModel.convoId)
                            }

                            true -> {}
                        }
                    }

                    false -> {
                        when (messageUnreadCount.contains(conversationModel.convoId)) {
                            true -> {
                                messageUnreadCount.remove(conversationModel.convoId)

                            }

                            false -> {}
                        }
                    }
                }
            }

            false -> {
                when (messageUnreadCount.contains(conversationModel.convoId)) {
                    true -> {
                        messageUnreadCount.remove(conversationModel.convoId)
                        //tvUnReadCount.text = "${messageUnreadCount.size}"
                    }

                    false -> TODO()
                }
            }
        }
        //tvUnReadCount.text = "${messageUnreadCount.size}"
        //tvUnReadCount.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))

        when (messageUnreadCount.isEmpty()) {
            true -> {
                // tvUnReadCount.visibility = View.INVISIBLE
            }

            false -> {
                // tvUnReadCount.visibility = View.VISIBLE
            }
        }
    }

    private fun updateConvo(conversationModel: ConversationModel) {
        val convo = sharedPreferencesUtil.fetchConversation()
        val index =
            convo.firstOrNull { conversationModel.convoId == it.convoId }?.let { convo.indexOf(it) }
                ?: -1

        if (index > -1) {
            convo[index] = conversationModel
            sharedPreferencesUtil.saveConversation(Gson().toJson(convo))
            conversationAdapter.update(conversationModel)
        }
    }

    private fun showLocalConversation() {

        firebaseUtil.fetchFullConversation(sharedPreferencesUtil.userId(),
            object : FirebaseListener {
                override fun onCancelled(error: DatabaseError) {
                    LogManager.logger.i(ArchitectureApp().tag, "DataSnapshot is Error : $error")
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    LogManager.logger.i(
                        ArchitectureApp().tag, "DataSnapshot is snapshot Conversation : $snapshot"
                    )
                    LogManager.logger.i(
                        ArchitectureApp.instance!!.tag, "Token is fetchConversation : $snapshot"
                    )
                    when (snapshot.exists()) {
                        true -> {

                            val conversation = snapshot.value as Map<*, *>
                            val data = arrayListOf<ConversationModel>()
                            for ((_, value) in conversation) {
                                try {

                                    val it = Gson().toJson(value)
                                    val item = Gson().fromJson(it, ConversationModel::class.java)
                                    LogManager.logger.i(
                                        ArchitectureApp.instance!!.tag,
                                        "Token is fetchConversation value last message : ${item.chatMessage}"
                                    )


                                    /* if(item.otherUserId == Constants.Admin.ConvoAdminOneToOne ) {
                                         fabSupporyAdmin.visibility = View.GONE
                                     }*/
                                    //  item.otherUserImageUrl = callForUserProfileImage(item.otherUserId)
                                    // item.otherUserImageUrl = callForUserProfileImage(item.otherUserId)
                                    fetchUserProfile(item.otherUserId)
                                    when (item.meDeletedConvo) {
                                        false -> {
                                            data.add(item)
                                        }

                                        true -> {
                                            if (Constants.Admin.ConvoAdminOneToOne == item.otherUserId) {
                                                data.remove(item)
                                                sharedPreferencesUtil.remove(item.convoId)
                                                sharedPreferencesUtil.remove("${item.convoId}_keys")
                                            }
                                        }
                                    }
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
                            sharedPreferencesUtil.saveConversation(
                                if (data.isNotEmpty()) Gson().toJson(
                                    data
                                ) else ""
                            )

                            conversationAdapter.refresh()
                            updateConversation()

                        }

                        false -> {
                            sharedPreferencesUtil.saveConversation("")
                            conversationAdapter.refresh()
                            updateConversation()
                        }
                    }
                    //ivChat.isSelected = true
                    //swipeRefreshChat.isRefreshing = false
                    //hideProgressView(rl_progress)
                }
            })
    }

    private fun updateConversation() {
        when (recycleChatConvoAdapter.itemCount > 0) {
            true -> {
                Log.e("PersonalFragment", "NoChat_Gon")
                personalChatRv.visibility = View.VISIBLE
                noChatLay.visibility = View.GONE

            }

            false -> {
                Log.e("PersonalFragment", "NoChat_Visi")
                personalChatRv.visibility = View.GONE
                noChatLay.visibility = View.VISIBLE

            }
        }
    }

    private fun fetchUserProfile(userId: String) {
        firebaseUtil.fetchUserProfileImage(userId, object : FirebaseListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if (!snapshot.exists()) return

                when (TextUtils.isDigitsOnly(snapshot.key)) {
                    true -> {
                        sharedPreferencesUtil.saveOtherUserProfile(
                            "${snapshot.key}", Gson().toJson(snapshot.value)
                        )
                    }

                    false -> TODO()
                }

            }
        })
    }

    private fun markMessageDelivered(convoId: String) {
        firebaseUtil.fetchFullUserChat(convoId, object : FirebaseListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                when (snapshot.exists()) {
                    true -> {
                        LogManager.logger.i(
                            ArchitectureApp.instance!!.tag, "Chat Status is : $snapshot"
                        )
                        val message = snapshot.value as Map<*, *>
                        for ((key, value) in message) {
                            try {
                                val chat =
                                    Gson().fromJson(Gson().toJson(value), ChatModel::class.java)
                                when (chat.chatStatus) {
                                    Constants.MessageStatus.Sent -> {
                                        when (chat.chatSenderId) {
                                            sharedPreferencesUtil.userId() -> {
                                                // Do Nothing
                                            }

                                            else -> {
                                                LogManager.logger.i(
                                                    ArchitectureApp.instance!!.tag,
                                                    "Chat Status is : ${chat.chatSenderId}"
                                                )
                                                val userChat =
                                                    sharedPreferencesUtil.fetchUserChat(convoId)
                                                userChat.add(chat.apply {
                                                    chatStatus = Constants.MessageStatus.Delivered
                                                })
                                                sharedPreferencesUtil.saveUserChat(
                                                    convoId, Gson().toJson(userChat)
                                                )
                                                LogManager.logger.i(
                                                    ArchitectureApp.instance!!.tag,
                                                    "Snapshot is key : $key"
                                                )

                                                if (sharedPreferencesUtil.userId().trim()
                                                        .isNotEmpty() && chat.chatSenderId.trim()
                                                        .isNotEmpty()
                                                ) firebaseUtil.updateChatStatus("${Constants.Firebase.Chat}/${
                                                    Constants.ConvoId.id(
                                                        chat.chatSenderId.toInt(),
                                                        sharedPreferencesUtil.userId().toInt()
                                                    )
                                                }/Chat/$key/chatStatus", chat.apply {
                                                    chatStatus = Constants.MessageStatus.Delivered
                                                })
                                            }
                                        }
                                    }

                                }

                            } catch (e: Exception) {
                                e.printStackTrace()
                            }

                        }
                    }

                    false -> TODO()
                }
            }
        })
    }
}