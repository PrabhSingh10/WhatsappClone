package com.example.whatsappclone.ui.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.whatsappclone.repository.FirebaseAuthRepository
import com.example.whatsappclone.util.AuthState
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.launch

class LoginViewModel(private val firebaseAuthRepository: FirebaseAuthRepository) : ViewModel() {

    val loginResult = MutableLiveData<AuthState<FirebaseUser>>()

    fun loginWithEmailPassword(email : String, password : String) = viewModelScope.launch {
        loginResult.postValue(AuthState.Loading())
        try {
            firebaseAuthRepository.loginUser(email, password).let {
                loginResult.postValue(AuthState.Success(it))
            }
        }catch (e : FirebaseAuthException){
            loginResult.postValue(AuthState.Failure(e.message!!))
        }
    }

    fun loginWithGoogle(credential: AuthCredential) = viewModelScope.launch {
        loginResult.postValue(AuthState.Loading())
        try {
            firebaseAuthRepository.loginUserUsingGoogle(credential).let {
                loginResult.postValue(AuthState.Success(it))

            }
        }catch (e : FirebaseAuthException){
            loginResult.postValue(AuthState.Failure(e.message!!))
        }
    }
}