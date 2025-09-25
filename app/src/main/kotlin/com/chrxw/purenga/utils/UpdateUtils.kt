package com.chrxw.purenga.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.net.toUri
import com.chrxw.purenga.Constant
import com.chrxw.purenga.utils.data.Release
import com.github.kyuubiran.ezxhelper.AndroidLogger
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request

object UpdateUtils {

    fun getReleaseInfo(onResult: (Release?) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val client = OkHttpClient.Builder().connectTimeout(10, java.util.concurrent.TimeUnit.SECONDS)
                    .readTimeout(20, java.util.concurrent.TimeUnit.SECONDS)
                    .writeTimeout(20, java.util.concurrent.TimeUnit.SECONDS).build()

                val url = Constant.API_PLUGIN_STANDALONE_URL
                val request = Request.Builder().url(url).build()
                val response = client.newCall(request).execute()
                if (!response.isSuccessful) {
                    onResult(null)
                    return@launch
                }

                val body = response.body.string()
                if (body.isEmpty()) {
                    onResult(null)
                    return@launch
                }

                val gson = Gson()
                val release: Release = gson.fromJson(body, Release::class.java)

                // 假设 Release 里有 tagName 字段，和当前版本比较
                if (release.tagName != Constant.CURRENT_VERSION) {
                    sendUpdateNotification(release)
                }

                onResult(release)
            } catch (ex: Exception) {
                AndroidLogger.e(ex)
                onResult(null)
            }
        }

    }

    private fun sendUpdateNotification(release: Release) {
        val context = Helper.context // 获取全局 Context
        if (context == null) {
            return
        }

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel =
                NotificationChannel(Constant.CHANNEL_ID, Constant.CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT)
            manager.createNotificationChannel(channel)
        }

        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = release.htmlUrl.toUri() // 假设 release 有 htmlUrl 字段
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val action = NotificationCompat.Action.Builder(
            android.R.drawable.ic_menu_view,
            "查看详情",
            pendingIntent
        ).build()

        val notification = NotificationCompat.Builder(context, Constant.CHANNEL_ID)
            .setContentTitle("有新版本可用")
            .setContentText("最新版本: ${release.tagName}")
            .setSmallIcon(android.R.drawable.stat_sys_download_done)
            .addAction(action)
            .build()

        manager.notify(1, notification)
    }


}