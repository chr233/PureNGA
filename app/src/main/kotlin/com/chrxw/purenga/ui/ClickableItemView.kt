package com.chrxw.purenga.ui

import android.content.Context
import android.content.res.Configuration
import android.graphics.Color
import android.util.TypedValue
import android.view.Gravity
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import com.chrxw.purenga.utils.ExtensionUtils.getStringFromMod
import com.chrxw.purenga.utils.ExtensionUtils.toPixel

/**
 * 文本控件
 */
open class ClickableItemView : FrameLayout {
    protected val containerLayout: LinearLayout
    protected val titleTextView: TextView
    protected val subTextView: TextView


    constructor(context: Context) : super(context) {
        containerLayout = LinearLayout(context)
        titleTextView = TextView(context)
        subTextView = TextView(context)

        this.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT).apply {
            setPadding(16.toPixel(context), 8.toPixel(context), 16.toPixel(context), 8.toPixel(context))
        }
        this.isClickable = true

        titleTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f)
        titleTextView.setPadding(0, 0, 64.toPixel(context), 0)

        subTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13f)
        subTextView.setPadding(0, 0, 64.toPixel(context), 0)
        subTextView.visibility = GONE

        containerLayout.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        containerLayout.orientation = LinearLayout.VERTICAL
        containerLayout.addView(titleTextView)
        containerLayout.addView(subTextView)

        applyColor(context.resources.configuration)

        this.addView(containerLayout)
    }

    constructor(context: Context, title: String) : this(context) {
        this.title = title
        isCenter = true
    }

    constructor(context: Context, title: String, subTitle: String) : this(context) {
        this.title = title
        this.subTitle = subTitle
    }

    constructor(context: Context, xposed: Boolean, titleId: Int) : this(context) {
        this.title = if (xposed) titleId.getStringFromMod() else context.getString(titleId)
        isCenter = true
    }

    constructor(context: Context, xposed: Boolean, titleId: Int, subTitleId: Int) : this(context) {
        this.title = if (xposed) titleId.getStringFromMod() else context.getString(titleId)
        this.subTitle = if (xposed) subTitleId.getStringFromMod() else context.getString(subTitleId)
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

    open var isDisabled: Boolean
        get() = !titleTextView.isEnabled
        set(value) {
            titleTextView.isEnabled = !value
            subTextView.isEnabled = !value
        }

    open var isCenter: Boolean
        get() = titleTextView.gravity == Gravity.CENTER
        set(value) {
            if (value) {
                titleTextView.gravity = Gravity.CENTER
                subTextView.gravity = Gravity.CENTER
            } else {
                titleTextView.gravity = Gravity.START
                subTextView.gravity = Gravity.START
            }
        }

    private fun applyColor(config: Configuration) {
        val isDarkMode = (config.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES

        if (isDarkMode) {
            // 暗黑模式，设置深色
            titleTextView.setTextColor(Color.WHITE)
            subTextView.setTextColor(Color.LTGRAY)
        } else {
            // 普通模式，设置浅色
            titleTextView.setTextColor(Color.BLACK)
            subTextView.setTextColor(Color.DKGRAY)
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        applyColor(newConfig)
    }
}
