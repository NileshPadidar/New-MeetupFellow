package com.connect.meetupsfellow.global.sharedPreferences

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences

class SharedPreferencesCustom {

    private var context: Context? = null
    private var keyPreference: String? = null

    constructor(activity: Activity, KEY_PREFERENCE: String) {
        this.keyPreference = KEY_PREFERENCE
        this.context = activity
    }

    constructor(context: Context, KEY_PREFERENCE: String) {
        this.keyPreference = KEY_PREFERENCE
        this.context = context
    }

    private
    val sharedPreferences: SharedPreferences
        get() = context!!.getSharedPreferences(keyPreference, Context.MODE_PRIVATE)

    private fun getFromSharedPreferences(KEY: String): String? {
        val value: String?
        value = try {
            val sp = sharedPreferences
            sp.getString(KEY, "")
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }

        return value
    }

    private fun putInSharedPreferences(KEY: String, VALUE: String) {
        try {
            val spEditor = sharedPreferences.edit()
            spEditor.putString(KEY, VALUE)
            spEditor.apply()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun putString(KEY: String, VALUE: String) {
        putInSharedPreferences(KEY, VALUE)
    }

    fun getString(KEY: String): String? = getFromSharedPreferences(KEY)

    fun removeString(KEY: String) {
        val editor = sharedPreferences.edit()
        editor.remove(KEY)
        editor.apply()
    }

    fun clearSharedPreferences() {
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()
    }
}
