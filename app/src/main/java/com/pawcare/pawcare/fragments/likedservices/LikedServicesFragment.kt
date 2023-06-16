package com.pawcare.pawcare.fragments.likedservices

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.pawcare.pawcare.App
import com.pawcare.pawcare.R
import com.pawcare.pawcare.Utils
import com.pawcare.pawcare.databinding.FragmentLikedServicesBinding
import com.pawcare.pawcare.fragments.explore.adapter.ServiceAdapter
import com.pawcare.pawcare.fragments.explore.model.Service
import com.pawcare.pawcare.libraries.LoadingDialog


class LikedServicesFragment : Fragment() {

    private var binding: FragmentLikedServicesBinding? = null
    private lateinit var loadingDialog: LoadingDialog

    private lateinit var recyclerViewServices: RecyclerView
    private var services: MutableList<Service> = mutableListOf()
    private lateinit var serviceAdapter: ServiceAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val fragmentBinding = FragmentLikedServicesBinding.inflate(inflater, container, false)
        binding = fragmentBinding

        App.instance.mainActivity.findViewById<LinearLayout>(R.id.bottombar).visibility = View.GONE

        return fragmentBinding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Utils.navigationBar(view, getString(R.string.liked_services), requireActivity())

        recyclerViewServices = binding!!.services
        recyclerViewServices.setHasFixedSize(true)
        recyclerViewServices.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)

        addServicesToList()

        serviceAdapter = ServiceAdapter(services)
        recyclerViewServices.adapter = serviceAdapter

    }

    private fun addServicesToList() {

        services.add(Service("Steven Segal", 4.8f, 10.0))
        services.add(Service("Steven Segal", 4.8f, 10.0))
        services.add(Service("Steven Segal", 4.8f, 10.0))
        services.add(Service("Steven Segal", 4.8f, 10.0))
        services.add(Service("Steven Segal", 4.8f, 10.0))
        services.add(Service("Steven Segal", 4.8f, 10.0))

    }

}