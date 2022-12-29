package com.example.whatsappclone.ui.fragment

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.whatsappclone.R
import com.example.whatsappclone.databinding.FragmentLoginBinding
import com.example.whatsappclone.ui.activity.LoginSignupActivity
import com.example.whatsappclone.ui.activity.MainActivity
import com.example.whatsappclone.ui.viewModel.LoginViewModel
import com.example.whatsappclone.util.AuthState
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class LoginFragment : Fragment() {

    private var loginBinding : FragmentLoginBinding? = null
    private lateinit var googleSignInClient : GoogleSignInClient
    private val loginViewModel: LoginViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        loginBinding = FragmentLoginBinding.inflate(
            inflater, container, false
        )
        return loginBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loginBinding?.btnLogin?.setOnClickListener{
            if (loginBinding?.etEmail?.text?.isEmpty() == true) {
                Toast.makeText(context, "Enter Email", Toast.LENGTH_SHORT).show()
            } else if (loginBinding?.etPassword?.text?.isEmpty() == true) {
                Toast.makeText(context, "Enter Password", Toast.LENGTH_SHORT).show()
            } else {
                val email = loginBinding!!.etEmail.text.toString()
                val password = loginBinding!!.etPassword.text.toString()

                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    Toast.makeText(context, "Incorrect Email", Toast.LENGTH_SHORT).show()
                } else if (password.length < 6) {
                    Toast.makeText(context, "Password too short", Toast.LENGTH_SHORT).show()
                } else {
                    loginViewModel.loginWithEmailPassword(email, password)
                }
            }
        }

        loginBinding?.ivGoogle?.setOnClickListener {
            googleSignIn()
        }

        loginViewModel.loginResult.observe(viewLifecycleOwner, Observer {
            when(it){

                is AuthState.Loading -> {
                    showProgressBar()
                }

                is AuthState.Success -> {
                    hideProgressBar()
                    requireActivity().finish()
                    val intent = Intent(activity, MainActivity::class.java)
                    startActivity(intent)
                }

                is AuthState.Failure -> {
                    hideProgressBar()
                    Toast.makeText(context, it.exception, Toast.LENGTH_SHORT).show()
                }
            }
        })

        loginBinding?.llSignup?.setOnClickListener {
            findNavController().navigate(
                R.id.action_loginFragment_to_signupFragment
            )
        }
    }

    private fun googleSignIn() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(requireContext(), gso)

        val intent = googleSignInClient.signInIntent
        googleSignInLauncher.launch(intent)
    }

    private val googleSignInLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()){
        if(it.resultCode == RESULT_OK){
            val task = GoogleSignIn.getSignedInAccountFromIntent(it.data)
            if(task.isSuccessful){
                val account = task.result
                account?.let {
                    loginUsingGoogle(account)
                }
            }else{
                Toast.makeText(activity, task.exception.toString(), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loginUsingGoogle(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        loginViewModel.loginWithGoogle(credential)
    }

    private fun showProgressBar() {
        loginBinding?.llSignup?.isEnabled = false
        loginBinding?.etEmail?.isEnabled = false
        loginBinding?.etPassword?.isEnabled = false
        loginBinding?.progressBar?.visibility = View.VISIBLE
        loginBinding?.btnLogin?.visibility = View.INVISIBLE
    }

    private fun hideProgressBar() {
        loginBinding?.llSignup?.isEnabled = true
        loginBinding?.etEmail?.isEnabled = true
        loginBinding?.etPassword?.isEnabled = true
        loginBinding?.progressBar?.visibility = View.INVISIBLE
        loginBinding?.btnLogin?.visibility = View.VISIBLE
    }

}