package com.pawcare.pawcare.fragments.bookings.adapter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.pawcare.pawcare.R
import com.pawcare.pawcare.services.ApiInterface
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import java.lang.Exception

class ActiveBookingsAdapter(
    private val list: List<ApiInterface.Booking>
    ) :
    RecyclerView.Adapter<ActiveBookingsAdapter.ItemViewHolder>() {

    private lateinit var mListener : onItemClickListener

    private lateinit var mListener2 : onItemClickListener2

    private lateinit var mListener3 : onItemClickListener3

    interface onItemClickListener {
        fun onItemClick(position: Int)
    }

    interface onItemClickListener2 {
        fun onItemClick(position: Int)
    }

    interface onItemClickListener3 {
        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(listener: onItemClickListener) {
        mListener = listener
    }

    fun setOnItemClickListener2(listener: onItemClickListener2) {
        mListener2 = listener
    }

    fun setOnItemClickListener3(listener: onItemClickListener3) {
        mListener3 = listener
    }

    class ItemViewHolder(itemView: View, listener: onItemClickListener, listener2: onItemClickListener2, listener3: onItemClickListener3) : RecyclerView.ViewHolder(itemView) {
        val name : TextView = itemView.findViewById(R.id.name)
        val service : TextView = itemView.findViewById(R.id.service)
        val chat: View = itemView.findViewById(R.id.chat)
        val moreDetails: ImageView = itemView.findViewById(R.id.more_details)
        val status: View = itemView.findViewById(R.id.status)
        val image : ImageView = itemView.findViewById(R.id.image)
        val pending : View = itemView.findViewById(R.id.pending)

        val cancel : View = itemView.findViewById(R.id.cancel_btn)
        val info : View = itemView.findViewById(R.id.info_btn)

        init {
            chat.setOnClickListener {
                listener.onItemClick(absoluteAdapterPosition)
            }
            moreDetails.setOnClickListener {

                if (status.visibility == View.VISIBLE) {
                    moreDetails.setImageResource(R.drawable.arrow_down)
                    status.visibility = View.GONE
                }
                else {
                    moreDetails.setImageResource(R.drawable.arrow_up)
                    status.visibility = View.VISIBLE
                }

            }

            cancel.setOnClickListener {
                listener2.onItemClick(absoluteAdapterPosition)
            }

            info.setOnClickListener {
                listener3.onItemClick(absoluteAdapterPosition)
            }

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.listview_item_booking_active, parent, false)
        return ItemViewHolder(view, mListener, mListener2, mListener3)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = list[position]
        holder.name.text = item.name

        holder.service.text = when (item.serviceType) {

            "petwalking" -> "Pet Walking"

            "petboarding" -> "Pet Boarding"

            "housesitting" -> "House Sitting"

            "pettraning" -> "Pet Traning"

            "petgrooming" -> "Pet Grooming"

            else -> ""
        }

        if (item.status == "pending") holder.pending.visibility = View.VISIBLE
        else holder.pending.visibility = View.GONE

        if (item.image != null && item.image != "") {

            Picasso.get()
                .load(item.image)
                .placeholder(R.drawable.pet_template)
                .error(R.drawable.pet_template)
                .into(holder.image, object : Callback {
                    override fun onSuccess() {

                    }

                    override fun onError(e: Exception?) {

                    }

                })

        }

    }

    fun getItem(position: Int): ApiInterface.Booking {
        return list[position]
    }

    override fun getItemCount(): Int {
        return list.size
    }


}