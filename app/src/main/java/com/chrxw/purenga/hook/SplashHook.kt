package com.chrxw.purenga.hook

import android.app.Activity
import com.chrxw.purenga.Constant
import com.chrxw.purenga.utils.Helper
import com.github.kyuubiran.ezxhelper.AndroidLogger
import com.github.kyuubiran.ezxhelper.HookFactory.`-Static`.createHook
import com.github.kyuubiran.ezxhelper.finders.MethodFinder
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
            // 跳过开屏Logo页面
            MethodFinder.fromClass(clsActivityLifecycle).filterByName("toForeGround").first().createHook {
                replace { param ->
                    val activity = param.args?.get(0) as Activity
                    if (activity.javaClass == clsLoadingActivity) {
                        AndroidLogger.d("跳过启动页")
                        XposedHelpers.setBooleanField(activity, "canJump", true)
                        XposedHelpers.setBooleanField(activity, "isADShow", true)
                        XposedHelpers.callMethod(activity, "goHome")
                    }
                }
            }

            // 修改时间戳实现切屏无广告
            MethodFinder.fromClass(Helper.clsSPUtil).filterByName("getInt")
                .filterByAssignableParamTypes(String::class.java, Int::class.java).first().createHook {
                    after { param ->
                        Helper.toast(param.args[0].toString())
                        when (param.args[0] as String) {
                            "AD_FORGROUND_TIME" -> {
                                AndroidLogger.d("FG " + param.result.toString())
                                param.result = 0
                            }

                            "AD_BACKGROUND_TIME" -> {
                                AndroidLogger.d("BG " + param.result.toString())
                                param.result = 0
                            }
                        }
                    }
                }
        }
    }
}
