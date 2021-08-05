package com.xsw.ui.widget.banner

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.viewpager.widget.PagerAdapter
import com.xiaosw.api.extend.isNull
import com.xiaosw.api.extend.use
import com.xiaosw.api.register.RegisterDelegate
import com.xsw.ui.R
import com.xsw.ui.widget.AppCompatViewPager
import com.xsw.ui.widget.banner.adapter.BannerAdapter

/**
 * ClassName: [LoopViewPager]
 * Description:
 *
 * Create by X at 2021/05/14 11:52.
 */
class LoopViewPager @JvmOverloads constructor (
    context: Context, attrs: AttributeSet? = null
) : AppCompatViewPager(context, attrs) {

    private val mNextTask by lazy {
        object : Runnable {
            override fun run() {
                setSuperCurrentItem(superCurrentItem() + 1)
                removeCallbacks(this)
                postDelayed(this, periodMillis)
            }
        }
    }

    private val mOnPageChangeListeners by lazy {
        RegisterDelegate.createWeak<OnPageChangeListener>()
    }

    private val mPageChangeListener by lazy {
        object : OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                val realPosition = toRealItem(position)
                mOnPageChangeListeners.forEach {
                    it.onPageScrolled(realPosition, positionOffset, positionOffsetPixels)
                }
            }

            override fun onPageSelected(position: Int) {
                mOnPageChangeListeners.forEach {
                    it.onPageSelected(toRealItem(position))
                }
            }

            override fun onPageScrollStateChanged(state: Int) {
                mOnPageChangeListeners.forEach {
                    it.onPageScrollStateChanged(state)
                }
            }

        }
    }

    private var mWindowVisibility = false
    private var mBannerAdapter: BannerAdapter<*>? = null
    private var isLooping = false
    private var isStopLoopFromUser = false

    /**
     * start loop delay time
     */
    var delayMillis = 3_000L

    /**
     * auto loop period
     */
    var periodMillis = 5_000L

    /**
     * auto loop is enable
     */
    var autoLoop = true

    init {
        parseAttrs(context, attrs)
        super.addOnPageChangeListener(mPageChangeListener)
    }

    private fun parseAttrs(context: Context, attrs: AttributeSet? = null) {
        context.obtainStyledAttributes(attrs, R.styleable.LoopViewPager).use {
            autoLoop = getBoolean(R.styleable.LoopViewPager_autoLoop, autoLoop)
            delayMillis = getInt(R.styleable.LoopViewPager_delayMillis, delayMillis.toInt()).toLong()
            periodMillis = getInt(R.styleable.LoopViewPager_periodMillis, periodMillis.toInt()).toLong()
        }
    }

    private inline fun toRealItem(item: Int) : Int {
        val realCount = mBannerAdapter?.getRealCount() ?: 0
        return if (realCount > 0) item % realCount else item
    }

    override fun setCurrentItem(item: Int) {
        setCurrentItem(item, true)
    }

    override fun setCurrentItem(item: Int, smoothScroll: Boolean) {
        try {
            val realItemCount = mBannerAdapter?.getRealCount() ?: 0
            if (realItemCount > 0) {
                super.setCurrentItem(superCurrentItem() / realItemCount + item, smoothScroll)
                return
            }
            super.setCurrentItem(item, smoothScroll)
        } catch (tr: Throwable) {
            val itemCount = if (mBannerAdapter.isNull()) {
                adapter?.count ?: 0
            } else {
                mBannerAdapter?.getRealCount() ?: 0
            }
            if (itemCount < 1) {
                return
            }
            super.setCurrentItem(0, true)
        }
    }

    private fun superCurrentItem() = super.getCurrentItem()

    private fun setSuperCurrentItem(item: Int) {
        super.setCurrentItem(item)
    }

    override fun getCurrentItem(): Int {
        val currentItem = super.getCurrentItem()
        return mBannerAdapter?.getRealPosition(currentItem) ?: currentItem
    }

    override fun setAdapter(adapter: PagerAdapter?) {
        mBannerAdapter = adapter as BannerAdapter<*>
        super.setAdapter(adapter)
    }

    override fun onWindowVisibilityChanged(visibility: Int) {
        super.onWindowVisibilityChanged(visibility)
        mWindowVisibility = (visibility === VISIBLE)
        checkLoop()
    }

    private fun canLoop() : Boolean {
        if (!mWindowVisibility) {
            return false
        }
        if (!autoLoop) {
            return false
        }
        val itemCount = mBannerAdapter?.getRealCount() ?: 0
        return itemCount > 1
    }

    fun startLoop() = startLoopLocked(true)

    protected fun internalStartLoop() {
        if (isStopLoopFromUser) {
            return
        }
        startLoopLocked(false)
    }

    private fun startLoopLocked(fromUser: Boolean) {
        if (!canLoop()) {
            return
        }
        removeCallbacks(mNextTask)
        postDelayed(mNextTask, delayMillis)
        isStopLoopFromUser = fromUser
        isLooping = true
    }

    fun stopLoop() = stopLoopLocked(true)

    protected fun internalStopLoop() = stopLoopLocked(false)

    private fun stopLoopLocked(fromUser: Boolean) {
        removeCallbacks(mNextTask)
        isStopLoopFromUser = fromUser
        isLooping = false
    }

    private fun checkLoop() {
        if (canLoop()) {
            internalStartLoop()
            return
        }
        internalStopLoop()
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        ev?.run {
            when(action.and(MotionEvent.ACTION_MASK)) {

                MotionEvent.ACTION_DOWN -> internalStopLoop()

                MotionEvent.ACTION_UP,
                MotionEvent.ACTION_CANCEL -> checkLoop()
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        if (mBannerAdapter?.getRealCount() ?: 0 <= 1) {
            return false
        }
        return super.onInterceptTouchEvent(ev)
    }

    private var isFirstDataSetChange = true
    override fun onDataSetChanged() {
        super.onDataSetChanged()
        mBannerAdapter?.run {
            if (isFirstDataSetChange) {
                val realCount = getRealCount()
                if (realCount === 1) {
                    setSuperCurrentItem(Int.MAX_VALUE / 2)
                }
                setSuperCurrentItem(getRealCount() * 1_000_000)
                isFirstDataSetChange = false
            }
        }
        checkLoop()
    }

    override fun setOnPageChangeListener(listener: OnPageChangeListener?) {
        listener?.run {
            addOnPageChangeListener(this)
        }
    }

    override fun addOnPageChangeListener(listener: OnPageChangeListener) {
        register(listener)
    }

    override fun removeOnPageChangeListener(listener: OnPageChangeListener) {
        unregister(listener)
    }

    fun register(t: OnPageChangeListener) = mOnPageChangeListeners.register(t)

    fun unregister(t: OnPageChangeListener) = mOnPageChangeListeners.unregister(t)

    override fun clear() = mOnPageChangeListeners.clear()
}