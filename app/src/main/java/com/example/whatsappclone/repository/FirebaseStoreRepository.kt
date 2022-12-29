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
import com.example.whatsappclone.util.Constants.Companion.NAME
import com.example.whatsappclone.util.Constants.Companion.RECEIVER_ID
import com.example.whatsappclone.util.Constants.Companion.SENDER_ID
import com.example.whatsappclone.util.Constants.Companion.TIME
import com.example.whatsappclone.util.Constants.Companion.TIMESTAMP
import com.example.whatsappclone.util.Constants.Companion.UIDS
import com.google.firebase.firestore.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class FirebaseStoreRepository {

    private val store : FirebaseFirestore = FirebaseFirestore.getInstance()
    private lateinit var db : DocumentReference
    private lateinit var cb : CollectionReference

    suspend fun addUser(userInfo: String, info: MutableMap<String, String>){
        db = store.collection(USERS).document(userInfo)
        try {
            db.set(info).await()
        }catch (e : Exception){
            e.printStackTrace()
        }
    }

    suspend fun fetchProfile(userInfo: String) : MutableMap<String, String> {
        db = store.collection(USERS).document(userInfo)
        val result = mutableMapOf<String, String>()
        try {
            val obj = db.get().await()
            result[NAME] = obj.getString(NAME).toString()
            result[EMAIL] = obj.getString(EMAIL).toString()
            result[BIO] = obj.getString(BIO).toString()
            result[DP] = obj.getString(DP).toString()
        }catch (e : Exception){
            e.printStackTrace()
        }
        return result
    }

    suspend fun updateProfile(userInfo: String, info: MutableMap<String, String>){
        db = store.collection(USERS).document(userInfo)
        try {
            db.update(info as Map<String, Any>).await()
        }catch (e : Exception){
            e.printStackTrace()
        }
    }

    suspend fun fetchAllProfile(userInfo: String) : MutableList<Friends> {
        cb = store.collection(USERS)
        val profilesList = mutableListOf<Friends>()
        try {
            val obj = cb.get().await()
            obj?.let {
                val list = it.documents
                for(i in list){
                    if(i.id == userInfo){
                        Log.d("onFound", "This is user account")
                    }else {
                        val status = checkingStatus(userInfo, i.id)
                        var chatRoomId = ""
                        if(status){
                            //This means they are friends and have an already established
                            //Chat Room Id in the collection friends of the user
                            val doc = cb.document(userInfo).collection(FRIENDS).document(i.id).get().await()
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
        }catch (e : Exception){
            e.printStackTrace()
        }
        return profilesList
    }

    suspend fun fetchSearchProfile(userInfo: String, queryTerm: String) : MutableList<Friends> {
        cb = store.collection(USERS)
        val profilesList = mutableListOf<Friends>()
        try {
            val obj = cb.orderBy(NAME).startAt(queryTerm).limit(3).get().await()
            obj?.let {
                val list = it.documents
                for(i in list){
                    if(i.id == userInfo){
                        Log.d("onFound", "This is user account")
                    }else {
                        val status = checkingStatus(userInfo, i.id)
                        var chatRoomId = ""
                        if(status){
                            val doc = cb.document(userInfo).collection(FRIENDS).document(i.id).get().await()
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
        }catch (e : Exception){
            e.printStackTrace()
        }
        return profilesList
    }

    suspend fun addFriend(userInfo: String, friendInfo: String, timeStamp: String) {
        val obj = mutableMapOf<String, Any>().also {
            it[UIDS] = arrayListOf(userInfo, friendInfo)
            it[COUNT] = 1
        }
        try {
            store.collection(CHATS).document().set(obj).await()
        }catch (e : Exception){
            e.printStackTrace()
        }

        val obj1 = mutableMapOf<String, String>().also {
            it[TIME] = timeStamp
            try {
                val query = store.collection(CHATS)
                    .whereArrayContains(UIDS, userInfo)
                    .get().await()
                val list = query.documents
                for (doc in list){
                    if(doc.data?.containsValue(arrayListOf(userInfo, friendInfo)) == true){
                        it[CHATROOM_ID] = doc.id
                    }
                }
            }catch (e : Exception){
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
        }catch (e : Exception){
            e.printStackTrace()
        }
    }

    suspend fun fetchingChat(userInfo: String): MutableList<ChatListModel> {
        cb = store.collection(CHATS)
        val profilesList = mutableListOf<ChatListModel>()
        try {
            val query = cb.whereArrayContains(UIDS, userInfo).get().await()
            val list = query.documents
            for(doc in list){
                val chatList = cb.document(doc.id).collection(MESSAGE)
                    .orderBy(MESSAGE_ID, Query.Direction.DESCENDING).get().await()
                val documents = chatList.documents
                val message = if(documents.isNotEmpty()){
                    documents[0].getString(MESSAGE)
                }else {
                    ""
                }

                val uids = doc.data?.get(UIDS) as List<*>
                for(id in uids){
                    if(id == userInfo){
                        Log.d("onFound", "This is user account")
                    } else {
                        val friend = store.collection(USERS).document(id.toString()).get().await()
                        val obj = ChatListModel(
                            id.toString(),
                            friend.getString(NAME).toString(),
                            friend.getString(DP).toString(),
                            doc.id,
                            message.toString()
                        )
                        profilesList.add(obj)
                    }
                }
            }
        }catch (e : Exception){
            e.printStackTrace()
        }
        return profilesList
    }

    suspend fun sendMessage(
        userInfo: String, friendInfo: String, message: String, timeStamp: String, chatRoomId: String
    ){
        db = store.collection(CHATS).document(chatRoomId)
        try {
            val info = db.get().await()
            val messageId = info.get(COUNT).toString().toInt()
            val countId = mutableMapOf<String, Any>().also {
                it[COUNT] = (messageId + 1)
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

        }catch (e : Exception){
            e.printStackTrace()
        }
    }

    /*suspend fun fetchMessages(chatRoomId: String) : MutableList<MessageModel>{
        val messages = mutableListOf<MessageModel>()
        cb = store.collection(CHATS)
        try{
            /*val chatList = cb.document(chatRoomId).collection(MESSAGE)
                .orderBy(MESSAGE_ID, Query.Direction.ASCENDING).get().await()
            Log.d("Chat List", chatList.toString())*/
            val doc = realtimeUpdates(chatRoomId)
            for(i in doc) {
                val obj = MessageModel(
                    i.getString(SENDER_ID).toString(),
                    i.getString(RECEIVER_ID).toString(),
                    i.getString(MESSAGE).toString(),
                    i.getString(TIMESTAMP).toString()
                )
                messages.add(obj)
            }

        }catch (e : Exception){
            Log.e("Message Exception", e.message.toString())
            e.printStackTrace()
        }
        Log.d("Message List", messages.toString())
        return messages
    }*/

    suspend fun realtimeUpdates(chatRoomId: String) : Flow<MutableList<MessageModel>> {
        return callbackFlow<MutableList<MessageModel>> {
            val messages = mutableListOf<MessageModel>()
            cb = store.collection(CHATS)
            var listener : ListenerRegistration? = null
            listener = cb.document(chatRoomId).collection(MESSAGE)
                .orderBy(MESSAGE_ID, Query.Direction.ASCENDING).addSnapshotListener { value, error ->
                    Log.d("Error,  Value", "$error , $value")
                    error?.let { err ->
                        // emit(err)
                    }
                    value?.let { query ->
                        Log.d("Query Documents", query.documents.toString())
                        for(doc in query.documentChanges){
                            Log.d("Document Type", doc.type.name)
                            when(doc.type){
                                DocumentChange.Type.ADDED ->{
                                    Log.d("Document Changes List", doc.document.data.toString())
                                    val i = doc.document
                                    val obj = MessageModel(
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
                        Log.d("Document Type", messages.size.toString())
                        trySend(messages)
                    }
                }
            awaitClose{
                listener.remove()
            }
        }
    }

    private suspend fun checkingStatus(userInfo: String, friendInfo: String) : Boolean{
        try {
            val obj = store.collection(USERS).document(userInfo)
                .collection(FRIENDS).document(friendInfo).get().await()
            return obj.exists()
        }catch (e : Exception){
            e.printStackTrace()
        }
        return false
    }

}