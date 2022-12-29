package com.example.whatsappclone.ui.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.whatsappclone.adapter.MessageAdapter
import com.example.whatsappclone.databinding.FragmentMessagesBinding
import com.example.whatsappclone.repository.FirebaseAuthRepository
import com.example.whatsappclone.repository.FirebaseStoreRepository
import com.example.whatsappclone.ui.viewModel.MessagesViewModel
import com.example.whatsappclone.ui.viewModel.MessagesViewModelProviderFactory
import com.example.whatsappclone.util.Constants.Companion.DP
import java.util.Calendar

class MessagesFragment : Fragment() {

    private var messagesBinding : FragmentMessagesBinding? = null
    private lateinit var messageAdapter : MessageAdapter
    private val messagesViewModel: MessagesViewModel by viewModels{
        val firebaseAuthRepository = FirebaseAuthRepository()
        val firebaseStoreRepository = FirebaseStoreRepository()
        MessagesViewModelProviderFactory(
            firebaseAuthRepository,
            firebaseStoreRepository
        )
    }
    private var values : Bundle? = null
    private var dp : String = ""
    private lateinit var friendId : String
    private lateinit var chatRoomId : String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

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
            if(messagesBinding?.etMessage?.text?.isNotEmpty() == true){
                sendMessage(messagesBinding?.etMessage?.text!!.toString())
                messagesBinding?.etMessage?.text!!.clear()
            }
        }
    }

    private fun setUpChats() {
        messagesViewModel.fetchMessage()
        messagesViewModel.chats.observe(viewLifecycleOwner){
            Log.d("Document Type", it.last().toString())
            messageAdapter.submitList(it)
            Log.d("Document Type", messageAdapter.itemCount.toString())
        }
    }

    private fun sendMessage(message: String) {
        val c = Calendar.getInstance()
        val hour = c.get(Calendar.HOUR_OF_DAY)
        val minute = c.get(Calendar.MINUTE)
        val timeStamp = "$hour:$minute"
        messagesViewModel.sendMessage(
            friendId, message, timeStamp
        )
    }

    private fun setUpRecyclerView() {
        messageAdapter = MessageAdapter(dp, friendId)
        val linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.stackFromEnd = true
        messagesBinding?.rvChat?.layoutManager = linearLayoutManager
        messagesBinding?.rvChat?.adapter = messageAdapter
    }

}