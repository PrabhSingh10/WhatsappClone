package com.example.whatsappclone.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.whatsappclone.util.Constants.Companion.CHAT_LIST_MODEL
import com.example.whatsappclone.util.Constants.Companion.FRIENDS

@Entity(tableName = CHAT_LIST_MODEL)
data class ChatListModel(
    @ColumnInfo(name = "Friends_ID")
    val friendId: String,
    @ColumnInfo(name = FRIENDS)
    val friendName: String,
    @ColumnInfo(name = "Friends_DP")
    val friendImage: String,
    @PrimaryKey
    val chatRoomId: String,
    @ColumnInfo(name = "Last_Message")
    val message: String = "",
    @ColumnInfo(name = "Message_Sender")
    val sender: String = "",
    @ColumnInfo(name = "Last_Interaction_Time")
    val time: String,
    @ColumnInfo(name = "Message_Status")
    val messageStatus: String
)