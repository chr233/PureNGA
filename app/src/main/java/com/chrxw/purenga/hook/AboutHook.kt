package com.chrxw.purenga.hook

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.view.View
import android.widget.TextView
import com.chrxw.purenga.BuildConfig
import com.chrxw.purenga.Constant
import com.chrxw.purenga.utils.Helper
import com.chrxw.purenga.utils.Helper.log
import com.github.kyuubiran.ezxhelper.HookFactory.`-Static`.createHook
import com.github.kyuubiran.ezxhelper.finders.MethodFinder
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
        MethodFinder.fromClass(clsAboutUsActivity).filterByName("initLayout").first().createHook {
            after {
                it.log()

                val activity = it.thisObject as Activity

                val viewBinding = XposedHelpers.getObjectField(activity, "viewBinding")
                val root = XposedHelpers.callMethod(viewBinding, "getRoot") as View
                val viewId = Helper.getRId("tv_app_version")

                val pluginVersion = BuildConfig.VERSION_NAME
                val ngaVersion = Helper.getNgaVersion()

                val textView = root.findViewById<TextView>(viewId)
                textView.text = "NGA 版本: $ngaVersion\nPureNGA 版本: $pluginVersion"

                textView.setOnClickListener {
                    root.context.startActivity(
                        Intent(
                            Intent.ACTION_VIEW, Uri.parse(Constant.REPO_URL)
                        )
                    )
                }
            }
        }
    }
}
