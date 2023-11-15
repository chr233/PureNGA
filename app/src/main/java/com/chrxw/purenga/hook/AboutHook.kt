package com.chrxw.purenga.hook

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.chrxw.purenga.BuildConfig
import com.chrxw.purenga.Constant
import com.chrxw.purenga.utils.ExtensionUtils.findFirstMethodByName
import com.chrxw.purenga.utils.ExtensionUtils.log
import com.chrxw.purenga.utils.Helper
import com.github.kyuubiran.ezxhelper.HookFactory.`-Static`.createHook
import de.robv.android.xposed.XposedHelpers


/**
 * 关于页面钩子
 */
class AboutHook : IHook {

    companion object {
        private lateinit var clsAboutUsActivity: Class<*>
    }

    override fun init(classLoader: ClassLoader) {
        clsAboutUsActivity = classLoader.loadClass("com.donews.nga.setting.AboutUsActivity")
    }

    @SuppressLint("SetTextI18n")
    override fun hook() {
        findFirstMethodByName(clsAboutUsActivity, "initLayout")?.createHook {
            after {
                it.log()

                val activity = it.thisObject as Activity

                val viewBinding = XposedHelpers.getObjectField(activity, "viewBinding")
                val root = XposedHelpers.callMethod(viewBinding, "getRoot") as View
                val viewId = Helper.getRId("tv_app_version")

                val pluginVersion = BuildConfig.VERSION_NAME
                val ngaVersion = Helper.getNgaVersion()

                val textView = root.findViewById<TextView>(viewId)
                textView.text = "NGA 版本: $ngaVersion\nPureNGA 版本: $pluginVersion\n点上方图标打开设置"

                textView.setOnClickListener {
                    root.context.startActivity(
                        Intent(
                            Intent.ACTION_VIEW, Uri.parse(Constant.REPO_URL)
                        )
                    )
                }

                val appIconId = Helper.getRId("iv_app_icon")
                val appIcon = root.findViewById<ImageView>(appIconId)

                appIcon.setOnClickListener {
                    val view = PreferencesHook.generateView(activity)

                    AlertDialog.Builder(activity).apply {
                        setTitle(Constant.BTN_TITLE)
                        setCancelable(false)
                        setView(view)
                        setNegativeButton("关闭") { _, _ ->
                            Helper.toast("设置已保存, 重启后生效")
                        }
                        setPositiveButton("重启 NGA") { _, _ ->
                            Helper.toast("设置已保存")
                            PreferencesHook.restartApplication(activity)
                        }

                        create()
                        show()
                    }
                }
            }
        }
    }

    override var name = "AboutHook"
}
