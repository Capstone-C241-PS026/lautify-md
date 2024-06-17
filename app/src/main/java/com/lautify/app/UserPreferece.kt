package com.lautify.app

import android.content.Context
import android.content.SharedPreferences

class UserPreferece(context: Context) {
    private val PREFS_NAME = "user_prefs"
    private val EMAIL = "user_email"
    private val USER_ID = "user_id"
    private val USER_TOKEN = "user_token"
    private val IS_LOGGED_IN = "is_logged_in"
    private val sharedPref: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun saveUserId(userId: String) {
        val editor = sharedPref.edit()
        editor.putString(USER_ID, userId)
        editor.apply()
    }

    fun getUserId(): String? {
        return sharedPref.getString(USER_ID, null)
    }

    fun saveUserToken(token: String) {
        val editor = sharedPref.edit()
        editor.putString(USER_TOKEN, token)
        editor.apply()
    }

    fun getUserToken(): String? {
        return sharedPref.getString(USER_TOKEN, null)
    }

    fun saveUsername(username: String) {
        val editor = sharedPref.edit()
        editor.putString(PREFS_NAME, username)
        editor.apply()
    }

    fun getUsername(): String? {
        return sharedPref.getString(PREFS_NAME, null)
    }

    fun saveEmail(email: String) {
        val editor = sharedPref.edit()
        editor.putString(EMAIL, email)
        editor.apply()
    }

    fun getEmail(): String? {
        return sharedPref.getString(EMAIL, null)
    }

    fun saveLoginStatus(isLoggedIn: Boolean) {
        val editor = sharedPref.edit()
        editor.putBoolean(IS_LOGGED_IN, isLoggedIn)
        editor.apply()
    }

    fun isLoggedIn(): Boolean {
        return sharedPref.getBoolean(IS_LOGGED_IN, false)
    }

    fun clear() {
        val editor = sharedPref.edit()
        editor.clear()
        editor.apply()
    }
}
