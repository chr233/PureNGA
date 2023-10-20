package com.chrxw.purenga.hook

import android.app.Activity
import com.chrxw.purenga.Constant
import com.chrxw.purenga.utils.ExtensionUtils.log
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
        private lateinit var clsActivityLifecycle: Class<*>
        private lateinit var clsLoadingActivity_a: Class<*>
    }

    override fun init(classLoader: ClassLoader) {
        clsActivityLifecycle = classLoader.loadClass("com.donews.nga.interfaces.ActivityLifecycleImpl")
        clsLoadingActivity_a = classLoader.loadClass("gov.pianzong.androidnga.activity.LoadingActivity\$a")

    }

    override fun hook() {
        if (Helper.getSpBool(Constant.PURE_SPLASH_AD, false)) {
            // 跳过开屏Logo页面
            MethodFinder.fromClass(clsActivityLifecycle).filterByName("toForeGround").first().createHook {
                replace {
                    it.log()

                    val activity = it.args[0] as Activity
                    if (activity.javaClass == MainHook.clsLoadingActivity) {
                        AndroidLogger.d("跳过启动页")
                        XposedHelpers.setBooleanField(activity, "canJump", true)
                        XposedHelpers.setBooleanField(activity, "isADShow", true)
                        XposedHelpers.callMethod(activity, "goHome")
                    }
                }
            }

            // 修改时间戳实现切屏无广告
            MethodFinder.fromClass(MainHook.clsSPUtil).filterByName("getInt")
                .filterByAssignableParamTypes(String::class.java, Int::class.java).first().createHook {
                    after {
                        it.log()

                        when (it.args[0] as String) {
                            "AD_FORGROUND_TIME", "AD_BACKGROUND_TIME" -> {
                                it.result = 0
                            }
                        }
                    }
                }

            try {
                MethodFinder.fromClass(clsLoadingActivity_a).filterByName("callBack").first().createHook {
                    replace {
                        it.log()
                    }
                }
            } catch (e: Throwable) {
                AndroidLogger.w("去开屏广告功能部分开启失败")
            }
        }
    }

    override var name = "SplashHook"
}
