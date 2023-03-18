package com.example.whatsappclone.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.whatsappclone.repository.FirebaseAuthRepository
import com.example.whatsappclone.repository.FirebaseStoreRepository
import com.example.whatsappclone.repository.RoomRepository

class MessagesViewModelProviderFactory(
    private val firebaseAuthRepository : FirebaseAuthRepository,
    private val firebaseStoreRepository : FirebaseStoreRepository,
    private val roomRepository: RoomRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MessagesViewModel(
            firebaseAuthRepository,
            firebaseStoreRepository,
            roomRepository
        ) as T
    }
}