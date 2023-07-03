package com.chrxw.purenga.hook

import com.chrxw.purenga.utils.Log
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers

/**
 * Vip钩子
 */
class VipHook(classLoader: ClassLoader) : BaseHook(classLoader) {
    override fun startHook() {
        try {
            // Hook getIsVip 方法
            XposedHelpers.findAndHookMethod(
                "com.donews.nga.vip.entitys.VipStatus",
                mClassLoader,
                "getIsVip",
                object : XC_MethodHook() {
                    @Throws(Throwable::class)
                    override fun beforeHookedMethod(param: MethodHookParam?) {
                        Log.d("getIsVip")
                        super.beforeHookedMethod(param)
                    }

                    @Throws(Throwable::class)
                    override fun afterHookedMethod(param: MethodHookParam?) {
                        Log.d("getIsVip " + param?.result.toString())
                        param?.result = true
                        super.afterHookedMethod(param)
                    }
                }
            )

            XposedHelpers.findAndHookMethod(
                "gov.pianzong.androidnga.model.UserInfoDataBean",
                mClassLoader,
                "getAdfree",
                object : XC_MethodHook() {
                    @Throws(Throwable::class)
                    override fun beforeHookedMethod(param: MethodHookParam) {
                        Log.d("getAdfree")
                        super.beforeHookedMethod(param)
                    }

                    @Throws(Throwable::class)
                    override fun afterHookedMethod(param: MethodHookParam) {
                        Log.d("getAdfree " + param.result.toString())
                        super.afterHookedMethod(param)
                    }
                }
            )


        } catch (e: Exception) {
            Log.e(e)
        }
    }
}
