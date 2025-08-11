package com.chrxw.purenga.hook

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.ShortcutInfo
import android.os.Build
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TableRow.LayoutParams
import android.widget.Toast
import androidx.core.graphics.toColorInt
import androidx.core.net.toUri
import com.chrxw.purenga.BuildConfig
import com.chrxw.purenga.Constant
import com.chrxw.purenga.hook.base.IHook
import com.chrxw.purenga.ui.ClickableItemView
import com.chrxw.purenga.ui.ToggleItemView
import com.chrxw.purenga.utils.ExtensionUtils.buildShortcut
import com.chrxw.purenga.utils.ExtensionUtils.findFirstMethodByName
import com.chrxw.purenga.utils.ExtensionUtils.setShortcuts
import com.chrxw.purenga.utils.Helper
import com.github.kyuubiran.ezxhelper.HookFactory.`-Static`.createHook
import de.robv.android.xposed.XposedHelpers


/**
 * 设置页面钩子
 */
class PreferencesHook : IHook {

    companion object {
        lateinit var clsSettingActivity: Class<*>

        /**
         * 生成设置界面
         */
        @SuppressLint("SetTextI18n")
        private fun generateView(context: Context): View {
            val root = ScrollView(context)
            val container = LinearLayout(context)
            container.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
            container.orientation = LinearLayout.VERTICAL

            // 净化设置
            container.addView(ClickableItemView(context).apply { title = "净化设置" })
            container.addView(ToggleItemView(context, Constant.PURE_SPLASH_AD).apply {
                title = "屏蔽开屏广告"
                subTitle = "冷启动会短暂黑屏, 属于正常现象"
            })
            container.addView(ToggleItemView(context, Constant.PURE_POST_AD).apply {
                title = "屏蔽信息流广告"
                subTitle = "去除Banner位, 帖子列表, 帖子末尾的广告"
            })
            container.addView(ToggleItemView(context, Constant.PURE_GAME_RECOMMEND).apply {
                title = "屏蔽游戏推荐"
                subTitle = "去除首页游戏推荐广告"
            })
            container.addView(ToggleItemView(context, Constant.PURE_POPUP_AD).apply {
                title = "屏蔽首页广告"
                subTitle = "去除首页浮窗广告"
            })
            container.addView(ToggleItemView(context, Constant.ENABLE_PURE_POST).apply {
                title = "屏蔽广告帖子"
                subTitle = "按照关键词过滤帖子列表"
            })
            container.addView(ClickableItemView(context).apply {
                title = " - 设置帖子屏蔽词"
                subTitle = "关键词之间使用 | 分隔, 关键词匹配"
                setOnClickListener {
                    if (Helper.getSpBool(Constant.ENABLE_PURE_POST, false)) {
                        val input = EditText(context).apply {
                            maxLines = 8
                            setText(Helper.getSpStr(Constant.PURE_POST, ""))
                        }

                        AlertDialog.Builder(context).apply {
                            setTitle(title)
                            setView(input)
                            setNeutralButton("内置规则", null)
                            setPositiveButton("保存") { _, _ ->
                                Helper.setSpStr(Constant.PURE_POST, input.text.toString())
                                Helper.toast("设置已保存, 重启应用生效")
                            }
                            setNegativeButton("取消", null)
                            create().apply {
                                setOnShowListener {
                                    getButton(AlertDialog.BUTTON_NEUTRAL).setOnClickListener {
                                        input.setText("饿了么|美团")
                                    }
                                }
                                show()
                            }
                        }
                    } else {
                        Helper.toast("请先打开【屏蔽广告帖子】")
                    }
                }
            })

            container.addView(ClickableItemView(context).apply {
                title = " - 设置发帖人屏蔽词"
                subTitle = "关键词之间使用 | 分隔, 全名匹配"
                setOnClickListener {
                    if (Helper.getSpBool(Constant.ENABLE_PURE_POST, false)) {
                        val input = EditText(context).apply {
                            maxLines = 8
                            setText(Helper.getSpStr(Constant.PURE_AUTHOR, ""))
                        }

                        AlertDialog.Builder(context).apply {
                            setTitle(title)
                            setView(input)
                            setNeutralButton("清空", null)
                            setPositiveButton("保存") { _, _ ->
                                Helper.setSpStr(Constant.PURE_AUTHOR, input.text.toString())
                                Helper.toast("设置已保存, 重启应用生效")
                            }
                            setNegativeButton("取消", null)
                            create().apply {
                                setOnShowListener {
                                    getButton(AlertDialog.BUTTON_NEUTRAL).setOnClickListener {
                                        input.setText(null)
                                    }
                                }
                                show()
                            }
                        }
                    } else {
                        Helper.toast("请先打开【启用自定义字体】")
                    }
                }
            })

            // 界面优化
            container.addView(ClickableItemView(context).apply { title = "界面优化" })
            container.addView(ClickableItemView(context).apply {
                title = "侧边栏净化"
                subTitle = "勾选要过滤的侧边栏菜单"
                setOnClickListener {
                    val pureSetting = Helper.getSpStr(Constant.PURE_SLIDE_MENU, null)

                    val availablePureItems = arrayOf(
                        "成为NGA付费会员",
                        "收藏",
                        "浏览历史",
                        "草稿箱",
                        "商城",
                        "订单",
                        "积分兑换",
                        "设置",
                        "深色模式",
                        "个性装扮",
                        "游戏档案",
                        "微信小游戏",
                        "钱包",
                        "分享NGA玩家社区",
                        "关于",
                        "退出登录",
                        "--旧版菜单--",
                        "商店",
                        "评分",
                        "个性换肤",
                    )

                    val enabledPureItems = pureSetting?.split("|")?.toTypedArray() ?: arrayOf()

                    val checkedItems =
                        availablePureItems.map { enabledPureItems.contains(it) }.toBooleanArray()

                    val selectedShortcuts = mutableListOf<String>()
                    for (i in availablePureItems.indices) {
                        if (checkedItems[i]) {
                            selectedShortcuts.add(availablePureItems[i])
                        }
                    }

                    AlertDialog.Builder(context).apply {
                        setTitle(subTitle)
                        setCancelable(false)
                        setMultiChoiceItems(availablePureItems, checkedItems) { _, which, isChecked ->
                            // 更新选项的选中状态
                            checkedItems[which] = isChecked

                            selectedShortcuts.clear()
                            for (i in availablePureItems.indices) {
                                if (checkedItems[i]) {
                                    selectedShortcuts.add(availablePureItems[i])
                                }
                            }

                        }
                        setNeutralButton("清空已选择", null)
                        setPositiveButton("保存") { _, _ ->
                            val save = selectedShortcuts.joinToString("|")
                            Helper.setSpStr(Constant.PURE_SLIDE_MENU, save)
                            Helper.toast("设置已保存, 重启应用生效")
                        }
                        setNegativeButton("取消", null)
                        create().apply {
                            setOnShowListener {
                                getButton(AlertDialog.BUTTON_NEUTRAL).setOnClickListener {
                                    checkedItems.fill(false)

                                    for ((i, a) in checkedItems.withIndex()) {
                                        listView.setItemChecked(i, a)
                                    }

                                    selectedShortcuts.clear()
                                }
                            }
                            show()
                        }
                    }
                }
            })
            container.addView(ToggleItemView(context, Constant.REMOVE_STORE_ICON).apply {
                title = "去除导航栏游戏库入口"
                subTitle = "净化导航栏"
            })
            container.addView(ToggleItemView(context, Constant.REMOVE_ACTIVITY_ICON).apply {
                title = "去除导航栏活动图标"
                subTitle = "净化导航栏"
            })
            container.addView(ToggleItemView(context, Constant.REMOVE_WECHAT_ICON).apply {
                title = "去除微信分享图标"
                subTitle = "移除帖子详情页右上角微信图标"
            })
            container.addView(ToggleItemView(context, Constant.REMOVE_POPUP_POST).apply {
                title = "去除首页文章推荐"
                subTitle = "移除首页导航栏上方文章推荐"
            })
            container.addView(ToggleItemView(context, Constant.QUICK_ACCOUNT_MANAGE).apply {
                title = "快捷切换账号"
                subTitle = "长按菜单上方的用户名跳转账号切换, 仅 9.9.4 以上版本有效"
            })
            container.addView(ToggleItemView(context, Constant.PREFER_NEW_POST).apply {
                title = "默认使用“新发布”"
                subTitle = "帖子列表默认使用“新发布”而不是“新回复”"
            })

            // 自定义
            container.addView(ClickableItemView(context).apply { title = "自定义" })
            container.addView(ClickableItemView(context).apply {
                title = "自定义首页"
                subTitle = "设置APP首页"
                setOnClickListener {
                    val items = arrayOf("首页", "社区", "我的")
                    val indexSetting = Helper.getSpStr(Constant.CUSTOM_INDEX, null)
                    var currentIndex = items.indexOf(indexSetting)

                    AlertDialog.Builder(context).apply {
                        setTitle(title)
                        setSingleChoiceItems(items, currentIndex) { _, which ->
                            currentIndex = which
                        }
                        setNeutralButton("清除选择", null)
                        setPositiveButton("保存") { _, _ ->
                            Helper.setSpStr(Constant.CUSTOM_INDEX, items[currentIndex])
                            Helper.toast("设置已保存, 重启应用生效")
                        }
                        setNegativeButton("取消", null)
                        create().apply {
                            setOnShowListener {
                                getButton(AlertDialog.BUTTON_NEUTRAL).setOnClickListener {
                                    listView.setItemChecked(currentIndex, false)
                                }
                            }
                            show()
                        }
                    }
                }
            })
            container.addView(ToggleItemView(context, Constant.ENABLE_CUSTOM_FONT).apply {
                title = "启用自定义字体"
                subTitle = "帖子详情页强制使用自定义字体"
            })
            container.addView(ClickableItemView(context).apply {
                title = "设置自定义字体"
                subTitle = "设置帖子详情页使用的字体"
                setOnClickListener {
                    if (Helper.getSpBool(Constant.ENABLE_CUSTOM_FONT, false)) {
                        val fontName = Helper.getSpStr(Constant.CUSTOM_FONT_NAME, Constant.SYSTEM_FONT)
                        val input = EditText(context).apply {
                            hint = "例如 system-ui, Roboto, Helvetica, Arial, sans-serif"
                            setText(fontName)
                        }

                        AlertDialog.Builder(context).apply {
                            setTitle(title)
                            setView(input)
                            setNeutralButton("使用系统字体", null)
                            setPositiveButton("保存") { _, _ ->
                                Helper.setSpStr(Constant.CUSTOM_FONT_NAME, input.text.toString())
                                Helper.toast("设置已保存, 重启应用生效")
                            }
                            setNegativeButton("取消", null)
                            create().apply {
                                setOnShowListener {
                                    getButton(AlertDialog.BUTTON_NEUTRAL).setOnClickListener {
                                        input.setText(Constant.SYSTEM_FONT)
                                        Helper.setSpStr(Constant.CUSTOM_FONT_NAME, input.text.toString())
                                    }
                                }
                                show()
                            }
                        }
                    } else {
                        Helper.toast("请先打开【启用自定义字体】")
                    }
                }
            })
            container.addView(ClickableItemView(context).apply {
                title = "自定义快捷方式"
                subTitle = "设置长按APP图标快捷方式, 仅支持安卓 7.1 及以上版本"
                setOnClickListener {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {

                        val shortcutSettings = Helper.getSpStr(Constant.SHORTCUT_SETTINGS, null)

                        // 可用快捷方式列表
                        val availableShortcuts = arrayOf(
                            context.buildShortcut("sign", "签到", "签到", null),
                            context.buildShortcut("home", "首页", "首页", null),
                            context.buildShortcut("account", "账号切换", "账号切换", null),
                            context.buildShortcut("qrcode", "扫码", "扫码", null),
                            context.buildShortcut("message", "消息", "消息", null),
                            context.buildShortcut("setting", "设置", "设置", null),
                            context.buildShortcut("about", "关于", "关于", null),
                            context.buildShortcut("theme", "个性装扮", "个性装扮", null),
                            context.buildShortcut("game", "游戏档案", "游戏档案", null),
                            context.buildShortcut("favorite", "收藏", "收藏", null),
                            context.buildShortcut("history", "浏览历史", "浏览历史", null),
                            context.buildShortcut("draft", "草稿箱", "草稿箱", null),
                            context.buildShortcut("diagnose", "网络诊断", "网络诊断", null),
                            context.buildShortcut("pluginSetting", "PureNGA设置", "PureNGA设置", null),
                        )

                        //选中的快捷方式列表
                        val enabledShortcutIds = shortcutSettings?.split(",")?.toTypedArray() ?: arrayOf()

                        val menuItems = availableShortcuts.map { it?.longLabel.toString() }.toTypedArray()
                        val checkedItems =
                            availableShortcuts.map { enabledShortcutIds.contains(it?.id) }.toBooleanArray()

                        val selectedShortcuts = mutableListOf<ShortcutInfo>()
                        for (i in menuItems.indices) {
                            if (checkedItems[i]) {
                                selectedShortcuts.add(availableShortcuts[i]!!)
                            }
                        }

                        AlertDialog.Builder(context).apply {
                            setTitle(title)
                            setCancelable(false)
                            setMultiChoiceItems(menuItems, checkedItems) { dialog, which, isChecked ->
                                // 更新选项的选中状态
                                checkedItems[which] = isChecked

                                if (isChecked && checkedItems.count { it } > 4) {
                                    checkedItems[which] = false
                                    Helper.toast("最多只能选4项")

                                    dialog.dismiss()
                                    create()
                                    show()
                                }

                                selectedShortcuts.clear()
                                for (i in menuItems.indices) {
                                    if (checkedItems[i]) {
                                        selectedShortcuts.add(availableShortcuts[i]!!)
                                    }
                                }

                            }
                            setNeutralButton("清空已选择", null)
                            setPositiveButton("保存") { _, _ ->
                                val save = selectedShortcuts.joinToString(",") { it.id }
                                Helper.setSpStr(Constant.SHORTCUT_SETTINGS, save)
                                //保存快捷菜单
                                context.setShortcuts(selectedShortcuts)
                                Helper.toast("设置已保存, 重启应用生效")
                            }
                            setNegativeButton("取消", null)
                            create().apply {
                                setOnShowListener {
                                    getButton(AlertDialog.BUTTON_NEUTRAL).setOnClickListener {
                                        checkedItems.fill(false)

                                        for ((i, a) in checkedItems.withIndex()) {
                                            listView.setItemChecked(i, a)
                                        }

                                        selectedShortcuts.clear()
                                    }
                                }
                                show()
                            }
                        }
                    } else {
                        AlertDialog.Builder(context).apply {
                            setTitle(title)
                            setMessage("当前安卓版本不支持此操作")
                            setPositiveButton("关闭", null)
                            create()
                            show()
                        }
                    }
                }
            })

            // 其他功能
            container.addView(ClickableItemView(context).apply { title = "其他功能" })
            container.addView(ToggleItemView(context, Constant.AUTO_SIGN).apply {
                title = "自动打开签到页面"
                subTitle = "没有签到时自动打开签到页面进行签到"
            })
            container.addView(ToggleItemView(context, Constant.PURE_CALENDAR_DIALOG).apply {
                title = "屏蔽日历弹窗"
                subTitle = "屏蔽签到页面的添加日历提醒弹窗, 9.9.20 之前无需开启"
            })
            container.addView(ToggleItemView(context, Constant.USE_EXTERNAL_BROWSER).apply {
                title = "使用外部浏览器打开链接"
                subTitle = "打开非NGA链接时自动调用外部系统浏览器"
            })
            container.addView(ToggleItemView(context, Constant.KILL_UPDATE_CHECK).apply {
                title = "禁止APP检查更新"
                subTitle = "尝试阻止NGA检查更新"
            })
            container.addView(ToggleItemView(context, Constant.KILL_POPUP_DIALOG).apply {
                title = "屏蔽应用内弹窗"
                subTitle = "作用不明"
            })
            container.addView(ToggleItemView(context, Constant.FAKE_SHARE).apply {
                title = "假装分享"
                subTitle = "在分享菜单增加一个“假装分享”按钮"
            })
            container.addView(ToggleItemView(context, Constant.LOCAL_VIP).apply {
                title = "本地会员"
                subTitle = "假装是付费会员(例如换肤功能有效)"
            })
            container.addView(ToggleItemView(context, Constant.BYPASS_INSTALL_CHECK).apply {
                title = "绕过已安装检查"
                subTitle = "分享到指定App前检查不检查是否已安装(调试用)"
            })

            // 插件设置
            container.addView(ClickableItemView(context).apply { title = "插件设置" })
            container.addView(ToggleItemView(context, Constant.HIDE_HOOK_INFO).apply {
                title = "静默运行"
                subTitle = "启动时不显示模块运行信息"
            })
            container.addView(ToggleItemView(context, Constant.HIDE_ERROR_INFO).apply {
                title = "静默报错信息"
                subTitle = "启动时不显示模块报错信息(如果有的话)"
            })

            // 关于
            container.addView(ClickableItemView(context).apply { title = "关于" })
            container.addView(ClickableItemView(context).apply {
                title = "手动检查更新"
                val ngaVersion = Helper.getNgaVersion()
                val sunType = if (Helper.isBundled()) "整合版" else "插件版"
                val pluginVersion = "${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE}) - $sunType"
                subTitle = "NGA版本: $ngaVersion | 插件版本: $pluginVersion"
                setOnClickListener {
                    val uri = if (Helper.isBundled()) Constant.RELEASE_BUNDLED else Constant.RELEASE_STANDALONE
                    val intent = Intent(Intent.ACTION_VIEW, uri.toUri())
                    context.startActivity(intent)
                }
            })
            container.addView(ClickableItemView(context).apply {
                title = "作者"
                subTitle = "GitHub @chr233"
                setOnClickListener {
                    val intent = Intent(Intent.ACTION_VIEW, Constant.AUTHOR_URL.toUri())
                    context.startActivity(intent)
                }
            })
            container.addView(ClickableItemView(context).apply {
                title = "捐赠"
                subTitle = "爱发电 @chr233"
                setOnClickListener {
                    val intent = Intent(Intent.ACTION_VIEW, Constant.DONATE_URL.toUri())
                    context.startActivity(intent)
                }
            })

            // 导入导出
            container.addView(ClickableItemView(context).apply { title = "导入导出" })
            container.addView(ClickableItemView(context).apply {
                title = "导出插件设置"
                subTitle = "-"
                setOnClickListener {
                    val result = Helper.exportSharedPreference(
                        context,
                        Constant.PLUGIN_PREFERENCE_NAME,
                        Constant.PLUGIN_PREFERENCE_NAME
                    )
                    val msg = buildString {
                        if (result != null) "导出成功" else "导出失败"
                        "配置文件路径: $result"
                    }
                    Helper.toast(msg, Toast.LENGTH_SHORT)
                }
            })
            container.addView(ClickableItemView(context).apply {
                title = "导入插件设置"
                subTitle = "-"
                setOnClickListener {
                    val result = Helper.importSharedPreference(
                        context,
                        Constant.PLUGIN_PREFERENCE_NAME,
                        Constant.PLUGIN_PREFERENCE_NAME
                    )
                    val msg = buildString {
                        if (result != null) "导入成功" else "导入失败"
                        "配置文件路径: $result"
                    }
                    Helper.toast(msg, Toast.LENGTH_SHORT)
                }
            })

            // 调试设置
            container.addView(ClickableItemView(context).apply { title = "调试设置" })
            container.addView(ToggleItemView(context, Constant.ENABLE_HOOK_LOG, false).apply {
                title = "启用Hook日志"
                subTitle = "在Logcat中输出详细日志"
            })
            container.addView(ToggleItemView(context, Constant.ENABLE_ACTIVITY_LOG, false).apply {
                title = "启用Activity日志"
                subTitle = "在Logcat中输出详细日志"
            })
            container.addView(ToggleItemView(context, Constant.ENABLE_POST_LOG, false).apply {
                title = "启用帖子信息日志"
                subTitle = "在Logcat中输出详细日志"
            })

            root.addView(container)
            return root
        }

        internal fun showSettingDialog(activity: Activity) {
            val view = generateView(activity)

            AlertDialog.Builder(activity).apply {
                setTitle(Constant.STR_PURENGA_SETTING)
                setCancelable(false)
                setView(view)
                setNegativeButton("仅保存") { _, _ ->
                    Helper.toast("设置已保存, 重启后生效")
                }
                setPositiveButton("保存并重启") { _, _ ->
                    Helper.toast("设置已保存, 正在重启")
                    Helper.restartApplication(activity)
                }
                setNeutralButton("重置设置", null)
                create().apply {
                    setOnShowListener {
                        getButton(AlertDialog.BUTTON_NEUTRAL).setOnClickListener {
                            AlertDialog.Builder(context).apply {
                                setTitle("警告")
                                setMessage("确定要清除插件设置吗?")
                                setCancelable(false)
                                setPositiveButton("确认") { _, _ ->
                                    if (Helper.resetPluginConfig()) {
                                        Helper.toast("插件设置已清除, 正在重启")
                                        Helper.restartApplication(activity)
                                    } else {
                                        Helper.toast("插件设置不存在")
                                    }
                                }
                                setNegativeButton("取消", null)
                                create()
                                show()
                            }
                        }
                    }
                    show()
                }
            }
        }
    }

    override fun init(classLoader: ClassLoader) {
        clsSettingActivity = classLoader.loadClass("com.donews.nga.setting.SettingActivity")
    }

    override fun hook() {
        var btnPureNGASetting: Button? = null

        findFirstMethodByName(clsSettingActivity, "initLayout")?.createHook {
            after { param ->
                val activity = param.thisObject as Activity

                val viewBinding = AdHook.fldViewBinding.get(activity)
                val root = XposedHelpers.callMethod(viewBinding, "getRoot") as LinearLayout
                val scrollView = root.getChildAt(1) as ScrollView
                val linearLayout = scrollView.getChildAt(0) as LinearLayout

                activity.runOnUiThread {
                    btnPureNGASetting = Button(activity).also { btn ->
                        btn.text = Constant.STR_PURENGA_SETTING
                        btn.setOnClickListener {
                            showSettingDialog(activity)
                        }

                        btn.setTextColor(if (Helper.isDarkModel()) "#f8fae3".toColorInt() else "#3c3b39".toColorInt())
                        btn.setBackgroundColor(0)
                        btn.setPadding(5, 5, 5, 5)
                        linearLayout.removeViewAt(linearLayout.childCount - 1)
                        linearLayout.addView(btn)
                    }
                }

                if (activity.intent.getBooleanExtra("openDialog", false)) {
                    showSettingDialog(activity)
                }
            }
        }

        findFirstMethodByName(MainHook.clsAppConfig, "setDarkModel")?.createHook {
            after {
                btnPureNGASetting?.setTextColor(if (Helper.isDarkModel()) "#f8fae3".toColorInt() else "#3c3b39".toColorInt())
            }
        }
    }

    override var name = "PreferencesHook"
}

