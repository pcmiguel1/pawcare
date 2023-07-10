package com.pawcare.pawcare.fragments.bookingsdetails.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.pawcare.pawcare.R
import com.pawcare.pawcare.services.ApiInterface
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

class PetSitterAdapter(
    private val list: List<ApiInterface.Pet>
): RecyclerView.Adapter<PetSitterAdapter.ItemViewHolder>() {

    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name : TextView = itemView.findViewById(R.id.name)
        val image : ImageView = itemView.findViewById(R.id.image)
        val gender: ImageView = itemView.findViewById(R.id.gender)
        val date : TextView = itemView.findViewById(R.id.dateofbirth)
        val vaccinated : View = itemView.findViewById(R.id.vaccinated)
        val friendly : View = itemView.findViewById(R.id.friendly)
        val microchip : View = itemView.findViewById(R.id.microchip)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.listview_item_pet_booking_details, parent, false)
        val viewHolder = ItemViewHolder(view)
        return viewHolder
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = list[position]
        holder.name.text = item.name

        if (item.gender == "Male") holder.gender.setImageResource(R.drawable.ic_baseline_male_24)
        else holder.gender.setImageResource(R.drawable.ic_baseline_female_24)

        if (item.vaccinated) holder.vaccinated.visibility = View.VISIBLE
        else holder.vaccinated.visibility = View.GONE

        if (item.friendly) holder.friendly.visibility = View.VISIBLE
        else holder.friendly.visibility = View.GONE

        if (item.microchip) holder.microchip.visibility = View.VISIBLE
        else holder.microchip.visibility = View.GONE

        val inputDateString = item!!.dateOfBirth!!
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd-M-yyyy", Locale.getDefault())

        val inputDate = inputFormat.parse(inputDateString)
        val outputDateString = outputFormat.format(inputDate)

        holder.date.text = outputDateString

        if (item.photo != null && item.photo != "") {

            Picasso.get()
                .load(item.photo)
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

    override fun getItemCount(): Int {
        return list.size
    }

    fun getItem(position: Int): ApiInterface.Pet {
        return list[position]
    }

}