package com.chrxw.purenga.hook

import android.R.attr.classLoader
import android.webkit.WebView
import com.chrxw.purenga.utils.Log
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.XposedHelpers


/**
 * 广告钩子
 */
class AdHook : IHook {
    companion object {
        var clsDnFeedAd: Class<*>? = null
        var clsNativeExpressAD: Class<*>? = null
        var clsUtils_bp: Class<*>? = null
        var clsAdSize: Class<*>? = null
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
        try {
            XposedHelpers.findAndHookMethod(
                clsDnFeedAd,
                "requestServerSuccess",
                object : XC_MethodReplacement() {
                    override fun replaceHookedMethod(param: MethodHookParam?) {
                        Log.i("DnFeedAd.requestServerSuccess")
                    }
                })
        } catch (e: NoSuchMethodException) {
            Log.e("Donews 广告过滤失败")
            Log.e(e)
        }

        try {
            XposedHelpers.findAndHookMethod(
                clsNativeExpressAD,
                "a",
                clsAdSize,
                object : XC_MethodReplacement() {
                    override fun replaceHookedMethod(param: MethodHookParam?) {
                        Log.i("NativeExpressAD.a")
                        param?.result = true
                    }
                })
        } catch (e: NoSuchMethodException) {
            Log.e("qq 广告过滤失败")
            Log.e(e)
        }

        try {
            XposedHelpers.findAndHookMethod(
                clsUtils_bp,
                "runOnUiThread",
                Runnable::class.java,
                object : XC_MethodReplacement() {
                    override fun replaceHookedMethod(param: MethodHookParam?) {
                        Log.i("utils.bp.runOnUiThread")
                        param?.result = true
                    }
                })
        } catch (e: NoSuchMethodException) {
            Log.e("kwad 广告过滤失败")
            Log.e(e)
        }
    }
}
