package com.chrxw.purenga.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.ShortcutInfo
import android.graphics.Color
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.ScrollView
import android.widget.TableRow.LayoutParams
import android.widget.TextView
import android.widget.Toast
import com.chrxw.purenga.BuildConfig
import com.chrxw.purenga.Constant
import com.chrxw.purenga.R
import com.chrxw.purenga.ui.ClickableItemView
import com.chrxw.purenga.ui.CopyrightWarnView
import com.chrxw.purenga.ui.DarkContainLayout
import com.chrxw.purenga.ui.FitImageView
import com.chrxw.purenga.ui.ToggleItemView
import com.chrxw.purenga.utils.ExtensionUtils.buildShortcut
import com.chrxw.purenga.utils.ExtensionUtils.getStringFromMod
import com.chrxw.purenga.utils.ExtensionUtils.setShortcuts
import com.chrxw.purenga.utils.ExtensionUtils.toPixel
import com.chrxw.purenga.utils.data.Release
import java.util.Timer
import kotlin.concurrent.schedule


object DialogUtils {
    /**
     * 设置帖子自定义过滤
     */
    private fun onSetThreadFilter(activity: Activity) {
        val root = LinearLayout(activity).apply {
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
            orientation = LinearLayout.VERTICAL
        }

        root.addView(
            ToggleItemView(activity, Constant.ENABLE_PURE_POST, "开启自定义屏蔽", "按照关键词过滤帖子列表")
        )
        root.addView(
            ClickableItemView(activity, "设置标题屏蔽词", "关键词之间使用 | 分隔, 关键词匹配").apply {
                setOnClickListener {
                    onSetThreadTitleBlacklist(activity)
                }
            })
        root.addView(
            ClickableItemView(activity, "设置发帖人屏蔽词", "关键词之间使用 | 分隔, 关键词匹配").apply {
                isEnabled
                setOnClickListener {
                    onSetThreadPosterBlacklist(activity)
                }
            })

        AlertDialog.Builder(activity).apply {
            setTitle("自定义屏蔽")
            setView(root)
            setPositiveButton("关闭") { _, _ ->
                Helper.toast("设置已保存, 重启应用生效")
            }
            setNeutralButton("清除设置", null)
            create().apply {
                setOnShowListener {
                    getButton(AlertDialog.BUTTON_NEUTRAL).setOnClickListener {
                        Helper.setSpBool(Constant.ENABLE_PURE_POST, false)
                        Helper.setSpStr(Constant.PURE_POST, "")
                        Helper.setSpStr(Constant.PURE_AUTHOR, "")
                    }
                }
                show()
            }
        }
    }

    /**
     * 设置标题屏蔽词
     */
    private fun onSetThreadTitleBlacklist(activity: Activity) {
        if (Helper.getSpBool(Constant.ENABLE_PURE_POST, false)) {
            val input = EditText(activity).apply {
                maxLines = 8
                setText(Helper.getSpStr(Constant.PURE_POST, ""))
            }

            AlertDialog.Builder(activity).apply {
                setTitle("设置标题屏蔽词")
                setView(input)
                setPositiveButton("保存") { _, _ ->
                    Helper.setSpStr(Constant.PURE_POST, input.text.toString())
                    Helper.toast("设置已保存, 重启应用生效")
                }
                setNegativeButton("取消", null)
                create()
                show()
            }
        } else {
            Helper.toast("请先打开【开启自定义屏蔽】")
        }
    }

    /**
     * 设置发帖人屏蔽词
     */
    private fun onSetThreadPosterBlacklist(activity: Activity) {
        if (Helper.getSpBool(Constant.ENABLE_PURE_POST, false)) {
            val input = EditText(activity).apply {
                maxLines = 8
                setText(Helper.getSpStr(Constant.PURE_AUTHOR, ""))
            }

            AlertDialog.Builder(activity).apply {
                setTitle("设置发帖人屏蔽词")
                setView(input)
                setPositiveButton("保存") { _, _ ->
                    Helper.setSpStr(Constant.PURE_AUTHOR, input.text.toString())
                    Helper.toast("设置已保存, 重启应用生效")
                }
                setNegativeButton("取消", null)
                create()
                show()
            }
        } else {
            Helper.toast("请先打开【开启自定义屏蔽】")
        }
    }

    /**
     * 设置侧边栏净化
     */
    private fun onSetPureSlideMenu(activity: Activity) {
        val pureSetting = Helper.getSpStr(Constant.PURE_SLIDE_MENU, null)

        val availablePureItems = arrayOf(
            "签到",
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
            "个性换肤"
        )

        val enabledPureItems = pureSetting?.split("|")?.toTypedArray() ?: arrayOf()

        val checkedItems = availablePureItems.map { enabledPureItems.contains(it) }.toBooleanArray()

        val selectedShortcuts = mutableListOf<String>()
        for (i in availablePureItems.indices) {
            if (checkedItems[i]) {
                selectedShortcuts.add(availablePureItems[i])
            }
        }

        AlertDialog.Builder(activity).apply {
            setTitle("侧边栏净化")
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

    /**
     * 设置自定义首页
     */
    private fun onSetCustomHome(activity: Activity) {
        val items = arrayOf("首页", "社区", "游戏库", "我的")
        val indexSetting = Helper.getSpStr(Constant.CUSTOM_INDEX, null)
        var currentIndex = items.indexOf(indexSetting)

        AlertDialog.Builder(activity).apply {
            setTitle("自定义首页")
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

    /**
     * 设置自定义字体
     */
    private fun onSetCustomFont(activity: Activity) {
        val root = LinearLayout(activity).apply {
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
            orientation = LinearLayout.VERTICAL
        }

        val check = ToggleItemView(
            activity, Constant.ENABLE_CUSTOM_FONT, "启用自定义字体", "帖子详情页强制使用自定义字体"
        )

        val fontName = Helper.getSpStr(Constant.CUSTOM_FONT_NAME, Constant.SYSTEM_FONT)
        val input = EditText(activity).apply {
            hint = "例如 system-ui, Roboto, Helvetica, Arial, sans-serif"
            setText(fontName)
        }

        root.addView(check)
        root.addView(input)

        AlertDialog.Builder(activity).apply {
            setTitle("自定义字体")
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

    fun getShortcutList(context: Context): List<ShortcutInfo?> {
        return listOf(
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
            context.buildShortcut("pluginSetting", Constant.STR_PURENGA_SETTING, Constant.STR_PURENGA_SETTING, null),
        )
    }

    /**
     *  设置自定义快捷方式
     */
    private fun onSetCustomShortCut(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            val shortcutSettings = Helper.getSpStr(Constant.SHORTCUT_SETTINGS, null)

            // 可用快捷方式列表
            val availableShortcuts = getShortcutList(activity)

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

            AlertDialog.Builder(activity).apply {
                setTitle("自定义快捷方式")
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
            Helper.toast("当前安卓版本不支持此操作")
        }
    }

    /**
     * 导出插件设置
     */
    private fun onDumpPluginSetting(activity: Activity) {
        val result = Helper.exportSharedPreference(
            activity, Constant.PLUGIN_PREFERENCE_NAME, Constant.PLUGIN_PREFERENCE_NAME
        )
        val msg = buildString {
            append(if (result != null) "导出成功" else "导出失败")
            append(", 配置文件路径: $result")
        }
        Helper.toast(msg, Toast.LENGTH_SHORT)
    }

    /**
     * 导入插件设置
     */
    private fun onImportPluginSetting(activity: Activity) {
        val result = Helper.importSharedPreference(
            activity, Constant.PLUGIN_PREFERENCE_NAME, Constant.PLUGIN_PREFERENCE_NAME
        )
        val msg = buildString {
            append(if (result != null) "导入成功" else "导入失败")
            append(",配置文件路径: $result")
        }
        Helper.toast(msg, Toast.LENGTH_SHORT)
    }

    /**
     * 弹出设置对话框
     */
    fun popupSettingDialog(activity: Activity) {
        val root = ScrollView(activity)
        val container = DarkContainLayout(activity, true)

        container.addView(CopyrightWarnView(activity))

        // 净化设置
        container.addView(ClickableItemView(activity, "净化设置"))
        container.addView(
            ToggleItemView(activity, Constant.PURE_SPLASH_AD, "屏蔽开屏广告", "冷启动会短暂黑屏, 属于正常现象")
        )
        container.addView(
            ToggleItemView(
                activity, Constant.PURE_POST_AD, "屏蔽信息流广告", "去除Banner位, 帖子列表, 帖子末尾的广告"
            )
        )
        container.addView(
            ToggleItemView(activity, Constant.PURE_GAME_RECOMMEND, "屏蔽游戏推荐", "去除首页游戏推荐广告")
        )
        container.addView(
            ToggleItemView(activity, Constant.PURE_POPUP_AD, "屏蔽首页广告", "去除首页浮窗广告")
        )
        container.addView(
            ClickableItemView(activity, "自定义屏蔽帖子", "按照关键词过滤帖子列表").apply {
                setOnClickListener {
                    onSetThreadFilter(activity)
                }
            })

        // 界面优化
        container.addView(ClickableItemView(activity, "界面优化"))
        container.addView(
            ClickableItemView(activity, "侧边栏净化", "勾选要过滤的侧边栏菜单").apply {
                setOnClickListener {
                    onSetPureSlideMenu(activity)
                }
            })
        container.addView(
            ToggleItemView(activity, Constant.REMOVE_STORE_ICON, "净化导航栏1", "去除导航栏游戏库入口")
        )
        container.addView(
            ToggleItemView(activity, Constant.REMOVE_ACTIVITY_ICON, "净化导航栏2", "去除导航栏活动图标")
        )
        container.addView(
            ToggleItemView(activity, Constant.REMOVE_WECHAT_ICON, "去除微信分享图标", "移除帖子详情页右上角微信图标")
        )
        container.addView(
            ToggleItemView(activity, Constant.REMOVE_POPUP_POST, "去除首页文章推荐", "移除首页导航栏上方文章推荐")
        )
        container.addView(
            ToggleItemView(
                activity,
                Constant.QUICK_ACCOUNT_MANAGE,
                "快捷切换账号",
                "长按菜单上方的用户名跳转账号切换, 仅 9.9.4 以上版本有效"
            )
        )
        container.addView(
            ToggleItemView(
                activity, Constant.QUICK_SIGN_IN, "长按搜索进入签到", "长按右上角搜索图标进入签到页面, 不推荐使用"
            )
        )
        container.addView(
            ToggleItemView(activity, Constant.POST_OPTIMIZE, "帖子详情优化", "移除帖子详情页的空白条")
        )

        // 自定义
        container.addView(ClickableItemView(activity, "自定义"))
        container.addView(
            ClickableItemView(activity, "自定义首页", "设置APP首页").apply {
                setOnClickListener {
                    onSetCustomHome(activity)
                }
            })
        container.addView(
            ClickableItemView(activity, "自定义字体", "设置帖子详情页使用的字体").apply {
                setOnClickListener {
                    onSetCustomFont(activity)
                }
            })
        container.addView(
            ClickableItemView(
                activity, "自定义快捷方式", "设置长按APP图标快捷方式, 仅支持安卓 7.1 及以上版本"
            ).apply {
                setOnClickListener {
                    onSetCustomShortCut(activity)
                }
            })

        // 其他功能
        container.addView(ClickableItemView(activity, "其他功能"))
        container.addView(
            ToggleItemView(
                activity, Constant.PREFER_NEW_POST, "默认使用“新发布”", "帖子列表默认使用“新发布”而不是“新回复”"
            )
        )
        container.addView(
            ToggleItemView(
                activity, Constant.AUTO_SIGN, "自动打开签到页面", "【不推荐, 开启 本地VIP 可以自动签到】"
            )
        )
        container.addView(
            ToggleItemView(
                activity,
                Constant.PURE_CALENDAR_DIALOG,
                "屏蔽日历弹窗",
                "屏蔽签到页面的添加日历提醒弹窗, 9.9.20 之前无需开启, 9.9.50 后无效"
            )
        )
        container.addView(
            ToggleItemView(
                activity,
                Constant.USE_EXTERNAL_BROWSER,
                "使用外部浏览器打开链接",
                "打开非NGA链接时自动调用外部系统浏览器"
            )
        )
        container.addView(
            ToggleItemView(
                activity, Constant.KILL_UPDATE_CHECK, "禁止APP检查更新", "尝试阻止NGA检查更新, 9.9.50 后无效"
            )
        )
        container.addView(ToggleItemView(activity, Constant.KILL_POPUP_DIALOG, "屏蔽应用内弹窗", "作用不明"))
        container.addView(
            ToggleItemView(
                activity, Constant.FAKE_SHARE, "假装分享", "在分享菜单增加一个“假装分享”按钮"
            )
        )
        container.addView(
            ToggleItemView(
                activity, Constant.LOCAL_VIP, "本地会员", "假装是付费会员(例如换肤功能有效)"
            )
        )

        // 插件设置
        container.addView(ClickableItemView(activity, "插件设置"))
        container.addView(
            ToggleItemView(activity, Constant.HIDE_HOOK_INFO, "静默运行", "启动时不显示模块运行信息")
        )
        container.addView(
            ToggleItemView(
                activity, Constant.HIDE_ERROR_INFO, "静默报错信息", "启动时不显示模块报错信息(如果有的话)"
            )
        )

        if (!Helper.hasSpKey(Constant.CHECK_PLUGIN_UPDATE)) {
            Helper.setSpBool(Constant.CHECK_PLUGIN_UPDATE, true)
        }

        // 插件更新
        container.addView(ClickableItemView(activity, "插件更新"))
        container.addView(
            ToggleItemView(
                activity, Constant.CHECK_PLUGIN_UPDATE, "定期检查插件更新", "3天检查一次更新, 如果有更新会显示通知"
            )
        )
        container.addView(
            ClickableItemView(activity, "立即检查更新", "").apply {
                val ngaVersion = Helper.getNgaVersion()
                val pluginVersion = Helper.PLUGIN_VERSION
                subTitle = "NGA: $ngaVersion | 插件: $pluginVersion"
                setOnClickListener {
                    popupCheckUpdate(activity)
                }
            })
        container.addView(
            ClickableItemView(activity, "当前版本信息", "").apply {
                val sunType = if (Helper.isBundled()) "整合版" else "插件版"
                subTitle = "插件类型: $sunType"
                setOnClickListener {
                    popupChangeLogDialog(activity)
                }
            })
        container.addView(
            ClickableItemView(activity, "获取最新发行版", "").apply {
                val sunType = if (Helper.isBundled()) "整合版" else "插件版"
                subTitle = "插件类型: $sunType"
                setOnClickListener {
                    popupGotoReleasePage(activity)
                }
            })

        // 关于
        container.addView(ClickableItemView(activity, "关于"))
        container.addView(
            ClickableItemView(activity, "作者", "GitHub @chr233").apply {
                setOnClickListener {
                    Helper.openUrl(context, Constant.AUTHOR_URL)
                }
            })
        container.addView(
            ClickableItemView(activity, "捐赠", "爱发电 @chr233").apply {
                setOnClickListener {
                    popupDonateDialog(activity)
                }
            })

        // 导入导出
        container.addView(ClickableItemView(activity, "导入导出"))
        container.addView(
            ClickableItemView(activity, "导出插件设置", "导出插件设置").apply {
                setOnClickListener {
                    onDumpPluginSetting(activity)
                }
            })
        container.addView(
            ClickableItemView(activity, "导入插件设置", "导入插件设置").apply {
                setOnClickListener {
                    onImportPluginSetting(activity)
                }
            })

        // 调试设置
        container.addView(ClickableItemView(activity, "调试设置"))
        container.addView(
            ToggleItemView(
                activity, Constant.ENABLE_HOOK_LOG, "启用Hook日志", "在Logcat中输出详细日志"
            )
        )
        container.addView(
            ToggleItemView(activity, Constant.ENABLE_ACTIVITY_LOG, "启用Activity日志", "在Logcat中输出详细日志")
        )
        container.addView(
            ToggleItemView(activity, Constant.ENABLE_POST_LOG, "启用帖子信息日志", "在Logcat中输出详细日志")
        )

        root.addView(container)

        AlertDialog.Builder(activity).apply {
            setTitle(Constant.STR_PURENGA_SETTING)
            setCancelable(false)

            setView(root)
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
     * 弹出用户协议对话框
     */
    fun popupEulaDialog(activity: Activity) {
        AlertDialog.Builder(activity).apply {
            setTitle("PureNGA 用户协议")
            setMessage(R.string.eula_content.getStringFromMod().replace("|", "\n\n"))
            setCancelable(false)
            if (Helper.isXposed) {
                setNeutralButton("我不同意") { _, _ ->
                    Helper.setSpBool(Constant.FORBID_LOAD, true)
                    Helper.toast(
                        "您已拒绝用户协议, 插件将被禁用", Toast.LENGTH_LONG
                    )
                    Helper.restartApplication(activity)
                }
                setPositiveButton("同意协议, 不再显示") { _, _ ->
                    Helper.setSpBool(Constant.EULA_AGREED, true)
                    Helper.toast(
                        "本App完全免费, 如果在任何渠道付费取得, 请申请退款", Toast.LENGTH_LONG
                    )
                }
            } else {
                setNeutralButton("我不同意") { _, _ ->
                    activity.finishAffinity()
                    activity.finish()
                }
                setPositiveButton("同意协议", null)
            }
            create()
            show()
        }
    }

    /**
     * 弹出首次运行对话框
     */
    fun popupTutorialDialog(activity: Activity) {
        AlertDialog.Builder(activity).apply {
            setTitle("PureNGA 提示")
            setMessage("检测到插件配置文件不存在, 是否打开插件设置?")
            setCancelable(false)
            setNegativeButton("取消") { _, _ ->
                Helper.toast(
                    "可以在【设置】>【PureNGA 设置】中配置插件功能", Toast.LENGTH_LONG
                )
            }
            setPositiveButton("确认") { _, _ ->
                popupSettingDialog(activity)
            }
            create()
            show()
        }
    }

    /**
     * 手动检查更新
     */
    fun popupCheckUpdateManually(activity: Activity) {
        AlertDialog.Builder(activity).apply {
            setTitle("PureNGA 获取版本信息失败")
            setMessage("是否前往发布页获取更新?")
            setPositiveButton("确定") { _, _ ->
                popupGotoReleasePage(activity)
            }
            setNegativeButton("取消", null)
            create()
            show()
        }
    }

    /**
     * 跳转发布页
     */
    fun popupGotoReleasePage(activity: Activity) {
        val releaseList = arrayOf(
            "Github 发布页", "123网盘镜像", "夸克网盘镜像"
        )
        AlertDialog.Builder(activity).apply {
            setTitle("获取 PureNGA 最新版本")
            setItems(releaseList) { _, which ->
                val url = when (which) {
                    0 -> if (Helper.isBundled()) Constant.RELEASE_BUNDLED else Constant.RELEASE_STANDALONE
                    1 -> Constant.RELEASE_123
                    2 -> Constant.RELEASE_QUARK
                    else -> null
                }
                val code = when (which) {
                    1 -> "JEFR"
                    2 -> "NGyD"
                    else -> null
                }
                if (code != null) {
                    Helper.toast("正在前往网盘, 提取码: $code", Toast.LENGTH_LONG)
                } else {
                    Helper.toast("正在前往发布页", Toast.LENGTH_LONG)
                }
                if (url != null) {
                    Helper.openUrl(activity, url)
                }
            }
            setPositiveButton("关闭", null)
            create()
            show()
        }
    }

    /**
     * 弹出检查更新对话框
     */
    fun popupCheckUpdate(activity: Activity) {
        val view = LinearLayout(activity).apply {
            layoutParams =
                LinearLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.MATCH_PARENT)
            orientation = LinearLayout.VERTICAL
            setPadding(16.toPixel(context), 16.toPixel(context), 16.toPixel(context), 16.toPixel(context))
        }
        view.addView(ProgressBar(activity).apply {
            isIndeterminate = true
        })

        AlertDialog.Builder(activity).apply {
            setTitle("PureNGA 正在检查更新...")
            setView(view)
            create().apply {
                show()

                UpdateUtils.getPluginReleaseInfo { resStandalone ->
                    //如果无需更新或者检测出错
                    if (resStandalone == null) {
                        activity.runOnUiThread {
                            dismiss()

                            Helper.toast("检查更新失败, 请稍后再试")
                            popupCheckUpdateManually(activity)
                        }
                        return@getPluginReleaseInfo
                    }

                    val code = UpdateUtils.getAssetVersionCode(resStandalone)
                    if (!UpdateUtils.checkIfNeedUpdate(code)) {
                        activity.runOnUiThread {
                            dismiss()

                            Helper.toast("当前已是最新版本")
                        }
                        return@getPluginReleaseInfo
                    }

                    if (!Helper.isBundled()) {
                        //独立插件检查更新
                        activity.runOnUiThread {
                            dismiss()

                            Helper.toast("有新版本了")
                            popupStandaloneNewVersionDialog(activity, resStandalone, false)
                        }
                    } else {
                        //整合版检查更新

                        val name = UpdateUtils.getAssetVersionName(resStandalone)

                        UpdateUtils.getBundledReleaseInfo { resBundle ->
                            activity.runOnUiThread {
                                dismiss()

                                if (name.isNullOrEmpty() || resBundle == null) {
                                    Helper.toast("检查更新失败, 请稍后再试")
                                    popupCheckUpdateManually(activity)
                                    return@runOnUiThread
                                }

                                if (name != resBundle.tagName || resBundle.assets.isNullOrEmpty()) {
                                    Helper.toast("整合版暂未适配 $name")
                                } else {
                                    Helper.toast("整合版有新版本了")
                                    popupBundledNewVersionDialog(activity, resStandalone, resBundle, false)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * 弹出更新日志对话框
     */
    private fun popupInfoDialog(
        activity: Activity,
        title: String,
        content: String,
        setupBuilder: (AlertDialog.Builder) -> Unit,
        setupDialog: (AlertDialog) -> Unit,
    ) {
        val root = LinearLayout(activity).apply {
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
            orientation = LinearLayout.VERTICAL
            setPadding(16.toPixel(context), 16.toPixel(context), 16.toPixel(context), 16.toPixel(context))
        }
        root.addView(TextView(activity).apply {
            text = content
            textSize = 18f
            isSingleLine = false
            setPadding(0.toPixel(context), 0.toPixel(context), 0.toPixel(context), 16.toPixel(context))
        })

        root.addView(TextView(activity).apply {
            text = "⬇️捐赠项目来支持持续开发"
        })

        // APP更新后显示弹窗
        AlertDialog.Builder(activity).apply {
            setTitle(title)
            setView(root)
            setCancelable(false)
            setupBuilder(this)
            create().apply {
                setupDialog(this)
                show()
            }
        }
    }

    /**
     * 弹出更新日志对话框
     */
    @SuppressLint("SetTextI18n")
    fun popupChangeLogDialog(activity: Activity) {
        val title = "PureNGA 更新日志"

        val changeLog = buildString {
            appendLine("当前版本: ${Helper.PLUGIN_VERSION}")
            appendLine("更新日志:")
            for (line in R.string.change_log_content.getStringFromMod().split("|")) {
                appendLine(" - $line")
            }
        }.trim()

        popupInfoDialog(activity, title, changeLog, setupBuilder = { builder ->
            builder.setNeutralButton("捐赠", null)
            builder.setPositiveButton("关闭并不再提示", null)
        }, setupDialog = { dialog ->
            dialog.setOnShowListener {
                val handler = Handler(Looper.getMainLooper())
                val timer = Timer()
                var count = 5

                dialog.getButton(AlertDialog.BUTTON_POSITIVE).apply {
                    text = "(5)"
                    isEnabled = false

                    timer.schedule(0, 1000) {
                        handler.post {
                            if (count-- > 0) {
                                text = "($count)"
                            } else {
                                isEnabled = true
                                text = "关闭"
                                dialog.setCancelable(true)
                                timer.cancel()
                            }
                        }
                    }
                }

                dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setOnClickListener {
                    popupDonateDialog(activity)
                }
            }

            dialog.setOnDismissListener {
                if (Helper.isXposed) {
                    Helper.setSpInt(Constant.LAST_SHOW_CHANGELOG, BuildConfig.VERSION_CODE)
                }
            }
        })
    }

    /**
     * 弹出版本更新对话框
     */
    fun popupStandaloneNewVersionDialog(activity: Activity, release: Release, showSkip: Boolean) {
        val changeLog = UpdateUtils.getChangeLog(release)
        val downloadUrl = UpdateUtils.getFirstAssetUrl(release)

        if (changeLog.isNullOrEmpty() || downloadUrl == null) {
            return
        }

        popupInfoDialog(activity, "PureNGA 有新版本可用", changeLog, setupBuilder = { builder ->
            builder.setCancelable(true)
            builder.setNeutralButton("捐赠", null)

            if (showSkip) builder.setNegativeButton("跳过该版本") { _, _ ->
                if (Helper.isXposed) {
                    val code = UpdateUtils.getAssetVersionCode(release)
                    Helper.setSpInt(Constant.SKIP_VERSION_CODE, code)
                    Helper.toast("可以在插件设置中手动检查更新")
                }
            }
            else {
                builder.setNegativeButton("网盘镜像") { _, _ ->
                    popupGotoReleasePage(activity)
                }
            }
            builder.setPositiveButton("直接下载") { _, _ ->
                val intent = Intent(Intent.ACTION_VIEW, downloadUrl)
                activity.startActivity(intent)
            }
        }, setupDialog = { dialog ->
            dialog.setOnShowListener {
                dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setOnClickListener {
                    popupDonateDialog(activity)
                }
            }
        })
    }

    /**
     * 弹出版本更新对话框
     */
    fun popupBundledNewVersionDialog(activity: Activity, release: Release, bundleRelease: Release, showSkip: Boolean) {
        val changeLog = UpdateUtils.getChangeLog(release)

        if (changeLog.isNullOrEmpty() || bundleRelease.assets.isNullOrEmpty()) {
            return
        }

        val releaseAssets =
            bundleRelease.assets.filter { x -> !x.name.isNullOrEmpty() && !x.browserDownloadUrl.isNullOrEmpty() }
                .sortedByDescending { x -> x.name }

        val items = mutableListOf<CharSequence>(changeLog)
        for (asset in releaseAssets) {

            val entries = asset.name!!.split("-")
            val name = (entries.firstOrNull() ?: asset.name).replace("nga_", "NGA ")
            items.add("下载 $name 整合版")
        }

        val adapter =
            object : ArrayAdapter<CharSequence>(activity, android.R.layout.simple_list_item_1, items.toTypedArray()) {
                override fun isEnabled(position: Int): Boolean {
                    // 禁用第一个项目（索引 0）
                    return position > 0
                }

                override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                    val v = super.getView(position, convertView, parent) as TextView
                    // 第一个项目文字置灰，增强可见性
                    if (position == 0) {
                        v.setTextColor(Color.GRAY)
                    } else {
                        v.setTextColor(Color.BLACK)
                    }
                    return v
                }
            }

        AlertDialog.Builder(activity).apply {
            setTitle("PureNGA 整合版 有新版本可用")
            setAdapter(adapter) { _, which ->
                if (which == 0) {
                    return@setAdapter
                }

                val url = releaseAssets[which - 1].browserDownloadUrl as String
                Helper.openUrl(activity, url)
            }
            setCancelable(true)
            setNeutralButton("捐赠", null)

            if (showSkip) {
                setNegativeButton("跳过该版本") { _, _ ->
                    if (Helper.isXposed) {
                        val code = UpdateUtils.getAssetVersionCode(release)
                        Helper.setSpInt(Constant.SKIP_VERSION_CODE, code)
                        Helper.toast("可以在插件设置中手动检查更新")
                    }
                }
            }
            setPositiveButton("网盘镜像") { _, _ ->
                popupGotoReleasePage(activity)
            }
            create().apply {
                setOnShowListener {
                    getButton(AlertDialog.BUTTON_NEUTRAL).setOnClickListener {
                        popupDonateDialog(activity)
                    }
                }

                setOnDismissListener {
                    if (Helper.isXposed) {
                        val code = UpdateUtils.getAssetVersionCode(release)
                        Helper.setSpInt(Constant.SKIP_VERSION_CODE, code)
                        Helper.toast("可以在插件设置中手动检查更新")
                    }
                }

                show()
            }
        }
    }

    /**
     * 点击捐赠
     */
    private fun onClickDonate(activity: Context) {
        Helper.toast("感谢支持")
        Helper.openUrl(activity, Constant.DONATE_URL)
    }

    /**
     * 弹出捐赠对话框
     */
    fun popupDonateDialog(activity: Context) {
        val view = FitImageView(activity, R.drawable.aifadian).apply {
            setOnClickListener {
                onClickDonate(activity)
            }
        }

        AlertDialog.Builder(activity).apply {
            setTitle("捐赠支持 PureNGA")
            setView(view)
            setCancelable(true)
            setNeutralButton("爱发电 @chr233") { dialog, _ ->
                onClickDonate(activity)
                dialog.dismiss()
            }
            setPositiveButton("关闭", null)
            create()
            show()
        }
    }
}
