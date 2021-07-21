package com.cooling.artifact.splash

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import cn.ycbjie.ycstatusbarlib.bar.StateAppBar
import com.common.theone.base.TheoneSDKApplication
import com.cooling.artifact.databinding.ActivitySplashBinding
import com.cooling.artifact.home.MainActivity
import com.cooling.artifact.utils.SimpleToast
import com.cooling.artifact.utils.StorageUtils
import com.cooling.artifact.views.StatementDialog
import com.gatherad.sdk.GatherAdSDK
import com.gatherad.sdk.GatherAdService
import com.permissionx.guolindev.PermissionX
import com.umeng.analytics.MobclickAgent
import com.umeng.commonsdk.UMConfigure
import java.util.*

/**
 * 开屏页
 */
class SplashActivity : AppCompatActivity() {

    private lateinit var splashAdHelper: SplashAdHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        StateAppBar.translucentStatusBar(this)

        splashAdHelper = SplashAdHelper.getInstance(this)
        splashAdHelper.registerLifecycleObserver(this)
        splashAdHelper.initSome(
            agreementShowed = false, permissionRequested = false, splashSwitcher = "splash_ad"
        )
        splashAdHelper.splashStepListener = object : SplashStepListener {
            override fun doStep(step: SplashStep) {
                when (step) {
                    SplashStep.AGREEMENT -> {
                        if (StorageUtils.isAcceptPolicyAndTerms()) {
                            splashAdHelper.finishAgreementStep()
                            splashAdHelper.nextStep()
                        } else {
                            showStatementDialog()
                        }
                    }
                    SplashStep.PERMISSION -> {
                        if (StorageUtils.isPermissionShowed()) {
                            splashAdHelper.finishPermissionStep()
                            splashAdHelper.nextStep()
                        } else {
                            StorageUtils.savePermissionShowed(true)
                            showPermissionDialog()
                        }
                    }
                    SplashStep.SHOW_AD -> {
                        splashAdHelper.showSplashAd(
                            this@SplashActivity,
                            GatherAdService.splashAd("0c10ad40046b63c1"),
                            binding.adContainer
                        )
                    }
                    SplashStep.FINISH_AD -> {
                        startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                        finish()
                    }
                }
            }
        }
        splashAdHelper.startStep()

    }

    /**
     * 展示协议和隐私的声明弹窗
     */
    private fun showStatementDialog() {
        val statementDialog = StatementDialog.newInstance()
        statementDialog.setOnClickListener(object :
            StatementDialog.OnDialogClickListener {
            override fun onAcceptClick(dialog: DialogFragment?) {
                dialog?.dismiss()
                StorageUtils.saveAcceptPolicyAndTerms(true)
                initCompanySdk()
                initUmSdk()
                splashAdHelper.finishAgreementStep()
                splashAdHelper.nextStep()
            }

            override fun onRefuseClick(dialog: DialogFragment?) {
                dialog?.dismiss()
                SimpleToast.show(this@SplashActivity, "不同意则退出应用")
                finish()
            }
        })
        statementDialog.show(
            supportFragmentManager,
            StatementDialog::class.simpleName
        )
    }

    /**
     * 展示请求权限弹窗
     */
    private fun showPermissionDialog() {
        PermissionX.init(this)
            .permissions(
                listOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            )
            .request { allGranted, _, _ ->
                if (!allGranted) {
                    SimpleToast.show(this, "某些权限未授权，可能会影响某些功能")
                }
                splashAdHelper.finishPermissionStep()
                splashAdHelper.nextStep()
            }
    }

    /**
     * 初始化我司SDK
     */
    private fun initCompanySdk() {
        TheoneSDKApplication.initSdk(application, true)
        GatherAdSDK.init(application, true)
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
        UMConfigure.setLogEnabled(true)
        MobclickAgent.setPageCollectionMode(MobclickAgent.PageMode.AUTO)
    }

}