package com.example.whatsappclone.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.whatsappclone.repository.FirebaseAuthRepository

class LoginViewModelProviderFactory(
    private val firebaseAuthRepository: FirebaseAuthRepository
    ) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return LoginViewModel(firebaseAuthRepository) as T
    }
}