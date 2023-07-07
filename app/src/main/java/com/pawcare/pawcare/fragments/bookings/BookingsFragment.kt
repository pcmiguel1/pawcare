package com.pawcare.pawcare.fragments.bookings

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.pawcare.pawcare.App
import com.pawcare.pawcare.R
import com.pawcare.pawcare.databinding.FragmentBookingsBinding
import com.pawcare.pawcare.fragments.bookings.adapter.ViewPagerAdapter

class BookingsFragment : Fragment() {

    private var binding: FragmentBookingsBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val fragmentBinding = FragmentBookingsBinding.inflate(inflater, container, false)
        binding = fragmentBinding

        App.instance.mainActivity!!.findViewById<LinearLayout>(R.id.bottombar).visibility = View.VISIBLE

        return fragmentBinding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tabLayout = binding!!.tabLayout
        val viewPager2 = binding!!.viewPager2

        val adapter = ViewPagerAdapter(childFragmentManager, lifecycle)

        viewPager2.adapter = adapter

        TabLayoutMediator(tabLayout, viewPager2) {tab, position ->
            when(position) {

                0 -> {
                    tab.text = getString(R.string.active)
                }
                1 -> {
                    tab.text = getString(R.string.completed)
                }

            }
        }.attach()


    }

}