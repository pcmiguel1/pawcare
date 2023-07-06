package com.pawcare.pawcare.fragments.inbox

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.pawcare.pawcare.App
import com.pawcare.pawcare.R
import com.pawcare.pawcare.databinding.FragmentInboxBinding
import com.pawcare.pawcare.fragments.inbox.adapter.InboxAdapter
import com.pawcare.pawcare.libraries.LoadingDialog
import com.pawcare.pawcare.services.ApiInterface
import com.pawcare.pawcare.services.Listener

class InboxFragment : Fragment() {

    private var binding: FragmentInboxBinding? = null

    private lateinit var recyclerViewMessages: RecyclerView
    private lateinit var swipeRefresh: SwipeRefreshLayout
    private var contacts: MutableList<ApiInterface.Contact> = mutableListOf()
    private lateinit var inboxAdapter: InboxAdapter

    private lateinit var loadingDialog: LoadingDialog

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

        loadingDialog = LoadingDialog(requireContext())

        swipeRefresh = binding!!.swipeRefresh
        recyclerViewMessages = binding!!.inbox
        recyclerViewMessages.setHasFixedSize(true)
        recyclerViewMessages.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)

        inboxAdapter = InboxAdapter(contacts)
        recyclerViewMessages.adapter = inboxAdapter

        inboxAdapter.setOnItemClickListener(object : InboxAdapter.onItemClickListener {
            override fun onItemClick(position: Int) {

                val item = inboxAdapter.getItem(position)

                val bundle = Bundle()
                if (App.instance.preferences.getBoolean("SITTER", false)) {
                    bundle.putString("SITTERID", item.userId)
                }
                else bundle.putString("SITTERID", item.sitterId)

                findNavController().navigate(R.id.action_inboxFragment2_to_chatFragment, bundle)

            }
        })


        addMessagesToList()
        setSwipeRefresh()

    }

    private fun setSwipeRefresh() {

        swipeRefresh.setOnRefreshListener {

            addMessagesToList()
            swipeRefresh.isRefreshing = false

        }

    }

    private fun addMessagesToList() {

        contacts.clear()

        loadingDialog.startLoading()

        if (App.instance.preferences.getBoolean("SITTER", false)) {

            App.instance.backOffice.listContactsSitter(object : Listener<Any> {
                override fun onResponse(response: Any?) {

                    loadingDialog.isDismiss()

                    if (isAdded) {

                        if (response != null && response is List<*>) {

                            val list = response as List<ApiInterface.Contact>

                            if (list.isNotEmpty()) {
                                binding!!.inbox.visibility = View.VISIBLE
                                binding!!.empty.visibility = View.GONE
                                contacts.addAll(list)
                                inboxAdapter.notifyDataSetChanged()

                            }
                            else {
                                binding!!.inbox.visibility = View.GONE
                                binding!!.empty.visibility = View.VISIBLE
                            }

                        }
                        else {
                            binding!!.inbox.visibility = View.GONE
                            binding!!.empty.visibility = View.GONE
                        }

                    }

                }

            })

        }
        else {

            App.instance.backOffice.listContacts(object : Listener<Any> {
                override fun onResponse(response: Any?) {

                    loadingDialog.isDismiss()

                    if (isAdded) {

                        if (response != null && response is List<*>) {

                            val list = response as List<ApiInterface.Contact>

                            if (list.isNotEmpty()) {
                                binding!!.inbox.visibility = View.VISIBLE
                                binding!!.empty.visibility = View.GONE
                                contacts.addAll(list)
                                inboxAdapter.notifyDataSetChanged()

                            }
                            else {
                                binding!!.inbox.visibility = View.GONE
                                binding!!.empty.visibility = View.VISIBLE
                            }

                        }
                        else {
                            binding!!.inbox.visibility = View.GONE
                            binding!!.empty.visibility = View.GONE
                        }

                    }

                }

            })

        }

    }

}