package com.example.sns_project

import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.example.sns_project.databinding.FragmentLayoutBinding
import com.example.sns_project.databinding.UserfeedfragmentLayoutBinding

class UserFeedFragment: Fragment(R.layout.userfeedfragment_layout) {
    lateinit var binding: UserfeedfragmentLayoutBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val arg:UserFeedFragmentArgs by navArgs()
        val snsActivity = activity as SnsActivity

        binding = UserfeedfragmentLayoutBinding.inflate(layoutInflater)
        var selectedName = arg.selectUserName
//        binding.usernameText.text = selectedName
        snsActivity.findViewById<TextView>(R.id.usernameText).text = selectedName
        System.out.println(binding.usernameText.text)
        Toast.makeText(context,selectedName,Toast.LENGTH_LONG).show()
    }
}