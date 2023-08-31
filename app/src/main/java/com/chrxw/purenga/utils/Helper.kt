package com.chrxw.purenga.utils

import android.content.Context
import android.content.SharedPreferences
import android.widget.Toast
import com.chrxw.purenga.Constant
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

        lateinit var clsR: Class<*>
        lateinit var clsRId: Class<*>

        lateinit var clsSPUtil: Class<*>
        lateinit var spPlugin: SharedPreferences

        /**
         * 初始化
         */
        fun init(): Boolean {
            return try {
                //设置SharedPreferences
                spDoinfo = EzXHelper.appContext.getSharedPreferences(Constant.DNINFO, Context.MODE_PRIVATE)
                spPlugin =
                    EzXHelper.appContext.getSharedPreferences(Constant.PLUGIN_PREFERENCE_NAME, Context.MODE_PRIVATE)

                true
            } catch (e: Exception) {
                Log.e(e)
                false
            }
        }

        /**
         * 显示Toast
         */
        fun toast(text: String, duration: Int = Toast.LENGTH_SHORT) {
            try {
                Toast.makeText(EzXHelper.appContext, text, duration).show()
            } catch (e: Throwable) {
                Log.d("toast 出错")
            }
        }

        private fun getRes(cls: Class<*>?, key: String): Int {
            return try {
                XposedHelpers.getStaticIntField(cls, key)
            } catch (e: Throwable) {
                Log.e("加载资源 $key 失败")
                Log.e(e)
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

        suspend fun fetchJson(url: URL) = withContext(Dispatchers.IO) {
            try {
                JSONObject(url.readText())
            } catch (e: Throwable) {
                null
            }
        }

        suspend fun fetchJson(url: String) = fetchJson(URL(url))
    }
}