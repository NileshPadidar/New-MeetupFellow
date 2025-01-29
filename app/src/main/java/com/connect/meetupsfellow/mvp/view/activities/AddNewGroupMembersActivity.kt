package com.connect.meetupsfellow.mvp.view.activities

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.connect.meetupsfellow.R
import com.connect.meetupsfellow.components.activity.CustomAppActivityCompatViewImpl
import com.connect.meetupsfellow.constants.Constants
import com.connect.meetupsfellow.databinding.ActivityAddNewGroupMembertsBinding
import com.connect.meetupsfellow.developement.interfaces.SharedPreferencesUtil
import com.connect.meetupsfellow.mvp.view.adapter.RecycleFellowListAdapter
import com.connect.meetupsfellow.mvp.view.model.RecycleModel
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

class AddNewGroupMembersActivity : CustomAppActivityCompatViewImpl() {

    lateinit var recycleModel: ArrayList<RecycleModel>
    lateinit var recycleFellowListAdapter: RecycleFellowListAdapter
    var fellowIds = ArrayList<String>()
    var fellowNames = ArrayList<String>()
    var groupImgUri: Uri? = null
    var currentUserId = ""
    var currentUserName = ""
    lateinit var groupName: String
    lateinit var groupDes: String

    @Inject
    lateinit var sharedPreferencesUtil: SharedPreferencesUtil
    private var binding: ActivityAddNewGroupMembertsBinding? = null
    private lateinit var progressBar: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        component.inject(this@AddNewGroupMembersActivity)
        binding = ActivityAddNewGroupMembertsBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        // setContentView(R.layout.activity_add_new_group_memberts)
        setupActionBar(getString(R.string.title_add_members), true)
        startLoadingAnim()
        progressBar = binding!!.includedLoading.rlProgress
        groupImgUri = Uri.parse(intent.getStringExtra("imgUri").toString())
        groupName = intent.getStringExtra("groupName").toString()
        groupDes = intent.getStringExtra("groupDes").toString()

        val layoutManager = LinearLayoutManager(
            this@AddNewGroupMembersActivity,
            LinearLayoutManager.VERTICAL,
            false
        )

        binding!!.fellowsAddRv.layoutManager = layoutManager

        fellowIds.clear()
        fellowNames.clear()

        val db =
            FirebaseDatabase.getInstance(Constants.Firebase.databaseUrl).getReference(Constants.Firebase.ChatDB)
                .child("UserProfile")

        val profile = sharedPreferencesUtil.fetchUserProfile()

        Log.d("userName", profile.name)

        val now = Date().time

        Log.d("currentDate", now.toString())

        // Create a formatter along with the desired output pattern
        val formatter = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())

        // Put the time (in millis) in our formatter
        val result = formatter.format(now)

        Log.d("currentDate", result.toString())

        db.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snap: DataSnapshot) {

                if (snap.exists()) {

                    recycleModel = ArrayList()

                    for (i in snap.children) {

                        Log.d("userName2222", i.child("userName").value.toString())
                        Log.d("userName2222", i.child("imgUrl").value.toString())
                        Log.d("userName2222", i.child("userId").value.toString())

                        if (i.child("userName").value.toString() == profile.name) {

                            currentUserId = i.child("userId").value.toString()
                            currentUserName = i.child("userName").value.toString()
                            Log.e("CurrentUser", currentUserId)
                        } else {

                            recycleModel.add(
                                RecycleModel(
                                    i.child("imgUrl").value.toString(),
                                    i.child("userName").value.toString(),
                                    i.child("userId").value.toString()
                                )
                            )
                        }
                    }
                    Log.e("userName2222", "setUser_list: " + recycleModel.size)

                    recycleFellowListAdapter =
                        RecycleFellowListAdapter(this@AddNewGroupMembersActivity, recycleModel)

                    binding!!.fellowsAddRv.adapter = recycleFellowListAdapter

                    recycleFellowListAdapter.notifyDataSetChanged()
                } else {

                    Log.e("userName2222", "notExist")
                }

            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })

        binding!!.createGroupBtn.setOnClickListener {

            createGroup()
        }

    }

    private fun startLoadingAnim() {

        val animation = android.view.animation.AnimationUtils.loadAnimation(
            applicationContext, R.anim.blink_anim
        )

        binding!!.includedLoading.animLogo.startAnimation(animation)
    }

    private fun createGroup() {
        Log.e("userName2222", "newGroupCreated")
        //loadingGroup.visibility = View.VISIBLE
        showProgressView(progressBar)
        binding!!.createGroupBtn.isEnabled = false

        val gDb =
            FirebaseDatabase.getInstance(Constants.Firebase.databaseUrl).getReference(Constants.Firebase.ChatDB)
                .child("ChatGroup")
        val storageRef = FirebaseStorage.getInstance().getReference("groupImgs")
            .child(System.currentTimeMillis().toString() + ".jpg")

        val uploadTask: UploadTask = storageRef.putFile(groupImgUri!!)

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
                    Log.e("userName2222", "addGroupImage")
                    val currentDateTime = Date().time
                    val groupName = groupName
                    val groupImgUri = task.result.toString()
                    val groupId = groupName + currentUserId + System.currentTimeMillis()
                    fellowIds.add(currentUserId)
                    fellowNames.add(currentUserName)
                    val newGDb = gDb.child(groupId)
                    val groupMembersDb = newGDb.child("GroupMembers")
                    val groupDescription = groupDes

                    val chatDb = newGDb.child("Chats").child(currentDateTime.toString())
                    chatDb.child("msg").setValue("")
                    chatDb.child("img").setValue("")
                    chatDb.child("notice").setValue("Group created by $currentUserName")
                    chatDb.child("msgTime").setValue(currentDateTime.toString())
                    chatDb.child("senderId").setValue(currentUserId)
                    chatDb.child("senderName").setValue(currentUserName)
                    val readDb = chatDb.child("readBy").child(currentUserId)

                    readDb.child("read").setValue("yes")

                    for (i in fellowIds.indices) {

                        groupMembersDb.child(fellowIds[i]).child("isBlocked").setValue("no")
                        groupMembersDb.child(fellowIds[i]).child("memberName")
                            .setValue(fellowNames[i])

                        if (fellowIds[i] != currentUserId) {

                            val currentTime = System.currentTimeMillis()

                            val chatDb = newGDb.child("Chats").child(currentTime.toString())
                            chatDb.child("msg").setValue("")
                            chatDb.child("img").setValue("")
                            chatDb.child("notice")
                                .setValue("$currentUserName added ${fellowNames[i]}")
                            chatDb.child("msgTime").setValue(currentTime.toString())
                            chatDb.child("senderId").setValue(currentUserId)
                            chatDb.child("senderName").setValue(currentUserName)
                            val readDb = chatDb.child("readBy").child(currentUserId)

                            readDb.child("read").setValue("yes")
                        }
                    }

                    newGDb.child("GroupAdmin").setValue(currentUserId)
                    newGDb.child("GroupName").setValue(groupName)
                    newGDb.child("GroupImg").setValue(groupImgUri)
                    newGDb.child("GroupDescription").setValue(groupDescription)
                    newGDb.child("GroupCreatedOn").setValue(currentDateTime.toString())
                    newGDb.child("LastMsg").setValue("New Group Created")
                    newGDb.child("LastMsgTime").setValue(currentDateTime.toString())
                    newGDb.child("SenderName").setValue(currentUserName)
                    Log.e("Group_Creat", "Data: " + newGDb)
                    Log.e("Group_Creat", "Data1: " + newGDb.database)
                    Log.e("Group_Creat", "Data2: " + newGDb.toString())
                    Log.e("Group_Creat", "Data3: " + chatDb)
                    newGDb.child("SenderId").setValue(currentUserId)
                        .addOnSuccessListener(object : OnSuccessListener<Void> {
                            override fun onSuccess(p0: Void?) {
                                //loadingGroup.visibility = View.GONE

                                hideProgressView(progressBar)
                                binding!!.createGroupBtn.isEnabled = true
                                Constants.Variables.isGroupCreated = true
                                finish()

                            }
                        }).addOnFailureListener(object : OnFailureListener {
                        override fun onFailure(p0: Exception) {

                            //loadingGroup.visibility = View.GONE
                            hideProgressView(progressBar)
                            binding!!.createGroupBtn.isEnabled = true
                            Toast.makeText(
                                this@AddNewGroupMembersActivity, p0.toString(), Toast.LENGTH_SHORT
                            ).show()
                        }


                    })


                }
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