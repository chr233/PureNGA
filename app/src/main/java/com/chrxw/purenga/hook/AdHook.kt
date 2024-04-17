package com.chrxw.purenga.hook

import android.app.Activity
import com.chrxw.purenga.Constant
import com.chrxw.purenga.utils.ExtensionUtils.findFirstMethodByName
import com.chrxw.purenga.utils.ExtensionUtils.findMethodByName
import com.chrxw.purenga.utils.ExtensionUtils.log
import com.chrxw.purenga.utils.Helper
import com.github.kyuubiran.ezxhelper.AndroidLogger
import com.github.kyuubiran.ezxhelper.HookFactory.`-Static`.createHook
import com.github.kyuubiran.ezxhelper.MemberExtensions.isAbstract
import com.github.kyuubiran.ezxhelper.finders.MethodFinder
import de.robv.android.xposed.XposedHelpers


/**
 * 广告钩子
 */
class AdHook : IHook {
    companion object {
        private lateinit var clsDnFeedAd: Class<*>
        private lateinit var clsNativeExpressAD: Class<*>
        private lateinit var clsUtils_bp: Class<*>
        private lateinit var clsAdSize: Class<*>
        private lateinit var clsZkAdNativeImpl: Class<*>
        private lateinit var clsLoadingActivity_a: Class<*>

        fun isClsZkAdNativeImplInit() = ::clsZkAdNativeImpl.isInitialized
    }

    override fun init(classLoader: ClassLoader) {
        clsDnFeedAd = classLoader.loadClass("com.donews.admediation.adimpl.feed.DnFeedAd")
        clsNativeExpressAD = classLoader.loadClass("com.qq.e.ads.nativ.NativeExpressAD")
        clsAdSize = classLoader.loadClass("com.qq.e.ads.nativ.ADSize")
        clsUtils_bp = classLoader.loadClass("com.kwad.sdk.utils.bp")
        try {
            clsZkAdNativeImpl = classLoader.loadClass("com.donews.zkad.api.ZkAdNativeImpl")
        } catch (e: Throwable) {
            AndroidLogger.e(e)
        }
        clsLoadingActivity_a = classLoader.loadClass("gov.pianzong.androidnga.activity.LoadingActivity\$a")
    }

    override fun hook() {
        //屏蔽广告
        if (Helper.getSpBool(Constant.PURE_POST_AD, false)) {
            val hook1 = findFirstMethodByName(clsDnFeedAd, "requestServerSuccess")?.createHook {
                replace {
                    it.log()
                }
            }
            if (hook1 == null) {
                AndroidLogger.e("Donews 广告过滤失败")
            }

            val hook2 = findMethodByName(clsNativeExpressAD, "a").filterByAssignableParamTypes(clsAdSize).firstOrNull()
                ?.createHook {
                    replace {
                        it.log()
                        return@replace true
                    }
                }
            if (hook2 == null) {
                AndroidLogger.e("qq 广告过滤失败")
            }

            findMethodByName(clsUtils_bp, "runOnUiThread").forEach { method ->
                method.createHook {
                    replace {
                        it.log()
                        return@replace true
                    }
                }
            }

            if (isClsZkAdNativeImplInit()) {
                MethodFinder.fromClass(clsZkAdNativeImpl).forEach { mtd ->
                    val name = mtd.name
                    if (name.startsWith("load") && name.endsWith("Ad") && !mtd.isAbstract) {
                        mtd.createHook {
                            replace {
                                it.log()
                                AndroidLogger.i(mtd.name)
                            }
                        }
                    }
                }
            }

            //9.9.3 preThirdParty 改为 initThirdParty
            findFirstMethodByName(MainHook.clsNGAApplication, "preThirdParty")
                ?: findFirstMethodByName(MainHook.clsNGAApplication, "initThirdParty")?.createHook {
                    replace {
                        it.log()
                    }
                }

            findFirstMethodByName(MainHook.clsLoadingActivity, "loadAD")?.createHook {
                replace {
                    it.log()
                }
            } ?: {
                AndroidLogger.d("clsLoadingActivity loadAD 匹配失败")
            }
        }

        //屏蔽开屏广告
        if (Helper.getSpBool(Constant.PURE_SPLASH_AD, false)) {
            // 跳过开屏Logo页面
            val hook1 = findFirstMethodByName(MainHook.clsActivityLifecycle, "toForeGround")?.createHook {
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

            val hook3 = findFirstMethodByName(clsLoadingActivity_a, "callBack")?.createHook {
                replace {
                    it.log()
                }
            }

            if (hook1 == null || hook3 == null) {
                AndroidLogger.w("过滤开屏广告功能部分加载失败")
            }
        }
    }

    override var name = "AdHook"
}
