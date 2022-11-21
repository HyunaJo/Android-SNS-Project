package com.example.sns_project

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import com.example.sns_project.databinding.FragmentLayoutBinding
import com.example.sns_project.databinding.MyfeedfragmentLayoutBinding
import com.example.sns_project.databinding.UserfeedfragmentLayoutBinding

class UserFeedFragment: Fragment(R.layout.userfeedfragment_layout) {
    lateinit var binding: UserfeedfragmentLayoutBinding
    lateinit var viewModel:SnsViewModel
    var selectedName = ""
    var OriginBlueColor = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = UserfeedfragmentLayoutBinding.inflate(inflater,container,false)
        return binding.root
    }

    @SuppressLint("ResourceAsColor")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val snsActivity = activity as SnsActivity
        snsActivity.findViewById<TextView>(R.id.toolbarTextView).text=""
        val arg:UserFeedFragmentArgs by navArgs()
        binding = UserfeedfragmentLayoutBinding.bind(view)
        viewModel = snsActivity.viewModel
        selectedName = arg.selectUserName
        OriginBlueColor = ContextCompat.getColor(snsActivity.baseContext, R.color.OriginBlue)

        viewModel = snsActivity.viewModel
        viewModel.searchUserData.observe(viewLifecycleOwner, Observer {
            binding.usernameText.text = selectedName // 사용자 닉네임
            binding.postNumberText.text = viewModel.searchUserData.value!!.boardList!!.filterNot{ it == null }.size.toString()
            binding.followingNumberText.text = viewModel.searchUserData.value!!.following!!.filterNot{ it == null }.size.toString() // 팔로잉 수
            binding.followerNumberText.text = viewModel.searchUserData.value!!.follower!!.filterNot{ it == null }.size.toString() // 팔로워 수
        })


        setFollowButton()

        binding.followButton.setOnClickListener {
            if(viewModel.myData.value!!.following!!.contains(viewModel.searchUserKey)) {// 팔로우하고 있는 사용자인 경우
                System.out.println("팔로우 취소")
                viewModel.unfollowUser()
                binding.followButton.setBackgroundResource(R.drawable.btn_blue)
                binding.followButton.setTextColor(Color.WHITE)
                binding.followButton.text = "팔로우"
            }
            else{ // 팔로우 안 한 사용자인 경우
                System.out.println("팔로우 ")
                viewModel.followUser()
                binding.followButton.setBackgroundResource(R.drawable.btn_clicked)
                binding.followButton.setTextColor(OriginBlueColor)
                binding.followButton.text = "팔로잉"
            }
        }
    }

    fun setFollowButton(){
        if(viewModel.myData.value!!.following!!.contains(viewModel.searchUserKey)){ // 팔로우하고 있는 사용자인 경우
            System.out.println("팔로우하고 있는 사용자")
            binding.followButton.setBackgroundResource(R.drawable.btn_clicked)
            binding.followButton.setTextColor(OriginBlueColor)
            binding.followButton.text = "팔로잉"
        }
        else{ // 팔로우 안 한 사용자인 경우
            System.out.println("팔로우 안하고 있는 사용자")
            binding.followButton.setBackgroundResource(R.drawable.btn_blue)
            binding.followButton.setTextColor(Color.WHITE)
            binding.followButton.text = "팔로우"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        System.out.println("*************** onDestroyView *******************")
        viewModel.searchUserData.value = User()
    }
}