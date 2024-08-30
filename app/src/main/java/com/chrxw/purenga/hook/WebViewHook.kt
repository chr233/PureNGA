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
import com.github.kyuubiran.ezxhelper.finders.MethodFinder


/**
 * 内置浏览器钩子
 */
class WebViewHook : IHook {

    companion object {
        private val ngaUrls = mutableListOf<String>()

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
        for (host in hosts) {
            ngaUrls.add(host as String)
        }

        if (BuildConfig.DEBUG) {
            for (host in ngaUrls) {
                AndroidLogger.i(host)
            }


            MethodFinder.fromClass("com.donews.nga.common.utils.glide.GlideUtils", classLoader)
                .filterByName("loadUrlImage")
                .forEach { mtd ->
                    mtd.createHook {
                        before {
                            it.log()

                            val str = it.args[1] as String?

//                        AndroidLogger.w(mtd.name)
//                        AndroidLogger.i(str ?: "null")


                            if (str == "https://img.nga.178.com/attachments/mon_202408/27/c8Q2u-eswvK9T8S35-3t.png") {
                                throw Exception("guanggao")
                            }
                        }
                    }
                }


//            MethodFinder.fromClass(
//                "com.donews.nga.fragments.presenters.HomeRecommendFragmentPresenter\$getActivityInfo$1", classLoader
//            ).filterByName("complete").forEach { mtd ->
//                mtd.createHook {
//                    after {
//                        it.log()
//
//                        for (arg in it.args) {
//                            AndroidLogger.i(arg.toString())
//                        }
//
//                        AndroidLogger.w(mtd.name)
//                        throw Exception()
//                    }
//                }
//            }
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
                            val url = Uri.parse(actUrl)
                            if (isNgaUrl(url.host)) {
                                return@before
                            } else {
                                if (BuildConfig.DEBUG) {
                                    AndroidLogger.w(actUrl)
                                    AndroidLogger.w("${url.scheme} ${url.host} ${url.path}")
                                }

                                if (url.host == "game.weixin.qq.com") {
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

