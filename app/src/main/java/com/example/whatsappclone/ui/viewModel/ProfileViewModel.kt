package com.example.whatsappclone.ui.viewModel

import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.whatsappclone.repository.FirebaseAuthRepository
import com.example.whatsappclone.repository.FirebaseStorageRepository
import com.example.whatsappclone.repository.FirebaseStoreRepository
import com.example.whatsappclone.util.Constants
import com.example.whatsappclone.util.Constants.Companion.DP
import kotlinx.coroutines.launch

class ProfileViewModel(
    firebaseAuthRepository: FirebaseAuthRepository,
    private val firebaseStoreRepository: FirebaseStoreRepository,
    private val firebaseStorageRepository: FirebaseStorageRepository
    )
    : ViewModel() {

    private val userInfo = firebaseAuthRepository.auth.currentUser?.uid!!

    val profileDetails = MutableLiveData<MutableMap<String, String>>()

    fun fetchProfile()  = viewModelScope.launch {
        profileDetails.postValue(firebaseStoreRepository.fetchProfile(userInfo))
    }

    fun updateProfile(info: MutableMap<String, String>) = viewModelScope.launch{
        firebaseStoreRepository.updateProfile(userInfo, info)
    }

    fun updateProfilePicture(image : Bitmap) = viewModelScope.launch {
        val info = mutableMapOf<String, String>()
        info[DP] = firebaseStorageRepository.updateProfilePicture(userInfo, image).toString()
        firebaseStoreRepository.updateProfile(userInfo, info)
    }

}