package com.connect.meetupsfellow.mvp.view.activities

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.connect.meetupsfellow.R
import com.connect.meetupsfellow.application.ArchitectureApp
import com.connect.meetupsfellow.components.activity.CustomAppActivityCompatViewImpl
import com.connect.meetupsfellow.constants.Constants
import com.connect.meetupsfellow.constants.ConstantsApi
import com.connect.meetupsfellow.databinding.SearchByloactionActivityBinding
import com.connect.meetupsfellow.developement.interfaces.SharedPreferencesUtil
import com.connect.meetupsfellow.log.LogManager
import com.connect.meetupsfellow.mvp.connector.activity.HomeConnector
import com.connect.meetupsfellow.mvp.connector.activity.SearchConnector
import com.connect.meetupsfellow.mvp.presenter.activity.HomePresenter
import com.connect.meetupsfellow.mvp.view.adapter.NearByAdapter
import com.connect.meetupsfellow.mvp.view.adapter.SearchItemAdapter
import com.connect.meetupsfellow.retrofit.request.RequestFeed
import com.connect.meetupsfellow.retrofit.response.CommonResponse
import com.connect.meetupsfellow.retrofit.response.ResponseUserData
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.gson.Gson
import javax.inject.Inject


class SearchByLocationActivity : CustomAppActivityCompatViewImpl() {

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

            when (type) {
                ConstantsApi.FAVORITE_USER -> {/*val search = arrayListOf<ResponseSearch>()
                    response.favoriteUsers.asSequence().forEach {
                        search.add(ResponseSearch().apply {
                            search_id = it.id
                            userId = "${it.id}"
                            name = it.name
                            mile = it.mile
                            km = it.km
                            userLocation = it.homeLocation
                            userImage = it.userPic
                            this.type = Constants.View.User
                        })
                    }
                    searchAdapter.update(search)*/
                }

                ConstantsApi.FAVORITE_EVENT -> {/* val search = arrayListOf<ResponseSearch>()
                    response.favoriteEvents.asSequence().forEach {
                        search.add(ResponseSearch().apply {
                            search_id = it.id
                            userId = "${it.id}"
                            title = it.title
                            mile = it.mile
                            km = it.km
                            eventLocation = it.location
                            eventImage = it.image
                            startDate = it.startDate
                            endDate = it.endDate
                            this.type = Constants.View.Event
                        })
                    }
                    searchAdapter.update(search)*/
                }

                else -> {
                    /// searchAdapter.update(response.search)
                    searchAdapter.update(response.nearbyuser)
                }
            }
            adjustNearbyUi()
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
            //swipeRefresh.isRefreshing = false
        }

        override fun showResponse(response: CommonResponse, type: ConstantsApi) {
            when (type) {

                ConstantsApi.ADMIN_ADS -> {
                    //   nearByListAdminAds = ArrayList()
                    //  nearByListAdminAds.clear()


                    if (!response.adminAdvertisements.isNullOrEmpty()) {

                        //sharedPreferencesUtil.saveAdminAdsCount(response?.adminAdvertisements.size.toString())

                        gridLayoutManager?.spanSizeLookup = object :
                            GridLayoutManager.SpanSizeLookup() {

                            override fun getSpanSize(position: Int): Int {
                                return if (position < response.adminAdvertisements.size * 17) {
                                    if ((position + 1) % 17 == 0 && (position + 1) != 1) { // totalRowCount : How many item you want to show
                                        4
                                    } else 1
                                } else 1
                            }
                        }


                        response.adminAdvertisements?.let { nearByListAdminAds.addAll(it) }
                    } else {
                        //  nearByListAdminAds.add(ResponseUserData      )
                        //sharedPreferencesUtil.saveAdminAdsCount("0")

                        gridLayoutManager?.spanSizeLookup = object :
                            GridLayoutManager.SpanSizeLookup() {

                            override fun getSpanSize(position: Int): Int {
                                return 1
                            }
                        }


                        //  nearByListAdminAds?.add()


                    }


                }


                ConstantsApi.FETCH_NEARBY -> {
                    when (response.haveNext) {
                        1 -> {
                            loadMoreNearby = true
                            nextNearby = response.nextPage
                        }
                    }

                    val dummy = linkedSetOf<ResponseUserData>()
                    dummy.addAll(nearByListAdminAds)

                    nearByListAdminAds.clear()
                    nearByListAdminAds.addAll(dummy)

                    response.nearbyuser?.let { nearByListRecentOnlyAds.addAll(it) }
                    response.nearbyuser?.let { nearByListSwitchs.addAll(it) }

                    if (nearByListAdminAds.isEmpty()) {

                        response.nearbyuser?.let { nearByList.addAll(it) }

                        nearByAdapter.updateNearBy(nearByList)


                    } else {

                        var local: Int = 0
                        for (j in 0 until nearByListSwitchs.size) {

                            if ((j) % 16 == 0 && j != 0) {
                                //  nearByList?.addAll(nearByListAdminAds)
                                local = ((j) / 16)
                                if (nearByListAdminAds.size < local) {
                                    nearByList.add(nearByListAdminAds.get(0))
                                } else {
                                    nearByList.add(nearByListAdminAds.get(local - 1))

                                }
                            }

                            nearByList.add(nearByListSwitchs.get(j))

                        }

                        nearByAdapter.updateNearBy(nearByList)

                        adjustNearbyUi()

                        Log.d("nearRef1", "ref")

                    }

                    if (nearByAdapter.itemCount > 0) {
                        binding!!.tvNoSearch.visibility = View.GONE
                        binding!!.rvSearch.visibility = View.VISIBLE
                        loading.visibility = View.GONE
                    } else {

                        binding!!.tvNoSearch.visibility = View.VISIBLE
                        binding!!.rvSearch.visibility = View.GONE
                        loading.visibility = View.GONE
                    }

                    loading.visibility = View.GONE
                }

                else -> {}
            }
        }
    }

    @Inject
    lateinit var sharedPreferencesUtil: SharedPreferencesUtil

    private var mPresenterS: SearchConnector.PresenterOps? = null
    lateinit var autocompleteFragment: AutocompleteSupportFragment
    private val requestFeed = RequestFeed()
    private var search = ""
    private var lat = ""
    private var lng = ""
    private var loadMoreNearby = false
    private var nextNearby = 1
    private var nearByList: ArrayList<ResponseUserData> = ArrayList()
    private var nearByListRecentOnlyAds: ArrayList<ResponseUserData> = ArrayList()
    private var nearByListSwitchs: ArrayList<ResponseUserData> = ArrayList()
    private var nearByListAdminAds: ArrayList<ResponseUserData> = ArrayList()
    private val nearByAdapter = NearByAdapter()
    private var gridLayoutManager: GridLayoutManager? = null
    private var mPresenter: HomeConnector.PresenterOps? = null
    lateinit var loading: ProgressBar
    private val searchAdapter = SearchItemAdapter()
    private var binding: SearchByloactionActivityBinding? = null
    private lateinit var progressBar: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //getIntentData()
        binding = SearchByloactionActivityBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        //setContentView(R.layout.search_byloaction_activity)
        component.inject(this@SearchByLocationActivity)
        setupActionBarSearch(false)

        // Initialize the AutocompleteSupportFragment.
        // Initialize the AutocompleteSupportFragment.
//        autocompleteFragment =
        //          (supportFragmentManager.findFragmentById(R.id.autocomplete_fragment) as AutocompleteSupportFragment?)!!

        gridLayoutManager = GridLayoutManager(this@SearchByLocationActivity, 4)
        loading = findViewById(R.id.pbSearch)

        init()
    }

    fun init() {

        with(binding!!.rvSearch) {
            layoutManager = gridLayoutManager
            adapter = nearByAdapter
            //adapter = searchAdapter
            hasFixedSize()
        }

        addOnScrollListenerNearby()

        binding!!.header.ivBack.setOnClickListener {

            onBackPressed()
        }

        binding!!.header.changeLoc.setOnClickListener {

            autoPlacePicker()
        }

        binding!!.header.locChangeIcon.setOnClickListener {

            autoPlacePicker()
        }

        if (Constants.Search.place != null) {

            requestFeed.searchPlace = "${Constants.Search.place!!.name}"
            search = "${Constants.Search.place!!.name}"
            lat = "${Constants.Search.place!!.latLng!!.latitude}"
            lng = "${Constants.Search.place!!.latLng!!.longitude}"
            binding!!.header.locName.text = "Based on ${Constants.Search.place!!.name}"
            loading.visibility = View.VISIBLE
            nearByList.clear()
            //locationSearch.location_txt.text = search

            val paint = binding!!.header.locName.paint
            val widtht = paint.measureText(binding!!.header.locName.text.toString())
            val textShader: Shader = LinearGradient(
                0f, 0f, widtht, binding!!.header.locName.textSize, intArrayOf(
                    Color.parseColor("#F4447E"), Color.parseColor("#8448F4")

                ), null, Shader.TileMode.REPEAT
            )

            binding!!.header.locName.paint.setShader(textShader)


            nearByListAdminAds.clear()
            nearByListRecentOnlyAds.clear()
            nearByListSwitchs.clear()
            nearByAdapter.clearNearBy()
            nearByAdapter.notifyDataSetChanged()
            nextNearby = 1
            initiateAdminADs()
            nearby()
        }

        /*autocompleteFragment.setPlaceFields(fields)

        autocompleteFragment.setHint("Search Users by City")
        autocompleteFragment.setTypeFilter(TypeFilter.CITIES)

        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                // TODO: Get info about the selected place.
                Log.i("TAG", "Place: " + place.name + ", " + place.id)

                LogManager.logger.i(
                    ArchitectureApp.instance!!.tag,
                    "Place: " + place.name + ", " + place.id + ", Address : " + place.address
                )
                requestFeed.searchPlace = "${place.name}"
                search = "${place.name}"
                lat = "${place.latLng!!.latitude}"
                lng = "${place.latLng!!.longitude}"
                //rlLocationSearch.visibility = View.VISIBLE
                //ivClear.visibility = View.VISIBLE
                //view1.visibility = View.VISIBLE
                //tvLocation.text = search
                //showProgressView(rl_progress)
                loading.visibility = View.VISIBLE
                nearByList.clear()
                //locationSearch.location_txt.text = search

                nearByListAdminAds.clear()
                nearByListRecentOnlyAds.clear()
                nearByListSwitchs.clear()
                nearByAdapter.clearNearBy()
                nearByAdapter?.notifyDataSetChanged()
                nextNearby = 1
                initiateAdminADs()
                nearby()

            }

            override fun onError(status: Status) {
                // TODO: Handle the error.
                Log.i("TAG", "An error occurred: $status")
            }
        })*/

    }

    fun initiateAdminADs() {

        if (null == mPresenter) {
            mPresenter = HomePresenter(presenter)
        }

        mPresenter!!.callRetrofit(ConstantsApi.ADMIN_ADS)
        //   nearByList.clear()
    }

    fun nearby() {

        if ((lat == "0.0") && (lng == "0.0")) {/*tvNoSearch.visibility = View.VISIBLE
            tvNoSearch.text = String.format(
                getString(R.string.text_rational_location), getString(R.string.app_name)
            )*/
            //hideProgressView(rl_progress)
            //checkRequiredPermissions()
            return
        }

        if (null == mPresenter) {
            mPresenter = HomePresenter(presenter)
        }

        run {
            mPresenter!!.addUserFeedObject(requestFeed.apply {
                page = "$nextNearby"
                limit = "32"
                //search = this@SearchByLocationActivity.search
                searchPlace = this@SearchByLocationActivity.search
                currentLat = lat
                currentLong = lng
                lastLoginTimeStamp = "${Constants.getUTCDateTime.getUTCTime()}"
            })
            mPresenter!!.callRetrofit(ConstantsApi.FETCH_NEARBY)

        }
    }

    private fun adjustNearbyUi() {
        when (nearByAdapter.itemCount > 0) {
            true -> {
                //swipeRefreshNearby.isRefreshing = false
                binding!!.tvNoSearch.visibility = View.GONE
                binding!!.rvSearch.visibility = View.VISIBLE
                loading.visibility = View.GONE
            }

            false -> {
                binding!!.tvNoSearch.visibility = View.VISIBLE
                binding!!.rvSearch.visibility = View.GONE
                loading.visibility = View.GONE
            }
        }
    }

    private fun addOnScrollListenerNearby() {
        binding!!.rvSearch.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val total = gridLayoutManager!!.itemCount
                val lastVisibleItemCount = gridLayoutManager!!.findLastVisibleItemPosition()
                var countPro: Int = 0

                //  relative_most_recent.visibility = View.GONE

                val itemPosition: Int = gridLayoutManager!!.findFirstVisibleItemPosition()

                Log.d("pos", itemPosition.toString())

                if (loadMoreNearby) {
                    if (total > 0) if (total - 1 == lastVisibleItemCount) {
//                            loadMoreNearby = false
//                            nearby()
                        // TODO : changed by abhina
                        val user = sharedPreferencesUtil.fetchUserProfile()
//                            when (sharedPreferencesUtil.fetchPremiunNearby()) {
                        when (user.isProMembership) {
                            true -> {
                                loadMoreNearby = false
                                nearby()
                                //become_pro.visibility = View.GONE
                            }

                            false -> {

                                if (nearByListAdminAds.isEmpty()) {
                                    countPro = 72
                                } else {
                                    countPro = 80
                                }

                                if (nearByAdapter.itemCount > countPro) {
                                    //become_pro.visibility = View.VISIBLE
                                } else {
                                    loadMoreNearby = false
                                    nearby()
                                    //become_pro.visibility = View.GONE
                                }

                            }
                        }

                    } else {
                        // become_pro.visibility = View.GONE
                    }
                }
            }
        })
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
                        //Constants.Search.place = place
                        //switchActivityOnly(Constants.Intent.SearchLocation, false)

                        requestFeed.searchPlace = "${place.name}"
                        search = "${place.name}"
                        lat = "${place.latLng!!.latitude}"
                        lng = "${place.latLng!!.longitude}"
                        binding!!.header.locName.text = "Based on ${place.name}"

                        val paint = binding!!.header.locName.paint
                        val widtht = paint.measureText(binding!!.header.locName.text.toString())
                        val textShader: Shader = LinearGradient(
                            0f, 0f, widtht, binding!!.header.locName.textSize, intArrayOf(
                                Color.parseColor("#F4447E"), Color.parseColor("#8448F4")/*Color.parseColor("#64B678"),
                            Color.parseColor("#478AEA"),*/
                                //Color.parseColor("#8446CC")
                            ), null, Shader.TileMode.REPEAT
                        )

                        binding!!.header.locName.paint.setShader(textShader)
                        //rlLocationSearch.visibility = View.VISIBLE
                        //ivClear.visibility = View.VISIBLE
                        //view1.visibility = View.VISIBLE
                        //tvLocation.text = search
                        loading.visibility = View.VISIBLE
                        nearByList.clear()
                        //locationSearch.location_txt.text = search

                        nearByListAdminAds.clear()
                        nearByListRecentOnlyAds.clear()
                        nearByListSwitchs.clear()
                        nearByAdapter.clearNearBy()
                        nearByAdapter.notifyDataSetChanged()
                        nextNearby = 1
                        initiateAdminADs()
                        nearby()
                        //  nearbyOnResult()
                        /* val params = RelativeLayout.LayoutParams(
                             RelativeLayout.LayoutParams.WRAP_CONTENT,
                             RelativeLayout.LayoutParams.WRAP_CONTENT
                         )
                         params.topMargin = resources.getDimensionPixelOffset(R.dimen._48sdp)
                         params.leftMargin = resources.getDimensionPixelOffset(R.dimen._12sdp)
                         fabFilterNew.layoutParams = params*/
                    }
                }
            }
        }
    }
}