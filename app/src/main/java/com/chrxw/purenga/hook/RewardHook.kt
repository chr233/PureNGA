package com.chrxw.purenga.hook

import android.app.Activity
import com.chrxw.purenga.BuildConfig
import com.chrxw.purenga.Constant
import com.chrxw.purenga.utils.ExtensionUtils.log
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
        private lateinit var clsAdManager_b: Class<*>
        private lateinit var clsAdManager_d: Class<*>
    }

    override fun init(classLoader: ClassLoader) {
        clsLoginWebView = classLoader.loadClass("gov.pianzong.androidnga.activity.user.LoginWebView")
        clsLoginWebView_a = classLoader.loadClass("gov.pianzong.androidnga.activity.user.LoginWebView\$a")
        clsLoginWebView_b = classLoader.loadClass("gov.pianzong.androidnga.activity.user.LoginWebView\$b")
        clsLoginWebView_P = classLoader.loadClass("gov.pianzong.androidnga.activity.user.LoginWebView\$p")
        clsDoNewsAdNative = classLoader.loadClass("com.donews.b.start.DnAdNative")
        clsAdManager_b = classLoader.loadClass("com.nga.admodule.AdManager\$b")
        clsAdManager_d = classLoader.loadClass("com.nga.admodule.AdManager\$d")
    }

    override fun hook() {
        //任务破解
        if (Helper.getSpBool(Constant.CRACK_AD_TASK, false)) {
            var activity: Activity? = null
            var webView: Any? = null

            MethodFinder.fromClass(clsLoginWebView).filterByName("onCreate").first().createHook {
                before {
                    it.log()

                    activity = it.thisObject as Activity?
                    webView = XposedHelpers.getObjectField(activity, "mWebView")
                }
            }
            MethodFinder.fromClass(clsLoginWebView).filterByName("onDestroy").first().createHook {
                before {
                    it.log()

                    activity = null
                    webView = null

                }
            }

            if (BuildConfig.DEBUG) {
                MethodFinder.fromClass(clsAdManager_b).forEach { method ->
                    val mtdName = method.name

                    if (!method.isAbstract) {
                        if (mtdName.startsWith("on1")) {
                            method.createHook {
                                replace {
                                    it.log()

                                    val obj = it.thisObject
                                    val a = XposedHelpers.getObjectField(obj, "a")
                                    XposedHelpers.callMethod(a, mtdName)

                                    return@replace null
                                }
                            }
                        } else {
                            method.createHook {
                                before {
                                    it.log()
                                }
                            }
                        }
                    }
                }

                MethodFinder.fromClass(clsAdManager_d).forEach { method ->
                    val mtdName = method.name

                    if (!method.isAbstract) {
                        if (mtdName.startsWith("on1")) {
                            method.createHook {
                                replace {
                                    it.log()

                                    AndroidLogger.i("clsAdManager_d $mtdName")

                                    val obj = it.thisObject
                                    val a = XposedHelpers.getObjectField(obj, "d")
                                    XposedHelpers.callMethod(a, mtdName)

                                    return@replace null
                                }
                            }
                        } else {
                            method.createHook {
                                before {
                                    it.log()
                                }
                            }
                        }
                    }
                }

                MethodFinder.fromClass(clsLoginWebView).filterByName("requestAD").first().createHook {
                    replace {
                        it.log()

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
                    replace {
                        it.log()

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
                    replace {
                        it.log()

                        val obj = it.thisObject
                        XposedHelpers.setBooleanField(activity, "mRewardVerify", true)
                        XposedHelpers.setBooleanField(activity, "mFreeRewardVerify", true)
                        XposedHelpers.callMethod(obj, "onAdClose")
                    }
                }

                // Hook onRewardVerify 方法
                MethodFinder.fromClass(clsLoginWebView_b).filterByName("onAdShow").first().createHook {
                    replace {
                        it.log()

                        val obj = it.thisObject
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
