package com.connect.meetupsfellow.mvp.view.activities

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.LayerDrawable
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.provider.ContactsContract
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.view.animation.AnimationUtils
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.core.widget.NestedScrollView
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.android.billingclient.api.BillingClient
import com.connect.meetupsfellow.BuildConfig
import com.connect.meetupsfellow.R
import com.connect.meetupsfellow.application.ArchitectureApp
import com.connect.meetupsfellow.components.activity.CustomAppActivityCompatViewImpl
import com.connect.meetupsfellow.components.activity.CustomAppActivityImpl
import com.connect.meetupsfellow.constants.Constants
import com.connect.meetupsfellow.constants.Constants.getUTCDateTime.getUTCTime
import com.connect.meetupsfellow.constants.ConstantsApi
import com.connect.meetupsfellow.constants.FirebaseChat
import com.connect.meetupsfellow.constants.ItemClickStatus
import com.connect.meetupsfellow.databinding.ActivityHomeBinding
import com.connect.meetupsfellow.databinding.ActivityInterestedPeopleBinding
import com.connect.meetupsfellow.databinding.FragmentFollowerBinding
import com.connect.meetupsfellow.developement.interfaces.ApiConnect
import com.connect.meetupsfellow.developement.interfaces.FirebaseUtil
import com.connect.meetupsfellow.developement.interfaces.SharedPreferencesUtil
import com.connect.meetupsfellow.global.firebase.FirebaseChatListener
import com.connect.meetupsfellow.global.firebase.FirebaseListener
import com.connect.meetupsfellow.global.interfaces.ItemClick
import com.connect.meetupsfellow.global.interfaces.OnNotificationReceived
import com.connect.meetupsfellow.global.utils.DisplayImage
import com.connect.meetupsfellow.global.utils.InterstitialAdManager
import com.connect.meetupsfellow.global.utils.RecyclerViewClick
import com.connect.meetupsfellow.global.view.CountDrawable
import com.connect.meetupsfellow.log.LogManager
import com.connect.meetupsfellow.mvp.connector.activity.CreateProfileConnector
import com.connect.meetupsfellow.mvp.connector.activity.HomeConnector
import com.connect.meetupsfellow.mvp.connector.activity.NotificationConnector
import com.connect.meetupsfellow.mvp.connector.activity.SearchConnector
import com.connect.meetupsfellow.mvp.presenter.activity.CreateProfilePresenter
import com.connect.meetupsfellow.mvp.presenter.activity.HomePresenter
import com.connect.meetupsfellow.mvp.presenter.activity.NotificationPresenter
import com.connect.meetupsfellow.mvp.presenter.activity.SearchPresenter
import com.connect.meetupsfellow.mvp.view.adapter.*
import com.connect.meetupsfellow.mvp.view.dialog.AlertBuyPremium
import com.connect.meetupsfellow.mvp.view.dialog.SearchFilterDialog
import com.connect.meetupsfellow.mvp.view.fragment.*
import com.connect.meetupsfellow.mvp.view.model.*
import com.connect.meetupsfellow.retrofit.request.*
import com.connect.meetupsfellow.retrofit.response.CommonResponse
import com.connect.meetupsfellow.retrofit.response.ResponseAdminLastPush
import com.connect.meetupsfellow.retrofit.response.ResponseUserData
import com.connect.meetupsfellow.retrofit.response.ResponseUserProfileImages
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.admanager.AdManagerAdRequest
import com.google.android.gms.ads.admanager.AdManagerAdView
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.tabs.TabLayout
import com.google.firebase.database.*
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import com.ivaniskandar.materialsheetfab.MaterialSheetFab
import com.ivaniskandar.materialsheetfab.MaterialSheetFabEventListener
import org.json.JSONObject
import retrofit2.Response
import rx.Observer
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@Suppress("SameParameterValue")
class HomeActivity : CustomAppActivityCompatViewImpl(), OnNotificationReceived {

    @Inject
    lateinit var apiConnect: ApiConnect

    private lateinit var mBillingClient: BillingClient


    var uploadMessage: ValueCallback<Array<Uri>>? = null


    private val refresh = SwipeRefreshLayout.OnRefreshListener {
        when {
            binding!!.includedLayHome.ivFeed.isSelected -> {
                feedsAdapter.clearFeeds()
                nextFeed = 1
                feeds()
                Log.d("nearRef", "feed")
            }

            /*ivTimeLine.isSelected -> {
                timeline()
            }*/

            binding!!.includedLayHome.ivNearby.isSelected -> {


                /* ShortcutBadger.applyCount(
                     context,
                     sharedPreferencesUtil.fetchNotificationCount().toInt()
                 )*/

                // setBadge( this@HomeActivity, sharedPreferencesUtil.fetchNotificationCount().toInt())
                //swifeRefresh = true
                Log.e("nearRef", "refresh")
                binding!!.includedLayHome.includedLayNearby.becomePro2.rlPro.visibility = View.GONE
                nearByList.clear()
                nearByListAdminAds.clear()
                nearByListRecentOnlyAds.clear()
                mostRecentUserList.clear()
                nearByAdapter.clearNearBy()
                mostRecentlyJoinedUserAdapter.clearNearBy()
                nextNearby = 1
                //initiateAdminADs()
                nextRecent = 1
                mostRecentlyJoindUser()
                Log.e("CallNearBy%", "111")
                getNearbyUser()
                binding!!.includedLayHome.includedLChat.swipeRefreshChat.isRefreshing = false

            }

            binding!!.includedLayHome.ivChat.isSelected -> {
                //adminConversation()
                //showLocalConversation()
                //initiateConversationUI()
                binding!!.includedLayHome.includedLChat.swipeRefreshChat.isRefreshing = false
                Log.d("nearRef", "chat")
            }

            else -> {

                //showLocalConversation()
                //binding!!.includedLayHome.includedLChat.swipeRefreshChat.isRefreshing = false
                Log.d("nearRef", "chat")
            }

            /*else -> {

                //swifeRefresh = true
                Log.d("nearRef", "ref")
                nearByList.clear()
                nearByListAdminAds.clear()
                nearByListRecentOnlyAds.clear()
                mostRecentUserList.clear()
                nearByAdapter.clearNearBy()
                mostRecentlyJoinedUserAdapter.clearNearBy()
                nextNearby = 1
                initiateAdminADs()
                nextRecent = 1
                mostRecentlyJoindUser()
                nearby()
            }*/
        }

    }

    private val drawerListener = object : DrawerLayout.DrawerListener {
        override fun onDrawerStateChanged(p0: Int) {

        }

        override fun onDrawerSlide(p0: View, p1: Float) {
        }

        override fun onDrawerClosed(p0: View) {
            home = false
        }

        override fun onDrawerOpened(p0: View) {
        }
    }

    private val presenterN = object : NotificationConnector.RequiredViewOps {
        override fun showToast(error: String, logout: Boolean?) {
            when (logout) {
                true -> {
                    logout()
                }

                false -> {}
                null -> {}
            }
            universalToast(error)
            //hideProgressView(rl_progress)
        }

        override fun showResponse(response: CommonResponse, type: ConstantsApi) {
            when (type) {
                ConstantsApi.NOTIFICATION -> {
                    LogManager.logger.i(
                        ArchitectureApp.instance!!.tag,
                        "Response_is* ::: ${Gson().toJson(response.notification)}"
                    )
                    notificationAdapter.removeAll()
                    notificationAdapter.add(
                        response.notification,
                        sharedPreferencesUtil.fetchUserProfile().isProMembership,
                        sharedPreferencesUtil.fetchUserProfile().currentPlanInfo?.planId ?: 2
                    )
                    Log.e("NOTIFICATION%1", "Count ${response.countUnreadMgs}")
                    updateUi()
                }

                ConstantsApi.CLEAR_NOTIFICATION -> {
                    sharedPreferencesUtil.saveNotificationCount("0")
                    notificationAdapter.removeAll()
                    updateUi()
                }

                ConstantsApi.READ_NOTIFICATION, ConstantsApi.DELETE_NOTIFICATION -> {
                    val count = sharedPreferencesUtil.fetchNotificationCount().toInt()
                    when (count > 0) {
                        true -> sharedPreferencesUtil.saveNotificationCount("${(count - 1)}")
                        false -> {}
                    }
                    fetchNotifications()
                }

                else -> {

                }
            }
            binding!!.includedLayHome.includedNotification.swipeRefreshNotification.isRefreshing =
                false
            hideProgressView(progressBar)
        }
    }

    private val presenterS = object : SearchConnector.RequiredViewOps {
        override fun showToast(error: String, logout: Boolean?) {
            when (logout) {
                true -> {
                    logout()
                }

                false -> {}
                null -> {}
            }
            universalToast(error)
            //pbSearch.visibility = View.GONE
        }

        override fun showResponse(response: CommonResponse, type: ConstantsApi) {

            Log.e("rr++", Gson().toJson(response))

            ///  searchAdapter.update(response.search)
            searchAdapter.update(response.nearbyuser)


            updateUiS()
            //swipeRefreshNearby.isRefreshing = false
            //pbSearch.visibility = View.GONE
        }

    }

    private val presenter = object : HomeConnector.RequiredViewOps {
        override fun showToast(error: String, logout: Boolean?) {
            when (logout) {
                true -> {
                    logout()
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
            flagNearbyCall = false
            binding!!.includedLayHome.includedFeed.swipeRefresh.isRefreshing = false
        }

        override fun showResponse(response: CommonResponse, type: ConstantsApi) {
            when (type) {

                ConstantsApi.ADMIN_LAST_PUSH -> {
                    adminlastpush.clear()
                    adminlastpush.addAll(response.notificationAdimPush)
                    //  Log.e("notification", adminlastpush.size.toString())

                    if (sharedPreferencesUtil.fetchPopUpUserChatPush() == "") {
                        //     Log.e("if fetchPopUpUserChatPush",sharedPreferencesUtil.fetchPopUpUserChatPush())
                        sharedPreferencesUtil.savePopUpUserChatPush(adminlastpush.get(0).message)
                        upDateAdminLastPush(adminlastpush.get(0))
                    }

                    if (sharedPreferencesUtil.fetchPopUpUserChatPush() == adminlastpush.get(0).message) {
                        //  Log.e("iff fetchPopUpUserChatPush",sharedPreferencesUtil.fetchPopUpUserChatPush())
                    } else {
                        sharedPreferencesUtil.savePopUpUserChatPush(adminlastpush.get(0).message)
                        upDateAdminLastPush(adminlastpush.get(0))
                    }


                }


                ConstantsApi.ADMIN_ADS -> {
                    //   nearByListAdminAds = ArrayList()
                    //  nearByListAdminAds.clear()


                    if (!response.adminAdvertisements.isNullOrEmpty()) {

                        sharedPreferencesUtil.saveAdminAdsCount(response.adminAdvertisements.size.toString())

                        gridLayoutManager?.spanSizeLookup =
                            object : GridLayoutManager.SpanSizeLookup() {

                                override fun getSpanSize(position: Int): Int {
                                    return if (position < response.adminAdvertisements.size * 17) {
                                        if ((position + 1) % 17 == 0 && (position + 1) != 1) { // totalRowCount : How many item you want to show
                                            4
                                        } else 1
                                    } else 1
                                }
                            }


                        nearByListAdminAds.addAll(response.adminAdvertisements)
                    } else {
                        //  nearByListAdminAds.add(ResponseUserData      )
                        sharedPreferencesUtil.saveAdminAdsCount("0")

                        gridLayoutManager?.spanSizeLookup =
                            object : GridLayoutManager.SpanSizeLookup() {

                                override fun getSpanSize(position: Int): Int {
                                    return 1
                                }
                            }


                        //  nearByListAdminAds?.add()


                    }


                }

                /*ConstantsApi.READ_NOTIFICATION -> {
                    val count = sharedPreferencesUtil.fetchNotificationCount().toInt()
                    when (count > 0) {
                        true -> sharedPreferencesUtil.saveNotificationCount("${(count - 1)}")
                    }
                    //   fetchNotifications()
                }*/

                ConstantsApi.FETCH_FEEDS -> {
                    when (response.haveNext) {
                        1 -> {
                            loadMoreFeed = true
                            nextFeed = response.nextPage
                        }
                    }
                    feedsAdapter.updateFeeds(response.eventData)
                    adjustFeedsUi()
                    binding!!.includedLayHome.includedFeed.swipeRefresh.isRefreshing = false
                    if (null != menu) {
                        setCount(this@HomeActivity, sharedPreferencesUtil.fetchNotificationCount())
                    }
//                    setBadge(this@HomeActivity, sharedPreferencesUtil.fetchNotificationCount().toInt())
                    hideProgressView(progressBar)
                }

                ConstantsApi.FETCH_NEARBY -> {
                    flagNearbyCall = false
                    /// response.nearbyuser.removeAt(0)
                    when (response.haveNext) {
                        1 -> {
                            loadMoreNearby = true
                            nextNearby = response.nextPage
                        }

                        else -> {
                            loadMoreNearby = false
                        }
                    }
                    nearByListRecentOnlyAds.addAll(response.nearbyuser)
                    nearByListSwitchs.addAll(response.nearbyuser)

                    Log.e("nearUsers12- ", response.nearbyuser.size.toString())
                    Log.e("nearUsers12- IC: ", nearByAdapter.itemCount.toString())

                    if (nearByList.isEmpty() && nearByAdapter.itemCount <= 0) {
                        nearByList.addAll(response.nearbyuser)
                        Log.e("nearUsers12- ", "IFFF")
                        nearByAdapter.updateNearBy(nearByList)
                        binding!!.includedLayHome.playerProgressbar.visibility = View.GONE
                    } else {
                        Log.e("nearUsers12- ", "ELSSSS")
                        nearByList.clear()
                        nearByList.addAll(response.nearbyuser)
                        nearByAdapter.addData(nearByList)
                        Log.e("nearUsers12- ", "ELSSSS_Size: ${nearByList.size}")
                        binding!!.includedLayHome.playerProgressbar.visibility = View.GONE
                        if (null != menu) {
                            setCount(
                                this@HomeActivity, sharedPreferencesUtil.fetchNotificationCount()
                            )
                        }

                    }
                    adjustNearbyUi()
                    binding!!.includedLayHome.includedLayNearby.swipeRefreshNearby.isRefreshing =
                        false

                    /*
                                        var attempts = 0
                                        val maxAttempts = 1 // Limit to a certain number of iterations

                                        while (response.nearbyuser.isNotEmpty() && attempts < maxAttempts) {
                                            for (user in response.nearbyuser) {
                                                // Process each user
                                                sendDirectMsgInFirebase("Testing message_ Today", user.id.toInt(),user)
                                                sendDirectMsgInFirebase("Good Morning!!", user.id.toInt(),user)
                                            }

                                            // Increment the counter
                                            attempts++

                                            // Check if no more data is available
                                            if (response.nearbyuser.isEmpty()) {
                                                println("No more users available.")
                                                break
                                            }
                                        }*/

                }

                ConstantsApi.GET_MOST_RECENTLY_JOIND_USER -> {

                    when (response.haveNext) {
                        1 -> {
                            loadMoreRecent = true
                            nextRecent = response.nextPage
                        }
                    }



                    mostRecentUserList = ArrayList()
                    mostRecentUserList.addAll(response.nearbyuser)
                    if (mostRecentUserList.size > 0) mostRecentUserList.removeAt(0)
                    mostRecentlyJoinedUserAdapter.updateNearBy(mostRecentUserList)

                    //adjustNearbyUi()
                    // swipeRefreshNearby.isRefreshing = false

//                    if (null != menu) {
//                        setCount(this@HomeActivity, sharedPreferencesUtil.fetchNotificationCount())
//                    }
//                    setBadge(this@HomeActivity, sharedPreferencesUtil.fetchNotificationCount().toInt())
                }


                ConstantsApi.NOTIFICATION -> {

                    LogManager.logger.i(
                        ArchitectureApp.instance!!.tag,
                        "Response is ::: ${Gson().toJson(response.notification)}"
                    )
                    hideProgressView(progressBar)
                    binding!!.includedLayHome.includedNotification.swipeRefreshNotification.isRefreshing =
                        false
                    notificationAdapter.removeAll()
                    notificationAdapter.add(
                        response.notification,
                        sharedPreferencesUtil.fetchUserProfile().isProMembership,
                        sharedPreferencesUtil.fetchUserProfile().currentPlanInfo?.planId ?: 2
                    )
                    updateUi()
                    Log.e("NOTIFICATION%", "Count ${response.countUnreadMgs}")
                }

                ConstantsApi.CLEAR_NOTIFICATION -> {
                    sharedPreferencesUtil.saveNotificationCount("0")
                    notificationAdapter.removeAll()
                    updateUi()
                    binding!!.includedLayHome.includedNotification.swipeRefreshNotification.isRefreshing =
                        false
                }

                ConstantsApi.READ_NOTIFICATION, ConstantsApi.DELETE_NOTIFICATION -> {
                    val count = sharedPreferencesUtil.fetchNotificationCount().toInt()
                    when (count > 0) {
                        true -> sharedPreferencesUtil.saveNotificationCount("${(count - 1)}")
                        false -> {}
                    }
                    fetchNotifications()
                }

                ConstantsApi.ADMIN_CONVERSATION -> {
                    conversationAdapter.addAdminConversation(response.adminChat)
                }

                ConstantsApi.CLEAR_CHAT -> {
                    conversationAdapter.remove(this@HomeActivity.position)
                    this@HomeActivity.position = -1
                }


                ConstantsApi.LOGOUT -> {
                    universalToast(response.message)

                    logout()
                }

                ConstantsApi.SAVE_USER_CONTACTS -> {
                   /// universalToast(response.message)
                    Log.e("API", "Contact: ${response.message}")
                }

                ConstantsApi.UPADTE_TIME_STAMP -> {
                    Log.v("Upadettime", "User time stamp updated successfully")
                }

                ConstantsApi.SUPPORT_CLEARCHAT -> {
                    universalToast("deleted")
                }


                else -> {
                    hideProgressView(progressBar)
                    flagNearbyCall = false
                }
            }
            //hideProgressView(rl_progress)
        }

        /*override fun showProUser(response: CommonResponse, type: ConstantsApi) {

          //  val  response: CommonResponse? =  response.body()
            val profile: ResponseUserData?  = response.userInfo
            val visible_pro_user: Boolean? = profile?.isProMembership
            Log.v("HomeActivity", "PRPUser"+visible_pro_user)
            val menuView: Menu = nav_view.menu
            if (visible_pro_user!!) {
                menuView.findItem(R.id.pro_user_nav).setVisible(true)
            } else {
                menuView.findItem(R.id.pro_user_nav).setVisible(false)
            }

        }*/
    }

    private fun upDateAdminLastPush(get: ResponseAdminLastPush) {
        if (!binding!!.includedLayHome.ivChat.isSelected) {
            try {
                menuNotificationView = findViewById(R.id.view3)
                // universalToast( get.message)
                /*displayPopupWindowAdmin(
                    this,
                    menuNotificationView!!,
                    "<font color=#000000>Message From: </font> <font color=#ED2424>" + "Admin" + "</font>",
                    get.message, "", false
                )*/
            } catch (exception: java.lang.Exception) {
                showAlert("" + exception.message, "Exception")
            }

        }
    }

    private val itemClick = object : ItemClick {
        override fun onItemClick(position: Int, status: ItemClickStatus) {
            when (status) {
                ItemClickStatus.FeedEventLike -> {
                    val eventId = feedsAdapter.getEventId(position)
                    when (eventId.isEmpty()) {
                        true -> universalToast("Invalid event details")
                        false -> likeEvent(eventId)
                    }
                }

                ItemClickStatus.ConversationDelete -> {
                    this@HomeActivity.position = position
                    showAlertDeleteConversation(
                        getString(R.string.alert_delete_conversation),
                        getString(R.string.text_delete),
                        position
                    )
                }

                ItemClickStatus.Unread -> {
                    when (val id = notificationAdapter.id(position)) {
                        -1 -> universalToast("Notification not found.")
                        else -> {
                            read("$id")
                        }
                    }
                }

                ItemClickStatus.Delete -> {
                    when (val id = notificationAdapter.id(position)) {
                        -1 -> universalToast("Notification not found.")
                        else -> {
                            showProgressView(progressBar)
                            delete("$id")
                        }
                    }
                }

                else -> {

                }
            }
        }
    }

    @Inject
    lateinit var sharedPreferencesUtil: SharedPreferencesUtil

    @Inject
    lateinit var firebaseUtil: FirebaseUtil

    private var home = false
    private val notificationAdapter = NotificationAdapter()
    private val feedsAdapter = EventFeedsAdapter()
    private val nearByAdapter = NearByAdapter()
    private val mostRecentlyJoinedUserAdapter = MostRecentlyJoinedUserAdapter()
    private val conversationAdapter = ConversationAdapter()

    private val favoriteUserFragment = FavoriteUserFragment()
    private val favoriteEventFragment = FavoriteEventFragment()
    private var adapter: TabLayoutAdapter? = null
    private var mProfilePresenter: CreateProfileConnector.PresenterOps? = null
    private var mPresenter: HomeConnector.PresenterOps? = null
    private var mPresenterN: NotificationConnector.PresenterOps? = null

    private var navigationUserName: TextView? = null
    private var navigationProUser: ImageView? = null
    private var navigationUserAddress: TextView? = null
    private var navigationUserImage: ImageView? = null
    private var messageUnreadCount = arrayListOf<String>()

    private var gStrProTag: Boolean = false
    private var gStrProTimeLine: Boolean = false
    private var menu: Menu? = null

    var isOpen = false
    private var loadMoreFeed = false
    private var loadMoreNearby = false
    private var loadMoreRecent = false

    private var nextFeed = 1
    private var nextNearby = 1
    private var nextRecent = 1
    private var nearByList: ArrayList<ResponseUserData> = ArrayList()
    private var nearByListRecentOnlyAds: ArrayList<ResponseUserData> = ArrayList()
    private var nearByListSwitchs: ArrayList<ResponseUserData> = ArrayList()

    private var nearByListAdminAds: ArrayList<ResponseUserData> = ArrayList()

    private var adminlastpush: ArrayList<ResponseAdminLastPush> = ArrayList()
    private var mostRecentUserList: ArrayList<ResponseUserData> = ArrayList()
    private var recentlyOnlineList: ArrayList<ResponseUserData> = ArrayList()

    private var linearLayoutManager: LinearLayoutManager? = null
    private var linearLayoutManagerRecent: LinearLayoutManager? = null
    private var gridLayoutManager: GridLayoutManager? = null

    private val requestFeed = RequestFeed()

    private var canRefresh = false
    private var swifeRefresh = false
    private val searchAdapter = SearchItemAdapter()

    private var position = -1
    private var timeLinePushId = ""
    private var timeLinePushType = ""
    private var next = 1
    private var notificationIdRead = ""
    private var mPresenterS: SearchConnector.PresenterOps? = null

    private var search = ""
    private var lat = ""
    private var lng = ""
    private var notificationRefresh: Boolean = false
    private var chatListRefresh: Boolean = false
    lateinit var locationSearch: View

    private var mFusedLocationClient: FusedLocationProviderClient? = null
    private var mLocationRequest: LocationRequest? = null
    private var mLocationCallback: LocationCallback? = null
    lateinit var dialog: Dialog
    private var mCountDownTimer: CountDownTimer? = null
    private var mTimerRunning = false
    private var mTimeLeftInMillis: Long = 0
    private var mEndTime: Long = 0
    private var hasPush: Boolean = false
    private var flagNearbyCall: Boolean = false
    private var firebaseUserId = ""
    lateinit var animLoading: ImageView
    lateinit var recyclerModal: ArrayList<RecycleModel>
    lateinit var recycleGroupConvoAdapter: RecycleGroupConvoAdapter
    private val REQUEST_CONTACTS_PERMISSION = 100
    var unreadCount = 0
    var unreadCountT = 0
    lateinit var chatList: DatabaseReference
    var binding: ActivityHomeBinding? = null
    private lateinit var progressBar: LinearLayout

    var permissions = arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO)
    var requestCode = 1

    var selector = arrayOf("Name", "Location")
    lateinit var mAdViewHome: AdManagerAdView
    lateinit var mAdViewChat: AdManagerAdView
    lateinit var mAdViewNotification: AdManagerAdView
    lateinit var mAdViewEvent: AdManagerAdView
    lateinit var nestedScrollView: NestedScrollView
    lateinit var tvNoNearbyUser: TextView
    lateinit var ivNoUserFound: AppCompatImageView



    @Suppress("unused")
    companion object Typing {
        var typing: ChatActivity.Typing? = null
            internal set
        const val REQ_CODE_VERSION_UPDATE = 6600
        var menuNotificationView: View? = null
        var mContext: OnNotificationReceived? = null
        var popup: PopupWindow? = null
    }

    private val refreshN = SwipeRefreshLayout.OnRefreshListener {
        Handler().postDelayed({
            fetchNotifications()
        }, 100)

    }

    lateinit var bottomNavigationView: BottomNavigationView

    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getIntentData()
        mContext = this
        //  setContentView(R.layout.activity_home)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        progressBar = binding!!.includedLayHome.includedProgress.rlProgress
        component.inject(this@HomeActivity)
        context = this@HomeActivity
        animLoading = findViewById(R.id.animLogo)

        setupActionBarNavigationHome(getString(R.string.title_nearby), true, R.drawable.menu_icon)
        LogManager.logger.i(
            ArchitectureApp.instance!!.tag, "Token is : ${sharedPreferencesUtil.userId()}"
        )
        startLoadingAnim()
        getLocations()
        getLastLocation()
        createLocationRequest()
        /// callForUserProfile(ConstantsApi.FETCH_PROFILE)
        fetchSelfProfile()
        updateUserStatus()
        updateUserDateTime()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestNotificationPermission()
           /// checkContactPermissions()
        } else {
          ///  checkContactPermissions()
            // Notification permission is granted automatically for Android versions below 13
            Log.e(
                "Permission",
                " Notification permission is granted automatically for Android versions below 13"
            )
        }

        //bandage()

        //  ShortcutBadger.applyCount(context, sharedPreferencesUtil.fetchNotificationCount().toInt())
        //  setBadge( this@HomeActivity, sharedPreferencesUtil.fetchNotificationCount().toInt())

        if (intent.extras != null) {

            val userName = intent.extras!!.get("userName").toString()
            val isNewUser = intent.extras!!.get("newUser").toString()

            if (userName != "null" && isNewUser == "true") {

                showSuccessPopUp(userName)
            }

            Log.d("user111111", userName + "  " + isNewUser)

        }
        //getUserId()

        /// google Banner Ads implement
        tvNoNearbyUser = findViewById(R.id.tv_no_user_found)
        ivNoUserFound = findViewById(R.id.ivNoUserFound)
        mAdViewHome = findViewById(R.id.mAdViewHome)
        mAdViewChat = findViewById(R.id.mAdViewChat)
        mAdViewNotification = findViewById(R.id.mAdViewNotification)
        mAdViewEvent = findViewById(R.id.mAdViewEvent)
        val profile = sharedPreferencesUtil.fetchUserProfile()
        if (profile.isProMembership && profile.currentPlanInfo!!.addFree == 1) {
            mAdViewHome.visibility = View.GONE
            mAdViewNotification.visibility = View.GONE
            mAdViewChat.visibility = View.GONE
            mAdViewEvent.visibility = View.GONE
        } else {
            mAdViewHome.visibility = View.VISIBLE
            mAdViewChat.visibility = View.VISIBLE
            mAdViewNotification.visibility = View.VISIBLE
            mAdViewEvent.visibility = View.VISIBLE
            loadBannerAdHome()
            loadBannerAdChat()
            loadBannerAdNotification()
            loadBannerAdEvent()

            /// Show for interstitial ad
            val interstitialAdManager: InterstitialAdManager =
                InterstitialAdManager.getInstance(this)
            interstitialAdManager.setUserLoggedIn(true)
            interstitialAdManager.loadInterstitialAd(this)
        }
        init()

        Log.v("Coming two time", " coming 22")
        Log.v("Coming two time", "sharedPreferencesUtil.fetchNotificationCount().toInt()")

        /*checkPermission(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.MANAGE_EXTERNAL_STORAGE,
            1
        )*/

    }


    private fun loadBannerAdHome() {
        MobileAds.initialize(this) {}
        val adRequest = AdManagerAdRequest.Builder().build()
        mAdViewHome.loadAd(adRequest)


        mAdViewHome.adListener = object : AdListener() {
            override fun onAdClicked() {
                // Code to be executed when the user clicks on an ad.
                Log.e("AdViewHome", "onAdClicked@#")
            }

            override fun onAdClosed() {
                // Code to be executed when the user is about to return
                // to the app after tapping on an ad.
                Log.e("mAdViewHome", "onAdClicked@#")
            }

            override fun onAdFailedToLoad(adError: LoadAdError) {
                // Code to be executed when an ad request fails.
                Log.e("mAdViewHome", "onAdFailedToLoad@#_nearby")
            }

            override fun onAdImpression() {
                // Code to be executed when an impression is recorded
                // for an ad.
                Log.e("mAdViewHome", "onAdImpression@#")
            }

            override fun onAdLoaded() {
                // Code to be executed when an ad finishes loading.
                Log.e("mAdViewHome", "onAdLoaded!@#")
                //  Toast.makeText(this@HomeActivity,"Ad Loaded", Toast.LENGTH_SHORT).show()

            }

            override fun onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
                Log.e("Adds", "onAdOpened**")
            }
        }


    }

    private fun loadBannerAdChat() {
        MobileAds.initialize(this) {}
        val adRequest = AdManagerAdRequest.Builder().build()
        mAdViewChat.loadAd(adRequest)

        mAdViewChat.adListener = object : AdListener() {
            override fun onAdClicked() {
                // Code to be executed when the user clicks on an ad.
                Log.e("mAdViewChat", "onAdClicked@#")
            }

            override fun onAdClosed() {
                // Code to be executed when the user is about to return
                // to the app after tapping on an ad.
                Log.e("mAdViewChat", "onAdClicked@#")
            }

            override fun onAdFailedToLoad(adError: LoadAdError) {
                // Code to be executed when an ad request fails.
                Log.e("mAdViewChat-Home", "onAdFailedToLoad@#-chat")
            }

            override fun onAdImpression() {
                // Code to be executed when an impression is recorded
                // for an ad.
                Log.e("mAdViewChat", "onAdImpression@#")
            }

            override fun onAdLoaded() {
                // Code to be executed when an ad finishes loading.
                Log.e("mAdViewChat", "onAdLoaded!@#")
                // Toast.makeText(this@HomeActivity,"Ad Loaded", Toast.LENGTH_SHORT).show()

            }

            override fun onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
                Log.e("mAdViewChat", "onAdOpened**")
            }
        }


    }

    private fun loadBannerAdNotification() {
        MobileAds.initialize(this) {}
        val adRequest = AdManagerAdRequest.Builder().build()
        mAdViewNotification.loadAd(adRequest)

        mAdViewNotification.adListener = object : AdListener() {
            override fun onAdClicked() {
                // Code to be executed when the user clicks on an ad.
                Log.e("mAdViewNotification", "onAdClicked@#")
            }

            override fun onAdClosed() {
                // Code to be executed when the user is about to return
                // to the app after tapping on an ad.
                Log.e("mAdViewNotification", "onAdClicked@#")
            }

            override fun onAdFailedToLoad(adError: LoadAdError) {
                // Code to be executed when an ad request fails.
                Log.e("mAdViewNotification", "onAdFailedToLoad@#")
            }

            override fun onAdImpression() {
                // Code to be executed when an impression is recorded
                // for an ad.
                Log.e("mAdViewNotification", "onAdImpression@#")
            }

            override fun onAdLoaded() {
                // Code to be executed when an ad finishes loading.
                Log.e("mAdViewNotification", "onAdLoaded!@#")
                ///  Toast.makeText(this@HomeActivity,"Ad Loaded", Toast.LENGTH_SHORT).show()

            }

            override fun onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
                Log.e("mAdViewNotification", "onAdOpened**")
            }
        }


    }

    private fun loadBannerAdEvent() {
        MobileAds.initialize(this) {}
        val adRequest = AdManagerAdRequest.Builder().build()
        mAdViewEvent.loadAd(adRequest)

        mAdViewEvent.adListener = object : AdListener() {
            override fun onAdClicked() {
                // Code to be executed when the user clicks on an ad.
                Log.e("mAdViewEvent", "onAdClicked@#")
            }

            override fun onAdClosed() {
                // Code to be executed when the user is about to return
                // to the app after tapping on an ad.
                Log.e("mAdViewEvent", "onAdClicked@#")
            }

            override fun onAdFailedToLoad(adError: LoadAdError) {
                // Code to be executed when an ad request fails.
                Log.e("mAdViewEvent", "onAdFailedToLoad@#-event")
            }

            override fun onAdImpression() {
                // Code to be executed when an impression is recorded
                // for an ad.
                Log.e("mAdViewEvent", "onAdImpression@#")
            }

            override fun onAdLoaded() {
                // Code to be executed when an ad finishes loading.
                Log.e("mAdViewEvent", "onAdLoaded!@#")
                ///   Toast.makeText(this@HomeActivity,"Ad Loaded", Toast.LENGTH_SHORT).show()

            }

            override fun onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
                Log.e("mAdViewEvent", "onAdOpened**")
            }
        }


    }

    private fun startLoadingAnim() {

        val animation = AnimationUtils.loadAnimation(
            applicationContext, R.anim.blink_anim
        )

        animLoading.startAnimation(animation)
    }

    fun checkPermission(permission: String, permission2: String, requestCode: Int): Boolean {
        // Checking if permission is not granted
        return if (ContextCompat.checkSelfPermission(
                this, permission
            ) == PackageManager.PERMISSION_DENIED
        ) {
            ActivityCompat.requestPermissions(
                this, arrayOf(permission, permission2), requestCode
            )
            Log.d("AppPermissionW", "denied")
            false
        } else {
            //Toast.makeText(activity, "You must give permission", Toast.LENGTH_SHORT).show();
            true
        }
    }

    private fun showSuccessPopUp(userName: String) {

        val dialog = Dialog(this@HomeActivity)

        dialog.setCancelable(true)

        //val activity = context as AppCompatActivity

        val view = layoutInflater.inflate(R.layout.profile_complete_dialog, null)

        dialog.setContentView(view)

        dialog.window!!.attributes.windowAnimations = R.style.DialogAnimation

        if (dialog.window != null) {
            dialog.window!!.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
        dialog.window!!.setGravity(Gravity.CENTER)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val congratUserTxt = view.findViewById<TextView>(R.id.congratUser_txt)
        val keepIt = view.findViewById<Button>(R.id.keepIt_btn)
        val goPro = view.findViewById<Button>(R.id.goPro_btn)

        congratUserTxt.text = "Congratulations ${userName}"

        keepIt.setOnClickListener {

            dialog.dismiss()
            //profile()
            //switchActivity(Constants.Intent.Home, true, Bundle())
        }

        goPro.setOnClickListener {

            AlertBuyPremium.Builder(this@HomeActivity, Constants.DialogTheme.NoTitleBar).build()
                .show()
        }

        dialog.show()
    }

    private fun getLocations() {
        context = this
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        mLocationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult) {
                super.onLocationResult(p0)
                p0!!.lastLocation?.let { onNewLocation(it) }
            }
        }
    }

    @Suppress("SENSELESS_COMPARISON")
    private fun onNewLocation(location: Location) {
        if (null != location) {

            sharedPreferencesUtil.saveLocation(
                Gson().toJson(
                    LatLng(location.latitude, location.longitude)
                )
            )
            val saveLocation = LocationModel()
            saveLocation.setLatitude(location.latitude)
            saveLocation.setLongitude(location.longitude)
            saveLocation.saveLocation()

        }
    }

    private fun getLastLocation() {
        try {
            mFusedLocationClient!!.lastLocation.addOnCompleteListener { task ->

                    if (task.isSuccessful && task.result != null) {

                        sharedPreferencesUtil.saveLocation(
                            Gson().toJson(
                                LatLng(task.result!!.latitude, task.result!!.longitude)
                            )
                        )
                        val saveLocation = LocationModel()
                        saveLocation.setLatitude(task.result!!.latitude)
                        saveLocation.setLongitude(task.result!!.longitude)
                        saveLocation.saveLocation()

                    }
                }
        } catch (unlikely: SecurityException) {

        }

    }

    private fun createLocationRequest() {
        mLocationRequest = LocationRequest.create()
        mLocationRequest!!.interval = 10 * 60 * 1000
        mLocationRequest!!.maxWaitTime = 60 * 60 * 1000
        mLocationRequest!!.fastestInterval = (10 * 60 * 1000) / 2
        mLocationRequest!!.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }

    private fun getIntentData() {
        when (intent.hasExtra(Constants.Notification.COLLAPSE_KEY)) {
            true -> {
                hasPush = true

                LogManager.logger.i(
                    ArchitectureApp.instance!!.tag, "Chat Notification : ${intent.extras}"
                )

                if (intent.extras!!["notificationId"].toString() != "") {
                    read(intent.extras!!["notificationId"].toString())
                }
                offlineNotification(RemoteMessage(intent.extras))
            }

            false -> {}
        }

        if (intent.hasExtra(Constants.IntentDataKeys.TimeLinePushId)) {
            hasPush = true
            timeLinePushId = intent.getStringExtra(Constants.IntentDataKeys.TimeLinePushId)!!
            timeLinePushType = intent.getStringExtra(Constants.IntentDataKeys.TimeLinePost)!!

            if (intent.hasExtra(Constants.IntentDataKeys.NotificationId)) {

                notificationIdRead =
                    intent.getStringExtra(Constants.IntentDataKeys.NotificationId)!!
                if (notificationIdRead != "") {
                    read(notificationIdRead)
                    notificationIdRead = ""
                }

            }

        }

    }

    private fun read(id: String) {
        if (null == mPresenter) mPresenter = HomePresenter(presenter)

        run {
            mPresenter!!.readNotificationObject(RequestReadNotification().apply {
                notificationId = id
            })
            mPresenter!!.callRetrofit(ConstantsApi.READ_NOTIFICATION)
        }
    }

    private fun saveUserContacts(contactsList: ArrayList<Contact>) {
        if (null == mPresenter) mPresenter = HomePresenter(presenter)

        run {
            mPresenter!!.saveUserContactObject(SaveContactDetailsReq().apply {
                contact_details = contactsList
            })
            mPresenter!!.callRetrofit(ConstantsApi.SAVE_USER_CONTACTS)
        }
    }

    private fun checkContectPermissions() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_CONTACTS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.READ_CONTACTS), REQUEST_CONTACTS_PERMISSION
            )
        } else {
            if (sharedPreferencesUtil.fetchUserProfile().is_contact_list_added == 0) {
                Log.e("LoadContact%", "Call")
                loadContacts()
            }
        }
    }

    private fun checkContactPermissions() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_CONTACTS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Check if the user denied the permission previously and we should show rationale
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_CONTACTS)) {
                // Show an explanation to the user
                showPermissionRationale()
            } else {
                // Directly request the permission
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_CONTACTS),
                    REQUEST_CONTACTS_PERMISSION
                )
            }
        } else {
            // Permission already granted, proceed with reading contacts
            if (sharedPreferencesUtil.fetchUserProfile().is_contact_list_added == 0) {
                Log.e("LoadContact%", "Call")
                loadContacts()
            }
        }
    }

    // Show a rationale dialog to explain why the app needs the permission
    private fun showPermissionRationale() {
        AlertDialog.Builder(this)
            .setTitle("Contact Permission Required")
            .setMessage("This app needs access to your contacts to load your contact list.")
            .setPositiveButton("Allow") { _, _ ->
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_CONTACTS),
                    REQUEST_CONTACTS_PERMISSION
                )
            }
            .setNegativeButton("Deny") { dialog, _ ->
                dialog.dismiss() // User denied permission
            }
            .show()
    }


    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_CONTACTS_PERMISSION -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    loadContacts()
                } else {
                    Toast.makeText(this, "Permission denied to read contacts", Toast.LENGTH_SHORT)
                        .show()
                }
                return
            }

            99 -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // Permission is granted, you can show notifications
                    Log.e("NotificationPermission", "permission_Grant_activity")
                } else {
                    // Permission is denied, handle accordingly
                    Log.e("NotificationPermission", "permission_Denied_activity")
                }
            }
        }
    }

    private fun loadContacts() {
        val contactsList = kotlin.collections.ArrayList<Contact>()

        val projection = arrayOf(
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.NUMBER
        )

        val cursor = contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI, projection, null, null, null
        )

        cursor?.use {
            val nameIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
            val numberIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)

            while (it.moveToNext()) {
                val name = it.getString(nameIndex)
                val number = it.getString(numberIndex)
                contactsList.add(Contact(name, number))
            }
        }

        displayContacts(contactsList)
        if (sharedPreferencesUtil.fetchUserProfile().is_contact_list_added == 0) {
            Log.e("LoadContact%", "saveUserContacts_Call")
            saveUserContacts(contactsList)
        }
    }

    // data class Contact(val name: String, val number: String)

    private fun displayContacts(contacts: List<Contact>) {
        // Update your UI with the contact list (e.g., using a RecyclerView)
        contacts.forEach {
            Log.e("Contact", "Name: ${it.name}, Number: ${it.mobile_no}")
        }
    }

    /* private fun fetchNotifications() {
       // showProgressView(progressBar)
         if (null == mPresenter)
             mPresenter = HomePresenter(presenter)

         run {
             mPresenter!!.callRetrofit(ConstantsApi.NOTIFICATION)
         }
     }*/

    @SuppressLint("NewApi")
    override fun onResume() {
        super.onResume()
        // below comment for notification click event
        /* val senderIdN = intent.getStringExtra(Constants.Notification.SENDER_ID)
         Log.e("Notification_DAta","senderIdN: $senderIdN")
         val RoomId = intent.getStringExtra(Constants.Notification.ROOM_ID)
         val senderImg = intent.getStringExtra(Constants.Notification.IMAGE)
         val senderName = intent.getStringExtra(Constants.Notification.USER_NAME)
         val type = intent.getStringExtra(Constants.Notification.TYPE)
         Log.e("Notification_DAta","RoomId: $RoomId")

        if (type != null && type.isNotEmpty()){
            when(type.equals("userChat")){
                true->{
                    senderIdN?.let{
                        if (RoomId != null) {
                            universalToast(RoomId)
                            val intent = Intent(
                                context,
                                ChatActivity::class.java
                            )
                            intent.putExtra("ChatRoomId", RoomId)
                            intent.putExtra("otherUserId", senderIdN)
                            intent.putExtra("otherUserName", senderName)
                            intent.putExtra("otherUserImg", senderImg)
                            intent.putExtra("firstTime", true)
                            //   intent.putExtra("IsUserConnected", users.get(pos).IsUserConnected)
                            context!!.startActivity(intent)
                        }
                    }
                }false->{
                senderIdN?.let{
                    universalToast(senderIdN)
                        startActivity(
                            Intent(Constants.Intent.ProfileUser).putExtras(
                                Bundle().apply {
                                    putInt(Constants.IntentDataKeys.UserId, senderIdN.toInt())
                                    putInt(Constants.IntentDataKeys.LINK,0)
                                }))
                }
                }
            }
        }else{
            Log.e("Notification_DAta","Not Comming By Notification Else")
        }*/

        //bottomNavigationView.selectedItemId = R.id.bottom_nav_timeline

        //setGroupChat()

        //swipeRefreshChat.isRefreshing = true
        Log.d("onResume", "resume")
        if (Constants.Variable.isProfile) {
            //Constants.Variable.isProfile = false
        } else {
            bottomNavigationView.selectedItemId = R.id.bottom_nav_nearBy
        }

        if (hasPush) {
            hasPush = false
        } else {

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


        // switchActivityOnly(Constants.Intent.Home, true)

        //popup!!.sh()

        /*if (popup != null) {
            if (popup!!.isShowing()) {
                popup!!.dismiss()
            }
        }*/


        /*  // resetTimer()
          val prefs = getSharedPreferences("prefs", MODE_PRIVATE)
          mTimeLeftInMillis = prefs.getLong("millisLeft", Constants.TimerRefresh. START_TIME_IN_MILLIS)
          mTimerRunning = prefs.getBoolean("timerRunning", false)
         // updateCountDownText()
        //  updateButtons()
          if (mTimerRunning) {
              mEndTime = prefs.getLong("endTime", 0)
              mTimeLeftInMillis = mEndTime - System.currentTimeMillis()
              if (mTimeLeftInMillis < 0) {
                  mTimeLeftInMillis = 0
                  mTimerRunning = false
                  //  updateCountDownText()
                  //  updateButtons()
                  //  if(timeLeftFormatted == "00:00"){
                  resetTimer()

                  Toast.makeText(this, "done", Toast.LENGTH_SHORT).show()
                  Log.d("TAG", "onResume:  In done")
              } else {
                  startTimer()
              }
          }*/

        //  timeline()


        /* var lasttrackTime    = sharedPreferencesUtil.featchTimmerTime()
         var currentbackTime : Long  = System.currentTimeMillis()



        Log.e("lasttrackTime",lasttrackTime);
        System.currentTimeMillis()
        if(lasttrackTime!="") {

            Log.e("lasttrackTime If",lasttrackTime);
          //  val minutes = (lasttrackTime.toLong() / 1000).toInt() / 60
          //  val seconds = (lasttrackTime.toLong() / 1000).toInt() % 60
           // val seconds = TimeUnit.MILLISECONDS.toSeconds(lasttrackTime.toLong())

          //  var seconds = currentbackTime - lasttrackTime.toLong()
            val currentSeconds =  ( (   currentbackTime.toLong() -  lasttrackTime.toLong()) / 1000).toInt()

           // Log.e("lasttrackTime seconds ",seconds.toString());
            Log.e("lasttrackTime minutes ",currentSeconds.toString());
            if (currentSeconds >= Constants.TimerRefresh.START_TIME_IN_SEC) {
               //  switchActivityOnly(SplashActivity::class.java, true)
               startActivity(Intent(this, SplashActivity::class.java))
                finish()

            }
        }*/


        //   val timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)

        setProfileData()
        initiateAdminPush()
        // swipeRefreshChat.setOnRefreshListener(refresh)

        // getCurrentVersion()
        //checkRequiredPermissions()
        setNavigationUserProfile()
        when (canRefresh) {
            true -> {
                if (notificationRefresh) {
                    notificationRefresh = false
                    refreshUI()
                }

                //adminConversation()
                //showLocalConversation()

                // refreshUI()
                canRefresh = false
            }

            false -> {}
        }
        RecyclerViewClick.enableClick(itemClick)

        if (null != menu) {
            setCount(this@HomeActivity, sharedPreferencesUtil.fetchNotificationCount())
        }


//        setBadge(this@HomeActivity, sharedPreferencesUtil.fetchNotificationCount().toInt())
    }

    private fun updateUserStatus() {
        val user = sharedPreferencesUtil.fetchUserProfile()
        if (user.id.isNotEmpty()) {
            firebaseUtil.saveUserProfile(
                ProfileFirebase(
                    "",
                    getApprovedImage(user.images),
                    true,
                    user.id.toInt(),
                    user.name,
                    sharedPreferencesUtil.fetchDeviceToken(),
                    sharedPreferencesUtil.fetchSettings().allowPush != 0,
                    1,
                    "" + getUTCTime()
                )
            )
        }
    }

    private fun refreshUI() {
//        if (binding!!.includedLayHome.ivFeed.isSelected) {
//            nextFeed = 1
//            feedsAdapter.clearFeeds()
//            showProgressView(rl_progress)
//            feeds()
//        }

        if (binding!!.includedLayHome.ivNearby.isSelected) {
            nextNearby = 1
            nearByListAdminAds.clear()
            nearByList.clear()
            // nearByListAdminAds.clear()
            nearByAdapter.clearNearBy()

            // initiateAdminADs()
            showProgressView(progressBar)
            Log.e("CallNearBy%", "222")
            getNearbyUser()
        }

        /* if (binding!!.includedLayHome.ivHeart.isSelected) {
             when (tabLayoutFavorite.selectedTabPosition) {
                 0 -> {
                     if (favoriteUserFragment.isAdded && favoriteUserFragment.isVisible)
                         favoriteUserFragment.favoriteUser()
                 }
                 1 -> {
                     if (favoriteEventFragment.isAdded && favoriteEventFragment.isVisible)
                         favoriteEventFragment.favoriteEvent()
                 }
             }
         }*/

        if (binding!!.includedLayHome.ivChat.isSelected) {
            //   Log.e("yes", "refreshUI+")

            //      conversationAdapter.clearAll()
            showProgressView(progressBar)
            //adminConversation()
            //showLocalConversation()
        }
    }

    override fun onPause() {
        super.onPause()
        //   Log.e("yes", "pause+")


        /* val prefs = getSharedPreferences("prefs", MODE_PRIVATE)
         val editor = prefs.edit()
         editor.putLong("millisLeft", mTimeLeftInMillis)
         editor.putBoolean("timerRunning", mTimerRunning)
         editor.putLong("endTime", mEndTime)
         editor.apply()
         if (mCountDownTimer != null) {
             mCountDownTimer!!.cancel()
         }*/

        // Log.e("saveTimmerTime",System.currentTimeMillis().toString())
        //  sharedPreferencesUtil.saveTimmerTime(System.currentTimeMillis().toString())
        sharedPreferencesUtil.saveTimmerTime(System.currentTimeMillis().toString())
        LogManager.logger.i(
            ArchitectureApp.instance!!.tag, "onPause ::: " + System.currentTimeMillis().toString()
        )


        when (canRefresh) {
            false -> canRefresh = true
            else -> {}
        }
        RecyclerViewClick.disableClick()
    }

    override fun onDestroy() {
        super.onDestroy()
        sharedPreferencesUtil.saveTimmerTime("")

        Log.e("ondestroy", "onDestroy:===== ")
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        this@HomeActivity.menu = menu
        menuInflater.inflate(R.menu.menu_home, menu)
        locationSearch = menu!!.findItem(R.id.menu_location).actionView!!

        val profile = sharedPreferencesUtil.fetchLocation()

        val geocoder = Geocoder(context!!, Locale.ENGLISH)
        var cityName = ""
        try {
            val addresses: List<Address> = geocoder.getFromLocation(
                profile.latitude, profile.longitude, 1
            )!!

            var cityName = ""

            if (addresses.size > 0) {

                cityName = addresses[0].locality
            }
        } catch (e: Exception) {
            Log.e("loaction@", "Exception- " + e.message)
            Toast.makeText(this@HomeActivity, "Please connect your connection", Toast.LENGTH_LONG)
                .show()
        }

        ////  locationSearch.location_txt.text = cityName
        locationSearch.setOnClickListener {
            /*if ((lat == "0.0") && (lng == "0.0")) {
                binding!!.includedLayHome.tvNoLocation.visibility = View.VISIBLE
                tvNoLocation.text = String.format(
                    getString(R.string.text_rational_location), getString(R.string.app_name)
                )
                hideProgressView(rl_progress)
                checkRequiredPermissions()
            } else {
                autoPlacePicker()
            }*/
        }
//        menuNotificationView = menu!!.findItem(R.id.menu_notification).actionView
        return super.onCreateOptionsMenu(menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        when {
            binding!!.includedLayHome.includedLChat.rlChat.visibility == View.VISIBLE -> {
                menu!!.getItem(2).isVisible = false
                menu.getItem(3).isVisible = false
                menu.getItem(0).isVisible = false
                menu.getItem(4).isVisible = false
                menu.getItem(1).isVisible = false
                val profile = sharedPreferencesUtil.fetchUserProfile()

                //menu.getItem(5).isVisible = profile.isProMembership
                menu.getItem(5).isVisible = false

            }

            binding!!.includedLayHome.includedNotification.notificationLay.visibility == View.VISIBLE -> {
                Log.e("Change", "notification_lay")

                /// menu!!.getItem(3).isVisible = notificationAdapter.itemCount > 0

                menu!!.getItem(2).isVisible = false
                menu.getItem(3).isVisible = true
                menu.getItem(0).isVisible = false
                menu.getItem(4).isVisible = false
                menu.getItem(1).isVisible = false
                menu.getItem(5).isVisible = false
            }

            binding!!.includedLayHome.includedTimeline.rlTimeLine.visibility == View.VISIBLE -> {
                Log.e("timeline", "timelineEnter")
                menu!!.getItem(0).isVisible = false
                menu.getItem(1).isVisible = false
                menu.getItem(2).isVisible = false
                menu.getItem(3).isVisible = true
                menu.getItem(4).isVisible = false
                menu.getItem(5).isVisible = false

                setCount(this@HomeActivity, sharedPreferencesUtil.fetchNotificationCount())
            }


            binding!!.includedLayHome.includedFeed.rlFeeds.visibility == View.VISIBLE -> {
                menu!!.getItem(0).isVisible = false
                menu.getItem(1).isVisible = false
                menu.getItem(3).isVisible = false
                menu.getItem(4).isVisible = false
                menu.getItem(5).isVisible = false
                setCount(this@HomeActivity, sharedPreferencesUtil.fetchNotificationCount())
                //       setBadge(this@HomeActivity, sharedPreferencesUtil.fetchNotificationCount().toInt())
            }

            else -> {
                menu!!.getItem(0).isVisible = true
                menu.getItem(1).isVisible = false
                menu.getItem(2).isVisible = false
                menu.getItem(3).isVisible = false
                menu.getItem(4).isVisible = false
                menu.getItem(5).isVisible = false
                setCount(this@HomeActivity, sharedPreferencesUtil.fetchNotificationCount())
//                setBadge(this@HomeActivity, sharedPreferencesUtil.fetchNotificationCount().toInt())
            }
        }
//        menuNotificationView = menu!!.findItem(R.id.menu_notification).actionView
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when (item.itemId) {/*android.R.id.home -> {
                home = true
                callForSettings()
                drawerLayout.openDrawer(GravityCompat.END)
                true
            }*/

            R.id.menu_location -> {/*if ((lat == "0.0") && (lng == "0.0")) {
                    tvNoLocation.visibility = View.VISIBLE
                    binding!!.includedLayHome.tvNoLocation.text = String.format(
                        getString(R.string.text_rational_location), getString(R.string.app_name)
                    )
                    hideProgressView(rl_progress)
                    checkRequiredPermissions()
                } else {
                    autoPlacePicker()
                }*/
                true
            }

            R.id.menu_refresh -> {/* showProgressView(rl_progress)
                 Handler().postDelayed({
                     timeline()
                     true

                 }, 1000)*/

                when (notificationAdapter.itemCount > 0) {
                    true -> showAlertClear("Are you sure, you want to clear all notifications?")
                    false -> {}
                }
                true
            }

            R.id.menu_search -> {
                if ((lat == "0.0") && (lng == "0.0")) {/*tvNoLocation.visibility = View.VISIBLE
                    tvNoLocation.text = String.format(
                        getString(R.string.text_rational_location), getString(R.string.app_name)
                    )*/
                    //hideProgressView(rl_progress)
                    //checkRequiredPermissions()
                } else {
                    var selected = -1
                    when {
                        binding!!.includedLayHome.ivNearby.isSelected -> selected = 0
                        binding!!.includedLayHome.ivFeed.isSelected -> selected = 1
                        binding!!.includedLayHome.ivHeart.isSelected -> selected = 3
                        /// if (tabLayoutFavorite.selectedTabPosition == 0) 3 else 4
                        binding!!.includedLayHome.ivChat.isSelected -> selected = 2
                        binding!!.includedLayHome.ivTimeLine.isSelected -> selected = 5

                    }
                    switchActivity(Constants.Intent.Search, false, Bundle().apply {
                        putInt(Constants.IntentDataKeys.Selected, selected)
                    })
                }
                true
            }

            R.id.menu_notification -> {
                if ((lat == "0.0") && (lng == "0.0")) {/*tvNoLocation.visibility = View.VISIBLE
                    tvNoLocation.text = String.format(
                        getString(R.string.text_rational_location), getString(R.string.app_name)
                    )*/
                    //hideProgressView(rl_progress)
                    //checkRequiredPermissions()
                } else {
                    notificationRefresh = true
                    switchActivity(Constants.Intent.Notification, false, Bundle())
                }
                true
            }

            R.id.menu_add -> {
                Log.e("onOptionsItemSelected", "menu_add_Click")
                if (sharedPreferencesUtil.fetchUserProfile().isProMembership) {
                    if (sharedPreferencesUtil.fetchUserProfile().eventPostLimit!! > 0) {
                        autoPlacePickerEvent()
                        /// switchActivity(Constants.Intent.CreateEvent, false, Bundle())
                    } else if (sharedPreferencesUtil.fetchUserProfile().currentPlanInfo!!.planId == 2) {
                        showEventAlertDialog()
                    } else {
                        universalToast(getString(R.string.event_limit_note))
                    }
                } else {
                    showEventAlertDialog()
                    // universalToast("Free membership doesn't allow to create Event.")
                }
                true
            }

            R.id.menu_addChat -> {
                switchActivity(Constants.Intent.NewGroup, false, Bundle())
                true
            }

            R.id.menu_drawerBtn -> {

                home = true
                callForSettings()
                binding!!.drawerLayout.openDrawer(GravityCompat.END)

                true
            }

            R.id.menu_clear -> {
                when (notificationAdapter.itemCount > 0) {
                    true -> showAlertClear("Are you sure, you want to clear all notifications?")
                    false -> {}
                }
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() {

        /* if (materialSheetFab.isSheetVisible()) {
             materialSheetFab.hideSheet();
         }
 */
        when (home) {
            true -> {
                when (binding!!.drawerLayout.isDrawerOpen(GravityCompat.END)) {
                    true -> {
                        binding!!.drawerLayout.closeDrawer(GravityCompat.END)
                    }

                    false -> {}
                }
            }

            false -> {
                showAlert(getString(R.string.alert_exit), getString(R.string.alert_title_exit))
            }
        }
    }

    private fun showEventAlertDialog() {

        val dialog = Dialog(this)

        dialog.setCancelable(true)

        val view = layoutInflater.inflate(R.layout.custom_exit_dialog, null)

        dialog.setContentView(view)

        dialog.window!!.attributes.windowAnimations = R.style.DialogAnimation

        if (dialog.window != null) {
            dialog.window!!.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
            )
        }
        dialog.window!!.setGravity(Gravity.CENTER)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val dialogHead = view.findViewById<TextView>(R.id.dialog_title)
        val dialogContent = view.findViewById<TextView>(R.id.dialog_content)
        val cancelBtn = view.findViewById<Button>(R.id.noExitBtn)
        val confirm = view.findViewById<Button>(R.id.yesExitBtn)
        Log.e("Profile", "Privet_Alert_dialog_Box")
        dialogHead.text = "Create Event"
        dialogHead.visibility = View.VISIBLE

        if (sharedPreferencesUtil.fetchUserProfile().currentPlanInfo!!.planId == 2) {
            dialogContent.text = getString(R.string.upgrade_plan_for_event)
            cancelBtn.text = "Okay"
            confirm.text = "Upgrade now"
            confirm.visibility = View.GONE
        } else {
            dialogContent.text = getString(R.string.free_user_event_note)
            cancelBtn.text = "Not now"
            confirm.text = "Subscribe now"
        }

        cancelBtn.setOnClickListener {
            dialog.cancel()
        }

        confirm.setOnClickListener {
            AlertBuyPremium.Builder(this@HomeActivity, Constants.DialogTheme.NoTitleBar).build()
                .show()
            dialog.dismiss()
        }

        dialog.show()

    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun init() {
        //resetTimer()
        //  startTimer()
        //querySubscriptions()
        if (sharedPreferencesUtil.isUserLogin()) {
            getSharedPreferences("isUserLogin", MODE_PRIVATE).edit().putBoolean("isUserLogin", true)
                .apply()
        }
        setNavBottomBadgeCount()
        swifeRefresh = false

        val location = sharedPreferencesUtil.fetchLocation()

        lat = "${location.latitude}"
        lng = "${location.longitude}"

        binding!!.drawerLayout.addDrawerListener(drawerListener)
        binding!!.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)

        binding!!.includedLayHome.includedFeed.swipeRefresh.setOnRefreshListener(refresh)
        binding!!.includedLayHome.includedLayNearby.swipeRefreshNearby.setOnRefreshListener(refresh)
        binding!!.includedLayHome.includedLChat.swipeRefreshChat.setOnRefreshListener(refresh)
        binding!!.includedLayHome.includedNotification.swipeRefreshNotification.setOnRefreshListener(
            refreshN
        )
        // binding!!.includedLayHome.includedFeed.swipeRefreshTimeline.setOnRefreshListener(refresh)

        initiateAdminPush()
        initiateFeedsUI()
        initiateNearByUI()
        initiateMostRecentUI()
        //initiateConversationUI()
        //initiateAdminADs()
        initiateBottomNav()

        setNavigationHeaderData()

        if (Constants.Variable.isProfile) {
            setSelectedBottomBar(binding!!.includedLayHome.ivNearby)
            Constants.Variable.isProfile = false
        }

        clickListener()
        nestedScrollView = findViewById(R.id.scrollViewHome)
        nestedScrollView.viewTreeObserver.addOnScrollChangedListener {
            val scrollY = nestedScrollView.scrollY
            val scrollHeight = nestedScrollView.height
            val childHeight = nestedScrollView.getChildAt(0).height
            // Check if the scroll position is at the bottom
            val isAtBottom = scrollY + scrollHeight >= childHeight
            if (isAtBottom) {
                // NestedScrollView is scrolled to the bottom
                Log.e("nestedScroolView1", "scrollIS-Bottom")
                nestedScroolView()
            }

            closeMenuFilter()
        }

        binding!!.includedLayHome.includedLayNearby.nearByClear.setOnClickListener {

            binding!!.includedLayHome.includedFeed.swipeRefresh.isRefreshing = true
            refresh.onRefresh()
            //home.nearBy_select.text = "All"
            //fabFilterNew.visibility = View.VISIBLE
            binding!!.includedLayHome.includedLayNearby.nearByClear.visibility = View.GONE

            val location = sharedPreferencesUtil.fetchLocation()
            search = ""
            requestFeed.searchPlace = ""
            lat = "${location.latitude}"
            lng = "${location.longitude}"
            //rlLocationSearch.visibility = View.GONE
            //view1.visibility = View.GONE
            //tvLocation.text = search
            //nearBy_search.setText("")
            // showProgressView(rl_progress)
            //swipeRefreshNearby.visibility = View.VISIBLE
            nearByListRecentOnlyAds.clear()
            nearByListSwitchs.clear()

            nearByList.clear()
            nearByListAdminAds.clear()
            nearByAdapter.clearNearBy()
            nextNearby = 1

            //initiateAdminADs()
            getNearbyUser()

        }

        binding!!.includedLayHome.includedLayNearby.nearBySelect.text = "All"

        binding!!.includedLayHome.includedLChat.searchChat.addTextChangedListener(object :
            TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                conversationAdapter.filter.filter(s)
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })

        /*%%---  Set Group and Personal Chat fragment & Tab Layout Comment below  &&
          * Remove the tabLayout badge from PersonalChatFragment ---%%%*/
        val nearAdapter = CustomViewAdapter(supportFragmentManager)
        nearAdapter.addFragment(PersonalChatFragment(), "Personal Chat")
        /// nearAdapter.addFragment(GroupChatFragment(), "Group Chat")
        binding!!.includedLayHome.includedLChat.chatTabLay.visibility = View.GONE

        //nearAdapter.addFragment(CallHistoryFragment(), "Call History")

        //nearByViewPager.adapter = nearAdapter
        binding!!.includedLayHome.includedLChat.chatViewPager.adapter = nearAdapter

        //nearByTab.setupWithViewPager(nearByViewPager)
        binding!!.includedLayHome.includedLChat.chatTabLay.setupWithViewPager(binding!!.includedLayHome.includedLChat.chatViewPager)

        binding!!.includedLayHome.includedLChat.chatTabLay.setOnTabSelectedListener(object :
            TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                binding!!.includedLayHome.includedLChat.chatViewPager.currentItem = tab.position

            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })

        with(binding!!.includedLayHome.includedNotification.rvNotifications) {
            layoutManager = LinearLayoutManager(
                binding!!.includedLayHome.includedNotification.rvNotifications.context,
                RecyclerView.VERTICAL,
                false
            )
            adapter = notificationAdapter
            addItemDecoration(
                DividerItemDecoration(
                    binding!!.includedLayHome.includedNotification.rvNotifications.context,
                    DividerItemDecoration.VERTICAL
                )
            )
        }

        with(binding!!.includedLayHome.includedLayNearby.rvSearch) {
            layoutManager = GridLayoutManager(this@HomeActivity, 4)
            adapter = searchAdapter
            hasFixedSize()/*addItemDecoration(
                DividerItemDecoration(rvSearch.context, DividerItemDecoration.VERTICAL)
            )*/
        }

        binding!!.includedLayHome.includedNotification.swipeRefreshNotification.setOnRefreshListener(
            refreshN
        )

        binding!!.includedLayHome.includedLayNearby.rlNearby.setOnTouchListener(object :
            View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {

                if (binding!!.includedLayHome.includedLayNearby.fabAM.isOpened) {
                    binding!!.includedLayHome.includedLayNearby.fabAM.close(true)
                    binding!!.includedLayHome.includedLayNearby.fabAM.menuIconView.setImageDrawable(
                        resources.getDrawable(R.drawable.menu_icon)
                    )
                    isOpen = false
                }

                return false
            }
        })

        binding!!.includedLayHome.includedLayNearby.rlNearby.setOnScrollChangeListener(object :
            View.OnScrollChangeListener {
            override fun onScrollChange(
                v: View?, scrollX: Int, scrollY: Int, oldScrollX: Int, oldScrollY: Int
            ) {
                binding!!.includedLayHome.includedLayNearby.fabAM.close(true)
                binding!!.includedLayHome.includedLayNearby.fabAM.menuIconView.setImageDrawable(
                    resources.getDrawable(R.drawable.menu_icon)
                )
                isOpen = false
            }
        })

        setProfileData()

        binding!!.tvVersionNav.text = String.format(
            getString(R.string.text_version), com.connect.meetupsfellow.BuildConfig.VERSION_NAME
        )

        // String.format(getString(R.string.text_version), BuildConfig.VERSION_NAME)

        /*nearBy_search.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                //nearByAdapter.filter.filter(s)

                if (nearBy_search.text.isNotEmpty()) {

                    nearBy_clear.visibility = View.VISIBLE
                }
                *//*else {

                    home.nearBy_clear.visibility = View.GONE
                }*//*

                if (nearBy_search.text.length > 2) {

                    searchAdapter.clearAll()
                    swipeRefreshNearby.isRefreshing = true
                    nearBy_searchIc.visibility = View.GONE
                    //pbSearch.visibility = View.VISIBLE
                    globalSearch()

                    //Toast.makeText(requireContext(), "search", Toast.LENGTH_SHORT).show()

                } else {
                    searchAdapter.clearAll()
                    search_rv_lay.visibility = View.GONE
                    swipeRefreshNearby.visibility = View.VISIBLE
                    //fabFilterNew.visibility = View.VISIBLE
                    nearBy_searchIc.visibility = View.VISIBLE
                    //home.nearBy_clear.visibility = View.GONE
                }

            }

            override fun afterTextChanged(s: Editable?) {

            }

        })*/


        /* val adapter: ArrayAdapter<String> =
             ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, selector)

         adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
         nearBy_selector.adapter = adapter*/

        /*nearBy_selector.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {

                nearBy_select.text = selector[position]

            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

        })*/

        /* adapter = TabLayoutAdapter(supportFragmentManager)
         adapter!!.addFragment(
             favoriteUserFragment,
             SpannableString(getString(R.string.label_users_text))
         )
         adapter!!.addFragment(
             favoriteEventFragment,
             SpannableString(getString(R.string.label_events_text))
         )
         viewPagerFavorite.adapter = adapter
         viewPagerFavorite.currentItem = 0
         tabLayoutFavorite.setupWithViewPager(viewPagerFavorite, true)
         tabLayoutFavorite.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
             override fun onTabReselected(tab: TabLayout.Tab?) {

             }

             override fun onTabUnselected(tab: TabLayout.Tab?) {

             }

             override fun onTabSelected(tab: TabLayout.Tab?) {
                 when (tab!!.position) {
                     0 -> {
                         favoriteUserFragment.favoriteUser()
                     }

                     1 -> {
                         favoriteEventFragment.favoriteEvent()
                     }
                 }
             }

         })*/
//        profileChanged()

        val sheetColor = resources.getColor(R.color.white)
        val fabColor = resources.getColor(R.color.red)
        val fab = binding!!.includedLayHome.includedLayNearby.fabBtn
        val sheetView = binding!!.includedLayHome.includedLayNearby.fabCard as View
        val overlay = binding!!.includedLayHome.includedLayNearby.dimOverly as View

        val materialSheetFab = MaterialSheetFab(
            fab, sheetView, overlay, sheetColor, fabColor
        )

        materialSheetFab.setEventListener(object : MaterialSheetFabEventListener() {
            override fun onShowSheet() {
                // Called when the material sheet's "show" animation starts.
            }

            override fun onSheetShown() {
                // Called when the material sheet's "show" animation ends.
            }

            override fun onHideSheet() {
                // Called when the material sheet's "hide" animation starts.
            }

            override fun onSheetHidden() {
                // Called when the material sheet's "hide" animation ends.
            }
        })

        binding!!.includedLayHome.includedLayNearby.fabAM.setOnMenuButtonClickListener {

            if (isOpen) {

                binding!!.includedLayHome.includedLayNearby.fabAM.close(true)

                binding!!.includedLayHome.includedLayNearby.fabAM.menuIconView.setImageDrawable(
                    resources.getDrawable(R.drawable.menu_icon)
                )

                isOpen = false


            } else {

                binding!!.includedLayHome.includedLayNearby.fabAM.open(true)

                binding!!.includedLayHome.includedLayNearby.fabAM.menuIconView.setImageDrawable(
                    resources.getDrawable(R.drawable.ic_baseline_add_24_white)
                )

                isOpen = true
            }


        }

        binding!!.includedLayHome.includedLayNearby.menuLoc.setOnClickListener {

            if ((lat == "0.0") && (lng == "0.0")) {/*tvNoLocation.visibility = View.VISIBLE
                tvNoLocation.text = String.format(
                    getString(R.string.text_rational_location), getString(R.string.app_name)
                )*/
                //hideProgressView(rl_progress)
                //checkRequiredPermissions()
            } else {
                autoPlacePicker()
                //switchActivityOnly(Constants.Intent.SearchLocation,false)
            }
            isOpen = false
            binding!!.includedLayHome.includedLayNearby.fabAM.menuIconView.setImageDrawable(
                resources.getDrawable(R.drawable.menu_icon)
            )
            binding!!.includedLayHome.includedLayNearby.fabAM.close(true)
        }

        binding!!.includedLayHome.includedLayNearby.menuName.setOnClickListener {

            if ((lat == "0.0") && (lng == "0.0")) {/*tvNoLocation.visibility = View.VISIBLE
                tvNoLocation.text = String.format(
                    getString(R.string.text_rational_location), getString(R.string.app_name)
                )*/
                //hideProgressView(rl_progress)
                //checkRequiredPermissions()
            } else {
                var selected = -1
                when {
                    binding!!.includedLayHome.ivNearby.isSelected -> selected = 0
                    binding!!.includedLayHome.ivFeed.isSelected -> selected = 1
                    binding!!.includedLayHome.ivHeart.isSelected -> selected = 3
                    /// if (tabLayoutFavorite.selectedTabPosition == 0) 3 else 4
                    binding!!.includedLayHome.ivChat.isSelected -> selected = 2
                    binding!!.includedLayHome.ivTimeLine.isSelected -> selected = 5

                }
                switchActivity(Constants.Intent.Search, false, Bundle().apply {
                    putInt(Constants.IntentDataKeys.Selected, 0)
                })
            }
            isOpen = false
            binding!!.includedLayHome.includedLayNearby.fabAM.menuIconView.setImageDrawable(
                resources.getDrawable(R.drawable.menu_icon)
            )
            binding!!.includedLayHome.includedLayNearby.fabAM.close(true)
        }

        binding!!.includedLayHome.includedLayNearby.menuFilter.setOnClickListener {
            if ((lat == "0.0") && (lng == "0.0")) {/*tvNoLocation.visibility = View.VISIBLE
                tvNoLocation.text = String.format(
                    getString(R.string.text_rational_location), getString(R.string.app_name)
                )*/
                //hideProgressView(rl_progress)
                //checkRequiredPermissions()
                return@setOnClickListener
            }
            SearchFilterDialog.Builder(this@HomeActivity, sharedPreferencesUtil.fetchUserProfile())
                .feeds(requestFeed).build().setOnDismissListener {
                  ///  showProgressView(progressBar)
                    /*  nearByList.clear()
                      nearByListAdminAds.clear()
                      nearByAdapter.clearNearBy()*/

                    nearByList.clear()
                    nearByListAdminAds.clear()
                    nearByListRecentOnlyAds.clear()
                    nearByListSwitchs.clear()
                    nearByAdapter.clearNearBy()
                    nearByAdapter.notifyDataSetChanged()

                    nextNearby = 1
                    requestFeed.apply {
                        when {
                            myIdentity.isEmpty() && intoIdentity.isEmpty() && gender.isEmpty() && partnerStatus.isEmpty() && generalIntercourse.isEmpty() && fistingId.isEmpty() -> binding!!.includedLayHome.includedLayNearby.fabFilterNew.backgroundTintList =
                                ColorStateList.valueOf(Color.parseColor("#ffffff"))

                            else -> binding!!.includedLayHome.includedLayNearby.fabFilterNew.backgroundTintList =
                                ColorStateList.valueOf(Color.parseColor("#ffffff"))
                        }
                    }
                    //initiateAdminADs()
                    Log.e("CallNearBy%", "333")
                    binding!!.includedLayHome.includedLayNearby.swipeRefreshNearby.isRefreshing =
                        true
                    binding!!.includedLayHome.includedLayNearby.becomePro2.rlPro.visibility =
                        View.GONE
                    getNearbyUser()
                    LogManager.logger.i(
                        ArchitectureApp.instance!!.tag, "Selected : ${Gson().toJson(requestFeed)}"
                    )
                }
            isOpen = false
            binding!!.includedLayHome.includedLayNearby.fabAM.menuIconView.setImageDrawable(
                resources.getDrawable(R.drawable.menu_icon)
            )
            binding!!.includedLayHome.includedLayNearby.fabAM.close(true)
        }

        binding!!.includedLayHome.includedLayNearby.becomePro2.rlPro.setOnClickListener {
//            showAlertForPremium("Your account has been upgraded to PRO.")

            if (binding!!.includedLayHome.includedLayNearby.becomePro2.proText.text.toString()
                    .equals(getString(R.string.click_to_load_more))
            ) {
                loadMoreNearby = false
                binding!!.includedLayHome.playerProgressbar.visibility = View.VISIBLE
                Log.e("CallNearBy%", "444")
                getNearbyUser()
            } else {
                AlertBuyPremium.Builder(this@HomeActivity, Constants.DialogTheme.NoTitleBar).build()
                    .show()
            }
        }

        //fetchConversations()
        binding!!.includedLayHome.tvUnReadCount.visibility = View.INVISIBLE


//        updateUserProfileToFirebase()
        //mostRecentlyJoindUser()

        binding!!.includedLayHome.includedLayNearby.switchText.text = "RECENTLY ONLINE"
        binding!!.includedLayHome.includedLayNearby.switchOnline.setOnCheckedChangeListener { _, isChecked ->
            val msg = if (isChecked) "RECENTLY ONLINE" else "ALL USERS"
            binding!!.includedLayHome.includedLayNearby.switchText.text = msg


            if (nearByListAdminAds.isEmpty()) {

                //  nearByList?.addAll(response?.nearbyuser)

                if (binding!!.includedLayHome.includedLayNearby.switchOnline.isChecked) {
                    nearByAdapter.updateNearBy(filterRecentlyOnline(nearByListRecentOnlyAds))
                } else {
                    nearByAdapter.updateNearBy(nearByListRecentOnlyAds)
                }


            } else {


                var local: Int = 0
                var localMod: Int = 0
                for (j in 0 until nearByListSwitchs.size) {

                    /*if ((j) % 16 == 0 && j != 0) {
                            //  nearByList?.addAll(nearByListAdminAds)
                            local = ((j) / 16)

                            if (nearByListAdminAds.size < local) {
                                localMod = local % nearByListAdminAds.size
                                nearByList.add(nearByListAdminAds.get(0))

                            } else {
                                nearByList.add(nearByListAdminAds.get(local - 1))
                            }
                        }*/

                    nearByList.add(nearByListSwitchs.get(j))

                }

            }


            if (isChecked) {

                recentlyOnlineList = ArrayList()
                // recentlyOnlineList = filterRecentlyOnline(nearByList)
                recentlyOnlineList = filterRecentlyOnline(nearByListRecentOnlyAds)
                nearByAdapter.clearNearBy()
                nearByAdapter.updateNearBy(recentlyOnlineList)
                nearByAdapter.notifyDataSetChanged()
            } else {

                nearByAdapter.clearNearBy()
                nearByAdapter.updateNearBy(nearByList)
                nearByAdapter.notifyDataSetChanged()

            }
        }

    }

    private fun setNavBottomBadgeCount() {
        chatList =
            FirebaseDatabase.getInstance(Constants.Firebase.databaseUrl).getReference(Constants.Firebase.ChatDB)
                .child("ChatList")

        chatList.child(sharedPreferencesUtil.userId())
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snap: DataSnapshot) {

                    if (snap.exists()) {

                        for (list in snap.children) {

                            val chatRoomId = list.child("convoId").value.toString()
                            val lastMsg = list.child("messageText").value.toString()
                            val lastMsgTime = list.child("messageTime").value.toString()
                            val otherUserName = list.child("otherUserName").value.toString()

                            if (chatRoomId != "null" && lastMsg != "null" && lastMsgTime != "null" && otherUserName != "null") {

                                unreadCount = list.child("unReadCount").value.toString().toInt()
                                Log.e("ChatUnreadCount", "Home: " + unreadCount.toString())

                            }
                            unreadCountT += unreadCount
                            unreadCount = 0

                        }

                        hideProgressView(progressBar)

                        Constants.unreadCountP += unreadCountT
                        Log.e("ChatUnreadCount", "Home%: " + unreadCountT.toString())
                        if (Constants.unreadCountP == 0 && Constants.unreadCountG == 0) {
                            binding!!.includedLayHome.tvUnReadCountH.visibility = View.GONE
                        } else {
                            binding!!.includedLayHome.tvUnReadCountH.visibility = View.VISIBLE
                            binding!!.includedLayHome.tvUnReadCountH.text =
                                (Constants.unreadCountP + Constants.unreadCountG).toString()
                        }

                        Constants.unreadCountP = 0
                        unreadCountT = 0

                    } else {
                        Log.e("PersonalFragment", "NoChat_VISIBle")
                        hideProgressView(progressBar)
                        //popupWindow.dismiss()
                    }

                }

                override fun onCancelled(p0: DatabaseError) {

                }
            })
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun setProfileData() {
        Log.e("userProfile_Home ", "setProfileData")
        val profile = sharedPreferencesUtil.fetchUserProfile()

        DisplayImage.with(this)
            .load(if (profile.images.isNotEmpty()) profile.images[0].imagePath else "")
            .placeholder(R.drawable.meetupsfellow_transpatent).shape(DisplayImage.Shape.DEFAULT)
            .into(binding!!.includedLayHome.includedLayNearby.userImg).build()

        Log.e("userProfile%", " 111" + profile.images)
///        Log.e("userProfile%"," 222"+ profile.images[0].imagePath)

        binding!!.includedLayHome.includedLayNearby.userNameTxt.text =
            profile.name + ", " + profile.age

        val paint = binding!!.includedLayHome.includedLayNearby.userNameTxt.paint
        val width =
            paint.measureText(binding!!.includedLayHome.includedLayNearby.userNameTxt.text.toString())
        val textShader: Shader = LinearGradient(
            0f,
            0f,
            width,
            binding!!.includedLayHome.includedLayNearby.userNameTxt.textSize,
            intArrayOf(
                Color.parseColor("#F4447E"), Color.parseColor("#8448F4")/*Color.parseColor("#64B678"),
                Color.parseColor("#478AEA"),*/
                //Color.parseColor("#8446CC")
            ),
            null,
            Shader.TileMode.REPEAT
        )

        binding!!.includedLayHome.includedLayNearby.userNameTxt.paint.shader = textShader


        if (profile.homeLocation.isEmpty()) {

            binding!!.includedLayHome.includedLayNearby.userHomeTownTxt.text = "Not available"
        } else {

            binding!!.includedLayHome.includedLayNearby.userHomeTownTxt.text = profile.homeLocation
        }

        when (profile.currentPlanInfo!!.planId) {
            2 -> {
                binding!!.includedLayHome.includedLayNearby.userproStatus.text =
                    profile.currentPlanInfo!!.planTitle
                binding!!.includedLayHome.includedLayNearby.userProIc.setImageResource(R.drawable.special_badge)
                binding!!.includedLayHome.includedLayNearby.userProIc.backgroundTintList =
                    ColorStateList.valueOf(Color.GRAY)
                binding!!.includedLayHome.includedLayNearby.userproStatus.visibility =
                    View.INVISIBLE
                binding!!.includedLayHome.includedLayNearby.userProIc.visibility = View.VISIBLE
                binding!!.includedLayHome.includedLayNearby.becomeProTextTop.text =
                    "You have currently having " + profile.currentPlanInfo!!.planTitle + " plan"
                binding!!.includedLayHome.includedLayNearby.becomeProTextTop.setTextColor(getColor(R.color.grey))
                binding!!.includedLayHome.includedLayNearby.becomeProTextTop.isClickable = false
                Log.e("userProfile%", "pro_2")
            }

            3 -> {
                binding!!.includedLayHome.includedLayNearby.userproStatus.text =
                    profile.currentPlanInfo!!.planTitle
                binding!!.includedLayHome.includedLayNearby.userProIc.setImageResource(R.drawable.standers_badge)
                binding!!.includedLayHome.includedLayNearby.userproStatus.visibility =
                    View.INVISIBLE
                binding!!.includedLayHome.includedLayNearby.userProIc.visibility = View.VISIBLE
                binding!!.includedLayHome.includedLayNearby.becomeProTextTop.text =
                    "You have currently having " + profile.currentPlanInfo!!.planTitle + " plan"
                binding!!.includedLayHome.includedLayNearby.becomeProTextTop.setTextColor(getColor(R.color.gradientStart))
                binding!!.includedLayHome.includedLayNearby.becomeProTextTop.isClickable = false
                Log.e("userProfile%", "pro_3")
            }

            4 -> {
                //  proImg.backgroundTintList = ColorStateList.valueOf(Color.WHITE)
                binding!!.includedLayHome.includedLayNearby.userproStatus.text =
                    profile.currentPlanInfo!!.planTitle
                binding!!.includedLayHome.includedLayNearby.userProIc.setImageResource(R.drawable.ic_baseline_workspace_premium_24)
                binding!!.includedLayHome.includedLayNearby.userproStatus.visibility =
                    View.INVISIBLE
                binding!!.includedLayHome.includedLayNearby.userProIc.visibility = View.VISIBLE
                binding!!.includedLayHome.includedLayNearby.becomeProTextTop.text =
                    "You have currently having " + profile.currentPlanInfo!!.planTitle + " plan"
                binding!!.includedLayHome.includedLayNearby.becomeProTextTop.setTextColor(getColor(R.color.premium_badge_color))
                binding!!.includedLayHome.includedLayNearby.becomeProTextTop.isClickable = false
                Log.e("userProfile%", "pro_4")
            }

            5 -> {
                binding!!.includedLayHome.includedLayNearby.userproStatus.text =
                    profile.currentPlanInfo!!.planTitle
                binding!!.includedLayHome.includedLayNearby.userProIc.setImageResource(R.drawable.featured_badge)
                binding!!.includedLayHome.includedLayNearby.userproStatus.visibility =
                    View.INVISIBLE
                binding!!.includedLayHome.includedLayNearby.userProIc.visibility = View.VISIBLE
                binding!!.includedLayHome.includedLayNearby.becomeProTextTop.text =
                    "You have currently having " + profile.currentPlanInfo!!.planTitle + " plan"
                binding!!.includedLayHome.includedLayNearby.becomeProTextTop.setTextColor(getColor(R.color.premium_badge_color))
                binding!!.includedLayHome.includedLayNearby.becomeProTextTop.isClickable = false
                Log.e("userProfile%", "Pro_5")
            }

            else -> {
                //  userPro_ic.backgroundTintList = ColorStateList.valueOf(Color.GRAY)
                binding!!.includedLayHome.includedLayNearby.userProIc.setImageResource(R.drawable.free_user_badge)
                binding!!.includedLayHome.includedLayNearby.userproStatus.text =
                    profile.currentPlanInfo!!.planTitle
                binding!!.includedLayHome.includedLayNearby.userproStatus.visibility =
                    View.INVISIBLE
                binding!!.includedLayHome.includedLayNearby.userProIc.visibility = View.GONE
                binding!!.includedLayHome.includedLayNearby.becomeProTextTop.isClickable = true
                Log.e("userProfile%", "ELSEEE")
            }
        }


        binding!!.includedLayHome.includedLayNearby.becomeProTextTop.setOnClickListener {
            if (!profile.isProMembership) {
                AlertBuyPremium.Builder(this@HomeActivity, Constants.DialogTheme.NoTitleBar).build()
                    .show()
            }

            closeMenuFilter()
        }

        binding!!.includedLayHome.includedLayNearby.imgCard1.setOnClickListener {
            switchActivityOnly(Constants.Intent.Profile, false)

            closeMenuFilter()
        }

        /*userSelf_phone.text =
            getSharedPreferences("userPhone", MODE_PRIVATE).getString("userPhone", "").toString()*/

        binding!!.includedLayHome.includedLayNearby.userSelfPhone.text =
            profile.countryCode + profile.number


        if (profile.email.length > 19) {

            val tempMail = profile.email.substring(0, 18) + "..."

            binding!!.includedLayHome.includedLayNearby.userSelfEmail.text = tempMail
        } else {
            binding!!.includedLayHome.includedLayNearby.userSelfEmail.text = profile.email
        }


/*        if (profile.images.size > 0) {
            FirebaseDatabase.getInstance(Constants.Firebase.databaseUrl).getReference("Live")
                .child("UserProfile").child(profile.userId).child("imgUrl")
                .setValue(profile.images[0].imagePath)

        }*/


        if (profile.currentPlanInfo!!.profileFilters.equals("nofilter")) {
            binding!!.includedLayHome.includedLayNearby.fabAM.visibility = View.GONE
        } else {
            binding!!.includedLayHome.includedLayNearby.fabAM.visibility = View.VISIBLE
        }
    }

    private fun globalSearch() {
        if (null == mPresenterS) mPresenterS = SearchPresenter(presenterS)

        run {
            mPresenterS!!.addSearchObject(RequestFeed().apply {
                page = "$next"
                limit = "10"
                search = binding!!.includedLayHome.includedLayNearby.nearBySearch.text.toString()
                currentLat = "${sharedPreferencesUtil.fetchLocation().latitude}"
                currentLong = "${sharedPreferencesUtil.fetchLocation().longitude}"
            })
            mPresenterS!!.callRetrofit(ConstantsApi.SEARCH)
        }
    }

    private fun fetchNotifications() {
        showProgressView(progressBar)
        if (null == mPresenterN) mPresenterN = NotificationPresenter(presenterN)

        run {
            mPresenterN!!.callRetrofit(ConstantsApi.NOTIFICATION)
        }
    }

    private fun showAlertClear(message: String) {

        val dialog = Dialog(this)

        dialog.setCancelable(true)

        val view = layoutInflater.inflate(R.layout.custom_exit_dialog, null)

        dialog.setContentView(view)

        dialog.window!!.attributes.windowAnimations = R.style.DialogAnimation

        if (dialog.window != null) {
            dialog.window!!.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
            )
        }
        dialog.window!!.setGravity(Gravity.CENTER)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val dialogHead = view.findViewById<TextView>(R.id.dialog_title)
        val dialogContent = view.findViewById<TextView>(R.id.dialog_content)
        val cancelBtn = view.findViewById<Button>(R.id.noExitBtn)
        val confirm = view.findViewById<Button>(R.id.yesExitBtn)
        Log.e("notification_Clear", "all_Notification_clear_dialog")
        dialogHead.text = "Clear Notifications"
        dialogContent.text = message

        cancelBtn.setOnClickListener {
            dialog.cancel()
        }

        confirm.setOnClickListener {
            dialog.dismiss()
            clear()
        }

        dialog.show()


        /*  val alertDialog = AlertDialog.Builder(this@HomeActivity, R.style.MyDialogTheme2)
          // Setting DialogAction Message
          //  alertDialog.setTitle(errorResponse.message)
          alertDialog.setMessage(message)
          // On pressing SettingsActivity button
          alertDialog.setPositiveButton(getString(R.string.text_clear_all)) { dialog, _ ->
              dialog.dismiss()
              showProgressView(rl_progress)
              clear()
          }
          alertDialog.setNegativeButton(getString(R.string.label_cancel_text)) { dialog, _ ->
              dialog.cancel()
          }
          alertDialog.setCancelable(false)
          // Showing Alert Message
          alertDialog.show()*/
    }


    private fun delete(id: String) {
        if (null == mPresenterN) mPresenterN = NotificationPresenter(presenterN)

        run {
            mPresenterN!!.readNotificationObject(RequestReadNotification().apply {
                notificationId = id
            })
            mPresenterN!!.callRetrofit(ConstantsApi.DELETE_NOTIFICATION)
        }
    }

    // claer all notification
    private fun clear() {
        if (null == mPresenterN) mPresenterN = NotificationPresenter(presenterN)

        run {
            binding!!.includedLayHome.includedNotification.swipeRefreshNotification.isRefreshing =
                true
            mPresenterN!!.callRetrofit(ConstantsApi.CLEAR_NOTIFICATION)
        }
    }

    private fun initiateBottomNav() {

        bottomNavigationView = findViewById(R.id.chip_nav)

        bottomNavigationView.visibility = View.VISIBLE

        bottomNavigationView.itemIconTintList = null

        //bottomNavigationView.menu.getItem(0).icon = resources.getDrawable(R.drawable.nearby__2_)
        //bottomNavigationView.menu.getItem(1).icon = resources.getDrawable(R.drawable.wedding_day__1_)
        //bottomNavigationView.menu.getItem(2).icon = resources.getDrawable(R.drawable.notification__1___1_)
        // bottomNavigationView.menu.getItem(3).icon = resources.getDrawable(R.drawable.chat__2_)
        //bottomNavigationView.menu.getItem(4).icon = resources.getDrawable(R.drawable.network__1_)

        bottomNavigationView.selectedItemId = R.id.bottom_nav_nearBy

        bottomNavigationView.setOnNavigationItemSelectedListener {

            when (it.itemId) {

                R.id.bottom_nav_timeline -> {

                    //it.icon = resources.getDrawable(R.drawable.network)

                    sharedPreferencesUtil.saveCurrentStateScreen("ivTimeLine")

                    Log.e("gStrPro", "gStrProTag" + gStrProTag)

                    val profile = sharedPreferencesUtil.fetchUserProfile()

                    Log.e("gStrPro1", "gStrProTag" + profile.isProMembership)

                    if (profile.isProMembership) {
                        if (binding!!.includedLayHome.ivTimeLine.isSelected) {
                            binding!!.includedLayHome.includedLayNearby.tvProNearby.visibility =
                                View.GONE

                        } else {
                            binding!!.includedLayHome.includedLayNearby.tvProNearby.visibility =
                                View.GONE
                        }
                    }

                    setupActionBarNavigationHome(
                        "TimeLines", true, R.drawable.menu_icon
                    )

                    setSelectedBottomLayout(binding!!.includedLayHome.includedTimeline.rlTimeLine)
                    timeline()

                    return@setOnNavigationItemSelectedListener true
                }

                R.id.bottom_nav_chat -> {
                    binding!!.includedLayHome.ivNearby.isSelected = false
                    binding!!.includedLayHome.ivFeed.isSelected = false
                    //ivChat.isSelected = true
                    //it.icon = resources.getDrawable(R.drawable.chat__1_)
                    sharedPreferencesUtil.saveCurrentStateScreen("ivChat")

                    if (gStrProTag) {
                        if (binding!!.includedLayHome.ivChat.isSelected) {
                            binding!!.includedLayHome.includedLayNearby.tvProNearby.visibility =
                                View.GONE

                        } else {
                            binding!!.includedLayHome.includedLayNearby.tvProNearby.visibility =
                                View.GONE
                        }
                    }
                    Constants.Variable.isProfile = true
                    setupActionBarNavigationHome(
                        getString(R.string.title_chat), true, R.drawable.menu_icon
                    )
                    // adminConversation()
                    //showLocalConversation()
                    setSelectedBottomLayout(binding!!.includedLayHome.includedLChat.rlChat)
                    return@setOnNavigationItemSelectedListener true
                }

                R.id.bottom_nav_events -> {

                    //it.icon = resources.getDrawable(R.drawable.wedding_day)
                    Log.e("nearUsers123- ", "Tab_Change_bottom_Event")
                    sharedPreferencesUtil.saveCurrentStateScreen("ivFeed")
                    binding!!.includedLayHome.ivFeed.isSelected = true
                    if (gStrProTag) {
                        if (binding!!.includedLayHome.ivFeed.isSelected) {
                            binding!!.includedLayHome.includedLayNearby.tvProNearby.visibility =
                                View.GONE
                        } else {
                            binding!!.includedLayHome.includedLayNearby.tvProNearby.visibility =
                                View.GONE
                        }
                    }

                    Constants.Variable.isProfile = true
                    setupActionBarNavigationHome("Meetups", true, R.drawable.menu_icon)
                    setSelectedBottomLayout(binding!!.includedLayHome.includedFeed.rlFeeds)

                    if (feedsAdapter.itemCount == 0) {
                        //  showProgressView(rl_progress)
                        binding!!.includedLayHome.includedFeed.swipeRefresh.isRefreshing = true
                        nextFeed = 1
                        feedsAdapter.clearFeeds()
                        feeds()
                    }
                    return@setOnNavigationItemSelectedListener true
                }

                R.id.bottom_nav_favorite -> {

                    // it.icon = resources.getDrawable(R.drawable.notification__2_)
                    Log.e("nearUsers123- ", "Tab_Change_bottom_Notification")

                    sharedPreferencesUtil.saveCurrentStateScreen("ivHeart")

                    if (gStrProTag) {
                        if (binding!!.includedLayHome.ivHeart.isSelected) {

                            binding!!.includedLayHome.includedLayNearby.tvProNearby.visibility =
                                View.GONE

                        } else {

                            binding!!.includedLayHome.includedLayNearby.tvProNearby.visibility =
                                View.GONE
                        }
                    }
                    binding!!.includedLayHome.includedNotification.notificationLay.visibility =
                        View.VISIBLE
                    setupActionBarNavigationHome(
                        "Notifications", true, R.drawable.menu_icon
                    )
                    Constants.Variable.isProfile = true
                    setSelectedBottomLayout(binding!!.includedLayHome.includedNotification.notificationLay)
                    if (notificationAdapter.itemCount == 0) {
                        //   showProgressView(rl_progress)
                        binding!!.includedLayHome.includedNotification.swipeRefreshNotification.isRefreshing =
                            true
                        fetchNotifications()
                    }

                    /* Constants.Variable.isProfile = true
                     switchActivity(Constants.Intent.Profile, false, Bundle())*//*Constants.Variable.isProfile = true
                    if ((lat == "0.0") && (lng == "0.0")) {
                        tvNoLocation.visibility = View.VISIBLE
                        tvNoLocation.text = String.format(
                            getString(R.string.text_rational_location), getString(R.string.app_name)
                        )
                        hideProgressView(rl_progress)
                        checkRequiredPermissions()
                    } else {
                        notificationRefresh = true
                        switchActivity(Constants.Intent.Notification, false, Bundle())
                    }*/

                    return@setOnNavigationItemSelectedListener true
                }

                R.id.bottom_nav_nearBy -> {

                    // it.icon = resources.getDrawable(R.drawable.nearby__1_)

                    sharedPreferencesUtil.saveCurrentStateScreen("ivNearby")
                    binding!!.includedLayHome.ivNearby.isSelected = true
                    if (gStrProTag) {
                        if (binding!!.includedLayHome.ivNearby.isSelected) {

                            binding!!.includedLayHome.includedLayNearby.tvProNearby.visibility =
                                View.VISIBLE
                        } else {

                            binding!!.includedLayHome.includedLayNearby.tvProNearby.visibility =
                                View.GONE
                        }
                    }
                    setupActionBarNavigationHome(
                        getString(R.string.title_nearby), true, R.drawable.menu_icon
                    )
                    setSelectedBottomLayout(binding!!.includedLayHome.includedLayNearby.rlNearby)
                    Constants.Variable.isProfile = true

                    Log.e("nearUsers123- ", "Tab_Change_bottom")

                    if (nearByList.isEmpty() || nearByList.size == 0) {
                        nearByList.clear()
                        nearByListAdminAds.clear()

                        nearByListRecentOnlyAds.clear()
                        nearByListSwitchs.clear()

                        nearByAdapter.clearNearBy()
                        nextNearby = 1
                        showProgressView(progressBar)
                        binding!!.includedLayHome.includedLayNearby.swipeRefreshNearby.isRefreshing =
                            true
                        Log.e("CallNearBy%", "5555")
                        getNearbyUser()

                        ///  mostRecentlyJoindUser()
                        // Above this line to Log commend by nilu New
                    }

                    //initiateAdminADs()

                    return@setOnNavigationItemSelectedListener true
                }
            }

            return@setOnNavigationItemSelectedListener false
        }

        /*.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener() {
            fun onNavigationItemSelected(item: MenuItem): Boolean {
                when (item.itemId) {
                    R.id.bottom_nav_category -> {
                        navigationView.setCheckedItem(R.id.homeNav)
                        toolbar_const.setTitle("")
                        item.isChecked = true
                        item.isEnabled = true
                        item.isCheckable = true
                        toolbar_const.getMenu().getItem(0).setVisible(true)
                        toolbar_const.getMenu().getItem(3).setVisible(true)
                        toolbar_const.getMenu().getItem(1).setVisible(true)
                        Functions.loadFragment(
                            supportFragmentManager,
                            Dashboard_frag(),
                            R.id.frag_cont,
                            true,
                            "Category",
                            null
                        )
                        return true
                    }
                    R.id.bottom_nav_favorie -> {
                        navigationView.setCheckedItem(R.id.invisibleNav)
                        toolbar_const.getMenu().getItem(0).setVisible(true)
                        toolbar_const.getMenu().getItem(3).setVisible(true)
                        toolbar_const.getMenu().getItem(1).setVisible(true)
                        if (auth.getCurrentUser() != null || isLogin != false) {
                            Functions.loadFragment(
                                supportFragmentManager,
                                Favorite_frag(),
                                R.id.frag_cont,
                                true,
                                "Favorites",
                                null
                            )
                        } else {
                            Functions.loadFragment(
                                supportFragmentManager,
                                NotLogIn_frag(),
                                R.id.frag_cont,
                                true,
                                "Faveroties",
                                null
                            )
                        }
                        return true
                    }
                    R.id.bottom_nav_orders -> {
                        navigationView.setCheckedItem(R.id.orderNav)
                        toolbar_const.getMenu().getItem(0).setVisible(false)
                        toolbar_const.getMenu().getItem(3).setVisible(false)
                        toolbar_const.getMenu().getItem(1).setVisible(true)
                        if (auth.getCurrentUser() != null || isLogin != false) {
                            Functions.loadFragment(
                                supportFragmentManager,
                                Orders_frag(),
                                R.id.frag_cont,
                                true,
                                "Orders",
                                null
                            )
                        } else {
                            Functions.loadFragment(
                                supportFragmentManager,
                                NotLogIn_frag(),
                                R.id.frag_cont,
                                true,
                                "Faveroties",
                                null
                            )
                        }
                        return true
                    }
                    R.id.bottom_nav_discount -> {
                        navigationView.setCheckedItem(R.id.offersNav)
                        toolbar_const.getMenu().getItem(0).setVisible(true)
                        toolbar_const.getMenu().getItem(3).setVisible(false)
                        toolbar_const.getMenu().getItem(1).setVisible(false)
                        Functions.loadFragment(
                            supportFragmentManager,
                            Offers_frag(),
                            R.id.frag_cont,
                            true,
                            "Offers",
                            null
                        )
                        return true
                    }
                    R.id.invisible -> {
                        navigationView.setCheckedItem(R.id.categoriesNav)
                        //toolbar_const.setTitle("");
                        toolbar_const.getMenu().getItem(0).setVisible(true)
                        toolbar_const.getMenu().getItem(3).setVisible(true)
                        toolbar_const.getMenu().getItem(1).setVisible(true)
                        Functions.loadFragment(
                            supportFragmentManager,
                            Category_frag(),
                            R.id.frag_cont,
                            false,
                            "DashBoard",
                            null
                        )
                        return true
                    }
                }
                return false
            }
        })*/


        /*

                bottomNavigation.show(0)
                bottomNavigation.add(MeowBottomNavigation.Model(ivNearby,R.drawable.selector_nearby))
                bottomNavigation.add(MeowBottomNavigation.Model(ivFeed,R.drawable.selector_feed))
                bottomNavigation.add(MeowBottomNavigation.Model(ivTimeLine,R.drawable.selecor_timeline))
                bottomNavigation.add(MeowBottomNavigation.Model(ivChat,R.drawable.selector_chat))
                bottomNavigation.add(MeowBottomNavigation.Model(ivHeart,R.drawable.selector_heart))
        */


    }

    private fun initiateAdminADs() {
        mPresenter!!.callRetrofit(ConstantsApi.ADMIN_ADS)
        //   nearByList.clear()
    }

    // working
    private fun initiateAdminPush() {
        //   mPresenter!!.callRetrofit(ConstantsApi.ADMIN_LAST_PUSH)

    }

    fun filterRecentlyOnline(nearBy: ArrayList<ResponseUserData>): ArrayList<ResponseUserData> {
        var onlineList: ArrayList<ResponseUserData> = ArrayList()
        var onlineListWithAds: ArrayList<ResponseUserData> = ArrayList()
        for (i in 0 until nearBy.size) {
            if (nearBy[i].onlineStatus == 1) {
//ss recent 4
                if (nearBy[i].lastLoginTimeStamp != "") {
                    try {
                        val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                        var lastDate = format.parse(nearBy[i].lastLoginTimeStamp)
                        var currentUTCTime = format.parse(getUTCTime())
                        val diff: Long = currentUTCTime.time - lastDate.time
                        val seconds = diff / 1000
                        val minutes = seconds / 60
                        val hours = minutes / 60
                        if (hours < 48) {
                            onlineList.add(nearBy[i])
                        }
                    } catch (e: Exception) {

                    }
                }

            }
        }

        var local: Int = 0
        for (j in 0 until onlineList.size) {/*if ((j) % 16 == 0 && j != 0 && (j) / 16 <= nearByListAdminAds.size) {
                local = ((j) / 16)
                if (nearByListAdminAds.size < local) {
                    onlineListWithAds.add(nearByListAdminAds.get(0))
                } else {
                    onlineListWithAds.add(nearByListAdminAds.get(local - 1))
                }
            }*/
            onlineListWithAds.add(onlineList.get(j))
        }
        return onlineListWithAds
    }

    private fun setCount(context: Context, count: String) {
        val menuItem = menu!!.findItem(R.id.menu_notification)
        val icon = menuItem.icon as LayerDrawable

//        val iconBitmap = menuItem.icon as NinePatchDrawable
//        val iconLayer = LayerDrawable(arrayOf<Drawable>(icon))

        val badge: CountDrawable

        // Reuse drawable if possible
        val reuse = icon.findDrawableByLayerId(R.id.ic_group_count)
        badge = if (reuse != null && reuse is CountDrawable) {
            reuse
        } else {
            CountDrawable(context)
        }

        badge.setCount(count)
        icon.mutate()
        icon.setDrawableByLayerId(R.id.ic_group_count, badge)
    }

    // Impelent this for review app in play store
    /*private fun reviewApp() {

        val manager = ReviewManagerFactory.create(this)
        val request = manager.requestReviewFlow()
        request.addOnCompleteListener { request ->
            if (request.isSuccessful) {
                // We got the ReviewInfo object
                val reviewInfo = request.result
                val flow = manager.launchReviewFlow(this, reviewInfo)
                flow.addOnCompleteListener { _ ->
                    // The flow has finished. The API does not indicate whether the user
                    // reviewed or not, or even whether the review dialog was shown. Thus, no
                    // matter the result, we continue our app flow.
                }
            } else {

                Log.d("ReviewErr", request.exception.toString())
                Log.d("ReviewErr", request.result.toString())
                // There was some problem, continue regardless of the result.
            }
        }
    }*/

    private fun clickListener() {
        val profile = sharedPreferencesUtil.fetchUserProfile()

        Log.d(
            "loaction22222",
            profile.currentLat + " - " + profile.currentLong + " - " + profile.homeLocation
        )
        val geocoder = Geocoder(context!!, Locale.ENGLISH)
        var cityName = ""
        try {
            val addresses: List<Address> = geocoder.getFromLocation(
                profile.currentLat.toDouble(), profile.currentLong.toDouble(), 1
            )!!
            if (addresses.size > 0) {

                cityName = addresses[0].locality
                val stateName = addresses[0].getAddressLine(1)
                val countryName = addresses[0].getAddressLine(2)
                binding!!.includedLayHome.header.toolbarCurrantLocation.text = cityName
                Log.e("Currant_Loc", "city:- $cityName")
            }
        } catch (e: Exception) {
            Log.e("loaction@@!", "Exception- " + e.message)
            /// Toast.makeText(this@HomeActivity, "Please connect your connection", Toast.LENGTH_LONG).show()
        }


        Log.d("loaction22222", cityName + "   " + profile.homeLocation)

        binding!!.includedLayHome.includedLayNearby.ivClear.setOnClickListener {
            val location = sharedPreferencesUtil.fetchLocation()
            search = ""
            requestFeed.searchPlace = ""
            lat = "${location.latitude}"
            lng = "${location.longitude}"
            binding!!.includedLayHome.includedLayNearby.rlLocationSearch.visibility = View.GONE
            binding!!.includedLayHome.includedLayNearby.view1.visibility = View.GONE
            binding!!.includedLayHome.includedLayNearby.tvLocation.text = search
            binding!!.includedLayHome.includedLayNearby.ivClear.visibility = View.GONE
            showProgressView(progressBar)
            //nearBy_search.setText("")
            ////   locationSearch.location_txt.text = cityName
            nearByListRecentOnlyAds.clear()
            nearByListSwitchs.clear()

            nearByList.clear()
            nearByListAdminAds.clear()
            nearByAdapter.clearNearBy()
            nextNearby = 1


            //initiateAdminADs()
            Log.e("CallNearBy%", "6666")
            getNearbyUser()/*val params = RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
            )
            params.topMargin = resources.getDimensionPixelOffset(R.dimen._12sdp)
            params.leftMargin = resources.getDimensionPixelOffset(R.dimen._12sdp)
            fabFilterNew.layoutParams = params*/
        }
        binding!!.includedLayHome.ivFeed.setOnClickListener {
            if (!binding!!.includedLayHome.ivFeed.isSelected) setSelectedBottomBar(binding!!.includedLayHome.ivFeed)
        }

        binding!!.includedLayHome.ivTimeLine.setOnClickListener {
            if (!binding!!.includedLayHome.ivTimeLine.isSelected)
            //   setupActionBarTransparent(true)

            // toolbar.visibility = View.GONE
                setSelectedBottomBar(binding!!.includedLayHome.ivTimeLine)
        }

        binding!!.includedLayHome.ivNearby.setOnClickListener {
            if (!binding!!.includedLayHome.ivNearby.isSelected) setSelectedBottomBar(binding!!.includedLayHome.ivNearby)
        }

        binding!!.includedLayHome.ivAbb.setOnClickListener {
            switchActivity(Constants.Intent.CreateEvent, false, Bundle())
        }

        binding!!.includedLayHome.ivChat.setOnClickListener {
            if (!binding!!.includedLayHome.ivChat.isSelected) setSelectedBottomBar(binding!!.includedLayHome.ivChat)
            //adminConversation()
            //showLocalConversation()
        }

        binding!!.includedLayHome.ivHeart.setOnClickListener {
            if (!binding!!.includedLayHome.ivHeart.isSelected) setSelectedBottomBar(binding!!.includedLayHome.ivHeart)
        }

        binding!!.includedLayHome.includedLayNearby.fabFilterNew.setOnClickListener {
            if ((lat == "0.0") && (lng == "0.0")) {/*tvNoLocation.visibility = View.VISIBLE
                tvNoLocation.text = String.format(
                    getString(R.string.text_rational_location), getString(R.string.app_name)
                )*/
                //hideProgressView(rl_progress)
                //checkRequiredPermissions()
                return@setOnClickListener
            }
            SearchFilterDialog.Builder(this@HomeActivity, sharedPreferencesUtil.fetchUserProfile())
                .feeds(requestFeed).build().setOnDismissListener {
                    showProgressView(progressBar)/*  nearByList.clear()
                      nearByListAdminAds.clear()
                      nearByAdapter.clearNearBy()*/


                    nearByList.clear()
                    nearByListAdminAds.clear()
                    nearByListRecentOnlyAds.clear()
                    nearByListSwitchs.clear()
                    nearByAdapter.clearNearBy()
                    nearByAdapter.notifyDataSetChanged()

                    nextNearby = 1
                    requestFeed.apply {
                        when {
                            myIdentity.isEmpty() && intoIdentity.isEmpty() && gender.isEmpty() && partnerStatus.isEmpty() && generalIntercourse.isEmpty() && fistingId.isEmpty() -> binding!!.includedLayHome.includedLayNearby.fabFilterNew.backgroundTintList =
                                ColorStateList.valueOf(Color.parseColor("#ffffff"))

                            else -> binding!!.includedLayHome.includedLayNearby.fabFilterNew.backgroundTintList =
                                ColorStateList.valueOf(Color.parseColor("#ffffff"))
                        }
                    }
                    //initiateAdminADs()
                    Log.e("CallNearBy%", "7777")
                    getNearbyUser()
                    LogManager.logger.i(
                        ArchitectureApp.instance!!.tag, "Selected : ${Gson().toJson(requestFeed)}"
                    )
                }
        }
    }

    private fun setNavigationHeaderData() {

        val hView = binding!!.navView.getHeaderView(0)
        navigationUserImage = hView.findViewById(R.id.ivProfileImage)
        navigationUserName = hView.findViewById(R.id.tvUserName)
        navigationUserAddress = hView.findViewById(R.id.tvAddress)
        navigationProUser = hView.findViewById(R.id.tvProUser)


        binding!!.navView.itemIconTintList = null

        val profile: RelativeLayout = hView.findViewById(R.id.rlProfile)
        val logout: RelativeLayout = binding!!.navView.findViewById(R.id.rlLogout)

        /*if (null != profile.layoutParams) {
            profile.layoutParams.height = DEVICE_HEIGHT / 4
        }*/

        profile.setOnClickListener {
            switchActivity(Constants.Intent.Profile, false, Bundle())
            binding!!.drawerLayout.closeDrawer(GravityCompat.END)
        }

        logout.setOnClickListener {
            binding!!.drawerLayout.closeDrawer(GravityCompat.END)
            showAlertLogout(getString(R.string.alert_logout_user))
        }

        binding!!.support.setOnClickListener {

            switchActivity(Constants.Intent.Webview, false, Bundle().apply {
                putString(
                    Constants.IntentDataKeys.TITLE, getString(R.string.label_support_text)
                )/*  putString(
                      Constants.IntentDataKeys.LINK,
                      BuildConfig.URL_SUPPORT
                  )*/
            })
//                    showSupport(getString(R.string.support_text), getString(R.string.label_support_text))
            Handler().postDelayed({
                binding!!.drawerLayout.closeDrawer(GravityCompat.END)
            }, 100)
        }

        binding!!.termsAndConditions.setOnClickListener {

            switchActivity(Constants.Intent.Webview, false, Bundle().apply {
                putString(
                    Constants.IntentDataKeys.TITLE, getString(R.string.label_conditions_text)
                )/*putString(
                    Constants.IntentDataKeys.LINK,
                    BuildConfig.URL_TERMS
                )*/
            })
            Handler().postDelayed({
                binding!!.drawerLayout.closeDrawer(GravityCompat.END)
            }, 100)
        }

        binding!!.privacyPolicy.setOnClickListener {

            switchActivity(Constants.Intent.Webview, false, Bundle().apply {
                putString(
                    Constants.IntentDataKeys.TITLE, getString(R.string.label_privacy_text)
                )/*  putString(
                      Constants.IntentDataKeys.LINK,
                      BuildConfig.URL_PRIVACY
                  )*/
            })
            Handler().postDelayed({
                binding!!.drawerLayout.closeDrawer(GravityCompat.END)
            }, 100)
        }

        navigationViewClickEvents()
    }
    private fun setToolbartTital(title: String){
        binding!!.includedLayHome.header.tvTitle.text = title
        val paint = binding!!.includedLayHome.header.tvTitle.paint
        val widtht = paint.measureText(binding!!.includedLayHome.header.tvTitle.text.toString())
        val textShader: Shader = LinearGradient(0f, 0f, widtht, binding!!.includedLayHome.header.tvTitle.textSize, intArrayOf(
            Color.parseColor("#F4447E"),
            Color.parseColor("#8448F4")
            /*Color.parseColor("#64B678"),
            Color.parseColor("#478AEA"),*/
            //Color.parseColor("#8446CC")
        ), null, Shader.TileMode.REPEAT)

        binding!!.includedLayHome.header.tvTitle.paint.setShader(textShader)
    }

   private fun setupActionBarNavigationHome(title: String, hideLogo: Boolean, resource: Int) {
        setSupportActionBar( binding!!.includedLayHome.header.toolbar)
        Log.e("setActionBarTitle", "title:- $title")
       setToolbartTital(title)
        supportActionBar!!.setDisplayHomeAsUpEnabled(false)
        supportActionBar!!.setHomeAsUpIndicator(R.color.transparent)
        when (hideLogo) {
            true -> {
                binding!!.includedLayHome.header.tvTitle.visibility = View.VISIBLE
                binding!!.includedLayHome.header.ivLogo.visibility = View.GONE
            }

            false -> {
                binding!!.includedLayHome.header.tvTitle.visibility = View.GONE
                binding!!.includedLayHome.header.ivLogo.visibility = View.VISIBLE
            }
        }

        if(title=="TimeLines"){
            binding!!.includedLayHome.header.ivTimeLineLogo.visibility = View.VISIBLE
            binding!!.includedLayHome.header.tvTitle.visibility = View.GONE
        }else{
            binding!!.includedLayHome.header.ivTimeLineLogo.visibility = View.GONE
        }

        if(title== getString(R.string.title_nearby)){
            binding!!.includedLayHome.header.toolbarCurrantLocation.visibility = View.VISIBLE
            binding!!.includedLayHome.header.toolbarLocationIc.visibility = View.VISIBLE
        }else{
            binding!!.includedLayHome.header.toolbarCurrantLocation.visibility = View.GONE
            binding!!.includedLayHome.header.toolbarLocationIc.visibility = View.GONE
        }

    }



    private fun setNavigationUserProfile() {
        if (null != navigationUserName && null != navigationUserImage && null != navigationUserAddress) {
            val profile = sharedPreferencesUtil.fetchUserProfile()
            navigationUserName!!.text = profile.name
            navigationUserAddress!!.text = profile.homeLocation
            gStrProTimeLine = profile.isProMembership


            /// callForUserProfile(ConstantsApi.FETCH_PROFILE)
            fetchSelfProfile()

            DisplayImage.with(this@HomeActivity)
                .load(if (profile.images.isNotEmpty()) profile.images[0].imagePath else "")
                .placeholder(R.drawable.meetupsfellow_transpatent)
                .shape(DisplayImage.Shape.CropSquare).into(navigationUserImage!!).build()

            //binding!!.includedLayHome.includedLayNearby.userNameTxt.text = profile.name +", "+profile.age
        }
    }

    private var isItemClicked = false
    private fun navigationViewClickEvents() {
        binding!!.navView.setNavigationItemSelectedListener { menuItem ->
            closeMenuFilter()

            if (isItemClicked) {
                // If an item is already clicked, ignore further clicks
                return@setNavigationItemSelectedListener true
            } else {
                isItemClicked = true
            }
            Log.e("menuItem", "id: ${menuItem.itemId}")
            when (menuItem.itemId) {
                R.id.block -> {
                    if ((lat == "0.0") && (lng == "0.0")) {/*tvNoLocation.visibility = View.VISIBLE
                        tvNoLocation.text = String.format(
                            getString(R.string.text_rational_location), getString(R.string.app_name)
                        )*/
                        //hideProgressView(rl_progress)
                        //checkRequiredPermissions()
                        return@setNavigationItemSelectedListener true
                    }
                    isItemClicked = false
                    switchActivity(Constants.Intent.Blocked, false, Bundle())
                    Handler().postDelayed({
                        binding!!.drawerLayout.closeDrawer(GravityCompat.END)
                    }, 100)
                    return@setNavigationItemSelectedListener true
                }

                R.id.connectRequest -> {
                    if ((lat == "0.0") && (lng == "0.0")) {/*tvNoLocation.visibility = View.VISIBLE
                        tvNoLocation.text = String.format(
                            getString(R.string.text_rational_location), getString(R.string.app_name)
                        )*/
                        //hideProgressView(rl_progress)
                        //checkRequiredPermissions()
                        return@setNavigationItemSelectedListener true
                    }
                    switchActivity(Constants.Intent.ConnectRequest, false, Bundle())
                    Handler().postDelayed({
                        isItemClicked = false
                        binding!!.drawerLayout.closeDrawer(GravityCompat.END)
                    }, 100)
                    return@setNavigationItemSelectedListener true
                }

                R.id.album -> {
                    if ((lat == "0.0") && (lng == "0.0")) {/* tvNoLocation.visibility = View.VISIBLE
                         tvNoLocation.text = String.format(
                             getString(R.string.text_rational_location), getString(R.string.app_name)
                         )*/
                        //hideProgressView(rl_progress)
                        //checkRequiredPermissions()
                        return@setNavigationItemSelectedListener true
                    }
                    switchActivity(Constants.Intent.NotificationSetting, false, Bundle())
                    Handler().postDelayed({
                        isItemClicked = false
                        binding!!.drawerLayout.closeDrawer(GravityCompat.END)
                    }, 100)
                    return@setNavigationItemSelectedListener true
                }

                R.id.invite -> {
                Log.e("Menu_Click","invite_User")
                    val intent = Intent(Intent.ACTION_SEND)
                    intent.type = "text/plain"
                    intent.putExtra(Intent.EXTRA_SUBJECT, "MeetupsFellow")
                   intent.putExtra(
                        Intent.EXTRA_TEXT,
                        "Invite other community members to join MeetUpsFellow! \n" + Constants.Firebase.AppURL
                    )
                   /* intent.putExtra(
                        Intent.EXTRA_TEXT,
                        "Invite other community members to join MeetUpsFellow! \n" + "https://meetupsfellow.com/"
                    )*/
                    startActivity(Intent.createChooser(intent, "choose one"))
                    /*  val imageUri = Uri.parse("android.resource://" + getPackageName()
                               + "/drawable/" + "ic_launcher")
                       intent.putExtra(Intent.EXTRA_STREAM, imageUri)*/
                    isItemClicked = false
                    return@setNavigationItemSelectedListener true
                }

                R.id.home -> {
                    isItemClicked = false
                    switchActivity(Constants.Intent.Home, false, Bundle())

                    setSelectedBottomBar(binding!!.includedLayHome.ivTimeLine)
                    bottomNavigationView.visibility = View.VISIBLE

                    Handler().postDelayed({
                        binding!!.drawerLayout.closeDrawer(GravityCompat.END)
                    }, 100)
                    return@setNavigationItemSelectedListener true
                }

                R.id.favorite -> {
                    switchActivity(Constants.Intent.Favorite, false, Bundle())
                    Handler().postDelayed({
                        isItemClicked = false
                        binding!!.drawerLayout.closeDrawer(GravityCompat.END)
                    }, 100)

                    /*bottomNavigationView.visibility = View.GONE

                    sharedPreferencesUtil.saveCurrentStateScreen("ivHeart")

                    if (gStrProTag!!) {
                        if (ivHeart.isSelected) {

                            binding!!.includedLayHome.includedLayNearby.tvProNearby.visibility = View.GONE

                        } else {

                            binding!!.includedLayHome.includedLayNearby.tvProNearby.visibility = View.GONE
                        }
                    }

                    setupActionBarNavigation(
                        getString(R.string.title_favorites),
                        true,
                        R.drawable.menu_icon
                    )
                    setSelectedBottomLayout(notification_lay)

                    Handler().postDelayed({
                        drawerLayout.closeDrawer(GravityCompat.END)
                    }, 100)*/

                    return@setNavigationItemSelectedListener true
                }


                R.id.settings -> {
                    switchActivity(Constants.Intent.Settings, false, Bundle())
                    Handler().postDelayed({
                        isItemClicked = false
                        binding!!.drawerLayout.closeDrawer(GravityCompat.END)
                    }, 100)
                    return@setNavigationItemSelectedListener true
                }

                R.id.policy -> {
                    switchActivity(Constants.Intent.Webview, false, Bundle().apply {
                        putString(
                            Constants.IntentDataKeys.TITLE, getString(R.string.label_privacy_text)
                        )
                        putString(
                            Constants.IntentDataKeys.LINK,
                            Constants.Firebase.PrivacyPolicyURL
                        )
                    })
                    isItemClicked = false
                    Handler().postDelayed({
                        binding!!.drawerLayout.closeDrawer(GravityCompat.END)
                    }, 100)
                    return@setNavigationItemSelectedListener true
                }

                R.id.terms -> {
                    switchActivity(Constants.Intent.Webview, false, Bundle().apply {
                        putString(
                            Constants.IntentDataKeys.TITLE,
                            getString(R.string.label_conditions_text)
                        )/*  putString(
                              Constants.IntentDataKeys.LINK,
                              BuildConfig.URL_TERMS
                          )*/
                    })
                    isItemClicked = false
                    Handler().postDelayed({
                        binding!!.drawerLayout.closeDrawer(GravityCompat.END)
                    }, 100)
                    return@setNavigationItemSelectedListener true
                }

                R.id.ImpLinks -> {
                    Handler().postDelayed({
                        binding!!.drawerLayout.closeDrawer(GravityCompat.END)
                    }, 100)
                    isItemClicked = false
                    return@setNavigationItemSelectedListener true
                }

                /*
                R.id.faqs -> {
                    switchActivity(Constants.Intent.Webview, false, Bundle().apply {
                        putString(
                            Constants.IntentDataKeys.TITLE,
                            getString(R.string.label_faqs_text)
                        )
                        putString(Constants.IntentDataKeys.LINK, BuildConfig.URL_FAQ)
                    })
                    Handler().postDelayed({
                        drawerLayout.closeDrawer(GravityCompat.START)
                    }, 100)
                    return@setNavigationItemSelectedListener true
                }*/

                R.id.support -> {
                    switchActivity(Constants.Intent.Webview, false, Bundle().apply {
                        putString(
                            Constants.IntentDataKeys.TITLE, getString(R.string.label_support_text)
                        )/*  putString(
                              Constants.IntentDataKeys.LINK,
                              BuildConfig.URL_SUPPORT
                          )*/
                    })
                    isItemClicked = false
//                    showSupport(getString(R.string.support_text), getString(R.string.label_support_text))
                    Handler().postDelayed({
                        binding!!.drawerLayout.closeDrawer(GravityCompat.END)
                    }, 100)
                    return@setNavigationItemSelectedListener true
                }

                R.id.howToUse -> {
                    switchActivity(Constants.Intent.Webview, false, Bundle().apply {
                        putString(
                            Constants.IntentDataKeys.TITLE,
                            getString(R.string.label_how_to_use_text)
                        )
                        putString(
                            Constants.IntentDataKeys.LINK, Constants.Firebase.FAQ_URL
                        )
                    })
                    isItemClicked = false

//                    showSupport(getString(R.string.support_text), getString(R.string.label_support_text))
                    Handler().postDelayed({
                        binding!!.drawerLayout.closeDrawer(GravityCompat.END)
                    }, 100)

                    return@setNavigationItemSelectedListener true
                }

                R.id.FAQ -> {
                    switchActivity(Constants.Intent.Webview, false, Bundle().apply {
                        putString(
                            Constants.IntentDataKeys.TITLE, getString(R.string.label_support_text)
                        )/* putString(
                             Constants.IntentDataKeys.LINK,
                             BuildConfig.URL_SUPPORT
                         )*/
                    })
                    isItemClicked = false
//                    showSupport(getString(R.string.support_text), getString(R.string.label_support_text))
                    Handler().postDelayed({
                        binding!!.drawerLayout.closeDrawer(GravityCompat.END)
                    }, 100)

                    return@setNavigationItemSelectedListener true
                }

                R.id.reviewApp -> {
                    isItemClicked = false
                    val reviewUrl = Constants.Firebase.App_Review_URL + sharedPreferencesUtil.fetchUserProfile().userId
                    switchActivity(Constants.Intent.Webview, false, Bundle().apply {
                        putString(
                            Constants.IntentDataKeys.TITLE,
                            getString(R.string.label_review_text)
                        )
                        putString(
                            Constants.IntentDataKeys.LINK,
                            reviewUrl
                        )
                    })
                    Log.e("reviewApp","reviewApp_Code : $reviewUrl")
                   /// reviewApp()
//                    showSupport(getString(R.string.support_text), getString(R.string.label_support_text))
                    Handler().postDelayed({
                        binding!!.drawerLayout.closeDrawer(GravityCompat.END)
                    }, 100)

                    return@setNavigationItemSelectedListener true
                }

                R.id.logout -> {
                    isItemClicked = false
                    binding!!.drawerLayout.closeDrawer(GravityCompat.END)
                    showAlertLogout(getString(R.string.alert_logout_user))
                    return@setNavigationItemSelectedListener true
                }

                R.id.pro_features -> {

                    /*if (sharedPreferencesUtil.fetchUserProfile().isProMembership) {
                        Log.e("Home_Activity", "ProUser_IF")
                        startActivity(Intent(this, TransactionHistoryActivity::class.java))
                        Handler().postDelayed({
                            isItemClicked = false
                            drawerLayout.closeDrawer(GravityCompat.END)
                        }, 100)
                    }else{
                        Log.e("Home_Activity", "ProUser_els")
                        AlertBuyPremium.Builder(this@HomeActivity, Constants.DialogTheme.NoTitleBar)
                            .build().show()
                        Handler().postDelayed({
                            drawerLayout.closeDrawer(GravityCompat.END)
                            isItemClicked = false
                        }, 100)
                    }*/

                    startActivity(Intent(this, TransactionHistoryActivity::class.java))
                    Handler().postDelayed({
                        isItemClicked = false
                        binding!!.drawerLayout.closeDrawer(GravityCompat.END)
                    }, 100)
                    return@setNavigationItemSelectedListener true
                }

                else -> {
                    isItemClicked = false
                    return@setNavigationItemSelectedListener true
                }
            }
        }

    }

    private fun closeMenuFilter() {
        if (binding!!.includedLayHome.includedLayNearby.fabAM.isOpened) {
            binding!!.includedLayHome.includedLayNearby.fabAM.close(true)
            binding!!.includedLayHome.includedLayNearby.fabAM.menuIconView.setImageDrawable(
                resources.getDrawable(R.drawable.menu_icon)
            )
            isOpen = false
        }
    }

    private fun setSelectedBottomBar(view: View) {
        binding!!.includedLayHome.ivFeed.isSelected = false
        binding!!.includedLayHome.ivNearby.isSelected = false
        binding!!.includedLayHome.ivChat.isSelected = false
        binding!!.includedLayHome.ivHeart.isSelected = false
        binding!!.includedLayHome.ivTimeLine.isSelected = false

        view.isSelected = true


        if ((lat == "0.0") && (lng == "0.0")) {/*tvNoLocation.visibility = View.VISIBLE
            tvNoLocation.text = String.format(
                getString(R.string.text_rational_location), getString(R.string.app_name)
            )*/
            //  hideProgressView(rl_progress)
            //checkRequiredPermissions()
            return
        }
        when (view.id) {
            R.id.ivFeed -> {

                /*sharedPreferencesUtil.saveCurrentStateScreen("ivFeed")

                if (gStrProTag!!) {
                    if (ivFeed.isSelected) {

                        binding!!.includedLayHome.includedLayNearby.tvProNearby.visibility = View.GONE

                    } else {

                        tv_pro_nearby.visibility = View.GONE
                    }
                }


                setupActionBarNavigation("Events", true, R.drawable.menu_icon)
                setSelectedBottomLayout(rlFeeds)
                nextFeed = 1
                feedsAdapter.clearFeeds()
                showProgressView(rl_progress)
                feeds()*/

                bottomNavigationView.selectedItemId = R.id.bottom_nav_events

            }


            R.id.ivTimeLine -> {

                /*sharedPreferencesUtil.saveCurrentStateScreen("ivTimeLine")

                Log.e("gStrPro", "gStrProTag" + gStrProTag)

                val profile = sharedPreferencesUtil.fetchUserProfile()

                Log.e("gStrPro1", "gStrProTag" + profile.isProMembership)

                if (profile.isProMembership!!) {
                    if (ivTimeLine.isSelected) {

                        tv_pro_nearby.visibility = View.GONE

                    } else {

                        tv_pro_nearby.visibility = View.GONE
                    }
                }

                setupActionBarNavigation(
                    "TimeLines",
                    true,
                    R.drawable.menu_icon
                )



                setSelectedBottomLayout(rlTimeLine)
                timeline()*/

                bottomNavigationView.selectedItemId = R.id.bottom_nav_timeline

            }


            R.id.ivNearby -> {

                /*sharedPreferencesUtil.saveCurrentStateScreen("ivNearby")

                if (gStrProTag!!) {
                    if (ivNearby.isSelected) {

                        tv_pro_nearby.visibility = View.VISIBLE
                    } else {

                        tv_pro_nearby.visibility = View.GONE
                    }
                }

                setupActionBarNavigation(
                    getString(R.string.title_nearby),
                    true,
                    R.drawable.menu_icon
                )
                setSelectedBottomLayout(rlNearby)

                nearByList.clear()
                nearByListAdminAds.clear()

                nearByListRecentOnlyAds.clear()
                nearByListSwitchs.clear()

                nearByAdapter.clearNearBy()
                nextNearby = 1


                initiateAdminADs()
                showProgressView(rl_progress)
                mostRecentlyJoindUser()
                nearby()*/

                bottomNavigationView.selectedItemId = R.id.bottom_nav_nearBy
            }

            R.id.ivChat -> {

                /*sharedPreferencesUtil.saveCurrentStateScreen("ivChat")

                if (gStrProTag!!) {
                    if (ivChat.isSelected) {

                        tv_pro_nearby.visibility = View.GONE

                    } else {

                        tv_pro_nearby.visibility = View.GONE
                    }
                }

                setupActionBarNavigation(getString(R.string.title_chat), true, R.drawable.menu_icon)
                // adminConversation()
                // showLocalConversation()


                setSelectedBottomLayout(binding!!.includedLayHome.includedLChat.rlChat)*/

                bottomNavigationView.selectedItemId = R.id.bottom_nav_chat
            }

            R.id.ivHeart -> {

                /*sharedPreferencesUtil.saveCurrentStateScreen("ivHeart")

                if (gStrProTag!!) {
                    if (ivHeart.isSelected) {

                        tv_pro_nearby.visibility = View.GONE

                    } else {

                        tv_pro_nearby.visibility = View.GONE
                    }
                }


                setupActionBarNavigation(
                    getString(R.string.title_favorites),
                    true,
                    R.drawable.menu_icon
                )
                setSelectedBottomLayout(notification_lay)*/

                bottomNavigationView.selectedItemId = R.id.bottom_nav_favorite
            }
        }
    }

    @SuppressLint("RestrictedApi")
    private fun setSelectedBottomLayout(view: View) {
        binding!!.includedLayHome.includedFeed.rlFeeds.visibility = View.GONE
        binding!!.includedLayHome.includedLayNearby.rlNearby.visibility = View.GONE
        binding!!.includedLayHome.includedLChat.rlChat.visibility = View.GONE
        binding!!.includedLayHome.includedNotification.notificationLay.visibility = View.GONE
        binding!!.includedLayHome.includedTimeline.rlTimeLine.visibility = View.GONE

        view.visibility = View.VISIBLE

        if (binding!!.includedLayHome.includedLayNearby.fabAM.isOpened) {
            binding!!.includedLayHome.includedLayNearby.fabAM.close(true)
            binding!!.includedLayHome.includedLayNearby.fabAM.menuIconView.setImageDrawable(
                resources.getDrawable(R.drawable.menu_icon)
            )
            isOpen = false
        }

        /*  when (view.id) {
              R.id.rlNearby -> {
                  //fabFilterNew.visibility = View.VISIBLE
                  //layout_switch.visibility = View.VISIBLE
              }
              R.id.notification_lay -> {

                  rlFeeds.visibility = View.GONE
                  rlNearby.visibility = View.GONE
                  rlChat.visibility = View.GONE
                  binding!!.includedLayHome.includedNotification.notificationLay.visibility = View.VISIBLE
                  binding!!.includedLayHome.includedTimeline.rlTimeLine.visibility = View.GONE
              }
              else -> {
                  fabFilterNew.visibility = View.GONE
                  layout_switch.visibility = View.GONE
              }
          }*/
    }

    private fun initiateFeedsUI() {
        linearLayoutManager = LinearLayoutManager(
            this@HomeActivity, RecyclerView.VERTICAL, false
        )
        with(binding!!.includedLayHome.includedFeed.rvFeeds) {
            layoutManager = linearLayoutManager
            adapter = feedsAdapter
        }
        addOnScrollListenerFeed()
    }

    private fun addOnScrollListenerFeed() {
        binding!!.includedLayHome.includedFeed.rvFeeds.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val total = linearLayoutManager!!.itemCount
                val lastVisibleItemCount = linearLayoutManager!!.findLastVisibleItemPosition()
                Log.e("ScrollView", "addOnScrollListenerFeed")
                //to avoid multiple calls to loadMore() method
                //maintain a boolean value (isLoading). if loadMore() task started set to true and completes set to false
                if (loadMoreFeed) {
                    if (total > 0) if (total - 1 == lastVisibleItemCount) {
                        loadMoreFeed = false
                        feeds()
                    }
                }
            }
        })
    }

    private fun addOnScrollListenerRecent() {
        binding!!.includedLayHome.includedLayNearby.rvMostRecent.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val total = linearLayoutManagerRecent!!.itemCount
                val lastVisibleItemCount = linearLayoutManagerRecent!!.findLastVisibleItemPosition()

                Log.e("ScrollView", "addOnScrollListenerRecent")
                //to avoid multiple calls to loadMore() method
                //maintain a boolean value (isLoading). if loadMore() task started set to true and completes set to false
                if (loadMoreRecent) {
                    if (total > 0) if (total - 1 == lastVisibleItemCount) {
                        loadMoreRecent = false
                        mostRecentlyJoindUser()
                    }
                }
            }
        })
    }

    private fun initiateNearByUI() {
        gridLayoutManager = GridLayoutManager(this@HomeActivity, 4)/*gridLayoutManager?.setSpanSizeLookup(object : GridLayoutManager.SpanSizeLookup() {

            override fun getSpanSize(position: Int): Int {
                return if ((position + 1) % 17 == 0 && (position + 1) != 1) { // totalRowCount : How many item you want to show
                    4
                } else 1
            }
        })*/

        with(binding!!.includedLayHome.includedLayNearby.rvNearby) {
            layoutManager = gridLayoutManager
            adapter = nearByAdapter
            hasFixedSize()
        }
        Log.e("ScrollView23", "initiateNearByUI")
        /// addOnScrollListenerNearby()
    }

    private fun initiateMostRecentUI() {
        linearLayoutManagerRecent = LinearLayoutManager(
            this@HomeActivity, RecyclerView.HORIZONTAL, false
        )
        with(binding!!.includedLayHome.includedLayNearby.rvMostRecent) {
            layoutManager = linearLayoutManagerRecent
            adapter = mostRecentlyJoinedUserAdapter
            hasFixedSize()
        }

        addOnScrollListenerRecent()

    }

    var unreadCountF = 0
    var unreadCountArr = ArrayList<Int>()

    private fun initiateConversationUI() {

        with(binding!!.includedLayHome.includedLChat.rvConversation) {
            layoutManager = LinearLayoutManager(
                binding!!.includedLayHome.includedLChat.rvConversation.context,
                RecyclerView.VERTICAL,
                false
            )
            adapter = conversationAdapter
            addItemDecoration(
                DividerItemDecoration(
                    binding!!.includedLayHome.includedLChat.rvConversation.context,
                    DividerItemDecoration.VERTICAL
                )
            )
        }


        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding!!.includedLayHome.includedLChat.rvGroupConversation.layoutManager = layoutManager

        binding!!.includedLayHome.includedLChat.rvGroupConversation.addItemDecoration(
            DividerItemDecoration(
                binding!!.includedLayHome.includedLChat.rvGroupConversation.context,
                DividerItemDecoration.VERTICAL
            )
        )

        //getUserId()
        //getUnreadCount()

        val mHandler = Handler()
        mHandler.postDelayed(Runnable {
            Log.d("UnreadCount", unreadCountF.toString())

            setGroupChat()
        }, 1000)

        //showLocalConversation()
        //updateConversation()

        Log.d(
            "RvChatCount",
            binding!!.includedLayHome.includedLChat.rvConversation.adapter!!.itemCount.toString()
        )

        if (binding!!.includedLayHome.includedLChat.rvConversation.adapter!!.itemCount <= 0) {

            binding!!.includedLayHome.includedLChat.personalChaTtxt.visibility = View.GONE
        } else {

            binding!!.includedLayHome.includedLChat.personalChaTtxt.visibility = View.VISIBLE
        }

    }

    private fun setGroupChat() {
        recyclerModal = ArrayList()

        val db = FirebaseDatabase.getInstance(Constants.Firebase.databaseUrl).getReference("Live")
            .child("ChatGroup")
        var userId = ""

        val dbUser =
            FirebaseDatabase.getInstance(Constants.Firebase.databaseUrl).getReference("Live")
                .child("UserProfile")
        val profile = sharedPreferencesUtil.fetchUserProfile()

        dbUser.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snap: DataSnapshot) {

                if (snap.exists()) {

                    for (i in snap.children) {

                        Log.d("FirebaseuserName", profile.name)

                        if (i.child("userName").value.toString() == profile.name) {

                            Log.d("UserId", i.child("userId").value.toString())

                            getSharedPreferences("UserId", MODE_PRIVATE).edit()
                                .putString("UserId", i.child("userId").value.toString()).apply()
                            userId = i.child("userId").value.toString()
                        }
                    }
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })

        val savedUserID =
            getSharedPreferences("UserId", MODE_PRIVATE).getString("UserId", "").toString()

        Log.d("FirebaseUserId", savedUserID.toString())



        db.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snap: DataSnapshot) {

                if (snap.exists()) {

                    recyclerModal.clear()

                    Log.d("FirebaseUserId", savedUserID)
                    //binding!!.includedLayHome.includedLChat.swipeRefreshChat.isRefreshing = true

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


                                    recycleGroupConvoAdapter = RecycleGroupConvoAdapter(
                                        this@HomeActivity, recyclerModal
                                    )

                                    binding!!.includedLayHome.includedLChat.rvGroupConversation.adapter =
                                        recycleGroupConvoAdapter

                                    recycleGroupConvoAdapter.notifyDataSetChanged()

                                    Log.d(
                                        "RvChatCount",
                                        binding!!.includedLayHome.includedLChat.rvGroupConversation.adapter!!.itemCount.toString()
                                    )

                                    if (binding!!.includedLayHome.includedLChat.rvGroupConversation.adapter!!.itemCount > 0) {

                                        binding!!.includedLayHome.includedLChat.groupChattxt.visibility =
                                            View.VISIBLE

                                    } else {
                                        binding!!.includedLayHome.includedLChat.groupChattxt.visibility =
                                            View.GONE

                                    }
                                    if (conversationAdapter.itemCount > 0) {

                                        binding!!.includedLayHome.includedLChat.personalChaTtxt.visibility =
                                            View.VISIBLE

                                    } else {

                                        binding!!.includedLayHome.includedLChat.personalChaTtxt.visibility =
                                            View.GONE
                                    }

                                    if (binding!!.includedLayHome.includedLChat.rvGroupConversation.adapter!!.itemCount <= 0 && conversationAdapter.itemCount <= 0) {

                                        binding!!.includedLayHome.includedLChat.noChatImg.visibility =
                                            View.VISIBLE
                                        binding!!.includedLayHome.includedLChat.tvNoChat.visibility =
                                            View.VISIBLE
                                        binding!!.includedLayHome.includedLChat.startNewConvoTbn.visibility =
                                            View.VISIBLE

                                    } else {

                                        binding!!.includedLayHome.includedLChat.noChatImg.visibility =
                                            View.GONE
                                        binding!!.includedLayHome.includedLChat.tvNoChat.visibility =
                                            View.GONE
                                        binding!!.includedLayHome.includedLChat.startNewConvoTbn.visibility =
                                            View.GONE
                                    }
                                }

                                //},1000)


                                //val unreadCount = getUnreadCount(i.key.toString(), db, savedUserID).toString()

                            }
                        }
                    }

                    if (binding!!.includedLayHome.includedLChat.rvGroupConversation.adapter == null) {

                        Log.d("rvGroupNull", "yes")
                        binding!!.includedLayHome.includedLChat.rvGroupConversation.visibility =
                            View.GONE
                    } else {

                        Log.d("rvGroupNull", "no")
                        Log.d(
                            "rvGroupNull",
                            binding!!.includedLayHome.includedLChat.rvGroupConversation.adapter!!.itemCount.toString()
                        )
                        if (binding!!.includedLayHome.includedLChat.rvGroupConversation.adapter!!.itemCount <= 0) {

                            binding!!.includedLayHome.includedLChat.rvGroupConversation.visibility =
                                View.GONE
                            binding!!.includedLayHome.includedLChat.groupChattxt.visibility =
                                View.GONE
                        } else {

                            binding!!.includedLayHome.includedLChat.rvGroupConversation.visibility =
                                View.VISIBLE
                            binding!!.includedLayHome.includedLChat.groupChattxt.visibility =
                                View.VISIBLE
                        }

                        binding!!.includedLayHome.includedLChat.rvGroupConversation.visibility =
                            View.VISIBLE
                    }

                    if (binding!!.includedLayHome.includedLChat.rvGroupConversation.adapter != null) {

                        if (binding!!.includedLayHome.includedLChat.rvGroupConversation.adapter!!.itemCount <= 0 && conversationAdapter.itemCount <= 0) {

                            Log.d("rvGroupChat", "no")

                            binding!!.includedLayHome.includedLChat.tvNoChat.visibility =
                                View.VISIBLE
                            binding!!.includedLayHome.includedLChat.noChatImg.visibility =
                                View.VISIBLE
                            binding!!.includedLayHome.includedLChat.startNewConvoTbn.visibility =
                                View.VISIBLE
                        }

                    }
                }
            }

            override fun onCancelled(p0: DatabaseError) {


            }

        })

        Handler().postDelayed(object : Runnable {
            override fun run() {

                //listenForUserCall()
            }

        }, 3000)
    }

    var roomID = ""

    private fun listenForUserCall() {

        val userId = getSharedPreferences("UserId", MODE_PRIVATE).getString("UserId", "").toString()

        Log.d("listenCall", userId)

        FirebaseDatabase.getInstance(Constants.Firebase.databaseUrl).getReference("Live")
            .child("VideoChatRoom").orderByChild("createdForId").equalTo(userId).limitToFirst(1)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snap: DataSnapshot) {

                    if (snap.exists()) {

                        for (i in snap.children) {

                            if (i.child("createdForId").value.toString() == userId) {

                                /*Toast.makeText(
                                    this@ChatActivity,
                                    "room available for you",
                                    Toast.LENGTH_SHORT
                                ).show()*/

                                val callFrom = i.child("createdByName").value.toString()
                                val callFromID = i.child("createdById").value.toString()

                                val roomId = i.key.toString()

                                if (roomID.isEmpty()) {

                                    roomID = roomId

                                    showJoinDialog(callFrom, roomId, callFromID)
                                } else if (roomID != roomId) {

                                    roomID = roomId
                                    showJoinDialog(callFrom, roomId, callFromID)
                                }

                                Log.d("listenCall", "Call Incomming")
                            }
                        }
                    } else {

                        //showJoinDialog("", "", "")
                    }
                }

                override fun onCancelled(p0: DatabaseError) {

                }
            })
    }

    private fun showJoinDialog(callFrom: String, roomId: String, callFromID: String) {

        dialog = Dialog(this)

        dialog.setCancelable(true)

        val view = layoutInflater.inflate(R.layout.custom_exit_dialog, null)

        dialog.setContentView(view)

        dialog.window!!.attributes.windowAnimations = R.style.DialogAnimation

        if (dialog.window != null) {
            dialog.window!!.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
            )
        }
        dialog.window!!.setGravity(Gravity.CENTER)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val dialogHead = view.findViewById<TextView>(R.id.dialog_title)
        val dialogContent = view.findViewById<TextView>(R.id.dialog_content)
        val cancelBtn = view.findViewById<Button>(R.id.noExitBtn)
        val confirm = view.findViewById<Button>(R.id.yesExitBtn)

        dialogHead.text = "Call from $callFrom"
        dialogContent.text = "Accept or Reject the call"
        cancelBtn.text = "Reject"
        confirm.text = "Accept"

        cancelBtn.setOnClickListener {

            dialog.dismiss()
            clearRoom(roomId)
        }

        confirm.setOnClickListener {

            if (checkPermissionGranted()) {

                val bundle = Bundle()
                bundle.putString("otherUserId", callFromID)
                bundle.putString("otherUserName", callFrom)
                bundle.putString("otherUserImg", "")
                bundle.putString("type", "join")
                bundle.putString("roomId", roomId)

                switchActivity(Constants.Intent.PersonalCall, false, bundle)

                dialog.dismiss()
            } else {

                askPermissions()
            }
        }

        dialog.show()

    }

    private fun askPermissions() {

        ActivityCompat.requestPermissions(this, permissions, requestCode)
    }

    private fun checkPermissionGranted(): Boolean {

        for (permission in permissions) {

            if (ActivityCompat.checkSelfPermission(
                    this, permission
                ) != PackageManager.PERMISSION_GRANTED
            ) {

                return false
            }
        }

        return true
    }

    private fun clearRoom(id: String) {

        val videoCallRoomDb =
            FirebaseDatabase.getInstance(Constants.Firebase.databaseUrl).getReference("Live")
                .child("VideoChatRoom")

        videoCallRoomDb.child(id).removeValue()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (resultCode) {
            Activity.RESULT_OK -> {
                when (requestCode) {
                    Constants.RequestCode.AUTOCOMPLETE_REQUEST_CODE -> {

                        val place = Autocomplete.getPlaceFromIntent(data!!)
                        LogManager.logger.i(
                            ArchitectureApp.instance!!.tag,
                            "Place: " + place.name + ", " + place.id + ", Address : " + place.address
                        )
                        Constants.Search.place = place
                        switchActivityOnly(Constants.Intent.SearchLocation, false)

                        /*requestFeed.searchPlace = "${place.name}"
                        search = "${place.name}"
                        lat = "${place.latLng!!.latitude}"
                        lng = "${place.latLng!!.longitude}"
                        //rlLocationSearch.visibility = View.VISIBLE
                        ivClear.visibility = View.VISIBLE
                        //view1.visibility = View.VISIBLE
                        //tvLocation.text = search
                        showProgressView(rl_progress)
                        nearByList.clear()
                            //locationSearch.location_txt.text = search

                        nearByListAdminAds.clear()
                        nearByListRecentOnlyAds.clear()
                        nearByListSwitchs.clear()
                        nearByAdapter.clearNearBy()
                        nearByAdapter?.notifyDataSetChanged()
                        nextNearby = 1
                        initiateAdminADs()
                        nearby()*/
                        //  nearbyOnResult()
                        /* val params = RelativeLayout.LayoutParams(
                             RelativeLayout.LayoutParams.WRAP_CONTENT,
                             RelativeLayout.LayoutParams.WRAP_CONTENT
                         )
                         params.topMargin = resources.getDimensionPixelOffset(R.dimen._48sdp)
                         params.leftMargin = resources.getDimensionPixelOffset(R.dimen._12sdp)
                         fabFilterNew.layoutParams = params*/
                    }

                    Constants.RequestCode.AUTOCOMPLETE_REQUEST_CODE_EVENT -> {

                        val place = Autocomplete.getPlaceFromIntent(data!!)
                        LogManager.logger.i(
                            ArchitectureApp.instance!!.tag,
                            "Place: " + place.name + ", " + place.id + ", Address : " + place.address
                        )
                        Constants.Search.place = place
                        switchActivity(Constants.Intent.CreateEvent, false, Bundle())
                    }

                    Constants.RequestCode.TIMELINE_REQUEST_CODE -> {

                        //  Toast.makeText(this@HomeActivity, "req code", Toast.LENGTH_LONG).show()

                        var results: Array<Uri?>? = null
                        try {

                            val dataString = data!!.dataString
                            val clipData = data.clipData
                            if (clipData != null) {
                                results = arrayOfNulls(clipData.itemCount)
                                for (i in 0 until clipData.itemCount) {
                                    val item = clipData.getItemAt(i)
                                    results[i] = item.uri
                                }
                            }
                            if (dataString != null) {
                                results = arrayOf(Uri.parse(dataString))
                            }
                        } catch (e: java.lang.Exception) {
                            e.printStackTrace()
                        }
                        uploadMessage?.onReceiveValue(results as? Array<Uri>)
                        uploadMessage = null

                        /*if (uploadMessage == null) return
                            uploadMessage!!.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(resultCode, data))
                            uploadMessage = null*/
                    }

                }
            }

            Activity.RESULT_CANCELED -> {
                // uploadMessage!!.onReceiveValue(null)
                Log.e("onActivityResult", "RESULT_CANCELED")
                uploadMessage = null
            }


            AutocompleteActivity.RESULT_ERROR -> {
                val status = Autocomplete.getStatusFromIntent(data!!)
                LogManager.logger.e(ArchitectureApp.instance!!.tag, "${status.statusMessage}")
                Log.e("onActivityResult", "RESULT_ERROR")
                universalToast("${status.statusMessage}")
                uploadMessage = null
            }
        }
    }

    private fun autoPlacePicker() {

        // nearByList.clear()
        /**
         * Initialize Places. For simplicity, the API key is hard-coded. In a production
         * environment we recommend using a secure mechanism to manage API keys.
         */
        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, getString(R.string.google_server_key))
        }

        // Set the fields to specify which types of place data to return.
        val fields =
            listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS)

        // Start the autocomplete intent.
        val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields)
            .setHint("Search Users by City").setTypeFilter(TypeFilter.CITIES).build(this)
        startActivityForResult(intent, Constants.RequestCode.AUTOCOMPLETE_REQUEST_CODE)
    }

    private fun autoPlacePickerEvent() {
        // nearByList.clear()
        /**
         * Initialize Places. For simplicity, the API key is hard-coded. In a production
         * environment we recommend using a secure mechanism to manage API keys.
         */
        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, getString(R.string.google_server_key))
        }

        // Set the fields to specify which types of place data to return.
        val fields =
            listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS)

        // Start the autocomplete intent.
        val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields)
            .setHint("Search Users by City").setTypeFilter(TypeFilter.CITIES).build(this)
        startActivityForResult(intent, Constants.RequestCode.AUTOCOMPLETE_REQUEST_CODE_EVENT)
    }

    var a = -1

    private fun nestedScroolView() {
        Log.e("nestedScroolView", "ScrollListenerNearby%%%")
        val total = gridLayoutManager!!.itemCount
        val lastVisibleItemCount = gridLayoutManager!!.findLastVisibleItemPosition()
        var countPro: Int = 0
        Log.e("nestedScroolView", "lastVisibleItemCount:- " + total)

        val itemPosition: Int = gridLayoutManager!!.findFirstVisibleItemPosition()

        Log.e("pos", itemPosition.toString())


        if (loadMoreNearby) {
            if (total > 36) if (total - 1 == lastVisibleItemCount) {

                // TODO : changed by abhina
                val user = sharedPreferencesUtil.fetchUserProfile()
                when (user.isProMembership) {
                    true -> {/*  loadMoreNearby = false
                              binding!!.includedLayHome.playerProgressbar.visibility = View.VISIBLE
                              getNearbyUser()*/
                        binding!!.includedLayHome.includedLayNearby.becomePro2.rlPro.visibility =
                            View.VISIBLE
                        binding!!.includedLayHome.includedLayNearby.becomePro2.proText.text =
                            getString(R.string.click_to_load_more)
                    }

                    false -> {

                        if (nearByListAdminAds.isEmpty()) {
                            countPro = 72
                        } else {
                            countPro = 80
                        }

                        if (nearByAdapter.itemCount > countPro) {
                            binding!!.includedLayHome.includedLayNearby.becomePro2.rlPro.visibility =
                                View.VISIBLE
                            binding!!.includedLayHome.includedLayNearby.becomePro2.proText.text =
                                getString(R.string.upgrade_to_pro)
                            Log.e("nestedScroolView", "Scroll-IFF")/* val paint = pro_text.paint
                                 val paint1 = see_text.paint
                                 val width1 = paint1.measureText(see_text.text.toString())
                                 val width = paint.measureText(pro_text.text.toString())
                                 val textShader: Shader = LinearGradient(
                                     0f, 0f, width, pro_text.textSize, intArrayOf(
                                         Color.parseColor("#F4447E"),
                                         Color.parseColor("#8448F4"),
                                         Color.parseColor("#64B678"),
                                         Color.parseColor("#478AEA"),
                                         //Color.parseColor("#8446CC")
                                     ), null, Shader.TileMode.REPEAT
                                 )
                                 val textShader1: Shader = LinearGradient(
                                     0f, 0f, width1, see_text.textSize, intArrayOf(
                                         Color.parseColor("#F4447E"),
                                         Color.parseColor("#8448F4"),
                                         Color.parseColor("#64B678"),
                                         Color.parseColor("#478AEA"),
                                         //Color.parseColor("#8446CC")
                                     ), null, Shader.TileMode.REPEAT
                                 )

                                 pro_text.paint.shader = textShader
                                 see_text.paint.shader = textShader1*/

                        } else {
                            Log.e("nestedScroolView", "Scroll-ELSE")/*loadMoreNearby = false
                                player_progressbar.visibility = View.VISIBLE
                                getNearbyUser()*/
                            binding!!.includedLayHome.includedLayNearby.becomePro2.rlPro.visibility =
                                View.VISIBLE
                            binding!!.includedLayHome.includedLayNearby.becomePro2.proText.text =
                                getString(R.string.click_to_load_more)
                        }/* if (nearByAdapter.itemCount < 32) {
                                 loadMoreNearby = false
                                 nearby()
                                 become_pro.visibility = View.GONE
                             } //else if (nearByAdapter.itemCount == 32) {
                             else if (nearByAdapter.itemCount == 80) {
                                 become_pro.visibility = View.VISIBLE
                             }*/
                    }
                }

            } else {
                binding!!.includedLayHome.includedLayNearby.becomePro2.rlPro.visibility = View.GONE
                binding!!.includedLayHome.playerProgressbar.visibility = View.GONE
            }
        } else {
            binding!!.includedLayHome.includedLayNearby.becomePro2.rlPro.visibility = View.GONE
            binding!!.includedLayHome.playerProgressbar.visibility = View.GONE
        }

    }

    /*
        private fun reason() {
            if (null == mPresenter) {
                mPresenter = HomePresenter(presenter)
            }

            run {
                mPresenter!!.callRetrofit(ConstantsApi.REASON)
            }
        }
    */

    private fun feeds() {

        Log.e("nearUsers123- ", "Call_feeds_APi")
        if ((lat == "0.0") && (lng == "0.0")) {/*tvNoLocation.visibility = View.VISIBLE
            tvNoLocation.text = String.format(
                getString(R.string.text_rational_location), getString(R.string.app_name)
            )*/
            //checkRequiredPermissions()
            hideProgressView(progressBar)
            return
        }

        if (null == mPresenter) {
            mPresenter = HomePresenter(presenter)
        }

        run {
            mPresenter!!.addUserFeedObject(RequestFeed().apply {
                page = "$nextFeed"
                limit = "10"
                currentLat = "${sharedPreferencesUtil.fetchLocation().latitude}"
                currentLong = "${sharedPreferencesUtil.fetchLocation().longitude}"

            })
            mPresenter!!.callRetrofit(ConstantsApi.FETCH_FEEDS)
        }
    }

    private fun getNearbyUser() {
        if (flagNearbyCall) {
            binding!!.includedLayHome.includedFeed.swipeRefresh.isRefreshing = false
            hideProgressView(progressBar)
            return
        }

        if ((lat == "0.0") && (lng == "0.0")) {/*tvNoLocation.visibility = View.VISIBLE
            tvNoLocation.text = String.format(
                getString(R.string.text_rational_location), getString(R.string.app_name)
            )*/
            // hideProgressView(progressBar)
            checkRequiredPermissions()
            binding!!.includedLayHome.includedLayNearby.noLocNearBy.visibility = View.VISIBLE
            return
        } else {

            binding!!.includedLayHome.includedLayNearby.noLocNearBy.visibility = View.GONE
        }

        flagNearbyCall = true
        if (null == mPresenter) {
            mPresenter = HomePresenter(presenter)
        }
        run {
            mPresenter!!.addUserFeedObject(requestFeed.apply {
                page = "$nextNearby"
                limit = "40"
                currentLat = lat
                currentLong = lng
                lastLoginTimeStamp = "${getUTCTime()}"
            })

            mPresenter!!.callRetrofit(ConstantsApi.FETCH_NEARBY)

        }
    }

    /* private fun nearbyOnResult(){
         nearByList.clear()
         if ((lat == "0.0") && (lng == "0.0")) {
             tvNoLocation.visibility = View.VISIBLE
             tvNoLocation.text = String.format(
                 getString(R.string.text_rational_location), getString(R.string.app_name)
             )
             hideProgressView(rl_progress)
             checkRequiredPermissions()
             return
         }

         if (null == mPresenter) {
             mPresenter = HomePresenter(presenter)
         }

         run {
             mPresenter!!.addUserFeedObject(requestFeed.apply {
                 page = "$nextNearby"
                 limit = "32"
                 currentLat = lat
                 currentLong = lng
                 lastLoginTimeStamp = "${Constants.getUTCDateTime.getUTCTime()}"
             })
             mPresenter!!.callRetrofit(ConstantsApi.FETCH_NEARBY)

         }
     }*/

    private fun mostRecentlyJoindUser() {
        if ((lat == "0.0") && (lng == "0.0")) {/*tvNoLocation.visibility = View.VISIBLE
            tvNoLocation.text = String.format(
                getString(R.string.text_rational_location), getString(R.string.app_name)
            )*/
            // hideProgressView(rl_progress)
            //checkRequiredPermissions()
            return
        }

        if (null == mPresenter) {
            mPresenter = HomePresenter(presenter)
        }

        run {
            mPresenter!!.addUserFeedObject(requestFeed.apply {
                //  if ()
                page = "$nextRecent"
                limit = "32"
                currentLat = lat
                currentLong = lng
                lastLoginTimeStamp = "${getUTCTime()}"
            })
            mPresenter!!.callRetrofit(ConstantsApi.GET_MOST_RECENTLY_JOIND_USER)

        }
    }

    private fun updateUserDateTime() {

        if (null == mPresenter) {
            mPresenter = HomePresenter(presenter)
        }

        run {
            mPresenter!!.addUserFeedObject(requestFeed.apply {
                id = "$nextNearby"
                lastLoginTimeStamp = "${getUTCTime()}"
            })
            mPresenter!!.callRetrofit(ConstantsApi.UPADTE_TIME_STAMP)

        }
    }

    private fun showAlertForPremium(message: String) {
        val alertDialog = AlertDialog.Builder(this@HomeActivity, R.style.MyDialogTheme2)
        // Setting DialogAction Message
        alertDialog.setMessage(message)
        // On pressing SettingsActivity button
        alertDialog.setPositiveButton(getString(R.string.text_okay)) { dialog, _ ->
            dialog.dismiss()
            sharedPreferencesUtil.premiunNearby("true")
            binding!!.includedLayHome.includedLayNearby.becomePro.rlPro.visibility = View.GONE
            loadMoreNearby = false
            Log.e("CallNearBy%", "888")
            getNearbyUser()
        }
        alertDialog.setNegativeButton(getString(R.string.text_cancel)) { dialog, _ ->
            dialog.dismiss()
        }
        alertDialog.setCancelable(false)
        // Showing Alert Message
        alertDialog.show()
    }

    private fun showAlertLogout(message: String) {

        val dialog = Dialog(this)

        val view = layoutInflater.inflate(R.layout.custom_logout_dialog, null)

        dialog.setContentView(view)

        dialog.window!!.attributes.windowAnimations = R.style.DialogAnimation

        if (dialog.window != null) {
            dialog.window!!.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
            )
        }
        dialog.window!!.setGravity(Gravity.CENTER)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        // Configure Google Sign-In options
        val gso =
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build()

        val googleSignInClient = GoogleSignIn.getClient(this, gso)

        val noBtn = view.findViewById<Button>(R.id.noLogBtn)
        val yesBtn = view.findViewById<Button>(R.id.yesLogBtn)

        noBtn.setOnClickListener {

            dialog.dismiss()
        }

        yesBtn.setOnClickListener {

            dialog.dismiss()
            showProgressView(progressBar)

            googleSignInClient.signOut().addOnCompleteListener(this) {
                    // Sign-out successful, update UI accordingly
                    Log.e("GoogleSignIn", "signOut:success")
                }
            logoutUser()
        }

        dialog.show()

        /*val alertDialog = AlertDialog.Builder(this@HomeActivity, R.style.MyDialogTheme2)
        // Setting DialogAction Message
        //  alertDialog.setTitle(errorResponse.message)
        alertDialog.setMessage(message)
        // On pressing SettingsActivity button
        alertDialog.setPositiveButton(getString(R.string.text_logout)) { dialog, _ ->
            dialog.dismiss()
            showProgressView(progressBar)
            logoutUser()
        }
        alertDialog.setNegativeButton(getString(R.string.label_cancel_text)) { dialog, _ ->
            dialog.cancel()
        }
        alertDialog.setCancelable(false)
        // Showing Alert Message
        alertDialog.show()*/
    }

    private fun logoutUser() {
        if (null == mPresenter) mPresenter = HomePresenter(presenter)

        run {
            mPresenter!!.callRetrofit(ConstantsApi.LOGOUT)
        }
    }

    private fun likeEvent(eventId: String) {
        if (null == mPresenter) mPresenter = HomePresenter(presenter)

        run {
            mPresenter!!.addLikeEventObject(eventId)
            mPresenter!!.callRetrofit(ConstantsApi.EVENT_LIKE)
        }
    }

    private fun adjustFeedsUi() {
        when (feedsAdapter.itemCount > 0) {
            true -> {
                binding!!.includedLayHome.includedFeed.tvNoFeeds.visibility = View.GONE
                binding!!.includedLayHome.includedFeed.noEventImg.visibility = View.GONE
                binding!!.includedLayHome.includedFeed.rvFeeds.visibility = View.VISIBLE
            }

            false -> {
                binding!!.includedLayHome.includedFeed.tvNoFeeds.visibility = View.VISIBLE
                binding!!.includedLayHome.includedFeed.noEventImg.visibility = View.VISIBLE
                binding!!.includedLayHome.includedFeed.rvFeeds.visibility = View.GONE
            }
        }
    }

    private fun adjustNearbyUi() {
        when (nearByAdapter.itemCount > 0) {
            true -> {
                //binding!!.includedLayHome.includedLayNearby.swipeRefreshNearby.isRefreshing = false
                tvNoNearbyUser.visibility = View.GONE
                ivNoUserFound.visibility = View.GONE
                binding!!.includedLayHome.includedLayNearby.rvNearby.visibility = View.VISIBLE
                Log.e("ScrollView23", "adjustNearbyUi_visible")
            }

            false -> {
                binding!!.includedLayHome.includedLayNearby.rvNearby.visibility = View.GONE
                binding!!.includedLayHome.includedLayNearby.becomePro2.rlPro.visibility = View.GONE
                tvNoNearbyUser.visibility = View.VISIBLE
                ivNoUserFound.visibility = View.VISIBLE
                Log.e("ScrollView23", "adjustNearbyUi-Gone")
            }
        }
    }

    private fun adminConversation() {
        if (null == mPresenter) mPresenter = HomePresenter(presenter)

        run {
            mPresenter!!.callRetrofit(ConstantsApi.ADMIN_CONVERSATION)
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
                            }/*sharedPreferencesUtil.saveConversation(
                                if (data.isNotEmpty()) Gson().toJson(
                                    data
                                ) else ""
                            )*/
                            conversationAdapter.refresh()
                            //updateConversation()
                        }

                        false -> {
                            //sharedPreferencesUtil.saveConversation("")
                            conversationAdapter.refresh()
                            //updateConversation()
                        }
                    }
                    binding!!.includedLayHome.ivChat.isSelected = true
                    binding!!.includedLayHome.includedLChat.swipeRefreshChat.isRefreshing = false
                    hideProgressView(progressBar)
                }
            })
    }

    /*    private fun profileChanged() {
            firebaseUtil.fetchUserProfile(object : FirebaseChatListener {
                override fun onChatEventListener(snapshot: DataSnapshot, chat: FirebaseChat) {
                    LogManager.logger.i(ArchitectureApp.instance!!.tag, "Profile is ::: ${snapshot.key}")
                    when (chat) {
                        FirebaseChat.Added -> {
                            when (TextUtils.isDigitsOnly(snapshot.key)) {
                                true -> {
    //                                val profile = snapshot.getValue(ProfileFirebase::class.java)
    //                                nearByAdapter.updateOnlineOfflineStatus(profile!!)
    //                                conversationAdapter.updateOnlineOfflineStatus(profile!!)
                                    sharedPreferencesUtil.saveOtherUserProfile(
                                        "${snapshot.key}",
                                        Gson().toJson(snapshot.value)
                                    )
                                    LogManager.logger.i(ArchitectureApp.instance!!.tag, "Profile is ::: Added $snapshot")
                                }
                            }

                        }

                        FirebaseChat.Changed -> {
                            when (TextUtils.isDigitsOnly(snapshot.key)) {
                                true -> {
    //                                val profile = snapshot.getValue(ProfileFirebase::class.java)
    //                                nearByAdapter.updateOnlineOfflineStatus(profile!!)
    //                                conversationAdapter.updateOnlineOfflineStatus(profile)
                                    sharedPreferencesUtil.saveOtherUserProfile(
                                        "${snapshot.key}",
                                        Gson().toJson(snapshot.value)
                                    )
                                    LogManager.logger.i(ArchitectureApp.instance!!.tag, "Profile is ::: Changed $snapshot")
                                }
                            }
                        }

                        FirebaseChat.Removed -> {
                            when (TextUtils.isDigitsOnly(snapshot.key)) {
                                true -> {
    //                                val profile = snapshot.getValue(ProfileFirebase::class.java)!!
    //                                profile!!.status = 0
    //                                profile.imgUrl = ""
    //                                nearByAdapter.updateOnlineOfflineStatus(profile)
    //                                conversationAdapter.updateOnlineOfflineStatus(profile)
    //                                sharedPreferencesUtil.remove("${snapshot.key}")
    //                                sharedPreferencesUtil.saveOtherUserProfile(
    //                                    "${snapshot.key}",
    //                                    Gson().toJson(snapshot.value)
    //                                )
    //                                LogManager.logger.i(ArchitectureApp.instance!!.tag, "Profile is ::: Changed $snapshot")
                                }
                            }
                        }
                    }
                }
            })
        }'*/

    private fun fetchConversations() {
        val userId = sharedPreferencesUtil.userId()
        LogManager.logger.i(ArchitectureApp.instance!!.tag, "User id : $userId")
        when (userId.isEmpty()) {
            true -> {
                showAlertLogout("Your session has been expired. Please login again.")
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

    private fun updateConvo(conversationModel: ConversationModel) {
        val convo = sharedPreferencesUtil.fetchConversation()
        val index =
            convo.firstOrNull { conversationModel.convoId == it.convoId }?.let { convo.indexOf(it) }
                ?: -1

        if (index > -1) {
            convo[index] = conversationModel
            //sharedPreferencesUtil.saveConversation(Gson().toJson(convo))
            //conversationAdapter.update(conversationModel)
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

                    false -> {}
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
                        binding!!.includedLayHome.tvUnReadCount.text = "${messageUnreadCount.size}"
                    }

                    false -> {}
                }
            }
        }
        binding!!.includedLayHome.tvUnReadCount.text = "${messageUnreadCount.size}"
        binding!!.includedLayHome.tvUnReadCount.setTextColor(
            ContextCompat.getColor(
                this@HomeActivity,
                R.color.white
            )
        )

        when (messageUnreadCount.isEmpty()) {
            true -> {
                binding!!.includedLayHome.tvUnReadCount.visibility = View.INVISIBLE
            }

            false -> {
                binding!!.includedLayHome.tvUnReadCount.visibility = View.VISIBLE
            }
        }
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

                    false -> {}
                }
            }
        })
    }

    private fun updateConversation() {
        when (conversationAdapter.itemCount > 0) {
            true -> {
                binding!!.includedLayHome.includedLChat.rvConversation.visibility = View.VISIBLE
                binding!!.includedLayHome.includedLChat.tvNoChat.visibility = View.GONE
                binding!!.includedLayHome.includedLChat.noChatImg.visibility = View.GONE
                binding!!.includedLayHome.includedLChat.startNewConvoTbn.visibility = View.GONE
                binding!!.includedLayHome.includedLChat.searchChat.visibility = View.VISIBLE
            }

            false -> {
                binding!!.includedLayHome.includedLChat.rvConversation.visibility = View.GONE
                binding!!.includedLayHome.includedLChat.tvNoChat.visibility = View.VISIBLE
                binding!!.includedLayHome.includedLChat.noChatImg.visibility = View.VISIBLE
                binding!!.includedLayHome.includedLChat.startNewConvoTbn.visibility = View.VISIBLE
                binding!!.includedLayHome.includedLChat.searchChat.visibility = View.GONE
            }
        }

        if (binding!!.includedLayHome.includedLChat.rvGroupConversation.adapter != null) {

            Log.d(
                "rvGroupItem",
                binding!!.includedLayHome.includedLChat.rvGroupConversation.adapter!!.itemCount.toString()
            )

            if (binding!!.includedLayHome.includedLChat.rvGroupConversation.adapter!!.itemCount <= 0) {

                binding!!.includedLayHome.includedLChat.groupChattxt.visibility = View.GONE

            } else {

                binding!!.includedLayHome.includedLChat.groupChattxt.visibility = View.VISIBLE
            }

            //binding!!.includedLayHome.includedLChat.rvConversation.visibility = View.VISIBLE
            binding!!.includedLayHome.includedLChat.tvNoChat.visibility = View.GONE
            binding!!.includedLayHome.includedLChat.noChatImg.visibility = View.GONE
            binding!!.includedLayHome.includedLChat.startNewConvoTbn.visibility = View.GONE
            //binding!!.includedLayHome.includedLChat.searchChat.visibility = View.VISIBLE

            //binding!!.includedLayHome.includedLChat.groupChattxt.visibility = View.VISIBLE

        } else {

            binding!!.includedLayHome.includedLChat.groupChattxt.visibility = View.GONE
            binding!!.includedLayHome.includedLChat.tvNoChat.visibility = View.VISIBLE
            binding!!.includedLayHome.includedLChat.noChatImg.visibility = View.VISIBLE
            binding!!.includedLayHome.includedLChat.startNewConvoTbn.visibility = View.VISIBLE
        }

        if (binding!!.includedLayHome.includedLChat.rvGroupConversation.adapter != null || conversationAdapter.itemCount > 0) {

            binding!!.includedLayHome.includedLChat.tvNoChat.visibility = View.GONE
            binding!!.includedLayHome.includedLChat.noChatImg.visibility = View.GONE
            binding!!.includedLayHome.includedLChat.startNewConvoTbn.visibility = View.GONE
        }

        if (binding!!.includedLayHome.includedLChat.rvGroupConversation.adapter != null) {

            if (binding!!.includedLayHome.includedLChat.rvGroupConversation.adapter!!.itemCount <= 0 && conversationAdapter.itemCount <= 0) {

                Log.d("rvGroupChat", "no")

                binding!!.includedLayHome.includedLChat.tvNoChat.visibility = View.VISIBLE
                binding!!.includedLayHome.includedLChat.noChatImg.visibility = View.VISIBLE
                binding!!.includedLayHome.includedLChat.startNewConvoTbn.visibility = View.VISIBLE
            }

        }
    }

    /*
        private fun showSupport(message: String, title: String) {
            val alertDialog = AlertDialog.Builder(
                this@HomeActivity, R.style.MyDialogTheme2
            )
            // Setting DialogAction Message
            alertDialog.setTitle(title)
            val input = TextView(this@HomeActivity)
            input.text = message
            Linkify.addLinks(input, Linkify.EMAIL_ADDRESSES)
            input.linksClickable = true
            input.setTextColor(ContextCompat.getColor(this@HomeActivity, R.color.black))
            input.setPadding(
                resources.getDimensionPixelOffset(R.dimen.inner_img_xxl),
                resources.getDimensionPixelOffset(R.dimen.inner_img_s),
                resources.getDimensionPixelOffset(R.dimen.inner_img_xxl), 0
            )
            input.setLinkTextColor(ContextCompat.getColor(this@HomeActivity, R.color.colorRed))
            alertDialog.setView(input)
            // On pressing SettingsActivity button
            alertDialog.setPositiveButton(getString(R.string.text_okay)) { dialog, _ ->
                dialog.dismiss()

            }
            alertDialog.setCancelable(false)
            // Showing Alert Message
            alertDialog.show()
        }
    */

    private fun showAlertDeleteConversation(message: String, title: String, position: Int) {
        val alertDialog = AlertDialog.Builder(this@HomeActivity, R.style.MyDialogTheme2)
        // Setting DialogAction Message
        Log.e("Delete_Conv", "showAlertDeleteConversation")
        alertDialog.setTitle(title)
        alertDialog.setMessage(message)
        // On pressing SettingsActivity button
        alertDialog.setPositiveButton(getString(R.string.text_yes)) { dialog, _ ->
            dialog.dismiss()
            val convoId = conversationAdapter.convoId(position)
            deleteConversation(convoId)
            clearChat(convoId)
        }
        alertDialog.setNegativeButton(getString(R.string.text_no)) { dialog, _ ->
            dialog.dismiss()
        }
        alertDialog.setCancelable(false)
        // Showing Alert Message
        alertDialog.show()
    }

    private fun clearChat(convoId: String) {
        if (null == mPresenter) mPresenter = HomePresenter(presenter)

        run {
            mPresenter!!.addClearChatObject(RequestClearChat().apply {
                conversationId = convoId
                clearChatTime = "${System.currentTimeMillis()}"
            })
            mPresenter!!.callRetrofit(ConstantsApi.CLEAR_CHAT)
        }
    }

    /*    private fun updateUserProfileToFirebase() {
            val user = sharedPreferencesUtil.fetchUserProfile()
            if (user.id.trim().isNotEmpty()) {
                firebaseUtil.saveUserProfile(
                    ProfileFirebase(
                        "",
                        getApprovedImage(user.images),
                        true,
                        user.id.toInt(),
                        user.name,
                        sharedPreferencesUtil.fetchDeviceToken(),
                        true,
                        1,
                         ""+Constants.getUTCDateTime.getUTCTime()
                    )
                )
            }
    //        firebaseUtil.fetchUserProfile()
        }*/

    private fun getApprovedImage(images: ArrayList<ResponseUserProfileImages>): String {
        var image = ""
        var imgeFill = ""/* for (j in 0 until image.size){
             when (it.status){

             }

         }*/

        /* images.asSequence().forEach {
             when (it.status) {
                 "approved" -> {
                     image = it.imagePath
                     Log.v("imk", "" + image)
                 }
             }
         }*/

        run breaker@{
            images.asSequence().forEach {
                when (it.status) {
                    "approved" -> {
                        image = it.imagePath
                        // Log.v("imk", "" + image)
                        return@breaker
                    }
                }

            }
        }
        return image
    }

    private fun deleteConversation(convoId: String) {
        firebaseUtil.updateConversationDeleted(sharedPreferencesUtil.userId(), convoId)
    }


    override fun onNotification(remoteMessage: RemoteMessage) {
        val convo = sharedPreferencesUtil.fetchPopUpUserChatPush()

        menuNotificationView = findViewById(R.id.view3)



        if (remoteMessage != null && menuNotificationView != null) {

            //  Log.i("remoteMessage", "remoteMessage=" + remoteMessage?.data)
            if (remoteMessage.data["type"] == "chat" || remoteMessage.data["type"] == "admin-chat") {
                setCount(this@HomeActivity, sharedPreferencesUtil.fetchNotificationCount())

                if (remoteMessage.data["otherSide"].isNullOrEmpty()) {
                    return
                }

                if (remoteMessage.data["otherSide"] == "[]") {
                    return
                }


                var json = JSONObject(remoteMessage.data["otherSide"])
                //     Log.v("+remoteMessageotherSide", json.optString("chatMessage"))

                if (popup != null) {
                    if (popup!!.isShowing) {
                        popup!!.dismiss()
                    }
                }

                if (!binding!!.includedLayHome.ivChat.isSelected) {
                    try {

                        /*displayPopupWindow(
                            this,
                            menuNotificationView!!,
                            "<font color=#000000>Message From: </font> <font color=#ED2424>" + remoteMessage!!.data["senderName"] + "</font>",
                            if (json.has("chatMessage")) json.optString("chatMessage") else "",
                            json,
                            if (json.has("otherUserImageUrl")) json.optString("otherUserImageUrl") else "",
                            if (json.has("otherUserOnline")) json.optBoolean("otherUserOnline") else false
                        )*/
                    } catch (exception: java.lang.Exception) {
                        showAlert("" + exception.message, "Exception")
                    }

                }
            }

            /*  if (remoteMessage!!.data["type"] == "adminClearChatPushNotification") {
              //  var conversation: ConversationModel? = null
               // conversation!!.convoId = remoteMessage!!.data["convoId"]!!
               sharedPreferencesUtil.remove(remoteMessage!!.data["convoId"]!!)
                sharedPreferencesUtil.remove("${remoteMessage!!.data["convoId"]}_keys")

                supportclearChat(remoteMessage!!.data["convoId"]!!)

            }*/


            // working
            /*if (remoteMessage!!.data["type"] == "pushNotification") {
                sharedPreferencesUtil.savePopUpUserChatPush(remoteMessage!!.data.get("alert").toString())
                setCount(this@HomeActivity, sharedPreferencesUtil.fetchNotificationCount())

                  if (popup != null) {
                    if (popup!!.isShowing()) {
                        popup!!.dismiss()
                    }
                }

                if (!ivChat.isSelected) {
                    try {

                        displayPopupWindowAdmin(
                            this,
                            menuNotificationView!!,
                            "<font color=#000000>Message From: </font> <font color=#ED2424>" + remoteMessage!!.data.get(
                                "senderName"
                            ) + "</font>",
                            remoteMessage!!.data.get("alert").toString(), "", false
                                 )
                    } catch (exception: java.lang.Exception) {
                        showAlert("" + exception.message, "Exception")
                    }
                }
            }*/

            if (remoteMessage.data["type"] == "open-profile") {

                if (sharedPreferencesUtil.featchCurrentStateScreen() == "ivTimeLine") {

                    setCount(this@HomeActivity, sharedPreferencesUtil.fetchNotificationCount())

                    when (remoteMessage.data["userId"]) {
                        sharedPreferencesUtil.userId() -> {
                            startActivity(Intent(Constants.Intent.Profile))
                        }

                        else -> {
                            startActivity(
                                Intent(Constants.Intent.ProfileUser).putExtras(Bundle().apply {
                                    putInt(
                                        Constants.IntentDataKeys.UserId,
                                        (remoteMessage.data["userId"])!!.toInt()
                                    )
                                })
                            )

                        }


                    }
                }  //


            }
        }
    }

    private fun supportclearChat(convoId: String) {

        if (null == mPresenter) mPresenter = HomePresenter(presenter)

        run {
            mPresenter!!.addClearChatObject(RequestClearChat().apply {
                conversationId = convoId
                clearChatTime = "${System.currentTimeMillis()}"
            })
            mPresenter!!.callRetrofit(ConstantsApi.SUPPORT_CLEARCHAT)
        }

    }

    private fun callForSettings() {
        try {
            val settings = apiConnect.api.settings(
                sharedPreferencesUtil.loginToken(), Constants.Random.random()
            )

            settings.subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<Response<CommonResponse>> {
                    override fun onError(e: Throwable?) {
                        e!!.printStackTrace()

                    }

                    override fun onNext(response: Response<CommonResponse>?) {
                        LogManager.logger.i(
                            ArchitectureApp.instance!!.tag, "Response is ::: $response"
                        )
                        if (response != null) {
                            when (response.isSuccessful) {
                                true -> {
                                    val loginResponse = response.body()!!
                                    sharedPreferencesUtil.saveSettings(Gson().toJson(loginResponse.settings))
                                }

                                false -> {

                                }
                            }
                        } else {

                        }
                    }

                    override fun onCompleted() {
                    }

                })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private val profilePresenter = object : CreateProfileConnector.RequiredViewOps {
        override fun showToast(error: String, logout: Boolean?) {
            when (logout) {
                true -> {
                    logout()
                }

                false -> {}
                null -> {}
            }
            universalToast(error)
            hideProgressView(progressBar)
        }

        override fun showResponse(response: CommonResponse, type: ConstantsApi) {
            when (type) {

                ConstantsApi.FETCH_SELF_PROFILE -> {
                    val profile: ResponseUserData = response.userInfo
                    Log.e("FETCH_SELF_PROFILE", "Home_Pro: ${profile!!.isProMembership}")
                    Log.e("FETCH_SELF_PROFILE", "Home_Name: ${profile.name}")
                    Log.e(
                        "FETCH_SELF_PROFILE", "Home_count: ${profile.available_connection_counts}"
                    )
                    var visible_pro_user: Boolean? = profile.isProMembership
                    gStrProTag = visible_pro_user!!
                    Log.e("gStrProTags", "" + gStrProTag)

                    sharedPreferencesUtil.saveHasPrivateAlbum(profile.hasPrivatePic.toString())

                    // var menuView: Menu = nav_view.menu
                    if (visible_pro_user) {
                        navigationProUser!!.visibility = View.VISIBLE
                        binding!!.includedLayHome.includedLayNearby.tvProNearby.visibility =
                            View.VISIBLE
                    } else {
                        navigationProUser!!.visibility = View.GONE
                        binding!!.includedLayHome.includedLayNearby.tvProNearby.visibility =
                            View.GONE

                    }
                }

                else -> {}
            }

            //hideProgressView(rl_progress)
        }

    }

    private fun fetchSelfProfile() {
        Log.e("FETCH_SELF_PROFILE", "Home_Api_call")
        if (null == mProfilePresenter) mProfilePresenter = CreateProfilePresenter(profilePresenter)

        run {
            mProfilePresenter!!.callRetrofit(ConstantsApi.FETCH_SELF_PROFILE)
        }
    }

    private fun callForUserProfile(type: ConstantsApi) {
        try {
            val user = sharedPreferencesUtil.fetchUserProfile()

            val people = apiConnect.api.userProfile(
                user.id, sharedPreferencesUtil.loginToken(), Constants.Random.random()
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
                                    val response: CommonResponse? = response.body()
                                    val profile: ResponseUserData? = response?.userInfo
                                    var visible_pro_user: Boolean? = profile?.isProMembership
                                    gStrProTag = visible_pro_user!!
                                    Log.e("gStrProTags", "" + gStrProTag)

                                    sharedPreferencesUtil.saveHasPrivateAlbum(profile?.hasPrivatePic.toString())

                                    // var menuView: Menu = nav_view.menu
                                    if (visible_pro_user) {
                                        navigationProUser!!.visibility = View.VISIBLE
                                        binding!!.includedLayHome.includedLayNearby.tvProNearby.visibility =
                                            View.VISIBLE
                                    } else {
                                        navigationProUser!!.visibility = View.GONE
                                        binding!!.includedLayHome.includedLayNearby.tvProNearby.visibility =
                                            View.GONE

                                    }

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
    }


    private fun callForUserProfileImage(userId: String): String {


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
                                    val response: CommonResponse? = response.body()
                                    val profile: ResponseUserData? = response?.userInfo
                                    if (profile!!.images[0].status == "approved") {
                                        imagees = profile.images[0].imageThumb
                                    }
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


    /*private fun bandagefddf(){


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val builder: NotificationCompat.Builder? = NotificationCompat.Builder(applicationContext)
                .setContentTitle("Notification")
                .setContentText("This is a sample notification")
                .setSmallIcon(R.drawable.ic_round_appicon)
            val channel = NotificationChannel(
                "com.technical.myapplication",
                "Example",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager: NotificationManager =
                getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
            val notification: Notification = builder!!.build()
            ShortcutBadger.applyNotification(applicationContext, notification, 5)
            manager.notify(0, builder.build())
        } else {
            val builder: NotificationCompat.Builder =NotificationCompat. Builder(applicationContext)
                .setContentTitle("Notification")
                .setContentText("This is a sample notification")
                .setSmallIcon(R.drawable.ic_round_appicon)
            val manager: NotificationManager =
                getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            val notification: Notification = builder.build()
            ShortcutBadger.applyNotification(applicationContext, notification, 6)
            manager.notify(0, builder.build())
        }


    }*/


    /* fun bangaddd(context: Context, count: Int) {
         val launcherClassName = getLauncherClassName(context) ?: return
         val intent = Intent("android.intent.action.BADGE_COUNT_UPDATE")
         intent.putExtra("badge_count", count)
         intent.putExtra("badge_count_package_name", context.packageName)
         intent.putExtra("badge_count_class_name", launcherClassName)
         context.sendBroadcast(intent)
     }

     fun getLauncherClassName(context: Context): String? {
         val pm: PackageManager = context.packageManager
         val intent = Intent(Intent.ACTION_MAIN)
         intent.addCategory(Intent.CATEGORY_LAUNCHER)
         val resolveInfos: List<ResolveInfo> = pm.queryIntentActivities(intent, 0)
         for (resolveInfo in resolveInfos) {
             val pkgName: String = resolveInfo.activityInfo.applicationInfo.packageName
             if (pkgName.equals(context.packageName, ignoreCase = true)) {
                 return resolveInfo.activityInfo.name
             }
         }
         return null
     }*/

    private fun timeline() {


        /* webSettings.javaScriptEnabled = true
         webSettings.pluginState = WebSettings.PluginState.ON
         webSettings.javaScriptEnabled = true
         webSettings.domStorageEnabled = true
         webSettings.useWideViewPort = true
         webSettings.loadWithOverviewMode = true
         webSettings.javaScriptCanOpenWindowsAutomatically = true
         webSettings.allowFileAccess = true
         webSettings.allowFileAccessFromFileURLs = true
         webSettings.allowUniversalAccessFromFileURLs = true
         webSettings.loadsImagesAutomatically = true
         webSettings.cacheMode = WebSettings.LOAD_NO_CACHE



  //       timeline_main_webview.setLayerType(View.LAYER_TYPE_HARDWARE, null)
     var urladd: String = BuildConfig.URL_TIMELINE + sharedPreferencesUtil.loginToken()
     timeline_main_webview.loadUrl(urladd)

     timeline_main_webview.webViewClient = MyWebViewClient()
     timeline_main_webview.webChromeClient = object : WebChromeClient() {

         override fun onShowFileChooser(mWebView: WebView, filePathCallback: ValueCallback<Array<Uri>>,
                                        fileChooserParams: FileChooserParams
         ): Boolean {
             if (uploadMessage != null) {
                 uploadMessage!!.onReceiveValue(null)
                 uploadMessage = null
             }
             uploadMessage = filePathCallback

             Toast.makeText(this@HomeActivity, "Chooser", Toast.LENGTH_LONG).show()

             val intent = fileChooserParams.createIntent()
             try {
                 Toast.makeText(this@HomeActivity, "In", Toast.LENGTH_LONG).show()
                 startActivityForResult(intent, Constants.RequestCode.TIMELINE_REQUEST_CODE)
             } catch (e: ActivityNotFoundException) {
                 uploadMessage = null
                 return false
             }
             return true
         }

     }*/
        var wv1: WebView? = null

        wv1 = findViewById<View>(R.id.timeline_main_webview) as WebView
        wv1.webViewClient = MyBrowser()
        wv1.settings.loadsImagesAutomatically = true
        wv1.settings.javaScriptEnabled = true
        wv1.clearCache(true)



        wv1.scrollBarStyle = View.SCROLLBARS_INSIDE_OVERLAY

        Log.d("timeLineToken", sharedPreferencesUtil.loginToken())

        if (timeLinePushId == "") {
            //  wv1.loadUrl(BuildConfig.URL_TIMELINE + sharedPreferencesUtil.loginToken())
        } else {
            // wv1.loadUrl(BuildConfig.URL_TIMELINE + sharedPreferencesUtil.loginToken() + "?type=" + timeLinePushType + "&pushId=" + timeLinePushId)
        }
        timeLinePushId = ""
        timeLinePushType = ""

        val mWebSettings = wv1.settings
        mWebSettings.setSupportZoom(false)
        mWebSettings.allowFileAccess = true
        mWebSettings.allowContentAccess = true

        wv1.webChromeClient = object : WebChromeClient() {

            override fun onShowFileChooser(
                mWebView: WebView,
                filePathCallback: ValueCallback<Array<Uri>>,
                fileChooserParams: FileChooserParams
            ): Boolean {
                if (uploadMessage != null) {
                    uploadMessage!!.onReceiveValue(null)
                    uploadMessage = null
                }
                uploadMessage = filePathCallback
                val intent = fileChooserParams.createIntent()
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
                try {
                    startActivityForResult(intent, Constants.RequestCode.TIMELINE_REQUEST_CODE)
                } catch (e: ActivityNotFoundException) {

                    filePathCallback.onReceiveValue(null)
                    return false
                }
                return true
            }

        }


        //hideProgressView(rl_progress)


        /*  wv1.addJavascriptInterface(object : Any() {
              fun performClick() {
                  // Deal with a click on the OK button
              }
          }, "ok")*/

        //  "javascript:document.getElementsByClassName('user-post-details')[0].textContent",


        /*wv1.evaluateJavascript(
            "document.getElementsByClassName('timeline-user');\n" +
                    "        for(var i = 0; i < anchors.length; i++) {\n" +
                    "            var anchor = anchors[i];\n" +
                    "            anchor.onclick = function(event) {                \n" +
                    "                alert(event.currentTarget.dataset.userid);\n" +
                    "                console.log(event.currentTarget.dataset.userid);\n" +
                    "                event.preventDefault();\n" +
                    "            }\n" +
                    "        }",
            object : ValueCallback<String> {
                override fun onReceiveValue(value: String) {
                    universalToast(value)
                }
            }) */


        /*"\$(document).ready(function(){\n" +
                                   "  \$(\".timeline-user\").click(function(){\n" +
                                   "    var id=\$(this).attr(\"data-userid\");\n" +
                                   "  return id ;\n" +
                                   "  });\n" +
                                   "});",*/


        /* wv1.setOnTouchListener(object : View.OnTouchListener {
              override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                  when (event?.action) {
                      MotionEvent.ACTION_DOWN ->

                          wv1.evaluateJavascript(
                             "document.getElementsByClassName('appuserid')[0].innerHTML",

                              object : ValueCallback<String> {
                                  override fun onReceiveValue(value: String) {
                                      universalToast(value)

                                      if (value == "null") {
                                      } else {
                                        // val newValue: String = value.replace("""[$,]""".toRegex(), "")
                                         var newValue: String = value.substring(1);
                                         var newValues: String = newValue.substring(0, newValue.length - 1)

                                           //universalToast(value)
                                          when (newValues) {
                                              sharedPreferencesUtil.userId() -> {
                                                  startActivity(Intent(Constants.Intent.Profile))
                                              }

                                              else -> {
                                                  startActivity(
                                                      Intent(Constants.Intent.ProfileUser).putExtras(
                                                          Bundle().apply {
                                                              putInt(
                                                                  Constants.IntentDataKeys.UserId,
                                                                  newValues.toInt()
                                                              )
                                                          })
                                                  )

                                              }
                                          }
                                          newValue =""
                                          newValues = ""
                                      }
                                  }

                              })
                  }
                  return v?.onTouchEvent(event) ?: true
              }
          })*/

    }

    class MyBrowser : WebViewClient() {
        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
            view.loadUrl(url)
            return true
        }
    }

    /* @Suppress("OverridingDeprecatedMember")
     private inner class MyWebViewClient : WebViewClient() {
         override fun shouldOverrideUrlLoading(webView: WebView, url: String): Boolean {
             //  webView.webChromeClient = myWebChromeClient
             webView.loadUrl(url)
             return true
         }

         override fun onPageFinished(view: WebView?, url: String?) {
             super.onPageFinished(view, url)
             // progress_bar_tm.visibility = View.GONE
         }

         override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
             super.onPageStarted(view, url, favicon)
             runOnUiThread {
                 // progress_bar_tm.visibility = View.VISIBLE
             }
         }

         override fun onReceivedSslError(view: WebView, handler: SslErrorHandler, error: SslError) {
             AlertDialog.Builder(this@HomeActivity)
                 .setMessage(R.string.notification_error_ssl_cert_invalid)
                 .setPositiveButton(getString(R.string.text_continue)) { _, _ -> handler.proceed() }
                 .setNegativeButton(getString(R.string.label_cancel_text)) { _, _ -> handler.cancel() }
                 .create().show()


         }
     }

     private var mCustomView: View? = null

     private val myWebChromeClient: WebChromeClient = object : WebChromeClient() {
         override fun onShowCustomView(view: View?, callback: CustomViewCallback?) {
             if (mCustomView != null) {
                 callback!!.onCustomViewHidden()
                 return
             }
             fullscreen_custom_content_tl!!.addView(view!!, coverScreenGravityName)
             mCustomView = view
             linearlayout_tm.visibility = View.GONE
             fullscreen_custom_content_tl.visibility = View.VISIBLE
             fullscreen_custom_content_tl.bringToFront()
         }

         override fun onHideCustomView() {
             if (mCustomView == null)
                 return

             mCustomView!!.visibility = View.GONE
             fullscreen_custom_content_tl.removeView(mCustomView)
             mCustomView = null
             fullscreen_custom_content.visibility = View.GONE
             linearlayout_tm.visibility = View.VISIBLE
         }

         override fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean {
             LogManager.logger.i("Dating On DL", "Console Message is : $consoleMessage")
             return true
         }

         override fun onJsAlert(
             view: WebView?,
             url: String?,
             message: String?,
             result: JsResult?
         ): Boolean {
             LogManager.logger.d("Dating On DL", "$message")
             AlertDialog.Builder(view!!.context)
                 .setMessage(message).setCancelable(true).show()
             result!!.confirm()
             return true
         }
     }

     private var coverScreenGravityName = FrameLayout.LayoutParams(
         ViewGroup.LayoutParams.WRAP_CONTENT,
         ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER
     )*/

    /*private fun resetTimer() {
        mTimeLeftInMillis = Constants.TimerRefresh.START_TIME_IN_MILLIS
    }*/


    /*   private fun startTimer() {
       mEndTime = System.currentTimeMillis() + mTimeLeftInMillis
       mCountDownTimer = object : CountDownTimer(mTimeLeftInMillis, 1000) {
           override fun onTick(millisUntilFinished: Long) {
               mTimeLeftInMillis = millisUntilFinished
              // updateCountDownText()
           }

           override fun onFinish() {
               mTimerRunning = false
              // updateButtons()
           }
       }.start()
       mTimerRunning = true
       //updateButtons()
   }*/

    class CustomViewAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        val arrayFragment = ArrayList<Fragment>()
        val stringArray = ArrayList<String>()

        fun addFragment(fragment: Fragment, s: String) {

            this.arrayFragment.add(fragment)
            this.stringArray.add(s)
        }

        override fun getCount(): Int {
            return arrayFragment.size
        }

        override fun getItem(position: Int): Fragment {
            return arrayFragment.get(position)
        }

        override fun getPageTitle(position: Int): CharSequence {
            return stringArray.get(position)
        }

    }

    private fun updateUi() {
        when (notificationAdapter.itemCount > 0) {
            true -> {
                binding!!.includedLayHome.includedNotification.rvNotifications.visibility =
                    View.VISIBLE
                binding!!.includedLayHome.includedNotification.tvNoNotification.visibility =
                    View.GONE
                binding!!.includedLayHome.includedNotification.noNotiImg.visibility = View.GONE
            }

            false -> {
                binding!!.includedLayHome.includedNotification.rvNotifications.visibility =
                    View.GONE
                binding!!.includedLayHome.includedNotification.noNotiImg.visibility = View.VISIBLE
                binding!!.includedLayHome.includedNotification.tvNoNotification.visibility =
                    View.VISIBLE
            }
        }
    }

    private fun updateUiS() {
        when (searchAdapter.itemCount > 0) {

            true -> {
                //binding!!.includedLayHome.includedLayNearby.swipeRefreshNearby.visibility = View.GONE
                binding!!.includedLayHome.includedLayNearby.searchRvLay.visibility = View.VISIBLE
                binding!!.includedLayHome.includedLayNearby.fabFilterNew.visibility = View.GONE
                binding!!.includedLayHome.includedLayNearby.tvNoSearch.visibility = View.GONE
                binding!!.includedLayHome.includedLayNearby.rvSearch.visibility = View.VISIBLE
            }

            false -> {
                //swipeRefreshNearby.visibility = View.GONE
                binding!!.includedLayHome.includedLayNearby.searchRvLay.visibility = View.VISIBLE
                binding!!.includedLayHome.includedLayNearby.tvNoSearch.visibility = View.VISIBLE
                binding!!.includedLayHome.includedLayNearby.rvSearch.visibility = View.GONE
            }
        }
    }


    /*  Create static chat list by nilu for testing => 11_sept-24 only temporally testing */

    private fun sendDirectMsgInFirebase(msg: String, otherUserId: Int, profile: ResponseUserData) {
        val selfUserId = sharedPreferencesUtil.fetchUserProfile().userId
        val msgTime = System.currentTimeMillis().toString()
        val newMsgId = msgTime
        lateinit var chatDb: DatabaseReference
        lateinit var chatListDb: DatabaseReference
        val chatRoomId = Constants.ConvoId.id(
            selfUserId.toInt(), otherUserId
        )
        // val  chatRoomId = selfUserId + "-" + otherUserId
        //   val  otherChatRoomId =   otherUserId.toString() + "-" + selfUserId
        Log.e("SendMsgTime", newMsgId)
        Log.e("chatRoomId:- ", chatRoomId)

        chatDb =
            FirebaseDatabase.getInstance(Constants.Firebase.databaseUrl).getReference(Constants.Firebase.ChatDB)
                .child("ChatMessages")
        Log.e("chatRoomId:: ", chatRoomId)

        chatListDb =
            FirebaseDatabase.getInstance(Constants.Firebase.databaseUrl).getReference(Constants.Firebase.ChatDB)
                .child("ChatList")

        chatDb.child(chatRoomId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snap: DataSnapshot) {
                Log.e("NotExists", "Create One")
                val chatList = chatListDb.child(selfUserId).child(chatRoomId)
                val otherChatList = chatListDb.child(otherUserId.toString()).child(chatRoomId)
                Log.e("Send_Sms*&", "createUpdateChatList_Call1_self")

                lateinit var otherProfile: String
                if (profile.images.isNotEmpty()) {
                    otherProfile = profile.images[0].imagePath
                } else {
                    otherProfile = profile.userPic
                }
                createUpdateChatList(
                    chatList,
                    msg,
                    otherProfile,
                    "",
                    "",
                    "",
                    "",
                    0,
                    profile.name,
                    chatRoomId,
                    profile.userId,
                    otherUserId.toString()
                )

                if (sharedPreferencesUtil.fetchUserProfile().images.isNotEmpty()) {
                    Log.e("Send_Sms*&", "createUpdateChatList_Call2_Other")
                    createUpdateChatList(
                        otherChatList,
                        msg,
                        sharedPreferencesUtil.fetchUserProfile().images[0].imagePath,
                        "",
                        "",
                        "",
                        "",
                        1,
                        sharedPreferencesUtil.fetchUserProfile().name,
                        chatRoomId,
                        sharedPreferencesUtil.fetchUserProfile().userId,
                        otherUserId.toString()

                    )
                } else {
                    Log.e("Send_Sms*&", "createUpdateChatList_Call33_Other")
                    createUpdateChatList(
                        otherChatList,
                        msg,
                        sharedPreferencesUtil.fetchUserProfile().userPic,
                        "",
                        "",
                        "",
                        "",
                        1,
                        sharedPreferencesUtil.fetchUserProfile().name,
                        chatRoomId,
                        sharedPreferencesUtil.fetchUserProfile().userId,
                        otherUserId.toString()
                    )
                }

                val msgId = System.currentTimeMillis().toString()

                val msgDb = chatDb.child(chatRoomId).child("chats").push()

                val dataMap = HashMap<String, Any>()
                dataMap["messageText"] = msg
                dataMap["deliveredTime"] = msgId
                dataMap["deliveryStatus"] = "1"
                val childRefKey: String = msgDb.key.toString()
                val innerHashMap = HashMap<String, String>()
                innerHashMap[otherUserId.toString()] = ""
                innerHashMap[selfUserId] = ""
                dataMap["mediaDeviceUrlOf"] = innerHashMap
                val innerHashMapDelet = HashMap<String, Boolean>()
                innerHashMapDelet[otherUserId.toString()] = false
                innerHashMapDelet[selfUserId] = false
                dataMap["messagesDeletedBY"] = innerHashMapDelet
                dataMap["messagesDeletedBYSender"] = false
                dataMap["msgTime"] = System.currentTimeMillis()
                dataMap["messageTime"] = System.currentTimeMillis()
                dataMap["mediaLength"] = "0"
                dataMap["mediaType"] = 1
                dataMap["mediaUrl"] = ""
                dataMap["messageId"] = childRefKey
                dataMap["messageStatus"] = 1
                dataMap["messageType"] = 1
                dataMap["readTime"] = ""
                dataMap["readStatus"] = ""
                dataMap["mediaSize"] = ""
                dataMap["receiverId"] = otherUserId
                dataMap["repliedOnMediaName"] = ""
                dataMap["repliedOnMediaType"] = ""
                dataMap["repliedOnMediaUrl"] = ""
                dataMap["repliedOnMessageId"] = ""
                dataMap["repliedOnText"] = ""
                dataMap["senderId"] = selfUserId
                dataMap["senderName"] = sharedPreferencesUtil.fetchUserProfile().name
                dataMap["thumbnailUrl"] = ""
                msgDb.setValue(dataMap)

                Log.e("Sent_msg", "object2: $dataMap")
                Log.e("Sent_msg", "object2: $msgDb")


            }

            override fun onCancelled(p0: DatabaseError) {
                Log.e("firebase_Error", "${p0.message}")
            }
        }

        )

    }

    private fun createUpdateChatList(
        chatList: DatabaseReference,
        msg: String,
        otherImg: String,
        replyTo: String,
        replyMsg: String,
        replyId: String,
        replyMedia: String,
        unreadCount: Int,
        userName: String,
        chatRoomId: String,
        otherUserId: String,
        receiverId: String
    ) {

        Log.e("Send_Sms*&", "createUpdateChatList")
        chatList.child("chatOpen").setValue(false)
        chatList.child("convoId").setValue(chatRoomId)
        chatList.child("deliveredTime").setValue(System.currentTimeMillis())
        chatList.child("deliveryStatus").setValue("1")
        chatList.child("messageText").setValue(msg)
        chatList.child("messageTime").setValue(System.currentTimeMillis())
        chatList.child("otherUserOnline").setValue(true)
        chatList.child("readStatus").setValue("")
        chatList.child("readTime").setValue("")
        chatList.child("messagesDeletedBY").setValue("")
        chatList.child("messagesDeletedBYSender").setValue(false)
        ///  chatList.child("receiverId").setValue(profile!!.userId)
        chatList.child("receiverId").setValue(receiverId)
        chatList.child("blockedBY").setValue("")
        chatList.child("isUserConnected").setValue("yes")
        ///  chatList.child("isFirstMassage").setValue(true)
        chatList.child("otherUserId").setValue(otherUserId)
        chatList.child("repliedOnMediaName").setValue("")
        chatList.child("repliedOnMediaType").setValue("")
        chatList.child("repliedOnMediaUrl").setValue(replyMedia)
        chatList.child("repliedOnMessageId").setValue(replyId)
        chatList.child("repliedOnText").setValue(replyMsg)
        chatList.child("senderId").setValue(sharedPreferencesUtil.fetchUserProfile().userId)
        chatList.child("senderName").setValue(sharedPreferencesUtil.fetchUserProfile().name)
        chatList.child("unReadCount").setValue(unreadCount.toString())
        chatList.child("OtherUserImage").setValue(otherImg)
        chatList.child("otherUserName").setValue(userName)
    }
}
