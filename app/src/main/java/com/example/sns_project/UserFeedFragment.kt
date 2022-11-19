package com.example.sns_project

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val snsActivity = activity as SnsActivity
        snsActivity.findViewById<TextView>(R.id.toolbarTextView).text=""
        val arg:UserFeedFragmentArgs by navArgs()
        binding = UserfeedfragmentLayoutBinding.bind(view)
        viewModel = snsActivity.viewModel
        selectedName = arg.selectUserName

        viewModel = snsActivity.viewModel
        viewModel.searchUserData.observe(viewLifecycleOwner, Observer {
            binding.usernameText.text = selectedName // 사용자 닉네임
            binding.postNumberText.text = viewModel.searchUserData.value!!.boardList!!.size.toString()
            binding.followingNumberText.text = viewModel.searchUserData.value!!.following!!.size.toString() // 팔로잉 수
            binding.followerNumberText.text = viewModel.searchUserData.value!!.follower!!.size.toString() // 팔로워 수
        })
        viewModel.getSearchUserInfo(selectedName)
//

    }
}