package com.pawcare.pawcare.fragments.sitterapplication

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.navigation.fragment.findNavController
import com.pawcare.pawcare.App
import com.pawcare.pawcare.R
import com.pawcare.pawcare.Utils
import com.pawcare.pawcare.databinding.FragmentProgressApplicationBinding


class ProgressApplicationFragment : Fragment() {

    private var binding: FragmentProgressApplicationBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val fragmentBinding = FragmentProgressApplicationBinding.inflate(inflater, container, false)
        binding = fragmentBinding

        App.instance.mainActivity.findViewById<LinearLayout>(R.id.bottombar).visibility = View.GONE

        return fragmentBinding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Utils.navigationBar(view, "Sitter Application", requireActivity())


        binding!!.step1.setOnClickListener {

            val bundle = Bundle()
            bundle.putInt("STEP", 1)
            findNavController().navigate(R.id.action_progressApplicationFragment_to_applicationAnswerFragment, bundle)

        }

        binding!!.step2.setOnClickListener {

            val bundle = Bundle()
            bundle.putInt("STEP", 2)
            findNavController().navigate(R.id.action_progressApplicationFragment_to_applicationAnswerFragment, bundle)

        }

        binding!!.step3.setOnClickListener {

            val bundle = Bundle()
            bundle.putInt("STEP", 3)
            findNavController().navigate(R.id.action_progressApplicationFragment_to_applicationAnswerFragment, bundle)

        }

        binding!!.step4.setOnClickListener {

            val bundle = Bundle()
            bundle.putInt("STEP", 4)
            findNavController().navigate(R.id.action_progressApplicationFragment_to_applicationAnswerFragment, bundle)

        }

        binding!!.step5.setOnClickListener {

            val bundle = Bundle()
            bundle.putInt("STEP", 5)
            findNavController().navigate(R.id.action_progressApplicationFragment_to_applicationAnswerFragment, bundle)

        }

        binding!!.step6.setOnClickListener {

            val bundle = Bundle()
            bundle.putInt("STEP", 6)
            findNavController().navigate(R.id.action_progressApplicationFragment_to_applicationAnswerFragment, bundle)

        }

        binding!!.step7.setOnClickListener {

            val bundle = Bundle()
            bundle.putInt("STEP", 7)
            findNavController().navigate(R.id.action_progressApplicationFragment_to_applicationAnswerFragment, bundle)

        }

    }

}