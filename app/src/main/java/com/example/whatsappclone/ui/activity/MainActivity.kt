package com.example.whatsappclone.ui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.whatsappclone.R
import com.example.whatsappclone.adapter.MainViewPagerAdapter
import com.example.whatsappclone.data.*
import com.example.whatsappclone.databinding.ActivityMainBinding
import com.example.whatsappclone.repository.FirebaseAuthRepository
import com.example.whatsappclone.repository.FirebaseStoreRepository
import com.example.whatsappclone.repository.RoomRepository
import com.example.whatsappclone.ui.viewModel.ChatViewModel
import com.example.whatsappclone.ui.viewModel.ChatViewModelProviderFactory
import com.example.whatsappclone.ui.viewModel.MessagesViewModel
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private var mainBinding: ActivityMainBinding? = null
    private val title = arrayOf("Chat", "Status", "Call")
    lateinit var chatViewModel: ChatViewModel
    lateinit var messageViewModel : MessagesViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainBinding?.root)


        setSupportActionBar(mainBinding?.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        //Menu Icon Tint
        mainBinding?.toolbar?.overflowIcon?.setTint(
            ContextCompat.getColor(
                this,
                R.color.white
            )
        )

        val firebaseAuthRepository = FirebaseAuthRepository()
        val firebaseStoreRepository = FirebaseStoreRepository()
        val roomRepository = RoomRepository((application as ChatApplication).db)

        val chatViewModelProviderFactory = ChatViewModelProviderFactory(
            firebaseAuthRepository,
            firebaseStoreRepository,
            roomRepository
        )

        chatViewModel = ViewModelProvider(
            this@MainActivity,
            chatViewModelProviderFactory
        )[ChatViewModel::class.java]

        val adapter = MainViewPagerAdapter(this)
        mainBinding?.viewPager?.adapter = adapter
        TabLayoutMediator(mainBinding!!.tabLayout, mainBinding!!.viewPager) { tab, position ->
            tab.text = title[position]
        }.attach()

        mainBinding?.fbContacts?.setOnClickListener {
            val intent = Intent(this, MenuActivity::class.java)
            intent.putExtra("fragment", "contacts")
            startActivity(intent)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {

            R.id.profile -> {
                val intent = Intent(this, MenuActivity::class.java)
                intent.putExtra("fragment", "profile")
                startActivity(intent)
            }
            R.id.about_us -> {
                val intent = Intent(this, MenuActivity::class.java)
                intent.putExtra("fragment", "about_us")
                startActivity(intent)
            }
            R.id.logout -> {
                chatViewModel.updateOnlineStatus("Offline")
                FirebaseAuth.getInstance().signOut()
                clearTableData()
                this.finish()
                val intent = Intent(this, LoginSignupActivity::class.java)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun clearTableData() {
        chatViewModel.clearRoom()
    }

    override fun onStart() {
        super.onStart()
        chatViewModel.updateOnlineStatus("Online")
    }

    override fun onPause() {
        super.onPause()
        chatViewModel.updateOnlineStatus("Offline")
    }
}