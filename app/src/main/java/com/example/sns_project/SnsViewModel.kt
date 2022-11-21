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
    var searchUserData :  MutableLiveData<User> = MutableLiveData<User>() // 검색해서 선택한 사용자 정보
    var searchUserEmail = ""
    var searchUserKey = ""

    init{
        userEmail = email
        userKey = getKey(userEmail)
        getMyInfo()
    }

    fun getMyInfo(){
        viewModelScope.launch {
            usersRef.orderByChild("email").equalTo(userEmail).addChildEventListener(object : ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    System.out.println(snapshot.value)
                    val user = snapshot.getValue(User::class.java)
                    myData.value = user!!
                    System.out.println(myData.value!!)
                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                    val user = snapshot.getValue(User::class.java)
                    myData.value = user!!
                    System.out.println(myData.value!!)
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

    fun getSearchUserInfo(searchUserNickname:String){
        viewModelScope.launch {
            usersRef.orderByChild("nickname").equalTo(searchUserNickname).addChildEventListener(object : ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    System.out.println("################ onChildAdded ######################")
                    val user = snapshot.getValue(User::class.java)
                    searchUserData.value = user!!
                    searchUserEmail = searchUserData.value!!.email
                    searchUserKey = getKey(searchUserEmail)
                    System.out.println(searchUserData.value!!)

                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                    System.out.println("################ onChildChanged ######################")
                    val user = snapshot.getValue(User::class.java)
                    searchUserData.value = user!!
                    searchUserEmail = searchUserData.value!!.email
                }

                override fun onChildRemoved(snapshot: DataSnapshot) {
                    System.out.println(searchUserEmail)
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
            // 상대방 follower에 사용자 key 저장
            usersRef.child(searchUserKey).child("follower").child((searchUserData.value!!.follower!!.size).toString()).setValue(userKey)
            // 사용자 following에 상대방 key 저장
            usersRef.child(userKey).child("following").child((myData.value!!.following!!.size).toString()).setValue(searchUserKey)
        }
    }

    fun unfollowUser(){
        val removeFollowerKey = searchUserData.value!!.follower!!.indexOf(userKey)
        val removeFollowingKey = myData.value!!.following!!.indexOf(searchUserKey)

        // 상대방 follower에 사용자 key 삭제
        usersRef.child(searchUserKey).child("follower").child(removeFollowerKey.toString()).removeValue()
        // 사용자 following에서 상대방 key 삭제
        usersRef.child(userKey).child("following").child(removeFollowingKey.toString()).removeValue()
    }

    fun getKey(email:String):String{
        return email.split('@')[0]
    }

}