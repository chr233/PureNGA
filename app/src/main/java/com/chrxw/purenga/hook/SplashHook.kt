package com.chrxw.purenga.hook

import android.app.Activity
import com.chrxw.purenga.Constant
import com.chrxw.purenga.utils.Helper
import com.chrxw.purenga.utils.Log
import com.github.kyuubiran.ezxhelper.HookFactory.`-Static`.createHook
import com.github.kyuubiran.ezxhelper.finders.MethodFinder
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers


/**
 * 开屏广告钩子
 */
class SplashHook : IHook {

    companion object {
        private lateinit var clsLoadingActivity: Class<*>
        private lateinit var clsActivityLifecycle: Class<*>
        private lateinit var clsNGAApplication: Class<*>
    }

    override fun hookName(): String {
        return "开屏广告过滤"
    }

    override fun init(classLoader: ClassLoader) {
        clsLoadingActivity = classLoader.loadClass("gov.pianzong.androidnga.activity.LoadingActivity")
        clsActivityLifecycle = classLoader.loadClass("com.donews.nga.interfaces.ActivityLifecycleImpl")
        clsNGAApplication = classLoader.loadClass("gov.pianzong.androidnga.activity.NGAApplication")
    }

    override fun hook() {
        if (Helper.getSpBool(Constant.PURE_SPLASH_AD, false)) {

//            XposedHelpers.findAndHookMethod(
//                Helper.clsSPUtil,
//                "getInt",
//                String::class.java,
//                Int::class.javaPrimitiveType,
//                object : XC_MethodHook() {
//                    @Throws(Throwable::class)
//                    override fun beforeHookedMethod(param: MethodHookParam?) {
//                        when (param?.args?.get(0) as String) {
//                            "AD_FORGROUND_TIME" -> {
//                                Log.d("FG " + param.result.toString())
//                                param.result = 0
//                            }
//
//                            "AD_BACKGROUND_TIME" -> {
//                                Log.d("BG " + param.result.toString())
//                                param.result = 0
//                            }
//                        }
//                    }
//                })
//
//            XposedHelpers.findAndHookMethod(Helper.clsSPUtil, "putInt",
//                String::class.java,
//                Int::class.javaPrimitiveType, object : XC_MethodHook() {
//                    @Throws(Throwable::class)
//                    override fun beforeHookedMethod(param: MethodHookParam) {
//                    }
//
//                    @Throws(Throwable::class)
//                    override fun afterHookedMethod(param: MethodHookParam) {
//                        Log.i("putInt ${param.args[0]} ${param.result}")
//                    }
//                })

            // 修改时间戳实现切屏无广告
//            MethodFinder.fromClass(Helper.clsSPUtil).filterByName("getInt").filterFinal().first().createHook {
//                replace { param ->
//                    val key = param.args[0]
//                    when (key as String) {
//                        "AD_FORGROUND_TIME" -> {
//                            return@replace 0
//                        }
//
//                        "AD_BACKGROUND_TIME" -> {
//                            Log.d("BG " + param.result.toString())
//                            return@replace 0
//                        }
//
//                        else -> {
//                            return@replace Helper.spDoinfo.getInt(key, param.args[1] as Int)
//                        }
//                    }
//                }
//            }

            // 修改时间戳实现切屏无广告
            XposedHelpers.findAndHookMethod(
                Helper.clsSPUtil,
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

        // 跳过开屏Logo页面
        if (Helper.getSpBool(Constant.SKIP_SPLASH_SCREEN, false)) {
            MethodFinder.fromClass(clsActivityLifecycle).filterByName("toForeGround").first().createHook {
                replace { param ->
                    val activity = param?.args?.get(0) as Activity
                    if (activity.javaClass == clsLoadingActivity) {
                        Log.d("跳过启动页")
                        XposedHelpers.setBooleanField(activity, "canJump", true)
                        XposedHelpers.setBooleanField(activity, "isADShow", true)
                        XposedHelpers.callMethod(activity, "goHome")
                    }
                }
            }
        }
    }
}
