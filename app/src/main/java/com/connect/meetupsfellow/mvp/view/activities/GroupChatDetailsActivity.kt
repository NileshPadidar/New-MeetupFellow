package com.connect.meetupsfellow.mvp.view.activities

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.*
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.connect.meetupsfellow.R
import com.connect.meetupsfellow.application.ArchitectureApp
import com.connect.meetupsfellow.components.activity.CustomAppActivityCompatViewImpl
import com.connect.meetupsfellow.constants.Constants
import com.connect.meetupsfellow.developement.interfaces.SharedPreferencesUtil
import com.connect.meetupsfellow.log.LogManager
import com.connect.meetupsfellow.mvp.view.adapter.RecycleGroupMembersAdapter
import com.connect.meetupsfellow.mvp.view.model.RecycleModel
/*import kotlinx.android.synthetic.main.activity_group_chat_details.*
import kotlinx.android.synthetic.main.layout_loading.*
import kotlinx.android.synthetic.main.layout_toolbar_chat.**/
import javax.inject.Inject


class GroupChatDetailsActivity : CustomAppActivityCompatViewImpl() {

    lateinit var userId:String
    lateinit var groupId:String
    lateinit var otherUserId : String
    lateinit var otherUserName : String
    lateinit var groupImgUri : Uri
    lateinit var db : DatabaseReference
    var iAmAdmin = false
    var adminId = ""
    var currentUser = ""
    lateinit var recycleModel: ArrayList<RecycleModel>
    lateinit var recycleGroupMembersAdapter : RecycleGroupMembersAdapter

    @Inject
    lateinit var sharedPreferencesUtil: SharedPreferencesUtil

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        component.inject(this@GroupChatDetailsActivity)
        setContentView(R.layout.activity_group_chat_details)
      ///  startLoadingAnim()
     ///   tvUsername.text = "Details"

        userId = getSharedPreferences("UserId", MODE_PRIVATE).getString("UserId", "").toString()

        groupId = intent.getStringExtra("groupId").toString()

        currentUser = Constants.currentUser

        Log.d("GroupId", groupId)

        db = FirebaseDatabase.getInstance(Constants.Firebase.databaseUrl).getReference(Constants.Firebase.ChatDB).child("ChatGroup").child(groupId)

        //setGroupNameAndImg()

        /*val paint = adminTxt.paint
        val width = paint.measureText(adminTxt.text.toString())
        val textShader: Shader = LinearGradient(
            0f, 0f, width, adminTxt.textSize, intArrayOf(
                Color.parseColor("#F4447E"),
                Color.parseColor("#8448F4")
            ), null, Shader.TileMode.REPEAT
        )

        adminTxt.paint.shader = textShader*/

       //// setUpGroupMembers()

      /*  groupBackBtn.setOnClickListener {

            onBackPressed()
        }*/


        /*editGroupImg.setOnClickListener {

            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            intent.setType("image/*")
            startActivityForResult(intent, 100)
        }

        editGroupName.setOnClickListener {

            updateGroupNameDialog("name")
        }

        editGroupDes.setOnClickListener {

            updateGroupNameDialog("des")
        }

        deleteGroupBtn.setOnClickListener {

            showDeleteLeaveGroupDialog("delete")

        }

        leftGroupChatBtn.setOnClickListener {
            showDeleteLeaveGroupDialog("leave")
        }*/

}

         */
    }

  /*  private fun startLoadingAnim() {

        val animation = android.view.animation.AnimationUtils.loadAnimation(
            applicationContext, R.anim.blink_anim
        )

        animLogo.startAnimation(animation)
    }*/

   /* private fun showDeleteLeaveGroupDialog(type : String) {

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
        val cancelBtn  = view.findViewById<Button>(R.id.noExitBtn)
        val confirm = view.findViewById<Button>(R.id.yesExitBtn)

        if (type == "delete"){

            dialogHead.text = "Delete Group"
            dialogContent.text = "Are you sure, you want to delete group?"

        }
        else if(type == "remove") {

            dialogHead.text = "Remove from Group"
            dialogContent.text = "Are you sure, you want to remove user from group?"
        }
        else if (type == "makeAdmin") {

            dialogHead.text = "Make Admin"
            dialogContent.text = "Are you sure, you want to Make this user Admin?"
        }

        else {

            dialogHead.text = "Leave Group"
            dialogContent.text = "Are you sure, you want to leave group?"

        }

        cancelBtn.setOnClickListener {

            dialog.dismiss()
        }

        confirm.setOnClickListener {

            if (type == "delete"){

                deleteGroup()
                dialog.dismiss()
            }
            else if (type == "remove"){

                removeUserFromGroup(otherUserId)
                dialog.dismiss()
            }
            else if (type == "makeAdmin") {

                transferAdmin()
                dialog.dismiss()
            }
            else {

                leaveGroup()
                dialog.dismiss()
            }
        }

        dialog.show()
    }*/

    private fun leaveGroup() {

        val currentTime = System.currentTimeMillis().toString()

        val newChat = db.child("Chats").child(currentTime)

        newChat.child("msg").setValue("")
        newChat.child("img").setValue("")
        newChat.child("notice").setValue("${currentUser} left the group.")
        newChat.child("msgTime").setValue(currentTime.toString())
        newChat.child("senderId").setValue(this.userId)
        newChat.child("senderName").setValue(currentUser)
        val readDb = newChat.child("readBy").child(this.userId)
        readDb.child("read").setValue("yes")

        db.child("GroupMembers").child(userId).removeValue().addOnSuccessListener(OnSuccessListener {

            Toast.makeText(this@GroupChatDetailsActivity, "Group Left", Toast.LENGTH_SHORT).show()

            finish()
        })
    }

    private fun deleteGroup() {

        db.removeValue().addOnSuccessListener(OnSuccessListener {

            Toast.makeText(this@GroupChatDetailsActivity, "Group Deleted", Toast.LENGTH_SHORT).show()

            switchActivity(Constants.Intent.Home, true, Bundle())
        })
    }

    /*private fun setUpGroupMembers() {

        showProgressView(rl_progress)

        recycleModel = ArrayList()

        val layoutManager = LinearLayoutManager(this@GroupChatDetailsActivity, LinearLayoutManager.VERTICAL, false)

        groupMembersRv.layoutManager = layoutManager

        db.child("GroupMembers").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snap: DataSnapshot) {

                if (snap.exists()) {

                    recycleModel.clear()

                    for (i in snap.children) {

                        //if (i.key.toString() != userId) {

                            Log.d("UserExits", i.key.toString())

                            val userDb =
                                FirebaseDatabase.getInstance(Constants.Firebase.databaseUrl)
                                    .getReference(Constants.Firebase.ChatDB).child("UserProfile")
                                    .child(i.key.toString())

                            userDb.addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(userSnap: DataSnapshot) {

                                    if (userSnap.exists()) {

                                        Log.d(
                                            "UserExits",
                                            userSnap.child("userName").value.toString()
                                        )

                                        val isAdmin = i.key.toString() == adminId

                                        val userName = userSnap.child("userName").value.toString()
                                        val userImg = userSnap.child("imgUrl").value.toString()
                                        val userId = userSnap.child("userId").value.toString()
                                        val onlineStatus = userSnap.child("onlineStatus").value.toString()
                                        val isBlocked = i.child("isBlocked").value.toString()

                                        recycleModel.add(RecycleModel(userName, onlineStatus, userImg, userId, isBlocked, isAdmin, iAmAdmin))

                                        Log.d("membersCount",  recycleModel.size.toString())
                                        totalGroupMembers.text = recycleModel.size.toString() + " Members"

                                    } else {
                                        Log.d("UserExits", "no")
                                    }
                                }

                                override fun onCancelled(p0: DatabaseError) {

                                    hideProgressView(rl_progress)
                                }
                            })
                      //  }
                    }

                    val handler = Handler()
                    handler.postDelayed(Runnable {

                        Log.d("UserExitsSizeRv", recycleModel.size.toString())

                        recycleGroupMembersAdapter = RecycleGroupMembersAdapter(this@GroupChatDetailsActivity, recycleModel, object : RecycleGroupMembersAdapter.OptionsMenuClickListener {

                            override fun onOptionMenuClicked(position: Int) {

                                Log.d("RvPosition", position.toString())

                                performOptionsMenuClick(position)
                            }

                        } )


                        groupMembersRv.adapter = recycleGroupMembersAdapter
                        recycleGroupMembersAdapter.notifyDataSetChanged()
                        hideProgressView(rl_progress)
                    }, 1000)
                }
            }

            override fun onCancelled(p0: DatabaseError) {
                hideProgressView(rl_progress)
            }


        })
    }

    private fun performOptionsMenuClick(position: Int) {

        val popupMenu = androidx.appcompat.widget.PopupMenu(this , groupMembersRv.getChildAt(position).findViewById(R.id.moreOptions))

        popupMenu.inflate(R.menu.option_menu)

        //popupMenu.menu.findItem(R.id.menu_block)

        for (i in 0 until popupMenu.menu.size()) {
            val item: MenuItem = popupMenu.menu.getItem(i)
            val spanString = SpannableString(popupMenu.menu.getItem(i).getTitle().toString())
            spanString.setSpan(
                ForegroundColorSpan(Color.BLACK),
                0,
                spanString.length,
                0
            ) //fix the color to white
            item.title = spanString
        }

        popupMenu.setOnMenuItemClickListener(object : androidx.appcompat.widget.PopupMenu.OnMenuItemClickListener {
            override fun onMenuItemClick(item: MenuItem?): Boolean {

                when(item?.itemId){

                    R.id.menu_remove -> {

                        otherUserId = recycleModel.get(position).userId.toString()
                        otherUserName = recycleModel.get(position).userName.toString()
                        showDeleteLeaveGroupDialog("remove")

                        return true
                    }

                    R.id.menu_make_admin -> {

                        otherUserId = recycleModel.get(position).userId.toString()
                        otherUserName = recycleModel.get(position).userName.toString()
                        showDeleteLeaveGroupDialog("makeAdmin")

                        return true
                    }

                }
                return false
            }
        })

        popupMenu.show()

    }*/

   /* private fun transferAdmin() {

        val currentTime = System.currentTimeMillis().toString()

        val newChat = db.child("Chats").child(currentTime)

        newChat.child("msg").setValue("")
        newChat.child("img").setValue("")
        newChat.child("notice").setValue("${currentUser} make ${otherUserName} Admin")
        newChat.child("msgTime").setValue(currentTime.toString())
        newChat.child("senderId").setValue(this.userId)
        newChat.child("senderName").setValue(currentUser)
        val readDb = newChat.child("readBy").child(this.userId)
        readDb.child("read").setValue("yes")

        db.child("GroupAdmin").setValue(otherUserId).addOnSuccessListener(OnSuccessListener {

            setGroupNameAndImg()
            setUpGroupMembers()
            Toast.makeText(this@GroupChatDetailsActivity, "Admin Transferred", Toast.LENGTH_SHORT).show()
        })
    }

    private fun removeUserFromGroup(userId: String?) {

        if (userId != null) {

            val currentTime = System.currentTimeMillis().toString()

            val newChat = db.child("Chats").child(currentTime)

            newChat.child("msg").setValue("")
            newChat.child("img").setValue("")
            newChat.child("notice").setValue("${currentUser} removed ${otherUserName}")
            newChat.child("msgTime").setValue(currentTime.toString())
            newChat.child("senderId").setValue(this.userId)
            newChat.child("senderName").setValue(currentUser)
            val readDb = newChat.child("readBy").child(this.userId)
            readDb.child("read").setValue("yes")

            db.child("GroupMembers").child(userId).removeValue().addOnSuccessListener(OnSuccessListener {

                setUpGroupMembers()

                Toast.makeText(this@GroupChatDetailsActivity, "User Removed from Group", Toast.LENGTH_SHORT).show()
            })
        }

    }

    override fun onResume() {
        super.onResume()
        setGroupNameAndImg()

        var lasttrackTime = sharedPreferencesUtil.featchTimmerTime()
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
        }
    }

    private fun setGroupNameAndImg() {

        db.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snap: DataSnapshot) {

                if (snap.exists()) {

                    adminId = snap.child("GroupAdmin").value.toString()

                    if (snap.child("GroupAdmin").value.toString() == userId) {

                        leftGroupChatBtn.visibility = View.GONE
                        deleteGroupBtn.visibility = View.VISIBLE
                        editGroupImgCard.visibility = View.VISIBLE
                        editGroupName.visibility = View.VISIBLE
                        editGroupDes.visibility = View.VISIBLE
                        iAmAdmin = true

                        //adminTxt.visibility = View.VISIBLE
                    }
                    else {

                        iAmAdmin = false
                        leftGroupChatBtn.visibility = View.VISIBLE
                        deleteGroupBtn.visibility = View.GONE
                        editGroupImgCard.visibility = View.GONE
                        editGroupName.visibility = View.GONE
                        editGroupDes.visibility = View.GONE
                        //adminTxt.visibility = View.GONE
                    }

                    Glide.with(this@GroupChatDetailsActivity)
                        .load(snap.child("GroupImg").value.toString())
                        .placeholder(R.drawable.meetupsfellow_transpatent)
                        .into(groupChatImg)

                    groupName.text = snap.child("GroupName").value.toString()
                    group_des.text = snap.child("GroupDescription").value.toString()
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }

    private fun updateGroupNameDialog(type : String) {

        val dialog = Dialog(this)

        dialog.setCancelable(true)

        val view = layoutInflater.inflate(R.layout.custom_update_group_dialog, null)

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

        val groupNameEtxt = view.findViewById<TextInputEditText>(R.id.groupNameUEtxt)
        val groupDesELay = view.findViewById<TextInputLayout>(R.id.groupDesELay)
        val groupNameELay = view.findViewById<TextInputLayout>(R.id.groupNameELay)
        val groupDes = view.findViewById<TextInputEditText>(R.id.groupDestxt)
        val dialogTitle = view.findViewById<TextView>(R.id.dialog_title)
        val noExitBtn = view.findViewById<Button>(R.id.noExitUBtn)
        val yesExitBtn = view.findViewById<Button>(R.id.yesExitUBtn)

        if (type == "des") {

            dialogTitle.text = "Edit Group Description"
            groupDesELay.visibility = View.VISIBLE
            groupNameELay.visibility = View.GONE
        } else {

            dialogTitle.text = "Edit Group Name"
            groupDesELay.visibility = View.GONE
            groupNameELay.visibility = View.VISIBLE
        }

        noExitBtn.setOnClickListener {

            dialog.dismiss()
        }

        yesExitBtn.setOnClickListener {

            if (type == "des"){

                if (groupDes.text!!.isNotEmpty()) {

                    if (groupDes.text.toString() == group_des.text.toString()) {

                        dialog.dismiss()
                    } else {

                        db.child("GroupDescription").setValue(groupDes.text.toString())
                        setGroupNameAndImg()
                        dialog.dismiss()
                    }
                } else {

                    Toast.makeText(
                        this@GroupChatDetailsActivity,
                        "Group Description can not be empty",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            } else {

                if (groupNameEtxt.text!!.isNotEmpty()) {

                    if (groupNameEtxt.text!!.toString() == groupName.text.toString()) {

                        dialog.dismiss()
                    } else {

                        db.child("GroupName").setValue(groupNameEtxt.text.toString())
                        setGroupNameAndImg()
                        dialog.dismiss()
                    }

                } else {

                    Toast.makeText(
                        this@GroupChatDetailsActivity,
                        "Group name can not be empty",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        dialog.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (resultCode) {
            Activity.RESULT_OK -> {
                when (requestCode) {

                    100 -> {

                        if (data != null && data.data != null){

                            groupImgUri = data.data!!

                            //loading_groupChat.visibility = View.VISIBLE
                            showProgressView(rl_progress)

                            setAndUploadImg()

                        }
                    }
                }
            }

        }

    }

    private fun setAndUploadImg() {

        val storageRef = FirebaseStorage.getInstance().getReference("groupImgs").child(System.currentTimeMillis().toString() + ".jpg")

        val uploadTask : UploadTask = storageRef.putFile(groupImgUri)

        uploadTask.continueWithTask(object :
            Continuation<UploadTask.TaskSnapshot, Task<Uri>> {
            override fun then(task: Task<UploadTask.TaskSnapshot>): Task<Uri> {

                if (!task.isSuccessful){

                    throw task.exception!!
                }
                return storageRef.downloadUrl
            }


        }).addOnCompleteListener(object : OnCompleteListener<Uri> {
            override fun onComplete(task: Task<Uri>) {

                if (task.isSuccessful) {

                    //loading_groupChat.visibility = View.GONE
                    hideProgressView(rl_progress)
                    db.child("GroupImg").setValue(task.result.toString())

                    Glide.with(this@GroupChatDetailsActivity)
                        .load(task.result.toString())
                        .placeholder(R.drawable.meetupsfellow_transpatent)
                        .into(groupChatImg)

                }

            }
        }).addOnFailureListener(object : OnFailureListener {
            override fun onFailure(p0: Exception) {


            }
        })
    }*/
}