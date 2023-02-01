package com.example.chatapp

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chatapp.adapters.ChatAdapter
import com.example.chatapp.databinding.FragmentMainScreenBinding
import com.example.chatapp.models.ChatData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class MainScreen : Fragment() {

    private lateinit var binding: FragmentMainScreenBinding
    lateinit var auth: FirebaseAuth
    private lateinit var dbRef : DatabaseReference
    lateinit var chatList : MutableList<ChatData>
    lateinit var chatAdapter : ChatAdapter
    lateinit var mainUserList : MutableList<ChatData>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentMainScreenBinding.inflate(layoutInflater)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()

        val actionBar = (activity as AppCompatActivity).supportActionBar!!
        actionBar.title = "Chats"
        actionBar.show()


        //This is for the menu state.
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                // Add menu items here
                menuInflater.inflate(R.menu.main_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                // Handle the menu selection
                return when (menuItem.itemId) {
                    R.id.logout -> {
                        FirebaseAuth.getInstance().signOut()
                        findNavController().navigate(R.id.action_mainScreen_to_signIn2)

                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

        //Adapter for the display of the chats.
        chatList = mutableListOf()
        dbRef = FirebaseDatabase.getInstance().getReference("UserData")
        chatAdapter = ChatAdapter(chatList, requireContext())
        binding.chatRecycler.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
            adapter = chatAdapter
        }

        getChatData()

        mainUserList = mutableListOf()

        chatAdapter.chatItemClick = {
            val bundle = Bundle()
            bundle.putString("username", it.username)
            bundle.putString("uid", it.uid)
            bundle.putString("mainUserEmail", mainUserList[0].username)
            Navigation.findNavController(view).navigate(R.id.action_mainScreen_to_chatScreen, bundle)
        }


    }

    private fun getChatData() {
        // Read from the database
        dbRef.addValueEventListener(object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                if (dataSnapshot.exists()) {
                    dataSnapshot.children.forEach{
                        val value = it.getValue(ChatData::class.java)
                        Log.d("SignUpFragment", value.toString())
                        if (auth.uid!! != value?.uid && value !in chatList) {
                            chatList.add(value!!)
                        }
                        else {
                            mainUserList.add(value!!)
                        }
                    }
                }
                chatAdapter.notifyDataSetChanged()
            }


            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.d("MainActivity", "Error: $error")
            }
        })
    }
}


