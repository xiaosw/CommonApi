package com.xsw.ui.widget.fold

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.RotateAnimation
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import com.xiaosw.api.extend.isNull
import com.xsw.ui.R
import kotlin.properties.Delegates

/**
 * ClassName: [FoldTextActionView]
 * Description:
 *
 * Create by X at 2021/04/29 16:24.
 */
internal class FoldTextActionView @JvmOverloads constructor (
    context: Context
    , attrs: AttributeSet? = null
    , defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr), IFoldTextActionView {

    /**
     * 折叠文字
     */
    var foldText: String? = null

    /**
     * 收起文字
     */
    var unfoldText: String? = null

    private var tvAction: TextView by Delegates.notNull()
    private var ivAction: ImageView by Delegates.notNull()

    private var isFold = true
    var enableAnim = true

    private val mDefFoldAnim by lazy {
        RotateAnimation(180f
            , 360f
            , Animation.RELATIVE_TO_SELF
            , 0.5f
            , Animation.RELATIVE_TO_SELF
            , 0.5f
        ).also {
            it.fillAfter = true
            it.duration = 200
        }
    }

    private val mDefUnfoldAnim by lazy {
        RotateAnimation(0f
            , 180f
            , Animation.RELATIVE_TO_SELF
            , 0.5f
            , Animation.RELATIVE_TO_SELF
            , 0.5f).also {
            it.fillAfter = true
            it.duration = 200
        }
    }

    private var mFoldAnim: Animation? = mDefFoldAnim
    private var mUnfoldAnim: Animation? = mDefUnfoldAnim

    init {
        foldText = resources.getString(R.string.unfold)
        unfoldText = resources.getString(R.string.fold)
        internalInstallContent(context)
    }

    private inline fun internalInstallContent(context: Context) {
        LayoutInflater.from(context).inflate(R.layout.view_fold_text_action, this, true)
        tvAction = findViewById(R.id.tv_action)
        ivAction = findViewById(R.id.iv_action)
    }

    fun setTextSize(size: Float) {
        tvAction.textSize = size
    }

    fun setTextSize(unit: Int, size: Float) = tvAction.setTextSize(unit, size)

    fun getTextSize() = tvAction.textSize

    fun setTextColor(color: Int) = tvAction.setTextColor(color)

    fun getCurrentTextColor() = tvAction.currentTextColor

    fun setTextStyle(style: Int) = tvAction.setTypeface(tvAction.typeface, style)

    fun setActionIcon(drawable: Drawable?) {
        ivAction.setImageDrawable(drawable)
    }

    fun setActionIcon(resId: Int) {
        ivAction.setImageResource(resId)
    }

    fun setActionIcon(bitmap: Bitmap?) {
        ivAction.setImageBitmap(bitmap)
    }

    fun setFoldAnimation(animation: Animation) {
        mFoldAnim = animation
    }

    fun setFoldAnimation(animationId: Int) {
        if (animationId === NO_ID) {
            return
        }
        setFoldAnimation(AnimationUtils.loadAnimation(context, animationId))
    }

    fun setUnfoldAnimation(animation: Animation) {
        mUnfoldAnim = animation
    }

    fun setUnfoldAnimation(animationId: Int) {
        if (animationId === NO_ID) {
            return
        }
        setUnfoldAnimation(AnimationUtils.loadAnimation(context, animationId))
    }

    override fun actionWidth() = tvAction.measuredWidth + ivAction.measuredWidth

    override fun actionHeight() = measuredHeight

    override fun onFoldStateChange(isFold: Boolean) {
        if (this.isFold == isFold) {
            return
        }
        this.isFold = isFold
        val anim = if (isFold) {
            tvAction.text = foldText
            mFoldAnim
        } else {
            tvAction.text = unfoldText
            mUnfoldAnim
        }
        if (!enableAnim || anim.isNull()) {
            return
        }
        ivAction.startAnimation(anim)
    }

    override fun enableAnim(enable: Boolean) {
        enableAnim = enable
    }

}