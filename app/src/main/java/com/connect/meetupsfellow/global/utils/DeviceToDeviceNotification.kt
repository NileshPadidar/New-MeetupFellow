package com.connect.meetupsfellow.global.utils

import android.annotation.SuppressLint
import android.os.AsyncTask
import android.util.Log
import com.connect.meetupsfellow.R
import com.connect.meetupsfellow.application.ArchitectureApp
import okhttp3.OkHttpClient
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

@SuppressLint("StaticFieldLeak")
object DeviceToDeviceNotification {
    private val mClient = OkHttpClient()


    fun sendNotification(deviceToken: String, msg: String, data: JSONObject) {

        val recipients = JSONArray()
        recipients.put(deviceToken)

        Log.d("Main Activity", "recipients: $recipients")
        object : AsyncTask<String, String, String>() {
            override fun doInBackground(vararg params: String): String? {
                try {
                    val root = JSONObject()
                    root.put("to", deviceToken)

                    val notification = JSONObject()
                    notification.put("body", msg)
                    notification.put("sound", "default")
                    notification.put("title", ArchitectureApp.instance!!.getString(R.string.app_name))

                    root.put("notification", notification)
                    root.put("data", data)

                    val notifica = JSONObject()
                    val notify = JSONObject()
                    notify.put("sound", "default")
                    notifica.put("notification", notify)
                    root.put("android", notifica)

                    val aps = JSONObject()
                    aps.put("sound", "default")
                    val payload = JSONObject()
                    payload.put("aps", aps)
                    val apns = JSONObject()
                    apns.put("payload", payload)
                    root.put("apns", apns)

                    val result = postToFCM(root.toString())
                    Log.d("Main Activity", "Result: $result")
                    return result
                } catch (ex: Exception) {
                    ex.printStackTrace()
                }

                return ""
            }

            override fun onPostExecute(result: String) {
                try {
                    val resultJson = JSONObject(result)
                    Log.d("Main Activity", "Result: $resultJson")
                    val success: Int
                    val failure: Int
                    success = resultJson.getInt("success")
                    failure = resultJson.getInt("failure")

                    if (failure == 1) {
//                        Toast.makeText(
//                            ArchitectureApp.instance, "Unable to send notification ", Toast.LENGTH_LONG
//                        ).show()
                    }

                } catch (e: JSONException) {
                    e.printStackTrace()
//                    Toast.makeText(this@MainActivity, "Message Failed, Unknown error occurred.", Toast.LENGTH_LONG)
//                            .show()
                }

            }
        }.execute()
    }

    @Throws(IOException::class)
    private fun postToFCM(bodyString: String): String {
//        val json = MediaType.parse("application/json; charset=utf-8")
//        val body = RequestBody.create(json, bodyString)
//        val request = Request.Builder()
//            .url(BuildConfig.URL_FCM)
//            .post(body)
//            .addHeader("Authorization", "key=${BuildConfig.SERVER_KEY}")
//            .addHeader("Content-Type", "application/json")
//            .build()
//        val response = mClient.newCall(request).execute()
//        return response.body()!!.string()
        return ""
    }


}
