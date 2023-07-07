package com.pawcare.pawcare.fragments.mypets

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.pawcare.pawcare.App
import com.pawcare.pawcare.R
import com.pawcare.pawcare.Utils
import com.pawcare.pawcare.databinding.FragmentMyPetsBinding
import com.pawcare.pawcare.fragments.mypets.adapter.PetAdapter
import com.pawcare.pawcare.libraries.LoadingDialog
import com.pawcare.pawcare.services.ApiInterface
import com.pawcare.pawcare.services.Listener


class MyPetsFragment : Fragment() {

    private var binding: FragmentMyPetsBinding? = null

    private lateinit var recyclerViewPets: RecyclerView
    private lateinit var swipeRefresh: SwipeRefreshLayout
    private var pets: MutableList<ApiInterface.Pet> = mutableListOf()
    private lateinit var petAdapter: PetAdapter

    private lateinit var loadingDialog: LoadingDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val fragmentBinding = FragmentMyPetsBinding.inflate(inflater, container, false)
        binding = fragmentBinding

        App.instance.mainActivity!!.findViewById<LinearLayout>(R.id.bottombar).visibility = View.GONE

        return fragmentBinding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadingDialog = LoadingDialog(requireContext())
        Utils.navigationBar(view, "My Pets", requireActivity())

        swipeRefresh = binding!!.swipeRefresh
        recyclerViewPets = binding!!.pets
        recyclerViewPets.setHasFixedSize(true)
        recyclerViewPets.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)

        petAdapter = PetAdapter(pets)
        recyclerViewPets.adapter = petAdapter

        petAdapter.setOnItemClickListener(object : PetAdapter.onItemClickListener {
            override fun onItemClick(position: Int) {

                val item = petAdapter.getItem(position)

                val bundle = Bundle()
                bundle.putParcelable("PET", item)

                findNavController().navigate(R.id.action_myPetsFragment_to_addPetFragment, bundle)

            }

        })

        addPetsToList()
        setSwipeRefresh()

        binding!!.addPetBtn.setOnClickListener {

            findNavController().navigate(R.id.action_myPetsFragment_to_addPetFragment)

        }


    }

    private fun setSwipeRefresh() {

        swipeRefresh.setOnRefreshListener {

            addPetsToList()
            swipeRefresh.isRefreshing = false

        }

    }

    private fun addPetsToList() {

        pets.clear()

        loadingDialog.startLoading()

        App.instance.backOffice.getPets(object : Listener<Any> {
            override fun onResponse(response: Any?) {

                loadingDialog.isDismiss()

                if (isAdded) {

                    if (response != null && response is List<*>) {

                        val list = response as List<ApiInterface.Pet>

                        if (list.isNotEmpty()) {
                            binding!!.pets.visibility = View.VISIBLE
                            binding!!.empty.visibility = View.GONE
                            pets.addAll(list)
                            petAdapter.notifyDataSetChanged()

                        }
                        else {
                            binding!!.pets.visibility = View.GONE
                            binding!!.empty.visibility = View.VISIBLE
                        }

                    }
                    else {
                        binding!!.pets.visibility = View.GONE
                        binding!!.empty.visibility = View.GONE
                    }

                }

            }

        })

    }

}