package com.chrxw.purenga.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import com.chrxw.purenga.utils.ExtensionUtils.getDrawable

class FitImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    var fitWidth: Boolean = true
        set(value) {
            field = value
            imageView.requestLayout()
        }

    private val imageView = ImageView(context).apply {
        scaleType = ImageView.ScaleType.FIT_CENTER
        layoutParams = LayoutParams(
            LayoutParams.WRAP_CONTENT,
            LayoutParams.WRAP_CONTENT
        )
    }

    init {
        addView(imageView)
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    fun setImageResource(resId: Int, theme: Resources.Theme?) {
        val drawable: Drawable = if (com.github.kyuubiran.ezxhelper.EzXHelper.isModuleResInited) {
            resId.getDrawable(theme)
        } else {
            context.resources.getDrawable(resId, theme)
        }
        imageView.setImageDrawable(drawable)
        adjustImageSize(drawable)
    }

    private fun adjustImageSize(drawable: Drawable?) {
        drawable ?: return
        if (fitWidth) {
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
                    val scale = parentWidth.toFloat() / dw
                    val newHeight = (dh * scale).toInt()
                    imageView.layoutParams = LayoutParams(parentWidth, newHeight)
                    imageView.requestLayout()
                }
            }
        } else {
            val width = when (drawable) {
                is BitmapDrawable -> drawable.bitmap.width
                else -> drawable.intrinsicWidth
            }
            val height = when (drawable) {
                is BitmapDrawable -> drawable.bitmap.height
                else -> drawable.intrinsicHeight
            }
            imageView.layoutParams = LayoutParams(width, height)
            imageView.requestLayout()
        }
    }

    override fun setOnClickListener(l: View.OnClickListener?) {
        imageView.setOnClickListener(l)
    }
}