package com.cooling.artifact.clearcache

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.common.theone.interfaces.common.admodel.AdConfigs
import com.cooling.artifact.ads.OnSimpleAdEventListener
import com.cooling.artifact.clean.CleanAnimActivity
import com.cooling.artifact.databinding.ActivityClearCacheCompleteBinding
import com.cooling.artifact.powersaving.PowerSavingScanningActivity
import com.cooling.artifact.removeapp.RemoveAppScanningActivity
import com.cooling.artifact.utils.CACHE_CLEAR_COUNT
import com.cooling.artifact.utils.StatusBarUtil
import com.cooling.artifact.utils.StorageUtils
import com.gatherad.sdk.GatherAdService
import me.jessyan.autosize.AutoSizeConfig
import kotlin.random.Random

/**
 * 清除缓存完成页
 */
class ClearCacheCompleteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityClearCacheCompleteBinding

    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, ClearCacheCompleteActivity::class.java)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityClearCacheCompleteBinding.inflate(layoutInflater)
        setContentView(binding.root)
        StatusBarUtil.setStatusBarColor(this, Color.parseColor("#3e74ff"))

        // 保存今日清缓次数
        var count = StorageUtils.getTodayCount(CACHE_CLEAR_COUNT)
        StorageUtils.saveTodayCount(CACHE_CLEAR_COUNT, ++count)

        binding.toolbar.tvTitle.text = "缓存清理完成"
        val number = Random.nextInt(300, 601) // [300,600]
        binding.tvDescription.text = number.toString()

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