package com.pawcare.pawcare.fragments.explore

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.pawcare.pawcare.App
import com.pawcare.pawcare.R
import com.pawcare.pawcare.databinding.FragmentExploreBinding
import com.pawcare.pawcare.libraries.LoadingDialog

class ExploreFragment : Fragment() {

    private var binding: FragmentExploreBinding? = null
    private lateinit var loadingDialog: LoadingDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val fragmentBinding = FragmentExploreBinding.inflate(inflater, container, false)
        binding = fragmentBinding

        App.instance.mainActivity.findViewById<LinearLayout>(R.id.bottombar).visibility = View.VISIBLE

        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



    }

}