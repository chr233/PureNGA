package com.chrxw.purenga.hook

import android.app.Activity
import com.chrxw.purenga.BuildConfig
import com.chrxw.purenga.Constant
import com.chrxw.purenga.utils.Helper
import com.github.kyuubiran.ezxhelper.AndroidLogger
import com.github.kyuubiran.ezxhelper.HookFactory.`-Static`.createHook
import com.github.kyuubiran.ezxhelper.MemberExtensions.isAbstract
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
        //任务破解
        if (Helper.getSpBool(Constant.CRACK_AD_TASK, false)) {
            var activity: Activity? = null
            var webView: Any? = null

            MethodFinder.fromClass(clsLoginWebView).filterByName("onCreate").first().createHook {
                before { param ->
                    activity = param.thisObject as Activity?
                    webView = XposedHelpers.getObjectField(activity, "mWebView")
                    AndroidLogger.i("clsLoginWebView onCreate")
                }
            }
            MethodFinder.fromClass(clsLoginWebView).filterByName("onDestroy").first().createHook {
                before {
                    activity = null
                    webView = null
                    AndroidLogger.i("clsLoginWebView onDestroy")

                }
            }

            if (BuildConfig.DEBUG) {
                MethodFinder.fromClass(clsAdManager_b).forEach {
                    val mtdName = it.name

                    if (!it.isAbstract) {
                        if (mtdName.startsWith("on1")) {
                            it.createHook {
                                replace { param ->
                                    AndroidLogger.i("clsAdManager_b $mtdName")

                                    val obj = param.thisObject
                                    val a = XposedHelpers.getObjectField(obj, "a")
                                    XposedHelpers.callMethod(a, mtdName)

                                    return@replace null
                                }
                            }
                        } else {
                            it.createHook {
                                before {
                                    AndroidLogger.i("clsAdManager_b $mtdName")
                                }
                            }
                        }
                    }
                }

                MethodFinder.fromClass(clsAdManager_d).forEach {
                    val mtdName = it.name

                    if (!it.isAbstract) {
                        if (mtdName.startsWith("on1")) {
                            it.createHook {
                                replace { param ->
                                    AndroidLogger.i("clsAdManager_d $mtdName")

                                    val obj = param.thisObject
                                    val a = XposedHelpers.getObjectField(obj, "d")
                                    XposedHelpers.callMethod(a, mtdName)

                                    return@replace null
                                }
                            }
                        } else {
                            it.createHook {
                                before {
                                    AndroidLogger.i("clsAdManager_b $mtdName")
                                }
                            }
                        }
                    }
                }

                MethodFinder.fromClass(clsLoginWebView).filterByName("requestAD").first().createHook {
                    replace { param ->
                        if (webView != null) {
                            AndroidLogger.i("clsLoginWebView_a onAdShow")
                            XposedHelpers.callMethod(webView, "loadUrl", "https://baidu.com", null)
//                            LoginWebView.this.mWebView.evaluateJavascript(
//                                "javascript:__doAction('domissionComplete',{'action':'app_ad_video'})",
//                                null
//                            );
//                            LoginWebView.this.mWebView.evaluateJavascript("javascript:__doAction('windowFocus')", null);
                        }
                    }
                }

                MethodFinder.fromClass(clsLoginWebView).filterByName("requestFreeOfAD").first().createHook {
                    replace { param ->
                        if (webView != null) {
                            AndroidLogger.i("clsLoginWebView_a onAdShow")
                            XposedHelpers.callMethod(webView, "loadUrl", "https://baidu.com", null)
//                            LoginWebView.this.mWebView.evaluateJavascript(
//                                "javascript:__doAction('domissionComplete',{'action':'app_ad_video'})",
//                                null
//                            );
//                            LoginWebView.this.mWebView.evaluateJavascript("javascript:__doAction('windowFocus')", null);
                        }
                    }
                }
            }

            try {
                // Hook onRewardVerify 方法
                MethodFinder.fromClass(clsLoginWebView_b).filterByName("onAdShow").first().createHook {
                    replace { param ->
                        AndroidLogger.i(("b.onAdShow"))
                        val obj = param.thisObject
                        XposedHelpers.setBooleanField(activity, "mRewardVerify", true)
                        XposedHelpers.setBooleanField(activity, "mFreeRewardVerify", true)
                        XposedHelpers.callMethod(obj, "onAdClose")
                    }
                }

                // Hook onRewardVerify 方法
                MethodFinder.fromClass(clsLoginWebView_b).filterByName("onAdShow").first().createHook {
                    replace { param ->
                        AndroidLogger.i(("a.onAdShow"))
                        val obj = param.thisObject
                        XposedHelpers.setBooleanField(activity, "mRewardVerify", true)
                        XposedHelpers.setBooleanField(activity, "mFreeRewardVerify", true)
                        XposedHelpers.callMethod(obj, "onAdClose")
                    }
                }
            } catch (e: Exception) {
                AndroidLogger.e(e)
            }
        }
    }
}
