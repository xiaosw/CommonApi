package com.xsw.ui.widget.recycler.manager

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.util.SparseArray
import android.util.SparseBooleanArray
import android.view.View
import androidx.core.util.valueIterator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.concurrent.LinkedBlockingDeque
import kotlin.math.abs

/**
 * ClassName: [CoverFlowLayoutManager]
 * Description:
 *
 * Create by X at 2021/05/19 10:41.
 */
internal class CoverFlowLayoutManager : LinearLayoutManager {

    private var mScrollX = 0
    private var mScrollY = 0
    private var mTotalWidth = 0
    private var mTotalHeight = 0
    private var mItemWidth = 0
    private var mItemHeight:Int = 0
    private val mItemRectArray = SparseArray<Rect>()
    private var mIntervalWidth = 0
    private var mIntervalHeight = 0
    private var mStartX = 0
    private var mStartY = 0
    private var mVisibleRect = Rect()

    /**
     * 记录Item是否出现过屏幕且还没有回收。true表示出现过屏幕上，并且还没被回收
     */
    private val mHasAttachedItems = SparseBooleanArray()
    /**
     * add        增加一个元索                     如果队列已满，则抛出一个IIIegaISlabEepeplian异常
     * remove   移除并返回队列头部的元素    如果队列为空，则抛出一个NoSuchElementException异常
     * element  返回队列头部的元素             如果队列为空，则抛出一个NoSuchElementException异常
     * offer       添加一个元素并返回true       如果队列已满，则返回false
     * poll         移除并返问队列头部的元素    如果队列为空，则返回null
     * peek       返回队列头部的元素             如果队列为空，则返回null
     * put         添加一个元素                      如果队列满，则阻塞
     * take        移除并返回队列头部的元素     如果队列为空，则阻塞
     */
    private var mRectCache = LinkedBlockingDeque<Rect>()

    var isVertical = false

    @JvmOverloads
    constructor(
        context: Context,
        @RecyclerView.Orientation orientation: Int = RecyclerView.HORIZONTAL,
        reverseLayout: Boolean = false
    ) : super(context, orientation, reverseLayout)

    constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0,
        defStyleRes: Int = 0
    ) : super(context, attrs, defStyleAttr, defStyleRes)

    override fun generateDefaultLayoutParams() = RecyclerView.LayoutParams(
        RecyclerView.LayoutParams.WRAP_CONTENT,
        RecyclerView.LayoutParams.WRAP_CONTENT
    )

    override fun setOrientation(orientation: Int) {
        super.setOrientation(orientation)
        isVertical = orientation === VERTICAL
    }

    override fun onLayoutChildren(recycler: RecyclerView.Recycler?, state: RecyclerView.State?) {
        recycler?.run {
            detachAndScrapAttachedViews(this)
            val itemCount = itemCount
            if (itemCount === 0) {
                return
            }
            mHasAttachedItems.clear()
            cacheAllRect()

            val firstChild: View = getViewForPosition(0)
            measureChildWithMargins(firstChild, 0, 0)
            mItemWidth = getDecoratedMeasuredWidth(firstChild)
            mItemHeight = getDecoratedMeasuredHeight(firstChild)
            mIntervalWidth = getIntervalWidth()
            mIntervalHeight = getIntervalHeight()
            mStartX = width / 2 - mIntervalWidth
            mStartY = height / 2 - mIntervalHeight

            val visibleRect = getVisibleRect()
            val visibleCount: Int
            if (isVertical) {
                //定义水平方向的偏移量
                var offsetY = 0
                for (i in 0 until itemCount) {
                    val rect = findRect().also {
                        it.set(paddingLeft,
                            mStartY + offsetY,
                            mItemWidth,
                            mStartY + offsetY + mItemHeight)
                    }
                    mItemRectArray.put(i, rect)
                    mHasAttachedItems.put(i, false)
                    offsetY += mIntervalHeight
                }
                visibleCount = getVerticalSpace() / mIntervalHeight
                for (i in 0 until visibleCount) {
                    insertView(i, visibleRect, recycler, false)
                }
                mTotalHeight = offsetY.coerceAtLeast(getVerticalSpace())
            } else {
                //定义水平方向的偏移量
                var offsetX = 0
                for (i in 0 until itemCount) {
                    val rect = findRect().also {
                        it.set(mStartX + offsetX,
                            paddingTop,
                            mStartX + offsetX + mItemWidth,
                            mItemHeight)
                    }
                    mItemRectArray.put(i, rect)
                    mHasAttachedItems.put(i, false)
                    offsetX += mIntervalWidth
                }
                visibleCount = getHorizontalSpace() / mIntervalWidth
                for (i in 0 until visibleCount) {
                    insertView(i, visibleRect, recycler, false)
                }
                mTotalWidth = offsetX.coerceAtLeast(getHorizontalSpace())
            }
        }
    }

    private fun cacheAllRect() {
        for (rect in mItemRectArray.valueIterator()) {
            rect?.run {
                mRectCache.offer(this)
            }
        }
        mItemRectArray.clear()
    }

    private fun findRect() = mRectCache.poll() ?: Rect()

    private fun getHorizontalSpace() = width - paddingLeft - paddingRight

    private fun getVerticalSpace() = height - paddingTop - paddingBottom

    override fun scrollHorizontallyBy(
        dx: Int,
        recycler: RecyclerView.Recycler,
        state: RecyclerView.State?
    ): Int {
        if (childCount <= 0) {
            return dx
        }
        var travel = dx
        //如果滑动到最顶部
        if (mScrollX + dx < 0) {
            travel = -mScrollX
        } else if (mScrollX + dx > getHorizontalMaxOffset()) {
            //如果滑动到最底部
            travel = getHorizontalMaxOffset() - mScrollX
        }
        mScrollX += travel
        val visibleRect = getVisibleRect()

        //回收越界子View
        for (i in childCount - 1 downTo 0) {
            val child = getChildAt(i)
            val position = getPosition(child!!)
            val rect = mItemRectArray[position]
            if (!Rect.intersects(rect, visibleRect)) {
                removeAndRecycleView(child, recycler)
                mHasAttachedItems.put(position, false)
            } else {
                layoutDecoratedWithMargins(
                    child,
                    rect.left - mScrollX,
                    rect.top,
                    rect.right - mScrollX,
                    rect.bottom
                )
                translateChildView(child, rect.left - mStartX - mScrollX)
                mHasAttachedItems.put(position, true)
            }
        }
        //填充空白区域
        if (travel >= 0) {
            val minPos = getPosition(getChildAt(0) as View)
            for (i in minPos until itemCount) {
                insertView(i, visibleRect, recycler, false)
            }
        } else {
            val maxPos = getPosition(getChildAt(childCount - 1) as View)
            for (i in maxPos downTo 0) {
                insertView(i, visibleRect, recycler, true)
            }
        }
        return travel
    }

    override fun scrollVerticallyBy(
        dy: Int,
        recycler: RecyclerView.Recycler,
        state: RecyclerView.State?
    ): Int {
        if (childCount <= 0) {
            return dy
        }
        var travel = dy
        //如果滑动到最顶部
        if (mScrollY + dy < 0) {
            travel = -mScrollY
        } else if (mScrollY + dy > getVerticalMaxOffset()) {
            //如果滑动到最底部
            travel = getVerticalMaxOffset() - mScrollY
        }
        mScrollY += travel
        val visibleRect = getVisibleRect()

        //回收越界子View
        for (i in childCount - 1 downTo 0) {
            val child = getChildAt(i)
            val position = getPosition(child!!)
            val rect = mItemRectArray[position]
            if (!Rect.intersects(rect, visibleRect)) {
                removeAndRecycleView(child, recycler)
                mHasAttachedItems.put(position, false)
            } else {
                layoutDecoratedWithMargins(
                    child,
                    rect.left,
                    rect.top - mScrollY,
                    rect.right,
                    rect.bottom - mScrollY
                )
                translateChildView(child, rect.top - mStartY - mScrollY)
                mHasAttachedItems.put(position, true)
            }
        }
        //填充空白区域
        if (travel >= 0) {
            val minPos = getPosition(getChildAt(0) as View)
            for (i in minPos until itemCount) {
                insertView(i, visibleRect, recycler, false)
            }
        } else {
            val maxPos = getPosition(getChildAt(childCount - 1) as View)
            for (i in maxPos downTo 0) {
                insertView(i, visibleRect, recycler, true)
            }
        }
        return travel
    }

    private fun insertView(pos: Int, visibleRect: Rect, recycler: RecyclerView.Recycler, firstPos: Boolean) {
        val rect = mItemRectArray[pos]
        if (Rect.intersects(visibleRect, rect) && !mHasAttachedItems[pos]) {
            val child: View = recycler.getViewForPosition(pos)
            if (firstPos) {
                addView(child, 0)
            } else {
                addView(child)
            }
            measureChildWithMargins(child, 0, 0)
            val l: Int
            val t: Int
            val r: Int
            val b: Int
            val scrollDistance: Int
            if (isVertical) {
                l = rect.left
                t = rect.top - mScrollY
                r = rect.right
                b = rect.bottom - mScrollY
                scrollDistance = rect.top - mScrollY - mStartY
            } else {
                l = rect.left - mScrollX
                t = rect.top
                r = rect.right - mScrollX
                b = rect.bottom
                scrollDistance = rect.left - mScrollX - mStartX
            }
            layoutDecoratedWithMargins(child, l, t, r, b)
            translateChildView(child, scrollDistance)
            mHasAttachedItems.put(pos, true)
        }
    }

    /**
     * 获取可见的区域Rect
     *
     * @return
     */
    private fun getVisibleRect() = mVisibleRect.apply {
        val l: Int
        val t: Int
        val r: Int
        val b: Int
        if (isVertical) {
            l = paddingLeft
            t = paddingTop + mScrollY
            r = width - paddingRight
            b = height - paddingBottom + mScrollY
        } else {
            l = paddingLeft + mScrollX
            t = paddingTop
            r = width - paddingRight + mScrollX
            b = height - paddingBottom
        }
        set(l, t, r, b)
    }

    fun getIntervalWidth() = mItemWidth / 2

    fun getIntervalHeight() = mItemHeight / 2

    fun getCenterPosition(): Int {
        val scroll: Int
        val size = if (isVertical) {
            scroll = mScrollY
            getIntervalHeight()
        } else {
            scroll = mScrollX
            getIntervalWidth()
        }
        var pos = (scroll / size)
        if ((scroll % size) > size * 0.5f) {
            pos++
        }
        return pos
    }

    /**
     * 获取第一个可见的Item位置
     *
     * Note:该Item为绘制在可见区域的第一个Item，有可能被第二个Item遮挡
     */
    fun getFirstVisiblePosition(): Int {
        if (childCount <= 0) {
            return 0
        }
        return getPosition(getChildAt(0) as View)
    }

    private fun translateChildView(child: View?, scrollDistance: Int) {
        child?.run {
            val scale = computeScale(scrollDistance)
            val rotation = computeRotation(scrollDistance)
            scaleX = scale
            scaleY = scale
            if (isVertical) {
                rotationX = rotation
            } else {
                rotationY = rotation
            }
        }
    }

    /**
     * 计算Item缩放系数
     *
     * @param x Item的偏移量
     * @return 缩放系数
     */
    private fun computeScale(scroll: Int): Float {
        val intervalSize = if (isVertical) {
            getIntervalHeight()
        } else {
            getIntervalWidth()
        }
        var scale = 1 - abs(scroll * 2.0f / (8f * intervalSize))
        return scale.coerceIn(0f, 1f)
    }

    /**
     * 水平最大偏移量
     */
    private fun getHorizontalMaxOffset(): Int {
        return (itemCount - 1) * getIntervalWidth()
    }

    /**
     * 垂直最大偏移量
     */
    private fun getVerticalMaxOffset(): Int {
        return (itemCount - 1) * getIntervalHeight()
    }

    fun calculateDistance(velocity: Int, distance: Double): Double {
        val size = if (isVertical) {
            getIntervalHeight()
        } else {
            getIntervalWidth()
        }
        val extra = if (isVertical) {
            mScrollY % size
        } else {
            mScrollX % size
        }
        return if (velocity > 0) {
            if (distance < size) {
                (size - extra).toDouble()
            } else {
                distance - distance % size - extra
            }
        } else {
            if (distance < size) {
                extra.toDouble()
            } else {
                distance - distance % size + extra
            }
        }
    }

    private fun computeRotation(distance: Int): Float {
        val size = if (isVertical) {
            getIntervalHeight()
        } else {
            getIntervalWidth()
        }
        var rotation = -MAX_ROTATION * distance / size
        if (abs(rotation) > MAX_ROTATION) {
            rotation = if (rotation > 0) {
                MAX_ROTATION
            } else {
                -MAX_ROTATION
            }
        }
        return rotation
    }

    companion object {
        /**
         * 最大轴旋转度数
         */
        private const val MAX_ROTATION = 0.0f

    }

}