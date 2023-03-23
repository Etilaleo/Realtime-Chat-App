package com.example.chatapp

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI.setupWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chatapp.adapters.MessageReceiveAdapter
import com.example.chatapp.adapters.MessageSendAdapter
import com.example.chatapp.databinding.FragmentChatScreenBinding
import com.example.chatapp.models.MessageData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.xwray.groupie.GroupieAdapter

class ChatScreen : Fragment() {

    private lateinit var binding : FragmentChatScreenBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var dbRef : DatabaseReference
    private lateinit var messageAdapter: GroupieAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentChatScreenBinding.inflate(layoutInflater)
        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as AppCompatActivity).supportActionBar?.hide()

        //Receiver details
        val data = arguments
        val username = data?.getString("username")
        val uid = data?.getString("uid")
        val mainUserEmail = data?.getString("mainUserEmail")

        val navController = findNavController()
        setupWithNavController(binding.toolbar, navController)
        binding.toolbar.title = username

        messageAdapter = GroupieAdapter()
        dbRef = FirebaseDatabase.getInstance().getReference("messages")
        auth = FirebaseAuth.getInstance()
        binding.messageRecycler.apply {
            adapter = messageAdapter
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
        }


        //Saving the messages to database.
        binding.sendBtn.setOnClickListener {
            if (binding.message.text.toString() != "") {
                auth = FirebaseAuth.getInstance()
                val messageData =
                    MessageData(
                        binding.message.text.toString(),
                        auth.uid.toString(),
                        mainUserEmail.toString(),
                        uid.toString(),
                        username.toString()
                    )

                dbRef = FirebaseDatabase.getInstance().getReference("messages").push()
                dbRef.setValue(messageData)
                    .addOnSuccessListener {
                        Log.d("MessageSent", "Message sent successfully")
                        binding.message.text?.clear()
                        binding.messageRecycler.scrollToPosition(messageAdapter.itemCount - 1)
                    }
            }
        }


        //Getting the messages from the database.
        dbRef.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val value = snapshot.getValue(MessageData::class.java)!!
                Log.d("chatScreen", value.toString())
                if (value.receiveUsername == username && value.sendUsername == mainUserEmail ||
                        value.receiveUsername == mainUserEmail && value.sendUsername == username) {
                    if (value.sendUid == auth.uid && value.receiveUid == uid) {
                        messageAdapter.add(MessageSendAdapter(value.message))
                    }
                    if (value.receiveUid == auth.uid || value.sendUid == uid) {
                        messageAdapter.add(MessageReceiveAdapter(value.message))
                    }
                }
                binding.messageRecycler.scrollToPosition(messageAdapter.itemCount -1)
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })

    }
}