package com.example.sns_project

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

class SearchListAdapter : BaseAdapter() {
    private var userNames = ArrayList<String>()

    override fun getCount(): Int = userNames.size

    override fun getItem(position: Int): String = userNames[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
        var view = convertView
        val context = parent!!.context

        if (view == null) {
            val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            view = inflater.inflate(R.layout.search_item, parent, false)
        }

        val userNameTextView = view!!.findViewById<TextView>(R.id.userNameTextView) // 닉네임

        val userName = userNames[position]

        // 아이템에 데이터 반영
        userNameTextView.text = userName

        return view
    }

    fun changeArrayList(userNames:ArrayList<String>){
        this.userNames = userNames
    }
}