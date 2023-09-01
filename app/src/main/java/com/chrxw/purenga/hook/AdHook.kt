package com.chrxw.purenga.hook

import com.chrxw.purenga.Constant
import com.chrxw.purenga.utils.Helper
import com.github.kyuubiran.ezxhelper.AndroidLogger
import com.github.kyuubiran.ezxhelper.HookFactory.`-Static`.createHook
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
    }

    override fun hookName(): String {
        return "信息流广告过滤"
    }

    override fun init(classLoader: ClassLoader) {
        clsDnFeedAd = XposedHelpers.findClass("com.donews.admediation.adimpl.feed.DnFeedAd", classLoader)
        clsNativeExpressAD = XposedHelpers.findClass("com.qq.e.ads.nativ.NativeExpressAD", classLoader)
        clsUtils_bp = XposedHelpers.findClass("com.kwad.sdk.utils.bp", classLoader)
        clsAdSize = XposedHelpers.findClass("com.qq.e.ads.nativ.ADSize", classLoader)
    }

    override fun hook() {
        if (Helper.spPlugin.getBoolean(Constant.PURE_POST_AD, false)) {
            try {
                MethodFinder.fromClass(clsDnFeedAd).filterByName("requestServerSuccess").first().createHook {
                    replace {
                        AndroidLogger.i("DnFeedAd.requestServerSuccess")
                    }
                }
            } catch (e: NoSuchMethodException) {
                AndroidLogger.e("Donews 广告过滤失败", e)
            }

            try {
                MethodFinder.fromClass(clsNativeExpressAD).filterByName("a").filterByAssignableParamTypes(clsAdSize)
                    .first().createHook {
                        replace {
                            AndroidLogger.i("NativeExpressAD.a")
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
                            AndroidLogger.i("utils.bp.runOnUiThread")
                            return@replace true
                        }
                    }
            } catch (e: NoSuchMethodException) {
                AndroidLogger.e("kwad 广告过滤失败", e)
            }
        }
    }
}
