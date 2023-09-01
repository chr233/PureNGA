package com.chrxw.purenga.hook

import android.widget.LinearLayout
import android.widget.ScrollView
import com.chrxw.purenga.Constant
import com.chrxw.purenga.utils.Helper
import com.github.kyuubiran.ezxhelper.AndroidLogger
import com.github.kyuubiran.ezxhelper.HookFactory.`-Static`.createHook
import com.github.kyuubiran.ezxhelper.finders.MethodFinder
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
        private lateinit var clsMainActivity: Class<*>
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
        clsMainActivity = classLoader.loadClass("com.donews.nga.activitys.MainActivity")
    }

    override fun hook() {
        // 屏蔽弹窗
        if (Helper.spPlugin.getBoolean(Constant.KILL_POPUP_DIALOG, false)) {
            MethodFinder.fromClass(clsAppConfig).filterByName("isAgreedAgreement").first().createHook {
                replace {
                    AndroidLogger.i("isAgreedAgreement")
                    return@replace true
                }
            }

            MethodFinder.fromClass(clsNGAApplication).filterByName("showNotificationDialog").first().createHook {
                replace {
                    AndroidLogger.i("showNotificationDialog")
                    return@replace true
                }
            }
        }

        // 屏蔽更新检测
        if (Helper.spPlugin.getBoolean(Constant.KILL_UPDATE_CHECK, false)) {
            MethodFinder.fromClass(clsMainActivityPresenter).filterByName("checkAppUpdate").first().createHook {
                replace {
                    AndroidLogger.i("checkAppUpdate")
                }
            }

            MethodFinder.fromClass(clsCommentDialog).filterByName("showUpdate").first().createHook {
                replace {
                    AndroidLogger.i("showUpdate")
                }
            }
        }

        //移除首页滑动菜单底部无用元素
        MethodFinder.fromClass(clsHomeDrawerLayout).filterByName("initLayout").first().createHook {
            after { param ->
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
        }

        //移除商城TAB导航栏按钮以及滑动菜单项
        MethodFinder.fromClass(clsMainActivityPresenter).filterByName("initTabParams").first().createHook {
            before { param ->
                val activity = param.thisObject
                val tabParam = XposedHelpers.getObjectField(activity, "tabParams") as ArrayList<*>

                val pureStore = Helper.spPlugin.getBoolean(Constant.REMOVE_STORE_ICON, false)
                val pureActivity = Helper.spPlugin.getBoolean(Constant.REMOVE_ACTIVITY_ICON, false)

                var i = 0
                while (i < tabParam.size) {
                    val current = tabParam[i]
                    val tabId = XposedHelpers.getIntField(current, "tabId")
                    if ((tabId == 2 && pureStore) || (tabId == 4 && pureActivity)) {
                        tabParam.remove(current)
                    } else {
                        i++
                    }
                }
            }
        }
    }
}

