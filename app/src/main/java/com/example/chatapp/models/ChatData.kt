package com.example.chatapp.models

data class ChatData(
    val email : String,
    val username : String,
    val uid : String,
    val imageUri : String
) {
    constructor() : this("", "", "", "")
}