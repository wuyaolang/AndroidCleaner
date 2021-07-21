package com.cooling.artifact.bean

import com.cooling.artifact.utils.FunctionType

/**
 * 今日记录类
 */
data class Record(
    val type: FunctionType = FunctionType.COOL_DOWN,
    val resId1: Int = 0,
    val resId2: Int = 0,
    val title1: String = "",
    val title2: String = "",
    val count1: Int = 0,
    val count2: Int = 0,
)