package com.example.whatsappclone.model

data class MessageModel(
    val senderId : String,
    val receiverId : String,
    val message : String,
    val timeStamp : String
)