package com.cooling.artifact.cooldown

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.common.theone.interfaces.common.admodel.AdConfigs
import com.cooling.artifact.R
import com.cooling.artifact.ads.OnSimpleAdEventListener
import com.cooling.artifact.clean.CleanAnimActivity
import com.cooling.artifact.clearcache.ClearCacheAnimActivity
import com.cooling.artifact.databinding.ActivityCoolDownCompleteBinding
import com.cooling.artifact.powersaving.PowerSavingScanningActivity
import com.cooling.artifact.removeapp.RemoveAppScanningActivity
import com.cooling.artifact.utils.COOL_DOWN_COUNT
import com.cooling.artifact.utils.StatusBarUtil
import com.cooling.artifact.utils.StorageUtils
import com.gatherad.sdk.GatherAdService
import me.jessyan.autosize.AutoSizeConfig
import java.util.*

/**
 * 降温完成页
 */
class CoolDownCompleteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCoolDownCompleteBinding
    private var countDownTimer: CountDownTimer? = null

    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, CoolDownCompleteActivity::class.java)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCoolDownCompleteBinding.inflate(layoutInflater)
        setContentView(binding.root)
        StatusBarUtil.setStatusBarColor(this, Color.parseColor("#3e74ff"))

        binding.toolbar.tvTitle.text = "降温完成"
        binding.tvTemperature.text = getString(R.string.temperature, StorageUtils.getTemperature())

        // 保存最近一次完成降温的时间
        StorageUtils.saveRecentCoolDownTime(Calendar.getInstance().timeInMillis)
        // 保存今日降温次数
        var count = StorageUtils.getTodayCount(COOL_DOWN_COUNT)
        StorageUtils.saveTodayCount(COOL_DOWN_COUNT, ++count)

        // 倒计时静止60秒
        countDownTimer = object : CountDownTimer(60000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                binding.tvCountDown.text = (millisUntilFinished / 1000).toInt().toString()
            }

            override fun onFinish() {
                binding.groupCountDown.visibility = View.GONE
                binding.groupCompleted.visibility = View.VISIBLE
            }
        }
        countDownTimer?.start()

        binding.toolbar.ivBack.setOnClickListener { finish() }
        binding.clean.root.setOnClickListener {
            startActivity(CleanAnimActivity.newIntent(this))
            finish()
        }
        binding.removeApp.root.setOnClickListener {
            startActivity(RemoveAppScanningActivity.newIntent(this))
            finish()
        }
        binding.powerSaving.root.setOnClickListener {
            startActivity(
                PowerSavingScanningActivity.newIntent(
                    this,
                    StorageUtils.getBatteryPercentage()
                )
            )
            finish()
        }
        binding.clearCache.root.setOnClickListener {
            startActivity(ClearCacheAnimActivity.newIntent(this))
            finish()
        }

        if (!AdConfigs.getInstance().isAdConfigsDisplay("remove_app_enter")) {
            binding.removeApp.root.visibility = View.GONE
        }

        if (AdConfigs.getInstance().isAdConfigsDisplay("complete_native_ad")) {
            showNativeAd()
        }
        if (AdConfigs.getInstance().isAdConfigsDisplay("complete_interstitial_ad")) {
            showInterstitialAd()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        countDownTimer?.cancel()
    }

    /**
     * 显示信息流
     */
    private fun showNativeAd() {
        val width =
            (((AutoSizeConfig.getInstance().screenWidth / 360f) / AutoSizeConfig.getInstance().initDensity) * 300)
        GatherAdService.nativeAd("2080bd3e33432dcf")
            .setBindDislike(true)
            .bindLifecycle(lifecycle)
            .setAdSize(width, 0f)
            .showAd(this, binding.flAdContainer, object : OnSimpleAdEventListener() {
                override fun onRenderSuccess(view: View) {
                    binding.cvAdContainer.visibility = View.VISIBLE
                }

                override fun onAdClose() {
                    binding.let {
                        binding.flAdContainer.removeAllViews()
                        binding.cvAdContainer.visibility = View.GONE
                    }
                }
            })
    }

    /**
     * 显示插屏
     */
    private fun showInterstitialAd() {
        val width =
            (((AutoSizeConfig.getInstance().screenWidth / 360f) / AutoSizeConfig.getInstance().initDensity) * 300)
        GatherAdService.interstitialAd("6b609030f40bfc0d")
            .bindLifecycle(lifecycle)
            .setAdSize(width, 0f)
            .showAd(this, null)
    }


}