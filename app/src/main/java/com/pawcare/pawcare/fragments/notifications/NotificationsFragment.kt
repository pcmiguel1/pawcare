package com.pawcare.pawcare.fragments.notifications

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.navigation.Navigation
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.pawcare.pawcare.App
import com.pawcare.pawcare.R
import com.pawcare.pawcare.databinding.FragmentNotificationsBinding
import com.pawcare.pawcare.fragments.notifications.adapter.NotificationAdapter
import com.pawcare.pawcare.fragments.notifications.model.Notification
import com.pawcare.pawcare.libraries.SwipeToDeleteCallback


class NotificationsFragment : Fragment() {

    private var binding: FragmentNotificationsBinding? = null

    private var notifications: MutableList<Notification> = mutableListOf()
    private lateinit var recyclerView: RecyclerView
    private lateinit var notificationAdapter: NotificationAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val fragmentBinding = FragmentNotificationsBinding.inflate(inflater, container, false)
        binding = fragmentBinding

        App.instance.mainActivity.findViewById<LinearLayout>(R.id.bottombar).visibility = View.GONE

        return fragmentBinding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding!!.toolbar.title.text = getString(R.string.notifications)

        binding!!.toolbar.backBt.setOnClickListener {
            Navigation.findNavController(view).navigateUp()
        }

        recyclerView = binding!!.notificationsList

        notificationAdapter = NotificationAdapter(this, notifications)
        val listLayoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        recyclerView.apply {
            layoutManager = listLayoutManager
            adapter = notificationAdapter
            isNestedScrollingEnabled = false
        }

        recyclerView.addItemDecoration(DividerItemDecoration(requireActivity(), DividerItemDecoration.VERTICAL))
        val swipeHandler = object : SwipeToDeleteCallback(requireActivity()) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                TODO("Not yet implemented")
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

                notificationAdapter.removeAt(viewHolder.adapterPosition)

            }

        }

        val itemTouchHelper = ItemTouchHelper(swipeHandler)
        itemTouchHelper.attachToRecyclerView(recyclerView)

        notificationAdapter.setOnItemClickListener(object : NotificationAdapter.onItemClickListener {
            override fun onItemClick(position: Int) {

                val item = notificationAdapter.getItem(position)



            }

        })

        getNotifications()

    }

    private fun getNotifications() {

        notifications.clear()

        notifications.add(Notification("ffffffff", "dafaffa", "18/05/2023 10:00"))
        notifications.add(Notification("ffffffff", "dafaffa", "18/05/2023 10:00"))
        notifications.add(Notification("ffffffff", "dafaffa", "18/05/2023 10:00"))
        notifications.add(Notification("ffffffff", "dafaffa", "18/05/2023 10:00"))
        notifications.add(Notification("ffffffff", "dafaffa", "18/05/2023 10:00"))
        notifications.add(Notification("ffffffff", "dafaffa", "18/05/2023 10:00"))
        notifications.add(Notification("ffffffff", "dafaffa", "18/05/2023 10:00"))
        notifications.add(Notification("ffffffff", "dafaffa", "18/05/2023 10:00"))

        if (notifications.isNotEmpty()) {
            recyclerView.visibility = View.VISIBLE
            notificationAdapter.notifyDataSetChanged()
        } else {
            Toast.makeText(requireContext(), "No Notifications!", Toast.LENGTH_SHORT).show()
        }


    }

}