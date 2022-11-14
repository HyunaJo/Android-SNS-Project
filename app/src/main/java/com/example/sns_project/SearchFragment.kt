package com.example.sns_project

import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.SearchView.SearchAutoComplete
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import com.example.sns_project.databinding.SearchfragmentLayoutBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class SearchFragment: Fragment(R.layout.searchfragment_layout){
    var names = arrayOf("aaaa","bbbb","asb","bet","hbvvwr","hhh","ggwfvb","htvrf")
    lateinit var arrayAdapter:ArrayAdapter<String>
    lateinit var listView:ListView
//    lateinit var listView:RecyclerView
    val database = Firebase.database("https://sns-project-dc395-default-rtdb.asia-southeast1.firebasedatabase.app/")
    val usersRef = database.getReference("users")
    var users = ArrayList<String>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val snsActivity = activity as SnsActivity

        System.out.println(usersRef)

        val binding = SearchfragmentLayoutBinding.bind(view)
        listView = binding.searchListView

        System.out.println(usersRef)
        usersRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
            }

            override fun onDataChange(snapshot: DataSnapshot) {

                for (userSnapShot in snapshot.children) {
                    System.out.println(userSnapShot)
                    val user = userSnapShot.getValue(User::class.java)
                    System.out.println(user)
                    users.add(user!!.nickname)
//                    if (user != null) {
//                        users.add(user)
//
//                    }
                }

            }
        })

        arrayAdapter = ArrayAdapter(snsActivity,android.R.layout.simple_list_item_1,users)
        listView.adapter = arrayAdapter
        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.search_menu, menu)

                val menuItem = menu.findItem(R.id.searchFragment)

                val searchView = menuItem.actionView as SearchView
                searchView.queryHint = "Type here to search users"
                val searchAutoComplete = searchView.findViewById<View>(androidx.appcompat.R.id.search_src_text) as SearchAutoComplete
                searchAutoComplete.setTextColor(Color.WHITE)
                searchView.clearFocus()
                searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
                    override fun onQueryTextSubmit(query: String?): Boolean {
                        arrayAdapter.filter.filter(query)
                        return false
                    }

                    override fun onQueryTextChange(query: String?): Boolean {
                        arrayAdapter.filter.filter(query)
                        return false
                    }
                })
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.searchFragment-> {
                        true
                    }
                    else -> false
                }
        }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

    }
}