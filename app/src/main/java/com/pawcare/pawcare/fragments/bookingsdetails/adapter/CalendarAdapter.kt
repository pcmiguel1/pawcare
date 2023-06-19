package com.pawcare.pawcare.fragments.bookingsdetails.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.pawcare.pawcare.R
import java.time.LocalDate

class CalendarAdapter(
    private val frag: Fragment,
    private val dataset: ArrayList<String>,
    private val current: Boolean

): RecyclerView.Adapter<CalendarAdapter.ItemViewHolder>() {

    private lateinit var mListener : onItemClickListener
    private var itemSelectionPosition = -1

    interface onItemClickListener {
        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(listener: onItemClickListener) {
        mListener = listener
    }

    class ItemViewHolder(itemView: View, listener: onItemClickListener): RecyclerView.ViewHolder(itemView) {

        val dayOfMonth: TextView = itemView.findViewById(R.id.cellDayText)
        val dayOfMonthStatus: View = itemView.findViewById(R.id.cellDayStatus)
        val cardView: MaterialCardView = itemView.findViewById(R.id.carddaycalendar)

        init {
            itemView.setOnClickListener {

                listener.onItemClick(absoluteAdapterPosition)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {

        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.calendar_cell, parent, false)
        val layoutParams = view.layoutParams
        layoutParams.height = (parent.height * 0.166666666).toInt()

        return ItemViewHolder(view, mListener)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = dataset[position]
        holder.dayOfMonth.text = item

        if (item.isNotEmpty()) {

            val date = LocalDate.now()
            val currentDate = date.dayOfMonth

            if (itemSelectionPosition == position) {

                holder.cardView.strokeColor = ContextCompat.getColor(frag.requireContext(), R.color.primaryColor)
                holder.dayOfMonth.setTextColor(frag.requireContext().resources.getColor(R.color.primaryColor))

            }
            else {

                holder.cardView.strokeColor = ContextCompat.getColor(frag.requireContext(), android.R.color.transparent)
                holder.dayOfMonth.setTextColor(frag.requireContext().resources.getColor(R.color.text_color))

            }


            if (item.toInt() < currentDate && current) {
                holder.dayOfMonth.setTextColor(frag.resources.getColor(R.color.placeholder))
                holder.dayOfMonthStatus.visibility = View.GONE
            }
            else {
                holder.dayOfMonthStatus.visibility = View.VISIBLE
            }

        }
    }

    fun getItem(position: Int): String {
        return dataset[position]
    }

    fun setPosition(position: Int) {
        itemSelectionPosition = position
    }

    override fun getItemCount() = dataset.size

}