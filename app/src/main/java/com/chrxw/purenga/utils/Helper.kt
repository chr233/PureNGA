package com.chrxw.purenga.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import com.chrxw.purenga.BuildConfig
import com.chrxw.purenga.Constant
import com.github.kyuubiran.ezxhelper.AndroidLogger
import com.github.kyuubiran.ezxhelper.EzXHelper
import de.robv.android.xposed.XposedHelpers
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import kotlin.system.exitProcess


/**
 * 功能性钩子
 */
object Helper {
    lateinit var spDoinfo: SharedPreferences

    lateinit var spPlugin: SharedPreferences
    lateinit var clsRId: Class<*>
    lateinit var clsDrawerId: Class<*>

    var enableLog = false

    /**
     * 发送Toast
     */
    fun toast(text: String, duration: Int = Toast.LENGTH_SHORT) {
        AndroidLogger.toast(text, duration)
    }

    /**
     * 获取版本号
     */
    fun getNgaVersion(): String {
        return try {
            EzXHelper.appContext.packageManager.getPackageInfo(
                Constant.NGA_PACKAGE_NAME, PackageInfo.INSTALL_LOCATION_AUTO
            ).versionName
        } catch (e: PackageManager.NameNotFoundException) {
            "获取失败"
        }
    }

    fun isBundled(): Boolean {
        return try {
            EzXHelper.appContext.packageManager.getPackageInfo(
                BuildConfig.APPLICATION_ID, PackageInfo.INSTALL_LOCATION_AUTO
            ).versionName
            false
        } catch (e: PackageManager.NameNotFoundException) {
            true
        }
    }

    /**
     * 获取ResId
     */
    private fun getRes(cls: Class<*>?, key: String): Int {
        return try {
            XposedHelpers.getStaticIntField(cls, key)
        } catch (e: Throwable) {
            AndroidLogger.w("加载资源 $key 失败")
            -1
        }
    }

    /**
     * 获取ResId
     */
    fun getRId(key: String): Int {
        return getRes(clsRId, key)
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
     * 获取SharedPreference值
     */
    fun getSpBool(key: String, defValue: Boolean): Boolean {
        return spPlugin.getBoolean(key, defValue)
    }

    /**
     * 设置SharedPreference值
     */
    fun setSpBool(key: String, value: Boolean) {
        spPlugin.edit().putBoolean(key, value).apply()
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
        spPlugin.edit().putString(key, value).apply()
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun checkForUpdates() {
        val currentVersion = BuildConfig.VERSION_NAME
        val url = Constant.API_PLUGIN_STANDALONE_URL

        GlobalScope.launch(Dispatchers.Main) {
            val response = withContext(Dispatchers.IO) {
                makeHttpRequest()
            }

            response?.let {
                val json = JSONObject(response)
                val latestVersion = json.getString("tag_name")
                val releaseNotes = json.getString("body")

                if (currentVersion != latestVersion) {
                    toast("有新版本")
                }
            }
        }
    }

    private fun makeHttpRequest(): String? {
        val url = URL(Constant.API_PLUGIN_STANDALONE_URL)
        val connection = url.openConnection() as? HttpURLConnection
        connection?.requestMethod = "GET"

        return connection?.inputStream?.use { inputStream ->
            val reader = BufferedReader(InputStreamReader(inputStream))
            val response = StringBuilder()

            var line = reader.readLine()
            while (line != null) {
                response.append(line)
                line = reader.readLine()
            }

            response.toString()
        }
    }

    /**
     * 重启应用
     */
    fun restartApplication(activity: Activity) {
        val pm = activity.packageManager
        val intent = pm.getLaunchIntentForPackage(activity.packageName)
        activity.finishAffinity()
        activity.startActivity(intent)
        exitProcess(0)
    }

    fun gotoReleasePage(context: Context) {
        context.startActivity(
            Intent(
                Intent.ACTION_VIEW, Uri.parse(Constant.REPO_URL)
            )
        )
    }
}