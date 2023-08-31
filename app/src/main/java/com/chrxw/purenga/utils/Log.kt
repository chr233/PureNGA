@file:Suppress("unused")

package com.chrxw.purenga.utils

import de.robv.android.xposed.XposedBridge
import android.util.Log as ALog

/**
 * 日志
 */
object Log {
    private const val TAG = "PureNGA"
    private const val maxLength = 3000

    @JvmStatic
    private fun doLog(f: (String, String) -> Int, obj: Any?, toXposed: Boolean = true) {
        if (obj is Throwable) {
            val str = ALog.getStackTraceString(obj)
            f(TAG, str)
            if (toXposed) {
                XposedBridge.log(obj)
            }

        } else {
            val str = obj.toString()

            if (str.length > maxLength) {
                val chunkCount: Int = str.length / maxLength
                for (i in 0..chunkCount) {
                    val max: Int = maxLength * (i + 1)
                    if (max >= str.length) {
                        doLog(f, str.substring(maxLength * i))
                    } else {
                        doLog(f, str.substring(maxLength * i, max))
                    }
                }
            } else {
                f(TAG, str)
                if (toXposed) {
                    XposedBridge.log("$TAG : $str")
                }
            }
        }
    }

    @JvmStatic
    fun d(obj: Any?) {
        doLog(ALog::d, obj)
    }

    @JvmStatic
    fun i(obj: Any?) {
        doLog(ALog::i, obj)
    }

    @JvmStatic
    fun e(obj: Any?) {
        doLog(ALog::e, obj)
    }

    @JvmStatic
    fun v(obj: Any?) {
        doLog(ALog::v, obj)
    }

    @JvmStatic
    fun w(obj: Any?) {
        doLog(ALog::w, obj)
    }
}

