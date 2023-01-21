package com.example.whatsappclone.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.whatsappclone.model.ChatListModel
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(chatListModel: MutableList<ChatListModel>)

    @Query("SELECT * FROM ChatListModel ORDER BY Last_Interaction_Time DESC")
    fun fetchFriends(): Flow<List<ChatListModel>>

    @Query("DELETE FROM ChatListModel")
    suspend fun clearChatData()

}