package com.chrxw.purenga.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.graphics.drawable.Icon
import android.os.Build
import android.util.DisplayMetrics
import com.chrxw.purenga.BuildConfig
import com.chrxw.purenga.hook.OptimizeHook
import com.github.kyuubiran.ezxhelper.AndroidLogger
import com.github.kyuubiran.ezxhelper.EzXHelper
import com.github.kyuubiran.ezxhelper.finders.MethodFinder
import de.robv.android.xposed.XC_MethodHook
import java.lang.reflect.Field
import java.lang.reflect.Method

/**
 * 显示单位换算
 */
object ExtensionUtils {
    /**
     * 单位转换
     */
    fun Int.toPixel(context: Context): Int {
        val resources = context.resources
        val metrics = resources.displayMetrics
        return this * (metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT)
    }

    /**
     * 输出日志
     */
    fun XC_MethodHook.MethodHookParam.log() {
        if (Helper.enableLog) {
            AndroidLogger.d("Method: ${this.method.name}")
            AndroidLogger.d("Object: ${this.thisObject}")

            if (this.args.any()) {
                AndroidLogger.d("Args:")
                this.args.forEachIndexed { index, item ->
                    val cls = item?.javaClass ?: "NULL"
                    AndroidLogger.d(" $index: $item ($cls)")
                }
            }
        }
    }

    fun XC_MethodHook.MethodHookParam.forceLog() {
        AndroidLogger.d("Method: ${this.method.name}")
        AndroidLogger.d("Object: ${this.thisObject}")

        if (this.args.any()) {
            AndroidLogger.d("Args:")
            this.args.forEachIndexed { index, item ->
                val cls = item?.javaClass ?: "NULL"
                AndroidLogger.d(" $index: $item ($cls)")
            }
        }
    }

    fun findMethodByName(clazz: Class<*>, name: String): MethodFinder {
        val finder = MethodFinder.fromClass(clazz).filterByName(name)

        if (finder.firstOrNull() == null) {
            AndroidLogger.w("${clazz.name} $name not found")
        }

        return finder
    }

    fun findFirstMethodByName(clazz: Class<*>, name: String): Method? {
        val finder = MethodFinder.fromClass(clazz).filterByName(name)

        val first = finder.firstOrNull()

        if (first == null) {
            AndroidLogger.w("${clazz.name} $name not found")
        } else {
            if (BuildConfig.DEBUG) {
                AndroidLogger.d("${clazz.name} $name hook init success")
            }
        }
        return first
    }

    fun Context.buildNormalIntent(clazz: Class<*>): Intent {
        val intent = Intent(this, clazz).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
            action = Intent.ACTION_VIEW
        }
        return intent
    }

    private fun Context.buildShortcutIntent(clazz: Class<*>, gotoName: String): Intent {
        val intent = Intent(this, clazz).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            action = Intent.ACTION_VIEW
            putExtra("fromShortcut", true)
            putExtra("gotoName", gotoName)
        }

        return intent
    }

    fun Context.buildShortcut(
        id: String, shortLabel: String, long: String, iconId: Int?,
    ): ShortcutInfo? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            val icon = Icon.createWithResource(this, iconId ?: Helper.getDrawerId("app_logo"))
            val intent = this.buildShortcutIntent(OptimizeHook.clsMainActivity, id)

            val shortcut = ShortcutInfo.Builder(this, id).setShortLabel(shortLabel).setLongLabel(long).setIcon(icon)
                .setIntent(intent).build()

            shortcut
        } else {
            Helper.toast("安卓版本不支持此操作")
            null
        }
    }

    fun Context.setShortcuts(shortcuts: List<ShortcutInfo>?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            val shortcutManager = this.getSystemService(ShortcutManager::class.java)
            shortcutManager.dynamicShortcuts = shortcuts ?: listOf<ShortcutInfo>()
        } else {
            Helper.toast("安卓版本不支持此操作")
        }
    }

    fun Context.getShortcuts(): List<ShortcutInfo>? {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            val shortcutManager = this.getSystemService(ShortcutManager::class.java)
            return shortcutManager.dynamicShortcuts
        } else {
            Helper.toast("安卓版本不支持此操作")
            return null
        }
    }

    /**
     * 输出类字段
     */
    fun Any.printObject() {
        val clazz: Class<*> = this::class.java
        val fields: Array<Field> = clazz.declaredFields

        AndroidLogger.w("===== $this =====")
        for (field in fields) {
            field.isAccessible = true
            val value = field.get(this)
            AndroidLogger.i("${field.name} = $value")
        }
        AndroidLogger.d("---------------------")
    }

    fun Int.getStringFromMod(): String {
        return if (Helper.isXposed) {
            EzXHelper.moduleRes.getString(this)
        } else {
            val ctx = Helper.context
            ctx?.resources?.getString(this) ?: ""
        }
    }

    fun Int.getStringFromMod(vararg formatArgs: Any): String {
        return if (Helper.isXposed) {
            EzXHelper.moduleRes.getString(this, formatArgs)
        } else {
            val ctx = Helper.context
            ctx?.resources?.getString(this, formatArgs) ?: ""
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    fun Int.getDrawable(theme: Resources.Theme?): Drawable {
        return if (Helper.isXposed) {
            EzXHelper.moduleRes.getDrawable(this, theme)
        } else {
            val ctx = Helper.context
            ctx?.resources?.getDrawable(this, theme) ?: throw Exception("Resource Not Found")
        }
    }
}