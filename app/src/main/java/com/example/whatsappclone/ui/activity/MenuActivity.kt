package com.example.whatsappclone.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.example.whatsappclone.R
import com.example.whatsappclone.data.ChatApplication
import com.example.whatsappclone.data.ChatDatabase
import com.example.whatsappclone.data.MessagesDao
import com.example.whatsappclone.databinding.ActivityMenuBinding
import com.example.whatsappclone.repository.FirebaseAuthRepository
import com.example.whatsappclone.repository.FirebaseStorageRepository
import com.example.whatsappclone.repository.FirebaseStoreRepository
import com.example.whatsappclone.ui.fragment.AboutUsFragment
import com.example.whatsappclone.ui.fragment.ContactsFragment
import com.example.whatsappclone.ui.fragment.MessagesFragment
import com.example.whatsappclone.ui.fragment.ProfileFragment
import com.example.whatsappclone.ui.viewModel.*
import com.example.whatsappclone.util.Constants.Companion.DP

class MenuActivity() : AppCompatActivity() {

    private lateinit var menuBinding: ActivityMenuBinding
    lateinit var profileViewModel: ProfileViewModel
    lateinit var contactsViewModel: ContactsViewModel
    lateinit var messagesViewModel: MessagesViewModel
    lateinit var messagesDao: MessagesDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        menuBinding = ActivityMenuBinding.inflate(layoutInflater)
        setContentView(menuBinding.root)

        messagesDao = (application as ChatApplication).db.messagesDao()

        setSupportActionBar(menuBinding.toolbarMenu)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        menuBinding.toolbarMenu.setNavigationOnClickListener {
            finish()
        }

        val firebaseAuthRepository = FirebaseAuthRepository()
        val firebaseStoreRepository = FirebaseStoreRepository()
        val firebaseStorageRepository = FirebaseStorageRepository()

        val profileViewModelProviderFactory = ProfileViewModelProviderFactory(
            firebaseAuthRepository,
            firebaseStoreRepository,
            firebaseStorageRepository
        )

        profileViewModel = ViewModelProvider(
            this@MenuActivity,
            profileViewModelProviderFactory
        )[ProfileViewModel::class.java]

        val contactsViewModelProviderFactory = ContactsViewModelProviderFactory(
            firebaseAuthRepository,
            firebaseStoreRepository
        )

        contactsViewModel = ViewModelProvider(
            this@MenuActivity,
            contactsViewModelProviderFactory
        )[ContactsViewModel::class.java]

        val messagesViewModelProviderFactory = MessagesViewModelProviderFactory(
            firebaseAuthRepository,
            firebaseStoreRepository
        )

        messagesViewModel = ViewModelProvider(
            this@MenuActivity,
            messagesViewModelProviderFactory
        )[MessagesViewModel::class.java]

        if (intent != null) {
            when (intent.getStringExtra("fragment").toString()) {

                "profile" -> {
                    supportFragmentManager.beginTransaction().replace(
                        R.id.fcv_menu, ProfileFragment()
                    ).commit()
                    menuBinding.tvTitle.text = getString(R.string.profile)
                }

                "about_us" -> {
                    supportFragmentManager.beginTransaction().replace(
                        R.id.fcv_menu, AboutUsFragment()
                    ).commit()
                    menuBinding.tvTitle.text = getString(R.string.about_us)
                }

                "contacts" -> {
                    supportFragmentManager.beginTransaction().replace(
                        R.id.fcv_menu, ContactsFragment()
                    ).commit()
                    menuBinding.tvTitle.text = getString(R.string.contacts)
                }

                "messaging" -> {
                    menuBinding.tvTitle.text = intent.getStringExtra("friendName")
                    val bundle = Bundle()
                    val fragment = MessagesFragment()
                    bundle.putString("chatroomId", intent.getStringExtra("chatroomId"))
                    bundle.putString("friendName", intent.getStringExtra("friendName"))
                    bundle.putString("friendId", intent.getStringExtra("friendId"))
                    bundle.putString(DP, intent.getStringExtra(DP))
                    fragment.arguments = bundle
                    supportFragmentManager.beginTransaction().replace(
                        R.id.fcv_menu, fragment
                    ).commit()
                }
            }
        }
    }
}