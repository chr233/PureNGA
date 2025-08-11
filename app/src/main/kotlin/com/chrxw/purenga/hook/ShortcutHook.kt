package com.chrxw.purenga.hook

import android.app.Activity
import android.app.AlertDialog
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TableRow.LayoutParams
import android.widget.TextView
import android.widget.Toast
import com.chrxw.purenga.BuildConfig
import com.chrxw.purenga.Constant
import com.chrxw.purenga.R
import com.chrxw.purenga.hook.base.IHook
import com.chrxw.purenga.utils.ExtensionUtils.buildNormalIntent
import com.chrxw.purenga.utils.ExtensionUtils.findFirstMethodByName
import com.chrxw.purenga.utils.ExtensionUtils.log
import com.chrxw.purenga.utils.Helper
import com.github.kyuubiran.ezxhelper.EzXHelper
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
                "about" -> AboutHook.clsAboutUsActivity
                "theme" -> clsThemeActivity
                "game" -> clsGamePlatformListActivity
                "favorite" -> clsFavoriteActivity
                "history" -> clsHistoryActivity
                "draft" -> clsDraftActivity
                "diagnose" -> clsDiagnoseNetworkActivity
                "pluginSetting" -> AboutHook.clsAboutUsActivity
                else -> null
            }

            if (gotoClazz != null) {
                val gotoIntent = activity.buildNormalIntent(gotoClazz)

                if (gotoName == "pluginSetting") {
                    gotoIntent.putExtra("openDialog", true)
                } else if (gotoName == "sign") {
                    gotoIntent.putExtra("sync_type", 5)
                }

                activity.startActivity(gotoIntent)
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
                    AlertDialog.Builder(activity).apply {
                        setTitle("PureNGA 提示")
                        setMessage("检测到插件配置文件不存在, 是否要前往插件设置?")
                        setCancelable(false)
                        setNegativeButton("取消") { _, _ ->
                            Helper.toast(
                                "可以在【设置】>【PureNGA 设置】中配置插件功能", Toast.LENGTH_LONG
                            )
                        }
                        setPositiveButton("确认") { _, _ ->
                            val intent = context.buildNormalIntent(PreferencesHook.clsSettingActivity).apply {
                                putExtra("openDialog", true)
                            }
                            context.startActivity(intent)
                        }
                        create()
                        show()
                    }
                } else if (Helper.getSpStr(Constant.LAST_SHOW, "") != BuildConfig.VERSION_NAME) {
                    val root = ScrollView(activity)
                    val linearLayout = LinearLayout(activity).apply {
                        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
                        orientation = LinearLayout.VERTICAL
                    }
                    linearLayout.addView(TextView(activity).apply {
                        text = Constant.CHANGE_LOG
                    })
                    linearLayout.addView(ImageView(activity).apply {
                        val imageId = R.drawable.aifadian
                        val drawable = EzXHelper.moduleRes.getDrawable(imageId, activity.theme)
                        setImageDrawable(drawable)
                        layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        )
                        scaleType = ImageView.ScaleType.CENTER_INSIDE
                    })

                    root.addView(linearLayout)

                    // APP更新后显示弹窗
                    AlertDialog.Builder(activity).apply {
                        setTitle("PureNGA 更新说明")
                        setView(root)
                        setCancelable(false)
                        setNegativeButton("取消") { _, _ ->
                            Helper.toast(
                                "可以在【设置】>【PureNGA 设置】中配置插件功能", Toast.LENGTH_LONG
                            )
                        }
                        setPositiveButton("确认") { _, _ ->
                            val intent = context.buildNormalIntent(PreferencesHook.clsSettingActivity).apply {
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