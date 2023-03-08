package com.xiaosw.simple

import android.content.*
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ContextThemeWrapper
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.doudou.http.HttpManager
import com.doudou.http.HttpMethod
import com.doudou.http.HttpRequest
import com.doudou.log.Logger
import com.doudou.log.loge
import com.xiaosw.api.annotation.AutoAdjustDensity
import com.xiaosw.api.delegate.CallbackDelegate
import com.xiaosw.api.extend.*
import com.xiaosw.api.floating.FloatWindowManager
import com.xiaosw.api.manager.ActivityLifeManager
import com.xiaosw.api.manager.DensityManager
import com.xiaosw.api.netspeed.NetworkSpeedManager
import com.xiaosw.api.netspeed.NetworkType
import com.xiaosw.api.netspeed.OnNetworkBucketChangeListener
import com.xiaosw.api.restart.RestartAppManager
import com.xiaosw.api.util.FpsMonitor
import com.xiaosw.api.util.ToastUtil
import com.xsw.compat.start.StartManager
import com.xsw.ui.anim.path.PathAnimator
import com.xsw.ui.widget.*
import com.xsw.ui.widget.banner.BaseBannerIndicator
import com.xsw.ui.widget.banner.tranforme.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList
import kotlin.random.Random

/**
 * @ClassName: [MainActivity]
 * @Description:
 *
 * Created by admin at 2019-09-27 16:42
 * @Email xiaosw0802@163.com
 */
@AutoAdjustDensity(baseDp = 360f, baseDpByWidth = true)
class MainActivity : AppCompatActivity(), ActivityLifeManager.AppLifecycleListener, View.OnClickListener {

    private val mFpsCallback by lazy {
        object : FpsMonitor.OnFpsMonitorListener {
            override fun onFpsMonitor(fps: Int) {Boolean
//                Logger.i("fps: $fps")
                tv_fps.text = "FPS:$fps"
            }
        }
    }

    private val monNetworkBucketChange by lazy {
        object : OnNetworkBucketChangeListener {
            override fun onNetworkBucketChange(
                type: NetworkType,
                avg: Float,
                max: Long,
                min: Long
            ) {
                tv_net_speed.text = "type = ${type.desc}" +
                        ", max = ${NetworkSpeedManager.formatSpeed(max * 1F)}" +
                        ", min = ${NetworkSpeedManager.formatSpeed(min * 1F)}" +
                        ", avg = ${NetworkSpeedManager.formatSpeed(avg * 1F)}"
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        DensityManager.addThirdAutoAdjustPage(javaClass)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ActivityLifeManager.register(this)
        Logger.e("onCreate：${resources.configuration.locale}, ${resources.getString(R.string.app_str_home)}")
        tv_fps.onClick {
            showFloatWindow()
        }
//        loge {
//            """the json is: {"key":"value"}"""
//        }
//        loge {
//            """{"key":"value"}"""
//        }
//        loge {
//            """{"key":"value"} is json"""
//        }
//
//        LogUtil.loge("test LogUtil wrapper.")

        registerReceiver(object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                val result = StartManager.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("pinduoduo://")))
                Logger.i("result = $result")
            }

        }, IntentFilter().also {
            it.addAction(Intent.ACTION_SCREEN_OFF)
        })

        FpsMonitor.start(mFpsCallback)
        NetworkSpeedManager.startTrack(this, 60_000)
        NetworkSpeedManager.register(monNetworkBucketChange)

        tv_text.setOnClickListener {
            //startActivity(Intent(this, NotRegisterActivity::class.java))
            startActivity(Intent(this, MaterialDesignActivity::class.java))
//            val main = ReflectCompat.forName(MainActivity::class.java.name)
            Logger.e("MainActivity#onCreate:")
        }

//        setFlickerProgressBar()

        flow_layout.adapter = object : FlowLayout.Adapter<String, FlowLayout.ViewHolder>() {

            override fun onCreateViewHolder(position: Int, parent: ViewGroup): FlowLayout.ViewHolder? {
                val view = layoutInflater.inflate(R.layout.item_flow_layout, parent, false)
                return FlowLayout.ViewHolder(view)
            }

            override fun onBindViewHolder(holder: FlowLayout.ViewHolder, position: Int) {
                (holder.itemView as? TextView)?.let {  tv ->
                    tv.text = getItem(position)
                    tv.setOnClickListener {
                        remove(position)
                        ToastUtil.showToast(message = tv.text.toString())
                    }
                }
            }

        }.also {
            val random = Random(1)
            for (index in 0..100) {
                it.add("${random.nextInt(0, 100000)}")
            }
        }
        with(PathAnimator(tv_text)) {
            duration = 6000
            lineTo(300f, 0f)
                .quadTo(450f, 0f, 300f, 300f)
                .cubicTo(450f, 250f, 150f, 750f, 300f, 900f)
//            start()
        }
        switch_view.interceptOnClick = object : SwitchView.InterceptOnClick {
            override fun interceptOnClick(switchView: SwitchView): Boolean {
                return false
            }
        }
        switch_view.onCheckedChangedListener = object : SwitchView.OnCheckedChangedListener {

            override fun onCheckedChanged(switchView: SwitchView, isChecked: Boolean) {
//                Logger.e("isChecked = $isChecked")
                displaySwitchViewState(switchView)
            }

        }
        displaySwitchViewState(switch_view)
        val bannerAdapter = AppBannerAdapter()
        bannerAdapter.add("https://c-ssl.duitang.com/uploads/item/201410/31/20141031050718_AGxJy.thumb.1000_0.jpeg")
        bannerAdapter.add("https://c-ssl.duitang.com/uploads/item/201908/26/20190826014644_haefl.thumb.1000_0.jpeg")
        bannerAdapter.add("https://c-ssl.duitang.com/uploads/item/201905/16/20190516100859_mknsy.thumb.1000_0.jpg")
        banner_view.setAdapter(bannerAdapter)
        banner_view.setTransform(DefaultTransformer())
        banner_view.bindIndicator(BaseBannerIndicator(this).also {
            val p = dp2px(8f).toInt()
            it.setPadding(p, p, p, p)
            it.forceCircle = false
            it.scrollEffect = BaseBannerIndicator.ScrollEffect.SLIDE
            it.indicatorHeight = 20f
            it.indicatorWidth = 80f
        })

        marquee.setOnClickListener(this)

        val verticalBannerAdapter = VerticalBannerAdapter()
        verticalBannerAdapter.add("Banner1")
        verticalBannerAdapter.add("Banner2")
        verticalBannerAdapter.add("Banner3")
        vertical_banner_view.setAdapter(verticalBannerAdapter)
        vertical_banner_view.setTransform(VerticalPageTransformer())

        cover_flow.adapter = object : BaseQuickAdapter<String, BaseViewHolder>(R.layout.item_cover_flow, ArrayList<String>().also {
            it.add("https://c-ssl.duitang.com/uploads/item/201410/31/20141031050718_AGxJy.thumb.1000_0.jpeg")
            it.add("https://c-ssl.duitang.com/uploads/item/201905/16/20190516100859_mknsy.thumb.1000_0.jpg")
            it.add("https://c-ssl.duitang.com/uploads/item/201908/26/20190826014644_haefl.thumb.1000_0.jpeg")
            it.add("https://c-ssl.duitang.com/uploads/item/201410/31/20141031050718_AGxJy.thumb.1000_0.jpeg")
            it.add("https://c-ssl.duitang.com/uploads/item/201905/16/20190516100859_mknsy.thumb.1000_0.jpg")
            it.add("https://c-ssl.duitang.com/uploads/item/201908/26/20190826014644_haefl.thumb.1000_0.jpeg")
        }) {
            override fun convert(holder: BaseViewHolder?, url: String?) {
                holder?.getView<ImageView>(R.id.item)?.run {
                    Glide.with(this@MainActivity).load(url).into(this)
                }
            }

        }
        radio_layout.onCheckedChangeIntercept = object : RadioLayout.OnCheckedChangeIntercept {
            override fun onCheckedChangeIntercept(from: RadioView?, to: RadioView): Boolean {
                loge("onCheckedChangeIntercept：$from ---> $to")
                return false
            }
        }
        radio_layout.onCheckedChangeListener = object : RadioLayout.OnCheckedChangeListener {
            override fun onCheckedChanged(layout: RadioLayout, radioView: RadioView) {
                when(radioView.id) {
                    R.id.tab_home -> {
                        loge("首页")
                        changeIcon(this@MainActivity.javaClass.name)
//                        RestartAppManager.restartApp(this@MainActivity)
                    }

                    R.id.tab_mine -> {
                        loge("我的")
                        changeIcon("${packageName}.MainAliasActivity")
                    }
                }
            }

        }

//        Ping.ping("www.baidu.com", 5, 1f, object : Ping.PingCallback {
//            override fun onPing(data: Ping.PingStatistics) {
//                tv_ping.text = "$data"
//            }
//
//        })
        HttpManager.request(HttpRequest.Builder()
            .setUrl("https://center.mfsnake.com/config/cloud")
            .setMethod(HttpMethod.POST)
            .setParams(mutableMapOf<String?, Any?>().also {
                it["username"] = "admin"
                it["password"] = "123456"
            })
            .build(), object : CallbackDelegate<String> {
            override fun onSuccess(result: String) {
                Logger.e {
                    result
                }
            }

            override fun onFailure(code: Int?, reason: String?) {
                Logger.e {
                    "code = $code, reason = $reason"
                }
            }

        })
    }

    private fun changeIcon(className: String) {
        val targetPN = ComponentName(this@MainActivity, className)
        with(packageManager) {
            if (getComponentEnabledSetting(targetPN) === PackageManager.COMPONENT_ENABLED_STATE_ENABLED) {
                Logger.e("$targetPN is enable!")
                return
            }
            setComponentEnabledSetting(componentName
                , PackageManager.COMPONENT_ENABLED_STATE_DISABLED
                , PackageManager.DONT_KILL_APP)
            setComponentEnabledSetting(targetPN
                , PackageManager.COMPONENT_ENABLED_STATE_ENABLED
                , PackageManager.DONT_KILL_APP)
            RestartAppManager.restartApp(this@MainActivity, true)
        }
    }

    private fun showFloatWindow() {
        FloatWindowManager.get("main_activity", false)
            .upAnimDuration(1_000)
            .run {
                show(TextView(this@MainActivity).apply {
                    text = "Hello FloatWindow"
                    textSize = dp2sp(18f)
                    setTextColor(Color.RED)
                    setPadding(20, 20, 20, 20)
                    onClick {
                        textSize = dp2sp(30f)
                        showToast("$text")
                    }
                }).also {
                    loge {
                        "show floating window: $it"
                    }
                    if (!FloatWindowManager.canDrawOverlays()) {
                        FloatWindowManager.openDrawOverlays()
                    }
                }
            }
    }

    private fun displaySwitchViewState(switchView: SwitchView) {
        tv_switch_view_state.text = if (switchView.isChecked) "开" else "关"
    }

    private inline fun setFlickerProgressBar() {
        with(flicker_progress_bar) {
            status = FlickerProgressBar.Status.DOWNLOADING
            onFlickerProgressBarClickListener = object : FlickerProgressBar.OnFlickerProgressBarClickListener {
                override fun onClick(view: View?, status: FlickerProgressBar.Status) {
                    Toast.makeText(this@MainActivity, status.desc, Toast.LENGTH_SHORT).show()
                }
            }
        }
        GlobalScope.launch {
            repeat(100) {
                delay(100)
                flicker_progress_bar.setProgress(it + 1)
            }
        }
    }

    override fun onAppForeground(isFirstLauncher: Boolean) {
        Logger.i("onAppForeground: $isFirstLauncher")
    }

    override fun onAppBackground(activeTime: Long) {
        Logger.i("onAppBackground：activeTime = $activeTime")
//        FloatWindowManager.get(this).dismiss()
    }

    override fun onClick(v: View?) {
        Logger.e("onClick: v = $v")
    }

    override fun onDestroy() {
        FpsMonitor.stop()
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            NetworkSpeedManager.stopTrack()
        }
        super.onDestroy()
    }

}