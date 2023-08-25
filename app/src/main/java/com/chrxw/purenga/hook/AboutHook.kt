package com.chrxw.purenga.hook

import android.content.Intent
import android.content.pm.PackageInfo
import android.net.Uri
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import com.chrxw.purenga.BuildConfig
import com.chrxw.purenga.Constant
import com.chrxw.purenga.utils.Helper
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers


/**
 * 关于页面钩子
 */
class AboutHook : IHook {

    companion object {
        private lateinit var clsAboutUsActivity: Class<*>
    }

    override fun hookName(): String {
        return "关于页修改"
    }

    override fun init(classLoader: ClassLoader) {
        clsAboutUsActivity = classLoader.loadClass("com.donews.nga.setting.AboutUsActivity")
    }

    override fun hook() {
        XposedHelpers.findAndHookMethod(clsAboutUsActivity, "initLayout", object : XC_MethodHook() {
            override fun afterHookedMethod(param: MethodHookParam) {
                val viewBinding = XposedHelpers.callMethod(param.thisObject, "getViewBinding")
                val root = XposedHelpers.callMethod(viewBinding, "getRoot") as View
                val viewId = Helper.getRId("tv_app_version")
                val textView = root.findViewById<TextView>(viewId)

                val pluginVersion = BuildConfig.VERSION_NAME
                val ngaVersion = try {
                    Helper.context.packageManager.getPackageInfo(
                        Constant.NGA_PACKAGE_NAME, PackageInfo.INSTALL_LOCATION_AUTO
                    ).versionName
                } catch (e: Throwable) {
                    "获取失败"
                }

                textView.text =
                    "NGA 版本: $ngaVersion" + System.lineSeparator() + "PureNGA 版本: $pluginVersion"

                val linearLayout = textView.parent as LinearLayout
                val btn = Button(root.context)
                btn.text = "检查插件更新"
                btn.setOnClickListener {
                    root.context.startActivity(
                        Intent(
                            Intent.ACTION_VIEW, Uri.parse(Constant.REPO_URL)
                        )
                    )
                }

                linearLayout.addView(btn)
            }
        })
    }
}
