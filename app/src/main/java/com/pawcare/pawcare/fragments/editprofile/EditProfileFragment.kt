package com.pawcare.pawcare.fragments.editprofile

import android.app.DatePickerDialog
import android.os.Bundle
import android.text.Selection
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.navigation.fragment.findNavController
import com.google.gson.JsonObject
import com.pawcare.pawcare.App
import com.pawcare.pawcare.R
import com.pawcare.pawcare.Utils
import com.pawcare.pawcare.databinding.FragmentEditProfileBinding
import com.pawcare.pawcare.services.Listener
import org.json.JSONException
import java.text.SimpleDateFormat
import java.util.*


class EditProfileFragment : Fragment() {

    private var binding: FragmentEditProfileBinding? = null

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

                                val editor = App.instance.preferences.edit()

                                editor.putString("fullname", fullName)
                                editor.putString("dateOfBirth", formattedDate)

                                editor.apply()

                                findNavController().navigate(R.id.action_editProfileFragment_to_profileFragment2)

                            }
                            else {
                                App.instance.mainActivity.popupError(response.toString())
                            }

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

}