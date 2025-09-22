package com.chrxw.purenga.ui

import android.annotation.SuppressLint
import android.content.Context
import android.view.Gravity
import android.view.View
import android.view.View.OnClickListener
import android.widget.Switch
import com.chrxw.purenga.utils.ExtensionUtils.getStringFromMod
import com.chrxw.purenga.utils.ExtensionUtils.toPixel
import com.chrxw.purenga.utils.Helper

/**
 * 开关控件
 */
open class ToggleItemView : ClickableItemView, OnClickListener {
    private val switch: Switch
    private val spKey: String

    constructor(context: Context, spKey: String = "") : super(context) {
        this.spKey = spKey
        switch = Switch(context)
        switch.isChecked = Helper.getSpBool(spKey, false)
        switch.layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT).apply {
            gravity = Gravity.CENTER_VERTICAL or Gravity.END
        }
        switch.setOnClickListener(this)
        this.addView(switch)

        containerLayout.setOnClickListener(this)
        titleTextView.setOnClickListener(this)
        titleTextView.setPadding(0.toPixel(context), 0.toPixel(context), 64.toPixel(context), 0.toPixel(context))
        subTextView.setOnClickListener(this)
        subTextView.setPadding(0.toPixel(context), 0.toPixel(context), 64.toPixel(context), 0.toPixel(context))

        applyColor(context.resources.configuration)
    }

    protected constructor(context: Context, spKey: String = "", xposed: Boolean) : this(context, spKey) {
        this.xposed = xposed
        applyColor(context.resources.configuration)
    }

    constructor(context: Context, spKey: String = "", title: String) : this(context, spKey) {
        this.title = title
        isCenter = true
    }

    constructor(context: Context, spKey: String = "", title: String, subTitle: String) : this(context, spKey) {
        this.title = title
        this.subTitle = subTitle
    }

    constructor(context: Context, spKey: String = "", titleId: Int) : this(context, spKey) {
        this.title = if (xposed) titleId.getStringFromMod() else context.getString(titleId)
        isCenter = true
    }

    constructor(context: Context, spKey: String = "", titleId: Int, subTitleId: Int) : this(
        context, spKey
    ) {
        this.title = if (xposed) titleId.getStringFromMod() else context.getString(titleId)
        this.subTitle = if (xposed) subTitleId.getStringFromMod() else context.getString(subTitleId)
    }

    private var isChecked: Boolean
        get() = switch.isChecked
        set(value) {
            switch.isChecked = value
            Helper.setSpBool(spKey, isChecked)
        }

    override var isDisabled: Boolean
        get() = !switch.isEnabled
        set(value) {
            switch.isEnabled = !value
            super.isDisabled = value
        }

    override fun onClick(v: View?) {
        isChecked = if (v !is Switch) {
            !isChecked
        } else {
            switch.isChecked
        }
    }
}