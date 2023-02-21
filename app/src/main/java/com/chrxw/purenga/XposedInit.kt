package com.chrxw.purenga

import android.content.res.Resources
import android.content.res.XModuleResources
import com.chrxw.purenga.hook.BaseHook
import com.chrxw.purenga.hook.RewardHook
import com.chrxw.purenga.hook.SplashHook
import com.chrxw.purenga.utils.Log
import de.robv.android.xposed.*
import de.robv.android.xposed.callbacks.XC_LoadPackage

/**
 * 初始化Xposed
 */
class XposedInit : IXposedHookLoadPackage, IXposedHookZygoteInit {
    override fun initZygote(startupParam: IXposedHookZygoteInit.StartupParam) {
        modulePath = startupParam.modulePath
        moduleRes = getModuleRes(modulePath)
    }

    @Throws(Throwable::class)
    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        if (BuildConfig.APPLICATION_ID == lpparam.packageName) {
            XposedHelpers.findAndHookMethod(
                MainActivity.Companion::class.java.name,
                lpparam.classLoader,
                "isModuleActive",
                object : XC_MethodReplacement() {
                    override fun replaceHookedMethod(param: MethodHookParam?): Any {
                        return true
                    }
                }
            )
        }

        if (Constant.NGA_PACKAGE_NAME == lpparam.packageName) {
            Log.d("NGA内运行" + lpparam.packageName)
            startHook(SplashHook(lpparam.classLoader))
            startHook(RewardHook(lpparam.classLoader))
        }
    }

    private fun startHook(hooker: BaseHook) {
        try {
            Log.i(hooker::class.java.name + " start")
            hooker.startHook()
        } catch (e: Throwable) {
            Log.e(e)
        }
    }

    companion object {

        lateinit var modulePath: String
        lateinit var moduleRes: Resources

        @JvmStatic
        fun getModuleRes(path: String): Resources {
            return XModuleResources.createInstance(path, null)
        }
    }
}
