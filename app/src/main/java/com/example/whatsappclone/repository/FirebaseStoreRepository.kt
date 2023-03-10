package com.example.whatsappclone.repository

import android.util.Log
import com.example.whatsappclone.model.ChatListModel
import com.example.whatsappclone.model.Friends
import com.example.whatsappclone.model.MessageModel
import com.example.whatsappclone.util.Constants.Companion.BIO
import com.example.whatsappclone.util.Constants.Companion.CHATROOM_ID
import com.example.whatsappclone.util.Constants.Companion.USERS
import com.example.whatsappclone.util.Constants.Companion.DP
import com.example.whatsappclone.util.Constants.Companion.EMAIL
import com.example.whatsappclone.util.Constants.Companion.CHATS
import com.example.whatsappclone.util.Constants.Companion.COUNT
import com.example.whatsappclone.util.Constants.Companion.FRIENDS
import com.example.whatsappclone.util.Constants.Companion.MESSAGE
import com.example.whatsappclone.util.Constants.Companion.MESSAGE_ID
import com.example.whatsappclone.util.Constants.Companion.MESSAGE_STATUS
import com.example.whatsappclone.util.Constants.Companion.NAME
import com.example.whatsappclone.util.Constants.Companion.ONLINE_STATUS
import com.example.whatsappclone.util.Constants.Companion.RECEIVER_ID
import com.example.whatsappclone.util.Constants.Companion.SENDER_ID
import com.example.whatsappclone.util.Constants.Companion.TIME
import com.example.whatsappclone.util.Constants.Companion.TIMESTAMP
import com.example.whatsappclone.util.Constants.Companion.UIDS
import com.example.whatsappclone.util.addSnapshotListenerFlow
import com.google.firebase.firestore.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class FirebaseStoreRepository {

    private val store: FirebaseFirestore = FirebaseFirestore.getInstance()
    private lateinit var db: DocumentReference
    private lateinit var cb: CollectionReference

    suspend fun addUser(userInfo: String, info: MutableMap<String, String>) {
        db = store.collection(USERS).document(userInfo)
        try {
            db.set(info).await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun fetchProfile(userInfo: String): MutableMap<String, String> {
        db = store.collection(USERS).document(userInfo)
        val result = mutableMapOf<String, String>()
        try {
            val obj = db.get().await()
            result[NAME] = obj.getString(NAME).toString()
            result[EMAIL] = obj.getString(EMAIL).toString()
            result[BIO] = obj.getString(BIO).toString()
            result[DP] = obj.getString(DP).toString()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return result
    }

    suspend fun updateProfile(userInfo: String, info: MutableMap<String, String>) {
        db = store.collection(USERS).document(userInfo)
        try {
            db.update(info as Map<String, String>).await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun fetchAllProfile(userInfo: String): MutableList<Friends> {
        cb = store.collection(USERS)
        val profilesList = mutableListOf<Friends>()
        try {
            val obj = cb.get().await()
            obj?.let {
                val list = it.documents
                for (i in list) {
                    if (i.id == userInfo) {
                        Log.d("onFound", "This is user account")
                    } else {
                        val status = checkingStatus(userInfo, i.id)
                        var chatRoomId = ""
                        if (status) {
                            //This means they are friends and have an already established
                            //Chat Room Id in the collection friends of the user
                            val doc = cb.document(userInfo).collection(FRIENDS).document(i.id).get()
                                .await()
                            chatRoomId = doc.getString(CHATROOM_ID).toString()
                        }
                        val contact = Friends(
                            i.id,
                            i.getString(NAME).toString(),
                            i.getString(EMAIL).toString(),
                            i.getString(BIO).toString(),
                            i.getString(DP).toString(),
                            status,
                            chatRoomId
                        )
                        profilesList.add(contact)
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return profilesList
    }

    suspend fun fetchSearchProfile(userInfo: String, queryTerm: String): MutableList<Friends> {
        cb = store.collection(USERS)
        val profilesList = mutableListOf<Friends>()
        try {
            val obj = cb.orderBy(NAME).startAt(queryTerm).limit(3).get().await()
            obj?.let {
                val list = it.documents
                for (i in list) {
                    if (i.id == userInfo) {
                        Log.d("onFound", "This is user account")
                    } else {
                        val status = checkingStatus(userInfo, i.id)
                        var chatRoomId = ""
                        if (status) {
                            val doc = cb.document(userInfo).collection(FRIENDS).document(i.id).get()
                                .await()
                            chatRoomId = doc.getString(CHATROOM_ID).toString()
                        }
                        val contact = Friends(
                            i.id,
                            i.getString(NAME).toString(),
                            i.getString(EMAIL).toString(),
                            i.getString(BIO).toString(),
                            i.getString(DP).toString(),
                            status,
                            chatRoomId
                        )
                        profilesList.add(contact)
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return profilesList
    }

    suspend fun addFriend(userInfo: String, friendInfo: String, timeStamp: String) {
        val obj = mutableMapOf<String, Any>().also {
            it[UIDS] = arrayListOf(userInfo, friendInfo)
            it[COUNT] = 1
            it[MESSAGE_STATUS] = 1
        }
        try {
            store.collection(CHATS).document().set(obj).await()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        val obj1 = mutableMapOf<String, String>().also {
            it[TIME] = timeStamp
            try {
                val query = store.collection(CHATS)
                    .whereArrayContains(UIDS, userInfo)
                    .get().await()
                val list = query.documents
                for (doc in list) {
                    if (doc.data?.containsValue(arrayListOf(userInfo, friendInfo)) == true) {
                        it[CHATROOM_ID] = doc.id
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        try {
            store.collection(USERS).document(userInfo)
                .collection(FRIENDS).document(friendInfo)
                .set(obj1).await()
            store.collection(USERS).document(friendInfo)
                .collection(FRIENDS).document(userInfo)
                .set(obj1).await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun fetchingChat(userInfo: String): Flow<MutableList<ChatListModel>> {
        cb = store.collection(CHATS)
        return cb.whereArrayContains(UIDS, userInfo)
            .addSnapshotListenerFlow()
            .map {
                val profilesList = mutableListOf<ChatListModel>()
                val list = it!!.documents
                for (doc in list) {
                    var sender = ""
                    var status = ""
                    var message = ""
                    var time = ""   //Contains either the time of the last message
                    //or the time the contact was added.
                    val docs = cb.document(doc.id).collection(MESSAGE)
                        .orderBy(MESSAGE_ID, Query.Direction.DESCENDING).get().await()
                    val documents = docs.documents
                    if (documents.isNotEmpty()) {
                        message = documents[0].getString(MESSAGE).toString()
                        sender = documents[0].getString(SENDER_ID).toString()
                        time = documents[0].getString(TIMESTAMP).toString()
                    }

                    val statusCheck = cb.document(doc.id).get().await()
                    status = statusCheck.get(MESSAGE_STATUS).toString()

                    val uids = doc.data?.get(UIDS) as List<*>
                    for (id in uids) {
                        if (id == userInfo) {
                            Log.d("onFound", "This is user account")
                        } else {
                            val friend =
                                store.collection(USERS).document(id.toString()).get().await()
                            if (time.isEmpty()) {
                                val d = store.collection(USERS).document(id.toString())
                                    .collection(FRIENDS).document(userInfo).get().await()
                                time = d.getString(TIME).toString()
                            }
                            val obj = ChatListModel(
                                id.toString(),
                                friend.getString(NAME).toString(),
                                friend.getString(DP).toString(),
                                doc.id,
                                message,
                                sender,
                                time,
                                status
                            )
                            profilesList.add(obj)
                        }
                    }
                }
                profilesList
            }
    }

    suspend fun sendMessage(
        userInfo: String, friendInfo: String, message: String, timeStamp: String, chatRoomId: String
    ) {
        db = store.collection(CHATS).document(chatRoomId)
        try {
            val info = db.get().await()
            val messageId = info.get(COUNT).toString().toInt()
            val countId = mutableMapOf<String, Any>().also {
                it[COUNT] = (messageId + 1)
                it[MESSAGE_STATUS] = 0
            }
            db.update(countId).await()
            val obj = mutableMapOf<String, Any>().also {
                it[MESSAGE_ID] = messageId
                it[SENDER_ID] = userInfo
                it[RECEIVER_ID] = friendInfo
                it[MESSAGE] = message
                it[TIMESTAMP] = timeStamp
            }
            db.collection(MESSAGE).document().set(obj).await()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun fetchMessages(chatRoomId: String): Flow<MutableList<MessageModel>> {
        return callbackFlow {
            val messages = mutableListOf<MessageModel>()
            cb = store.collection(CHATS)
            cb.document(chatRoomId).collection(MESSAGE)
                .orderBy(MESSAGE_ID, Query.Direction.ASCENDING)
                .addSnapshotListener { value, error ->
                    error?.let { err ->
                        Log.d("Update Error", err.message.toString())
                    }
                    value?.let { query ->
                        for (doc in query.documentChanges) {
                            when (doc.type) {
                                DocumentChange.Type.ADDED -> {
                                    val i = doc.document
                                    val obj = MessageModel(
                                        chatRoomId,
                                        i.getString(SENDER_ID).toString(),
                                        i.getString(RECEIVER_ID).toString(),
                                        i.getString(MESSAGE).toString(),
                                        i.getString(TIMESTAMP).toString()
                                    )
                                    messages.add(obj)
                                }
                                else -> {}
                            }
                        }
                        trySend(messages)
                    }
                }
            awaitClose()
        }
    }

    suspend fun onlineStatus(userInfo: String): Flow<Boolean> {
        return callbackFlow{
            db = store.collection(USERS).document(userInfo)
            try {
                db.addSnapshotListener { value, error ->
                    error?.let {
                        it.printStackTrace()
                    }
                    value?.let {
                        val status = (it.getString(ONLINE_STATUS) == "Online")
                        trySend(status)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            awaitClose()
        }
    }

    suspend fun messageStatus(chatRoomId: String) {
        try {
            val update = mutableMapOf<String, Any>()
            update[MESSAGE_STATUS] = 1
            store.collection(CHATS).document(chatRoomId).update(update).await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private suspend fun checkingStatus(userInfo: String, friendInfo: String): Boolean {
        try {
            val obj = store.collection(USERS).document(userInfo)
                .collection(FRIENDS).document(friendInfo).get().await()
            return obj.exists()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }

}