package com.chrxw.purenga.ui

import android.content.Context
import android.content.res.Configuration
import android.graphics.Color
import android.view.Gravity
import android.widget.LinearLayout

class DarkContainLayout : LinearLayout {
    private val xposed: Boolean

    constructor(context: Context, xposed: Boolean) : super(context) {
        this.xposed = xposed
        this.layoutParams = LayoutParams(
            LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT
        )
        gravity = Gravity.CENTER
        orientation = VERTICAL

        applyColor(context.resources.configuration)
    }

    private fun applyColor(config: Configuration) {
        val isDarkMode = if (xposed) {
            false
        } else {
            (config.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
        }

        if (isDarkMode) {
            // 暗黑模式，设置深色
            setBackgroundColor(Color.DKGRAY)
        } else {
            // 普通模式，设置浅色
            setBackgroundColor(Color.WHITE)
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        applyColor(newConfig)
    }
}