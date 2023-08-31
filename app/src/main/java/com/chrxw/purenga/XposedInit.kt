package com.chrxw.purenga

import android.app.AndroidAppHelper
import android.app.Application
import android.app.Instrumentation
import android.content.pm.PackageInfo
import android.content.pm.PackageManager.NameNotFoundException
import android.content.res.Resources
import android.content.res.XModuleResources
import android.widget.Toast
import com.chrxw.purenga.utils.Helper
import com.chrxw.purenga.utils.Log
import com.github.kyuubiran.ezxhelper.EzXHelper
import com.github.kyuubiran.ezxhelper.HookFactory.`-Static`.createHook
import com.github.kyuubiran.ezxhelper.finders.MethodFinder
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.IXposedHookZygoteInit
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
        // 初始化EzHelper
        EzXHelper.initHandleLoadPackage(lpparam)
        EzXHelper.setLogTag(Constant.LOG_TAG)

        if (lpparam.packageName == BuildConfig.APPLICATION_ID) {
            Log.d("模块内运行")

            MethodFinder.fromClass(MainActivity.Companion::class.java.name).filterByName("isModuleActive").first()
                .createHook {
                    replace {
                        return@replace true
                    }
                }

        } else if (lpparam.packageName == Constant.NGA_PACKAGE_NAME) {
            Log.d("NGA内运行")

            var inited = false

            MethodFinder.fromClass(Instrumentation::class.java).filterByName("callApplicationOnCreate")
                .filterByAssignableParamTypes(Application::class.java).first().createHook {
                    after { param ->
                        if (!inited && param.args[0] is Application) {
                            inited = true

                            val context = AndroidAppHelper.currentApplication().applicationContext

                            EzXHelper.initAppContext(context, true)

                            if (Helper.init()) {
                                Hooks.initHooks(lpparam.classLoader)

                                if (!Helper.spPlugin.getBoolean(Constant.HIDE_HOOK_INFO, false)) {
                                    Helper.toast("PureNGA 加载成功, 请到【设置】>【PureNGA】开启功能", Toast.LENGTH_LONG)
                                }
                            } else {
                                var crlf = System.lineSeparator()
                                val ngaVersion = try {
                                    context.packageManager.getPackageInfo(
                                        Constant.NGA_PACKAGE_NAME, PackageInfo.INSTALL_LOCATION_AUTO
                                    ).versionName
                                } catch (e: NameNotFoundException) {
                                    "获取失败"
                                }
                                Helper.toast(
                                    "PureNGA 初始化失败, 可能不支持当前版本${crlf}NGA 版本: $ngaVersion${crlf}插件版本: ${BuildConfig.VERSION_NAME}",
                                    Toast.LENGTH_LONG
                                )
                            }
                        }
                    }
                }
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


