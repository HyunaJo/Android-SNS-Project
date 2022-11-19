package com.example.sns_project

import android.content.Context
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.sns_project.databinding.FollowItemBinding
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import de.hdodenhof.circleimageview.CircleImageView

//class FollowData(val profile : String, val ID: String, val username : String)

//프로필을 담는다.
//data class FollowData(val ID: String, val username : String)
class FollowAdapter(val context : Context, private val FollowDataList : MutableList<FollowData>) : BaseAdapter() {
    override fun getCount(): Int = FollowDataList.size
    override fun getItem(p0: Int): FollowData = FollowDataList[p0]
    override fun getItemId(p0: Int): Long = 0L
    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
        val binding : FollowItemBinding = FollowItemBinding.inflate(LayoutInflater.from(context))
        val followListEmailTextView = binding.FollowListEmailTextView
        val followListNameTextView = binding.FollowListNameTextView
        val personalProfileImg = binding.personalProfileImg
        val rootRef = Firebase.storage.reference
        val ref = rootRef.child("images/default.jpg")
        ref.getBytes(Long.MAX_VALUE).addOnCompleteListener {
            if(it.isSuccessful) {
                val bmp = BitmapFactory.decodeByteArray(it.result, 0, it.result.size)
                personalProfileImg.setImageBitmap(bmp)
            }
        }
        val follow = FollowDataList[p0]
        followListEmailTextView.text = follow.id
//        followListNameTextView.text = follow.username;
        return binding.root
    }
}