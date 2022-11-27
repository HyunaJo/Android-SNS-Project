package com.example.sns_project

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.example.sns_project.databinding.FollowinglistfragmentLayoutBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class FollowingListFragment(userKey:String): Fragment(R.layout.followinglistfragment_layout) {
    lateinit var binding: FollowinglistfragmentLayoutBinding
    lateinit var listView: ListView
    lateinit var arrayAdapter: ArrayAdapter<String>
    var followings = ArrayList<String>()
    lateinit var viewModel:SnsViewModel
    var userKey = ""
    val database = Firebase.database("https://sns-project-dc395-default-rtdb.asia-southeast1.firebasedatabase.app/")

    init{
        this.userKey = userKey
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FollowinglistfragmentLayoutBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val snsActivity = activity as SnsActivity
        viewModel = snsActivity.viewModel
        listView = binding.followingListView

        val userRef = database.getReference("users")
        userRef.child(userKey).child("following").addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
            }
            override fun onDataChange(snapshot: DataSnapshot) {
               followings.clear()
                for (userSnapShot in snapshot.children) {
                    val following = userSnapShot.getValue(String::class.java)
                    if(!following.equals("")){
                        userRef.child(following!!).child("nickname").addListenerForSingleValueEvent(object:ValueEventListener{
                            override fun onDataChange(snapshot2: DataSnapshot) {
                                followings.add(snapshot2.value as String)
                                arrayAdapter.notifyDataSetChanged()
                            }
                            override fun onCancelled(error: DatabaseError) {
                                TODO("Not yet implemented")
                            }
                        })
                    }
                }
                arrayAdapter = ArrayAdapter(snsActivity,android.R.layout.simple_list_item_1,followings)
                listView.adapter = arrayAdapter
            }
        })
    }
}