package com.chrxw.purenga.hook

import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.widget.LinearLayout
import android.widget.ScrollView
import androidx.core.app.NotificationCompat
import com.chrxw.purenga.hook.base.IHook
import com.chrxw.purenga.ui.ClickableItemXpView
import com.chrxw.purenga.utils.ExtensionUtils.findFirstMethodByName
import com.chrxw.purenga.utils.ExtensionUtils.log
import com.chrxw.purenga.utils.Helper
import com.github.kyuubiran.ezxhelper.EzXHelper
import com.github.kyuubiran.ezxhelper.HookFactory.`-Static`.createHook
import de.robv.android.xposed.XposedHelpers


class DebugHook : IHook {
    companion object {

    }

    override fun init(classLoader: ClassLoader) {
    }

    override fun hook() {
        findFirstMethodByName(OptimizeHook.clsHomeDrawerLayout, "initLayout")?.createHook {
            after {
                it.log()

                val viewBinding = XposedHelpers.getObjectField(it.thisObject, "binding")
                val root = XposedHelpers.callMethod(viewBinding, "getRoot") as LinearLayout

                //净化侧拉菜单
                val scrollView = root.getChildAt(1) as ScrollView
                val linearLayout = scrollView.getChildAt(0) as LinearLayout

                val view = ClickableItemXpView(root.context, "重启 NGA", "调试用").apply {
                    setBackgroundColor(Color.LTGRAY)
                    setOnClickListener { _ ->
                        Helper.toast("正在重启")
                        val activity = XposedHelpers.callMethod(it.thisObject, "getActivity") as Activity
                        Helper.restartApplication(activity)
                    }
                }

                linearLayout.addView(view, linearLayout.childCount - 1)


                val channelId = "purenga_update"
                val channelName = "PureNGA 更新通知"
                val notificationManager =
                    EzXHelper.appContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

// 创建通知渠道（仅需一次）
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val channel = NotificationChannel(channelId,channelName, NotificationManager.IMPORTANCE_DEFAULT)
                    notificationManager.createNotificationChannel(channel)
                }

                val intent = Intent(EzXHelper.appContext, PreferencesHook.clsAboutUsActivity).apply {
                    putExtra("openDialog", true)
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
                val pendingIntent = PendingIntent.getActivity(
                    EzXHelper.appContext, 0, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )

                val notification = NotificationCompat.Builder(EzXHelper.appContext, channelId)
                    .setContentTitle("PureNGA 有新版本了")
                    .setContentText("内容")
                    .setSmallIcon(Helper.getRId("iv_app_icon"))
                    .setContentIntent(pendingIntent) // 设置点击跳转
                    .build()

// 发送通知
                notificationManager.notify(1, notification)
            }
        }
    }

    override var name = "DebugHook"
}