package com.chrxw.purenga.hook

import android.app.Activity
import android.webkit.WebView
import com.chrxw.purenga.Constant
import com.chrxw.purenga.utils.Helper
import com.chrxw.purenga.utils.Log
import com.github.kyuubiran.ezxhelper.HookFactory.`-Static`.createHook
import com.github.kyuubiran.ezxhelper.finders.MethodFinder
import de.robv.android.xposed.XposedHelpers


/**
 * 浏览广告钩子
 */
class RewardHook : IHook {
    companion object {
        private lateinit var clsLoginWebView: Class<*>
        private lateinit var clsLoginWebView_a: Class<*>
        private lateinit var clsLoginWebView_b: Class<*>
        private lateinit var clsLoginWebView_P: Class<*>
        private lateinit var clsDoNewsAdNative: Class<*>
        private lateinit var clsZKAdNativeImpl: Class<*>
        private lateinit var clsAdManager_b: Class<*>
        private lateinit var clsAdManager_d: Class<*>
    }

    override fun hookName(): String {
        return "去广告任务破解"
    }

    override fun init(classLoader: ClassLoader) {
        clsLoginWebView = classLoader.loadClass("gov.pianzong.androidnga.activity.user.LoginWebView")
        clsLoginWebView_a = classLoader.loadClass("gov.pianzong.androidnga.activity.user.LoginWebView\$a")
        clsLoginWebView_b = classLoader.loadClass("gov.pianzong.androidnga.activity.user.LoginWebView\$b")
        clsLoginWebView_P = classLoader.loadClass("gov.pianzong.androidnga.activity.user.LoginWebView\$p")
        clsDoNewsAdNative = classLoader.loadClass("com.donews.b.start.DnAdNative")
        clsZKAdNativeImpl = classLoader.loadClass("com.donews.zkad.api.ZKAdNativeImpl")
        clsAdManager_b = classLoader.loadClass("com.nga.admodule.AdManager\$b")
        clsAdManager_d = classLoader.loadClass("com.nga.admodule.AdManager\$d")


    }

    override fun hook() {

        if (Helper.spPlugin.getBoolean(Constant.CRACK_AD_TASK, false)) {
            var activity: Activity? = null
            var webView: WebView? = null

            MethodFinder.fromClass(clsLoginWebView).filterByName("initView").first().createHook {
                after { param ->
                    activity = param.thisObject as Activity?
                    webView = XposedHelpers.getObjectField(activity, "mWebView") as WebView
                }
            }

            try {
                // Hook onRewardVerify 方法
                MethodFinder.fromClass(clsLoginWebView_b).filterByName("onAdShow").first().createHook {
                    replace { param ->
                        Log.i(("b.onAdShow"))
                        val obj = param?.thisObject

                        val webView = XposedHelpers.getObjectField(obj, "a")
                        XposedHelpers.setBooleanField(activity, "mRewardVerify", true)
                        XposedHelpers.setBooleanField(activity, "mFreeRewardVerify", true)
                        XposedHelpers.callMethod(obj, "onAdClose")
                    }
                }

                // Hook onRewardVerify 方法
                MethodFinder.fromClass(clsLoginWebView_b).filterByName("onAdShow").first().createHook {
                    replace { param ->
                        Log.i(("a.onAdShow"))
                        val obj = param?.thisObject

                        val webView = XposedHelpers.getObjectField(obj, "a")
                        XposedHelpers.setBooleanField(activity, "mRewardVerify", true)
                        XposedHelpers.setBooleanField(activity, "mFreeRewardVerify", true)
                        XposedHelpers.callMethod(obj, "onAdClose")
                    }
                }
            } catch (e: Exception) {
                Log.e(e)
            }

            try {
//                XposedHelpers.findAndHookMethod(clsDoNewsAdNative, "showRewardAd", object : XC_MethodReplacement() {
//                    override fun replaceHookedMethod(param: MethodHookParam?) {
//                        Log.i("clsDoNewsAdNative showRewardAd")
//                    }
//                })
//
//                XposedHelpers.findAndHookMethod(clsZKAdNativeImpl,
//                    "showRewardAd",
//                    Activity::class.java,
//                    object : XC_MethodReplacement() {
//                        override fun replaceHookedMethod(param: MethodHookParam?) {
//                            Log.i("clsZKAdNativeImpl showRewardAd")
//                        }
//                    })

            } catch (e: Throwable) {
                Log.e(e)
            }

//            XposedHelpers.findAndHookMethod(clsLoginWebView_P,
//                "onProgressChanged",
//                WebView::class.java,
//                Int::class.javaPrimitiveType,
//                object : XC_MethodHook() {
//                    override fun afterHookedMethod(param: MethodHookParam) {
//                        if (webView != null) {
//                            val url = webView!!.url
//                            Log.i("url: $url")
//                        }
//
//                    }
//                })
//
//            XposedHelpers.findAndHookMethod(clsAdManager_b, "onAdShow", object : XC_MethodReplacement() {
//                override fun replaceHookedMethod(param: MethodHookParam?) {
//                    val obj = param?.thisObject
//
//                    val callback = XposedHelpers.getObjectField(obj, "a")
//                    XposedHelpers.setBooleanField(activity, "mRewardVerify", true)
//                    XposedHelpers.setBooleanField(activity, "mFreeRewardVerify", true)
//                    XposedHelpers.callMethod(callback, "onAdClose")
//                }
//            })
//
//
//            XposedHelpers.findAndHookMethod(clsLoginWebView_a,
//                "onError",
//                Int::class.javaPrimitiveType,
//                String::class.java,
//                object : XC_MethodReplacement() {
//                    override fun replaceHookedMethod(param: MethodHookParam?) {
//                        Log.i(("a.onError"))
//                        val obj = param?.thisObject
//
//                        val webView = XposedHelpers.getObjectField(obj, "a")
//                        XposedHelpers.setBooleanField(activity, "mRewardVerify", true)
//                        XposedHelpers.setBooleanField(activity, "mFreeRewardVerify", true)
//                        XposedHelpers.callMethod(obj, "onAdClose")
//                        return
//                    }
//                })
//
//
//            XposedHelpers.findAndHookMethod(clsLoginWebView_b,
//                "onError",
//                Int::class.javaPrimitiveType,
//                String::class.java,
//                object : XC_MethodReplacement() {
//                    override fun replaceHookedMethod(param: MethodHookParam?) {
//                        Log.i(("b.onError"))
//                        val obj = param?.thisObject
//
//                        val webView = XposedHelpers.getObjectField(obj, "a")
//                        XposedHelpers.setBooleanField(activity, "mRewardVerify", true)
//                        XposedHelpers.setBooleanField(activity, "mFreeRewardVerify", true)
//                        XposedHelpers.callMethod(obj, "onAdClose")
//                        return
//                    }
//                })


        }
    }
}
