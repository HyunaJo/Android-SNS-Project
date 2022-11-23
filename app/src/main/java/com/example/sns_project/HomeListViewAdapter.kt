package com.example.sns_project

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageButton
import android.widget.TextView
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class HomeListViewAdapter :BaseAdapter() {
    val database = Firebase.database("https://sns-project-dc395-default-rtdb.asia-southeast1.firebasedatabase.app/")
    val boardRef = database.reference.child("board")
    private var listViewItemList = ArrayList<Board>()

    init{
        boardRef.orderByChild("writer").equalTo("hansung").addChildEventListener(object :
            ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                System.out.println("==============================================")
                val board = snapshot.getValue(Board::class.java)
                listViewItemList.add(board!!)
                notifyDataSetChanged()
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                val board = snapshot.getValue(Board::class.java)
                listViewItemList.add(board!!)
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
        System.out.println("getItemId =====================> $position")
        return 0
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
        var view = convertView
        val context = parent!!.context
//
        if (view == null) {
            val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            view = inflater.inflate(R.layout.board_item, parent, false)
        }

        val postID = view!!.findViewById<TextView>(R.id.postID) // 글 쓴 사람
        val likeCount = view.findViewById<TextView>(R.id.likeCountText) // 좋아요 수
        val likeButton = view.findViewById<ImageButton>(R.id.likeButton) // 좋아요 버튼
        val commentButton= view.findViewById<ImageButton>(R.id.commentButton) // 댓글 버튼
        val postID2= view.findViewById<TextView>(R.id.postID2) // 댓글 버튼
        val postContent = view.findViewById<TextView>(R.id.postContent) // 게시글

        val listViewItem = listViewItemList[position]

        // 아이템에 데이터 반영
        postID.text = listViewItem.writer
        postID2.text = listViewItem.writer
        postContent.text = listViewItem.post
        likeCount.text = "좋아요 "+listViewItem.likes!!.size.toString()+"개"

        likeButton.setOnClickListener {
            System.out.println("좋아요 클릭")
        }
        commentButton.setOnClickListener {
            System.out.println("댓글 클릭")
        }

        return view
    }

    fun changeArrayList(boards:ArrayList<Board>){
        listViewItemList = boards
    }

    // 아이템 데이터 추가를 위한 함수
    fun addItem(board : Board) {
        listViewItemList.add(board)
    }
}