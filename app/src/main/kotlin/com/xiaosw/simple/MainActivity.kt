package com.xiaosw.simple

import android.content.*
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.xiaosw.api.annotation.AutoAdjustDensity
import com.xiaosw.api.extend.dp2px
import com.xiaosw.api.logger.Logger
import com.xiaosw.api.manager.ActivityLifeManager
import com.xiaosw.api.manager.DensityManager
import com.xiaosw.api.util.FpsMonitor
import com.xiaosw.api.util.ToastUtil
import com.xsw.compat.start.StartManager
import com.xsw.ui.anim.path.PathAnimator
import com.xsw.ui.widget.FlickerProgressBar
import com.xsw.ui.widget.FlowLayout
import com.xsw.ui.widget.SwitchView
import com.xsw.ui.widget.banner.BaseBannerIndicator
import com.xsw.ui.widget.banner.adapter.BannerAdapter
import com.xsw.ui.widget.banner.tranforme.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

/**
 * @ClassName: [MainActivity]
 * @Description:
 *
 * Created by admin at 2019-09-27 16:42
 * @Email xiaosw0802@163.com
 */
@AutoAdjustDensity(baseDp = 360f, baseDpByWidth = true)
class MainActivity : AppCompatActivity(), ActivityLifeManager.AppLifecycleListener {

    private val mFpsCallback by lazy {
        object : FpsMonitor.OnFpsMonitorListener {
            override fun onFpsMonitor(fps: Int) {
//                Logger.i("fps: $fps")
//                tv_fps.text = "FPS:$fps"
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        DensityManager.addThirdAutoAdjustPage(javaClass)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ActivityLifeManager.registerAppLifecycleListener(this)

        registerReceiver(object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                val result = StartManager.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("pinduoduo://")))
                Logger.i("result = $result")
            }

        }, IntentFilter().also {
            it.addAction(Intent.ACTION_SCREEN_OFF)
        })

        FpsMonitor.start(mFpsCallback)

        tv_text.setOnClickListener {
            //startActivity(Intent(this, NotRegisterActivity::class.java))
            startActivity(Intent(this, MaterialDesignActivity::class.java))
//            val main = ReflectCompat.forName(MainActivity::class.java.name)
//            Log.e("MainActivity", "onCreate: $main")
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
            start()
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
        banner_view.setTransform(VerticalPageTransformer())
        banner_view.bindIndicator(BaseBannerIndicator(this).also {
            val p = dp2px(8f).toInt()
            it.setPadding(p, p, p, p)
            it.forceCircle = false
            it.scrollEffect = BaseBannerIndicator.ScrollEffect.SLIDE
            it.indicatorHeight = 20f
            it.indicatorWidth = 80f
        })
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
    }

}