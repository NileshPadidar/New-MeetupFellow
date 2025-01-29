package com.connect.meetupsfellow.mvp.view.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.recyclerview.widget.GridLayoutManager
import com.connect.meetupsfellow.R
import com.connect.meetupsfellow.application.ArchitectureApp
import com.connect.meetupsfellow.components.activity.CustomAppActivityCompatViewImpl
import com.connect.meetupsfellow.constants.Constants
import com.connect.meetupsfellow.constants.ConstantsApi
import com.connect.meetupsfellow.databinding.ActivitySearchBinding
import com.connect.meetupsfellow.developement.interfaces.SharedPreferencesUtil
import com.connect.meetupsfellow.log.LogManager
import com.connect.meetupsfellow.mvp.connector.activity.SearchConnector
import com.connect.meetupsfellow.mvp.presenter.activity.SearchPresenter
import com.connect.meetupsfellow.mvp.view.adapter.SearchItemAdapter
import com.connect.meetupsfellow.retrofit.request.RequestFeed
import com.connect.meetupsfellow.retrofit.response.CommonResponse
import com.connect.meetupsfellow.retrofit.response.ResponseSearch
import com.google.gson.Gson
import java.util.Locale
import javax.inject.Inject

class SearchActivity : CustomAppActivityCompatViewImpl() {

    private val presenter = object : SearchConnector.RequiredViewOps {
        override fun showToast(error: String, logout: Boolean?) {
            when (logout) {
                true -> {
                    logout()
                }

                false -> {}
                null -> {}
            }
            universalToast(error)
            binding!!.pbSearch.visibility = View.GONE
        }

        override fun showResponse(response: CommonResponse, type: ConstantsApi) {

            Log.e("rr++", Gson().toJson(response))

            when (type) {
                ConstantsApi.FAVORITE_USER -> {
                    val search = arrayListOf<ResponseSearch>()/* response.favoriteUsers.asSequence().forEach {
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

                ConstantsApi.FAVORITE_EVENT -> {
                    val search = arrayListOf<ResponseSearch>()
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
                    ///  searchAdapter.update(search)
                }

                else -> {
                    searchAdapter.update(response.nearbyuser)
                }
            }
            updateUi()
            binding!!.pbSearch.visibility = View.GONE
        }

    }

    @Inject
    lateinit var sharedPreferencesUtil: SharedPreferencesUtil
    private var mPresenter: SearchConnector.PresenterOps? = null

    private var next = 1
    private var search = ""
    private var selected = -1
    private val searchAdapter = SearchItemAdapter()
    private var binding: ActivitySearchBinding? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getIntentData()
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        //setContentView(R.layout.activity_search)
        component.inject(this@SearchActivity)
        setupActionBarSearch(false)
        Log.e("SearchActivity*", "Search_By_name")
        init()
    }

    private fun getIntentData() {
        when (intent.hasExtra(Constants.IntentDataKeys.Selected)) {
            true -> {
                selected = intent.getIntExtra(Constants.IntentDataKeys.Selected, -1)
                Log.e("SearchActivity*", "selected-: $selected ")
            }

            false -> {}
        }
    }

    private fun init() {

        binding!!.header.ivCross.visibility = View.GONE

        binding!!.header.etSearch.requestFocus()

        binding!!.header.ivBack.setOnClickListener { onBackPressed() }

        when (selected) {
            Constants.Search.NearBy, Constants.Search.Chat, Constants.Search.User -> binding!!.header.etSearch.setHint(
                R.string.text_search_user
            )

            Constants.Search.Event, Constants.Search.Favorite -> binding!!.header.etSearch.setHint(R.string.text_search_event)
        }

        binding!!.header.etSearch.afterTextChanged { search ->
            this@SearchActivity.search = search

            if (search.isNotEmpty()) {
                binding!!.header.ivCross.visibility = View.VISIBLE
            } else {
                binding!!.header.ivCross.visibility = View.GONE
            }

            if (search.length > 2) {
                searchAdapter.clearAll()
                binding!!.pbSearch.visibility = View.VISIBLE
                when (selected) {
                    Constants.Search.NearBy -> globalSearch()
                    Constants.Search.User -> searchFavoriteUser()
                    Constants.Search.Event -> searchEvent()
                    Constants.Search.Favorite -> searchFavoriteEvent()
                    Constants.Search.Chat -> searchConversation()
                }
            } else if (search.isEmpty()) {
                searchAdapter.clearAll()
            }
        }

        binding!!.header.etSearch.setOnEditorActionListener { _, actionId, _ ->
            binding!!.header.etSearch.clearFocus()
            hideKeyboard(binding!!.header.etSearch)
            when (actionId == EditorInfo.IME_ACTION_SEARCH) {
                true -> {
                    if (search.isEmpty()) {
                        universalToast("Nothing to searchFavoriteUser")
                        return@setOnEditorActionListener true
                    } else if (search.length > 2) {
                        searchAdapter.clearAll()
                        binding!!.pbSearch.visibility = View.VISIBLE
                        when (selected) {
                            Constants.Search.NearBy -> globalSearch()
                            Constants.Search.User -> searchFavoriteUser()
                            Constants.Search.Event -> searchEvent()
                            Constants.Search.Favorite -> searchFavoriteEvent()
                            Constants.Search.Chat -> searchConversation()
                        }
                    }
                }

                false -> {}
            }
            return@setOnEditorActionListener false
        }

        binding!!.header.ivCross.setOnClickListener {
            if (binding!!.header.etSearch.text.isNotEmpty()) binding!!.header.etSearch.text.clear()
            searchAdapter.clearAll()
            hideKeyboard(binding!!.header.etSearch)
        }

        with(binding!!.rvSearch) {
            layoutManager = GridLayoutManager(
                this@SearchActivity, 4
            )
            adapter = searchAdapter/*addItemDecoration(
                DividerItemDecoration(rvSearch.context, DividerItemDecoration.VERTICAL)
            )*/
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

        checkRequiredPermissions()
    }

    private fun searchConversation() {
        val user = sharedPreferencesUtil.fetchConversation()
        val searchUser = arrayListOf<ResponseSearch>()
        user.asSequence().forEach {
            when (it.otherUserName.lowercase(Locale.getDefault()).contains(search.lowercase(Locale.getDefault()))) {
                true -> {
                    val searchedUser = Gson().fromJson<ResponseSearch>(
                        Gson().toJson(it), ResponseSearch::class.java
                    )
                    val profile = sharedPreferencesUtil.fetchOtherUserProfile(searchedUser.search)
                    searchUser.add(searchedUser.apply {
                        type = Constants.View.Conversation
                        conversation = it
                        userImage = profile.imgUrl
                        profileStatus = "${profile.onlineStatus}"
                        userId = "${if (profile.userId == -1) profile.userId else profile.status}"
                    })
                }

                false -> {}
            }
        }
        ///   searchAdapter.update(searchUser)
        binding!!.pbSearch.visibility = View.GONE
        updateUi()
    }

    private fun searchFavoriteUser() {
        if (null == mPresenter) mPresenter = SearchPresenter(presenter)

        run {
            mPresenter!!.addSearchObject(RequestFeed().apply {
                page = "$next"
                limit = "10"
                search = this@SearchActivity.search
                currentLat = "${sharedPreferencesUtil.fetchLocation().latitude}"
                currentLong = "${sharedPreferencesUtil.fetchLocation().longitude}"
            })
            mPresenter!!.callRetrofit(ConstantsApi.FAVORITE_USER)
        }
    }

    private fun searchFavoriteEvent() {
        if (null == mPresenter) mPresenter = SearchPresenter(presenter)

        run {
            mPresenter!!.addSearchObject(RequestFeed().apply {
                page = "$next"
                limit = "10"
                search = this@SearchActivity.search
                currentLat = "${sharedPreferencesUtil.fetchLocation().latitude}"
                currentLong = "${sharedPreferencesUtil.fetchLocation().longitude}"
            })
            mPresenter!!.callRetrofit(ConstantsApi.FAVORITE_EVENT)
        }
    }

    private fun globalSearch() {
        if (null == mPresenter) mPresenter = SearchPresenter(presenter)
        Log.e("SearchActivity*", "globalSearch_Api")
        run {
            mPresenter!!.addSearchObject(RequestFeed().apply {
                page = "$next"
                limit = "10"
                search = this@SearchActivity.search
                currentLat = "${sharedPreferencesUtil.fetchLocation().latitude}"
                currentLong = "${sharedPreferencesUtil.fetchLocation().longitude}"
            })
            mPresenter!!.callRetrofit(ConstantsApi.SEARCH)
        }
    }

    private fun searchEvent() {
        if (null == mPresenter) mPresenter = SearchPresenter(presenter)

        run {
            mPresenter!!.addSearchObject(RequestFeed().apply {
                page = "$next"
                limit = "10"
                search = this@SearchActivity.search
                currentLat = "${sharedPreferencesUtil.fetchLocation().latitude}"
                currentLong = "${sharedPreferencesUtil.fetchLocation().longitude}"
            })
            mPresenter!!.callRetrofit(ConstantsApi.SEARCH_EVENTS)
        }
    }

    private fun updateUi() {
        when (searchAdapter.itemCount > 0) {
            true -> {
                binding!!.tvNoSearch.visibility = View.GONE
                binding!!.ivNoUserFoundSearch.visibility = View.GONE
                binding!!.rvSearch.visibility = View.VISIBLE
            }

            false -> {
                binding!!.tvNoSearch.visibility = View.VISIBLE
                binding!!.ivNoUserFoundSearch.visibility = View.VISIBLE
                binding!!.rvSearch.visibility = View.GONE
            }
        }
    }
}