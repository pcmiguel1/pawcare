package com.pawcare.pawcare.fragments.bookings

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.pawcare.pawcare.App
import com.pawcare.pawcare.R
import com.pawcare.pawcare.databinding.FragmentCompletedBookingsBinding
import com.pawcare.pawcare.fragments.bookings.adapter.ActiveBookingsAdapter
import com.pawcare.pawcare.fragments.bookings.adapter.CompletedBookingsAdapter
import com.pawcare.pawcare.libraries.LoadingDialog
import com.pawcare.pawcare.services.ApiInterface
import com.pawcare.pawcare.services.Listener


class CompletedBookingsFragment : Fragment() {

    private var binding: FragmentCompletedBookingsBinding? = null

    private lateinit var recyclerBookings: RecyclerView
    private lateinit var swipeRefresh: SwipeRefreshLayout
    private var bookings: MutableList<ApiInterface.Booking> = mutableListOf()
    private lateinit var completedBookingsAdapter: CompletedBookingsAdapter

    private lateinit var loadingDialog: LoadingDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val fragmentBinding = FragmentCompletedBookingsBinding.inflate(inflater, container, false)
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


        completedBookingsAdapter = CompletedBookingsAdapter(bookings)
        recyclerBookings.adapter = completedBookingsAdapter

        completedBookingsAdapter.setOnItemClickListener(object : CompletedBookingsAdapter.onItemClickListener {
            override fun onItemClick(position: Int) {

                val item = completedBookingsAdapter.getItem(position)

                val bundle = Bundle()
                bundle.putString("SITTERID", item.sitterId)

                //Bottom sheet leave review

                val view: View = layoutInflater.inflate(R.layout.item_bottom_sheet_leave_review, null)
                val dialog = BottomSheetDialog(requireContext(), R.style.AppBottomSheetDialogTheme)
                dialog.setContentView(view)
                dialog.setCancelable(false)

                val cancelBtn = dialog.findViewById<View>(R.id.cancel_btn)
                val submitBtn = dialog.findViewById<View>(R.id.submit_btn)

                cancelBtn!!.setOnClickListener {
                    dialog.dismiss()
                }

                submitBtn!!.setOnClickListener {


                }

                dialog.show()

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

        App.instance.backOffice.getBookingsCompleted(object : Listener<Any> {
            override fun onResponse(response: Any?) {

                loadingDialog.isDismiss()

                if (isAdded) {

                    if (response != null && response is List<*>) {

                        val list = response as List<ApiInterface.Booking>

                        if (list.isNotEmpty()) {

                            recyclerBookings.visibility = View.VISIBLE
                            binding!!.empty.visibility = View.GONE
                            bookings.addAll(list)
                            completedBookingsAdapter.notifyDataSetChanged()

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