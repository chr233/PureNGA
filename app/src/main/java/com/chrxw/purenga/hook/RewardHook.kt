package com.chrxw.purenga.hook

import android.app.Activity
import com.chrxw.purenga.Constant
import com.chrxw.purenga.utils.Helper
import com.github.kyuubiran.ezxhelper.AndroidLogger
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

        fun logMap(map: MutableMap<*, *>) {
            for (item in map) {
                AndroidLogger.i("${item.key} - ${item.value}")
            }
        }
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


//        MethodFinder.fromClass("rg.a", classLoader).filterByName("d")
//            .filterByAssignableParamTypes(Context::class.java, MutableMap::class.java).first().createHook {
//                before { param ->
//                    val map = param.args[1] as MutableMap<*, *>
//                    logMap(map)
//                }
//            }
//
//
//        MethodFinder.fromClass("rg.a", classLoader).filterByName("e")
//            .filterByAssignableParamTypes(Context::class.java, MutableMap::class.java).first().createHook {
//                before { param ->
//                    val map = param.args[1] as MutableMap<*, *>
//                    logMap(map)
//                }
//            }
//
//        MethodFinder.fromClass("rg.a", classLoader).filterByName("f")
//            .filterByAssignableParamTypes(Context::class.java, MutableMap::class.java).first().createHook {
//                before { param ->
//                    val map = param.args[1] as MutableMap<*, *>
//                    logMap(map)
//                }
//            }
//
//        MethodFinder.fromClass("rg.a", classLoader).filterByName("c")
//            .filterByAssignableParamTypes(Context::class.java, MutableMap::class.java).first().createHook {
//                before { param ->
//                    val map = param.args[1] as MutableMap<*, *>
//                    logMap(map)
//                }
//            }
    }

    override fun hook() {

        if (Helper.getSpBool(Constant.CRACK_AD_TASK, false)) {
            var activity: Activity? = null
//            var webView: WebView? = null

            MethodFinder.fromClass(clsLoginWebView).filterByName("initView").first().createHook {
                after { param ->
                    activity = param.thisObject as Activity?
//                    webView = XposedHelpers.getObjectField(activity, "mWebView") as WebView
                }
            }

            try {
                // Hook onRewardVerify 方法
                MethodFinder.fromClass(clsLoginWebView_b).filterByName("onAdShow").first().createHook {
                    replace { param ->
                        AndroidLogger.i(("b.onAdShow"))
                        val obj = param.thisObject

//                        val webView = XposedHelpers.getObjectField(obj, "a")
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

//                        val webView = XposedHelpers.getObjectField(obj, "a")
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
