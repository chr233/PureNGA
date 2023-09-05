package com.chrxw.purenga.hook

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TableRow.LayoutParams
import com.chrxw.purenga.BuildConfig
import com.chrxw.purenga.Constant
import com.chrxw.purenga.ui.ClickableItemView
import com.chrxw.purenga.ui.ToggleItemView
import com.chrxw.purenga.utils.Helper
import com.github.kyuubiran.ezxhelper.EzXHelper
import com.github.kyuubiran.ezxhelper.HookFactory.`-Static`.createHook
import com.github.kyuubiran.ezxhelper.finders.MethodFinder
import de.robv.android.xposed.XposedHelpers
import kotlin.system.exitProcess

/**
 * 设置页面钩子
 */
class PreferencesHook : IHook {

    companion object {
        lateinit var clsMainActivity: Class<*>
        lateinit var clsSettingActivity: Class<*>

        fun restartApplication(activity: Activity) {
            val pm = activity.packageManager
            val intent = pm.getLaunchIntentForPackage(activity.packageName)
            activity.finishAffinity()
            activity.startActivity(intent)
            exitProcess(0)
        }
    }

    override fun init(classLoader: ClassLoader) {
        clsMainActivity = classLoader.loadClass("com.donews.nga.activitys.MainActivity")
        clsSettingActivity = classLoader.loadClass("com.donews.nga.setting.SettingActivity")
    }

    override fun hook() {

        var btnPureNGASetting: Button? = null

        MethodFinder.fromClass(clsSettingActivity).filterByName("initLayout").first().createHook {
            after { param ->
                val activity = param.thisObject as Activity

                val viewBinding = XposedHelpers.getObjectField(activity, "viewBinding")
                val root = XposedHelpers.callMethod(viewBinding, "getRoot") as LinearLayout
                val scrollView = root.getChildAt(1) as ScrollView
                val linearLayout = scrollView.getChildAt(0) as LinearLayout

                EzXHelper.addModuleAssetPath(activity)

                activity.runOnUiThread {
                    btnPureNGASetting = Button(activity).also { btn ->
                        btn.text = Constant.BTN_TITLE
                        btn.setOnClickListener {

                            val view = generateView(activity)

                            AlertDialog.Builder(activity).run {
                                setTitle(Constant.BTN_TITLE)
                                setCancelable(false)
                                setView(view)
                                setNegativeButton("关闭") { _, _ ->
                                    Helper.toast("设置已保存, 重启后生效")
                                }
                                setPositiveButton("重启 NGA") { _, _ ->
                                    Helper.toast("设置已保存")
                                    restartApplication(activity)
                                }

                                create()
                                show()
                            }
                        }

                        btn.setTextColor(Color.parseColor(if (Helper.isDarkModel()) "#f8fae3" else "#3c3b39"))
                        btn.setBackgroundColor(0)
                        btn.setPadding(5, 5, 5, 5)
                        linearLayout.removeViewAt(linearLayout.childCount - 1)
                        linearLayout.addView(btn)
                    }
                }
            }
        }

        MethodFinder.fromClass(MainHook.clsAppConfig).filterByName("setDarkModel")
            .filterByAssignableParamTypes(Boolean::class.java)
            .first().createHook {
                after {
                    btnPureNGASetting?.setTextColor(Color.parseColor(if (Helper.isDarkModel()) "#f8fae3" else "#3c3b39"))
                }
            }
    }

    /**
     * 生成设置界面
     */
    private fun generateView(context: Context): View {
        val root = ScrollView(context)
        val container = LinearLayout(context)
        container.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        container.orientation = LinearLayout.VERTICAL

        container.addView(ClickableItemView(context).apply { title = "净化设置" })
        container.addView(ToggleItemView(context, Constant.PURE_SPLASH_AD).apply {
            title = "屏蔽开屏广告"
            subTitle = "冷启动会短暂黑屏, 属于正常现象"
        })
        container.addView(ToggleItemView(context, Constant.PURE_POST_AD).apply {
            title = "屏蔽信息流广告"
            subTitle = "去除Banner位, 帖子列表, 帖子末尾的广告"
        })
        container.addView(ToggleItemView(context, Constant.CRACK_AD_TASK).apply {
            title = "破解看广告任务"
            subTitle = "秒关不影响任务奖励结算"
        })

        container.addView(ClickableItemView(context).apply { title = "界面优化" })
        container.addView(ToggleItemView(context, Constant.REMOVE_STORE_ICON).apply {
            title = "去除商城和钱包入口"
            subTitle = "移除导航栏和滑动菜单中的入口"
        })
        container.addView(ToggleItemView(context, Constant.REMOVE_ACTIVITY_ICON).apply {
            title = "去除活动图标"
            subTitle = "移除导航栏活动图标"
        })
        container.addView(ToggleItemView(context, Constant.REMOVE_WECHAT_ICON).apply {
            title = "去除微信分享图标"
            subTitle = "移除文章详情页右上角微信图标"
        })

        container.addView(ClickableItemView(context).apply { title = "功能设置" })
        container.addView(ToggleItemView(context, Constant.USE_EXTERNAL_BROWSER).apply {
            title = "使用外部浏览器打开链接"
            subTitle = "打开非NGA链接时自动调用外部系统浏览器"
        })
        container.addView(ToggleItemView(context, Constant.KILL_UPDATE_CHECK).apply {
            title = "禁止APP检查更新"
            subTitle = "尝试阻止NGA检查更新"
        })
        container.addView(ToggleItemView(context, Constant.REMOVE_ACTIVITY_ICON).apply {
            title = "屏蔽应用内弹窗"
            subTitle = "作用不明"
        })

        container.addView(ClickableItemView(context).apply { title = "插件设置" })
//        container.addView(ToggleItemView(context, Constant.CHECK_PLUGIN_UPDATE).apply {
//            title = "检查插件更新(WIP)"
//            subTitle = "定期检查插件更新"
//            isEnabled=false
//
//        })
        container.addView(ToggleItemView(context, Constant.HIDE_HOOK_INFO).apply {
            title = "静默运行"
            subTitle = "不显示模块运行信息"
        })
        container.addView(ClickableItemView(context).apply {
            title = "手动检查更新"
            val ngaVersion = Helper.getNgaVersion()
            val type = if (Helper.isBundled()) "插件版" else "整合版"
            subTitle = "NGA版本: $ngaVersion | 插件版本: ${BuildConfig.VERSION_NAME} - $type"
            setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(Constant.REPO_URL))
                context.startActivity(intent)
//                Helper.toast("todo")
            }
        })

        container.addView(ClickableItemView(context).apply { title = "关于" })
        container.addView(ClickableItemView(context).apply {
            title = "作者"
            subTitle = "chr233"
            setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(Constant.AUTHOR_URL))
                context.startActivity(intent)
            }
        })
        container.addView(ClickableItemView(context).apply {
            title = "捐赠(爱发电)"
            subTitle = "@chr233"
            setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(Constant.DONATE_URL))
                context.startActivity(intent)
            }
        })

        root.addView(container)
        return root
    }
}

