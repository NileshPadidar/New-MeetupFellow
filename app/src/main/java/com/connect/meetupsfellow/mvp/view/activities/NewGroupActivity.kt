package com.connect.meetupsfellow.mvp.view.activities

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import com.bumptech.glide.Glide
import com.connect.meetupsfellow.R
import com.connect.meetupsfellow.application.ArchitectureApp
import com.connect.meetupsfellow.components.activity.CustomAppActivityCompatViewImpl
import com.connect.meetupsfellow.constants.Constants
import com.connect.meetupsfellow.databinding.ActivityNewGroupBinding
import com.connect.meetupsfellow.developement.interfaces.SharedPreferencesUtil
import com.connect.meetupsfellow.log.LogManager
import com.connect.meetupsfellow.mvp.view.adapter.RecycleFellowListAdapter
import com.connect.meetupsfellow.mvp.view.model.RecycleModel
import javax.inject.Inject

class NewGroupActivity : CustomAppActivityCompatViewImpl() {

    lateinit var recycleModel: ArrayList<RecycleModel>
    lateinit var recycleFellowListAdapter: RecycleFellowListAdapter
    var fellowIds = ArrayList<String>()
    var fellowNames = ArrayList<String>()
    var groupImgUri: Uri? = null
    var currentUserId = ""
    var currentUserName = ""

    @Inject
    lateinit var sharedPreferencesUtil: SharedPreferencesUtil

    private var binding: ActivityNewGroupBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        component.inject(this@NewGroupActivity)
        binding = ActivityNewGroupBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        // setContentView(R.layout.activity_new_group)
        setupActionBar(getString(R.string.title_create_group), true)

        /*val layoutManager =
            LinearLayoutManager(this@NewGroupActivity, LinearLayoutManager.VERTICAL, false)

        fellowsRv.layoutManager = layoutManager

        fellowIds.clear()
        fellowNames.clear()

        val db = FirebaseDatabase.getInstance(Constants.Firebase.databaseUrl).getReference("Live")
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
                            Log.d("CurrentUser", currentUserId)
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


                    recycleFellowListAdapter =
                        RecycleFellowListAdapter(this@NewGroupActivity, recycleModel)

                    fellowsRv.adapter = recycleFellowListAdapter

                    recycleFellowListAdapter.notifyDataSetChanged()
                } else {

                    Log.d("userName2222", "notExist")
                }

            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })*/

        binding!!.addGroupImgBtn.setOnClickListener {

            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            intent.setType("image/*")
            startActivityForResult(intent, 100)
        }

        binding!!.searchFellows.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                recycleFellowListAdapter.filter.filter(s)
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })

        binding!!.createGroupBtn.setOnClickListener {

            if (binding!!.groupNameEtxt.text!!.isEmpty() || binding!!.groupNameEtxt.text!!.length < 5) {

                binding!!.groupNameEtxt.error = "Please give a valid name"
                binding!!.groupNameEtxt.requestFocus()
            } else if (groupImgUri == null) {

                Toast.makeText(this, "Please upload a group image", Toast.LENGTH_SHORT).show()
            } else if (binding!!.groupDestxt.text!!.isEmpty()) {

                binding!!.groupDestxt.error = "Please give a group description"
//                Toast.makeText(this, "Please give a group description", Toast.LENGTH_SHORT).show()
                binding!!.groupDestxt.requestFocus()
            }/*else if (fellowIds.size < 2) {

                Toast.makeText(this, "Group members must be more than 1", Toast.LENGTH_SHORT).show()
            }*/
            else {

                val bundle = Bundle()

                bundle.putString("imgUri", groupImgUri.toString())
                bundle.putString("groupName", binding!!.groupNameEtxt.text.toString())
                bundle.putString("groupDes", binding!!.groupDestxt.text.toString())

                switchActivity(Constants.Intent.AddNewGroupMembers, false, bundle)

                /*loadingGroup.visibility = View.VISIBLE
                createGroupBtn.isEnabled = false

                val gDb = FirebaseDatabase.getInstance(Constants.Firebase.databaseUrl)
                    .getReference("Live").child("ChatGroup")
                val storageRef = FirebaseStorage.getInstance().getReference("groupImgs")
                    .child(System.currentTimeMillis().toString() + ".jpg")

                val uploadTask: UploadTask = storageRef.putFile(groupImgUri!!)

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

                            val currentDateTime = Date().time
                            val groupName = groupNameEtxt.text.toString()
                            val groupImgUri = task.result.toString()
                            val groupId = groupName + currentUserId + System.currentTimeMillis()
                            fellowIds.add(currentUserId)
                            fellowNames.add(currentUserName)
                            val newGDb = gDb.child(groupId)
                            val groupMembersDb = newGDb.child("GroupMembers")
                            val groupDescription = groupDestxt.text.toString()

                            for (i in fellowIds.indices) {

                                groupMembersDb.child(fellowIds[i]).child("isBlocked").setValue("no")
                                groupMembersDb.child(fellowIds[i]).child("memberName")
                                    .setValue(fellowNames[i])
                            }
                            newGDb.child("GroupAdmin").setValue(currentUserId)
                            newGDb.child("GroupName").setValue(groupName)
                            newGDb.child("GroupImg").setValue(groupImgUri)
                            newGDb.child("GroupDescription").setValue(groupDescription)
                            newGDb.child("GroupCreatedOn").setValue(currentDateTime.toString())
                            newGDb.child("LastMsg").setValue("New Group Created")
                            newGDb.child("LastMsgTime").setValue(currentDateTime.toString())
                            newGDb.child("SenderName").setValue(currentUserName)
                            newGDb.child("SenderId").setValue(currentUserId)

                            val chatDb = newGDb.child("Chats").child(currentDateTime.toString())
                            chatDb.child("msg").setValue("New Group Created")
                            chatDb.child("msgTime").setValue(currentDateTime.toString())
                            chatDb.child("senderId").setValue(currentUserId)
                            chatDb.child("senderName").setValue(currentUserName)
                            val readDb = chatDb.child("readBy").child(currentUserId)

                            readDb.child("read").setValue("yes")
                                .addOnSuccessListener(object : OnSuccessListener<Void> {
                                    override fun onSuccess(p0: Void?) {
                                        loadingGroup.visibility = View.GONE
                                        createGroupBtn.isEnabled = true
                                        finish()
                                    }
                                }).addOnFailureListener(object : OnFailureListener {
                                override fun onFailure(p0: Exception) {

                                    loadingGroup.visibility = View.GONE
                                    createGroupBtn.isEnabled = true
                                    Toast.makeText(
                                        this@NewGroupActivity,
                                        p0.toString(),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }


                            })


                        }
                    }
                })*/


            }
        }
    }

    override fun onResume() {

        super.onResume()

        if (Constants.Variables.isGroupCreated) {

            finish()
            Constants.Variables.isGroupCreated = false
        }

        var lasttrackTime = sharedPreferencesUtil.featchTimmerTime()
        var currentbackTime: Long = System.currentTimeMillis()
        Log.e("lasttrackTime", lasttrackTime)
        LogManager.logger.i(ArchitectureApp.instance!!.tag, "onResume ::: " + lasttrackTime)

        if (lasttrackTime != "") {
            val currentSeconds = ((currentbackTime - lasttrackTime.toLong()) / 1000).toInt()
            LogManager.logger.i(
                ArchitectureApp.instance!!.tag, "onResume ::: " + currentSeconds
            )
            if (currentSeconds > Constants.TimerRefresh.START_TIME_IN_SEC) {
                startActivity(Intent(this, SplashActivity::class.java))
                finish()
            }
        }
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (resultCode) {
            Activity.RESULT_OK -> {
                when (requestCode) {

                    100 -> {

                        if (data != null && data.data != null) {

                            groupImgUri = data.data

                            Glide.with(this).load(groupImgUri)
                                .placeholder(R.drawable.meetupsfellow_transpatent)
                                .into(binding!!.groupImg)
                        }
                    }
                }
            }

        }

    }

}