package com.pawcare.pawcare.fragments.services

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.pawcare.pawcare.App
import com.pawcare.pawcare.R
import com.pawcare.pawcare.Utils
import com.pawcare.pawcare.databinding.FragmentServicesBinding
import com.pawcare.pawcare.fragments.explore.adapter.ServiceAdapter
import com.pawcare.pawcare.fragments.explore.model.Service

class ServicesFragment : Fragment() {

    private var binding: FragmentServicesBinding? = null

    private lateinit var recyclerViewServices: RecyclerView
    private var services: MutableList<Service> = mutableListOf()
    private lateinit var serviceAdapter: ServiceAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val fragmentBinding = FragmentServicesBinding.inflate(inflater, container, false)
        binding = fragmentBinding

        App.instance.mainActivity.findViewById<LinearLayout>(R.id.bottombar).visibility = View.GONE

        return fragmentBinding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Utils.navigationBar(view, "Services", requireActivity())

        recyclerViewServices = binding!!.services
        recyclerViewServices.setHasFixedSize(true)
        recyclerViewServices.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)

        addServicesToList()

        serviceAdapter = ServiceAdapter(services)
        recyclerViewServices.adapter = serviceAdapter

        serviceAdapter.setOnItemClickListener(object : ServiceAdapter.onItemClickListener {
            override fun onItemClick(position: Int) {

                val item = serviceAdapter.getItem(position)

                val bundle = Bundle()
                bundle.putString("SITTER_NAME", item.name)

                findNavController().navigate(R.id.action_exploreFragment2_to_sitterInfoFragment2)


            }

        })

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