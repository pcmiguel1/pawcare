package com.pawcare.pawcare.fragments.profile

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
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

class ProfileFragment : Fragment() {

    private var binding: FragmentProfileBinding? = null

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

        binding!!.name.text = App.instance.preferences.getString("fullname", "")
        binding!!.email.text = App.instance.preferences.getString("email", "")

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

        if (App.instance.preferences.getBoolean("SITTER", false))
            binding!!.switchsittertext.text = "Switch to Pet Owner"
        else binding!!.switchsittertext.text = "Switch to Sitter"

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