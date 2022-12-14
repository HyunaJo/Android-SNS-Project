package com.example.sns_project

import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.sns_project.databinding.MyboardfragmentLayoutBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlin.collections.ArrayList

class UserBoardFragment : Fragment(R.layout.myboardfragment_layout) {
    lateinit var viewModel: SnsViewModel
    lateinit var binding: MyboardfragmentLayoutBinding
    lateinit var snsActivity: SnsActivity
    lateinit var myBoardList : ArrayList<String>
    lateinit var selectedBoard:Board
    var userEmail = Firebase.auth.currentUser?.email.toString().split("@")[0]
    val database = Firebase.database("https://sns-project-dc395-default-rtdb.asia-southeast1.firebasedatabase.app/")
    lateinit var board: Board
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = MyboardfragmentLayoutBinding.inflate(inflater, container, false)
        snsActivity = activity as SnsActivity
        snsActivity.findViewById<TextView>(R.id.toolbarTextView).text=""
        viewModel = snsActivity.viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val arg: UserBoardFragmentArgs by navArgs()
        val boardIdx = arg.boardIdx

        var selectedName = arg.selectUserName //해당 게시물 글쓴이
        binding.postWriter.text = selectedName


        viewModel.getSearchUserInfo(selectedName)
        myBoardList = viewModel.searchUserData.value!!.boardList!!
        val size = myBoardList.size
        val selectedBoardName = myBoardList.get(size-(boardIdx+1)) //선택된 게시물 식별자

        val commentButton = binding.commentButton


        //게시물 식별자에 해당하는 게시물 가져오기
        var count = 0
        val boardRef = database.getReference("board")
        boardRef.child(selectedBoardName!!).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
            }
            override fun onDataChange(snapshot: DataSnapshot) {
                board = snapshot.getValue(Board::class.java)!!
                if (board != null) {
                    binding.postContent.text = board.post
                    Glide.with(snsActivity.baseContext).load(board.imageUrl).into(binding.postImageView)
                }

                //내가 좋아요 했었는지, 안 했었는지 판단
                var flag = board.likes!!.get(userEmail)
                if(flag.equals("true")) binding.likeButton.setImageResource(R.drawable.ic_like)
                else binding.likeButton.setImageResource(R.drawable.ic_unlike)

                //좋아요 버튼 클릭
                binding.likeButton.setOnClickListener() {
                    var boardRef = database.getReference("board")
                    if(flag.equals("true")) { //좋아요 취소
                        boardRef.child(selectedBoardName).child("likes").child(userEmail).setValue("false")
                        board.likes?.set(userEmail,"false")
                        flag = "false"
                        binding.likeButton.setImageResource(R.drawable.ic_unlike)
                    }
                    else { //좋아요 하기
                        boardRef.child(selectedBoardName).child("likes").child(userEmail).setValue("true")
                        board.likes?.set(userEmail,"true")
                        flag = "true"
                        binding.likeButton.setImageResource(R.drawable.ic_like)
                    }
                }

                commentButton.setOnClickListener {
                    val navAction = UserBoardFragmentDirections.actionUserBoardFragment3ToCommentFragment(
                        selectedBoardName, selectedName, board.post!!)
                    findNavController().navigate(navAction)
                }
            }
        })



        boardRef.child(selectedBoardName).child("likes").addChildEventListener(object :
            ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                if(snapshot.getValue()?.equals("true") == true) count+=1
                binding.likeCountText.text = "좋아요 " + count.toString() + "개"
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                if(snapshot.getValue()?.equals("true") == true) count+=1
                else if(snapshot.getValue()?.equals("false") == true) count-=1;
                binding.likeCountText.text = "좋아요 " + count.toString() + "개"
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {

            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                TODO("Not yet implemented")
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }
}
