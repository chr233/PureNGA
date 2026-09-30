package com.chrxw.purenga.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.graphics.drawable.Icon
import android.util.DisplayMetrics
import android.view.View
import com.chrxw.purenga.BuildConfig
import com.chrxw.purenga.hook.OptimizeHook
import io.github.kyuubiran.ezxhelper.android.logging.Logger
import io.github.kyuubiran.ezxhelper.core.finder.MethodFinder
import io.github.kyuubiran.ezxhelper.xposed.common.HookParam
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
    fun HookParam.log() {
        if (Helper.enableLog) {
            this.forceLog()
        }
    }

    fun HookParam.forceLog() {
        Logger.d("Method: ${this.javaClass.name}")
        Logger.d("Object: ${this.thisObject}")

        if (this.args.any()) {
            Logger.d("Args:")
            this.args.forEachIndexed { index, item ->
                val cls = item?.javaClass ?: "NULL"
                Logger.d(" $index: $item ($cls)")
            }
        }
    }

    fun findMethodByName(clazz: Class<*>, name: String): MethodFinder {
        val finder = MethodFinder.fromClass(clazz).filterByName(name)

        if (finder.firstOrNull() == null) {
            Logger.w("${clazz.name} $name not found")
        }

        return finder
    }

    fun findFirstMethodByName(clazz: Class<*>, name: String): Method? {
        val finder = MethodFinder.fromClass(clazz).filterByName(name)

        val first = finder.firstOrNull()

        if (first == null) {
            Logger.w("${clazz.name} $name not found")
        } else {
            if (BuildConfig.DEBUG) {
                Logger.d("${clazz.name} $name hook init success")
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
    ): ShortcutInfo {
        val icon = Icon.createWithResource(this, iconId ?: Helper.getDrawerId("app_logo"))
        val intent = this.buildShortcutIntent(OptimizeHook.clsMainActivity, id)

        val shortcut = ShortcutInfo.Builder(this, id).setShortLabel(shortLabel).setLongLabel(long)
            .setIcon(icon)
            .setIntent(intent).build()

        return shortcut
    }

    fun Context.setShortcuts(shortcuts: List<ShortcutInfo>?) {
        val shortcutManager = this.getSystemService(ShortcutManager::class.java)
        shortcutManager.dynamicShortcuts = shortcuts ?: listOf<ShortcutInfo>()
    }

    fun Context.getShortcuts(): List<ShortcutInfo> {
        val shortcutManager = this.getSystemService(ShortcutManager::class.java)
        return shortcutManager.dynamicShortcuts
    }

    /**
     * 输出类字段
     */
    fun Any.printObject() {
        val clazz: Class<*> = this::class.java
        val fields: Array<Field> = clazz.declaredFields

        Logger.w("===== ${this.javaClass.name} =====")
        for (field in fields) {
            field.isAccessible = true
            val value = field.get(this)
            Logger.i("${field.name} = $value")
        }
        Logger.d("---------------------")
    }

    fun Int.getStringFromMod(): String {
        return if (Helper.isXposed) {
            Helper.moduleResources.getString(this)
        } else {
            val ctx = Helper.context
            ctx?.resources?.getString(this) ?: ""
        }
    }

    fun Int.getStringFromMod(vararg formatArgs: Any): String {
        return if (Helper.isXposed) {
            Helper.moduleResources.getString(this, formatArgs)
        } else {
            val ctx = Helper.context
            ctx?.resources?.getString(this, formatArgs) ?: ""
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    fun Int.getDrawable(theme: Resources.Theme?): Drawable {
        return if (Helper.isXposed) {
            Helper.moduleResources.getDrawable(this, theme)
        } else {
            val ctx = Helper.context
            ctx?.resources?.getDrawable(this, theme) ?: throw Exception("Resource Not Found")
        }
    }

    fun View.getActivity(): Activity? {
        var context = this.context
        // 循环遍历 Context 包装链
        while (context is ContextWrapper) {
            if (context is Activity) {
                return context
            }
            context = context.baseContext // 解包，继续向上找
        }
        return null
    }
}