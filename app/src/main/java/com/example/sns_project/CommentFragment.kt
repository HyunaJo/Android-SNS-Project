package com.example.sns_project

import android.content.Context
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.example.sns_project.databinding.CommentfragmentLayoutBinding
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class CommentFragment : Fragment(R.layout.commentfragment_layout){
    val database = Firebase.database("https://sns-project-dc395-default-rtdb.asia-southeast1.firebasedatabase.app/")
    val boardRef = database.reference.child("board")

    lateinit var arrayAdapter:CommentListAdapter
    lateinit var listView: ListView
    lateinit var viewModel:SnsViewModel
    lateinit var binding:CommentfragmentLayoutBinding

    var listViewItemList = ArrayList<HashMap<String,String>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = CommentfragmentLayoutBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val snsActivity = activity as SnsActivity
        snsActivity.findViewById<TextView>(R.id.toolbarTextView).text=""

        listView = binding.commentListView
        viewModel = snsActivity.viewModel

        val arg:CommentFragmentArgs by navArgs()
        val boardKey = arg.boardKey
        val nickname = arg.nickname
        val postContent = arg.postContent

        val nicknameTextView = binding.postWriter
        val postContentTextView = binding.postContent
        val addCommentButton = binding.addCommentButton
        val commentEditText = binding.commentInputEditText

        nicknameTextView.text = nickname
        postContentTextView.text = postContent

        arrayAdapter = CommentListAdapter(boardKey)
        listView.adapter = arrayAdapter
        arrayAdapter.notifyDataSetChanged()

        addCommentButton.setOnClickListener{
            val data = hashMapOf(
                "user" to viewModel.userKey,
                "content" to commentEditText.text.toString()
            )

            boardRef.child(boardKey).child("comments").child(listViewItemList.size.toString()).setValue(data)

            commentEditText.text!!.clear()
            commentEditText.clearFocus()
            val inputMethodManager = snsActivity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    inner class CommentListAdapter(boardKey:String) : BaseAdapter() {
        init{
            boardRef.child(boardKey).child("comments").addChildEventListener(object :
                ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    val user = snapshot.child("user").getValue(String::class.java)
                    val content = snapshot.child("content").getValue(String::class.java)
                    val commentInfo = HashMap<String,String>()
                    commentInfo["user"] = user!!
                    commentInfo["content"] = content!!
                    listViewItemList.add(commentInfo)
                    notifyDataSetChanged()
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
                view = inflater.inflate(R.layout.comment_item, parent, false)
            }

            val listViewItem = listViewItemList[position]
            val postWriter = view!!.findViewById<TextView>(R.id.postWriter)
            val commentTextView = view.findViewById<TextView>(R.id.comment)

            postWriter.text = listViewItem["user"]
            commentTextView.text = listViewItem["content"]

            return view
        }
    }
}