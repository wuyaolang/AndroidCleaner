package com.cooling.artifact.adapters

import android.view.View
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.cooling.artifact.R
import com.cooling.artifact.adapters.AppInfoAdapter.MViewHolder
import com.cooling.artifact.bean.AppInfo
import java.text.SimpleDateFormat
import java.util.*

/**
 * 应用信息
 */
class AppInfoAdapter : BaseQuickAdapter<AppInfo, MViewHolder>(R.layout.item_list_app_info) {
    private val sdf: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.CHINA)

    override fun convert(holder: MViewHolder, item: AppInfo) {
        Glide.with(holder.itemView.context)
            .load(item.icon)
            .error(R.mipmap.ic_app_default)
            .placeholder(R.mipmap.ic_app_default)
            .into(holder.ivImage)
        holder.tvName.text = item.label
        holder.tvDate.text =
            context.getString(R.string.install_time, sdf.format(item.firstInstallTime))
        holder.tvSize.text =
            context.getString(R.string.package_size, item.size)
        holder.checkBox.isChecked = item.isChecked
    }

    class MViewHolder(view: View) : BaseViewHolder(view) {
        val ivImage: ImageView = view.findViewById(R.id.iv_image)
        val tvName: TextView = view.findViewById(R.id.tv_name)
        val tvSize: TextView = view.findViewById(R.id.tv_size)
        val tvDate: TextView = view.findViewById(R.id.tv_date)
        val checkBox: CheckBox = view.findViewById(R.id.check_box)
    }
}