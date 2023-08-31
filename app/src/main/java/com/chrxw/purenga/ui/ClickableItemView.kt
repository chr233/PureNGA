package com.chrxw.purenga.ui

import android.content.Context
import android.graphics.Color
import android.util.TypedValue
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import com.chrxw.purenga.utils.ViewUtils.toPixelInt

open class ClickableItemView(context: Context) : FrameLayout(context) {
    private val containerLayout = LinearLayout(context)
    private val titleTextView = TextView(context)
    private val subTextView = TextView(context)

    init {
        this.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply {
            setPadding(16.toPixelInt(context), 8.toPixelInt(context), 16.toPixelInt(context), 8.toPixelInt(context))
//            marginStart = 10.toDpInt(context)
        }
        this.isClickable = true

        titleTextView.setTextColor(Color.BLACK)
        titleTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
        titleTextView.setPadding(0, 0, 64.toPixelInt(context), 0)

        subTextView.setTextColor(Color.GRAY)
        subTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
        subTextView.setPadding(0, 0, 64.toPixelInt(context), 0)

        containerLayout.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT).apply {
//            marginStart = 10.toPixelInt(context)
        }
        containerLayout.orientation = LinearLayout.VERTICAL
        containerLayout.addView(titleTextView)
        containerLayout.addView(subTextView)

        this.addView(containerLayout)
    }

    var title: CharSequence
        get() = titleTextView.text
        set(value) {
            titleTextView.text = value
            titleTextView.contentDescription = value
        }

    var subTitle: CharSequence
        get() = subTextView.text
        set(value) {
            subTextView.visibility = if (value.isNullOrEmpty()) GONE else VISIBLE
            subTextView.text = value
            subTextView.contentDescription=value
        }
}
