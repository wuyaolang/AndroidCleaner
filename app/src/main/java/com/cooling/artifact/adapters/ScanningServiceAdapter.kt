package com.cooling.artifact.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cooling.artifact.R
import com.cooling.artifact.bean.AppInfo
import java.util.*

/**
 * 温度扫描-后台服务
 */
class ScanningServiceAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val data: MutableList<AppInfo> = ArrayList()
    override fun getItemViewType(position: Int): Int {
        return if (position >= 80) ELLIPSIS else IMAGE
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == IMAGE) {
            ImageHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_list_service, parent, false)
            )
        } else {
            EllipsisHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_list_ellipsis, parent, false)
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ImageHolder) {
            Glide.with(holder.itemView.context)
                .load(data[position].icon)
                .error(R.mipmap.ic_app_default)
                .placeholder(R.mipmap.ic_app_default)
                .into(holder.ivImage)
        } else if (holder is EllipsisHolder) {
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    /**
     * 重置数据
     *
     * @param newData 新数据
     */
    fun setList(newData: List<AppInfo>?) {
        data.clear()
        data.addAll(newData!!)
        notifyDataSetChanged()
    }

    fun addData(newData: AppInfo) {
        data.add(newData)
        notifyItemChanged(data.size - 1, newData)
    }

    fun getData(): List<AppInfo> {
        return data
    }

    internal class ImageHolder(view: View) : RecyclerView.ViewHolder(view) {
        var ivImage: ImageView = view.findViewById(R.id.iv_image)
    }

    internal class EllipsisHolder(view: View) : RecyclerView.ViewHolder(view) {

    }

    companion object {
        /** 图片  */
        private const val ELLIPSIS = 1

        /** 省略号  */
        private const val IMAGE = 0
    }
}