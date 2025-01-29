package com.connect.meetupsfellow.components.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import com.connect.meetupsfellow.constants.Constants

open class CustomFragment : Fragment() {

    internal fun showProgressView(layout: View) {
        layout.visibility = View.VISIBLE
    }

    internal fun hideProgressView(layout: View) {
        layout.visibility = View.GONE
    }

    protected fun logout() {
        startActivity(
            Intent(Constants.Intent.Login)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        )
        requireActivity().finish()
    }

    protected fun universalToast(message: String) {
        if (null != activity){}
            //Toast.makeText(activity!!, message, Toast.LENGTH_SHORT).show()
    }

    internal fun switchActivity(activity: String, finish: Boolean, extras: Bundle?) {
        startActivity(Intent(activity).putExtras(extras!!))
    }
}