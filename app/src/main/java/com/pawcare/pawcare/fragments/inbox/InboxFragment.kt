package com.pawcare.pawcare.fragments.inbox

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.pawcare.pawcare.App
import com.pawcare.pawcare.R
import com.pawcare.pawcare.databinding.FragmentInboxBinding
import com.pawcare.pawcare.fragments.inbox.adapter.InboxAdapter
import com.pawcare.pawcare.fragments.inbox.model.Message

class InboxFragment : Fragment() {

    private var binding: FragmentInboxBinding? = null

    private lateinit var recyclerViewMessages: RecyclerView
    private var messages: MutableList<Message> = mutableListOf()
    private lateinit var inboxAdapter: InboxAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val fragmentBinding = FragmentInboxBinding.inflate(inflater, container, false)
        binding = fragmentBinding

        App.instance.mainActivity.findViewById<LinearLayout>(R.id.bottombar).visibility = View.VISIBLE

        return fragmentBinding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerViewMessages = binding!!.inbox
        recyclerViewMessages.setHasFixedSize(true)
        recyclerViewMessages.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)

        addMessagesToList()

        inboxAdapter = InboxAdapter(messages)
        recyclerViewMessages.adapter = inboxAdapter

    }

    private fun addMessagesToList() {

        messages.add(Message("Steven Segal", "How are you?", "12:00 PM"))

    }

}