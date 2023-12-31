package com.pawcare.pawcare.fragments.notification

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.pawcare.pawcare.App
import com.pawcare.pawcare.R
import com.pawcare.pawcare.Utils
import com.pawcare.pawcare.databinding.FragmentNotificationBinding

class NotificationFragment : Fragment() {

    private var binding: FragmentNotificationBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val fragmentBinding = FragmentNotificationBinding.inflate(inflater, container, false)
        binding = fragmentBinding

        App.instance.mainActivity!!.findViewById<LinearLayout>(R.id.bottombar).visibility = View.GONE

        return fragmentBinding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Utils.navigationBar(view, getString(R.string.notifications), requireActivity())

    }

}