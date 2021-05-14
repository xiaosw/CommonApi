package com.xsw.ui.widget.banner.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.viewpager.widget.PagerAdapter
import com.xiaosw.api.extend.isNull
import java.util.concurrent.LinkedBlockingQueue

/**
 * ClassName: [BannerAdapter]
 * Description:
 *
 * Create by X at 2021/05/14 10:39.
 */
abstract class BannerAdapter<Source> @JvmOverloads constructor (
    @LayoutRes private val layoutRes: Int
    , sources: MutableList<Source>? = null
) : PagerAdapter() {

    val sources by lazy {
        mutableListOf<Source>()
    }

    private var mInflater: LayoutInflater? = null
    private val mItemViewCache by lazy {
        LinkedBlockingQueue<View>()
    }

    init {
        sources?.run {
            addAll(this)
        }
    }

    override fun getCount() = if (getRealCount() > 0) Int.MAX_VALUE else 0

    override fun isViewFromObject(view: View, obj: Any) = view == obj

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val child = inflateChild(container).also {
            container.addView(it)
        }
        val realPosition = getRealPosition(position)
        bindData(container, child, realPosition, sources[realPosition])
        return child
    }

    override fun destroyItem(container: ViewGroup, position: Int, obj: Any) {
        (obj as? View)?.let {
            container.removeView(it)
            mItemViewCache.offer(it)
            return
        }
        super.destroyItem(container, position, obj)
    }

    private inline fun inflateChild(container: ViewGroup) : View {
        mItemViewCache.poll()?.let {
            return it
        }
        if (mInflater.isNull()) {
            mInflater = LayoutInflater.from(container.context)
        }
        return mInflater!!.inflate(layoutRes, container, false)
    }

    ///////////////////////////////////////////////////////////////////////////
    // api
    ///////////////////////////////////////////////////////////////////////////
    fun add(source: Source) {
        sources.add(source)
        notifyDataSetChanged()
    }

    fun addAll(sources: MutableList<Source>) {
        this.sources.addAll(sources)
        notifyDataSetChanged()
    }

    fun remove(source: Source) {
        sources.remove(source)
        notifyDataSetChanged()
    }

    fun removeAll() {
        sources.clear()
        notifyDataSetChanged()
    }

    fun clear() = removeAll()

    fun getRealCount() = sources.size

    fun getRealPosition(position: Int) : Int {
        val realCount = getRealCount()
        if (realCount === 0) {
            return position
        }
        return position % realCount
    }

    abstract fun bindData(parent: ViewGroup, item: View, position: Int,source: Source)
}