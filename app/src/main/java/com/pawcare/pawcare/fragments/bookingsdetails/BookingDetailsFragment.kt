package com.pawcare.pawcare.fragments.bookingsdetails

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.pawcare.pawcare.App
import com.pawcare.pawcare.R
import com.pawcare.pawcare.Utils
import com.pawcare.pawcare.databinding.FragmentBookingDetailsBinding
import com.pawcare.pawcare.fragments.bookingsdetails.adapter.CalendarAdapter
import com.pawcare.pawcare.fragments.bookingsdetails.adapter.ScheduleAdapter
import com.pawcare.pawcare.fragments.bookingsdetails.model.Schedule
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

class BookingDetailsFragment : Fragment() {

    private var binding: FragmentBookingDetailsBinding? = null

    private lateinit var recyclerViewCalendar: RecyclerView
    private lateinit var calendarAdapter: CalendarAdapter

    private var horarios: MutableList<Schedule> = mutableListOf()
    private lateinit var recyclerViewSchedules: RecyclerView
    private lateinit var scheduleAdapter: ScheduleAdapter

    private lateinit var monthYearText: TextView
    private lateinit var selectedDate: LocalDate

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val fragmentBinding = FragmentBookingDetailsBinding.inflate(inflater, container, false)
        binding = fragmentBinding

        App.instance.mainActivity.findViewById<LinearLayout>(R.id.bottombar).visibility = View.GONE

        return fragmentBinding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Utils.navigationBar(view, "Booking Details", requireActivity())

        //Calendar
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
        }

        nextMonth!!.setOnClickListener {

            if (selectedDate.plusMonths(1) > now) previousMonth!!.visibility = View.VISIBLE

            selectedDate = selectedDate.plusMonths(1)
            setMonthView()
        }

        //Schedules
        recyclerViewSchedules = binding!!.horarios

        scheduleAdapter = ScheduleAdapter(this, horarios)
        val listLayoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        recyclerViewSchedules.apply {
            layoutManager = listLayoutManager
            adapter = scheduleAdapter
            isNestedScrollingEnabled = false
        }

        scheduleAdapter.setOnItemClickListener(object : ScheduleAdapter.onItemClickListener {
            override fun onItemClick(position: Int) {
                val item = scheduleAdapter.getItem(position)

                scheduleAdapter.setPosition(position)
                scheduleAdapter.notifyDataSetChanged()

            }

        })

        getHorarios()

    }

    private fun getHorarios() {

        horarios.clear()

        horarios.add(Schedule("10:00 AM"))
        horarios.add(Schedule("12:00 AM"))
        horarios.add(Schedule("14:00 PM"))
        horarios.add(Schedule("16:00 PM"))
        horarios.add(Schedule("17:00 PM"))
        horarios.add(Schedule("18:00 PM"))
        horarios.add(Schedule("19:00 PM"))
        horarios.add(Schedule("20:00 PM"))
        horarios.add(Schedule("21:00 PM"))

        if (horarios.isNotEmpty()) {
            recyclerViewSchedules.visibility = View.VISIBLE
            scheduleAdapter.notifyDataSetChanged()
        } else {
            Toast.makeText(requireContext(), "Sem Horários Disponíveis!", Toast.LENGTH_SHORT).show()
        }


    }

    private fun setMonthView() {

        var current = false
        val currentDate = LocalDate.now()
        if (currentDate == selectedDate)  current = true

        monthYearText!!.text = monthYearFromDate(selectedDate)
        val daysInMonth = daysInMonthArray(selectedDate)

        calendarAdapter = CalendarAdapter(this, daysInMonth, current)
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
        val formatter = DateTimeFormatter.ofPattern("MMMM, YYYY")
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