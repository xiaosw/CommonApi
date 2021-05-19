package com.xsw.ui.widget.recycler

import android.content.Context
import android.hardware.SensorManager
import android.util.AttributeSet
import android.view.ViewConfiguration
import androidx.recyclerview.widget.RecyclerView
import com.xiaosw.api.logger.Logger
import com.xsw.ui.widget.recycler.manager.CoverFlowLayoutManager
import kotlin.math.abs
import kotlin.math.exp
import kotlin.math.ln

/**
 * ClassName: [CoverFlowRecyclerView]
 * Description:
 *
 * Create by X at 2021/05/19 14:27.
 */
class CoverFlowRecyclerView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : RecyclerView(context, attrs, defStyleAttr) {

    private val mCoverFlowLayoutManager = CoverFlowLayoutManager(context, VERTICAL)

    init {
        isChildrenDrawingOrderEnabled = true
        super.setLayoutManager(mCoverFlowLayoutManager)
    }

    override fun setLayoutManager(layout: LayoutManager?) {
        // super.setLayoutManager(layout)
    }

    fun setOrientation(@Orientation orientation: Int) {
        mCoverFlowLayoutManager.orientation = orientation
    }

    override fun getChildDrawingOrder(childCount: Int, i: Int): Int {
        val center: Int = (mCoverFlowLayoutManager.getCenterPosition()
                - mCoverFlowLayoutManager.getFirstVisiblePosition()) //计算正在显示的所有Item的中间位置

        return when {
            i == center -> {
                childCount - 1
            }
            i > center -> {
                center + childCount - 1 - i
            }
            else -> {
                i
            }
        }
    }

    override fun fling(velocityX: Int, velocityY: Int): Boolean {
        val velocity = if (mCoverFlowLayoutManager.isVertical) {
            velocityY
        } else {
            velocityX
        }
        // 缩小滚动距离
        val distance: Double = getSplineFlingDistance((velocity * 0.4f).toInt())
        val newDistance = mCoverFlowLayoutManager.calculateDistance(velocity, distance)
        val fixVelocity: Int = getVelocity(newDistance)
        val newVelocity = if (velocity > 0) {
            fixVelocity
        } else {
            -fixVelocity
        }
        return if (mCoverFlowLayoutManager.isVertical) {
            super.fling(velocityX, newVelocity)
        } else {
            super.fling(newVelocity, velocityY)
        }

    }

    /**
     * 根据松手后的滑动速度计算出fling的距离
     *
     * @param velocity
     * @return
     */
    private fun getSplineFlingDistance(velocity: Int): Double {
        val l = getSplineDeceleration(velocity)
        val decelMinusOne = DECELERATION_RATE - 1.0
        return mFlingFriction * getPhysicalCoeff() * exp(DECELERATION_RATE / decelMinusOne * l)
    }

    /**
     * 根据距离计算出速度
     *
     * @param distance
     * @return
     */
    private fun getVelocity(distance: Double): Int {
        val decelMinusOne = DECELERATION_RATE - 1.0
        val aecel =
            ln(distance / (mFlingFriction * mPhysicalCoeff)) * decelMinusOne / DECELERATION_RATE
        return abs((exp(aecel) * (mFlingFriction * mPhysicalCoeff) / INFLEXION).toInt())
    }

    /**
     * --------------flling辅助类---------------
     */
    private val mFlingFriction = ViewConfiguration.getScrollFriction()
    private var mPhysicalCoeff = 0f

    private fun getSplineDeceleration(velocity: Int): Double {
        val ppi = this.resources.displayMetrics.density * 160.0f
        val mPhysicalCoeff = (SensorManager.GRAVITY_EARTH // g (m/s^2)
                * 39.37f // inch/meter
                * ppi
                * 0.84f) // look and feel tuning
        return ln((INFLEXION * abs(velocity) / (mFlingFriction * mPhysicalCoeff)).toDouble())
    }

    private fun getPhysicalCoeff(): Float {
        if (mPhysicalCoeff == 0f) {
            val ppi = this.resources.displayMetrics.density * 160.0f
            mPhysicalCoeff = (SensorManager.GRAVITY_EARTH // g (m/s^2)
                    * 39.37f // inch/meter
                    * ppi
                    * 0.84f) // look and feel tuning
        }
        return mPhysicalCoeff
    }

    companion object {
        private const val INFLEXION = 0.35f // Tension lines cross at (INFLEXION, 1)
        private val DECELERATION_RATE = (ln(0.78) / ln(0.9)).toFloat()
    }

}