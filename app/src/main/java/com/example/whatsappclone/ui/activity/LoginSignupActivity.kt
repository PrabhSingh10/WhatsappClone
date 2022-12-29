package com.example.whatsappclone.ui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.example.whatsappclone.databinding.ActivityLoginSignupBinding
import com.example.whatsappclone.repository.FirebaseAuthRepository
import com.example.whatsappclone.repository.FirebaseStoreRepository
import com.example.whatsappclone.ui.viewModel.LoginViewModel
import com.example.whatsappclone.ui.viewModel.LoginViewModelProviderFactory
import com.example.whatsappclone.ui.viewModel.SignupViewModel
import com.example.whatsappclone.ui.viewModel.SignupViewModelProviderFactory
import com.google.firebase.auth.FirebaseAuth

class LoginSignupActivity : AppCompatActivity() {

    private var loginSignupBinding : ActivityLoginSignupBinding? = null
    lateinit var loginViewModel: LoginViewModel
    lateinit var signupViewModel: SignupViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loginSignupBinding = ActivityLoginSignupBinding.inflate(layoutInflater)
        setContentView(loginSignupBinding?.root)

        val firebaseAuthRepository = FirebaseAuthRepository()
        val firebaseStoreRepository = FirebaseStoreRepository()

        if(FirebaseAuth.getInstance().currentUser != null){
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            this.finish()
        }

        val signupViewModelProviderFactory = SignupViewModelProviderFactory(
            firebaseAuthRepository,
            firebaseStoreRepository
        )
        val loginViewModelProviderFactory =  LoginViewModelProviderFactory(firebaseAuthRepository)

        signupViewModel = ViewModelProvider(this@LoginSignupActivity,
        signupViewModelProviderFactory)[SignupViewModel::class.java]
        loginViewModel = ViewModelProvider(this@LoginSignupActivity,
        loginViewModelProviderFactory)[LoginViewModel::class.java]
    }
}