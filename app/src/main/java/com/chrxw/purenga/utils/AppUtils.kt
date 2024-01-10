package com.chrxw.purenga.utils

import android.content.Context
import android.content.Intent
import android.net.Uri


object AppUtils {
    fun isAppAvailable(context: Context, packageName: String?): Boolean {
        val packageManager = context.packageManager
        val intent = packageManager.getLaunchIntentForPackage(packageName!!)
        return intent != null
    }

    fun openAppOrWebPage(context: Context, packageName: String?, webUrl: String?) {
        if (isAppAvailable(context, packageName)) {
            // 应用可用，启动应用
            val intent = context.packageManager.getLaunchIntentForPackage(packageName!!)
            context.startActivity(intent)
        } else {
            // 应用不可用，尝试打开网页
            openWebPage(context, webUrl)
        }
    }

    fun openWebPage(context: Context, url: String?) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        context.startActivity(intent)
    }
}
