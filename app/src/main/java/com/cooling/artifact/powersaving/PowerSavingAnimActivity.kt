package com.cooling.artifact.powersaving

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.airbnb.lottie.LottieDrawable
import com.cooling.artifact.databinding.ActivityPowerSavingAnimBinding
import com.cooling.artifact.utils.StatusBarUtil

/**
 * 省电动画页
 */
class PowerSavingAnimActivity : AppCompatActivity() {

    private var countDownTimer: CountDownTimer? = null

    companion object {
        fun newIntent(context: Context?): Intent {
            return Intent(context, PowerSavingAnimActivity::class.java)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityPowerSavingAnimBinding.inflate(layoutInflater)
        setContentView(binding.root)
        StatusBarUtil.setStatusBarColor(this, Color.parseColor("#AD9AFF"))

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
                binding.lavCoolingDown.cancelAnimation()
                startActivity(PowerSavingCompleteActivity.newIntent(this@PowerSavingAnimActivity))
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