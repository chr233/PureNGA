package com.chrxw.purenga.hook

import android.R.attr
import android.app.Instrumentation
import android.content.Intent
import android.net.Uri
import com.chrxw.purenga.Constant
import com.chrxw.purenga.utils.Helper
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers


/**
 * 内置浏览器钩子
 */
class WebViewHook : IHook {

    companion object {
        private fun isNgaUrl(url: String): Boolean {
            return XposedHelpers.callMethod(OptimizeHook.clsAppConfig, "isNgaUrl", url) as Boolean
        }
    }

    override fun hookName(): String {
        return "内置浏览器优化"
    }

    override fun init(classLoader: ClassLoader) {
    }

    override fun hook() {
        if (Helper.spPlugin.getBoolean(Constant.USE_EXTERNAL_BROWSER, false)) {
            XposedBridge.hookAllMethods(Instrumentation::class.java, "execStartActivity", object : XC_MethodHook() {
                override fun beforeHookedMethod(param: MethodHookParam?) {
                    val intent = param?.args?.get(4) as? Intent ?: return
                    val bundle = intent.extras
                    val clsName = intent.component?.className ?: ""
                    if (bundle != null && clsName == "com.donews.nga.activitys.WebActivity") {
                        val url = bundle.getString("act_url")
                        if (url != null && !isNgaUrl(url)) {
                            param.args[4] = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                        }
                    }
                }
            })
        }
    }
}

