package com.chrxw.purenga.hook

import android.app.Activity
import android.content.Context
import com.chrxw.purenga.BuildConfig
import com.chrxw.purenga.Constant
import com.chrxw.purenga.hook.base.IHook
import com.chrxw.purenga.utils.DialogUtils
import com.chrxw.purenga.utils.ExtensionUtils.findFirstMethodByName
import com.chrxw.purenga.utils.ExtensionUtils.forceLog
import com.chrxw.purenga.utils.ExtensionUtils.log
import com.chrxw.purenga.utils.Helper
import com.chrxw.purenga.utils.UpdateUtils
import com.github.kyuubiran.ezxhelper.AndroidLogger
import com.github.kyuubiran.ezxhelper.EzXHelper
import com.github.kyuubiran.ezxhelper.HookFactory.`-Static`.createHook


/**
 * 插件初始化钩子
 */
class MainHook : IHook {
    /**
     *
     */
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
        Helper.clsRId2 = classLoader.loadClass("com.donews.nga.common.R\$id")
        Helper.clsDrawerId = classLoader.loadClass("gov.pianzong.androidnga.R\$drawable")

        EzXHelper.appContext.apply {
            Helper.spDoinfo = getSharedPreferences(Constant.DN_INFO_PREFERENCE_NAME, Context.MODE_PRIVATE)
            Helper.spPlugin = getSharedPreferences(Constant.PLUGIN_PREFERENCE_NAME, Context.MODE_PRIVATE)

            Helper.enableLog = Helper.getSpBool(Constant.ENABLE_HOOK_LOG, false)
        }
    }

    override fun hook() {
        // 清理静态资源
        findFirstMethodByName(clsMainActivity, "onDestroy")?.createHook {
            after {
                it.log()
                Helper.clearStaticResource()
            }
        }

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

            findFirstMethodByName(clsMainActivity, "setupNotification")?.createHook {
                replace {
                    it.log()
                }
            }

            if (BuildConfig.DEBUG) {
                findFirstMethodByName(clsNGAApplication, "handleMessage")?.createHook {
                    before {
                        it.log()
                        AndroidLogger.w(it.args[0].toString())
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

        if (Helper.getSpBool(Constant.CHECK_PLUGIN_UPDATE, true)) {
            findFirstMethodByName(clsMainActivity, "initLayout")?.createHook {
                after {
                    it.forceLog()

                    val activity = it.thisObject as Activity

                    UpdateUtils.getReleaseInfo { res ->
                        if (res == null) {
                            return@getReleaseInfo
                        }

                        val code = UpdateUtils.getAssetVersionCode(res)

                        if (UpdateUtils.checkIfNeedCheck()) {
                            activity.runOnUiThread {
                                if (UpdateUtils.checkIfNeedUpdate(code) && UpdateUtils.checkIfSkipUpdate(code)) {
                                    AndroidLogger.e("需要更新")

                                    DialogUtils.popupNewVersionDialog(activity, res)
                                } else {
                                    AndroidLogger.e("无需更新")
                                }
                            }
                        } else {
                            AndroidLogger.i("跳过更新检测")
                        }
                    }
                }
            }
        }
    }

    override var name = "MainHook"
}