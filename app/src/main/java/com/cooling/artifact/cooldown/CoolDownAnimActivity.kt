package com.cooling.artifact.cooldown

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.airbnb.lottie.LottieDrawable
import com.cooling.artifact.databinding.ActivityCoolDownAnimBinding
import com.cooling.artifact.utils.StatusBarUtil
import com.cooling.artifact.utils.StorageUtils
import java.util.*
import kotlin.random.Random

/**
 * 降温动画页
 */
class CoolDownAnimActivity : AppCompatActivity() {

    private var countDownTimer: CountDownTimer? = null

    companion object {
        fun newIntent(context: Context?): Intent {
            return Intent(context, CoolDownAnimActivity::class.java)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityCoolDownAnimBinding.inflate(layoutInflater)
        setContentView(binding.root)
        StatusBarUtil.setStatusBarColor(this, Color.parseColor("#67E3FF"))

        binding.lavCoolingDown.repeatCount = LottieDrawable.INFINITE
        binding.lavCoolingDown.playAnimation()

        // 动画播放倒计时
        countDownTimer = object : CountDownTimer(5000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
            }

            override fun onFinish() {
                // 距上次降温10分钟后，温度才会下降
                val currentTime = Calendar.getInstance().timeInMillis
                val recentCoolDownTime = StorageUtils.getRecentCoolDownTime()
                if (currentTime - recentCoolDownTime > (10 * 60 * 1000)) {
                    val number = Random.nextInt(3, 6) // [3,5]
                    val temperature = StorageUtils.getTemperature()
                    StorageUtils.saveTemperature(temperature - number)
                } else {
                    val timeInterval = (currentTime - recentCoolDownTime) / 60 / 1000
                    Log.i("电池广播", "距上次降温${timeInterval.toInt()}分钟了，未到时间还不能降温")
                }

                binding.lavCoolingDown.cancelAnimation()
                startActivity(CoolDownCompleteActivity.newIntent(this@CoolDownAnimActivity))
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