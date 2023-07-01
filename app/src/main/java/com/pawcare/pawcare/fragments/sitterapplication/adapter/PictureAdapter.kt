package com.pawcare.pawcare.fragments.sitterapplication.adapter

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

class PictureAdapter(
    private val list: List<ApiInterface.Picture>
): RecyclerView.Adapter<PictureAdapter.ItemViewHolder>() {

    private lateinit var mListener : onItemClickListener

    interface onItemClickListener {
        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(listener: onItemClickListener) {
        mListener = listener
    }

    class ItemViewHolder(itemView: View, listener: onItemClickListener) : RecyclerView.ViewHolder(itemView) {
        val image : ImageView = itemView.findViewById(R.id.image)
        val deleteBtn : View = itemView.findViewById(R.id.delete_btn)

        init {
            deleteBtn.setOnClickListener {
                listener.onItemClick(absoluteAdapterPosition)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.listview_item_image, parent, false)
        return ItemViewHolder(view, mListener)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = list[position]

        if (item.url != null && item.url != "") {

            Picasso.get()
                .load(item.url)
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

    fun getItem(position: Int): ApiInterface.Picture {
        return list[position]
    }

}