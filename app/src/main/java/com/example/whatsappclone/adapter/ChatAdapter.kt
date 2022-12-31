package com.example.whatsappclone.adapter

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.whatsappclone.R
import com.example.whatsappclone.databinding.ChatFriendsListBinding
import com.example.whatsappclone.model.ChatListModel
import com.example.whatsappclone.ui.activity.MenuActivity
import com.example.whatsappclone.util.Constants.Companion.DP
import com.example.whatsappclone.util.Constants.Companion.TAG

class ChatAdapter : RecyclerView.Adapter<ChatAdapter.ViewHolder>() {
    class ViewHolder(chatFriendsListBinding: ChatFriendsListBinding)
        : RecyclerView.ViewHolder(chatFriendsListBinding.root){
        val dp = chatFriendsListBinding.civDp
        val name = chatFriendsListBinding.tvName
        val message = chatFriendsListBinding.tvMessage
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = ChatFriendsListBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(view)
    }

    private val differCallback = object : DiffUtil.ItemCallback<ChatListModel>() {
        override fun areItemsTheSame(oldItem: ChatListModel, newItem: ChatListModel): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: ChatListModel, newItem: ChatListModel): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this@ChatAdapter, differCallback)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val friendInfo = differ.currentList[position]
        Log.d("Adapter Log", friendInfo.message)

        holder.itemView.apply {
            Glide.with(this)
                .load(friendInfo.friendImage)
                .apply (
                    RequestOptions().placeholder(R.drawable.ic_person)
                )
                .into(holder.dp)

            setOnClickListener {
                val intent = Intent(context, MenuActivity::class.java).also {
                    it.putExtra("fragment", "messaging")
                    it.putExtra("chatroomId", friendInfo.docID)
                    it.putExtra("friendName", friendInfo.friendName)
                    it.putExtra("friendId", friendInfo.friendId)
                    it.putExtra(DP, friendInfo.friendImage)
                }
                context.startActivity(intent)
            }
        }
        holder.name.text = friendInfo.friendName
        holder.message.text = friendInfo.message
    }

    override fun getItemCount(): Int = differ.currentList.size
}