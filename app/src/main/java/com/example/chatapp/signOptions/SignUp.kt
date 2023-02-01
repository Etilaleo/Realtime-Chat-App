package com.example.chatapp.signOptions

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import com.example.chatapp.models.ChatData
import com.example.chatapp.R
import com.example.chatapp.databinding.FragmentSignUpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.util.*

class SignUp : Fragment() {

    private lateinit var binding: FragmentSignUpBinding
    private lateinit var auth : FirebaseAuth
    private lateinit var stRef : StorageReference
    private lateinit var dbRef : DatabaseReference
    private lateinit var uri : Uri

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentSignUpBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        (activity as AppCompatActivity).supportActionBar?.hide()

        binding.signIn.setOnClickListener{
            findNavController().navigate(R.id.action_signUp_to_signIn2)
        }

        binding.image.setOnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_PICK
            launchIntent.launch(intent)
        }

        binding.signUpBtn.setOnClickListener{
            val email = binding.email.text.toString().trim()
            val username = binding.username.text.toString().trim()
            val password = binding.password.text.toString().trim()
            val rePassword = binding.rePassword.text.toString().trim()

            if (email != "" && password != "" && rePassword != "") {
                if (password != rePassword) {
                    Toast.makeText(requireContext(), "Passwords don't match", Toast.LENGTH_SHORT)
                        .show()
                }
                else {
                    registerEmailAndPassword(email, password, username)
                }
            }
            else {
                Toast.makeText(requireContext(), "Fill all fields", Toast.LENGTH_SHORT)
                    .show()
            }
        }

    }

    private fun registerEmailAndPassword(email :String, password: String, username: String) {
        auth = FirebaseAuth.getInstance()

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (it.isSuccessful){
                    Toast.makeText(requireContext(), "Authentication successful", Toast.LENGTH_SHORT)
                        .show()
                    findNavController().navigate(R.id.action_signUp_to_signIn2)

                    saveImageToFirebase(email, username)
                }
                else {
                    Log.d("SignUp", it.exception.toString())
                    Toast.makeText(requireContext(), "Authentication Failed.", Toast.LENGTH_SHORT)
                        .show()
                }
            }
    }

    //Getting photo from Device
    private val launchIntent =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                val its = it.data
                uri = its?.data!!

                binding.image.setImageURI(uri)

            }
        }

    //Saving the image to Firebase Storage to get the downloadUrl for the photo.
    private fun saveImageToFirebase(email: String, username: String) {
        stRef = FirebaseStorage.getInstance().
            getReference("images/${binding.username.text.toString()}")
        stRef.putFile(uri)
            .addOnSuccessListener{
                Log.d("SignUpFragment", "Saved to Storage!")

                //Using this to get the DownloadUrl
                stRef.downloadUrl.addOnSuccessListener { uri->
                    val uid = FirebaseAuth.getInstance().uid
                    val signUpData = ChatData(email,
                        username.replaceFirstChar
                        { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() },
                        uid!!, uri.toString())
                    dbRef = FirebaseDatabase.getInstance().getReference("UserData/$username")
                    dbRef.setValue(signUpData)
                        .addOnSuccessListener {
                            Log.d("SignUpFragment", "Saved to database!")
                        }
                }
            }
    }
}
