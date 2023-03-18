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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        chatViewModel = (activity as MainActivity).chatViewModel
        chatBinding = FragmentChatBinding.inflate(
            inflater, container, false
        )
        return chatBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpRecyclerView()
    }

    private fun setUpRecyclerView() {
        chatBinding?.recyclerViewChat?.layoutManager = LinearLayoutManager(context)
        chatAdapter = ChatAdapter()
        chatViewModel.friends.observe(viewLifecycleOwner){
            chatAdapter.differ.submitList(it)
        }
        chatBinding?.recyclerViewChat?.adapter = chatAdapter
    }

}