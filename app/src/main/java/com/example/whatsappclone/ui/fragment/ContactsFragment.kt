package com.example.whatsappclone.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.whatsappclone.adapter.ContactsAdapter
import com.example.whatsappclone.databinding.FragmentContactsBinding
import com.example.whatsappclone.ui.activity.MenuActivity
import com.example.whatsappclone.ui.viewModel.ContactsViewModel
import com.example.whatsappclone.util.Constants.Companion.SEARCH_TIME_DELAY
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ContactsFragment : Fragment() {

    private var contactsBinding : FragmentContactsBinding? = null
    private lateinit var contactsAdapter: ContactsAdapter
    private val contactsViewModel: ContactsViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        contactsBinding = FragmentContactsBinding.inflate(
            inflater, container, false
        )
        return contactsBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpRecyclerView()
    }

    override fun onResume() {
        super.onResume()

        var job : Job? = null

        contactsBinding?.etContactSearch?.addTextChangedListener {
            job?.cancel()
            job = MainScope().launch {
                delay(SEARCH_TIME_DELAY)
                it?.let { query ->
                    if(query.toString().isNotEmpty()){
                        contactsViewModel.getSearchContact(query.toString())
                        getSearchedContacts()
                    }else {
                        getAllContacts()
                    }
                }
            }
        }
    }

    private fun setUpRecyclerView() {
        contactsAdapter = ContactsAdapter{ id, timestamp ->
            contactsViewModel.addFriend(id, timestamp)
        }
        contactsBinding?.rvContacts?.layoutManager = LinearLayoutManager(context)
        contactsViewModel.getContacts()
        getAllContacts()
        contactsBinding?.rvContacts?.adapter = contactsAdapter
    }

    private fun getAllContacts() {
        contactsViewModel.contactsList.observe(viewLifecycleOwner, Observer {
            contactsAdapter.differ.submitList(it)
        })
    }

    private fun getSearchedContacts() {
        contactsViewModel.searchedList.observe(viewLifecycleOwner, Observer {
            contactsAdapter.differ.submitList(it)
        })
    }

}