package com.pawcare.pawcare.fragments.mypets.adapter

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

class PetAdapter(
    private val list: List<ApiInterface.Pet>
): RecyclerView.Adapter<PetAdapter.ItemViewHolder>() {

    private lateinit var mListener : onItemClickListener

    interface onItemClickListener {
        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(listener: onItemClickListener) {
        mListener = listener
    }

    class ItemViewHolder(itemView: View, listener: onItemClickListener) : RecyclerView.ViewHolder(itemView) {
        val name : TextView = itemView.findViewById(R.id.name)
        val type : TextView = itemView.findViewById(R.id.type)
        val image : ImageView = itemView.findViewById(R.id.image)

        init {
            itemView.setOnClickListener {
                listener.onItemClick(absoluteAdapterPosition)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.listview_item_pet, parent, false)
        return ItemViewHolder(view, mListener)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = list[position]
        holder.name.text = item.name
        holder.type.text = item.specie

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