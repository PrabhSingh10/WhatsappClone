package com.example.whatsappclone.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.whatsappclone.model.MessageModel
import kotlinx.coroutines.flow.Flow

@Dao
interface MessagesDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun upsert(messageModel: MutableList<MessageModel>)

    @Query("SELECT * FROM Message WHERE chatRoomId = :chatRoomId")
    fun fetchMessage(chatRoomId : String) : Flow<List<MessageModel>>

    @Query("DELETE FROM Message")
    suspend fun clearMessageData()
}