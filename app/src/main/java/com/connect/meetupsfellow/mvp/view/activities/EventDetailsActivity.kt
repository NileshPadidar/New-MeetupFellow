package com.connect.meetupsfellow.mvp.view.activities

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import com.connect.meetupsfellow.R
import com.connect.meetupsfellow.application.ArchitectureApp
import com.connect.meetupsfellow.components.activity.CustomAppActivityCompatViewImpl
import com.connect.meetupsfellow.constants.Constants
import com.connect.meetupsfellow.constants.ConstantsApi
import com.connect.meetupsfellow.databinding.ActivityEventDetailBinding
import com.connect.meetupsfellow.developement.interfaces.ApiConnect
import com.connect.meetupsfellow.developement.interfaces.SharedPreferencesUtil
import com.connect.meetupsfellow.global.utils.DisplayImage
import com.connect.meetupsfellow.log.LogManager
import com.connect.meetupsfellow.mvp.connector.activity.EventDetailsConnector
import com.connect.meetupsfellow.mvp.presenter.activity.EventDetailPresenter
import com.connect.meetupsfellow.mvp.view.dialog.FlagReportingDialog
import com.connect.meetupsfellow.retrofit.request.RequestReportEvent
import com.connect.meetupsfellow.retrofit.response.CommonResponse
import com.connect.meetupsfellow.retrofit.response.ResponseEventFeeds
import com.connect.meetupsfellow.retrofit.response.ResponseEventInterestedPeople
import com.connect.meetupsfellow.retrofit.response.ResponseUserData
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.gson.Gson
import retrofit2.Response
import rx.Observer
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject


@Suppress("UNUSED_PARAMETER")
class EventDetailsActivity : CustomAppActivityCompatViewImpl(), OnMapReadyCallback {

    private val presenter = object : EventDetailsConnector.RequiredViewOps {
        override fun showToast(error: String, logout: Boolean?) {
            when (logout) {
                true -> {
                    logout()
                }

                false -> {}
                null -> {}
            }
            universalToast(error)
            //hideProgressView(progressBar)
        }

        override fun showResponse(response: CommonResponse, type: ConstantsApi) {
            when (type) {
                ConstantsApi.REPORT_EVENT -> {
                    universalToast(response.message)
                    hideProgressView(progressBar)
                }

                ConstantsApi.EVENT_LIKE -> {
                    when (response.message == "Event is no longer available." || response.message == "MeetUpsFellow has blocked this event.") {
                        true -> {
                            universalToast(response.message)
                            finish()
                        }

                        false -> {}
                    }
                    hideProgressView(progressBar)
                }

                ConstantsApi.EVENT_INTERESTED -> {

                    hideProgressView(progressBar)

                    Log.d("runn", "runnn")

                    when (response.message == "Event is no longer available." || response.message == "MeetUpsFellow has blocked this event.") {
                        true -> {
                            universalToast(response.message)
                            finish()
                            return
                        }

                        false -> {}
                    }

                    eventDetails!!.meInterested = !eventDetails!!.meInterested
                    binding!!.ivInterested.isSelected = eventDetails!!.meInterested
                    when (eventDetails!!.meInterested) {
                        true -> {
                            eventDetails!!.interestedUsers.add(ResponseEventInterestedPeople().apply {
                                imagePath = sharedPreferencesUtil.fetchUserProfileImage()
                                uid = sharedPreferencesUtil.userId().toInt()
                            })
                            //setInterestedPeople()
                            setEventDetailsData()
                        }

                        false -> {
                            val index = eventDetails!!.interestedUsers.firstOrNull {
                                it.uid == sharedPreferencesUtil.userId().toInt()
                            }?.let { eventDetails!!.interestedUsers.indexOf(it) } ?: -1
                            if (index > -1) {
                                eventDetails!!.interestedUsers.removeAt(index)
                                if (indexExists(
                                        interestedPeople,
                                        index
                                    )
                                ) interestedPeople[index].visibility = View.INVISIBLE
                                //setInterestedPeople()
                                setEventDetailsData()
                            }
                        }
                    }

                }

                ConstantsApi.EVENT -> {
                    when (response.message.isEmpty()) {
                        true -> {
                            eventDetails = response.event
                            setEventDetailsData()
                        }

                        false -> {
                            universalToast(response.message)
                            finish()
                        }
                    }
                    hideProgressView(progressBar)

                }

                else -> {
                }
            }
            //hideProgressView(progressBar)
        }
    }

    private val onClickListener = View.OnClickListener { view ->
        when (view.id) {
            R.id.ivEventInterestedUser0, R.id.ivEventInterestedUser1, R.id.ivEventInterestedUser2, R.id.ivEventInterestedUser3, R.id.ivEventInterestedUser4, R.id.ivEventInterestedUser5, R.id.tvMorePeople -> {
                switchActivity(Constants.Intent.Interested, false, Bundle().apply {
                    putInt(Constants.IntentDataKeys.EventId, eventDetails!!.id)
                })
            }
        }
    }

    @Inject
    lateinit var sharedPreferencesUtil: SharedPreferencesUtil

    @Inject
    lateinit var apiConnect: ApiConnect

    private var mPresenter: EventDetailsConnector.PresenterOps? = null

    private var eventDetails: ResponseEventFeeds? = null
    private var eventId = -1

    private val interestedPeople = arrayListOf<RelativeLayout>()
    private val interestedPeopleImage = arrayListOf<ImageView>()

    private lateinit var mapView: MapView
    private lateinit var googleMap: GoogleMap
    private var MAPVIEW_BUNDLE_KEY = ""
    private var binding: ActivityEventDetailBinding? = null
    private lateinit var progressBar: LinearLayout
    private val MAX_LINES_COLLAPSED: Int = 5

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getIntentData()
        component.inject(this@EventDetailsActivity)
        binding = ActivityEventDetailBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        progressBar = binding!!.includedLoading.rlProgress
        //setContentView(R.layout.activity_event_detail)
        if (null == eventDetails && eventId == -1) {
            universalToast("No event details found")
            finish()
            return
        }
        MAPVIEW_BUNDLE_KEY = getString(R.string.google_server_key)
        setupImages()
        setupActionBar(getString(R.string.title_event_detail), true)
        startLoadingAnim()
        init()
        callForUserProfile()

        mapView = findViewById(R.id.mapView)

        // Initialize MapView
        var mapViewBundle: Bundle? = null
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY)
        }
        mapView.onCreate(mapViewBundle)
        mapView.getMapAsync(this@EventDetailsActivity)
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        Log.e("Event_Detail", "Set_Map_location: " + eventDetails!!.location)
        // Add a marker and move the camera

        ///  val location = LatLng(37.7749, -122.4194) // Example: San Francisco
        val location = LatLng(
            eventDetails!!.locationLat.toDouble(),
            eventDetails!!.locationLong.toDouble()
        ) // Example: San Francisco
        googleMap.addMarker(MarkerOptions().position(location).title(eventDetails!!.location))
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 10f))
    }

    // Handle MapView lifecycle
    override fun onDestroy() {
        mapView.onDestroy()
        super.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        var mapViewBundle = outState.getBundle(MAPVIEW_BUNDLE_KEY)
        if (mapViewBundle == null) {
            mapViewBundle = Bundle()
            outState.putBundle(MAPVIEW_BUNDLE_KEY, mapViewBundle)
        }
        mapView.onSaveInstanceState(mapViewBundle)
    }

    private fun startLoadingAnim() {

        val animation = android.view.animation.AnimationUtils.loadAnimation(
            applicationContext, R.anim.blink_anim
        )

        binding!!.includedLoading.animLogo.startAnimation(animation)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_event, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_flag -> {
                FlagReportingDialog.Builder(
                    this@EventDetailsActivity, Constants.DialogTheme.NoTitleBar
                ).reasons(sharedPreferencesUtil.fetchReason())
                    .setOnCompletionListener(object : FlagReportingDialog.OnCompleteListener {
                        override fun onComplete(param: String, dialog: DialogInterface) {
                            when (param.isNotEmpty()) {
                                true -> {
                                    showProgressView(progressBar)
                                    reportEvent(param)
                                }

                                false -> {}
                            }
                            dialog.dismiss()
                        }
                    }).build()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }

    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
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



        checkRequiredPermissions()
        reason()
    }

    private fun setupImages() {
        interestedPeople.add(binding!!.rlInterestedPeople0)
        interestedPeople.add(binding!!.rlInterestedPeople1)
        interestedPeople.add(binding!!.rlInterestedPeople2)
        interestedPeople.add(binding!!.rlInterestedPeople3)
        interestedPeople.add(binding!!.rlInterestedPeople4)
        interestedPeople.add(binding!!.rlInterestedPeople5)

        interestedPeopleImage.add(binding!!.ivEventInterestedUser0)
        interestedPeopleImage.add(binding!!.ivEventInterestedUser1)
        interestedPeopleImage.add(binding!!.ivEventInterestedUser2)
        interestedPeopleImage.add(binding!!.ivEventInterestedUser3)
        interestedPeopleImage.add(binding!!.ivEventInterestedUser4)
        interestedPeopleImage.add(binding!!.ivEventInterestedUser5)

        interestedPeople.asSequence().forEach {
            it.visibility = View.INVISIBLE
        }

        interestedPeopleImage.asSequence().forEach {
            it.setOnClickListener(onClickListener)
        }

        binding!!.tvMorePeople.setOnClickListener(onClickListener)

        binding!!.tvEventWebsite.setOnClickListener {
            switchActivity(Constants.Intent.Webview, false, Bundle().apply {
                putString(Constants.IntentDataKeys.TITLE, getString(R.string.label_website))
                putString(Constants.IntentDataKeys.LINK, eventDetails!!.websiteUrl)
            })
        }

        binding!!.frameMore.visibility = View.INVISIBLE
        binding!!.tvMorePeople.visibility = View.INVISIBLE
    }

    private fun getIntentData() {
        if (intent.hasExtra(Constants.IntentDataKeys.EventDetails)) {
            eventDetails = Gson().fromJson<ResponseEventFeeds>(
                intent.getStringExtra(Constants.IntentDataKeys.EventDetails),
                ResponseEventFeeds::class.java
            )
        }

        if (intent.hasExtra(Constants.IntentDataKeys.EventId)) {
            eventId = intent.getIntExtra(Constants.IntentDataKeys.EventId, -1)
        }
    }

    private fun init() {

        if (null != binding!!.ivEventImage.layoutParams) binding!!.ivEventImage.layoutParams.height =
            (DEVICE_HEIGHT / 3)

        if (null != binding!!.llInterestedPeople.layoutParams) binding!!.llInterestedPeople.layoutParams.height =
            (DEVICE_HEIGHT / 9)

        if (null == eventDetails) {
            showProgressView(progressBar)
            fetchEventDetails()
        } else {
            setEventDetailsData()
        }

        binding!!.eventBackBtn.setOnClickListener {

            finish()
        }

        binding!!.flagEvent.setOnClickListener {

            FlagReportingDialog.Builder(
                this@EventDetailsActivity, Constants.DialogTheme.NoTitleBar
            ).reasons(sharedPreferencesUtil.fetchReason())
                .setOnCompletionListener(object : FlagReportingDialog.OnCompleteListener {
                    override fun onComplete(param: String, dialog: DialogInterface) {
                        when (param.isNotEmpty()) {
                            true -> {
                                showProgressView(progressBar)
                                reportEvent(param)
                            }

                            false -> {}
                        }
                        dialog.dismiss()
                    }
                }).build()
        }


    }

    private fun setEventDetailsData() {
        binding!!.tvEventName.text = eventDetails!!.title
        binding!!.ivFavorite.isSelected = eventDetails!!.meliked
        binding!!.ivInterested.isSelected = eventDetails!!.meInterested
        binding!!.tvEventLocation.text = eventDetails!!.location
        Log.e("Data2", "size:- ${eventDetails!!.interestedUsers.size} ")
        binding!!.tvMorePeople1.text = eventDetails!!.interestedUsers.size.toString()

        if (sharedPreferencesUtil.fetchSettings().unit != 0) {
            binding!!.tvEventDistance.text =
                String.format(getString(R.string.text_distance), eventDetails!!.mile)
        } else {
            binding!!.tvEventDistance.text =
                String.format(getString(R.string.text_distance_km), eventDetails!!.km)
        }

        binding!!.tvEventDate.text = String.format(
            getString(R.string.text_event_dates_2),
            formatDate(eventDetails!!.startDate),
            formatDate(eventDetails!!.endDate)
        )
        binding!!.tvEventDescription.text = eventDetails!!.description
       /// binding!!.tvEventDescription.setTrimCollapsedText("Read More")
      ///  binding!!.tvEventDescription.setTrimExpandedText(" Read Less")
       /* binding!!.tvEventDescription.post(Runnable {
            // Past the maximum number of lines we want to display.
            if (binding!!.tvEventDescription.getLineCount() > MAX_LINES_COLLAPSED) {
                val lastCharShown: Int = binding!!.tvEventDescription.getLayout()
                    .getLineVisibleEnd(MAX_LINES_COLLAPSED - 1)

                binding!!.tvEventDescription.setMaxLines(MAX_LINES_COLLAPSED)

                Log.e("Set_Text", "ReadMore_Visible_self: ")
                // String moreString = context.getString(R.string.more);
                val moreString = this.getString(R.string.read_more)
                val suffix = moreString

                // 3 is a "magic number" but it's just basically the length of the ellipsis we're going to insert
                val actionDisplayText: String = eventDetails!!.description.substring(
                    0,
                    lastCharShown - suffix.length - 10
                ) + "..." + suffix

                val truncatedSpannableString = SpannableString(actionDisplayText)
                val startIndex = actionDisplayText.indexOf(moreString)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    truncatedSpannableString.setSpan(
                        ForegroundColorSpan(this.getColor(android.R.color.holo_blue_dark)),
                        startIndex,
                        startIndex + moreString.length,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }
                binding!!.tvEventDescription.setText(truncatedSpannableString)
                binding!!.tvEventDescription.setClickable(true)
            } else {
                binding!!.tvEventDescription.setClickable(false)
            }
        })*/

        setupReadMoreReadLess(
            binding!!.tvEventDescription,
            eventDetails!!.description,
            maxLines = 5,
            readMoreText = " Read More",
            readLessText = " Read Less"
        )

        binding!!.tvEventWebsite.text = eventDetails!!.websiteUrl

        showEventImage(binding!!.ivEventImage, eventDetails!!.image)

        hideProgressView(progressBar)

        setInterestedPeople()

        binding!!.ivEventImage.setOnClickListener {
            val intent = Intent(
                this,
                VideoPlayerActivity::class.java
            )
            intent.putExtra(Constants.IntentDataKeys.ImgPath, eventDetails!!.image)
            startActivity(intent)
        }
    }


    fun setupReadMoreReadLess(
        textView: TextView,
        fullText: String,
        maxLines: Int = 5,
        readMoreText: String = " Read More",
        readLessText: String = " Read Less"
    ) {
        // Ensure the TextView is fully rendered before setting text
        textView.post {
            if (textView.layout != null && textView.lineCount > maxLines) {
                // Truncate the text for the collapsed state
                val layout = textView.layout
                val lastVisibleCharIndex = layout.getLineEnd(maxLines - 1)
                val truncatedText = fullText.substring(0, lastVisibleCharIndex).trim() + "..."

                val spannable = SpannableString(truncatedText + readMoreText)
                spannable.setSpan(
                    ForegroundColorSpan(this.getColor(android.R.color.holo_blue_dark)),
                    truncatedText.length,
                    spannable.length,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                spannable.setSpan(object : ClickableSpan() {
                    override fun onClick(widget: View) {
                        showFullText(textView, fullText, maxLines, readMoreText, readLessText)
                    }
                }, truncatedText.length, spannable.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

                textView.text = spannable
                textView.maxLines = maxLines
                textView.movementMethod = LinkMovementMethod.getInstance() // Enable link clicks
            } else {
                textView.text = fullText // No truncation needed
            }
        }
    }

    private fun showFullText(
        textView: TextView,
        fullText: String,
        maxLines: Int,
        readMoreText: String,
        readLessText: String
    ) {
        val spannable = SpannableString(fullText + readLessText)
        spannable.setSpan(
           // ForegroundColorSpan(textView.context.getColor(android.R.color.holo_blue_dark)),
            ForegroundColorSpan(this.getColor(android.R.color.holo_blue_dark)),
            fullText.length,
            spannable.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spannable.setSpan(object : ClickableSpan() {
            override fun onClick(widget: View) {
                setupReadMoreReadLess(textView, fullText, maxLines, readMoreText, readLessText)
            }
        }, fullText.length, spannable.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        textView.text = spannable
        textView.maxLines = Int.MAX_VALUE
        textView.movementMethod = LinkMovementMethod.getInstance() // Enable link clicks
    }

    private fun setInterestedPeople() {
        if (eventDetails!!.interestedUsers.isEmpty()) {
            binding!!.llInterestedPeople.visibility = View.GONE
        } else if (eventDetails!!.interestedUsers.size == 6) {
            binding!!.frameMore.visibility = View.GONE
            binding!!.tvMorePeople.visibility = View.GONE
            binding!!.ivEventInterestedUser5.visibility = View.VISIBLE
        } else if (eventDetails!!.interestedUsers.size > 6) {
            binding!!.frameMore.visibility = View.VISIBLE
            binding!!.tvMorePeople.visibility = View.VISIBLE
            binding!!.tvMorePeople.text = String.format(
                getString(
                    R.string.text_more_people
                ), "${eventDetails!!.interestedUsers.size - 5}"
            )
        }

        eventDetails!!.interestedUsers.asSequence().forEach {
            val index = eventDetails!!.interestedUsers.indexOf(it)
            when (indexExists(interestedPeople, index)) {
                true -> {
                    interestedPeople[index].visibility = View.VISIBLE
                    showCircleImage(interestedPeopleImage[index], it.imagePath)
                }

                false -> {}
            }
        }

    }

    private fun showCircleImage(imageView: ImageView, imageUrl: String) {
        DisplayImage.with(this@EventDetailsActivity).load(imageUrl)
            .placeholder(R.drawable.meetupsfellow_transpatent).shape(DisplayImage.Shape.CropCircle)
            .into(imageView).build()
    }

    private fun showEventImage(imageView: ImageView, imageUrl: String) {
        DisplayImage.with(this@EventDetailsActivity).load(imageUrl)
            .placeholder(R.drawable.meetupsfellow_transpatent).shape(DisplayImage.Shape.DEFAULT)
            .into(imageView).build()
    }

    private fun formatDate(date: String): String {
        return DateFormat.getDateInstance(DateFormat.DEFAULT, Locale("en", "US")).format(
                SimpleDateFormat("MM-dd-yyyy", Locale("en", "US")).parse(date)
            )
    }

    fun onBuyTicketClick(view: View) {
        if (eventDetails != null) switchActivity(Constants.Intent.Webview, false, Bundle().apply {
            putString(Constants.IntentDataKeys.TITLE, getString(R.string.label_buy_ticket))
            putString(Constants.IntentDataKeys.LINK, eventDetails!!.buyTicketUrl)
        })
    }

    fun onFavoriteClick(view: View) {
        showProgressView(progressBar)
        eventDetails!!.meliked = !eventDetails!!.meliked
        binding!!.ivFavorite.isSelected = eventDetails!!.meliked
        eventLike()
    }

    fun onInterestedClick(view: View) {
        //showProgressView(progressBar)
        eventInterested()
    }

    private fun eventLike() {
        if (null == mPresenter) mPresenter = EventDetailPresenter(presenter)

        run {
            mPresenter!!.addLikeEventObject("${eventDetails!!.id}")
            mPresenter!!.callRetrofit(ConstantsApi.EVENT_LIKE)
        }
    }

    private fun eventInterested() {
        if (null == mPresenter) mPresenter = EventDetailPresenter(presenter)

        run {
            showProgressView(progressBar)
            mPresenter!!.addLikeEventObject("${eventDetails!!.id}")
            mPresenter!!.callRetrofit(ConstantsApi.EVENT_INTERESTED)
        }
    }

    private fun fetchEventDetails() {
        if (null == mPresenter) mPresenter = EventDetailPresenter(presenter)

        run {
            mPresenter!!.addLikeEventObject("$eventId")
            mPresenter!!.callRetrofit(ConstantsApi.EVENT)
        }
    }

    private fun reason() {
        if (null == mPresenter) mPresenter = EventDetailPresenter(presenter)

        run {
            mPresenter!!.callRetrofit(ConstantsApi.REASON)
        }
    }

    private fun reportEvent(message: String) {
        if (null == mPresenter) mPresenter = EventDetailPresenter(presenter)

        run {
            mPresenter!!.addReportEventObject(RequestReportEvent().apply {
                id = "${eventDetails!!.id}"
                reason = message
            })
            mPresenter!!.callRetrofit(ConstantsApi.REPORT_EVENT)
        }
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
                                    val profile: ResponseUserData? = response?.userInfo/* var visible_pro_user: Boolean? = profile?.isProMembership
                                    if (visible_pro_user!!) {
                                        tv_pro_visibility.visibility = View.VISIBLE
                                    } else {
                                        tv_pro_visibility.visibility = View.INVISIBLE
                                    }*/

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
        mapView.onPause()
        sharedPreferencesUtil.saveTimmerTime(System.currentTimeMillis().toString())
        LogManager.logger.i(
            ArchitectureApp.instance!!.tag,
            "onPause ::: " + System.currentTimeMillis().toString()
        )

    }


}