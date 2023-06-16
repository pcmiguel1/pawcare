package com.pawcare.pawcare.fragments.inbox.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.pawcare.pawcare.R
import com.pawcare.pawcare.fragments.inbox.model.Message

class InboxAdapter(
    private val list: List<Message>
): RecyclerView.Adapter<InboxAdapter.ItemViewHolder>() {

    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val sitter : TextView = itemView.findViewById(R.id.sitter)
        val message : TextView = itemView.findViewById(R.id.message)
        val time : TextView = itemView.findViewById(R.id.time)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.listview_item_inbox, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = list[position]
        holder.sitter.text = item.sitter
        holder.message.text = item.message
        holder.time.text = item.time
    }

    override fun getItemCount(): Int {
        return list.size
    }

}