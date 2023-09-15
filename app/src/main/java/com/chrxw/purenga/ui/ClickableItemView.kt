package com.chrxw.purenga.ui

import android.content.Context
import android.graphics.Color
import android.util.TypedValue
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import com.chrxw.purenga.utils.ViewUtils.toPixel

/**
 * 文本控件
 */
open class ClickableItemView(context: Context) : FrameLayout(context) {
    protected val containerLayout = LinearLayout(context)
    protected val titleTextView = TextView(context)
    protected val subTextView = TextView(context)

    init {
        this.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply {
            setPadding(16.toPixel(context), 8.toPixel(context), 16.toPixel(context), 8.toPixel(context))
        }
        this.isClickable = true

        titleTextView.setTextColor(Color.BLACK)
        titleTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f)
        titleTextView.setPadding(0, 0, 64.toPixel(context), 0)

        subTextView.setTextColor(Color.GRAY)
        subTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13f)
        subTextView.setPadding(0, 0, 64.toPixel(context), 0)
        subTextView.visibility = GONE

        containerLayout.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
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
            subTextView.text = value
            subTextView.contentDescription = value

            if (value.isEmpty()) {
                subTextView.visibility = GONE
                titleTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f)
            } else {
                subTextView.visibility = VISIBLE
                titleTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
            }
        }
}
