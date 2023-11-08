package com.chrxw.purenga.hook

import android.app.Activity
import com.chrxw.purenga.Constant
import com.chrxw.purenga.utils.ExtensionUtils.findFirstMethodByName
import com.chrxw.purenga.utils.ExtensionUtils.log
import com.chrxw.purenga.utils.Helper
import com.github.kyuubiran.ezxhelper.AndroidLogger
import com.github.kyuubiran.ezxhelper.HookFactory.`-Static`.createHook
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
            val hook1 = findFirstMethodByName(clsActivityLifecycle, "toForeGround")?.createHook {
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
            val hook2 = findFirstMethodByName(MainHook.clsSPUtil, "getInt")?.createHook {
                after {
                    it.log()

                    when (it.args[0] as String) {
                        "AD_FORGROUND_TIME", "AD_BACKGROUND_TIME" -> {
                            it.result = 0
                        }
                    }
                }
            }

            val hook3 = findFirstMethodByName(clsLoadingActivity_a, "callBack")?.createHook {
                replace {
                    it.log()
                }
            }

            if (hook1 == null || hook2 == null || hook3 == null) {
                AndroidLogger.w("部分hook加载失败")
            }

        }
    }

    override var name = "SplashHook"
}
