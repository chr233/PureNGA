package com.chrxw.purenga.hook

import android.app.Instrumentation
import android.content.Intent
import android.net.Uri
import com.chrxw.purenga.Constant
import com.chrxw.purenga.utils.ExtensionUtils.findFirstMethodByName
import com.chrxw.purenga.utils.ExtensionUtils.log
import com.chrxw.purenga.utils.Helper
import com.github.kyuubiran.ezxhelper.AndroidLogger
import com.github.kyuubiran.ezxhelper.HookFactory.`-Static`.createHook
import java.lang.reflect.Method


/**
 * 内置浏览器钩子
 */
class WebViewHook : IHook {

    companion object {
        private lateinit var insAppConfig: Any
        private lateinit var mtdIsNgaUrl: Method

        private fun isNgaUrl(url: String): Boolean {
            return mtdIsNgaUrl.invoke(insAppConfig, url) as Boolean
        }

        private fun isBilibiliUrl(url: String): Boolean {
            return false
        }
    }

    override fun init(classLoader: ClassLoader) {
        insAppConfig = MainHook.clsAppConfig.getField("INSTANCE").get(null)!!
        mtdIsNgaUrl = MainHook.clsAppConfig.getMethod("isNgaUrl", String::class.java)

        val hosts = MainHook.clsAppConfig.getField("hosts").get(null) as Array<*>
        for (host in hosts) {
            AndroidLogger.w(host.toString())
        }
    }

    override fun hook() {


        if (Helper.getSpBool(Constant.USE_EXTERNAL_BROWSER, false)) {
            findFirstMethodByName(Instrumentation::class.java, "execStartActivity")?.createHook {
                before {
                    it.log()

                    val intent = it.args?.get(4) as? Intent? ?: return@before
                    val bundle = intent.extras
                    val clsName = intent.component?.className ?: ""
                    if (bundle != null && clsName == "com.donews.nga.activitys.WebActivity") {
                        val url = bundle.getString("act_url")
                        if (url != null && !isNgaUrl(url)) {


                            it.args[4] = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                        }
                    }
                }
            }
        }
    }

    override var name = "WebViewHook"
}

