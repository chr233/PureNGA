package com.chrxw.purenga.hook

import android.app.Activity
import com.chrxw.purenga.BuildConfig
import com.chrxw.purenga.utils.Log
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers

/**
 * 开屏广告钩子
 */
class SplashHook : IHook {

    companion object {
        var clsLoadingActivity: Class<*>? = null
        var clsSPUtil: Class<*>? = null
    }

    override fun hookName(): String {
        return "开屏广告过滤"
    }

    override fun init(classLoader: ClassLoader) {
        clsLoadingActivity =
            XposedHelpers.findClass("gov.pianzong.androidnga.activity.LoadingActivity", classLoader)

        clsSPUtil =
            XposedHelpers.findClass("com.donews.nga.common.utils.SPUti", classLoader)
    }

    override fun hook() {
        // 模拟点击跳过按钮, 立即进入主界面
        XposedHelpers.findAndHookMethod(
            Companion.clsLoadingActivity,
            "toForeGround",
            Activity::class.java,
            object : XC_MethodHook() {
                @Throws(Throwable::class)
                override fun beforeHookedMethod(param: MethodHookParam?) {
                    super.beforeHookedMethod(param)
                    val activity = param?.args?.get(0) as Activity
                    if (activity.javaClass == clsLoadingActivity) {
                        Log.d("跳过启动页")
                        XposedHelpers.setBooleanField(activity, "canJump", true)
                        XposedHelpers.setBooleanField(activity, "isADShow", true)
                        XposedHelpers.callMethod(activity, "goHome")
                    }
                    //Log.d(activity.toString())
                }
            })

        // 修改时间戳实现切屏无广告
        XposedHelpers.findAndHookMethod(
            clsSPUtil,
            "getInt",
            String::class.java,
            Int::class.java,
            object : XC_MethodHook() {
                @Throws(Throwable::class)
                override fun afterHookedMethod(param: MethodHookParam) {
                    when (param.args[0] as String) {
                        "AD_FORGROUND_TIME" -> {
                            Log.d("FG " + param.result.toString())
                            param.result = 0
                        }

                        "AD_BACKGROUND_TIME" -> {
                            Log.d("BG " + param.result.toString())
                            param.result = 0
                        }
                    }
                }
            })

    }
}
