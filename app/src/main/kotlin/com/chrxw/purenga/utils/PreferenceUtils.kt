package com.chrxw.purenga.utils

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.pm.ShortcutInfo
import android.os.Build
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TableRow.LayoutParams
import android.widget.Toast
import com.chrxw.purenga.BuildConfig
import com.chrxw.purenga.Constant
import com.chrxw.purenga.ui.ClickableItemXpView
import com.chrxw.purenga.ui.DarkContainLayout
import com.chrxw.purenga.ui.ToggleItemXpView
import com.chrxw.purenga.utils.ExtensionUtils.buildShortcut
import com.chrxw.purenga.utils.ExtensionUtils.setShortcuts

object PreferenceUtils {
    fun showSettingDialog(activity: Activity) {
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

    /**
     * 生成设置界面
     */
    fun generateView(context: Context): View {
        val root = ScrollView(context)
        val container = DarkContainLayout(context, true)

        // 净化设置
        container.addView(ClickableItemXpView(context, "净化设置"))
        container.addView(
            ToggleItemXpView(context, Constant.PURE_SPLASH_AD, "屏蔽开屏广告", "冷启动会短暂黑屏, 属于正常现象")
        )
        container.addView(
            ToggleItemXpView(context, Constant.PURE_POST_AD, "屏蔽信息流广告", "去除Banner位, 帖子列表, 帖子末尾的广告")
        )
        container.addView(
            ToggleItemXpView(context, Constant.PURE_GAME_RECOMMEND, "屏蔽游戏推荐", "去除首页游戏推荐广告")
        )
        container.addView(
            ToggleItemXpView(context, Constant.PURE_POPUP_AD, "屏蔽首页广告", "去除首页浮窗广告")
        )
        container.addView(
            ToggleItemXpView(context, Constant.ENABLE_PURE_POST, "自定义屏蔽帖子", "按照关键词过滤帖子列表")
        )
        container.addView(ClickableItemXpView(context, " - 设置帖子屏蔽词", "关键词之间使用 | 分隔, 关键词匹配").apply {
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

        container.addView(ClickableItemXpView(context, " - 设置发帖人屏蔽词", "关键词之间使用 | 分隔, 全名匹配").apply {
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
        container.addView(ClickableItemXpView(context, "界面优化"))
        container.addView(ClickableItemXpView(context, "侧边栏净化", "勾选要过滤的侧边栏菜单").apply {
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

                val checkedItems = availablePureItems.map { enabledPureItems.contains(it) }.toBooleanArray()

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
        container.addView(ToggleItemXpView(context, Constant.REMOVE_STORE_ICON, "净化导航栏1", "去除导航栏游戏库入口"))
        container.addView(ToggleItemXpView(context, Constant.REMOVE_ACTIVITY_ICON, "净化导航栏2", "去除导航栏活动图标"))
        container.addView(
            ToggleItemXpView(context, Constant.REMOVE_WECHAT_ICON, "去除微信分享图标", "移除帖子详情页右上角微信图标")
        )
        container.addView(
            ToggleItemXpView(context, Constant.REMOVE_POPUP_POST, "去除首页文章推荐", "移除首页导航栏上方文章推荐")
        )
        container.addView(
            ToggleItemXpView(
                context,
                Constant.QUICK_ACCOUNT_MANAGE,
                "快捷切换账号",
                "长按菜单上方的用户名跳转账号切换, 仅 9.9.4 以上版本有效"
            )
        )
        container.addView(
            ToggleItemXpView(
                context, Constant.PREFER_NEW_POST, "默认使用“新发布”", "帖子列表默认使用“新发布”而不是“新回复”"
            )
        )

        // 自定义
        container.addView(ClickableItemXpView(context, "自定义"))
        container.addView(ClickableItemXpView(context, "自定义首页", "设置APP首页").apply {
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

        container.addView(ClickableItemXpView(context, "设置自定义字体", "设置帖子详情页使用的字体").apply {
            setOnClickListener {

                val root = LinearLayout(context).apply {
                    layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
                    orientation = LinearLayout.VERTICAL
                }

                val check = ToggleItemXpView(
                    context,
                    Constant.ENABLE_CUSTOM_FONT,
                    "启用自定义字体",
                    "帖子详情页强制使用自定义字体"
                )

                val fontName = Helper.getSpStr(Constant.CUSTOM_FONT_NAME, Constant.SYSTEM_FONT)
                val input = EditText(context).apply {
                    hint = "例如 system-ui, Roboto, Helvetica, Arial, sans-serif"
                    setText(fontName)
                }

                root.addView(check)
                root.addView(input)

                AlertDialog.Builder(context).apply {
                    setTitle(title)
                    setView(root)
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
            }
        })
        container.addView(ClickableItemXpView(context, "", "").apply {
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
                    val checkedItems = availableShortcuts.map { enabledShortcutIds.contains(it?.id) }.toBooleanArray()

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
        container.addView(ClickableItemXpView(context, "其他功能"))
        container.addView(
            ToggleItemXpView(
                context, Constant.AUTO_SIGN, "自动打开签到页面", "【建议关闭, 开启 本地VIP 可以自动签到】没有签到时自动打开签到页面进行签到"
            )
        )
        container.addView(
            ToggleItemXpView(
                context,
                Constant.PURE_CALENDAR_DIALOG,
                "屏蔽日历弹窗",
                "屏蔽签到页面的添加日历提醒弹窗, 9.9.20 之前无需开启, 9.9.50 疑似失效"
            )
        )
        container.addView(
            ToggleItemXpView(
                context,
                Constant.USE_EXTERNAL_BROWSER,
                "使用外部浏览器打开链接",
                "打开非NGA链接时自动调用外部系统浏览器"
            )
        )
        container.addView(
            ToggleItemXpView(
                context,
                Constant.KILL_UPDATE_CHECK,
                "禁止APP检查更新",
                "尝试阻止NGA检查更新, 9.9.50 疑似失效"
            )
        )
        container.addView(ToggleItemXpView(context, Constant.KILL_POPUP_DIALOG, "屏蔽应用内弹窗", "作用不明"))
        container.addView(
            ToggleItemXpView(
                context,
                Constant.FAKE_SHARE,
                "假装分享",
                "在分享菜单增加一个“假装分享”按钮"
            )
        )
        container.addView(ToggleItemXpView(context, Constant.LOCAL_VIP, "本地会员", "假装是付费会员(例如换肤功能有效)"))
        container.addView(
            ToggleItemXpView(
                context, Constant.BYPASS_INSTALL_CHECK, "绕过已安装检查", "分享到指定App前检查不检查是否已安装(调试用)"
            )
        )

        // 插件设置
        container.addView(ClickableItemXpView(context, "插件设置"))
        container.addView(
            ToggleItemXpView(context, Constant.HIDE_HOOK_INFO, "静默运行", "启动时不显示模块运行信息")
        )
        container.addView(
            ToggleItemXpView(
                context, Constant.HIDE_ERROR_INFO, "静默报错信息", "启动时不显示模块报错信息(如果有的话)"
            )
        )

        // 关于
        container.addView(ClickableItemXpView(context, "关于"))
        container.addView(ClickableItemXpView(context, "手动检查更新", "").apply {
            val ngaVersion = Helper.getNgaVersion()
            val pluginVersion = "${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})"
            subTitle = "NGA: $ngaVersion | 插件: $pluginVersion"
            setOnClickListener {
                val uri = if (Helper.isBundled()) Constant.RELEASE_BUNDLED else Constant.RELEASE_STANDALONE
                Helper.openUrl(context, uri)
            }
        })
        container.addView(ClickableItemXpView(context, "作者", "GitHub @chr233").apply {
            setOnClickListener {
                Helper.openUrl(context, Constant.AUTHOR_URL)
            }
        })
        container.addView(ClickableItemXpView(context, "捐赠", "爱发电 @chr233").apply {
            setOnClickListener {
                Helper.openUrl(context, Constant.DONATE_URL)
            }
        })

        // 导入导出
        container.addView(ClickableItemXpView(context, "导入导出"))
        container.addView(ClickableItemXpView(context, "导出插件设置", "导出插件设置").apply {
            setOnClickListener {
                val result = Helper.exportSharedPreference(
                    context, Constant.PLUGIN_PREFERENCE_NAME, Constant.PLUGIN_PREFERENCE_NAME
                )
                val msg = buildString {
                    appendLine(if (result != null) "导出成功" else "导出失败")
                    appendLine("配置文件路径: $result")
                }
                Helper.toast(msg, Toast.LENGTH_SHORT)
            }
        })
        container.addView(ClickableItemXpView(context, "导入插件设置", "导入插件设置").apply {
            setOnClickListener {
                val result = Helper.importSharedPreference(
                    context, Constant.PLUGIN_PREFERENCE_NAME, Constant.PLUGIN_PREFERENCE_NAME
                )
                val msg = buildString {
                    appendLine(if (result != null) "导入成功" else "导入失败")
                    appendLine("配置文件路径: $result")
                }
                Helper.toast(msg, Toast.LENGTH_SHORT)
            }
        })

        // 调试设置
        container.addView(ClickableItemXpView(context, "调试设置"))
        container.addView(ToggleItemXpView(context, Constant.ENABLE_HOOK_LOG, "启用Hook日志", "在Logcat中输出详细日志"))
        container.addView(
            ToggleItemXpView(
                context, Constant.ENABLE_ACTIVITY_LOG, "启用Activity日志", "在Logcat中输出详细日志"
            )
        )
        container.addView(
            ToggleItemXpView(
                context, Constant.ENABLE_POST_LOG, "启用帖子信息日志", "在Logcat中输出详细日志"
            )
        )

        root.addView(container)
        return root
    }
}