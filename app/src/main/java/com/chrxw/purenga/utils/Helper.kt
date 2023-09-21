package com.chrxw.purenga.utils

import android.content.SharedPreferences
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.widget.Toast
import com.chrxw.purenga.BuildConfig
import com.chrxw.purenga.Constant
import com.github.kyuubiran.ezxhelper.AndroidLogger
import com.github.kyuubiran.ezxhelper.EzXHelper
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL


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
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
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
        return getRes(clsDrawerId, key);
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

    private suspend fun fetchJson(url: URL) = withContext(Dispatchers.IO) {
        try {
            JSONObject(url.readText())
        } catch (e: Throwable) {
            null
        }
    }

    private suspend fun fetchJson(url: String) = fetchJson(URL(url))

    private suspend fun checkUpdate() {
        val response = fetchJson(Constant.REPO_URL)
    }

    fun XC_MethodHook.MethodHookParam.log() {
        if (enableLog) {
            AndroidLogger.i("Method: ${this.method.name}")
            AndroidLogger.i("Object: ${this.thisObject}")

            if (this.args.any()) {
                AndroidLogger.i("Args:")
                this.args.forEachIndexed { index, item ->
                    AndroidLogger.d(" $index: $item")
                }
            }
        }
    }
}