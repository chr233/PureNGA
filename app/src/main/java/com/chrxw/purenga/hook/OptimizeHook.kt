package com.chrxw.purenga.hook

import android.R.attr.classLoader
import android.app.Activity
import android.view.View
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
        private lateinit var clsCommentDialog: Class<*>
    }

    override fun hookName(): String {
        return "界面优化"
    }

    override fun init(classLoader: ClassLoader) {
        clsAppConfig = classLoader.loadClass("com.donews.nga.common.utils.AppConfig")
        clsNGAApplication = classLoader.loadClass("gov.pianzong.androidnga.activity.NGAApplication")
        clsMainActivityPresenter = classLoader.loadClass("com.donews.nga.activitys.presenters.MainActivityPresenter")
        clsHomeDrawerLayout = classLoader.loadClass("com.donews.nga.widget.HomeDrawerLayout")
        clsCommentDialog = classLoader.loadClass("gov.pianzong.androidnga.view.CommentDialog")
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
            XposedHelpers.findAndHookMethod(
                clsMainActivityPresenter,
                "checkAppUpdate",
                Activity::class.java,
                object : XC_MethodReplacement() {
                    override fun replaceHookedMethod(param: MethodHookParam?) {
                        Log.i("checkAppUpdate")
                    }
                })

            XposedHelpers.findAndHookMethod(clsCommentDialog,
                "showUpdate",
                String::class.java,
                String::class.java,
                object : XC_MethodReplacement() {
                    override fun replaceHookedMethod(param: MethodHookParam?) {
                        Log.i("showUpdate")
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

                if (Helper.spPlugin.getBoolean(Constant.REMOVE_STORE_ICON, false)) {
                    linearLayout.removeViewAt(6)
                    linearLayout.removeViewAt(5)
                }
            }
        })

        //移除商城TAB导航栏按钮以及滑动菜单项
        XposedHelpers.findAndHookMethod(clsMainActivityPresenter, "initTabParams", object : XC_MethodHook() {
            override fun beforeHookedMethod(param: MethodHookParam) {
                val activity = param.thisObject
                val tabParam = XposedHelpers.getObjectField(activity, "tabParams") as ArrayList<*>

                val pureStore = Helper.spPlugin.getBoolean(Constant.REMOVE_STORE_ICON, false)
                val pureActivity = Helper.spPlugin.getBoolean(Constant.REMOVE_ACTIVITY_ICON, false)

                var i = 0
                while (i < tabParam.size) {
                    var current = tabParam[i]
                    val tabId = XposedHelpers.getIntField(current, "tabId")
                    if ((tabId == 2 && pureStore) || (tabId == 4 && pureActivity)) {
                        tabParam.remove(current)
                    } else {
                        i++
                    }
                }
            }
        })
    }
}

