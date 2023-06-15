package com.pawcare.pawcare.fragments.onboarding

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.pawcare.pawcare.App
import com.pawcare.pawcare.R
import com.pawcare.pawcare.databinding.FragmentOnBoardingBinding
import com.pawcare.pawcare.libraries.LoadingDialog

class OnBoardingFragment : Fragment() {

    private var binding: FragmentOnBoardingBinding? = null

    private var titleList = mutableListOf<String>()
    private var descList = mutableListOf<String>()
    private var imagesList = mutableListOf<Int>()

    private lateinit var loadingDialog: LoadingDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val fragmentBinding = FragmentOnBoardingBinding.inflate(inflater, container, false)
        binding = fragmentBinding

        return fragmentBinding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        App.instance.mainActivity.findViewById<View>(R.id.bottombar).visibility = View.GONE

        postToList()

        binding!!.viewPager2.adapter = ViewPagerAdapter(titleList, descList, imagesList)
        binding!!.viewPager2.orientation = ViewPager2.ORIENTATION_HORIZONTAL

        val indicator = binding!!.indicator
        indicator.setViewPager(binding!!.viewPager2)


        binding!!.signupBtn.setOnClickListener {
            findNavController().navigate(R.id.action_onBoardingFragment_to_registerFragment)
        }

        binding!!.loginBtn.setOnClickListener {
            findNavController().navigate(R.id.action_onBoardingFragment_to_loginFragment)
        }


    }

    private fun addToList(title: String, description: String, image: Int) {
        titleList.add(title)
        descList.add(description)
        imagesList.add(image)
    }

    private fun postToList() {
        titleList.clear()
        descList.clear()
        imagesList.clear()
        addToList(getString(R.string.onboarding_1_title), getString(R.string.onboarding_1_desc), R.drawable.onboarding1)
        addToList(getString(R.string.onboarding_2_title), getString(R.string.onboarding_2_desc), R.drawable.onboarding2)
        addToList(getString(R.string.onboarding_3_title), getString(R.string.onboarding_3_desc), R.drawable.onboarding3)
        addToList(getString(R.string.onboarding_4_title), getString(R.string.onboarding_4_desc), R.drawable.onboarding4)
    }

}