package com.pawcare.pawcare.fragments.dashboard

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import com.pawcare.pawcare.App
import com.pawcare.pawcare.R
import com.pawcare.pawcare.databinding.FragmentDashboardBinding
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import java.lang.Exception


class DashboardFragment : Fragment() {

    private var binding: FragmentDashboardBinding? = null

    private lateinit var pieChart : PieChart

    private var pieEntryList = arrayListOf<PieEntry>()
    private var colors = arrayListOf<Int>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val fragmentBinding = FragmentDashboardBinding.inflate(inflater, container, false)
        binding = fragmentBinding

        App.instance.mainActivity!!.findViewById<LinearLayout>(R.id.bottombar).visibility = View.VISIBLE

        return fragmentBinding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding!!.username.text = App.instance.preferences.getString("fullname", "")

        val userPhoto = binding!!.userPhoto
        val photoUrl = App.instance.preferences.getString("image", "")

        if (photoUrl != "") {

            Picasso.get()
                .load(photoUrl)
                .placeholder(R.drawable.profile_template)
                .error(R.drawable.profile_template)
                .into(userPhoto, object : Callback {
                    override fun onSuccess() {

                    }

                    override fun onError(e: Exception?) {

                    }

                })

        }

        pieChart = binding!!.chart

        setValues()

        setUpChart()


    }

    private fun setValues() {

        pieEntryList.add(PieEntry(200f, "Active"))
        pieEntryList.add(PieEntry(300f, "Finished"))
        pieEntryList.add(PieEntry(400f, "Canceled"))

        colors.add(Color.parseColor("#D6DA2C"))
        colors.add(Color.parseColor("#4558CE"))
        colors.add(Color.parseColor("#ED6D4E"))

    }

    private fun setUpChart() {
        val pieDataSet = PieDataSet(pieEntryList, "Pie Chart")
        val pieData = PieData(pieDataSet)
        pieDataSet.colors = colors
        pieData.setValueTextSize(12f)
        pieData.setDrawValues(false)

        pieChart.description.isEnabled = false
        pieChart.isRotationEnabled = false
        pieChart.isHighlightPerTapEnabled = false
        pieChart.legend.isEnabled = false
        pieChart.setDrawRoundedSlices(false)
        pieChart.setDrawSliceText(false)

        pieChart.data = pieData
        pieChart.invalidate()
    }

}