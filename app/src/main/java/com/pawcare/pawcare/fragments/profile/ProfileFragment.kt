package com.pawcare.pawcare.fragments.profile

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.pawcare.pawcare.App
import com.pawcare.pawcare.R
import com.pawcare.pawcare.databinding.FragmentProfileBinding
import com.pawcare.pawcare.libraries.LoadingDialog
import com.pawcare.pawcare.services.ApiInterface
import com.pawcare.pawcare.services.Listener
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import java.lang.Exception

class ProfileFragment : Fragment() {

    private var binding: FragmentProfileBinding? = null

    private var isSitter : Boolean = false
    private var verifiedSitter : Boolean = false
    private var applicationSubmitted : Boolean = false

    private lateinit var loadingDialog: LoadingDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val fragmentBinding = FragmentProfileBinding.inflate(inflater, container, false)
        binding = fragmentBinding

        App.instance.mainActivity.findViewById<LinearLayout>(R.id.bottombar).visibility = View.VISIBLE

        return fragmentBinding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadingDialog = LoadingDialog(requireContext())

        binding!!.name.text = App.instance.preferences.getString("fullname", "")
        binding!!.email.text = App.instance.preferences.getString("email", "")

        val userPhoto = binding!!.profileIcon
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

        loadingDialog.startLoading()

        App.instance.backOffice.getSitter(object : Listener<Any> {
            override fun onResponse(response: Any?) {

                if (isAdded) {

                    if (response != null && response is ApiInterface.Sitter) {

                        //Is a sitter
                        if (response.verified!!) {
                            isSitter = true
                            verifiedSitter = true
                            if (App.instance.preferences.getBoolean("SITTER", false))
                                binding!!.switchsittertext.text = "Switch to Pet Owner"
                            else binding!!.switchsittertext.text = "Switch to Sitter"
                        }
                        else {

                            isSitter = true
                            verifiedSitter = false
                            binding!!.switchsittertext.text = "Become a Sitter"

                        }

                    } else {

                        //is not a sitter
                        binding!!.switchsittertext.text = "Become a Sitter"
                        isSitter = false
                        verifiedSitter = false

                    }

                }

            }

        })

        App.instance.backOffice.getApplication(object : Listener<Any> {
            override fun onResponse(response: Any?) {

                loadingDialog.isDismiss()

                if (isAdded) {

                    if (response != null && response is ApiInterface.ApplicationSitter) {

                        applicationSubmitted = true

                    }
                    else {
                        applicationSubmitted = false
                    }

                }

            }
        })

        binding!!.logoutBtn.setOnClickListener {

            with(App.instance.preferences.edit()) {
                remove("phoneNumber")
                remove("stayLoggedIn")
                remove("TOKEN_DATE")
                remove("dateOfBirth")
                remove("fullname")
                remove("phoneNumber")
                remove("TOKEN")
                remove("SITTER")
                remove("userId")
                remove("sitterId")
                remove("email")
                apply()
            }

            App.instance.backOffice.getToken(null)

            // Remove the back stack and navigate to the specified destination
            findNavController().apply {
                popBackStack(R.id.onBoardingFragment, true)
                navigate(R.id.onBoardingFragment)
            }

        }

        binding!!.mypets.setOnClickListener {

            findNavController().navigate(R.id.action_profileFragment2_to_myPetsFragment)

        }

        binding!!.editProfileBtn.setOnClickListener {

            findNavController().navigate(R.id.action_profileFragment2_to_editProfileFragment)

        }

        binding!!.editProfile.setOnClickListener {

            findNavController().navigate(R.id.action_profileFragment2_to_editProfileFragment)

        }

        binding!!.aboutPawcare.setOnClickListener {

            findNavController().navigate(R.id.action_profileFragment2_to_aboutFragment)

        }

        binding!!.notifications.setOnClickListener {

            findNavController().navigate(R.id.action_profileFragment2_to_notificationFragment)

        }

        binding!!.switchsitter.setOnClickListener {

            if (isSitter) { //is a sitter

                if (verifiedSitter) { // already approved and submited the application

                    val bottomNavigationView = App.instance.mainActivity.findViewById<BottomNavigationView>(R.id.bottom_navigation_view)

                    if (App.instance.preferences.getBoolean("SITTER", false)) {

                        App.instance.preferences.edit().putBoolean("SITTER", false).apply()

                        findNavController().navigate(R.id.action_profileFragment2_to_exploreFragment2)

                        bottomNavigationView.menu.findItem(R.id.exploreFragment).title = getString(R.string.explore)
                        bottomNavigationView.menu.findItem(R.id.exploreFragment).icon = resources.getDrawable(R.drawable.explore)
                        bottomNavigationView.menu.findItem(R.id.exploreFragment).isChecked = true

                        bottomNavigationView.menu.findItem(R.id.bookingsFragment).title = getString(R.string.bookings)
                        bottomNavigationView.menu.findItem(R.id.bookingsFragment).icon = resources.getDrawable(R.drawable.bookings)

                    }
                    else {

                        App.instance.preferences.edit().putBoolean("SITTER", true).apply()

                        findNavController().navigate(R.id.action_profileFragment2_to_dashboardFragment)

                        bottomNavigationView.menu.findItem(R.id.exploreFragment).title = getString(R.string.dashboard)
                        bottomNavigationView.menu.findItem(R.id.exploreFragment).icon = resources.getDrawable(R.drawable.dashboard)
                        bottomNavigationView.menu.findItem(R.id.exploreFragment).isChecked = true

                        bottomNavigationView.menu.findItem(R.id.bookingsFragment).title = getString(R.string.calendar)
                        bottomNavigationView.menu.findItem(R.id.bookingsFragment).icon = resources.getDrawable(R.drawable.calendar)



                    }

                } else { //navigate to the progress application

                    if (applicationSubmitted) {
                        val bundle = Bundle()
                        bundle.putBoolean("SUBMITTED", true)
                        findNavController().navigate(R.id.action_profileFragment2_to_progressApplicationFragment, bundle)
                    }
                    else {
                        val bundle = Bundle()
                        bundle.putBoolean("SUBMITTED", false)
                        findNavController().navigate(R.id.action_profileFragment2_to_progressApplicationFragment, bundle)
                    }

                }

            } else { //if not option to become a sitter

                findNavController().navigate(R.id.action_profileFragment2_to_sitterApplicationFragment)

            }

        }

        binding!!.deleteAccount.setOnClickListener {

            val mDialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_delete_account, null)

            val builder = AlertDialog.Builder(requireContext())
                .setView(mDialogView)
                .setCancelable(false)

            val dialog = builder.create()
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            val cancelBtn = mDialogView.findViewById<Button>(R.id.cancel_btn)
            val sendBtn = mDialogView.findViewById<Button>(R.id.continue_btn)

            cancelBtn.setOnClickListener {

                dialog.dismiss()

            }

            sendBtn.setOnClickListener {


            }

            dialog.show()


        }

    }

}