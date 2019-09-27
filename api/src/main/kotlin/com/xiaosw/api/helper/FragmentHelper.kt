package com.xiaosw.api.helper

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

/**
 * @ClassName [FragmentHelper]
 * @Description
 *
 * @Date 2019-08-29.
 * @Author xiaosw<xiaosw0802@163.com>.
 */
object FragmentHelper {

    fun <F : Fragment> newInstance(
        fragmentManager: FragmentManager,
        fragmentClazz: Class<F>,
        tag: String = fragmentClazz.name,
        args: Bundle? = null) : F {
        return (fragmentManager.findFragmentByTag(tag) ?: fragmentClazz.newInstance().also {
            it.arguments = args
        }) as F
    }

}
