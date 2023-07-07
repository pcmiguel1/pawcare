package com.pawcare.pawcare.fragments.login

import android.Manifest
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Selection
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.google.gson.JsonObject
import com.pawcare.pawcare.App
import com.pawcare.pawcare.R
import com.pawcare.pawcare.Utils
import com.pawcare.pawcare.Utils.hideKeyboard
import com.pawcare.pawcare.databinding.FragmentLoginBinding
import com.pawcare.pawcare.libraries.LoadingDialog
import com.pawcare.pawcare.services.Listener
import org.json.JSONException
import java.io.IOException
import java.util.*
import java.util.concurrent.TimeUnit

class LoginFragment : Fragment() {

    private var binding: FragmentLoginBinding? = null
    private lateinit var loadingDialog: LoadingDialog

    private var rememberUser = false

    var userId = ""

    private var timerActive = false

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val fragmentBinding = FragmentLoginBinding.inflate(inflater, container, false)
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

        binding!!.signupBtn.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }

        binding!!.checkRemember.setOnCheckedChangeListener { buttonView, isChecked ->
            rememberUser = isChecked
        }

        binding!!.loginBtn.setOnClickListener {
            login()
        }

        binding!!.forgotPasswordBtn.setOnClickListener {

            val mDialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_forgot_password, null)

            val builder = AlertDialog.Builder(requireContext())
                .setView(mDialogView)
                .setCancelable(false)

            val dialog = builder.create()
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            val emailForm = mDialogView.findViewById<EditText>(R.id.email_form)
            val cancelBtn = mDialogView.findViewById<Button>(R.id.cancel_btn)
            val sendBtn = mDialogView.findViewById<Button>(R.id.continue_btn)
            val rlprogressContinue = mDialogView.findViewById<View>(R.id.rlprogresscontinue)
            val errorMessage = mDialogView.findViewById<TextView>(R.id.error)

            cancelBtn.setOnClickListener {
                dialog.dismiss()
            }

            sendBtn.setOnClickListener {

                sendBtn.hideKeyboard()

                val validEmail = Utils.validEmail(emailForm.text.toString())

                if (Utils.isOnline(requireContext()) && validEmail) {

                    sendBtn.visibility = View.GONE
                    rlprogressContinue.visibility = View.VISIBLE

                    val user = JsonObject()
                    user.addProperty("email", emailForm.text.toString())

                    App.instance.backOffice.sendVerificationEmailForgotPassword(object : Listener<Any> {
                        override fun onResponse(response: Any?) {

                            sendBtn.visibility = View.VISIBLE
                            rlprogressContinue.visibility = View.GONE

                            if (isAdded) {

                                if (response != null && response is JsonObject && response.has("userId")) {

                                    dialog.dismiss()

                                    userId = response.get("userId").asString

                                    val mDialogView2 = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_forgot_password2, null)
                                    val builder = AlertDialog.Builder(requireContext())
                                        .setView(mDialogView2)
                                        .setCancelable(false)
                                    val dialog2 = builder.create()
                                    dialog2.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

                                    val cancelBtn = mDialogView2.findViewById<Button>(R.id.cancel_btn)
                                    val verifyBtn = mDialogView2.findViewById<Button>(R.id.continue_btn)
                                    val rlprogressContinue = mDialogView2.findViewById<View>(R.id.rlprogresscontinue)

                                    val otpNumber1 = mDialogView2.findViewById<EditText>(R.id.otp_number_1)
                                    val otpNumber2 = mDialogView2.findViewById<EditText>(R.id.otp_number_2)
                                    val otpNumber3 = mDialogView2.findViewById<EditText>(R.id.otp_number_3)
                                    val otpNumber4 = mDialogView2.findViewById<EditText>(R.id.otp_number_4)

                                    val timer = mDialogView2.findViewById<TextView>(R.id.timer)
                                    val resendCodeBtn = mDialogView2.findViewById<TextView>(R.id.resendCode_btn)
                                    val errorMessage = mDialogView2.findViewById<TextView>(R.id.error)

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

                                        App.instance.backOffice.sendVerificationEmailForgotPassword(object : Listener<Any> {
                                            override fun onResponse(response: Any?) {

                                                if (isAdded) {
                                                    if (response != null && response is JsonObject && response.has("userId")) {

                                                        resendCodeBtn.visibility = View.GONE
                                                        countDownTimer.start()
                                                        timerActive = true
                                                        timer.visibility = View.VISIBLE

                                                    }
                                                }

                                            }

                                        }, user)
                                    }

                                    cancelBtn.setOnClickListener {
                                        if (timerActive) countDownTimer.cancel()
                                        dialog2.dismiss()
                                    }

                                    verifyBtn.setOnClickListener {

                                        //dialog2.dismiss()
                                        verifyBtn.hideKeyboard()

                                        val code = otpNumber1.text.toString()+otpNumber2.text.toString()+otpNumber3.text.toString()+otpNumber4.text.toString()

                                        val validCode = Utils.validCode(code)

                                        if (validCode) {

                                            verifyBtn.visibility = View.GONE
                                            rlprogressContinue.visibility = View.VISIBLE

                                            App.instance.backOffice.verifyForgotPasswordCode(object : Listener<Any> {
                                                override fun onResponse(response: Any?) {

                                                    verifyBtn.visibility = View.VISIBLE
                                                    rlprogressContinue.visibility = View.GONE

                                                    if (isAdded) {
                                                        if (response == null) {
                                                            //errorMessage.visibility = View.INVISIBLE
                                                            //Toast.makeText(activity, getString(R.string.register_success), Toast.LENGTH_SHORT).show()
                                                            //requireActivity().onBackPressed()
                                                            dialog2.dismiss()
                                                            val mDialogView3 = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_forgot_password3, null)

                                                            val builder = AlertDialog.Builder(requireContext())
                                                                .setView(mDialogView3)
                                                                .setCancelable(false)

                                                            val dialog3 = builder.create()
                                                            dialog3.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

                                                            val cancelBtn = mDialogView3.findViewById<Button>(R.id.cancel_btn)
                                                            val sendBtn = mDialogView3.findViewById<Button>(R.id.continue_btn)
                                                            val rlprogressContinue = mDialogView3.findViewById<View>(R.id.rlprogresscontinue)


                                                            val newPassworldForm = mDialogView3.findViewById<EditText>(R.id.new_password_form)
                                                            val confirmPassworldForm = mDialogView3.findViewById<EditText>(R.id.confirm_password_form)

                                                            val showHideNewPw = mDialogView3.findViewById<ImageView>(R.id.showHideNewPw)
                                                            val showHideConfirmPw = mDialogView3.findViewById<ImageView>(R.id.showHideConfirmPw)

                                                            val errorMessage = mDialogView3.findViewById<TextView>(R.id.error)

                                                            showHideNewPw.setOnTouchListener { _, event ->
                                                                when(event.action) {
                                                                    MotionEvent.ACTION_UP -> {
                                                                        showHideNewPw.setImageResource(R.drawable.password_hide)
                                                                        val cursorPosition = newPassworldForm.selectionStart
                                                                        newPassworldForm.transformationMethod = PasswordTransformationMethod.getInstance()
                                                                        Selection.setSelection(newPassworldForm.text, cursorPosition)
                                                                        true
                                                                    }
                                                                    MotionEvent.ACTION_DOWN -> {
                                                                        showHideNewPw.setImageResource(R.drawable.password_show)
                                                                        val cursorPosition = newPassworldForm.selectionStart
                                                                        newPassworldForm.transformationMethod = HideReturnsTransformationMethod.getInstance()
                                                                        Selection.setSelection(newPassworldForm.text, cursorPosition)
                                                                        true
                                                                    }
                                                                    else -> false
                                                                }
                                                            }

                                                            showHideConfirmPw.setOnTouchListener { _, event ->
                                                                when(event.action) {
                                                                    MotionEvent.ACTION_UP -> {
                                                                        showHideConfirmPw.setImageResource(R.drawable.password_hide)
                                                                        val cursorPosition = confirmPassworldForm.selectionStart
                                                                        confirmPassworldForm.transformationMethod = PasswordTransformationMethod.getInstance()
                                                                        Selection.setSelection(confirmPassworldForm.text, cursorPosition)
                                                                        true
                                                                    }
                                                                    MotionEvent.ACTION_DOWN -> {
                                                                        showHideConfirmPw.setImageResource(R.drawable.password_show)
                                                                        val cursorPosition = confirmPassworldForm.selectionStart
                                                                        confirmPassworldForm.transformationMethod = HideReturnsTransformationMethod.getInstance()
                                                                        Selection.setSelection(confirmPassworldForm.text, cursorPosition)
                                                                        true
                                                                    }
                                                                    else -> false
                                                                }
                                                            }

                                                            cancelBtn.setOnClickListener {
                                                                dialog3.dismiss()
                                                            }

                                                            sendBtn.setOnClickListener {

                                                                //Utils.hideKeyboard(requireActivity())
                                                                sendBtn.hideKeyboard()

                                                                val newPassword = newPassworldForm.text.toString()
                                                                val confirmPassword = confirmPassworldForm.text.toString()

                                                                val validEqualPassword = newPassword == confirmPassword
                                                                val validPassword = Utils.isValidPassword(newPassword)

                                                                if (Utils.isOnline(requireContext()) && validEqualPassword && validPassword) {

                                                                    val user = JsonObject()

                                                                    user.addProperty("userId", userId)
                                                                    user.addProperty("newPassword", newPassword)
                                                                    user.addProperty("code", code)

                                                                    sendBtn.visibility = View.GONE
                                                                    rlprogressContinue.visibility = View.VISIBLE

                                                                    App.instance.backOffice.resetPassword(object : Listener<Any> {
                                                                        override fun onResponse(response: Any?) {

                                                                            sendBtn.visibility = View.VISIBLE
                                                                            rlprogressContinue.visibility = View.GONE

                                                                            if (isAdded) {
                                                                                if (response == null) {
                                                                                    dialog3.dismiss()
                                                                                    Toast.makeText(activity, getString(R.string.password_reset_success), Toast.LENGTH_SHORT).show()
                                                                                }
                                                                                else {
                                                                                    App.instance.mainActivity!!.popupError(response.toString())
                                                                                }
                                                                            }

                                                                        }

                                                                    }, user)

                                                                }
                                                                else {
                                                                    var erro = ""
                                                                    if (!Utils.isOnline(requireContext())) erro = getString(R.string.no_internet)
                                                                    else if (!validEqualPassword) erro = getString(R.string.password_not_equal)
                                                                    else if (!validPassword) erro = getString(R.string.password_minimum)
                                                                    errorMessage.text = erro
                                                                    errorMessage.visibility = View.VISIBLE
                                                                }

                                                            }

                                                            dialog3.show()

                                                        }
                                                        else {
                                                            errorMessage.text = response.toString()
                                                            errorMessage.visibility = View.VISIBLE
                                                            //App.instance.mainActivity.popupError(response.toString())
                                                        }
                                                    }

                                                }

                                            }, userId, code)

                                        } else {

                                            errorMessage.visibility = View.VISIBLE
                                            errorMessage.text = getString(R.string.invalid_code)

                                        }

                                    }

                                    dialog2.show()
                                    countDownTimer.start()
                                    timerActive = true


                                }
                                else {

                                    //App.instance.mainActivity.popupError(response.toString())
                                    errorMessage.text = response.toString()
                                    errorMessage.visibility = View.VISIBLE

                                }

                            }


                        }
                    }, user)

                }
                else {
                    var erro = ""
                    if (!Utils.isOnline(requireContext())) erro = getString(R.string.no_internet)
                    else if (!validEmail) erro = getString(R.string.invalid_email)

                    errorMessage.visibility = View.VISIBLE
                    errorMessage.text = erro

                }

            }
            dialog.show()

        }


    }

    private fun login() {

        Utils.hideKeyboard(requireActivity())

        val email = binding!!.emailForm.text.toString()
        val password = binding!!.passwordForm.text.toString()

        val loginBtn = binding!!.loginBtn
        val rlProgressLogin = binding!!.rlprogresslogin
        val checkboxRemember = binding!!.checkRemember


        val validEmail = Utils.validEmail(email)

        if (Utils.isOnline(requireContext()) && validEmail) {

            val user = JsonObject()

            try {

                user.addProperty("email", email)
                user.addProperty("password", password)

                loginBtn.visibility = View.GONE
                rlProgressLogin.visibility = View.VISIBLE
                checkboxRemember.isEnabled = false

                App.instance.backOffice.loginUser(object : Listener<Any> {

                    override fun onResponse(response: Any?) {

                        loginBtn.visibility = View.VISIBLE
                        rlProgressLogin.visibility = View.GONE
                        checkboxRemember.isEnabled = true

                        if (response != null && response is JsonObject && response.getAsJsonObject("user") != null) {

                            getMyLocation()

                            App.instance.preferences.edit().putBoolean("stayLoggedIn", checkboxRemember.isChecked).apply()

                            //findNavController().navigate(R.id.action_loginFragment_to_exploreFragment2)

                            App.instance.preferences.edit().putString("currentPassword", password).apply()

                            // Remove the back stack and navigate to the specified destination
                            findNavController().apply {
                                popBackStack(R.id.onBoardingFragment, true)
                                navigate(R.id.exploreFragment2)
                            }

                        }
                        else {
                            App.instance.mainActivity!!.popupError(response.toString())
                        }

                    }

                }, user)

            }
            catch (e: JSONException) {
                e.printStackTrace()
            }

        }
        else {
            var erro = ""
            if (!Utils.isOnline(requireContext())) erro = getString(R.string.no_internet)
            else if (!validEmail) erro = getString(R.string.invalid_email)

            App.instance.mainActivity!!.popupError(erro)

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

                        App.instance.preferences.edit()

                            .putString("Latitude", latitude.toString())
                            .putString("Longitude", longitude.toString())

                            .apply()

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