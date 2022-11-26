package com.example.sns_project

data class Board (
    var boardKey :String? = "",
    val writer : String = "",
    val boardId: String = "",
    val imageUrl: String = "",
    val post: String? = "",
    val time: String = "",
    val uid: String = "",
    var comments : ArrayList<HashMap<String,String>>? = ArrayList<HashMap<String,String>>()
//    val likes : ArrayList<String>? = ArrayList<String>()
)

