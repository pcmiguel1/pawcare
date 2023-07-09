package com.pawcare.pawcare

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.messaging.FirebaseMessaging
import com.pawcare.pawcare.fragments.bookings.BookingsFragment
import com.pawcare.pawcare.fragments.explore.ExploreFragment
import com.pawcare.pawcare.fragments.inbox.ChatFragment

class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private lateinit var navigationView: NavigationView
    private var countDownTimer: CountDownTimer? = null
    private var isTimerRunning = false

    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        App.instance.mainActivity = this

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        bottomNavigationView = findViewById(R.id.bottom_navigation_view)

        val inflater = navHostFragment.navController.navInflater
        val graph = inflater.inflate(R.navigation.nav)

        if (!checkNotificationPermission()) {
            requestNotificationPermission()
        }

        if (App.instance.preferences.getBoolean("stayLoggedIn", false)) {

            if (FirebaseMessaging.getInstance() != null) {
                FirebaseMessaging.getInstance().getToken().addOnSuccessListener(this) { instanceIdResult ->
                    val newToken = instanceIdResult

                    if (newToken != null) {
                        //App.instance.preferences.edit().putString("NotificationCode", newToken).apply()
                        App.instance.backOffice.postNotificationToken(newToken)
                    }
                }
            }

            getMyLocation()
            if (App.instance.preferences.getBoolean("SITTER", false)) {
                graph.setStartDestination(R.id.dashboardFragment)
            }
            else graph.setStartDestination(R.id.exploreFragment2)
        }
        else {
            graph.setStartDestination(R.id.onBoardingFragment)
        }

        navController.graph = graph

        bottomNavigationView.setupWithNavController(navController)

        if (App.instance.preferences.getBoolean("SITTER", false)) {

            bottomNavigationView.menu.findItem(R.id.exploreFragment).title = getString(R.string.dashboard)
            bottomNavigationView.menu.findItem(R.id.exploreFragment).icon = resources.getDrawable(R.drawable.dashboard)
            bottomNavigationView.menu.findItem(R.id.exploreFragment).isChecked = true

            bottomNavigationView.menu.findItem(R.id.bookingsFragment).title = getString(R.string.calendar)
            bottomNavigationView.menu.findItem(R.id.bookingsFragment).icon = resources.getDrawable(R.drawable.calendar)

        }


        bottomNavigationView.setOnItemSelectedListener {

            val shouldClearBackStack = true

            when (it.itemId) {

                R.id.exploreFragment -> {

                    if (App.instance.preferences.getBoolean("SITTER", false)) navController.navigate(R.id.dashboardFragment)
                    else navController.navigate(R.id.exploreFragment2)

                }

                R.id.bookingsFragment -> {

                    if (App.instance.preferences.getBoolean("SITTER", false)) navController.navigate(R.id.calendarFragment)
                    else navController.navigate(R.id.bookingsFragment2)

                }

                R.id.inboxFragment -> {
                    navController.navigate(R.id.inboxFragment2)
                }

                R.id.profileFragment -> {
                    navController.navigate(R.id.profileFragment2)
                }

            }

            /*if (shouldClearBackStack) {
                val backStackEntryCount = navController.backQueue.size
                for (i in 0 until backStackEntryCount) {
                    navController.popBackStack()
                }
            }*/

            true

        }

        navController.addOnDestinationChangedListener {_, destination, _ ->

            when (destination.id) {

                R.id.exploreFragment2 -> {
                    bottomNavigationView.menu.findItem(R.id.exploreFragment).isChecked = true
                }
                R.id.bookingsFragment2 -> {
                    bottomNavigationView.menu.findItem(R.id.bookingsFragment).isChecked = true
                }
                R.id.inboxFragment2 -> {
                    bottomNavigationView.menu.findItem(R.id.inboxFragment).isChecked = true
                }
                R.id.profileFragment2 -> {
                    bottomNavigationView.menu.findItem(R.id.profileFragment).isChecked = true
                }

            }
        }


    }

    fun popupError(error: String) {

        if (isTimerRunning) {
            countDownTimer?.cancel()
        }

        val alert = this.findViewById<LinearLayout>(R.id.popup_error)
        val alertText = this.findViewById<TextView>(R.id.popup_error_text)
        alertText.text = error
        this@MainActivity.runOnUiThread {
            slideDown(alert)
        }
        countDownTimer = object : CountDownTimer(3000, 3000) {
            override fun onTick(millisUntilFinished: Long) {}

            override fun onFinish() {
                this@MainActivity.runOnUiThread { slideUp(alert) }
                isTimerRunning = false
            }

        }
        countDownTimer?.start()
        isTimerRunning = true

    }

    private fun slideDown(view: View) {
        view.visibility = View.VISIBLE
        val animate = TranslateAnimation(
            Animation.RELATIVE_TO_SELF, //fromXType
            0f,  //fromXValue
            Animation.RELATIVE_TO_SELF,  //toXType
            0f, //toXValue
            Animation.RELATIVE_TO_SELF, //fromYType
            -1f, //fromYValue
            Animation.RELATIVE_TO_SELF, //toYType
            0f //toYValue
        )
        animate.duration = 500
        animate.fillAfter = true
        view.startAnimation(animate)
    }

    private fun slideUp(view: View) {
        val animate = TranslateAnimation(
            Animation.RELATIVE_TO_SELF, //fromXType
            0f,  //fromXValue
            Animation.RELATIVE_TO_SELF,  //toXType
            0f, //toXValue
            Animation.RELATIVE_TO_SELF, //fromYType
            0f, //fromYValue
            Animation.RELATIVE_TO_SELF, //toYType
            -1f //toYValue
        )
        animate.duration = 500
        animate.fillAfter = true
        view.startAnimation(animate)
    }

    private fun getMyLocation() {

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Check for location permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED
        ) {
            // Permission granted, fetch the location
            fetchLocation()
        } else {
            // Request location permission
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSION_REQUEST_CODE
            )
        }

    }

    private fun fetchLocation() {
        try {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location ->
                    if (location != null) {
                        val latitude = location.latitude
                        val longitude = location.longitude

                        App.instance.preferences.edit()

                            .putString("Latitude", latitude.toString())
                            .putString("Longitude", longitude.toString())

                            .apply()

                    }
                }
                .addOnFailureListener { exception ->
                    // Handle any errors that occurred during location retrieval
                    println("Failed to retrieve location: ${exception.message}")
                }
        } catch (e: SecurityException) {
            // Handle the case when the permission is not granted
            println("Location permission not granted: ${e.message}")
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Location permission granted, fetch the location
                fetchLocation()
            } else {
                // Location permission denied, handle it accordingly
                println("Location permission denied.")
            }
        }
    }

    companion object {
        private const val PERMISSION_REQUEST_CODE = 123
    }

    private fun checkNotificationPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val result = ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.POST_NOTIFICATIONS)
            result == PackageManager.PERMISSION_GRANTED
        } else true
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), 2)
        }
    }

}