package com.doudou.component.def

import com.doudou.component.Component
import com.doudou.component.params.CommonParams

/**
 * ClassName: [DefCommonParams]
 * Description:
 *
 * Create by X at 2022/06/24 10:33.
 */
internal class DefCommonParams : CommonParams, Component {

    private val params by lazy {
        mutableMapOf<String?, Any?>()
    }

    override fun commonParams() = params

    override fun providerComponentClass(): Class<*> {
        return CommonParams::class.java
    }
}