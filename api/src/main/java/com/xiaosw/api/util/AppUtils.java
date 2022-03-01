package com.xiaosw.api.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.text.TextUtils;

import com.xiaosw.api.extend.ContextKt;
import com.xiaosw.api.extend.StandardKt;
import com.xiaosw.api.manager.ThreadManager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static android.content.Context.ACTIVITY_SERVICE;

/**
 * @ClassName {@link AppUtils}
 * @Description
 *
 * @Date 2020-09-11.
 * @Author xiaosw<xiaosw0802@163.com>.
 */
public class AppUtils {

    private AppUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    /**
     * 判断App是否安装
     *
     * @param context     上下文
     * @param packageName 包名
     * @return {@code true}: 已安装<br>{@code false}: 未安装
     */
    public static boolean isInstallApp(Context context, String packageName) {
        return !StringUtils.isSpace(packageName) && IntentUtils.getLaunchAppIntent(context, packageName) != null;
    }

    /**
     * 卸载App
     *
     * @param context     上下文
     * @param packageName 包名
     */
    public static void uninstallApp(Context context, String packageName) {
        if (StringUtils.isSpace(packageName)) return;
        context.startActivity(IntentUtils.getUninstallAppIntent(packageName));
    }

    /**
     * 卸载App
     *
     * @param activity    activity
     * @param packageName 包名
     * @param requestCode 请求值
     */
    public static void uninstallApp(Activity activity, String packageName, int requestCode) {
        if (StringUtils.isSpace(packageName)) return;
        activity.startActivityForResult(IntentUtils.getUninstallAppIntent(packageName), requestCode);
    }

    /**
     * 静默卸载App
     * <p>非root需添加权限 {@code <uses-permission android:name="android.permission.DELETE_PACKAGES" />}</p>
     *
     * @param context     上下文
     * @param packageName 包名
     * @param isKeepData  是否保留数据
     * @return {@code true}: 卸载成功<br>{@code false}: 卸载成功
     */
    public static boolean uninstallAppSilent(Context context, String packageName, boolean isKeepData) {
        if (StringUtils.isSpace(packageName)) return false;
        String command = "LD_LIBRARY_PATH=/vendor/lib:/system/lib pm uninstall " + (isKeepData ? "-k " : "") + packageName;
        ShellUtils.CommandResult commandResult = ShellUtils.execCmd(command, !isSystemApp(context), true);
        return commandResult.successMsg != null && commandResult.successMsg.toLowerCase().contains("success");
    }

    /**
     * 打开App
     *
     * @param context     上下文
     * @param packageName 包名
     */
    public static void launchApp(Context context, String packageName) {
        if (StringUtils.isSpace(packageName)) return;
        context.startActivity(IntentUtils.getLaunchAppIntent(context, packageName));
    }

    /**
     * 打开App
     *
     * @param activity    activity
     * @param packageName 包名
     * @param requestCode 请求值
     */
    public static void launchApp(Activity activity, String packageName, int requestCode) {
        if (StringUtils.isSpace(packageName)) return;
        activity.startActivityForResult(IntentUtils.getLaunchAppIntent(activity, packageName), requestCode);
    }

    /**
     * 获取App包名
     *
     * @param context 上下文
     * @return App包名
     */
    public static String getAppPackageName(Context context) {
        return context.getPackageName();
    }

    /**
     * 获取App具体设置
     *
     * @param context 上下文
     */
    public static void getAppDetailsSettings(Context context) {
        getAppDetailsSettings(context, context.getPackageName());
    }

    /**
     * 获取App具体设置
     *
     * @param context     上下文
     * @param packageName 包名
     */
    public static void getAppDetailsSettings(Context context, String packageName) {
        if (StringUtils.isSpace(packageName)) return;
        context.startActivity(IntentUtils.getAppDetailsSettingsIntent(packageName));
    }

    /**
     * 获取App名称
     *
     * @param context 上下文
     * @return App名称
     */
    public static String getAppName(Context context) {
        return getAppName(context, context.getPackageName());
    }

    /**
     * 获取App名称
     *
     * @param context     上下文
     * @param packageName 包名
     * @return App名称
     */
    public static String getAppName(Context context, String packageName) {
        if (StringUtils.isSpace(packageName)) return null;
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(packageName, 0);
            return pi == null ? null : pi.applicationInfo.loadLabel(pm).toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取App图标
     *
     * @param context 上下文
     * @return App图标
     */
    public static Drawable getAppIcon(Context context) {
        return getAppIcon(context, context.getPackageName());
    }

    /**
     * 获取App图标
     *
     * @param context     上下文
     * @param packageName 包名
     * @return App图标
     */
    public static Drawable getAppIcon(Context context, String packageName) {
        if (StringUtils.isSpace(packageName)) return null;
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(packageName, 0);
            return pi == null ? null : pi.applicationInfo.loadIcon(pm);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取App路径
     *
     * @param context 上下文
     * @return App路径
     */
    public static String getAppPath(Context context) {
        return getAppPath(context, context.getPackageName());
    }

    /**
     * 获取App路径
     *
     * @param context     上下文
     * @param packageName 包名
     * @return App路径
     */
    public static String getAppPath(Context context, String packageName) {
        if (StringUtils.isSpace(packageName)) return null;
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(packageName, 0);
            return pi == null ? null : pi.applicationInfo.sourceDir;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取App版本号
     *
     * @param context 上下文
     * @return App版本号
     */
    public static String getAppVersionName(Context context) {
        return getAppVersionName(context, context.getPackageName());
    }

    /**
     * 获取App版本号
     *
     * @param context     上下文
     * @param packageName 包名
     * @return App版本号
     */
    public static String getAppVersionName(Context context, String packageName) {
        if (StringUtils.isSpace(packageName)) return null;
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(packageName, 0);
            return pi == null ? null : pi.versionName;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取App版本码
     *
     * @param context 上下文
     * @return App版本码
     */
    public static int getAppVersionCode(Context context) {
        return getAppVersionCode(context, context.getPackageName());
    }

    /**
     * 获取App版本码
     *
     * @param context     上下文
     * @param packageName 包名
     * @return App版本码
     */
    public static int getAppVersionCode(Context context, String packageName) {
        if (StringUtils.isSpace(packageName)) return -1;
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(packageName, 0);
            return pi == null ? -1 : pi.versionCode;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * 判断App是否是系统应用
     *
     * @param context 上下文
     * @return {@code true}: 是<br>{@code false}: 否
     */
    public static boolean isSystemApp(Context context) {
        return isSystemApp(context, context.getPackageName());
    }

    /**
     * 判断App是否是系统应用
     *
     * @param context     上下文
     * @param packageName 包名
     * @return {@code true}: 是<br>{@code false}: 否
     */
    public static boolean isSystemApp(Context context, String packageName) {
        if (StringUtils.isSpace(packageName)) return false;
        try {
            PackageManager pm = context.getPackageManager();
            ApplicationInfo ai = pm.getApplicationInfo(packageName, 0);
            return ai != null && (ai.flags & ApplicationInfo.FLAG_SYSTEM) != 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 判断App是否是Debug版本
     *
     * @param context 上下文
     * @return {@code true}: 是<br>{@code false}: 否
     */
    public static boolean isAppDebug(Context context) {
        return isAppDebug(context, context.getPackageName());
    }

    /**
     * 判断App是否是Debug版本
     *
     * @param context     上下文
     * @param packageName 包名
     * @return {@code true}: 是<br>{@code false}: 否
     */
    public static boolean isAppDebug(Context context, String packageName) {
        if (StringUtils.isSpace(packageName)) return false;
        try {
            PackageManager pm = context.getPackageManager();
            ApplicationInfo ai = pm.getApplicationInfo(packageName, 0);
            return ai != null && (ai.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 获取App签名
     *
     * @param context 上下文
     * @return App签名
     */
    public static Signature[] getAppSignature(Context context) {
        return getAppSignature(context, context.getPackageName());
    }

    /**
     * 获取App签名
     *
     * @param context     上下文
     * @param packageName 包名
     * @return App签名
     */
    @SuppressLint("PackageManagerGetSignatures")
    public static Signature[] getAppSignature(Context context, String packageName) {
        if (StringUtils.isSpace(packageName)) return null;
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(packageName, PackageManager.GET_SIGNATURES);
            return pi == null ? null : pi.signatures;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 封装App信息的Bean类
     */
    public static class AppInfo {

        private String name;
        private Drawable icon;
        private String packageName;
        private String packagePath;
        private String versionName;
        private int versionCode;
        private boolean isSystem;

        public Drawable getIcon() {
            return icon;
        }

        public void setIcon(Drawable icon) {
            this.icon = icon;
        }

        public boolean isSystem() {
            return isSystem;
        }

        public void setSystem(boolean isSystem) {
            this.isSystem = isSystem;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPackageName() {
            return packageName;
        }

        public void setPackageName(String packagName) {
            this.packageName = packagName;
        }

        public String getPackagePath() {
            return packagePath;
        }

        public void setPackagePath(String packagePath) {
            this.packagePath = packagePath;
        }

        public int getVersionCode() {
            return versionCode;
        }

        public void setVersionCode(int versionCode) {
            this.versionCode = versionCode;
        }

        public String getVersionName() {
            return versionName;
        }

        public void setVersionName(String versionName) {
            this.versionName = versionName;
        }

        /**
         * @param name        名称
         * @param icon        图标
         * @param packageName 包名
         * @param packagePath 包路径
         * @param versionName 版本号
         * @param versionCode 版本码
         * @param isSystem    是否系统应用
         */
        public AppInfo(String packageName, String name, Drawable icon, String packagePath,
                       String versionName, int versionCode, boolean isSystem) {
            this.setName(name);
            this.setIcon(icon);
            this.setPackageName(packageName);
            this.setPackagePath(packagePath);
            this.setVersionName(versionName);
            this.setVersionCode(versionCode);
            this.setSystem(isSystem);
        }

        @Override
        public String toString() {
            return "App包名：" + getPackageName() +
                    "\nApp名称：" + getName() +
                    "\nApp图标：" + getIcon() +
                    "\nApp路径：" + getPackagePath() +
                    "\nApp版本号：" + getVersionName() +
                    "\nApp版本码：" + getVersionCode() +
                    "\n是否系统App：" + isSystem();
        }
    }

    /**
     * 获取App信息
     * <p>AppInfo（名称，图标，包名，版本号，版本Code，是否系统应用）</p>
     *
     * @param context 上下文
     * @return 当前应用的AppInfo
     */
    public static AppInfo getAppInfo(Context context) {
        return getAppInfo(context, context.getPackageName());
    }

    /**
     * 获取App信息
     * <p>AppInfo（名称，图标，包名，版本号，版本Code，是否系统应用）</p>
     *
     * @param context     上下文
     * @param packageName 包名
     * @return 当前应用的AppInfo
     */
    public static AppInfo getAppInfo(Context context, String packageName) {
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(packageName, 0);
            return getBean(pm, pi);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 得到AppInfo的Bean
     *
     * @param pm 包的管理
     * @param pi 包的信息
     * @return AppInfo类
     */
    private static AppInfo getBean(PackageManager pm, PackageInfo pi) {
        if (pm == null || pi == null) return null;
        ApplicationInfo ai = pi.applicationInfo;
        String packageName = pi.packageName;
        String name = ai.loadLabel(pm).toString();
        Drawable icon = ai.loadIcon(pm);
        String packagePath = ai.sourceDir;
        String versionName = pi.versionName;
        int versionCode = pi.versionCode;
        boolean isSystem = (ApplicationInfo.FLAG_SYSTEM & ai.flags) != 0;
        return new AppInfo(packageName, name, icon, packagePath, versionName, versionCode, isSystem);
    }

    /**
     * 获取所有已安装App信息
     * <p>{@link #getBean(PackageManager, PackageInfo)}（名称，图标，包名，包路径，版本号，版本Code，是否系统应用）</p>
     * <p>依赖上面的getBean方法</p>
     *
     * @param context 上下文
     * @return 所有已安装的AppInfo列表
     */
    public static List<AppInfo> getAppsInfo(Context context) {
        List<AppInfo> list = new ArrayList<>();
        PackageManager pm = context.getPackageManager();
        // 获取系统中安装的所有软件信息
        List<PackageInfo> installedPackages = pm.getInstalledPackages(0);
        for (PackageInfo pi : installedPackages) {
            AppInfo ai = getBean(pm, pi);
            if (ai == null) continue;
            list.add(ai);
        }
        return list;
    }

    /**
     * 取application下的APP_KEY标签值
     *
     * @param context
     * @return
     */
    public static String getAppKey(Context context) {
        return getMetaDataValue(context, "com.shareinstall.APP_KEY");
    }

    /**
     * 取application下的APP_KEY标签值
     *
     * @return
     */
    public static String getAppKey() {
        return getMetaDataValue(appContext, "com.shareinstall.APP_KEY");
    }

    /**
     * 取application下的APP_TYPEID标签值
     *
     * @param context
     * @return
     */
    public static String getAppTypeId(Context context) {
        return getMetaDataValue(context, "com.shareinstall.APP_TYPEID");
    }

    /**
     * 取application下的APP_TYPEID标签值
     *
     * @return
     */
    public static String getAppTypeId() {
        return getMetaDataValue(appContext, "com.shareinstall.APP_TYPEID");
    }

    /**
     * 取application下的meta_data标签值
     *
     * @param context
     * @param dataName
     * @return
     */
    public static String getMetaDataValue(Context context, String dataName) {
        if (context == null || TextUtils.isEmpty(dataName)) {
            return "";
        }
        context = context.getApplicationContext();
        ApplicationInfo appInfo;
        String value = "";
        try {
            appInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            if (appInfo == null || appInfo.metaData.get(dataName) == null) {
                return "";
            }
            value = appInfo.metaData.get(dataName).toString();
        } catch (Exception e) {
            e.printStackTrace();
            return value;
        }

        return value;
    }

    /**
     * 获取系统版本OS
     *
     * @return
     */
    public static String getSystemVersion() {
        return Build.VERSION.RELEASE;
    }

    /**
     * 获取机型
     *
     * @return
     */
    public static String getPhoneModel() {
        return Build.MODEL;
    }


    /**
     * 获取AndroidID
     *
     * @param context
     * @return
     */
    public static String getAndroidID(Context context) {
        String androidId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        return androidId;
    }

    /**
     * 获取手机上安装的所有应用，该方法为排除了系统应用
     *
     * @param context
     * @return
     */
    public static List<PackageInfo> getAllInstalledApps(Context context) {
        PackageManager packageManager = context.getApplicationContext().getPackageManager();
        // 非系统应用列表
        List<PackageInfo> userList = new ArrayList<>();
        try {
            // 所有应用列表
            List<PackageInfo> systemList = packageManager.getInstalledPackages(0);
            for (PackageInfo packageInfo : systemList) {
                //判断是否为非系统预装的应用程序
                if ((packageInfo.applicationInfo.flags & packageInfo.applicationInfo.FLAG_SYSTEM) <= 0) {
                    userList.add(packageInfo);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return userList;
    }

    /**
     * 是否被授权 应用手机相关组件的使用统计权限
     */
    public static boolean isGrantedOps(Context context) {
        boolean isGrantedOps = false;
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            AppOpsManager appOpt = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
            int model = appOpt.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, android.os.Process.myUid(),
                    context.getPackageName());
            isGrantedOps = model == AppOpsManager.MODE_ALLOWED;
        }
        return isGrantedOps;
    }

    public static boolean hasPermission(Context ctx, String permission) {
        return ContextKt.checkSelfPermissionCompat(ctx, permission);
    }

    public static boolean hasNetworkPermission() {
        return hasPermission(appContext, "android.permission.INTERNET");
    }

    public static boolean hasPhoneStatePermission() {
        return hasPermission(appContext, "android.permission.READ_PHONE_STATE");
    }

    private static Context appContext;

    public static void setAppContext(Context ctx) {
        appContext = ctx.getApplicationContext();
    }

    public static Context getAppContext() {
        return appContext;
    }

    public static void isAppForeground(final CallBack callBack) {
        isAppForeground(appContext, callBack);
    }

    public static void isAppForeground(final Context context, final CallBack callBack) {
        if (StandardKt.isNull(context)) {
            callErrorInMainThread(callBack);
            return;
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            callBackInMainThread(callBack, _isAppForeground(context));
            return;
        }
        ThreadManager.execute(ThreadManager.ThreadType.THREAD_TYPE_WORK, new Runnable() {
            @Override
            public void run() {
                try {
                    ActivityManager am = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
                    List<ActivityManager.RunningAppProcessInfo> runnings = am.getRunningAppProcesses();
                    if (StandardKt.isNull(runnings)) {
                        callBackInMainThread(callBack, false);
                        return;
                    }
                    for (ActivityManager.RunningAppProcessInfo running : runnings) {
                        if (StandardKt.isNull(running)) {
                            continue;
                        }
                        if (running.processName.equals(context.getApplicationInfo().processName)) {
                            if (running.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND
                                    || running.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_VISIBLE) {
                                callBackInMainThread(callBack, true);
                            } else {
                                callBackInMainThread(callBack, false);
                            }
                            return;
                        }
                    }
                    callBackInMainThread(callBack, false);
                } catch (Exception e) {
                    callBackInMainThread(callBack, false);
                }
            }
        });
    }

    private static boolean _isAppForeground(Context context) {
        boolean isForeground = false;
        if (StandardKt.isNull(context)) {
            return false;
        }
        ActivityManager am = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            try {
                List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
                if (StandardKt.isNull(runningProcesses)) {
                    return false;
                }
                for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                    if (StandardKt.isNull(processInfo)) {
                        continue;
                    }
                    if (StandardKt.isNull(processInfo.pkgList)) {
                        continue;
                    }
                    if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                        for (String pkgName : processInfo.pkgList) {
                            if (StringUtils.equals(pkgName, context.getPackageName())) {
                                isForeground = true;
                            }
                        }
                    }
                }
            } catch (Exception e) {
                return false;
            }
        } else {
            try {
                List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
                if (StandardKt.isNull(taskInfo)) {
                    return false;
                }
                ComponentName componentInfo = taskInfo.get(0).topActivity;
                if (StringUtils.equals(componentInfo.getPackageName(), context.getPackageName())) {
                    isForeground = true;
                }
            } catch (SecurityException e) {
                return false;
            }
        }

        return isForeground;
    }

    private static Handler handler = new Handler(Looper.getMainLooper());

    private static void callBackInMainThread(final CallBack callBack, final boolean isForeground) {
        if (StandardKt.isNull(callBack)) {
            return;
        }

        if (StandardKt.isNull(handler)) {
            handler = new Handler(Looper.getMainLooper());
        }

        handler.post(new Runnable() {
            @Override
            public void run() {
                callBack.callBack(isForeground);
            }
        });
    }

    private static void callErrorInMainThread(final CallBack callBack) {
        if (StandardKt.isNull(callBack)) {
            return;
        }

        if (StandardKt.isNull(handler)) {
            handler = new Handler(Looper.getMainLooper());
        }

        handler.post(new Runnable() {
            @Override
            public void run() {
                callBack.onError();
            }
        });
    }

    public static abstract class CallBack implements ICallBack {
        @Override
        public void onError() {
        }
    }

    public interface ICallBack {
        void callBack(boolean isForeground);

        void onError();
    }

}