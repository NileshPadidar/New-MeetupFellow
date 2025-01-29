package com.connect.meetupsfellow.mvp.view.activities

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.connect.meetupsfellow.R
import com.connect.meetupsfellow.components.activity.CustomAppActivityCompatViewImpl
import com.connect.meetupsfellow.constants.Constants
import com.connect.meetupsfellow.constants.ConstantsApi
import com.connect.meetupsfellow.databinding.ActivityPrivateListBinding
import com.connect.meetupsfellow.developement.interfaces.ApiConnect
import com.connect.meetupsfellow.developement.interfaces.SharedPreferencesUtil
import com.connect.meetupsfellow.global.utils.ToolbarUtils
import com.connect.meetupsfellow.mvp.connector.activity.ConnectionRequestConnector
import com.connect.meetupsfellow.mvp.connector.activity.PrivateAlbumConnector
import com.connect.meetupsfellow.mvp.presenter.activity.ConnectionRequestPresenter
import com.connect.meetupsfellow.mvp.presenter.activity.PrivateAlbumPresenter
import com.connect.meetupsfellow.mvp.view.adapter.PrivateAlbumAdapter
import com.connect.meetupsfellow.mvp.view.adapter.RecycleViewAccessListAdapter
import com.connect.meetupsfellow.mvp.view.dialog.AlertBuyPremium
import com.connect.meetupsfellow.mvp.view.model.PrivateAlbumModel
import com.connect.meetupsfellow.mvp.view.model.RecycleModel
import com.connect.meetupsfellow.retrofit.request.setAccessPrivateAlbumPermissionReq
import com.connect.meetupsfellow.retrofit.response.CommonResponse
import com.connect.meetupsfellow.retrofit.response.ResponseConnectionRequest
import com.google.android.material.bottomsheet.BottomSheetDialog
import javax.inject.Inject

class PrivateAccessListActivity : CustomAppActivityCompatViewImpl() {


    private val connectPresenter = object : ConnectionRequestConnector.RequiredViewOps {
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

            when (type) {
                ConstantsApi.GET_MY_CONNECTIONS -> {
                    Log.e(
                        "ConnectRequest",
                        "getMyConnectionData_Done- ${response.getMyConnectionList.size}"
                    )
                    binding!!.btnSetAccessTop.visibility = View.GONE
                    if (response.getMyConnectionList.size > 0) {
                        binding!!.privateAccListRV.layoutManager =
                            GridLayoutManager(binding!!.privateAccListRV.context!!, 1)

                        connectionList.clear()
                        connectionList.addAll(response.getMyConnectionList)
                        Log.e("ConnectRequest", "connectionList: ${connectionList.size}")
                        binding!!.llSearch.visibility = View.VISIBLE

                        if (isSelected) {
                            binding!!.btnSetAccess.visibility = View.VISIBLE
                            //   binding!!.tvCheckClear.visibility = View.VISIBLE
                            binding!!.floatingBtnAccess.visibility = View.GONE
                            binding!!.btnSetAccessTop.visibility = View.GONE

                        } else {
                            binding!!.btnSetAccess.visibility = View.GONE
                            //  binding!!.tvCheckClear.visibility = View.GONE
                            binding!!.btnSetAccessTop.visibility = View.GONE
                            binding!!.floatingBtnAccess.visibility = View.VISIBLE
                        }

                        recyclePrivateAlbumAdapter = RecycleViewAccessListAdapter(
                            this@PrivateAccessListActivity, isSelected, response.getMyConnectionList
                        )

                        binding!!.privateAccListRV.adapter = recyclePrivateAlbumAdapter
                        setupSearchView()
                        recyclePrivateAlbumAdapter.notifyDataSetChanged()

                        when (response.getMyConnectionList.isEmpty()) {
                            true -> {
                                binding!!.privateAccListRV.visibility = View.GONE
                                binding!!.noFellowTxt.visibility = View.VISIBLE
                                binding!!.noFellowImg.visibility = View.VISIBLE
                                binding!!.btnSetAccessTop.visibility = View.VISIBLE
                                binding!!.llSearch.visibility = View.GONE
                            }

                            false -> {
                                binding!!.privateAccListRV.visibility = View.VISIBLE
                                binding!!.noFellowTxt.visibility = View.GONE
                                binding!!.noFellowImg.visibility = View.GONE
                                binding!!.btnSetAccessTop.visibility = View.GONE
                                binding!!.llSearch.visibility = View.VISIBLE
                            }
                        }

                    } else {
                        binding!!.privateAccListRV.visibility = View.GONE
                        binding!!.noFellowTxt.visibility = View.VISIBLE
                        binding!!.noFellowTxt.text =
                            "You don't have any connection, please make your connection first than try again."
                    }
                    hideProgressView(progressBar)
                }


                else -> {
                    Log.e("ConnectRequest", "MY_Api_Else")
                }
            }
            hideProgressView(progressBar)
        }
    }

    private val presenter = object : PrivateAlbumConnector.RequiredViewOps {
        override fun showToast(error: String, logout: Boolean?) {
            when (logout) {
                true -> {
                    logout()
                }

                false -> {}
                null -> {}
            }
            universalToast(error)
            privateAlbumAdapter.clearAll()
            binding!!.privateAccListRV.visibility = View.GONE
            binding!!.noFellowTxt.visibility = View.VISIBLE
            binding!!.noFellowImg.visibility = View.VISIBLE
            hideProgressView(progressBar)
            Log.e("PrivateAcces*", "ErrorMassage: $error")

        }

        override fun showResponse(response: CommonResponse, type: ConstantsApi) {
            when (type) {
                ConstantsApi.UPLOAD_PRIVATE -> {
                    universalToast(response.message)
                    showProgressView(progressBar)
                    privatePicsLocal.clear()
                    //showHideSave()
                    //fetchPrivateAlbum()
                }

                ConstantsApi.SET_ACCESS_PRIVET_ALBUM_PERMISSION -> {
                    accessStatus = response.access_status
                    binding!!.llSearch.visibility = View.GONE
                    updateUi(response.message)
                    if (response.access_status.equals("selected_connected")) {
                        hideProgressView(progressBar)
                        getMyConnectionRequest()
                        universalToast("Access change successfully")
                    }
                    Log.e("MY_PRIVET_ALBUM22", "${response.message}")
                }

                ConstantsApi.PRIVATE_ALBUM, ConstantsApi.OTHER_PRIVATE_ALBUM -> {
                    updateUi(response.message)
                }

                ConstantsApi.DELETE_PRIVATE -> {
                    universalToast(response.message)
                    //showHideSave()
                    showProgressView(progressBar)
                    //fetchPrivateAlbum()
                }

                ConstantsApi.MY_PRIVET_ALBUM_PERMISSION -> {
                    selectedUserId.clear()
                    accessStatus = response.myprivatealbumpermission!!.accessStatus
                    Log.e("MY_PRIVET_ALBUM", "$accessStatus")
                    updateUi(response.message)
                    if (accessStatus.equals("public")) {
                        if (!sharedPreferencesUtil.fetchUserProfile().isProMembership) {
                            showAlertPrivetDialog()
                        }
                    } else if (accessStatus.equals("selected_connected")) {
                        getMyConnectionRequest()
                    } else {
                        updateUi(response.message)
                    }
                }


                ConstantsApi.PRIVATE_ACCESS_OTHERS -> {

                }

                ConstantsApi.SHARE_PRIVATE, ConstantsApi.REMOVE_PRIVATE -> {
                    showProgressView(progressBar)
                    //fetchPrivateAccessList()
                }

                else -> {
                }
            }
            hideProgressView(progressBar)
        }
    }

    @Inject
    lateinit var sharedPreferencesUtil: SharedPreferencesUtil

    @Inject
    lateinit var apiConnect: ApiConnect

    lateinit var recyclePrivateAlbumAdapter: RecycleViewAccessListAdapter
    private var recycleViewModels = ArrayList<RecycleModel>()
    private var connectionList = ArrayList<ResponseConnectionRequest>()

    private var accessStatus: String? = null
    private var isSelected: Boolean = false
    private var connectionPresenter: ConnectionRequestConnector.PresenterOps? = null
    private var mPresenter: PrivateAlbumConnector.PresenterOps? = null
    private val privatePicsLocal = arrayListOf<PrivateAlbumModel>()
    private var selectedUserId = arrayListOf<Int>()
    private val privateAlbumAdapter = PrivateAlbumAdapter()
    private var isSelectCheck: Boolean = false
    private var binding: ActivityPrivateListBinding? = null
    private lateinit var progressBar: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPrivateListBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        //setContentView(R.layout.activity_private_list)
        component.inject(this)
        startLoadingAnim()
        progressBar = binding!!.includedLoading.rlProgress
        showProgressView(progressBar)
        //setupActionBar(getString(R.string.title_private_album_access), true)
        ToolbarUtils.setupActionBar(
            this,
            binding!!.header.toolbar,
            getString(R.string.title_private_album_access),
            binding!!.header.tvTitle,
            showBackArrow = true,
            activity = this
        )
        //callForUserProfile()
        myPrivateAlbumPermission()
        Log.e("Activity*", "PrivateAccessListActivity")
        binding!!.privateAccListRV.layoutManager =
            GridLayoutManager(this@PrivateAccessListActivity, 1)

        binding!!.floatingBtnAccess.setOnClickListener {
            if (sharedPreferencesUtil.fetchUserProfile().isProMembership && sharedPreferencesUtil.fetchUserProfile().currentPlanInfo!!.isMediaPrivate > 0) {
                openBottomSheet()
            } else {
                showAlertPrivetDialog()
            }
        }

        binding!!.btnSetAccessTop.setOnClickListener {
            if (sharedPreferencesUtil.fetchUserProfile().isProMembership && sharedPreferencesUtil.fetchUserProfile().currentPlanInfo!!.isMediaPrivate > 0) {
                openBottomSheet()
            } else {
                showAlertPrivetDialog()
            }
        }

        binding!!.tvCheckClear.setOnClickListener {
            isSelectCheck = !isSelectCheck
            recyclePrivateAlbumAdapter.checkUncheck(isSelectCheck)
            Toast.makeText(this, "Check All", Toast.LENGTH_SHORT).show()
        }
        binding!!.btnSetAccess.setOnClickListener {
            if (selectedUserId != null && selectedUserId.size > 0) {
                isSelected = false
                setAccessPermissionForPrivateAlbum("selected_connected")
                binding!!.btnSetAccess.visibility = View.GONE
                binding!!.floatingBtnAccess.visibility = View.VISIBLE
            } else {
                Toast.makeText(this, "Please select user", Toast.LENGTH_SHORT).show()
            }

        }
        binding!!.search.queryHint = "Type name to search..."
        val searchEditText: EditText =
            binding!!.search.findViewById(androidx.appcompat.R.id.search_src_text)
        // Change the text size
        searchEditText.textSize = 15f

        ///  fetchPrivateAccessList()
    }


    private fun setupSearchView() {
        binding!!.search.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                recyclePrivateAlbumAdapter.filter.filter(query)
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                recyclePrivateAlbumAdapter.filter.filter(newText)
                return false
            }
        })
    }

    private fun startLoadingAnim() {

        val animation = android.view.animation.AnimationUtils.loadAnimation(
            applicationContext, R.anim.blink_anim
        )

        binding!!.includedLoading.animLogo.startAnimation(animation)
    }

    private fun fetchPrivateAccessList() {
        if (null == mPresenter) mPresenter = PrivateAlbumPresenter(presenter)

        run {
            mPresenter!!.callRetrofit(ConstantsApi.PRIVATE_ACCESS)
        }
    }

    private fun myPrivateAlbumPermission() {
        if (null == mPresenter) mPresenter = PrivateAlbumPresenter(presenter)

        run {
            mPresenter!!.callRetrofit(ConstantsApi.MY_PRIVET_ALBUM_PERMISSION)
        }
    }


    private fun setAccessPermissionForPrivateAlbum(status: String) {
        if (null == mPresenter) mPresenter = PrivateAlbumPresenter(presenter)

        run {
            mPresenter!!.addObjectSetMyPrivateAlbum(setAccessPrivateAlbumPermissionReq().apply {
                access_status = status
                users = selectedUserId
                Log.e("ConnectRequest", "users: ${users}")
            })
            showProgressView(progressBar)
            mPresenter!!.callRetrofit(ConstantsApi.SET_ACCESS_PRIVET_ALBUM_PERMISSION)
        }
    }

    private fun updateUi(msg: String) {
        binding!!.noFellowTxt.text = msg
        if (accessStatus.equals("private")) {
            binding!!.privateAccListRV.visibility = View.GONE
            binding!!.noFellowTxt.visibility = View.VISIBLE
            binding!!.noFellowImg.visibility = View.VISIBLE
            binding!!.btnSetAccessTop.visibility = View.VISIBLE
            binding!!.floatingBtnAccess.visibility = View.GONE
        } else if (accessStatus.equals("public")) {
            binding!!.privateAccListRV.visibility = View.GONE
            binding!!.noFellowTxt.visibility = View.VISIBLE
            binding!!.noFellowImg.visibility = View.VISIBLE
            binding!!.btnSetAccessTop.visibility = View.VISIBLE
            binding!!.floatingBtnAccess.visibility = View.GONE
        } else if (accessStatus.equals("private_connected")) {
            binding!!.privateAccListRV.visibility = View.GONE
            binding!!.noFellowTxt.visibility = View.VISIBLE
            binding!!.noFellowImg.visibility = View.VISIBLE
            binding!!.btnSetAccessTop.visibility = View.VISIBLE
            binding!!.floatingBtnAccess.visibility = View.GONE
        } else if (accessStatus.equals("selected_connected")) {
            binding!!.privateAccListRV.visibility = View.VISIBLE
            binding!!.noFellowTxt.visibility = View.GONE
            binding!!.noFellowImg.visibility = View.GONE
            binding!!.btnSetAccessTop.visibility = View.GONE
            binding!!.floatingBtnAccess.visibility = View.VISIBLE
        }
    }

    private fun showAlertPrivetDialog() {

        val dialog = Dialog(this)

        dialog.setCancelable(true)

        val view = layoutInflater.inflate(R.layout.privet_album_access_dialog, null)

        dialog.setContentView(view)

        dialog.window!!.attributes.windowAnimations = R.style.DialogAnimation

        if (dialog.window != null) {
            dialog.window!!.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
            )
        }
        dialog.window!!.setGravity(Gravity.CENTER)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val dialogHead = view.findViewById<TextView>(R.id.dialog_title)
        val dialogContent = view.findViewById<TextView>(R.id.dialog_content)
        val cancelBtn = view.findViewById<Button>(R.id.noExitBtn)
        val confirm = view.findViewById<Button>(R.id.yesExitBtn)
        Log.e("PrivetALBUM", "OPEN dIALOG")


        cancelBtn.setOnClickListener {
            dialog.cancel()
        }

        confirm.setOnClickListener {
            if (sharedPreferencesUtil.fetchUserProfile().isProMembership) {
                openBottomSheet()
                dialog.dismiss()
            } else {
                AlertBuyPremium.Builder(this, Constants.DialogTheme.NoTitleBar).build().show()
                dialog.dismiss()
                return@setOnClickListener
            }
        }

        dialog.show()

    }

    @SuppressLint("MissingInflatedId")
    private fun openBottomSheet() {
        // on below line we are creating a new bottom sheet dialog.
        val dialog = BottomSheetDialog(this, R.style.BottomSheetStyle)

        // on below line we are inflating a layout file which we have created.
        val view = layoutInflater.inflate(R.layout.bottom_sheet_mack_privet_album, null)
        val btnPrivet = view.findViewById<Button>(R.id.btn_privet_public)
        val btnPublic = view.findViewById<Button>(R.id.btn_public)
        val btnMyConnection = view.findViewById<Button>(R.id.btn_my_connection)
        val btnSelectConnection = view.findViewById<Button>(R.id.btn_selected)
        val ll_cancel = view.findViewById<LinearLayout>(R.id.ll_cancel)

        if (accessStatus.equals("private")) {
            btnPrivet.visibility = View.GONE
            btnPublic.visibility = View.VISIBLE
        } else if (accessStatus.equals("public")) {
            btnPrivet.visibility = View.VISIBLE
            btnPublic.visibility = View.GONE
        }

        btnPublic.setOnClickListener {
            setAccessPermissionForPrivateAlbum("public")
            dialog.dismiss()

        }
        btnPrivet.setOnClickListener {
            setAccessPermissionForPrivateAlbum("private")
            dialog.dismiss()

        }
        ll_cancel.setOnClickListener {
            dialog.dismiss()
        }
        btnMyConnection.setOnClickListener {
            setAccessPermissionForPrivateAlbum("private_connected")
            dialog.dismiss()
        }
        btnSelectConnection.setOnClickListener {
            isSelected = true
            getMyConnectionRequest()
            dialog.dismiss()
        }

        dialog.setContentView(view)
        dialog.show()
    }

    private fun getMyConnectionRequest() {
        if (null == connectionPresenter) connectionPresenter =
            ConnectionRequestPresenter(connectPresenter)

        run {
            showProgressView(progressBar)
            connectionPresenter!!.callRetrofit(ConstantsApi.GET_MY_CONNECTIONS)
        }
    }

    fun selectUser(selectUserList: ArrayList<Int>) {
        //  this.selectedUserId = selectUserList
        val set: HashSet<Int> = HashSet(selectUserList) // Convert ArrayList to HashSet
        this.selectedUserId = ArrayList(set) // Convert back to ArrayList


        Log.e("ConnectRequest", "selectedUserIdList: ${selectedUserId.size}")
        // Log.e("ConnectRequest", "selectedUserIdList: ${selectedUserId.get(0)}")
    }

}