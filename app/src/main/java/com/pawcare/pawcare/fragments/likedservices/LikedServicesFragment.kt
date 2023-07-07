package com.pawcare.pawcare.fragments.likedservices

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
import com.pawcare.pawcare.databinding.FragmentLikedServicesBinding
import com.pawcare.pawcare.fragments.explore.adapter.ServiceAdapter
import com.pawcare.pawcare.fragments.explore.model.Service
import com.pawcare.pawcare.libraries.LoadingDialog
import com.pawcare.pawcare.services.ApiInterface
import com.pawcare.pawcare.services.Listener


class LikedServicesFragment : Fragment() {

    private var binding: FragmentLikedServicesBinding? = null
    private lateinit var loadingDialog: LoadingDialog

    private lateinit var recyclerViewServices: RecyclerView
    private var services: MutableList<ApiInterface.Sitter> = mutableListOf()
    private lateinit var serviceAdapter: ServiceAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val fragmentBinding = FragmentLikedServicesBinding.inflate(inflater, container, false)
        binding = fragmentBinding

        App.instance.mainActivity!!.findViewById<LinearLayout>(R.id.bottombar).visibility = View.GONE

        return fragmentBinding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadingDialog = LoadingDialog(requireContext())
        Utils.navigationBar(view, getString(R.string.liked_petsitters), requireActivity())

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
                bundle.putParcelable("SITTER", item)

                //findNavController().navigate(R.id.action_likedServicesFragment_to_sitterInfoFragment, bundle)
                findNavController().apply {
                    popBackStack(R.id.likedServicesFragment, false)
                    navigate(R.id.sitterInfoFragment, bundle)
                }


            }

        })

        serviceAdapter.setOnItemClickListener2(object : ServiceAdapter.onItemClickListener2 {
            override fun onItemClick(position: Int) {

                val item = serviceAdapter.getItem(position)

                loadingDialog.startLoading()

                App.instance.backOffice.getFavourite(object : Listener<Any> {
                    override fun onResponse(response: Any?) {

                        if (isAdded) {

                            if (response != null && response is ApiInterface.Favourite) {

                                //if exists so will remove

                                App.instance.backOffice.deleteFavourite(object: Listener<Any> {
                                    override fun onResponse(response: Any?) {

                                        loadingDialog.isDismiss()

                                        if (isAdded) {

                                            if (response == null) {

                                                serviceAdapter.notifyItemChanged(position)

                                            }

                                        }

                                    }

                                }, item.sitterId!!)


                            }
                            else {

                                // if exists will add

                                App.instance.backOffice.addFavourite(object: Listener<Any> {
                                    override fun onResponse(response: Any?) {

                                        loadingDialog.isDismiss()

                                        if (isAdded) {

                                            if (response == null) {

                                                serviceAdapter.notifyItemChanged(position)

                                            }

                                        }

                                    }

                                }, item.sitterId!!)


                            }

                        }

                    }
                }, item.sitterId!!)


            }

        })

    }

    private fun addServicesToList() {

        services.clear()

        loadingDialog.startLoading()

        App.instance.backOffice.getFavourites(object : Listener<Any> {
            override fun onResponse(response: Any?) {

                loadingDialog.isDismiss()

                if (isAdded) {

                    if (response != null && response is List<*>) {

                        val list = response as List<ApiInterface.Sitter>

                        if (list.isNotEmpty()) {

                            binding!!.services.visibility = View.VISIBLE
                            binding!!.empty.visibility = View.GONE
                            services.addAll(list)
                            serviceAdapter.notifyDataSetChanged()
                        }
                        else {
                            binding!!.services.visibility = View.GONE
                            binding!!.empty.visibility = View.VISIBLE
                        }

                    }
                    else {
                        binding!!.services.visibility = View.GONE
                        binding!!.empty.visibility = View.VISIBLE
                    }

                }

            }
        })

    }

}