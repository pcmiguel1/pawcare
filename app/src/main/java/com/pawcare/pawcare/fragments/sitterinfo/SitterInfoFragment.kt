package com.pawcare.pawcare.fragments.sitterinfo

import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
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
import com.pawcare.pawcare.services.ApiInterface
import com.pawcare.pawcare.services.Listener
import java.io.IOException
import java.util.*


class SitterInfoFragment : Fragment() {

    private var binding: FragmentSitterInfoBinding? = null
    private lateinit var loadingDialog: LoadingDialog

    private lateinit var recyclerViewReviews: RecyclerView
    private var reviews: MutableList<Review> = mutableListOf()
    private lateinit var reviewAdapter: ReviewAdapter

    private var sitter : ApiInterface.Sitter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val fragmentBinding = FragmentSitterInfoBinding.inflate(inflater, container, false)
        binding = fragmentBinding

        App.instance.mainActivity.findViewById<LinearLayout>(R.id.bottombar).visibility = View.GONE

        val bundle = arguments
        if (bundle != null) {

            if (bundle.containsKey("SITTER"))
                sitter = bundle.getParcelable("SITTER")

        }

        return fragmentBinding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadingDialog = LoadingDialog(requireContext())
        Utils.navigationBar(view, "", requireActivity())

        binding!!.name.text = sitter!!.name ?: ""

        val imageSlider = binding!!.slider

        val slideModels = arrayListOf<SlideModel>()

        loadingDialog.startLoading()

        App.instance.backOffice.getPictures(object : Listener<Any> {
            override fun onResponse(response: Any?) {

                loadingDialog.isDismiss()

                if (isAdded) {

                    if (response != null && response is List<*>) {

                        val list = response as List<ApiInterface.Picture>

                        if (list.isNotEmpty()) {

                            for (image in list) {

                                slideModels.add(SlideModel(image.url))

                            }
                            imageSlider.setImageList(slideModels, ScaleTypes.FIT)


                        }
                        else {

                        }

                    }
                    else {

                    }

                }

            }
        })

        val geocoder = Geocoder(requireContext(), Locale.getDefault())
        try {
            val addresses: List<Address> = geocoder.getFromLocation(sitter!!.lat!!.toDouble(), sitter!!.long!!.toDouble(), 1)!!
            if (addresses.isNotEmpty()) {
                val address: Address = addresses[0]
                val addressString = address.getAddressLine(0)

                binding!!.address.text = addressString

            }
        }
        catch (e: IOException) {
            println("Failed to retrieve address: ${e.message}")
        }

        binding!!.aboutme.text = sitter!!.description ?: ""

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