package com.example.whatsappclone.data

import android.app.Application

class ChatApplication : Application() {

    val db by lazy {
        ChatDatabase.getInstance(this)
    }
}