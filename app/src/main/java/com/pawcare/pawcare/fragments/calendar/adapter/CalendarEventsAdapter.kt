package com.pawcare.pawcare.fragments.calendar.adapter

import android.location.Address
import android.location.Geocoder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.marginEnd
import androidx.recyclerview.widget.RecyclerView
import com.pawcare.pawcare.R
import com.pawcare.pawcare.fragments.mypets.adapter.PetAdapter
import com.pawcare.pawcare.services.ApiInterface
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import java.io.IOException
import java.lang.Exception
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

class CalendarEventsAdapter(private val list: List<ApiInterface.Booking>) :
    RecyclerView.Adapter<CalendarEventsAdapter.ItemViewHolder>() {

    private lateinit var mListener : onItemClickListener

    interface onItemClickListener {
        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(listener: onItemClickListener) {
        mListener = listener
    }

    class ItemViewHolder(itemView: View, listener: onItemClickListener) : RecyclerView.ViewHolder(itemView) {
        val serviceType : TextView = itemView.findViewById(R.id.servicetype)
        val address : TextView = itemView.findViewById(R.id.address)
        val day : TextView = itemView.findViewById(R.id.day)
        val dayWeek : TextView = itemView.findViewById(R.id.dayweek)
        val iconService : ImageView = itemView.findViewById(R.id.icon_service)
        val total : TextView = itemView.findViewById(R.id.total)
        val status : TextView = itemView.findViewById(R.id.status)
        val updateState : AppCompatButton = itemView.findViewById(R.id.update_state)
        val cancel : View = itemView.findViewById(R.id.cancel_btn)

        init {
            updateState.setOnClickListener {
                listener.onItemClick(absoluteAdapterPosition)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_event_calendar, parent, false)
        return ItemViewHolder(view, mListener)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = list[position]

        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
        val bookingDate: LocalDate = LocalDate.parse(item.startDate, formatter)
        val simplifiedDayOfWeek: String = bookingDate.format(DateTimeFormatter.ofPattern("E"))

        holder.dayWeek.text =simplifiedDayOfWeek
        holder.day.text = bookingDate.dayOfMonth.toString()

        when (item.status) {

            "pending" -> {
                holder.status.text = item.status!!.replaceFirstChar { it.uppercaseChar() }
                holder.updateState.visibility = View.VISIBLE
                holder.cancel.visibility = View.VISIBLE

            }

            "started" -> {

                holder.updateState.visibility = View.VISIBLE
                holder.updateState.layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
                val layoutParams = holder.updateState.layoutParams as ViewGroup.MarginLayoutParams
                layoutParams.marginEnd = 0
                holder.updateState.layoutParams = layoutParams

                if (!item.petpicketup && !item.inprogress && !item.returning && !item.completed) {
                    holder.status.text = item.status!!.replaceFirstChar { it.uppercaseChar() }
                    holder.updateState.text = "Pet Picketup"
                }
                if (item.petpicketup && !item.inprogress && !item.returning && !item.completed) {
                    holder.status.text = "Pet Picketup"
                    holder.updateState.text = "In Progress"
                }
                if (item.petpicketup && item.inprogress && !item.returning && !item.completed) {
                    holder.status.text = "In Progress"
                    holder.updateState.text = "Returning"
                }
                if (item.petpicketup && item.inprogress && item.returning && !item.completed) {
                    holder.status.text = "Returning"
                    holder.updateState.text = "Completed"
                }


                holder.cancel.visibility = View.GONE

            }

            "completed" -> {
                holder.status.text = item.status!!.replaceFirstChar { it.uppercaseChar() }
                holder.updateState.visibility = View.GONE
                holder.cancel.visibility = View.GONE

            }

            "canceled" -> {
                holder.status.text = item.status!!.replaceFirstChar { it.uppercaseChar() }
                holder.updateState.visibility = View.GONE
                holder.cancel.visibility = View.GONE

            }



            else -> {

                holder.updateState.visibility = View.GONE
                holder.cancel.visibility = View.GONE

            }

        }

        holder.total.text = item.total + "€"

        val location = item.location!!.split(":")

        val geocoder = Geocoder(holder.address.context, Locale.getDefault())
        try {
            val addresses: List<Address> = geocoder.getFromLocation(location[0].toDouble(), location[1].toDouble(), 1)!!
            if (addresses.isNotEmpty()) {
                val address: Address = addresses[0]
                val addressString = address.getAddressLine(0)

                holder.address.text = addressString

            }
        }
        catch (e: IOException) {
            println("Failed to retrieve address: ${e.message}")
        }

        when (item.serviceType) {

            "petwalking" -> {
                holder.serviceType.text = "Pet Walking"
                holder.iconService.setImageResource(R.drawable.walking_dashboard)
            }

            "petboarding" -> {
                holder.serviceType.text ="Pet Boarding"
                holder.iconService.setImageResource(R.drawable.boarding2)
            }

            "housesitting" -> {
                holder.serviceType.text ="House Sitting"
                holder.iconService.setImageResource(R.drawable.adoption)
            }

            "pettraning" -> {
                holder.serviceType.text ="Pet Traning"
                holder.iconService.setImageResource(R.drawable.training2)
            }

            "petgrooming" -> {
                holder.serviceType.text ="Pet Grooming"
                holder.iconService.setImageResource(R.drawable.grooming)
            }

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