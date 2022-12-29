package com.example.whatsappclone.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.whatsappclone.repository.FirebaseAuthRepository
import com.example.whatsappclone.repository.FirebaseStorageRepository
import com.example.whatsappclone.repository.FirebaseStoreRepository

class ProfileViewModelProviderFactory(
    private val firebaseAuthRepository: FirebaseAuthRepository,
    private val firebaseStoreRepository: FirebaseStoreRepository,
    private val firebaseStorageRepository: FirebaseStorageRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ProfileViewModel(
            firebaseAuthRepository, firebaseStoreRepository, firebaseStorageRepository
        ) as T
    }
}