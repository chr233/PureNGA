package com.chrxw.purenga.hook

import android.app.Instrumentation
import android.content.Intent
import androidx.core.net.toUri
import com.chrxw.purenga.BuildConfig
import com.chrxw.purenga.Constant
import com.chrxw.purenga.hook.base.IHook
import com.chrxw.purenga.utils.ExtensionUtils.findFirstMethodByName
import com.chrxw.purenga.utils.ExtensionUtils.log
import com.chrxw.purenga.utils.Helper
import com.github.kyuubiran.ezxhelper.AndroidLogger
import com.github.kyuubiran.ezxhelper.HookFactory.`-Static`.createHook


/**
 * 内置浏览器钩子
 */
class WebViewHook : IHook {

    companion object {
        private lateinit var ngaUrls: List<String>

        private fun isNgaUrl(host: String?): Boolean {
            if (host != null) {
                for (h in ngaUrls) {
                    if (host == h) {
                        return true
                    }
                }
            }
            return false
        }
    }

    override fun init(classLoader: ClassLoader) {
        val field = MainHook.clsAppConfig.getDeclaredField("hosts")
        field.isAccessible = true

        val hosts = field.get(null) as Array<*>

        var urls = mutableListOf<String>()
        for (host in hosts) {
            var url = host as String
            urls.add(url)

            if (BuildConfig.DEBUG) {
                AndroidLogger.i(url)
            }
        }

        ngaUrls = urls
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
                        val actUrl = bundle.getString("act_url")
                        if (actUrl != null) {
                            val url = actUrl.toUri()
                            if (isNgaUrl(url.host)) {
                                return@before
                            } else {
                                if (BuildConfig.DEBUG) {
                                    AndroidLogger.w(actUrl)
                                    AndroidLogger.w("${url.scheme} ${url.host} ${url.path}")
                                }

                                if (url.host == "game.weixin.qq.com" && BuildConfig.DEBUG) {
                                    Helper.toast("test")

                                    throw Exception("114514")
                                } else {
                                    it.args[4] = Intent(Intent.ACTION_VIEW, url)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    override var name = "WebViewHook"
}

