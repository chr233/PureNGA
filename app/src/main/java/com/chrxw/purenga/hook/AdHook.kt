package com.chrxw.purenga.hook

import android.widget.Toast
import com.chrxw.purenga.utils.Log
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.XposedHelpers


/**
 * 广告钩子
 */
class AdHook(classLoader: ClassLoader) : BaseHook(classLoader) {
    override fun startHook() {
        try {
            XposedHelpers.findAndHookMethod(
                "com.donews.admediation.adimpl.feed.DnFeedAd",
                mClassLoader,
                "requestServerSuccess",
                object : XC_MethodReplacement() {
                    override fun replaceHookedMethod(param: MethodHookParam?) {
                        Log.i("DnFeedAd.requestServerSuccess")
                    }
                })
        } catch (e: Exception) {
            Log.e(e)
        }
    }
}
