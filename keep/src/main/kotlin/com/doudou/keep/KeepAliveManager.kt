package com.doudou.keep

import android.app.Application
import android.content.Context
import android.content.Intent
import android.util.Log
import com.doudou.log.Logger
import com.kalive.IKeepaliveCustomParams
import com.kalive.WSSAdSdk
import com.kalive.biz.config.IKeepaliveUrlsProvider
import com.kalive.scene.*
import com.kalive.utils.AppProcessUtil
import com.qaz.aaa.e.keeplive.QAZKeepLive
import com.qaz.aaa.e.keeplive.notification.ICustomNotificationCreator
import org.json.JSONObject

/**
 * ClassName: [KeepAliveManager]
 * Description:
 *
 * Create by X at 2021/12/04 15:36.
 */
object KeepAliveManager {

    /**
     * （必须）
     * 必须在application oncreate中调用，所有进程都要调，请勿判断进程,无隐私政策问题
     * target:26,ICustomNotificationCreator这个传null即可；
     * target>26,会默认显示天气通知栏，如果需要自定义，请按下面的方式来实现
     * @param app
     * @return 是否是保活进程，保活进程请勿做任何操作
     */
    fun applicationCreate(app: Application) = QAZKeepLive.preinit(app, object : ICustomNotificationCreator {
        override fun getSmallIconResId(): Int {
            return 0
        }

        override fun getLargeIconResId(): Int {
            return 0
        }

        override fun notificationMode(): Int {
            //传2，自己推送并更新天气通知栏；sdk自己更新传0
            return 0
        }

        override fun useCustomNotification(): Boolean {
            return false
        }

        override fun createNotification(context: Context, intent: Intent) = null

        override fun getReceiverClass(): Class<*>? {
            //点击后通知栏后设置BroadcastReceiver来接收
            return null
        }
    }).also {
        Logger.e("is keep process: $it")
        if (!it) {
            //第一次启动需要在隐私政策同意之后调用，之后在此处调用
            init(app)
            //开启保活
            start(true)
        }
    }

    /**
     * （必须）
     * 必须在Application的attachBaseContext方法中调用，无隐私政策问题，请勿判断进程
     * 例如：attachBaseContext(application,WelcomeActivity.class)
     *
     * 注意：第二个参数clazz如果传null，视为不启用adj为0的方案
     * 设置adj为0存在的问题：冷启动时部分手机会闪一下，冷启动较慢；好处：进程优先级adj=0，系统不到万不得已不会杀死你
     *
     * @param clazz 原项目启动页activity，启动时需要中转
     * @return 是否是保活进程，保活进程请勿初始化任何逻辑
     */
    fun attachBaseContext(app: Application?, clazz: Class<*>? = null) = QAZKeepLive.attachBaseContext(app, clazz).also {
        if (BuildConfig.DEBUG) {
            Log.e(javaClass.simpleName, "attachBaseContext: is keep alive process: $it")
        }
    }

    /**
     * （必须）
     * 每次冷启动时调用，首次启动同意隐私政策后调用，（必须）
     * 需要与setWallpaper(Context context)方法和onMainCreate(Context context)方法在同一进程中执行，一般为主进程
     * 外部接入:IKeepaliveUrlsProvider传null;IKeepaliveCustomParams参照demo，请看注释
     * 内部接入:都需要实现
     * @param app
     */
    fun init(app: Application) {
        val customParams: IKeepaliveCustomParams = KeepAliveCustomParams()
        val linksProvider: IKeepaliveUrlsProvider = KeepAliveUrlsProvider()
        QAZKeepLive.init(app, customParams, linksProvider, false, false)
        initSceneSdk(app)
        stopNotification(true)
    }

    /**
     * 弹出设置壁纸界面（如果需要）
     * 需要与init(Application app)方法和onMainCreate(Context context)方法在统一进程中执行，一般为主进程
     * @param context
     */
    fun setWallpaper(context: Context?) {
        QAZKeepLive.setWallpaper(context)
    }

    /**
     * （必须）
     * 初始化壁纸相关
     * @param context
     */
    private fun initSceneSdk(context: Context) {
        if (AppProcessUtil.isMainProcess(context)) {
            val sceneConfig = SceneConfig.Builder()
                .sceneController(object : ISceneController {
                    override fun isSceneOn(scene: Int): Boolean {
                        return true
                    }

                    override fun wpBgName() = null
                })
                .activityClickListener { }
                .wallpaperListener(object : WallpaperListener {
                    override fun shouldSetWallpaper(): Boolean {
                        return true
                    }

                    override fun onWallpaperSettingsPageShow() {}
                    override fun onWallpaperSettingsPageClose(
                        type: Int,
                        state: Int,
                        wpCode: String
                    ) {
                    }

                    override fun onWallpaperSettingsSuccess() {}
                    override fun onWallpaperSettingsPageFailShow(wpCode: String) {}
                    override fun takeWpDrawable() = null

                    override fun takeWpCode() = null

                    override fun onConditionResult(result: Boolean) {}
                })
                .build()
            WSSSceneSdk.init(context, sceneConfig)
        }
    }

    /**
     * （必须）
     * 开启保活:true开启  false关闭；调用一次后，永久生效（已缓存到sp），默认关闭;如果无需云控开关，直接在applicationCreate后调用
     */
    fun start(isStart: Boolean) {
        QAZKeepLive.startKeepAlive(isStart)
    }

    /**
     * （非必须）
     * target>26才需要，=26请无视
     * 如果需要移除通知，每次冷启动都要主动调用，否则会一直显示
     * true移除通知栏并停止前台服务  false重新启动前台服务并显示通知栏
     * 注意：这里会启动保活逻辑，如果有开关请做好判断
     */
    fun stopNotification(isStop: Boolean) {
        QAZKeepLive.removeNotification(isStop)
    }

    /**
     * （非必须，需要自主更新天气通知栏数据的才需要）
     * target>26才需要，=26请无视
     * 对外暴露
     * 使用客户端自己获取的天气数据，必须在主进程中调用
     */
    fun pushWeatherData(context: Context?, jo: JSONObject?) {
        QAZKeepLive.pushWeatherData(context, jo)
    }

    /**
     * （非必须）内部使用sdk中壁纸时需要
     * 传入归因渠道,QAZKeepLive.init(app,customParams,linksProvider,TEST_SERVER,debug)初始化后调用，提前调会报错
     */
    fun onVTAInfoUpdate(srcPlat: String?, srcQid: String?) {
        WSSAdSdk.onVTAInfoUpdate(srcPlat, srcQid)
    }

}