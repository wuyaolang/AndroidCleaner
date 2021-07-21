package com.cooling.artifact.me

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.cooling.artifact.BuildConfig
import com.cooling.artifact.R
import com.cooling.artifact.databinding.ActivityAboutUsBinding
import com.cooling.artifact.utils.StatusBarUtil

/**
 * 关于我们
 */
class AboutUsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityAboutUsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        StatusBarUtil.setStatusBarColor(this, Color.parseColor("#2D89FF"))
        binding.toolbar.root.setBackgroundColor(Color.parseColor("#2D89FF"))
        binding.toolbar.tvTitle.text = "关于我们"
        binding.tvAppVersion.text = getString(R.string.app_version, BuildConfig.VERSION_NAME)
        binding.toolbar.ivBack.setOnClickListener { finish() }
    }

}