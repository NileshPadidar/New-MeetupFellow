package com.connect.meetupsfellow.mvp.view.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.core.content.ContextCompat
import com.connect.meetupsfellow.components.activity.CustomAppActivityCompatViewImpl
import com.connect.meetupsfellow.constants.Constants
import com.connect.meetupsfellow.databinding.ActivityVerifyPinBinding
import com.connect.meetupsfellow.developement.interfaces.SharedPreferencesUtil
import com.connect.meetupsfellow.global.view.otp.OTPListener
import javax.inject.Inject

class VerifyPinActivity : CustomAppActivityCompatViewImpl() {

    private var isNotification = false
    var contentId = ""
    var IsFirstTime = ""
    var active = false

    @Inject
    lateinit var sharedPreferencesUtil: SharedPreferencesUtil
    lateinit var binding: ActivityVerifyPinBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVerifyPinBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // setContentView(com.connect.meetupsfellow.R.layout.activity_verify_pin)
        component.inject(this@VerifyPinActivity)
        getIntentData()
        init()
    }

    override fun onStart() {
        super.onStart()
        if (active) {
            finish()
        }
        active = true
    }

    override fun onDestroy() {
        super.onDestroy()
        active = false
    }

    private fun init() {
        /// sharedPreferencesUtil.premiunNearby("true")
        val isAppPinEnabled =
            getSharedPreferences("appPinB", Context.MODE_PRIVATE).getBoolean("appPinB", false)
        val fingerPrint = getSharedPreferences("Finger", MODE_PRIVATE).getBoolean("Finger", false)

        if (getSharedPreferences("appPin", Context.MODE_PRIVATE).getString("appPin", "").toString()
                .isNotEmpty() && isAppPinEnabled && fingerPrint
        ) {
            showFingerScanner()
        } else if (fingerPrint) {
            showFingerScanner()
            binding.tvHeading.text = "Verify FingerPrint to Unlock"
            binding.tvDigitPin.visibility = View.GONE
            binding.pinView.visibility = View.GONE
            binding.pinVerify.visibility = View.GONE
        } else {
            Log.e("VeriftPin%", "Els_init")
        }

        binding.pinView.otpListener = object : OTPListener {
            override fun onInteractionListener() {

            }

            override fun onOTPComplete(otp: String) {


            }
        }

        binding.fingerScanner.setOnClickListener {

            if (getSharedPreferences("Finger", MODE_PRIVATE).getBoolean("Finger", false)) {
                showFingerScanner()
            } else {
                Toast.makeText(
                    this@VerifyPinActivity, "FingerPrint unlock is not enabled", Toast.LENGTH_SHORT
                ).show()
            }
        }

        binding.pinVerify.setOnClickListener {

            if (validateOtp()) {

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
                            if (IsFirstTime.equals("yes")) {
                                switchActivity(
                                    Constants.Intent.Home,
                                    true,
                                    if (isNotification) intent.extras else Bundle()
                                )
                            } else {
                                finish()
                            }
                        }

                    }
                } else {

                    Log.d("splash11114", "in_verifyPin")
                    if (contentId != null && contentId.isNotEmpty() && !contentId.equals("null")) {
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
                                    putInt(
                                        Constants.IntentDataKeys.UserId, contentId.trim().toInt()
                                    )
                                    putInt(Constants.IntentDataKeys.LINK, 1)
                                })
                            )
                            finish()
                        }
                        ///   contentId = ""
                    } else {
                        Log.e("DeepLink@*", "Elsse_Not_last")/*if (IsFirstTime.equals("yes")) {
                            switchActivity(
                                Constants.Intent.Home,
                                true,
                                if (isNotification) intent.extras else Bundle()
                            )
                        } else {
                            Log.e("DeepLink@*", "only_Finish")
                            finish()
                        }*/

                        ridyractToActivity()
                    }

                    /*switchActivity(
                        Constants.Intent.Home,
                        true,
                        if (isNotification) intent.extras else Bundle()
                    )*/

                }
                ///   Toast.makeText(this@VerifyPinActivity, "Pin Verified", Toast.LENGTH_SHORT).show()

            }
        }

    }

    private fun showFingerScanner() {

        val executor = ContextCompat.getMainExecutor(this)

        val biometricPrompt = androidx.biometric.BiometricPrompt(this@VerifyPinActivity, executor,

            object : androidx.biometric.BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(
                    errorCode: Int, @NonNull errString: CharSequence
                ) {
                    Log.d("VerifyPinActivity", errString.toString() + "   " + errorCode)

                    if (errorCode == 13) {
                        ///finish()/
                    } else if (errorCode == 11) {
                        Toast.makeText(
                            this@VerifyPinActivity, errString.toString(), Toast.LENGTH_SHORT
                        ).show()
                        switchActivity(
                            Constants.Intent.Home,
                            true,
                            if (isNotification) intent.extras else Bundle()
                        )
                    } else if (errorCode == 7) {
                        Toast.makeText(
                            this@VerifyPinActivity,
                            "Too many attempts, Please unlock with Pin or try again later.",
                            Toast.LENGTH_SHORT
                        ).show()

                    } else if (errorCode == 10) {
                        if (IsFirstTime.equals("yes")) {
                            showFingerScanner()
                        }
                        Log.e(
                            "BioError10",
                            "VerifyPinActivity: " + errString.toString() + "   " + errorCode
                        )
                    } else {

                        Log.d("BioError", errString.toString() + "   " + errorCode)
                    }

                    //finish()
                    super.onAuthenticationError(errorCode, errString)
                }

                // THIS METHOD IS CALLED WHEN AUTHENTICATION IS SUCCESS
                override fun onAuthenticationSucceeded(result: androidx.biometric.BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)

                    if (isNotification) {

                        when (intent.extras!!["type"].toString()) {

                            Constants.Notification.PostStatus, Constants.Notification.PostMention, Constants.Notification.PostNewComment, Constants.Notification.PostMentionComment, Constants.Notification.PostNewLike, Constants.Notification.PostNewUnlike, Constants.Notification.LikeComment, Constants.Notification.UnlikeComment -> {

                                Log.d("VerifyPinActivity", "in")

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

                                Log.d("VerifyPinActivity", "in")

                                /*if (IsFirstTime.equals("yes")) {
                                    switchActivity(
                                        Constants.Intent.Home,
                                        true,
                                        if (isNotification) intent.extras else Bundle()
                                    )
                                } else {
                                    finish()
                                }*/
                                ridyractToActivity()
                            }

                        }
                    } else {

                        Log.e("VerifyPinActivity", "in_contentIdFinger")

                        if (contentId != null && contentId.isNotEmpty() && !contentId.equals("null")) {
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
                                        putInt(
                                            Constants.IntentDataKeys.UserId,
                                            contentId.trim().toInt()
                                        )
                                        putInt(Constants.IntentDataKeys.LINK, 1)
                                    })
                                )
                                finish()
                            }
                            ///   contentId = ""
                        } else {
                            Log.e("VerifyPinActivity@*", "Elsse_Not_last_Finger")/*if (IsFirstTime.equals("yes")) {
                                switchActivity(
                                    Constants.Intent.Home,
                                    true,
                                    if (isNotification) intent.extras else Bundle()
                                )
                            } else {
                                Log.e("VerifyPinActivity@*", "only_Finish_finger")
                                finish()
                            }*/
                            ridyractToActivity()
                        }
                    }

                }
            })

        val promptInfo = androidx.biometric.BiometricPrompt.PromptInfo.Builder().setTitle("Verify")
            .setDescription("Verify FingerPrint to Unlock").setNegativeButtonText("Cancel").build()

        biometricPrompt.authenticate(promptInfo)
    }

    private fun validateOtp(): Boolean {

        val pinCode = getSharedPreferences("appPin", MODE_PRIVATE).getString("appPin", "")

        return when {
            (binding.pinView.otp.isEmpty()) -> {
                //universalToast("Please enter pin code")
                Toast.makeText(
                    this@VerifyPinActivity, "Please enter 6 digit pin code", Toast.LENGTH_SHORT
                ).show()
                false
            }

            (binding.pinView.otp.length < 6) -> {
                //universalToast("Please Enter 6 digit pin code")
                Toast.makeText(
                    this@VerifyPinActivity, "Pin code must be 6 digits", Toast.LENGTH_SHORT
                ).show()
                false
            }

            (binding.pinView.otp.toString() != pinCode.toString()) -> {
                Toast.makeText(
                    this@VerifyPinActivity, "Pin code does not match", Toast.LENGTH_SHORT
                ).show()

                false
            }

            else -> true
        }
    }

    private fun getIntentData() {
        if (intent != null) {
            isNotification = intent.hasExtra(Constants.Notification.COLLAPSE_KEY)
            contentId = intent.getStringExtra(Constants.IntentDataKeys.UserId).toString()
            IsFirstTime = intent.getStringExtra(Constants.IntentDataKeys.IsFirstTime).toString()
            Log.e("Verify_Data", "contentId;- $contentId")
            Log.e("Verify_Data", "IS_FirstTime;- $IsFirstTime")
        }
    }

    private fun ridyractToActivity() {
        val senderIdN = intent.getStringExtra(Constants.Notification.SENDER_ID)
        Log.e("Notification_DAta_V", "senderIdN: $senderIdN")
        val RoomId = intent.getStringExtra(Constants.Notification.ROOM_ID)
        val senderImg = intent.getStringExtra(Constants.Notification.IMAGE)
        val senderName = intent.getStringExtra(Constants.Notification.USER_NAME)
        val type = intent.getStringExtra(Constants.Notification.TYPE)
        Log.e("Notification_DAta_V", "RoomId: $RoomId")
        if (type != null && type.isNotEmpty()) {
            when (type) {
                Constants.Notification.UserChat, Constants.Notification.Chat -> {
                    senderIdN?.let {
                        if (RoomId != null) {
                            //  universalToast(RoomId)
                            val intent = Intent(
                                this, ChatActivity::class.java
                            )
                            intent.putExtra("ChatRoomId", RoomId)
                            intent.putExtra("otherUserId", senderIdN)
                            intent.putExtra("otherUserName", senderName)
                            intent.putExtra("otherUserImg", senderImg)
                            intent.putExtra("firstTime", true)
                            //   intent.putExtra("IsUserConnected", users.get(pos).IsUserConnected)
                            startActivity(intent)
                            finish()
                        }
                    }
                }

                Constants.Notification.ProfileLike, Constants.Notification.Favourite, Constants.Notification.Following, Constants.Notification.BirthdayWish, Constants.Notification.NewUser, Constants.Notification.Connection -> {
                    senderIdN?.let {
                        //   universalToast(senderIdN)
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
                    }
                }

                else -> {
                    Log.e("Notification_DAta", "Coming By Notification_Else_But typeNot")
                    switchActivity(
                        Constants.Intent.Home, true, if (isNotification) intent.extras else Bundle()
                    )
                }
            }
        } else {
            Log.e("Notification_DAta", "Elss^%$")
            switchActivity(
                Constants.Intent.Home, true, if (isNotification) intent.extras else Bundle()
            )
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}