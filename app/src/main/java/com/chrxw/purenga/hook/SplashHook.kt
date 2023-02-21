package com.chrxw.purenga.hook

import android.app.Activity
import com.chrxw.purenga.utils.Log
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers

/**
 * 开屏广告钩子
 */
class SplashHook(classLoader: ClassLoader) : BaseHook(classLoader) {

    override fun startHook() {
        try {
            // 获取ActivityLifecycleImpl类
            val clsLifeCycle = XposedHelpers.findClass(
                "com.donews.nga.interfaces.ActivityLifecycleImpl",
                mClassLoader
            )
            // 获取LoadingActivity类
            val clsLoadingActivity = XposedHelpers.findClass(
                "gov.pianzong.androidnga.activity.LoadingActivity",
                mClassLoader
            )
            // Hook toForeGround 方法
            XposedHelpers.findAndHookMethod(
                clsLifeCycle,
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
                        Log.d(activity.toString())
                    }
                })
            // 获取SPUtil类
            val clsSPUtil =
                XposedHelpers.findClass("com.donews.nga.common.utils.SPUtil", mClassLoader)
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
                            else -> {
//                                Log.d(param.args[0].toString() + " " + param.result.toString())
                            }
                        }
                    }
                })
        } catch (e: Exception) {
            Log.e(e)
        }
    }
}
