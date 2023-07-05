package com.pawcare.pawcare.fragments.services

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.BitmapImageViewTarget
import com.bumptech.glide.request.target.CustomTarget
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.gson.JsonObject
import com.pawcare.pawcare.App
import com.pawcare.pawcare.R
import com.pawcare.pawcare.Utils
import com.pawcare.pawcare.databinding.FragmentServicesBinding
import com.pawcare.pawcare.fragments.explore.adapter.ServiceAdapter
import com.pawcare.pawcare.fragments.explore.model.Service
import com.pawcare.pawcare.libraries.BitmapHelper
import com.pawcare.pawcare.libraries.LoadingDialog
import com.pawcare.pawcare.services.ApiInterface
import com.pawcare.pawcare.services.Listener
import com.bumptech.glide.request.transition.Transition
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.slider.RangeSlider

class ServicesFragment : Fragment(), OnMapReadyCallback {

    private var binding: FragmentServicesBinding? = null

    private lateinit var recyclerViewServices: RecyclerView
    private lateinit var recyclerViewServicesMap: RecyclerView

    private var services: MutableList<ApiInterface.Sitter> = mutableListOf()
    private lateinit var serviceAdapter: ServiceAdapter

    private lateinit var loadingDialog : LoadingDialog

    private lateinit var mMap: GoogleMap
    private lateinit var mFusedLocationProviderClient: FusedLocationProviderClient
    private val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1
    private val DEFAULT_ZOOM = 15

    private var mapActive = false

    private var serviceFilter = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val fragmentBinding = FragmentServicesBinding.inflate(inflater, container, false)
        binding = fragmentBinding

        App.instance.mainActivity.findViewById<LinearLayout>(R.id.bottombar).visibility = View.GONE

        val bundle = arguments
        if (bundle != null) {

            if (bundle.containsKey("SERVICE"))
                serviceFilter = bundle.getString("SERVICE")!!

        }

        return fragmentBinding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadingDialog = LoadingDialog(requireContext())
        Utils.navigationBar(view, "Pet Sitters", requireActivity())

        binding!!.filter.setOnClickListener {

            //Bottom sheet leave filters

            val view: View = layoutInflater.inflate(R.layout.item_bottom_sheet_filters, null)
            val dialog = BottomSheetDialog(requireContext(), R.style.AppBottomSheetDialogTheme)
            dialog.setContentView(view)
            //dialog.setCancelable(false)

            val submitBtn = dialog.findViewById<View>(R.id.submit_btn)
            val priceRangeSeekBar = dialog.findViewById<RangeSlider>(R.id.priceRangeSeekBar)
            val pricesText = dialog.findViewById<TextView>(R.id.prices)
            val resetFilter = dialog.findViewById<View>(R.id.resetFilter)

            val check_petwalking = dialog.findViewById<CheckBox>(R.id.check_petwalking)
            val check_petboarding = dialog.findViewById<CheckBox>(R.id.check_petboarding)
            val check_housesitting = dialog.findViewById<CheckBox>(R.id.check_housesitting)
            val check_pettraining = dialog.findViewById<CheckBox>(R.id.check_pettraining)
            val check_petgrooming = dialog.findViewById<CheckBox>(R.id.check_petgrooming)

            when (serviceFilter) {

                "Pet\nWalking" -> check_petwalking!!.isChecked = true

                "Pet\nBoarding" -> check_petboarding!!.isChecked = true

                "House\nSitting" -> check_housesitting!!.isChecked = true

                "Pet\nTraining" -> check_pettraining!!.isChecked = true

                "Pet\nGrooming" -> check_petgrooming!!.isChecked = true

                else -> {}

            }

            priceRangeSeekBar!!.valueFrom = 0f
            priceRangeSeekBar!!.stepSize = 1f
            priceRangeSeekBar!!.valueTo = 100f
            priceRangeSeekBar!!.values = listOf(0f, 20f)

            pricesText!!.text = "0€ to 20€"

            priceRangeSeekBar.addOnChangeListener { slider, _, _ ->
                val rangeStart = slider.values[0].toInt()
                val rangeEnd = slider.values[1].toInt()

                pricesText!!.text = rangeStart.toString() + "€ to " + rangeEnd.toString() + "€"

            }

            resetFilter!!.setOnClickListener {

                check_petwalking!!.isChecked = false
                check_petboarding!!.isChecked = false
                check_housesitting!!.isChecked = false
                check_pettraining!!.isChecked = false
                check_petgrooming!!.isChecked = false

                priceRangeSeekBar!!.values = listOf(0f, 20f)

            }

            submitBtn!!.setOnClickListener {


            }

            dialog.show()

        }

        binding!!.mapBtn.setOnClickListener {

            mapActive = !mapActive

            if (mapActive) {
                binding!!.map.visibility = View.VISIBLE
                binding!!.list.visibility = View.GONE
                binding!!.mapBtn.setImageResource(R.drawable.ic_round_list_24)
            }
            else {
                binding!!.map.visibility = View.GONE
                binding!!.list.visibility = View.VISIBLE
                binding!!.mapBtn.setImageResource(R.drawable.ic_round_map_24)
            }


        }

        if (checkPermission()) {
            val mapFragment = childFragmentManager.findFragmentById(R.id.map_fragment) as SupportMapFragment
            mapFragment.getMapAsync(this)

            mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext())

        }
        else checkPermission()

        recyclerViewServices = binding!!.services
        recyclerViewServices.setHasFixedSize(true)
        recyclerViewServices.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)

        recyclerViewServicesMap = binding!!.servicesmap
        recyclerViewServicesMap.setHasFixedSize(true)
        recyclerViewServicesMap.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
        val snapHelper: SnapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(recyclerViewServicesMap)

        recyclerViewServicesMap.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager?
                    val snapView = snapHelper.findSnapView(layoutManager)
                    if (snapView != null) {
                        val snapPosition = layoutManager!!.getPosition(snapView)

                        val item = serviceAdapter.getItem(snapPosition)

                        mMap.moveCamera(
                            CameraUpdateFactory.newLatLngZoom(
                                LatLng(item.lat!!.toDouble(), item.long!!.toDouble()), DEFAULT_ZOOM.toFloat()))


                    }
                }
            }

        })


        addServicesToList()

        serviceAdapter = ServiceAdapter(services)
        recyclerViewServices.adapter = serviceAdapter
        recyclerViewServicesMap.adapter = serviceAdapter

        serviceAdapter.setOnItemClickListener(object : ServiceAdapter.onItemClickListener {
            override fun onItemClick(position: Int) {

                val item = serviceAdapter.getItem(position)

                val bundle = Bundle()
                bundle.putParcelable("SITTER", item)

                findNavController().navigate(R.id.action_servicesFragment_to_sitterInfoFragment, bundle)


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
        }, "0", "0")

    }

    override fun onMapReady(googleMap: GoogleMap) {

        mMap = googleMap

        checkPermission()

        mMap.isMyLocationEnabled = true
        mMap.uiSettings.isCompassEnabled = false
        mMap.uiSettings.isMyLocationButtonEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = false

        getLastLocation()

        /*mMap.setOnMapClickListener { p0 ->

            mMap.clear()
            //getAddress(p0.latitude, p0.longitude)
            mMap.addMarker(
                MarkerOptions()
                    .title("Pet Pickup")
                    .position(p0)
                    .icon(
                        BitmapHelper.vectorToBitmap(requireContext(), R.drawable.location, resources.getColor(
                            R.color.red))
                    )
            )
            mMap.moveCamera(CameraUpdateFactory.newLatLng(p0))

        }*/

        //services.clear()
        App.instance.backOffice.getSitters(object : Listener<Any> {
            override fun onResponse(response: Any?) {

                if (isAdded) {

                    if (response != null && response is List<*>) {

                        val list = response as List<ApiInterface.Sitter>

                        if (list.isNotEmpty()) {

                            //services.addAll(list)
                            //serviceAdapter.notifyDataSetChanged()

                            for (item in list) {

                                createCircularMarkerWithPhoto(mMap, LatLng(item.lat!!.toDouble(), item.long!!.toDouble()), item)

                            }

                        }

                    }

                }

            }
        }, "0", "0")

    }

    private fun getLastLocation() {

        if (checkPermission()) {

            if (isLocationEnable()) {

                mFusedLocationProviderClient.lastLocation.addOnCompleteListener { task ->
                    var location : Location? = task.result
                    if (location == null) {
                        getNewLocation()
                    } else {

                        mMap.clear()

                        //getAddress(location.latitude, location.longitude)


                        /*mMap.addMarker(
                            MarkerOptions()
                                .title("Pet Pickup")
                                .position(LatLng(location.latitude, location.longitude))
                                .icon(
                                    BitmapHelper.vectorToBitmap(requireContext(), R.drawable.location, resources.getColor(
                                        R.color.red))
                                )

                        )*/

                        mMap.moveCamera(
                            CameraUpdateFactory.newLatLngZoom(
                                LatLng(location.latitude, location.longitude), DEFAULT_ZOOM.toFloat()))

                    }
                }

            }

        } else requestPermission()

    }

    fun createCircularMarkerWithPhoto(googleMap: GoogleMap, position: LatLng, item: ApiInterface.Sitter) {
        val markerOptions = MarkerOptions().position(position)

        // Load the user photo asynchronously using Glide
        Glide.with(requireContext())
            .asBitmap()
            .load(item.image)
            .apply(RequestOptions().override(150, 150)) // Set the desired width and height
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    // Resize the user photo bitmap to a fixed size
                    val resizedBitmap = resizeBitmap(resource, 150, 150) // Set the desired width and height

                    // Create a circular bitmap with the resized user photo and yellow stroke
                    val circularBitmap = createCircularBitmapWithStroke(resizedBitmap)

                    // Set the circular bitmap as a marker icon
                    markerOptions.icon(BitmapDescriptorFactory.fromBitmap(circularBitmap))

                    // Add the marker to the map
                    val marker = googleMap.addMarker(markerOptions)
                    marker?.tag = item

                    googleMap.setOnMarkerClickListener { clickedMarker ->

                        val clickedItem = clickedMarker.tag as? ApiInterface.Sitter

                        if (clickedItem != null) {

                            val bundle = Bundle()
                            bundle.putParcelable("SITTER", clickedItem)

                            findNavController().navigate(R.id.action_servicesFragment_to_sitterInfoFragment, bundle)

                        }

                        true
                    }

                }

                override fun onLoadFailed(errorDrawable: Drawable?) {
                    // Handle bitmap loading failure
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                    // Handle bitmap load being cleared
                }
            })
    }

    // Utility function to convert a resource ID to Bitmap
    fun getBitmapFromResource(resourceId: Int): Bitmap {
        val drawable = ContextCompat.getDrawable(requireContext(), resourceId)
        return (drawable as BitmapDrawable).bitmap
    }

    // Utility function to resize a bitmap to a desired width and height
    fun resizeBitmap(bitmap: Bitmap, width: Int, height: Int): Bitmap {
        return Bitmap.createScaledBitmap(bitmap, width, height, false)
    }

    // Utility function to create a circular bitmap with yellow stroke from a source bitmap
    fun createCircularBitmapWithStroke(sourceBitmap: Bitmap): Bitmap {
        val outputBitmap = Bitmap.createBitmap(sourceBitmap.width, sourceBitmap.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(outputBitmap)

        val color = 0xff424242.toInt()
        val strokeColor = 0xffD6DA2C.toInt() // Yellow stroke color
        val strokeWidth = 5 // Stroke width in pixels

        val paint = Paint()
        val rect = Rect(0, 0, sourceBitmap.width, sourceBitmap.height)

        paint.isAntiAlias = true
        canvas.drawARGB(0, 0, 0, 0)
        paint.color = color
        canvas.drawCircle(sourceBitmap.width / 2f, sourceBitmap.height / 2f, sourceBitmap.width / 2f, paint)
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        canvas.drawBitmap(sourceBitmap, rect, rect, paint)

        // Draw the yellow stroke
        paint.reset()
        paint.isAntiAlias = true
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = strokeWidth.toFloat()
        paint.color = strokeColor
        val oval = RectF(rect)
        canvas.drawOval(oval, paint)

        return outputBitmap
    }

    private fun getNewLocation() {
        val locationRequest = LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            interval = 0
            fastestInterval = 0
            numUpdates = 1
        }

        checkPermission()

        Looper.myLooper()?.let {
            mFusedLocationProviderClient.requestLocationUpdates(
                locationRequest,locationCallback, it
            )
        }
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(p0: LocationResult) {

            val lastLocation = p0.lastLocation

            mMap.clear()

            mMap.moveCamera(
                CameraUpdateFactory.newLatLngZoom(
                    LatLng(lastLocation!!.latitude, lastLocation.longitude), DEFAULT_ZOOM.toFloat()))

        }
    }

    private fun isLocationEnable() : Boolean {
        val locationManager = activity?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER)
    }

    private fun checkPermission() : Boolean {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true
        }
        else {
            requestPermission()
        }
        return false
    }

    private fun requestPermission() {
        requestPermissions(arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION), PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION)
    }

}