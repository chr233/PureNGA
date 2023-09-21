package com.chrxw.purenga.hook

import com.chrxw.purenga.Constant
import com.chrxw.purenga.utils.Helper
import com.chrxw.purenga.utils.Helper.log
import com.github.kyuubiran.ezxhelper.AndroidLogger
import com.github.kyuubiran.ezxhelper.HookFactory.`-Static`.createHook
import com.github.kyuubiran.ezxhelper.finders.MethodFinder


/**
 * 广告钩子
 */
class AdHook : IHook {
    companion object {
        private lateinit var clsDnFeedAd: Class<*>
        private lateinit var clsNativeExpressAD: Class<*>
        private lateinit var clsUtils_bp: Class<*>
        private lateinit var clsAdSize: Class<*>
    }

    override fun init(classLoader: ClassLoader) {
        clsDnFeedAd = classLoader.loadClass("com.donews.admediation.adimpl.feed.DnFeedAd")
        clsNativeExpressAD = classLoader.loadClass("com.qq.e.ads.nativ.NativeExpressAD")
        clsUtils_bp = classLoader.loadClass("com.kwad.sdk.utils.bp")
        clsAdSize = classLoader.loadClass("com.qq.e.ads.nativ.ADSize")
    }

    override fun hook() {
        //屏蔽广告
        if (Helper.getSpBool(Constant.PURE_POST_AD, false)) {
            try {
                MethodFinder.fromClass(clsDnFeedAd).filterByName("requestServerSuccess").first().createHook {
                    replace {
                        it.log()
                    }
                }
            } catch (e: NoSuchMethodException) {
                AndroidLogger.e("Donews 广告过滤失败", e)
            }

            try {
                MethodFinder.fromClass(clsNativeExpressAD).filterByName("a").filterByAssignableParamTypes(clsAdSize)
                    .first().createHook {
                        replace {
                            it.log()
                            return@replace true
                        }
                    }
            } catch (e: NoSuchMethodException) {
                AndroidLogger.e("qq 广告过滤失败", e)
            }

            try {
                MethodFinder.fromClass(clsUtils_bp).filterByName("runOnUiThread")
                    .filterByAssignableParamTypes(Runnable::class.java).first().createHook {
                        replace {
                            it.log()
                            return@replace true
                        }
                    }
            } catch (e: NoSuchMethodException) {
                AndroidLogger.e("kwad 广告过滤失败", e)
            }

            MethodFinder.fromClass(MainHook.clsNGAApplication).filterByName("preThirdParty").first().createHook {
                replace {
                    it.log()
                }
            }

            MethodFinder.fromClass(MainHook.clsLoadingActivity).filterByName("loadAD").first().createHook {
                replace {
                    it.log()
                }
            }
        }
    }
}
