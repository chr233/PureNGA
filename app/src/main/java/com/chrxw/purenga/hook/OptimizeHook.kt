package com.chrxw.purenga.hook

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.view.View.OnLongClickListener
import android.widget.HorizontalScrollView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.core.view.children
import com.chrxw.purenga.Constant
import com.chrxw.purenga.utils.ExtensionUtils.buiildNormalIntent
import com.chrxw.purenga.utils.ExtensionUtils.findFirstMethodByName
import com.chrxw.purenga.utils.ExtensionUtils.log
import com.chrxw.purenga.utils.Helper
import com.github.kyuubiran.ezxhelper.AndroidLogger
import com.github.kyuubiran.ezxhelper.HookFactory.`-Static`.createHook
import com.github.kyuubiran.ezxhelper.finders.MethodFinder
import de.robv.android.xposed.XposedHelpers
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.InputStream as InputStream1


/**
 * 优化功能钩子
 */
class OptimizeHook : IHook {

    companion object {
        private lateinit var clsMainActivityPresenter: Class<*>
        private lateinit var clsHomeDrawerLayout: Class<*>
        private lateinit var clsCommentDialog: Class<*>
        lateinit var clsMainActivity: Class<*>
        private lateinit var clsArticleDetailActivity: Class<*>
        private lateinit var clsHomeFragment: Class<*>
        private lateinit var clsHomeFragmentPresenter: Class<*>
        lateinit var clsLoginWebView: Class<*>
        private lateinit var clsCalendarUtils: Class<*>
        private lateinit var clsAssetManager: Class<*>
        private lateinit var clsResources: Class<*>
        private lateinit var clsAboutUsActivityA: Class<*>
        private lateinit var clsAccountManageActivity: Class<*>
        private lateinit var clsMessageActivity: Class<*>

        private fun readTextFromInputStream(inputStream: InputStream1): String {
            BufferedReader(InputStreamReader(inputStream)).use { reader ->
                return reader.readText()
            }
        }

        private fun onShortcut(activity: Activity) {
            // 如果来源是Shortcut
            val intent = activity.intent
            if (intent.getBooleanExtra("fromShortcut", false)) {
                intent.putExtra("fromShortcut", false)

                val gotoName = intent.getStringExtra("gotoName")
                Helper.toast(gotoName.toString())

//                if (gotoName == "sign") {
//                    XposedHelpers.callStaticMethod(clsLoginWebView,"show")
//                    gotoIntent.putExtra("fromShortcut", true)
//                    return
//                }

                val gotoClazz = when (gotoName) {
                    "sign" -> clsLoginWebView
                    "home" -> null
                    "account" -> clsAccountManageActivity
                    "message" -> clsMessageActivity
                    "setting" -> PreferencesHook.clsSettingActivity
                    "about" -> AboutHook.clsAboutUsActivity
                    "pluginSetting" -> AboutHook.clsAboutUsActivity
                    else -> null
                }

                if (gotoClazz != null) {
                    val gotoIntent = activity.buiildNormalIntent(gotoClazz)

                    if (gotoName == "pluginSetting") {
                        gotoIntent.putExtra("openDialog", true)
                    }

                    activity.startActivity(gotoIntent)
                }
            }
        }
    }

    override fun init(classLoader: ClassLoader) {
        clsMainActivityPresenter = classLoader.loadClass("com.donews.nga.activitys.presenters.MainActivityPresenter")
        clsHomeDrawerLayout = classLoader.loadClass("com.donews.nga.widget.HomeDrawerLayout")
        clsCommentDialog = classLoader.loadClass("gov.pianzong.androidnga.view.CommentDialog")
        clsMainActivity = classLoader.loadClass("com.donews.nga.activitys.MainActivity")
        clsArticleDetailActivity =
            classLoader.loadClass("gov.pianzong.androidnga.activity.forumdetail.ArticleDetailActivity")
        clsHomeFragment = classLoader.loadClass("com.donews.nga.fragments.HomeFragment")
        clsHomeFragmentPresenter = classLoader.loadClass("com.donews.nga.fragments.presenters.HomeFragmentPresenter")
        clsLoginWebView = classLoader.loadClass("gov.pianzong.androidnga.activity.user.LoginWebView")
        clsCalendarUtils = classLoader.loadClass("gov.pianzong.androidnga.utils.CalendarUtils")
        clsAssetManager = classLoader.loadClass("android.content.res.AssetManager")
        clsResources = classLoader.loadClass("android.content.res.Resources")
        clsAboutUsActivityA = classLoader.loadClass("gov.pianzong.androidnga.activity.setting.AboutUsActivity\$a")
        clsAccountManageActivity = classLoader.loadClass("com.donews.nga.setting.AccountManageActivity")
        clsMessageActivity = classLoader.loadClass("com.donews.nga.message.MessageActivity")
    }

    override fun hook() {
        // 处理Shortcut跳转以及显示首次运行提示
        findFirstMethodByName(clsMainActivity, "initLayout")?.createHook {
            after {
                it.log()

                val activity = it.thisObject as Activity

                if (!Helper.isPluginConfigExists()) {
                    // 首次打开APP, 弹出提示框
                    AlertDialog.Builder(activity).apply {
                        setTitle("PureNGA 提示")
                        setMessage("检测到插件配置文件不存在, 是否要前往插件设置?")
                        setCancelable(false)
                        setNegativeButton("取消", null)
                        setPositiveButton("确认") { _, _ ->
                            val intent = context.buiildNormalIntent(PreferencesHook.clsSettingActivity).apply {
                                putExtra("openDialog", true)
                            }
                            context.startActivity(intent)
                        }
                        create()
                        show()
                    }
                }

                // 如果来源是Shortcut
                onShortcut(activity)
            }
        }

        findFirstMethodByName(clsMainActivity, "onNewIntent")?.createHook {
            after {
                // 如果来源是Shortcut
                val activity = it.thisObject as Activity
                onShortcut(activity)
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

            findFirstMethodByName(clsAboutUsActivityA, "updateCallback")?.createHook {
                replace {
                    it.log()

                    Helper.toast("PureNGA")
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

                        //长按切换账号
                        if (Helper.getSpBool(Constant.QUICK_ACCOUNT_MANAGE, false)) {
                            val nickNameId = Helper.getRId("tv_home_drawer_name")
                            val nickNameView = root.findViewById<TextView>(nickNameId)

                            val avatarId = Helper.getRId("civ_home_drawer_head")
                            val avatarView = root.findViewById<ImageView>(avatarId)

                            val onLongClickListener = OnLongClickListener { view ->
                                val intent = Intent(root.context, clsAccountManageActivity)
                                root.context.startActivity(intent)
                                true
                            }

                            nickNameView.setOnLongClickListener(onLongClickListener)
                            avatarView.setOnLongClickListener(onLongClickListener)
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

                    val canChecked = it.args[0] == 0
                    val isLogin = mtdCheckLogin.invoke(it.thisObject, false) as Boolean
                    AndroidLogger.i("canCheck $canChecked isLogin $isLogin")

                    if (canChecked && isLogin && firstClick) {
                        firstClick = false
                        try {
                            Helper.toast("自动签到, 打开签到页面")
                            val mtdGetContext = clsHomeFragment.getMethod("getContext")
                            val context = mtdGetContext.invoke(it.thisObject)
                            val mtdShowLoginWebView =
                                clsLoginWebView.getMethod("show", Context::class.java, Int::class.java)
                            mtdShowLoginWebView.invoke(null, context, 5)
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

        // 日历弹窗
        if (Helper.getSpBool(Constant.PURE_CALENDAR_DIALOG, false)) {
            findFirstMethodByName(clsCalendarUtils, "f")?.createHook {
                replace {
                    it.log()
                    return@replace true
                }
            }
        }

        // 自定义字体
        if (Helper.getSpBool(Constant.ENABLE_CUSTOM_FONT, false)) {
            MethodFinder.fromClass(clsAssetManager).filterByName("open").forEach { mtd ->
                mtd.createHook {
                    after {
                        it.log()

                        val fileName = it.args[0] as String

                        if (fileName == "css/style.night.css" || fileName == "css/style.css") {
                            AndroidLogger.e("AssetManager.Open css")

                            val inputStream = it.result as InputStream1
                            val css = readTextFromInputStream(inputStream)

                            val newFont = Helper.getSpStr(Constant.CUSTOM_FONT_NAME, Constant.SYSTEM_FONT)
                            val regex = Regex("font-family:[^;]+;?")
                            val newCss = regex.replace(css, "font-family: $newFont;")

                            it.result = newCss.byteInputStream()
                        }
                    }
                }
            }
        }
    }

    override var name = "OptimizeHook"
}
