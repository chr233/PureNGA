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
class AdHook(classLoader: ClassLoader) : BaseHook(classLoader) {
    override fun startHook() {
        try {
            XposedHelpers.findAndHookMethod(
                "com.donews.admediation.adimpl.feed.DnFeedAd",
                mClassLoader,
                "requestServerSuccess",
                object : XC_MethodReplacement() {
                    override fun replaceHookedMethod(param: MethodHookParam?) {
                        Log.i("DnFeedAd.requestServerSuccess")
                    }
                })
        } catch (e: Exception) {
            Log.e(e)
        }

        val AdSizeCls = XposedHelpers.findClass("com.qq.e.ads.nativ.ADSize", mClassLoader)

        XposedHelpers.findAndHookMethod(
            "com.qq.e.ads.nativ.NativeExpressAD",
            mClassLoader,
            "a",
            AdSizeCls,
            object : XC_MethodReplacement() {
                override fun replaceHookedMethod(param: MethodHookParam?) {
                    Log.i("NativeExpressAD.a")
                    param?.result = true
                }
            })

        XposedHelpers.findAndHookMethod(
            "com.kwad.sdk.utils.bp",
            mClassLoader,
            "runOnUiThread",
            Runnable::class.java,
            object : XC_MethodReplacement() {
                override fun replaceHookedMethod(param: MethodHookParam?) {
                    Log.i("utils.bp.runOnUiThread")
                    param?.result = true
                }
            })
    }
}
