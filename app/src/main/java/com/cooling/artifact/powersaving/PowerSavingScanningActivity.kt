package com.cooling.artifact.powersaving

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import android.view.animation.TranslateAnimation
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.common.theone.interfaces.common.admodel.AdConfigs
import com.cooling.artifact.R
import com.cooling.artifact.adapters.ScanningServiceAdapter
import com.cooling.artifact.ads.OnSimpleAdEventListener
import com.cooling.artifact.databinding.ActivityPowerSavingScanningBinding
import com.cooling.artifact.utils.StatusBarUtil
import com.cooling.artifact.viewmodels.UtilViewModel
import com.gatherad.sdk.GatherAdService
import me.jessyan.autosize.AutoSizeConfig
import kotlin.random.Random

/**
 * 省电扫描页
 */
class PowerSavingScanningActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPowerSavingScanningBinding
    private lateinit var viewModel: UtilViewModel
    private val serviceAdapter = ScanningServiceAdapter()
    private var problemTotal = 0

    companion object {
        private const val EXTRA_BATTERY_PERCENTAGE = "battery_percentage"
        fun newIntent(context: Context, percentage: Float): Intent {
            val intent = Intent(context, PowerSavingScanningActivity::class.java)
            intent.putExtra(EXTRA_BATTERY_PERCENTAGE, percentage)
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPowerSavingScanningBinding.inflate(layoutInflater)
        setContentView(binding.root)
        StatusBarUtil.setStatusBarColor(this, Color.parseColor("#7159FF"))

        binding.toolbar.tvTitle.text = "超强省电"
        val percentage = intent?.getFloatExtra(EXTRA_BATTERY_PERCENTAGE, 0f)
        if (percentage != null && percentage != 0f) {
            binding.tvBatteryLevel.text = getString(R.string.percentage, (percentage * 100).toInt())

            val hours = 18 * percentage
            val minutes = Random.nextInt(10, 60) // [10,59]
            binding.tvTime.text = getString(R.string.time, hours.toInt(), minutes)
        }


        binding.rvService.adapter = serviceAdapter
        binding.rvService.layoutManager = GridLayoutManager(this, 6)

        binding.toolbar.ivBack.setOnClickListener {
            intent?.putExtra("problemTotal", problemTotal)
            setResult(RESULT_CANCELED, intent)
            finish()
        }

        viewModel = UtilViewModel(application)
        viewModel.app.observe(this, {
            serviceAdapter.addData(it)
            problemTotal = problemTotal.inc()
            binding.tvServiceCount.text = problemTotal.toString()
        })
        viewModel.scanningState.observe(this, {
            if (it) {
                showCoolingLayoutWithAnimation()
            }
        })
        viewModel.getAppDelay()

        if (AdConfigs.getInstance().isAdConfigsDisplay("scanning_native_ad")) {
            showNativeAd()
        }
    }

    override fun onBackPressed() {
        intent?.putExtra("problemTotal", problemTotal)
        setResult(RESULT_CANCELED, intent)
        super.onBackPressed()
    }

    /**
     * 显示带动画的降温布局
     */
    private fun showCoolingLayoutWithAnimation() {
        val translateAnim = TranslateAnimation(
            Animation.RELATIVE_TO_SELF, 0f,
            Animation.RELATIVE_TO_SELF, 0f,
            Animation.RELATIVE_TO_SELF, 1f,
            Animation.RELATIVE_TO_SELF, 0f
        )
        translateAnim.duration = 500
        translateAnim.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {
            }

            override fun onAnimationEnd(animation: Animation?) {
                startCompleteActivity()
            }

            override fun onAnimationRepeat(animation: Animation?) {
            }

        })

        binding.clOperate.visibility = View.VISIBLE
        binding.clOperate.startAnimation(translateAnim)
    }

    /**
     * 跳转到降温动画页
     */
    private fun startCompleteActivity() {
        if (AdConfigs.getInstance().getAdConfigsType("power_saving_execute_mode", 0) == 1) {
            val scaleAnim = ScaleAnimation(
                Animation.RELATIVE_TO_SELF.toFloat(), 0.85f,
                Animation.RELATIVE_TO_SELF.toFloat(), 0.85f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f
            )
            scaleAnim.duration = 500
            scaleAnim.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation?) {
                }

                override fun onAnimationEnd(animation: Animation?) {
                    startActivity(PowerSavingAnimActivity.newIntent(this@PowerSavingScanningActivity))
                    intent?.putExtra("problemTotal", problemTotal)
                    setResult(RESULT_OK, intent)
                    finish()
                }

                override fun onAnimationRepeat(animation: Animation?) {
                }
            })
            binding.tvPowerSaving.startAnimation(scaleAnim)
            binding.ivPowerSavingArrow.startAnimation(scaleAnim)
        } else {
            binding.tvPowerSaving.setOnClickListener {
                startActivity(PowerSavingAnimActivity.newIntent(this@PowerSavingScanningActivity))
                intent?.putExtra("problemTotal", problemTotal)
                setResult(RESULT_OK, intent)
                finish()
            }
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

}