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
import kotlin.math.abs

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

    private var isVertical = false
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
        isVertical = ((transformer as? OrientationPageTransformer)?.orientation
                === OrientationPageTransformer.VERTICAL)
        super.setPageTransformer(reverseDrawingOrder, transformer)
    }

    override fun setPageTransformer(
        reverseDrawingOrder: Boolean,
        transformer: PageTransformer?,
        pageLayerType: Int
    ) {
        isVertical = ((transformer as? OrientationPageTransformer)?.orientation
                === OrientationPageTransformer.VERTICAL)
        super.setPageTransformer(reverseDrawingOrder, transformer, pageLayerType)
    }

    private var isScroll = false
    private var mLastY = 0f
    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (isVertical) {
            onTouchEvent(ev)
            if (isScroll && ev?.action === MotionEvent.ACTION_UP) {
                ev?.run {
                    action = MotionEvent.ACTION_CANCEL
                    return super.dispatchTouchEvent(ev)
                }
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        if (isVertical) {
            requestDisallowInterceptTouchEvent(true)
        }
        return super.onInterceptTouchEvent(ev)
    }

    override fun onTouchEvent(ev: MotionEvent?): Boolean {
        if (!scrollEnable) {
            return false
        }
        ev?.run {
            if (isVertical) {
                when(action and MotionEvent.ACTION_MASK) {
                    MotionEvent.ACTION_DOWN -> {
                        isScroll = false
                    }

                    MotionEvent.ACTION_MOVE -> {
                        if (abs(y - mLastY) > 3) {
                            isScroll = true
                        }
                        mLastY = y
                    }
                }
                return useSwapXYIfNeeded(this) {
                    super.onTouchEvent(this)
                }
            }
        }
        return super.onTouchEvent(ev)
    }

    private inline fun useSwapXYIfNeeded(ev: MotionEvent, action: MotionEvent.() -> Boolean) : Boolean {
        Logger.e("isVertical: $isVertical")
        return ev?.run {
            if (!isVertical) {
                return false
            }
            val w = measuredWidth
            val h = measuredHeight
            val originalX = x
            val originalY= y
            val newX = y / h * w
            val newY = x / w * h
            setLocation(newX, newY)
            return action.invoke(this).also {
                setLocation(originalX, originalY)
            }
        }
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