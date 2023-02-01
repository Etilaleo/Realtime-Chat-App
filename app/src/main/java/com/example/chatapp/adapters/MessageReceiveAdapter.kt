package com.example.chatapp.adapters

import android.widget.TextView
import com.example.chatapp.R
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item

class MessageReceiveAdapter(private val text: String) :Item<GroupieViewHolder>() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.findViewById<TextView>(R.id.receiving).text = text
    }

    override fun getLayout(): Int {
        return R.layout.recieving
    }

}