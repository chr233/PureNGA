package com.chrxw.purenga.hook

import android.annotation.SuppressLint
import android.app.Activity
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.core.graphics.toColorInt
import com.chrxw.purenga.BuildConfig
import com.chrxw.purenga.Constant
import com.chrxw.purenga.R
import com.chrxw.purenga.hook.base.IHook
import com.chrxw.purenga.utils.ExtensionUtils.findFirstMethodByName
import com.chrxw.purenga.utils.ExtensionUtils.getDrawable
import com.chrxw.purenga.utils.ExtensionUtils.log
import com.chrxw.purenga.utils.Helper
import com.chrxw.purenga.utils.DialogUtils
import com.github.kyuubiran.ezxhelper.EzXHelper
import com.github.kyuubiran.ezxhelper.HookFactory.`-Static`.createHook
import de.robv.android.xposed.XposedHelpers


/**
 * 设置页面钩子
 */
class PreferencesHook : IHook {

    companion object {
        lateinit var clsSettingActivity: Class<*>
        lateinit var clsAboutUsActivity: Class<*>
    }

    override fun init(classLoader: ClassLoader) {
        clsSettingActivity = classLoader.loadClass("com.donews.nga.setting.SettingActivity")
        clsAboutUsActivity = classLoader.loadClass("com.donews.nga.setting.AboutUsActivity")
    }

    @SuppressLint("UseCompatLoadingForDrawables")
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
                    btnPureNGASetting = Button(activity).apply {
                        text = Constant.STR_PURENGA_SETTING
                        setOnClickListener {
                            DialogUtils.popupSettingDialog(activity)
                        }

                        setTextColor(if (Helper.isDarkModel()) "#f8fae3".toColorInt() else "#3c3b39".toColorInt())
                        setBackgroundColor(0)
                        setPadding(5, 5, 5, 5)
                        linearLayout.removeViewAt(linearLayout.childCount - 1)
                        linearLayout.addView(this)
                    }
                }

                if (activity.intent.getBooleanExtra("openDialog", false)) {
                    DialogUtils.popupSettingDialog(activity)
                }
            }
        }

        findFirstMethodByName(MainHook.clsAppConfig, "setDarkModel")?.createHook {
            after {
                btnPureNGASetting?.setTextColor(if (Helper.isDarkModel()) "#f8fae3".toColorInt() else "#3c3b39".toColorInt())
            }
        }

        findFirstMethodByName(clsAboutUsActivity, "initLayout")?.createHook {
            after {
                it.log()

                val activity = it.thisObject as Activity

                val viewBinding = AdHook.fldViewBinding.get(activity)
                val root = XposedHelpers.callMethod(viewBinding, "getRoot") as View
                val viewId = Helper.getRId("tv_app_version")

                val pluginVersion = "${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})"
                val ngaVersion = Helper.getNgaVersion()

                val textView = root.findViewById<TextView>(viewId)
                textView.text = buildString {
                    appendLine("NGA 版本: $ngaVersion")
                    appendLine("插件 版本: $pluginVersion")
                    appendLine("点上方图标打开设置")
                }
                textView.gravity = Gravity.CENTER

                textView.setOnClickListener {
                    Helper.toast("正在前往 PureNGA 项目主页")
                    Helper.gotoReleasePage(root.context)
                }

                val appIconId = Helper.getRId("iv_app_icon")
                val appIcon = root.findViewById<ImageView>(appIconId)

                if (EzXHelper.isModuleResInited) {
                    val pluginIcon = R.mipmap.ic_launcher.getDrawable(activity.theme)
                    appIcon.setImageDrawable(pluginIcon)
                }

                appIcon.setOnClickListener {
                    DialogUtils.popupSettingDialog(activity)
                }

                if (activity.intent.getBooleanExtra("openDialog", false)) {
                    DialogUtils.popupSettingDialog(activity)
                }
            }
        }
    }

    override var name = "PreferencesHook"
}

