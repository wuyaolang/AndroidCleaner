package com.cooling.artifact.utils

import android.util.Log
import org.json.JSONException
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

/**
 * 用于存储数据
 */
object StorageUtils {

    private const val TAG = "存储数据"

    /**
     * 保存是否同意条款
     */
    fun saveAcceptPolicyAndTerms(flag: Boolean) {
        MMKVUtils.putBoolean("AcceptPolicyAndTerms", flag)
    }

    /**
     * 判断是否同意了条款
     */
    fun isAcceptPolicyAndTerms(): Boolean {
        return MMKVUtils.getBoolean("AcceptPolicyAndTerms", false)
    }

    /**
     * 保存开屏页是否已请求过权限
     */
    fun savePermissionShowed(flag: Boolean) {
        MMKVUtils.putBoolean("PermissionShowed", flag)
    }

    /**
     * 判断开屏页是否已请求过权限
     */
    fun isPermissionShowed(): Boolean {
        return MMKVUtils.getBoolean("PermissionShowed", false)
    }

    /**
     * 获取当前设备温度
     */
    fun getTemperature(): Float {
        return MMKVUtils.getFloat("Temperature", 0.0f)
    }

    /**
     * 保存当前设备温度
     */
    fun saveTemperature(temp: Float) {
        MMKVUtils.putFloat("Temperature", temp)
    }

    /**
     * 获取当前设备电量百分比
     */
    fun getBatteryPercentage(): Float {
        return MMKVUtils.getFloat("BatteryPercentage", 0.0f)
    }

    /**
     * 保存当前设备电量百分比
     */
    fun saveBatteryPercentage(temp: Float) {
        MMKVUtils.putFloat("BatteryPercentage", temp)
    }

    /**
     * 获取最近一次降温成功的时间
     */
    fun getRecentCoolDownTime(): Long {
        return MMKVUtils.getLong("RecentCoolDownTime", 0L)
    }

    /**
     * 保存最近一次降温成功的时间
     */
    fun saveRecentCoolDownTime(time: Long) {
        MMKVUtils.putLong("RecentCoolDownTime", time)
    }


    /**
     * 获取当天指定key中保存的数字
     */
    fun getTodayCount(key: String): Int {
        val sdf = SimpleDateFormat("yyyyMMdd", Locale.CHINA)
        val date = sdf.format(Calendar.getInstance().time)
        val valueStr = MMKVUtils.getString(key, "")
        return try {
            val jsonObject = JSONObject(valueStr.orEmpty())
            if (jsonObject.has(date)) {
                jsonObject.getInt(date)
            } else {
                0
            }
        } catch (e: JSONException) {
            Log.e(TAG, "今日次数转换异常: $e")
            0
        }
    }

    /**
     * 保存当天指定key中的数字
     */
    fun saveTodayCount(key: String, count: Int) {
        val sdf = SimpleDateFormat("yyyyMMdd", Locale.CHINA)
        val date = sdf.format(Calendar.getInstance().time)
        val map = mutableMapOf(date to count)
        val valueStr = try {
            val jsonObject = JSONObject(map as Map<*, *>)
            jsonObject.toString()
        } catch (e: JSONException) {
            Log.e(TAG, "今日次数转换异常: $e")
            ""
        } catch (e: NullPointerException) {
            Log.e(TAG, "今日次数转换异常: $e")
            ""
        }
        Log.i(TAG, "今日次数: $valueStr")
        MMKVUtils.putString(key, valueStr)
    }

}