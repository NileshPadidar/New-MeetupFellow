package com.connect.meetupsfellow.mvp.view.activities

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.connect.meetupsfellow.R
import com.connect.meetupsfellow.application.ArchitectureApp
import com.connect.meetupsfellow.components.activity.CustomAppActivityCompatViewImpl
import com.connect.meetupsfellow.constants.Constants
import com.connect.meetupsfellow.constants.ConstantsApi
import com.connect.meetupsfellow.databinding.ActivityLoginWithEmailBinding
import com.connect.meetupsfellow.developement.interfaces.SharedPreferencesUtil
import com.connect.meetupsfellow.log.LogManager
import com.connect.meetupsfellow.mvp.connector.activity.LoginConnector
import com.connect.meetupsfellow.mvp.presenter.activity.LoginPresenter
import com.connect.meetupsfellow.retrofit.request.LoginWithEmailRequest
import com.connect.meetupsfellow.retrofit.response.CommonResponse
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import java.util.Locale
import javax.inject.Inject

class LoginWithEmailActivity : CustomAppActivityCompatViewImpl() {

    private lateinit var googleSignInClient: GoogleSignInClient
    private var mAuth: FirebaseAuth? = null
    private var binding: ActivityLoginWithEmailBinding? = null
    private lateinit var progressBar: LinearLayout


    @Inject
    lateinit var sharedPreferencesUtil: SharedPreferencesUtil
    private var requestUserAuth = LoginWithEmailRequest()
    private var mPresenter: LoginConnector.PresenterOps? = null
    private val ForgetPasswordReq = com.connect.meetupsfellow.retrofit.request.ForgetPasswordReq()
    private val signInLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                handleSignInResult(task)
            }
        }

    private val presenter = object : LoginConnector.RequiredViewOps {
        override fun showResponse(response: CommonResponse, type: ConstantsApi) {

            // TODO: Changed by Nilu
            sharedPreferencesUtil.premiunNearby(response.userInfo.isProMembership.toString())
            when (type) {
                ConstantsApi.FETCH_PROFILE -> {
                    getSharedPreferences("isUserLogin", MODE_PRIVATE).edit()
                        .putBoolean("isUserLogin", true).apply()
                      switchActivity(Constants.Intent.Home, true, Bundle())
                    /*val intent = Intent(this@LoginWithEmailActivity, HomeActivity::class.java)
                    startActivity(intent, null)
                    finish()*/
                    hideProgressView(progressBar)
                }

                ConstantsApi.LOGIN_WITH_EMAIL -> {
                    when (response.status) {
                        Constants.ProfileStatus.Approved -> {
                            hideProgressView(progressBar)
                            profile()
                        }

                        Constants.ProfileStatus.Blocked -> {
                            showAlertForStatusRejected(
                                response.message, response.status.uppercase(Locale.getDefault())
                            )
                            hideProgressView(progressBar)
                        }

                        Constants.ProfileStatus.Unapproved -> {
                            universalToast(response.message)
                            switchActivity(Constants.Intent.Edit, false, Bundle())
                            Log.e("LOGIN_WITH_EMAIL:", "Presenter_ Login1")
                            hideProgressView(progressBar)
                        }

                        else -> {
                            when (response.is_verify) {
                                0 -> {
                                    showAlertDialog(
                                        response.message, "Check Your Email"
                                    )
                                    Log.e(
                                        "LOGIN_WITH_EMAIL:",
                                        "Email: ${binding!!.userEmailId.text.toString().trim()}"
                                    )/* val bundle = Bundle()
                                     bundle.putString(Constants.IntentDataKeys.User_Phone, "")
                                     bundle.putString(Constants.IntentDataKeys.Country_Code, "")
                                     bundle.putString(Constants.IntentDataKeys.User_email, userEmailId.text.toString().trim())
                                     bundle.putBoolean(Constants.IntentDataKeys.Is_Verify, false)
                                     switchActivity(Constants.Intent.Create, true, bundle)*/
                                }

                                else -> {
                                    universalToast(response.message)
                                }
                            }
                            hideProgressView(progressBar)
                        }
                    }
                }

                ConstantsApi.RESEND_EMAIL_VERIFICATION_LINK -> {
                    hideProgressView(progressBar)
                    universalToast(response.message)
                }

                else -> {
                    hideProgressView(progressBar)
                }
            }
        }

        override fun showToast(error: String, logout: Boolean?) {
            when (logout) {
                true -> {
                    logout()
                }

                false -> TODO()
                null -> TODO()
            }
            universalToast(error)
            hideProgressView(progressBar)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //  setContentView(R.layout.activity_login_with_email)
        component.inject(this@LoginWithEmailActivity)
        context = this@LoginWithEmailActivity
        binding = ActivityLoginWithEmailBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        progressBar = binding!!.includedLoading.rlProgress
        mAuth = FirebaseAuth.getInstance()
        startLoadingAnim()
        // Configure Google Sign-In options
        val gso =
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        init()
    }

    fun init() {
        Log.e("Activity", "ResetPassword_New")
        binding!!.userEmailId.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                if (!binding!!.userEmailId.text.toString().trim().isEmpty()) {
                    binding!!.userEmailId.background =
                        resources.getDrawable(R.drawable.rounded_corners_new)
                } else {
                    binding!!.userEmailId.background =
                        resources.getDrawable(R.drawable.round_corners_default)
                }
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })

        binding!!.userPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                if (!binding!!.userPassword.text.toString().trim().isEmpty()) {
                    binding!!.userPassword.background =
                        resources.getDrawable(R.drawable.rounded_corners_new)
                } else {
                    binding!!.userPassword.background =
                        resources.getDrawable(R.drawable.round_corners_default)
                }
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })

        binding!!.tvRecoverPassword.setOnClickListener {
              switchActivity(Constants.Intent.ForgotPasswordActivity, false, Bundle())
           /* val intent = Intent(this@LoginWithEmailActivity, ForgotPasswordActivity::class.java)
            startActivity(intent, null)*/
        }

        binding!!.tvSignUp.setOnClickListener {
            val bundle = Bundle()
            bundle.putString(Constants.IntentDataKeys.User_Phone, "")
            bundle.putString(Constants.IntentDataKeys.Country_Code, "")
            bundle.putString(Constants.IntentDataKeys.User_email, "")
            bundle.putBoolean(Constants.IntentDataKeys.Is_Verify, false)
             switchActivity(Constants.Intent.Create, true, Bundle())
           /* val intent = Intent(this@LoginWithEmailActivity, CreateProfileActivityNew::class.java)
            startActivity(intent, bundle)*/
        }

        binding!!.ivEmailLogin.setOnClickListener {
            ///   signIn()
            Toast.makeText(
                this@LoginWithEmailActivity, "Coming soon", Toast.LENGTH_SHORT
            ).show()
        }

        binding!!.ivMobileLogin.setOnClickListener {
             switchActivity(Constants.Intent.Login, false, Bundle())
           /* val intent = Intent(this@LoginWithEmailActivity, LoginActivity::class.java)
            startActivity(intent, null)*/
        }

        binding!!.btnSignIn.setOnClickListener {
            if (binding!!.userEmailId.text!!.isEmpty()) {
                Toast.makeText(
                    this@LoginWithEmailActivity, "Email cannot be empty.", Toast.LENGTH_SHORT
                ).show()
                binding!!.userEmailId.requestFocus()
            } else if (!isValidEmail(binding!!.userEmailId.text.toString())) {
                Toast.makeText(
                    this@LoginWithEmailActivity, "Please Enter valid email.", Toast.LENGTH_SHORT
                ).show()
                binding!!.userEmailId.requestFocus()
            } else if (binding!!.userPassword.text!!.isEmpty()) {
                Toast.makeText(
                    this@LoginWithEmailActivity, "password cannot be empty.", Toast.LENGTH_SHORT
                ).show()
                binding!!.userPassword.requestFocus()
            } else {
                loginUser()
                // Toast.makeText(this@LoginWithEmailActivity, "Call Login Api", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showAlertDialog(message: String, title: String) {
        val alertDialog = AlertDialog.Builder(
            this@LoginWithEmailActivity, R.style.MyDialogTheme2
        )
        alertDialog.setTitle(title)
        alertDialog.setMessage(message)
        alertDialog.setPositiveButton(getString(R.string.text_okay)) { dialog, _ ->
            dialog.dismiss()
        }
        alertDialog.setNegativeButton(getString(R.string.text_resend)) { dialog, _ ->
            dialog.dismiss()
            universalToast("call resend Api")
            resendVerifyLink()
        }
        alertDialog.setCancelable(false)

        alertDialog.show()
    }

    private fun isValidEmail(email: CharSequence?): Boolean {
        return !email.isNullOrEmpty() && android.util.Patterns.EMAIL_ADDRESS.matcher(email)
            .matches()
    }

    private fun startLoadingAnim() {

        val animation = android.view.animation.AnimationUtils.loadAnimation(
            applicationContext, R.anim.blink_anim
        )

        binding!!.includedLoading.animLogo.startAnimation(animation)

    }

    override fun onResume() {
        super.onResume()
        getCurrentVersion()
        checkRequiredPermissions()
    }

    private fun profile() {
        showProgressView(progressBar)
        if (null == mPresenter) mPresenter = LoginPresenter(presenter)

        run {
            showProgressView(progressBar)
            mPresenter!!.callRetrofit(ConstantsApi.FETCH_PROFILE)
        }
    }

    private fun loginUser() {
        val deviceToken = sharedPreferencesUtil.fetchDeviceToken()
        LogManager.logger.e(ArchitectureApp.instance!!.tag, "Device Token is ::: $deviceToken   ")
        when (deviceToken.isEmpty()) {
            true -> {
                fetchDeviceToken()
            }

            false -> {
                requestUserAuth.deviceToken = deviceToken
                login()
            }
        }
    }

    private fun resendVerifyLink() {
        showProgressView(progressBar)
        ForgetPasswordReq.email = binding!!.userEmailId.text.toString()
        if (null == mPresenter) mPresenter = LoginPresenter(presenter)

        run {
            showProgressView(progressBar)
            mPresenter!!.addSendVerifyLinkObject(ForgetPasswordReq)
            mPresenter!!.callRetrofit(ConstantsApi.RESEND_EMAIL_VERIFICATION_LINK)
        }
    }

    private fun login() {
        if (null == mPresenter) {
            mPresenter = LoginPresenter(presenter)
        }
        ///sharedPreferencesUtil.saveUserPhoneNumberPrivate(countryCodePicker.fullNumberWithPlus)
        //  Log.e("Number",""+ countryCodePicker.fullNumberWithPlus)

        val location = sharedPreferencesUtil.fetchLocation()
        requestUserAuth.currentLat = if (location.latitude == 0.0) "" else "${location.latitude}"
        requestUserAuth.currentLong = if (location.longitude == 0.0) "" else "${location.longitude}"
        requestUserAuth.currentLocation = sharedPreferencesUtil.fetchLocationName()
        requestUserAuth.email = binding!!.userEmailId.text.toString().trim()
        requestUserAuth.password = binding!!.userPassword.text.toString().trim()

        LogManager.logger.i(
            ArchitectureApp.instance!!.tag, "Auth Details::: ${Gson().toJson(requestUserAuth)}"
        )
        run {
            showProgressView(progressBar)
            mPresenter!!.addUserLoginWithEmailObject(requestUserAuth)
            mPresenter!!.callRetrofit(ConstantsApi.LOGIN_WITH_EMAIL)
        }
    }

    /* private fun fetchDeviceToken() {
         FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener { instanceIdResult ->
             LogManager.logger.i(
                 ArchitectureApp.instance!!.tag, "Device_Token_isss ::: ${instanceIdResult.token}"
             )
             sharedPreferencesUtil.saveDeviceToken(instanceIdResult.token)
             loginUser()
         }
     }*/
    private fun fetchDeviceToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                LogManager.logger.e(
                    ArchitectureApp.instance!!.tag,
                    "Fetching FCM device token failed: ${task.exception}"
                )
                return@addOnCompleteListener
            }

            // Get the FCM device token
            val token = task.result
            LogManager.logger.i(
                ArchitectureApp.instance!!.tag, "Device_Token_isss ::: $token"
            )

            // Save the device token to shared preferences
            sharedPreferencesUtil.saveDeviceToken(token)

            // Continue with the login process
            loginUser()
        }
    }


    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        signInLauncher.launch(signInIntent)
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            // Signed in successfully
            Log.e("GoogleSignIn", "signInResult:success, email: ${account?.email}")
            Log.e("GoogleSignIn", "signInResult:success, name: ${account?.displayName}")
            Log.e("GoogleSignIn", "signInResult:success, id: ${account?.id}")
            /// Toast.makeText(this@LoginWithEmailActivity, "Successfully", Toast.LENGTH_SHORT).show()
            val bundle = Bundle()
            bundle.putString(Constants.IntentDataKeys.User_Phone, "")
            bundle.putString(Constants.IntentDataKeys.Country_Code, "")
            bundle.putString(Constants.IntentDataKeys.User_email, account?.email)
            bundle.putBoolean(Constants.IntentDataKeys.Is_Verify, false)
             switchActivity(Constants.Intent.Create, true, bundle)
           /* val intent = Intent(this@LoginWithEmailActivity, CreateProfileActivityNew::class.java)
            startActivity(intent, bundle)
            finish()*/
        } catch (e: ApiException) {
            Log.w("GoogleSignIn", "signInResult:failed code=" + e.statusCode)
        }
    }


}