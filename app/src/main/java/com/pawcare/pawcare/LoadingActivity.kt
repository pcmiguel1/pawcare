package com.pawcare.pawcare

import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.pawcare.pawcare.libraries.ErrorDialog
import com.pawcare.pawcare.services.Listener
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger
import kotlin.concurrent.thread

class LoadingActivity : AppCompatActivity() {

    private lateinit var errorDialog: ErrorDialog
    private val success = AtomicBoolean(true)
    private val receivedResponses = AtomicInteger(0)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loading)

        errorDialog = ErrorDialog(this)

        App.instance.backOffice.getToken(object : Listener<Any> {
            override fun onResponse(response: Any?) {
                if (!isFinishing) {



                }
            }

        })

        Handler(Looper.getMainLooper()).postDelayed({
            thread {
                runOnUiThread {
                    //performBackOfficeRequests()
                    startMainActivity()
                }
            }
        }, 3000)

    }

    private fun startMainActivity() {

        runOnUiThread {

            if (checkPermission()) {

                val intent = Intent(applicationContext, MainActivity::class.java)

                if (getIntent() != null && getIntent().extras != null)
                    intent.putExtras(getIntent().extras!!)

                startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION))
                finish()

            }
            else requestPermission()

        }

    }

    private fun checkPermission() : Boolean {
        val result = ContextCompat.checkSelfPermission(applicationContext, android.Manifest.permission.ACCESS_FINE_LOCATION)
        val result2 = ContextCompat.checkSelfPermission(applicationContext, android.Manifest.permission.ACCESS_COARSE_LOCATION)
        return result == PackageManager.PERMISSION_GRANTED && result2 == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION), 1)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {

            1 -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    startMainActivity()
                }
                else {

                    errorDialog.startLoading(getString(R.string.location_permission), getString(R.string.permission_needed)) {
                        requestPermission()
                    }

                }
            }

        }
    }

    private fun performBackOfficeRequests() {

        val numberOfServices = 1
        val downloadProgress = findViewById<TextView>(R.id.downloading_progress)

        val listener = object : Listener<Any> {
            override fun onResponse(response: Any?) {
                if (success.get()) {

                    if (response == null) {

                        var percentage = (receivedResponses.incrementAndGet() / numberOfServices.toFloat() * 100).toInt()
                        if (percentage > 100) percentage = 100
                        runOnUiThread { downloadProgress.text = "$percentage%" }

                        if (receivedResponses.get() == numberOfServices)
                            startMainActivity()

                    }
                    else {

                        success.set(false)

                    }

                }
            }
        }

        App.instance.backOffice.getToken(object : Listener<Any> {
            override fun onResponse(response: Any?) {
                if (!isFinishing)
                    performUpdates(listener)
            }

        })

    }

    fun performUpdates(listener: Listener<Any>) {



    }

}