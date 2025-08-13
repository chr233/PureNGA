package com.chrxw.purenga.ui

import android.content.Context
import com.chrxw.purenga.utils.ExtensionUtils.getStringFromMod

/**
 * 文本控件
 */
open class ClickableItemXpView : ClickableItemView {
    constructor(context: Context) : super(context,true)
    constructor(context: Context, title: String) : this(context) {
        this.title = title
        isCenter = true
    }

    constructor(context: Context, title: String, subTitle: String) : this(context) {
        this.title = title
        this.subTitle = subTitle
    }

    constructor(context: Context, titleId: Int) : this(context) {
        this.title = if (xposed) titleId.getStringFromMod() else context.getString(titleId)
        isCenter = true
    }

    constructor(context: Context, titleId: Int, subTitleId: Int) : this(context) {
        this.title = if (xposed) titleId.getStringFromMod() else context.getString(titleId)
        this.subTitle = if (xposed) subTitleId.getStringFromMod() else context.getString(subTitleId)
    }
}
