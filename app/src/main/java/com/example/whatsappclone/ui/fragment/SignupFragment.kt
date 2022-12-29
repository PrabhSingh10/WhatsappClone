package com.example.whatsappclone.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.whatsappclone.R
import com.example.whatsappclone.databinding.FragmentSignupBinding
import com.example.whatsappclone.ui.activity.LoginSignupActivity
import com.example.whatsappclone.ui.activity.MainActivity
import com.example.whatsappclone.ui.viewModel.SignupViewModel
import com.example.whatsappclone.util.AuthState
import com.google.firebase.auth.FirebaseAuth

class SignupFragment : Fragment() {

    private var signupBinding : FragmentSignupBinding? = null
    private val signupViewModel: SignupViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        signupBinding = FragmentSignupBinding.inflate(
            inflater, container, false
        )
        return signupBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        signupBinding?.btnSignUp?.setOnClickListener {
            if(signupBinding?.etName?.text?.isEmpty() == true){
                Toast.makeText(context, "Enter Name", Toast.LENGTH_SHORT).show()
            }else if(signupBinding?.etEmail?.text?.isEmpty() == true){
                Toast.makeText(context, "Enter Email", Toast.LENGTH_SHORT).show()
            }else if(signupBinding?.etPassword?.text?.isEmpty() == true){
                Toast.makeText(context, "Enter Password", Toast.LENGTH_SHORT).show()
            }else if(signupBinding?.etConfirmPassword?.text?.isEmpty() == true){
                Toast.makeText(context, "Enter Confirm Password", Toast.LENGTH_SHORT).show()
            }else{
                val name = signupBinding!!.etName.text.toString()
                val email = signupBinding!!.etEmail.text.toString()
                val password = signupBinding!!.etPassword.text.toString()
                val confirmPassword = signupBinding!!.etConfirmPassword.text.toString()

                if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    Toast.makeText(context, "Incorrect Email", Toast.LENGTH_SHORT).show()
                }else if(password != confirmPassword){
                    Toast.makeText(context, "Passwords don't match", Toast.LENGTH_SHORT).show()
                }else if(password.length < 6){
                    Toast.makeText(context, "Password too short", Toast.LENGTH_SHORT).show()
                }else{
                    signupViewModel.createUser(name ,email, password)
                }
            }
        }

        signupBinding?.llLogin?.setOnClickListener {
            findNavController().navigate(
                R.id.action_signupFragment_to_loginFragment
            )
        }

        signupViewModel.signupResult.observe(viewLifecycleOwner, Observer {
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
    }

    private fun hideProgressBar() {
        signupBinding?.etEmail?.isEnabled = true
        signupBinding?.llLogin?.isEnabled = true
        signupBinding?.etPassword?.isEnabled = true
        signupBinding?.etConfirmPassword?.isEnabled = true
        signupBinding?.progressBar?.visibility = View.INVISIBLE
        signupBinding?.btnSignUp?.visibility = View.VISIBLE
    }

    private fun showProgressBar() {
        signupBinding?.etEmail?.isEnabled = false
        signupBinding?.llLogin?.isEnabled = false
        signupBinding?.etPassword?.isEnabled = false
        signupBinding?.etConfirmPassword?.isEnabled = false
        signupBinding?.progressBar?.visibility = View.VISIBLE
        signupBinding?.btnSignUp?.visibility = View.INVISIBLE
    }

}