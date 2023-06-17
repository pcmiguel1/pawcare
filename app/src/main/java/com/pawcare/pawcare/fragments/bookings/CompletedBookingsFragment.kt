package com.pawcare.pawcare.fragments.bookings

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.pawcare.pawcare.App
import com.pawcare.pawcare.R
import com.pawcare.pawcare.databinding.FragmentCompletedBookingsBinding
import com.pawcare.pawcare.fragments.bookings.adapter.ActiveBookingsAdapter
import com.pawcare.pawcare.fragments.bookings.adapter.CompletedBookingsAdapter
import com.pawcare.pawcare.fragments.bookings.model.Bookings


class CompletedBookingsFragment : Fragment() {

    private var binding: FragmentCompletedBookingsBinding? = null

    private lateinit var recyclerBookings: RecyclerView
    private var bookings: MutableList<Bookings> = mutableListOf()
    private lateinit var completedBookingsAdapter: CompletedBookingsAdapter

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

        val empty = binding!!.empty

        recyclerBookings = binding!!.bookings
        recyclerBookings.setHasFixedSize(true)

        recyclerBookings.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)

        addBookingsToList(empty)

        completedBookingsAdapter = CompletedBookingsAdapter(bookings)
        recyclerBookings.adapter = completedBookingsAdapter

        completedBookingsAdapter.setOnItemClickListener(object : CompletedBookingsAdapter.onItemClickListener {
            override fun onItemClick(position: Int) {

                val item = completedBookingsAdapter.getItem(position)

                val bundle = Bundle()
                bundle.putString("SITTER_NAME", item.sitter)

                //Bottom sheet leave review

                val view: View = layoutInflater.inflate(R.layout.item_bottom_sheet_leave_review, null)
                val dialog = BottomSheetDialog(requireContext(), R.style.AppBottomSheetDialogTheme)
                dialog.setContentView(view)
                dialog.setCancelable(false)

                /*val cancelBtn = dialog.findViewById<View>(R.id.cancel_btn)
                val submitBtn = dialog.findViewById<View>(R.id.submit_btn)

                cancelBtn!!.setOnClickListener {
                    dialog.dismiss()
                }

                submitBtn!!.setOnClickListener {


                }*/

                dialog.show()

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