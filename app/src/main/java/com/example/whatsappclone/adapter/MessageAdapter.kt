package com.example.whatsappclone.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.whatsappclone.R
import com.example.whatsappclone.databinding.ReceivedMessageBinding
import com.example.whatsappclone.databinding.SentMessageBinding
import com.example.whatsappclone.model.MessageModel

class MessageAdapter(
    private val image : String,
    private val friendId : String
) : ListAdapter<MessageModel, MessageAdapter.MessageViewHolder>(object : DiffUtil.ItemCallback<MessageModel>() {
    override fun areItemsTheSame(oldItem: MessageModel, newItem: MessageModel): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: MessageModel, newItem: MessageModel): Boolean {
        return oldItem.toString() == newItem.toString()
    }
}) {

    private val sentMessage = 1
    private val receivedMessage = 2

    class MessageViewHolder(view : View) : RecyclerView.ViewHolder(view) {
        val message: TextView = view.findViewById(R.id.tv_message)
        val time : TextView = view.findViewById(R.id.tv_time)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val view = if(viewType == sentMessage){
            SentMessageBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        }else {
            val rb = ReceivedMessageBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
            Glide.with(rb.root)
                .load(image)
                .apply(
                    RequestOptions().placeholder(R.drawable.ic_person)
                )
                .into(rb.civDp)
            rb
        }
        return MessageViewHolder(view.root)
    }

    override fun getItemViewType(position: Int): Int {
        return if(getItem(position).senderId == friendId){
            receivedMessage
        }else{
            sentMessage
        }
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val friend = getItem(position)
        Log.d("Document Type", "$itemCount $position, ${friend.message}")
        holder.message.text = friend.message
        holder.time.text = friend.timeStamp
    }

}