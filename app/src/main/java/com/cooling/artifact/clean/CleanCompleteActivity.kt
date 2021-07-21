package com.cooling.artifact.clean

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.common.theone.interfaces.common.admodel.AdConfigs
import com.cooling.artifact.ads.OnSimpleAdEventListener
import com.cooling.artifact.clearcache.ClearCacheAnimActivity
import com.cooling.artifact.databinding.ActivityCleanCompleteBinding
import com.cooling.artifact.powersaving.PowerSavingScanningActivity
import com.cooling.artifact.removeapp.RemoveAppScanningActivity
import com.cooling.artifact.utils.CLEAN_COUNT
import com.cooling.artifact.utils.StatusBarUtil
import com.cooling.artifact.utils.StorageUtils
import com.gatherad.sdk.GatherAdService
import me.jessyan.autosize.AutoSizeConfig
import kotlin.random.Random

/**
 * 清理完成页
 */
class CleanCompleteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCleanCompleteBinding

    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, CleanCompleteActivity::class.java)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCleanCompleteBinding.inflate(layoutInflater)
        setContentView(binding.root)
        StatusBarUtil.setStatusBarColor(this, Color.parseColor("#3e74ff"))

        // 保存今日清理次数
        var count = StorageUtils.getTodayCount(CLEAN_COUNT)
        StorageUtils.saveTodayCount(CLEAN_COUNT, ++count)

        binding.toolbar.tvTitle.text = "清理完成"
        val number = Random.nextInt(300, 601) // [300,600]
        binding.tvDescription.text = number.toString()

        binding.toolbar.ivBack.setOnClickListener { finish() }
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