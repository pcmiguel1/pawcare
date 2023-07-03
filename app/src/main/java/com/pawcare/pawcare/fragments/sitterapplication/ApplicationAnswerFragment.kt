package com.pawcare.pawcare.fragments.sitterapplication

import android.Manifest
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Address
import android.location.Geocoder
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Environment
import android.util.Log
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.google.gson.JsonObject
import com.pawcare.pawcare.*
import com.pawcare.pawcare.Utils.hideKeyboard
import com.pawcare.pawcare.databinding.FragmentApplicationAnswerBinding
import com.pawcare.pawcare.fragments.mypets.adapter.PetAdapter
import com.pawcare.pawcare.fragments.sitterapplication.adapter.PictureAdapter
import com.pawcare.pawcare.libraries.LoadingDialog
import com.pawcare.pawcare.services.ApiInterface
import com.pawcare.pawcare.services.Listener
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import java.io.*
import java.lang.Exception
import java.util.*
import java.util.concurrent.TimeUnit

class ApplicationAnswerFragment : Fragment() {

    private var binding: FragmentApplicationAnswerBinding? = null
    private lateinit var loadingDialog: LoadingDialog

    private lateinit var pictureAdapter : PictureAdapter

    private var step = 0
    private var sitter : ApiInterface.Sitter? = null

    private lateinit var fotoPicture: Bitmap
    private lateinit var fotoUser: Bitmap
    private var updatePhoto = false

    private var pictures : MutableList<ApiInterface.Picture> = mutableListOf()

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private var location : LatLng? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val fragmentBinding = FragmentApplicationAnswerBinding.inflate(inflater, container, false)
        binding = fragmentBinding

        Places.initialize(requireContext(), BuildConfig.MAPS_API_KEY)
        App.instance.mainActivity.findViewById<LinearLayout>(R.id.bottombar).visibility = View.GONE

        val bundle = arguments
        if (bundle != null) {
            step = bundle.getInt("STEP")

            if (bundle.containsKey("SITTER"))
                sitter = bundle.getParcelable("SITTER")

        }

        return fragmentBinding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadingDialog = LoadingDialog(requireContext())
        Utils.navigationBar(view, "", requireActivity())

        when (step) {

            1 -> {

                val photoUrl = App.instance.preferences.getString("image", "")

                if (photoUrl != "") {

                    Picasso.get()
                        .load(photoUrl)
                        .placeholder(R.drawable.profile_template)
                        .error(R.drawable.profile_template)
                        .into(binding!!.userPhoto, object : Callback {
                            override fun onSuccess() {

                            }

                            override fun onError(e: Exception?) {

                            }

                        })

                }

                if (sitter!!.headline != null && sitter!!.headline != "")
                    binding!!.headline.setText(sitter!!.headline)

                if (sitter!!.description != null && sitter!!.description != "")
                    binding!!.description.setText(sitter!!.description)



                binding!!.step1.visibility = View.VISIBLE

                binding!!.browsePhoto.setOnClickListener {

                    photoPickMethodDialog2()

                }

                binding!!.saveBtn.setOnClickListener {

                    val sitter = JsonObject()
                    if (binding!!.headline.text.isNotEmpty()) sitter.addProperty("headline", binding!!.headline.text.toString())
                    if (binding!!.description.text.isNotEmpty()) sitter.addProperty("description", binding!!.description.text.toString())

                    var temporaryFile : File? = null

                    if (updatePhoto) {
                        temporaryFile = saveBitmapAsTemporaryFile(fotoUser)
                    }

                    binding!!.saveBtn.visibility = View.GONE
                    binding!!.rlprogressave.visibility = View.VISIBLE

                    App.instance.backOffice.updateSitter(object : Listener<Any> {
                        override fun onResponse(response: Any?) {

                            binding!!.saveBtn.visibility = View.VISIBLE
                            binding!!.rlprogressave.visibility = View.GONE

                            if (isAdded) {

                                if (response == null) {

                                    findNavController().navigate(R.id.action_applicationAnswerFragment_to_progressApplicationFragment)

                                }

                            }

                        }
                    }, sitter, temporaryFile)

                }

            }

            2 -> {

                binding!!.step2.visibility = View.VISIBLE


                binding!!.saveBtn.text = "Add picture"

                binding!!.saveBtn.setOnClickListener {

                    photoPickMethodDialog()

                }

                val recyclerViewPictures = binding!!.pictures
                recyclerViewPictures.setHasFixedSize(true)
                recyclerViewPictures.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)

                pictureAdapter = PictureAdapter(pictures)
                recyclerViewPictures.adapter = pictureAdapter

                pictureAdapter.setOnItemClickListener(object : PictureAdapter.onItemClickListener {
                    override fun onItemClick(position: Int) {

                        loadingDialog.startLoading()

                        val item = pictureAdapter.getItem(position)

                        App.instance.backOffice.deletePicture(object : Listener<Any> {
                            override fun onResponse(response: Any?) {

                                loadingDialog.isDismiss()

                                if (isAdded) {

                                    if (response == null) {

                                        pictures.remove(item)
                                        pictureAdapter.notifyDataSetChanged()

                                    }

                                }

                            }

                        }, item.filename!!)


                    }
                })

                addPicturesToList()

            }

            3 -> {

                binding!!.step3.visibility = View.VISIBLE

                binding!!.saveBtn.text = "Confirm phone number"

                val ccp = binding!!.ccp
                ccp.registerCarrierNumberEditText(binding!!.phoneForm)

                binding!!.saveBtn.setOnClickListener {

                    val validPhoneNumber = ccp.isValidFullNumber

                    if (validPhoneNumber) {

                        binding!!.saveBtn.visibility = View.GONE
                        binding!!.rlprogressave.visibility = View.VISIBLE

                        App.instance.backOffice.sendPhoneVerification(object : Listener<Any> {
                            override fun onResponse(response: Any?) {

                                binding!!.saveBtn.visibility = View.VISIBLE
                                binding!!.rlprogressave.visibility = View.GONE

                                if (isAdded) {
                                    if (response == null) {

                                        showDialogVerifyPhone(ccp.fullNumber)

                                    }
                                    else {

                                        App.instance.mainActivity.popupError(response.toString())

                                    }
                                }

                            }

                        }, ccp.fullNumber)

                    } else {

                        App.instance.mainActivity.popupError("Phone number is not valid!")

                    }

                }

            }

            4 -> {

                if (sitter!!.sortcode != null && sitter!!.sortcode != "") binding!!.sortcode.setText(sitter!!.sortcode)
                if (sitter!!.accountnumber != null && sitter!!.accountnumber != "") binding!!.accountnumber.setText(sitter!!.accountnumber)

                binding!!.step4.visibility = View.VISIBLE

                binding!!.saveBtn.setOnClickListener {

                    val sitter = JsonObject()
                    if (binding!!.sortcode.text.isNotEmpty()) sitter.addProperty("sortcode", binding!!.sortcode.text.toString())
                    if (binding!!.accountnumber.text.isNotEmpty()) sitter.addProperty("accountnumber", binding!!.accountnumber.text.toString())

                    binding!!.saveBtn.visibility = View.GONE
                    binding!!.rlprogressave.visibility = View.VISIBLE

                    App.instance.backOffice.updateSitter(object : Listener<Any> {
                        override fun onResponse(response: Any?) {

                            binding!!.saveBtn.visibility = View.VISIBLE
                            binding!!.rlprogressave.visibility = View.GONE

                            if (isAdded) {

                                if (response == null) {

                                    findNavController().navigate(R.id.action_applicationAnswerFragment_to_progressApplicationFragment)

                                }

                            }

                        }
                    }, sitter, null)

                }

            }

            5 -> {

                if (sitter!!.lat != null && sitter!!.lat != "" && sitter!!.long != null && sitter!!.long != "") {

                    val geocoder = Geocoder(requireContext(), Locale.getDefault())
                    try {
                        val addresses: List<Address> = geocoder.getFromLocation(sitter!!.lat!!.toDouble(), sitter!!.long!!.toDouble(), 1)!!
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

                binding!!.step5.visibility = View.VISIBLE

                // Initialize the AutocompleteSupportFragment.
                val autocompleteFragment =
                    childFragmentManager.findFragmentById(R.id.autocomplete_fragment)
                            as AutocompleteSupportFragment

                // Specify the types of place data to return.
                autocompleteFragment.setPlaceFields(listOf(Place.Field.ID, Place.Field.NAME))

                // Set up a PlaceSelectionListener to handle the response.
                autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
                    override fun onPlaceSelected(place: Place) {
                        location = place.latLng
                        binding!!.location.text = place.name
                    }

                    override fun onError(status: Status) {

                    }
                })

                binding!!.currentLocationBtn.setOnClickListener {

                    getMyLocation()

                }

                binding!!.saveBtn.setOnClickListener {

                    val sitter = JsonObject()
                    if (location != null) {
                        sitter.addProperty("lat", location!!.latitude.toString())
                        sitter.addProperty("long", location!!.longitude.toString())
                    }

                    binding!!.saveBtn.visibility = View.GONE
                    binding!!.rlprogressave.visibility = View.VISIBLE

                    App.instance.backOffice.updateSitter(object : Listener<Any> {
                        override fun onResponse(response: Any?) {

                            binding!!.saveBtn.visibility = View.VISIBLE
                            binding!!.rlprogressave.visibility = View.GONE

                            if (isAdded) {

                                if (response == null) {

                                    findNavController().navigate(R.id.action_applicationAnswerFragment_to_progressApplicationFragment)

                                }

                            }

                        }
                    }, sitter, null)

                }

            }

            6 -> {

                binding!!.step6.visibility = View.VISIBLE

                //services
                val checkPetWalking = binding!!.checkPetwalking
                val ratesPetWalking = binding!!.ratesWalk
                val ratePetWalking = binding!!.rateWalk
                val ratePetWalkingAddPet = binding!!.rateWalkAddPet

                val checkPetBoarding = binding!!.checkPetBoarding
                val ratesPetBoarding = binding!!.ratesPetBoarding
                val ratePetBoarding = binding!!.ratePetBoarding
                val ratePetBoardingAddPet = binding!!.ratePetBoardingAddPet

                val checkHouseSitting = binding!!.checkHouseSitting
                val ratesHouseSitting = binding!!.ratesHouseSitting
                val rateHouseSitting = binding!!.rateHouseSitting
                val rateHouseSittingAddPet = binding!!.rateHouseSittingAddPet

                val checkTraining = binding!!.checkTraining
                val ratesTraining = binding!!.ratesTraining
                val rateTraining = binding!!.rateTraining
                val rateTrainingAddPet = binding!!.rateTrainingAddPet

                val checkGrooming = binding!!.checkGromming
                val ratesGrooming = binding!!.ratesGromming
                val rateGrooming = binding!!.rateGromming
                val rateGroomingAddPet = binding!!.rateGrommingAddPet

                //Adicional services
                val checkPickupDropoff = binding!!.checkPickupdropoff
                val checkAdmisterOralMedications = binding!!.checkAdmisterOralMedications
                val checkAdmisterInjectMedications = binding!!.checkAdmisterInjectMedications

                if (sitter!!.petwalking != null) {
                    checkPetWalking.isChecked = sitter!!.petwalking!!
                    if (checkPetWalking.isChecked) ratesPetWalking.visibility = View.VISIBLE
                }
                if (sitter!!.petboarding != null) {
                    checkPetBoarding.isChecked = sitter!!.petboarding!!
                    if (checkPetBoarding.isChecked) ratesPetBoarding.visibility = View.VISIBLE
                }
                if (sitter!!.housesitting != null) {
                    checkHouseSitting.isChecked = sitter!!.housesitting!!
                    if (checkHouseSitting.isChecked) ratesHouseSitting.visibility = View.VISIBLE
                }
                if (sitter!!.training != null) {
                    checkTraining.isChecked = sitter!!.training!!
                    if (checkTraining.isChecked) ratesTraining.visibility = View.VISIBLE
                }
                if (sitter!!.grooming != null) {
                    checkGrooming.isChecked = sitter!!.grooming!!
                    if (checkGrooming.isChecked) ratesGrooming.visibility = View.VISIBLE
                }

                if (sitter!!.pickupdropoff != null) checkPickupDropoff.isChecked = sitter!!.pickupdropoff!!
                if (sitter!!.oralmedications != null) checkAdmisterOralMedications.isChecked = sitter!!.oralmedications!!
                if (sitter!!.injectmedications != null) checkAdmisterInjectMedications.isChecked = sitter!!.injectmedications!!

                if (sitter!!.ratewalking != null && sitter!!.ratewalking != "") ratePetWalking.setText(sitter!!.ratewalking)
                if (sitter!!.ratewalkingaddpet != null && sitter!!.ratewalkingaddpet != "") ratePetWalkingAddPet.setText(sitter!!.ratewalkingaddpet)

                if (sitter!!.ratepetboarding != null && sitter!!.ratepetboarding != "") ratePetBoarding.setText(sitter!!.ratepetboarding)
                if (sitter!!.ratepetboardingaddpet != null && sitter!!.ratepetboardingaddpet != "") ratePetBoardingAddPet.setText(sitter!!.ratepetboardingaddpet)

                if (sitter!!.ratehousesitting != null && sitter!!.ratehousesitting != "") rateHouseSitting.setText(sitter!!.ratehousesitting)
                if (sitter!!.ratehousesittingaddpet != null && sitter!!.ratehousesittingaddpet != "") rateHouseSittingAddPet.setText(sitter!!.ratehousesittingaddpet)

                if (sitter!!.ratetraining != null && sitter!!.ratetraining != "") rateTraining.setText(sitter!!.ratetraining)
                if (sitter!!.ratetrainingaddpet != null && sitter!!.ratetrainingaddpet != "") rateTrainingAddPet.setText(sitter!!.ratetrainingaddpet)

                if (sitter!!.rategrooming != null && sitter!!.rategrooming != "") rateGrooming.setText(sitter!!.rategrooming)
                if (sitter!!.rategroomingaddpet != null && sitter!!.rategroomingaddpet != "") rateGroomingAddPet.setText(sitter!!.rategroomingaddpet)


                checkPetWalking.setOnCheckedChangeListener { buttonView, isChecked ->
                    // Perform actions based on the checkbox state
                    if (isChecked) {
                        ratesPetWalking.visibility = View.VISIBLE
                    } else {
                        ratesPetWalking.visibility = View.GONE
                    }
                }

                checkPetBoarding.setOnCheckedChangeListener { buttonView, isChecked ->
                    // Perform actions based on the checkbox state
                    if (isChecked) {
                        ratesPetBoarding.visibility = View.VISIBLE
                    } else {
                        ratesPetBoarding.visibility = View.GONE
                    }
                }

                checkHouseSitting.setOnCheckedChangeListener { buttonView, isChecked ->
                    // Perform actions based on the checkbox state
                    if (isChecked) {
                        ratesHouseSitting.visibility = View.VISIBLE
                    } else {
                        ratesHouseSitting.visibility = View.GONE
                    }
                }

                checkTraining.setOnCheckedChangeListener { buttonView, isChecked ->
                    // Perform actions based on the checkbox state
                    if (isChecked) {
                        ratesTraining.visibility = View.VISIBLE
                    } else {
                        ratesTraining.visibility = View.GONE
                    }
                }

                checkGrooming.setOnCheckedChangeListener { buttonView, isChecked ->
                    // Perform actions based on the checkbox state
                    if (isChecked) {
                        ratesGrooming.visibility = View.VISIBLE
                    } else {
                        ratesGrooming.visibility = View.GONE
                    }
                }


                binding!!.saveBtn.setOnClickListener {

                    val sitter = JsonObject()
                    sitter.addProperty("petwalking", checkPetWalking.isChecked)
                    sitter.addProperty("petboarding", checkPetBoarding.isChecked)
                    sitter.addProperty("housesitting", checkHouseSitting.isChecked)
                    sitter.addProperty("training", checkTraining.isChecked)
                    sitter.addProperty("grooming", checkGrooming.isChecked)
                    sitter.addProperty("pickupdropoff", checkPickupDropoff.isChecked)
                    sitter.addProperty("oralmedications", checkAdmisterOralMedications.isChecked)
                    sitter.addProperty("injectmedications", checkAdmisterInjectMedications.isChecked)

                    sitter.addProperty("ratewalking", ratePetWalking.text.toString())
                    sitter.addProperty("ratewalkingaddpet", ratePetWalkingAddPet.text.toString())

                    sitter.addProperty("ratepetboarding", ratePetBoarding.text.toString())
                    sitter.addProperty("ratepetboardingaddpet", ratePetBoardingAddPet.text.toString())

                    sitter.addProperty("ratehousesitting", rateHouseSitting.text.toString())
                    sitter.addProperty("ratehousesittingaddpet", rateHouseSittingAddPet.text.toString())

                    sitter.addProperty("ratetraining", rateTraining.text.toString())
                    sitter.addProperty("ratetrainingaddpet", rateTrainingAddPet.text.toString())

                    sitter.addProperty("rategrooming", rateGrooming.text.toString())
                    sitter.addProperty("rategroomingaddpet", rateGroomingAddPet.text.toString())

                    binding!!.saveBtn.visibility = View.GONE
                    binding!!.rlprogressave.visibility = View.VISIBLE

                    App.instance.backOffice.updateSitter(object : Listener<Any> {
                        override fun onResponse(response: Any?) {

                            binding!!.saveBtn.visibility = View.VISIBLE
                            binding!!.rlprogressave.visibility = View.GONE

                            if (isAdded) {

                                if (response == null) {

                                    findNavController().navigate(R.id.action_applicationAnswerFragment_to_progressApplicationFragment)

                                }

                            }

                        }
                    }, sitter, null)

                }


            }

        }

    }

    private fun photoPickMethodDialog() {

        val dialogClickListener = DialogInterface.OnClickListener { dialog, which ->
            when (which) {
                0 -> {
                    if (Utils.checkPhotoPermissions(this, 11))
                        takeAPhoto()
                }
                1 -> {
                    if (Utils.checkPhotoPermissions(this, 12))
                        chooseFromGallery()
                }
            }
            dialog.dismiss()
        }

        val items = arrayOf(getString(R.string.take_photo), getString(R.string.choose_from_gallery))
        val builder = AlertDialog.Builder(requireActivity())
        builder.setItems(items, dialogClickListener).show()

    }

    private var uri: Uri? = null
    fun takeAPhoto() {

        val photoFile = File.createTempFile(
            "IMG_",
            ".jpg",
            requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        )

        uri = FileProvider.getUriForFile(
            requireContext(),
            BuildConfig.APPLICATION_ID + ".provider",
            photoFile
        )

        getPreviewImage.launch(uri)

    }

    private fun chooseFromGallery() {
        getContent.launch("image/*")
    }

    private val getPreviewImage =
        registerForActivityResult(ActivityResultContracts.TakePicture()) {
                isSaved ->

            if (isSaved) {

                val imageUri = uri
                val imageStream: InputStream?
                try {
                    imageStream = requireActivity().contentResolver.openInputStream(imageUri!!)

                    fotoPicture = BitmapFactory.decodeStream(imageStream)

                    fotoPicture = Utils.rotateImageIfRequired(requireContext(), fotoPicture, imageUri)

                    updatePhoto = true

                    addPicture()

                } catch (e: FileNotFoundException) {
                    e.printStackTrace()
                }

            }
        }

    private val getContent =
        registerForActivityResult(ActivityResultContracts.GetContent()) {
                uri ->

            if (uri != null) {

                val imageUri = uri
                val imageStream: InputStream?
                try {
                    imageStream = requireActivity().contentResolver.openInputStream(imageUri!!)

                    fotoPicture = BitmapFactory.decodeStream(imageStream)

                    fotoPicture = Utils.rotateImageIfRequired(requireContext(), fotoPicture, imageUri)

                    updatePhoto = true

                    addPicture()

                } catch (e: FileNotFoundException) {
                    e.printStackTrace()
                }

            }
        }

    fun saveBitmapAsTemporaryFile(bitmap: Bitmap): File? {
        var tempFile: File? = null
        try {
            tempFile = File.createTempFile("temp_image_", ".jpg")
            val outputStream = FileOutputStream(tempFile)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            outputStream.flush()
            outputStream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return tempFile
    }

    private fun addPicturesToList() {

        pictures.clear()

        loadingDialog.startLoading()

        App.instance.backOffice.getPictures(object : Listener<Any> {
            override fun onResponse(response: Any?) {

                loadingDialog.isDismiss()

                if (isAdded) {

                    if (response != null && response is List<*>) {

                        val list = response as List<ApiInterface.Picture>

                        if (list.isNotEmpty()) {

                            binding!!.pictures.visibility = View.VISIBLE
                            binding!!.emptyPictures.visibility = View.GONE
                            pictures.addAll(list)
                            pictureAdapter.notifyDataSetChanged()
                        }
                        else {
                            binding!!.pictures.visibility = View.GONE
                            binding!!.emptyPictures.visibility = View.VISIBLE
                        }

                    }
                    else {
                        binding!!.pictures.visibility = View.GONE
                        binding!!.emptyPictures.visibility = View.VISIBLE
                    }

                }

            }
        })

    }

    private fun addPicture() {

        var temporaryFile : File? = null

        if (updatePhoto) {
            temporaryFile = saveBitmapAsTemporaryFile(fotoPicture)
        }

        val saveBtn = binding!!.saveBtn
        val rlprogressave = binding!!.rlprogressave

        saveBtn.visibility = View.GONE
        rlprogressave.visibility = View.VISIBLE

        App.instance.backOffice.addPicture(object : Listener<Any> {
            override fun onResponse(response: Any?) {

                saveBtn.visibility = View.VISIBLE
                rlprogressave.visibility = View.GONE

                if (isAdded) {

                    if (response != null && response is ApiInterface.Picture) {

                        pictures.add(response)
                        pictureAdapter.notifyDataSetChanged()

                    }

                }

            }
        }, temporaryFile)

    }

    private fun showDialogVerifyPhone(phoneNumber: String) {

        lateinit var dialog: AlertDialog
        var timerActive = false

        val mDialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_verify_phone, null)

        val builder = AlertDialog.Builder(requireContext())
            .setView(mDialogView)
            .setCancelable(false)

        dialog = builder.create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val timer = mDialogView.findViewById<TextView>(R.id.timer)
        val resendCodeBtn = mDialogView.findViewById<TextView>(R.id.resendCode_btn)
        val cancelBtn = mDialogView.findViewById<View>(R.id.cancel_btn)

        val otpNumber1 = mDialogView.findViewById<EditText>(R.id.otp_number_1)
        val otpNumber2 = mDialogView.findViewById<EditText>(R.id.otp_number_2)
        val otpNumber3 = mDialogView.findViewById<EditText>(R.id.otp_number_3)
        val otpNumber4 = mDialogView.findViewById<EditText>(R.id.otp_number_4)

        otpNumber1.requestFocus()

        otpNumber1.setOnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_DEL && event.action == KeyEvent.ACTION_UP) {

                otpNumber1.setText("")

                return@setOnKeyListener true
            }
            if (Utils.isKeyCodeNumber(keyCode) && event.action == KeyEvent.ACTION_UP) {

                otpNumber2.requestFocus()

            }
            return@setOnKeyListener false
        }

        otpNumber2.setOnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_DEL && event.action == KeyEvent.ACTION_UP) {

                otpNumber2.setText("")
                otpNumber1.requestFocus()

                return@setOnKeyListener true
            }
            if (Utils.isKeyCodeNumber(keyCode) && event.action == KeyEvent.ACTION_UP) {

                otpNumber3.requestFocus()

            }
            return@setOnKeyListener false
        }

        otpNumber3.setOnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_DEL && event.action == KeyEvent.ACTION_UP) {

                otpNumber3.setText("")
                otpNumber2.requestFocus()

                return@setOnKeyListener true
            }
            if (Utils.isKeyCodeNumber(keyCode) && event.action == KeyEvent.ACTION_UP) {

                otpNumber4.requestFocus()

            }
            return@setOnKeyListener false
        }

        otpNumber4.setOnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_DEL && event.action == KeyEvent.ACTION_UP) {

                otpNumber4.setText("")
                otpNumber3.requestFocus()

                return@setOnKeyListener true
            }
            if (Utils.isKeyCodeNumber(keyCode) && event.action == KeyEvent.ACTION_UP) {

                otpNumber4.clearFocus()
                otpNumber4.hideKeyboard()

            }
            return@setOnKeyListener false
        }

        val countDownTimer = object : CountDownTimer(120000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                //timer.text = (TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) % 60).toString()
                timer.text = String.format("%02d:%02d",
                    TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished),
                    TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)))
            }

            override fun onFinish() {
                timerActive = false
                timer.visibility = View.GONE
                resendCodeBtn.visibility = View.VISIBLE
            }

        }

        resendCodeBtn.setOnClickListener {
            resendCodeBtn.visibility = View.GONE
            countDownTimer.start()
            timerActive = true
            timer.visibility = View.VISIBLE
        }

        val errorMessage = mDialogView.findViewById<TextView>(R.id.error)

        val continueButton = mDialogView.findViewById<View>(R.id.continue_btn)
        val rlprogresverify = mDialogView.findViewById<View>(R.id.rlprogresverify)
        continueButton.setOnClickListener {

            val code = otpNumber1.text.toString()+otpNumber2.text.toString()+otpNumber3.text.toString()+otpNumber4.text.toString()

            val validCode = Utils.validCode(code)

            if (validCode) {

                continueButton.visibility = View.GONE
                rlprogresverify.visibility = View.VISIBLE

                val user = JsonObject()
                user.addProperty("phoneNumber", phoneNumber)
                user.addProperty("code", code)

                App.instance.backOffice.verifyPhone(object : Listener<Any> {
                    override fun onResponse(response: Any?) {

                        continueButton.visibility = View.VISIBLE
                        rlprogresverify.visibility = View.GONE

                        if (isAdded) {
                            if (response == null) {
                                errorMessage.visibility = View.INVISIBLE
                                dialog.dismiss()
                                Toast.makeText(activity, "Phone Number verified successfully!", Toast.LENGTH_SHORT).show()
                                findNavController().navigate(R.id.action_applicationAnswerFragment_to_progressApplicationFragment)
                                //requireActivity().onBackPressed()
                            }
                            else {
                                errorMessage.text = response.toString()
                                errorMessage.visibility = View.VISIBLE
                                //App.instance.mainActivity.popupError(response.toString())
                            }
                        }

                    }

                }, user)

            }
            else {

                errorMessage.text = "Verification Code not valid!"
                errorMessage.visibility = View.VISIBLE

            }

        }

        cancelBtn.setOnClickListener {
            if (timerActive) countDownTimer.cancel()
            dialog.dismiss()
        }

        dialog.show()
        countDownTimer.start()
        timerActive = true

    }

    private fun photoPickMethodDialog2() {

        val dialogClickListener = DialogInterface.OnClickListener { dialog, which ->
            when (which) {
                0 -> {
                    if (Utils.checkPhotoPermissions(this, 11))
                        takeAPhoto2()
                }
                1 -> {
                    if (Utils.checkPhotoPermissions(this, 12))
                        chooseFromGallery2()
                }
            }
            dialog.dismiss()
        }

        val items = arrayOf(getString(R.string.take_photo), getString(R.string.choose_from_gallery))
        val builder = AlertDialog.Builder(requireActivity())
        builder.setItems(items, dialogClickListener).show()

    }

    private var uri2: Uri? = null
    fun takeAPhoto2() {

        val photoFile = File.createTempFile(
            "IMG_",
            ".jpg",
            requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        )

        uri2 = FileProvider.getUriForFile(
            requireContext(),
            BuildConfig.APPLICATION_ID + ".provider",
            photoFile
        )

        getPreviewImage2.launch(uri2)

    }

    private fun chooseFromGallery2() {
        getContent2.launch("image/*")
    }

    private val getPreviewImage2 =
        registerForActivityResult(ActivityResultContracts.TakePicture()) {
                isSaved ->

            if (isSaved) {

                val imageUri = uri2
                val imageStream: InputStream?
                try {
                    imageStream = requireActivity().contentResolver.openInputStream(imageUri!!)

                    fotoUser = BitmapFactory.decodeStream(imageStream)

                    fotoUser = Utils.rotateImageIfRequired(requireContext(), fotoUser, imageUri)

                    updatePhoto = true

                    Glide.with(this).load(fotoUser).apply(
                        RequestOptions()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .placeholder(R.drawable.profile_template))
                        .into(binding!!.userPhoto)

                } catch (e: FileNotFoundException) {
                    e.printStackTrace()
                }

            }
        }

    private val getContent2 =
        registerForActivityResult(ActivityResultContracts.GetContent()) {
                uri ->

            if (uri != null) {

                val imageUri = uri
                val imageStream: InputStream?
                try {
                    imageStream = requireActivity().contentResolver.openInputStream(imageUri!!)

                    fotoUser = BitmapFactory.decodeStream(imageStream)

                    fotoUser = Utils.rotateImageIfRequired(requireContext(), fotoUser, imageUri)

                    updatePhoto = true

                    Glide.with(this).load(fotoUser).apply(
                        RequestOptions()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .placeholder(R.drawable.profile_template))
                        .into(binding!!.userPhoto)

                } catch (e: FileNotFoundException) {
                    e.printStackTrace()
                }

            }
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
                        val geocoder = Geocoder(requireContext(), Locale.getDefault())
                        try {
                            val addresses: List<Address> = geocoder.getFromLocation(latitude, longitude, 1)!!
                            if (addresses.isNotEmpty()) {
                                val address: Address = addresses[0]
                                val addressString = address.getAddressLine(0)

                                this.location = LatLng(latitude, longitude)
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

}