package com.chrxw.purenga.hook

import com.chrxw.purenga.utils.Helper
import com.github.kyuubiran.ezxhelper.AndroidLogger
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.XposedHelpers


/**
 * 插件初始化钩子
 */
class MainHook : IHook {
    override fun init(classLoader: ClassLoader) {
        Helper.clsSPUtil = classLoader.loadClass("com.donews.nga.common.utils.SPUtil")
        Helper.clsR = classLoader.loadClass("gov.pianzong.androidnga.R")
        Helper.clsRId = classLoader.loadClass("gov.pianzong.androidnga.R\$id")

        XposedHelpers.findAndHookMethod(
            "gov.pianzong.androidnga.activity.NGAApplication",
            classLoader,
            "preThirdParty",
            object : XC_MethodReplacement() {
                override fun replaceHookedMethod(param: MethodHookParam?) {
                    AndroidLogger.i("preThirdParty")
                }
            })

        XposedHelpers.findAndHookMethod(
            "gov.pianzong.androidnga.activity.LoadingActivity",
            classLoader,
            "loadAD",
            object : XC_MethodReplacement() {
                override fun replaceHookedMethod(param: MethodHookParam?) {
                    AndroidLogger.i("loadAD")
                }
            })
    }

    override fun hook() {
    }
}