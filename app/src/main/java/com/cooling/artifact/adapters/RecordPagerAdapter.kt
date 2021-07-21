package com.cooling.artifact.adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.cooling.artifact.home.RecordFragment
import com.cooling.artifact.utils.FunctionType

const val TEMPERATURE_PAGE_INDEX = 0
const val CLEANING_PAGE_INDEX = 1
const val SCANNING_PAGE_INDEX = 2
const val AD_PAGE_INDEX = 3

class RecordPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    private val navFragmentsCreators: Map<Int, () -> Fragment> = mapOf(
        TEMPERATURE_PAGE_INDEX to { RecordFragment.newInstance(FunctionType.COOL_DOWN) },
        CLEANING_PAGE_INDEX to { RecordFragment.newInstance(FunctionType.CLEAN) },
        SCANNING_PAGE_INDEX to { RecordFragment.newInstance(FunctionType.CLEAR_CACHE) },
        AD_PAGE_INDEX to { RecordFragment() }
    )

    override fun getItemCount() = navFragmentsCreators.size

    override fun createFragment(position: Int): Fragment {
        return navFragmentsCreators[position]?.invoke() ?: throw IndexOutOfBoundsException()
    }

}