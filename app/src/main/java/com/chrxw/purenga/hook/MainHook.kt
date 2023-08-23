package com.chrxw.purenga.hook

import android.R.attr
import android.app.AndroidAppHelper
import android.app.Application
import android.app.Instrumentation
import android.widget.Toast
import com.chrxw.purenga.utils.Helper
import com.chrxw.purenga.utils.Log
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers


/**
 * 插件初始化钩子
 */
class MainHook : IHook {

    companion object {
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
    }


    override fun hook() {
        XposedHelpers.findAndHookMethod(
            Instrumentation::class.java,
            "callApplicationOnCreate",
            Application::class.java,
            object : XC_MethodHook() {
                override fun afterHookedMethod(param: MethodHookParam) {
                    Log.i(param.args[0].toString())

                    if (param.args[0] is Application) {
                        Helper.context =
                            AndroidAppHelper.currentApplication().applicationContext // (param.args[0] as Application).applicationContext

                        if (Helper.init()) {
                            Helper.toast("PureNGA 加载成功, 请到设置页面开启功能")
                        } else {
                            Helper.toast("PureNGA 初始化失败，可能不支持当前版本 NGA: " + Helper.packageInfo?.versionName)
                        }
                    }
                }
            })


    }
}