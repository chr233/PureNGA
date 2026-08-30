package com.chrxw.purenga.hook

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.FrameLayout
import android.widget.HorizontalScrollView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.core.view.children
import com.chrxw.purenga.BuildConfig
import com.chrxw.purenga.Constant
import com.chrxw.purenga.hook.base.IHook
import com.chrxw.purenga.ui.ClickableItemView
import com.chrxw.purenga.utils.DialogUtils
import com.chrxw.purenga.utils.ExtensionUtils.buildNormalIntent
import com.chrxw.purenga.utils.ExtensionUtils.findFirstMethodByName
import com.chrxw.purenga.utils.ExtensionUtils.log
import com.chrxw.purenga.utils.Helper
import com.github.kyuubiran.ezxhelper.AndroidLogger
import com.github.kyuubiran.ezxhelper.EzXHelper
import com.github.kyuubiran.ezxhelper.HookFactory.`-Static`.createHook
import com.github.kyuubiran.ezxhelper.finders.ConstructorFinder
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
        lateinit var clsHomeDrawerLayout: Class<*>
        private lateinit var clsCommentDialog: Class<*>
        lateinit var clsMainActivity: Class<*>
        private lateinit var clsArticleDetailActivity: Class<*>
        private lateinit var clsCalendarUtils: Class<*>
        private lateinit var clsAssetManager: Class<*>
        private lateinit var clsAboutUsActivityA: Class<*>
        lateinit var clsLoginWebView: Class<*>
        lateinit var clsAccountManageActivity: Class<*>
        lateinit var clsVipStatus: Class<*>
        lateinit var clsAppLogoActivity: Class<*>
        private lateinit var clsUserProviderImpl: Class<*>
        private lateinit var clsForumDetailActivity: Class<*>
        private lateinit var clsAllPostListFragment: Class<*>
        private lateinit var clsPostListFragment: Class<*>
        private lateinit var clsForumDetailActivity_f: Class<*>

        private fun readTextFromInputStream(inputStream: InputStream1): String {
            BufferedReader(InputStreamReader(inputStream)).use { reader ->
                return reader.readText()
            }
        }

        fun isClsCalendarUtilsInit() = ::clsCalendarUtils.isInitialized
        fun isClsCommentDialogInit() = ::clsCommentDialog.isInitialized
        fun isClsAboutUsActivityAInit() = ::clsAboutUsActivityA.isInitialized
    }

    override fun init(classLoader: ClassLoader) {
        clsMainActivityPresenter = classLoader.loadClass("com.donews.nga.activitys.presenters.MainActivityPresenter")
        clsHomeDrawerLayout = classLoader.loadClass("com.donews.nga.widget.HomeDrawerLayout")
        try {
            clsCommentDialog = classLoader.loadClass("gov.pianzong.androidnga.view.CommentDialog")
        } catch (_: Throwable) {
            AndroidLogger.e("CommentDialog 不存在")
        }
        clsMainActivity = classLoader.loadClass("com.donews.nga.activitys.MainActivity")
        clsArticleDetailActivity =
            classLoader.loadClass("gov.pianzong.androidnga.activity.forumdetail.ArticleDetailActivity")

        try {
            clsCalendarUtils = classLoader.loadClass("gov.pianzong.androidnga.utils.CalendarUtils")
        } catch (e: Throwable) {
            AndroidLogger.e(e)
        }

        clsAssetManager = classLoader.loadClass("android.content.res.AssetManager")
        try {
            clsAboutUsActivityA = classLoader.loadClass("gov.pianzong.androidnga.activity.setting.AboutUsActivity\$a")
        } catch (_: Throwable) {
            AndroidLogger.e("AboutUsActivity\$a 不存在")
        }
        clsLoginWebView = classLoader.loadClass("gov.pianzong.androidnga.activity.user.LoginWebView")
        clsAccountManageActivity = classLoader.loadClass("com.donews.nga.setting.AccountManageActivity")
        clsVipStatus = classLoader.loadClass("com.donews.nga.vip.entitys.VipStatus")
        clsUserProviderImpl = classLoader.loadClass("gov.pianzong.androidnga.providers.UserProviderImpl")
        clsAppLogoActivity = classLoader.loadClass("com.donews.nga.setting.AppLogoActivity")
        clsForumDetailActivity =
            classLoader.loadClass("gov.pianzong.androidnga.activity.forumdetail.ForumDetailActivity")
        clsAllPostListFragment =
            classLoader.loadClass("gov.pianzong.androidnga.activity.forumdetail.AllPostListFragment")
        clsPostListFragment = classLoader.loadClass("gov.pianzong.androidnga.activity.forumdetail.PostListFragment")
        clsForumDetailActivity_f =
            classLoader.loadClass("gov.pianzong.androidnga.activity.forumdetail.ForumDetailActivity\$f")
    }

    override fun hook() {
        // 屏蔽更新检测
        if (Helper.getSpBool(Constant.KILL_UPDATE_CHECK, false)) {
            findFirstMethodByName(clsMainActivityPresenter, "checkAppUpdate")?.createHook {
                replace {
                    it.log()
                }
            }

            if (isClsCommentDialogInit()) {
                findFirstMethodByName(clsCommentDialog, "showUpdate")?.createHook {
                    replace {
                        it.log()
                    }
                }
            }

            if (isClsAboutUsActivityAInit()) {
                findFirstMethodByName(clsAboutUsActivityA, "updateCallback")?.createHook {
                    replace {
                        it.log()

                        Helper.toast("PureNGA")
                    }
                }
            }
        }

        //移除首页商城入口
        val pureSlideMenu = Helper.getSpStr(Constant.PURE_SLIDE_MENU, null)?.split("|") ?: arrayListOf()
        val quickAccount = Helper.getSpBool(Constant.QUICK_ACCOUNT_MANAGE, false)

        if (pureSlideMenu.isNotEmpty() || quickAccount || BuildConfig.DEBUG) {
            findFirstMethodByName(clsHomeDrawerLayout, "initLayout")?.createHook {
                after {
                    it.log()

                    val viewBinding = XposedHelpers.getObjectField(it.thisObject, "binding")
                    val root = XposedHelpers.callMethod(viewBinding, "getRoot") as LinearLayout

                    //净化侧拉菜单
                    if (pureSlideMenu.isNotEmpty()) {
                        val scrollView = root.getChildAt(1) as ScrollView
                        val linearLayout = scrollView.getChildAt(0) as LinearLayout

                        var isFirst = true

                        val pureViews = arrayListOf<View>()

                        for (view in linearLayout.children) {
                            if (view is RelativeLayout) {
                                for (childView in view.children) {

                                    if (childView is TextView && pureSlideMenu.contains(childView.text)) {
                                        AndroidLogger.e(childView.text.toString())

                                        pureViews.add(view)
                                        break
                                    }
                                }
                            } else if (view is FrameLayout) {
                                if (isFirst && pureSlideMenu.contains("成为NGA付费会员")) {
                                    pureViews.add(view)
                                }
                            }

                            isFirst = false
                        }

                        for (view in pureViews) {
                            linearLayout.removeView(view)
                        }

                        if ((pureSlideMenu.contains("设置") && pureSlideMenu.contains("关于"))) {
                            linearLayout.addView(
                                ClickableItemView(root.context, "PureNGA 设置", "打开插件设置").apply {
                                    setBackgroundColor(Color.LTGRAY)
                                    setOnClickListener { _ ->
                                        val activity =
                                            XposedHelpers.callMethod(it.thisObject, "getActivity") as Activity
                                        DialogUtils.popupSettingDialog(activity)
                                    }
                                })
                        }
                    }

                    //长按切换账号
                    if (quickAccount) {
                        val nickNameId = Helper.getRId("tv_home_drawer_name")
                        val nickNameView = root.findViewById<TextView>(nickNameId)

                        val avatarId = Helper.getRId("civ_home_drawer_head")
                        val avatarView = root.findViewById<ImageView>(avatarId)

                        val onLongClickListener = View.OnLongClickListener { view ->
                            val intent = Intent(root.context, clsAccountManageActivity)
                            view.context.startActivity(intent)
                            true
                        }

                        nickNameView.setOnLongClickListener(onLongClickListener)
                        avatarView.setOnLongClickListener(onLongClickListener)
                    }
                }
            }
        }

        //长按打开签到
        if (Helper.getSpBool(Constant.QUICK_SIGN_IN, false)) {
            MethodFinder.fromClass(AdHook. clsHomeFragment).filterByName("initLayout").firstOrNull()?.createHook {
                after {
                    it.log()

                    val viewBinding = XposedHelpers.callMethod(it.thisObject, "getViewBinding")
                    val view = XposedHelpers.getObjectField(viewBinding, "f") as ImageView

                    view.setOnLongClickListener {
                        val activity = EzXHelper.appContext

                        val gotoIntent = activity.buildNormalIntent(clsLoginWebView)
                        gotoIntent.putExtra("sync_type", 5)

                        activity.startActivity(gotoIntent)
                        true
                    }
                }
            }
        }

        //移除导航栏游戏库图标
        if (Helper.getSpBool(Constant.REMOVE_STORE_ICON, false)) {
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

            findFirstMethodByName(clsMainActivity, "initTabs")?.createHook {
                before {
                    it.log()

                    val tabParam = it.args[0] as ArrayList<*>

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

            //9.9.45+
            findFirstMethodByName(clsMainActivity, "setupCenterMenu")?.createHook {
                replace {
                    it.log()
                }
            }
        } else if (Helper.getSpBool(Constant.REMOVE_STORE_ICON, false)) {
            findFirstMethodByName(clsMainActivity, "setupCenterMenu")?.createHook {
                before {
                    it.log()
                    it.args[1] = 2
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
            val mtdCheckLogin = AdHook.clsHomeFragment.getDeclaredMethod("checkLogin", Boolean::class.java)
            mtdCheckLogin.isAccessible = true

            var firstClick = true

            findFirstMethodByName(AdHook.clsHomeFragment, "updateSingStatus")?.createHook {
                after {
                    it.log()

                    val canChecked = it.args[0] == 0
                    val isLogin = mtdCheckLogin.invoke(it.thisObject, false) as Boolean
                    AndroidLogger.i("canCheck $canChecked isLogin $isLogin")

                    if (canChecked && isLogin && firstClick) {
                        firstClick = false
                        try {
                            Helper.toast("自动签到, 打开签到页面")
                            val mtdGetContext = AdHook.clsHomeFragment.getMethod("getContext")
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
        if (Helper.getSpBool(Constant.PURE_CALENDAR_DIALOG, false) && isClsCalendarUtilsInit()) {
            findFirstMethodByName(clsCalendarUtils, "f")?.createHook {
                replace {
                    it.log()
                    return@replace true
                }
            }
        }

        // 自定义字体
        if (Helper.getSpBool(Constant.ENABLE_CUSTOM_FONT, false) || Helper.getSpBool(Constant.POST_OPTIMIZE, false)) {
            MethodFinder.fromClass(clsAssetManager).filterByName("open").forEach { mtd ->
                mtd.createHook {
                    after {
                        it.log()

                        val fileName = it.args[0] as String

                        if (fileName == "css/style.night.css" || fileName == "css/style.css") {
                            AndroidLogger.e("AssetManager.Open css")

                            val inputStream = it.result as InputStream1
                            var css = readTextFromInputStream(inputStream)

                            if(Helper.getSpBool(Constant.ENABLE_CUSTOM_FONT, false)) {
                                val newFont = Helper.getSpStr(Constant.CUSTOM_FONT_NAME, Constant.SYSTEM_FONT)
                                val regex = Regex("font-family:[^;]+;?")
                                css = regex.replace(css, "font-family: $newFont;")
                            }

                            if(Helper.getSpBool(Constant.POST_OPTIMIZE, false)) {
                                //优化帖子内容显示
                                css += "#advertisementTop { display: none; } #advertisementBottom { display: none; }"
                            }

                            it.result = css.byteInputStream()
                        }
                    }
                }
            }
        }

        // 本地Vip
        if (Helper.getSpBool(Constant.LOCAL_VIP, false)) {
            findFirstMethodByName(clsVipStatus, "isVip")?.createHook {
                after {
                    it.log()

                    it.result = true
                }
            }

            findFirstMethodByName(clsUserProviderImpl, "isVip")?.createHook {
                after {
                    it.log()

                    it.result = true
                }
            }
        }

        // 快捷方式优化
        if (!Helper.getSpStr(Constant.SHORTCUT_SETTINGS, null)
                .isNullOrEmpty() && Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1
        ) {
            findFirstMethodByName(clsAppLogoActivity, "saveAppLogo")?.createHook {
                after {
                    it.log()

                    Helper.toast("修改图标后需要重新设置快捷方式")
                }
            }
        }

        // 优先使用“新发布”
        if (Helper.getSpBool(Constant.PREFER_NEW_POST, false)) {
            val mtdSetReplyOrderBy =
                MethodFinder.fromClass(clsAllPostListFragment).filterByName("setReplyOrderBy").first()
            val mtdAutoRefresh = MethodFinder.fromClass(clsPostListFragment).filterByName("autoRefresh").first()
            var objAllPostListFragment: Any? = null

            ConstructorFinder.fromClass(clsForumDetailActivity_f).first().createHook {
                after {
                    it.log()

                    objAllPostListFragment = it.args[1]
                }
            }

            findFirstMethodByName(clsForumDetailActivity, "initTabs")?.createHook {
                after {
                    it.log()

                    val activity = it.thisObject as Activity

                    if (objAllPostListFragment != null) {
                        Handler(Looper.getMainLooper()).postDelayed({
                            mtdSetReplyOrderBy.invoke(objAllPostListFragment, false)
                            mtdAutoRefresh.invoke(objAllPostListFragment)

                            val tabNameId = Helper.getRId2("tv_tab_name")
                            if (tabNameId != -1) {
                                val tvTabName = activity.findViewById<TextView>(tabNameId)
                                AndroidLogger.w(tvTabName.toString())
                                tvTabName.text = "新发布"
                                tvTabName.tag = "新发布"
                            }
                        }, 50)
                    }
                }
            }
        }
    }

    override var name = "OptimizeHook"
}
