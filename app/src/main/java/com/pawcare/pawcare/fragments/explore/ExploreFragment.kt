package com.pawcare.pawcare.fragments.explore

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import com.denzcoskun.imageslider.constants.AnimationTypes
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.google.gson.JsonObject
import com.pawcare.pawcare.App
import com.pawcare.pawcare.R
import com.pawcare.pawcare.databinding.FragmentExploreBinding
import com.pawcare.pawcare.fragments.explore.adapter.CategoryAdapter
import com.pawcare.pawcare.fragments.explore.adapter.ServiceAdapter
import com.pawcare.pawcare.fragments.explore.model.Category
import com.pawcare.pawcare.fragments.explore.model.Service
import com.pawcare.pawcare.libraries.LoadingDialog
import com.pawcare.pawcare.services.ApiInterface
import com.pawcare.pawcare.services.Listener
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import java.lang.Exception

class ExploreFragment : Fragment() {

    private var binding: FragmentExploreBinding? = null
    private lateinit var loadingDialog: LoadingDialog

    private lateinit var recyclerViewCategories: RecyclerView
    private var categories: MutableList<Category> = mutableListOf()
    private lateinit var categoryAdapter: CategoryAdapter

    private lateinit var recyclerViewServices: RecyclerView
    private var services: MutableList<ApiInterface.Sitter> = mutableListOf()
    private lateinit var serviceAdapter: ServiceAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val fragmentBinding = FragmentExploreBinding.inflate(inflater, container, false)
        binding = fragmentBinding

        App.instance.mainActivity!!.findViewById<LinearLayout>(R.id.bottombar).visibility = View.VISIBLE

        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadingDialog = LoadingDialog(requireContext())

        binding!!.username.text = App.instance.preferences.getString("fullname", "")

        val userPhoto = binding!!.userPhoto
        val photoUrl = App.instance.preferences.getString("image", "")

        if (photoUrl != "") {

            Picasso.get()
                .load(photoUrl)
                .placeholder(R.drawable.profile_template)
                .error(R.drawable.profile_template)
                .into(userPhoto, object : Callback {
                    override fun onSuccess() {

                    }

                    override fun onError(e: Exception?) {

                    }

                })

        }



        binding!!.notificationsBtn.setOnClickListener {

            findNavController().navigate(R.id.action_exploreFragment2_to_notificationsFragment)

        }

        binding!!.likes.setOnClickListener {

            findNavController().navigate(R.id.action_exploreFragment2_to_likedServicesFragment)

        }

        val imageSlider = binding!!.slider

        val slideModels = arrayListOf<SlideModel>()

        slideModels.add(SlideModel(R.drawable.banner1))
        slideModels.add(SlideModel(R.drawable.banner2))
        //slideModels.add(SlideModel("https://picsum.photos/200/300?grayscale"))
        //slideModels.add(SlideModel("https://picsum.photos/200/300/?blur"))

        imageSlider.setImageList(slideModels, ScaleTypes.FIT)
        imageSlider.setSlideAnimation(AnimationTypes.FOREGROUND_TO_BACKGROUND)

        //Categories
        recyclerViewCategories = binding!!.categories
        recyclerViewCategories.setHasFixedSize(true)
        recyclerViewCategories.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
        //val snapHelper: SnapHelper = LinearSnapHelper()
        //snapHelper.attachToRecyclerView(recyclerViewCategories)

        addCategoriesToList()

        categoryAdapter = CategoryAdapter(categories)
        recyclerViewCategories.adapter = categoryAdapter

        categoryAdapter.setOnItemClickListener(object : CategoryAdapter.onItemClickListener {
            override fun onItemClick(position: Int) {

                val item = categoryAdapter.getItem(position)

                val bundle = Bundle()
                bundle.putString("SERVICE", item.label)

                findNavController().navigate(R.id.action_exploreFragment2_to_servicesFragment, bundle)

            }

        })

        //Services
        recyclerViewServices = binding!!.services
        recyclerViewServices.setHasFixedSize(true)
        recyclerViewServices.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        //snapHelper.attachToRecyclerView(recyclerViewServices)

        addServicesToList()

        serviceAdapter = ServiceAdapter(services)
        recyclerViewServices.adapter = serviceAdapter

        serviceAdapter.setOnItemClickListener(object : ServiceAdapter.onItemClickListener {
            override fun onItemClick(position: Int) {

                val item = serviceAdapter.getItem(position)

                val bundle = Bundle()
                bundle.putParcelable("SITTER", item)

                findNavController().navigate(R.id.action_exploreFragment2_to_sitterInfoFragment2, bundle)


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

        binding!!.allServicesBtn.setOnClickListener {

            findNavController().navigate(R.id.action_exploreFragment2_to_servicesFragment)

        }

    }

    private fun addCategoriesToList() {

        categories.clear()

        categories.add(Category("Pet\nWalking", R.drawable.walking_icon, "petwalking"))
        categories.add(Category("Pet\nBoarding", R.drawable.petboarding, "petboarding"))
        categories.add(Category("House\nSitting", R.drawable.sitting_icon, "housesitting"))
        categories.add(Category("Pet\nTraining", R.drawable.trainning, "training"))
        categories.add(Category("Pet\nGrooming", R.drawable.washing_icon, "grooming"))

    }

    private fun addServicesToList() {

        services.clear()

        loadingDialog.startLoading()

        App.instance.backOffice.getSitters(object : Listener<Any> {
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
        }, App.instance.preferences.getString("Latitude", "0")!!, App.instance.preferences.getString("Longitude", "0")!!, listOf())

    }

}