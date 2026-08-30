package com.chrxw.purenga.ui

import android.content.Context
import android.graphics.Color
import android.util.TypedValue
import android.widget.TableRow.LayoutParams
import androidx.appcompat.widget.AppCompatTextView
import com.chrxw.purenga.Constant
import com.chrxw.purenga.utils.DialogUtils
import com.chrxw.purenga.utils.ExtensionUtils.toPixel

/**
 * 文本控件
 */
open class CopyrightWarnView : AppCompatTextView {
    constructor(context: Context) : super(context) {
        text = Constant.STR_COPYRIGHT_WARNING
        setTextColor(Color.RED)
        setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
        textAlignment = TEXT_ALIGNMENT_CENTER
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        setPadding(16.toPixel(context), 8.toPixel(context), 16.toPixel(context), 8.toPixel(context))

        setOnClickListener {
            DialogUtils.popupDonateDialog(context)
        }
    }
}
