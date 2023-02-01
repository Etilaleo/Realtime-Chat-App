package com.example.chatapp.signOptions

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import com.example.chatapp.R
import com.example.chatapp.databinding.FragmentSignInBinding
import com.google.firebase.auth.FirebaseAuth

class SignIn : Fragment() {

    private lateinit var binding: FragmentSignInBinding
    private lateinit var auth: FirebaseAuth
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentSignInBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as AppCompatActivity).supportActionBar?.hide()

        binding.signUp.setOnClickListener {
            findNavController().navigate(R.id.action_signIn2_to_signUp)
        }

        binding.signInBtn.setOnClickListener{
            val email = binding.loginEmail.text.toString().trim()
            val password = binding.loginPassword.text.toString().trim()
            if (email != "" && password != "" ) {
                signInEmailAndPassword(email, password)
            }
            else {
                Toast.makeText(requireContext(), "Fill all fields", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun signInEmailAndPassword(email : String, password : String) {
        auth = FirebaseAuth.getInstance()
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    findNavController().navigate(R.id.action_signIn2_to_mainScreen)
                }
                else {
                    Toast.makeText(requireContext(), "User does not Exist!", Toast.LENGTH_LONG)
                        .show()
                }
            }
    }
}