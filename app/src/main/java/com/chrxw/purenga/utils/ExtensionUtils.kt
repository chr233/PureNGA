package com.chrxw.purenga.utils

import android.content.Context
import android.util.DisplayMetrics
import com.github.kyuubiran.ezxhelper.AndroidLogger
import de.robv.android.xposed.XC_MethodHook

/**
 * 显示单位换算
 */
object ExtensionUtils {
    /**
     * 单位转换
     */
    fun Int.toPixel(context: Context): Int {
        val resources = context.resources
        val metrics = resources.displayMetrics
        return this * (metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT)
    }

    /**
     * 输出日志
     */
    fun XC_MethodHook.MethodHookParam.log() {
        if (Helper.enableLog) {
            AndroidLogger.d("Method: ${this.method.name}")
            AndroidLogger.d("Object: ${this.thisObject}")

            if (this.args.any()) {
                AndroidLogger.d("Args:")
                this.args.forEachIndexed { index, item ->
                    val cls = item?.javaClass ?: "NULL"
                    AndroidLogger.d(" $index: $item ($cls)")
                }
            }
        }
    }
}