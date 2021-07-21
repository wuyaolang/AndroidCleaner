package com.cooling.artifact.bean

import android.graphics.drawable.Drawable

/**
 * 应用信息类
 */
data class AppInfo(
    val id: String = "",
    val label: String = "",
    val packageName: String = "",
    val size: String = "",
    val length: Long = 0L,
    val firstInstallTime: Long = 0L,
    val icon: Drawable,
    var isChecked: Boolean = false
)