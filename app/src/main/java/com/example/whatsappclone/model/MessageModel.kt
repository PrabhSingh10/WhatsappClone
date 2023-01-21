package com.example.whatsappclone.model

import androidx.room.Entity
import com.example.whatsappclone.util.Constants.Companion.MESSAGE

@Entity(
    tableName = MESSAGE,
    primaryKeys = ["senderId", "receiverId", "message", "timeStamp"]
)
data class MessageModel(
    val chatRoomId : String,
    val senderId : String,
    val receiverId : String,
    val message : String,
    val timeStamp : String
)