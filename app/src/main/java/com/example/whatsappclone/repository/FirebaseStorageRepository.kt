package com.example.whatsappclone.repository

import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream

class FirebaseStorageRepository {

    private var storage : StorageReference = FirebaseStorage.getInstance().reference

    suspend fun updateProfilePicture(userInfo : String, image : Bitmap) : Uri {

        val byte = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.PNG, 90, byte)
        val byteArray = byte.toByteArray()
        storage.child("$userInfo/profilePicture").apply {
            putBytes(byteArray).await()
            return downloadUrl.await()
        }
    }
}