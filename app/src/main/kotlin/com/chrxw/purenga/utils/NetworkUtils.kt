package com.chrxw.purenga.utils

import com.chrxw.purenga.Constant
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray

object NetworkUtils {

    private val client = OkHttpClient()

    fun hasNewVersion(currentVersion: String, repo: String): Boolean {
        val url = Constant.REPO_URL
        val request = Request.Builder().url(url).build()
        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) return false
            val body = response.body?.string() ?: return false
            val releases = JSONArray(body)
            if (releases.length() == 0) return false
            val latestTag = releases.getJSONObject(0).getString("tag_name")
            return latestTag != currentVersion
        }
    }

}