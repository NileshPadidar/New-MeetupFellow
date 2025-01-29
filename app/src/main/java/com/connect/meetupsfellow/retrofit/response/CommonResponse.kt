package com.connect.meetupsfellow.retrofit.response

import com.connect.meetupsfellow.mvp.view.model.ChatModel
import com.connect.meetupsfellow.mvp.view.model.ConversationModel
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Created by Jammwal on 07-03-2018.
 */
class CommonResponse : Serializable {

    @SerializedName("message", alternate = ["actionMessage"])
    @Expose
    internal val message = ""

    @SerializedName("token")
    @Expose
    internal val token = ""

    @SerializedName("connection_status")
    @Expose
    internal val connection_status = ""

    @SerializedName("access_status")
    @Expose
    internal val access_status = ""

    @SerializedName("userId")
    @Expose
    internal val userId = ""

    @SerializedName("profileStatus")
    @Expose
    internal val status = ""

    @SerializedName("isprofileprivate")
    @Expose
    internal val isprofileprivate = false

    @SerializedName("isprofilehidden")
    internal var isprofilehidden = false

    @SerializedName("isBlocked")
    @Expose
    internal val blocked = -1

    @SerializedName("code")
    @Expose
    internal val code = 0

    @SerializedName("my_followers_list")
    @Expose
    var myFollowersList: ArrayList<MyFollowFollowingListRes>? = null

    @SerializedName("my_following_list")
    @Expose
    var myFollowingList: ArrayList<MyFollowFollowingListRes>? = null

    @SerializedName("my_likeuserprofilelist")
    @Expose
    var myLikeUserProfileList: ArrayList<MyFollowFollowingListRes>? = null

    @SerializedName("notification_type_list")
    @Expose
    var notificationTypeList: ArrayList<AllTypeNotificationResponse>? = null

    @SerializedName("is_favourite")
    @Expose
    internal val is_favourite = 0

    @SerializedName("totaluserprofilelike")
    @Expose
    internal val totaluserprofilelike = 0

    @SerializedName("total_followers")
    @Expose
    internal val total_followers = 0

    @SerializedName("total_following")
    @Expose
    internal val total_following = 0

    @SerializedName("profileCreated")
    @Expose
    internal var created = -1

    @SerializedName("is_verify")
    @Expose
    internal var is_verify = -1

    @SerializedName("isShareStatus")
    @Expose
    internal val isShareStatus = 0

    @SerializedName("userData")
    @Expose
    internal val userInfo = ResponseUserData()

    @SerializedName("updatePhone")
    @Expose
    internal val updatePhone = ResponseUserData()

    @SerializedName("nearbyUsers")
    @Expose
    internal val nearbyuser = arrayListOf<ResponseUserData>()

    @SerializedName("totaluser")
    @Expose
    internal val totaluser = -1

    @SerializedName("haveNext")
    @Expose
    internal val haveNext = -1

    @SerializedName("nextPage")
    @Expose
    internal val nextPage = -1

    @SerializedName("unReadMgsCount")
    @Expose
    internal val unReadMgsCount = 0

    @SerializedName("countUnreadMgs")
    @Expose
    internal val countUnreadMgs = 0

    @SerializedName("eventInformation")
    @Expose
    internal val eventData = arrayListOf<ResponseEventFeeds>()

    @SerializedName("eventById")
    @Expose
    internal val event = ResponseEventFeeds()

    @SerializedName("settings")
    @Expose
    internal val settings = ResponseSettings()

    @SerializedName("interestedUsers")
    @Expose
    internal val interestedUsers = arrayListOf<ResponseInterestedPeople>()


    @SerializedName("my_favourite_user_list")
    @Expose
    var myFavouriteUserList: ArrayList<ResponseFavoriteUser>? = null

    @SerializedName("myprivatealbumpermission")
    @Expose
    var myprivatealbumpermission: MyprivatealbumpermissionRes? = null

    @SerializedName("total_favourite")
    @Expose
    var totalFavourite: Int? = null

    @SerializedName("favoriteEvents")
    @Expose
    internal val favoriteEvents = arrayListOf<ResponseEventFeeds>()

    @SerializedName("searchUsers")
    @Expose
    internal val search = arrayListOf<ResponseSearch>()

    @SerializedName("reason_info")
    @Expose
    internal val reason = arrayListOf<ResponseFlagReason>()

    @SerializedName("getuserblock")
    @Expose
    internal val blockedUsers = arrayListOf<ResponseBlockedUsers>()

    @SerializedName("connection_request_list")
    @Expose
    internal val getConnectionRequestList = arrayListOf<ResponseConnectionRequest>()

    @SerializedName("my_connection_request_list")
    @Expose
    internal val getMyConnectionList = arrayListOf<ResponseConnectionRequest>()

    @SerializedName("notification")
    @Expose
    internal val notification = arrayListOf<ResponseNotifications>()

    @SerializedName("chat", alternate = ["adminchat"])
    @Expose
    internal val chat = arrayListOf<ChatModel>()

    @SerializedName("adminChat")
    @Expose
    internal val adminChat = ConversationModel()

    @SerializedName("connection_data")
    @Expose
    internal val sendConnectionReqResponse = SendConnectionReqResponse()

    @SerializedName("adminChatCount")
    @Expose
    internal val adminChatCount = 0

    @SerializedName("picAccessList", alternate = ["UsersListForReceivedAlbums"])
    @Expose
    internal val picAccessList = arrayListOf<ResponsePrivateAccess>()

    @SerializedName("privatePics")
    @Expose
    internal val privatePics = arrayListOf<ResponsePrivatePics>()

    @SerializedName("adminAdvertisements")
    @Expose
    internal val adminAdvertisements = arrayListOf<ResponseUserData>()

    @SerializedName("lastnotification")
    @Expose
    internal val notificationAdimPush = arrayListOf<ResponseAdminLastPush>()

    @SerializedName("current_plans")
    @Expose
    var currentPlans = CurrentPlans()

    @SerializedName("transaction_history")
    @Expose
    var transactionHistory = arrayListOf<TransactionHistory>()

    @SerializedName("is_show")
    @Expose
    var isShow: Int? = null

    @SerializedName("visitors_lists")
    @Expose
    var visitorsLists = arrayListOf<VisitorsList>()

    @SerializedName("profile_like_user_lists")
    @Expose
    var profile_like_user_lists = arrayListOf<VisitorsList>()

    @SerializedName("following_user_lists")
    @Expose
    var following_user_lists = arrayListOf<VisitorsList>()

    @SerializedName("event_interested_user_lists")
    @Expose
    var event_interested_user_lists = arrayListOf<VisitorsList>()

    @SerializedName("event_liked_user_lists")
    @Expose
    var event_liked_user_lists = arrayListOf<VisitorsList>()

    @SerializedName("profile_favourite_user_lists")
    @Expose
    var profile_favourite_user_lists = arrayListOf<VisitorsList>()

    @SerializedName("plans")
    @Expose
    var plansList = arrayListOf<PlanList>()

}