package com.chrxw.purenga.ui

import android.annotation.SuppressLint
import android.content.Context
import android.view.Gravity
import android.view.View
import android.view.View.OnClickListener
import android.widget.Switch
import com.chrxw.purenga.utils.Helper

/**
 * 开关控件
 */
class ToggleItemView(context: Context, spKey: String, defValue: Boolean = false) : ClickableItemView(context),
    OnClickListener {
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private val switch = Switch(context)
    private var spKey: String = ""

    init {
        this.spKey = spKey
        switch.isChecked = Helper.getSpBool(spKey, defValue)

        val switchParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT).apply {
            gravity = Gravity.CENTER_VERTICAL or Gravity.END
        }
        switch.layoutParams = switchParams

        this.containerLayout.setOnClickListener(this)
        this.titleTextView.setOnClickListener(this)
        this.subTextView.setOnClickListener(this)
        this.switch.setOnClickListener(this)
        this.addView(switch)
    }

    constructor(context: Context) : this(context, "")

    private var isChecked: Boolean
        get() = switch.isChecked
        set(value) {
            switch.isChecked = value
            Helper.setSpBool(spKey, isChecked)
        }

    override var idDisabled: Boolean
        get() = !switch.isEnabled
        set(value) {
            switch.isEnabled = !value
            super.idDisabled = value
        }

    override fun onClick(v: View?) {
        isChecked = if (v !is Switch) {
            !isChecked
        } else {
            switch.isChecked
        }
    }
}