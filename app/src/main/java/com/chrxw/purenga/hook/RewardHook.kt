package com.chrxw.purenga.hook

import android.app.Activity
import com.chrxw.purenga.Constant
import com.chrxw.purenga.utils.Helper
import com.github.kyuubiran.ezxhelper.AndroidLogger
import com.github.kyuubiran.ezxhelper.HookFactory.`-Static`.createHook
import com.github.kyuubiran.ezxhelper.finders.MethodFinder
import de.robv.android.xposed.XC_MethodHook
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


        XposedHelpers.findAndHookMethod("com.donews.nga.fragments.CommonWebFragment\$JsInterface\$createListener$1",
            classLoader,
            "clickItem",
            Int::class.javaPrimitiveType,
            String::class.java,
            object : XC_MethodHook() {
                override fun beforeHookedMethod(param: MethodHookParam) {
                    val i10 = param.args[0] as Int
                    val str4 = param.args[1] as String
                    AndroidLogger.i("clickItem: i10 $i10 str4 $str4")
                }
            })

        XposedHelpers.findAndHookMethod("com.donews.nga.fragments.CommonWebFragment\$JsInterface",
            classLoader,
            "doAction",
            Int::class.javaPrimitiveType,
            Array<String>::class.java,
            object : XC_MethodHook() {
                override fun beforeHookedMethod(param: MethodHookParam) {
                    val i10 = param.args[0] as Int
                    val str4 = param.args[1] as Array<*>
                    AndroidLogger.i("doAction: i10 $i10 str4 $str4")
                }
            })

        MethodFinder.fromClass("com.umeng.socialize.UMShareAPI", classLoader).filterByName("isInstall").first()
            .createHook {
                after { param ->
                    val activity = param.args[0] as Activity
                    val mode = param.args[1]

                    AndroidLogger.i("isInstall $activity mode $mode")

                    param.result = true
                }
            }

        //假装分享
        //内置浏览器分享
        MethodFinder.fromClass("com.donews.nga.fragments.CommonWebFragment\$JsInterface", classLoader)
            .filterByName("createListener").first().createHook {
                before {
                    AndroidLogger.i("createListener")
                }
            }

        //帖子分享
        MethodFinder.fromClass("gov.pianzong.androidnga.activity.forumdetail.ArticleDetailActivity\$x", classLoader)
            .filterByName("clickItem").first().createHook {
                before { param ->
                    val i = param.args[0] as Int
                    val s = param.args[1] as String
                    AndroidLogger.i("clickItem $i $s")
                }
            }
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
