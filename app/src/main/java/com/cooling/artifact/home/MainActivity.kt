package com.cooling.artifact.home

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.cooling.artifact.R
import com.cooling.artifact.adapters.MainPagerAdapter
import com.cooling.artifact.databinding.ActivityMainBinding
import com.cooling.artifact.utils.StatusBarUtil

class MainActivity : AppCompatActivity() {

    private var statueBarColor: String = "#3e74ff"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        // 状态栏根据温度而改变的三种颜色 #3e74ff #ff8529 #ff4d77
        StatusBarUtil.setStatusBarColor(this, Color.parseColor("#ff8529"))

        viewBinding.bottomNav.itemIconTintList = null
        viewBinding.bottomNav.setOnNavigationItemSelectedListener { item ->
            val position = when (item.itemId) {
                R.id.navigation_cooling -> 0
                R.id.navigation_me -> 1
                else -> 0
            }
            viewBinding.viewpager2.setCurrentItem(position, false)
            return@setOnNavigationItemSelectedListener true
        }

        viewBinding.viewpager2.adapter = MainPagerAdapter(this)
        viewBinding.viewpager2.isUserInputEnabled = false
        viewBinding.viewpager2.offscreenPageLimit = 1
        viewBinding.viewpager2.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                viewBinding.bottomNav.menu.getItem(position).isChecked = true

                when (position) {
                    0 -> {
                        StatusBarUtil.setStatusBarColor(
                            this@MainActivity,
                            Color.parseColor(statueBarColor)
                        )
                    }
                    1 -> {
                        StatusBarUtil.setStatusBarColor(
                            this@MainActivity,
                            Color.parseColor("#2D89FF")
                        )
                    }
                }
            }
        })

        supportFragmentManager.setFragmentResultListener(
            "home",
            this,
            { requestKey, result ->
                statueBarColor = result.getString("statueBarColor", "#3e74ff")
                StatusBarUtil.setStatusBarColor(this, Color.parseColor(statueBarColor))
            })
    }

}