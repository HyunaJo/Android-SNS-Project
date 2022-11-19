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
    var userEmail = ""

    var myData : MutableLiveData<User> = MutableLiveData<User>() // 내 정보
    var searchUserData :  MutableLiveData<User> = MutableLiveData<User>() // 검색해서 선택한 사용자 정보
    var allUserNicknameData : MutableLiveData<ArrayList<String>> = MutableLiveData<ArrayList<String>>() // 모든 사용자 닉네임
    var allUserNicknames: ArrayList<String> = ArrayList()

    var searchUserEmail = ""

//    fun add(item: String) {
//        items.add(item)
//        liveItems.value = items
//    }
//
//    fun remove(item: String) {
//        items.remove(item)
//        liveItems.value = items
//    }

    init{
        userEmail = email
        getMyInfo()
    }

    fun getMyInfo(){
        viewModelScope.launch {
            usersRef.orderByChild("email").equalTo(userEmail).addChildEventListener(object : ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
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
                    val user = snapshot.getValue(User::class.java)
                    searchUserData.value = user!!
                    searchUserEmail = searchUserData.value!!.email
                    System.out.println(searchUserData.value!!)

                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
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

    fun getAllUserNickname(){
        viewModelScope.launch {
            usersRef.addChildEventListener(object : ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    val nickname = snapshot.child("nickname").getValue(String::class.java)
                    if(nickname != myData.value!!.nickname){
                        allUserNicknames.add(nickname!!)
                    }
                    allUserNicknameData.value = allUserNicknames
                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                    val user = snapshot.getValue(User::class.java)
                    searchUserData.value = user!!
                    System.out.println(searchUserData.value!!)
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
}