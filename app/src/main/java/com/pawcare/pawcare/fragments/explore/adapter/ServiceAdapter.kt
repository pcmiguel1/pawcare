package com.pawcare.pawcare.fragments.explore.adapter

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.pawcare.pawcare.App
import com.pawcare.pawcare.R
import com.pawcare.pawcare.fragments.bookings.model.Bookings
import com.pawcare.pawcare.fragments.explore.model.Service
import com.pawcare.pawcare.services.ApiInterface
import com.pawcare.pawcare.services.Listener
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import java.lang.Exception

class ServiceAdapter(private val list: List<ApiInterface.Sitter>) :
    RecyclerView.Adapter<ServiceAdapter.ItemViewHolder>() {

    private lateinit var mListener : onItemClickListener

    private lateinit var mListener2: onItemClickListener2

    interface onItemClickListener {
        fun onItemClick(position: Int)
    }

    interface onItemClickListener2 {
        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(listener: onItemClickListener) {
        mListener = listener
    }

    fun setOnItemClickListener2(listener: onItemClickListener2) {
        mListener2 = listener
    }

    class ItemViewHolder(itemView: View, listener: onItemClickListener, listener2: onItemClickListener2) : RecyclerView.ViewHolder(itemView) {
        val name : TextView = itemView.findViewById(R.id.name)
        val image : ImageView = itemView.findViewById(R.id.image)
        val like : ImageView = itemView.findViewById(R.id.like)

        init {

            itemView.setOnClickListener {

                listener.onItemClick(absoluteAdapterPosition)

            }

            like.setOnClickListener {

                listener2.onItemClick(absoluteAdapterPosition)

            }

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_service, parent, false)
        return ItemViewHolder(view, mListener, mListener2)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = list[position]
        holder.name.text = item.name

        if (item.image != null && item.image != "") {

            Picasso.get()
                .load(item.image)
                .placeholder(R.drawable.profile_template)
                .error(R.drawable.profile_template)
                .into(holder.image, object : Callback {
                    override fun onSuccess() {

                    }

                    override fun onError(e: Exception?) {

                    }

                })

        }

        App.instance.backOffice.getFavourite(object : Listener<Any> {
            override fun onResponse(response: Any?) {

                if (response != null && response is ApiInterface.Favourite) {

                    holder.like.setImageResource(R.drawable.like_red)

                }
                else {
                    holder.like.setImageResource(R.drawable.heart_icon)
                }

            }

        }, item.sitterId!!)


    }

    fun getItem(position: Int): ApiInterface.Sitter {
        return list[position]
    }

    override fun getItemCount(): Int {
        return list.size
    }


}