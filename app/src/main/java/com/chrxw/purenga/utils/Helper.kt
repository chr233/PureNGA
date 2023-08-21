package com.chrxw.purenga.utils

import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.res.Resources
import android.widget.Toast
import com.chrxw.purenga.Constant
import de.robv.android.xposed.XposedBridge


/**
 * 功能性钩子
 */
class Helper {
    companion object {
        var context: Context? = null

        var prefs: SharedPreferences? = null
        var packageInfo: PackageInfo? = null

        fun showToast(text: String, duration: Int = Toast.LENGTH_SHORT) {
            if (context != null) {
                Toast.makeText(context, text, duration).show()
            } else {
                Log.d("AppContext 为 NULL")
            }
        }

        fun init(): Boolean {
            return try {
                prefs = context?.getSharedPreferences("zhiliao_preferences", Context.MODE_PRIVATE)
                packageInfo = context?.packageManager?.getPackageInfo(Constant.NGA_PACKAGE_NAME, 0)

                true
            } catch (e: Exception) {
                Log.e(e)
                false
            }
        }

    }
}
