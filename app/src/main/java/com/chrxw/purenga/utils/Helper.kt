package com.chrxw.purenga.utils

import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageInfo
import android.widget.Toast
import com.chrxw.purenga.BuildConfig
import com.chrxw.purenga.Constant
import de.robv.android.xposed.XposedHelpers


/**
 * 功能性钩子
 */
class Helper {
    companion object {
        lateinit var context: Context

        lateinit var prefs: SharedPreferences
        lateinit var packageInfo: PackageInfo

        lateinit var clsR: Class<*>
        lateinit var clsRId: Class<*>
        lateinit var clsRColor: Class<*>
        lateinit var clsRDimen: Class<*>
        lateinit var clsRDrawable: Class<*>
        lateinit var clsRLayout: Class<*>

        var darkMode = false

        /**
         * 初始化
         */
        fun init(): Boolean {
            return try {
                prefs = context.getSharedPreferences(Constant.PLUGIN_PREFERENCE, Context.MODE_PRIVATE)
                packageInfo = context.packageManager.getPackageInfo(Constant.NGA_PACKAGE_NAME, 0)
                Log.i(packageInfo)


                packageInfo = context.packageManager.getPackageInfo(BuildConfig.APPLICATION_ID, 0)
                Log.i(packageInfo)

                true
            } catch (e: Exception) {
                Log.e(e)
                false
            }
        }

        /**
         * 显示Toast
         */
        fun toast(text: String, duration: Int = Toast.LENGTH_SHORT) {
            if (context != null) {
                Toast.makeText(context, text, duration).show()
            } else {
                Log.d("AppContext 为 NULL")
            }
        }

        private inline fun getRes(cls: Class<*>?, key: String): Int {
            return try {
                XposedHelpers.getStaticIntField(cls, key)
            } catch (e: Throwable) {
                Log.e("加载资源 $key 失败")
                Log.e(e)
                -1
            }
        }

        fun getRId(key: String): Int {
            return getRes(clsRId, key)
        }

        fun getRColor(key: String): Int {
            return getRes(clsRColor, key)
        }

        fun getRDimen(key: String): Int {
            return getRes(clsRDimen, key)

        }

        fun getRDrawable(key: String): Int {
            return getRes(clsRDrawable, key)
        }

        fun getRLayout(key: String): Int {
            return getRes(clsRLayout, key)
        }

    }
}
