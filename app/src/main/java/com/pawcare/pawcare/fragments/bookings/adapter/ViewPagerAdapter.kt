package com.pawcare.pawcare.fragments.bookings.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.pawcare.pawcare.fragments.bookings.ActiveBookingsFragment
import com.pawcare.pawcare.fragments.bookings.CompletedBookingsFragment

class ViewPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle): FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
       return when(position) {

            0 -> {
                ActiveBookingsFragment()
            }

            1 -> {
                CompletedBookingsFragment()
            }
            else -> {
                Fragment()
            }

        }
    }

}