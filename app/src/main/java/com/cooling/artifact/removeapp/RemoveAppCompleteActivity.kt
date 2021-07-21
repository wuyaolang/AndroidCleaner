package com.cooling.artifact.removeapp

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.common.theone.interfaces.common.admodel.AdConfigs
import com.cooling.artifact.ads.OnSimpleAdEventListener
import com.cooling.artifact.clean.CleanAnimActivity
import com.cooling.artifact.clearcache.ClearCacheAnimActivity
import com.cooling.artifact.databinding.ActivityRemoveAppCompleteBinding
import com.cooling.artifact.powersaving.PowerSavingScanningActivity
import com.cooling.artifact.utils.StatusBarUtil
import com.cooling.artifact.utils.StorageUtils
import com.gatherad.sdk.GatherAdService
import me.jessyan.autosize.AutoSizeConfig

/**
 * 卸载应用完成页
 */
class RemoveAppCompleteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRemoveAppCompleteBinding

    companion object {
        private const val EXTRA_REMOVE_APP_COUNT = "remove_app_count"
        fun newIntent(context: Context, count: Int): Intent {
            val intent = Intent(context, RemoveAppCompleteActivity::class.java)
            intent.putExtra(EXTRA_REMOVE_APP_COUNT, count)
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRemoveAppCompleteBinding.inflate(layoutInflater)
        setContentView(binding.root)
        StatusBarUtil.setStatusBarColor(this, Color.parseColor("#3e74ff"))

        binding.toolbar.tvTitle.text = "卸载完成"
        binding.tvDescription.text = intent?.getIntExtra(EXTRA_REMOVE_APP_COUNT, 0).toString()

        binding.toolbar.ivBack.setOnClickListener { finish() }
        binding.clean.root.setOnClickListener {
            startActivity(CleanAnimActivity.newIntent(this))
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