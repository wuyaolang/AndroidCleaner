package com.cooling.artifact.ads

import android.view.View
import com.gatherad.sdk.style.listeners.OnAdEventListener

/**
 * OnAdEventListener的实现类
 * 按需重写方法，简化广告GatherAdService回调的代码
 */
open class OnSimpleAdEventListener : OnAdEventListener {
    override fun onAdShowLoaded() {}
    override fun onAdShowLoadFail(i: Int, s: String) {}
    override fun onRenderSuccess(view: View) {}
    override fun onRenderFail(i: Int, s: String) {}
    override fun onAdClick() {}
    override fun onAdShow() {}
    override fun onAdClose() {}
}