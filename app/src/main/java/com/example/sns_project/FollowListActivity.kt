package com.example.sns_project

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.sns_project.databinding.ActivityFollowListBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.firestore.ktx.toObject

data class FollowData(
    val id: String? = null,
    val nickname : String? = null
)
//팔로잉 팔로워 리스트
class FollowListActivity : AppCompatActivity() {
    lateinit var binding: ActivityFollowListBinding
    var followerList = mutableListOf<FollowData>()
    var followingList = mutableListOf<FollowData>()
    var firestore : FirebaseFirestore? = null
    private lateinit var myEmail : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFollowListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        var uid = Firebase.auth.currentUser?.uid //현재 로그인한 사용자의 uid
        println("uid ======================================" + uid)
        myEmail = Firebase.auth.currentUser?.email.toString() //(이메일을 땄어)

//        binding.FollowerListTextView.setOnClickListener {
//            val followerAdapter = FollowAdapter(this, followerList)
//            binding.FollowListView.adapter = followerAdapter
//        }

//        binding.FollowingListTextView.setOnClickListener {
//            val followingAdapter = FollowAdapter(this, followingList)
//            binding.FollowListView.adapter = followingAdapter
//        }
    }

}
