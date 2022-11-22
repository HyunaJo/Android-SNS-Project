package com.example.sns_project

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.sns_project.databinding.MyfeedfragmentLayoutBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage

class MyFeedFragment : Fragment(R.layout.myfeedfragment_layout) {
    lateinit var viewModel:SnsViewModel
    lateinit var binding:MyfeedfragmentLayoutBinding
    lateinit var imageUrl:Uri

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = MyfeedfragmentLayoutBinding.inflate(inflater,container,false)
        binding.recyclerview?.adapter = ImageFragmentRecyclerAdapter()
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

        binding.followerNumberText.setOnClickListener {       //팔로워 팔로잉 activity
            System.out.println("click")
            val tabIdx = 0
            val navAction = MyFeedFragmentDirections.actionMyFeedFragmentToFollowListFragment(tabIdx)
            findNavController().navigate(navAction)
        }

        binding.FollowerTextView.setOnClickListener {       //팔로워 팔로잉 activity
            System.out.println("click")
            val tabIdx = 0
            val navAction = MyFeedFragmentDirections.actionMyFeedFragmentToFollowListFragment(tabIdx)
            findNavController().navigate(navAction)
        }


        //Intent로 following을 눌렀다는 표시를 FollowListActivity에서 알도록 Intent에 값을 실어서 보내야 할거 같으!
        binding.followingNumberText.setOnClickListener {      //팔로워 팔로잉 activity
            System.out.println("click")
            val tabIdx = 1
            val navAction = MyFeedFragmentDirections.actionMyFeedFragmentToFollowListFragment(tabIdx)
            findNavController().navigate(navAction)
        }
        binding.FollowingTextView.setOnClickListener {      //팔로워 팔로잉 activity
            System.out.println("click")
            val tabIdx = 1
            val navAction = MyFeedFragmentDirections.actionMyFeedFragmentToFollowListFragment(tabIdx)
            findNavController().navigate(navAction)
        }

        binding.signoutButton.setOnClickListener {
            Firebase.auth.signOut()
            val intent = Intent(snsActivity, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

            snsActivity.finish()
            startActivity(intent)
        }
    }

    inner class ImageFragmentRecyclerAdapter: RecyclerView.Adapter<ImageFragmentRecyclerAdapter.ViewHolder>() {
        val database = Firebase.database("https://sns-project-dc395-default-rtdb.asia-southeast1.firebasedatabase.app/")
        var imageList: ArrayList<String> = arrayListOf<String>()
        var imageUrl: String = ""
        init {
            // 내 사진의 url만 찾아서 imageList에 저장
            var userList = java.util.ArrayList<User>()
            val myRef = database.getReference("users")
            myRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                }
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (userSnapShot in snapshot.children) {
                        val user = userSnapShot.getValue(User::class.java)
                        userList.add(user!!)
                    }
                    var userEmail = Firebase.auth.currentUser?.email.toString()
                    for(i in userList) {
                        if(i.email.equals(userEmail)) {
                            imageList = i.imageList!!
                            imageList.reverse()
                            System.out.println(imageList)
                            break;
                        }
                    }
                    notifyDataSetChanged()
                }
            })
            System.out.println(imageList)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.myfeedimage_item, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val width = resources.displayMetrics.widthPixels / 3
            holder.profileImage.layoutParams = LinearLayoutCompat.LayoutParams(width, width)
            Glide.with(holder.itemView.context).load(imageList[position]).into(holder.profileImage)
            System.out.println(imageList.size)
        }

        inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
            val profileImage = itemView.findViewById<ImageView>(R.id.MyFeedGridImage)
        }

        override fun getItemCount(): Int {
            return imageList.size
        }
    }
}