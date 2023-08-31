package com.chrxw.purenga.ui

import android.annotation.SuppressLint
import android.content.Context
import android.view.Gravity
import android.view.View
import android.view.View.OnClickListener
import android.widget.Switch
import com.chrxw.purenga.utils.Helper
import com.chrxw.purenga.utils.Log

class ToggleItemView(context: Context) : ClickableItemView(context), OnClickListener {
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private val switch = Switch(context)
    private var spKey: String? = null

    init {
        val switchParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT).apply {
            gravity = Gravity.CENTER_VERTICAL or Gravity.END
        }
        switch.layoutParams = switchParams
        this.addView(switch)
        this.setOnClickListener(this)
    }

    constructor(context: Context, spKey: String) : this(context) {
        this.spKey = spKey
        if (!spKey.isNullOrEmpty()) {
            switch.isChecked = Helper.getSpBool(spKey, false)
        }
    }

    var key: String?
        get() = spKey
        set(value) {
            spKey = value
        }

    fun bind(onToggle: ((Boolean) -> Unit)? = null) {
        switch.setOnCheckedChangeListener { _, isChecked ->
            onToggle?.invoke(isChecked)

            if (key.isNullOrEmpty()) {
                Helper.spPlugin.edit().putBoolean(key, isChecked).apply()
            }
        }
    }

    fun bind(key: String, onToggle: ((Boolean) -> Unit)? = null) {
        this.spKey = key
        isChecked = Helper.getSpBool(key, false)
        bind(onToggle)
    }

    var isChecked: Boolean
        get() = switch.isChecked
        set(value) {
            switch.isChecked = value
        }

    override fun onClick(v: View?) {
        isChecked = !isChecked
        if (!spKey.isNullOrEmpty()) {
            Helper.spPlugin.edit().putBoolean(spKey, isChecked).apply()
            Log.i(Helper.spPlugin.getBoolean(spKey, false))
        }
    }
}