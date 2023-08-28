package com.chrxw.purenga

import android.app.Activity
import android.app.AndroidAppHelper
import android.app.Application
import android.app.Instrumentation
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.res.Resources
import android.content.res.XModuleResources
import android.widget.Toast
import com.chrxw.purenga.utils.Helper
import com.chrxw.purenga.utils.Log
import com.github.kyuubiran.ezxhelper.EzXHelper
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.IXposedHookZygoteInit
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage


/**
 * 初始化Xposed
 */
class XposedInit : IXposedHookLoadPackage, IXposedHookZygoteInit {

    override fun initZygote(startupParam: IXposedHookZygoteInit.StartupParam) {
        EzXHelper.initZygote(startupParam)

        modulePath = startupParam.modulePath
        moduleRes = getModuleRes(modulePath)
    }

    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {

        EzXHelper.initHandleLoadPackage(lpparam)
        EzXHelper.setLogTag(Constant.LOG_TAG)

        if (lpparam.packageName == BuildConfig.APPLICATION_ID) {
            Log.d("模块内运行")

            XposedHelpers.findAndHookMethod(MainActivity.Companion::class.java.name,
                lpparam.classLoader,
                "isModuleActive",
                object : XC_MethodReplacement() {
                    override fun replaceHookedMethod(param: MethodHookParam?): Any {
                        return true
                    }
                })

        } else if (lpparam.packageName == Constant.NGA_PACKAGE_NAME) {
            Log.d("NGA内运行")

            XposedBridge.hookAllMethods(Activity::class.java, "startActivity", object : XC_MethodHook() {
                override fun beforeHookedMethod(param: MethodHookParam?) {
                    var obj = param?.args?.get(0)
                    if (obj is Activity) {
                        val activity = obj
                        val clsName = activity.localClassName
                        Log.i("className: $clsName")
                    } else if (obj is Array<*> && obj.isArrayOf<Activity>()) {
                        for (activity in obj) {
                            val clsName = (activity as Activity).localClassName
                            Log.i("classNames: $clsName")
                        }
                    } else if (obj is Intent) {
                        val clsName = obj.component?.className ?: ""
                        Log.i("className: $clsName")
                    } else {
                        Log.i(obj.toString())
                    }
                }
            })

            XposedHelpers.findAndHookMethod(Instrumentation::class.java,
                "callApplicationOnCreate",
                Application::class.java,
                object : XC_MethodHook() {
                    override fun afterHookedMethod(param: MethodHookParam) {

                        if (param.args[0] is Application) {
                            val context = AndroidAppHelper.currentApplication().applicationContext

                            EzXHelper.initAppContext(context, true)

                            if (Helper.init()) {
                                Hooks.initHooks(lpparam.classLoader)

                                if (!Helper.spPlugin.getBoolean(Constant.HIDE_HOOK_INFO, false)) {
                                    Helper.toast("PureNGA 加载成功, 请到【设置】>【PureNGA】开启功能", Toast.LENGTH_LONG)
                                }
                            } else {
                                val ngaVersion = try {
                                    EzXHelper.appContext.packageManager.getPackageInfo(
                                        Constant.NGA_PACKAGE_NAME, PackageInfo.INSTALL_LOCATION_AUTO
                                    ).versionName
                                } catch (e: Throwable) {
                                    "获取失败"
                                }
                                Helper.toast(
                                    "PureNGA 初始化失败, 可能不支持当前版本 NGA: $ngaVersion", Toast.LENGTH_LONG
                                )
                            }
                        }
                    }
                })
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


