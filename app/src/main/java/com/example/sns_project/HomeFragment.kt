package com.example.sns_project

import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.onNavDestinationSelected
import com.bumptech.glide.Glide
import com.example.sns_project.databinding.HomefragmentLayoutBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.*
import kotlin.collections.ArrayList


class HomeFragment:Fragment(R.layout.homefragment_layout) {
    lateinit var viewModel:SnsViewModel
    lateinit var listviewAdapter :HomeListViewAdapter
    lateinit var listView: ListView
    lateinit var binding: HomefragmentLayoutBinding
    lateinit var snsActivity : SnsActivity
    var userEmail = Firebase.auth.currentUser?.email.toString().split("@")[0]

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = HomefragmentLayoutBinding.inflate(inflater,container,false)
        snsActivity = activity as SnsActivity
        viewModel = snsActivity.viewModel

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.top_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.searchFragment-> {
                        menuItem.onNavDestinationSelected(findNavController())
                        snsActivity.setToolbarTitle("")
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

        if(viewModel.myData.value!=null){
            listviewAdapter = HomeListViewAdapter(viewModel.myData.value!!.following!!)
            listView = binding.homeListView
            listView.adapter = listviewAdapter
        }

        viewModel.myData.observe(viewLifecycleOwner, Observer {
            val followings = viewModel.myData.value!!.following!!.filterNot { it == "" } as ArrayList<String>// ????????? ????????? ?????? ?????????
            listviewAdapter = HomeListViewAdapter(followings)
            listView = binding.homeListView
            listView.adapter = listviewAdapter
            listviewAdapter.notifyDataSetChanged()
        })
    }

    inner class HomeListViewAdapter(followings: ArrayList<String>) :BaseAdapter() {
        val database = Firebase.database("https://sns-project-dc395-default-rtdb.asia-southeast1.firebasedatabase.app/")
        val boardRef = database.reference.child("board")
        private var listViewItemList = ArrayList<Board>()
        private var followings = ArrayList<String>()


        init{
            this.followings = followings
            boardRef.addChildEventListener(object :
                ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    val board = snapshot.getValue(Board::class.java)
                    board!!.boardKey = snapshot.key.toString()

                    if(followings.contains(board.writer) || board.writer.equals(viewModel.userKey)){
                        listViewItemList.add(board)
                    }
                    listViewItemList.sortWith(Comparator { o1, o2 -> o2.time.compareTo(o1.time) })
                    viewModel.boardData = listViewItemList
                    notifyDataSetChanged()
                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                    val board = snapshot.getValue(Board::class.java)
                    board!!.boardKey = snapshot.key.toString()

                    for(item in listViewItemList){
                        if(item.boardKey.equals(board.boardKey)){
                            item.comments = board.comments
                            break
                        }
                    }
                    listViewItemList.sortWith { o1, o2 -> o2.time.compareTo(o1.time) }

                    viewModel.boardData = listViewItemList
                    notifyDataSetChanged()
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

        override fun getCount(): Int {
            return listViewItemList.size
        }

        override fun getItem(position: Int): Any {
            return listViewItemList[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
            var view = convertView
            val context = parent!!.context

            if (view == null) {
                val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                view = inflater.inflate(R.layout.board_item, parent, false)
            }


            val postID = view!!.findViewById<TextView>(R.id.postWriter) // ??? ??? ??????
            val postImgView = view.findViewById<ImageView>(R.id.postImageView)
            val likeCount = view.findViewById<TextView>(R.id.likeCountText) // ????????? ???
            val likeButton = view.findViewById<ImageButton>(R.id.likeButton) // ????????? ??????
            val commentButton= view.findViewById<ImageButton>(R.id.commentButton) // ?????? ??????
            val postID2= view.findViewById<TextView>(R.id.postID2) // ?????? ??????
            val postContent = view.findViewById<TextView>(R.id.postContent) // ?????????

            // ???????????? ????????? ??????
            val listViewItem = listViewItemList[position]
            var nickname = ""
            if(listViewItem.writer.equals(viewModel.userKey))
                nickname = viewModel.myData.value!!.nickname
            else{
                for(user in viewModel.followingUserData){
                    if(user.email.split("@")[0].equals(listViewItem.writer)){
                        nickname = user.nickname
                        break
                    }
                }
            }

            postID.text = nickname
            postID2.text = nickname
            postContent.text = listViewItem.post
            Glide.with(context).load(listViewItem.imageUrl).into(postImgView)

            commentButton.setOnClickListener {
                val boardKey = listViewItem.boardKey
                val navAction = HomeFragmentDirections.actionHomeFragmentToCommentFragment(boardKey!!, nickname, listViewItem.post!!)
                findNavController().navigate(navAction)
            }

            //????????? ?????? ??????
            var boardKey = listViewItem.boardKey
            var count = 0
            val boardRef = database.getReference("board")
            boardRef.child(boardKey!!).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                }
                override fun onDataChange(snapshot: DataSnapshot) {
                    var board = snapshot.getValue(Board::class.java)!!

                    //?????? ????????? ????????????, ??? ???????????? ??????
                    var flag = board.likes!!.get(userEmail)
                    if(flag.equals("true")) likeButton.setImageResource(R.drawable.ic_like)
                    else likeButton.setImageResource(R.drawable.ic_unlike)

                    //????????? ?????? ??????
                    likeButton.setOnClickListener() {
                        if(flag.equals("true")) { //????????? ??????
                            boardRef.child(boardKey).child("likes").child(userEmail).setValue("false")
                            board.likes?.set(userEmail,"false")
                            flag = "false"
                            likeButton.setImageResource(R.drawable.ic_unlike)
                        }
                        else { //????????? ??????
                            boardRef.child(boardKey).child("likes").child(userEmail).setValue("true")
                            board.likes?.set(userEmail,"true")
                            flag = "true"
                            likeButton.setImageResource(R.drawable.ic_like)
                        }
                    }
                }
            })

            boardRef.child(boardKey).child("likes").addChildEventListener(object :
                ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    if(snapshot.getValue()?.equals("true") == true) count+=1
                    likeCount.text = "????????? " + count.toString() + "???"
                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                    if(snapshot.getValue()?.equals("true") == true) count+=1
                    else if(snapshot.getValue()?.equals("false") == true) count-=1;
                    likeCount.text = "????????? " + count.toString() + "???"
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

            boardRef.addChildEventListener(object :
                ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    if(snapshot.getValue()?.equals("true") == true) count+=1
                    likeCount.text = "????????? " + count.toString() + "???"
                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
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

            return view
        }
    }
}