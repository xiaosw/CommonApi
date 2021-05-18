package com.xiaosw.simple

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.xiaosw.api.util.ToastUtil
import com.xsw.ui.widget.banner.adapter.BannerAdapter

/**
 * ClassName: [VerticalBannerAdapter]
 * Description:
 *
 * Create by X at 2021/05/14 11:07.
 */
class VerticalBannerAdapter : BannerAdapter<String>(R.layout.item_vertical_banner) {

    override fun bindData(container: ViewGroup, item: View, position: Int, source: String) {
        val title = item.findViewById<TextView>(R.id.tv_banner_title)
        title.setOnClickListener {
            ToastUtil.showToast(container.context, "$position")
        }
        title.text = source
    }

}