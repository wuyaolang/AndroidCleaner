package com.cooling.artifact.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.cooling.artifact.home.HomeFragment
import com.cooling.artifact.me.MeFragment

const val COOLING_PAGE_INDEX = 0
const val ME_PAGE_INDEX = 1

class MainPagerAdapter(fragment: FragmentActivity) : FragmentStateAdapter(fragment) {

    private val navFragmentsCreators: Map<Int, () -> Fragment> = mapOf(
        COOLING_PAGE_INDEX to { HomeFragment() },
        ME_PAGE_INDEX to { MeFragment() }
    )

    override fun getItemCount() = navFragmentsCreators.size

    override fun createFragment(position: Int): Fragment {
        return navFragmentsCreators[position]?.invoke() ?: throw IndexOutOfBoundsException()
    }
}