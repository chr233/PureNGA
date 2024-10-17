package com.chrxw.purenga.hook

import android.app.Activity
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.chrxw.purenga.BuildConfig
import com.chrxw.purenga.utils.ExtensionUtils.findFirstMethodByName
import com.chrxw.purenga.utils.ExtensionUtils.log
import com.chrxw.purenga.utils.Helper
import com.github.kyuubiran.ezxhelper.HookFactory.`-Static`.createHook
import de.robv.android.xposed.XposedHelpers


/**
 * 关于页面钩子
 */
class AboutHook : IHook {
    /**
     *
     */
    companion object {
        lateinit var clsAboutUsActivity: Class<*>
    }

    override fun init(classLoader: ClassLoader) {
        clsAboutUsActivity = classLoader.loadClass("com.donews.nga.setting.AboutUsActivity")
    }

    override fun hook() {
        findFirstMethodByName(clsAboutUsActivity, "initLayout")?.createHook {
            after {
                it.log()

                val activity = it.thisObject as Activity

                val viewBinding = XposedHelpers.getObjectField(activity, "viewBinding")
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

                appIcon.setOnClickListener {
                    PreferencesHook.showSettingDialog(activity)
                }

                if (activity.intent.getBooleanExtra("openDialog", false)) {
                    PreferencesHook.showSettingDialog(activity)
                }
            }
        }
    }

    override var name = "AboutHook"
}
