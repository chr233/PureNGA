package com.chrxw.purenga.hook

import android.app.Instrumentation
import android.content.Intent
import android.net.Uri
import com.chrxw.purenga.BuildConfig
import com.chrxw.purenga.Constant
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
        private val bilibiliUrls = arrayOf(
            "www.bilibili.com",
            "b23.tv",
            "bili2233.cn",
            "bili23.cn",
            "bili33.cn",
            "bili22.cn",
            "space.bilibili.com",
            "bilibili.kankanews.com",
            "bilibili.tv",
            "bilibili.cn",
            "bilibili.com",
            "www.bilibili.tv",
            "www.bilibili.cn",
            "www.bilibili.com",
            "bilibili.smgbb.cn",
            "m.acg.tv",
            "n.bilibili.com",
            "live.bilibili.com",
        )

        val ngaUrls = arrayOf(
            "ngabbs.com",
            "ngabbs.cn",
            "ngabbs.com",
            "bbs.ngacn.cc",
            "nga.178.com",
            "bbs.nga.cn",
            "bbs.bigccq.cn",
        )

        private fun isMatchHost(host: String?, hosts: Array<String>): Boolean {
            if (host != null) {
                for (h in hosts) {
                    if (host == h) {
                        AndroidLogger.i("$host == $h")

                        return true
                    }else{
                        AndroidLogger.i("$host != $h")
                    }
                }
            }
            return false
        }

        private fun isNgaUrl(host: String?): Boolean {
            return isMatchHost(host, ngaUrls)
        }

        private fun isBilibiliUrl(host: String?): Boolean {
            return isMatchHost(host, bilibiliUrls)
        }
    }

    override fun init(classLoader: ClassLoader) {
        if (BuildConfig.DEBUG) {
            AndroidLogger.w("NGA urls:")
            val hosts = MainHook.clsAppConfig.getField("hosts").get(null) as Array<*>
            for (host in hosts) {
                AndroidLogger.w(host.toString())
            }
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
                        val actUrl = bundle.getString("act_url")
                        if (actUrl != null) {
                            var url = Uri.parse(actUrl)
                            if (isNgaUrl(url.host)) {
                                return@before
                            }

                            AndroidLogger.e(actUrl)
                            AndroidLogger.w("${url.scheme} ${url.host} ${url.path}")

                            if (Helper.getSpBool(Constant.OPEN_URL_OPTIMIZE, false)) {
                                if (isBilibiliUrl(url.host)) {
                                    AndroidLogger.w("isBilibiliUrl")
                                    val builder = url.buildUpon();
                                    builder.scheme("bilibili")
                                    url = builder.build()
                                }
                            }

                            AndroidLogger.w("${url.scheme} ${url.host} ${url.path}")

                            it.args[4] = Intent(Intent.ACTION_VIEW, url)
                        }
                    }
                }
            }
        }
    }

    override var name = "WebViewHook"
}

