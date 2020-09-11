# [CommonApi](https://github.com/xiaosw/CommonApi)
工具集。

## Usage
## JitPack.io
I strongly recommend https://jitpack.io

```groovy
repositories {
    // ...
    maven { url "https://jitpack.io" }
}

dependencies {
    implementation 'com.github.xiaosw:CommonApi:1.0.2'
}
```

## Api 使用

**1、屏幕适配**

    a. 单个页面适配。在需要适配的 Activity 中使用 @AutoAdjustDensity 注解
    ，同时可自由配置适配基数 baseDp (float, 一般为设计稿dp尺寸)及适配参考线
    baseDpByWidth(true: 以宽作为参考适配，false：以高作为参考适配)
    
    b. 全局适配。application 节点下添加以下配置
        // 适配基数(float), 一般为设计稿dp尺寸
        <meta-data android:name="APP_BASE_DP" android:value="320" />
        // 适配参考基线。true: 以宽作为参考适配，false：以高作为参考适配    
        <meta-data android:name="APP_BASE_DP_BY_WIDTH" android:value="true"/>
        // 是否启用全局适配。true： 启用；false：禁用，仅针对全局配置生效，不会影响 @AutoAdjustDensity 及白名单配置   
        <meta-data android:name="APP_BASE_DP_ENABLE" android:value="true"/>

    c. 白名单适配:DensityManager.addThirdAutoAdjustPage(clazz: Class<out Activity>)。
    这里需要注意，白名单依赖全局配置的适配基数及参考基线，但不受全局启用/禁止的限制

    优先级说明：AutoAdjustDensity 注解 > xml 配置
## FAQ