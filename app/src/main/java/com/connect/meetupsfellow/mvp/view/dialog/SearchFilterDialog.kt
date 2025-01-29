package com.connect.meetupsfellow.mvp.view.dialog

import android.app.Activity
import android.app.Dialog
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import com.connect.meetupsfellow.R
import com.connect.meetupsfellow.application.ArchitectureApp
import com.connect.meetupsfellow.databinding.LayoutFilterNewBinding
import com.connect.meetupsfellow.retrofit.request.RequestFeed
import com.connect.meetupsfellow.retrofit.response.ResponseUserData


class SearchFilterDialog(private val activity: Activity) : Dialog(activity) {

    private val identity = arrayListOf<TextView>()
    private val members = arrayListOf<TextView>()
    private val sex = arrayListOf<CheckBox>()
    private val partner = arrayListOf<CheckBox>()
    private val general = arrayListOf<TextView>()
    private val handball = arrayListOf<TextView>()
    private val selectedIdentity = arrayListOf<String>()
    private val selectedMembers = arrayListOf<String>()
    private val selectedSex = arrayListOf<String>()
    private val selectedPartner = arrayListOf<String>()
    private val selectedGeneral = arrayListOf<String>()
    private val selectedHandball = arrayListOf<String>()
    private var isCheckedSex = false
    private var isCheckedStatus = false

    private lateinit var requestFeed: RequestFeed
    private var fetchUserProfile: ResponseUserData? = null
    private var binding: LayoutFilterNewBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LayoutFilterNewBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        //  setContentView(R.layout.layout_filter_new)
        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        window!!.clearFlags(
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM
        )

        if (binding!!.parent.layoutParams != null) {
            binding!!.parent.layoutParams.width =
                ArchitectureApp.instance!!.resources.displayMetrics.widthPixels
        }

        val llFellowStatus: LinearLayout = findViewById(R.id.llFellowStatus)

        if (fetchUserProfile?.currentPlanInfo!!.profileFilters.equals("advance")) {
            llFellowStatus.visibility = View.VISIBLE
        } else {
            llFellowStatus.visibility = View.GONE
        }

        binding!!.closeDialog.setOnClickListener {

            dismiss()
        }

        init()
    }

    private fun init() {

        sex.add(binding!!.maleCheck)
        sex.add(binding!!.femaleCheck)
        sex.add(binding!!.transCheck)
        sex.add(binding!!.nonCheck)

        partner.add(binding!!.rsSingleCb)
        partner.add(binding!!.rsOpenCb)
        partner.add(binding!!.rsMarriedCb)
        partner.add(binding!!.rsDatingCb)

        /*general.add(tvIntercourse1)
        general.add(tvIntercourse2)
        general.add(tvIntercourse3)

        handball.add(tvFisting1)
        handball.add(tvFisting2)
        handball.add(tvFisting3)

        identity.add(tvIdentity1)
        identity.add(tvIdentity2)
        identity.add(tvIdentity3)
        identity.add(tvIdentity4)
        identity.add(tvIdentity5)
        identity.add(tvIdentity6)
        identity.add(tvIdentity7)
        identity.add(tvIdentity8)
        identity.add(tvIdentity9)
        identity.add(tvIdentity10)
        identity.add(tvIdentity11)
        identity.add(tvIdentity12)
        identity.add(tvIdentity13)
        identity.add(tvIdentity14)
        identity.add(tvIdentity15)
        identity.add(tvIdentity16)
        identity.add(tvIdentity17)
        identity.add(tvIdentity18)
        identity.add(tvIdentity19)

        members.add(tvMember1)
        members.add(tvMember2)
        members.add(tvMember3)
        members.add(tvMember4)
        members.add(tvMember5)
        members.add(tvMember6)
        members.add(tvMember7)
        members.add(tvMember8)
        members.add(tvMember9)
        members.add(tvMember10)
        members.add(tvMember11)
        members.add(tvMember12)
        members.add(tvMember13)
        members.add(tvMember14)
        members.add(tvMember15)
        members.add(tvMember16)
        members.add(tvMember17)
        members.add(tvMember18)
        members.add(tvMember19)*/

        /*if (requestFeed.myIdentity.isNotEmpty()) {
            selectedIdentity.addAll(requestFeed.myIdentity.split(","))
            identity.asSequence().forEach {
                if (selectedIdentity.contains("${it.tag}")) {
                    it.isSelected = true
                }
            }
        }*/

        /*if (requestFeed.intoIdentity.isNotEmpty()) {
            selectedMembers.addAll(requestFeed.intoIdentity.split(","))
            members.asSequence().forEach {
                if (selectedMembers.contains("${it.tag}")) {
                    it.isSelected = true
                }
            }
        }*/

        /*if (requestFeed.generalIntercourse.isNotEmpty()) {
            selectedGeneral.addAll(requestFeed.generalIntercourse.split(","))
            general.asSequence().forEach {
                if (selectedGeneral.contains("${it.tag}")) {
                    it.isSelected = true
                }
            }
        }*/

        if (requestFeed.gender.isNotEmpty()) {
            selectedSex.addAll(requestFeed.gender.split(","))
            sex.asSequence().forEach {
                if (selectedSex.contains("${it.tag}")) {
                    it.isChecked = true

                    if (it.tag == context.getString(R.string.text_male)) {

                        setCheckBoxTextColorGradient(it)

                        binding!!.maleCheckLay.setBackgroundResource(R.drawable.rounded_corners_filter_selected)

                    }

                    if (it.tag == context.getString(R.string.text_female)) {

                        setCheckBoxTextColorGradient(it)

                        binding!!.femaleCheckLay.setBackgroundResource(R.drawable.rounded_corners_filter_selected)
                    }

                    if (it.tag == context.getString(R.string.text_trans)) {

                        setCheckBoxTextColorGradient(it)

                        binding!!.transCheckLay.setBackgroundResource(R.drawable.rounded_corners_filter_selected)
                    }

                    if (it.tag == context.getString(R.string.text_no_gender_con)) {

                        setCheckBoxTextColorGradient(it)

                        binding!!.nonCheckLay.setBackgroundResource(R.drawable.rounded_corners_filter_selected)
                    }

                } else {

                    it.isChecked = false
                }
            }
        }

        /* if (requestFeed.fistingId.isNotEmpty()) {
             selectedHandball.addAll(requestFeed.fistingId.split(","))
             handball.asSequence().forEach {
                 if (selectedHandball.contains("${it.tag}")) {
                     it.isSelected = true
                 }
             }
         }*/

        if (requestFeed.partnerStatus.isNotEmpty()) {
            selectedPartner.addAll(requestFeed.partnerStatus.split(","))
            partner.asSequence().forEach {
                if (selectedPartner.contains("${it.tag}")) {

                    Log.d("isChecked2", it.isChecked.toString())

                    it.isChecked = true

                    if (it.tag == context.getString(R.string.text_partner_status_1)) {

                        setCheckBoxTextColorGradient(it)

                        binding!!.rsSingleCbLay.setBackgroundResource(R.drawable.rounded_corners_filter_selected)

                    }

                    if (it.tag == context.getString(R.string.text_partner_status_3)) {

                        setCheckBoxTextColorGradient(it)

                        binding!!.rsMarriedCbLay.setBackgroundResource(R.drawable.rounded_corners_filter_selected)
                    }

                    if (it.tag == context.getString(R.string.text_partner_status_2)) {

                        setCheckBoxTextColorGradient(it)

                        binding!!.rsOpenCbLay.setBackgroundResource(R.drawable.rounded_corners_filter_selected)
                    }

                    if (it.tag == context.getString(R.string.text_partner_status_4)) {

                        setCheckBoxTextColorGradient(it)

                        binding!!.rsDatingCbLay.setBackgroundResource(R.drawable.rounded_corners_filter_selected)
                    }
                    //it.background = context.resources.getDrawable(R.drawable.rounded_corners_filter_selected)
                } else {

                    Log.d("isChecked2", it.isChecked.toString())

                    it.isChecked = false
                    //it.background = context.resources.getDrawable(R.drawable.rounded_corners_filter_default)
                }
            }
        }

        setClickListener()
    }

    private fun setClickListener() {
        binding!!.clearAllFilter.setOnClickListener {

            clearAll()

        }
        binding!!.filterBtn.setOnClickListener { dismiss() }

        identity.asSequence().forEach { it ->
            it.setOnClickListener {
                it.isSelected = !it.isSelected
                if (it.isSelected) selectedIdentity.add("${it.tag}") else selectedIdentity.remove("${it.tag}")
                selectedIdentity.sort()
                requestFeed.myIdentity =
                    selectedIdentity.toString().removeSurrounding("[", "]").replace(", ", ",")
            }
        }

        members.asSequence().forEach { it ->
            it.setOnClickListener {
                it.isSelected = !it.isSelected
                if (it.isSelected) selectedMembers.add("${it.tag}") else selectedMembers.remove("${it.tag}")
                selectedMembers.sort()
                requestFeed.intoIdentity =
                    selectedMembers.toString().removeSurrounding("[", "]").replace(", ", ",")
            }
        }

        sex.asSequence().forEach { it ->


            //Log.d("isChecked", isCheckedSex.toString())

            it.setOnClickListener { it1 ->
                //it.isChecked = !it.isChecked
                isCheckedSex = it.isChecked

                Log.d("isChecked", isCheckedSex.toString())

                if (isCheckedSex) {

                    Log.d("isChecked", isCheckedSex.toString())

                    if (it.tag == context.getString(R.string.text_male)) {

                        Log.d("isChecked", "select")

                        setCheckBoxTextColorGradient(it)

                        binding!!.maleCheckLay.setBackgroundResource(R.drawable.rounded_corners_filter_selected)

                    }

                    if (it.tag == context.getString(R.string.text_female)) {

                        setCheckBoxTextColorGradient(it)

                        binding!!.femaleCheckLay.setBackgroundResource(R.drawable.rounded_corners_filter_selected)
                    }

                    if (it.tag == context.getString(R.string.text_trans)) {

                        setCheckBoxTextColorGradient(it)

                        binding!!.transCheckLay.setBackgroundResource(R.drawable.rounded_corners_filter_selected)
                    }

                    if (it.tag == context.getString(R.string.text_no_gender_con)) {

                        setCheckBoxTextColorGradient(it)

                        binding!!.nonCheckLay.setBackgroundResource(R.drawable.rounded_corners_filter_selected)
                    }

                    isCheckedSex = true

                    selectedSex.add("${it.tag}")
                } else {

                    if (it.tag == context.getString(R.string.text_male)) {

                        setCheckBoxTextColorBlack(it)

                        binding!!.maleCheckLay.setBackgroundResource(R.drawable.rounded_corners_filter_default)
                    }

                    if (it.tag == context.getString(R.string.text_female)) {

                        setCheckBoxTextColorBlack(it)

                        binding!!.femaleCheckLay.setBackgroundResource(R.drawable.rounded_corners_filter_default)
                    }

                    if (it.tag == context.getString(R.string.text_trans)) {

                        setCheckBoxTextColorBlack(it)

                        binding!!.transCheckLay.setBackgroundResource(R.drawable.rounded_corners_filter_default)
                    }

                    if (it.tag == context.getString(R.string.text_no_gender_con)) {

                        setCheckBoxTextColorBlack(it)

                        binding!!.nonCheckLay.setBackgroundResource(R.drawable.rounded_corners_filter_default)
                    }

                    isCheckedSex = false

                    selectedSex.remove("${it.tag}")
                }


                selectedSex.sort()
                requestFeed.gender =
                    selectedSex.toString().removeSurrounding("[", "]").replace(", ", ",")
            }
        }

        /* general.asSequence().forEach { it ->
             it.setOnClickListener {
                 it.isSelected = !it.isSelected
                 if (it.isSelected) selectedGeneral.add("${it.tag}") else selectedGeneral.remove("${it.tag}")
                 selectedGeneral.sort()
                 requestFeed.generalIntercourse =
                     selectedGeneral.toString().removeSurrounding("[", "]").replace(", ", ",")
             }
         }*/

        partner.asSequence().forEach { it ->


            Log.d("isChecked1", it.isChecked.toString())



            it.setOnClickListener { it1 ->
                //it.isChecked = !it.isChecked

                isCheckedStatus = it.isChecked

                if (isCheckedStatus) {

                    Log.d("isChecked", isCheckedStatus.toString())

                    if (it.tag == context.getString(R.string.text_partner_status_1)) {

                        setCheckBoxTextColorGradient(it)

                        Log.d("isChecked", "select")

                        binding!!.rsSingleCbLay.setBackgroundResource(R.drawable.rounded_corners_filter_selected)

                    }

                    if (it.tag == context.getString(R.string.text_partner_status_2)) {

                        setCheckBoxTextColorGradient(it)

                        binding!!.rsOpenCbLay.setBackgroundResource(R.drawable.rounded_corners_filter_selected)
                    }

                    if (it.tag == context.getString(R.string.text_partner_status_3)) {

                        setCheckBoxTextColorGradient(it)

                        binding!!.rsMarriedCbLay.setBackgroundResource(R.drawable.rounded_corners_filter_selected)
                    }

                    if (it.tag == context.getString(R.string.text_partner_status_4)) {

                        setCheckBoxTextColorGradient(it)

                        binding!!.rsDatingCbLay.setBackgroundResource(R.drawable.rounded_corners_filter_selected)
                    }

                    isCheckedStatus = true

                    //it.background = context.resources.getDrawable(R.drawable.rounded_corners_filter_selected)

                    selectedPartner.add("${it.tag}")

                } else {

                    Log.d("isChecked", isCheckedStatus.toString())

                    //it.background = context.resources.getDrawable(R.drawable.rounded_corners_filter_default)

                    if (it.tag == context.getString(R.string.text_partner_status_1)) {

                        Log.d("isChecked", "default")

                        setCheckBoxTextColorBlack(it)

                        binding!!.rsSingleCbLay.setBackgroundResource(R.drawable.rounded_corners_filter_default)
                    }

                    if (it.tag == context.getString(R.string.text_partner_status_2)) {

                        setCheckBoxTextColorBlack(it)

                        binding!!.rsOpenCbLay.setBackgroundResource(R.drawable.rounded_corners_filter_default)
                    }

                    if (it.tag == context.getString(R.string.text_partner_status_3)) {

                        setCheckBoxTextColorBlack(it)

                        binding!!.rsMarriedCbLay.setBackgroundResource(R.drawable.rounded_corners_filter_default)
                    }

                    if (it.tag == context.getString(R.string.text_partner_status_4)) {

                        setCheckBoxTextColorBlack(it)

                        binding!!.rsDatingCbLay.setBackgroundResource(R.drawable.rounded_corners_filter_default)
                    }

                    isCheckedStatus = false

                    selectedPartner.remove("${it.tag}")

                }
                selectedPartner.sort()
                requestFeed.partnerStatus =
                    selectedPartner.toString().removeSurrounding("[", "]").replace(", ", ",")
            }
        }

        /*handball.asSequence().forEach { it ->
            it.setOnClickListener {
                it.isSelected = !it.isSelected
                if (it.isSelected) selectedHandball.add("${it.tag}") else selectedHandball.remove("${it.tag}")
                selectedHandball.sort()
                requestFeed.fistingId =
                    selectedHandball.toString().removeSurrounding("[", "]").replace(", ", ",")
            }
        }*/
    }

    private fun setCheckBoxTextColorBlack(box: CheckBox) {

        val paint = box.paint
        val widtht = paint.measureText(box.text.toString())
        val textShader: Shader = LinearGradient(
            0f, 0f, widtht, box.textSize, intArrayOf(
                Color.parseColor("#000000"), Color.parseColor("#000000")

            ), null, Shader.TileMode.REPEAT
        )

        box.paint.setShader(textShader)
    }

    private fun setCheckBoxTextColorGradient(box: CheckBox) {

        val paint = box.paint
        val widtht = paint.measureText(box.text.toString())
        val textShader: Shader = LinearGradient(
            0f, 0f, widtht, box.textSize, intArrayOf(
                Color.parseColor("#F4447E"), Color.parseColor("#8448F4")

            ), null, Shader.TileMode.REPEAT
        )

        box.paint.setShader(textShader)
    }

    private fun clearAll() {

        binding!!.maleCheckLay.setBackgroundResource(R.drawable.rounded_corners_filter_default)
        binding!!.femaleCheckLay.setBackgroundResource(R.drawable.rounded_corners_filter_default)
        binding!!.transCheckLay.setBackgroundResource(R.drawable.rounded_corners_filter_default)
        binding!!.nonCheckLay.setBackgroundResource(R.drawable.rounded_corners_filter_default)
        binding!!.rsSingleCbLay.setBackgroundResource(R.drawable.rounded_corners_filter_default)
        binding!!.rsOpenCbLay.setBackgroundResource(R.drawable.rounded_corners_filter_default)
        binding!!.rsMarriedCbLay.setBackgroundResource(R.drawable.rounded_corners_filter_default)
        binding!!.rsDatingCbLay.setBackgroundResource(R.drawable.rounded_corners_filter_default)
        isCheckedSex = false
        isCheckedStatus = false
        setCheckBoxTextColorBlack(binding!!.maleCheck)
        setCheckBoxTextColorBlack(binding!!.femaleCheck)
        setCheckBoxTextColorBlack(binding!!.nonCheck)
        setCheckBoxTextColorBlack(binding!!.transCheck)
        setCheckBoxTextColorBlack(binding!!.rsSingleCb)
        setCheckBoxTextColorBlack(binding!!.rsDatingCb)
        setCheckBoxTextColorBlack(binding!!.rsMarriedCb)
        setCheckBoxTextColorBlack(binding!!.rsOpenCb)

        var clear = false
        identity.asSequence().forEach {
            it.isSelected = false
            if (selectedIdentity.isNotEmpty()) {
                selectedIdentity.clear()
                requestFeed.myIdentity = ""
                clear = true
            }
        }

        members.asSequence().forEach {
            it.isSelected = false
            if (selectedMembers.isNotEmpty()) {
                selectedMembers.clear()
                requestFeed.intoIdentity = ""
                clear = true
            }
        }

        sex.asSequence().forEach {
            it.isChecked = false
            if (selectedSex.isNotEmpty()) {
                selectedSex.clear()
                requestFeed.gender = ""
                clear = true
            }
        }

        general.asSequence().forEach {
            it.isSelected = false
            if (selectedGeneral.isNotEmpty()) {
                selectedGeneral.clear()
                requestFeed.generalIntercourse = ""
                clear = true
            }
        }

        partner.asSequence().forEach {
            it.isChecked = false
            //it.background = context.resources.getDrawable(R.drawable.rounded_corners_filter_default)
            if (selectedPartner.isNotEmpty()) {
                selectedPartner.clear()
                requestFeed.partnerStatus = ""
                clear = true
            }
        }

        handball.asSequence().forEach {
            it.isSelected = false
            if (selectedHandball.isNotEmpty()) {
                selectedHandball.clear()
                requestFeed.fistingId = ""
                clear = true
            }
        }

        if (clear) {
        }
        //Toast.makeText(activity, "Filters cleared successfully", Toast.LENGTH_SHORT).show()
    }

    class Builder(
        private val activity: Activity,
        private val fetchUserProfile: ResponseUserData
    ) {
        private lateinit var requestFeed: RequestFeed

        internal fun feeds(requestFeed: RequestFeed): Builder {
            this.requestFeed = requestFeed
            return this
        }

        fun build(): SearchFilterDialog {
            val dialog = SearchFilterDialog(activity)
            dialog.requestFeed = this.requestFeed
            dialog.fetchUserProfile = this.fetchUserProfile

            dialog.show()
            return dialog
        }
    }

}