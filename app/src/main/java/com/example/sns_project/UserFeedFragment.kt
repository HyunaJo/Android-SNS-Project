package com.example.sns_project

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.sns_project.databinding.UserfeedfragmentLayoutBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.*

class UserFeedFragment: Fragment(R.layout.userfeedfragment_layout) {
    lateinit var binding: UserfeedfragmentLayoutBinding
    lateinit var viewModel:SnsViewModel
    var selectedName = ""
    var OriginBlueColor = 0
    var userEmail = ""
    lateinit var snsActivity: SnsActivity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = UserfeedfragmentLayoutBinding.inflate(inflater,container,false)
        snsActivity = activity as SnsActivity
        viewModel = snsActivity.viewModel

        return binding.root
    }

    @SuppressLint("ResourceAsColor")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val snsActivity = activity as SnsActivity
        snsActivity.findViewById<TextView>(R.id.toolbarTextView).text=""
        val arg:UserFeedFragmentArgs by navArgs()
        binding = UserfeedfragmentLayoutBinding.bind(view)

        selectedName = arg.selectUserName

        OriginBlueColor = ContextCompat.getColor(snsActivity.baseContext, R.color.OriginBlue)

        viewModel.searchUserData.observe(viewLifecycleOwner, Observer {
            binding.usernameText.text = selectedName // 사용자 닉네임
            userEmail = viewModel.searchUserData.value!!.email!!.split("@")[0] //해당 피드의 사용자 이메일 앞자리
            binding.postNumberText.text = viewModel.searchUserData.value!!.boardList!!.filterNot{ it == "" }.size.toString()
            binding.followingNumberText.text = viewModel.searchUserData.value!!.following!!.filterNot{ it == "" }.size.toString() // 팔로잉 수
            binding.followerNumberText.text = viewModel.searchUserData.value!!.follower!!.filterNot{ it == "" }.size.toString() // 팔로워 수
        })

        binding.followerNumberText.setOnClickListener {       //팔로워 팔로잉 activity
            val tabIdx = 0
            val navAction = UserFeedFragmentDirections.actionUserFeedFragmentToFollowListFragment(viewModel.searchUserKey, tabIdx)
            findNavController().navigate(navAction)
        }

        binding.FollowerTextView.setOnClickListener {       //팔로워 팔로잉 activity
            val tabIdx = 0
            val navAction = UserFeedFragmentDirections.actionUserFeedFragmentToFollowListFragment(viewModel.searchUserKey,tabIdx)
            findNavController().navigate(navAction)
        }

        binding.followingNumberText.setOnClickListener {      //팔로워 팔로잉 activity
            val tabIdx = 1
            val navAction = UserFeedFragmentDirections.actionUserFeedFragmentToFollowListFragment(viewModel.searchUserKey,tabIdx)
            findNavController().navigate(navAction)
        }
        binding.FollowingTextView.setOnClickListener {      //팔로워 팔로잉 activity
            val tabIdx = 1
            val navAction = UserFeedFragmentDirections.actionUserFeedFragmentToFollowListFragment(viewModel.searchUserKey,tabIdx)
            findNavController().navigate(navAction)
        }

        viewModel.isFollowingUser.observe(viewLifecycleOwner, Observer {
            if(viewModel.isFollowingUser.value == true){
                binding.followButton.setBackgroundResource(R.drawable.btn_clicked)
                binding.followButton.setTextColor(OriginBlueColor)
                binding.followButton.text = "팔로잉"
            }
            else{
                binding.followButton.setBackgroundResource(R.drawable.btn_blue)
                binding.followButton.setTextColor(Color.WHITE)
                binding.followButton.text = "팔로우"
            }
        })

        viewModel.getSearchUserInfo(selectedName)

        binding.followButton.setOnClickListener {
            if(viewModel.myData.value!!.following!!.contains(viewModel.searchUserKey)) {// 팔로우하고 있는 사용자인 경우
                viewModel.unfollowUser()
            }
            else{ // 팔로우 안 한 사용자인 경우
                viewModel.followUser()
            }
        }

        binding.recyclerview.adapter = ImageFragmentRecyclerAdapter()
    }

    inner class ImageFragmentRecyclerAdapter: RecyclerView.Adapter<ImageFragmentRecyclerAdapter.ViewHolder>() {
        val database = Firebase.database("https://sns-project-dc395-default-rtdb.asia-southeast1.firebasedatabase.app/")
        var boardList : ArrayList<Board> = ArrayList<Board>()
        var imageList: ArrayList<String> = arrayListOf<String>()
        var userBoardList : ArrayList<Board> = ArrayList()
        init {
            // 친구 사진의 url만 찾아서 imageList에 저장
            val myRef = database.getReference("board")
            myRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                }
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (userSnapShot in snapshot.children) {
                        val board = userSnapShot.getValue(Board::class.java)
                        board!!.boardKey = board.boardId+"_"+board.writer
                        boardList.add(board)
                    }

                    //날짜순 정렬
                    if (boardList.size > 1) {
                        boardList.sortWith(Comparator { o1, o2 -> o2.time.compareTo(o1.time) })
                    }
                    for(i in boardList) {
                        if(i.writer.equals(userEmail)) {
                            imageList.add(i.imageUrl)
                            userBoardList.add(i)
                        }
                    }
                    notifyDataSetChanged()
                }
            })
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.myfeedimage_item, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val width = resources.displayMetrics.widthPixels / 3
            holder.profileImage.layoutParams = LinearLayoutCompat.LayoutParams(width, width)
            Glide.with(holder.itemView.context).load(imageList[position]).into(holder.profileImage)
        }

        inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
            val profileImage = itemView.findViewById<ImageView>(R.id.MyFeedGridImage)
            init{
                profileImage.setOnClickListener{
                    val navAction = UserFeedFragmentDirections.actionUserFeedFragmentToUserBoardFragment3(selectedName, adapterPosition)
                    findNavController().navigate(navAction)
                }
            }
        }

        override fun getItemCount(): Int {
            return imageList.size
        }
    }
}