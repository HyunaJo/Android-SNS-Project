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
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.*
import kotlin.collections.ArrayList

class MyBoardFragment : Fragment(R.layout.myboardfragment_layout) {
    lateinit var viewModel: SnsViewModel
    lateinit var binding: MyboardfragmentLayoutBinding
    lateinit var snsActivity: SnsActivity
    lateinit var myBoardList : ArrayList<Board>
    lateinit var selectedBoard:Board
    var userEmail = Firebase.auth.currentUser?.email.toString().split("@")[0]
    val database = Firebase.database("https://sns-project-dc395-default-rtdb.asia-southeast1.firebasedatabase.app/")

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

        binding.postWriter.text = nickname
        binding.postContent.text = selectedBoard.post

        var count = 0
        var likes = selectedBoard.likes
        if (likes != null) {
            for (i in likes) {
                println(i)
                if(i.value.equals("true")) count+=1;
            }
        }
        binding.likeCountText.text = "좋아요 " + count.toString() + "개"

        var boardId = selectedBoard.boardId
        var writer = selectedBoard.writer
        var boardIdentifier = writer + "_" + boardId //fkgnssla_0

        //내가 좋아요 했었는지, 안 했었는지 판단
        var flag = selectedBoard.likes!!.get(userEmail)
        if(flag.equals("true")) binding.likeButton.setImageResource(R.drawable.ic_like)
        else binding.likeButton.setImageResource(R.drawable.ic_unlike)

       //좋아요 버튼 클릭
        binding.likeButton.setOnClickListener() {
            System.out.println("좋아요 버튼 클릭! " + boardIdentifier)

            var boardRef = database.getReference("board")
            if(flag.equals("true")) { //좋아요 취소
                boardRef.child(boardIdentifier).child("likes").child(userEmail).setValue("false")
                binding.likeCountText.text = "좋아요 " + (--count).toString() + "개"
                selectedBoard.likes?.set(userEmail,"false")
                flag = "false"
                binding.likeButton.setImageResource(R.drawable.ic_unlike)
            }
            else { //좋아요 하기
                boardRef.child(boardIdentifier).child("likes").child(userEmail).setValue("true")
                binding.likeCountText.text = "좋아요 " + (++count).toString() + "개"
                selectedBoard.likes?.set(userEmail,"true")
                flag = "true"
                binding.likeButton.setImageResource(R.drawable.ic_like)
            }
        }

        Glide.with(snsActivity.baseContext).load(selectedBoard.imageUrl).into(binding.postImageView)
    }
}
