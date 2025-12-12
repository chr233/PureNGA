package com.chrxw.purenga.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.widget.FrameLayout
import android.widget.ImageView
import com.chrxw.purenga.utils.ExtensionUtils.getDrawable
import com.chrxw.purenga.utils.Helper

open class FitImageView : FrameLayout {
    protected val imageView: ImageView

    constructor(context: Context) : super(context) {
        imageView = ImageView(context).apply {
            scaleType = ImageView.ScaleType.FIT_CENTER
            layoutParams = LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT,
            )
        }

        this.addView(imageView)
    }

    constructor(context: Context, resId: Int, theme: Resources.Theme? = null) : this(context) {
        setImageResource(resId, theme)
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    fun setImageResource(resId: Int, theme: Resources.Theme?) {
        val drawable: Drawable = if (Helper.isXposed) {
            resId.getDrawable(theme)
        } else {
            context.resources.getDrawable(resId, theme)
        }
        imageView.setImageDrawable(drawable)
        adjustImageSize(drawable)
    }

    private fun adjustImageSize(drawable: Drawable?) {
        drawable ?: return
        post {
            val parentWidth = width
            val dw = when (drawable) {
                is BitmapDrawable -> drawable.bitmap.width
                else -> drawable.intrinsicWidth
            }
            val dh = when (drawable) {
                is BitmapDrawable -> drawable.bitmap.height
                else -> drawable.intrinsicHeight
            }
            if (dw > 0 && dh > 0 && parentWidth > 0) {
                val newHeight = (dh * width.toFloat() / dw).toInt()
                imageView.layoutParams.height = newHeight
                imageView.requestLayout()
            }
        }
    }

    override fun setOnClickListener(l: OnClickListener?) {
        imageView.setOnClickListener(l)
    }
}