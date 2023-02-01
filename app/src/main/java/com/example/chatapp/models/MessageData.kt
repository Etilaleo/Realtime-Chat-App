package com.example.chatapp.models

data class MessageData(
    val message : String,
    val sendUid : String,
    val sendUsername : String,
    val receiveUid: String,
    val receiveUsername : String
){
    constructor() : this ("", "", "", "", "")
}
