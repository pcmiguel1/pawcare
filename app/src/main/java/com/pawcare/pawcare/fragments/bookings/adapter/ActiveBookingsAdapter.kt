package com.pawcare.pawcare.fragments.bookings.adapter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.pawcare.pawcare.R
import com.pawcare.pawcare.fragments.bookings.model.Bookings
import com.pawcare.pawcare.fragments.explore.model.Service
import com.pawcare.pawcare.fragments.notifications.model.Notification

class ActiveBookingsAdapter(
    private val list: List<Bookings>
    ) :
    RecyclerView.Adapter<ActiveBookingsAdapter.ItemViewHolder>() {

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
        val chat: View = itemView.findViewById(R.id.chat)

        init {
            chat.setOnClickListener {
                listener.onItemClick(absoluteAdapterPosition)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.listview_item_booking_active, parent, false)
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