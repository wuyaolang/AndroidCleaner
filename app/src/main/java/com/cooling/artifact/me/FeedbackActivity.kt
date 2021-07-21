package com.cooling.artifact.me

import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.common.theone.interfaces.common.factory.FactoryCallBack
import com.common.theone.interfaces.common.factory.FeedbackFactory
import com.cooling.artifact.databinding.ActivityFeedbackBinding
import com.cooling.artifact.utils.SimpleToast
import com.cooling.artifact.utils.StatusBarUtil

/**
 * 意见反馈
 */
class FeedbackActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityFeedbackBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFeedbackBinding.inflate(layoutInflater)
        setContentView(binding.root)
        StatusBarUtil.setStatusBarColor(this, Color.parseColor("#2D89FF"))
        binding.toolbar.root.setBackgroundColor(Color.parseColor("#2D89FF"))
        binding.toolbar.tvTitle.text = "意见反馈"
        binding.tvSubmit.setOnClickListener(this)
        binding.toolbar.ivBack.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        if (v === binding.toolbar.ivBack) {
            finish()
        } else if (v === binding.tvSubmit) {
            if (!binding.tvSubmit.isEnabled) {
                SimpleToast.show(this, "已经在提交了")
                return
            }
            val content = binding.editContent.text.toString().trim { it <= ' ' }
            if (TextUtils.isEmpty(content)) {
                SimpleToast.show(this, "还没有写吐槽的内容呢")
            } else {
                binding.tvSubmit.isEnabled = false
                requestFeedback(content)
            }
        }
    }

    private fun requestFeedback(content: String) {
        FeedbackFactory.getInstance().requestData(content, object : FactoryCallBack {
            override fun onSuccess() {
                binding.tvSubmit.isEnabled = true
            }

            override fun onError() {
                binding.tvSubmit.isEnabled = true
            }
        })
    }
}