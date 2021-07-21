package com.cooling.artifact.splash

import android.app.Activity
import android.app.Application
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.lifecycle.*
import com.common.theone.interfaces.common.admodel.AdConfigs
import com.common.theone.interfaces.common.admodel.AdInfoVos
import com.common.theone.interfaces.common.factory.ConfigFactory
import com.common.theone.interfaces.common.factory.FactoryCallBack
import com.common.theone.interfaces.common.factory.PreConfigFactory
import com.cooling.artifact.R
import com.cooling.artifact.views.RoundProgressBar
import com.gatherad.sdk.data.entity.AdShowInfo
import com.gatherad.sdk.style.ad.DSplashAd
import com.gatherad.sdk.style.listeners.OnSplashAdEventListener
import kotlin.random.Random

/**
 *    @author : Yonji
 *    date   : 2021/7/6 18:15
 *    desc   :
 */
class SplashAdHelper(application: Application) : AndroidViewModel(application), LifecycleObserver {

    private var splashAdIsClicked = false
    private var agreementShowed = false
    private var permissionRequested = false
    private var adConfigSuccess = false
    private var preConfigSuccess = false
    private var splashAdOpened = false
    private var mForceGoMain = false
    private var isResumed = false
    var splashStepListener: SplashStepListener? = null
    private var registerLifecycleObserver: Boolean = false
    private var adSkip = false
    private var splashSwitcher = ""

    companion object {
        fun getInstance(owner: ViewModelStoreOwner): SplashAdHelper {
            return ViewModelProvider(owner).get(SplashAdHelper::class.java)
        }
    }

    fun registerLifecycleObserver(lifecycleOwner: LifecycleOwner) {
        registerLifecycleObserver = true
        lifecycleOwner.lifecycle.addObserver(this)
    }

    fun initSome(
        agreementShowed: Boolean,
        permissionRequested: Boolean,
        splashSwitcher: String
    ) {
        if (!registerLifecycleObserver) {
            throw RuntimeException("得先注册生命周期监听者：registerLifecycleObserver(lifecycleOwner: LifecycleOwner)")
        }
        this.agreementShowed = agreementShowed
        this.permissionRequested = permissionRequested
        this.splashAdOpened = splashAdOpened
        this.splashSwitcher = splashSwitcher
    }

    fun finishAgreementStep() {
        this.agreementShowed = true
    }

    fun finishPermissionStep() {
        this.permissionRequested = true
    }

    fun showSplashAd(activity: Activity, mDSplashAd: DSplashAd, viewGroup: FrameLayout) {
        if (!registerLifecycleObserver) {
            throw RuntimeException(
                "得先注册生命周期监听者：registerLifecycleObserver(lifecycleOwner: LifecycleOwner)"
            )
        }
        try {
            val view = LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.layout_skip, null, false) as View
            val roundProgressBar = view.findViewById<RoundProgressBar>(R.id.round_progressBar)
            mDSplashAd.setSkipTime(5000)
                .setCustomSkipView(view)
                .setTimeOut(
                    AdInfoVos.getInstance().getAdInfoValue("splashAdTimeOut", "5000").toInt()
                )
                .showAd(activity, viewGroup, object : OnSplashAdEventListener() {
                    override fun onAdShowLoaded() {

                    }

                    override fun onAdShowLoadFail(p0: Int, p1: String?) {
                        Log.i("开屏广告", "错误 code = $p0 msg = $p1")
                        splashStepListener?.doStep(SplashStep.FINISH_AD)
                    }

                    override fun onAdTimeOver() {
                        if (!splashAdIsClicked && !adSkip) {
                            splashStepListener?.doStep(SplashStep.FINISH_AD)
                        }
                    }

                    override fun onAdShow() {
                        val isCsjM = mDSplashAd.adShowInfo.platform == "csjm"
                        view.visibility = if (isCsjM) View.GONE else View.VISIBLE
                        viewGroup.addView(
                            view,
                            ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                        )
                        var params = view.layoutParams as FrameLayout.LayoutParams
                        params.topMargin = 80
                        params.rightMargin = 40
                        params.gravity = Gravity.RIGHT or Gravity.TOP
                        view.layoutParams = params
                        val notCanSkip = isShowADProbability(mDSplashAd.adShowInfo)
                        if (!notCanSkip) {
                            view.setOnClickListener {
                                adSkip = true
                                splashStepListener?.doStep(SplashStep.FINISH_AD)
                            }
                        }
                    }

                    override fun onAdClose() {
                        adSkip = true
                        splashStepListener?.doStep(SplashStep.FINISH_AD)
                    }

                    override fun onAdClick() {
                        splashAdIsClicked = true
                        Handler(Looper.getMainLooper()).postDelayed(Runnable {
                            if (isResumed) {
                                splashStepListener?.doStep(SplashStep.FINISH_AD)
                            }
                        }, 550)
                    }

                    override fun onAdTick(p0: Long) {
                        roundProgressBar.max = 5000
                        roundProgressBar.progress = (5000 - p0).toInt()
                    }
                })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun nextStep() {
        if (!preConfigSuccess) {
            getPreConfig()
            return
        }
        preConfigSuccess = true
        if (!agreementShowed) {
            splashStepListener?.doStep(SplashStep.AGREEMENT)
            return
        }
        agreementShowed = true
        if (!permissionRequested) {
            splashStepListener?.doStep(SplashStep.PERMISSION)
            return
        }
        permissionRequested = true
        if (!adConfigSuccess) {
            getAdConfig()
            return
        }
        adConfigSuccess = true
        if (splashAdOpened) {
            splashStepListener?.doStep(SplashStep.SHOW_AD)
        } else {
            splashStepListener?.doStep(SplashStep.FINISH_AD)
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun onCreate() {

    }

    fun startStep() {
        getPreConfig()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onResume() {
        isResumed = true
        if (mForceGoMain && adConfigSuccess || splashAdIsClicked) {
            try {
                splashStepListener?.doStep(SplashStep.FINISH_AD)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy() {

    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onStop() {
        isResumed = false
        if (agreementShowed) {
            mForceGoMain = true
        }
    }

    private fun getPreConfig() {
        Log.i("欢迎页帮助", "预配置信息开始加载")
        PreConfigFactory.getInstance().requestData(object : FactoryCallBack {
            override fun onSuccess() {
                preConfigSuccess = true
                Log.i("欢迎页帮助", "预配置信息加载成功")
                splashAdOpened = AdConfigs.getInstance().isAdConfigsDisplay(splashSwitcher, false)
                nextStep()
            }

            override fun onError() {
                preConfigSuccess = true
                Log.i("欢迎页帮助", "预配置信息加载失败")
                nextStep()
            }
        })
    }

    private fun getAdConfig() {
        Log.i("欢迎页帮助", "开始调用Config接口")
        ConfigFactory.getInstance().requestData(object : FactoryCallBack {
            override fun onSuccess() {
                Log.i("欢迎页帮助", "调用Config接口成功")
                adConfigSuccess = true
                nextStep()
            }

            override fun onError() {
                Log.i("欢迎页帮助", "调用Config接口失败")
                adConfigSuccess = false
                nextStep()
            }
        })
    }

    /**
     * 概率透过 true-点击广告，false-不点广告
     */
    private fun isShowADProbability(adShowInfo: AdShowInfo?): Boolean {
        var isShowProbability = false
        val number: Int = Random.nextInt(100) + 1
        adShowInfo?.let {
            if (it.isSwitchOn) {
                isShowProbability = number < adShowInfo.probability
            }
        }
        return isShowProbability
    }

}