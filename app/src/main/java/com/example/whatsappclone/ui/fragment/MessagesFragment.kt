package com.example.whatsappclone.ui.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.whatsappclone.adapter.MessageAdapter
import com.example.whatsappclone.data.MessagesDao
import com.example.whatsappclone.databinding.FragmentMessagesBinding
import com.example.whatsappclone.ui.activity.MenuActivity
import com.example.whatsappclone.ui.viewModel.MessagesViewModel
import com.example.whatsappclone.util.Constants.Companion.DP
import com.example.whatsappclone.util.Constants.Companion.TAG
import kotlinx.coroutines.launch

class MessagesFragment : Fragment() {

    private var messagesBinding: FragmentMessagesBinding? = null
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var messagesViewModel: MessagesViewModel
    private var values: Bundle? = null
    private var dp: String = ""
    private lateinit var friendId: String
    private lateinit var chatRoomId: String
    private lateinit var messagesDao: MessagesDao

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        messagesViewModel = (activity as MenuActivity).messagesViewModel
        messagesDao = (activity as MenuActivity).messagesDao

        messagesBinding = FragmentMessagesBinding.inflate(
            inflater, container, false
        )
        return messagesBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        values = arguments
        dp = values?.getString(DP).toString()
        friendId = values?.getString("friendId").toString()
        chatRoomId = values?.getString("chatroomId").toString()
        messagesViewModel.chatroomId = chatRoomId

        setUpChats()
        setUpRecyclerView()

        messagesBinding?.ibSend?.setOnClickListener {
            if (messagesBinding?.etMessage?.text?.isNotEmpty() == true) {
                sendMessage(messagesBinding?.etMessage?.text!!.toString())
                messagesBinding?.etMessage?.text!!.clear()
            }
        }
    }

    private fun setUpChats() {
        messagesViewModel.fetchMessage()
        messagesViewModel.chats.observe(viewLifecycleOwner) {
            lifecycleScope.launch {
                messagesDao.upsert(it)
            }
            messagesBinding?.rvChat?.smoothScrollToPosition(messageAdapter.itemCount)
        }
    }

    private fun sendMessage(message: String) {
        val timeStamp = System.currentTimeMillis().toString()
        messagesViewModel.sendMessage(
            friendId, message, timeStamp
        )
    }

    private fun setUpRecyclerView() {
        messageAdapter = MessageAdapter(dp, friendId)
        lifecycleScope.launch {
            messagesDao.fetchMessage(chatRoomId).collect {
                messageAdapter.differ.submitList(it)
            }
        }
        val linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.stackFromEnd = true
        messagesBinding?.rvChat?.layoutManager = linearLayoutManager
        messagesBinding?.rvChat?.adapter = messageAdapter
    }

    override fun onStop() {
        super.onStop()
        if(messageAdapter.differ.currentList.last().senderId == friendId){
            messagesViewModel.messageStatus()
        }
    }

}