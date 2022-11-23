package com.example.sns_project

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.SearchView.SearchAutoComplete
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.sns_project.databinding.SearchfragmentLayoutBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.*
import kotlin.collections.ArrayList

class SearchFragment: Fragment(R.layout.searchfragment_layout){
    lateinit var arrayAdapter:ArrayAdapter<String>
    lateinit var listView:ListView
    val database = Firebase.database("https://sns-project-dc395-default-rtdb.asia-southeast1.firebasedatabase.app/")
    val usersRef = database.getReference("users")
    var users = ArrayList<String>()
    var displayUsers = ArrayList<String>()
    lateinit var viewModel:SnsViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val snsActivity = activity as SnsActivity
        snsActivity.findViewById<TextView>(R.id.toolbarTextView).text=""

        val binding = SearchfragmentLayoutBinding.bind(view)
        listView = binding.searchListView
        viewModel = snsActivity.viewModel

        usersRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                users.clear()
                for (userSnapShot in snapshot.children) {
                    val user = userSnapShot.getValue(User::class.java)
                    if(!user!!.nickname.equals(snsActivity.viewModel.myData.value!!.nickname)){
                        users.add(user.nickname)
                    }
                }
                displayUsers.clear()
                displayUsers.addAll(users)
                arrayAdapter.notifyDataSetChanged()
                System.out.println(users)
                System.out.println(displayUsers)
//                arrayAdapter = ArrayAdapter(snsActivity,android.R.layout.simple_list_item_1,users)
//                listView.adapter = arrayAdapter
            }
        })

        displayUsers.addAll(users)
        arrayAdapter = ArrayAdapter(snsActivity,android.R.layout.simple_list_item_1,displayUsers)
        listView.adapter = arrayAdapter
        listView.setOnItemClickListener{ parent, view, position, id ->
            System.out.println("position ==> $position")
            System.out.println("id ==> $id")
            val name = displayUsers.get(position)
            viewModel.getSearchUserInfo(name)
            val navAction = SearchFragmentDirections.actionSearchFragmentToUserFeedFragment(name)
            findNavController().navigate(navAction)
        }

        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.search_menu, menu)

                val menuItem = menu.findItem(R.id.searchFragment)
                val searchView = menuItem.actionView as SearchView
                searchView.queryHint = "Type nickname to search"
                val searchAutoComplete = searchView.findViewById<View>(androidx.appcompat.R.id.search_src_text) as SearchAutoComplete
                searchAutoComplete.setTextColor(Color.WHITE)
                searchView.clearFocus()
                searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
                    override fun onQueryTextSubmit(query: String?): Boolean {
//                        arrayAdapter.filter.filter(query)
                        return false
                    }

                    override fun onQueryTextChange(query: String?): Boolean {
                        if(query!!.isNotEmpty()){
                            displayUsers.clear()
                            var search = query.lowercase(Locale.getDefault())
                            users.forEach{
                                if(it.lowercase(Locale.getDefault()).contains(search)){
                                    displayUsers.add(it)
                                }
                            }
//                            arrayAdapter = ArrayAdapter(snsActivity,android.R.layout.simple_list_item_1,displayUsers)
//                            listView.adapter = arrayAdapter
                            arrayAdapter.notifyDataSetChanged()
                        }
                        else{
                            displayUsers.clear()
                            displayUsers.addAll(users)
//                            arrayAdapter = ArrayAdapter(snsActivity,android.R.layout.simple_list_item_1,displayUsers)
//                            listView.adapter = arrayAdapter
                            arrayAdapter.notifyDataSetChanged()
                        }
//                        arrayAdapter.filter.filter(query)

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