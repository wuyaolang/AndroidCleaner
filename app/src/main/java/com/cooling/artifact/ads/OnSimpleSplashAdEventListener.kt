package com.cooling.artifact.ads

import com.gatherad.sdk.style.listeners.OnSplashAdEventListener

/**
 * OnSplashAdEventListener的实现类
 * 按需重写方法，简化广告GatherAdService回调的代码
 */
class OnSimpleSplashAdEventListener : OnSplashAdEventListener() {
    override fun onAdShowLoaded() {}
    override fun onAdShowLoadFail(i: Int, s: String) {}
    override fun onAdTimeOver() {}
    override fun onAdShow() {}
    override fun onAdClose() {}
    override fun onAdClick() {}
    override fun onAdTick(l: Long) {}
}