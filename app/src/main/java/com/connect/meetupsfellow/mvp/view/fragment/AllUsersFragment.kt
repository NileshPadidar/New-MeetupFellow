package com.connect.meetupsfellow.mvp.view.fragment

import android.app.Activity
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.connect.meetupsfellow.R
import com.connect.meetupsfellow.application.ArchitectureApp
import com.connect.meetupsfellow.components.fragment.CustomFragment
import com.connect.meetupsfellow.constants.Constants
import com.connect.meetupsfellow.constants.ConstantsApi
import com.connect.meetupsfellow.databinding.LayoutAllusersFragBinding
import com.connect.meetupsfellow.databinding.LayoutNearbyNewBinding
import com.connect.meetupsfellow.developement.interfaces.ApiConnect
import com.connect.meetupsfellow.developement.interfaces.FirebaseUtil
import com.connect.meetupsfellow.developement.interfaces.SharedPreferencesUtil
import com.connect.meetupsfellow.log.LogManager
import com.connect.meetupsfellow.mvp.connector.activity.HomeConnector
import com.connect.meetupsfellow.mvp.connector.activity.SearchConnector
import com.connect.meetupsfellow.mvp.presenter.activity.HomePresenter
import com.connect.meetupsfellow.mvp.presenter.activity.SearchPresenter
import com.connect.meetupsfellow.mvp.view.activities.HomeActivity
import com.connect.meetupsfellow.mvp.view.adapter.MostRecentlyJoinedUserAdapter
import com.connect.meetupsfellow.mvp.view.adapter.NearByAdapter
import com.connect.meetupsfellow.mvp.view.adapter.SearchItemAdapter
import com.connect.meetupsfellow.mvp.view.dialog.SearchFilterDialog
import com.connect.meetupsfellow.retrofit.request.RequestFeed
import com.connect.meetupsfellow.retrofit.response.CommonResponse
import com.connect.meetupsfellow.retrofit.response.ResponseUserData
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import java.text.SimpleDateFormat
import javax.inject.Inject

class AllUsersFragment : CustomFragment() {

    @Inject
    lateinit var apiConnect: ApiConnect

    private val refresh = SwipeRefreshLayout.OnRefreshListener {


        swifeRefresh = true
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

    }

    private val presenterS = object : SearchConnector.RequiredViewOps {
        override fun showToast(error: String, logout: Boolean?) {
            when (logout) {
                true -> {
                    logout()
                }

                false -> TODO()
                null -> TODO()
            }
            universalToast(error)
            //pbSearch.visibility = View.GONE
        }

        override fun showResponse(response: CommonResponse, type: ConstantsApi) {

            Log.e("rr++", Gson().toJson(response))

            /// searchAdapter.update(response.search)
            searchAdapter.update(response.nearbyuser)


            updateUi()
            swipeRefresh.isRefreshing = false
            //pbSearch.visibility = View.GONE
        }

    }

    private val presenter = object : HomeConnector.RequiredViewOps {
        override fun showToast(error: String, logout: Boolean?) {
            when (logout) {
                true -> {
                    logout()
                }

                false -> TODO()
                null -> TODO()
            }

            if (error.equals("Trying to get property 'conversationId' of non-object")) {
                //universalToast("")
            } else {
                universalToast(error)
            }

            //hideProgressView(rlProgressBar)
            swipeRefresh.isRefreshing = false
        }

        override fun showResponse(response: CommonResponse, type: ConstantsApi) {
            when (type) {

                ConstantsApi.FETCH_NEARBY -> {
                    when (response.haveNext) {
                        1 -> {
                            loadMoreNearby = true
                            nextNearby = response.nextPage
                        }
                    }

//                    response.nearbyuser.asSequence().forEach {
//                        it.onlineStatus =
//                            when (sharedPreferencesUtil.fetchOtherUserProfile(it.id).onlineStatus) {
//                                true -> 1
//                                false -> 0
//                            }
//                    }
//                    response.nearbyuser.add(ResponseUserData().apply {
//                        item = NearByAdapter.Footer
//                    })
                    //  nearByList?.addAll(response?.nearbyuser)

                    val dummy = linkedSetOf<ResponseUserData>()
                    dummy.addAll(nearByListAdminAds)
                    //    Log.e("sizecheck", nearByListAdminAds.size.toString())
                    nearByListAdminAds.clear()
                    nearByListAdminAds.addAll(dummy)
                    //    Log.e("sizecheck2", nearByListAdminAds.size.toString())
                    //   Log.e("sizecheck3 before ads", response?.nearbyuser.size.toString())

                    //   nearByList.addAll(response?.nearbyuser)


                    //   Log.e("sizecheck4 before ads", response?.nearbyuser.size.toString())
                    response.nearbyuser.let { nearByListRecentOnlyAds.addAll(it) }
                    response.nearbyuser.let { nearByListSwitchs.addAll(it) }

                    if (nearByListAdminAds.isEmpty()) {

                        response.nearbyuser.let { nearByList.addAll(it) }

                        /*if (switch_online.isChecked) {
                            nearByAdapter.updateNearBy(filterRecentlyOnline(nearByList))
                        } else {
                            nearByAdapter.updateNearBy(nearByList)
                        }*/
                        nearByAdapter.updateNearBy(nearByList)

                    } else {


                        /*if (switch_online.isChecked) {
                            //    Log.e("switch check ed", "switch")
                            nearByAdapter.updateNearBy(filterRecentlyOnline(nearByListRecentOnlyAds))*/
                        // } else {
                        //      Log.e("switch check", "switch")
                        //nearByListAdminAds
                        //      Log.e("sizecheck4 before ads", response?.nearbyuser.size.toString())
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
                    }


                    adjustNearbyUi()
                    swipeRefresh.isRefreshing = false

                    /*if (null != menu) {
                        setCount(this@HomeActivity, sharedPreferencesUtil.fetchNotificationCount())
                    }*/
//                    setBadge(this@HomeActivity, sharedPreferencesUtil.fetchNotificationCount().toInt())
                }

                ConstantsApi.GET_MOST_RECENTLY_JOIND_USER -> {

                    when (response.haveNext) {
                        1 -> {
                            loadMoreRecent = true
                            nextRecent = response.nextPage
                        }
                    }



                    mostRecentUserList = ArrayList()
                    response.nearbyuser.let { mostRecentUserList.addAll(it) }
                    if (mostRecentUserList.size > 0) mostRecentUserList.removeAt(0)
                    mostRecentlyJoinedUserAdapter.updateNearBy(mostRecentUserList)

                    //adjustNearbyUi()
                    // swipeRefreshNearby.isRefreshing = false

//                    if (null != menu) {
//                        setCount(this@HomeActivity, sharedPreferencesUtil.fetchNotificationCount())
//                    }
//                    setBadge(this@HomeActivity, sharedPreferencesUtil.fetchNotificationCount().toInt())
                }

                ConstantsApi.ADMIN_ADS -> {
                    //   nearByListAdminAds = ArrayList()
                    //  nearByListAdminAds.clear()


                    if (!response.adminAdvertisements.isNullOrEmpty()) {

                        sharedPreferencesUtil.saveAdminAdsCount(response.adminAdvertisements.size.toString())

                        gridLayoutManager?.spanSizeLookup =
                            object : GridLayoutManager.SpanSizeLookup() {

                                override fun getSpanSize(position: Int): Int {
                                    return if (position < (response.adminAdvertisements.size)?.times(
                                            17
                                        ) ?: 0
                                    ) {
                                        if ((position + 1) % 17 == 0 && (position + 1) != 1) { // totalRowCount : How many item you want to show
                                            4
                                        } else 1
                                    } else 1
                                }
                            }


                        response.adminAdvertisements.let { nearByListAdminAds.addAll(it) }
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

                else -> {}

            }
        }
    }

    @Inject
    lateinit var sharedPreferencesUtil: SharedPreferencesUtil

    @Inject
    lateinit var firebaseUtil: FirebaseUtil

    private var mPresenter: HomeConnector.PresenterOps? = null

    private var loadMoreFeed = false
    private var loadMoreNearby = false
    private var loadMoreRecent = false
    private var nextNearby = 1
    private var nextRecent = 1
    private var nearByList: ArrayList<ResponseUserData> = ArrayList()
    private var nearByListRecentOnlyAds: ArrayList<ResponseUserData> = ArrayList()
    private var nearByListSwitchs: ArrayList<ResponseUserData> = ArrayList()

    private var mostRecentUserList: ArrayList<ResponseUserData> = ArrayList()
    private var recentlyOnlineList: ArrayList<ResponseUserData> = ArrayList()
    private var nearByListAdminAds: ArrayList<ResponseUserData> = ArrayList()

    private val nearByAdapter = NearByAdapter()
    private val mostRecentlyJoinedUserAdapter = MostRecentlyJoinedUserAdapter()

    private var linearLayoutManager: LinearLayoutManager? = null
    private var linearLayoutManagerRecent: LinearLayoutManager? = null
    private var gridLayoutManager: GridLayoutManager? = null

    private val requestFeed = RequestFeed()
    private var canRefresh = false
    private var swifeRefresh = false

    private var lat = ""
    private var lng = ""
    private var search = ""
    private var next = 1

    private var mPresenterS: SearchConnector.PresenterOps? = null
    lateinit var rvNearby: RecyclerView
    lateinit var rvMostRecent: RecyclerView
    lateinit var swipeRefresh: SwipeRefreshLayout
    lateinit var fabFilterNew: FloatingActionButton
    lateinit var home: HomeActivity
    lateinit var rlLocationSearch: RelativeLayout
    lateinit var view1: View
    lateinit var tvLocation: TextView
    private val searchAdapter = SearchItemAdapter()
    lateinit var ivClear: ImageView
    lateinit var tvNoSearch: TextView
    lateinit var rvSearch: RecyclerView
    lateinit var search_rv_lay: LinearLayout
    lateinit var tvNoNearby: TextView
    private var _binding: LayoutAllusersFragBinding? = null
    private val binding get() = _binding!!
    private lateinit var nearbyNewBinding: LayoutNearbyNewBinding
    private lateinit var rlProgressBar: LinearLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        ArchitectureApp.component!!.inject(this@AllUsersFragment)
        // val view = inflater.inflate(R.layout.layout_allusers_frag, container, false)
        _binding = LayoutAllusersFragBinding.inflate(inflater, container, false)
        return binding.root
        // return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        home = activity as HomeActivity
        nearbyNewBinding = home.binding!!.includedLayHome.includedLayNearby
        rlProgressBar = home.binding!!.includedLayHome.includedProgress.rlProgress

        swipeRefresh = view.findViewById(R.id.swipeRefreshNearby1)
        rvNearby = view.findViewById(R.id.rvNearby)
        rvMostRecent = view.findViewById(R.id.rvMostRecent)
        fabFilterNew = view.findViewById(R.id.fabFilterNew)
        swipeRefresh.setOnRefreshListener(refresh)
        rlLocationSearch = view.findViewById(R.id.rlLocationSearch)
        view1 = view.findViewById(R.id.view1)
        tvLocation = view.findViewById(R.id.tvLocation)
        ivClear = view.findViewById(R.id.ivClear)
        rvSearch = view.findViewById(R.id.rvSearch)
        tvNoSearch = view.findViewById(R.id.tvNoSearch)
        search_rv_lay = view.findViewById(R.id.search_rv_lay)
        tvNoNearby = view.findViewById(R.id.tvNoNearbyUser)

        setHasOptionsMenu(true)

        mPresenter = HomePresenter(presenter)

        val location = sharedPreferencesUtil.fetchLocation()
        lat = "${location.latitude}"
        lng = "${location.longitude}"

        initiateNearByUI()
        initiateAdminADs()
        initiateMostRecentUI()
        mostRecentlyJoindUser()

        fabFilterNew.setOnClickListener {
            if ((lat == "0.0") && (lng == "0.0")) {/*tvNoLocation.visibility = View.VISIBLE
                tvNoLocation.text = String.format(
                    getString(R.string.text_rational_location), getString(R.string.app_name)
                )*/
                //hideProgressView(rl_progress)
                //checkRequiredPermissions()
                return@setOnClickListener
            }

            SearchFilterDialog.Builder(requireActivity(), sharedPreferencesUtil.fetchUserProfile())
                .feeds(requestFeed).build().setOnDismissListener {
                    //showProgressView(rl_progress)
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
                            myIdentity.isEmpty() && intoIdentity.isEmpty() && gender.isEmpty() && partnerStatus.isEmpty() && generalIntercourse.isEmpty() && fistingId.isEmpty() -> fabFilterNew.backgroundTintList =
                                ColorStateList.valueOf(Color.parseColor("#ffffff"))

                            else -> fabFilterNew.backgroundTintList =
                                ColorStateList.valueOf(Color.parseColor("#ffffff"))
                        }
                    }
                    initiateAdminADs()
                    nearby()
                    LogManager.logger.i(
                        ArchitectureApp.instance!!.tag, "Selected : ${Gson().toJson(requestFeed)}"
                    )
                }
        }

        with(rvSearch) {
            layoutManager = GridLayoutManager(requireContext(), 4)
            adapter = searchAdapter
            hasFixedSize()/*addItemDecoration(
                DividerItemDecoration(rvSearch.context, DividerItemDecoration.VERTICAL)
            )*/
        }


        nearbyNewBinding.nearBySearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                //nearByAdapter.filter.filter(s)

                if (nearbyNewBinding.nearBySearch.text.isNotEmpty()) {

                    nearbyNewBinding.nearByClear.visibility = View.VISIBLE
                }/*else {

                    home.nearBy_clear.visibility = View.GONE
                }*/

                if (nearbyNewBinding.nearBySearch.text.length > 2) {

                    searchAdapter.clearAll()
                    swipeRefresh.isRefreshing = true
                    //pbSearch.visibility = View.VISIBLE
                    globalSearch()

                    //Toast.makeText(requireContext(), "search", Toast.LENGTH_SHORT).show()

                } else {
                    searchAdapter.clearAll()
                    search_rv_lay.visibility = View.GONE
                    swipeRefresh.visibility = View.VISIBLE
                    fabFilterNew.visibility = View.VISIBLE
                    //home.nearBy_clear.visibility = View.GONE
                }

            }

            override fun afterTextChanged(s: Editable?) {

            }

        })

        /* nearbyNewBinding.nearBySearch.setOnEditorActionListener(object : TextView.OnEditorActionListener{
            override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {



                when (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    true -> {
                        if ( nearbyNewBinding.nearBySearch.text.isEmpty()) {
                            universalToast("Nothing to searchFavoriteUser")
                            return true
                        } else if ( nearbyNewBinding.nearBySearch.text.length > 2) {
                            searchAdapter.clearAll()
                            //pbSearch.visibility = View.VISIBLE
                            swipeRefresh.isRefreshing = true
                            globalSearch()
                        }
                    }
                }
                return false
            }
        })*/

        nearbyNewBinding.nearByClear.setOnClickListener {

            swipeRefresh.isRefreshing = true
            refresh.onRefresh()
            nearbyNewBinding.nearBySelect.text = "All"
            fabFilterNew.visibility = View.VISIBLE
            nearbyNewBinding.nearByClear.visibility = View.GONE

            val location = sharedPreferencesUtil.fetchLocation()
            search = ""
            requestFeed.searchPlace = ""
            lat = "${location.latitude}"
            lng = "${location.longitude}"
            //rlLocationSearch.visibility = View.GONE
            //view1.visibility = View.GONE
            //tvLocation.text = search
            nearbyNewBinding.nearBySearch.setText("")
            // showProgressView(rl_progress)

            nearByListRecentOnlyAds.clear()
            nearByListSwitchs.clear()

            nearByList.clear()
            nearByListAdminAds.clear()
            nearByAdapter.clearNearBy()
            nextNearby = 1

            initiateAdminADs()
            nearby()

        }
        nearbyNewBinding.nearBySelector.setOnClickListener {
            autoPlacePicker()
        }
    }

    private fun initiateMostRecentUI() {
        linearLayoutManagerRecent = LinearLayoutManager(
            requireContext(), RecyclerView.HORIZONTAL, false
        )
        with(rvMostRecent) {
            layoutManager = linearLayoutManagerRecent
            adapter = mostRecentlyJoinedUserAdapter
            hasFixedSize()
        }

        addOnScrollListenerRecent()

    }

    private fun addOnScrollListenerRecent() {
        rvMostRecent.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val total = linearLayoutManagerRecent!!.itemCount
                val lastVisibleItemCount = linearLayoutManagerRecent!!.findLastVisibleItemPosition()

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
        gridLayoutManager = GridLayoutManager(requireContext(), 4)

        /*gridLayoutManager?.setSpanSizeLookup(object : GridLayoutManager.SpanSizeLookup() {

            override fun getSpanSize(position: Int): Int {
                return if ((position + 1) % 17 == 0 && (position + 1) != 1) { // totalRowCount : How many item you want to show
                    4
                } else 1
            }
        })*/



        with(rvNearby) {
            layoutManager = gridLayoutManager
            adapter = nearByAdapter
            hasFixedSize()
        }

        addOnScrollListenerNearby()
        nearby()
    }

    private fun autoPlacePicker() {

        // nearByList.clear()
        /**
         * Initialize Places. For simplicity, the API key is hard-coded. In a production
         * environment we recommend using a secure mechanism to manage API keys.
         */

        if (!Places.isInitialized()) {
            Places.initialize(home.applicationContext, getString(R.string.google_server_key))
        }

        // Set the fields to specify which types of place data to return.
        val fields =
            listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS)

        // Start the autocomplete intent.


        val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
            .setHint("Search Users by City").setTypeFilter(TypeFilter.CITIES)
            .build(home.applicationContext)
        startActivityForResult(intent, Constants.RequestCode.AUTOCOMPLETE_REQUEST_CODE)
    }

    private fun addOnScrollListenerNearby() {
        rvNearby.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val total = gridLayoutManager!!.itemCount
                val lastVisibleItemCount = gridLayoutManager!!.findLastVisibleItemPosition()
                var countPro: Int = 0

                //  relative_most_recent.visibility = View.GONE

                //to avoid multiple calls to loadMore() method
                //maintain a boolean value (isLoading). if loadMore() task started set to true and completes set to false
                if (loadMoreNearby) {
                    if (total > 0) if (total - 1 == lastVisibleItemCount) {
//                            loadMoreNearby = false
//                            nearby()
                        // TODO : changed by abhinav
                        val user = sharedPreferencesUtil.fetchUserProfile()
//                            when (sharedPreferencesUtil.fetchPremiunNearby()) {
                        when (user.isProMembership) {
                            true -> {
                                loadMoreNearby = false
                                nearby()
                                binding.includedBecomePro.rlPro.visibility = View.GONE
                            }

                            false -> {

                                if (nearByListAdminAds.isEmpty()) {
                                    countPro = 72
                                } else {
                                    countPro = 80
                                }

                                if (nearByAdapter.itemCount > countPro) {
                                    binding.includedBecomePro.rlPro.visibility = View.VISIBLE
                                } else {
                                    loadMoreNearby = false
                                    nearby()
                                    binding.includedBecomePro.rlPro.visibility = View.GONE
                                }

//                                    if (nearByAdapter.itemCount < 32) {
//                                        loadMoreNearby = false
//                                        nearby()
//                                        become_pro.visibility = View.GONE
//                                    } //else if (nearByAdapter.itemCount == 32) {
//                                    else if (nearByAdapter.itemCount == 80) {
//                                        become_pro.visibility = View.VISIBLE
//                                    }
                            }
                        }

                    } else {
                        binding.includedBecomePro.rlPro.visibility = View.GONE
                    }
                }
            }
        })
    }

    private fun initiateAdminADs() {
        mPresenter!!.callRetrofit(ConstantsApi.ADMIN_ADS)
        //   nearByList.clear()
    }

    private fun mostRecentlyJoindUser() {
        if ((lat == "0.0") && (lng == "0.0")) {/*tvNoLocation.visibility = View.VISIBLE
            tvNoLocation.text = String.format(
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
                //  if ()
                page = "$nextRecent"
                limit = "32"
                currentLat = lat
                currentLong = lng
                lastLoginTimeStamp = "${Constants.getUTCDateTime.getUTCTime()}"
            })
            mPresenter!!.callRetrofit(ConstantsApi.GET_MOST_RECENTLY_JOIND_USER)

        }
    }

    private fun nearby() {

        if ((lat == "0.0") && (lng == "0.0")) {/*tvNoLocation.visibility = View.VISIBLE
            tvNoLocation.text = String.format(
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
                currentLat = lat
                currentLong = lng
                lastLoginTimeStamp = "${Constants.getUTCDateTime.getUTCTime()}"
            })
            mPresenter!!.callRetrofit(ConstantsApi.FETCH_NEARBY)

        }
    }

    fun filterRecentlyOnline(nearBy: ArrayList<ResponseUserData>): ArrayList<ResponseUserData> {
        var onlineList: ArrayList<ResponseUserData> = ArrayList()
        var onlineListWithAds: ArrayList<ResponseUserData> = ArrayList()
        for (i in 0 until nearBy.size) {
            if (nearBy[i].onlineStatus == 1) {
//ss recent 4
                if (nearBy[i].lastLoginTimeStamp != "") {
                    try {
                        val format = SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
                        var lastDate = format.parse(nearBy[i].lastLoginTimeStamp)
                        var currentUTCTime = format.parse(Constants.getUTCDateTime.getUTCTime())
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
        for (j in 0 until onlineList.size) {
            if ((j) % 16 == 0 && j != 0 && (j) / 16 <= nearByListAdminAds.size) {
                local = ((j) / 16)
                if (nearByListAdminAds.size < local) {
                    onlineListWithAds.add(nearByListAdminAds.get(0))
                } else {
                    onlineListWithAds.add(nearByListAdminAds.get(local - 1))
                }
            }
            onlineListWithAds.add(onlineList.get(j))
        }
        return onlineListWithAds
    }

    private fun adjustNearbyUi() {
        when (nearByAdapter.itemCount > 0) {
            true -> {
                tvNoNearby.visibility = View.GONE
                rvNearby.visibility = View.VISIBLE
            }

            false -> {
                tvNoNearby.visibility = View.VISIBLE
                rvNearby.visibility = View.GONE
            }
        }
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
                        requestFeed.searchPlace = "${place.name}"
                        search = "${place.name}"
                        lat = "${place.latLng!!.latitude}"
                        lng = "${place.latLng!!.longitude}"
                        //rlLocationSearch.visibility = View.VISIBLE
                        //view1.visibility = View.VISIBLE
                        //tvLocation.text = search
                        nearbyNewBinding.nearByClear.visibility = View.VISIBLE
                        nearbyNewBinding.nearBySelect.text = search
                        // nearbyNewBinding.nearBySearch.setText(search)
                        //home.showProgressView(rl_progress)
                        nearByList.clear()

                        nearByListAdminAds.clear()
                        nearByListRecentOnlyAds.clear()
                        nearByListSwitchs.clear()
                        nearByAdapter.clearNearBy()
                        nearByAdapter.notifyDataSetChanged()
                        nextNearby = 1
                        initiateAdminADs()
                        nearby()
                        //  nearbyOnResult()
                        /*val params = RelativeLayout.LayoutParams(
                            RelativeLayout.LayoutParams.WRAP_CONTENT,
                            RelativeLayout.LayoutParams.WRAP_CONTENT
                        )
                        params.topMargin = resources.getDimensionPixelOffset(R.dimen._48sdp)
                        params.leftMargin = resources.getDimensionPixelOffset(R.dimen._12sdp)
                        fabFilterNew.layoutParams = params*/
                    }
                }
            }

            AutocompleteActivity.RESULT_ERROR -> {
                val status = Autocomplete.getStatusFromIntent(data!!)
                LogManager.logger.i(ArchitectureApp.instance!!.tag, "${status.statusMessage}")
                universalToast("${status.statusMessage}")
                //uploadMessage = null
            }
        }
    }

    private fun updateUi() {
        when (searchAdapter.itemCount > 0) {

            true -> {
                swipeRefresh.visibility = View.GONE
                search_rv_lay.visibility = View.VISIBLE
                fabFilterNew.visibility = View.GONE
                tvNoSearch.visibility = View.GONE
                rvSearch.visibility = View.VISIBLE
            }

            false -> {
                swipeRefresh.visibility = View.GONE
                search_rv_lay.visibility = View.VISIBLE
                tvNoSearch.visibility = View.VISIBLE
                rvSearch.visibility = View.GONE
            }
        }
    }

    private fun globalSearch() {
        if (null == mPresenterS) mPresenterS = SearchPresenter(presenterS)

        run {
            mPresenterS!!.addSearchObject(RequestFeed().apply {
                page = "$next"
                limit = "10"
                search = nearbyNewBinding.nearBySearch.text.toString()
                currentLat = "${sharedPreferencesUtil.fetchLocation().latitude}"
                currentLong = "${sharedPreferencesUtil.fetchLocation().longitude}"
            })
            mPresenterS!!.callRetrofit(ConstantsApi.SEARCH)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {

            R.id.menu_location -> {

                Log.d("userFrag", "click")

                if ((lat == "0.0") && (lng == "0.0")) {/*tvNoLocation.visibility = View.VISIBLE
                    tvNoLocation.text = String.format(
                        getString(R.string.text_rational_location), getString(R.string.app_name)
                    )*/
                    hideProgressView(rlProgressBar)
                    //home.checkRequiredPermissions()
                } else {
                    autoPlacePicker()
                }

            }
        }

        return super.onOptionsItemSelected(item)
    }

}