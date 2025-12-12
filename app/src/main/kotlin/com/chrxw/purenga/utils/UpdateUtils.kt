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

    fun getChangeLog(release: Release?): String? {
        if (release?.body != null) {
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

    fun getAssetVersionCode(release: Release?): Int {
        if (release?.tagName.isNullOrEmpty()) {
            return 0
        }

        val code = release.tagName.split("-").firstOrNull()?.toIntOrNull()
        return code ?: 0
    }

    fun checkIfNeedCheck(): Boolean {
        val currentTime = System.currentTimeMillis() / 1000
        val lastCheck = Helper.getSpLong(Constant.LAST_UPDATE_CHECK_DATE, 0)

        val needUpdate = (currentTime - lastCheck) > Constant.UPDATE_CHECK_INTERVAL
        if (needUpdate) {
            Helper.setSpLong(Constant.LAST_UPDATE_CHECK_DATE, currentTime)
        }
        return needUpdate
    }

    fun checkIfNeedUpdate(code: Int): Boolean {
        return BuildConfig.VERSION_CODE < code
    }

    fun checkIfSkipUpdate(code: Int): Boolean {
        val skipCode = Helper.getSpInt(Constant.SKIP_VERSION_CODE, 0)
        return code != skipCode
    }
}

