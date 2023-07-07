package com.pawcare.pawcare.fragments.bookings

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.JsonObject
import com.pawcare.pawcare.App
import com.pawcare.pawcare.R
import com.pawcare.pawcare.databinding.FragmentCompletedBookingsBinding
import com.pawcare.pawcare.fragments.bookings.adapter.ActiveBookingsAdapter
import com.pawcare.pawcare.fragments.bookings.adapter.CompletedBookingsAdapter
import com.pawcare.pawcare.libraries.LoadingDialog
import com.pawcare.pawcare.services.ApiInterface
import com.pawcare.pawcare.services.Listener
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import java.lang.Exception


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

        App.instance.mainActivity!!.findViewById<LinearLayout>(R.id.bottombar).visibility = View.VISIBLE

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

                //val bundle = Bundle()
                //bundle.putString("SITTERID", item.sitterId)

                //Bottom sheet leave review

                val view: View = layoutInflater.inflate(R.layout.item_bottom_sheet_leave_review, null)
                val dialog = BottomSheetDialog(requireContext(), R.style.AppBottomSheetDialogTheme)
                dialog.setContentView(view)
                dialog.setCancelable(false)

                val cancelBtn = dialog.findViewById<View>(R.id.cancel_btn)
                val submitBtn = dialog.findViewById<View>(R.id.submit_btn)
                val rlprogress = dialog.findViewById<View>(R.id.rlprogress)
                val message = dialog.findViewById<EditText>(R.id.message)
                val rating = dialog.findViewById<RatingBar>(R.id.rating)
                val image = dialog.findViewById<ImageView>(R.id.image)
                val name = dialog.findViewById<TextView>(R.id.name)
                val service = dialog.findViewById<TextView>(R.id.service)

                name!!.text = item.name

                service!!.text = when (item.serviceType) {

                    "petwalking" -> "Pet Walking"

                    "petboarding" -> "Pet Boarding"

                    "housesitting" -> "House Sitting"

                    "pettraning" -> "Pet Traning"

                    "petgrooming" -> "Pet Grooming"

                    else -> ""
                }

                if (item.image != null && item.image != "") {

                    Picasso.get()
                        .load(item.image)
                        .placeholder(R.drawable.profile_template)
                        .error(R.drawable.profile_template)
                        .into(image, object : Callback {
                            override fun onSuccess() {

                            }

                            override fun onError(e: Exception?) {

                            }

                        })

                }

                cancelBtn!!.setOnClickListener {
                    dialog.dismiss()
                }

                submitBtn!!.setOnClickListener {

                    val messageText = message!!.text.toString()

                    if (messageText.isNotEmpty() && rating!!.rating >= 1) {

                        submitBtn.visibility = View.GONE
                        rlprogress!!.visibility = View.VISIBLE

                        val reviewObj = JsonObject()
                        reviewObj.addProperty("message", messageText)
                        reviewObj.addProperty("sitterId", item.sitterId!!)
                        reviewObj.addProperty("bookingId", item.id)
                        reviewObj.addProperty("rate", rating.rating.toString())

                        App.instance.backOffice.addReview(object : Listener<Any> {
                            override fun onResponse(response: Any?) {

                                submitBtn.visibility = View.VISIBLE
                                rlprogress!!.visibility = View.GONE

                                if (isAdded) {

                                    if (response != null && response is ApiInterface.Review) {

                                        Toast.makeText(activity, "Review sent successfully!", Toast.LENGTH_SHORT).show()
                                        item.review = response
                                        completedBookingsAdapter.notifyDataSetChanged()
                                        dialog.dismiss()

                                    }

                                }

                            }

                        }, item.sitterId!!, reviewObj)

                    }
                    else {

                        var error = ""
                        if (messageText.isEmpty()) error = "Write a message!!"
                        if (rating!!.rating <= 0) error = "Give a rating!"

                        Toast.makeText(activity, error, Toast.LENGTH_SHORT).show()

                    }

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