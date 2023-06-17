package com.pawcare.pawcare.fragments.bookings.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.pawcare.pawcare.R
import com.pawcare.pawcare.fragments.bookings.model.Bookings
import com.pawcare.pawcare.fragments.explore.model.Service

class CompletedBookingsAdapter(private val list: List<Bookings>) :
    RecyclerView.Adapter<CompletedBookingsAdapter.ItemViewHolder>() {

    private lateinit var mListener : onItemClickListener

    interface onItemClickListener {
        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(listener: onItemClickListener) {
        mListener = listener
    }

    class ItemViewHolder(itemView: View, listener: onItemClickListener) : RecyclerView.ViewHolder(itemView) {
        val name : TextView = itemView.findViewById(R.id.name)
        val service : TextView = itemView.findViewById(R.id.service)
        val leaveReview : View = itemView.findViewById(R.id.leave_review)

        init {
            leaveReview.setOnClickListener {
                listener.onItemClick(absoluteAdapterPosition)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.listview_item_booking_completed, parent, false)
        return ItemViewHolder(view, mListener)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = list[position]
        holder.name.text = item.sitter
        holder.service.text = item.service
    }

    fun getItem(position: Int): Bookings {
        return list[position]
    }

    override fun getItemCount(): Int {
        return list.size
    }


}