package com.teaphy.fragmentcrawler.lifecycle

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.fragment.app.FragmentStatePagerAdapter

/**
 * @Desc:
 * @author tiany
 * @time  2021-02-23  17:27
 * @version 1.0
 */
class CustomPageAdapter(fragmentManager: FragmentManager, private val listFrm: List<Fragment>, private val listTitle: List<String>): FragmentPagerAdapter(fragmentManager) {
    override fun getCount(): Int {
        return listFrm.size
    }

    override fun getItem(position: Int): Fragment {
       return listFrm[position]
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return listTitle[position]
    }
}