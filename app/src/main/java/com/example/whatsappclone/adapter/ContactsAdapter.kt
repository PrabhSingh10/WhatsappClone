package com.example.whatsappclone.adapter

import android.util.Log
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

class ContactsAdapter(
    private val addFriend: (id: String, timestamp: String) -> Unit
) : RecyclerView.Adapter<ContactsAdapter.ViewHolder>() {

    class ViewHolder(contactsListBinding: ContactsListBinding) :
        RecyclerView.ViewHolder(contactsListBinding.root) {

        val civProfilePhoto = contactsListBinding.civContact
        val tvContactName = contactsListBinding.tvContactName
        val tvContactEmail = contactsListBinding.tvContactEmail
        val tvContactBio = contactsListBinding.tvContactBio
        val btnAdd = contactsListBinding.btnAdd
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
                .apply(
                    RequestOptions().placeholder(R.drawable.ic_person)
                )
                .into(holder.civProfilePhoto)

        }

        holder.btnAdd.setOnClickListener {
            val timeStamp = System.currentTimeMillis().toString()
            addFriend(user.id, timeStamp)
            holder.btnAdd.visibility = View.INVISIBLE
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
}