package com.chrxw.purenga.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.res.Resources
import android.os.Environment
import android.widget.Toast
import androidx.core.content.edit
import androidx.core.net.toUri
import com.chrxw.purenga.BuildConfig
import com.chrxw.purenga.Constant
import io.github.kyuubiran.ezxhelper.android.logging.Logger
import io.github.kyuubiran.ezxhelper.xposed.EzXposed
import java.io.File
import kotlin.system.exitProcess


/**
 * 功能性钩子
 */
@SuppressLint("StaticFieldLeak")
object Helper {
    lateinit var spDoinfo: SharedPreferences

    lateinit var spPlugin: SharedPreferences
    lateinit var clsRId: Class<*>
    lateinit var clsRId2: Class<*>
    lateinit var clsDrawerId: Class<*>

    lateinit var moduleResources: Resources

    var context: Context? = null
    var isXposed = false

    var enableLog = false

    /**
     * 发送Toast
     */
    fun toast(text: String, duration: Int = Toast.LENGTH_SHORT) {
        val ctx = context
        if (ctx != null) {
            Toast.makeText(ctx, text, duration).show()
        }
    }

    /**
     * 清理资源
     */
    fun clearStaticResource() {
        context = null
    }

    /**
     * 获取版本号
     */
    fun getNgaVersion(): String {
        val ctx = context
        if (ctx == null) {
            return "null"
        } else {
            return try {
                val info = ctx.packageManager.getPackageInfo(
                    Constant.NGA_PACKAGE_NAME, PackageInfo.INSTALL_LOCATION_AUTO
                )

                return info.versionName ?: "获取失败"
            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
                "获取失败"
            }
        }
    }

    /**
     * 获取版本号
     */
    const val PLUGIN_VERSION = "${BuildConfig.VERSION_CODE}-${BuildConfig.VERSION_NAME}"


    /**
     * 是否为整合版
     */
    fun isBundled(): Boolean {
        val ctx = context
        return if (ctx == null) {
            false
        } else {
            try {
                ctx.packageManager.getPackageInfo(
                    BuildConfig.APPLICATION_ID, PackageInfo.INSTALL_LOCATION_AUTO
                ).versionName
                false
            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
                true
            }
        }
    }

    fun isPluginConfigExists(): Boolean {
        val path = "${EzXposed.appContext.filesDir.path}/../shared_prefs/${Constant.PLUGIN_PREFERENCE_NAME}.xml"
        return File(path).exists()
    }

    fun resetPluginConfig(): Boolean {
        val path = "${EzXposed.appContext.filesDir.path}/../shared_prefs/${Constant.PLUGIN_PREFERENCE_NAME}.xml"
        return File(path).delete()
    }

    /**
     * 复制到剪贴板
     */
    fun copyToClipboard(context: Context, text: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("label", text)
        clipboard.setPrimaryClip(clip)
    }

    /**
     * 获取ResId
     */
    private fun getRes(cls: Class<*>?, key: String): Int {
        return try {
            cls?.getDeclaredField(key)?.getInt(null) ?: -1
        } catch (e: Throwable) {
            e.printStackTrace()
            Logger.w("加载资源 $key 失败")
            -1
        }
    }

    /**
     * 获取ResId
     */
    fun getRId(key: String): Int {
        return getRes(clsRId, key)
    }

    fun getRId2(key: String): Int {
        return getRes(clsRId2, key)
    }

    fun getDrawerId(key: String): Int {
        return getRes(clsDrawerId, key)
    }

    /**
     * 是否为夜间模式
     */
    fun isDarkModel(): Boolean {
        return spDoinfo.getBoolean("DARK_MODEL", false)
    }

    /**
     * 是否存在SharedPreference值
     */
    fun hasSpKey(key: String): Boolean {
        return spPlugin.contains(key)
    }

    /**
     * 获取SharedPreference值
     */
    fun getSpBool(key: String, defValue: Boolean): Boolean {
        return spPlugin.getBoolean(key, defValue)
    }

    /**
     * 设置SharedPreference值
     */
    fun setSpBool(key: String, value: Boolean) {
        spPlugin.edit { putBoolean(key, value) }
    }

    /**
     * 获取SharedPreference值
     */
    fun getSpStr(key: String, defValue: String?): String? {
        return spPlugin.getString(key, defValue)
    }

    /**
     * 设置SharedPreference值
     */
    fun setSpStr(key: String, value: String?) {
        spPlugin.edit { putString(key, value) }
    }

    /**
     * 获取SharedPreference值
     */
    fun getSpInt(key: String, defValue: Int): Int {
        return spPlugin.getInt(key, defValue)
    }

    /**
     * 设置SharedPreference值
     */
    fun setSpInt(key: String, value: Int) {
        spPlugin.edit { putInt(key, value) }
    }

    /**
     * 获取SharedPreference值
     */
    fun getSpLong(key: String, defValue: Long): Long {
        return spPlugin.getLong(key, defValue)
    }

    /**
     * 设置SharedPreference值
     */
    fun setSpLong(key: String, value: Long) {
        spPlugin.edit { putLong(key, value) }
    }

    /**
     * 重启应用
     */
    fun restartApplication(activity: Activity) {
        val pm = activity.packageManager
        val intent = pm.getLaunchIntentForPackage(activity.packageName)
        intent?.putExtra("fromShortcut", true)
        intent?.putExtra("gotoName", "pluginSetting")
        activity.finishAffinity()
        activity.startActivity(intent)
        exitProcess(0)
    }

    fun gotoReleasePage(context: Context) {
        context.startActivity(
            Intent(
                Intent.ACTION_VIEW, Constant.REPO_URL.toUri()
            )
        )
    }

    fun exportSharedPreference(context: Context, sharedPreferencesName: String, exportName: String): String? {
        return try {
            val sdCardFile = Environment.getExternalStorageDirectory()

            val sourceFile = File(context.filesDir.parent, "shared_prefs/$sharedPreferencesName.xml")
            val destinationFile = File(sdCardFile, "$exportName.xml")

            if (sourceFile.exists()) {
                sourceFile.copyTo(destinationFile, overwrite = true)
                destinationFile.path
            } else {
                null
            }
        } catch (e: Exception) {
            Logger.e("导出失败", e)
            null
        }
    }

    fun importSharedPreference(context: Context, sharedPreferencesName: String, importName: String): String? {
        return try {
            val sdCardFile = Environment.getExternalStorageDirectory()

            val sourceFile = File(sdCardFile, "$importName.xml")
            val destinationFile = File(context.filesDir.parent, "shared_prefs/$sharedPreferencesName.xml")

            if (sourceFile.exists()) {
                sourceFile.copyTo(destinationFile, overwrite = true)
                sourceFile.path
            } else {
                null
            }
        } catch (e: Exception) {
            Logger.e("导出失败", e)
            null
        }
    }

    /**
     * 打开链接
     */
    fun openUrl(context: Context, uri: String) {
        if (uri.isNotEmpty()) {
            val intent = Intent(Intent.ACTION_VIEW, uri.toUri())
            context.startActivity(intent)
        }
    }

    @JvmStatic
    internal fun printStackTrace(
        stackDepth: Int = Int.MAX_VALUE,
    ) {
        // 1. 获取当前线程完整堆栈
        val stackTrace = Thread.currentThread().stackTrace

        // 4. 打印堆栈头部信息
        Logger.i("===== 堆栈信息开始 (总深度:${stackTrace.size}) =====")

        // 5. 遍历打印堆栈（格式化输出关键信息）
        for (element in stackTrace) {
            val stackLine = formatStackTraceElement(element)
            Logger.i(stackLine)
        }

        // 6. 打印堆栈尾部信息
        Logger.i("===== 堆栈信息结束 =====")
    }

    /**
     * 格式化堆栈元素，统一输出格式
     * 格式：类全路径.方法名(源文件:行号)
     */
    @JvmStatic
    internal fun formatStackTraceElement(element: StackTraceElement): String {
        val className = element.className // 类全路径
        val methodName = element.methodName // 方法名
        val fileName = element.fileName ?: "UnknownFile" // 源文件（空则为UnknownFile）
        val lineNumber = element.lineNumber // 行号（混淆后为-1）
        return "$className.$methodName ($fileName:$lineNumber)"
    }
}