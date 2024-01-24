package com.chrxw.purenga.hook

import android.app.Activity
import android.content.Context
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.core.view.children
import com.chrxw.purenga.Constant
import com.chrxw.purenga.utils.ExtensionUtils.findFirstMethodByName
import com.chrxw.purenga.utils.ExtensionUtils.log
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
        private lateinit var clsMainActivityPresenter: Class<*>
        private lateinit var clsHomeDrawerLayout: Class<*>
        private lateinit var clsCommentDialog: Class<*>
        private lateinit var clsMainActivity: Class<*>
        private lateinit var clsArticleDetailActivity: Class<*>
        private lateinit var clsHomeFragment: Class<*>
        private lateinit var clsLoginWebView: Class<*>
    }

    override fun init(classLoader: ClassLoader) {
        clsMainActivityPresenter = classLoader.loadClass("com.donews.nga.activitys.presenters.MainActivityPresenter")
        clsHomeDrawerLayout = classLoader.loadClass("com.donews.nga.widget.HomeDrawerLayout")
        clsCommentDialog = classLoader.loadClass("gov.pianzong.androidnga.view.CommentDialog")
        clsMainActivity = classLoader.loadClass("com.donews.nga.activitys.MainActivity")
        clsArticleDetailActivity =
            classLoader.loadClass("gov.pianzong.androidnga.activity.forumdetail.ArticleDetailActivity")
        clsHomeFragment = classLoader.loadClass("com.donews.nga.fragments.HomeFragment")
        clsLoginWebView = classLoader.loadClass("gov.pianzong.androidnga.activity.user.LoginWebView")
    }

    override fun hook() {
        // 屏蔽更新检测
        if (Helper.getSpBool(Constant.KILL_UPDATE_CHECK, false)) {
            findFirstMethodByName(clsMainActivityPresenter, "checkAppUpdate")?.createHook {
                replace {
                    it.log()
                }
            }

            findFirstMethodByName(clsCommentDialog, "showUpdate")?.createHook {
                replace {
                    it.log()
                }
            }
        }

        //移除首页商城入口
        if (Helper.getSpBool(Constant.REMOVE_STORE_ICON, false)) {
            findFirstMethodByName(clsHomeDrawerLayout, "initLayout")?.createHook {
                after {
                    it.log()

                    val viewBinding = XposedHelpers.getObjectField(it.thisObject, "binding")
                    val root = XposedHelpers.callMethod(viewBinding, "getRoot") as LinearLayout
                    val scrollView = root.getChildAt(1) as ScrollView
                    val linearLayout = scrollView.getChildAt(0) as LinearLayout

                    val childCount = linearLayout.childCount

                    //移除滑动菜单底部无用元素
                    linearLayout.removeViewAt(childCount - 1)

                    if (childCount <= 12) {
                        //NGA <= 9.9.3

                        //移除滑动菜单商店和钱包
                        linearLayout.removeViewAt(6)
                        linearLayout.removeViewAt(5)
                    } else {
                        //NGA >= 9.9.4 新版侧边栏菜单

                        //移除滑动菜单商店和钱包
                        linearLayout.removeViewAt(10)
                        linearLayout.removeViewAt(9)
                        linearLayout.removeViewAt(4)

                        //移除会员banner
                        if (Helper.getSpBool(Constant.REMOVE_VIP_BANNER, false)) {
                            linearLayout.removeViewAt(0)
                        }
                    }
                }
            }

            findFirstMethodByName(clsMainActivityPresenter, "initTabParams")?.createHook {
                before {
                    it.log()

                    val activity = it.thisObject
                    val tabParam = XposedHelpers.getObjectField(activity, "tabParams") as ArrayList<*>

                    var i = 0
                    while (i < tabParam.size) {
                        val current = tabParam[i]
                        val tabId = XposedHelpers.getIntField(current, "tabId")
                        if ((tabId == 2)) {
                            tabParam.remove(current)
                        } else {
                            i++
                        }
                    }
                }
            }
        }

        //移除导航栏活动图标
        if (Helper.getSpBool(Constant.REMOVE_ACTIVITY_ICON, false)) {
            findFirstMethodByName(clsMainActivity, "initActivityMenu")?.createHook {
                replace {
                    it.log()
                }
            }
        }

        //移除右上角微信图标
        if (Helper.getSpBool(Constant.REMOVE_WECHAT_ICON, false)) {
            findFirstMethodByName(clsArticleDetailActivity, "initView")?.createHook {
                after {
                    it.log()

                    val activity = it.thisObject as Activity
                    val wxRid = Helper.getRId("right_second_btn")
                    val wxBtn = activity.findViewById<TextView>(wxRid)
                    val actionBar = wxBtn.parent as LinearLayout
                    actionBar.removeView(wxBtn)
                }
            }
        }

        //移除首页下方浮动文章推送
        if (Helper.getSpBool(Constant.REMOVE_POPUP_POST, false)) {
            MethodFinder.fromClass(clsMainActivity).filterByName("setColumn").first().createHook {
                before {
                    it.log()
                    it.args[0] = null
                }
            }
        }

        //自定义起始页
        val option = Helper.getSpStr(Constant.CUSTOM_INDEX, null)
        if (!option.isNullOrEmpty()) {
            findFirstMethodByName(clsMainActivity, "initTabs")?.createHook {
                after {
                    it.log()

                    val activity = it.thisObject as Activity
                    val tabId = Helper.getRId("tab_home_navigation")
                    val actionBars = activity.findViewById<HorizontalScrollView>(tabId)
                    val linearLayout = actionBars.children.first() as LinearLayout

                    for (view in linearLayout.children) {
                        if (view.contentDescription == option) {
                            view.performClick()
                        }
                    }
                }
            }
        }

        // 自动签到
        if (Helper.getSpBool(Constant.AUTO_SIGN, false)) {
            val mtdCheckLogin = clsHomeFragment.getDeclaredMethod("checkLogin", Boolean::class.java)
            mtdCheckLogin.isAccessible = true

            var firstClick = true

            findFirstMethodByName(clsHomeFragment, "updateSingStatus")?.createHook {
                after {
                    it.log()

                    val isLogin = mtdCheckLogin.invoke(it.thisObject, false) as Boolean
                    AndroidLogger.i("isLogin ${isLogin}")

                    if (it.args[0] == 0 && isLogin && firstClick) {
                        firstClick = false
                        try {
                            AndroidLogger.i("自动签到, 打开签到页面")
                            val mtdGetContext = clsHomeFragment.getMethod("getContext")
                            val context = mtdGetContext.invoke(it.thisObject)
                            val mtdShowLoginWebView = clsLoginWebView.getMethod("show", Context::class.java)
                            mtdShowLoginWebView.invoke(null, context)
                        } catch (ex: Exception) {
                            AndroidLogger.e(ex, "出错")
                            Helper.toast("自动签到失败, 可能不适配当前版本")
                            return@after
                        }
                    }
                    AndroidLogger.w("updateSingStatus")
                }
            }
        }
    }

    override var name = "OptimizeHook"
}

