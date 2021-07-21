package com.cooling.artifact.home

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import com.cooling.artifact.databinding.ActivityWebViewBinding
import com.cooling.artifact.utils.StatusBarUtil
import com.just.agentweb.AgentWeb

/**
 * 简易的网页浏览页
 * 主要用于加载隐私政策和用户协议
 */
class WebViewActivity : AppCompatActivity() {
    private lateinit var agentWeb: AgentWeb

    companion object {
        private const val EXTRA_TITLE = "title"
        private const val EXTRA_URL = "url"
        fun newIntent(context: Context?, title: String, url: String): Intent {
            val intent = Intent(context, WebViewActivity::class.java)
            intent.putExtra(EXTRA_TITLE, title)
            intent.putExtra(EXTRA_URL, url)
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityWebViewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        StatusBarUtil.setStatusBarColor(this, Color.parseColor("#2D89FF"))
        binding.toolbar.root.setBackgroundColor(Color.parseColor("#2D89FF"))
        binding.toolbar.tvTitle.text = intent?.getStringExtra(EXTRA_TITLE)
        agentWeb = AgentWeb.with(this)
            .setAgentWebParent(binding.flWebContainer, FrameLayout.LayoutParams(-1, -1))
            .useDefaultIndicator()
            .createAgentWeb()
            .ready()
            .go(intent.getStringExtra(EXTRA_URL))

        binding.toolbar.ivBack.setOnClickListener { finish() }
    }

    override fun onResume() {
        agentWeb.webLifeCycle.onResume()
        super.onResume()
    }

    override fun onPause() {
        agentWeb.webLifeCycle.onPause()
        super.onPause()
    }

    override fun onDestroy() {
        agentWeb.webLifeCycle.onDestroy()
        super.onDestroy()
    }

}