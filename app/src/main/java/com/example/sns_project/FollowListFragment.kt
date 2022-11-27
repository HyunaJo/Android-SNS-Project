package com.example.sns_project

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator


class FollowListFragment:Fragment(R.layout.followlistfragment_layout) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val arg: FollowListFragmentArgs by navArgs()
        val snsActivity = activity as SnsActivity

        val tablayout = snsActivity.findViewById<TabLayout>(R.id.tabLayout)
        val viewPager = snsActivity.findViewById<ViewPager2>(R.id.viewpager)

        val adapter = FollowViewPagerAdapter(snsActivity.supportFragmentManager,lifecycle)
        viewPager.adapter = adapter

        TabLayoutMediator(tablayout, viewPager){tab, position->
            when(position){
                0 -> tab.text = "팔로워"
                1 -> tab.text = "팔로잉"
            }
        }.attach()

        snsActivity.findViewById<TextView>(R.id.toolbarTextView).text=""
        var tabIdx = arg.tabIdx
        viewPager.post{ viewPager.setCurrentItem(tabIdx,false) }
    }
}