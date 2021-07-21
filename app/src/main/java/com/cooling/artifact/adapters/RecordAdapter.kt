package com.cooling.artifact.adapters

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.cooling.artifact.R
import com.cooling.artifact.ads.NativeCustomAdRender
import com.cooling.artifact.ads.OnSimpleNativeCustomAdListener
import com.cooling.artifact.bean.Record
import com.cooling.artifact.utils.FunctionType
import com.gatherad.sdk.GatherAdService
import com.gatherad.sdk.data.entity.NativeCustomAdInfo
import java.util.*

/**
 * 应用信息
 */
class RecordAdapter(private val activity: Activity) :
    BaseQuickAdapter<Record, RecordAdapter.MViewHolder>(R.layout.fragment_record) {

    override fun convert(holder: RecordAdapter.MViewHolder, item: Record) {
        if (item.type == FunctionType.UNKNOWN) {
            holder.clRecord.visibility = View.GONE
            holder.adContainer.visibility = View.VISIBLE
            showNativeCustomAd(activity, holder.adContainer)
        } else {
            holder.adContainer.visibility = View.GONE
            holder.clRecord.visibility = View.VISIBLE
            holder.tvTitle1.text = item.title1
            holder.tvTitle2.text = item.title2
            holder.tvTitle1.setCompoundDrawablesWithIntrinsicBounds(item.resId1, 0, 0, 0)
            holder.tvTitle2.setCompoundDrawablesWithIntrinsicBounds(item.resId2, 0, 0, 0)
            holder.tvCount1.text = context.getString(R.string.count, item.count1)
            holder.tvCount2.text = context.getString(R.string.count, item.count2)
        }
    }

    private fun showNativeCustomAd(activity: Activity, adContainer: ViewGroup) {
        val nativeCustomAdRender = NativeCustomAdRender(context)
        val customView: View = nativeCustomAdRender.customView
        val mediaView: ViewGroup = nativeCustomAdRender.mediaView
        val clickViewList: MutableList<View> = ArrayList()
        clickViewList.add(customView)
        // todo 替换广告id
        GatherAdService
            .customNativeAd("e15104a1b82ffa72")
            .setClickViewList(clickViewList)
            .setCustomView(customView as ViewGroup)
            .setAdMediaView(mediaView)
            .requestAd(activity, object : OnSimpleNativeCustomAdListener() {
                override fun onRenderView(view: View?, nativeCustomAdInfo: NativeCustomAdInfo?) {
                    // 手动去曝光
                    if (nativeCustomAdInfo != null) {
                        nativeCustomAdRender.renderView(context, nativeCustomAdInfo)
                    }
                    adContainer.removeAllViews()
                    adContainer.addView(view)
                }
            })
    }

    class MViewHolder(view: View) : BaseViewHolder(view) {
        val tvTitle1: TextView = view.findViewById(R.id.tv_title1)
        val tvCount1: TextView = view.findViewById(R.id.tv_count1)
        val tvTitle2: TextView = view.findViewById(R.id.tv_title2)
        val tvCount2: TextView = view.findViewById(R.id.tv_count2)
        val clRecord: ConstraintLayout = view.findViewById(R.id.cl_record)
        val adContainer: FrameLayout = view.findViewById(R.id.ad_container)
    }
}