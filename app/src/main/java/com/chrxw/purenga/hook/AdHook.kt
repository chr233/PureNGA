package com.chrxw.purenga.hook

import com.chrxw.purenga.Constant
import com.chrxw.purenga.utils.ExtensionUtils.log
import com.chrxw.purenga.utils.Helper
import com.github.kyuubiran.ezxhelper.AndroidLogger
import com.github.kyuubiran.ezxhelper.HookFactory.`-Static`.createHook
import com.github.kyuubiran.ezxhelper.MemberExtensions.isAbstract
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
        private lateinit var clsZkAdNativeImpl: Class<*>
    }

    override fun init(classLoader: ClassLoader) {
        clsDnFeedAd = classLoader.loadClass("com.donews.admediation.adimpl.feed.DnFeedAd")
        clsNativeExpressAD = classLoader.loadClass("com.qq.e.ads.nativ.NativeExpressAD")
        clsAdSize = classLoader.loadClass("com.qq.e.ads.nativ.ADSize")
        clsUtils_bp = classLoader.loadClass("com.kwad.sdk.utils.bp")
        clsZkAdNativeImpl = classLoader.loadClass("com.donews.zkad.api.ZkAdNativeImpl")
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
                MethodFinder.fromClass(clsUtils_bp).filterByName("runOnUiThread").forEach { method ->
                    method.createHook {
                        replace {
                            it.log()
                            return@replace true
                        }
                    }
                }
            } catch (e: NoSuchMethodException) {
                AndroidLogger.e("kwad 广告过滤失败", e)
            }

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

    override var name = "AdHook"
}
