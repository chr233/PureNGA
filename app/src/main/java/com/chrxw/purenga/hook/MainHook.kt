package com.chrxw.purenga.hook

import android.R.attr
import android.app.Application
import android.app.Instrumentation
import android.widget.Toast
import com.chrxw.purenga.utils.Helper
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers


/**
 * 插件初始化钩子
 */
class MainHook : IHook {

    companion object {
        var clsAppConfig: Class<*>? = null
    }

    override fun hookName(): String {
        return "插件初始化"
    }

    override fun init(classLoader: ClassLoader) {

        Helper.clsR = classLoader.loadClass("gov.pianzong.androidnga.R")
        Helper.clsRId = classLoader.loadClass("gov.pianzong.androidnga.R\$id")
        Helper.clsRColor = classLoader.loadClass("gov.pianzong.androidnga.R\$color")
        Helper.clsRDimen = classLoader.loadClass("gov.pianzong.androidnga.R\$dimen")
        Helper.clsRDrawable = classLoader.loadClass("gov.pianzong.androidnga.R\$drawable")
        Helper.clsRLayout = classLoader.loadClass("gov.pianzong.androidnga.R\$layout")

        val scale = Helper.context?.resources?.displayMetrics?.density
        if (scale != null) {
            Helper.scale = scale
        }

        clsAppConfig = classLoader.loadClass("com.donews.nga.common.utils.AppConfig")
    }


    override fun hook() {
        XposedHelpers.findAndHookMethod(
            Instrumentation::class.java, "callApplicationOnCreate",
            Application::class.java, object : XC_MethodHook() {
                override fun afterHookedMethod(param: MethodHookParam) {
                    if (param.args[0] is Application) {
                        Helper.context = (param.args[0] as Application).applicationContext

                        if (Helper.init()) {
                            Helper.toast(
                                "PureNGA 加载成功, 请到设置页面开启功能",
                                Toast.LENGTH_LONG
                            )
                        } else {
                            Helper.toast("PureNGA 初始化失败，可能不支持当前版本 NGA: " + Helper.packageInfo?.versionName)
                        }
                    }
                }
            })

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