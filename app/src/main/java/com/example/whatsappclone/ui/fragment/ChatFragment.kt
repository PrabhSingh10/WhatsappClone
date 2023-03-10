package com.example.whatsappclone.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.whatsappclone.adapter.ChatAdapter
import com.example.whatsappclone.data.ChatsDao
import com.example.whatsappclone.databinding.FragmentChatBinding
import com.example.whatsappclone.ui.activity.MainActivity
import com.example.whatsappclone.ui.viewModel.ChatViewModel
import kotlinx.coroutines.launch

class ChatFragment : Fragment() {

    private var chatBinding : FragmentChatBinding? = null
    private lateinit var chatAdapter : ChatAdapter
    private lateinit var chatViewModel : ChatViewModel
    private lateinit var chatsDao : ChatsDao

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        chatViewModel = (activity as MainActivity).chatViewModel
        chatsDao = (activity as MainActivity).chatsDao
        chatBinding = FragmentChatBinding.inflate(
            inflater, container, false
        )
        return chatBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fetchingFriends()
        setUpRecyclerView()
    }

    private fun fetchingFriends() {
        chatViewModel.fetchingChat()
        chatViewModel.friendList.observe(viewLifecycleOwner){
            lifecycleScope.launch {
                chatsDao.upsert(it)
            }
        }
    }

    private fun setUpRecyclerView() {
        chatBinding?.recyclerViewChat?.layoutManager = LinearLayoutManager(context)
        chatAdapter = ChatAdapter()
        lifecycleScope.launch {
            chatsDao.fetchFriends().collect{
                chatAdapter.differ.submitList(it)
            }
        }
        chatBinding?.recyclerViewChat?.adapter = chatAdapter
    }

}