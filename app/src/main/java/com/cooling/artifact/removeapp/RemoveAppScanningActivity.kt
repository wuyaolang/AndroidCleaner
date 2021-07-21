package com.cooling.artifact.removeapp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.airbnb.lottie.LottieDrawable
import com.cooling.artifact.R
import com.cooling.artifact.adapters.AppInfoAdapter
import com.cooling.artifact.bean.AppInfo
import com.cooling.artifact.databinding.ActivityRemoveAppScanningBinding
import com.cooling.artifact.utils.StatusBarUtil
import com.cooling.artifact.viewmodels.UtilViewModel

/**
 * 卸载应用扫描页
 */
class RemoveAppScanningActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRemoveAppScanningBinding
    private lateinit var viewModel: UtilViewModel
    private val appInfoAdapter = AppInfoAdapter()
    private val appCheckedSet: MutableSet<String> = hashSetOf()
    private var removePackageReceiver: RemovePackageReceiver? = null
    private var removePackageCount = 0
    private var currentPosition = 0

    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, RemoveAppScanningActivity::class.java)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRemoveAppScanningBinding.inflate(layoutInflater)
        setContentView(binding.root)
        StatusBarUtil.setStatusBarColor(this, Color.parseColor("#4497FF"))

        // 注册卸载应用的广播
        removePackageReceiver = RemovePackageReceiver()
        val intentFilter = IntentFilter(Intent.ACTION_PACKAGE_REMOVED)
        intentFilter.addDataScheme("package")
        registerReceiver(removePackageReceiver, intentFilter)

        binding.toolbar.tvTitle.text = "软件管理"
        binding.toolbar.root.setBackgroundColor(Color.parseColor("#4497FF"))
        binding.lavScanning.repeatCount = LottieDrawable.INFINITE
        binding.lavScanning.playAnimation()
        binding.spinner.adapter = ArrayAdapter<String>(
            this, R.layout.spinner_list_item,
            arrayOf("按安装日期", "按文件大小")
        )
        binding.spinner.setSelection(0)
        binding.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (currentPosition == position) {
                    return
                }

                when (position) {
                    0 -> {
                        appInfoAdapter.data.sortByDescending { it.firstInstallTime }
                    }
                    1 -> {
                        appInfoAdapter.data.sortByDescending { it.length }
                    }
                    else -> {
                        appInfoAdapter.data.sortByDescending { it.firstInstallTime }
                    }
                }

                currentPosition = position
                appInfoAdapter.notifyDataSetChanged()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

        }

        binding.rvApp.adapter = appInfoAdapter
        binding.rvApp.layoutManager = LinearLayoutManager(this)

        binding.toolbar.ivBack.setOnClickListener {
            setResult(RESULT_CANCELED, intent)
            finish()
        }
        binding.tvRemoveApp.setOnClickListener {
            for (packageName in appCheckedSet) {
                val intent = Intent(Intent.ACTION_DELETE)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                intent.data = Uri.parse("package:$packageName")
                startActivity(intent)
            }
        }
        appInfoAdapter.addChildClickViewIds(R.id.check_box)
        appInfoAdapter.setOnItemChildClickListener { _, _, position ->
            val item = appInfoAdapter.getItem(position)
            item.isChecked = !item.isChecked
            if (item.isChecked) {
                appCheckedSet.add(item.packageName)
            } else {
                appCheckedSet.remove(item.packageName)
            }
            appInfoAdapter.notifyItemChanged(position)

            if (appCheckedSet.isEmpty()) {
                binding.tvRemoveApp.text = "选择应用"
            } else {
                binding.tvRemoveApp.text = getString(R.string.remove_package, appCheckedSet.size)
            }
        }

        viewModel = UtilViewModel(application)
        viewModel.apps.observe(this, {
            appInfoAdapter.setList(it)
        })
        viewModel.scanningState.observe(this, {
            if (it) {
                binding.groupScanning.visibility = View.GONE
                binding.groupData.visibility = View.VISIBLE
            } else {
                binding.lavScanning.cancelAnimation()
                binding.tvDescription.text = "扫描失败"
            }
        })
        viewModel.getApps()

    }

    override fun onBackPressed() {
        setResult(RESULT_CANCELED, intent)
        super.onBackPressed()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.lavScanning.cancelAnimation()
        removePackageReceiver?.let { unregisterReceiver(it) }
    }

    /**
     * 关于卸载应用的广播接收器
     */
    inner class RemovePackageReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (Intent.ACTION_PACKAGE_REMOVED == intent?.action) {
                val packageName = intent.data?.schemeSpecificPart
                val remove = arrayListOf<AppInfo>()
                for (item in appInfoAdapter.data) {
                    packageName?.let {
                        appCheckedSet.remove(packageName)
                        if (item.packageName.contains(it)) {
                            remove.add(item)
                            removePackageCount++
                        }
                    }
                }

                for (r in remove) {
                    appInfoAdapter.remove(r)
                }

                if (appCheckedSet.isEmpty()) {
                    startActivity(
                        RemoveAppCompleteActivity.newIntent(
                            this@RemoveAppScanningActivity,
                            removePackageCount
                        )
                    )
                    finish()
                }
            }
        }
    }

}