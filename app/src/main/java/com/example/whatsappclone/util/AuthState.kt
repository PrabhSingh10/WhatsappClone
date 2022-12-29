package com.example.whatsappclone.util

sealed class AuthState<T>(
    val data: T? = null,
    val exception: String = ""
){

    class Success<T>(data: T) : AuthState<T>(data)
    class Failure<T>(exception: String, data: T? = null) : AuthState<T>(data, exception)
    class Loading<T> : AuthState<T>()
}
