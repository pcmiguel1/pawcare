package com.pawcare.pawcare.fragments.addpet

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import androidx.navigation.fragment.findNavController
import com.google.gson.JsonObject
import com.pawcare.pawcare.App
import com.pawcare.pawcare.R
import com.pawcare.pawcare.Utils
import com.pawcare.pawcare.databinding.FragmentAddPetBinding
import com.pawcare.pawcare.libraries.LoadingDialog
import com.pawcare.pawcare.services.ApiInterface
import com.pawcare.pawcare.services.Listener
import org.json.JSONException
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class AddPetFragment : Fragment() {

    private var binding: FragmentAddPetBinding? = null

    private lateinit var loadingDialog : LoadingDialog

    private val itemsSpecie = listOf("Dog", "Cat", "Bird", "Fish", "Reptile", "Amphibian", "Small mammal")
    private val itemsGender = listOf("Male", "Female")

    private var specieSelected : String = ""
    private var breedSelected : String = ""
    private var genderSelected : String = ""

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

                specieSelected = selectedItem

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

                                        binding!!.breedForm.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                                            override fun onItemSelected(
                                                parent: AdapterView<*>?, view: View?, position: Int, id: Long
                                            ) {
                                                val selectedItem = parent!!.getItemAtPosition(position).toString()
                                                breedSelected = selectedItem
                                            }

                                            override fun onNothingSelected(p0: AdapterView<*>?) {
                                                breedSelected = ""
                                            }

                                        }

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

                                        binding!!.breedForm.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                                            override fun onItemSelected(
                                                parent: AdapterView<*>?, view: View?, position: Int, id: Long
                                            ) {
                                                val selectedItem = parent!!.getItemAtPosition(position).toString()
                                                breedSelected = selectedItem
                                            }

                                            override fun onNothingSelected(p0: AdapterView<*>?) {
                                                breedSelected = ""
                                            }

                                        }

                                    }

                                }

                            }

                        })

                    }

                    else -> {

                        binding!!.breedLabel.visibility = View.GONE
                        binding!!.breedFormParent.visibility = View.GONE

                        breedSelected = ""
                        //if (loadingDialog.isShowing()) loadingDialog.isDismiss()

                    }

                }

            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                specieSelected = ""
            }


        }


        //Spinner Gender
        val adapterGender = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, itemsGender)

        adapterGender.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        binding!!.generoForm.adapter = adapterGender

        binding!!.generoForm.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?, view: View?, position: Int, id: Long
            ) {
                val selectedItem = parent!!.getItemAtPosition(position).toString()
                genderSelected = selectedItem
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                genderSelected = ""
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

        binding!!.addBtn.setOnClickListener {

            addPet()

        }


    }

    private fun addPet() {

        Utils.hideKeyboard(requireActivity())

        val name = binding!!.nameForm.text.toString()
        val dateOfBirth = binding!!.dateForm.text.toString()

        val vaccinated = binding!!.vaccinated.isChecked
        val friendly = binding!!.friendly.isChecked
        val microchip = binding!!.microchip.isChecked

        if (Utils.isOnline(requireContext()) &&
            dateOfBirth.isNotEmpty() && name.isNotEmpty() &&
            specieSelected.isNotEmpty() && genderSelected.isNotEmpty()
        ) {

            if (specieSelected == "Dog" || specieSelected == "Cat") {
                if (breedSelected.isEmpty()) {
                    val erro = "Select breed of your pet."
                    App.instance.mainActivity.popupError(erro)
                    return
                }

            }

            val pet = JsonObject()

            try {

                var formattedDate = ""
                if (dateOfBirth.isNotEmpty()) {
                    val parser = SimpleDateFormat("dd-MM-yyyy")
                    val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                    formattedDate = formatter.format(parser.parse(dateOfBirth))
                }

                pet.addProperty("name", name)
                pet.addProperty("specie", specieSelected)
                pet.addProperty("breed", breedSelected)
                pet.addProperty("gender", genderSelected)
                pet.addProperty("dateOfBirth", formattedDate)
                pet.addProperty("vaccinated", vaccinated)
                pet.addProperty("friendly", friendly)
                pet.addProperty("microchip", microchip)

                val addbtn = binding!!.addBtn
                val rlprogressaddpet = binding!!.rlprogressaddpet

                addbtn.visibility = View.GONE
                rlprogressaddpet.visibility = View.VISIBLE

                App.instance.backOffice.addPet(object : Listener<Any> {
                    override fun onResponse(response: Any?) {

                        if (isAdded) {

                            addbtn.visibility = View.VISIBLE
                            rlprogressaddpet.visibility = View.GONE

                            if (response == null) {

                                val fragmentManager = requireActivity().supportFragmentManager
                                fragmentManager.popBackStack()

                            }
                            else {
                                App.instance.mainActivity.popupError(response.toString())
                            }

                        }

                    }
                }, pet)

            }
            catch (e: JSONException) {
                e.printStackTrace()
            }

        }
        else {

            var erro = ""
            if (!Utils.isOnline(requireContext())) erro = getString(R.string.no_internet)
            else if (name.isEmpty()) erro = "Insert name of your pet."
            else if (specieSelected.isEmpty()) erro = "Select specie of your pet."
            else if (genderSelected.isEmpty()) erro = "Select gender of your pet."
            else if (dateOfBirth.isEmpty()) erro = "Insert date of birth."

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