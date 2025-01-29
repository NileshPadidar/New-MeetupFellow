package com.connect.meetupsfellow.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.connect.meetupsfellow.log.LogManager
import com.connect.meetupsfellow.R

/**
 * Created by Jammwal on 10-03-2018.
 */
class UninstallIntentReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        val packageNames = intent!!.getStringArrayExtra("android.intent.extra.PACKAGES")
        LogManager.logger.i("DatingOnDl", "UninstallIntentReceiver Calls")
        Toast.makeText(context, "Uninstall Begins", Toast.LENGTH_LONG).show()
        if (packageNames != null) {
            for (packageName in packageNames) {
                if (packageName != null && packageName == context!!.getString(R.string.app_name)) {
                    // User has selected our application under the Manage Apps settings
                    // now initiating background thread to watch for activity
//                    ListenActivities(context).start()
                    Toast.makeText(context, "Uninstall Begins", Toast.LENGTH_LONG).show()

                }
            }
        }
    }
}