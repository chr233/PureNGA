package com.chrxw.purenga.hook

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import com.chrxw.purenga.utils.Helper
import com.chrxw.purenga.utils.Log
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers


/**
 * 内置浏览器钩子
 */
class WebViewHook : IHook {

    companion object {
        lateinit var clsCompanion: Class<*>
        lateinit var clsCommonWebFragmentPresenter: Class<*>
    }

    override fun hookName(): String {
        return "内置浏览器"
    }

    override fun init(classLoader: ClassLoader) {
        clsCompanion = classLoader.loadClass("com.donews.nga.activitys.WebActivity\$Companion")
        clsCommonWebFragmentPresenter =
            classLoader.loadClass("com.donews.nga.fragments.presenters.CommonWebFragmentPresenter")
    }

    override fun hook() {
//        XposedHelpers.findAndHookMethod(
//            clsCompanion,
//            "getIntent",
//            Context::class.java,
//            String::class.java,
//            String::class.java,
//            object : XC_MethodHook() {
//                override fun afterHookedMethod(param: MethodHookParam?) {
//                    Log.i("Dump Stack: ---------------start----------------")
//
//                    try {
//                        val ex = Throwable()
//                        val stackElements = ex.stackTrace
//                        if (stackElements != null) {
//                            for (i in stackElements.indices) {
//                                Log.i("Dump Stack$i: ")
//                                Log.i(
//                                    stackElements[i].className
//                                            + "----" + stackElements[i].fileName
//                                            + "----" + stackElements[i].lineNumber
//                                            + "----" + stackElements[i].methodName
//                                )
//                            }
//                        }
//                        Log.i("Dump Stack: ---------------over----------------")
//                    } catch (e: Exception) {
//                        Log.e(e)
//                    }
//
//                }
//            })

        XposedHelpers.findAndHookMethod(
            clsCommonWebFragmentPresenter,
            "initData",
            Bundle::class.java,
            object : XC_MethodHook() {
                override fun afterHookedMethod(param: MethodHookParam?) {
                    val bd = param!!.args[0] as Bundle
                    val url = bd.getString("act_url", "")
                    val intent = Intent()
                    intent.action = "android.intent.action.VIEW"
                    intent.data = Uri.parse(url)

                    try {
                        (clsCommonWebFragmentPresenter as View).context.startActivity(intent)
                    } catch (e: Exception) {
                        Log.e(e)
                        Helper.toast("未找到浏览器")
                    }
                }
            })
    }
}

