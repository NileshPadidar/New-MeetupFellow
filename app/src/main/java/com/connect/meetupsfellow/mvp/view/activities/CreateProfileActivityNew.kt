package com.connect.meetupsfellow.mvp.view.activities

import android.app.Activity
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.ClipData
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.provider.MediaStore
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import androidx.exifinterface.media.ExifInterface
import com.connect.meetupsfellow.R
import com.connect.meetupsfellow.application.ArchitectureApp
import com.connect.meetupsfellow.components.activity.CustomAppActivityCompatViewImpl
import com.connect.meetupsfellow.constants.Constants
import com.connect.meetupsfellow.constants.ConstantsApi
import com.connect.meetupsfellow.databinding.ActivityCreateProfileModelNewBinding
import com.connect.meetupsfellow.developement.interfaces.SharedPreferencesUtil
import com.connect.meetupsfellow.global.utils.AlphabetUnderscoreInputFilter
import com.connect.meetupsfellow.global.view.easyphotopicker.ConstantsImage
import com.connect.meetupsfellow.global.view.easyphotopicker.DefaultCallback
import com.connect.meetupsfellow.global.view.easyphotopicker.EasyImage
import com.connect.meetupsfellow.global.view.otp.OTPListener
import com.connect.meetupsfellow.global.view.otp.OtpTextView
import com.connect.meetupsfellow.log.LogManager
import com.connect.meetupsfellow.mvp.connector.activity.CreateProfileConnector
import com.connect.meetupsfellow.mvp.presenter.activity.CreateProfilePresenter
import com.connect.meetupsfellow.mvp.view.model.ProfileImageModel
import com.connect.meetupsfellow.retrofit.request.SignupRequest
import com.connect.meetupsfellow.retrofit.response.CommonResponse
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.gson.Gson
import com.hbb20.CountryCodePicker
//import id.zelory.compressor.Compressor
import pub.devrel.easypermissions.AfterPermissionGranted
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject


class CreateProfileActivityNew : CustomAppActivityCompatViewImpl() {
    private val firebaseCallback =
        object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {
                Log.e("Error:", "phoneAuthCredential: " + phoneAuthCredential)
                if (null == phoneAuthCredential) {
                    /// loginUser()
                    universalToast("Call_registerAPi_1")
                } else {
                    val code = phoneAuthCredential.smsCode
                    if (null != code) {
                        ///   otp_view.otp = code
                        phone_otp.otp = code
//                        hideKeyboard(etPhoneNumber)
//                        verificationCode = otp_view.otp
//                        setSelected(verify)
//                        authenticateOtp()
                    } else {
                        /// loginUser()
                        universalToast("Call_registerAPi_22")
                    }
                }
                hideProgressView(progressBar)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                e.printStackTrace()
                universalToast("${e.message}")
                Log.e("Error:", "Firebase_Fail: " + e.message)
                hideProgressView(progressBar)
            }

            override fun onCodeSent(
                verificationId: String, token: PhoneAuthProvider.ForceResendingToken
            ) {
                super.onCodeSent(verificationId, token)
                this@CreateProfileActivityNew.verificationId = verificationId
                this@CreateProfileActivityNew.resendingToken = token
                hideProgressView(progressBar)
                universalToast("Verification code sent successfully.")
                Log.e("Error:", "onCodeSend- Verify ")
                openBottomSheetVerify()
                phone_otp.otp = ""

            }
        }


    val myCalendar = Calendar.getInstance()
    val genders = arrayListOf<String>("Male", "Female", "Transgender", "Non-Binary")
    private val requestCreateUser = SignupRequest()
    lateinit var loginActivity: LoginActivity

    @Inject
    lateinit var sharedPreferencesUtil: SharedPreferencesUtil

    private var mPresenter: CreateProfileConnector.PresenterOps? = null
    private var photoFile: File? = null
    private val images = arrayListOf<ProfileImageModel>()
    private var selectedImage = -1
    private var is_verify = false
    private var user_email = ""
    private var from_activity = ""
    private var user_phone = ""
    lateinit var phoneNumber: TextInputEditText
    private var country_code = ""
    private var deviceToken = ""
    lateinit var countryCodePicker: CountryCodePicker
    private val cross = arrayListOf<ImageView>()
    private val profileImage = arrayListOf<ImageView>()

    private var mAuth: FirebaseAuth? = null
    private lateinit var phone_otp: OtpTextView
    private var verificationCode = ""
    private var verificationId = ""
    private var resendingToken: PhoneAuthProvider.ForceResendingToken? = null
    private var binding: ActivityCreateProfileModelNewBinding? = null
    private lateinit var progressBar: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateProfileModelNewBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        // setContentView(R.layout.activity_create_profile_model_new)
        context = this@CreateProfileActivityNew
        component.inject(this@CreateProfileActivityNew)
        progressBar = binding!!.includedLoading.rlProgress
        //updateLabel()
        getIntentData()
        startLoadingAnim()
        mAuth = FirebaseAuth.getInstance()
        deviceToken = sharedPreferencesUtil.fetchDeviceToken()
        Log.e("SignUp", "CreateProfileActivityNew")
        binding!!.dobPicker.setOnClickListener {

            openCalender()
        }
        countryCodePicker = findViewById(R.id.countryCodePicker)
        countryCodePicker.registerCarrierNumberEditText(binding!!.etUserPhoneNumber)

        val arrayAdapter = ArrayAdapter(this, R.layout.drop_down_items, genders)

        binding!!.selectGenderSel.setAdapter(arrayAdapter)

        EasyImage.configuration(this@CreateProfileActivityNew)
            .setImagesFolderName("MeetUpsFellow Images New")
            .setCopyTakenPhotosToPublicGalleryAppFolder(true)
            .setCopyPickedImagesToPublicGalleryAppFolder(true).setAllowMultiplePickInGallery(false)

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

        binding!!.userConfirmPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                if (!binding!!.userConfirmPassword.text.toString().trim().isEmpty()) {
                    binding!!.userConfirmPassword.background =
                        resources.getDrawable(R.drawable.rounded_corners_new)
                } else {
                    binding!!.userConfirmPassword.background =
                        resources.getDrawable(R.drawable.round_corners_default)
                }
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })

        binding!!.userEmailEtxt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                if (!binding!!.userEmailEtxt.text.toString().trim().isEmpty()) {
                    binding!!.userEmailEtxt.background =
                        resources.getDrawable(R.drawable.rounded_corners_new)
                } else {
                    binding!!.userEmailEtxt.background =
                        resources.getDrawable(R.drawable.round_corners_default)
                }
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })

        binding!!.etUserPhoneNumber.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                if (!binding!!.etUserPhoneNumber.text.toString().trim().isEmpty()) {
                    binding!!.userNumberField.background =
                        resources.getDrawable(R.drawable.rounded_corners_new)

                } else {
                    binding!!.userNumberField.background =
                        resources.getDrawable(R.drawable.round_corners_default)
                }
            }

            override fun afterTextChanged(s: Editable?) {

            }


        })

        binding!!.userHometxt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                if (!binding!!.userHometxt.text.toString().trim().isEmpty()) {
                    binding!!.userHometxt.background =
                        resources.getDrawable(R.drawable.rounded_corners_new)
                } else {
                    binding!!.userHometxt.background =
                        resources.getDrawable(R.drawable.round_corners_default)
                }
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })

        binding!!.userNameEtxt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                if (!binding!!.userNameEtxt.text.toString().trim().isEmpty()) {
                    binding!!.userNameEtxt.background =
                        resources.getDrawable(R.drawable.rounded_corners_new)
                    binding!!.userNameEtxt.filters =
                        arrayOf(AlphabetUnderscoreInputFilter(), InputFilter.LengthFilter(18))
                } else {
                    binding!!.userNameEtxt.background =
                        resources.getDrawable(R.drawable.round_corners_default)
                }
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })

        binding!!.firstNameEtxt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                if (!binding!!.firstNameEtxt.text.toString().trim().isEmpty()) {
                    binding!!.firstNameEtxt.background =
                        resources.getDrawable(R.drawable.rounded_corners_new)
                } else {
                    binding!!.firstNameEtxt.background =
                        resources.getDrawable(R.drawable.round_corners_default)
                }
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })

        binding!!.lastNameEtxt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                if (!binding!!.lastNameEtxt.text.toString().trim().isEmpty()) {
                    binding!!.lastNameEtxt.background =
                        resources.getDrawable(R.drawable.rounded_corners_new)
                } else {
                    binding!!.lastNameEtxt.background =
                        resources.getDrawable(R.drawable.round_corners_default)
                }
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })

        binding!!.dobPicker.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                if (!binding!!.dobPicker.text.toString().trim().isEmpty()) {
                    binding!!.dobPicker.background =
                        resources.getDrawable(R.drawable.rounded_corners_new)
                } else {
                    binding!!.dobPicker.background =
                        resources.getDrawable(R.drawable.round_corners_default)
                }
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })

        binding!!.selectGenderSel.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                if (!binding!!.selectGenderSel.text.toString().trim().isEmpty()) {
                    binding!!.selectGender.background =
                        resources.getDrawable(R.drawable.rounded_corners_new)
                } else {
                    binding!!.selectGender.background =
                        resources.getDrawable(R.drawable.round_corners_default)
                }
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })

        binding!!.userShortBiotxt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                if (!binding!!.userShortBiotxt.text.toString().trim().isEmpty()) {
                    binding!!.userShortBiotxt.background =
                        resources.getDrawable(R.drawable.rounded_corners_new)
                } else {
                    binding!!.userShortBiotxt.background =
                        resources.getDrawable(R.drawable.round_corners_default)
                }
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })

        binding!!.userShortBiotxt.setOnTouchListener(OnTouchListener { v, event ->
            if (v.id == R.id.userShortBiotxt) {
                v.parent.requestDisallowInterceptTouchEvent(true)
                when (event.action and MotionEvent.ACTION_MASK) {
                    MotionEvent.ACTION_UP -> v.parent.requestDisallowInterceptTouchEvent(false)
                }
            }
            false
        })

        binding!!.userHometxt.setOnClickListener {

            autoPlacePicker()
        }

        binding!!.tvAlreadAccount.setOnClickListener {

            //  switchActivity(Constants.Intent.LoginWithEmailActivity, true, Bundle())
            finish()
            val intent = Intent(this@CreateProfileActivityNew, LoginWithEmailActivity::class.java)
            startActivity(intent, null)
            finish()
        }


        //Log.d("userlat", location.latitude.toString())
        //Log.d("userLong", location.longitude.toString())

        binding!!.createNewProfile.setOnClickListener {

            if (validateData()) {

                if (requestCreateUser.age.toInt() < 18) {

                    Toast.makeText(
                        this, "Sorry you can't create profile", Toast.LENGTH_SHORT
                    ).show()
                } else {

                    if (is_verify) {
                        showProgressView(progressBar)
                        generateCreateProfileRequest()
                    } else {
                        showProgressView(progressBar)
                        generateCreateProfileRequest()/* when (validatePhoneNumber()) {
                                    true -> {
                                        authenticatePhoneNumber()
                                       /// openBottomSheetVerify()
                                       /// universalToast("open verify bottom_sheet")
                                    }false ->{
                                    Log.e("Error", "isValid: ${countryCodePicker.isValidFullNumber}")
                                    Log.e("Error", "Invalid_Num: ${countryCodePicker.fullNumberWithPlus}")
                                    universalToast("Invalid number : ${countryCodePicker.fullNumberWithPlus}")
                                    }
                                }*/
                    }


                    /*when {
                        requestCreateUser.homeLocation.trim()
                            .isEmpty() || requestCreateUser.homeLat.isEmpty() ||
                                requestCreateUser.homeLong.isEmpty() -> {
                            *//*showAlertHometown(
                                        getString(R.string.alert_no_hometown),
                                        "Alert"
                                    )*//*
                                    requestCreateUser.homeLocation = ""
                                    requestCreateUser.homeLat = ""
                                    requestCreateUser.homeLong = ""
                                    requestCreateUser.dob = dob_picker.text.toString().trim()
                                    showProgressView(progressBar)
                                    generateCreateProfileRequest()
                                }

                                else -> {
                                    showProgressView(progressBar)
                                    generateCreateProfileRequest()
                                }
                            }*/
                }

            } else {
                Log.e("validateData", "Fail")
            }
        }

        binding!!.circleImageView.setOnClickListener {
            selectedImage = 0
            showImagePopUp()
            Log.e("Click$%", "PhotoGallery")

        }

        binding!!.ivAddProfile.setOnClickListener {

            selectedImage = 0

            /* showAlertProfile(
                 getString(R.string.text_image_warning),
                 getString(R.string.textWarning)
             )*/

            //selectImagePopup()
            showImagePopUp()
            Log.e("Click$%", "PhotoGallery")

        }

        if (!user_phone.equals("null") && is_verify && user_phone != null) {
            binding!!.etUserPhoneNumber.setText(user_phone)
            binding!!.etUserPhoneNumber.isEnabled = false
            countryCodePicker.setDefaultCountryUsingNameCode(country_code)
            countryCodePicker.setCcpClickable(false)
            binding!!.userEmailEtxt.isClickable = true
            binding!!.llUserNumber.isClickable = false
            binding!!.confirmPasswordField.visibility = View.GONE
            binding!!.userEmailField.visibility = View.GONE
            binding!!.passwordField.visibility = View.GONE
            binding!!.userNumberField.visibility = View.VISIBLE

        } else if (!user_email.equals("null") && user_email != null) {
            binding!!.etUserPhoneNumber.isEnabled = true
            countryCodePicker.setDefaultCountryUsingNameCode("IN")
            countryCodePicker.setCcpClickable(true)
            binding!!.userEmailEtxt.isClickable = false
            binding!!.userEmailEtxt.isEnabled = false
            binding!!.userEmailEtxt.setText(user_email)
            binding!!.llUserNumber.isClickable = true
            binding!!.userEmailField.visibility = View.VISIBLE
            binding!!.confirmPasswordField.visibility = View.VISIBLE
            binding!!.passwordField.visibility = View.VISIBLE
            binding!!.userNumberField.visibility = View.GONE
        } else {
            binding!!.etUserPhoneNumber.isEnabled = true
            countryCodePicker.setDefaultCountryUsingNameCode("IN")
            countryCodePicker.setCcpClickable(true)
            binding!!.userEmailEtxt.isEnabled = true
            binding!!.llUserNumber.isEnabled = true
            binding!!.userEmailField.visibility = View.VISIBLE
            binding!!.confirmPasswordField.visibility = View.VISIBLE
            binding!!.passwordField.visibility = View.VISIBLE
            binding!!.userNumberField.visibility = View.GONE
        }
    }

    private fun getIntentData() {
        val bundle = intent.extras

        bundle?.let {
            user_phone = it.getString(Constants.IntentDataKeys.User_Phone).toString()
            country_code = it.getString(Constants.IntentDataKeys.Country_Code).toString()
            user_email = it.getString(Constants.IntentDataKeys.User_email).toString()
            is_verify = it.getBoolean(Constants.IntentDataKeys.Is_Verify, false)
        }

        if (!user_phone.equals("null") && !user_phone.isNullOrEmpty()) {
            from_activity = Constants.Intent.Login
            Log.e("IntentData1&", "Intent.Login")
        } else {
            from_activity = Constants.Intent.LoginWithEmailActivity
            Log.e("IntentData2&", "else: Intent.LoginWithEmailActivity")
        }
        Log.e("IntentData&", "user_phone: $user_phone")
        Log.e("IntentData&", "country_code: $country_code")
        Log.e("IntentData&", "user_email: $user_email")
        Log.e("IntentData&", "is_verify: $is_verify")
    }

    private fun openBottomSheetVerify() {
        // on below line we are creating a new bottom sheet dialog.
        val dialog = BottomSheetDialog(this, R.style.BottomSheetStyle)

        // on below line we are inflating a layout file which we have created.
        val view = layoutInflater.inflate(R.layout.bottom_sheet_verify_otp, null)
        val btnClose = view.findViewById<ImageView>(R.id.close)
        val btnVerifyOtp = view.findViewById<AppCompatButton>(R.id.btn_otp_verify)
        phone_otp = view.findViewById<OtpTextView>(R.id.phone_otp_view)
        val resendOtp = view.findViewById<TextView>(R.id.tvResendOtp)

        dialog.setCancelable(false)

        btnClose.setOnClickListener {
            dialog.setCancelable(true)
            dialog.dismiss()
        }

        resendOtp.setOnClickListener {
            universalToast("resend Otp")
            authenticatePhoneNumber()
        }
        btnVerifyOtp.setOnClickListener {
            when (validateOtp()) {
                true -> {
                    authenticateOtp()
                }

                false -> TODO()
            }
            dialog.setCancelable(true)
            dialog.dismiss()
        }
        phone_otp.otpListener = object : OTPListener {
            override fun onInteractionListener() {

            }

            override fun onOTPComplete(otp: String) {
                verificationCode = otp
                /// authenticateOtp()
            }
        }

        dialog.setContentView(view)

        /* dialog.setOnShowListener {
             val bottomSheet = dialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
             bottomSheet?.let {
                 val behavior = BottomSheetBehavior.from(it)
                 val layoutParams = it.layoutParams
                 layoutParams.height = scroll.height + 80  // Set your desired height here
                 it.layoutParams = layoutParams
                 behavior.state = BottomSheetBehavior.STATE_EXPANDED
                 behavior.skipCollapsed = true
             }
         }*/
        dialog.show()
    }

    private fun validatePhoneNumber(): Boolean {
        return countryCodePicker.isValidFullNumber
    }

    private fun authenticatePhoneNumber() {
        showProgressView(progressBar)
        Log.e("Error:", "authenticatePhoneNumber: " + countryCodePicker.fullNumberWithPlus)
        val options = PhoneAuthOptions.newBuilder(mAuth!!)
            .setPhoneNumber(countryCodePicker.fullNumberWithPlus) // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(this) // Activity (for callback binding)
            .setCallbacks(firebaseCallback) // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun validateOtp(): Boolean {
        return when {
            (phone_otp.otp.isEmpty()) -> {
                universalToast("Please enter verification code")
                false
            }

            (phone_otp.otp.length < 6) -> {
                universalToast("Entered verification code incorrect")
                false
            }

            else -> true
        }
    }

    private fun authenticateOtp() {
        try {
            showProgressView(progressBar)
            val credential = PhoneAuthProvider.getCredential(verificationId, verificationCode)

            signInWithPhoneAuthCredential(credential)
        } catch (e: Exception) {
            hideProgressView(progressBar)
            universalToast("Verification Code is wrong, try again")
            Log.e("Error:", "authenticateOtp: ${e.message}")
        }

    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential?) {
        if (null == mAuth) {
            mAuth = FirebaseAuth.getInstance()
        }
        mAuth!!.signInWithCredential(credential!!).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                getSharedPreferences("userPhone", MODE_PRIVATE).edit()
                    .putString("userPhone", binding!!.etUserPhoneNumber.text.toString()).apply()
                ///    loginUser()
                Log.e("verify*", "Code verify successfully")
                hideProgressView(progressBar)
                showProgressView(progressBar)
                generateCreateProfileRequest()
            } else {
                hideProgressView(progressBar)
                universalToast("You have entered invalid verification code")
            }
        }
    }

    private fun startLoadingAnim() {

        val animation = android.view.animation.AnimationUtils.loadAnimation(
            applicationContext, R.anim.blink_anim
        )

        binding!!.includedLoading.animLogo.startAnimation(animation)
    }

    private fun showImagePopUp() {

        val dialog = Dialog(this@CreateProfileActivityNew)

        dialog.setCancelable(false)

        //val activity = context as AppCompatActivity

        val view = layoutInflater.inflate(R.layout.custom_media_chooser, null)

        dialog.setContentView(view)

        dialog.window!!.attributes.windowAnimations = R.style.DialogAnimation

        if (dialog.window != null) {
            dialog.window!!.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
        dialog.window!!.setGravity(Gravity.CENTER)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val takePhoto = view.findViewById<LinearLayout>(R.id.takePhoto)
        val chooseGallery = view.findViewById<LinearLayout>(R.id.chooseGallery)
        val cancelBtn = view.findViewById<Button>(R.id.cancel_btn)

        takePhoto.setOnClickListener {
            ///  this@CreateProfileActivityNew.onCameraTapped()
            if (!hasCameraPermission()) {
                Log.e("Click$%", "hasCameraPermission")
                requestCameraPermission()
            } else {
                Log.e("Click$%", "hasCameraPermission__Grant")
                capturePhoto()
            }
            dialog.cancel()
        }

        chooseGallery.setOnClickListener {
            //  this@CreateProfileActivityNew.onGalleryTapped()
            if (!hasGalleryPermission()) {
                Log.e("Click$%", "hasGalleryPermission")
                requestGalleryPermission()
            } else {
                Log.e("Click$%", "hasGalleryPermission__Grant")
                EasyImage.openGallery(
                    this@CreateProfileActivityNew,
                    //ConstantsImage.RequestCodes.PICK_PICTURE_FROM_GALLERY
                    Constants.ImagePicker.Image
                )
            }
            dialog.cancel()
        }

        cancelBtn.setOnClickListener {

            dialog.cancel()
        }

        dialog.show()


    }

    override fun onBackPressed() {

        switchActivityOnly(from_activity, true)

    }

    private fun showAlertHometown(message: String, title: String) {
        val alertDialog = AlertDialog.Builder(
            this@CreateProfileActivityNew, R.style.MyDialogTheme2
        )
        alertDialog.setTitle(title)
        alertDialog.setMessage(message)
        alertDialog.setPositiveButton(getString(R.string.btn_text_add)) { dialog, _ ->
            dialog.dismiss()
            autoPlacePicker()
        }
        alertDialog.setNegativeButton(getString(R.string.btn_text_ignore)) { dialog, _ ->
            dialog.dismiss()/*  requestCreateUser.homeLocation = ""
              requestCreateUser.homeLat = ""
              requestCreateUser.homeLong = ""*/
            showProgressView(progressBar)
            generateCreateProfileRequest()
        }
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    private fun selectImagePopup() {
        val options = arrayOf<CharSequence>("Take Photo", "Choose from Gallery", "Cancel")
        val builder: android.app.AlertDialog.Builder = android.app.AlertDialog.Builder(context)
        builder.setTitle("Add Photo!")
        builder.setItems(options, DialogInterface.OnClickListener { dialog, item ->
            if (options[item] == "Take Photo") {

                this@CreateProfileActivityNew.onCameraTapped()

            } else if (options[item] == "Choose from Gallery") {

                this@CreateProfileActivityNew.onGalleryTapped()

            } else if (options[item] == "Cancel") {

                dialog.dismiss()
            }
        })
        builder.show()
    }

    private fun showAlertProfile(message: String, title: String) {
        val alertDialog = AlertDialog.Builder(
            this@CreateProfileActivityNew, R.style.MyDialogTheme2
        )
        alertDialog.setTitle(title)
        alertDialog.setMessage(message)
        alertDialog.setPositiveButton(getString(R.string.text_okay)) { dialog, _ ->
            dialog.dismiss()
            selectImagePopup()
        }
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    private fun generateCreateProfileRequest() {

        val currentLocation = sharedPreferencesUtil.fetchLocation()

        images.asSequence().forEach {
            when (val index = images.indexOf(it)) {
                0, 1, 2, 3, 4 -> requestCreateUser.apply {
                    image1 = images[index].image!!
                    id1 = 0
                }
            }
        }

        requestCreateUser.name = binding!!.userNameEtxt.text.toString().trim()
        requestCreateUser.dob = binding!!.dobPicker.text.toString().trim()


        //  requestCreateUser.countryCode = userAgeEtxt.text.toString()
        requestCreateUser.phoneNumber = binding!!.etUserPhoneNumber.text.toString().trim()
        requestCreateUser.password = binding!!.userConfirmPassword.text.toString().trim()
        requestCreateUser.email = binding!!.userEmailEtxt.text.toString().trim()

        Log.d("userLat", "${currentLocation.latitude}")
        Log.d("userLong", "${currentLocation.longitude}")

        //   requestCreateUser.homeLat = "${currentLocation.latitude}"
        //  requestCreateUser.homeLong = "${currentLocation.longitude}"
        requestCreateUser.currentLat = "${currentLocation.latitude}"
        requestCreateUser.currentLong = "${currentLocation.longitude}"
        requestCreateUser.aboutMe = binding!!.userShortBiotxt.text.toString().trim()
        requestCreateUser.isoCode = countryCodePicker.selectedCountryNameCode
        requestCreateUser.countryCode = "+" + countryCodePicker.selectedCountryCode
        requestCreateUser.deviceToken = deviceToken
        requestCreateUser.deviceType = "android"
        requestCreateUser.gender = binding!!.selectGenderSel.text.toString().trim()
        if (binding!!.userNumberField.isVisible) {
            requestCreateUser.signup_type = "phone"
            /// requestCreateUser.phoneNumber = etUserPhoneNumber.text.toString().trim()
        } else {
            requestCreateUser.signup_type = "email"
            ///requestCreateUser.email = userEmailEtxt.text.toString().trim()
        }

        if (requestCreateUser.countryCode.isEmpty()) {
            requestCreateUser.isoCode = "IN"
            requestCreateUser.countryCode = "+91"
        }

        LogManager.logger.e(
            ArchitectureApp.instance!!.tag, "SignU_Request is : ${Gson().toJson(requestCreateUser)}"
        )
        Handler().postDelayed({
            createProfile()
        }, 200)
    }

    private fun autoPlacePicker() {
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
        val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
            .setTypeFilter(TypeFilter.CITIES).build(this)
        startActivityForResult(intent, Constants.RequestCode.AUTOCOMPLETE_REQUEST_CODE)
    }

    private val presenter = object : CreateProfileConnector.RequiredViewOps {
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

        override fun showResponse(response: CommonResponse, type: ConstantsApi) {
            when (type) {
                ConstantsApi.SIGN_UP -> {
                    hideProgressView(progressBar)
                    Log.e("Req_SignUp", "Api_res-: ${response.message}")
                    Log.e("Req_SignUp", "Api_res-: ${response.userInfo.name}")
                    Log.e("Req_SignUp", "profileStatus-: ${response.userInfo.profileStatus}")

                    if (response.is_verify == 0) {
                        showAlertForStatusCreate(
                            response.message, "Check Your Email"
                        )
                    } else {
                        profile()
                    }/*  if (response.userInfo.profileStatus.equals("approved")){
                          hideProgressView(progressBar)
                          profile()
                      }else{
                          hideProgressView(progressBar)
                          showAlertForStatusCreate(
                          response.message,
                          response.userInfo.profileStatus.toUpperCase(Locale.getDefault())
                      )
                      }*/
                }

                ConstantsApi.FETCH_PROFILE -> {
                    //showSuccessPopUp()
                    hideProgressView(progressBar)
                    val bundle = Bundle()
                    bundle.putString("userName", binding!!.userNameEtxt.text.toString())
                    bundle.putBoolean("newUser", true)
                    ///  switchActivity(Constants.Intent.Home, true, bundle)
                    val intent = Intent(this@CreateProfileActivityNew, HomeActivity::class.java)
                    startActivity(intent, null)
                    finish()
                }

                else -> {}
            }

            hideProgressView(progressBar)
        }

    }

    private fun showSuccessPopUp() {

        val dialog = Dialog(this@CreateProfileActivityNew)

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

        congratUserTxt.text = "Congratulations ${binding!!.userNameEtxt.text.toString()}"

        keepIt.setOnClickListener {

            dialog.dismiss()
            profile()
            //switchActivity(Constants.Intent.Home, true, Bundle())
        }

        goPro.setOnClickListener {
            /// New comment 16_12
            /* AlertBuyPremium.Builder(this@CreateProfileActivityNew, Constants.DialogTheme.NoTitleBar)
                 .build().show()*/
        }

        dialog.show()
    }

    private fun showAlertForStatusCreate(message: String, title: String) {
        val alertDialog = AlertDialog.Builder(
            this@CreateProfileActivityNew, R.style.MyDialogTheme2
        )
        alertDialog.setTitle(title)
        alertDialog.setMessage(message)
        alertDialog.setPositiveButton(getString(R.string.text_okay)) { dialog, _ ->
            dialog.dismiss()
            switchActivityOnly(from_activity, true)
            //profile()
            //showSuccessPopUp()
        }
        alertDialog.setCancelable(false)

        alertDialog.show()
    }

    private fun profile() {
        showProgressView(progressBar)
        if (null == mPresenter) mPresenter = CreateProfilePresenter(presenter)

        run {
            mPresenter!!.callRetrofit(ConstantsApi.FETCH_PROFILE)
        }
    }

    private fun createProfile() {
        if (null == mPresenter) mPresenter = CreateProfilePresenter(presenter)

        run {
            mPresenter!!.addSignUpUserObject(requestCreateUser)
            mPresenter!!.callRetrofit(ConstantsApi.SIGN_UP)
        }
    }

    private fun validateData(): Boolean {

        /*if (firstNameEtxt.text!!.isEmpty()) {

            firstNameEtxt.requestFocus()

            return false
        } else if (lastNameEtxt.text.isEmpty()) {

            lastNameEtxt.requestFocus()

            return false
        }*/
        if (binding!!.userNameEtxt.text!!.isEmpty()) {

            binding!!.userNameEtxt.requestFocus()
            Toast.makeText(
                this@CreateProfileActivityNew, "User Name cannot to empty", Toast.LENGTH_SHORT
            ).show()

            return false
        } else if (binding!!.dobPicker.text!!.isEmpty()) {

            binding!!.dobPicker.requestFocus()
            Toast.makeText(
                this@CreateProfileActivityNew, "Date of birth cannot be empty", Toast.LENGTH_SHORT
            ).show()

            return false
        } else if (binding!!.selectGenderSel.text.toString().isEmpty()) {
            Toast.makeText(
                this@CreateProfileActivityNew, "please select gender", Toast.LENGTH_SHORT
            ).show()

            return false
        } else if (binding!!.userNumberField.isVisible && binding!!.etUserPhoneNumber.text!!.isEmpty()) {
            binding!!.etUserPhoneNumber.requestFocus()
            Toast.makeText(
                this@CreateProfileActivityNew, "Phone number cannot be empty.", Toast.LENGTH_SHORT
            ).show()

            return false
        } else if (binding!!.userEmailField.isVisible && binding!!.userEmailEtxt.text!!.isEmpty()) {

            binding!!.userEmailEtxt.requestFocus()

            Toast.makeText(
                this@CreateProfileActivityNew, "Email cannot be empty.", Toast.LENGTH_SHORT
            ).show()

            return false
        } else if (binding!!.userEmailField.isVisible && !isValidEmail(binding!!.userEmailEtxt.text)) {

            binding!!.userEmailEtxt.requestFocus()

            Toast.makeText(this@CreateProfileActivityNew, "Enter valid email.", Toast.LENGTH_SHORT)
                .show()

            return false
        } else if (binding!!.confirmPasswordField.isVisible && binding!!.userPassword.text!!.isEmpty()) {
            binding!!.userPassword.requestFocus()
            Toast.makeText(
                this@CreateProfileActivityNew, "Password cannot be empty", Toast.LENGTH_SHORT
            ).show()

            return false
        } else if (binding!!.confirmPasswordField.isVisible && binding!!.userPassword.text.toString()
                .trim().length < 4
        ) {
            binding!!.userPassword.requestFocus()
            Toast.makeText(
                this@CreateProfileActivityNew,
                "Password should grater than equal to 4 character.",
                Toast.LENGTH_SHORT
            ).show()
            return false
        } else if (binding!!.confirmPasswordField.isVisible && !isValidPassword(
                binding!!.userPassword.text.toString().trim()
            )
        ) {
            binding!!.userPassword.requestFocus()
            Toast.makeText(
                this@CreateProfileActivityNew,
                "Password Contains at least one alphabet, one number, and one special character.",
                Toast.LENGTH_SHORT
            ).show()
            return false
        } else if (binding!!.confirmPasswordField.isVisible && binding!!.userConfirmPassword.text!!.isEmpty()) {
            binding!!.userConfirmPassword.requestFocus()
            Toast.makeText(
                this@CreateProfileActivityNew,
                "Confirm Password cannot be empty",
                Toast.LENGTH_SHORT
            ).show()
            return false
        } else if (binding!!.confirmPasswordField.isVisible && binding!!.userConfirmPassword.text.toString() != binding!!.userPassword.text.toString()) {
            binding!!.userConfirmPassword.requestFocus()
            Toast.makeText(
                this@CreateProfileActivityNew, "Please enter same password", Toast.LENGTH_SHORT
            ).show()
            return false
        } else if (binding!!.userShortBiotxt.text!!.trim().isEmpty()) {

            binding!!.userShortBiotxt.requestFocus()
            Toast.makeText(this@CreateProfileActivityNew, "Bio cannot be empty", Toast.LENGTH_SHORT)
                .show()
            return false
        } else if (binding!!.userShortBiotxt.text!!.trim().length < 25) {

            binding!!.userShortBiotxt.requestFocus()
            Toast.makeText(
                this@CreateProfileActivityNew,
                "Bio must be greater than 25 words",
                Toast.LENGTH_SHORT
            ).show()
            return false
        } else if (images.isEmpty() || images.size == 0) {
            Toast.makeText(
                this@CreateProfileActivityNew, "Please add profile pic", Toast.LENGTH_SHORT
            ).show()
            return false
        }/*else if(userHometxt.text!!.isEmpty()){

            userHometxt.requestFocus()

            return false
        } */
        else {

            return true
        }
    }

    private fun isValidEmail(email: CharSequence?): Boolean {
        return !email.isNullOrEmpty() && android.util.Patterns.EMAIL_ADDRESS.matcher(email)
            .matches()
    }

    fun isValidPassword(password: String): Boolean {
        // Regular expression to check if the password contains at least one alphabet, one number, and one special character
        val passwordPattern = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@\$!%*?&])[A-Za-z\\d@\$!%*?&]{4,12}$"
        val passwordMatcher = Regex(passwordPattern)
        return passwordMatcher.matches(password)
    }

    private fun openCalender() {
        val maxDateCalendar = Calendar.getInstance()
        maxDateCalendar.add(Calendar.YEAR, -18)
        val maxDateInMillis = maxDateCalendar.timeInMillis

        val date: DatePickerDialog.OnDateSetListener = object : DatePickerDialog.OnDateSetListener {
            override fun onDateSet(view: DatePicker?, year: Int, month: Int, day: Int) {
                myCalendar.set(Calendar.YEAR, year)
                myCalendar.set(Calendar.MONTH, month)
                myCalendar.set(Calendar.DAY_OF_MONTH, day)
                updateLabel()
            }
        }

        val datePickerDialog = DatePickerDialog(
            this,
            R.style.DatePickerDialogTheme,
            date,
            myCalendar[Calendar.YEAR],
            myCalendar[Calendar.MONTH],
            myCalendar[Calendar.DAY_OF_MONTH]
        )

        // Set the maximum date to today's date
        datePickerDialog.datePicker.maxDate = System.currentTimeMillis()
        datePickerDialog.datePicker.maxDate = maxDateInMillis
        datePickerDialog.show()

    }

    private fun updateLabel() {

        /// val myFormat = "dd-MM-yyyy"
        val myFormat = "yyyy-MM-dd"
        val dateFormat = SimpleDateFormat(myFormat, Locale.US)
        binding!!.dobPicker.setText(dateFormat.format(myCalendar.time))

        val todayCal = Calendar.getInstance()

        Log.d("year", todayCal.get(Calendar.YEAR).toString())
        Log.d("yearC", myCalendar.get(Calendar.YEAR).toString())

        val currentYear = todayCal.get(Calendar.YEAR)
        val selectedYear = myCalendar.get(Calendar.YEAR)

        val age = currentYear - selectedYear

        binding!!.userAgeTxt.text = "You are $age year's old"
        binding!!.userAgeTxt.visibility = View.VISIBLE

        requestCreateUser.age = age.toString()
    }


    @AfterPermissionGranted(Constants.PermissionsCode.REQUEST_PERMISSION_CAMERA)
    fun onCameraTapped() =
        when (cameraPermission(Constants.PermissionsCode.REQUEST_PERMISSION_CAMERA)) {
            true -> {
                when (isDeviceSupportCamera()) {
                    true -> {
                        capturePhoto()
                    }

                    false -> {
                        universalToast(getString(R.string.text_no_camera))
                    }
                }
            }

            false -> {
                Log.e("Click$%", "isDeviceSupportCamera--False")
            }
        }


    @AfterPermissionGranted(Constants.PermissionsCode.REQUEST_PERMISSION_GALLERY)
    fun onGalleryTapped() =
        when (cameraPermission(Constants.PermissionsCode.REQUEST_PERMISSION_GALLERY)) {
            true -> {
                when (checkGalleryAppAvailability()) {
                    true -> {
                        EasyImage.openGallery(this@CreateProfileActivityNew, 0)
                    }

                    false -> {
                        EasyImage.openPhoto(this@CreateProfileActivityNew, 0)
                        //universalToast(getString(R.string.text_no_camera))
                    }
                }
            }

            false -> {
                Log.e("Click$%", "checkGalleryAppAvailability--False")
            }
        }


    @Suppress("DEPRECATION")
    @Throws(IOException::class)
    private fun createImageFileWith(): File {
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val imageFileName = "JPEG_$timestamp"
        val storageDir = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "pics"
        )
        storageDir.mkdirs()
        return File.createTempFile(imageFileName, ".jpg", storageDir)
    }

    private fun capturePhoto() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            var photoURI: Uri? = null
            try {
                photoFile = createImageFileWith()
                photoURI = FileProvider.getUriForFile(
                    this@CreateProfileActivityNew,
                    getString(R.string.file_provider_authority),
                    photoFile!!
                )

            } catch (ex: IOException) {
                LogManager.logger.e("TakePicture", ex.message!!)
            }

            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
                takePictureIntent.clipData = ClipData.newRawUri("", photoURI)
                takePictureIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            }
            startActivityForResult(takePictureIntent, Constants.ImagePicker.CAMERA)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (resultCode) {
            Activity.RESULT_OK -> {
                when (requestCode) {
                    Constants.ImagePicker.CAMERA -> {

                        var exif: ExifInterface? = null
                        try {
                            exif = ExifInterface(photoFile!!.path)
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }

                        val orientation = exif!!.getAttributeInt(
                            ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED
                        )
                        val bmOptions = BitmapFactory.Options()
                        val imageBitmap = BitmapFactory.decodeFile(photoFile!!.path, bmOptions)
                        val rotatedBitmap = rotateBitmap(imageBitmap, orientation)!!

                        val os = BufferedOutputStream(FileOutputStream(photoFile!!))
                        rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 50, os)
                        os.close()

                        /*  photoFile = Compressor(this@CreateProfileActivityNew).setMaxWidth(1280)
                              .setMaxHeight(720).setQuality(50)
                              .setCompressFormat(Bitmap.CompressFormat.JPEG).compressToFile(photoFile)
  */
                        setImage(photoFile!!)
                    }

                    Constants.RequestCode.AUTOCOMPLETE_REQUEST_CODE -> {
                        val place = Autocomplete.getPlaceFromIntent(data!!)
                        LogManager.logger.i(
                            ArchitectureApp.instance!!.tag,
                            "Place: " + place.name + ", " + place.id + ", Address : " + place.address
                        )/* requestCreateUser.homeLocation = "${place.address}"
                         requestCreateUser.homeLat = "${place.latLng!!.latitude}"
                         requestCreateUser.homeLong = "${place.latLng!!.longitude}"
                         userHometxt.setText(requestCreateUser.homeLocation)*/
                        binding!!.userHometxt.error = null
                    }

                    ConstantsImage.RequestCodes.PICK_PICTURE_FROM_GALLERY -> {
                        imageFromGallery(requestCode, resultCode, data)
                    }
                }
            }

            AutocompleteActivity.RESULT_ERROR -> {
                val status = Autocomplete.getStatusFromIntent(data!!)
                LogManager.logger.i(ArchitectureApp.instance!!.tag, "${status.statusMessage}")
                universalToast("${status.statusMessage}")
            }

        }
    }

    private fun rotateBitmap(bitmap: Bitmap, orientation: Int): Bitmap? {
        val matrix = Matrix()
        when (orientation) {
            ExifInterface.ORIENTATION_NORMAL -> {
                return bitmap
            }

            ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> {
                matrix.setScale((-1).toFloat(), 1F)
            }

            ExifInterface.ORIENTATION_ROTATE_180 -> {
                matrix.setRotate(180F)
            }

            ExifInterface.ORIENTATION_FLIP_VERTICAL -> {
                matrix.setRotate(180F)
                matrix.postScale((-1).toFloat(), 1F)
            }

            ExifInterface.ORIENTATION_TRANSPOSE -> {
                matrix.setRotate(90F)
                matrix.postScale((-1).toFloat(), 1F)
            }

            ExifInterface.ORIENTATION_ROTATE_90 -> {
                matrix.setRotate(90F)
            }

            ExifInterface.ORIENTATION_TRANSVERSE -> {
                matrix.setRotate((-90).toFloat())
                matrix.postScale((-1).toFloat(), 1F)
            }

            ExifInterface.ORIENTATION_ROTATE_270 -> {
                matrix.setRotate((-90).toFloat())
            }

            else -> {
                return bitmap
            }
        }

        return try {
            val bmRotated =
                Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
            bitmap.recycle()
            bmRotated
        } catch (e: OutOfMemoryError) {
            e.printStackTrace()
            null
        }
    }

    private fun imageFromGallery(requestCode: Int, resultCode: Int, data: Intent?) {
        EasyImage.handleActivityResult(
            requestCode,
            resultCode,
            data,
            this,
            object : DefaultCallback() {
                override fun onImagesPicked(
                    imageFiles: MutableList<File>, source: EasyImage.ImageSource?, type: Int
                ) {
                    var image = imageFiles[0]
                    var exif: ExifInterface? = null
                    try {
                        exif = ExifInterface(image.path)
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }

                    val orientation = exif!!.getAttributeInt(
                        ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED
                    )
                    val bmOptions = BitmapFactory.Options()
                    val imageBitmap = BitmapFactory.decodeFile(image.path, bmOptions)
                    val rotatedBitmap = rotateBitmap(imageBitmap, orientation)!!

                    val os = BufferedOutputStream(FileOutputStream(image))
                    rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 50, os)
                    os.close()

                    /*   image = Compressor(this@CreateProfileActivityNew).setMaxWidth(1280)
                           .setMaxHeight(720).setQuality(50)
                           .setCompressFormat(Bitmap.CompressFormat.JPEG).compressToFile(image)*/

                    setImage(image)
                }

                override fun onCanceled(source: EasyImage.ImageSource, type: Int) {
                    if (source == EasyImage.ImageSource.CAMERA_IMAGE) {
                        EasyImage.lastlyTakenButCanceledPhoto(this@CreateProfileActivityNew)
                            ?.delete()
                    }
                }

                override fun onImagePickerError(
                    e: Exception, source: EasyImage.ImageSource, type: Int
                ) {
                    e.printStackTrace()
                }
            })
    }

    private fun setImage(image: File) {
        when (images.size == 5) {
            true -> {
                images[selectedImage].image = image
            }

            false -> {
                images.add(ProfileImageModel().apply {
                    this.image = image
                })
            }
        }
        setCrossImages()
    }

    private fun setCrossImages() {/*cross.asSequence().forEach {
            it.visibility = View.GONE
        }

        profileImage.asSequence().forEach {
            it.setImageResource(R.drawable.ic_image_upload)
        }
*/
        when (images.isNotEmpty()) {
            true -> {
                images.asSequence().forEach {
                    //val index = images.indexOf(it)
                    //cross[index].visibility = View.VISIBLE
                    binding!!.circleImageView.setImageURI(Uri.fromFile(it.image))
                }
            }

            false -> {}
        }
    }
}