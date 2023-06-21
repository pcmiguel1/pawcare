package com.pawcare.pawcare.fragments.mypets

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.pawcare.pawcare.App
import com.pawcare.pawcare.R
import com.pawcare.pawcare.Utils
import com.pawcare.pawcare.databinding.FragmentMyPetsBinding
import com.pawcare.pawcare.fragments.mypets.adapter.PetAdapter
import com.pawcare.pawcare.fragments.mypets.model.Pet


class MyPetsFragment : Fragment() {

    private var binding: FragmentMyPetsBinding? = null

    private lateinit var recyclerViewPets: RecyclerView
    private var pets: MutableList<Pet> = mutableListOf()
    private lateinit var petAdapter: PetAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val fragmentBinding = FragmentMyPetsBinding.inflate(inflater, container, false)
        binding = fragmentBinding

        App.instance.mainActivity.findViewById<LinearLayout>(R.id.bottombar).visibility = View.GONE

        return fragmentBinding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Utils.navigationBar(view, "My Pets", requireActivity())

        recyclerViewPets = binding!!.pets
        recyclerViewPets.setHasFixedSize(true)
        recyclerViewPets.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)

        addPetsToList()

        petAdapter = PetAdapter(pets)
        recyclerViewPets.adapter = petAdapter

        binding!!.addPetBtn.setOnClickListener {

            findNavController().navigate(R.id.action_myPetsFragment_to_addPetFragment)

        }

    }

    private fun addPetsToList() {

        pets.clear()

        pets.add(Pet("Alex", "Dog"))
        pets.add(Pet("Nala", "Dog"))
        pets.add(Pet("Oreo", "Cat"))

    }

}