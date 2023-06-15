package com.pawcare.pawcare

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import java.util.ArrayList

object Utils {

    fun navigationBar(v: View, theme: String, activity: Activity) {

        val title = v.findViewById<View>(R.id.toolbar).findViewById<TextView>(R.id.title)
        title.text = theme

        val backBt = v.findViewById<View>(R.id.back_bt)
        backBt.setOnClickListener { activity.onBackPressed() }

    }

    fun checkPhotoPermissions(fragment: Fragment, code: Int): Boolean {
        val cameraPermission = Manifest.permission.CAMERA
        val writeExternalPermission = Manifest.permission.WRITE_EXTERNAL_STORAGE
        val readExternalPermission = Manifest.permission.READ_EXTERNAL_STORAGE

        val permissionsToRequest = ArrayList<String>()

        if (ActivityCompat.checkSelfPermission(fragment.requireContext(), cameraPermission) != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(cameraPermission)
        }

        if (ActivityCompat.checkSelfPermission(fragment.requireContext(), writeExternalPermission) != PackageManager.PERMISSION_GRANTED && Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            permissionsToRequest.add(writeExternalPermission)
        }

        if (ActivityCompat.checkSelfPermission(fragment.requireContext(), readExternalPermission) != PackageManager.PERMISSION_GRANTED && Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            permissionsToRequest.add(readExternalPermission)
        }

        if (permissionsToRequest.isNotEmpty()) {
            ActivityCompat.requestPermissions(fragment.requireActivity(), permissionsToRequest.toTypedArray(), code)
            return false
        }

        return true
    }

    fun rotateImageIfRequired(context: Context, img: Bitmap, selectedImage: Uri) : Bitmap {

        var ei : ExifInterface
        val input = context.contentResolver.openInputStream(selectedImage)
        if (Build.VERSION.SDK_INT > 23) ei = ExifInterface(input!!)
        else ei = ExifInterface(selectedImage.path!!)

        val orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED)
        when (orientation) {
            0 -> return rotateBitmap(img, 0)
            ExifInterface.ORIENTATION_ROTATE_90 -> return rotateBitmap(img, 90)
            ExifInterface.ORIENTATION_ROTATE_180 -> return rotateBitmap(img, 180)
            ExifInterface.ORIENTATION_ROTATE_270 -> return rotateBitmap(img, 270)
            else -> return img

        }

    }

    fun rotateBitmap(bMap: Bitmap, orientation: Int): Bitmap {
        Log.d("Time", "Rotate init" + System.currentTimeMillis())

        val bMapRotate: Bitmap
        bMapRotate = if (orientation != 0) {
            val matrix = Matrix()
            matrix.postRotate(orientation.toFloat())
            Bitmap.createBitmap(bMap, 0, 0, bMap.width,
                bMap.height, matrix, true)
        } else
            Bitmap.createScaledBitmap(bMap, bMap.width,
                bMap.height, true)

        return bMapRotate
    }

}