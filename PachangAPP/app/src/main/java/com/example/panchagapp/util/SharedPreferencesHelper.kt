package com.example.panchagapp.util


import android.content.Context
import android.content.SharedPreferences

object SharedPreferencesHelper {
    // Function to save a string value in SharedPreferences
    fun saveString(context: Context, key: String, value: String) {
        val sharedPreferences = context.getSharedPreferences("MySharedPreferences", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(key, value)
        editor.apply()
    }
    fun saveBoolean(context: Context, key: String, value: Boolean) {
        val sharedPreferences = context.getSharedPreferences("MySharedPreferences", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean(key, value)
        editor.apply()
    }
    fun saveInt(context: Context, key: String, value: Int) {
        val sharedPreferences = context.getSharedPreferences("MySharedPreferences", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putInt(key, value)
        editor.apply()
    }

    // Function to retrieve a string value from SharedPreferences
    fun getString(context: Context, key: String): String? {
        val sharedPreferences = context.getSharedPreferences("MySharedPreferences", Context.MODE_PRIVATE)
        return sharedPreferences.getString(key, null)
    }
}