package com.chrxw.purenga.hook

import android.content.Intent
import android.os.Bundle
import com.chrxw.purenga.Constant
import com.chrxw.purenga.utils.Helper
import com.chrxw.purenga.utils.Log
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.XposedHelpers

/**
 * 浏览广告钩子
 */
class RewardHook : IHook {
    companion object {
        lateinit var clsLoginWebView_a: Class<*>
        lateinit var clsLoginWebView_b: Class<*>
    }

    override fun hookName(): String {
        return "去广告任务破解"
    }

    override fun init(classLoader: ClassLoader) {
        clsLoginWebView_a = XposedHelpers.findClass("gov.pianzong.androidnga.activity.user.LoginWebView.a", classLoader)
        clsLoginWebView_b = XposedHelpers.findClass("gov.pianzong.androidnga.activity.user.LoginWebView.b", classLoader)
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
                        XposedHelpers.setBooleanField(webView, "mFreeRewardVerify", true)
                        XposedHelpers.callMethod(obj, "onAdClose")

//                        XposedHelpers.setBooleanField(webView, "mFreeRewardVerify", true)
//                        XposedHelpers.callMethod(obj, "onAdClose")
                        return
                    }
                })

//            if (BuildConfig.DEBUG) {
//                // Hook startActivityForResult 方法
//                XposedHelpers.findAndHookMethod(
//                    Activity::class.java,
//                    "startActivityForResult",
//                    Intent::class.java,
//                    Int::class.javaPrimitiveType,
//                    Bundle::class.java,
//                    object : XC_MethodHook() {
//                        @Throws(Throwable::class)
//                        override fun beforeHookedMethod(param: MethodHookParam?) {
//                            Log.i("1")
//                            val intent = param?.args?.get(0) as Intent
//                            val code = param.args?.get(1) as Int
//                            val options = param.args?.get(2) as Bundle?
//                            newStartActivityForResult(intent, code, options)
//                            super.beforeHookedMethod(param)
//                        }
//                    })
//                XposedHelpers.findAndHookMethod(
//                    Activity::class.java,
//                    "startActivityForResult",
//                    Intent::class.java,
//                    Int::class.java,
//                    object : XC_MethodHook() {
//                        @Throws(Throwable::class)
//                        override fun beforeHookedMethod(param: MethodHookParam?) {
//                            Log.i("2")
//                            val intent = param?.args?.get(0) as Intent
//                            val code = param.args?.get(1) as Int
//                            newStartActivityForResult(intent, code)
//                            super.beforeHookedMethod(param)
//                        }
//                    })
//            }
            } catch (e: Exception) {
                Log.e(e)
            }
        }
    }

    private fun newStartActivityForResult(
        intent: Intent, requestCode: Int, options: Bundle? = null
    ) {
        Log.i(intent)
        Log.i(requestCode)
        Log.i(options)
    }

    private fun onAdClose(webView: Any) {
        val mWebView = XposedHelpers.getObjectField(webView, "mWebView")
        XposedHelpers.callMethod(
            mWebView,
            "evaluateJavascript",
            "javascript:__doAction\\(\\'domissionComplete\\',{\\'action\\':\\'app_ad_video\\'}\\)",
            null
        )
        XposedHelpers.callMethod(
            mWebView, "evaluateJavascript", "javascript:__doAction\\(\\'windowFocus\\'\\)", null
        )
    }
}
