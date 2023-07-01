package com.pawcare.pawcare.fragments.sitterapplication

import android.app.AlertDialog
import android.content.DialogInterface
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.pawcare.pawcare.App
import com.pawcare.pawcare.BuildConfig
import com.pawcare.pawcare.R
import com.pawcare.pawcare.Utils
import com.pawcare.pawcare.databinding.FragmentApplicationAnswerBinding
import com.pawcare.pawcare.fragments.mypets.adapter.PetAdapter
import com.pawcare.pawcare.fragments.sitterapplication.adapter.PictureAdapter
import com.pawcare.pawcare.libraries.LoadingDialog
import com.pawcare.pawcare.services.ApiInterface
import com.pawcare.pawcare.services.Listener
import java.io.*

class ApplicationAnswerFragment : Fragment() {

    private var binding: FragmentApplicationAnswerBinding? = null
    private lateinit var loadingDialog: LoadingDialog

    private lateinit var pictureAdapter : PictureAdapter

    private var step = 0

    private lateinit var fotoPicture: Bitmap
    private var updatePhoto = false

    private var pictures : MutableList<ApiInterface.Picture> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val fragmentBinding = FragmentApplicationAnswerBinding.inflate(inflater, container, false)
        binding = fragmentBinding

        App.instance.mainActivity.findViewById<LinearLayout>(R.id.bottombar).visibility = View.GONE

        val bundle = arguments
        if (bundle != null) {
            step = bundle.getInt("STEP")
        }

        return fragmentBinding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadingDialog = LoadingDialog(requireContext())
        Utils.navigationBar(view, "", requireActivity())

        when (step) {

            1 -> {

                binding!!.step1.visibility = View.VISIBLE

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

                binding!!.step1.visibility = View.VISIBLE

            }

            4 -> {

                binding!!.step1.visibility = View.VISIBLE

            }

            5 -> {

                binding!!.step1.visibility = View.VISIBLE

            }

            6 -> {

                binding!!.step1.visibility = View.VISIBLE

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

}