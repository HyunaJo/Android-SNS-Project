package com.example.sns_project

import android.os.Bundle
import android.view.*
import android.widget.ListView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.sns_project.databinding.BoardItemBinding
import com.example.sns_project.databinding.MyboardfragmentLayoutBinding
import java.util.*
import kotlin.collections.ArrayList

class MyBoardFragment : Fragment(R.layout.myboardfragment_layout) {
    lateinit var viewModel: SnsViewModel
    lateinit var listView: ListView
    lateinit var binding: MyboardfragmentLayoutBinding
    lateinit var snsActivity: SnsActivity
    lateinit var myBoardList : ArrayList<Board>
    lateinit var selectedBoard:Board

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = MyboardfragmentLayoutBinding.inflate(inflater, container, false)
        snsActivity = activity as SnsActivity
        snsActivity.findViewById<TextView>(R.id.toolbarTextView).text=""
        viewModel = snsActivity.viewModel
        myBoardList = viewModel.myBoardData

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val arg: MyBoardFragmentArgs by navArgs()
        val boardIdx = arg.boardIdx
        selectedBoard = myBoardList[boardIdx]
        val nickname = arg.nickname
        binding.postID.text = nickname
//        binding.postID2.text = nickname
        binding.postContent.text = selectedBoard.post
//        binding.likeCountText.text = "좋아요 "+selectedBoard.likes!!.size.toString()+"개"

        Glide.with(snsActivity.baseContext).load(selectedBoard.imageUrl).into(binding.postImageView)
    }
}
