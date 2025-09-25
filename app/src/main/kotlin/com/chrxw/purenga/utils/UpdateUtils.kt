package com.chrxw.purenga.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import com.chrxw.purenga.Constant
import com.chrxw.purenga.utils.data.Release
import com.github.kyuubiran.ezxhelper.AndroidLogger
import com.github.kyuubiran.ezxhelper.EzXHelper
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request

object UpdateUtils {

    fun getLatestVersion() {
        CoroutineScope(Dispatchers.IO).launch {
            val client = OkHttpClient()
            val url = Constant.API_PLUGIN_STANDALONE_URL
            val request = Request.Builder().url(url).build()
            val response = client.newCall(request).execute()
            if (!response.isSuccessful) return@launch

            val body = response.body.string() ?: return@launch
            if (body.isEmpty()) return@launch

            val gson = Gson()
            val release: Release = gson.fromJson(body, Release::class.java)

            // 假设 Release 里有 tagName 字段，和当前版本比较
            if (release.tagName != Constant.CURRENT_VERSION) {
                sendUpdateNotification(release)
            }
        }
    }

    private fun sendUpdateNotification(release: Release) {
        val context = EzXHelper.appContext // 获取全局 Context
        val channelId = "update_channel"
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "PureNGA 更新通知", NotificationManager.IMPORTANCE_DEFAULT)
            manager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(context, channelId)
            .setContentTitle("有新版本可用")
            .setContentText("最新版本: ${release.tagName}")
            .setSmallIcon(android.R.drawable.stat_sys_download_done)
            .build()

        manager.notify(1, notification)
    }


    private fun getLatestVersion2(): Boolean {
        val client = OkHttpClient()

        val url = Constant.API_PLUGIN_STANDALONE_URL
        val request = Request.Builder().url(url).build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                return false
            }

            val body = response.body.string()

            if (body.isEmpty()) {
                return false
            }

            AndroidLogger.w(body)

            val gson = Gson()
            val releases: Release = gson.fromJson(body, Release::class.java)

            AndroidLogger.e(releases.toString())
            AndroidLogger.i(releases.body)
            AndroidLogger.i(releases.tagName)

            return true
        }
    }
}