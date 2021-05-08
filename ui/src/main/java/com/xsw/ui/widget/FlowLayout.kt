package com.xsw.ui.widget

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.xiaosw.api.extend.isNull
import com.xiaosw.api.extend.use
import com.xiaosw.api.logger.Logger
import com.xsw.ui.R
import java.util.*
import java.util.concurrent.LinkedBlockingQueue
import kotlin.math.max
import kotlin.math.min

/**
 * ClassName: [FlowLayout]
 * Description:
 *
 * Create by X at 2021/05/07 10:46.
 */
class FlowLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    var adapter: Adapter<*, ViewHolder>? = null
        set(value) {
            field = value
            field?.mParent = this
            refreshAllChild()
        }

    private val mChildLocations by lazy {
        mutableListOf<Rect>()
    }

    private var mLines: Int = NO_ID

    private val mHolderCache by lazy {
        mutableMapOf<Int, LinkedBlockingQueue<ViewHolder>>()
    }

    private var mLayoutMode = LayoutMode.WRAP_CONTENT

    private val mRows by lazy {
        mutableMapOf<Int, Row>()
    }

    init {
        parseAttrs(attrs)
    }

    private inline fun parseAttrs(attrs: AttributeSet?) {
        attrs?.let {
            context.obtainStyledAttributes(it, R.styleable.FlowLayout).use {
                setLines(getInt(R.styleable.FlowLayout_android_lines, mLines))
            }
        }
    }

    private inline fun verticalMeasure(widthSize: Int) = intArrayOf(0, 0).also {
        val childCount = childCount
        var width = 0
        var height = paddingTop

        var l = paddingLeft
        var lineMaxHeight = 0

        for (index in 0 until childCount) {
            val child = getChildAt(index)
            var childWidth = child.measuredWidth
            var childHeight = child.measuredHeight

            var childLeftMargin = 0
            var childTopMargin = 0
            (child.layoutParams as? MarginLayoutParams)?.let { p ->
                childLeftMargin = p.leftMargin
                childTopMargin = p.topMargin
                childWidth += childLeftMargin + p.rightMargin
                childHeight += childTopMargin + p.bottomMargin
            }
            if (l + childWidth > widthSize) { // 换行
                width = max(width, l)
                height += lineMaxHeight
                l = paddingLeft
                lineMaxHeight = 0
            }
            val left = l + childLeftMargin
            val top = height + childTopMargin
            mChildLocations.add(Rect(left
                , top
                , left + child.measuredWidth
                , top + child.measuredHeight))
            l += childWidth
            lineMaxHeight = max(lineMaxHeight, childHeight)
        }
        width = max(width, l)
        height += lineMaxHeight + paddingTop + paddingBottom
        it[0] = width
        it[1] = height
    }

    private inline fun horizontalMeasure(heightSize: Int) = intArrayOf(0, 0).also {
        val realHeightSize = heightSize - paddingTop - paddingBottom
        val isFixRow = mLayoutMode == LayoutMode.FIX_ROW
        var isFirstRow = true
        val childCount = childCount
        var width = 0
        var height = 0

        var rowHeight = 0

        for (index in 0 until childCount) {
            val child = getChildAt(index)
            var childWidth = child.measuredWidth
            var childHeight = child.measuredHeight

            var childLeftMargin = 0
            var childTopMargin = 0
            (child.layoutParams as? MarginLayoutParams)?.let { p ->
                childLeftMargin = p.leftMargin
                childTopMargin = p.topMargin
                childWidth += childLeftMargin + p.rightMargin
                childHeight += childTopMargin + p.bottomMargin
            }
            if ((isFixRow && index % mLines === 0)
                || (rowHeight + childHeight > realHeightSize)) { // 换列
                height = max(height, rowHeight)
                rowHeight = 0
                if (isFirstRow && index > 0) {
                    isFirstRow = false
                }
            }
            val row = if (isFirstRow) {
                Row(childLeftMargin, childWidth, rowHeight + childTopMargin + paddingTop).also { row ->
                    mRows[index] = row
                }
            } else {
                findMinWidthRow().also { row ->
                    row.left = row.right + childLeftMargin
                    row.right += childWidth
                }
            }
            mChildLocations.add(Rect(row.left
               , row.top
               , row.left + child.measuredWidth
               , row.top + child.measuredHeight))
            width = row.right
            rowHeight += childHeight
        }
        width += paddingLeft + paddingRight
        height = max(height, rowHeight) + paddingTop + paddingBottom
        it[0] = width
        it[1] = height
    }

    private inline fun findMinWidthRow() : Row {
        var row: Row? = null
        for (r in mRows.values) {
            row = row?.let {
                if (it.right > r.right) r else row
            } ?: r
        }
        return row ?: Row()
    }

    private inline fun onMeasureLocked(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        // 获得它的父容器为它设置的测量模式和大小
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val wh = if (orientation === VERTICAL) verticalMeasure(widthSize) else horizontalMeasure(heightSize)
        var measureWidth = when {
            widthMode === MeasureSpec.EXACTLY -> {
                widthSize
            }
            else -> {
                wh[0]
            }
        }

        var measureHeight = when {
            heightMode === MeasureSpec.EXACTLY -> {
                heightSize
            }
            else -> {
                wh[1]
            }
        }
        setMeasuredDimension(measureWidth, measureHeight)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        if (mLayoutMode == LayoutMode.SINGLE) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
            return
        }
        // 先测量一把子 view
        measureChildren(widthMeasureSpec, heightMeasureSpec)
        mChildLocations.clear()
        onMeasureLocked(widthMeasureSpec, heightMeasureSpec)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        if (mLayoutMode == LayoutMode.SINGLE) {
            super.onLayout(changed, l, t, r, b)
            return
        }
        val size = min(childCount, mChildLocations.size)
        for (index in 0 until size) {
            with(mChildLocations[index]) {
                getChildAt(index).layout(left, top, right, bottom)
            }
        }
    }

    override fun removeAllViews() {
        val count = childCount
        for (index in 0 until count) {
            cacheHolder(getChildAt(index))
        }
        super.removeAllViews()
    }

    override fun removeView(view: View?) {
        cacheHolder(view)
        super.removeView(view)
    }

    override fun removeViewAt(index: Int) {
        removeView(getChildAt(index))
    }

    private inline fun cacheHolder(view: View?) {
        (view?.getTag(R.id.flow_view_hold) as? ViewHolder)?.apply {
            val cacheQueue = mHolderCache[type] ?: LinkedBlockingQueue<ViewHolder>(30).also {
                mHolderCache[type] = it
            }
            cacheQueue.offer(this)
        }
    }

    private inline fun findHolder(type: Int) = mHolderCache[type]?.poll()

    override fun generateLayoutParams(attrs: AttributeSet?): LayoutParams {
        return attrs?.let {
            LayoutParams(context, it)
        } ?: LayoutParams(MarginLayoutParams.WRAP_CONTENT, MarginLayoutParams.WRAP_CONTENT)
    }

    private inline fun internalAddChild(position: Int) {
        adapter?.apply {
            val itemType = getItemType(position)
            (findHolder(itemType)?.apply {
                Logger.v("find view by cache. skip onBindViewHolder()")
                onBindViewHolder(this, position)
            } ?: onCreateViewHolder(position, this@FlowLayout)?.also { holder ->
                holder.type = itemType
                onBindViewHolder(holder, position)
            })?.also {
                it.itemView?.apply {
                    setTag(R.id.flow_view_hold, it)
                    this@FlowLayout.addView(this)
                }
            }
        }
    }

    private inline fun refreshAllChild() {
        removeAllViews()
        adapter?.apply {
            val count = getItemCount()
            for (position in 0 until count) {
                internalAddChild(position)
            }
        }
        requestLayout()
    }

    private fun notifyDataSetChanged() = refreshAllChild()

    private fun notifyItemChanged(position: Int) {
        (getChildAt(position)?.getTag(R.id.flow_view_hold) as? ViewHolder)?.let {
            adapter?.onBindViewHolder(it, position)
        }
    }

    private fun notifyItemInsert(position: Int) {
        internalAddChild(position)
    }

    fun setLines(lines: Int) {
        if (mLines === lines) {
            return
        }
        mLines = lines
        updateLayoutMode()
        refreshAllChild()
    }

    private fun updateLayoutMode() {
        mLayoutMode = when {
            mLines === 1 -> {
                LayoutMode.SINGLE
            }

            mLines < 2 -> {
                LayoutMode.WRAP_CONTENT
            }

            orientation === VERTICAL -> {
                LayoutMode.FIX_COLUMN
            }

            else -> {
                LayoutMode.FIX_ROW
            }
        }
    }

    class LayoutParams : LinearLayout.LayoutParams {

        constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

        constructor(w: Int, h: Int) : super(w, h)
    }

    abstract class Adapter<T, VH : ViewHolder> {

        internal var mParent: FlowLayout? = null
        var dataSource: MutableList<T?> = mutableListOf()

        open fun getItemCount() = dataSource.size

        open fun getItem(position: Int) = dataSource[position]

        fun add(data: T?) {
            dataSource.add(data)
            notifyItemInsert(dataSource.size - 1)
        }

        fun addAll(newData: MutableList<T?>?) {
            if (newData.isNullOrEmpty()) {
                return
            }
            val from = dataSource.size - 1
            dataSource.addAll(newData)
            for (position in from until dataSource.size) {
                notifyItemInsert(position)
            }
        }

        fun remove(position: Int) {
            dataSource.removeAt(position)
            notifyItemRemoved(position)
        }

        fun clear() {
            dataSource.clear()
            notifyDataSetChanged()
        }

        fun notifyDataSetChanged() {
            mParent?.notifyDataSetChanged()
        }

        fun notifyItemChanged(position: Int) {
            mParent?.notifyItemChanged(position)
        }

        fun notifyItemRangeChanged(start: Int, end: Int) {
            for (position in start until end) {
                notifyItemChanged(position)
            }
        }

        fun notifyItemRemoved(position: Int) {
            mParent?.removeViewAt(position)
        }

        fun notifyItemInsert(position: Int) {
            mParent?.notifyItemInsert(position)
        }

        open fun getItemType(position: Int) : Int = NO_ID

        abstract fun onCreateViewHolder(
            position: Int
            , parent: ViewGroup
        ) : ViewHolder?

        abstract fun onBindViewHolder(holder: VH, position: Int)

    }

    class ViewHolder(val itemView: View) {
        internal var type: Int = NO_ID
    }

    enum class LayoutMode(val desc: String) {
        FIX_COLUMN("固定列"),
        FIX_ROW("固定行"),
        WRAP_CONTENT("自动填充"),
        SINGLE("单列/行");
    }

    data class Row(var left: Int = 0, var right: Int = 0, var top: Int = 0)
}