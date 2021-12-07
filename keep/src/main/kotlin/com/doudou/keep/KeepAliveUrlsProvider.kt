package com.doudou.keep

import android.content.Context
import com.kalive.biz.config.IKeepaliveUrlsProvider

/**
 * ClassName: [KeepAliveUrlsProvider]
 * Description:
 *
 * Create by X at 2021/12/04 15:37.
 */
internal class KeepAliveUrlsProvider : IKeepaliveUrlsProvider {

    override fun locationInfoUrl(context: Context?) = null

    override fun historyLocationInfoUrl(context: Context?) = null

    override fun extInfoUrl(context: Context?) = null

    override fun externalLogUrl(context: Context?) = null

    override fun externalTimerLogUrl(context: Context?) = null

    override fun externalCtrlUrl(context: Context?) = null

    override fun newLocationInfoUrl(context: Context?) = null
}