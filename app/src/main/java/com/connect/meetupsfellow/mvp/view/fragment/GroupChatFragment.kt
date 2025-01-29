package com.connect.meetupsfellow.mvp.view.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.connect.meetupsfellow.R
import com.connect.meetupsfellow.application.ArchitectureApp
import com.connect.meetupsfellow.components.fragment.CustomFragment
import com.connect.meetupsfellow.constants.Constants
import com.connect.meetupsfellow.databinding.FragmentGroupChatBinding
import com.connect.meetupsfellow.databinding.LayoutChatBinding
import com.connect.meetupsfellow.developement.interfaces.SharedPreferencesUtil
import com.connect.meetupsfellow.mvp.view.activities.HomeActivity
import com.connect.meetupsfellow.mvp.view.adapter.RecycleGroupConvoAdapter
import com.connect.meetupsfellow.mvp.view.model.RecycleModel
import com.connect.meetupsfellow.retrofit.response.ResponseUserData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.Collections
import javax.inject.Inject

class GroupChatFragment : CustomFragment() {

    lateinit var recyclerModal: ArrayList<RecycleModel>
    lateinit var recycleGroupConvoAdapter: RecycleGroupConvoAdapter
    lateinit var groupChatRv: RecyclerView
    lateinit var swipeGroupChat: SwipeRefreshLayout
    lateinit var addGroupBtn: com.google.android.material.floatingactionbutton.FloatingActionButton
    var unreadCountF = 0
    var unreadCountT = 0
    lateinit var profile: ResponseUserData
    lateinit var noGroupChatLay: RelativeLayout
    lateinit var homeActivity: HomeActivity
    private var _binding: FragmentGroupChatBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var sharedPreferencesUtil: SharedPreferencesUtil
    private lateinit var layoutChatBinding: LayoutChatBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        ArchitectureApp.component!!.inject(this@GroupChatFragment)
       // val view = inflater.inflate(R.layout.fragment_group_chat, container, false)
        _binding = FragmentGroupChatBinding.inflate(inflater, container, false)
        return binding.root

        // return view

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        groupChatRv = view.findViewById(R.id.groupChatRv)
        addGroupBtn = view.findViewById(R.id.addGroupBtn)
        noGroupChatLay = view.findViewById(R.id.noGroupChatLay)
        swipeGroupChat = view.findViewById(R.id.swipeGroupChat)

        homeActivity = activity as HomeActivity
        layoutChatBinding = homeActivity.binding!!.includedLayHome.includedLChat
        profile = sharedPreferencesUtil.fetchUserProfile()

        //checkPremium()
        setGroupChat()

        addGroupBtn.setOnClickListener {

            switchActivity(Constants.Intent.NewGroup, false, Bundle())
        }

        swipeGroupChat.setOnRefreshListener {

            setGroupChat()

            swipeGroupChat.isRefreshing = false
        }


    }

    private fun checkPremium() {

        if (profile.isProMembership) {

            addGroupBtn.visibility = View.VISIBLE
        } else {
            addGroupBtn.visibility = View.GONE
        }
    }

    override fun onResume() {
        super.onResume()

        setGroupChat()/*var lasttrackTime = sharedPreferencesUtil.featchTimmerTime()
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
                startActivity(Intent(requireContext(), SplashActivity::class.java))
            }
        }*/
    }

    private fun setGroupChat() {

        val profile = sharedPreferencesUtil.fetchUserProfile()
        val layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        groupChatRv.layoutManager = layoutManager

        groupChatRv.addItemDecoration(
            DividerItemDecoration(
                groupChatRv.context, DividerItemDecoration.VERTICAL
            )
        )

        recyclerModal = ArrayList()

        val db =
            FirebaseDatabase.getInstance(Constants.Firebase.databaseUrl).getReference(Constants.Firebase.ChatDB)
                .child("ChatGroup")
        var userId = ""

        val dbUser =
            FirebaseDatabase.getInstance(Constants.Firebase.databaseUrl).getReference(Constants.Firebase.ChatDB)
                .child("UserProfile")


        Constants.currentUser = profile.name
        Log.d("userID", profile.userId)

        /*dbUser.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snap: DataSnapshot) {

                if (snap.exists()) {

                    for (i in snap.children) {

                        Log.d("FirebaseuserName", profile.name)

                        if (i.child("userName").value.toString() == profile.name) {

                            Log.d("UserId", i.child("userId").value.toString())

                            activity!!.getSharedPreferences("UserId", AppCompatActivity.MODE_PRIVATE).edit()
                                .putString("UserId", i.child("userId").value.toString()).apply()
                            userId = i.child("userId").value.toString()
                        }
                    }
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })*/

        val savedUserID = profile.userId

        Log.d("FirebaseUserId", savedUserID.toString())

        db.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snap: DataSnapshot) {

                if (snap.exists()) {

                    recyclerModal.clear()

                    Log.d("FirebaseUserId", savedUserID)
                    //swipeRefreshChat.isRefreshing = true

                    for (i in snap.children) {

                        for (j in i.child("GroupMembers").children) {

                            Log.d("FirebaseMembers", j.key.toString())

                            if (j.key.toString() == savedUserID) {

                                var unreadCount = 0

                                for (k in i.child("Chats").children) {

                                    if (k.child("readBy").child(savedUserID).exists()) {
                                        Log.d("exists", "exists")

                                    } else {
                                        Log.d("exists", "Notexists")
                                        unreadCount++
                                        Log.d(
                                            "FirebaseUnread", unreadCount.toString()
                                        )
                                    }
                                }


                                Log.d("UnreadCountFFF", unreadCountF.toString())
                                //Log.d("unreadCountArr", unreadCountArr.get(0).toString() + "   " + unreadCountArr.get(1).toString())

                                val groupImg = i.child("GroupImg").value.toString()
                                val groupName = i.child("GroupName").value.toString()
                                val groupLastMsg = i.child("LastMsg").value.toString()
                                val groupLastMsgTime = i.child("LastMsgTime").value.toString()
                                val groupId = i.key.toString()
                                var lastMsgSender = ""

                                if (i.child("SenderName").value.toString() == profile.name) {

                                    lastMsgSender = "You : "
                                } else {

                                    lastMsgSender = i.child("SenderName").value.toString() + " : "
                                }

                                if (groupId != "null" && groupLastMsg != "null" && groupLastMsgTime != "null" && groupName != "null" && groupImg != "null") {

                                    Log.d("recycleSize", recyclerModal.size.toString())

                                    recyclerModal.add(
                                        RecycleModel(
                                            groupImg,
                                            groupName,
                                            groupLastMsg,
                                            unreadCount.toString(),
                                            groupLastMsgTime,
                                            groupId,
                                            lastMsgSender
                                        )
                                    )

                                    unreadCountT += unreadCount/*if (groupChatRv.adapter!!.itemCount > 0) {

                                        groupChattxt.visibility = View.VISIBLE

                                    } else {
                                        groupChattxt.visibility = View.GONE

                                    }*/

                                }

                                //},1000)


                                //val unreadCount = getUnreadCount(i.key.toString(), db, savedUserID).toString()

                            }
                        }
                    }

                    Collections.sort(recyclerModal)

                    recycleGroupConvoAdapter = RecycleGroupConvoAdapter(
                        activity, recyclerModal
                    )

                    groupChatRv.adapter = recycleGroupConvoAdapter

                    recycleGroupConvoAdapter.notifyDataSetChanged()

                    Log.d(
                        "RvChatCount", groupChatRv.adapter!!.itemCount.toString()
                    )

                    Log.d("UnreadCount", unreadCountT.toString())

                    if (unreadCountT == 0) {

                        layoutChatBinding.tvUnReadCountG.visibility = View.GONE

                    } else {

                        layoutChatBinding.tvUnReadCountG.visibility = View.VISIBLE
                        layoutChatBinding.tvUnReadCountG.text = unreadCountT.toString()
                    }

                    Constants.unreadCountG += unreadCountT

                    if (Constants.unreadCountG == 0 && Constants.unreadCountP == 0) {

                        homeActivity.binding!!.includedLayHome.tvUnReadCountH.visibility = View.GONE

                    } else {

                        homeActivity.binding!!.includedLayHome.tvUnReadCountH.visibility =
                            View.VISIBLE
                        homeActivity.binding!!.includedLayHome.tvUnReadCountH.text =
                            (Constants.unreadCountP + Constants.unreadCountG).toString()
                    }

                    Constants.unreadCountG = 0
                    unreadCountT = 0/*if (groupChatRv.adapter == null) {

                        Log.d("rvGroupNull", "yes")
                        groupChatRv.visibility = View.GONE
                    } else {

                        Log.d("rvGroupNull", "no")
                        Log.d("rvGroupNull", groupChatRv.adapter!!.itemCount.toString())
                        if (groupChatRv.adapter!!.itemCount <= 0){

                            groupChatRv.visibility = View.GONE
                            groupChattxt.visibility = View.GONE
                        } else {

                            groupChatRv.visibility = View.VISIBLE
                            groupChattxt.visibility = View.VISIBLE
                        }

                        groupChatRv.visibility = View.VISIBLE
                    }*/

                    if (groupChatRv.adapter != null) {

                        if (groupChatRv.adapter!!.itemCount <= 0) {

                            Log.d("rvGroupChat", "no")

                            noGroupChatLay.visibility = View.VISIBLE
                            groupChatRv.visibility = View.GONE
                            //checkPremium()

                        } else {

                            noGroupChatLay.visibility = View.GONE
                            groupChatRv.visibility = View.VISIBLE
                            //checkPremium()
                        }
                    } else {

                        noGroupChatLay.visibility = View.VISIBLE
                        groupChatRv.visibility = View.GONE
                        //checkPremium()

                    }

                } else {

                    noGroupChatLay.visibility = View.VISIBLE
                    groupChatRv.visibility = View.GONE
                    //checkPremium()
                }
            }

            override fun onCancelled(p0: DatabaseError) {


            }

        })

    }

}