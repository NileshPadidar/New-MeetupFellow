package com.connect.meetupsfellow.mvp.view.activities

import android.content.Intent
import android.content.IntentFilter
import android.location.Location
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import com.android.billingclient.api.*
import com.connect.meetupsfellow.BuildConfig
import com.connect.meetupsfellow.R
import com.connect.meetupsfellow.application.ArchitectureApp
import com.connect.meetupsfellow.components.activity.CustomAppActivityCompatViewImpl
import com.connect.meetupsfellow.constants.Constants
import com.connect.meetupsfellow.developement.interfaces.SharedPreferencesUtil
import com.connect.meetupsfellow.log.LogManager
import com.connect.meetupsfellow.mvp.view.model.LocationModel
import com.connect.meetupsfellow.receiver.ConnectionReceiver
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.google.gson.Gson
import javax.inject.Inject

class SplashActivity : CustomAppActivityCompatViewImpl() {

    @Inject
    lateinit var sharedPreferencesUtil: SharedPreferencesUtil

    private var isNotification = false
    var contentId = ""

    lateinit var isProUser: String
    private lateinit var mBillingClient: BillingClient

    private var mFusedLocationClient: FusedLocationProviderClient? = null
    private var mLocationRequest: LocationRequest? = null
    private var mLocationCallback: LocationCallback? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getIntentData()
        setContentView(R.layout.activity_splash)
        val tvVersion = findViewById<AppCompatTextView>(R.id.tvVersion)
        val tvCopyRight = findViewById<AppCompatTextView>(R.id.tvCopyRight)
        context = this@SplashActivity
        component.inject(this@SplashActivity)
        tvVersion.text = String.format(getString(R.string.text_version), BuildConfig.VERSION_NAME)
        tvCopyRight.text = getString(R.string.text_copy_right)
        fetchLocation()
        getDynamicLinkFromFirebase()/*getLocations()
        getLastLocation()FirebaseMessagingService
        createLocationRequest()*/

        ///handleDeepLink()
    }

    private fun getDynamicLinkFromFirebase() {
        FirebaseDynamicLinks.getInstance().getDynamicLink(intent).addOnSuccessListener {
            Log.e("DeepLink@", "intent: $intent")
            Log.e("DeepLink@", "I_data: ${intent.data}")
            val intent = intent
            if (intent != null && intent.data != null) {
                val deepLink = intent.data
                Log.e("DynamicLink@*", "data: $deepLink")
                if (deepLink != null) {
                    ///  navigateToDeepLink(deepLink)
                    contentId = deepLink.getQueryParameter("contentId").toString()
                    Log.e("DeepLink@*", "contentId11: $contentId")
                } else {
                    val deepLink = it?.link
                    if (deepLink != null) {
                        val contentIdParam = deepLink.getQueryParameter("contentId")
                        if (contentIdParam != null) {
                            contentId = contentIdParam
                            Log.e("DeepLink@*", "contentIdEls: $contentId")
                            // Navigate to the deep link if needed
                            // navigateToDeepLink(deepLink)
                        } else {
                            Log.e("DeepLink@*", "contentId parameter is null(*&")
                        }
                    }
                }
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Error: ${it.message}", Toast.LENGTH_SHORT).show()
        }
    }

    /*  private fun handleDeepLink() {
          val intent = intent
          if (intent != null && intent.data != null) {
              val deepLink = intent.data
              Log.e("DeepLink@*", "data: $deepLink")
              if (deepLink != null) {
                  navigateToDeepLink(deepLink)
              }
          }
      }*/

    private fun navigateToDeepLink(deepLink: Uri) {
        // Extract information from the deep link
        contentId = deepLink.getQueryParameter("contentId").toString()
        Log.e("DeepLink@*", "contentId: $contentId")
        Toast.makeText(this, "Link_id: $contentId", Toast.LENGTH_SHORT).show()
        // Use the information to navigate to the appropriate part of your app
        /*if (contentId != null) {
            val bundle = Bundle().apply {
                putString("contentId", contentId)
            }
            val navController = findNavController(R.id.nav_host_fragment).also {
                it.navigate(R.id.contentFragment, bundle)
            }
        }*/
    }

    override fun onStart() {
        super.onStart()
        splash()
    }

    private fun splash() {/*DisplayImage.with(this@SplashActivity)
            .shape(DisplayImage.Shape.GIF).into(splashGif).build()*/

        splashTimeout()
    }

    private fun splashTimeout() {
        Handler(Looper.getMainLooper()).postDelayed({
            sharedPreferencesUtil.premiunNearby("true")
            userLoginOrNot()
            startReceiver()
        }, 1500)
    }

    @Suppress("DEPRECATION")
    private fun startReceiver() {
        ArchitectureApp.instance!!.registerReceiver(
            ConnectionReceiver(), IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        )
    }

    private fun userLoginOrNot() {
        when (sharedPreferencesUtil.isUserLogin()) {
            true -> {
                create()
                getSharedPreferences("isUserLogin", MODE_PRIVATE).edit()
                    .putBoolean("isUserLogin", true).apply()

            }

            false -> {
                login()
            }
        }
    }

    private fun login() {
          switchActivity(Constants.Intent.GetStart, true, Bundle())
       /* val intent = Intent(this@SplashActivity,GetStartActivity::class.java)
        startActivity(intent)*/
    }

    private fun create() {

        val isAppPinEnabled =
            getSharedPreferences("appPinB", MODE_PRIVATE).getBoolean("appPinB", false)

        if (getSharedPreferences("appPin", MODE_PRIVATE).getString("appPin", "").toString()
                .isNotEmpty() && isAppPinEnabled
        ) {
            checkSubs()
            switchActivity(Constants.Intent.VerifyPin,
                true,
                if (intent.getStringExtra(Constants.Notification.SENDER_ID)
                        ?.isNotEmpty() == true
                ) intent.extras else Bundle().apply {
                    putString(Constants.IntentDataKeys.UserId, contentId.trim())
                })
           /* val intent = Intent(this@SplashActivity,VerifyPinActivity::class.java)
            if (intent.getStringExtra(Constants.Notification.SENDER_ID)
                    ?.isNotEmpty() == true
            ){
                val bundle = Bundle()
                bundle.putString(Constants.IntentDataKeys.UserId, contentId.trim())
                startActivity(intent,bundle)
                finish()
            }else{
                startActivity(intent,null)
            }*/

            Log.e("splash111$", "SetPin_enable")
        } else if (getSharedPreferences("Finger", MODE_PRIVATE).getBoolean("Finger", false)) {
            checkSubs()
            Log.e("splash111$", "yes_FingerPrint_enable")
            showFingerScanner()
        } else {
            checkSubs()
            getDeepLinkData()
        }
        /// checkSubs()
        // intent.extras["type"]

        Log.d("splash11111", "in")
    }

    private fun checkSubs() {

        Log.d("subscription Cancelled", "funcation called")


        mBillingClient = BillingClient.newBuilder(ArchitectureApp.instance!!)
            .setListener { billingResult, purchases ->
                when (billingResult.responseCode) {

                    BillingClient.BillingResponseCode.OK -> {

                        Log.d("subscription Cancelled", billingResult.responseCode.toString())

                        mBillingClient.queryPurchasesAsync(BillingClient.SkuType.SUBS,
                            PurchasesResponseListener { result, list ->
                                when (result.responseCode) {
                                    BillingClient.BillingResponseCode.USER_CANCELED -> {

                                        Log.d(
                                            "TAG",
                                            "subscription Cancelled" + result.responseCode.toString()
                                        )
                                    }

                                    BillingClient.BillingResponseCode.BILLING_UNAVAILABLE -> {

                                        Log.d(
                                            "TAG",
                                            "subscription Cancelled" + result.responseCode.toString()
                                        )
                                    }

                                    BillingClient.BillingResponseCode.SERVICE_UNAVAILABLE -> {

                                        Log.d(
                                            "TAG",
                                            "subscription Cancelled" + result.responseCode.toString()
                                        )
                                    }

                                    BillingClient.BillingResponseCode.ITEM_UNAVAILABLE -> {

                                        Log.d(
                                            "TAG",
                                            "subscription Cancelled" + result.responseCode.toString()
                                        )
                                    }

                                    BillingClient.BillingResponseCode.OK -> {

                                        Log.d(
                                            "TAG",
                                            "subscription Cancelled" + result.responseCode.toString()
                                        )
                                    }
                                }
                            })

                    }
                }
            }.enablePendingPurchases().build()

        mBillingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingServiceDisconnected() {
                LogManager.logger.i(ArchitectureApp.instance!!.tag, "Connection ended.")
            }

            override fun onBillingSetupFinished(billingResult: BillingResult) {
                when (billingResult.responseCode) {
                    BillingClient.BillingResponseCode.ERROR -> {
                        return
                    }
                }
            }

        })

        val params =
            QueryPurchaseHistoryParams.newBuilder().setProductType(BillingClient.ProductType.SUBS)

        mBillingClient.queryPurchaseHistoryAsync(params.build(),
            PurchaseHistoryResponseListener { result, list ->

                if (list != null) {

                    for (i in list) {

                        Log.d("list", i.toString())
                    }
                }
            })
    }

    private fun showFingerScanner() {

        val executor = ContextCompat.getMainExecutor(this)

        val biometricPrompt = androidx.biometric.BiometricPrompt(this@SplashActivity, executor,

            object : androidx.biometric.BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(
                    errorCode: Int, @NonNull errString: CharSequence
                ) {
                    Log.e("splash111", "Error_contentIdFinger: $errorCode")
                    if (errorCode == 13) {
                        finish()
                    } else if (errorCode == 11) {
                        Toast.makeText(
                            this@SplashActivity,
                            errString.toString(),
                            Toast.LENGTH_SHORT
                        ).show()
                         switchActivity(
                             Constants.Intent.Home,
                             true,
                             if (isNotification) intent.extras else Bundle()
                         )
                       /* val intent = Intent(this@SplashActivity,HomeActivity::class.java)
                        startActivity(intent,null)
                        finish()*/
                    } else if (errorCode == 7) {
                        Toast.makeText(
                            this@SplashActivity, errString.toString(), Toast.LENGTH_SHORT
                        ).show()

                        Handler().postDelayed({
                            finish()
                        }, 1000)
                    } else {

                        Log.d("BioError", errString.toString() + "   " + errorCode)
                    }


                    //finish()
                    super.onAuthenticationError(errorCode, errString)
                }

                // THIS METHOD IS CALLED WHEN AUTHENTICATION YOUR FINGERPRINT IS SUCCESS or NOT
                override fun onAuthenticationSucceeded(result: androidx.biometric.BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    //Toast.makeText(applicationContext, "Login Success", Toast.LENGTH_SHORT).show()

                    if (isNotification) {

                        when (intent.extras!!["type"].toString()) {

                            Constants.Notification.PostStatus, Constants.Notification.PostMention, Constants.Notification.PostNewComment, Constants.Notification.PostMentionComment, Constants.Notification.PostNewLike, Constants.Notification.PostNewUnlike, Constants.Notification.LikeComment, Constants.Notification.UnlikeComment -> {

                                Log.d("splash11112", "in")

                                startActivity(Intent(Constants.Intent.Home).putExtras(Bundle().apply {
                                    putString(
                                        Constants.IntentDataKeys.TimeLinePushId,
                                        intent.extras!!["postId"].toString()
                                    )
                                    putString(
                                        Constants.IntentDataKeys.TimeLinePost,
                                        intent.extras!!["type"].toString()
                                    )
                                    putString(
                                        Constants.IntentDataKeys.NotificationId,
                                        intent.extras!!["notificationId"].toString()
                                    )
                                }))

                            }

                            else -> {

                                Log.d("splash11113", "in")

                                   switchActivity(
                                       Constants.Intent.Home,
                                       true,
                                       if (isNotification) intent.extras else Bundle()
                                   )
                                /*val intent = Intent(this@SplashActivity,HomeActivity::class.java)
                                startActivity(intent,null)
                                finish()*/
                            }

                        }
                    } else {

                        Log.e("splash11114", "in_contentIdFinger")
                        Log.e("DeepLink@*", "contentIdFinger: $contentId")
                        if (contentId != null && contentId.isNotEmpty()) {
                            getDeepLinkData()
                        } else {
                            Log.e("DeepLink@*", "finger- No_Id SwitchHome")
                            ridyractToActivity()
                            ///switchActivity(Constants.Intent.Home, true, if (isNotification) intent.extras else Bundle())
                        }
                    }
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    Toast.makeText(
                        applicationContext, "FingerPrint Not Match", Toast.LENGTH_SHORT
                    ).show()
                }
            })

        val promptInfo = androidx.biometric.BiometricPrompt.PromptInfo.Builder().setTitle("Login")
            .setDescription("Verify FingerPrint to Unlock").setNegativeButtonText("Cancel").build()



        biometricPrompt.authenticate(promptInfo)
    }

    private fun getIntentData() {
        isNotification = intent.hasExtra(Constants.Notification.COLLAPSE_KEY)
    }

    private fun getDeepLinkData() {
        Log.e("DeepLink@*", "contentId&: $contentId")
        if (contentId != null && contentId.isNotEmpty()) {
            if (contentId.equals(sharedPreferencesUtil.userId())) {
                Log.e("DeepLink@*", "Ifff_Id")
                //   switchActivityOnly(Constants.Intent.Profile, true)
                 startActivity(
                     Intent(Constants.Intent.Profile).putExtras(Bundle().apply {
                         putInt(Constants.IntentDataKeys.LINK, 1)
                     })
                 )
                finish()
            } else {
                Log.e("DeepLink@*", "els_AuthoreUser")
                  startActivity(
                      Intent(Constants.Intent.ProfileUser).putExtras(Bundle().apply {
                          putInt(Constants.IntentDataKeys.UserId, contentId.trim().toInt())
                          putInt(Constants.IntentDataKeys.LINK, 1)
                          intent
                      })
                  )
                finish()
            }
            ///   contentId = ""
        } else {
            Log.e("DeepLink@*", "Elsse_Not_last")/* switchActivity(
             Constants.Intent.Home,
             true,
             if (isNotification) intent.extras else Bundle()
         )*/
            ridyractToActivity()

        }

    }

    private fun ridyractToActivity() {
        val senderIdN = intent.getStringExtra(Constants.Notification.SENDER_ID)
        Log.e("Notification_DAta", "senderIdN: $senderIdN")
        val RoomId = intent.getStringExtra(Constants.Notification.ROOM_ID)
        val senderImg = intent.getStringExtra(Constants.Notification.IMAGE)
        val senderName = intent.getStringExtra(Constants.Notification.USER_NAME)
        val type = intent.getStringExtra(Constants.Notification.TYPE)
        Log.e("Notification_DAta", "RoomId: $RoomId")
        if (type != null && type.isNotEmpty()) {
            when (type) {
                Constants.Notification.UserChat, Constants.Notification.Chat -> {
                    senderIdN?.let {
                        if (RoomId != null) {
                            //  universalToast(RoomId)
                            val intent = Intent(
                                context, ChatActivity::class.java
                            )
                            intent.putExtra("ChatRoomId", RoomId)
                            intent.putExtra("otherUserId", senderIdN)
                            intent.putExtra("otherUserName", senderName)
                            intent.putExtra("otherUserImg", senderImg)
                            intent.putExtra("firstTime", true)
                            //   intent.putExtra("IsUserConnected", users.get(pos).IsUserConnected)
                            context!!.startActivity(intent)
                            finish()
                        }
                    }
                }
                Constants.Notification.ProfileLike, Constants.Notification.Favourite, Constants.Notification.Following, Constants.Notification.BirthdayWish, Constants.Notification.NewUser, Constants.Notification.Connection -> {
                    senderIdN?.let {
                        // universalToast(senderIdN)
                        startActivity(
                            Intent(Constants.Intent.ProfileUser).putExtras(Bundle().apply {
                                putInt(Constants.IntentDataKeys.UserId, senderIdN.toInt())
                                putInt(Constants.IntentDataKeys.LINK, 1)
                            })
                        )
                        finish()
                    }
                }

                Constants.Notification.User_Viewed_profile -> {
                    if (sharedPreferencesUtil.fetchUserProfile().isProMembership && sharedPreferencesUtil.fetchUserProfile().currentPlanInfo!!.planId != 2) {
                        senderIdN?.let {
                            startActivity(
                                Intent(Constants.Intent.ProfileUser).putExtras(Bundle().apply {
                                    putInt(Constants.IntentDataKeys.UserId, senderIdN.toInt())
                                    putInt(Constants.IntentDataKeys.LINK, 1)
                                })
                            )
                            finish()
                        }
                    } else {
                          switchActivity(
                              Constants.Intent.Home,
                              true,
                              if (isNotification) intent.extras else Bundle()
                          )
                        /*val intent = Intent(this@SplashActivity,HomeActivity::class.java)
                        startActivity(intent,null)
                        finish()*/
                    }
                }

                Constants.Notification.eventInterest, Constants.Notification.EventLike -> {
                    senderIdN?.let {
                        // universalToast(senderIdN)
                          switchActivity(
                              Constants.Intent.Home,
                              true,
                              if (isNotification) intent.extras else Bundle()
                          )
                       /* val intent = Intent(this@SplashActivity,HomeActivity::class.java)
                        startActivity(intent,null)
                        finish()*/
                    }
                }

                else -> {
                    Log.e("Notification_DAta", "Coming By Notification_Else_But typeNot")
                     switchActivity(
                         Constants.Intent.Home, true, if (isNotification) intent.extras else Bundle()
                     )
                    /*val intent = Intent(this@SplashActivity,HomeActivity::class.java)
                    startActivity(intent,null)
                    finish()*/
                }
            }
        } else {
            Log.e("Notification_DAta", "Elss^%$")
            switchActivity(
                Constants.Intent.Home, true, if (isNotification) intent.extras else Bundle()
            )
            /*val intent = Intent(this@SplashActivity,HomeActivity::class.java)
            startActivity(intent,null)
            finish()*/
        }
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

    override fun onResume() {
        super.onResume()
        sharedPreferencesUtil.saveTimmerTime("")

    }


}