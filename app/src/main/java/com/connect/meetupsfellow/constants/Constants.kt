package com.connect.meetupsfellow.constants

import android.Manifest
import android.app.Activity
import com.google.android.libraries.places.api.model.Place
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.connect.meetupsfellow.R
import com.connect.meetupsfellow.application.ArchitectureApp
import com.connect.meetupsfellow.log.LogManager
import com.connect.meetupsfellow.mvp.view.model.RecycleModel
import java.math.BigInteger
import java.security.SecureRandom
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern
import kotlin.collections.ArrayList


/**
 * Created by Nilesh on 10/24/2018.
 */

@Suppress("unused")
object Constants {

    private val APP_NAME = ArchitectureApp.instance!!.getString(R.string.app_name)

    var currentUser = ""

    var ImgPath = ""

    var unreadCountP = 0
    var unreadCountG = 0

    internal const val GIPHY_API_KEY = "EIiebR0VR9fmcp2JXjOJ99BExLXBbXmH"

    var personalChatList = ArrayList<RecycleModel>()

    object Variables {

        internal var isGroupCreated = false
        internal var currentUserName = ""
    }

    object EnableCrashlytics {
        internal const val ENABLE = true
    }

    object Device {
        internal const val TYPE = "Android"
    }

    object Admin {
        internal const val Convo = "01_011"
        internal const val ConvoAdminOneToOne = "3"
    }

    object Zone {
        internal fun getTimeZone(): String {
            val myDate = Date()
            val calendar = Calendar.getInstance()
            calendar.timeZone = TimeZone.getTimeZone("UTC")
            calendar.time = myDate
            val time = calendar.time
            val outputFmt = SimpleDateFormat("zz", Locale.getDefault())
            val dateAsString = outputFmt.format(time)
            LogManager.logger.i(ArchitectureApp.instance!!.tag, "Timezone is : $dateAsString")
            return dateAsString
        }
    }

    object Default {
//        internal const val Location = "Key West, Florida"
//        internal const val Latitude = "24.555059"
//        internal const val Longitude = "-81.779984"
    }

    object ProfileStatus {
        internal const val Pending = "pending"
        internal const val Approved = "approved"
        internal const val Unapproved = "unapproved"
        internal const val Blocked = "blocked"
    }

    object View {
        internal const val Favorite = "favorite"
        internal const val Interested = "interested"
        internal const val User = "user"
        internal const val Event = "event"
        internal const val Conversation = "conversation"
        internal const val FavoriteEvent = "fav_event"
        internal const val TimeLine = "time_line"

    }

    object UserName {
        internal val valid = Pattern
            .compile("^(?!\\.)(?!.*\\.\$)(?!.*?\\.\\.)(?!_)(?!.*_\$)(?!.*?__)[a-zA-Z0-9_.]{5,20}+\$")!!
    }

    object UserName100 {
        internal val valid = Pattern
            .compile("^(?!\\.)(?!.*\\.\$)(?!.*?\\.\\.)(?!_)(?!.*_\$)(?!.*?__)[a-zA-Z0-9_.]{5,100}+\$")!!
    }






    object EmailValidation {
        internal val PATTERN = Pattern
            .compile("^(?!\\.)(?!.*\\.\$)(?!.*?\\.\\.)(?!_)(?!.*_\$)(?!.*?__)(?!\\-)(?!.*?\\-\\-)[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$")!!

        internal val Patterns = Pattern
            .compile("[a-zA-Z0-9._-]+@[a-z]+\\\\.+[a-z]+")!!
    }

    object IntentDataKeys {
        const val TITLE = "Title"
        const val Class = "Class"
        const val LINK = "Link"
        const val IsFirstTime = "Is_FirstTime"
        const val EventId = "event_id"
        const val UserId = "user_id"
        const val EventDetails = "event_details"
        const val ProfileImages = "profile_images"
        const val PrivateAlbumImages = "private_album_images"
        const val Selected = "selected"
        const val Conversation = "conversation"
        const val ConvoId = "convoId"
        const val UserName = "user_name"
        const val UserImage = "user_image"
        const val Status = "status"
        const val Blocked = "blocked"
        const val UserStatus = "user_status"
        const val VideoUrl = "video_url"
        const val FileUrl = "file"
        const val FileUri = "file_uri"
        const val FilePath = "file_path"
        const val FileName = "file_name"
        const val FileExt = "file_extension"
        const val Type = "request_type"
        const val ImgUri = "Image_uri"
        const val HasSharedPrivateAlbumWithMe = "has_Shared_Private_AlbumWith_Me"
        const val IsMyPrivateAlbumSharedWithUser = "isMy_Private_Album_Shared_WithUser"
        const val TimeLinePost ="has_time_linepost"
        const val TimeLinePushId ="has_time_line_pushid"
        const val NotificationId ="notificationId"
        const val isNotification ="is_Notification"
        const val ImgPath = "imgPath"
        const val IsUserConnected = "isUserConnected"
        const val connection_status = "connection_status"
        const val Login_user_isPro = "login_user_ispro"
        const val User_Phone = "phone_number"
        const val User_email = "email_id"
        const val Is_Verify = "is_verify"
        const val Country_Code = "country_code"
        const val Verify_Code = "verify_code"


    }

    object Search {
        const val Universal = -1
        const val NearBy = 0
        const val Event = 1
        const val Chat = 2
        const val User = 3
        const val Favorite = 4
        const val TimeLine = 5
        var place : Place? = null
    }

    object Injection {
        internal const val API_DEVELOPMENT_URL = "developmentURL"
        internal const val API_TESTING_URL = "testingURL"
        internal const val API_LIVE_URL = "liveURL"
    }

    object NotificationStatus {
        internal const val UnRead = 0
        internal const val Read = 1
    }


    object InstanceSave{
        var saveInstanceNew : Activity? = null

    }


    object Json {
        internal fun validJson(reader: String): Boolean {
            return try {
                Gson().fromJson(reader, Object::class.java)
                true
            } catch (e: JsonSyntaxException) {
                false
            }
        }
    }

    object Intent {

        internal const val Welcome = "MEETUP_APP_WELCOME"
        internal const val GetStart = "MEETUP_APP_GET_START"
        internal const val Login = "MEETUP_APP_LOGIN"
        internal const val Verify = "MEETUP_APP_VERIFY"
        internal const val Create = "MEETUP_APP_CREATE_PROFILE"
        internal const val Home = "MEETUP_APP_HOME1"
        internal const val VerifyPin = "MEETUP_APP_VERIFY_PIN"
        internal const val NotificationSetting = "MEETUP_APP_NOTIFICATION_SETTING"
        internal const val Place = "MEETUP_APP_PLACE_AUTOCOMPLETE"
        internal const val Settings = "MEETUP_APP_SETTINGS"
        internal const val Favorite = "MEETUP_APP_FAVORITE"
        internal const val Search = "MEETUP_APP_SEARCH"
        internal const val SearchLocation = "MEETUP_APP_SEARCH_LOCATION"
        internal const val Webview = "MEETUP_APP_WEB_VIEW"
        internal const val Interested = "MEETUP_APP_INTERESTED_PEOPLE"
        internal const val Profile = "MEETUP_APP_PROFILE_SELF"
        internal const val ProfileUser = "MEETUP_APP_PROFILE_USER"
        internal const val Edit = "MEETUP_APP_PROFILE_EDIT"
        internal const val PersonalCall = "MEETUP_APP_PERSONAL_CALL"
        internal const val EventDetails = "MEETUP_APP_EVENT_DETAILS"
        internal const val MultiUserViewLike = "MULTI_USER_VIEW_LIKE_ACTIVITY"
        internal const val CreateEvent = "MEETUP_APP_CREATE_EVENT"
        internal const val ChangeNumber = "MEETUP_APP_CHANGE_NUMBER"
        internal const val Notification = "MEETUP_APP_NOTIFICATION"
        internal const val NewGroup = "MEETUP_APP_NEW_GROUP"
        internal const val GroupChat = "MEETUP_APP_GROUP_CHAT"
        internal const val GroupChatDetails = "MEETUP_APP_GROUP_CHAT_DETAILS"
        internal const val ImagePager = "MEETUP_APP_IMAGE_PAGER"
        internal const val AddGroupMembers = "MEETUP_APP_ADD_GROUP_MEMBERS"
        internal const val Reporting = "MEETUP_APP_FLAG_REPORTING"
        internal const val Chat = "MEETUP_APP_CHAT"
        internal const val Chat_Video_Player = "MEETUP_APP_CHAT_VIDEO_PLAYER"
        internal const val Blocked = "MEETUP_APP_BLOCKED_USERS"
        internal const val ConnectRequest = "MEETUP_APP_CONNECT_REQUEST"
        internal const val MyConnections = "MY_CONNECTIONS"
        internal const val AddNewGroupMembers = "MEETUP_APP_ADD_NEW_GROUP_MEMBERS"
        internal const val Private = "MEETUP_APP_PRIVATE_ACCESS"
        internal const val PrivateAlbumImages = "MEETUP_APP_VIEW_PRIVATE_ALBUM_IMAGES"
        internal const val PrivateAlbum = "MEETUP_APP_VIEW_PRIVATE_ALBUM"
        internal const val PrivateAlbumList = "MEETUP_APP_PRIVATE_LIST_ACCESS"
        internal const val Gallery = "MEETUP_APP_VIEW_GALLERY_ACTIVITY"
        internal const val Recent = "MEETUP_APP_VIEW_RECENT_ACTIVITY"
        internal const val FollowFollowingActivity = "FOLLOW_FOLLOWING_ACTIVITY"
        internal const val ForgotPasswordActivity = "FORGOT_PASSWORD_ACTIVITY"
        internal const val ResetPasswordActivity = "RESET_PASSWORD_ACTIVITY"
        internal const val LoginWithEmailActivity = "LOGIN_WITH_EMAIL_ACTIVITY"

    }

    object AWS {

        object URL {
            //            const val AWS_URL = "https://s3.amazonaws.com/"
            internal const val AWS_URL = "https://s3.us-east-1.amazonaws.com/"
            internal const val AWS_URL_PROFILE = "https://s3.us-east-1.amazonaws.com/reegurstorage/"
//            internal const val AWS_URL = "https://s3.amazonaws.com/"
        }

        object PoolId {
            /*
            * You should replace these values with your own. See the README for details
            * on what to fill in.
            */
//            internal const val COGNITO_POOL_ID = "us-east-2:7088e338-49a7-4170-a6bf-41335c6f4c2b"
//            internal const val COGNITO_POOL_ID = "eu-west-1:e9bb49de-2ab8-40f9-b0b0-2c24b5ab5256"
            internal const val COGNITO_POOL_ID = "us-east-1:6ec37545-83ba-494a-aad3-d9bbeb55e0d1"

        }

        object PoolRegion {
            /*
            * Region of your Cognito identity pool ID.
            */
//            internal const val COGNITO_POOL_REGION = "us-east-2"
            internal const val COGNITO_POOL_REGION = "us-east-1"
        }

        object AwsProfileBucketName {
            /*
            * Note, you must first create a bucket using the S3 console before running
            * the sample (https://console.aws.amazon.com/s3/). After creating a bucket,
            * put it's name in the field below.
            */
            internal const val BUCKET_NAME = "reegurapp"
        }

        object AwsSelfieBucketName {
            /*
            * Note, you must first create a bucket using the S3 console before running
            * the sample (https://console.aws.amazon.com/s3/). After creating a bucket,
            * put it's name in the field below.
            */
//            internal const val BUCKET_NAME = "dldating-selfies"
        }

        object BucketRegion {
            /*
            * Region of your bucket
            */
//            internal const val AWS_BUCKET_REGION = "us-east-2"
            internal const val AWS_BUCKET_REGION = "us-east-1"
        }

        object ProfileBucketPath {
            internal const val PATH = URL.AWS_URL + AwsProfileBucketName.BUCKET_NAME + "/"
        }

        object SelfieBucketPath {
//            internal const val PATH = URL.AWS_URL + AwsSelfieBucketName.BUCKET_NAME + "/"
        }

    }

    object Random {
        internal fun random(): String {
            val token = ByteArray(10)
            SecureRandom().nextBytes(token)
            return BigInteger(1, token).toString(10)
        }
    }


    object TimerRefresh{

        internal const val START_TIME_IN_SEC: Long = 300

    }

    object Profile {
        internal const val Deactivate = 0
        internal const val Activate = 1
        internal const val Delete = -1
    }

    object ServerError {
        internal const val InvalidToken = "token_not_valid"
        internal const val SessionExpired = "sessionExpired"
        internal const val Blocked = "accountBlocked"
    }

    object DialogTheme {
        internal const val NoTitleBar = android.R.style.Theme_Translucent_NoTitleBar
        //internal const val FullScreen = R.style.DialogFullscreen
    }

    object ImagePicker {
        internal const val GALLERY = 1001
        internal const val CAMERA = 1002
        internal const val FACEBOOK = 1003
        internal const val Video = 1004
        internal const val Private = 1005
        internal const val View = 1006
        internal const val Image = 1006

    }

    object UserAction {
        internal const val LIKE = "like"
        internal const val BLOCK = "block"
        internal const val UNBLOCK = "unblock"
        internal const val REPORT = "report"
        internal const val MATCH = "match"
        internal const val ADMIN = "admin"
        internal const val SELFIE = "selfie"
        internal const val UNMATCH = "unmatch"
        internal const val CHAT = "Chat"
        internal const val PROFILE = "profile"
        internal const val ACCEPT = "accept"
        internal const val REJECT = "reject"
    }

    object RequestCode {
        internal const val AUTOCOMPLETE_REQUEST_CODE = 8001
        internal const val AUTOCOMPLETE_REQUEST_CODE_EVENT = 8002
        internal  const val TIMELINE_REQUEST_CODE = 2001
    }

    object IntentData {
        internal val PREVIEW_TYPE_SELF = APP_NAME + "_preview_type_self"
        internal val ANOTHER_USER_ID = APP_NAME + "_another_user_id"
        internal val SETTINGS_NEW_USER = APP_NAME + "_settings_new_user"
        internal val NEARBY_NEW_USER = APP_NAME + "_nearby_new_user"

        internal val DETAILS_TITLE = APP_NAME + "_details_title"
        internal val DETAILS = APP_NAME + "_details"
        internal val DETAILS_POSITION = APP_NAME + "_details_position"

        internal const val Image_URL = "ImageUrl"
        internal const val POST_IMAGES = "post_images"
        internal const val POST_IMAGES_POSITION = "post_images_position"

        internal const val PROFILE_IMAGES = "profile_images"
        internal const val NEED_IMAGES = "need_images"

        internal const val TITLE = "title"
        internal const val LINK = "link"

        internal const val CALL_ID = "call_id"

        internal const val NOTIFICATION = "dating_on_dl_notification"
        internal const val NOTIFICATION_TYPE = "dating_on_dl_notification_type"
    }

    object Location {
        internal const val SUCCESS_RESULT = 0

        private const val PACKAGE_NAME = "com.google.android.gms.location.sample.locationaddress"

        internal const val RECEIVER = "$PACKAGE_NAME.RECEIVER"

        internal const val RESULT_DATA_KEY = "$PACKAGE_NAME.RESULT_DATA_KEY"
        internal const val RESULT_ADMIN_AREA = "$PACKAGE_NAME.RESULT_ADMIN_AREA"
        internal const val RESULT_DATA_KEY_LATITUDE = "$PACKAGE_NAME.RESULT_DATA_LATITUDE"
        internal const val RESULT_DATA_KEY_LONGITUDE = "$PACKAGE_NAME.RESULT_DATA_LONGITUDE"

        internal const val LOCATION_DATA_EXTRA = "$PACKAGE_NAME.LOCATION_DATA_EXTRA"
    }

    object Notification {
        internal const val FLAG = "flag"
        internal const val COLLAPSE_KEY = "collapse_key"
        internal const val NOTIFICATION_DATA = "notification_data"
        internal const val USER_ID = "userId"
        internal const val SENDER_ID = "senderId"
        internal const val ROOM_ID = "roomId"
        internal const val USER_NAME = "userName"
        internal const val TYPE = "type"
        internal const val IMAGE = "image"
        internal const val REEGUR_UPDATE="admin-chat"

        internal const val PostStatus = "post-status"
        internal const val PostMention = "mention"
        internal const val PostNewComment = "new-comment"
        internal const val PostMentionComment = "mention-comment"
        internal const val PostNewLike = "new-like"
        internal const val PostNewUnlike = "new-unlike"
        internal const val LikeComment = "like_comment"
        internal const val UnlikeComment = "unlike_comment"



        internal const val EventInterest = "EventInterest"
        internal const val EventApproved = "event-approved"
        internal const val EventRejected = "event-rejected"
        internal const val EventBlocked = "event-blocked"
        internal const val EventStatusApproved = "event-status-approved"
        internal const val EventUnblock = "event-unblock"
        internal const val UserBlock = "user-block"

        internal const val ProfileLike = "profile-like"
        internal const val Following = "following"
        internal const val Favourite = "favourite"
        internal const val UserChat = "userChat"
        internal const val Chat = "chat"
        internal const val Admin_Notify = "admin-notify"
        internal const val BirthdayWish = "birthday_wish"
        internal const val SessionExpired = "session-expired"
        internal const val PrivateAlbumShare = "private-album-share"
        internal const val User_Viewed_profile = "user_viewed_profile"
        internal const val Connection = "connection"
        internal const val Membership_Purchase = "membership_purchase"
        internal const val Membership_Expire = "membership_expire"
        internal const val eventInterest = "eventInterest"
        internal const val EventLike = "eventLikeBy"
        internal const val NewUser = "new-user-join"
        internal const val NewEvent = "new-event-post"
    }

    object Permissions {
        private const val CAMERA = Manifest.permission.CAMERA
        private const val STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE
        private const val FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION
        private const val COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION
        private const val RECORD_AUDIO = Manifest.permission.RECORD_AUDIO
        private const val READ_PHONE_STATE = Manifest.permission.READ_PHONE_STATE
        internal val CAMERA_STORAGE = arrayOf(CAMERA, STORAGE)
        internal val LOCATION = arrayOf(FINE_LOCATION, COARSE_LOCATION)
        internal val CALLING = arrayOf(RECORD_AUDIO, READ_PHONE_STATE)
    }

    object PermissionsCode {
        internal const val REQUEST_PERMISSION_CAMERA = 6001
        internal const val REQUEST_PERMISSION_GALLERY = 6002
        internal const val REQUEST_PERMISSION_VIDEO = 6003
        internal const val REQUEST_PERMISSION_LOCATION = 6004
        internal const val REQUEST_PERMISSION_RECORD_AUDIO = 6005
    }

    object DatePicker {
        internal const val Month = "month"
        internal const val Year = "year"
        internal const val Day = "day"
    }

    object Firebase {
        internal const val Profile = "/UserProfile"
        internal const val Conversation = "/Conversations"
        internal const val Chat = "/UsersChat"
        //internal const val BucketUrl = "gs://meetupsfellow-d0b85.appspot.com/"
        internal const val BucketUrl = "gs://meetupsfellow-eacdf.appspot.com/"
        //internal const val databaseUrl = "https://meetupsfellow-d0b85-default-rtdb.firebaseio.com"
        internal const val databaseUrl = "https://meetupsfellow-eacdf-default-rtdb.firebaseio.com"

      /**  For real time chat, we are creating live and demo chat rooms with below variables for Firebase*/
      ///  internal const val ChatDB = "MeetUpsLive"
        internal const val ChatDB = "MeetUpsDemo"

        internal const val AppURL = "https://play.google.com/store/apps/details?id=com.connect.meetupsfellow&hl=en"
        internal const val PrivacyPolicyURL = "https://www.privacy-policy.meetupsfellow.com"
        internal const val FAQ_URL = "https://www.use-guide.meetupsfellow.com"
        internal const val Term_Condition_URL = "https://www.terms-conditions.meetupsfellow.com"
        internal const val App_Review_URL = "https://demoapis.meetupsfellow.com/review/"
    }

    object Time {
        var DAY = 0
        var DAYS = 1
        var WEEK_ONE = 2
        var WEEK_TWO = 3
        var WEEK_THREE = 4
        var ONE_MONTH = 5
        var TWO_MONTH = 6
        var THREE_MONTH = 7
        var FOUR_MONTH = 8
        var FIVE_MONTH = 9
        var SIX_MONTH = 10
        var SEVEN_MONTH = 11
        var EIGHT_MONTH = 12
        var NINE_MONTH = 13
        var TEN_MONTH = 14
        var ELEVEN_MONTH = 15
        var TWELVE_MONTH = 16
    }

    object Chat {
        internal const val Me = 0
        internal const val Other = 1
        internal const val Add = 2
    }

    object MediaType {
        internal const val Text = 1
        internal const val Image = 2
        internal const val Audio = 3
        internal const val Video = 4
    }

    object MessageStatus {
        internal const val NotSent = 1
        internal const val Sent = 2
        internal const val Delivered = 3
        internal const val Read = 4
        internal const val Deleted = 5
        internal const val AdminSupportRead = 6
    }

    object ConvoId {
        internal fun id(meUserId: Int, otherUserId: Int): String {
            return when (otherUserId < meUserId) {
                true -> "${otherUserId}-$meUserId"
                false -> "${meUserId}-$otherUserId"
            }
        }
    }

    object Variable{

        var isProfile = false
    }

    object SkuDetails {
        internal const val subscriptionOne = "one_month_subscription_new"
        internal const val subscriptionTwo = "three_month_subscription_new"
    }

    object getUTCDateTime{
        internal fun getUTCTime(): String? {
//            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS")
//                    .withZone(ZoneOffset.UTC).format(Instant.now())
//            } else {
              ///  val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSS")
                val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                formatter.timeZone = TimeZone.getTimeZone("UTC")
                return formatter.format(Date())
//            }
        }
    }
}