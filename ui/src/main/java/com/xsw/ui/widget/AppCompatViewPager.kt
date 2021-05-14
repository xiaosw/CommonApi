package com.xsw.ui.widget

import android.content.Context
import android.database.DataSetObserver
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.xiaosw.api.extend.tryCatch
import com.xiaosw.api.extend.use
import com.xiaosw.api.logger.Logger
import com.xsw.ui.R

/**
 * ClassName: [AppCompatViewPager]
 * Description:
 *
 * Create by X at 2021/05/13 16:40.
 */
open class AppCompatViewPager @JvmOverloads constructor(
    context: Context
    , attrs: AttributeSet? = null
) : ViewPager(context, attrs) {

    private val mDataSetObserver by lazy {
        object : DataSetObserver() {
            override fun onChanged() {
                super.onChanged()
                onDataSetChanged()
            }

            override fun onInvalidated() {
                super.onInvalidated()
                onDataSetChanged()
            }
        }
    }

    var scrollEnable = true

    init {
        parseAttrs(context, attrs)
        addOnAdapterChangeListener { _, oldAdapter, newAdapter ->
            oldAdapter?.unregisterDataSetObserver(mDataSetObserver)
            newAdapter?.registerDataSetObserver(mDataSetObserver)
        }
    }

    private inline fun parseAttrs(context: Context, attrs: AttributeSet? = null) {
        attrs?.run {
            context.obtainStyledAttributes(this, R.styleable.AppCompatViewPager).use {
                scrollEnable = getBoolean(R.styleable.AppCompatViewPager_disableScroll, scrollEnable)
            }
        }
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        if (!scrollEnable) {
            return false
        }
        return super.onInterceptTouchEvent(ev)
    }

    override fun onTouchEvent(ev: MotionEvent?): Boolean {
        if (!scrollEnable) {
            return false
        }
        return super.onTouchEvent(ev)
    }

    override fun setAdapter(adapter: PagerAdapter?) {
        super.setAdapter(adapter)
        onDataSetChanged()
    }

    internal open fun onDataSetChanged() {
        // nothing
    }

}