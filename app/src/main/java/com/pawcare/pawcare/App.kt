package com.pawcare.pawcare

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.multidex.MultiDexApplication
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.pawcare.pawcare.services.BackOffice
import com.pawcare.pawcare.services.Backend

class App : MultiDexApplication(), DefaultLifecycleObserver {

    private lateinit var requests: Backend
    lateinit var preferences: SharedPreferences
    var mainActivity: MainActivity? = null

    val backOffice: BackOffice
        get() = requests.backOffice

    override fun onCreate() {
        super<MultiDexApplication>.onCreate()
        instance = this
        preferences = this.getSharedPreferences("pawcare", Context.MODE_PRIVATE)

        initFirebase()

        initBackend()

    }

    private fun initFirebase() {
        val options = FirebaseOptions.Builder()
            .setApplicationId(BuildConfig.FIREBASE_APP_ID)
            .setApiKey(BuildConfig.FIREBASE_API_KEY)
            .setProjectId(BuildConfig.PROJECT_ID)
            .build()

        if (FirebaseApp.getApps(this).isEmpty())
            FirebaseApp.initializeApp(this, options)

    }

    fun initBackend() {
        requests = Backend(this, preferences)
    }

    companion object {
        @get:Synchronized
        lateinit var instance: App
    }

}