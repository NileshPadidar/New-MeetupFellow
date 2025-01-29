package com.connect.meetupsfellow.mvp.view.activities

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.ClipData
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Matrix
import android.graphics.Shader
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.*
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.exifinterface.media.ExifInterface
import androidx.recyclerview.widget.GridLayoutManager
import com.connect.meetupsfellow.R
import com.connect.meetupsfellow.application.ArchitectureApp
import com.connect.meetupsfellow.components.activity.CustomAppActivityCompatViewImpl
import com.connect.meetupsfellow.constants.Constants
import com.connect.meetupsfellow.constants.ConstantsApi
import com.connect.meetupsfellow.databinding.ActivityPrivateAlbumBinding
import com.connect.meetupsfellow.databinding.BottomSheetGetPickBinding
import com.connect.meetupsfellow.developement.interfaces.ApiConnect
import com.connect.meetupsfellow.developement.interfaces.SharedPreferencesUtil
import com.connect.meetupsfellow.global.utils.Path
import com.connect.meetupsfellow.global.utils.Path.getRealPathFromURI
import com.connect.meetupsfellow.global.utils.compressImage
import com.connect.meetupsfellow.global.view.easyphotopicker.ConstantsImage
import com.connect.meetupsfellow.global.view.easyphotopicker.EasyImage
import com.connect.meetupsfellow.log.LogManager
import com.connect.meetupsfellow.mvp.connector.activity.PrivateAlbumConnector
import com.connect.meetupsfellow.mvp.presenter.activity.PrivateAlbumPresenter
import com.connect.meetupsfellow.mvp.view.adapter.PrivateAlbumAdapter
import com.connect.meetupsfellow.mvp.view.adapter.RecyclePrivateAlbumAdapter
import com.connect.meetupsfellow.mvp.view.model.PrivateAlbumModel
import com.connect.meetupsfellow.mvp.view.model.RecycleModel
import com.connect.meetupsfellow.retrofit.response.CommonResponse
import com.connect.meetupsfellow.retrofit.response.ResponsePrivatePics
import com.connect.meetupsfellow.retrofit.response.ResponseUserData
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.floatingactionbutton.FloatingActionButton
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


class PrivateAlbumActivity : CustomAppActivityCompatViewImpl() {

    private var videoPath: String? = null
    private var privatePicsIds = ArrayList<ResponsePrivatePics>()


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
            binding!!.rvPrivateAlbum.visibility = View.GONE
            binding!!.tvNoUser.visibility = View.VISIBLE
            hideProgressView(progressBar)
            isUpdated = true
        }

        override fun showResponse(response: CommonResponse, type: ConstantsApi) {
            when (type) {
                ConstantsApi.UPLOAD_PRIVATE -> {
                    universalToast(response.message)
                    showProgressView(progressBar)
                    privatePicsLocal.clear()
                    isUpdated = true
                    fetchPrivateAlbum()
                }

                ConstantsApi.PRIVATE_ALBUM, ConstantsApi.OTHER_PRIVATE_ALBUM -> {

                    recycleViewModels.clear()

                    val images = Gson().toJson(response.privatePics)

                    privatePicsIds.clear()

                    for (i in response.privatePics.indices) {

                        privatePicsIds.add(response.privatePics[i])

                        recycleViewModels.add(
                            RecycleModel(
                                response.privatePics.get(i).path,
                                response.userId,
                                images,
                                response.privatePics.size,
                                response.privatePics[i].type
                            )
                        )
                    }
                    //val layoutManager = GridLayoutManager(this@PrivateAlbumActivity, 3 ,GridLayoutManager.VERTICAL, false)

                    /*layoutManager.setSpanSizeLookup(object : SpanSizeLookup() {
                        override fun getSpanSize(position: Int): Int {
                            // 7 is the sum of items in one repeated section
                            when (position % response.privatePics.size) {
                                0, 1, 2 -> return 6
                                3, 4, 5, 6 -> return 3
                            }
                            throw IllegalStateException("internal error")
                        }
                    })*/

                    /* layoutManager?.setSpanSizeLookup(object :
                         GridLayoutManager.SpanSizeLookup() {

                         override fun getSpanSize(position: Int): Int {
                             return if (position < response.privatePics.size * 4) {
                                 if ((position + 1) % 4 == 0 && (position + 1) != 1) { // totalRowCount : How many item you want to show
                                     3
                                 } else 1
                             } else 1
                         }
                     })

 */

                    /*  val spannedGridLayoutManager = SpannedGridLayoutManager(orientation = SpannedGridLayoutManager.Orientation.VERTICAL, spans = 4)
                      spannedGridLayoutManager.itemOrderIsStable = true*/
                    //spannedGridLayoutManager.removeAllViews()
                    /*  spannedGridLayoutManager.spanSizeLookup = SpannedGridLayoutManager.SpanSizeLookup { position ->
                          Log.d("posGridpos", position.toString())
                          Log.d("posGrid", (position % 7).toString())

                          when (position % 7) {
                              0, 5 ->
                                  SpanSize(2, 1)
                              3, 7 ->
                                  SpanSize(3, 2)
                              8,10 ->
                                  SpanSize(3, 2)
                              else ->
                                  SpanSize(1, 1)
                          }
                          //SpanSize((1..3).random(), (1..2).random())
                      }*/

                    ///  binding!!.rvPrivateAlbum.layoutManager = spannedGridLayoutManager

                    //val layoutManager = GreedoLayoutManager(recyclePrivateAlbumAdapter)
                    //layoutManager.setMaxRowHeight(MeasUtils.dpToPx(150F, this@PrivateAlbumActivity))

                    //rvPrivateAlbum.layoutManager = layoutManager
                    //val layoutManager = StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL)
                    // layoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);
                    //rvPrivateAlbum.hasFixedSize()

                    binding!!.rvPrivateAlbum.setHasFixedSize(true)
                    binding!!.rvPrivateAlbum.layoutManager =
                        GridLayoutManager(this@PrivateAlbumActivity, 3)
                    recyclePrivateAlbumAdapter =
                        RecyclePrivateAlbumAdapter(this@PrivateAlbumActivity, recycleViewModels)

                    binding!!.rvPrivateAlbum.itemAnimator = null
                    binding!!.rvPrivateAlbum.setItemViewCacheSize(50)

                    binding!!.rvPrivateAlbum.adapter = recyclePrivateAlbumAdapter
                    deletePvtImage!!.visibility = View.GONE
                    recyclePrivateAlbumAdapter.notifyDataSetChanged()


                    if (isUpdated) {

                        isUpdated = false
                        showUpdatedDialog()

                    }

                    /*rvPrivateAlbum.layoutManager =
                        GridLayoutManager(rvPrivateAlbum.context!!, 4)
                    privateAlbumAdapter.setType(PrivateAlbumAdapter.Image)
                    privateAlbumAdapter.privateAlbum(response.privatePics)*/

                    if (recycleViewModels.isEmpty() && recyclePrivateAlbumAdapter.itemCount < 1) {
                        binding!!.rvPrivateAlbum.visibility = View.GONE
                        binding!!.tvNoUser.visibility = View.VISIBLE
                        binding!!.noPrivateImg.visibility = View.VISIBLE
                    } else {
                        binding!!.rvPrivateAlbum.visibility = View.VISIBLE
                        binding!!.tvNoUser.visibility = View.GONE
                        binding!!.noPrivateImg.visibility = View.GONE
                    }
                }

                ConstantsApi.DELETE_PRIVATE -> {
                    universalToast(response.message)
                    showProgressView(progressBar)
                    isUpdated = true
                    fetchPrivateAlbum()
                }

                ConstantsApi.PRIVATE_ACCESS -> {
                    binding!!.rvPrivateAlbum.layoutManager =
                        GridLayoutManager(binding!!.rvPrivateAlbum.context!!, 1)
                    privateAlbumAdapter.setType(PrivateAlbumAdapter.User)
                    privateAlbumAdapter.getData().clear()
                    privateAlbumAdapter.update(response.picAccessList)
                    when (response.picAccessList.isEmpty()) {
                        true -> {
                            binding!!.rvPrivateAlbum.visibility = View.GONE
                            binding!!.tvNoUser.visibility = View.VISIBLE
                        }

                        false -> {
                            binding!!.rvPrivateAlbum.visibility = View.VISIBLE
                            binding!!.tvNoUser.visibility = View.GONE
                        }
                    }
                }

                ConstantsApi.PRIVATE_ACCESS_OTHERS -> {
                    binding!!.rvPrivateAlbum.layoutManager =
                        GridLayoutManager(binding!!.rvPrivateAlbum.context!!, 1)
                    privateAlbumAdapter.setType(PrivateAlbumAdapter.Other)
                    privateAlbumAdapter.getData().clear()
                    privateAlbumAdapter.update(response.picAccessList)
                    when (response.picAccessList.isEmpty()) {
                        true -> {
                            binding!!.rvPrivateAlbum.visibility = View.GONE
                            binding!!.tvNoUser.visibility = View.VISIBLE
                        }

                        false -> {
                            binding!!.rvPrivateAlbum.visibility = View.VISIBLE
                            binding!!.tvNoUser.visibility = View.GONE
                        }
                    }
                }

                ConstantsApi.SHARE_PRIVATE, ConstantsApi.REMOVE_PRIVATE -> {
                    showProgressView(progressBar)
                    fetchPrivateAccessList()
                }

                else -> {
                }
            }
            hideProgressView(progressBar)
        }
    }

    private fun showUpdatedDialog() {

        Log.d("run1111", "run")

        val dialog = Dialog(this@PrivateAlbumActivity)

        dialog.setCancelable(true)

        val view = layoutInflater.inflate(R.layout.profile_update_sucess_dialog, null)

        dialog.setContentView(view)

        dialog.window!!.attributes.windowAnimations = R.style.DialogAnimation

        if (dialog.window != null) {
            dialog.window!!.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
        dialog.window!!.setGravity(Gravity.CENTER)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val ok_btn = view.findViewById<Button>(R.id.ok_btn)
        privatePicsLocal.clear()
        ok_btn.setOnClickListener {

            dialog.dismiss()
        }

        dialog.show()
    }


    @Inject
    lateinit var sharedPreferencesUtil: SharedPreferencesUtil

    @Inject
    lateinit var apiConnect: ApiConnect

    lateinit var recyclePrivateAlbumAdapter: RecyclePrivateAlbumAdapter
    private var recycleViewModels = ArrayList<RecycleModel>()

    private var lock: MenuItem? = null
    private var save: MenuItem? = null

    private var isUpdated = false
    private var photoFile: File? = null
    var deletePvtImage: FloatingActionButton? = null

    private var mPresenter: PrivateAlbumConnector.PresenterOps? = null
    private val privatePicsLocal = arrayListOf<PrivateAlbumModel>()
    private var imagesPrvt = arrayListOf<ResponsePrivatePics>()
    private val privateAlbumAdapter = PrivateAlbumAdapter()
    private var binding: ActivityPrivateAlbumBinding? = null
    private lateinit var progressBar: LinearLayout
    private val PREVIEW_REQUEST_CODE = 999

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPrivateAlbumBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        component.inject(this)
        progressBar = binding!!.includedLoading.rlProgress
        showProgressView(progressBar)
        deletePvtImage = binding!!.deletePvtImg
        setToolbar()
        Log.e("witch_activity", "PrivateAlbumActivity_new")

        startLoadingAnim()
        callForUserProfile()

    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        init()
    }

    private fun startLoadingAnim() {
        val animation = android.view.animation.AnimationUtils.loadAnimation(
            applicationContext, R.anim.blink_anim
        )

        binding!!.includedLoading.animLogo.startAnimation(animation)
    }

    private fun setToolbar() {
        binding!!.header.tvTitle.text = getString(R.string.title_private_album)
        val paint = binding!!.header.tvTitle.paint
        val width = paint.measureText(binding!!.header.tvTitle.text.toString())
        val textShader: Shader = LinearGradient(
            0f, 0f, width, binding!!.header.tvTitle.textSize, intArrayOf(
                Color.parseColor("#F4447E"), Color.parseColor("#8448F4")
            ), null, Shader.TileMode.REPEAT
        )
        binding!!.header.tvTitle.paint.shader = textShader
        binding!!.header.myAccessImg.visibility = View.VISIBLE
        binding!!.header.ivBack.visibility = View.VISIBLE
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

        //RecyclerViewClick.enableClick(itemClick)
    }

    override fun onPause() {
        super.onPause()
        sharedPreferencesUtil.saveTimmerTime(System.currentTimeMillis().toString())
        LogManager.logger.i(
            ArchitectureApp.instance!!.tag, "onPause ::: " + System.currentTimeMillis().toString()
        )

        //RecyclerViewClick.disableClick()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_save_lock, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        lock = menu?.findItem(R.id.menu_private)
        save = menu?.findItem(R.id.menu_save)
        val rootView = save?.actionView as RelativeLayout
        rootView.setOnClickListener {
            if (privatePicsLocal.isEmpty()) {
                universalToast("Please choose at least 1 picture")
            } else {
                showProgressView(progressBar)
                uploadPics()
            }
        }

        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_private -> {
                if (privateAlbumAdapter.itemCount > 0) {
                    showAlertUnshare(
                        "Are you sure you want to unshare your private pictures from all users?"
                    )
                }
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun init() {

        setupRecyclerView()

        EasyImage.configuration(this@PrivateAlbumActivity)
            .setImagesFolderName("MeetUpsFellow Private Images")
            .setCopyTakenPhotosToPublicGalleryAppFolder(true)
            .setCopyPickedImagesToPublicGalleryAppFolder(true).setAllowMultiplePickInGallery(false)

        fetchPrivateAlbum()

        binding!!.addPvtImg.setOnClickListener {

            if (sharedPreferencesUtil.fetchUserProfile().availableMediaStorage!! > 0) {
                if (privatePicsLocal.size == 3) {
                    Toast.makeText(
                        this@PrivateAlbumActivity,
                        "You can only upload 3 images at a time",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Log.e("Click*&", "PrivateAlbumAcivity@")
                    /// selectImagePopup()
                    openBottomSheet()
                }
            } else {
                Toast.makeText(
                    this@PrivateAlbumActivity,
                    getString(R.string.reach_your_media_uploading),
                    Toast.LENGTH_SHORT
                ).show()
            }

        }

        binding!!.header.myAccessImg.setOnClickListener {

            switchActivity(Constants.Intent.PrivateAlbumList, false, Bundle())
        }

        binding!!.header.ivBack.setOnClickListener {
            finish()
        }

        deletePvtImage!!.setOnClickListener {


            showDeleteImgDialog(
                "Delete Images", "Are you sure, you want to delete selected images?"
            )

            /*val alertDialog = AlertDialog.Builder(this@PrivateAlbumActivity, R.style.MyDialogTheme2)
            alertDialog.setMessage("Selected Images will be removed from your Media Album")
            alertDialog.setPositiveButton("Delete") { dialog, _ ->
                showProgressView(progressBar)

                for (i in recyclePrivateAlbumAdapter.deletePosition.indices) {

                    Log.d("deleteIds",
                        privatePicsIds[recyclePrivateAlbumAdapter.deletePosition[i].toInt()].id.toString()
                    )

                    deleteImage(privatePicsIds[recyclePrivateAlbumAdapter.deletePosition[i].toInt()].id)

                }

            }
            alertDialog.setNegativeButton("Cancel") { dialog, _ ->
                dialog.cancel()
            }
            alertDialog.setCancelable(false)
            // Showing Alert Message
            alertDialog.show()*/

            //showProgressView(progressBar)
        }

    }

    private fun showDeleteImgDialog(head: String, content: String) {

        val dialog = Dialog(this)

        dialog.setCancelable(true)

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

        val dialogHead = view.findViewById<TextView>(R.id.dialog_title)
        val dialogContent = view.findViewById<TextView>(R.id.dialog_content)
        val cancelBtn = view.findViewById<Button>(R.id.noExitBtn)
        val confirm = view.findViewById<Button>(R.id.yesExitBtn)

        dialogHead.text = head
        dialogContent.text = content

        cancelBtn.setOnClickListener {

            dialog.cancel()
        }

        confirm.setOnClickListener {
            //showProgressView(progressBar)
            dialog.cancel()
            for (i in recyclePrivateAlbumAdapter.deletePosition.indices) {

                Log.d(
                    "deleteIds",
                    privatePicsIds[recyclePrivateAlbumAdapter.deletePosition[i].toInt()].id.toString()
                )

                deleteImage(privatePicsIds[recyclePrivateAlbumAdapter.deletePosition[i].toInt()].id)

            }
        }

        dialog.show()

    }

    private fun setupRecyclerView() {

        //rvPrivateAlbum.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)

    }

    private fun getPics(position: Int) {
        when (position) {
            0 -> {
                if (privatePicsLocal.size == 3) {
                    universalToast("You can upload maximum 3 pictures at once.")
                } else {
                    /// selectImagePopup()
                    openBottomSheet()
                }
            }

            else -> {
            }
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
                        EasyImage.openGallery(this@PrivateAlbumActivity, 0)
                    }

                    false -> {
                        EasyImage.openPhoto(this@PrivateAlbumActivity, 0)
                        //universalToast(getString(R.string.text_no_camera))
                    }
                }
            }

            false -> {

            }
        }

    @AfterPermissionGranted(Constants.PermissionsCode.REQUEST_PERMISSION_VIDEO)
    fun onVideoTapped() =
        when (cameraPermission(Constants.PermissionsCode.REQUEST_PERMISSION_VIDEO)) {
            true -> {
                when (checkGalleryAppAvailability()) {
                    true -> {
                        val intent = Intent(Intent.ACTION_GET_CONTENT)
                        intent.addCategory(Intent.CATEGORY_OPENABLE)
                        intent.type = "video/*"
                        startActivityForResult(intent, Constants.ImagePicker.Video)
                    }

                    false -> {
                        universalToast(getString(R.string.text_no_gallery))
                    }
                }
            }

            false -> {

            }
        }

    private val CAMERA_PERMISSION = Manifest.permission.CAMERA
    private val READ_STORAGE_PERMISSION = Manifest.permission.READ_EXTERNAL_STORAGE
    private fun camerarequestRuntimePermission() {
        if (ContextCompat.checkSelfPermission(
                this, CAMERA_PERMISSION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            ///  Toast.makeText(this, "Permission accepted", Toast.LENGTH_SHORT).show()
            capturePhoto()
        } else {
            ActivityCompat.requestPermissions(
                this, arrayOf(CAMERA_PERMISSION, READ_STORAGE_PERMISSION), 100
            )
        }
    }


    private fun capturePhoto() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            var photoURI: Uri? = null
            try {
                photoFile = createImageFileWith()
                photoURI = FileProvider.getUriForFile(
                    this@PrivateAlbumActivity,
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (resultCode) {
            Activity.RESULT_OK -> {
                when (requestCode) {
                    Constants.ImagePicker.CAMERA -> {
                        LogManager.logger.i(
                            ArchitectureApp.instance!!.tag,
                            "Image File length before : ${photoFile!!.length()}"
                        )

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
                        rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, os)
                        os.close()
                        val photoFile = compressImage(photoFile!!, maxWidth = 1920, maxHeight = 1080, quality = 80)
                        /*photoFile = compressImage(
                            file = photoFile!!,          // Original file
                            maxWidth = 1280,           // Max width
                            maxHeight = 720,           // Max height
                            quality = 50               // Compression quality
                        )*/

                        privatePicsLocal.add(PrivateAlbumModel().apply {
                            this.image = photoFile
                        })

                        privateAlbumAdapter.update(ResponsePrivatePics().apply {
                            path = photoFile!!.absolutePath
                            type = "image"
                        })

                        imagesPrvt.clear()
                        imagesPrvt.add(ResponsePrivatePics().apply {
                            this.path = Uri.fromFile(photoFile).toString()
                        })

                        if (imagesPrvt.isNotEmpty()) {
                            Log.e("Private_Album", "RecyclePrivateAlbumAdapter: Switch_Activity")
                            val jsonImages =
                                Gson().toJson(imagesPrvt) // Convert list to JSON string
                            val intent = Intent(this, ImagePagerActivity::class.java)
                            intent.putExtra(
                                Constants.IntentDataKeys.PrivateAlbumImages, jsonImages
                            ) // Pass JSON string
                            intent.putExtra(
                                Constants.IntentDataKeys.Type, Constants.IntentDataKeys.ImgUri
                            )
                            intent.putExtra(
                                Constants.IntentDataKeys.Class,
                                Constants.IntentDataKeys.PrivateAlbumImages
                            )
                            intent.putExtra("position", 0)
                            ///startActivity(intent)
                            startActivityForResult(intent, PREVIEW_REQUEST_CODE)
                        }

                    }

                    ConstantsImage.RequestCodes.PICK_PICTURE_FROM_GALLERY -> {
                        /// imageFromGallery(requestCode, resultCode, data)
                        if (data?.clipData != null) {  // Multiple images selected
                            val clipData = data.clipData
                            val selectedImages = mutableListOf<Uri>()
                            if (clipData!!.itemCount > 3) {
                                Toast.makeText(
                                    this, "You can add up to 3 images only", Toast.LENGTH_SHORT
                                ).show()
                            }

                            for (i in 0 until clipData.itemCount.coerceAtMost(3)) {  // Limit to 3 images
                                selectedImages.add(clipData.getItemAt(i).uri)
                            }
                            processSelectedImages(selectedImages)
                        } else if (data?.data != null) {  // Single image selected
                            val imageUri = data.data
                            processSelectedImages(listOf(imageUri!!))
                        }
                    }

                    Constants.ImagePicker.Video -> {
                        uploadVideoFile(data!!.data!!)
                    }

                    PREVIEW_REQUEST_CODE -> {
                        Log.e("PrivatAlbumActivity", "Upload_data_call")
                        showProgressView(progressBar)
                        uploadPics()
                    }
                }

            }

            AutocompleteActivity.RESULT_ERROR -> {
                val status = Autocomplete.getStatusFromIntent(data!!)
                LogManager.logger.i(ArchitectureApp.instance!!.tag, "${status.statusMessage}")
                universalToast("${status.statusMessage}")
            }

            Activity.RESULT_CANCELED -> {
                Log.e("ActivityPrivatAlbum", "RESULT_CANCELED: Result_Cancle")
                privatePicsLocal.clear()
                imagesPrvt.clear()
            }
        }
    }

    private fun processSelectedImages(imageUris: List<Uri>) {
        privatePicsLocal.clear()
        imagesPrvt.clear()
        imageUris.forEach { uri ->
            val imagePath = getRealPathFromURI(uri, this)
            Log.e("PrivateAlbum", "imagePath_GET: $imagePath")
            if (imagePath != null) {
                val imageFile = File(imagePath)
                if (imageFile.exists() && imageFile.length() > 0) {
                   /* val compressedImage = compressImage(
                        file = imageFile,          // Original file
                        maxWidth = 1280,           // Max width
                        maxHeight = 720,           // Max height
                        quality = 50               // Compression quality
                    )*/
                    val compressedImage = compressImage(imageFile, maxWidth = 1920, maxHeight = 1080, quality = 80)
                    privatePicsLocal.add(PrivateAlbumModel().apply {
                        this.image = compressedImage
                    })
                    imagesPrvt.add(ResponsePrivatePics().apply {
                        this.path = uri.toString()
                    })

                    privateAlbumAdapter.update(ResponsePrivatePics().apply {
                        this.path = compressedImage.absolutePath
                        this.type = "image"
                    })
                    Log.e("PrivateAlbum", "Get_img_data_Done")
                } else {
                    Log.e("PrivateAlbum", "Image file is invalid or does not exist: $imagePath")
                }
            } else {
                Log.e("PrivateAlbum", "Failed to retrieve file path from URI: $uri")
            }
        }

        if (imagesPrvt.isNotEmpty()) {
            Log.e("Private_Album", "RecyclePrivateAlbumAdapter: Switch_Activity")
            val jsonImages = Gson().toJson(imagesPrvt) // Convert list to JSON string
            val intent = Intent(this, ImagePagerActivity::class.java)
            intent.putExtra(
                Constants.IntentDataKeys.PrivateAlbumImages, jsonImages
            ) // Pass JSON string
            intent.putExtra(Constants.IntentDataKeys.Type, Constants.IntentDataKeys.ImgUri)
            intent.putExtra(
                Constants.IntentDataKeys.Class, Constants.IntentDataKeys.PrivateAlbumImages
            )
            intent.putExtra("position", 0)
            /// startActivity(intent)
            startActivityForResult(intent, PREVIEW_REQUEST_CODE)

            /* Log.e("ActivityImgVideo", "FromChat: $fileUri")
             val intent = Intent(this@PrivateAlbumActivity, ImageVideoPreviewActivity::class.java)
             intent.putExtra(Constants.IntentDataKeys.ImgPath, fileUri.toString())
             // startActivity(intent)
             startActivityForResult(intent, PREVIEW_REQUEST_CODE)*/
        }

    }

    @SuppressLint("ResourceType")
    private fun openBottomSheet() {
        val binding = BottomSheetGetPickBinding.inflate(layoutInflater)
        val dialog = BottomSheetDialog(this, R.style.BottomSheetStyle)
        dialog.setContentView(binding.root)
        val mediaUploadType =
            sharedPreferencesUtil.fetchUserProfile().currentPlanInfo!!.mediaUploadType

        binding.llCamera.setOnClickListener {
            camerarequestRuntimePermission()
            dialog.dismiss()  // Dismiss the bottom sheet
        }
        binding.llImage.setOnClickListener {
            EasyImage.openGalleryMultiImage(
                this@PrivateAlbumActivity, Constants.ImagePicker.Image
            )
            dialog.dismiss()  // Dismiss the bottom sheet
        }
        binding.llVideo.setOnClickListener {
            if (mediaUploadType == 0) {
                universalToast(getString(R.string.restricted_video_uploading))
            } else {
                val intent = Intent(Intent.ACTION_GET_CONTENT)
                intent.addCategory(Intent.CATEGORY_OPENABLE)
                intent.type = "video/*"
                startActivityForResult(intent, Constants.ImagePicker.Video)
                dialog.cancel()
            }
        }

        dialog.show()
    }

    @Suppress("DEPRECATION", "NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    @SuppressLint("SetWorldWritable")
    private fun uploadVideoFile(uri: Uri) {

        try {

            videoPath = Path.getPath(this@PrivateAlbumActivity, uri)
            Log.i("sss", "From Gallery 1 URI_Path -- $videoPath")

            val file = File(videoPath)
            Log.d("Private_Album", "uploadVideoFile22: " + privatePicsLocal.size)

            privatePicsLocal.add(PrivateAlbumModel().apply {
                this.image = file
            })

            privateAlbumAdapter.update(ResponsePrivatePics().apply {
                this.path = file.absolutePath
                this.type = "video"
            })
            imagesPrvt.clear()
            imagesPrvt.add(ResponsePrivatePics().apply {
                this.path = uri.toString()
            })

            if (imagesPrvt.isNotEmpty()) {
                Log.e("Private_Album", "RecyclePrivateAlbumAdapter: Switch_Activity")
                val jsonImages = Gson().toJson(imagesPrvt) // Convert list to JSON string
                val intent = Intent(this, ImagePagerActivity::class.java)
                intent.putExtra(
                    Constants.IntentDataKeys.PrivateAlbumImages, jsonImages
                ) // Pass JSON string
                intent.putExtra(Constants.IntentDataKeys.Type, Constants.IntentDataKeys.VideoUrl)
                intent.putExtra(
                    Constants.IntentDataKeys.Class, Constants.IntentDataKeys.PrivateAlbumImages
                )
                intent.putExtra("position", 0)
                startActivityForResult(intent, PREVIEW_REQUEST_CODE)
            }


        } catch (e: Exception) {
            println("error in uploading video")
            e.printStackTrace()
        }
    }

    private fun rotateBitmap(bitmap: Bitmap, orientation: Int): Bitmap? {
        val matrix = Matrix()
        when (orientation) {
            ExifInterface.ORIENTATION_NORMAL -> {
                return bitmap
            }

            ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> matrix.setScale((-1).toFloat(), 1F)
            ExifInterface.ORIENTATION_ROTATE_180 -> matrix.setRotate(180F)
            ExifInterface.ORIENTATION_FLIP_VERTICAL -> {
                matrix.setRotate(180F)
                matrix.postScale((-1).toFloat(), 1F)
            }

            ExifInterface.ORIENTATION_TRANSPOSE -> {
                matrix.setRotate(90F)
                matrix.postScale((-1).toFloat(), 1F)
            }

            ExifInterface.ORIENTATION_ROTATE_90 -> matrix.setRotate(90F)
            ExifInterface.ORIENTATION_TRANSVERSE -> {
                matrix.setRotate((-90).toFloat())
                matrix.postScale((-1).toFloat(), 1F)
            }

            ExifInterface.ORIENTATION_ROTATE_270 -> matrix.setRotate((-90).toFloat())

            else -> return bitmap
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

    private fun uploadPics() {
        if (null == mPresenter) mPresenter = PrivateAlbumPresenter(presenter)
        Log.d("Private_Album%", "uploadPics: " + privatePicsLocal.size)
        run {
            mPresenter!!.addObjectPrivateAlbum(privatePicsLocal)
            mPresenter!!.callRetrofit(ConstantsApi.UPLOAD_PRIVATE)
        }
    }

    private fun fetchPrivateAlbum() {
        if (null == mPresenter) mPresenter = PrivateAlbumPresenter(presenter)

        run {
            mPresenter!!.callRetrofit(ConstantsApi.PRIVATE_ALBUM)
        }
    }

    private fun fetchPrivateAccessList() {
        if (null == mPresenter) mPresenter = PrivateAlbumPresenter(presenter)

        run {
            mPresenter!!.callRetrofit(ConstantsApi.PRIVATE_ACCESS)
        }
    }

    private fun fetchPrivateAccessListOthers() {
        if (null == mPresenter) mPresenter = PrivateAlbumPresenter(presenter)

        run {
            mPresenter!!.callRetrofit(ConstantsApi.PRIVATE_ACCESS_OTHERS)
        }
    }

    private fun sharePrivateAccess(userId: String) {
        if (null == mPresenter) mPresenter = PrivateAlbumPresenter(presenter)

        run {
            mPresenter!!.addObjectForPrivateAlbum(userId)
            mPresenter!!.callRetrofit(ConstantsApi.SHARE_PRIVATE)
        }
    }

    private fun removePrivateAccess() {
        if (null == mPresenter) mPresenter = PrivateAlbumPresenter(presenter)

        run {
            mPresenter!!.callRetrofit(ConstantsApi.REMOVE_PRIVATE)
        }
    }

    private fun deleteImage(id: Int) {
        if (null == mPresenter) mPresenter = PrivateAlbumPresenter(presenter)

        showProgressView(progressBar)

        run {
            mPresenter!!.addObjectForDeleteImage(id)
            mPresenter!!.callRetrofit(ConstantsApi.DELETE_PRIVATE)
        }

    }

    private fun showAlertProfile(message: String, title: String) {
        val alertDialog = android.app.AlertDialog.Builder(
            this@PrivateAlbumActivity, R.style.MyDialogTheme2
        )
        alertDialog.setTitle(title)
        alertDialog.setMessage(message)
        alertDialog.setPositiveButton(getString(R.string.text_okay)) { dialog, _ ->
            dialog.dismiss()
            /// selectImagePopup()
            openBottomSheet()
        }
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    private fun showAlertUnshare(message: String, position: String) {
        val alertDialog = AlertDialog.Builder(this@PrivateAlbumActivity, R.style.MyDialogTheme2)
        alertDialog.setMessage(message)
        alertDialog.setPositiveButton(getString(R.string.text_Unshare)) { dialog, _ ->
            dialog.dismiss()
            showProgressView(progressBar)
            sharePrivateAccess(position)
        }
        alertDialog.setNegativeButton(getString(R.string.label_cancel_text)) { dialog, _ ->
            dialog.cancel()
        }
        alertDialog.setCancelable(false)
        // Showing Alert Message
        alertDialog.show()
    }

    private fun showAlertUnshare(message: String) {
        val alertDialog = AlertDialog.Builder(this@PrivateAlbumActivity, R.style.MyDialogTheme2)
        alertDialog.setMessage(message)
        alertDialog.setPositiveButton(getString(R.string.text_Unshare)) { dialog, _ ->
            dialog.dismiss()
            showProgressView(progressBar)
            removePrivateAccess()
        }
        alertDialog.setNegativeButton(getString(R.string.label_cancel_text)) { dialog, _ ->
            dialog.cancel()
        }
        alertDialog.setCancelable(false)
        // Showing Alert Message
        alertDialog.show()
    }

    private fun showAlertDelete(position: Int) {
        val alertDialog =
            android.app.AlertDialog.Builder(this@PrivateAlbumActivity, R.style.MyDialogTheme2)
        alertDialog.setMessage(getText(R.string.alert_delete))
        alertDialog.setPositiveButton(getString(R.string.text_delete)) { dialog, _ ->
            dialog.dismiss()
            val id = privateAlbumAdapter.imageId(position)
            if (id == -1) {
                val image = privateAlbumAdapter.delete(position)
                val index =
                    privatePicsLocal.firstOrNull { it.image?.absolutePath == image.path }?.let {
                        privatePicsLocal.indexOf(it)
                    } ?: -1
                if (index > -1) {
                    privatePicsLocal.removeAt(index)
                }

            } else {
                deleteImage(id)
            }
        }
        alertDialog.setNegativeButton(getString(R.string.label_cancel_text)) { dialog, _ ->
            dialog.cancel()
        }
        alertDialog.setCancelable(false)
        // Showing Alert Message
        alertDialog.show()
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

}
