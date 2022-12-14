package com.example.sns_project

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

class SnsViewModel(email:String):ViewModel() {
    val database = Firebase.database("https://sns-project-dc395-default-rtdb.asia-southeast1.firebasedatabase.app/")
    val usersRef = database.reference.child("users")
    var userEmail = "" // 현재 로그인한 사용자 이메일
    var userKey = "" // 현재 로그인한 사용자의 firebase 내 키값

    var myData : MutableLiveData<User> = MutableLiveData<User>() // 내 정보
    var followingUserData: ArrayList<User> = ArrayList<User>()
    var searchUserData :  MutableLiveData<User> = MutableLiveData<User>() // 검색해서 선택한 사용자 정보
    var searchUserEmail = ""
    var searchUserKey = ""

    var isFollowingUser : MutableLiveData<Boolean> = MutableLiveData<Boolean>()
    var myBoardData = ArrayList<Board>()
    var boardData = ArrayList<Board>() // 모든 유저의 boardData

    init{
        isFollowingUser.value = false
        userEmail = email
        userKey = getKey(userEmail)
        getMyInfo()
        getFollowingUserData()
    }

    fun getMyInfo(){
        viewModelScope.launch {
            usersRef.orderByChild("email").equalTo(userEmail).addChildEventListener(object : ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    val user = snapshot.getValue(User::class.java)
                    myData.value = user!!
                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                    val user = snapshot.getValue(User::class.java)
                    myData.value = user!!
                }

                override fun onChildRemoved(snapshot: DataSnapshot) {
                    TODO("Not yet implemented")
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

    fun getFollowingUserData(){
        viewModelScope.launch {
            usersRef.addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    for (userSnapShot in snapshot.children) {
                        val user = userSnapShot.getValue(User::class.java)
                        if(myData.value!!.following!!.contains(getKey(user!!.email)))
                            followingUserData.add(user)
                    }
                }
            })
        }
    }

    fun getSearchUserInfo(searchUserNickname:String){
        viewModelScope.launch {
            usersRef.orderByChild("nickname").equalTo(searchUserNickname).addChildEventListener(object : ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    val user = snapshot.getValue(User::class.java)
                    searchUserData.value = user!!
                    searchUserEmail = searchUserData.value!!.email
                    searchUserKey = getKey(searchUserEmail)
                    isFollowingUser.value = myData.value!!.following!!.contains(searchUserKey)

                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                    val user = snapshot.getValue(User::class.java)
                    searchUserData.value = user!!
                    searchUserEmail = searchUserData.value!!.email
                }

                override fun onChildRemoved(snapshot: DataSnapshot) {
                    usersRef.orderByChild("email").equalTo(searchUserEmail).addListenerForSingleValueEvent(object : ValueEventListener{
                        override fun onDataChange(snapshot: DataSnapshot) {
                            for (snapshot2 in snapshot.children) {
                                searchUserData.value = snapshot2.getValue(User::class.java)
                            }
                            searchUserEmail = searchUserData.value!!.email
                        }
                        override fun onCancelled(error: DatabaseError) {
                            TODO("Not yet implemented")
                        }
                    })
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

    fun followUser(){
        viewModelScope.launch {
            isFollowingUser.value = true

            val addFollowerKey = searchUserData.value!!.follower!!.indexOf("")
            val addFollowingKey = myData.value!!.following!!.indexOf("")

            if(addFollowerKey >= 0){
                usersRef.child(searchUserKey).child("follower").child((addFollowerKey).toString()).setValue(userKey)
            }
            else{
                usersRef.child(searchUserKey).child("follower").child((searchUserData.value!!.follower!!.size).toString()).setValue(userKey)
            }

            if(addFollowingKey >= 0){
                usersRef.child(userKey).child("following").child((addFollowingKey).toString()).setValue(searchUserKey)
            }
            else{
                usersRef.child(userKey).child("following").child((myData.value!!.following!!.size).toString()).setValue(searchUserKey)
            }
        }
    }

    fun unfollowUser(){
        isFollowingUser.value = false

        val removeFollowerKey = searchUserData.value!!.follower!!.indexOf(userKey)
        val removeFollowingKey = myData.value!!.following!!.indexOf(searchUserKey)

        // 상대방 follower에 사용자 key 삭제
        usersRef.child(searchUserKey).child("follower").child(removeFollowerKey.toString()).setValue("")
        // 사용자 following에서 상대방 key 삭제
        usersRef.child(userKey).child("following").child(removeFollowingKey.toString()).setValue("")
    }

    fun getKey(email:String):String{
        return email.split('@')[0]
    }
}