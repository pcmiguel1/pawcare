package com.pawcare.pawcare.fragments.explore

import android.os.Bundle
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
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.pawcare.pawcare.App
import com.pawcare.pawcare.R
import com.pawcare.pawcare.databinding.FragmentExploreBinding
import com.pawcare.pawcare.fragments.explore.adapter.CategoryAdapter
import com.pawcare.pawcare.fragments.explore.adapter.ServiceAdapter
import com.pawcare.pawcare.fragments.explore.model.Category
import com.pawcare.pawcare.fragments.explore.model.Service
import com.pawcare.pawcare.libraries.LoadingDialog

class ExploreFragment : Fragment() {

    private var binding: FragmentExploreBinding? = null
    private lateinit var loadingDialog: LoadingDialog

    private lateinit var recyclerViewCategories: RecyclerView
    private var categories: MutableList<Category> = mutableListOf()
    private lateinit var categoryAdapter: CategoryAdapter

    private lateinit var recyclerViewServices: RecyclerView
    private var services: MutableList<Service> = mutableListOf()
    private lateinit var serviceAdapter: ServiceAdapter

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

        binding!!.notificationsBtn.setOnClickListener {

            findNavController().navigate(R.id.action_exploreFragment2_to_notificationsFragment)

        }

        binding!!.likes.setOnClickListener {

            findNavController().navigate(R.id.action_exploreFragment2_to_likedServicesFragment)

        }

        val imageSlider = binding!!.slider

        val slideModels = arrayListOf<SlideModel>()

        slideModels.add(SlideModel(R.drawable.back1))
        slideModels.add(SlideModel("https://picsum.photos/seed/picsum/200/300"))
        slideModels.add(SlideModel("https://picsum.photos/200/300?grayscale"))
        slideModels.add(SlideModel("https://picsum.photos/200/300/?blur"))

        imageSlider.setImageList(slideModels, ScaleTypes.FIT)


        //Categories
        recyclerViewCategories = binding!!.categories
        recyclerViewCategories.setHasFixedSize(true)
        recyclerViewCategories.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
        val snapHelper: SnapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(recyclerViewCategories)

        addCategoriesToList()

        categoryAdapter = CategoryAdapter(categories)
        recyclerViewCategories.adapter = categoryAdapter

        //Services
        recyclerViewServices = binding!!.services
        recyclerViewServices.setHasFixedSize(true)
        recyclerViewServices.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        //snapHelper.attachToRecyclerView(recyclerViewServices)

        addServicesToList()

        serviceAdapter = ServiceAdapter(services)
        recyclerViewServices.adapter = serviceAdapter

    }

    private fun addCategoriesToList() {

        categories.add(Category("Sitting", R.drawable.sitting_icon))
        categories.add(Category("Walking", R.drawable.walking_icon))
        categories.add(Category("Washing", R.drawable.washing_icon))
        categories.add(Category("More", R.drawable.more_icon))
        categories.add(Category("Training", R.drawable.sitting_icon))
        categories.add(Category("Grooming", R.drawable.walking_icon))
        categories.add(Category("Veterinary", R.drawable.washing_icon))
        categories.add(Category("More", R.drawable.more_icon))

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