package com.cooling.artifact.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.cooling.artifact.R
import com.cooling.artifact.databinding.FragmentRecordBinding
import com.cooling.artifact.utils.*
import com.cooling.artifact.utils.FunctionType.*

/**
 * 首页的今日记录
 */
class RecordFragment : Fragment() {

    private lateinit var binding: FragmentRecordBinding

    companion object {
        private const val ARGS_TYPE = "type"
        fun newInstance(type: FunctionType): RecordFragment {
            val args = Bundle()
            args.putSerializable(ARGS_TYPE, type)
            val fragment = RecordFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRecordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateUi()
    }

    private fun updateUi() {
        when (arguments?.getSerializable(ARGS_TYPE)) {
            COOL_DOWN -> {
                binding.tvTitle1.text = "高温"
                binding.tvTitle2.text = "降温"
                binding.tvTitle1.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_temperature_1,
                    0,
                    0,
                    0
                )
                binding.tvTitle2.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_temperature_2,
                    0,
                    0,
                    0
                )
                binding.tvCount1.text =
                    getString(R.string.count, StorageUtils.getTodayCount(HIGH_TEMPERATURE_COUNT))
                binding.tvCount2.text =
                    getString(R.string.count, StorageUtils.getTodayCount(COOL_DOWN_COUNT))
            }
            CLEAN -> {
                binding.tvTitle1.text = "扫描"
                binding.tvTitle2.text = "清理"
                binding.tvTitle1.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_clean_1,
                    0,
                    0,
                    0
                )
                binding.tvTitle2.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_clean_2,
                    0,
                    0,
                    0
                )
                binding.tvCount1.text =
                    getString(R.string.count, StorageUtils.getTodayCount(CLEAN_SCANNING_COUNT))
                binding.tvCount2.text =
                    getString(R.string.count, StorageUtils.getTodayCount(CLEAN_COUNT))
            }
            CLEAR_CACHE -> {
                binding.tvTitle1.text = "查缓"
                binding.tvTitle2.text = "清缓"
                binding.tvTitle1.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_clear_cache_1,
                    0,
                    0,
                    0
                )
                binding.tvTitle2.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_clear_cache_2,
                    0,
                    0,
                    0
                )
                binding.tvCount1.text =
                    getString(R.string.count, StorageUtils.getTodayCount(CACHE_SCANNING_COUNT))
                binding.tvCount2.text =
                    getString(R.string.count, StorageUtils.getTodayCount(CACHE_CLEAR_COUNT))
            }
            else -> {
                // todo 接入自渲染信息流
            }
        }
    }
}