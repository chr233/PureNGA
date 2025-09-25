package com.chrxw.purenga.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.ShortcutInfo
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TableRow.LayoutParams
import android.widget.TextView
import android.widget.Toast
import androidx.core.net.toUri
import com.chrxw.purenga.BuildConfig
import com.chrxw.purenga.Constant
import com.chrxw.purenga.R
import com.chrxw.purenga.ui.ClickableItemXpView
import com.chrxw.purenga.ui.DarkContainLayout
import com.chrxw.purenga.ui.FitImageXpView
import com.chrxw.purenga.ui.ToggleItemXpView
import com.chrxw.purenga.utils.ExtensionUtils.buildShortcut
import com.chrxw.purenga.utils.ExtensionUtils.getStringFromMod
import com.chrxw.purenga.utils.ExtensionUtils.setShortcuts
import com.chrxw.purenga.utils.ExtensionUtils.toPixel
import com.github.kyuubiran.ezxhelper.AndroidLogger
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
            ToggleItemXpView(activity, Constant.ENABLE_PURE_POST, "开启自定义屏蔽", "按照关键词过滤帖子列表")
        )
        root.addView(
            ClickableItemXpView(activity, "设置标题屏蔽词", "关键词之间使用 | 分隔, 关键词匹配").apply {
                setOnClickListener {
                    onSetThreadTitleBlacklist(activity)
                }
            })
        root.addView(
            ClickableItemXpView(activity, "设置发帖人屏蔽词", "关键词之间使用 | 分隔, 关键词匹配").apply {
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
            }
            show()
        }
    }

    /**
     * 设置帖子屏蔽词
     */
    private fun onSetThreadTitleBlacklist(activity: Activity) {
        if (Helper.getSpBool(Constant.ENABLE_PURE_POST, false)) {
            val input = EditText(activity).apply {
                maxLines = 8
                setText(Helper.getSpStr(Constant.PURE_POST, ""))
            }

            AlertDialog.Builder(activity).apply {
                setTitle("设置帖子屏蔽词")
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
            }
            show()
        }
    }

    /**
     * 设置自定义首页
     */
    private fun onSetCustomHome(activity: Activity) {
        val items = arrayOf("首页", "社区", "我的")
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

        val check = ToggleItemXpView(
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
            setTitle("设置自定义字体")
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

    /**
     *  设置自定义快捷方式
     */
    private fun onSetCustomShortCut(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            val shortcutSettings = Helper.getSpStr(Constant.SHORTCUT_SETTINGS, null)

            // 可用快捷方式列表
            val availableShortcuts = arrayOf(
                activity.buildShortcut("sign", "签到", "签到", null),
                activity.buildShortcut("home", "首页", "首页", null),
                activity.buildShortcut("account", "账号切换", "账号切换", null),
                activity.buildShortcut("qrcode", "扫码", "扫码", null),
                activity.buildShortcut("message", "消息", "消息", null),
                activity.buildShortcut("setting", "设置", "设置", null),
                activity.buildShortcut("about", "关于", "关于", null),
                activity.buildShortcut("theme", "个性装扮", "个性装扮", null),
                activity.buildShortcut("game", "游戏档案", "游戏档案", null),
                activity.buildShortcut("favorite", "收藏", "收藏", null),
                activity.buildShortcut("history", "浏览历史", "浏览历史", null),
                activity.buildShortcut("draft", "草稿箱", "草稿箱", null),
                activity.buildShortcut("diagnose", "网络诊断", "网络诊断", null),
                activity.buildShortcut("pluginSetting", "PureNGA设置", "PureNGA设置", null),
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
                }
                show()
            }
        } else {
            Helper.toast("当前安卓版本不支持此操作")
        }
    }

    /**
     * 手动检查更新
     */
    private fun onCheckUpdateManually(activity: Activity) {
        val uri = if (Helper.isBundled()) Constant.RELEASE_BUNDLED else Constant.RELEASE_STANDALONE
        Helper.openUrl(activity, uri)
    }

    /**
     * 导出插件设置
     */
    private fun onDumpPluginSetting(activity: Activity) {
        val result = Helper.exportSharedPreference(
            activity, Constant.PLUGIN_PREFERENCE_NAME, Constant.PLUGIN_PREFERENCE_NAME
        )
        val msg = buildString {
            appendLine(if (result != null) "导出成功" else "导出失败")
            appendLine("配置文件路径: $result")
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
            appendLine(if (result != null) "导入成功" else "导入失败")
            appendLine("配置文件路径: $result")
        }
        Helper.toast(msg, Toast.LENGTH_SHORT)
    }

    /**
     * 弹出设置对话框
     */
    fun popupSettingDialog(activity: Activity) {
        val root = ScrollView(activity)
        val container = DarkContainLayout(activity, true)

        // 净化设置
        container.addView(ClickableItemXpView(activity, "净化设置"))
        container.addView(
            ToggleItemXpView(activity, Constant.PURE_SPLASH_AD, "屏蔽开屏广告", "冷启动会短暂黑屏, 属于正常现象")
        )
        container.addView(
            ToggleItemXpView(
                activity, Constant.PURE_POST_AD, "屏蔽信息流广告", "去除Banner位, 帖子列表, 帖子末尾的广告"
            )
        )
        container.addView(
            ToggleItemXpView(activity, Constant.PURE_GAME_RECOMMEND, "屏蔽游戏推荐", "去除首页游戏推荐广告")
        )
        container.addView(
            ToggleItemXpView(activity, Constant.PURE_POPUP_AD, "屏蔽首页广告", "去除首页浮窗广告")
        )
        container.addView(
            ClickableItemXpView(activity, "自定义屏蔽帖子", "按照关键词过滤帖子列表").apply {
                setOnClickListener {
                    onSetThreadFilter(activity)
                }
            })

        // 界面优化
        container.addView(ClickableItemXpView(activity, "界面优化"))
        container.addView(
            ClickableItemXpView(activity, "侧边栏净化", "勾选要过滤的侧边栏菜单").apply {
                setOnClickListener {
                    onSetPureSlideMenu(activity)
                }
            })
        container.addView(
            ToggleItemXpView(activity, Constant.REMOVE_STORE_ICON, "净化导航栏1", "去除导航栏游戏库入口")
        )
        container.addView(
            ToggleItemXpView(activity, Constant.REMOVE_ACTIVITY_ICON, "净化导航栏2", "去除导航栏活动图标")
        )
        container.addView(
            ToggleItemXpView(activity, Constant.REMOVE_WECHAT_ICON, "去除微信分享图标", "移除帖子详情页右上角微信图标")
        )
        container.addView(
            ToggleItemXpView(activity, Constant.REMOVE_POPUP_POST, "去除首页文章推荐", "移除首页导航栏上方文章推荐")
        )
        container.addView(
            ToggleItemXpView(
                activity,
                Constant.QUICK_ACCOUNT_MANAGE,
                "快捷切换账号",
                "长按菜单上方的用户名跳转账号切换, 仅 9.9.4 以上版本有效"
            )
        )
        container.addView(
            ToggleItemXpView(
                activity, Constant.PREFER_NEW_POST, "默认使用“新发布”", "帖子列表默认使用“新发布”而不是“新回复”"
            )
        )

        // 自定义
        container.addView(ClickableItemXpView(activity, "自定义"))
        container.addView(
            ClickableItemXpView(activity, "自定义首页", "设置APP首页").apply {
                setOnClickListener {
                    onSetCustomHome(activity)
                }
            })

        container.addView(
            ClickableItemXpView(activity, "设置自定义字体", "设置帖子详情页使用的字体").apply {
                setOnClickListener {
                    onSetCustomFont(activity)
                }
            })
        container.addView(
            ClickableItemXpView(activity, "", "").apply {
                title = "自定义快捷方式"
                subTitle = "设置长按APP图标快捷方式, 仅支持安卓 7.1 及以上版本"
                setOnClickListener { onSetCustomShortCut(activity) }
            })

        // 其他功能
        container.addView(ClickableItemXpView(activity, "其他功能"))
        container.addView(
            ToggleItemXpView(
                activity,
                Constant.AUTO_SIGN,
                "自动打开签到页面",
                "【不推荐, 开启 本地VIP 可以自动签到】没有签到时自动打开签到页面进行签到"
            )
        )
        container.addView(
            ToggleItemXpView(
                activity,
                Constant.PURE_CALENDAR_DIALOG,
                "屏蔽日历弹窗",
                "屏蔽签到页面的添加日历提醒弹窗, 9.9.20 之前无需开启, 9.9.50 疑似失效"
            )
        )
        container.addView(
            ToggleItemXpView(
                activity,
                Constant.USE_EXTERNAL_BROWSER,
                "使用外部浏览器打开链接",
                "打开非NGA链接时自动调用外部系统浏览器"
            )
        )
        container.addView(
            ToggleItemXpView(
                activity, Constant.KILL_UPDATE_CHECK, "禁止APP检查更新", "尝试阻止NGA检查更新, 9.9.50 疑似失效"
            )
        )
        container.addView(ToggleItemXpView(activity, Constant.KILL_POPUP_DIALOG, "屏蔽应用内弹窗", "作用不明"))
        container.addView(
            ToggleItemXpView(
                activity, Constant.FAKE_SHARE, "假装分享", "在分享菜单增加一个“假装分享”按钮"
            )
        )
        container.addView(
            ToggleItemXpView(
                activity, Constant.LOCAL_VIP, "本地会员", "假装是付费会员(例如换肤功能有效)"
            )
        )
        container.addView(
            ToggleItemXpView(
                activity, Constant.BYPASS_INSTALL_CHECK, "绕过已安装检查", "分享到指定App前检查不检查是否已安装(调试用)"
            )
        )

        // 插件设置
        container.addView(ClickableItemXpView(activity, "插件设置"))
        container.addView(
            ToggleItemXpView(activity, Constant.HIDE_HOOK_INFO, "静默运行", "启动时不显示模块运行信息")
        )
        container.addView(
            ToggleItemXpView(
                activity, Constant.HIDE_ERROR_INFO, "静默报错信息", "启动时不显示模块报错信息(如果有的话)"
            )
        )

        if (!Helper.hasSpKey(Constant.CHECK_PLUGIN_UPDATE)) {
            Helper.setSpBool(Constant.CHECK_PLUGIN_UPDATE, true)
        }

        // 关于
        container.addView(ClickableItemXpView(activity, "关于"))
        container.addView(
            ToggleItemXpView(
                activity, Constant.CHECK_PLUGIN_UPDATE, "定期检查插件更新", "如果有更新会显示通知"
            )
        )
        container.addView(
            ClickableItemXpView(activity, "立即检查更新", "").apply {
                val ngaVersion = Helper.getNgaVersion()
                val pluginVersion = "${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})"
                subTitle = "NGA: $ngaVersion | 插件: $pluginVersion"
                setOnClickListener {
                    popupCheckUpdate(activity)
                }
            })
        container.addView(
            ClickableItemXpView(activity, "前往发布页", "").apply {
                subTitle = if (Helper.isBundled()) {
                    "查看最新整合版"
                } else {
                    "查看最新独立插件版"
                }

                setOnClickListener {
                    val url = if (Helper.isBundled()) {
                        Constant.RELEASE_BUNDLED
                    } else {
                        Constant.RELEASE_STANDALONE
                    }
                    context.startActivity(
                        Intent(Intent.ACTION_VIEW, url.toUri())
                    )
                }
            })
        container.addView(
            ClickableItemXpView(activity, "作者", "GitHub @chr233").apply {
                setOnClickListener {
                    Helper.openUrl(context, Constant.AUTHOR_URL)
                }
            })
        container.addView(
            ClickableItemXpView(activity, "捐赠", "爱发电 @chr233").apply {
                setOnClickListener {
                    Helper.openUrl(context, Constant.DONATE_URL)
                }
            })

        // 导入导出
        container.addView(ClickableItemXpView(activity, "导入导出"))
        container.addView(
            ClickableItemXpView(activity, "导出插件设置", "导出插件设置").apply {
                setOnClickListener {
                    onDumpPluginSetting(activity)
                }
            })
        container.addView(
            ClickableItemXpView(activity, "导入插件设置", "导入插件设置").apply {
                setOnClickListener {
                    onImportPluginSetting(activity)
                }
            })

        // 调试设置
        container.addView(ClickableItemXpView(activity, "调试设置"))
        container.addView(
            ToggleItemXpView(
                activity, Constant.ENABLE_HOOK_LOG, "启用Hook日志", "在Logcat中输出详细日志"
            )
        )
        container.addView(
            ToggleItemXpView(activity, Constant.ENABLE_ACTIVITY_LOG, "启用Activity日志", "在Logcat中输出详细日志")
        )
        container.addView(
            ToggleItemXpView(activity, Constant.ENABLE_POST_LOG, "启用帖子信息日志", "在Logcat中输出详细日志")
        )

        root.addView(container)

        AlertDialog.Builder(activity).apply() {
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

    fun popupCheckUpdate(activity: Activity) {
        val dialog = AlertDialog.Builder(activity)
            .setMessage("正在检查更新…")
            .create()

        dialog.show()

        UpdateUtils.getReleaseInfo { res ->
          activity.runOnUiThread {
              Helper.toast(res?.tagName ?: "null")
          }
            dialog.dismiss()
        }
    }

    private fun onClickDonate(activity: Activity) {
        Helper.toast("感谢支持")
        Helper.openUrl(activity, Constant.DONATE_URL)
    }

    @SuppressLint("SetTextI18n")
    fun popupChangeLogDialog(activity: Activity) {
        val root = ScrollView(activity)
        val linearLayout = DarkContainLayout(activity, true)

        val ngaVersion = Helper.getNgaVersion()
        val sunType = if (Helper.isBundled()) "整合版" else "插件版"
        val pluginVersion = "${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE}) - $sunType"

        val changeLog = buildString {
            appendLine(R.string.chang_log.getStringFromMod().replace("|", "\n"))
            appendLine("-------------------------")
            appendLine("NGA版本: $ngaVersion")
            appendLine("插件版本: $pluginVersion")
            appendLine("-------------------------")
            append("⬇️捐赠项目来支持持续开发⬇️")
        }

        linearLayout.addView(TextView(activity).apply {
            text = changeLog
            setPadding(16.toPixel(context), 16.toPixel(context), 16.toPixel(context), 16.toPixel(context))
            isSingleLine = false
        })

        linearLayout.addView(FitImageXpView(activity, R.drawable.aifadian).apply {
            setOnClickListener {
                onClickDonate(activity)
            }
        })

        root.addView(linearLayout)

        // APP更新后显示弹窗
        AlertDialog.Builder(activity).apply {
            setTitle("PureNGA 更新说明")
            setView(root)
            setCancelable(false)
            setNeutralButton("爱发电") { dialog, _ ->
                onClickDonate(activity)
                dialog.dismiss()
            }
            setPositiveButton("关闭并不再提示", null)
            create().apply {
                setOnShowListener {
                    val btn = getButton(AlertDialog.BUTTON_POSITIVE)
                    btn.isEnabled = false

                    val handler = Handler(Looper.getMainLooper())
                    val timer = Timer()
                    var count = 4
                    timer.schedule(0, 1000) {
                        handler.post {
                            AndroidLogger.w(count.toString())
                            if (count > 0) {
                                btn.text = "($count)"
                                count--
                            } else {
                                btn.isEnabled = true
                                btn.text = "关闭并不再提示"
                                timer.cancel()
                            }
                        }
                    }
                }
            }

            setOnDismissListener {
                Helper.setSpInt(Constant.LAST_SHOW, BuildConfig.VERSION_CODE)
            }

            show()
        }
    }
}