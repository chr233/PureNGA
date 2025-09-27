package com.chrxw.purenga.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import com.chrxw.purenga.Constant
import com.chrxw.purenga.utils.UpdateUtils.getAssetUrl
import com.chrxw.purenga.utils.data.Release

object NotificationUtils {
    fun sendNotification(release: Release?) {
        val ctx = Helper.context
        if (ctx == null || release == null) {
            return
        }

        val downloadUrl = getAssetUrl(release)
        if (downloadUrl == null) {
            return
        }
    }

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                Constant.CHANNEL_ID,
                Constant.CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = context.getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
        }
    }

    fun sendNotification(context: Context, url: Uri) {
        createNotificationChannel(context)

        // Intent for the action button to open browser
        val browserIntent = Intent(Intent.ACTION_VIEW,url)
        val pendingIntent = PendingIntent.getActivity(
            context, 0, browserIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, Constant.CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("PureNGA 有新版本")
            .setContentText("点击下面按钮访问网页")
            .setStyle(NotificationCompat.BigTextStyle().bigText("tttt"))
            .addAction(
                android.R.drawable.ic_menu_view,
                "打开浏览器",
                pendingIntent
            )
            .setAutoCancel(true)
            .build()

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(1, notification)
    }
}