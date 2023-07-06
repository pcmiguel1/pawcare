package com.pawcare.pawcare.fragments.inbox.adapter

import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.pawcare.pawcare.App
import com.pawcare.pawcare.R
import com.pawcare.pawcare.services.ApiInterface
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MessageAdapter(
    private var list: ArrayList<ApiInterface.Message>,
    private var sitter: Boolean
): RecyclerView.Adapter<MessageAdapter.ItemViewHolder>() {

    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val card : FrameLayout = itemView.findViewById(R.id.card)
        val message : TextView = itemView.findViewById(R.id.message)
        val time : TextView = itemView.findViewById(R.id.time)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        //val view = LayoutInflater.from(parent.context).inflate(R.layout.item_message, parent, false)

        val layoutInflater = LayoutInflater.from(parent.context)
        val layoutResId = if (viewType == VIEW_TYPE_ME) {
            R.layout.item_message
        } else {
            R.layout.item_message2
        }
        val view = layoutInflater.inflate(layoutResId, parent, false)
        return ItemViewHolder(view)
    }

    override fun getItemViewType(position: Int): Int {
        val item = list[position]
        return if (isMe(item)) {
            VIEW_TYPE_ME // A constant representing the view type for messages sent by the user
        } else {
            VIEW_TYPE_OTHER // A constant representing the view type for messages received from others
        }
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = list[position]
        holder.message.text = item.message

        val dateFormat = SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault())
        val date = item.createdat
        val formattedDate = dateFormat.format(date)

        holder.time.text = formattedDate



    }

    override fun getItemCount(): Int {
        return list.size
    }

    private fun isMe(message: ApiInterface.Message): Boolean {
        val userId = if (sitter) App.instance.preferences.getString("sitterId", "") else App.instance.preferences.getString("userId", "")
        return message.sender?.id == userId
    }

    fun appendData(message: ApiInterface.Message) {
        this.list.add(message)
        notifyItemInserted(this.list.size)
    }

    fun setData(messages: List<ApiInterface.Message>) {
        this.list.clear()
        this.list.addAll(messages)
        notifyDataSetChanged()
    }

    companion object {
        private const val VIEW_TYPE_ME = 0
        private const val VIEW_TYPE_OTHER = 1
    }

}