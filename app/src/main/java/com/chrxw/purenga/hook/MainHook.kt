package com.chrxw.purenga.hook

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

    override fun hookName(): String {
        return "插件初始化"
    }

    override fun init(classLoader: ClassLoader) {
    }

    override fun hook() {
        XposedHelpers.findAndHookMethod(
            Instrumentation::class.java, "callApplicationOnCreate",
            Application::class.java, object : XC_MethodHook() {
                override fun afterHookedMethod(param: MethodHookParam) {
                    if (param.args[0] is Application) {
                        Helper.context = (param.args[0] as Application).applicationContext

                        if (Helper.init()) {
                            Helper.showToast(
                                "PureNGA 加载成功, 请到设置页面开启功能",
                                Toast.LENGTH_LONG
                            )
                        } else {
                            Helper.showToast("PureNGA 初始化失败，可能不支持当前版本 NGA: " + Helper.packageInfo?.versionName)
                        }
                    }
                }
            })
    }

}