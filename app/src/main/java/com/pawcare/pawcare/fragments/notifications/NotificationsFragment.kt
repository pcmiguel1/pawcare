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
import com.pawcare.pawcare.libraries.LoadingDialog
import com.pawcare.pawcare.libraries.SwipeToDeleteCallback
import com.pawcare.pawcare.services.ApiInterface
import com.pawcare.pawcare.services.Listener


class NotificationsFragment : Fragment() {

    private var binding: FragmentNotificationsBinding? = null

    private var notifications: MutableList<ApiInterface.Notification> = mutableListOf()
    private lateinit var recyclerView: RecyclerView
    private lateinit var notificationAdapter: NotificationAdapter

    private lateinit var loadingDialog: LoadingDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val fragmentBinding = FragmentNotificationsBinding.inflate(inflater, container, false)
        binding = fragmentBinding

        App.instance.mainActivity!!.findViewById<LinearLayout>(R.id.bottombar).visibility = View.GONE

        return fragmentBinding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadingDialog = LoadingDialog(requireContext())

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

                val item = notificationAdapter.getItem(viewHolder.absoluteAdapterPosition)

                App.instance.backOffice.deleteNotification(object : Listener<Any> {
                    override fun onResponse(response: Any?) {

                        if (isAdded) {

                            if (response == null) {



                            }

                        }

                    }
                }, item.id!!)

                notificationAdapter.removeAt(viewHolder.absoluteAdapterPosition)

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

        loadingDialog.startLoading()

        App.instance.backOffice.getNotifications(object : Listener<Any> {
            override fun onResponse(response: Any?) {

                loadingDialog.isDismiss()

                if (isAdded) {

                    if (response != null && response is List<*>) {

                        val list = response as List<ApiInterface.Notification>

                        if (list.isNotEmpty()) {

                            binding!!.notificationsList.visibility = View.VISIBLE
                            binding!!.empty.visibility = View.GONE
                            notifications.addAll(list)
                            notificationAdapter.notifyDataSetChanged()

                        }
                        else {
                            binding!!.notificationsList.visibility = View.GONE
                            binding!!.empty.visibility = View.VISIBLE
                        }

                    }
                    else {
                        binding!!.notificationsList.visibility = View.GONE
                        binding!!.empty.visibility = View.VISIBLE
                    }

                }

            }

        })

    }

}