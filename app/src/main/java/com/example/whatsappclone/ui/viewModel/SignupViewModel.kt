package com.example.whatsappclone.ui.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.whatsappclone.repository.FirebaseAuthRepository
import com.example.whatsappclone.repository.FirebaseStoreRepository
import com.example.whatsappclone.util.AuthState
import com.example.whatsappclone.util.Constants
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.launch

class SignupViewModel(
    private val firebaseAuthRepository: FirebaseAuthRepository,
    private val firebaseStoreRepository: FirebaseStoreRepository
    ) : ViewModel() {

    val signupResult = MutableLiveData<AuthState<FirebaseUser>>()

    fun createUser(name : String, email : String, password : String) = viewModelScope.launch {
        signupResult.postValue(AuthState.Loading())
        try{
            firebaseAuthRepository.signUpUser(email, password).let {
                signupResult.postValue(AuthState.Success(it))
                val obj = mutableMapOf<String, String>()
                obj[Constants.EMAIL] = email
                obj[Constants.PASSWORD] = password
                obj[Constants.NAME] = name
                obj[Constants.BIO] = "Available"
                obj[Constants.DP] = ""
                obj[Constants.ONLINE_STATUS] = "Online"
                firebaseStoreRepository.addUser(it.uid, obj)
            }
        }catch (e : FirebaseAuthException){
            signupResult.postValue(AuthState.Failure(e.message!!))
        }
    }
}