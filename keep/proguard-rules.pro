# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

#--保活 start---
-keep class com.kecarle.**{*;}
-keep class com.qaz.aaa.e.**{*;}
-keepattributes *Annotation*
-keep class com.kalive.eventbus.Subscribe{*;}
-keepclassmembers class * {
    @com.kalive.eventbus.Subscribe <methods>;
}
-keep class com.kalive.eventbus.ThreadMode { *; }
-keep class com.fire.phoenix.**{*;}
-keep class com.qaz.aaa.e.keeplive.daemon.NativeKeepAlive{*;}
-keep class com.qaz.aaa.e.keeplive.daemon.DaemonMain{*;}
-keep class com.kalive.manager.KALogWorker{*;}
-keep class com.fire.phoenix.core.FPLauncher$LauncherWorker{*;}
-keep class com.fire.phoenix.core.utils.FPLauncherUtils$LauncherWorker{*;}
#--保活 end--