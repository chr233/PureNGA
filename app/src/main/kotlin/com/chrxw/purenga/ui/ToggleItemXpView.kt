package com.chrxw.purenga.ui

import android.content.Context
import android.view.View.OnClickListener
import com.chrxw.purenga.utils.ExtensionUtils.getStringFromMod

/**
 * 开关控件
 */
class ToggleItemXpView : ToggleItemView, OnClickListener {
    constructor(context: Context, spKey: String = "") : super(context, spKey, true) {
        xposed = true
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
}