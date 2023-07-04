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

class PetAdapter(
    private val list: List<ApiInterface.Pet>,
    private val callback: PetAdapterCallback
): RecyclerView.Adapter<PetAdapter.ItemViewHolder>() {

    interface PetAdapterCallback {
        fun onPetActiveChanged(position: Int, isActive: Boolean)
    }

    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name : TextView = itemView.findViewById(R.id.name)
        val image : ImageView = itemView.findViewById(R.id.image)
        val active : Switch = itemView.findViewById(R.id.active)

        lateinit var callback: PetAdapterCallback  // Reference to the callback interface

        init {
            active.setOnCheckedChangeListener { _, isChecked ->
                callback.onPetActiveChanged(adapterPosition, isChecked)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.listview_item_pet_booking, parent, false)
        val viewHolder = ItemViewHolder(view)
        viewHolder.callback = callback  // Assign the callback reference
        return viewHolder
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = list[position]
        holder.name.text = item.name

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