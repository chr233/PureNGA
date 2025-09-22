package com.chrxw.purenga.utils

import com.chrxw.purenga.Constant
import com.chrxw.purenga.utils.data.Release
import com.github.kyuubiran.ezxhelper.AndroidLogger
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.Request

object UpdateUtils {

    fun getLatestVersion() {
        CoroutineScope(Dispatchers.IO).launch {

        }
    }

    private fun getLatestVersion2(): Boolean {
        val client = OkHttpClient()

        val url = Constant.API_PLUGIN_STANDALONE_URL
        val request = Request.Builder().url(url).build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                return false
            }

            val body = response.body.string()

            if (body.isEmpty()) {
                return false
            }

            AndroidLogger.w(body)

            val gson = Gson()
            val releases: Release = gson.fromJson(body, Release::class.java)

            AndroidLogger.e(releases.toString())
            AndroidLogger.i(releases.body)
            AndroidLogger.i(releases.tagName)

            return true
        }
    }
}