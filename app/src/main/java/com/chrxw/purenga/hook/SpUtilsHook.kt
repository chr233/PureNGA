package com.chrxw.purenga.hook

import com.chrxw.purenga.BuildConfig
import com.chrxw.purenga.Constant
import com.chrxw.purenga.utils.ExtensionUtils.findFirstMethodByName
import com.chrxw.purenga.utils.ExtensionUtils.log
import com.chrxw.purenga.utils.Helper
import com.github.kyuubiran.ezxhelper.AndroidLogger
import com.github.kyuubiran.ezxhelper.HookFactory.`-Static`.createHook


/**
 * 开屏广告钩子
 */
class SpUtilsHook : IHook {
    override fun init(classLoader: ClassLoader) {}

    override fun hook() {
        val pureSplashAd = Helper.getSpBool(Constant.PURE_SPLASH_AD, false)
        val killUpdateCheck = Helper.getSpBool(Constant.KILL_UPDATE_CHECK, false)

        findFirstMethodByName(MainHook.clsSPUtil, "getInt")?.createHook {
            after {
                it.log()

                when (it.args[0] as String) {
                    // 修改时间戳实现切屏无广告
                    "AD_FORGROUND_TIME", "AD_BACKGROUND_TIME" -> {
                        if (pureSplashAd) {
                            it.result = 0
                        }
                    }

                    else -> {
                        if (BuildConfig.DEBUG) {
                            AndroidLogger.w("SPUtils.getInt: ${it.args[0]} ${it.result}")
                        }
                    }
                }
            }
        }

        val killPopupDialog = Helper.getSpBool(Constant.KILL_POPUP_DIALOG, false)
        findFirstMethodByName(MainHook.clsSPUtil, "getLong")?.createHook {
            after {
                it.log()

                when (it.args[0] as String) {
                    // 屏蔽打开通知弹窗
                    "last_Guide_notification" -> {
                        if (killPopupDialog) {
                            it.result = System.currentTimeMillis()
                        }
                    }

                    else -> {
                        if (BuildConfig.DEBUG) {
                            AndroidLogger.w("SPUtils.getLong: ${it.args[0]} ${it.result}")
                        }
                    }
                }
            }
        }

    }

    override var name = "SplashHook"
}
