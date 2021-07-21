package com.cooling.artifact.clean

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.airbnb.lottie.LottieDrawable
import com.cooling.artifact.databinding.ActivityCleanAnimBinding
import com.cooling.artifact.utils.CLEAN_SCANNING_COUNT
import com.cooling.artifact.utils.StatusBarUtil
import com.cooling.artifact.utils.StorageUtils

/**
 * 清理动画页
 */
class CleanAnimActivity : AppCompatActivity() {

    private var countDownTimer: CountDownTimer? = null

    companion object {
        fun newIntent(context: Context?): Intent {
            return Intent(context, CleanAnimActivity::class.java)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityCleanAnimBinding.inflate(layoutInflater)
        setContentView(binding.root)
        StatusBarUtil.setStatusBarColor(this, Color.parseColor("#78CFFF"))

        binding.toolbar.ivBack.setOnClickListener {
            if (binding.tvCancel.visibility == View.VISIBLE) {
                binding.tvCancel.visibility = View.GONE
            } else {
                binding.tvCancel.visibility = View.VISIBLE
            }
        }
        binding.lavCoolingDown.setOnClickListener {
            if (binding.tvCancel.visibility == View.VISIBLE) {
                binding.tvCancel.visibility = View.GONE
            }
        }
        binding.tvCancel.setOnClickListener {
            binding.lavCoolingDown.cancelAnimation()
            setResult(RESULT_CANCELED)
            finish()
        }

        binding.lavCoolingDown.repeatCount = LottieDrawable.INFINITE
        binding.lavCoolingDown.playAnimation()
        // 动画播放倒计时
        countDownTimer = object : CountDownTimer(5000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
            }

            override fun onFinish() {
                // 保存今日扫描次数
                var count = StorageUtils.getTodayCount(CLEAN_SCANNING_COUNT)
                StorageUtils.saveTodayCount(CLEAN_SCANNING_COUNT, ++count)

                binding.lavCoolingDown.cancelAnimation()
                startActivity(CleanCompleteActivity.newIntent(this@CleanAnimActivity))
                setResult(RESULT_OK)
                finish()
            }
        }
        countDownTimer?.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        countDownTimer?.cancel()
    }

    override fun onBackPressed() {
        // 禁止后退
    }

}