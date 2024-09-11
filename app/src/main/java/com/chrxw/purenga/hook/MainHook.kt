package com.chrxw.purenga.hook

import android.app.Activity
import android.content.Context
import com.chrxw.purenga.BuildConfig
import com.chrxw.purenga.Constant
import com.chrxw.purenga.utils.ExtensionUtils.findFirstMethodByName
import com.chrxw.purenga.utils.ExtensionUtils.log
import com.chrxw.purenga.utils.Helper
import com.github.kyuubiran.ezxhelper.AndroidLogger
import com.github.kyuubiran.ezxhelper.EzXHelper
import com.github.kyuubiran.ezxhelper.HookFactory.`-Static`.createHook


/**
 * 插件初始化钩子
 */
class MainHook : IHook {
    companion object {
        lateinit var clsNGAApplication: Class<*>
        lateinit var clsAppConfig: Class<*>
        lateinit var clsLoadingActivity: Class<*>
        lateinit var clsSPUtil: Class<*>
        lateinit var clsMainActivity: Class<*>
        lateinit var clsActivityLifecycle: Class<*>
    }

    override fun init(classLoader: ClassLoader) {
        clsNGAApplication = classLoader.loadClass("gov.pianzong.androidnga.activity.NGAApplication")
        clsAppConfig = classLoader.loadClass("com.donews.nga.common.utils.AppConfig")
        clsLoadingActivity = classLoader.loadClass("gov.pianzong.androidnga.activity.LoadingActivity")
        clsMainActivity = classLoader.loadClass("com.donews.nga.activitys.MainActivity")
        clsActivityLifecycle = classLoader.loadClass("com.donews.nga.interfaces.ActivityLifecycleImpl")

        clsSPUtil = classLoader.loadClass("com.donews.nga.common.utils.SPUtil")

        Helper.clsRId = classLoader.loadClass("gov.pianzong.androidnga.R\$id")
        Helper.clsDrawerId = classLoader.loadClass("gov.pianzong.androidnga.R\$drawable")

        EzXHelper.appContext.run {
            Helper.spDoinfo = getSharedPreferences(Constant.DN_INFO_PREFERENCE_NAME, Context.MODE_PRIVATE)
            Helper.spPlugin = getSharedPreferences(Constant.PLUGIN_PREFERENCE_NAME, Context.MODE_PRIVATE)

            Helper.enableLog = Helper.getSpBool(Constant.ENABLE_HOOK_LOG, false)
        }
    }

    override fun hook() {
        // 屏蔽弹窗
        if (Helper.getSpBool(Constant.KILL_POPUP_DIALOG, false)) {
            findFirstMethodByName(clsAppConfig, "isAgreedAgreement")?.createHook {
                replace {
                    it.log()
                    return@replace true
                }
            }

            findFirstMethodByName(clsNGAApplication, "showNotificationDialog")?.createHook {
                replace {
                    it.log()
                }
            }

            findFirstMethodByName(clsMainActivity, "showNotificationDialog")?.createHook {
                replace {
                    it.log()
                }
            }

            if (BuildConfig.DEBUG) {
                findFirstMethodByName(clsNGAApplication, "handleMessage")?.createHook {
                    before {
                        it.log()
                        AndroidLogger.w(it.args.get(0).toString())
                    }
                }
            }
        }

        // 记录 Activity 日志
        if (Helper.getSpBool(Constant.ENABLE_ACTIVITY_LOG, false)) {
            findFirstMethodByName(clsActivityLifecycle, "onActivityStarted")?.createHook {
                after {
                    it.log()

                    val activity = it.args[0] as Activity

                    AndroidLogger.e("Activity 日志:")
                    AndroidLogger.i(activity.toString())
                    AndroidLogger.i(activity.intent.toString())
                    AndroidLogger.i(activity.intent.extras.toString())
                }
            }
        }
    }

    override var name = "MainHook"
}