package com.pawcare.pawcare.fragments.sitterinfo.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.pawcare.pawcare.R
import com.pawcare.pawcare.fragments.sitterinfo.model.Review

class ReviewAdapter(
    private val list: List<Review>
): RecyclerView.Adapter<ReviewAdapter.ItemViewHolder>() {

    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val sitter : TextView = itemView.findViewById(R.id.name)
        val rating : TextView = itemView.findViewById(R.id.rating)
        val review : TextView = itemView.findViewById(R.id.review)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.listview_item_review, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = list[position]
        holder.sitter.text = item.sitter
        holder.rating.text = item.rating.toString()
        holder.review.text = item.review
    }

    override fun getItemCount(): Int {
        return list.size
    }

}