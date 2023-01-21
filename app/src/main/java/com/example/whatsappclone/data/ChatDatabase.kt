package com.example.whatsappclone.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.whatsappclone.model.ChatListModel
import com.example.whatsappclone.model.MessageModel

@Database(
    entities = [ChatListModel::class, MessageModel::class],
    version = 1
)
abstract class ChatDatabase : RoomDatabase() {

    abstract fun chatsDao(): ChatsDao

    abstract fun messagesDao(): MessagesDao

    companion object {
        @Volatile
        private var INSTANCE: ChatDatabase? = null

        fun getInstance(context: Context): ChatDatabase {
            synchronized(this) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        ChatDatabase::class.java,
                        "Chat_Database"
                    ).build()
                }

                return INSTANCE!!
            }
        }
    }
}