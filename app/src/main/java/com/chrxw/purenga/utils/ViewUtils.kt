package com.chrxw.purenga.utils

import android.content.Context
import android.util.DisplayMetrics

object ViewUtils {
    fun Float.toPixel(context: Context): Float {
        val resources = context.resources
        val metrics = resources.displayMetrics
        return this * (metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT)
    }

    fun Int.toPixel(context: Context): Int {
        val resources = context.resources
        val metrics = resources.displayMetrics
        return this * (metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT)
    }

    fun Float.toDp(context: Context): Float {
        val resources = context.resources
        val metrics = resources.displayMetrics
        return this / (metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT)
    }

    fun Int.toDp(context: Context): Int {
        val resources = context.resources
        val metrics = resources.displayMetrics
        return this / (metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT)
    }
}