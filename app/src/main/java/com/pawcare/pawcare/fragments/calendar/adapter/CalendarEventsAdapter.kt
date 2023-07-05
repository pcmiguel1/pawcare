package com.pawcare.pawcare.fragments.calendar.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.pawcare.pawcare.R
import com.pawcare.pawcare.services.ApiInterface
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import java.lang.Exception

class CalendarEventsAdapter(private val list: List<ApiInterface.Booking>) :
    RecyclerView.Adapter<CalendarEventsAdapter.ItemViewHolder>() {

    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val serviceType : TextView = itemView.findViewById(R.id.servicetype)
        val address : TextView = itemView.findViewById(R.id.address)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_event_calendar, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = list[position]
        holder.address.text = "Rua José Almada Negreiros, Montijo"

        holder.serviceType.text = when (item.serviceType) {

            "petwalking" -> "Pet Walking"

            "petboarding" -> "Pet Boarding"

            "housesitting" -> "House Sitting"

            "pettraning" -> "Pet Traning"

            "petgrooming" -> "Pet Grooming"

            else -> ""
        }

    }

    fun getItem(position: Int): ApiInterface.Booking {
        return list[position]
    }

    override fun getItemCount(): Int {
        return list.size
    }


}