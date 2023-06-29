package com.pawcare.pawcare.fragments.addpet

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import androidx.navigation.fragment.findNavController
import com.pawcare.pawcare.App
import com.pawcare.pawcare.R
import com.pawcare.pawcare.Utils
import com.pawcare.pawcare.databinding.FragmentAddPetBinding
import com.pawcare.pawcare.libraries.LoadingDialog
import com.pawcare.pawcare.services.ApiInterface
import com.pawcare.pawcare.services.Listener

class AddPetFragment : Fragment() {

    private var binding: FragmentAddPetBinding? = null

    private lateinit var loadingDialog : LoadingDialog

    private val itemsSpecie = listOf("Dog", "Cat", "Bird", "Fish", "Reptile", "Amphibian", "Small mammal")
    private val itemsGender = listOf("Male", "Female")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val fragmentBinding = FragmentAddPetBinding.inflate(inflater, container, false)
        binding = fragmentBinding

        App.instance.mainActivity.findViewById<LinearLayout>(R.id.bottombar).visibility = View.GONE

        return fragmentBinding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadingDialog = LoadingDialog(requireContext())

        Utils.navigationBar(view, "Add Pet Details", requireActivity())


        //Spinner Specie
        val adapterSpecie = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, itemsSpecie)

        adapterSpecie.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        binding!!.specieForm.adapter = adapterSpecie

        binding!!.specieForm.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

                val selectedItem = parent!!.getItemAtPosition(position).toString()

                when (selectedItem) {

                    "Dog" -> {

                        binding!!.breedLabel.visibility = View.VISIBLE
                        binding!!.breedFormParent.visibility = View.VISIBLE

                        loadingDialog.startLoading()


                        App.instance.backOffice.getDogBreeds(object : Listener<Any> {
                            override fun onResponse(response: Any?) {

                                loadingDialog.isDismiss()

                                if (isAdded) {

                                    if (response != null && response is List<*>) {

                                        val list = response as List<ApiInterface.DogBreed>

                                        val adapterBreeds = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, list)

                                        adapterBreeds.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

                                        binding!!.breedForm.adapter = adapterBreeds

                                    }

                                }

                            }

                        })

                    }

                    "Cat" -> {

                        binding!!.breedLabel.visibility = View.VISIBLE
                        binding!!.breedFormParent.visibility = View.VISIBLE

                        loadingDialog.startLoading()

                        App.instance.backOffice.getCatBreeds(object : Listener<Any> {
                            override fun onResponse(response: Any?) {

                                loadingDialog.isDismiss()

                                if (isAdded) {

                                    if (response != null && response is List<*>) {

                                        val list = response as List<ApiInterface.CatBreed>

                                        val adapterBreeds = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, list)

                                        adapterBreeds.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

                                        binding!!.breedForm.adapter = adapterBreeds

                                    }

                                }

                            }

                        })

                    }

                    else -> {

                        binding!!.breedLabel.visibility = View.GONE
                        binding!!.breedFormParent.visibility = View.GONE

                        //if (loadingDialog.isShowing()) loadingDialog.isDismiss()

                    }

                }

            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }


        }


        //Spinner Gender
        val adapterGender = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, itemsGender)

        adapterGender.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        binding!!.generoForm.adapter = adapterGender



    }

}