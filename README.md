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
    implementation 'com.github.xiaosw:CommonApi:1.0.1'
}
```

## Api 使用
* 屏幕适配 AutoAdjustDensity
    ```
    考虑使用屏幕适配方案，对三方页面可能会存在影响，所以需要在需要使用适配的页面，
    使用 AutoAdjustDensity 注解，其中 baseDp 为设计稿的 dp 尺寸，使用注解后，
    对应页面 xml 无需考虑不同屏幕兼容问题，框架会自动计算。    
    ```

## FAQ