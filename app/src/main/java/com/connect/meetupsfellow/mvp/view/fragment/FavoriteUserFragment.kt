package com.connect.meetupsfellow.mvp.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.connect.meetupsfellow.application.ArchitectureApp
import com.connect.meetupsfellow.components.fragment.CustomFragment
import com.connect.meetupsfellow.constants.ConstantsApi
import com.connect.meetupsfellow.databinding.LayoutFavoriteUserBinding
import com.connect.meetupsfellow.developement.interfaces.SharedPreferencesUtil
import com.connect.meetupsfellow.global.interfaces.RecyclerViewClickListener
import com.connect.meetupsfellow.mvp.connector.fragment.FavoriteUserConnector
import com.connect.meetupsfellow.mvp.presenter.fragment.FavoriteUserPresenter
import com.connect.meetupsfellow.mvp.view.adapter.FavoritePeopleAdapter
import com.connect.meetupsfellow.retrofit.request.MyFavoriteUserReq
import com.connect.meetupsfellow.retrofit.response.CommonResponse
import com.google.android.gms.maps.model.LatLng
import javax.inject.Inject

class FavoriteUserFragment : CustomFragment() {

    private val refresh = SwipeRefreshLayout.OnRefreshListener {
        next = 1
        favoriteUser()
    }

    private val clickListener = object : RecyclerViewClickListener {
        override fun onClick(view: View, position: Int) {
            val id = favoritePeopleAdapter!!.getProfileId(position)
            when (id.isNotEmpty()) {
                true -> {
                    this@FavoriteUserFragment.profileId = id
                    this@FavoriteUserFragment.position = position
                    showProgressView(rlProgressBar)
                    ///  markUnfavorite()
                    favouriteunfavouriteuser()
                }

                false -> universalToast("User not found")
            }
        }
    }

    private val presenter = object : FavoriteUserConnector.RequiredViewOps {
        override fun showToast(error: String, logout: Boolean?) {
            when (logout) {
                true -> {
                    logout()
                }

                false -> {}
                null -> {}
            }
            universalToast(error)
            binding.swipeRefreshUsers.isRefreshing = false
            hideProgressView(rlProgressBar)
        }

        override fun showResponse(response: CommonResponse, type: ConstantsApi) {
            when (type) {
                ConstantsApi.FAVORITE_USER -> {
                    when (response.haveNext) {
                        1 -> {
                            loadMore = true
                            next = response.nextPage
                        }
                    }
                    favoritePeopleAdapter!!.clear()
                    response.myFavouriteUserList?.let { favoritePeopleAdapter!!.update(it) }

//                    Log.e("n++**", response.toString())

                }

                ConstantsApi.USER_LIKE -> {
                    favoritePeopleAdapter!!.removeIndex(position)
                    profileId = ""
                    position = -1
                }

                ConstantsApi.USER_FAVOURIT_UNFAVOURIT -> {
                    favoritePeopleAdapter!!.removeIndex(position)
                    profileId = ""
                    position = -1
                    Toast.makeText(requireContext(), response.message, Toast.LENGTH_SHORT).show()
                }

                else -> {
                }
            }
            adjustUi()
            binding.swipeRefreshUsers.isRefreshing = false
            hideProgressView(rlProgressBar)
        }
    }

    @Inject
    lateinit var sharedPreferencesUtil: SharedPreferencesUtil

    private var favoritePeopleAdapter: FavoritePeopleAdapter? = null

    private var mPresenter: FavoriteUserConnector.PresenterOps? = null
    private var profileId = ""
    private var position = -1
    private var loadMore = false
    private var next = 1
    private var location: LatLng? = null
    private var linearLayoutManager: androidx.recyclerview.widget.LinearLayoutManager? = null
    private lateinit var rlProgressBar: LinearLayout
    private var _binding: LayoutFavoriteUserBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        ArchitectureApp.component!!.inject(this@FavoriteUserFragment)
        // val view = inflater.inflate(R.layout.layout_favorite_user, container, false)
        _binding = LayoutFavoriteUserBinding.inflate(inflater, container, false)
        return binding.root

        // return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        rlProgressBar = binding.includedLoading.rlProgress
        location = sharedPreferencesUtil.fetchLocation()
        favoritePeopleAdapter = FavoritePeopleAdapter(clickListener)
        binding.swipeRefreshUsers.setOnRefreshListener(refresh)
        linearLayoutManager = androidx.recyclerview.widget.LinearLayoutManager(
            requireActivity(), RecyclerView.VERTICAL, false
        )
        with(binding.rvFavoriteUsers) {
            layoutManager = linearLayoutManager
            adapter = favoritePeopleAdapter
            addItemDecoration(
                DividerItemDecoration(
                    binding.rvFavoriteUsers.context, DividerItemDecoration.VERTICAL
                )
            )
        }
        addOnScrollListener()
        showProgressView(rlProgressBar)
        favoriteUser()
    }

    private fun addOnScrollListener() {
        binding.rvFavoriteUsers.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrolled(
                recyclerView: RecyclerView, dx: Int, dy: Int
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
                        favoriteUser()
                    }
                }
//                }
            }
        })
    }

    private fun adjustUi() {
        when (favoritePeopleAdapter!!.itemCount > 0) {
            true -> {
                binding.tvNoFavorite.visibility = View.GONE
                binding.ivNoFavFellow.visibility = View.GONE
                binding.rvFavoriteUsers.visibility = View.VISIBLE
            }

            false -> {
                binding.tvNoFavorite.visibility = View.VISIBLE
                binding.ivNoFavFellow.visibility = View.VISIBLE
                binding.rvFavoriteUsers.visibility = View.GONE
            }
        }
    }

    /*  internal fun favoriteUser() {
          if (null == mPresenter)
              mPresenter = FavoriteUserPresenter(presenter)

          run {
              mPresenter!!.addFavoriteUserObject(RequestFeed().apply {
                  page = "$next"
                  limit = "10"
                  currentLat = if (location != null) "${location!!.latitude}" else ""
                  currentLong = if (location != null) "${location!!.longitude}" else ""
              })
              mPresenter!!.callRetrofit(ConstantsApi.FAVORITE_USER)
          }
      }
  */
    internal fun favoriteUser() {
        if (null == mPresenter) mPresenter = FavoriteUserPresenter(presenter)

        run {
            mPresenter!!.callRetrofit(ConstantsApi.FAVORITE_USER)
        }
    }

    private fun markUnfavorite() {
        if (null == mPresenter) mPresenter = FavoriteUserPresenter(presenter)

        run {
            mPresenter!!.addUserProfileObject(profileId)
            mPresenter!!.callRetrofit(ConstantsApi.USER_LIKE)
        }
    }

    private fun favouriteunfavouriteuser() {
        if (null == mPresenter) mPresenter = FavoriteUserPresenter(presenter)

        run {
            mPresenter!!.favouriteunfavouriteuser(MyFavoriteUserReq().apply {
                favourite_user_id = profileId.toInt()
            })
            mPresenter!!.callRetrofit(ConstantsApi.USER_FAVOURIT_UNFAVOURIT)
        }
    }
}