package com.example.whatsappclone.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.whatsappclone.R
import com.example.whatsappclone.databinding.ReceivedMessageBinding
import com.example.whatsappclone.databinding.SentMessageBinding
import com.example.whatsappclone.model.MessageModel
import java.text.SimpleDateFormat
import java.util.*

class MessageAdapter(
    private val image: String,
    private val friendId: String
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val sentMessage = 1
        private const val receivedMessage = 2
    }

    class SentMessageViewHolder(sentMessageBinding: SentMessageBinding) :
        RecyclerView.ViewHolder(sentMessageBinding.root) {
        val message = sentMessageBinding.tvMessage
        val time = sentMessageBinding.tvTime
    }

    class ReceivedMessageViewHolder(receivedMessageBinding: ReceivedMessageBinding) :
        RecyclerView.ViewHolder(receivedMessageBinding.root) {
        val message = receivedMessageBinding.tvMessage
        val time = receivedMessageBinding.tvTime
        val dp = receivedMessageBinding.civDp
    }

    private val differCallback = object : DiffUtil.ItemCallback<MessageModel>() {
        override fun areItemsTheSame(oldItem: MessageModel, newItem: MessageModel): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: MessageModel, newItem: MessageModel): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this@MessageAdapter, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder{
        return if (viewType == sentMessage) {
            val view = SentMessageBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
            SentMessageViewHolder(view)
        } else {
            val view = ReceivedMessageBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
            ReceivedMessageViewHolder(view)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (differ.currentList[position].senderId == friendId) {
            receivedMessage
        } else {
            sentMessage
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val friend = differ.currentList[position]
        val sdf = SimpleDateFormat("HH:mm", Locale.UK)
        val t = sdf.format(friend.timeStamp.toLong())
        if(getItemViewType(position) == sentMessage){
            (holder as SentMessageViewHolder).apply {
                message.text = friend.message
                time.text = t
            }
        }else {
            (holder as ReceivedMessageViewHolder).apply {
                message.text = friend.message
                time.text = t
                Glide.with(dp.context)
                    .load(image)
                    .apply(
                        RequestOptions().placeholder(R.drawable.ic_person)
                    )
                    .into(dp)
            }
        }
    }

    override fun getItemCount(): Int = differ.currentList.size
}