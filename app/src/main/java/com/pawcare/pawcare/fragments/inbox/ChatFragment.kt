package com.pawcare.pawcare.fragments.inbox

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.JsonObject
import com.pawcare.pawcare.App
import com.pawcare.pawcare.R
import com.pawcare.pawcare.Utils
import com.pawcare.pawcare.databinding.FragmentChatBinding
import com.pawcare.pawcare.fragments.inbox.adapter.MessageAdapter
import com.pawcare.pawcare.libraries.LoadingDialog
import com.pawcare.pawcare.services.ApiInterface
import com.pawcare.pawcare.services.Listener

class ChatFragment : Fragment() {

    private var binding: FragmentChatBinding? = null

    private lateinit var recyclerViewMessages: RecyclerView
    private var messages : ArrayList<ApiInterface.Message> = arrayListOf()
    private lateinit var messageAdapter: MessageAdapter

    private lateinit var loadingDialog: LoadingDialog

    private var sitterId = ""

    private var sitter : ApiInterface.Sitter? = null
    private var user : ApiInterface.User? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val fragmentBinding = FragmentChatBinding.inflate(inflater, container, false)
        binding = fragmentBinding

        App.instance.mainActivity.findViewById<LinearLayout>(R.id.bottombar).visibility = View.GONE

        val bundle = arguments
        if (bundle != null) {

            if (bundle.containsKey("SITTERID"))
                sitterId = bundle.getString("SITTERID")!!

        }

        return fragmentBinding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadingDialog = LoadingDialog(requireContext())

        loadingDialog.startLoading()

        recyclerViewMessages = binding!!.messages
        recyclerViewMessages.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)



        messageAdapter = MessageAdapter(messages, App.instance.preferences.getBoolean("SITTER", false))
        recyclerViewMessages.adapter = messageAdapter


        if (App.instance.preferences.getBoolean("SITTER", false)) {

            App.instance.backOffice.getUserById(object : Listener<Any> {
                override fun onResponse(response: Any?) {

                    loadingDialog.isDismiss()

                    if (isAdded) {

                        if (response != null && response is ApiInterface.User) {

                            user = response
                            Utils.navigationBar(view, response.fullname!!, requireActivity())
                            addMessagesToList()

                        }

                    }

                }

            }, sitterId)

        }
        else {

            App.instance.backOffice.getSitterById(object : Listener<Any> {
                override fun onResponse(response: Any?) {

                    if (isAdded) {

                        if (response != null && response is ApiInterface.Sitter) {

                            sitter = response
                            Utils.navigationBar(view, response.name!!, requireActivity())

                            App.instance.backOffice.addContact(object : Listener<Any> {
                                override fun onResponse(response: Any?) {

                                    if (isAdded) {

                                        if (response == null) {
                                            loadingDialog.isDismiss()
                                        }
                                        else {
                                            addMessagesToList()
                                        }

                                    }

                                }
                            }, sitterId)

                        }

                    }

                }

            }, sitterId)

        }

        binding!!.sendBtn.setOnClickListener {

            val messageText = binding!!.message.text.toString()

            if (App.instance.preferences.getBoolean("SITTER", false)) {

                if (messageText.isNotEmpty()) {

                    binding!!.sendBtn.visibility = View.GONE
                    binding!!.loading.visibility = View.VISIBLE

                    val messageObj = JsonObject()
                    messageObj.addProperty("message", messageText)
                    messageObj.addProperty("senderId", App.instance.preferences.getString("userId", ""))
                    messageObj.addProperty("senderName", App.instance.preferences.getString("fullname", ""))
                    messageObj.addProperty("receiverId", user!!.id)
                    messageObj.addProperty("receiverName", user!!.fullname)

                    App.instance.backOffice.sendMessageSitter(object : Listener<Any> {
                        override fun onResponse(response: Any?) {

                            binding!!.sendBtn.visibility = View.VISIBLE
                            binding!!.loading.visibility = View.GONE

                            if (isAdded) {

                                if (response != null && response is ApiInterface.Message) {

                                    binding!!.message.setText("")

                                    messageAdapter.appendData(response)

                                    // Get the total number of items in the RecyclerView
                                    val itemCount = recyclerViewMessages.adapter?.itemCount ?: 0

                                    // Scroll to the last item
                                    recyclerViewMessages.layoutManager?.scrollToPosition(itemCount - 1)

                                }

                            }

                        }

                    }, messageObj)

                }
                else {

                    Toast.makeText(requireContext(), "Write a message!", Toast.LENGTH_SHORT).show()

                }


            }
            else {

                if (messageText.isNotEmpty()) {

                    binding!!.sendBtn.visibility = View.GONE
                    binding!!.loading.visibility = View.VISIBLE

                    val messageObj = JsonObject()
                    messageObj.addProperty("message", messageText)
                    messageObj.addProperty("senderId", App.instance.preferences.getString("userId", ""))
                    messageObj.addProperty("senderName", App.instance.preferences.getString("fullname", ""))
                    messageObj.addProperty("receiverId", sitter!!.sitterId)
                    messageObj.addProperty("receiverName", sitter!!.name)

                    App.instance.backOffice.sendMessage(object : Listener<Any> {
                        override fun onResponse(response: Any?) {

                            binding!!.sendBtn.visibility = View.VISIBLE
                            binding!!.loading.visibility = View.GONE

                            if (isAdded) {

                                if (response != null && response is ApiInterface.Message) {

                                    binding!!.message.setText("")

                                    messageAdapter.appendData(response)

                                    // Get the total number of items in the RecyclerView
                                    val itemCount = recyclerViewMessages.adapter?.itemCount ?: 0

                                    // Scroll to the last item
                                    recyclerViewMessages.layoutManager?.scrollToPosition(itemCount - 1)

                                }

                            }

                        }

                    }, messageObj)

                }
                else {

                    Toast.makeText(requireContext(), "Write a message!", Toast.LENGTH_SHORT).show()

                }

            }

        }

    }

    private fun addMessagesToList() {

        if (App.instance.preferences.getBoolean("SITTER", false)) {

            messages.clear()

            App.instance.backOffice.chatMessagesSitter(object : Listener<Any> {
                override fun onResponse(response: Any?) {

                    loadingDialog.isDismiss()

                    if (isAdded) {

                        if (response != null && response is List<*>) {

                            val list = response as List<ApiInterface.Message>

                            if (list.isNotEmpty()) {
                                binding!!.messages.visibility = View.VISIBLE
                                //binding!!.empty.visibility = View.GONE
                                messages.addAll(list)
                                messageAdapter.notifyDataSetChanged()

                                // Get the total number of items in the RecyclerView
                                val itemCount = recyclerViewMessages.adapter?.itemCount ?: 0

                                // Scroll to the last item
                                recyclerViewMessages.layoutManager?.scrollToPosition(itemCount - 1)

                                //messageAdapter.setData(messages)

                            }
                            else {
                                binding!!.messages.visibility = View.GONE
                                //binding!!.empty.visibility = View.VISIBLE
                            }

                        }
                        else {
                            binding!!.messages.visibility = View.GONE
                            //binding!!.empty.visibility = View.GONE
                        }

                    }

                }

            }, user!!.id!!)

        }
        else {

            messages.clear()

            App.instance.backOffice.chatMessages(object : Listener<Any> {
                override fun onResponse(response: Any?) {

                    loadingDialog.isDismiss()

                    if (isAdded) {

                        if (response != null && response is List<*>) {

                            val list = response as List<ApiInterface.Message>

                            if (list.isNotEmpty()) {
                                binding!!.messages.visibility = View.VISIBLE
                                //binding!!.empty.visibility = View.GONE
                                messages.addAll(list)
                                messageAdapter.notifyDataSetChanged()

                                // Get the total number of items in the RecyclerView
                                val itemCount = recyclerViewMessages.adapter?.itemCount ?: 0

                                // Scroll to the last item
                                recyclerViewMessages.layoutManager?.scrollToPosition(itemCount - 1)

                                //messageAdapter.setData(messages)

                            }
                            else {
                                binding!!.messages.visibility = View.GONE
                                //binding!!.empty.visibility = View.VISIBLE
                            }

                        }
                        else {
                            binding!!.messages.visibility = View.GONE
                            //binding!!.empty.visibility = View.GONE
                        }

                    }

                }

            }, sitterId)

        }

    }

}