package com.connect.meetupsfellow.mvp.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.connect.meetupsfellow.application.ArchitectureApp
import com.connect.meetupsfellow.components.fragment.CustomFragment
import com.connect.meetupsfellow.constants.ConstantsApi
import com.connect.meetupsfellow.databinding.LayoutFavoriteEventBinding
import com.connect.meetupsfellow.developement.interfaces.SharedPreferencesUtil
import com.connect.meetupsfellow.global.interfaces.RecyclerViewClickListener
import com.connect.meetupsfellow.mvp.connector.fragment.FavoriteEventConnector
import com.connect.meetupsfellow.mvp.presenter.fragment.FavoriteEventPresenter
import com.connect.meetupsfellow.mvp.view.adapter.FavoriteEventAdapter
import com.connect.meetupsfellow.retrofit.request.RequestFeed
import com.connect.meetupsfellow.retrofit.response.CommonResponse
import com.google.android.gms.maps.model.LatLng
import javax.inject.Inject

class FavoriteEventFragment : CustomFragment() {

    private val refresh = SwipeRefreshLayout.OnRefreshListener {
        next = 1
        favoriteEvent()
    }

    private val clickListener = object : RecyclerViewClickListener {
        override fun onClick(view: View, position: Int) {
            val id = favoriteEventAdapter!!.getEventId(position)
            when (id.isNotEmpty()) {
                true -> {
                    this@FavoriteEventFragment.eventId = id
                    this@FavoriteEventFragment.position = position
                    showProgressView(rlProgressBar)
                    markUnfavoriteEvent()
                }

                false -> universalToast("Event not found")
            }
        }
    }

    private val presenter = object : FavoriteEventConnector.RequiredViewOps {
        override fun showToast(error: String, logout: Boolean?) {
            when (logout) {
                true -> {
                    logout()
                }

                false -> TODO()
                null -> TODO()
            }
            universalToast(error)
            if (null != binding.swipeRefreshEvent) binding.swipeRefreshEvent.isRefreshing = false
            hideProgressView(rlProgressBar)
        }

        override fun showResponse(response: CommonResponse, type: ConstantsApi) {
            when (type) {
                ConstantsApi.FAVORITE_EVENT -> {
                    when (response.haveNext) {
                        1 -> {
                            loadMore = true
                            next = response.nextPage
                        }
                    }
                    favoriteEventAdapter!!.clear()
                    favoriteEventAdapter!!.update(response.favoriteEvents)
                }

                ConstantsApi.EVENT_LIKE -> {
                    favoriteEventAdapter!!.removeIndex(position)
                    eventId = ""
                    position = -1
                }

                else -> {

                }
            }

            adjustUi()
            binding.swipeRefreshEvent.isRefreshing = false
            hideProgressView(rlProgressBar)
        }
    }

    private var favoriteEventAdapter: FavoriteEventAdapter? = null

    private var mPresenter: FavoriteEventConnector.PresenterOps? = null

    @Inject
    lateinit var sharedPreferencesUtil: SharedPreferencesUtil

    private var eventId = ""
    private var position = -1
    private var loadMore = false
    private var next = 1
    private var location: LatLng? = null

    private var linearLayoutManager: androidx.recyclerview.widget.LinearLayoutManager? = null

    private var _binding: LayoutFavoriteEventBinding? = null
    private val binding get() = _binding!!
    private lateinit var rlProgressBar: LinearLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        ArchitectureApp.component!!.inject(this@FavoriteEventFragment)
        //   return inflater.inflate(R.layout.layout_favorite_event, container, false)
        _binding = LayoutFavoriteEventBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        rlProgressBar = binding.includedLoading.rlProgress
        location = sharedPreferencesUtil.fetchLocation()
        favoriteEventAdapter = FavoriteEventAdapter(clickListener)
        binding.swipeRefreshEvent.setOnRefreshListener(refresh)
        linearLayoutManager = androidx.recyclerview.widget.LinearLayoutManager(
            requireActivity(), androidx.recyclerview.widget.RecyclerView.VERTICAL, false
        )
        with(binding.rvFavoriteEvents) {
            layoutManager = linearLayoutManager
            adapter = favoriteEventAdapter
        }
        addOnScrollListener()
        showProgressView(rlProgressBar)
        favoriteEvent()
    }

    private fun addOnScrollListener() {
        binding.rvFavoriteEvents.addOnScrollListener(object :
            androidx.recyclerview.widget.RecyclerView.OnScrollListener() {
            override fun onScrolled(
                recyclerView: androidx.recyclerview.widget.RecyclerView, dx: Int, dy: Int
            ) {
                super.onScrolled(recyclerView, dx, dy)

//                if (dy > 0) {
                val total = linearLayoutManager!!.itemCount
                val lastVisibleItemCount = linearLayoutManager!!.findLastVisibleItemPosition()

                //to avoid multiple calls to loadMore() method
                //maintain a boolean value (isLoading). if loadMore() task started set to true and completes set to false
                if (loadMore) {
                    if (total > 0) if (total - 1 == lastVisibleItemCount) {
                        loadMore = false
                        favoriteEvent()
                    }
                }
//                }
            }
        })
    }

    private fun adjustUi() {
        when (favoriteEventAdapter!!.itemCount > 0) {
            true -> {
                binding.tvNoFavorite.visibility = View.GONE
                binding.rvFavoriteEvents.visibility = View.VISIBLE
            }

            false -> {
                binding.tvNoFavorite.visibility = View.VISIBLE
                binding.rvFavoriteEvents.visibility = View.GONE
            }
        }
    }

    internal fun favoriteEvent() {
        if (null == mPresenter) mPresenter = FavoriteEventPresenter(presenter)

        run {
            mPresenter!!.addFavoriteEventObject(RequestFeed().apply {
                page = "$next"
                limit = "10"
                currentLat = if (location != null) "${location!!.latitude}" else ""
                currentLong = if (location != null) "${location!!.longitude}" else ""
            })
            mPresenter!!.callRetrofit(ConstantsApi.FAVORITE_EVENT)
        }
    }

    private fun markUnfavoriteEvent() {
        if (null == mPresenter) mPresenter = FavoriteEventPresenter(presenter)

        run {
            mPresenter!!.addLikeEventObject(eventId)
            mPresenter!!.callRetrofit(ConstantsApi.EVENT_LIKE)
        }
    }
}