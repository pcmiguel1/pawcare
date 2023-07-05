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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.pawcare.pawcare.App
import com.pawcare.pawcare.R
import com.pawcare.pawcare.databinding.FragmentActiveBookingsBinding
import com.pawcare.pawcare.fragments.bookings.adapter.ActiveBookingsAdapter
import com.pawcare.pawcare.libraries.LoadingDialog
import com.pawcare.pawcare.services.ApiInterface
import com.pawcare.pawcare.services.Listener


class ActiveBookingsFragment : Fragment() {

    private var binding: FragmentActiveBookingsBinding? = null

    private lateinit var recyclerBookings: RecyclerView
    private lateinit var swipeRefresh: SwipeRefreshLayout
    private var bookings: MutableList<ApiInterface.Booking> = mutableListOf()
    private lateinit var activeBookingsAdapter: ActiveBookingsAdapter

    private lateinit var loadingDialog: LoadingDialog

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

        loadingDialog = LoadingDialog(requireContext())

        swipeRefresh = binding!!.swipeRefresh

        recyclerBookings = binding!!.bookings
        recyclerBookings.setHasFixedSize(true)

        recyclerBookings.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)

        activeBookingsAdapter = ActiveBookingsAdapter(bookings)
        recyclerBookings.adapter = activeBookingsAdapter

        activeBookingsAdapter.setOnItemClickListener(object : ActiveBookingsAdapter.onItemClickListener {
            override fun onItemClick(position: Int) {

                val item = activeBookingsAdapter.getItem(position)

                val bundle = Bundle()
                bundle.putString("SITTERID", item.sitterId)

                findNavController().navigate(R.id.action_bookingsFragment2_to_chatFragment2, bundle)

            }

        })

        addBookingsToList()
        setSwipeRefresh()


    }

    private fun setSwipeRefresh() {

        swipeRefresh.setOnRefreshListener {

            addBookingsToList()
            swipeRefresh.isRefreshing = false

        }

    }

    private fun addBookingsToList() {

        bookings.clear()

        loadingDialog.startLoading()

        App.instance.backOffice.getBookingsActive(object : Listener<Any> {
            override fun onResponse(response: Any?) {

                loadingDialog.isDismiss()

                if (isAdded) {

                    if (response != null && response is List<*>) {

                        val list = response as List<ApiInterface.Booking>

                        if (list.isNotEmpty()) {

                            recyclerBookings.visibility = View.VISIBLE
                            binding!!.empty.visibility = View.GONE
                            bookings.addAll(list)
                            activeBookingsAdapter.notifyDataSetChanged()

                        }
                        else {
                            recyclerBookings.visibility = View.GONE
                            binding!!.empty.visibility = View.VISIBLE
                        }

                    }
                    else {
                        recyclerBookings.visibility = View.GONE
                        binding!!.empty.visibility = View.VISIBLE
                    }

                }

            }
        })

    }

}