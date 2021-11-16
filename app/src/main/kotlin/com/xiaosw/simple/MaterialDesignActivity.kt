package com.xiaosw.simple

import android.graphics.Color
import android.os.Bundle
import androidx.annotation.FloatRange
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.AppBarLayout
import kotlinx.android.synthetic.main.activity_material_design.*
import kotlin.math.abs

/**
 * ClassName: [MaterialDesignActivity]
 * Description:
 *
 * Create by X at 2021/05/06 10:28.
 */
open class MaterialDesignActivity : AppCompatActivity() {

    private val expandedTitleColor by lazy {
        Color.BLACK
    }

    private val collapsingTitleColor by lazy {
        Color.WHITE
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_material_design)
        app_bar_layout.addOnOffsetChangedListener(object : CompatOnOffsetChangedListener<AppBarLayout>() {

            override fun onOffsetChanged(
                appBarLayout: AppBarLayout,
                verticalOffset: Int,
                status: Status
            ) {
                val scrollPercent = abs(verticalOffset).toFloat() / appBarLayout.totalScrollRange
                title_bar_container.setBackgroundColor(translateColor(Color.WHITE, 0xffffff, 1 - scrollPercent))
                iv_parallax.alpha = 1 - scrollPercent
                tv_center.setTextColor(translateColor(collapsingTitleColor, expandedTitleColor, scrollPercent))
            }

        })
    }

    private inline fun translateColor(
        from: Int
        , to: Int
        , @FloatRange(from = .0, to = 1.0) percent: Float
    ) : Int {
        val fromA = Color.alpha(from)
        val fromR = Color.red(from)
        val fromG = Color.green(from)
        val fromB = Color.blue(from)
        val toA = Color.alpha(to)
        val toR = Color.red(to)
        val toG = Color.green(to)
        val toB = Color.blue(to)
        return Color.argb(fromA + ((toA - fromA) * percent).toInt()
            , fromR + ((toR - fromR) * percent).toInt()
            , fromG + ((toG - fromG) * percent).toInt()
            , fromB + ((toB - fromB) * percent).toInt())
    }
}