package com.example.whatsappclone.ui.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
import kotlinx.coroutines.launch

class MessagesFragment : Fragment() {

    private var messagesBinding: FragmentMessagesBinding? = null
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var messagesViewModel: MessagesViewModel
    private var values: Bundle? = null
    private var dp: String = ""
    private lateinit var friendId: String
    private lateinit var chatRoomId: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        values = arguments
        dp = values?.getString(DP).toString()
        friendId = values?.getString("friendId").toString()
        messagesViewModel = (activity as MenuActivity).messagesViewModel
        messagesViewModel.chatroomId = values?.getString("chatroomId").toString()
        messagesViewModel.fetchMessage()

        messagesBinding = FragmentMessagesBinding.inflate(
            inflater, container, false
        )
        return messagesBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpRecyclerView()
        checkOnlineStatus()
        checkTypingStatus()

        messagesBinding?.ibSend?.setOnClickListener {
            if (messagesBinding?.etMessage?.text?.isNotEmpty() == true) {
                sendMessage(messagesBinding?.etMessage?.text!!.toString())
                messagesBinding?.etMessage?.text!!.clear()
            }
        }

        messagesBinding?.etMessage?.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(s.toString().isNotEmpty()){
                    messagesViewModel.updateTypingStatus(friendId, "Y")
                }else {
                    messagesViewModel.updateTypingStatus(friendId, "N")
                }
            }

            override fun afterTextChanged(s: Editable?) {}

        } )
    }

    private fun checkTypingStatus(){
        messagesViewModel.checkTypingStatus(friendId)
        messagesViewModel.typingStatus.observe(viewLifecycleOwner) {
            if(it){
                messagesBinding?.typing?.visibility = View.VISIBLE
            }else {
                messagesBinding?.typing?.visibility = View.GONE
            }
        }
    }

    private fun checkOnlineStatus(){
        messagesViewModel.checkOnlineStatus(friendId)
        messagesViewModel.onlineStatus.observe(viewLifecycleOwner) {
            if(it){
                (activity as MenuActivity).menuBinding.ivOnline.visibility = View.VISIBLE
            }else{
                (activity as MenuActivity).menuBinding.ivOnline.visibility = View.INVISIBLE
            }
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
        messagesViewModel.messages.observe(viewLifecycleOwner){
            messageAdapter.differ.submitList(it)
            messagesBinding?.rvChat?.smoothScrollToPosition(messageAdapter.itemCount)
        }
        val linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.stackFromEnd = true
        messagesBinding?.rvChat?.layoutManager = linearLayoutManager
        messagesBinding?.rvChat?.adapter = messageAdapter
    }

    override fun onPause() {
        super.onPause()
        messagesViewModel.updateTypingStatus(friendId, "N")
        if(messageAdapter.differ.currentList.last().senderId == friendId){
            messagesViewModel.messageStatus()
        }
    }

}