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

        App.instance.backOffice.getPicturesSitter(object : Listener<Any> {
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
        }, sitter!!.userId!!)

        App.instance.backOffice.getFavourite(object : Listener<Any> {
            override fun onResponse(response: Any?) {

                if (isAdded) {

                    if (response != null && response is ApiInterface.Favourite) {

                        binding!!.like.setImageResource(R.drawable.like_red)

                    }
                    else {
                        binding!!.like.setImageResource(R.drawable.heart_icon)
                    }

                }

            }

        }, sitter!!.sitterId!!)

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

        if (sitter!!.petwalking!!) {

            binding!!.petwalking.visibility = View.VISIBLE
            binding!!.petwalkingDesc.text = "A nice walk for your pet\n${sitter!!.ratewalkingaddpet}€ for each addicional pet"
            binding!!.petwalkingPrice.text = "${sitter!!.ratewalking}€\n/walk"

        }

        if (sitter!!.petboarding!!) {

            binding!!.petboarding.visibility = View.VISIBLE
            binding!!.petboardingDesc.text = "Stay with the sitter, day and night\n${sitter!!.ratewalkingaddpet}€ for each addicional pet"
            binding!!.petboardingPrice.text = "${sitter!!.ratewalking}€\n/night"

        }

        if (sitter!!.housesitting!!) {

            binding!!.housesitting.visibility = View.VISIBLE
            binding!!.housesittingDesc.text = "The sitter will stay at your home\n${sitter!!.ratewalkingaddpet}€ for each addicional pet"
            binding!!.housesittingPrice.text = "${sitter!!.ratewalking}€\n/night"

        }

        if (sitter!!.training!!) {

            binding!!.pettraining.visibility = View.VISIBLE
            binding!!.pettrainingDesc.text = "Stay with the sitter during the day\n${sitter!!.ratewalkingaddpet}€ for each addicional pet"
            binding!!.pettrainingPrice.text = "${sitter!!.ratewalking}€\n/training"

        }

        if (sitter!!.grooming!!) {

            binding!!.petgrooming.visibility = View.VISIBLE
            binding!!.petgroomingDesc.text = "Stay with the sitter during the day\n${sitter!!.ratewalkingaddpet}€ for each addicional pet"
            binding!!.petgroomingPrice.text = "${sitter!!.ratewalking}€\n/day"

        }

        binding!!.like.setOnClickListener {

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

                                            binding!!.like.setImageResource(R.drawable.heart_icon)

                                        }

                                    }

                                }

                            }, sitter!!.sitterId!!)


                        }
                        else {

                            // if exists will add

                            App.instance.backOffice.addFavourite(object: Listener<Any> {
                                override fun onResponse(response: Any?) {

                                    loadingDialog.isDismiss()

                                    if (isAdded) {

                                        if (response == null) {

                                            binding!!.like.setImageResource(R.drawable.like_red)

                                        }

                                    }

                                }

                            }, sitter!!.sitterId!!)


                        }

                    }

                }
            }, sitter!!.sitterId!!)

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

            val bundle = Bundle()
            bundle.putParcelable("SITTER", sitter)

            findNavController().navigate(R.id.action_sitterInfoFragment_to_bookingDetailsFragment, bundle)

        }

        binding!!.messageBtn.setOnClickListener {

            val bundle = Bundle()
            bundle.putString("SITTERID", sitter!!.sitterId)

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