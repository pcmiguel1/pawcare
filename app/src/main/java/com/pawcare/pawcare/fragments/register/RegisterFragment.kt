package com.pawcare.pawcare.fragments.register

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.DialogInterface
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Environment
import android.text.Selection
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.google.gson.JsonObject
import com.hbb20.CountryCodePicker
import com.pawcare.pawcare.App
import com.pawcare.pawcare.BuildConfig
import com.pawcare.pawcare.R
import com.pawcare.pawcare.Utils
import com.pawcare.pawcare.Utils.hideKeyboard
import com.pawcare.pawcare.databinding.FragmentRegisterBinding
import com.pawcare.pawcare.libraries.LoadingDialog
import com.pawcare.pawcare.services.ApiInterface
import com.pawcare.pawcare.services.Listener
import org.json.JSONException
import java.io.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class RegisterFragment : Fragment() {

    private var binding: FragmentRegisterBinding? = null
    private lateinit var loadingDialog: LoadingDialog

    private lateinit var fotoUser: Bitmap
    private var updatePhoto = false

    private lateinit var dialog: AlertDialog
    private var timerActive = false

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

        Utils.hideKeyboard(requireActivity())

        val fullName = binding!!.fullnameForm.text.toString()
        val dateBirth = binding!!.dateForm.text.toString()
        val email = binding!!.emailForm.text.toString()
        val password = binding!!.passwordForm.text.toString()

        val validFullName = fullName.length >= 5
        val validPhoneNumber = ccp.isValidFullNumber
        val validEmail = Utils.validEmail(email)
        val validPassword = Utils.isValidPassword(password)

        if (Utils.isOnline(requireContext()) && validFullName && dateBirth.isNotEmpty() && validEmail && validPassword) {

            val user = JsonObject()

            try {

                var formattedDate = ""
                if (dateBirth.isNotEmpty()) {
                    val parser = SimpleDateFormat("dd-MM-yyyy")
                    val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                    formattedDate = formatter.format(parser.parse(dateBirth))
                }

                user.addProperty("fullname", fullName)
                user.addProperty("dateOfBirth", formattedDate)
                //if (validPhoneNumber) user.addProperty("phoneNumber", ccp.fullNumber)
                user.addProperty("email", email)
                user.addProperty("password", password)

                var temporaryFile : File? = null

                if (updatePhoto) {
                    temporaryFile = saveBitmapAsTemporaryFile(fotoUser)
                }

                val signupbtn = binding!!.signupBtn
                val rlprogresssignup = binding!!.rlprogresssignup

                signupbtn.visibility = View.GONE
                rlprogresssignup.visibility = View.VISIBLE

                App.instance.backOffice.registerUser(object : Listener<Any> {
                    override fun onResponse(response: Any?) {

                        if (isAdded) {

                            signupbtn.visibility = View.VISIBLE
                            rlprogresssignup.visibility = View.GONE

                            if (response != null && response is ApiInterface.User) {

                                showDialogVerifyEmail(response.id!!)

                            }
                            else {

                                App.instance.mainActivity!!.popupError(response.toString())

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
            else if (!validFullName) erro = getString(R.string.name_minimum)
            else if (dateBirth.isEmpty()) erro = getString(R.string.data_nascimento_erro)
            //else if (!validPhoneNumber) erro = getString(R.string.invalid_phone)
            else if (!validEmail) erro = getString(R.string.invalid_email)
            else if (!validPassword) erro = getString(R.string.password_minimum)

            App.instance.mainActivity!!.popupError(erro)

        }

    }

    private fun showDialogVerifyEmail(id: String) {

        val mDialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_verify_email, null)

        val builder = AlertDialog.Builder(requireContext())
            .setView(mDialogView)
            .setCancelable(false)

        dialog = builder.create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val timer = mDialogView.findViewById<TextView>(R.id.timer)
        val resendCodeBtn = mDialogView.findViewById<TextView>(R.id.resendCode_btn)
        val cancelBtn = mDialogView.findViewById<View>(R.id.cancel_btn)
        val rlprogresverify = mDialogView.findViewById<View>(R.id.rlprogresverify)
        val continueButton = mDialogView.findViewById<View>(R.id.continue_btn)

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

            App.instance.backOffice.resendCodeEmail(object : Listener<Any> {
                override fun onResponse(response: Any?) {

                    if (isAdded) {
                        if (response == null) {

                            countDownTimer.start()
                            timerActive = true
                            timer.visibility = View.VISIBLE

                        }
                        else {

                        }
                    }

                }

            }, id)

        }

        val errorMessage = mDialogView.findViewById<TextView>(R.id.error)

        continueButton.setOnClickListener {

            val code = otpNumber1.text.toString()+otpNumber2.text.toString()+otpNumber3.text.toString()+otpNumber4.text.toString()

            val validCode = Utils.validCode(code)

            if (validCode) {

                continueButton.visibility = View.GONE
                rlprogresverify.visibility = View.VISIBLE

                App.instance.backOffice.verifyCodeEmail(object : Listener<Any> {
                    override fun onResponse(response: Any?) {

                        continueButton.visibility = View.VISIBLE
                        rlprogresverify.visibility = View.GONE

                        if (isAdded) {
                            if (response == null) {
                                errorMessage.visibility = View.INVISIBLE
                                dialog.dismiss()
                                Toast.makeText(activity, getString(R.string.register_success), Toast.LENGTH_SHORT).show()
                                requireActivity().onBackPressed()
                            }
                            else {
                                errorMessage.text = response.toString()
                                errorMessage.visibility = View.VISIBLE
                                //App.instance.mainActivity.popupError(response.toString())
                            }
                        }

                    }

                }, id, code)

            }
            else {

                errorMessage.text = getString(R.string.invalid_code)
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