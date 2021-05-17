package com.xsw.ui.widget

import android.content.Context
import android.database.DataSetObserver
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.LinearLayout
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.xiaosw.api.extend.use
import com.xiaosw.api.logger.Logger
import com.xiaosw.api.manager.WeakRegisterManager
import com.xsw.ui.R
import com.xsw.ui.widget.listener.OnDataSetChangeListener

/**
 * ClassName: [AppCompatViewPager]
 * Description:
 *
 * Create by X at 2021/05/13 16:40.
 */
open class AppCompatViewPager @JvmOverloads constructor(
    context: Context
    , attrs: AttributeSet? = null
) : ViewPager(context, attrs)
    , WeakRegisterManager.IRegisterManager<OnDataSetChangeListener>
    , OnDataSetChangeListener {

    private val mDataSetObserver by lazy {
        object : DataSetObserver() {
            override fun onChanged() {
                super.onChanged()
                dispatchDataSetChanged()
            }

            override fun onInvalidated() {
                super.onInvalidated()
                dispatchDataSetChanged()
            }
        }
    }

    private val mWeakRegisterManager by lazy {
        WeakRegisterManager<OnDataSetChangeListener>()
    }

    private var mOrientationPageTransformer: OrientationPageTransformer? = null

    var scrollEnable = true

    init {
        parseAttrs(context, attrs)
        addOnAdapterChangeListener { _, oldAdapter, newAdapter ->
            oldAdapter?.unregisterDataSetObserver(mDataSetObserver)
            newAdapter?.registerDataSetObserver(mDataSetObserver)
            register(this)
        }
    }

    private inline fun parseAttrs(context: Context, attrs: AttributeSet? = null) {
        attrs?.run {
            context.obtainStyledAttributes(this, R.styleable.AppCompatViewPager).use {
                scrollEnable = getBoolean(R.styleable.AppCompatViewPager_disableScroll, scrollEnable)
            }
        }
    }

    override fun setPageTransformer(reverseDrawingOrder: Boolean, transformer: PageTransformer?) {
        mOrientationPageTransformer = transformer as? OrientationPageTransformer
        super.setPageTransformer(reverseDrawingOrder, transformer)
    }

    override fun setPageTransformer(
        reverseDrawingOrder: Boolean,
        transformer: PageTransformer?,
        pageLayerType: Int
    ) {
        mOrientationPageTransformer = transformer as? OrientationPageTransformer
        super.setPageTransformer(reverseDrawingOrder, transformer, pageLayerType)
    }

    private var isSwapCoordinate = false

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        if (!scrollEnable) {
            return false
        }
        isSwapCoordinate = swapXYIfNeeded(ev)
        val intercept = super.onInterceptTouchEvent(ev)
        if (isSwapCoordinate) {
            requestDisallowInterceptTouchEvent(true)
            swapXYIfNeeded(ev)
        }
        Logger.e("$intercept, ${ev?.action}, ${MotionEvent.ACTION_DOWN}, ${MotionEvent.ACTION_MOVE}")
        return intercept
    }

    override fun onTouchEvent(ev: MotionEvent?): Boolean {
        if (!scrollEnable) {
            return false
        }
        if (isSwapCoordinate) {
           swapXYIfNeeded(ev)
        }
        Logger.e("${ev?.action}")
        return super.onTouchEvent(ev)
    }

    private inline fun swapXYIfNeeded(ev: MotionEvent?) : Boolean {
        ev?.apply {
            val isVertical = mOrientationPageTransformer?.orientation === OrientationPageTransformer.VERTICAL
            if (!isVertical) {
                return false
            }
            val w = measuredWidth
            val h = measuredHeight
            val newX = y / h * w
            val newY = x / w * h
            setLocation(newX, newY)
            return true
        }
        return false
    }


    override fun setAdapter(adapter: PagerAdapter?) {
        super.setAdapter(adapter)
        dispatchDataSetChanged()
    }

    private fun dispatchDataSetChanged() {
        mWeakRegisterManager?.forEach {
            it.onDataSetChanged()
        }
    }

    override fun register(t: OnDataSetChangeListener) {
        mWeakRegisterManager.register(t)
    }

    override fun unregister(t: OnDataSetChangeListener) = mWeakRegisterManager.unregister(t)

    override fun clear() = mWeakRegisterManager.clear().also {
        register(this)
    }

    override fun onDataSetChanged() {
    }

    interface OrientationPageTransformer : PageTransformer {

        val orientation: Int
            get() = HORIZONTAL

        companion object {
            const val HORIZONTAL = LinearLayout.HORIZONTAL
            const val VERTICAL = LinearLayout.VERTICAL
        }

    }

}