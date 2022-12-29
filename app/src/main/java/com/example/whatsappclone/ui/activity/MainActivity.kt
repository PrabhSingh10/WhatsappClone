package com.example.whatsappclone.ui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.addCallback
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.whatsappclone.R
import com.example.whatsappclone.adapter.MainViewPagerAdapter
import com.example.whatsappclone.databinding.ActivityMainBinding
import com.example.whatsappclone.repository.FirebaseAuthRepository
import com.example.whatsappclone.repository.FirebaseStoreRepository
import com.example.whatsappclone.ui.viewModel.ChatViewModel
import com.example.whatsappclone.ui.viewModel.ChatViewModelProviderFactory
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging

class MainActivity : AppCompatActivity() {

    private var mainBinding : ActivityMainBinding? = null
    private val title = arrayOf("Chat", "Status", "Call")
    lateinit var chatViewModel : ChatViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainBinding?.root)

        setSupportActionBar(mainBinding?.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        mainBinding?.toolbar?.overflowIcon?.setTint(
            ContextCompat.getColor(
                this,
                R.color.white
            )
        )

        val firebaseAuthRepository = FirebaseAuthRepository()
        val firebaseStoreRepository = FirebaseStoreRepository()

        val chatViewModelProviderFactory = ChatViewModelProviderFactory(
            firebaseAuthRepository,
            firebaseStoreRepository
        )

        chatViewModel = ViewModelProvider(this@MainActivity,
        chatViewModelProviderFactory)[ChatViewModel::class.java]

        val adapter = MainViewPagerAdapter(this)
        mainBinding?.viewPager?.adapter = adapter
        TabLayoutMediator(mainBinding!!.tabLayout, mainBinding!!.viewPager){ tab, position ->
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
        when(item.itemId){

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
                FirebaseAuth.getInstance().signOut()
                this.finish()
                val intent = Intent(this, LoginSignupActivity::class.java)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }
}