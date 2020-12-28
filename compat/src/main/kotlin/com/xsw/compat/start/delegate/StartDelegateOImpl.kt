package com.xsw.compat.start.delegate

import android.annotation.TargetApi
import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.widget.RemoteViews
import com.xiaosw.api.extend.checkActivityValid
import com.xiaosw.api.extend.isNull
import com.xiaosw.api.extend.tryCatch
import com.xiaosw.api.logger.Logger
import com.xiaosw.api.util.ParseMD5Util
import com.xsw.compat.R
import com.xsw.compat.start.bridge.StartBridgeActivity
import java.util.*

/**
 * @ClassName: [StartDelegateOImpl]
 * @Description:
 *
 * Created by admin at 2020-12-25
 * @Email xiaosw0802@163.com
 */
class StartDelegateOImpl : StartDelegate() {

    private val mReceiver by lazy {
        AlarmReceiver()
    }

    private val mIntentFilter by lazy {
        IntentFilter().also {
            it.addAction(ACTION_START_ACTIVITY)
            it.priority = Integer.MAX_VALUE
        }
    }

    private val mAlarmManager by lazy {
        app.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    }

    private val mKeyguardManager by lazy {
        app.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
    }

    private val mNotificationManager by lazy {
        app.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    init {
        tryCatch(showException = false) {
            app.applicationContext.unregisterReceiver(mReceiver)
        }
        tryCatch {
            app.applicationContext.registerReceiver(mReceiver, mIntentFilter)
        }
    }

    override fun startActivity(context: Context, intent: Intent): Boolean {
        return intent.checkActivityValid(context)?.also {
            if (it) {
                executeAlarm(getReceiverPendingIntent(context, Intent().also { receiverIntent ->
                    receiverIntent.action = ACTION_START_ACTIVITY
                    receiverIntent.putExtra(StartBridgeActivity.EXTRA_KEY_REAL_INTENT, intent)
                }))
            }
        }
    }

    private fun startActivityLocked(context: Context, intent: Intent) {
        Logger.i("startActivityLocked: ", TAG)
        val bridgeIntent = Intent(context, StartBridgeActivity::class.java).also {
            it.putExtra(StartBridgeActivity.EXTRA_KEY_REAL_INTENT, intent)

            val key = UUID.randomUUID().toString() + System.nanoTime()
                    + SystemClock.elapsedRealtimeNanos() + Random().nextLong()
            it.putExtra(StartBridgeActivity.EXTRA_KEY_START_ID, ParseMD5Util.parseStrToMd5L32(key))
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && inKeyguardRestrictedInputMode()) {
            startActivityByFullIntentLocked(context, bridgeIntent)
            return
        }
        Logger.i("startActivityByAlarm")
        executeAlarm(getActivityPendingIntent(context, bridgeIntent))
    }

    @TargetApi(Build.VERSION_CODES.Q)
    private fun startActivityByFullIntentLocked(context: Context, intent: Intent) {
        Logger.e("startActivityByFullIntentLocked", TAG)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            return
        }
        with(mNotificationManager) {
            if (getNotificationChannel("full_intent_channel").isNull()) {
                with(
                    NotificationChannel(
                        "full_intent_channel",
                        "title",
                        NotificationManager.IMPORTANCE_HIGH
                    )
                ) {
                    description = "content"
                    lockscreenVisibility = -1
                    enableLights(false)
                    enableVibration(false)
                    setShowBadge(false)
                    setSound(null, null)
                    setBypassDnd(true)
                    createNotificationChannel(this)
                }
            }

            Logger.e("getActivityPendingIntent: intent = $intent", TAG)
            Logger.e("getActivityPendingIntent: pi = ${getActivityPendingIntent(context, intent)}", TAG)
            getActivityPendingIntent(context, intent)?.let { pi ->
                pi.tryCatch {
                    pi.send()
                    context.startActivity(intent)
                }
                cancel("AA_TAG1", 10101)
                notify(
                    "AA_TAG1", 10101,
                    Notification.Builder(context, "full_intent_channel")
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setFullScreenIntent(pi, true)
                        .setCustomHeadsUpContentView(
                            RemoteViews(
                                context.packageName,
                                R.layout.layout_start_full_intent
                            )
                        )
                        .build()
                )
                Logger.e("show notify.", TAG)
                Handler(Looper.getMainLooper()).postDelayed({ cancel("AA_TAG1", 10101) }, 1000L)
            }
        }
    }

    private inline fun getReceiverPendingIntent(context: Context, intent: Intent) = PendingIntent
        .getBroadcast(context, 101, intent, PendingIntent.FLAG_UPDATE_CURRENT)

    private inline fun getActivityPendingIntent(context: Context, intent: Intent) = PendingIntent
        .getActivity(context, 100, intent, PendingIntent.FLAG_UPDATE_CURRENT)

    private inline fun executeAlarm(pi: PendingIntent) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val info = AlarmManager.AlarmClockInfo(System.currentTimeMillis(), pi)
            mAlarmManager.setAlarmClock(info, pi)
            return
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mAlarmManager.setExact(
                AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime(), pi
            )
            return
        }
        mAlarmManager.set(AlarmManager.RTC, System.currentTimeMillis(), pi)
    }

    private inline fun inKeyguardRestrictedInputMode() : Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            return mKeyguardManager.isKeyguardLocked
        }
        return mKeyguardManager.inKeyguardRestrictedInputMode()
    }

    private inner class AlarmReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Logger.e("onReceiver: ${intent?.action}", TAG)
            intent?.let {
                if (ACTION_START_ACTIVITY == it.action) {
                    it.getParcelableExtra<Intent>(StartBridgeActivity.EXTRA_KEY_REAL_INTENT)?.let { realIntent ->
                        startActivityLocked(app, realIntent)
                    }
                }
            }
        }

    }

    private companion object {
        const val ACTION_START_ACTIVITY = "com.xsw.compat.start.delegate.ACTION_START_ACTIVITY"

        private val TAG = StartDelegateOImpl::class.java.simpleName
    }
}