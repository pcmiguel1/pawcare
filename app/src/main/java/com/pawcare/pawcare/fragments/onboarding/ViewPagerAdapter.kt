package com.pawcare.pawcare.fragments.onboarding

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.pawcare.pawcare.R

class ViewPagerAdapter(
    private var title: List<String>,
    private var details: List<String>,
    private var images: List<Int>
): RecyclerView.Adapter<ViewPagerAdapter.PagerViewHolder>() {

    inner class PagerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val itemTitle: TextView = itemView.findViewById(R.id.title)
        val itemDetails: TextView = itemView.findViewById(R.id.description)
        val itemImage: ImageView = itemView.findViewById(R.id.image)
        val hand: ImageView = itemView.findViewById(R.id.hand)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PagerViewHolder {
        return PagerViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_boarding, parent, false))
    }

    override fun onBindViewHolder(holder: PagerViewHolder, position: Int) {

        if (position == 0) holder.hand.visibility = View.VISIBLE
        else holder.hand.visibility = View.GONE

        holder.itemTitle.text = title[position]
        holder.itemDetails.text = details[position]
        holder.itemImage.setImageResource(images[position])

    }

    override fun getItemCount(): Int {
        return title.size
    }

}