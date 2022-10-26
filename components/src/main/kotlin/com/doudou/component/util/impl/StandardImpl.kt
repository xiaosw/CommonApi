package com.doudou.component.util.impl

import com.doudou.component.Component
import com.doudou.component.util.Standard

/**
 * ClassName: [StandardImpl]
 * Description:
 *
 * Create by X at 2022/06/24 11:30.
 */
internal class StandardImpl : Standard, Component {

    override fun isMainThread(): Boolean {

    }

    override fun providerComponentClass(): Class<*> {
        return Standard::class.java
    }

}