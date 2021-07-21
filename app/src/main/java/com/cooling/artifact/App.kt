package com.cooling.artifact

import android.app.Application
import android.util.Log
import com.common.theone.base.TheoneSDKApplication
import com.cooling.artifact.utils.StorageUtils
import com.cooling.artifact.utils.MMKVUtils
import com.gatherad.sdk.GatherAdSDK
import com.umeng.analytics.MobclickAgent
import com.umeng.commonsdk.UMConfigure

class App : Application() {

    private val isLogEnabled = false;

    override fun onCreate() {
        super.onCreate()
        MMKVUtils.initialize(this)
        if (StorageUtils.isAcceptPolicyAndTerms()) {
            initCompanySdk()
            initUmSdk()
        } else {
            preInitSdk()
        }
    }

    /**
     * 预初始化SDK
     */
    private fun preInitSdk() {
        TheoneSDKApplication.preInitSdk(this, isLogEnabled)
        UMConfigure.preInit(this, "60e3c36326a57f10184814d1", null)
    }

    /**
     * 初始化我司SDK
     */
    private fun initCompanySdk() {
        TheoneSDKApplication.initSdk(this, isLogEnabled)
        GatherAdSDK.init(this, isLogEnabled)
    }

    /**
     * 初始化友盟SDK
     */
    private fun initUmSdk() {
        UMConfigure.init(
            this,
            "60e3c36326a57f10184814d1",
            null,
            UMConfigure.DEVICE_TYPE_PHONE,
            null
        )
        UMConfigure.setLogEnabled(isLogEnabled)
        MobclickAgent.setPageCollectionMode(MobclickAgent.PageMode.AUTO)
    }
}