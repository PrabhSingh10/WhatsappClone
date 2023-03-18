package com.example.whatsappclone.repository

import com.example.whatsappclone.data.ChatDatabase
import com.example.whatsappclone.model.ChatListModel
import com.example.whatsappclone.model.MessageModel

class RoomRepository(db : ChatDatabase) {

    private val chatDao = db.chatsDao()
    private val messageDao = db.messagesDao()

    suspend fun upsertChat(chatListModel: MutableList<ChatListModel>){
        chatDao.upsert(chatListModel)
    }

    suspend fun upsertMessage(messageModel: MutableList<MessageModel>){
        messageDao.upsert(messageModel)
    }

    fun fetchFriends() = chatDao.fetchFriends()

    fun fetchMessage(chatRoomId : String) = messageDao.fetchMessage(chatRoomId)

    suspend fun clearRoom(){
        chatDao.clearChatData()
        messageDao.clearMessageData()
    }

}