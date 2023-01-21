package com.example.whatsappclone.ui.viewModel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.whatsappclone.model.Friends
import com.example.whatsappclone.repository.FirebaseAuthRepository
import com.example.whatsappclone.repository.FirebaseStoreRepository
import kotlinx.coroutines.launch

class ContactsViewModel(
    firebaseAuthRepository: FirebaseAuthRepository,
    private val firebaseStoreRepository: FirebaseStoreRepository
    ) : ViewModel() {

    val contactsList = MutableLiveData<MutableList<Friends>>()
    val searchedList = MutableLiveData<MutableList<Friends>>()
    private val userInfo = firebaseAuthRepository.auth.currentUser?.uid!!

    fun getContacts() = viewModelScope.launch {
        contactsList.postValue(firebaseStoreRepository.fetchAllProfile(userInfo))
    }

    fun getSearchContact(queryTerm : String) = viewModelScope.launch {
        searchedList.postValue(firebaseStoreRepository.fetchSearchProfile(userInfo, queryTerm))
    }

    fun addFriend(friendInfo : String, timeStamp : String) = viewModelScope.launch {
        firebaseStoreRepository.addFriend(userInfo, friendInfo, timeStamp)
    }

}