package com.example.whatsappclone.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.whatsappclone.repository.FirebaseAuthRepository
import com.example.whatsappclone.repository.FirebaseStoreRepository

class ContactsViewModelProviderFactory(
    private val firebaseAuthRepository : FirebaseAuthRepository,
    private val firebaseStoreRepository : FirebaseStoreRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ContactsViewModel(
            firebaseAuthRepository,
            firebaseStoreRepository
        ) as T
    }
}