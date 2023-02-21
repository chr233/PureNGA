package com.chrxw.purenga.hook

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.chrxw.purenga.BuildConfig
import com.chrxw.purenga.utils.Log
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.XposedHelpers

/**
 * 浏览广告钩子
 */
class RewardHook(classLoader: ClassLoader) : BaseHook(classLoader) {
    override fun startHook() {
        try {
            // 获取LoginWebView.b类实例
            val clsNamelessB = XposedHelpers.findClass(
                "gov.pianzong.androidnga.activity.user.LoginWebView.b",
                mClassLoader
            )
            // Hook onRewardVerify 方法
            XposedHelpers.findAndHookMethod(
                clsNamelessB,
                "onAdShow",
                object : XC_MethodReplacement() {
                    @Throws(Throwable::class)
                    override fun replaceHookedMethod(param: MethodHookParam?) {
                        Log.i(("b.onAdShow"))
                        val obj = param?.thisObject

                        val webView = XposedHelpers.getObjectField(obj, "a")
                        XposedHelpers.setBooleanField(webView, "mFreeRewardVerify", true)
                        XposedHelpers.callMethod(obj, "onAdClose")
                        return
                    }
                })

            // 获取LoginWebView.a类实例
            val clsNamelessA = XposedHelpers.findClass(
                "gov.pianzong.androidnga.activity.user.LoginWebView.a",
                mClassLoader
            )
            // Hook onRewardVerify 方法
            XposedHelpers.findAndHookMethod(
                clsNamelessA,
                "onAdShow",
                object : XC_MethodReplacement() {
                    @Throws(Throwable::class)
                    override fun replaceHookedMethod(param: MethodHookParam?) {
                        Log.i(("a.onAdShow"))
                        val obj = param?.thisObject

                        val webView = XposedHelpers.getObjectField(obj, "a")
                        onAdClose(webView)

//                        XposedHelpers.setBooleanField(webView, "mFreeRewardVerify", true)
//                        XposedHelpers.callMethod(obj, "onAdClose")
                        return
                    }
                })

            if (BuildConfig.DEBUG) {
                // Hook startActivityForResult 方法
                XposedHelpers.findAndHookMethod(
                    Activity::class.java,
                    "startActivityForResult",
                    Intent::class.java,
                    Int::class.javaPrimitiveType,
                    Bundle::class.java,
                    object : XC_MethodHook() {
                        @Throws(Throwable::class)
                        override fun beforeHookedMethod(param: MethodHookParam?) {
                            Log.i("1")
                            val intent = param?.args?.get(0) as Intent
                            val code = param.args?.get(1) as Int
                            val options = param.args?.get(2) as Bundle?
                            newStartActivityForResult(intent, code, options)
                            super.beforeHookedMethod(param)
                        }
                    })
                XposedHelpers.findAndHookMethod(
                    Activity::class.java,
                    "startActivityForResult",
                    Intent::class.java,
                    Int::class.java,
                    object : XC_MethodHook() {
                        @Throws(Throwable::class)
                        override fun beforeHookedMethod(param: MethodHookParam?) {
                            Log.i("2")
                            val intent = param?.args?.get(0) as Intent
                            val code = param.args?.get(1) as Int
                            newStartActivityForResult(intent, code)
                            super.beforeHookedMethod(param)
                        }
                    })
            }
        } catch (e: Exception) {
            Log.e(e)
        }
    }

    private fun newStartActivityForResult(
        intent: Intent,
        requestCode: Int,
        options: Bundle? = null
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
            mWebView,
            "evaluateJavascript",
            "javascript:__doAction\\(\\'windowFocus\\'\\)",
            null
        )
    }
}
