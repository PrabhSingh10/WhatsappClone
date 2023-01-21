package com.example.whatsappclone.repository

import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await

class FirebaseAuthRepository {

    val auth : FirebaseAuth = FirebaseAuth.getInstance()

    suspend fun signUpUser(email: String, password: String) : FirebaseUser {
        auth.createUserWithEmailAndPassword(email, password).await()
        return auth.currentUser ?: throw FirebaseAuthException("","")
    }

    suspend fun loginUser(email : String, password : String) : FirebaseUser {
        auth.signInWithEmailAndPassword(email, password).await()
        return auth.currentUser ?: throw FirebaseAuthException("","")
    }
}