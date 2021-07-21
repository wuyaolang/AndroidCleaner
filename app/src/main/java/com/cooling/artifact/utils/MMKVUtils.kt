package com.cooling.artifact.utils

import android.content.Context
import android.util.Log
import com.tencent.mmkv.MMKV

/**
 * MMKV 工具类
 */
object MMKVUtils {
    private var kv: MMKV? = null

    fun initialize(context: Context?) {
        if (kv == null) {
            val rootDir = MMKV.initialize(context)
            Log.i("MMKVUtils", "initialize: $rootDir")
            kv = MMKV.defaultMMKV()
        }
    }

    fun putString(key: String?, value: String?): Boolean {
        return kv!!.encode(key, value)
    }

    fun getString(key: String?, defaultValue: String?): String? {
        return kv!!.decodeString(key, defaultValue)
    }

    fun putInt(key: String?, value: Int): Boolean {
        return kv!!.encode(key, value)
    }

    fun getInt(key: String?, defaultValue: Int): Int {
        return kv!!.decodeInt(key, defaultValue)
    }

    fun putLong(key: String?, value: Long): Boolean {
        return kv!!.encode(key, value)
    }

    fun getLong(key: String?, defaultValue: Long): Long {
        return kv!!.decodeLong(key, defaultValue)
    }

    fun putFloat(key: String?, value: Float): Boolean {
        return kv!!.encode(key, value)
    }

    fun getFloat(key: String?, defaultValue: Float): Float {
        return kv!!.decodeFloat(key, defaultValue)
    }

    fun putBoolean(key: String?, value: Boolean): Boolean {
        return kv!!.encode(key, value)
    }

    fun getBoolean(key: String?, defaultValue: Boolean): Boolean {
        return kv!!.decodeBool(key, defaultValue)
    }

    fun putStringSet(key: String?, value: Set<String>): Boolean {
        return kv!!.encode(key, value)
    }

    fun getStringSet(key: String?, defaultValue: Set<String>): MutableSet<String>? {
        return kv!!.decodeStringSet(key, defaultValue)
    }
}