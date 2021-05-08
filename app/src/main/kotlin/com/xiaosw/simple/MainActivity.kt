package com.xiaosw.simple

import android.content.*
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.xiaosw.api.annotation.AutoAdjustDensity
import com.xiaosw.api.logger.Logger
import com.xiaosw.api.manager.ActivityLifeManager
import com.xiaosw.api.manager.DensityManager
import com.xiaosw.api.reflect.ReflectCompat
import com.xiaosw.api.util.FpsMonitor
import com.xiaosw.api.util.ToastUtil
import com.xsw.compat.start.StartManager
import com.xsw.ui.widget.FlickerProgressBar
import com.xsw.ui.widget.FlowLayout
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random
import kotlin.random.nextUInt

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