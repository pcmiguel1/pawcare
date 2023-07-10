package com.pawcare.pawcare.fragments.bookingsdetails

import android.location.Address
import android.location.Geocoder
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
import com.pawcare.pawcare.databinding.FragmentBookingDetailsSitterBinding
import com.pawcare.pawcare.fragments.bookingsdetails.adapter.PetSitterAdapter
import com.pawcare.pawcare.services.ApiInterface
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import java.io.IOException
import java.lang.Exception
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

class BookingDetailsSitterFragment : Fragment() {

    private var binding: FragmentBookingDetailsSitterBinding? = null

    private lateinit var recyclerViewPets: RecyclerView
    private lateinit var petSitterAdapter: PetSitterAdapter

    private var booking : ApiInterface.Booking? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val fragmentBinding = FragmentBookingDetailsSitterBinding.inflate(inflater, container, false)
        binding = fragmentBinding

        App.instance.mainActivity!!.findViewById<LinearLayout>(R.id.bottombar).visibility = View.GONE

        val bundle = arguments
        if (bundle != null) {

            if (bundle.containsKey("BOOKING"))
                booking = bundle.getParcelable("BOOKING")

        }

        return fragmentBinding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Utils.navigationBar(view, "Booking Details", requireActivity())

        recyclerViewPets = binding!!.pets
        recyclerViewPets.setHasFixedSize(true)
        recyclerViewPets.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)

        petSitterAdapter = PetSitterAdapter(booking!!.pets!!)

        recyclerViewPets.adapter = petSitterAdapter

        if (booking!!.user!!.image != null && booking!!.user!!.image != "") {

            Picasso.get()
                .load(booking!!.user!!.image)
                .placeholder(R.drawable.profile_template)
                .error(R.drawable.profile_template)
                .into(binding!!.userPhoto, object : Callback {
                    override fun onSuccess() {

                    }

                    override fun onError(e: Exception?) {

                    }

                })

        }

        binding!!.username.text = booking!!.user!!.fullname

        when (booking!!.serviceType) {

            "petwalking" -> {
                binding!!.petwalking.visibility = View.VISIBLE
                binding!!.petboarding.visibility = View.GONE
                binding!!.housesitting.visibility = View.GONE
                binding!!.pettraining.visibility = View.GONE
                binding!!.petgrooming.visibility = View.GONE
            }

            "petboarding" -> {
                binding!!.petwalking.visibility = View.GONE
                binding!!.petboarding.visibility = View.VISIBLE
                binding!!.housesitting.visibility = View.GONE
                binding!!.pettraining.visibility = View.GONE
                binding!!.petgrooming.visibility = View.GONE
            }

            "housesitting" -> {
                binding!!.petwalking.visibility = View.GONE
                binding!!.petboarding.visibility = View.GONE
                binding!!.housesitting.visibility = View.VISIBLE
                binding!!.pettraining.visibility = View.GONE
                binding!!.petgrooming.visibility = View.GONE
            }

            "pettraning" -> {
                binding!!.petwalking.visibility = View.GONE
                binding!!.petboarding.visibility = View.GONE
                binding!!.housesitting.visibility = View.GONE
                binding!!.pettraining.visibility = View.VISIBLE
                binding!!.petgrooming.visibility = View.GONE
            }

            "petgrooming" -> {
                binding!!.petwalking.visibility = View.GONE
                binding!!.petboarding.visibility = View.GONE
                binding!!.housesitting.visibility = View.GONE
                binding!!.pettraining.visibility = View.GONE
                binding!!.petgrooming.visibility = View.VISIBLE
            }

        }

        binding!!.dates.text = booking!!.startDate + " - " + booking!!.endDate

        val location = booking!!.location!!.split(":")

        val geocoder = Geocoder(requireContext(), Locale.getDefault())
        try {
            val addresses: List<Address> = geocoder.getFromLocation(location[0].toDouble(), location[1].toDouble(), 1)!!
            if (addresses.isNotEmpty()) {
                val address: Address = addresses[0]
                val addressString = address.getAddressLine(0)

                binding!!.location.text = addressString

            }
        }
        catch (e: IOException) {
            println("Failed to retrieve address: ${e.message}")
        }


    }

}