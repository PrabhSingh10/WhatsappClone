package com.example.whatsappclone.util

import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow

//addSnapshotListenerFlow is an extension function of Firebase Queries
fun Query.addSnapshotListenerFlow() = callbackFlow {
    val listener = object : EventListener<QuerySnapshot> {
        override fun onEvent(snapshot: QuerySnapshot?, exception: FirebaseFirestoreException?) {
            if (exception != null) {
                // An error occurred
                cancel()
                return
            }
            trySend(snapshot)
        }
    }

    val registration = addSnapshotListener(listener)
    awaitClose { registration.remove() }
}