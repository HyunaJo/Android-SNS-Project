package com.example.sns_project

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.sns_project.databinding.MyfeedfragmentLayoutBinding
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class MyFeedFragment : Fragment(R.layout.myfeedfragment_layout) {
    lateinit var viewModel:SnsViewModel
    lateinit var binding:MyfeedfragmentLayoutBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = MyfeedfragmentLayoutBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val snsActivity = activity as SnsActivity
        binding = MyfeedfragmentLayoutBinding.bind(view)      //binding 따기,
        val userNameText = binding.usernameText
        val followingNumberText = binding.followingNumberText
        val followerNumberText = binding.followerNumberText
        val postNumberText = binding.postNumberText

        viewModel = snsActivity.viewModel
        viewModel.myData.observe(viewLifecycleOwner, Observer {
            userNameText.text = it.nickname // 사용자 닉네임
            postNumberText.text = it.boardList!!.filterNot{ it == "" }.size.toString()
            followingNumberText.text = it.following!!.filterNot{ it == "" }.size.toString() // 팔로잉 수
            followerNumberText.text = it.follower!!.filterNot{ it == "" }.size.toString() // 팔로워 수
        })

        binding.profileImg.setOnClickListener {}    //프로필 이미지를 변경해야할 경우,

        //Intent로 following을 눌렀다는 표시를 FollowListActivity에서 알도록 Intent에 값을 실어서 보내야 할거 같으!
        binding.followerNumberText.setOnClickListener {       //팔로워 팔로잉 activity
//            val intent = Intent(getActivity(), FollowListActivity::class.java)
//            startActivity(intent)
            System.out.println("click")
            val tabIdx = 0
            val navAction = MyFeedFragmentDirections.actionMyFeedFragmentToFollowListFragment(tabIdx)
            findNavController().navigate(navAction)
//            snsActivity.supportFragmentManager.beginTransaction().replace(R.id.fragment_container,FollowerListFragment()).commit()

        }

        binding.FollowerTextView.setOnClickListener {       //팔로워 팔로잉 activity
//            val intent = Intent(getActivity(), FollowListActivity::class.java)
//            startActivity(intent)
            System.out.println("click")
            val tabIdx = 0
            val navAction = MyFeedFragmentDirections.actionMyFeedFragmentToFollowListFragment(tabIdx)
            findNavController().navigate(navAction)
//            snsActivity.supportFragmentManager.beginTransaction().replace(R.id.fragment_container,FollowerListFragment()).commit()
        }


        //Intent로 following을 눌렀다는 표시를 FollowListActivity에서 알도록 Intent에 값을 실어서 보내야 할거 같으!
        binding.followingNumberText.setOnClickListener {      //팔로워 팔로잉 activity
//            val intent = Intent(getActivity(), FollowListActivity::class.java)
//            startActivity(intent)
            System.out.println("click")
            val tabIdx = 1
            val navAction = MyFeedFragmentDirections.actionMyFeedFragmentToFollowListFragment(tabIdx)
            findNavController().navigate(navAction)
//            snsActivity.supportFragmentManager.beginTransaction().replace(R.id.fragment_container,FollowingListFragment()).commit()
        }
        binding.FollowingTextView.setOnClickListener {      //팔로워 팔로잉 activity
//            val intent = Intent(getActivity(), FollowListActivity::class.java)
//            startActivity(intent)
            System.out.println("click")
            val tabIdx = 1
            val navAction = MyFeedFragmentDirections.actionMyFeedFragmentToFollowListFragment(tabIdx)
            findNavController().navigate(navAction)
//            snsActivity.supportFragmentManager.beginTransaction().replace(R.id.fragment_container,FollowingListFragment()).commit()
        }


    }
}