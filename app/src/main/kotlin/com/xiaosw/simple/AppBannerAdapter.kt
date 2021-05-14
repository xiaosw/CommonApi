package com.xiaosw.simple

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.xiaosw.api.util.ToastUtil
import com.xsw.ui.widget.banner.adapter.BannerAdapter

/**
 * ClassName: [AppBannerAdapter]
 * Description:
 *
 * Create by X at 2021/05/14 11:07.
 */
class AppBannerAdapter : BannerAdapter<String>(R.layout.item_banner) {

    override fun bindData(container: ViewGroup, item: View, position: Int, source: String) {
        val banner = item.findViewById<ImageView>(R.id.iv_banner)
        banner.setOnClickListener {
            ToastUtil.showToast(container.context, "$position")
        }
        Glide.with(container.context).load(source).into(banner)
    }

}