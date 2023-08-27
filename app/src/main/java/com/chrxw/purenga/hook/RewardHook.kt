package com.chrxw.purenga.hook

import android.webkit.WebView
import com.chrxw.purenga.Constant
import com.chrxw.purenga.utils.Helper
import com.chrxw.purenga.utils.Log
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.XposedHelpers


/**
 * 浏览广告钩子
 */
class RewardHook : IHook {
    companion object {
        private lateinit var clsLoginWebView_a: Class<*>
        private lateinit var clsLoginWebView_b: Class<*>
        private lateinit var clsLoginWebView: Class<*>
        private lateinit var clsLoginWebView_P: Class<*>
    }

    override fun hookName(): String {
        return "去广告任务破解"
    }

    override fun init(classLoader: ClassLoader) {
        clsLoginWebView_a = classLoader.loadClass("gov.pianzong.androidnga.activity.user.LoginWebView\$a")
        clsLoginWebView_b = classLoader.loadClass("gov.pianzong.androidnga.activity.user.LoginWebView\$b")
        clsLoginWebView = classLoader.loadClass("gov.pianzong.androidnga.activity.user.LoginWebView")
        clsLoginWebView_P = classLoader.loadClass("gov.pianzong.androidnga.activity.user.LoginWebView\$p")
    }

    override fun hook() {
        if (Helper.spPlugin.getBoolean(Constant.CRACK_AD_TASK, false)) {
            try {
                // Hook onRewardVerify 方法
                XposedHelpers.findAndHookMethod(clsLoginWebView_b, "onAdShow", object : XC_MethodReplacement() {
                    override fun replaceHookedMethod(param: MethodHookParam?) {
                        Log.i(("b.onAdShow"))
                        val obj = param?.thisObject

                        val webView = XposedHelpers.getObjectField(obj, "a")
                        XposedHelpers.setBooleanField(webView, "mRewardVerify", true)
                        XposedHelpers.setBooleanField(webView, "mFreeRewardVerify", true)
                        XposedHelpers.callMethod(obj, "onAdClose")
                        return
                    }
                })

                // Hook onRewardVerify 方法
                XposedHelpers.findAndHookMethod(clsLoginWebView_a, "onAdShow", object : XC_MethodReplacement() {
                    override fun replaceHookedMethod(param: MethodHookParam?) {
                        Log.i(("a.onAdShow"))
                        val obj = param?.thisObject

                        val webView = XposedHelpers.getObjectField(obj, "a")
                        XposedHelpers.setBooleanField(webView, "mRewardVerify", true)
                        XposedHelpers.setBooleanField(webView, "mFreeRewardVerify", true)
                        XposedHelpers.callMethod(obj, "onAdClose")
                        return
                    }
                })

            } catch (e: Exception) {
                Log.e(e)
            }

            var webView: WebView? = null

            XposedHelpers.findAndHookMethod(clsLoginWebView, "initView", object : XC_MethodHook() {
                override fun afterHookedMethod(param: MethodHookParam) {
                    val obj = param.thisObject
                    webView = XposedHelpers.getObjectField(obj, "mWebView") as WebView
                }
            })


            XposedHelpers.findAndHookMethod(clsLoginWebView_P,
                "onProgressChanged",
                WebView::class.java,
                Int::class.javaPrimitiveType,
                object : XC_MethodHook() {
                    override fun afterHookedMethod(param: MethodHookParam) {
                        if (webView != null) {
                            val url = webView!!.url
                            Log.i("url: $url")
                        }

                    }
                })
        }
    }
}
