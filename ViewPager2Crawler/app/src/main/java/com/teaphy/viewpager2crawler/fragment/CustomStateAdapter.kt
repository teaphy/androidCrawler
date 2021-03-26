package com.teaphy.viewpager2crawler.fragment

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

/**
 *
 * Create by: teaphy
 * Date: 3/26/21
 * Time: 2:06 PM
 */
class CustomStateAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle,
    private val listFrm: List<Fragment>
) :
    FragmentStateAdapter(fragmentManager, lifecycle) {
    override fun getItemCount(): Int {
        return listFrm.size
    }

    override fun createFragment(position: Int): Fragment {
        return listFrm[position]
    }

}