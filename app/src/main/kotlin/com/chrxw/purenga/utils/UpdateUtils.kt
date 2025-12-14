package com.chrxw.purenga.utils

import android.net.Uri
import androidx.core.net.toUri
import com.chrxw.purenga.BuildConfig
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
    private fun sendRequest(url: String, onResult: (Release?) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val client = OkHttpClient.Builder().connectTimeout(10, java.util.concurrent.TimeUnit.SECONDS)
                    .readTimeout(20, java.util.concurrent.TimeUnit.SECONDS)
                    .writeTimeout(20, java.util.concurrent.TimeUnit.SECONDS).build()

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

    /**
     * 获取插件更新信息
     */
    fun getPluginReleaseInfo(onResult: (Release?) -> Unit) {
        sendRequest(Constant.API_PLUGIN_STANDALONE_URL, onResult)
    }

    /**
     * 获取整合版更新信息
     */
    fun getBundledReleaseInfo(onResult: (Release?) -> Unit) {
        sendRequest(Constant.API_PLUGIN_BUNDLED_URL, onResult)
    }

    /**
     * 获取第一个Apk下载链接
     */
    fun getFirstAssetUrl(release: Release): Uri? {
        if (release.assets != null) {
            for (asset in release.assets) {
                if (asset.name?.endsWith(".apk") == true) {
                    return asset.browserDownloadUrl?.toUri()
                }
            }
        }
        return null
    }

    /**
     * 获取更新日志
     */
    fun getChangeLog(release: Release): String? {
        if (release.body != null) {
            val result = buildString {

                if (!release.tagName.isNullOrEmpty()) {
                    appendLine("最新版本: ${release.tagName}")
                    appendLine("更新日志:")
                }

                for (line in release.body.split("\n")) {
                    if (line.contains("---")) {
                        break
                    }

                    if (!line.startsWith("![") && line.isNotBlank()) {
                        appendLine(" - ${line.trim()}")
                    }
                }
            }

            return result.trim()
        }
        return null
    }

    /**
     * 获取资源版本号
     */
    fun getAssetVersionCode(release: Release): Int {
        if (release.tagName.isNullOrEmpty()) {
            return 0
        }

        val code = release.tagName.split("-").firstOrNull()?.toIntOrNull()
        return code ?: 0
    }

    /**
     * 获取资源版本名
     */
    fun getAssetVersionName(release: Release): String? {
        if (release.tagName.isNullOrEmpty()) {
            return null
        }

        val code = release.tagName.split("-").lastOrNull()
        return code
    }

    /**
     * 判断是否需要检测更新
     */
    fun checkIfNeedCheck(): Boolean {
        val currentTime = System.currentTimeMillis() / 1000
        val lastCheck = Helper.getSpLong(Constant.LAST_UPDATE_CHECK_DATE, 0)

        val needUpdate = (currentTime - lastCheck) > Constant.UPDATE_CHECK_INTERVAL
        if (needUpdate) {
            Helper.setSpLong(Constant.LAST_UPDATE_CHECK_DATE, currentTime)
        }
        return needUpdate
    }

    /**
     * 判断是否需要更新
     */
    fun checkIfNeedUpdate(code: Int): Boolean {
        return BuildConfig.VERSION_CODE < code
    }

    /**
     * 判断是否跳过该版本更新
     */
    fun checkIfSkipUpdate(code: Int): Boolean {
        val skipCode = Helper.getSpInt(Constant.SKIP_VERSION_CODE, 0)
        return code != skipCode
    }
}

