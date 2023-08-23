package com.chrxw.purenga.hook

import android.R.attr.classLoader
import android.app.AlertDialog
import android.content.Context
import android.view.WindowManager
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
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
        lateinit var clsHomeDrawerLayout: Class<*>
    }

    override fun hookName(): String {
        return "界面优化"
    }

    override fun init(classLoader: ClassLoader) {
        clsAppConfig = classLoader.loadClass("com.donews.nga.common.utils.AppConfig")
        clsHomeDrawerLayout = classLoader.loadClass("com.donews.nga.widget.HomeDrawerLayout")
    }

    override fun hook() {
        //
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

        XposedHelpers.findAndHookMethod(
            clsHomeDrawerLayout,
            "initLayout",
            object : XC_MethodHook() {
                override fun afterHookedMethod(param: MethodHookParam) {
                    val viewBinding = XposedHelpers.getObjectField(param.thisObject, "binding")
                    val root = XposedHelpers.callMethod(viewBinding, "getRoot") as LinearLayout
                    val scrollView = root.getChildAt(1) as ScrollView
                    val linearLayout = scrollView.getChildAt(0) as LinearLayout
                    linearLayout.removeViewAt(linearLayout.childCount - 1)
                }
            })


        // 获取是否为深色模式
        XposedHelpers.findAndHookMethod(
            clsAppConfig,
            "isDarkModel",
            object : XC_MethodHook() {
                @Throws(Throwable::class)
                override fun afterHookedMethod(param: MethodHookParam) {
                    Helper.darkMode = param.result as Boolean
                }
            })

        XposedHelpers.findAndHookMethod(
            clsAppConfig,
            "setDarkModel",
            Boolean::class.java,
            object : XC_MethodHook() {
                @Throws(Throwable::class)
                override fun afterHookedMethod(param: MethodHookParam) {
                    var result = param.result as Boolean?
                    if (result != null) {
                        Helper.darkMode = result
                    } else {
                        Helper.darkMode = false
                    }
                }
            })
    }
}

