package com.pawcare.pawcare.fragments.calendar

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.pawcare.pawcare.App
import com.pawcare.pawcare.R
import com.pawcare.pawcare.databinding.FragmentCalendarBinding
import com.pawcare.pawcare.fragments.calendar.adapter.CalendarAdapter
import com.pawcare.pawcare.fragments.calendar.adapter.CalendarEventsAdapter
import com.pawcare.pawcare.libraries.LoadingDialog
import com.pawcare.pawcare.services.ApiInterface
import com.pawcare.pawcare.services.Listener
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.*

class CalendarFragment : Fragment() {

    private var binding: FragmentCalendarBinding? = null

    private lateinit var recyclerViewEvents : RecyclerView
    private lateinit var calendarEventsAdapter : CalendarEventsAdapter
    private var events : MutableList<ApiInterface.Booking> = mutableListOf()

    private lateinit var recyclerViewCalendar : RecyclerView
    private lateinit var calendarAdapter : CalendarAdapter

    private lateinit var monthYearText: TextView
    private lateinit var selectedDate: LocalDate

    private var bookingsDates : ArrayList<String> = arrayListOf()

    private lateinit var loadingDialog: LoadingDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val fragmentBinding = FragmentCalendarBinding.inflate(inflater, container, false)
        binding = fragmentBinding

        App.instance.mainActivity!!.findViewById<LinearLayout>(R.id.bottombar).visibility = View.VISIBLE

        return fragmentBinding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadingDialog = LoadingDialog(requireContext())

        recyclerViewCalendar = binding!!.calendarRecyclerView
        selectedDate = LocalDate.now()
        val now = LocalDate.now()

        monthYearText = binding!!.monthYearTV
        val previousMonth = binding!!.previous
        val nextMonth = binding!!.next

        setMonthView()

        if (selectedDate.minusMonths(1) < now) previousMonth!!.visibility = View.INVISIBLE
        else previousMonth!!.visibility = View.VISIBLE

        previousMonth!!.setOnClickListener {

            if (selectedDate.minusMonths(1) <= now) previousMonth!!.visibility = View.INVISIBLE

            selectedDate = selectedDate.minusMonths(1)
            setMonthView()
            addEventsToList()
        }

        nextMonth!!.setOnClickListener {

            if (selectedDate.plusMonths(1) > now) previousMonth!!.visibility = View.VISIBLE

            selectedDate = selectedDate.plusMonths(1)
            setMonthView()
            addEventsToList()
        }

        recyclerViewEvents = binding!!.events
        recyclerViewEvents.setHasFixedSize(true)

        recyclerViewEvents.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)

        calendarEventsAdapter = CalendarEventsAdapter(events)
        recyclerViewEvents.adapter = calendarEventsAdapter

        calendarEventsAdapter.setOnItemClickListener(object : CalendarEventsAdapter.onItemClickListener {
            override fun onItemClick(position: Int) {

                val item = calendarEventsAdapter.getItem(position)

                loadingDialog.startLoading()

                App.instance.backOffice.updateStateBooking(object: Listener<Any> {
                    override fun onResponse(response: Any?) {

                        loadingDialog.isDismiss()

                        if (isAdded) {

                            if (response == null) {

                                addEventsToList()

                            }

                        }

                    }

                }, item.id!!)


            }
        })

        addEventsToList()


    }

    private fun addEventsToList() {

        events.clear()

        loadingDialog.startLoading()

        App.instance.backOffice.getBookings(object : Listener<Any> {
            override fun onResponse(response: Any?) {

                loadingDialog.isDismiss()

                if (isAdded) {

                    if (response != null && response is List<*>) {

                        val list = response as List<ApiInterface.Booking>

                        if (list.isNotEmpty()) {

                            for (item in list) bookingsDates.add(item.startDate!!)

                            calendarAdapter.notifyDataSetChanged()

                            recyclerViewEvents.visibility = View.VISIBLE
                            binding!!.empty.visibility = View.GONE
                            events.addAll(list)
                            calendarEventsAdapter.notifyDataSetChanged()

                        }
                        else {
                            recyclerViewEvents.visibility = View.GONE
                            binding!!.empty.visibility = View.VISIBLE
                        }

                    }
                    else {
                        recyclerViewEvents.visibility = View.GONE
                        binding!!.empty.visibility = View.VISIBLE
                    }

                }

            }
        }, selectedDate.monthValue, selectedDate.year)

    }

    private fun setMonthView() {

        var current = false
        val currentDate = LocalDate.now()
        if (currentDate == selectedDate)  current = true

        monthYearText!!.text = monthYearFromDate(selectedDate)
        val daysInMonth = daysInMonthArray(selectedDate)

        //val bookingsDates = arrayListOf<String>("05-07-2023", "21-07-2023", "19-07-2023")

        calendarAdapter = CalendarAdapter(this, daysInMonth, bookingsDates, selectedDate, current)
        val listLayoutManager = GridLayoutManager(context, 7)

        recyclerViewCalendar.apply {
            layoutManager = listLayoutManager
            adapter = calendarAdapter
            isNestedScrollingEnabled = false
        }

        calendarAdapter.setOnItemClickListener(object : CalendarAdapter.onItemClickListener {
            override fun onItemClick(position: Int) {
                val item = calendarAdapter.getItem(position)

                calendarAdapter.setPosition(position)
                calendarAdapter.notifyDataSetChanged()

            }

        })

    }

    private fun monthYearFromDate(date: LocalDate): String {
        val formatter = DateTimeFormatter.ofPattern("MMMM, YYYY", Locale.ENGLISH)
        return date.format(formatter)
    }

    private fun daysInMonthArray(date: LocalDate) : ArrayList<String> {

        val daysInMonthArray = ArrayList<String>()
        val yearMonth = YearMonth.from(date)

        val daysInMonth = yearMonth.lengthOfMonth()

        val firstOfMonth = selectedDate.withDayOfMonth(1)
        val day = selectedDate.dayOfMonth
        val dayOfWeek = firstOfMonth.dayOfWeek.value

        for (i in 1..42) {

            if (i <= dayOfWeek || i > daysInMonth + dayOfWeek) {
                daysInMonthArray.add("")
            }
            else {
                daysInMonthArray.add((i-dayOfWeek).toString())
            }

        }
        return daysInMonthArray

    }

}