package com.pawcare.pawcare

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.pawcare.pawcare.fragments.bookings.BookingsFragment
import com.pawcare.pawcare.fragments.explore.ExploreFragment
import com.pawcare.pawcare.fragments.inbox.ChatFragment

class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private lateinit var navigationView: NavigationView
    private var countDownTimer: CountDownTimer? = null
    private var isTimerRunning = false

    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        App.instance.mainActivity = this

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        bottomNavigationView = findViewById(R.id.bottom_navigation_view)

        bottomNavigationView.setupWithNavController(navController)


        if (App.instance.preferences.getBoolean("SITTER", false)) {

            bottomNavigationView.menu.findItem(R.id.exploreFragment).title = getString(R.string.dashboard)
            bottomNavigationView.menu.findItem(R.id.exploreFragment).icon = resources.getDrawable(R.drawable.dashboard)
            bottomNavigationView.menu.findItem(R.id.exploreFragment).isChecked = true

            bottomNavigationView.menu.findItem(R.id.bookingsFragment).title = getString(R.string.calendar)
            bottomNavigationView.menu.findItem(R.id.bookingsFragment).icon = resources.getDrawable(R.drawable.calendar)

        }


        bottomNavigationView.setOnItemSelectedListener {

            when (it.itemId) {

                R.id.exploreFragment -> {

                    if (App.instance.preferences.getBoolean("SITTER", false)) navController.navigate(R.id.dashboardFragment)
                    else navController.navigate(R.id.exploreFragment2)

                }

                R.id.bookingsFragment -> {

                    if (App.instance.preferences.getBoolean("SITTER", false)) navController.navigate(R.id.bookingsFragment2)
                    else navController.navigate(R.id.bookingsFragment2)

                }

                R.id.inboxFragment -> {
                    navController.navigate(R.id.inboxFragment2)
                }

                R.id.profileFragment -> {
                    navController.navigate(R.id.profileFragment2)
                }

            }
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

}