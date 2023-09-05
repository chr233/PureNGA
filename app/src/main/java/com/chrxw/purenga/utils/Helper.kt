package com.chrxw.purenga.utils

import android.content.SharedPreferences
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.widget.Toast
import com.chrxw.purenga.BuildConfig
import com.chrxw.purenga.Constant
import com.github.kyuubiran.ezxhelper.AndroidLogger
import com.github.kyuubiran.ezxhelper.EzXHelper
import de.robv.android.xposed.XposedHelpers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL


/**
 * 功能性钩子
 */
class Helper {
    companion object {
        lateinit var spDoinfo: SharedPreferences

        lateinit var spPlugin: SharedPreferences
        lateinit var clsRId: Class<*>

        fun toast(text: String, duration: Int = Toast.LENGTH_SHORT) {
            AndroidLogger.toast(text, duration)
        }

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

        private fun getRes(cls: Class<*>?, key: String): Int {
            return try {
                XposedHelpers.getStaticIntField(cls, key)
            } catch (e: Throwable) {
                AndroidLogger.e("加载资源 $key 失败")
                AndroidLogger.e(e)
                -1
            }
        }

        fun getRId(key: String): Int {
            return getRes(clsRId, key)
        }

        fun isDarkModel(): Boolean {
            return spDoinfo.getBoolean("DARK_MODEL", false)
        }

        fun getSpBool(key: String, defValue: Boolean): Boolean {
            return spPlugin.getBoolean(key, defValue)
        }

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
    }
}