package com.cooling.artifact.ads

import android.view.View
import com.gatherad.sdk.data.entity.NativeCustomAdInfo
import com.gatherad.sdk.style.listeners.OnAdEventListener
import com.gatherad.sdk.style.listeners.OnNativeCustomAdListener

/**
 * OnAdEventListener的实现类
 * 按需重写方法，简化广告GatherAdService回调的代码
 */
open class OnSimpleNativeCustomAdListener : OnNativeCustomAdListener {
    override fun onAdLoaded() {}
    override fun onAdLoadFail(p0: Int, p1: String?) {}
    override fun onRenderView(p0: View?, p1: NativeCustomAdInfo?) {}
    override fun onAdClick() {}
    override fun onAdShow() {}
}