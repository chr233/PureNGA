package com.chrxw.purenga

import android.annotation.SuppressLint
import android.app.AndroidAppHelper
import android.content.Context
import android.content.res.Resources
import android.content.res.XModuleResources
import com.chrxw.purenga.hook.BaseHook
import com.chrxw.purenga.hook.SplashHook
import com.chrxw.purenga.utils.Log
import com.chrxw.purenga.utils.replaceMethod
import de.robv.android.xposed.*
import de.robv.android.xposed.callbacks.XC_LoadPackage
import java.util.concurrent.CompletableFuture

class XposedInit : IXposedHookLoadPackage, IXposedHookZygoteInit {
    override fun initZygote(startupParam: IXposedHookZygoteInit.StartupParam) {
        modulePath = startupParam.modulePath
        moduleRes = getModuleRes(modulePath)
        Log.i("模块路径" + startupParam.modulePath)
    }

    @Throws(Throwable::class)
    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        if (BuildConfig.APPLICATION_ID == lpparam.packageName) {
            MainActivity.Companion::class.java.name.replaceMethod(
                lpparam.classLoader,
                "isModuleActive"
            ) { true }
        }

        if (Constant.NGA_PACKAGE_NAME == lpparam.packageName) {
            Log.i("模块运行" + lpparam.packageName)
            startHook(SplashHook(lpparam.classLoader))
        }
    }

    private fun startHook(hooker: BaseHook) {
        try {
            hookers.add(hooker)
            hooker.startHook()
        } catch (e: Throwable) {
            Log.e(e)
        }
    }

    companion object {
        private val hookers = ArrayList<BaseHook>()

        lateinit var modulePath: String
        lateinit var moduleRes: Resources

        @JvmStatic
        fun getModuleRes(path: String): Resources {
            return XModuleResources.createInstance(path, null)
        }
    }
}
