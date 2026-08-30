package com.chrxw.purenga

import android.app.AndroidAppHelper
import android.app.Application
import android.app.Instrumentation
import android.widget.Toast
import androidx.annotation.Keep
import com.chrxw.purenga.hook.DebugHook
import com.chrxw.purenga.utils.ExtensionUtils.log
import com.chrxw.purenga.utils.Helper
import com.chrxw.purenga.utils.StatusUtils
import com.github.kyuubiran.ezxhelper.AndroidLogger
import com.github.kyuubiran.ezxhelper.EzXHelper
import com.github.kyuubiran.ezxhelper.HookFactory.`-Static`.createHook
import com.github.kyuubiran.ezxhelper.finders.FieldFinder
import com.github.kyuubiran.ezxhelper.finders.MethodFinder
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.IXposedHookZygoteInit
import de.robv.android.xposed.callbacks.XC_LoadPackage


/**
 * 初始化Xposed
 */
@Keep
class XposedInit : IXposedHookLoadPackage, IXposedHookZygoteInit {
    companion object {
        private var isInit = false
    }

    override fun initZygote(startupParam: IXposedHookZygoteInit.StartupParam) {
        EzXHelper.initZygote(startupParam)
    }

    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        // 初始化EzHelper
        EzXHelper.initHandleLoadPackage(lpparam)
        EzXHelper.setLogTag(Constant.LOG_TAG)

        if (!lpparam.isFirstApplication) {
            return
        }

        if (lpparam.packageName == BuildConfig.APPLICATION_ID) {
            AndroidLogger.d("模块内运行")

            FieldFinder.fromClass(StatusUtils::class.java.name).filterByName("modelEnabled").firstOrNull()
                ?.setBoolean(null, true) ?: AndroidLogger.e("MainActivity.Companion 方法未找到")

        } else if (lpparam.packageName == Constant.NGA_PACKAGE_NAME) {
            AndroidLogger.d("NGA内运行")

            Helper.isXposed = true

            MethodFinder.fromClass(Instrumentation::class.java).filterByName("callApplicationOnCreate")
                .filterByAssignableParamTypes(Application::class.java).first().createHook {
                    after {
                        it.log()

                        if (it.args[0] is Application) {
                            if (!isInit) {
                                isInit = true
                                val context = AndroidAppHelper.currentApplication().applicationContext

                                EzXHelper.initAppContext(context, true)
                                Helper.context = EzXHelper.appContext

                                val error = Hooks.initHooks(lpparam.classLoader)

                                if (error == -1) {
                                    return@after
                                }

                                if (BuildConfig.DEBUG) {
                                    AndroidLogger.w("!!! Debug 模式 !!!")
                                    val hook = DebugHook()
                                    try {
                                        hook.init(lpparam.classLoader)
                                    } catch (e: Exception) {
                                        error + 1
                                        AndroidLogger.e("DebugHook 初始化失败", e)
                                    }

                                    try {
                                        hook.hook()
                                    } catch (e: Exception) {
                                        AndroidLogger.e("DebugHook Hook失败", e)
                                    }
                                }

                                if (error == 0) {
                                    if (!Helper.getSpBool(Constant.HIDE_HOOK_INFO, false)) {
                                        Helper.toast(
                                            buildString {
                                                appendLine("PureNGA 加载成功")
                                                appendLine("【可以在设置中禁用】")
                                            }, Toast.LENGTH_LONG
                                        )
                                    }
                                } else {
                                    Helper.toast(
                                        buildString {
                                            appendLine("PureNGA $error 个模块加载失败")
                                            appendLine("可能不支持当前版本")
                                            appendLine("NGA 版本: ${Helper.getNgaVersion()}")
                                            appendLine("插件版本: ${BuildConfig.VERSION_NAME}")
                                        }, Toast.LENGTH_LONG
                                    )
                                }
                            }
                        } else {
                            AndroidLogger.d("跳过初始化")
                        }
                    }
                }
        }
    }
}


