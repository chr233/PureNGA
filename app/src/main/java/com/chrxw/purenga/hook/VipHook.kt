package com.chrxw.purenga.hook

import com.chrxw.purenga.utils.Log
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers

/**
 * Vip钩子
 */
class VipHook : IHook {

    companion object {
        var clsVipStatus: Class<*>? = null
        var clsUserInfoDataBean: Class<*>? = null;
    }

    override fun hookName(): String {
        return "VIP破解（弃用）"
    }

    override fun init(classLoader: ClassLoader) {
        clsVipStatus =
            XposedHelpers.findClass("com.donews.nga.vip.entitys.VipStatus", classLoader)
        clsUserInfoDataBean =
            XposedHelpers.findClass("com.donews.nga.vip.entitys.VipStatus", classLoader)
    }

    override fun hook() {
        // Hook getIsVip 方法
        XposedHelpers.findAndHookMethod(
            clsVipStatus,
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
            clsUserInfoDataBean,
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
    }
}
