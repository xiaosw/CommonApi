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
    compile 'com.github.xiaosw:CommonApi:1.0.0'
}
```
 
## FAQ
如果设备多个项目引用 [CommonApi](https://github.com/xiaosw/CommonApi) 导致安装失败，可使用以下配置解决：
```xml
<provider
    android:authorities="you package.provider.InitAndroidContextProvider"
    android:name="com.xiaosw.api.provider.InitAndroidContextProvider"
    tools:replace="android:authorities"/>
```