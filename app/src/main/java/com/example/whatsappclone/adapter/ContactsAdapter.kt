package com.example.whatsappclone.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.whatsappclone.R
import com.example.whatsappclone.databinding.ContactsListBinding
import com.example.whatsappclone.model.Friends
import com.example.whatsappclone.ui.activity.MenuActivity
import com.example.whatsappclone.util.Constants.Companion.DP
import java.util.Calendar

class ContactsAdapter(
    private val addFriend : (id: String, timestamp: String) -> Unit
) : RecyclerView.Adapter<ContactsAdapter.ViewHolder>() {

    class ViewHolder(contactsListBinding : ContactsListBinding)
        : RecyclerView.ViewHolder(contactsListBinding.root){

        var civProfilePhoto = contactsListBinding.civContact
        var tvContactName = contactsListBinding.tvContactName
        var tvContactEmail = contactsListBinding.tvContactEmail
        var tvContactBio = contactsListBinding.tvContactBio
        var btnAdd = contactsListBinding.btnAdd
    }


    private val differCallback = object : DiffUtil.ItemCallback<Friends>() {
        override fun areItemsTheSame(oldItem: Friends, newItem: Friends): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Friends, newItem: Friends): Boolean {
            return oldItem == newItem
        }

    }

    val differ = AsyncListDiffer(this@ContactsAdapter, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = ContactsListBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val user = differ.currentList[position]
        holder.tvContactName.text = user.userName
        holder.tvContactEmail.text = user.userEmail
        holder.tvContactBio.text = user.userBio

        holder.btnAdd.isVisible = !user.status

        holder.itemView.apply {
            Glide.with(this)
                .load(user.userProfilePhoto)
                .apply (
                    RequestOptions().placeholder(R.drawable.ic_person)
                )
                .into(holder.civProfilePhoto)

            setOnClickListener {
                val intent = Intent(context, MenuActivity::class.java).also {
                    it.putExtra("fragment", "messaging")
                    it.putExtra("chatroomId", user.chatRoomId)
                    it.putExtra("friendName", user.userName)
                    it.putExtra("friendId", user.id)
                    it.putExtra(DP, user.userProfilePhoto)
                }
                context.startActivity(intent)
            }
        }

        holder.btnAdd.setOnClickListener {
            val c = Calendar.getInstance()
            val hour = c.get(Calendar.HOUR_OF_DAY)
            val minute = c.get(Calendar.MINUTE)
            val timeStamp = "$hour:$minute"
            addFriend(user.id, timeStamp)
            holder.btnAdd.visibility = View.INVISIBLE
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
}