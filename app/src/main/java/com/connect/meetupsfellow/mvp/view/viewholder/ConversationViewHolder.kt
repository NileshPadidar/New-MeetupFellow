package com.connect.meetupsfellow.mvp.view.viewholder

import android.content.Intent
import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.connect.meetupsfellow.R
import com.connect.meetupsfellow.application.ArchitectureApp
import com.connect.meetupsfellow.constants.Constants
import com.connect.meetupsfellow.constants.ItemClickStatus
import com.connect.meetupsfellow.databinding.ItemConversationBinding
import com.connect.meetupsfellow.developement.interfaces.ApiConnect
import com.connect.meetupsfellow.developement.interfaces.SharedPreferencesUtil
import com.connect.meetupsfellow.global.utils.DisplayImage
import com.connect.meetupsfellow.global.utils.RecyclerViewClick
import com.connect.meetupsfellow.log.LogManager
import com.connect.meetupsfellow.mvp.view.model.ConversationModel
import com.connect.meetupsfellow.retrofit.response.CommonResponse
import com.connect.meetupsfellow.retrofit.response.ResponseUserData
import com.daimajia.swipe.SwipeLayout
import com.google.gson.Gson
import retrofit2.Response
import rx.Observer
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.util.Calendar
import javax.inject.Inject


class ConversationViewHolder(private val binding: ItemConversationBinding) :
    RecyclerView.ViewHolder(binding.root) {

    @Inject
    lateinit var sharedPreferencesUtil: SharedPreferencesUtil

    @Inject
    lateinit var apiConnect: ApiConnect


    private var profile: ResponseUserData? = null

    init {

        ArchitectureApp.component!!.inject(this@ConversationViewHolder)

        binding.ivDelete.setOnClickListener {
            if (null != RecyclerViewClick.onClickListener) {
                RecyclerViewClick.onClickListener!!.onItemClick(
                    adapterPosition, ItemClickStatus.ConversationDelete
                )
                binding.swipe.close(true, true)
            }
        }

        binding.includedConversation.ivUserImage.setOnClickListener {
            if (conversationModel.convoId == Constants.Admin.Convo || conversationModel.otherUserId == Constants.Admin.ConvoAdminOneToOne) {
                return@setOnClickListener
            }
            when (conversationModel.blockedBY.trim().isEmpty()) {
                true -> {
                    when (status) {
                        Constants.Profile.Deactivate -> universalToast(
                            String.format(
                                itemView.context.getString(R.string.text_deactivated),
                                conversationModel.otherUserName
                            )
                        )

                        Constants.Profile.Delete -> universalToast(
                            String.format(
                                itemView.context.getString(R.string.text_deleted),
                                conversationModel.otherUserName
                            )
                        )

                        else -> {
                            if (conversationModel.otherUserId.trim()
                                    .isNotEmpty()
                            ) itemView.context.startActivity(
                                Intent(Constants.Intent.ProfileUser).putExtras(Bundle().apply {
                                    putInt(
                                        Constants.IntentDataKeys.UserId,
                                        conversationModel.otherUserId.toInt()
                                    )
                                })
                            )
                        }
                    }

                }

                false -> {
                    when (conversationModel.blockedBY.trim()) {
                        sharedPreferencesUtil.userId() -> universalToast(
                            String.format(
                                itemView.context.getString(R.string.text_you_block),
                                conversationModel.otherUserName
                            )
                        )

                        else -> universalToast(
                            String.format(
                                itemView.context.getString(R.string.text_blocked_you),
                                conversationModel.otherUserName
                            )
                        )
                    }
                }
            }

        }

        binding.includedConversation.llConversation.setOnClickListener {
            itemView.context.startActivity(Intent(Constants.Intent.Chat).putExtras(Bundle().apply {
                putString(Constants.IntentDataKeys.Conversation, Gson().toJson(conversationModel))
                Log.d("OtherUserID", conversationModel.otherUserId)
                Log.d("OtherUserIDS", conversationModel.chatSenderId)
                putString(Constants.IntentDataKeys.UserImage, imageUrl)
                putInt(Constants.IntentDataKeys.Status, status)
                //  putString(Constants.IntentDataKeys.UserImage,
                //   if (profile!!.images.isNotEmpty()) profile!!.images[0].imageThumb else "")
            }))
        }
    }

    private lateinit var conversationModel: ConversationModel

    private var imageUrl = ""
    private var onlineStatus = false
    private var status = 1

    internal fun bind(conversationModel: ConversationModel) {
        this.conversationModel = conversationModel
        // Access the included layout views
        LogManager.logger.i(
            ArchitectureApp.instance!!.tag,
            "Profile Status is conversationModel :: ${Gson().toJson(conversationModel)}"
        )
        binding.swipe.showMode = SwipeLayout.ShowMode.PullOut

        binding.swipe.isRightSwipeEnabled = adapterPosition > 1

        //  binding.admin_space.visibility = View.GONE

        if (conversationModel.convoId == Constants.Admin.Convo) {
            // itemView.llConversation.paddingBottom = 10
            //  itemView.admin_space.visibility = View.VISIBLE
            /*val params = itemView.layoutParams as LinearLayout.LayoutParams
            params.bottomMargin = 100
            itemView.llConversation = params*/

            //  itemView.llConversation.paddingBottom =10

        }


        /*if(conversationModel.otherUserId == Constants.Admin.ConvoAdminOneToOne || conversationModel.convoId == Constants.Admin.Convo ){
            itemView.llConversation.setBackgroundColor(itemView.context.resources
                .getColor(R.color.colorLoading))
        }
*/
        binding.swipe.isClickToClose = true
        binding.includedConversation.tvUserName.text = conversationModel.otherUserName
        binding.includedConversation.tvLastMessage.text = when {
            conversationModel.userTyping -> "typing..."
            sharedPreferencesUtil.fetchUserChat(conversationModel.convoId)
                .isNotEmpty() -> getLastMessage(
                conversationModel.chatMessage.trim(), conversationModel.convoId
            )

            else -> conversationModel.chatMessage.trim()
        }

        binding.includedConversation.senderNameGChat.visibility = View.GONE

        //  val other = conversationModel!!.otherUserId
        //conversationModel.lastMessagetime = if (other == "3") Math.abs(conversationModel.lastMessagetime)  else conversationModel.lastMessagetime

        //  conversationModel.lastMessagetime = if (conversationModel.lastMessagetime < 0 )  Math.abs(conversationModel.lastMessagetime)  else conversationModel.lastMessagetime

        conversationModel.lastMessagetime = Math.abs(conversationModel.lastMessagetime)

        if (conversationModel.otherUserId == Constants.Admin.ConvoAdminOneToOne && conversationModel.otherUserImageUrl == "Reegur_00120_test") {
            binding.includedConversation.tvTimeAgo.visibility = View.GONE
        } else {
            binding.includedConversation.tvTimeAgo.visibility = View.VISIBLE
            binding.includedConversation.tvTimeAgo.text =
                setFormattedDate(conversationModel.lastMessagetime)
        }


        when (conversationModel.convoId) {
            Constants.Admin.Convo -> showCircleImage(
                binding.includedConversation.ivUserImage, conversationModel.otherUserImageUrl
            )

            Constants.Admin.ConvoAdminOneToOne -> showCircleImage(
                binding.includedConversation.ivUserImage, conversationModel.otherUserImageUrl
            )


            else -> fetchUserProfileImage(conversationModel.otherUserId)
        }

        showCircleImage(binding.includedConversation.ivUserImageBackground, "")
        //  showCircleImage(itemView.ivUserImage, "")

//        Log.e("m+++"+ adapterPosition, conversationModel.otherUserImageUrl +"  * " + conversationModel.otherUserName)

        binding.includedConversation.tvUnReadCount.visibility = View.GONE
        binding.includedConversation.tvUnReadCount.text = ""
        when (conversationModel.unReadCount.isNotEmpty()) {
            true -> {
                when (conversationModel.unReadCount.trim().toInt() > 0) {
                    true -> {
                        binding.includedConversation.tvUnReadCount.visibility = View.VISIBLE
                        binding.includedConversation.tvUnReadCount.text =
                            conversationModel.unReadCount
                    }

                    false -> {
                        binding.includedConversation.tvUnReadCount.visibility = View.GONE
                        binding.includedConversation.tvUnReadCount.text = ""
                    }
                }
            }

            false -> {
                binding.includedConversation.tvUnReadCount.visibility = View.GONE
                binding.includedConversation.tvUnReadCount.text = ""
            }
        }

    }

    private fun getLastMessage(message: String, convoId: String): String {
        return when (message.isNotEmpty()) {
            true -> message
            false -> {
                sharedPreferencesUtil.fetchUserChat(convoId)[sharedPreferencesUtil.fetchUserChat(
                    convoId
                ).size - 1].chatMessage
            }
        }
    }

    private fun fetchUserProfileImage(userId: String) {


        LogManager.logger.i(ArchitectureApp.instance!!.tag, "Profile Status is userId :: $userId")
        if (userId.isEmpty()) {
            showCircleImage(binding.includedConversation.ivUserImage, imageUrl)
            return
        }

        val profile = sharedPreferencesUtil.fetchOtherUserProfile(userId)
        LogManager.logger.i(
            ArchitectureApp.instance!!.tag, "Profile Status is :: ${profile.status}"
        )
        LogManager.logger.i(
            ArchitectureApp().tag, "DataSnapshot is snapshot Profile : ${Gson().toJson(profile)}"
        )

        //  imageUrl = callForUserProfile(userId);
        imageUrl = profile.imgUrl
        status = if (profile.userId == -1) profile.userId else profile.status

        onlineStatus = profile.onlineStatus
        conversationModel.otherUserOnline = onlineStatus
//        itemView.ivUserStatus.isSelected = onlineStatus


        showCircleImage(binding.includedConversation.ivUserImage, imageUrl)

    }

    private fun showCircleImage(imageView: ImageView, imageUrl: String) {
        DisplayImage.with(itemView.context).load(imageUrl)
            .placeholder(if (conversationModel.convoId == Constants.Admin.Convo || conversationModel.convoId == Constants.Admin.ConvoAdminOneToOne || conversationModel.otherUserId == Constants.Admin.ConvoAdminOneToOne) R.drawable.meetupsfellow_transpatent else R.drawable.meetupsfellow_transpatent)
            .shape(DisplayImage.Shape.CropSquare).into(imageView).build()
    }

    private fun universalToast(message: String) {
        //Toast.makeText(itemView.context, message, Toast.LENGTH_SHORT).show()
    }

    private fun setFormattedDate(smsTimeInMilis: Long): String {

        val smsTime = Calendar.getInstance()
        smsTime.timeInMillis = smsTimeInMilis

        val now = Calendar.getInstance()

        val timeFormatString = "h:mm aa"

        return when {
            now.get(Calendar.DATE) == smsTime.get(Calendar.DATE) -> "Today " + DateFormat.format(
                timeFormatString, smsTime
            )

            now.get(Calendar.DATE) - smsTime.get(Calendar.DATE) == 1 -> "Yesterday " + DateFormat.format(
                timeFormatString, smsTime
            )

            now.get(Calendar.YEAR) == smsTime.get(Calendar.YEAR) -> DateFormat.format(
                "dd MMMM yyyy",
                smsTime
            ).toString()

            else -> DateFormat.format("dd MMMM yyyy", smsTime).toString()
        }
    }


    private fun callForUserProfile(userId: String): String {  // no use

        //  val user = sharedPreferencesUtil.fetchUserProfile()
        var imagees: String = ""

        try {
            val people = apiConnect.api.userProfile(
                userId, sharedPreferencesUtil.loginToken(), Constants.Random.random()
            )

            people.subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<Response<CommonResponse>> {
                    override fun onError(e: Throwable?) {
                        e!!.printStackTrace()
                        //returnMessage("${e.message}")
                    }

                    override fun onNext(response: Response<CommonResponse>?) {
                        LogManager.logger.i(
                            ArchitectureApp.instance!!.tag, "Response is ::: $response"
                        )
                        if (response != null) {
                            when (response.isSuccessful) {
                                true -> {
                                    val responses: CommonResponse? = response.body()
                                    val profile: ResponseUserData? = responses?.userInfo
                                    imagees = profile!!.images[0].toString()
                                }

                                false -> {
                                    // returnError(response.errorBody()!!.charStream())
                                }
                            }
                        } else {
                            //  returnMessage(ArchitectureApp.instance!!.getString(R.string.error_title))
                        }
                    }

                    override fun onCompleted() {
                    }
                })
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return imagees
    }

}