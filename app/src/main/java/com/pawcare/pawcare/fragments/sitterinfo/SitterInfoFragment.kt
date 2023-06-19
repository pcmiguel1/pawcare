package com.pawcare.pawcare.fragments.sitterinfo

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.pawcare.pawcare.App
import com.pawcare.pawcare.R
import com.pawcare.pawcare.Utils
import com.pawcare.pawcare.databinding.FragmentSitterInfoBinding
import com.pawcare.pawcare.fragments.sitterinfo.adapter.ReviewAdapter
import com.pawcare.pawcare.fragments.sitterinfo.model.Review
import com.pawcare.pawcare.libraries.LoadingDialog


class SitterInfoFragment : Fragment() {

    private var binding: FragmentSitterInfoBinding? = null
    private lateinit var loadingDialog: LoadingDialog

    private lateinit var recyclerViewReviews: RecyclerView
    private var reviews: MutableList<Review> = mutableListOf()
    private lateinit var reviewAdapter: ReviewAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val fragmentBinding = FragmentSitterInfoBinding.inflate(inflater, container, false)
        binding = fragmentBinding

        App.instance.mainActivity.findViewById<LinearLayout>(R.id.bottombar).visibility = View.GONE

        return fragmentBinding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Utils.navigationBar(view, "", requireActivity())

        val imageSlider = binding!!.slider

        val slideModels = arrayListOf<SlideModel>()

        slideModels.add(SlideModel("https://picsum.photos/seed/picsum/400/300"))
        slideModels.add(SlideModel("https://picsum.photos/400/300?grayscale"))
        slideModels.add(SlideModel("https://picsum.photos/400/300/?blur"))

        imageSlider.setImageList(slideModels, ScaleTypes.FIT)

        //Reviews
        recyclerViewReviews = binding!!.reviews
        recyclerViewReviews.setHasFixedSize(true)
        recyclerViewReviews.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)

        addReviewsToList()

        reviewAdapter = ReviewAdapter(reviews)
        recyclerViewReviews.adapter = reviewAdapter


        binding!!.booknowBtn.setOnClickListener {

            findNavController().navigate(R.id.action_sitterInfoFragment_to_bookingDetailsFragment)

        }

        binding!!.messageBtn.setOnClickListener {

            val bundle = Bundle()
            bundle.putString("SITTER_NAME", "sddd")

            findNavController().navigate(R.id.action_sitterInfoFragment_to_chatFragment, bundle)

        }

    }

    private fun addReviewsToList() {

        reviews.clear()

        reviews.add(Review("Darlene Robertson", "My cat loves her she is just perfect !!", 4))
        reviews.add(Review("Darlene Robertson", "My cat loves her she is just perfect !!", 4))
        reviews.add(Review("Darlene Robertson", "My cat loves her she is just perfect !!", 4))

    }

}