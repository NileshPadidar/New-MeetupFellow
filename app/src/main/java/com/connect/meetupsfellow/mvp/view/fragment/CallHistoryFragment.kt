package com.connect.meetupsfellow.mvp.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import com.connect.meetupsfellow.R
import com.connect.meetupsfellow.application.ArchitectureApp
import com.connect.meetupsfellow.components.fragment.CustomFragment
import com.connect.meetupsfellow.developement.interfaces.SharedPreferencesUtil
import javax.inject.Inject

class CallHistoryFragment : CustomFragment() {

    lateinit var noCallLay : RelativeLayout

    @Inject
    lateinit var sharedPreferencesUtil: SharedPreferencesUtil

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        ArchitectureApp.component!!.inject(this@CallHistoryFragment)
        val view = inflater.inflate(R.layout.fragment_call_history, container, false)

        noCallLay = view.findViewById(R.id.noCallLay)

        noCallLay.visibility = View.VISIBLE

        return view
    }
}