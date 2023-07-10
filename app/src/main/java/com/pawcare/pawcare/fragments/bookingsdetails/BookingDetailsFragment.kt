package com.pawcare.pawcare.fragments.bookingsdetails

import android.Manifest
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.pawcare.pawcare.App
import com.pawcare.pawcare.R
import com.pawcare.pawcare.Utils
import com.pawcare.pawcare.databinding.FragmentBookingDetailsBinding
import com.pawcare.pawcare.fragments.bookingsdetails.adapter.PetAdapter
import com.pawcare.pawcare.libraries.LoadingDialog
import com.pawcare.pawcare.services.ApiInterface
import com.pawcare.pawcare.services.Listener
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class BookingDetailsFragment : Fragment(), PetAdapter.PetAdapterCallback {

    private var binding: FragmentBookingDetailsBinding? = null

    private lateinit var recyclerViewPets: RecyclerView
    private var pets: MutableList<ApiInterface.Pet> = mutableListOf()
    private lateinit var petAdapter: PetAdapter

    private lateinit var loadingDialog: LoadingDialog

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private var sitter : ApiInterface.Sitter? = null

    private var serviceSelected = ""

    private var location = ""
    private var startDate = ""
    private var endDate = ""

    private var petsAtive = arrayListOf<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val fragmentBinding = FragmentBookingDetailsBinding.inflate(inflater, container, false)
        binding = fragmentBinding

        App.instance.mainActivity!!.findViewById<LinearLayout>(R.id.bottombar).visibility = View.GONE

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
        Utils.navigationBar(view, "Booking Details", requireActivity())

        var visibleCount = 0

        var petwalkingvisible = false
        var petboardingvisible = false
        var housesittingvisible = false
        var pettrainingvisible = false
        var petgroomingvisible = false

        if (sitter!!.petwalking!!) {
            petwalkingvisible = true
            binding!!.petwalking.visibility = View.VISIBLE
            visibleCount++
        }

        if (sitter!!.petboarding!!) {
            petboardingvisible = true
            binding!!.petboarding.visibility = View.VISIBLE
            visibleCount++
        }

        if (sitter!!.housesitting!!) {
            housesittingvisible = true
            binding!!.housesitting.visibility = View.VISIBLE
            visibleCount++
        }

        if (sitter!!.training!!) {
            pettrainingvisible = true
            binding!!.pettraining.visibility = View.VISIBLE
            visibleCount++
        }

        if (sitter!!.grooming!!) {
            petgroomingvisible = true
            binding!!.petgrooming.visibility = View.VISIBLE
            visibleCount++
        }

        if (visibleCount == 1) {

            if (petwalkingvisible) {
                serviceSelected = "petwalking"
                binding!!.petwalkingCheck.visibility = View.VISIBLE
            }
            if (petboardingvisible) {
                serviceSelected = "petboarding"
                binding!!.petboardingCheck.visibility = View.VISIBLE
            }
            if (housesittingvisible) {
                serviceSelected = "housesitting"
                binding!!.housesittingCheck.visibility = View.VISIBLE
            }
            if (pettrainingvisible) {
                serviceSelected = "pettraning"
                binding!!.pettrainingCheck.visibility = View.VISIBLE
            }
            if (petgroomingvisible) {
                serviceSelected = "petgrooming"
                binding!!.petgroomingCheck.visibility = View.VISIBLE
            }

        }
        else {

            binding!!.petwalking.setOnClickListener {
                serviceSelected = "petwalking"
                binding!!.petwalkingCheck.visibility = View.VISIBLE
                binding!!.petboardingCheck.visibility = View.GONE
                binding!!.housesittingCheck.visibility = View.GONE
                binding!!.pettrainingCheck.visibility = View.GONE
                binding!!.petgroomingCheck.visibility = View.GONE
            }

            binding!!.petboarding.setOnClickListener {
                serviceSelected = "petboarding"
                binding!!.petboardingCheck.visibility = View.VISIBLE
                binding!!.petwalkingCheck.visibility = View.GONE
                binding!!.housesittingCheck.visibility = View.GONE
                binding!!.pettrainingCheck.visibility = View.GONE
                binding!!.petgroomingCheck.visibility = View.GONE
            }

            binding!!.housesitting.setOnClickListener {
                serviceSelected = "housesitting"
                binding!!.housesittingCheck.visibility = View.VISIBLE
                binding!!.pettrainingCheck.visibility = View.GONE
                binding!!.petgroomingCheck.visibility = View.GONE
                binding!!.petboardingCheck.visibility = View.GONE
                binding!!.petwalkingCheck.visibility = View.GONE
            }

            binding!!.pettraining.setOnClickListener {
                serviceSelected = "pettraning"
                binding!!.pettrainingCheck.visibility = View.VISIBLE
                binding!!.housesittingCheck.visibility = View.GONE
                binding!!.petgroomingCheck.visibility = View.GONE
                binding!!.petboardingCheck.visibility = View.GONE
                binding!!.petwalkingCheck.visibility = View.GONE
            }

            binding!!.petgrooming.setOnClickListener {
                serviceSelected = "petgrooming"
                binding!!.petgroomingCheck.visibility = View.VISIBLE
                binding!!.petboardingCheck.visibility = View.GONE
                binding!!.petwalkingCheck.visibility = View.GONE
                binding!!.pettrainingCheck.visibility = View.GONE
                binding!!.housesittingCheck.visibility = View.GONE
            }

        }

        recyclerViewPets = binding!!.pets
        recyclerViewPets.setHasFixedSize(true)
        recyclerViewPets.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)

        petAdapter = PetAdapter(pets, this)

        recyclerViewPets.adapter = petAdapter

        addPetsToList()

        getMyLocation()

        binding!!.dateBtn.setOnClickListener {

            showDateRangePicker()

        }

        binding!!.myLocationBtn.setOnClickListener {

            getMyLocation()

        }

        binding!!.continueBtn.setOnClickListener {

            val continueBtn = binding!!.continueBtn
            val rlprogressave = binding!!.rlprogressave

            if (Utils.isOnline(requireContext()) &&
                serviceSelected.isNotEmpty() &&
                location.isNotEmpty() &&
                startDate.isNotEmpty() && endDate.isNotEmpty() &&
                    petsAtive.isNotEmpty()) {

                val booking = JsonObject()

                booking.addProperty("sitterId", sitter!!.sitterId)
                booking.addProperty("startDate", startDate)
                booking.addProperty("endDate", endDate)
                booking.addProperty("serviceType", serviceSelected)
                booking.addProperty("location", location)
                booking.addProperty("message", binding!!.message.text.toString())

                val petsArray = JsonArray()
                for (id in petsAtive) {

                    val petObject = JsonObject()
                    petObject.addProperty("id", id)

                    petsArray.add(petObject)

                }

                booking.add("pets", petsArray)

                var total = 0.0

                when (serviceSelected) {

                    "petwalking" -> {
                        total += sitter!!.ratewalking!!.toDouble() + (sitter!!.ratewalkingaddpet!!.toDouble() * petsArray.size())
                    }

                    "petboarding" -> {
                        total += sitter!!.ratepetboarding!!.toDouble() + (sitter!!.ratepetboardingaddpet!!.toDouble() * petsArray.size())
                    }

                    "housesitting" -> {
                        total += sitter!!.ratehousesitting!!.toDouble() + (sitter!!.ratehousesittingaddpet!!.toDouble() * petsArray.size())
                    }

                    "pettraning" -> {
                        total += sitter!!.ratetraining!!.toDouble() + (sitter!!.ratetrainingaddpet!!.toDouble() * petsArray.size())
                    }

                    "petgrooming" -> {
                        total += sitter!!.rategrooming!!.toDouble() + (sitter!!.rategroomingaddpet!!.toDouble() * petsArray.size())
                    }

                }

                booking.addProperty("total", total.toString())


                continueBtn.visibility = View.GONE
                rlprogressave.visibility = View.VISIBLE

                App.instance.backOffice.addBooking(object: Listener<Any> {
                    override fun onResponse(response: Any?) {

                        continueBtn.visibility = View.VISIBLE
                        rlprogressave.visibility = View.GONE

                        if (isAdded) {

                            if (response == null) {

                                Toast.makeText(activity, "Booking submitted successfully!", Toast.LENGTH_SHORT).show()

                                val navOptions = NavOptions.Builder()
                                    .setPopUpTo(R.id.bookingDetailsFragment, false)
                                    .build()

                                val bundle = Bundle()
                                bundle.putParcelable("SITTER", sitter)
                                findNavController().navigate(R.id.action_bookingDetailsFragment_to_sitterInfoFragment,bundle,navOptions)

                            }

                        }

                    }

                }, booking)


            } else {
                var erro = ""
                if (!Utils.isOnline(requireContext())) erro = getString(R.string.no_internet)
                else if (serviceSelected.isEmpty()) erro = "Select one service!"
                else if (location.isEmpty()) erro = "Get your location!"
                else if (startDate.isEmpty()) erro = "Select start date!"
                else if (endDate.isEmpty()) erro = "Select end date!"
                else if (petsAtive.isEmpty()) erro = "Select at least one pet!"

                App.instance.mainActivity!!.popupError(erro)

            }


        }

    }

    private fun addPetsToList() {

        pets.clear()

        loadingDialog.startLoading()

        App.instance.backOffice.getPets(object : Listener<Any> {
            override fun onResponse(response: Any?) {

                loadingDialog.isDismiss()

                if (isAdded) {

                    if (response != null && response is List<*>) {

                        val list = response as List<ApiInterface.Pet>

                        if (list.isNotEmpty()) {
                            binding!!.pets.visibility = View.VISIBLE
                            binding!!.empty.visibility = View.GONE
                            pets.addAll(list)
                            petAdapter.notifyDataSetChanged()

                        }
                        else {
                            binding!!.pets.visibility = View.GONE
                            binding!!.empty.visibility = View.VISIBLE
                        }

                    }
                    else {
                        binding!!.pets.visibility = View.GONE
                        binding!!.empty.visibility = View.GONE
                    }

                }

            }

        })

    }

    private fun showDateRangePicker() {

        val dateRangePicker = MaterialDatePicker.Builder
            .dateRangePicker()
            .setTitleText("Select Date")
            .build()

        dateRangePicker.show(
            childFragmentManager,
            "date_range_picker"
        )

        dateRangePicker.addOnPositiveButtonClickListener { datePicked ->

            val startDate = datePicked.first
            val endDate = datePicked.second

            if (startDate != null && endDate != null) {

                this.startDate = convertLongToDate(startDate)
                this.endDate = convertLongToDate(endDate)

                binding!!.dates.text = convertLongToDate(startDate) + " - " + convertLongToDate(endDate)

            }

        }

    }

    private fun convertLongToDate(time: Long): String {

        val date = Date(time)
        val format = SimpleDateFormat(
            "dd-MM-yyyy",
            Locale.getDefault()
        )

        return format.format(date)

    }

    private fun getMyLocation() {

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        // Check for location permission
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED
        ) {
            // Permission granted, fetch the location
            fetchLocation()
        } else {
            // Request location permission
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSION_REQUEST_CODE
            )
        }

    }

    private fun fetchLocation() {
        try {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location ->
                    if (location != null) {
                        val latitude = location.latitude
                        val longitude = location.longitude

                        this.location = "${latitude}:${longitude}"

                        val geocoder = Geocoder(requireContext(), Locale.getDefault())
                        try {
                            val addresses: List<Address> = geocoder.getFromLocation(latitude, longitude, 1)!!
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
                .addOnFailureListener { exception ->
                    // Handle any errors that occurred during location retrieval
                    println("Failed to retrieve location: ${exception.message}")
                }
        } catch (e: SecurityException) {
            // Handle the case when the permission is not granted
            println("Location permission not granted: ${e.message}")
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Location permission granted, fetch the location
                fetchLocation()
            } else {
                // Location permission denied, handle it accordingly
                println("Location permission denied.")
            }
        }
    }

    companion object {
        private const val PERMISSION_REQUEST_CODE = 123
    }

    override fun onPetActiveChanged(position: Int, isActive: Boolean) {
        val item = petAdapter.getItem(position)

        if (isActive) {

            if (!petsAtive.contains(item.id!!))
                petsAtive.add(item.id!!)

        }
        else {

            if (petsAtive.contains(item.id!!))
                petsAtive.remove(item.id!!)

        }

    }

}