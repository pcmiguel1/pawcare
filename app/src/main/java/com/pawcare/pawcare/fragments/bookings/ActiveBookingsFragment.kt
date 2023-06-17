package com.pawcare.pawcare.fragments.bookings

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.pawcare.pawcare.App
import com.pawcare.pawcare.R
import com.pawcare.pawcare.databinding.FragmentActiveBookingsBinding
import com.pawcare.pawcare.fragments.bookings.adapter.ActiveBookingsAdapter
import com.pawcare.pawcare.fragments.bookings.model.Bookings
import com.pawcare.pawcare.fragments.inbox.adapter.InboxAdapter


class ActiveBookingsFragment : Fragment() {

    private var binding: FragmentActiveBookingsBinding? = null

    private lateinit var recyclerBookings: RecyclerView
    private var bookings: MutableList<Bookings> = mutableListOf()
    private lateinit var activeBookingsAdapter: ActiveBookingsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val fragmentBinding = FragmentActiveBookingsBinding.inflate(inflater, container, false)
        binding = fragmentBinding

        App.instance.mainActivity.findViewById<LinearLayout>(R.id.bottombar).visibility = View.VISIBLE

        return fragmentBinding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val empty = binding!!.empty

        recyclerBookings = binding!!.bookings
        recyclerBookings.setHasFixedSize(true)

        recyclerBookings.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)

        addBookingsToList(empty)

        activeBookingsAdapter = ActiveBookingsAdapter(bookings)
        recyclerBookings.adapter = activeBookingsAdapter

        activeBookingsAdapter.setOnItemClickListener(object : ActiveBookingsAdapter.onItemClickListener {
            override fun onItemClick(position: Int) {

                val item = activeBookingsAdapter.getItem(position)

                val bundle = Bundle()
                bundle.putString("SITTER_NAME", item.sitter)

                findNavController().navigate(R.id.action_bookingsFragment2_to_chatFragment2, bundle)

            }

        })


    }

    private fun addBookingsToList(empty: View) {

        bookings.add(Bookings("Steven Segal", "Pet Walking"))
        bookings.add(Bookings("Steven Segal", "Training"))

        if (bookings.isNotEmpty()) {
            empty.visibility = View.GONE
            recyclerBookings.visibility = View.VISIBLE
           // activeBookingsAdapter.notifyDataSetChanged()
        } else {
            recyclerBookings.visibility = View.GONE
            empty.visibility = View.VISIBLE
        }

    }

}