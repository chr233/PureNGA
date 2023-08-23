package com.chrxw.purenga.hook

import android.R.attr.classLoader
import com.chrxw.purenga.Constant
import com.chrxw.purenga.utils.Helper
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.XposedHelpers


/**
 * 优化功能钩子
 */
class OptimizeHook : IHook {

    companion object {
        lateinit var clsAppConfig: Class<*>
    }

    override fun hookName(): String {
        return "优化功能"
    }

    override fun init(classLoader: ClassLoader) {
        clsAppConfig = classLoader.loadClass("com.donews.nga.common.utils.AppConfig")


    }

    override fun hook() {
        if (Helper.prefs.getBoolean(Constant.Optimize, false)) {
            XposedHelpers.findAndHookMethod(
                clsAppConfig,
                "isAgreedAgreement",
                object : XC_MethodReplacement() {
                    override fun replaceHookedMethod(param: MethodHookParam?): Any {
                        return true
                    }
                })
        }
    }
}

