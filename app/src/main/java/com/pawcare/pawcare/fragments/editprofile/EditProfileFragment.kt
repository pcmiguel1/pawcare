package com.pawcare.pawcare.fragments.editprofile

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.DialogInterface
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.text.Selection
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.google.gson.JsonObject
import com.pawcare.pawcare.App
import com.pawcare.pawcare.BuildConfig
import com.pawcare.pawcare.R
import com.pawcare.pawcare.Utils
import com.pawcare.pawcare.databinding.FragmentEditProfileBinding
import com.pawcare.pawcare.services.Listener
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import org.json.JSONException
import java.io.*
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*


class EditProfileFragment : Fragment() {

    private var binding: FragmentEditProfileBinding? = null

    private lateinit var fotoUser: Bitmap
    private var updatePhoto = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val fragmentBinding = FragmentEditProfileBinding.inflate(inflater, container, false)
        binding = fragmentBinding

        App.instance.mainActivity.findViewById<LinearLayout>(R.id.bottombar).visibility = View.GONE

        return fragmentBinding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Utils.navigationBar(view, getString(R.string.editprofile), requireActivity())

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

        binding!!.browsePhoto.setOnClickListener {
            photoPickMethodDialog()
        }

        val formPass = binding!!.passwordForm

        binding!!.showHidePw.setOnTouchListener { _, event ->
            when(event.action) {
                MotionEvent.ACTION_UP -> {
                    binding!!.showHidePw.setImageResource(R.drawable.password_hide)
                    val cursorPosition = formPass.selectionStart
                    formPass.transformationMethod = PasswordTransformationMethod.getInstance()
                    Selection.setSelection(formPass.text, cursorPosition)
                    true
                }
                MotionEvent.ACTION_DOWN -> {
                    binding!!.showHidePw.setImageResource(R.drawable.password_show)
                    val cursorPosition = formPass.selectionStart
                    formPass.transformationMethod = HideReturnsTransformationMethod.getInstance()
                    Selection.setSelection(formPass.text, cursorPosition)
                    true
                }
                else -> false
            }
        }

        val newFormPass = binding!!.newPasswordForm

        binding!!.showHidenewPw.setOnTouchListener { _, event ->
            when(event.action) {
                MotionEvent.ACTION_UP -> {
                    binding!!.showHidenewPw.setImageResource(R.drawable.password_hide)
                    val cursorPosition = newFormPass.selectionStart
                    newFormPass.transformationMethod = PasswordTransformationMethod.getInstance()
                    Selection.setSelection(newFormPass.text, cursorPosition)
                    true
                }
                MotionEvent.ACTION_DOWN -> {
                    binding!!.showHidenewPw.setImageResource(R.drawable.password_show)
                    val cursorPosition = newFormPass.selectionStart
                    newFormPass.transformationMethod = HideReturnsTransformationMethod.getInstance()
                    Selection.setSelection(newFormPass.text, cursorPosition)
                    true
                }
                else -> false
            }
        }

        binding!!.dateForm.setOnTouchListener { v, event ->
            if (event?.action == MotionEvent.ACTION_UP) {
                selectBirthDate()
            }
            true
        }

        binding!!.dateFormParent.setOnClickListener {
            selectBirthDate()
        }

        val fullName = App.instance.preferences.getString("fullname", "")
        val dateOfBirth = App.instance.preferences.getString("dateOfBirth", "")
        val email = App.instance.preferences.getString("email", "")

        binding!!.fullnameForm.setText(fullName)

        if (dateOfBirth != "") {

            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
            val outputFormat = SimpleDateFormat("dd-M-yyyy", Locale.getDefault())

            val inputDate = inputFormat.parse(dateOfBirth)
            val outputDateString = outputFormat.format(inputDate)

            binding!!.dateForm.setText(outputDateString)

        }

        binding!!.emailForm.setText(email)


        binding!!.saveBtn.setOnClickListener {

            updateProfile()

        }


    }

    private fun updateProfile() {

        Utils.hideKeyboard(requireActivity())

        if (Utils.isOnline(requireContext())) {

            val fullName = binding!!.fullnameForm.text.toString()
            val dateOfBirth = binding!!.dateForm.text.toString()
            val currentPassword = binding!!.passwordForm.text.toString()
            val newPassword = binding!!.newPasswordForm.text.toString()

            val user = JsonObject()

            try {

                var formattedDate = ""
                if (dateOfBirth.isNotEmpty()) {
                    val parser = SimpleDateFormat("dd-MM-yyyy")
                    val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                    formattedDate = formatter.format(parser.parse(dateOfBirth))
                }

                user.addProperty("fullname", fullName)
                user.addProperty("dateOfBirth", formattedDate)

                var temporaryFile : File? = null

                if (updatePhoto) {
                    temporaryFile = saveBitmapAsTemporaryFile(fotoUser)
                }

                if (currentPassword.isNotEmpty() && newPassword.isNotEmpty()) {

                    if (currentPassword == App.instance.preferences.getString("currentPassword", "")) {

                        user.addProperty("password", newPassword)

                    }
                    else {

                        App.instance.mainActivity.popupError("The current password is wrong!")
                        return

                    }

                }

                //user.addProperty("password", newPassword)

                val addbtn = binding!!.saveBtn
                val rlprogressave = binding!!.rlprogressave

                addbtn.visibility = View.GONE
                rlprogressave.visibility = View.VISIBLE

                App.instance.backOffice.updateProfile(object : Listener<Any> {
                    override fun onResponse(response: Any?) {

                        if (isAdded) {

                            addbtn.visibility = View.VISIBLE
                            rlprogressave.visibility = View.GONE

                            if (response == null) {

                                Toast.makeText(activity, "Profile updated!", Toast.LENGTH_SHORT).show()
                                findNavController().navigate(R.id.action_editProfileFragment_to_profileFragment2)

                            }
                            else {
                                App.instance.mainActivity.popupError(response.toString())
                            }

                        }

                    }
                }, user, temporaryFile)

            }
            catch (e: JSONException) {
                e.printStackTrace()
            }

        }
        else {

            var erro = ""
            if (!Utils.isOnline(requireContext())) erro = getString(R.string.no_internet)

            App.instance.mainActivity.popupError(erro)

        }

    }

    //selecionar Data de Nascimento
    fun selectBirthDate() {

        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        val dpd = DatePickerDialog(requireContext(), R.style.DialogTheme, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->

            //Display Selected date in textview
            val month = monthOfYear + 1
            val dn = "$dayOfMonth-$month-$year"

            binding!!.dateForm.setText(dn)

        }, year, month, day)

        dpd.show()
        dpd.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextColor(resources.getColor(R.color.primaryColor))
        dpd.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(resources.getColor(R.color.primaryColor))

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

    private val getContent =
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
}