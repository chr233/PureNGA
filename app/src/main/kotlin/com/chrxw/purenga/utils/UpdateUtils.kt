package com.chrxw.purenga.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
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

                onResult(release)
            } catch (ex: Exception) {
                AndroidLogger.e(ex)
                onResult(null)
            }
        }

    }

    fun getAssetUrl(release: Release?): Uri? {
        if (release?.assets != null) {
            for (asset in release.assets) {
                if (asset.name?.endsWith(".apk") == true) {
                    return asset.browserDownloadUrl?.toUri()
                }
            }
        }
        return null
    }

    fun sendUpdateNotification(release: Release) {

        val context = Helper.context // 获取全局 Context
        if (context == null) {
            return
        }

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                Constant.CHANNEL_ID, Constant.CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT
            )
            manager.createNotificationChannel(channel)
        }

        val intent = Intent(Intent.ACTION_VIEW, release.htmlUrl?.toUri())
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val action = NotificationCompat.Action.Builder(
            android.R.drawable.ic_menu_view, "立即下载", pendingIntent
        ).build()

        val notification = NotificationCompat.Builder(context, Constant.CHANNEL_ID).setContentTitle("有新版本可用")
            .setContentText("最新版本: ${release.tagName}").setSmallIcon(android.R.drawable.stat_sys_download_done)
            .addAction(action).build()

        manager.notify(1, notification)
    }


}