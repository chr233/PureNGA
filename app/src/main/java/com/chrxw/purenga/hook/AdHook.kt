package com.chrxw.purenga.hook

import com.chrxw.purenga.utils.Log
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.XposedHelpers


/**
 * 广告钩子
 */
class AdHook(classLoader: ClassLoader) : BaseHook(classLoader) {
    override fun startHook() {
        try {
            val clsB = XposedHelpers.findClass(
                "com.nga.admodule.AdManager\$b",
                mClassLoader
            )
            XposedHelpers.findAndHookMethod(
                clsB,
                "onAdLoad",
                object : XC_MethodReplacement() {
                    @Throws(Throwable::class)
                    override fun replaceHookedMethod(param: MethodHookParam) {
                        Log.i("onAdLoad ")
                    }
                }
            )
            XposedHelpers.findAndHookMethod(
                clsB,
                "onAdShow",
                object : XC_MethodReplacement() {
                    @Throws(Throwable::class)
                    override fun replaceHookedMethod(param: MethodHookParam) {
                        Log.i("onAdShow ")
                    }
                }
            )

            val clsD = XposedHelpers.findClass(
                "com.nga.admodule.AdManager\$d",
                mClassLoader
            )
            XposedHelpers.findAndHookMethod(
                clsD,
                "onAdLoad",
                object : XC_MethodReplacement() {
                    @Throws(Throwable::class)
                    override fun replaceHookedMethod(param: MethodHookParam) {
                        Log.i("onAdLoad ")
                    }
                }
            )
            XposedHelpers.findAndHookMethod(
                clsD,
                "onAdShow",
                object : XC_MethodReplacement() {
                    @Throws(Throwable::class)
                    override fun replaceHookedMethod(param: MethodHookParam) {
                        Log.i("onAdShow ")
                    }
                }
            )


        } catch (e: Exception) {
            Log.e(e)
        }
    }
}
