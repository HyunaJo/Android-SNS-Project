package com.example.sns_project

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.example.sns_project.databinding.FollowerlistfragmentLayoutBinding
import com.example.sns_project.databinding.UserfeedfragmentLayoutBinding

class FollowerListFragment:Fragment(R.layout.followerlistfragment_layout) {
    lateinit var binding: FollowerlistfragmentLayoutBinding
    lateinit var listView: ListView
    lateinit var arrayAdapter: ArrayAdapter<String>
    var followers = ArrayList<String>()
    lateinit var viewModel:SnsViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FollowerlistfragmentLayoutBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val snsActivity = activity as SnsActivity
        viewModel = snsActivity.viewModel
        listView = binding.followerListView

        viewModel.myData.observe(viewLifecycleOwner, Observer {
            followers = viewModel.myData.value!!.follower!!.filterNot{ it == "" } as ArrayList<String>
            arrayAdapter = ArrayAdapter(snsActivity,android.R.layout.simple_list_item_1,followers)
            listView.adapter = arrayAdapter
        })

        followers = viewModel.myData.value!!.follower!!.filterNot{ it == "" } as ArrayList<String>
        arrayAdapter = ArrayAdapter(snsActivity,android.R.layout.simple_list_item_1,followers)
        listView.adapter = arrayAdapter


    }
}