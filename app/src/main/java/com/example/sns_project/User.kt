package com.example.sns_project

data class User (
    val birthday: String = "",
    val email: String = "",
    val nickname: String = "",
    val password: String = "",
    val profileimage: String = "gs://sns-project-dc395.appspot.com/images/default.png",
    val boardList: ArrayList<String>? = ArrayList(),
    val following: ArrayList<String>? = ArrayList(),
    val follower: ArrayList<String>? = ArrayList(),
    val imageList: ArrayList<String>? = ArrayList()
)