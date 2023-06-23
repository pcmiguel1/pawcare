package com.pawcare.pawcare.fragments.login

import android.os.Bundle
import android.text.Selection
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.gson.JsonObject
import com.pawcare.pawcare.App
import com.pawcare.pawcare.R
import com.pawcare.pawcare.Utils
import com.pawcare.pawcare.databinding.FragmentLoginBinding
import com.pawcare.pawcare.libraries.LoadingDialog
import com.pawcare.pawcare.services.Listener
import org.json.JSONException

class LoginFragment : Fragment() {

    private var binding: FragmentLoginBinding? = null
    private lateinit var loadingDialog: LoadingDialog

    private var rememberUser = false

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

                            App.instance.preferences.edit().putBoolean("stayLoggedIn", checkboxRemember.isChecked).apply()

                            findNavController().navigate(R.id.action_loginFragment_to_exploreFragment2)

                        }
                        else {
                            App.instance.mainActivity.popupError(response.toString())
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

            App.instance.mainActivity.popupError(erro)

        }

    }

}