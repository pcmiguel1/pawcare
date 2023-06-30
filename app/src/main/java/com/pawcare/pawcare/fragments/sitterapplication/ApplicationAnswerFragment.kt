package com.pawcare.pawcare.fragments.sitterapplication

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.pawcare.pawcare.App
import com.pawcare.pawcare.R
import com.pawcare.pawcare.Utils
import com.pawcare.pawcare.databinding.FragmentApplicationAnswerBinding

class ApplicationAnswerFragment : Fragment() {

    private var binding: FragmentApplicationAnswerBinding? = null

    private var step = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val fragmentBinding = FragmentApplicationAnswerBinding.inflate(inflater, container, false)
        binding = fragmentBinding

        App.instance.mainActivity.findViewById<LinearLayout>(R.id.bottombar).visibility = View.GONE

        val bundle = arguments
        if (bundle != null) {
            step = bundle.getInt("STEP")
        }

        return fragmentBinding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Utils.navigationBar(view, "", requireActivity())

        when (step) {

            1 -> {

                binding!!.step1.visibility = View.VISIBLE

            }

            2 -> {

                binding!!.step2.visibility = View.VISIBLE

            }

            3 -> {

                binding!!.step1.visibility = View.VISIBLE

            }

            4 -> {

                binding!!.step1.visibility = View.VISIBLE

            }

            5 -> {

                binding!!.step1.visibility = View.VISIBLE

            }

            6 -> {

                binding!!.step6.visibility = View.VISIBLE

            }

            7 -> {

                binding!!.step1.visibility = View.VISIBLE

            }


        }

    }

}