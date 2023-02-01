package com.example.chatapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.R
import com.example.chatapp.models.ChatData
import com.squareup.picasso.Picasso

class ChatAdapter (
    private var chatList : List<ChatData>,
    private val context: Context
    //this will now take this data to set everything
) : RecyclerView.Adapter<ChatAdapter.ChatViewHolder>(){

    var chatItemClick : ((ChatData) -> Unit)? = null


    //Every Adapter of the recyclerview must hold an inner class which will be the view holder class
    inner class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.username)
        var image: ImageView = itemView.findViewById(R.id.userImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.contacts_display, parent, false)
        return ChatViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        holder.name.text = chatList[position].username
        Picasso.with(context).load(chatList[position].imageUri).into(holder.image)

        holder.itemView.setOnClickListener {
            chatItemClick?.invoke(ChatData(chatList[position].email, holder.name.text.toString(), chatList[position].uid, ""))
        }
    }

    override fun getItemCount(): Int {
        return chatList.size
    }
}