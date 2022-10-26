package com.doudou.component

/**
 * ClassName: [Component]
 * Description:
 *
 * Create by X at 2022/06/24 11:46.
 */
interface Component {

    fun inject() {
        val name = providerComponentName() ?: providerComponentClass().name
        ComponentManager.register(name, this)
    }

    fun providerComponentName() : String? = null

    fun providerComponentClass() : Class<*>
}