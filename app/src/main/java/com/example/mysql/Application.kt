package com.example.mysql

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class Application: Application() {

    lateinit var sharedPreferences: SharedPreferences
        private set

    override fun onCreate() {
        super.onCreate()

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("myNewPrefs", Context.MODE_PRIVATE)
    }
}