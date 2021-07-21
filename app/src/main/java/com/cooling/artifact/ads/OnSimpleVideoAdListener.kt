package com.cooling.artifact.ads

import com.gatherad.sdk.style.listeners.OnVideoAdListener

/**
 * OnVideoAdListener的实现类
 * 按需重写方法，简化广告GatherAdService回调的代码
 */
class OnSimpleVideoAdListener : OnVideoAdListener {
    override fun onAdShowLoaded() {}
    override fun onAdShowLoadFail(i: Int, s: String) {}
    override fun onAdShow() {}
    override fun onAdClick() {}
    override fun onAdClose() {}
    override fun onVideoComplete() {}
    override fun onVideoError() {}
    override fun onRewardVerify() {}
    override fun onSkippedVideo() {}
}