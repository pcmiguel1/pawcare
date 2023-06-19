package com.pawcare.pawcare.fragments.bookingsdetails.adapter

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.pawcare.pawcare.R
import com.pawcare.pawcare.fragments.bookingsdetails.model.Schedule

class ScheduleAdapter(
    private val frag: Fragment,
    private val dataset: List<Schedule>
): RecyclerView.Adapter<ScheduleAdapter.ItemViewHolder>() {

    private lateinit var mListener: onItemClickListener
    private var itemSelectionPosition = -1

    interface onItemClickListener {
        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(listener: onItemClickListener) {
        mListener = listener
    }

    class ItemViewHolder(itemView: View, listener: onItemClickListener, dataset: List<Schedule>): RecyclerView.ViewHolder(itemView) {
        val cardView: MaterialCardView = itemView.findViewById(R.id.cardhorario)
        val horario: TextView = itemView.findViewById(R.id.horario)


        init {
            itemView.setOnClickListener {

                //cardView.strokeColor = ContextCompat.getColor(it.context, R.color.colorPrimary)
                //horario.setTextColor(it.resources.getColor(R.color.colorPrimary))

                listener.onItemClick(absoluteAdapterPosition)

            }

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val adapterLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.listview_item_schedule, parent, false)

        return ItemViewHolder(adapterLayout, mListener, dataset)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = dataset[position]
        holder.horario.text = item.hour

        if (itemSelectionPosition == position) {
            holder.cardView.strokeColor = ContextCompat.getColor(frag.requireContext(), R.color.primaryColor)
            holder.cardView.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(frag.requireContext(), R.color.primaryColor))
            holder.horario.setTextColor(frag.requireContext().resources.getColor(R.color.white))
        }
        else {
            holder.cardView.strokeColor = ContextCompat.getColor(frag.requireContext(), R.color.primaryColor)
            holder.cardView.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(frag.requireContext(), android.R.color.white))
            holder.horario.setTextColor(frag.requireContext().resources.getColor(R.color.text_color))
        }

    }

    fun getItem(position: Int): Schedule {
        return dataset[position]
    }

    fun getPosition() : Int {
        return itemSelectionPosition
    }

    fun setPosition(position: Int) {
        itemSelectionPosition = position
    }

    override fun getItemCount() = dataset.size

}