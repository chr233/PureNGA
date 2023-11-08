package com.chrxw.purenga.hook

import android.R.attr
import android.app.Activity
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
import de.robv.android.xposed.XC_MethodHook
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
    }

    override fun init(classLoader: ClassLoader) {
        clsMainActivityPresenter = classLoader.loadClass("com.donews.nga.activitys.presenters.MainActivityPresenter")
        clsHomeDrawerLayout = classLoader.loadClass("com.donews.nga.widget.HomeDrawerLayout")
        clsCommentDialog = classLoader.loadClass("gov.pianzong.androidnga.view.CommentDialog")
        clsMainActivity = classLoader.loadClass("com.donews.nga.activitys.MainActivity")
        clsArticleDetailActivity =
            classLoader.loadClass("gov.pianzong.androidnga.activity.forumdetail.ArticleDetailActivity")
    }

    override fun hook() {
        // 屏蔽弹窗
        if (Helper.getSpBool(Constant.KILL_POPUP_DIALOG, false)) {
            findFirstMethodByName(MainHook.clsAppConfig, "isAgreedAgreement")?.createHook {
                replace {
                    it.log()
                    return@replace true
                }
            }

            findFirstMethodByName(MainHook.clsNGAApplication, "showNotificationDialog")?.createHook {
                replace {
                    it.log()
                    return@replace true
                }
            }
        }

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

                    //移除滑动菜单底部无用元素
                    linearLayout.removeViewAt(linearLayout.childCount - 1)

                    //移除滑动菜单商店和钱包
                    linearLayout.removeViewAt(6)
                    linearLayout.removeViewAt(5)
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
            MethodFinder.fromClass(clsMainActivity).filterByName("setColumn").first()
                .createHook {
                    before {
                        it.log()
                        it.args[0] = null
                    }
                }
        }

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
    }

    override var name = "OptimizeHook"
}

