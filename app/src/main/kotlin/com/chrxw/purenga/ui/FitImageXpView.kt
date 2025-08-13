package com.chrxw.purenga.ui

import android.content.Context
import android.content.res.Resources

class FitImageXpView : FitImageView {
    constructor(context: Context) : super(context, true)
    constructor(context: Context, resId: Int, theme: Resources.Theme? = null) : this(context) {
        setImageResource(resId, theme)
    }
}