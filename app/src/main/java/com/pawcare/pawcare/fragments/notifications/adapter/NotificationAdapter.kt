package com.pawcare.pawcare.fragments.notifications.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.pawcare.pawcare.R
import com.pawcare.pawcare.services.ApiInterface
import java.text.SimpleDateFormat
import java.util.*

class NotificationAdapter(
    private val frag: Fragment,
    private val dataset: MutableList<ApiInterface.Notification>
): RecyclerView.Adapter<NotificationAdapter.ItemViewHolder>() {

    private lateinit var mListener : onItemClickListener

    interface onItemClickListener {
        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(listener: onItemClickListener) {
        mListener = listener
    }

    class ItemViewHolder(itemView: View, listener: onItemClickListener, dataset: List<ApiInterface.Notification>): RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.title)
        val desc: TextView = itemView.findViewById(R.id.desc)
        val time: TextView = itemView.findViewById(R.id.time)


        init {
            itemView.setOnClickListener {

                listener.onItemClick(absoluteAdapterPosition)

            }

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val adapterLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.listview_item_notification, parent, false)

        return ItemViewHolder(adapterLayout, mListener, dataset)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = dataset[position]

        holder.title.text = item.title
        holder.desc.text = item.body

        val dateFormat = SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault())
        val date = item.date
        val formattedDate = dateFormat.format(date)

        holder.time.text = formattedDate

    }

    fun removeAt(position: Int) {
        dataset.removeAt(position)
        notifyItemRemoved(position)
    }

    fun getItem(position: Int): ApiInterface.Notification {
        return dataset[position]
    }

    override fun getItemCount() = dataset.size

}