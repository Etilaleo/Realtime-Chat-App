package com.example.chatapp.adapters

import android.widget.TextView
import com.example.chatapp.R
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item

class MessageSendAdapter(private val text : String) : Item<GroupieViewHolder>() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.findViewById<TextView>(R.id.sending).text = text
    }

    override fun getLayout(): Int {
        return R.layout.sending
    }

}