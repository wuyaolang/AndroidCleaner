package com.cooling.artifact.utils

import android.annotation.TargetApi
import android.app.Activity
import android.view.WindowManager

/**
 * 简易的Toast
 */
object StatusBarUtil {

    /**
     * 设置状态栏颜色
     */
    @TargetApi(21)
    fun setStatusBarColor(activity: Activity, color: Int) {
        val window = activity.window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = color
    }
}