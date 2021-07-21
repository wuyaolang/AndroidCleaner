package com.cooling.artifact.home

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.BatteryManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.common.theone.interfaces.common.admodel.AdConfigs
import com.cooling.artifact.R
import com.cooling.artifact.adapters.RecordAdapter
import com.cooling.artifact.bean.Record
import com.cooling.artifact.clean.CleanAnimActivity
import com.cooling.artifact.clearcache.ClearCacheAnimActivity
import com.cooling.artifact.cooldown.CoolDownAnimActivity
import com.cooling.artifact.cooldown.CoolDownScanningActivity
import com.cooling.artifact.databinding.FragmentHomeBinding
import com.cooling.artifact.powersaving.PowerSavingScanningActivity
import com.cooling.artifact.removeapp.RemoveAppScanningActivity
import com.cooling.artifact.utils.*
import java.util.*

/**
 * 首页
 */
class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private var batteryInfoReceiver: BatteryInfoReceiver? = null
    private var recordAdapter: RecordAdapter? = null
    private var temperature = 0.0f
    private var batteryPercentage = 0.0f

    /** 注册跳转降温扫描页 */
    private val startCoolDownScanningForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                binding.tvCooling.text = "降温引擎保护中"
                binding.ivCoolingArrow.visibility = View.GONE
                binding.llProblemTotal.visibility = View.GONE
                temperature = StorageUtils.getTemperature()
                updateTemperatureUi(temperature)
                when {
                    temperature <= 38 -> sendStatueBarColor("#3e74ff")
                    temperature <= 45 -> sendStatueBarColor("#ff8529")
                    else -> sendStatueBarColor("#ff4d77")
                }
            } else {
                binding.tvCooling.text = "立即降温"
                binding.ivCoolingArrow.visibility = View.VISIBLE
                binding.llProblemTotal.visibility = View.VISIBLE
                binding.tvProblemTotal.text = it.data?.getIntExtra("problemTotal", 0).toString()
            }
        }

    /** 注册跳转降温动画页 */
    private val startCoolDownAnimForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                binding.tvCooling.text = "降温引擎保护中"
                binding.ivCoolingArrow.visibility = View.GONE
                binding.llProblemTotal.visibility = View.GONE
                temperature = StorageUtils.getTemperature()
                updateTemperatureUi(temperature)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        temperature = StorageUtils.getTemperature()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // 注册关于电池的广播
        batteryInfoReceiver = BatteryInfoReceiver()
        requireActivity().registerReceiver(
            batteryInfoReceiver,
            IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        )

        if (!AdConfigs.getInstance().isAdConfigsDisplay("remove_app_enter")) {
            binding.tvRemoveApp.visibility = View.INVISIBLE
        }

        recordAdapter = RecordAdapter(requireActivity())
        binding.recyclerView.adapter = recordAdapter
        binding.recyclerView.layoutManager =
            LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
        recordAdapter?.setList(getRecordList())

        updateTemperatureUi(temperature)
        initOnClickListener()
    }

    override fun onResume() {
        super.onResume()
        recordAdapter?.setList(getRecordList())
    }

    override fun onPause() {
        super.onPause()
        StorageUtils.saveTemperature(temperature)
        StorageUtils.saveBatteryPercentage(batteryPercentage)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        batteryInfoReceiver?.let { requireActivity().unregisterReceiver(it) }
    }

    /**
     * 获取记录数据
     */
    private fun getRecordList(): MutableList<Record> {
        val data1 = Record(
            FunctionType.COOL_DOWN,
            R.drawable.ic_temperature_1,
            R.drawable.ic_temperature_2,
            "高温",
            "降温",
            StorageUtils.getTodayCount(HIGH_TEMPERATURE_COUNT),
            StorageUtils.getTodayCount(COOL_DOWN_COUNT)
        )
        val data2 = Record(
            FunctionType.CLEAN,
            R.drawable.ic_clean_1,
            R.drawable.ic_clean_2,
            "扫描",
            "清理",
            StorageUtils.getTodayCount(CLEAN_SCANNING_COUNT),
            StorageUtils.getTodayCount(CLEAN_COUNT)
        )
        val data3 = Record(
            FunctionType.CLEAR_CACHE,
            R.drawable.ic_clear_cache_1,
            R.drawable.ic_clear_cache_2,
            "查缓",
            "清缓",
            StorageUtils.getTodayCount(CACHE_SCANNING_COUNT),
            StorageUtils.getTodayCount(CACHE_CLEAR_COUNT)
        )
        val list = mutableListOf<Record>()
        list.add(data1)
        list.add(data2)
        list.add(data3)
        // 添加自渲染信息流
        if (AdConfigs.getInstance().isAdConfigsDisplay("record_native_custom_ad")) {
            list.add(Record(type = FunctionType.UNKNOWN))
        }
        return list
    }

    /**
     * 初始化点击事件
     */
    private fun initOnClickListener() {
        binding.tvCooling.setOnClickListener {
            StorageUtils.saveTemperature(temperature)
            if (binding.llProblemTotal.visibility == View.GONE) {
                startCoolDownScanningForResult.launch(
                    CoolDownScanningActivity.newIntent(
                        requireActivity()
                    )
                )
            } else {
                startCoolDownAnimForResult.launch(CoolDownAnimActivity.newIntent(requireActivity()))
            }
        }

        binding.tvClean.setOnClickListener {
            startActivity(CleanAnimActivity.newIntent(requireActivity()))
        }

        binding.tvRemoveApp.setOnClickListener {
            startActivity(RemoveAppScanningActivity.newIntent(requireActivity()))
        }

        binding.tvPowerSaving.setOnClickListener {
            startActivity(
                PowerSavingScanningActivity.newIntent(
                    requireActivity(),
                    batteryPercentage
                )
            )
        }

        binding.tvClearCache.setOnClickListener {
            startActivity(ClearCacheAnimActivity.newIntent(requireActivity()))
        }
    }

    /**
     * 更新有关温度的UI
     */
    private fun updateTemperatureUi(temperature: Float) {
        when {
            temperature <= 38 -> { // 常温
                binding.ivTemperatureBg.setImageResource(R.mipmap.bg_temperature_1)
                binding.tvTemperature.setBackgroundResource(R.mipmap.bg_temperature_ripple_1)
                binding.tvTemperature.setTextColor(Color.parseColor("#FF4285FF"))
                binding.tvTemperature.text = getString(R.string.temperature, temperature)
                // 可见的时候再发
                if (lifecycle.currentState == Lifecycle.State.RESUMED) {
                    sendStatueBarColor("#3e74ff")
                }
            }
            temperature <= 45 -> { // 有点高
                binding.ivTemperatureBg.setImageResource(R.mipmap.bg_temperature_2)
                binding.tvTemperature.setBackgroundResource(R.mipmap.bg_temperature_ripple_2)
                binding.tvTemperature.setTextColor(Color.parseColor("#FFFFFFFF"))
                binding.tvTemperature.text = getString(R.string.temperature, temperature)
                // 可见的时候再发
                if (lifecycle.currentState == Lifecycle.State.RESUMED) {
                    sendStatueBarColor("#ff8529")
                }
            }
            else -> { // 高温
                binding.ivTemperatureBg.setImageResource(R.mipmap.bg_temperature_3)
                binding.tvTemperature.setBackgroundResource(R.mipmap.bg_temperature_ripple_3)
                binding.tvTemperature.setTextColor(Color.parseColor("#FFFFFFFF"))
                binding.tvTemperature.text = getString(R.string.temperature, temperature)
                // 可见的时候再发
                if (lifecycle.currentState == Lifecycle.State.RESUMED) {
                    sendStatueBarColor("#ff4d77")
                }
                // 记录高温
                var count = StorageUtils.getTodayCount(HIGH_TEMPERATURE_COUNT)
                StorageUtils.saveTodayCount(HIGH_TEMPERATURE_COUNT, ++count)
            }
        }
    }

    /**
     * 发送状态颜色给宿主
     */
    private fun sendStatueBarColor(color: String) {
        val result = Bundle()
        result.putString("statueBarColor", color)
        parentFragmentManager.setFragmentResult("home", result)
    }

    /**
     * 关于电池的广播接收器
     */
    inner class BatteryInfoReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val scale = intent?.getIntExtra(BatteryManager.EXTRA_SCALE, 0)
            val level = intent?.getIntExtra(BatteryManager.EXTRA_LEVEL, 0)
            Log.i("电池广播", "scale: $scale ,level: $level")
            batteryPercentage = level?.div(scale?.toFloat() ?: 100f) ?: 0f

            val originTemp = intent?.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0)
            var newTemp = originTemp?.div(10f) ?: 0.0f
            // +7修正，近似CPU温度
            newTemp += 7
            Log.i("电池广播", "原温度: $originTemp ,转换后温度: $newTemp")
            // 距上次降温10分钟后，才会更新UI
            val currentTime = Calendar.getInstance().timeInMillis
            val recentCoolDownTime = StorageUtils.getRecentCoolDownTime()
            if (currentTime - recentCoolDownTime > (10 * 60 * 1000)) {
                temperature = newTemp
                updateTemperatureUi(newTemp)
            } else {
                val timeInterval = (currentTime - recentCoolDownTime) / 60 / 1000
                Log.i("电池广播", "距上次降温${timeInterval.toInt()}分钟了")
            }
        }
    }

}