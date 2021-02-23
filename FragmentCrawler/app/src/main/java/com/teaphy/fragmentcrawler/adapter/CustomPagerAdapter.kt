package com.teaphy.fragmentcrawler.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter

/**
 * @Desc:
 * @author tiany
 * @time  2021-01-07  21:00
 * @version 1.0
 */
class CustomPagerAdapter(fragmentManager: FragmentManager,val list: List<Fragment>) : FragmentStatePagerAdapter(fragmentManager) {
    override fun getCount(): Int {
        return list.size
    }

    override fun getItem(position: Int): Fragment {
        return list[position]
    }
}