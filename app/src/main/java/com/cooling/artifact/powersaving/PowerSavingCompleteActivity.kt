package com.cooling.artifact.powersaving

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.common.theone.interfaces.common.admodel.AdConfigs
import com.cooling.artifact.R
import com.cooling.artifact.ads.OnSimpleAdEventListener
import com.cooling.artifact.clean.CleanAnimActivity
import com.cooling.artifact.clearcache.ClearCacheAnimActivity
import com.cooling.artifact.databinding.ActivityPowerSavingCompleteBinding
import com.cooling.artifact.removeapp.RemoveAppScanningActivity
import com.cooling.artifact.utils.StatusBarUtil
import com.gatherad.sdk.GatherAdService
import me.jessyan.autosize.AutoSizeConfig
import kotlin.random.Random

/**
 * 省电完成页
 */
class PowerSavingCompleteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPowerSavingCompleteBinding

    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, PowerSavingCompleteActivity::class.java)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPowerSavingCompleteBinding.inflate(layoutInflater)
        setContentView(binding.root)
        StatusBarUtil.setStatusBarColor(this, Color.parseColor("#7159FF"))

        binding.toolbar.tvTitle.text = "清理完成"
        val number = Random.nextInt(5, 11) // [5,10]
        binding.tvDescription.text = getString(R.string.percentage, number)

        binding.toolbar.ivBack.setOnClickListener { finish() }
        binding.clean.root.setOnClickListener {
            startActivity(CleanAnimActivity.newIntent(this))
            finish()
        }
        binding.removeApp.root.setOnClickListener {
            startActivity(RemoveAppScanningActivity.newIntent(this))
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
            showNativeAdForTop()
        }
        if (AdConfigs.getInstance().isAdConfigsDisplay("complete_interstitial_ad")) {
            showInterstitialAd()
        }
    }

    /**
     * 显示信息流
     */
    private fun showNativeAdForTop() {
        val width =
            (((AutoSizeConfig.getInstance().screenWidth / 360f) / AutoSizeConfig.getInstance().initDensity) * 300)
        GatherAdService.nativeAd("2080bd3e33432dcf")
            .setBindDislike(true)
            .bindLifecycle(lifecycle)
            .setAdSize(width, 0f)
            .showAd(this, binding.flAdContainerTop, object : OnSimpleAdEventListener() {
                override fun onRenderSuccess(view: View) {
                    binding.cvAdContainerTop.visibility = View.VISIBLE
                }

                override fun onAdShow() {
//                    showNativeAdForBottom()
                }

                override fun onAdClose() {
                    binding.let {
                        binding.flAdContainerTop.removeAllViews()
                        binding.cvAdContainerTop.visibility = View.GONE
                    }
                }
            })
    }

    /**
     * 显示信息流
     */
    private fun showNativeAdForBottom() {
        val width =
            (((AutoSizeConfig.getInstance().screenWidth / 360f) / AutoSizeConfig.getInstance().initDensity) * 300)
        GatherAdService.nativeAd("2080bd3e33432dcf")
            .setBindDislike(true)
            .bindLifecycle(lifecycle)
            .setAdSize(width, 0f)
            .showAd(this, binding.flAdContainerBottom, object : OnSimpleAdEventListener() {
                override fun onRenderSuccess(view: View) {
                    binding.cvAdContainerBottom.visibility = View.VISIBLE
                }

                override fun onAdClose() {
                    binding.let {
                        binding.flAdContainerBottom.removeAllViews()
                        binding.cvAdContainerBottom.visibility = View.GONE
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