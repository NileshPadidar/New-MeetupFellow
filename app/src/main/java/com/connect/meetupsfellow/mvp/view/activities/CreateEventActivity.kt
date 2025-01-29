package com.connect.meetupsfellow.mvp.view.activities

import android.app.Activity
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.util.Patterns
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.webkit.URLUtil
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.FileProvider
import com.connect.meetupsfellow.R
import com.connect.meetupsfellow.application.ArchitectureApp
import com.connect.meetupsfellow.components.activity.CustomAppActivityCompatViewImpl
import com.connect.meetupsfellow.constants.Constants
import com.connect.meetupsfellow.constants.ConstantsApi
import com.connect.meetupsfellow.databinding.ActivityCreateEventBinding
import com.connect.meetupsfellow.developement.interfaces.ApiConnect
import com.connect.meetupsfellow.developement.interfaces.SharedPreferencesUtil
import com.connect.meetupsfellow.global.utils.ToolbarUtils
import com.connect.meetupsfellow.global.utils.compressImage
import com.connect.meetupsfellow.global.view.easyphotopicker.ConstantsImage
import com.connect.meetupsfellow.global.view.easyphotopicker.DefaultCallback
import com.connect.meetupsfellow.global.view.easyphotopicker.EasyImage
import com.connect.meetupsfellow.log.LogManager
import com.connect.meetupsfellow.mvp.connector.activity.CreateEventConnector
import com.connect.meetupsfellow.mvp.presenter.activity.CreateEventPresenter
import com.connect.meetupsfellow.retrofit.request.RequestCreateEvent
import com.connect.meetupsfellow.retrofit.response.CommonResponse
import com.connect.meetupsfellow.retrofit.response.ResponseUserData
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.gson.Gson
import pub.devrel.easypermissions.AfterPermissionGranted
import retrofit2.Response
import rx.Observer
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject


class CreateEventActivity : CustomAppActivityCompatViewImpl() {

    @Inject
    lateinit var sharedPreferencesUtil: SharedPreferencesUtil

    @Inject
    lateinit var apiConnect: ApiConnect

    private val presenter = object : CreateEventConnector.RequiredViewOps {
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
            showAlertEvent(response.message, "Pending")
            hideProgressView(progressBar)
        }

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

            }
        }

    @AfterPermissionGranted(Constants.PermissionsCode.REQUEST_PERMISSION_GALLERY)
    fun onGalleryTapped() =
        when (cameraPermission(Constants.PermissionsCode.REQUEST_PERMISSION_GALLERY)) {
            true -> {
                when (checkGalleryAppAvailability()) {
                    true -> {
                        EasyImage.openGallery(this@CreateEventActivity, 0)
                    }

                    false -> {

                        EasyImage.openPhoto(this@CreateEventActivity, 0)

                        // val photoPickerIntent = Intent(
                        //     Intent.ACTION_PICK,
                        //      MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                        //   )
                        //  photoPickerIntent.type = "image/*"
                        //   photoPickerIntent.putExtra("crop", "true")
                        //  photoPickerIntent.putExtra("scale", true)
                        //   photoPickerIntent.putExtra("outputX", 256)
                        //    photoPickerIntent.putExtra("outputY", 256)
                        //   photoPickerIntent.putExtra("aspectX", 1)
                        //   photoPickerIntent.putExtra("aspectY", 1)
                        //   photoPickerIntent.putExtra(
                        //       "outputFormat",
                        //       Bitmap.CompressFormat.JPEG.toString()
                        //    )
                        //   photoPickerIntent.putExtra(MediaStore.EXTRA_OUTPUT, "")
                        //   startActivityForResult(photoPickerIntent, 1)

                        //universalToast(getString(R.string.text_no_camera))
                    }
                }

            }

            false -> {

            }
        }

    private var photoFile: File? = null

    private val requestCreateEvent = RequestCreateEvent()

    private var mPresenter: CreateEventConnector.PresenterOps? = null
    private var binding: ActivityCreateEventBinding? = null
    private lateinit var progressBar: LinearLayout
    private var selectedStartTime: Pair<Int, Int>? = null // To store the selected start time

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateEventBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        // setContentView(R.layout.activity_create_event)
        //  this@CreateEventActivity
        component.inject(this)
        ToolbarUtils.setupActionBar(
            this,
            binding!!.header.toolbar,
            getString(R.string.title_create_event),
            binding!!.header.tvTitle,
            showBackArrow = true,
            activity = this
        )
        progressBar = binding!!.includedLoading.rlProgress
        startLoadingAnim()
      // setupActionBar(getString(R.string.title_create_event), true)
        init()
        callForUserProfile()
    }

    private fun startLoadingAnim() {

        val animation = android.view.animation.AnimationUtils.loadAnimation(
            applicationContext, R.anim.blink_anim
        )

        binding!!.includedLoading.animLogo.startAnimation(animation)
    }

   /* private fun showTimePicker(textView: TextView) {
        val calendar = Calendar.getInstance()
        val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
        val currentMinute = calendar.get(Calendar.MINUTE)

        // Create and show the TimePickerDialog with a custom theme
        val timePickerDialog = TimePickerDialog(
            this, R.style.DatePickerDialogTheme, // Apply custom theme
            { _, hourOfDay, minute ->
                // Format the time to 12-hour format with AM/PM
                val isPM = hourOfDay >= 12
                val formattedHour = if (hourOfDay % 12 == 0) 12 else hourOfDay % 12
                val amPm = if (isPM) "PM" else "AM"
                val selectedTime = String.format("%02d:%02d %s", formattedHour, minute, amPm)

                // Show the selected time
                /// Toast.makeText(this, "Selected Time: $selectedTime", Toast.LENGTH_SHORT).show()
                textView.text = selectedTime
            }, currentHour, currentMinute, false // Set to false for 12-hour format
        )

        timePickerDialog.show()
    }*/

    private fun showTimePicker(
        textView: TextView,
        isStartTime: Boolean,
        startTime: Pair<Int, Int>? = null // Pass start time for validation
    ) {
        val calendar = Calendar.getInstance()
        val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
        val currentMinute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(
            this, R.style.DatePickerDialogTheme, // Apply custom theme
            { _, hourOfDay, minute ->
                val isPM = hourOfDay >= 12
                val formattedHour = if (hourOfDay % 12 == 0) 12 else hourOfDay % 12
                val amPm = if (isPM) "PM" else "AM"
                val selectedTime = String.format("%02d:%02d %s", formattedHour, minute, amPm)

                if (!isStartTime && startTime != null && binding!!.eventSingle.isChecked) {
                    val (startHour, startMinute) = startTime
                    if (hourOfDay < startHour || (hourOfDay == startHour && minute <= startMinute)) {
                        Toast.makeText(
                            this,
                            "End time must be after start time.",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@TimePickerDialog
                    }
                }

                if (isStartTime) {
                    selectedStartTime = Pair(hourOfDay, minute)
                }

                textView.text = selectedTime
            }, currentHour, currentMinute, false // Set to false for 12-hour format
        )

        timePickerDialog.show()
    }


    private fun init() {
        if (Constants.Search.place != null) {

            binding!!.tvEventLocation.setText(Constants.Search.place!!.address.toString())
            requestCreateEvent.location = "${Constants.Search.place!!.address}"
            requestCreateEvent.locationLat = "${Constants.Search.place!!.latLng!!.latitude}"
            requestCreateEvent.locationLong = "${Constants.Search.place!!.latLng!!.longitude}"
        }

        /*requestCreateEvent.location = "Bhanwar kuaa, Indore, Mp"
        requestCreateEvent.locationLat = "${22.7477351}"
        requestCreateEvent.locationLong = "${75.8416688}"*/
        binding!!.tvEventLocation.setText(requestCreateEvent.location)

        if (null != binding!!.flEventImage.layoutParams) binding!!.flEventImage.layoutParams.height =
            (DEVICE_HEIGHT / 3.5).toInt()

        binding!!.flEventImage.setOnClickListener {
            selectImagePopup()

        }

        binding!!.etEventTitle.afterTextChanged { title ->
            requestCreateEvent.title = title
        }

        binding!!.etDescription.afterTextChanged { description ->
            requestCreateEvent.description = description
        }

        binding!!.etWebsiteUrl.afterTextChanged { website ->
            requestCreateEvent.websiteUrl = website
        }

        binding!!.etBuyTicket.afterTextChanged { buyTicket ->
            requestCreateEvent.buyTicketUrl = buyTicket
        }

        binding!!.tvEventLocation.setOnClickListener {
            autoPlacePicker()
        }

        binding!!.etEventDate.setOnClickListener {

            calendar(Date().time, 2)
        }

        binding!!.tvStartDate.setOnClickListener {
            calendar(Date().time, 0)
        }

        binding!!.tvStartTime.setOnClickListener {
            //  getTime(tvStartTime)
            showTimePicker(binding!!.tvStartTime, isStartTime = true)
           // showTimePicker(binding!!.tvStartTime)
        }

        binding!!.tvEndTime.setOnClickListener {
            // getTime(tvEndTime)
         ///   showTimePicker(binding!!.tvEndTime)

            if (selectedStartTime != null) {
                showTimePicker(binding!!.tvEndTime, isStartTime = false, selectedStartTime)
            } else {
                Toast.makeText(this, "Please select the start time first.", Toast.LENGTH_SHORT).show()
            }
        }

        binding!!.btnSubmit.setOnClickListener {

            Log.d("clickEv", "clickEv")
            //showProgressView(progressBar)
            requestCreateEvent.startTime = binding!!.tvStartTime.text.toString().trim()
            requestCreateEvent.endTime = binding!!.tvEndTime.text.toString().trim()
            if (validate()) {
                requestCreateEvent.timeZone = Constants.Zone.getTimeZone()
                LogManager.logger.i(
                    ArchitectureApp.instance!!.tag,
                    "Req_Event_Details : ${Gson().toJson(requestCreateEvent)}"
                )
                showProgressView(progressBar)
                createEvent()
            } else {

                Log.d("clickEv", "noValidate")
            }
        }

        /* btnSubmit.setOnClickListener {

             when (validate()) {
                 true -> {
                     requestCreateEvent.timeZone = Constants.Zone.getTimeZone()
                     LogManager.logger.i(
                         ArchitectureApp.instance!!.tag,
                         "Event Details : ${Gson().toJson(requestCreateEvent)}"
                     )
                     showProgressView(progressBar)
                     createEvent()
                 }
             }
         }*/

        binding!!.tvEndDate.setOnClickListener {
            when (requestCreateEvent.start.isNotEmpty()) {
                true -> {
                    //  val formatter = SimpleDateFormat("MM-dd-yyyy", Locale.getDefault())
                    val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    val date = formatter.parse(requestCreateEvent.start + " 00:00:00") as Date
                    calendar(date.time, 1)
                }

                false -> {
                    universalToast("Please select event start date first")
                }
            }
        }

        var eventMultiChecked = binding!!.eventMulti.isChecked
        var eventSingleChecked = binding!!.eventSingle.isChecked

        if (eventSingleChecked) {
            binding!!.startEndLay.visibility = View.GONE
        }

        if (eventMultiChecked) {
            binding!!.startEndLay.visibility = View.VISIBLE
        }

        binding!!.eventSingle.setOnClickListener {

            binding!!.startEndLay.visibility = View.GONE
            binding!!.etEventDateLay.visibility = View.VISIBLE
            eventSingleChecked = true
            eventMultiChecked = false
            requestCreateEvent.start = ""
            requestCreateEvent.end = ""
            binding!!.tvStartDate.setText("")
            binding!!.tvEndDate.setText("")
            binding!!.etEventDate.setText("")
            selectedStartTime = null
            binding!!.tvStartTime.setText("")
            binding!!.tvEndTime.setText("")
        }

        binding!!.eventMulti.setOnClickListener {

            binding!!.etEventDateLay.visibility = View.GONE
            binding!!.startEndLay.visibility = View.VISIBLE
            eventMultiChecked = true
            eventSingleChecked = false
            requestCreateEvent.start = ""
            requestCreateEvent.end = ""
            binding!!.tvStartDate.setText("")
            binding!!.tvEndDate.setText("")
            binding!!.etEventDate.setText("")
            selectedStartTime = null
            binding!!.tvStartTime.setText("")
            binding!!.tvEndTime.setText("")
        }

        EasyImage.configuration(this@CreateEventActivity)
            .setImagesFolderName("MeetUpsFellow Event Images")
            .setCopyTakenPhotosToPublicGalleryAppFolder(true)
            .setCopyPickedImagesToPublicGalleryAppFolder(true).setAllowMultiplePickInGallery(false)
    }

    private fun selectImagePopup() {

        val dialog = Dialog(this@CreateEventActivity)

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

            //this@CreateEventActivity.onCameraTapped()
            capturePhoto()
            dialog.cancel()
        }

        chooseGallery.setOnClickListener {

            //this@CreateEventActivity.onGalleryTapped()
            EasyImage.openGallery(this@CreateEventActivity, 0)
            dialog.cancel()
        }

        cancelBtn.setOnClickListener {

            dialog.cancel()
        }

        dialog.show()/*val options = arrayOf<CharSequence>("Take Photo", "Choose from Gallery", "Cancel")
        val builder: android.app.AlertDialog.Builder = android.app.AlertDialog.Builder(this)
        builder.setTitle("Add Photo!")
        builder.setItems(options, DialogInterface.OnClickListener { dialog, item ->
            if (options[item] == "Take Photo") {

                this@CreateEventActivity.onCameraTapped()

            } else if (options[item] == "Choose from Gallery") {

                this@CreateEventActivity.onGalleryTapped()

            } else if (options[item] == "Cancel") {

                dialog.dismiss()
            }
        })
        builder.show()*/
    }

    private fun capturePhoto() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            var photoURI: Uri? = null
            try {
                photoFile = createImageFileWith()
                photoURI = FileProvider.getUriForFile(
                    this@CreateEventActivity,
                    getString(R.string.file_provider_authority),
                    photoFile!!
                )

            } catch (ex: IOException) {
                LogManager.logger.e("TakePicture", ex.message!!)
            }

            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)/* if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
                takePictureIntent.clipData = ClipData.newRawUri("", photoURI)
                takePictureIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            }*/
            startActivityForResult(takePictureIntent, Constants.ImagePicker.CAMERA)
        }
    }

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

    private fun calendar(time: Long, i: Int) {
        val c = Calendar.getInstance()
        val mYear = c.get(Calendar.YEAR)
        val mMonth = c.get(Calendar.MONTH)
        val mDay = c.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this@CreateEventActivity,
            R.style.DatePickerDialogTheme,
            DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                var monthOfYear = "${(month + 1)}"
                if (monthOfYear.length == 1) monthOfYear = "0$monthOfYear"

                var day = "$dayOfMonth"
                if (day.length == 1) day = "0$day"

                when (i) {
                    0 -> {

                        requestCreateEvent.start = "${
                            StringBuilder().append(year).append("-").append(monthOfYear).append("-")
                                .append(day)
                        }"/* requestCreateEvent.start =
                            "${StringBuilder().append(year).append("-").append(monthOfYear).append("-").append(
                                day
                            ).append(" 00:00:00")}"*/
                        requestCreateEvent.end = ""
                        binding!!.tvEndDate.setText(requestCreateEvent.end)
                        binding!!.tvStartDate.setText(requestCreateEvent.start)
                        binding!!.tvStartDate.error = null
                    }

                    1 -> {
                        requestCreateEvent.end = "${
                            StringBuilder().append(year).append("-").append(monthOfYear).append("-")
                                .append(day)
                        }"/*  requestCreateEvent.end =
                            "${StringBuilder().append(year).append("-").append(monthOfYear).append("-").append(
                                day
                            ).append(" 23:59:59")}"*/
                        binding!!.tvEndDate.setText(requestCreateEvent.end)
                        binding!!.tvEndDate.error = null
                    }

                    2 -> {

                        requestCreateEvent.start = "${
                            StringBuilder().append(year).append("-").append(monthOfYear).append("-")
                                .append(day)
                        }"/* requestCreateEvent.start =
                            "${StringBuilder().append(year).append("-").append(monthOfYear).append("-").append(
                                day
                            ).append(" 00:00:00")}"*/

                        ///  requestCreateEvent.end = "${StringBuilder().append(year).append("-").append(monthOfYear).append("-").append(day)}"
                        /*  requestCreateEvent.end = "${StringBuilder().append(year).append("-").append(monthOfYear).append("-").append(
                              day
                          ).append(" 00:00:00")}"*/

                        requestCreateEvent.end = requestCreateEvent.start
                        binding!!.tvEndDate.setText(requestCreateEvent.end)
                        binding!!.etEventDate.setText(requestCreateEvent.start)
                        binding!!.etEventDate.error = null
                    }
                }

            },
            mYear,
            mMonth,
            mDay
        )

        datePickerDialog.datePicker.minDate = time
        datePickerDialog.show()
    }

    private fun validate(): Boolean {

        if (requestCreateEvent.title.isEmpty()) {
            binding!!.etEventTitle.error = "Please add event title"
            binding!!.etEventTitle.requestFocus()
            //Toast.makeText(this@CreateEventActivity, "title", Toast.LENGTH_SHORT).show()
            return false
        }

        if (binding!!.eventMulti.isChecked) {
            if (requestCreateEvent.start.isEmpty()) {
                binding!!.tvStartDate.error = "Please add event start date"
                binding!!.tvStartDate.requestFocus()
                Toast.makeText(
                    this@CreateEventActivity,
                    "Start date is required",
                    Toast.LENGTH_SHORT
                ).show()
                return false
            } else if (requestCreateEvent.end.isEmpty()) {
                binding!!.tvEndDate.error = "Please add event end date"
                binding!!.tvEndDate.requestFocus()
                Toast.makeText(this@CreateEventActivity, "End date is required", Toast.LENGTH_SHORT)
                    .show()
                return false
            }
        } else {
            if (requestCreateEvent.start.isEmpty()) {
                binding!!.etEventDate.setError("Please add event start date")
                binding!!.etEventDate.requestFocus()
                Toast.makeText(
                    this@CreateEventActivity,
                    "Start date is required",
                    Toast.LENGTH_SHORT
                ).show()
                return false
            }
        }


        if (requestCreateEvent.startTime.isEmpty()) {/* tvStartTime.error = "Please add event start time."
            tvStartTime.requestFocus()*/
            Toast.makeText(this@CreateEventActivity, "Start time is required", Toast.LENGTH_SHORT)
                .show()
            return false
        }

        if (requestCreateEvent.endTime.isEmpty()) {/* tvEndTime.error = "Please add event end time."
            tvEndTime.requestFocus()*/
            Toast.makeText(this@CreateEventActivity, "End time is required", Toast.LENGTH_SHORT)
                .show()
            return false
        }

        if (requestCreateEvent.location.isEmpty()) {
            binding!!.tvEventLocation.error = "Please add event location"
            binding!!.tvEventLocation.requestFocus()
            //Toast.makeText(this@CreateEventActivity, "loc", Toast.LENGTH_SHORT).show()
            return false
        }

        if (requestCreateEvent.description.isEmpty()) {
            binding!!.etDescription.error = "Please enter event description"
            binding!!.etDescription.requestFocus()
            //Toast.makeText(this@CreateEventActivity, "des", Toast.LENGTH_SHORT).show()
            return false
        }

        if (requestCreateEvent.websiteUrl.isNotEmpty()) {

            when (Patterns.WEB_URL.matcher(requestCreateEvent.websiteUrl).matches()) {
                true -> {
                    when (URLUtil.isValidUrl(requestCreateEvent.websiteUrl)) {
                        false -> {
                            binding!!.etWebsiteUrl.error = "Please enter a valid website Url"
                            binding!!.etWebsiteUrl.requestFocus()
                            binding!!.scEventDetails.post {
                                binding!!.scEventDetails.smoothScrollTo(
                                    0,
                                    binding!!.scEventDetails.bottom
                                )
                            }
                            return false
                        }

                        true -> {}
                    }
                }

                false -> {
                    binding!!.etWebsiteUrl.error = "Please enter a valid website Url"
                    binding!!.etWebsiteUrl.requestFocus()
                    binding!!.scEventDetails.post {
                        binding!!.scEventDetails.smoothScrollTo(0, binding!!.scEventDetails.bottom)
                    }
                    //Toast.makeText(this@CreateEventActivity, "web", Toast.LENGTH_SHORT).show()
                    return false
                }
            }
        } else {
            binding!!.etWebsiteUrl.error = "Please enter url"
            binding!!.etWebsiteUrl.requestFocus()
            binding!!.scEventDetails.post {
                binding!!.scEventDetails.smoothScrollTo(0, binding!!.scEventDetails.bottom)
            }
            return false
        }

        if (requestCreateEvent.buyTicketUrl.isNotEmpty()) {

            when (Patterns.WEB_URL.matcher(requestCreateEvent.buyTicketUrl).matches()) {
                true -> {
                    when (URLUtil.isValidUrl(requestCreateEvent.buyTicketUrl)) {
                        false -> {
                            binding!!.etBuyTicket.error = "Please enter a valid website Url"
                            binding!!.etBuyTicket.requestFocus()
                            binding!!.scEventDetails.post {
                                binding!!.scEventDetails.smoothScrollTo(
                                    0,
                                    binding!!.scEventDetails.bottom
                                )
                            }
                            return false
                        }

                        true -> {}
                    }
                }

                false -> {
                    binding!!.etBuyTicket.error = "Please enter a valid website Url"
                    binding!!.etBuyTicket.requestFocus()
                    //Toast.makeText(this@CreateEventActivity, "ticket", Toast.LENGTH_SHORT).show()
                    binding!!.scEventDetails.post {
                        binding!!.scEventDetails.smoothScrollTo(0, binding!!.scEventDetails.bottom)
                    }
                    return false
                }
            }
        } else {
            binding!!.etBuyTicket.error = "Please enter url"
            binding!!.etBuyTicket.requestFocus()
            binding!!.scEventDetails.post {
                binding!!.scEventDetails.smoothScrollTo(0, binding!!.scEventDetails.bottom)
            }
            return false
        }

        if (null == requestCreateEvent.image) {
            Toast.makeText(this@CreateEventActivity, "Image is required", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
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
        val fields = Arrays.asList(
            Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS
        )

        // Start the autocomplete intent.
        val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields)
            .setTypeFilter(TypeFilter.CITIES).build(this)
        startActivityForResult(intent, Constants.RequestCode.AUTOCOMPLETE_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (resultCode) {
            Activity.RESULT_OK -> {
                when (requestCode) {
                    Constants.ImagePicker.CAMERA -> {

                        var exif: androidx.exifinterface.media.ExifInterface? = null
                        try {
                            exif = androidx.exifinterface.media.ExifInterface(photoFile!!.path)
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }

                        val orientation = exif!!.getAttributeInt(
                            androidx.exifinterface.media.ExifInterface.TAG_ORIENTATION,
                            androidx.exifinterface.media.ExifInterface.ORIENTATION_UNDEFINED
                        )
                        val bmOptions = BitmapFactory.Options()
                        val imageBitmap = BitmapFactory.decodeFile(photoFile!!.path, bmOptions)
                        val rotatedBitmap = rotateBitmap(imageBitmap, orientation)!!

                        val os = BufferedOutputStream(FileOutputStream(photoFile))
                        rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 50, os)
                        os.close()

                        photoFile  = compressImage(
                            file = photoFile!!,          // Original file
                            maxWidth = 1280,           // Max width
                            maxHeight = 720,           // Max height
                            quality = 50               // Compression quality
                        )


                          /*  Compressor(this@CreateEventActivity).setMaxWidth(1280).setMaxHeight(720)
                                .setQuality(50).setCompressFormat(Bitmap.CompressFormat.JPEG)
                                .compressToFile(photoFile)*/

                        requestCreateEvent.image = photoFile!!
                        binding!!.ivEventImage.setImageURI(Uri.fromFile(requestCreateEvent.image))
                    }

                    Constants.RequestCode.AUTOCOMPLETE_REQUEST_CODE -> {
                        val place = Autocomplete.getPlaceFromIntent(data!!)
                        LogManager.logger.i(
                            ArchitectureApp.instance!!.tag,
                            "Place: " + place.name + ", " + place.id + ", Address : " + place.address
                        )
                        requestCreateEvent.location = "${place.address}"
                        requestCreateEvent.locationLat = "${place.latLng!!.latitude}"
                        requestCreateEvent.locationLong = "${place.latLng!!.longitude}"
                        binding!!.tvEventLocation.setText(requestCreateEvent.location)
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
            androidx.exifinterface.media.ExifInterface.ORIENTATION_NORMAL -> {
                return bitmap
            }

            androidx.exifinterface.media.ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> {
                matrix.setScale((-1).toFloat(), 1F)
            }

            androidx.exifinterface.media.ExifInterface.ORIENTATION_ROTATE_180 -> {
                matrix.setRotate(180F)
            }

            androidx.exifinterface.media.ExifInterface.ORIENTATION_FLIP_VERTICAL -> {
                matrix.setRotate(180F)
                matrix.postScale((-1).toFloat(), 1F)
            }

            androidx.exifinterface.media.ExifInterface.ORIENTATION_TRANSPOSE -> {
                matrix.setRotate(90F)
                matrix.postScale((-1).toFloat(), 1F)
            }

            androidx.exifinterface.media.ExifInterface.ORIENTATION_ROTATE_90 -> {
                matrix.setRotate(90F)
            }

            androidx.exifinterface.media.ExifInterface.ORIENTATION_TRANSVERSE -> {
                matrix.setRotate((-90).toFloat())
                matrix.postScale((-1).toFloat(), 1F)
            }

            androidx.exifinterface.media.ExifInterface.ORIENTATION_ROTATE_270 -> {
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
        EasyImage.handleActivityResult(requestCode,
            resultCode,
            data,
            this,
            object : DefaultCallback() {
                override fun onImagesPicked(
                    imageFiles: MutableList<File>, source: EasyImage.ImageSource?, type: Int
                ) {
                    var image = imageFiles[0]
                    var exif: androidx.exifinterface.media.ExifInterface? = null
                    try {
                        exif = androidx.exifinterface.media.ExifInterface(image.path)
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }

                    val orientation = exif!!.getAttributeInt(
                        androidx.exifinterface.media.ExifInterface.TAG_ORIENTATION,
                        androidx.exifinterface.media.ExifInterface.ORIENTATION_UNDEFINED
                    )
                    val bmOptions = BitmapFactory.Options()
                    val imageBitmap = BitmapFactory.decodeFile(image.path, bmOptions)
                    val rotatedBitmap = rotateBitmap(imageBitmap, orientation)!!

                    val os = BufferedOutputStream(FileOutputStream(image))
                    rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 50, os)
                    os.close()

                    requestCreateEvent.image = compressImage(
                        file = image,          // Original file
                        maxWidth = 1280,           // Max width
                        maxHeight = 720,           // Max height
                        quality = 50               // Compression quality
                    )

                  /*  requestCreateEvent.image =
                        Compressor(this@CreateEventActivity).setMaxWidth(1280).setMaxHeight(720)
                            .setQuality(50).setCompressFormat(Bitmap.CompressFormat.JPEG)
                            .compressToFile(image)*/

                    binding!!.ivEventImage.setImageURI(Uri.fromFile(requestCreateEvent.image))
                }

                override fun onCanceled(source: EasyImage.ImageSource, type: Int) {
                    if (source == EasyImage.ImageSource.CAMERA_IMAGE) {
                        EasyImage.lastlyTakenButCanceledPhoto(this@CreateEventActivity)?.delete()
                    }
                }

                override fun onImagePickerError(
                    e: Exception, source: EasyImage.ImageSource, type: Int
                ) {
                    e.printStackTrace()
                }
            })

        /*object : DefaultCallback() {
            override fun onImagesPicked(
                imageFiles: MutableList<File>,
                source: EasyImage.ImageSource?,
                type: Int
            ) {
                requestCreateEvent.image = imageFiles[0]
                var exif: androidx.exifinterface.media.ExifInterface? = null
                try {
                    exif =
                        androidx.exifinterface.media.ExifInterface(requestCreateEvent.image!!.path)
                } catch (e: IOException) {
                    e.printStackTrace()
                }

                val orientation = exif!!.getAttributeInt(
                    androidx.exifinterface.media.ExifInterface.TAG_ORIENTATION,
                    androidx.exifinterface.media.ExifInterface.ORIENTATION_UNDEFINED
                )
                val bmOptions = BitmapFactory.Options()
                val imageBitmap =
                    BitmapFactory.decodeFile(requestCreateEvent.image!!.path, bmOptions)
                val rotatedBitmap = rotateBitmap(imageBitmap, orientation)!!

                val os = BufferedOutputStream(FileOutputStream(requestCreateEvent.image))
                rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 50, os)
                os.close()

                requestCreateEvent.image = Compressor(this@CreateEventActivity)
                    .setMaxWidth(1280)
                    .setMaxHeight(720)
                    .setQuality(50)
                    .setCompressFormat(Bitmap.CompressFormat.JPEG)
                    .compressToFile(requestCreateEvent.image)

                ivEventImage.setImageURI(Uri.fromFile(requestCreateEvent.image))
            }

            override fun onCanceled(source: EasyImage.ImageSource, type: Int) {
                if (source == EasyImage.ImageSource.CAMERA_IMAGE) {
                    EasyImage.lastlyTakenButCanceledPhoto(this@CreateEventActivity)?.delete()
                }
            }

            override fun onImagePickerError(
                e: Exception,
                source: EasyImage.ImageSource,
                type: Int
            ) {
                e!!.printStackTrace()
            }
        })*/
    }

    private fun createEvent() {
        if (null == mPresenter) mPresenter = CreateEventPresenter(presenter)

        run {
            mPresenter!!.addCreateEventObject(requestCreateEvent)
            mPresenter!!.callRetrofit(ConstantsApi.CREATE_EVENT)
        }
    }

    private fun showAlertEvent(message: String, title: String) {

        val dialog = Dialog(this@CreateEventActivity)

        dialog.setCancelable(false)

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

        val dialog_title = view.findViewById<TextView>(R.id.dialog_title)
        val dialog_content = view.findViewById<TextView>(R.id.dialog_content)
        val noExitBtn = view.findViewById<Button>(R.id.noExitBtn)
        var yesExitBtn = view.findViewById<Button>(R.id.yesExitBtn)

        dialog_title.text = title
        dialog_content.text =
            "Your Event is under review by admin, Fellow's will be able to see your event after it's verified."
        yesExitBtn.text = "Okay"
        noExitBtn.visibility = View.GONE

        yesExitBtn.setOnClickListener {

            dialog.dismiss()
            finish()
        }

        dialog.show()

        /*val alertDialog = AlertDialog.Builder(
            this@CreateEventActivity, R.style.MyDialogTheme2
        )
        alertDialog.setTitle(title)
        alertDialog.setMessage("Your Event is under review, you will be able to see it once it is verified")
        alertDialog.setPositiveButton(getString(R.string.text_okay)) { dialog, _ ->
            dialog.dismiss()
            finish()
        }
        alertDialog.setCancelable(false)

        alertDialog.show()*/
    }

    private fun callForUserProfile() {
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

    override fun onPause() {
        super.onPause()
        sharedPreferencesUtil.saveTimmerTime(System.currentTimeMillis().toString())
        LogManager.logger.i(
            ArchitectureApp.instance!!.tag,
            "onPause ::: " + System.currentTimeMillis().toString()
        )

    }

    override fun onResume() {
        super.onResume()

        var lasttrackTime = sharedPreferencesUtil.featchTimmerTime()
        var currentbackTime: Long = System.currentTimeMillis()
        Log.e("lasttrackTime", lasttrackTime)
        LogManager.logger.i(ArchitectureApp.instance!!.tag, "onResume ::: " + lasttrackTime)

        if (lasttrackTime != "") {
            val currentSeconds = ((currentbackTime - lasttrackTime.toLong()) / 1000).toInt()
            if (currentSeconds >= Constants.TimerRefresh.START_TIME_IN_SEC) {
                startActivity(Intent(this, SplashActivity::class.java))
                finish()
            }
        }


    }


}