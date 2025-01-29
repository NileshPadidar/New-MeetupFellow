package com.connect.meetupsfellow.retrofit.api

import com.connect.meetupsfellow.retrofit.request.CancelSendConnectionRequest
import com.connect.meetupsfellow.retrofit.request.CheckSentEmailCode
import com.connect.meetupsfellow.retrofit.request.EnableDisableNotificationReq
import com.connect.meetupsfellow.retrofit.request.FollowUnfollowUserReq
import com.connect.meetupsfellow.retrofit.request.ForgetPasswordReq
import com.connect.meetupsfellow.retrofit.request.LoginWithEmailRequest
import com.connect.meetupsfellow.retrofit.request.MyFavoriteUserReq
import com.connect.meetupsfellow.retrofit.request.ReqPrivetUserProfile
import com.connect.meetupsfellow.retrofit.request.RequestAdminAdsCountClick
import com.connect.meetupsfellow.retrofit.request.RequestChatSave
import com.connect.meetupsfellow.retrofit.request.RequestClearBadge
import com.connect.meetupsfellow.retrofit.request.RequestClearChat
import com.connect.meetupsfellow.retrofit.request.RequestConnectionRequest
import com.connect.meetupsfellow.retrofit.request.RequestDeactivateAccount
import com.connect.meetupsfellow.retrofit.request.RequestDeleteAccount
import com.connect.meetupsfellow.retrofit.request.RequestFeed
import com.connect.meetupsfellow.retrofit.request.RequestReadNotification
import com.connect.meetupsfellow.retrofit.request.RequestReportEvent
import com.connect.meetupsfellow.retrofit.request.RequestReportUser
import com.connect.meetupsfellow.retrofit.request.RequestResetPassword
import com.connect.meetupsfellow.retrofit.request.RequestSendNotification
import com.connect.meetupsfellow.retrofit.request.RequestUpdateLocation
import com.connect.meetupsfellow.retrofit.request.RequestUpdateNumber
import com.connect.meetupsfellow.retrofit.request.RequestUserAuth
import com.connect.meetupsfellow.retrofit.request.RequestUserBlock
import com.connect.meetupsfellow.retrofit.request.RequestUserPayment
import com.connect.meetupsfellow.retrofit.request.SaveContactDetailsReq
import com.connect.meetupsfellow.retrofit.request.SendConnectionRequest
import com.connect.meetupsfellow.retrofit.request.UnfriendConnectionReq
import com.connect.meetupsfellow.retrofit.request.getConectionRequest
import com.connect.meetupsfellow.retrofit.request.setAccessPrivateAlbumPermissionReq
import com.connect.meetupsfellow.retrofit.response.CommonResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import rx.Observable

/**
 * Created by Maheshwar on 12/16/2015.
 * Update by Nilu on 12-12-24
 */
@JvmSuppressWildcards
interface Api {

    @Headers("content-type:application/json", "Accept:application/json")
    @POST("auth/login")
    fun userLoginWithMobile(@Body body: RequestUserAuth): Observable<Response<CommonResponse>>

    @Headers("content-type:application/json", "Accept:application/json")
    @POST("auth/loginwithpassword")
    fun userLoginWithEmail(@Body body: LoginWithEmailRequest): Observable<Response<CommonResponse>>

    @Headers("content-type:application/json", "Accept:application/json")
    @POST("auth/forgetpassword")
    fun userForgetPassword(@Body body: ForgetPasswordReq): Observable<Response<CommonResponse>>

    @Headers("content-type:application/json", "Accept:application/json")
    @POST("resendemailverificationlink")
    fun sendVerifyEmailLink(@Body body: ForgetPasswordReq): Observable<Response<CommonResponse>>

    @Headers("content-type:application/json", "Accept:application/json")
    @POST("auth/checkresetcode")
    fun checkUserCode(@Body body: CheckSentEmailCode): Observable<Response<CommonResponse>>

    @Headers("content-type:application/json", "Accept:application/json")
    @POST("auth/resetpassword")
    fun userResetPassword(@Body body: RequestResetPassword): Observable<Response<CommonResponse>>

    @Headers("content-type:application/json", "Accept:application/json")
    @POST("auth/registration")
    fun userAuth(@Body body: RequestUserAuth): Observable<Response<CommonResponse>>

    @Headers("Content-Type:application/json", "Accept:application/json")
    @GET("profile")
    fun getUserProfile(
        @Header("Authorization") auth: String, @Query("rand") rand: String, @Query("t") t: String
    ): Observable<Response<CommonResponse>>

    @Headers("content-type:application/json", "Accept:application/json")
    @POST("events")
    fun eventFeeds(
        @Header("Authorization") auth: String, @Body body: RequestFeed
    ): Observable<Response<CommonResponse>>

    @Headers("content-type:application/json", "Accept:application/json")
    @POST("getNearbyUsers")
    fun nearbyUsers(
        @Header("Authorization") auth: String, @Body body: RequestFeed
    ): Observable<Response<CommonResponse>>

    @Headers("content-type:application/json", "Accept:application/json")
    @POST("getMostRecentUser")
    fun mostRecentUsers(
        @Header("Authorization") auth: String, @Body body: RequestFeed
    ): Observable<Response<CommonResponse>>


    @Headers("content-type:application/json", "Accept:application/json")
    @POST("updateUserTimeStamp")
    fun updateUserTimeStamp(
        @Header("Authorization") auth: String, @Body body: RequestFeed
    ): Observable<Response<CommonResponse>>

    @Headers("content-type:application/json", "Accept:application/json")
    @GET("auth/logout")
    fun logout(
        @Header("Authorization") auth: String, @Query("rand") rand: String
    ): Observable<Response<CommonResponse>>

    @Headers("content-type:application/json", "Accept:application/json")
    @GET("user/setting")
    fun settings(
        @Header("Authorization") auth: String, @Query("rand") rand: String
    ): Observable<Response<CommonResponse>>

    @Headers("content-type:application/json", "Accept:application/json")
    @POST("userprofileprivate")
    fun mackProfilePrivet(
        @Header("Authorization") auth: String, @Body body: ReqPrivetUserProfile
    ): Observable<Response<CommonResponse>>

    @Headers("content-type:application/json", "Accept:application/json")
    @GET("interestedUsers/{id}")
    fun interestedPeople(
        @Path("id") id: String, @Header("Authorization") auth: String, @Query("rand") rand: String
    ): Observable<Response<CommonResponse>>

    @Headers("content-type:application/json", "Accept:application/json")
    @GET("event/like/{id}")
    fun likeEvent(
        @Path("id") id: String, @Header("Authorization") auth: String, @Query("rand") rand: String
    ): Observable<Response<CommonResponse>>

    @Headers("content-type:application/json", "Accept:application/json")
    @GET("event/interested/{id}")
    fun interestedEvent(
        @Path("id") id: String, @Header("Authorization") auth: String, @Query("rand") rand: String
    ): Observable<Response<CommonResponse>>

    @Headers("content-type:application/json", "Accept:application/json")
    @POST("user/favorite")
    fun favoriteUser(
        @Header("Authorization") auth: String, @Body body: RequestFeed
    ): Observable<Response<CommonResponse>>

    @Headers("content-type:application/json", "Accept:application/json")
    @POST("favouriteunfavouriteuser")
    fun favouriteunfavouriteuser(
        @Header("Authorization") auth: String, @Body body: MyFavoriteUserReq
    ): Observable<Response<CommonResponse>>

    @Headers("content-type:application/json", "Accept:application/json")
    @POST("saveusercontactdetails")
    fun saveusercontactdetails(
        @Header("Authorization") auth: String, @Body body: SaveContactDetailsReq
    ): Observable<Response<CommonResponse>>

    @Headers("content-type:application/json", "Accept:application/json")
    @GET("myfavouriteuserlist")
    fun myFavoriteUser(
        @Header("Authorization") auth: String,
    ): Observable<Response<CommonResponse>>

    @Headers("content-type:application/json", "Accept:application/json")
    @GET("myfollowinglist")
    fun myFollowingList(
        @Header("Authorization") auth: String,
    ): Observable<Response<CommonResponse>>

    @Headers("content-type:application/json", "Accept:application/json")
    @GET("myfollowerslist")
    fun myFollowerList(
        @Header("Authorization") auth: String,
    ): Observable<Response<CommonResponse>>

    @Headers("content-type:application/json", "Accept:application/json")
    @GET("mylikeuserprofilelist")
    fun myLikeUserList(
        @Header("Authorization") auth: String,
    ): Observable<Response<CommonResponse>>

    @Headers("content-type:application/json", "Accept:application/json")
    @POST("event/favorite")
    fun favoriteEvent(
        @Header("Authorization") auth: String, @Body body: RequestFeed
    ): Observable<Response<CommonResponse>>

    @Headers("content-type:application/json", "Accept:application/json")
    @GET("profile/{id}")
    fun userProfile(
        @Path("id") id: String, @Header("Authorization") auth: String, @Query("rand") rand: String
    ): Observable<Response<CommonResponse>>

    @Headers("content-type:application/json", "Accept:application/json")
    @GET("user/like/{id}")
    fun likeUser(
        @Path("id") id: String, @Header("Authorization") auth: String, @Query("rand") rand: String
    ): Observable<Response<CommonResponse>>

    @Headers("content-type:application/json", "Accept:application/json")
    @GET("likeUserProflie/{id}")
    fun likeUserProfileMultipleTime(
        @Path("id") id: String, @Header("Authorization") auth: String, @Query("rand") rand: String
    ): Observable<Response<CommonResponse>>

    @Headers("content-type:application/json", "Accept:application/json")
    @GET("likeUserProflie/{id}/{type_id}")
    fun likeUserProfileHeart(
        @Path("id") id: String,
        @Path("type_id") type_id: String,
        @Header("Authorization") auth: String,
        @Query("rand") rand: String
    ): Observable<Response<CommonResponse>>


    @Headers("content-type:application/json", "Accept:application/json")
    @GET("user/setting/allowPush/{id}")
    fun allowPush(
        @Path("id") id: Int, @Header("Authorization") auth: String, @Query("rand") rand: String
    ): Observable<Response<CommonResponse>>

    @Headers("content-type:application/json", "Accept:application/json")
    @GET("user/setting/imperial_unit/{id}")
    fun unitSystem(
        @Path("id") id: Int, @Header("Authorization") auth: String, @Query("rand") rand: String
    ): Observable<Response<CommonResponse>>

    @Headers("content-type:application/json", "Accept:application/json")
    @GET("user/setting/allowEvent/{id}")
    fun allowEvent(
        @Path("id") id: Int, @Header("Authorization") auth: String, @Query("rand") rand: String
    ): Observable<Response<CommonResponse>>

    @Headers("content-type:application/json", "Accept:application/json")
    @GET("user/setting/allowEventUpdate/{id}")
    fun allowEventUpdate(
        @Path("id") id: Int, @Header("Authorization") auth: String, @Query("rand") rand: String
    ): Observable<Response<CommonResponse>>


    @Headers("content-type:application/json", "Accept:application/json")
    @GET("user/setting/nsfw/{value}")
    fun allowNsfw(
        @Path("value") id: Int, @Header("Authorization") auth: String, @Query("rand") rand: String
    ): Observable<Response<CommonResponse>>


    @Headers("content-type:application/json", "Accept:application/json")
    @POST("user/updatelocation")
    fun updateLocation(
        @Header("Authorization") auth: String, @Body requestUpdateLocation: RequestUpdateLocation
    ): Observable<Response<CommonResponse>>

    @Headers("content-type:application/json", "Accept:application/json")
    @POST("disablenotificationtype")
    fun enableDisableNotifications(
        @Header("Authorization") auth: String,
        @Body enableDisableNotificationReq: EnableDisableNotificationReq
    ): Observable<Response<CommonResponse>>

    @Headers("content-type:application/json", "Accept:application/json")
    @GET("notificationtypelist")
    fun getAllTypeNotifications(
        @Header("Authorization") auth: String
    ): Observable<Response<CommonResponse>>


    @Headers("content-type:application/json", "Accept:application/json")
    @POST("user/updatePhone")
    fun updateNumber(
        @Header("Authorization") auth: String, @Body requestUpdateNumber: RequestUpdateNumber
    ): Observable<Response<CommonResponse>>

    @Headers("content-type:application/json", "Accept:application/json")
    @POST("getNearbyUsers")
    fun search(
        @Header("Authorization") auth: String, @Body requestFeed: RequestFeed
    ): Observable<Response<CommonResponse>>

    @Headers("content-type:application/json", "Accept:application/json")
    @POST("searchEvent")
    fun searchEvents(
        @Header("Authorization") auth: String, @Body requestFeed: RequestFeed
    ): Observable<Response<CommonResponse>>

    @Headers("content-type:application/json", "Accept:application/json")
    @GET("event/{id}")
    fun event(
        @Path("id") id: String, @Header("Authorization") auth: String, @Query("rand") rand: String
    ): Observable<Response<CommonResponse>>

    @Headers("content-type:application/json", "Accept:application/json")
    @GET("reason")
    fun reasons(
        @Header("Authorization") auth: String, @Query("rand") rand: String
    ): Observable<Response<CommonResponse>>

    @Headers("content-type:application/json", "Accept:application/json")
    @POST("reportEvent")
    fun reportEvent(
        @Header("Authorization") auth: String, @Body requestReportEvent: RequestReportEvent
    ): Observable<Response<CommonResponse>>

    @Headers("content-type:application/json", "Accept:application/json")
    @GET("getuserblock")
    fun getBlockedUsers(
        @Header("Authorization") auth: String, @Query("rand") rand: String
    ): Observable<Response<CommonResponse>>

    @Headers("content-type:application/json", "Accept:application/json")
    @POST("userblock")
    fun blockUser(
        @Header("Authorization") auth: String, @Body requestUserBlock: RequestUserBlock
    ): Observable<Response<CommonResponse>>

    @Headers("content-type:application/json", "Accept:application/json")
    @POST("sendconnectionrequest")
    fun SendConnetionRequest(
        @Header("Authorization") auth: String, @Body sendConnectionRequest: SendConnectionRequest
    ): Observable<Response<CommonResponse>>

    @Headers("content-type:application/json", "Accept:application/json")
    @POST("getconnectionrequestlist")
    fun getConnectionRequest(
        @Header("Authorization") auth: String, @Body getConectionRequest: getConectionRequest
    ): Observable<Response<CommonResponse>>

    @Headers("content-type:application/json", "Accept:application/json")
    @GET("myconnectionrequestlist")
    fun getMyConnectionsRequest(
        @Header("Authorization") auth: String,
    ): Observable<Response<CommonResponse>>


    @Headers("content-type:application/json", "Accept:application/json")
    @POST("rejectsentconnectionrequest")
    fun declineConnectionRequest(
        @Header("Authorization") auth: String,
        @Body requestConnectionRequest: RequestConnectionRequest
    ): Observable<Response<CommonResponse>>

    @Headers("content-type:application/json", "Accept:application/json")
    @POST("cancelsentconnectionrequest")
    fun cancelSendConnectionRequest(
        @Header("Authorization") auth: String,
        @Body cancelSendConnectionRequest: CancelSendConnectionRequest
    ): Observable<Response<CommonResponse>>

    @Headers("content-type:application/json", "Accept:application/json")
    @POST("unfriendconnection")
    fun unfriendConnectionRequest(
        @Header("Authorization") auth: String, @Body unfriendConnectionReq: UnfriendConnectionReq
    ): Observable<Response<CommonResponse>>

    @Headers("content-type:application/json", "Accept:application/json")
    @POST("acceptsentconnectionrequest")
    fun acceptConnectionRequest(
        @Header("Authorization") auth: String,
        @Body requestConnectionRequest: RequestConnectionRequest
    ): Observable<Response<CommonResponse>>

    @Headers("content-type:application/json", "Accept:application/json")
    @GET("getnotification")
    fun getNotifications(
        @Header("Authorization") auth: String, @Query("rand") rand: String
    ): Observable<Response<CommonResponse>>

    @Headers("content-type:application/json", "Accept:application/json")
    @POST("reportUser")
    fun reportUser(
        @Header("Authorization") auth: String, @Body requestReportUser: RequestReportUser
    ): Observable<Response<CommonResponse>>

    @Headers("content-type:application/json", "Accept:application/json")
    @POST("notificationRead")
    fun readNotification(
        @Header("Authorization") auth: String,
        @Body requestReadNotification: RequestReadNotification
    ): Observable<Response<CommonResponse>>

    @Headers("content-type:application/json", "Accept:application/json")
    @POST("getProfileVisitorsList")
    fun getProfileVisitorList(
        @Header("Authorization") auth: String,
        @Body requestReadNotification: RequestReadNotification
    ): Observable<Response<CommonResponse>>

    @Headers("content-type:application/json", "Accept:application/json")
    @POST("getprofileFavouriteUserList")
    fun getFavouriteList(
        @Header("Authorization") auth: String,
        @Body requestReadNotification: RequestReadNotification
    ): Observable<Response<CommonResponse>>

    @Headers("content-type:application/json", "Accept:application/json")
    @POST("geteventinterestedUserList")
    fun getEventInterestUserList(
        @Header("Authorization") auth: String,
        @Body requestReadNotification: RequestReadNotification
    ): Observable<Response<CommonResponse>>

    @Headers("content-type:application/json", "Accept:application/json")
    @POST("geteventlikedUserList")
    fun getEventLikeUserList(
        @Header("Authorization") auth: String,
        @Body requestReadNotification: RequestReadNotification
    ): Observable<Response<CommonResponse>>

    @Headers("content-type:application/json", "Accept:application/json")
    @POST("getProfilelikeUserList")
    fun getUserLikeList(
        @Header("Authorization") auth: String,
        @Body requestReadNotification: RequestReadNotification
    ): Observable<Response<CommonResponse>>

    @Headers("content-type:application/json", "Accept:application/json")
    @POST("notificationDelete")
    fun deleteNotification(
        @Header("Authorization") auth: String,
        @Body requestReadNotification: RequestReadNotification
    ): Observable<Response<CommonResponse>>

    @Headers("content-type:application/json", "Accept:application/json")
    @POST("getfollowuserlistnotification")
    fun getFollowUserListNotification(
        @Header("Authorization") auth: String,
        @Body requestReadNotification: RequestReadNotification
    ): Observable<Response<CommonResponse>>

    @Headers("content-type:application/json", "Accept:application/json")
    @POST("notificationClear")
    fun clearNotification(
        @Header("Authorization") auth: String
    ): Observable<Response<CommonResponse>>

    @Headers("content-type:application/json", "Accept:application/json")
    @POST("accountDeactive")
    fun deactivateAccount(
        @Header("Authorization") auth: String,
        @Body requestDeactivateAccount: RequestDeactivateAccount
    ): Observable<Response<CommonResponse>>

    @Headers("content-type:application/json", "Accept:application/json")
    @POST("accountDelete")
    fun deleteAccount(
        @Header("Authorization") auth: String, @Body requestDeleteAccount: RequestDeleteAccount
    ): Observable<Response<CommonResponse>>

    @Headers("content-type:application/json", "Accept:application/json")
    @POST("chatSave")
    fun saveChat(
        @Header("Authorization") auth: String, @Body requestChatSave: RequestChatSave
    ): Observable<Response<CommonResponse>>

    @Headers("content-type:application/json", "Accept:application/json")
    @GET("chat/{id}")
    fun fetchChat(
        @Path("id") id: String, @Header("Authorization") auth: String, @Query("rand") rand: String
    ): Observable<Response<CommonResponse>>

    @Headers("content-type:application/json", "Accept:application/json")
    @POST("clearChat")
    fun clearChat(
        @Header("Authorization") auth: String, @Body requestClearChat: RequestClearChat
    ): Observable<Response<CommonResponse>>

    @Headers("content-type:application/json", "Accept:application/json")
    @POST("sendChatNotifications")
    fun sendNotification(
        @Header("Authorization") auth: String,
        @Body requestSendNotification: RequestSendNotification
    ): Observable<Response<CommonResponse>>

    @Headers("content-type:application/json", "Accept:application/json")
    @POST("clearChatBadge")
    fun clearBadge(
        @Header("Authorization") auth: String, @Body requestClearBadge: RequestClearBadge
    ): Observable<Response<CommonResponse>>

    @Headers("content-type:application/json", "Accept:application/json")
    @POST("followtouser")
    fun followToUser(
        @Header("Authorization") auth: String, @Body followUnfollowUserReq: FollowUnfollowUserReq
    ): Observable<Response<CommonResponse>>

    @Headers("content-type:application/json", "Accept:application/json")
    @POST("unfollowuser")
    fun unFollowUser(
        @Header("Authorization") auth: String, @Body followUnfollowUserReq: FollowUnfollowUserReq
    ): Observable<Response<CommonResponse>>

    @Headers("content-type:application/json", "Accept:application/json")
    @POST("setaccesspermissionforprivatealbum")
    fun setAccessPermissionForPrivateAlbum(
        @Header("Authorization") auth: String,
        @Body setAccessPrivateAlbumPermissionReq: setAccessPrivateAlbumPermissionReq
    ): Observable<Response<CommonResponse>>

    @Headers("content-type:application/json", "Accept:application/json")
    @GET("getPicAccessList")
    fun fetchAccessList(
        @Header("Authorization") auth: String, @Query("rand") rand: String
    ): Observable<Response<CommonResponse>>

    @Headers("content-type:application/json", "Accept:application/json")
    @GET("myprivatealbumpermission")
    fun myPrivateAlbumPermission(
        @Header("Authorization") auth: String,
    ): Observable<Response<CommonResponse>>

    @Headers("content-type:application/json", "Accept:application/json")
    @GET("removeAllUserFromAccessList")
    fun removeAccessList(
        @Header("Authorization") auth: String, @Query("rand") rand: String
    ): Observable<Response<CommonResponse>>

    @Headers("content-type:application/json", "Accept:application/json")
    @GET("getUsersListForReceivedAlbums")
    fun fetchAccessListOthers(
        @Header("Authorization") auth: String, @Query("rand") rand: String
    ): Observable<Response<CommonResponse>>

    @Headers("content-type:application/json", "Accept:application/json")
    @POST("sharePrivatePicByUserId/{id}")
    fun privateAccess(
        @Path("id") id: String, @Header("Authorization") auth: String, @Query("rand") rand: String
    ): Observable<Response<CommonResponse>>

    @Headers("content-type:application/json", "Accept:application/json")
    @GET("getPrivatePic")
    fun getPrivateAlbum(
        @Header("Authorization") auth: String, @Query("rand") rand: String
    ): Observable<Response<CommonResponse>>

    @Headers("content-type:application/json", "Accept:application/json")
    @GET("getPrivatePicByUserId/{id}")
    fun getPrivateAlbumById(
        @Path("id") id: String, @Header("Authorization") auth: String, @Query("rand") rand: String
    ): Observable<Response<CommonResponse>>

    @Headers("content-type:application/json", "Accept:application/json")
    @GET("deletePrivatePic/{id}")
    fun deletePrivateAlbumById(
        @Path("id") id: String, @Header("Authorization") auth: String, @Query("rand") rand: String
    ): Observable<Response<CommonResponse>>

    @Headers("content-type:application/json", "Accept:application/json")
    @GET("adminChat")
    fun fetchAdminConversation(
        @Header("Authorization") auth: String, @Query("rand") rand: String
    ): Observable<Response<CommonResponse>>

    @Headers("content-type:application/json", "Accept:application/json")
    @GET("chat/{id}")
    fun fetchAdminChat(
        @Path("id") id: String, @Header("Authorization") auth: String, @Query("rand") rand: String
    ): Observable<Response<CommonResponse>>

    /*  @Headers("content-type:application/json", "Accept:application/json")
      @POST("userpayment")
      fun addUserPayment(
          @Header("Authorization") auth: String, @Body requestDeleteAccount: RequestUserPayment
      ): Observable<Response<CommonResponse>>
  */
    @Headers("content-type:application/json", "Accept:application/json")
    @POST("membershipplanspurchase")
    fun addUserPayment(
        @Header("Authorization") auth: String, @Body requestDeleteAccount: RequestUserPayment
    ): Observable<Response<CommonResponse>>


    @Headers("Content-Type:application/json", "Accept:application/json")
    @GET("transaction_history")
    fun getTransectionHistory(
        @Header("Authorization") auth: String

    ): Observable<Response<CommonResponse>>

    @Headers("Content-Type:application/json", "Accept:application/json")
    @GET("subscriptionplans")
    fun getPlanList(
        @Header("Authorization") auth: String

    ): Observable<Response<CommonResponse>>

    @Headers("Content-Type:application/json", "Accept:application/json")
    @GET("getAdminAds")
    fun getAdminAds(
        @Header("Authorization") auth: String

    ): Observable<Response<CommonResponse>>

    @Headers("Content-Type:application/json", "Accept:application/json")
    @GET("getlastnotification")
    fun getAdminLastPush(
        @Header("Authorization") auth: String
    ): Observable<Response<CommonResponse>>


    @Headers("content-type:application/json", "Accept:application/json")
    @POST("updateAdsCount")
    fun updateAdsCount(
        @Header("Authorization") auth: String, @Body AdminAdsCountClick: RequestAdminAdsCountClick
    ): Observable<Response<CommonResponse>>


    @Headers("content-type:application/json", "Accept:application/json")
    @POST("notificationReadAdminChat")
    fun notificationReadAdminChat(
        @Header("Authorization") auth: String
    ): Observable<Response<CommonResponse>>


}