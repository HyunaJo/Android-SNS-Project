package com.example.sns_project

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.sns_project.databinding.MyfeedfragmentLayoutBinding
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class MyFeedFragment : Fragment(R.layout.myfeedfragment_layout) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val snsActivity = activity as SnsActivity
//        snsActivity.hideUpButton()
        val binding = MyfeedfragmentLayoutBinding.bind(view)      //binding 따기,
        val rootRef = Firebase.storage.reference
        val ref = rootRef.child("images/default.jpg")
        ref.getBytes(Long.MAX_VALUE).addOnCompleteListener {
            if(it.isSuccessful) {
                val bmp = BitmapFactory.decodeByteArray(it.result, 0, it.result.size)
                val MyFeedProfile = binding.profileImg //findviewById로 해야할 수 도 있음
                MyFeedProfile.setImageBitmap(bmp)
            }
        }

        binding.profileImg.setOnClickListener {}    //프로필 이미지를 변경해야할 경우,

        //Intent로 following을 눌렀다는 표시를 FollowListActivity에서 알도록 Intent에 값을 실어서 보내야 할거 같으!
        binding.followingNumberText.setOnClickListener {      //팔로워 팔로잉 activity
            val intent = Intent(getActivity(), FollowListActivity::class.java)
            startActivity(intent)
        }

        //Intent로 following을 눌렀다는 표시를 FollowListActivity에서 알도록 Intent에 값을 실어서 보내야 할거 같으!
        binding.followerNumberText.setOnClickListener {       //팔로워 팔로잉 activity
            val intent = Intent(getActivity(), FollowListActivity::class.java)
            startActivity(intent)
        }
    }
}