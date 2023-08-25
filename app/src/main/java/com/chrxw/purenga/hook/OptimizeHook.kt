package com.chrxw.purenga.hook

import android.app.Activity
import android.widget.LinearLayout
import android.widget.ScrollView
import com.chrxw.purenga.Constant
import com.chrxw.purenga.utils.Helper
import com.chrxw.purenga.utils.Log
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers


/**
 * 优化功能钩子
 */
class OptimizeHook : IHook {

    companion object {
        lateinit var clsAppConfig: Class<*>
        private lateinit var clsNGAApplication: Class<*>
        private lateinit var clsMainActivityPresenter: Class<*>
        private lateinit var clsHomeDrawerLayout: Class<*>
    }

    override fun hookName(): String {
        return "界面优化"
    }

    override fun init(classLoader: ClassLoader) {
        clsAppConfig = classLoader.loadClass("com.donews.nga.common.utils.AppConfig")
        clsNGAApplication = classLoader.loadClass("gov.pianzong.androidnga.activity.NGAApplication")
        clsMainActivityPresenter = classLoader.loadClass("com.donews.nga.activitys.presenters.MainActivityPresenter")
        clsHomeDrawerLayout = classLoader.loadClass("com.donews.nga.widget.HomeDrawerLayout")
    }

    override fun hook() {
        // 屏蔽弹窗
        if (Helper.spPlugin.getBoolean(Constant.KILL_POPUP_DIALOG, false)) {
            XposedHelpers.findAndHookMethod(clsAppConfig, "isAgreedAgreement", object : XC_MethodReplacement() {
                override fun replaceHookedMethod(param: MethodHookParam?): Any {
                    return true
                }
            })

            XposedBridge.hookAllMethods(clsNGAApplication, "showNotificationDialog", object : XC_MethodReplacement() {
                override fun replaceHookedMethod(param: MethodHookParam?) {
                    Log.i("showNotificationDialog")
                }
            })
        }

        // 屏蔽更新检测
        if (Helper.spPlugin.getBoolean(Constant.KILL_UPDATE_CHECK, false)) {
            XposedHelpers.findAndHookMethod(clsMainActivityPresenter,
                "checkAppUpdate",
                Activity::class.java,
                object : XC_MethodReplacement() {
                    override fun replaceHookedMethod(param: MethodHookParam?) {
                        Log.i("checkAppUpdate")
                    }
                })
        }

        //移除首页滑动菜单底部无用元素
        XposedHelpers.findAndHookMethod(clsHomeDrawerLayout, "initLayout", object : XC_MethodHook() {
            override fun afterHookedMethod(param: MethodHookParam) {
                val viewBinding = XposedHelpers.getObjectField(param.thisObject, "binding")
                val root = XposedHelpers.callMethod(viewBinding, "getRoot") as LinearLayout
                val scrollView = root.getChildAt(1) as ScrollView
                val linearLayout = scrollView.getChildAt(0) as LinearLayout
                linearLayout.removeViewAt(linearLayout.childCount - 1)
            }
        })
    }
}

