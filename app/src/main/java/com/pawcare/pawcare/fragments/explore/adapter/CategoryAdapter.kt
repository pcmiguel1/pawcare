package com.pawcare.pawcare.fragments.explore.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.pawcare.pawcare.R
import com.pawcare.pawcare.fragments.explore.model.Category

class CategoryAdapter(
    private val list: List<Category>
): RecyclerView.Adapter<CategoryAdapter.ItemViewHolder>() {

    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name : TextView = itemView.findViewById(R.id.name)
        val icon : ImageView = itemView.findViewById(R.id.icon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_category, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = list[position]
        holder.name.text = item.name

        holder.icon.setImageResource(item.icon)

    }

    override fun getItemCount(): Int {
        return list.size
    }

}