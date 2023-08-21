package com.chrxw.purenga.utils

import android.content.Context
import android.widget.Toast
import com.chrxw.purenga.hook.IHook
import de.robv.android.xposed.XposedHelpers


/**
 * 功能性钩子
 */
class Helper : IHook {

    companion object {
        var clsNGAApplication: Class<*>? = null
        var AppContext: Context? = null

        fun showToast(text: String) {
            if (AppContext != null) {
                Toast.makeText(AppContext, text, Toast.LENGTH_SHORT)
            } else {
                Log.e("AppContext 为 NULL")
            }
        }
    }

    override fun hookName(): String {
        return "工具类"
    }

    override fun init(classLoader: ClassLoader) {
        clsNGAApplication = XposedHelpers.findClass(
            "gov.pianzong.androidnga.activity.NGAApplication",
            classLoader
        )
    }

    override fun hook() {
        AppContext = XposedHelpers.callMethod(clsNGAApplication, "getInstance") as Context
    }


}
