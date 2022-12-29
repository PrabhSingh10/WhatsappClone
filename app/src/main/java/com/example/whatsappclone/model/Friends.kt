package com.example.whatsappclone.model

data class Friends(
    val id: String,
    val userName: String,
    val userEmail: String,
    val userBio: String,
    val userProfilePhoto: String = "",
    val status: Boolean,
    val chatRoomId: String = ""
)


