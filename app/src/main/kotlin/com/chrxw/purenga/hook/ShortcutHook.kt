package com.chrxw.purenga.hook

import android.app.Activity
import com.chrxw.purenga.BuildConfig
import com.chrxw.purenga.Constant
import com.chrxw.purenga.hook.base.IHook
import com.chrxw.purenga.utils.DialogUtils
import com.chrxw.purenga.utils.ExtensionUtils.buildNormalIntent
import com.chrxw.purenga.utils.ExtensionUtils.findFirstMethodByName
import com.chrxw.purenga.utils.ExtensionUtils.log
import com.chrxw.purenga.utils.Helper
import com.github.kyuubiran.ezxhelper.HookFactory.`-Static`.createHook

class ShortcutHook : IHook {

    companion object {
        private lateinit var clsMessageActivity: Class<*>
        private lateinit var clsGamePlatformListActivity: Class<*>
        private lateinit var clsThemeActivity: Class<*>
        private lateinit var clsFavoriteActivity: Class<*>
        private lateinit var clsHistoryActivity: Class<*>
        private lateinit var clsDraftActivity: Class<*>
        private lateinit var clsScanningActivity: Class<*>
        private lateinit var clsDiagnoseNetworkActivity: Class<*>
    }

    override fun init(classLoader: ClassLoader) {
        clsGamePlatformListActivity = classLoader.loadClass("com.donews.nga.game.activitys.GamePlatformListActivity")
        clsThemeActivity = classLoader.loadClass("com.donews.nga.setting.ThemeActivity")
        clsMessageActivity = classLoader.loadClass("com.donews.nga.message.MessageActivity")
        clsFavoriteActivity = classLoader.loadClass("gov.pianzong.androidnga.activity.post.FavoriteActivity")
        clsHistoryActivity = classLoader.loadClass("com.donews.nga.activitys.HistoryActivity")
        clsDraftActivity = classLoader.loadClass("gov.pianzong.androidnga.activity.forumdetail.DraftActivity")
        clsScanningActivity = classLoader.loadClass("gov.pianzong.androidnga.activity.scan.ScanningActivity")
        clsDiagnoseNetworkActivity = classLoader.loadClass("com.donews.nga.setting.DiagnoseNetworkActivity")
    }

    private fun onShortcut(activity: Activity) {
        // 如果来源是Shortcut
        val intent = activity.intent
        if (intent.getBooleanExtra("fromShortcut", false)) {
            intent.putExtra("fromShortcut", false)

            val gotoName = intent.getStringExtra("gotoName")

            if (BuildConfig.DEBUG) {
                Helper.toast(gotoName.toString())
            }

            val gotoClazz = when (gotoName) {
                "sign" -> OptimizeHook.clsLoginWebView
                "home" -> null
                "account" -> OptimizeHook.clsAccountManageActivity
                "qrcode" -> clsScanningActivity
                "message" -> clsMessageActivity
                "setting" -> PreferencesHook.clsSettingActivity
                "about" -> PreferencesHook.clsAboutUsActivity
                "theme" -> clsThemeActivity
                "game" -> clsGamePlatformListActivity
                "favorite" -> clsFavoriteActivity
                "history" -> clsHistoryActivity
                "draft" -> clsDraftActivity
                "diagnose" -> clsDiagnoseNetworkActivity
                "pluginSetting" -> null
                else -> null
            }

            if (gotoClazz != null) {
                val gotoIntent = activity.buildNormalIntent(gotoClazz)

                if (gotoName == "sign") {
                    gotoIntent.putExtra("sync_type", 5)
                }

                activity.startActivity(gotoIntent)
            } else if (gotoName == "pluginSetting") {
                DialogUtils.popupSettingDialog(activity)
            }
        }
    }

    override fun hook() {
        //显示首次运行提示
        findFirstMethodByName(OptimizeHook.clsMainActivity, "initLayout")?.createHook {
            after {
                it.log()

                val activity = it.thisObject as Activity

                if (!Helper.isPluginConfigExists()) {
                    // 首次打开APP, 弹出提示框
                    DialogUtils.popupTutorialDialog(activity)
                } else if (Helper.getSpInt(Constant.LAST_SHOW, 0) != BuildConfig.VERSION_CODE) {
                    DialogUtils.popupChangeLogDialog(activity)
                }

                // 如果来源是Shortcut
                onShortcut(activity)
            }
        }

        // 处理Shortcut跳转
        if (!Helper.getSpStr(Constant.SHORTCUT_SETTINGS, null).isNullOrEmpty()) {
            findFirstMethodByName(OptimizeHook.clsMainActivity, "onNewIntent")?.createHook {
                after {
                    // 如果来源是Shortcut
                    val activity = it.thisObject as Activity
                    onShortcut(activity)
                }
            }
        }
    }

    override var name = "ShortcutHook"
}