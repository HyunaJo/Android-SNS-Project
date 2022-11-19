package com.example.sns_project

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class SnsViewModel(email:String):ViewModel() {
    val database = Firebase.database("https://sns-project-dc395-default-rtdb.asia-southeast1.firebasedatabase.app/")
    val usersRef = database.reference.child("users")
    var userEmail = ""

    var userData : MutableLiveData<User> = MutableLiveData<User>()

    init{
        userEmail = email
        usersRef.orderByChild("email").equalTo(userEmail).addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                System.out.println("======================onChildAdded=======================")
                val user = snapshot.getValue(User::class.java)
                userData.value = user!!
                System.out.println(userData.value!!)
                System.out.println("==============================================================")
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                System.out.println("======================onChildChanged=======================")
                for (userSnapShot in snapshot.children) {
                    val user = userSnapShot.getValue(User::class.java)
                    System.out.println(user)
                    userData.value = user!!
                    System.out.println(userData.value!!)
                }
                System.out.println("=============================================")
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