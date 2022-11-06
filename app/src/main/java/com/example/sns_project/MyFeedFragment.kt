package com.example.sns_project

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.sns_project.databinding.FragmentLayoutBinding

class MyFeedFragment : Fragment(R.layout.fragment_layout){
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val snsActivity = activity as SnsActivity
        snsActivity.hideUpButton()

        val binding = FragmentLayoutBinding.bind(view)
        binding.textView.text = "MyFeedFragment"
    }
}
