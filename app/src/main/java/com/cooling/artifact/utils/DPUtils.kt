package com.cooling.artifact.utils

import android.content.res.Resources
import android.util.TypedValue

/**
 * 用于存储数据
 */
object DPUtils {

    private const val TAG = "存储数据"

    /**
     * DP转PX
     */
    fun dp2px(dp: Float): Float {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            Resources.getSystem().displayMetrics
        )
    }

}