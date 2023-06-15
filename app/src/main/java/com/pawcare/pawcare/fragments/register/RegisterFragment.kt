package com.pawcare.pawcare.fragments.register

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
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.hbb20.CountryCodePicker
import com.pawcare.pawcare.BuildConfig
import com.pawcare.pawcare.R
import com.pawcare.pawcare.Utils
import com.pawcare.pawcare.databinding.FragmentRegisterBinding
import com.pawcare.pawcare.libraries.LoadingDialog
import java.io.File
import java.io.FileNotFoundException
import java.io.InputStream
import java.util.*

class RegisterFragment : Fragment() {

    private var binding: FragmentRegisterBinding? = null
    private lateinit var loadingDialog: LoadingDialog

    private lateinit var fotoUser: Bitmap
    private var updatePhoto = false

    private lateinit var ccp : CountryCodePicker

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val fragmentBinding = FragmentRegisterBinding.inflate(inflater, container, false)
        binding = fragmentBinding

        return fragmentBinding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadingDialog = LoadingDialog(requireContext())
        Utils.navigationBar(view, "", requireActivity())

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

        binding!!.dateForm.setOnTouchListener { v, event ->
            if (event?.action == MotionEvent.ACTION_UP) {
                selectBirthDate()
            }
            true
        }

        binding!!.browsePhoto.setOnClickListener {
            photoPickMethodDialog()
        }

        binding!!.dateFormParent.setOnClickListener {

            selectBirthDate()

        }

        binding!!.signupBtn.setOnClickListener {

            register()

        }

        ccp = binding!!.ccp
        ccp.registerCarrierNumberEditText(binding?.phoneForm)

    }

    private fun register() {

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

}