package com.pawcare.pawcare.fragments.sitterinfo.adapter

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
import org.w3c.dom.Text
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

class ReviewAdapter(
    private val list: List<ApiInterface.Review>
): RecyclerView.Adapter<ReviewAdapter.ItemViewHolder>() {

    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name : TextView = itemView.findViewById(R.id.name)
        val rating : TextView = itemView.findViewById(R.id.rating)
        val review : TextView = itemView.findViewById(R.id.review)
        val date : TextView = itemView.findViewById(R.id.date)
        val icon : ImageView = itemView.findViewById(R.id.icon)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.listview_item_review, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = list[position]
        holder.name.text = item.user!!.fullname
        holder.rating.text = item.rate!!.toDouble().toInt().toString()
        holder.review.text = item.message



        val dateFormat = SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault())
        val date = item.createdat
        val formattedDate = dateFormat.format(date)

        holder.date.text = formattedDate

        if (item.user!!.image != null && item.user!!.image != "") {

            Picasso.get()
                .load(item.user!!.image)
                .placeholder(R.drawable.profile_template)
                .error(R.drawable.profile_template)
                .into(holder.icon, object : Callback {
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

}