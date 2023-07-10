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
import com.pawcare.pawcare.databinding.FragmentSitterApplicationBinding
import com.pawcare.pawcare.services.Listener

class SitterApplicationFragment : Fragment() {

    private var binding: FragmentSitterApplicationBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val fragmentBinding = FragmentSitterApplicationBinding.inflate(inflater, container, false)
        binding = fragmentBinding

        App.instance.mainActivity!!.findViewById<LinearLayout>(R.id.bottombar).visibility = View.GONE

        return fragmentBinding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Utils.navigationBar(view, "Sitter Application", requireActivity())

        val startApplicationBtn = binding!!.startApplicationBtn
        val rlprogresstartBtn = binding!!.rlprogresstart


        binding!!.startApplicationBtn.setOnClickListener {

            startApplicationBtn.visibility = View.GONE
            rlprogresstartBtn.visibility = View.VISIBLE

            App.instance.backOffice.startApplication(object : Listener<Any> {
                override fun onResponse(response: Any?) {

                    if (isAdded) {

                        startApplicationBtn.visibility = View.VISIBLE
                        rlprogresstartBtn.visibility = View.GONE

                        if (response == null) {

                            findNavController().navigate(R.id.action_sitterApplicationFragment_to_progressApplicationFragment)

                        }

                    }

                }

            })

        }

    }

}