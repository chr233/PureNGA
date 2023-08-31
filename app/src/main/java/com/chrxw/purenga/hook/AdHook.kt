package com.chrxw.purenga.hook

import com.chrxw.purenga.Constant
import com.chrxw.purenga.utils.Helper
import com.github.kyuubiran.ezxhelper.AndroidLogger
import de.robv.android.xposed.XC_MethodReplacement
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
                XposedHelpers.findAndHookMethod(clsDnFeedAd, "requestServerSuccess", object : XC_MethodReplacement() {
                    override fun replaceHookedMethod(param: MethodHookParam?) {
                        AndroidLogger.i("DnFeedAd.requestServerSuccess")
                    }
                })
            } catch (e: NoSuchMethodException) {
                AndroidLogger.e("Donews 广告过滤失败", e)
            }

            try {
                XposedHelpers.findAndHookMethod(clsNativeExpressAD, "a", clsAdSize, object : XC_MethodReplacement() {
                    override fun replaceHookedMethod(param: MethodHookParam?) {
                        AndroidLogger.i("NativeExpressAD.a")
                        param?.result = true
                    }
                })
            } catch (e: NoSuchMethodException) {
                AndroidLogger.e("qq 广告过滤失败", e)
            }

            try {
                XposedHelpers.findAndHookMethod(clsUtils_bp,
                    "runOnUiThread",
                    Runnable::class.java,
                    object : XC_MethodReplacement() {
                        override fun replaceHookedMethod(param: MethodHookParam?) {
                            AndroidLogger.i("utils.bp.runOnUiThread")
                            param?.result = true
                        }
                    })
            } catch (e: NoSuchMethodException) {
                AndroidLogger.e("kwad 广告过滤失败", e)
            }
        }
    }
}
