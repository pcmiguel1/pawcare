package com.pawcare.pawcare.fragments.sitterapplication

import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.pawcare.pawcare.App
import com.pawcare.pawcare.R
import com.pawcare.pawcare.Utils
import com.pawcare.pawcare.databinding.FragmentProgressApplicationBinding
import com.pawcare.pawcare.libraries.LoadingDialog
import com.pawcare.pawcare.services.ApiInterface
import com.pawcare.pawcare.services.Listener


class ProgressApplicationFragment : Fragment() {

    private var binding: FragmentProgressApplicationBinding? = null
    private lateinit var loadingDialog: LoadingDialog

    private var sitter : ApiInterface.Sitter? = null

    private var submitted : Boolean = false

    private var validStep1 = false
    private var validStep2 = false
    private var validStep3 = false
    private var validStep4 = false
    private var validStep5 = false
    private var validStep6 = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val fragmentBinding = FragmentProgressApplicationBinding.inflate(inflater, container, false)
        binding = fragmentBinding

        App.instance.mainActivity!!.findViewById<LinearLayout>(R.id.bottombar).visibility = View.GONE

        val bundle = arguments
        if (bundle != null) {

            if (bundle.containsKey("SUBMITTED"))
                submitted = bundle.getBoolean("SUBMITTED")

        }

        return fragmentBinding.root

    }

    override fun onResume() {
        super.onResume()

        if (!submitted) {

            loadingDialog.startLoading()

            App.instance.backOffice.getSitter(object : Listener<Any> {
                override fun onResponse(response: Any?) {

                    if (isAdded) {

                        if (response != null && response is ApiInterface.Sitter) {

                            sitter = response

                            App.instance.backOffice.getPictures(object : Listener<Any> {
                                override fun onResponse(response: Any?) {

                                    loadingDialog.isDismiss()

                                    if (isAdded) {

                                        if (response != null && response is List<*>) {

                                            val list = response as List<ApiInterface.Picture>

                                            //Step 1 validation
                                            // check if contains phone, headline and description
                                            if (App.instance.preferences.getString("image", "") != "" && sitter!!.headline != null && sitter!!.headline != "" && sitter!!.description != null && sitter!!.description != "") {

                                                validStep1 = true

                                                binding!!.step1Cardview.backgroundTintList = ColorStateList.valueOf(
                                                    ContextCompat.getColor(requireContext(), R.color.primaryLighterColor))
                                                binding!!.step1Icon.visibility = View.VISIBLE
                                                binding!!.step1Number.visibility = View.GONE

                                            }
                                            else {
                                                validStep1 = false

                                                binding!!.step1Cardview.backgroundTintList = ColorStateList.valueOf(
                                                    ContextCompat.getColor(requireContext(), R.color.white))
                                                binding!!.step1Icon.visibility = View.GONE
                                                binding!!.step1Number.visibility = View.VISIBLE

                                            }


                                            //Step2 validation
                                            // check if contains at least one picture
                                            if (list.isNotEmpty()) {

                                                validStep2 = true

                                                binding!!.step2Cardview.backgroundTintList = ColorStateList.valueOf(
                                                    ContextCompat.getColor(requireContext(), R.color.primaryLighterColor))
                                                binding!!.step2Icon.visibility = View.VISIBLE
                                                binding!!.step2Number.visibility = View.GONE


                                            }
                                            else {
                                                validStep2 = false

                                                binding!!.step2Cardview.backgroundTintList = ColorStateList.valueOf(
                                                    ContextCompat.getColor(requireContext(), R.color.white))
                                                binding!!.step2Icon.visibility = View.GONE
                                                binding!!.step2Number.visibility = View.VISIBLE

                                            }

                                            // step3 validation
                                            //check if exists phone number

                                            if (sitter!!.phone != null && sitter!!.phone != "") {

                                                validStep3 = true
                                                binding!!.step3Cardview.backgroundTintList = ColorStateList.valueOf(
                                                    ContextCompat.getColor(requireContext(), R.color.primaryLighterColor))
                                                binding!!.step3Icon.visibility = View.VISIBLE
                                                binding!!.step3Number.visibility = View.GONE

                                            }
                                            else {

                                                validStep3 = false
                                                binding!!.step3Cardview.backgroundTintList = ColorStateList.valueOf(
                                                    ContextCompat.getColor(requireContext(), R.color.white))
                                                binding!!.step3Icon.visibility = View.GONE
                                                binding!!.step3Number.visibility = View.VISIBLE
                                            }

                                            //step4 validation
                                            // check if exists sorcode and account number

                                            if (sitter!!.sortcode != null && sitter!!.sortcode != "" && sitter!!.accountnumber != null && sitter!!.accountnumber != "") {

                                                validStep4 = true

                                                binding!!.step4Cardview.backgroundTintList = ColorStateList.valueOf(
                                                    ContextCompat.getColor(requireContext(), R.color.primaryLighterColor))
                                                binding!!.step4Icon.visibility = View.VISIBLE
                                                binding!!.step4Number.visibility = View.GONE

                                            }
                                            else {

                                                validStep4 = false

                                                binding!!.step4Cardview.backgroundTintList = ColorStateList.valueOf(
                                                    ContextCompat.getColor(requireContext(), R.color.white))
                                                binding!!.step4Icon.visibility = View.GONE
                                                binding!!.step4Number.visibility = View.VISIBLE

                                            }


                                            //step5 validation
                                            //check if exists lat and long

                                            if (sitter!!.lat != null && sitter!!.lat != "" && sitter!!.long != null && sitter!!.long != "") {

                                                validStep5 = true

                                                binding!!.step5Cardview.backgroundTintList = ColorStateList.valueOf(
                                                    ContextCompat.getColor(requireContext(), R.color.primaryLighterColor))
                                                binding!!.step5Icon.visibility = View.VISIBLE
                                                binding!!.step5Number.visibility = View.GONE

                                            }
                                            else {

                                                validStep5 = false

                                                binding!!.step5Cardview.backgroundTintList = ColorStateList.valueOf(
                                                    ContextCompat.getColor(requireContext(), R.color.white))
                                                binding!!.step5Icon.visibility = View.GONE
                                                binding!!.step5Number.visibility = View.VISIBLE

                                            }

                                            //step6 validation
                                            //check if at least one service is selected

                                            if (sitter!!.petwalking!! || sitter!!.petboarding!! || sitter!!.housesitting!! || sitter!!.training!! || sitter!!.grooming!!) {

                                                validStep6 = true

                                                binding!!.step6Cardview.backgroundTintList = ColorStateList.valueOf(
                                                    ContextCompat.getColor(requireContext(), R.color.primaryLighterColor))
                                                binding!!.step6Icon.visibility = View.VISIBLE
                                                binding!!.step6Number.visibility = View.GONE

                                            }
                                            else {

                                                validStep6 = false

                                                binding!!.step6Cardview.backgroundTintList = ColorStateList.valueOf(
                                                    ContextCompat.getColor(requireContext(), R.color.white))
                                                binding!!.step6Icon.visibility = View.GONE
                                                binding!!.step6Number.visibility = View.VISIBLE

                                            }

                                            if (validStep1 && validStep2 && validStep3 && validStep4 && validStep5 && validStep6) {
                                                binding!!.startApplicationBtn.isEnabled = true
                                                binding!!.startApplicationBtn.backgroundTintList = ColorStateList.valueOf(
                                                    ContextCompat.getColor(requireContext(), R.color.primaryColor))
                                            }
                                            else {
                                                binding!!.startApplicationBtn.isEnabled = false
                                                binding!!.startApplicationBtn.backgroundTintList = ColorStateList.valueOf(
                                                    ContextCompat.getColor(requireContext(), R.color.grey))
                                            }



                                        }
                                        else {
                                            validStep2 = false

                                            binding!!.step2Cardview.backgroundTintList = ColorStateList.valueOf(
                                                ContextCompat.getColor(requireContext(), R.color.white))
                                            binding!!.step2Icon.visibility = View.GONE
                                            binding!!.step2Number.visibility = View.VISIBLE

                                        }

                                    }

                                }
                            })

                        }

                    }

                }

            })

        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadingDialog = LoadingDialog(requireContext())

        Utils.navigationBar(view, "Sitter Application", requireActivity())

        if (!submitted) {

            binding!!.form.visibility = View.VISIBLE

            binding!!.startApplicationBtn.setOnClickListener {

                binding!!.startApplicationBtn.visibility = View.GONE
                binding!!.rlprogresstart.visibility = View.VISIBLE

                App.instance.backOffice.applicationSubmit(object : Listener<Any> {
                    override fun onResponse(response: Any?) {

                        binding!!.startApplicationBtn.visibility = View.VISIBLE
                        binding!!.rlprogresstart.visibility = View.GONE

                        if (isAdded) {

                            if (response == null) {

                                Toast.makeText(requireContext(), "Successfully submitted!", Toast.LENGTH_SHORT).show()
                                findNavController().navigate(R.id.action_progressApplicationFragment_to_profileFragment2)

                            }

                        }

                    }

                })

            }


            binding!!.step1.setOnClickListener {

                val bundle = Bundle()
                bundle.putInt("STEP", 1)
                bundle.putParcelable("SITTER", sitter)
                findNavController().navigate(R.id.action_progressApplicationFragment_to_applicationAnswerFragment, bundle)

            }

            binding!!.step2.setOnClickListener {

                val bundle = Bundle()
                bundle.putInt("STEP", 2)
                findNavController().navigate(R.id.action_progressApplicationFragment_to_applicationAnswerFragment, bundle)

            }

            binding!!.step3.setOnClickListener {

                val bundle = Bundle()
                bundle.putInt("STEP", 3)
                findNavController().navigate(R.id.action_progressApplicationFragment_to_applicationAnswerFragment, bundle)

            }

            binding!!.step4.setOnClickListener {

                val bundle = Bundle()
                bundle.putInt("STEP", 4)
                bundle.putParcelable("SITTER", sitter)
                findNavController().navigate(R.id.action_progressApplicationFragment_to_applicationAnswerFragment, bundle)

            }

            binding!!.step5.setOnClickListener {

                val bundle = Bundle()
                bundle.putInt("STEP", 5)
                bundle.putParcelable("SITTER", sitter)
                findNavController().navigate(R.id.action_progressApplicationFragment_to_applicationAnswerFragment, bundle)

            }

            binding!!.step6.setOnClickListener {

                val bundle = Bundle()
                bundle.putInt("STEP", 6)
                bundle.putParcelable("SITTER", sitter)
                findNavController().navigate(R.id.action_progressApplicationFragment_to_applicationAnswerFragment, bundle)

            }

        }
        else {

            binding!!.form.visibility = View.GONE
            binding!!.submited.visibility = View.VISIBLE

        }

    }

}