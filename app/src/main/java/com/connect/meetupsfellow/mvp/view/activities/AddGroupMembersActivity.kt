package com.connect.meetupsfellow.mvp.view.activities

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.connect.meetupsfellow.R
import com.connect.meetupsfellow.components.activity.CustomAppActivityCompatViewImpl
import com.connect.meetupsfellow.constants.Constants
import com.connect.meetupsfellow.databinding.ActivityAddGroupMembersBinding
import com.connect.meetupsfellow.developement.interfaces.SharedPreferencesUtil
import com.connect.meetupsfellow.mvp.view.adapter.RecycleAddGroupMembersAdapter
import com.connect.meetupsfellow.mvp.view.model.RecycleModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import javax.inject.Inject

class AddGroupMembersActivity : CustomAppActivityCompatViewImpl() {

    var groupMembers = ArrayList<String>()
    var groupId = ""
    var currentUserId = ""
    var currentUseName = ""
    lateinit var db: DatabaseReference
    lateinit var recycleModel: java.util.ArrayList<RecycleModel>
    lateinit var recycleAddGroupMembersAdapter: RecycleAddGroupMembersAdapter
    var fellowIds = java.util.ArrayList<String>()
    var fellowNames = java.util.ArrayList<String>()
    private var binding: ActivityAddGroupMembersBinding? = null
    private lateinit var progressBar: LinearLayout

    @Inject
    lateinit var sharedPreferencesUtil: SharedPreferencesUtil

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddGroupMembersBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        //  setContentView(R.layout.activity_add_group_members)
        component.inject(this@AddGroupMembersActivity)
        setupActionBar(getString(R.string.title_add_members), true)
        startLoadingAnim()
        progressBar = binding!!.includedLoading.rlProgress
        fellowIds.clear()
        fellowNames.clear()

        val profile = sharedPreferencesUtil.fetchUserProfile()

        groupMembers = intent.getStringArrayListExtra("members")!!
        groupId = intent.getStringExtra("groupId").toString()

        currentUserId =
            getSharedPreferences("UserId", MODE_PRIVATE).getString("UserId", "").toString()
        currentUseName = profile.name

        db =
            FirebaseDatabase.getInstance(Constants.Firebase.databaseUrl).getReference(Constants.Firebase.ChatDB)
                .child("UserProfile")

        val layoutManager =
            LinearLayoutManager(this@AddGroupMembersActivity, LinearLayoutManager.VERTICAL, false)

        binding!!.fellowsAddRv.layoutManager = layoutManager

        setUpMembersList()

        binding!!.addMembersBtn.setOnClickListener {

            addMembers()
        }

        binding!!.searchAddFellows.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {


            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                recycleAddGroupMembersAdapter.filter.filter(s)
            }

            override fun afterTextChanged(s: Editable?) {


            }


        })

    }

    private fun startLoadingAnim() {

        val animation = android.view.animation.AnimationUtils.loadAnimation(
            applicationContext, R.anim.blink_anim
        )

        binding!!.includedLoading.animLogo.startAnimation(animation)
    }

    private fun addMembers() {

        val mDb =
            FirebaseDatabase.getInstance(Constants.Firebase.databaseUrl).getReference(Constants.Firebase.ChatDB)
                .child("ChatGroup").child(groupId).child("GroupMembers")
        val cDb =
            FirebaseDatabase.getInstance(Constants.Firebase.databaseUrl).getReference(Constants.Firebase.ChatDB)
                .child("ChatGroup").child(groupId).child("Chats")

        for (i in fellowIds.indices) {

            mDb.child(fellowIds[i]).child("isBlocked").setValue("no")
            mDb.child(fellowIds[i]).child("memberName").setValue(fellowNames[i])

            val currentDateTime = System.currentTimeMillis()

            val chatDb = cDb.child(currentDateTime.toString())
            chatDb.child("msg").setValue("")
            chatDb.child("img").setValue("")
            chatDb.child("notice").setValue("${currentUseName} added ${fellowNames[i]}")
            chatDb.child("msgTime").setValue(currentDateTime.toString())
            chatDb.child("senderId").setValue(currentUserId)
            chatDb.child("senderName").setValue(currentUseName)
            val readDb = chatDb.child("readBy").child(currentUserId)

            readDb.child("read").setValue("yes")
        }

        finish()
    }

    private fun setUpMembersList() {

        showProgressView(progressBar)

        db.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snap: DataSnapshot) {

                if (snap.exists()) {

                    recycleModel = java.util.ArrayList()

                    for (i in snap.children) {

                        Log.d("userName2222", i.child("userName").value.toString())
                        Log.d("userName2222", i.child("imgUrl").value.toString())
                        Log.d("userName2222", i.child("userId").value.toString())

                        Log.d("members", groupMembers.get(0))

                        if (groupMembers.contains(i.child("userId").value.toString())) {

                            //currentUserId = i.child("userId").value.toString()
                            //currentUserName =  i.child("userName").value.toString()
                            //Log.d("CurrentUser", currentUserId)
                        } else {
                            Log.d("membersNo", i.child("userName").value.toString())
                            Log.d("membersNo", i.child("imgUrl").value.toString())
                            Log.d("membersNo", i.child("userId").value.toString())

                            recycleModel.add(
                                RecycleModel(
                                    i.child("imgUrl").value.toString(),
                                    i.child("userName").value.toString(),
                                    i.child("userId").value.toString()
                                )
                            )
                        }
                    }


                    recycleAddGroupMembersAdapter =
                        RecycleAddGroupMembersAdapter(this@AddGroupMembersActivity, recycleModel)

                    binding!!.fellowsAddRv.adapter = recycleAddGroupMembersAdapter

                    recycleAddGroupMembersAdapter.notifyDataSetChanged()

                    hideProgressView(progressBar)
                } else {

                    hideProgressView(progressBar)
                    Log.d("userName2222", "notExist")
                }

            }

            override fun onCancelled(p0: DatabaseError) {
                hideProgressView(progressBar)
            }
        })

    }

    fun addOrRemoveFellowsToGroup(fellowId: String, fellowName: String) {

        if (fellowIds.contains(fellowId)) {

            fellowIds.remove(fellowId)
            fellowNames.remove(fellowName)
            Log.d("userIdRem2222", fellowId)
        } else {

            fellowIds.add(fellowId)
            fellowNames.add(fellowName)
            Log.d("userIdAdd2222", fellowId)
        }
    }
}